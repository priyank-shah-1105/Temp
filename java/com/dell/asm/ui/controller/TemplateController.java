/**************************************************************************
 *   Copyright (c) 2013 - 2015 Dell Inc. All rights reserved.             *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.dell.asm.asmcore.asmmanager.client.deployment.DeploymentFilterResponse;
import com.dell.asm.asmcore.asmmanager.client.deployment.SelectedServer;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.ProtectionDomain;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareRepository;
import com.dell.asm.asmcore.asmmanager.client.hardware.ScaleIODiskConfiguration;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.Fabric;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.Interface;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.NetworkConfiguration;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.Partition;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateCategory;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponent;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponentSubType;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponentType;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateExportRequest;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateOption;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSetting;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSetting.ServiceTemplateSettingType;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSettingIDs;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateUploadRequest;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateValid;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.TemplateRaidConfiguration;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.VirtualDiskConfiguration;
import com.dell.asm.asmcore.asmmanager.client.util.ServiceTemplateClientUtil;
import com.dell.asm.asmcore.user.model.User;
import com.dell.asm.common.utilities.ASMCommonsMessages;
import com.dell.asm.i18n2.exception.AsmRuntimeException;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.FirmwareRepositoryServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.TemplateServiceAdapter;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.exception.InvalidConfigXmlException;
import com.dell.asm.ui.model.ErrorObj;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringsRequest;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.credential.UICredential;
import com.dell.asm.ui.model.device.UIDevice;
import com.dell.asm.ui.model.template.JobCreateTemplateRequest;
import com.dell.asm.ui.model.template.JobExportTemplateRequest;
import com.dell.asm.ui.model.template.JobGetImportConfigComponentRequest;
import com.dell.asm.ui.model.template.JobGetReferenceComponentRequest;
import com.dell.asm.ui.model.template.JobImportTemplateRequest;
import com.dell.asm.ui.model.template.JobSaveTemplateRequest;
import com.dell.asm.ui.model.template.JobTemplateAttachmentRequest;
import com.dell.asm.ui.model.template.JobTemplateBuilderComponentRequest;
import com.dell.asm.ui.model.template.JobTemplateBuilderRequest;
import com.dell.asm.ui.model.template.JobUpdatedTemplateBuilderComponentRequest;
import com.dell.asm.ui.model.template.UIComponentFabric;
import com.dell.asm.ui.model.template.UIComponentInterface;
import com.dell.asm.ui.model.template.UIComponentNetwork;
import com.dell.asm.ui.model.template.UIComponentPartition;
import com.dell.asm.ui.model.template.UIComponentRaid;
import com.dell.asm.ui.model.template.UIRelatedComponent;
import com.dell.asm.ui.model.template.UITemplate;
import com.dell.asm.ui.model.template.UITemplateBuilder;
import com.dell.asm.ui.model.template.UITemplateBuilderCategory;
import com.dell.asm.ui.model.template.UITemplateBuilderComponent;
import com.dell.asm.ui.model.template.UITemplateBuilderCreate;
import com.dell.asm.ui.model.template.UITemplateBuilderListItem;
import com.dell.asm.ui.model.template.UITemplateBuilderSetting;
import com.dell.asm.ui.model.template.UIValidateResponse;
import com.dell.asm.ui.model.template.UIVirtualDisk;
import com.dell.asm.ui.model.users.UIUser;
import com.dell.asm.ui.util.MappingUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class TemplateController.
 */
@RestController
@RequestMapping(value = "/templates/")
public class TemplateController extends BaseController {

    public static final String DEFAULT_TEMPLATE_ID = "1000";
    public static final String USE_DEFAULT_CATALOG_ID = "usedefaultcatalog";
    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(TemplateController.class);
    // An ObjectMapper suitable for mapping the AsmManagerClient NetworkConfiguration object
    private static final ObjectMapper BACKEND_MAPPER = ServiceTemplateClientUtil.buildObjectMapper();
    private static final String TEMPLATE_ATTACHMENT_DIR = "/opt/Dell/ASM/templates/";
    private TemplateServiceAdapter templateServiceAdapter;
    private FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter;
    private DeploymentServiceAdapter serviceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;

    @Autowired
    public TemplateController(TemplateServiceAdapter templateServiceAdapter,
                              FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter,
                              DeploymentServiceAdapter serviceAdapter,
                              DeviceInventoryServiceAdapter deviceInventoryServiceAdapter) {
        this.templateServiceAdapter = templateServiceAdapter;
        this.firmwareRepositoryServiceAdapter = firmwareRepositoryServiceAdapter;
        this.serviceAdapter = serviceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
    }

    public static UITemplateBuilder parseTemplateBuilder(ServiceTemplate template,
                                                         ApplicationContext context) throws ControllerException {
        return TemplateController.parseTemplateBuilder(template, context, false);
    }

    private static Interface convertUiInterface(UIComponentInterface uiInterface,
                                                boolean isPartitioned) {
        Interface i = new Interface();
        i.setId(uiInterface.id);
        i.setName(uiInterface.name);
        // Note: partitioned flag moved from this level in UI to fabric level
        i.setPartitioned(isPartitioned);

        i.setPartitions(new ArrayList<>());
        for (UIComponentPartition uiPartition : uiInterface.partitions) {
            Partition p = new Partition();
            p.setId(uiPartition.id);
            p.setName(uiPartition.name);
            p.setMinimum(uiPartition.minimum);
            p.setMaximum(uiPartition.maximum);
            p.setNetworks(uiPartition.networks);
            i.getPartitions().add(p);
        }
        return i;
    }

    private static UIComponentInterface convertBackendInterface(Interface i) {
        UIComponentInterface uiInterface = new UIComponentInterface();
        uiInterface.id = i.getId();
        uiInterface.name = i.getName();

        for (Partition p : i.getPartitions()) {
            UIComponentPartition uiPartition = new UIComponentPartition();
            uiPartition.id = p.getId();
            uiPartition.name = p.getName();
            uiPartition.minimum = p.getMinimum();
            uiPartition.maximum = p.getMaximum();
            uiPartition.networks = p.getNetworks();
            uiInterface.partitions.add(uiPartition);
        }

        return uiInterface;
    }

    private static VirtualDiskConfiguration convertUiVirtDiskConfigToBackend(
            UIVirtualDisk uivirtdisk) {
        VirtualDiskConfiguration virtualDiskConfig = new VirtualDiskConfiguration();
        virtualDiskConfig.setComparator(
                VirtualDiskConfiguration.ComparatorValue.valueOf(uivirtdisk.comparator));
        virtualDiskConfig.setDisktype(
                VirtualDiskConfiguration.DiskMediaType.valueOf(uivirtdisk.disktype));
        virtualDiskConfig.setId(uivirtdisk.id);
        virtualDiskConfig.setNumberofdisks(uivirtdisk.numberofdisks);
        String raidLevelFormatted = uivirtdisk.raidlevel.toUpperCase().replaceAll(
                "(?<=[RAID])(?=[0-9])", " ");
        virtualDiskConfig.setRaidlevel(
                VirtualDiskConfiguration.UIRaidLevel.fromConfigValue(raidLevelFormatted));
        return virtualDiskConfig;
    }

    private static TemplateRaidConfiguration convertUiRaidConfigToBackend(UIComponentRaid ui) {
        TemplateRaidConfiguration ret = new TemplateRaidConfiguration();
        ret.setRaidtype(TemplateRaidConfiguration.RaidTypeUI.valueOf(ui.raidtype));
        if (ui.basicraidlevel != null) {
            String raidLevelFormatted = ui.basicraidlevel.toUpperCase().replaceAll(
                    "(?<=[RAID])(?=[0-9])", " ");
            ret.setBasicraidlevel(
                    VirtualDiskConfiguration.UIRaidLevel.fromConfigValue(raidLevelFormatted));
        }
        ret.setEnableglobalhotspares(ui.enableglobalhotspares);
        ret.setGlobalhotspares(ui.globalhotspares);
        ret.setMinimumssd(ui.minimumssd);
        List<VirtualDiskConfiguration> virtualDisks = new ArrayList<>();
        List<UIVirtualDisk> uiVirtualDisks = ui.virtualdisks;
        for (UIVirtualDisk thisDisk : uiVirtualDisks) {
            virtualDisks.add(convertUiVirtDiskConfigToBackend(thisDisk));
        }
        ret.setVirtualdisks(virtualDisks);
        List<VirtualDiskConfiguration> externalVirtualDisks = new ArrayList<>();
        List<UIVirtualDisk> uiExternalVirtualDisks = ui.externalvirtualdisks;
        for (UIVirtualDisk thisDisk : uiExternalVirtualDisks) {
            externalVirtualDisks.add(convertUiVirtDiskConfigToBackend(thisDisk));
        }
        ret.setExternalvirtualdisks(externalVirtualDisks);
        ret.setMinimumssdexternal(ui.minimumssdexternal);
        ret.setEnableglobalhotsparesexternal(ui.enableglobalhotsparesexternal);
        ret.setGlobalhotsparesexternal(ui.globalhotsparesexternal);
        return ret;

    }

    private static NetworkConfiguration convertUiNetworkConfigToBackend(UIComponentNetwork ui) {
        NetworkConfiguration ret = new NetworkConfiguration();
        ret.setId(ui.id);
        ret.setInterfaces(new ArrayList<>());
        for (UIComponentFabric uiFabric : ui.interfaces) {
            if (uiFabric.enabled) {
                Fabric f = new Fabric();
                f.setId(uiFabric.id);
                f.setName(uiFabric.name);
                f.setEnabled(uiFabric.enabled);
                f.setNictype(uiFabric.nictype);
                f.setRedundancy(uiFabric.redundancy);
                f.setFabrictype(uiFabric.fabrictype);
                f.setPartitioned(uiFabric.partitioned);

                f.setInterfaces(new ArrayList<>());
                for (UIComponentInterface uiInterface : uiFabric.interfaces) {
                    Interface i = convertUiInterface(uiInterface, uiFabric.partitioned);
                    f.getInterfaces().add(i);
                }

                ret.getInterfaces().add(f);
            }
        }

        // clear networks for unused partitions.
        // this will prevent storing obsolete network
        List<Interface> usedInterfaces = ret.getUsedInterfaces();
        for (Fabric f : ret.getInterfaces()) {
            for (Interface itf : f.getInterfaces()) {
                if (!usedInterfaces.contains(itf)) {
                    for (Partition p : itf.getPartitions()) {
                        if (p.getNetworks() != null) {
                            p.getNetworks().clear();
                        }
                    }
                }
            }
        }


        return ret;
    }

    private static UIVirtualDisk convertBackendVirtualDiskConfigToUi(
            VirtualDiskConfiguration virtualdiskconf) throws InvalidConfigXmlException {
        UIVirtualDisk uiVirtDisk = new UIVirtualDisk();
        boolean valid = true;
        String missingAttribute = "UNKNOWN";
        if (virtualdiskconf.getId() == null) {
            valid = false;
            missingAttribute = "DiskID";
        } else if (virtualdiskconf.getRaidlevel() == null) {
            valid = false;
            missingAttribute = "RAID Level";
        } else if (virtualdiskconf.getComparator() == null) {
            valid = false;
            missingAttribute = "Comparator";
        } else if (virtualdiskconf.getDisktype() == null) {
            valid = false;
            missingAttribute = "Disk Type";
        }
        if (!valid) {
            throw new InvalidConfigXmlException("RAID", missingAttribute);
        }

        uiVirtDisk.id = virtualdiskconf.getId();
        uiVirtDisk.raidlevel = virtualdiskconf.getRaidlevel().toString();
        uiVirtDisk.comparator = virtualdiskconf.getComparator().toString();
        uiVirtDisk.numberofdisks = virtualdiskconf.getNumberofdisks();
        uiVirtDisk.disktype = virtualdiskconf.getDisktype().toString();
        return uiVirtDisk;
    }

    private static UIComponentRaid convertBackendRAIDConfigToUi(
            TemplateRaidConfiguration conf) throws InvalidConfigXmlException {
        UIComponentRaid ret = new UIComponentRaid();

        if (conf.getRaidtype() == null) {
            throw new InvalidConfigXmlException("RAID", "RAIDTypes");
        }

        ret.raidtype = conf.getRaidtype().toString();
        if (conf.getBasicraidlevel() != null) {
            ret.basicraidlevel = conf.getBasicraidlevel().toString();
        }
        ret.enableglobalhotspares = conf.isEnableglobalhotspares();
        ret.globalhotspares = conf.getGlobalhotspares();
        ret.minimumssd = conf.getMinimumssd();
        List<VirtualDiskConfiguration> virtDiskConfig = conf.getVirtualdisks();
        List<UIVirtualDisk> uiVirtualDisk = new ArrayList<>();
        for (VirtualDiskConfiguration disk : virtDiskConfig) {
            uiVirtualDisk.add(convertBackendVirtualDiskConfigToUi(disk));
        }
        ret.virtualdisks = uiVirtualDisk;
        virtDiskConfig = conf.getExternalvirtualdisks();
        uiVirtualDisk = new ArrayList<>();
        for (VirtualDiskConfiguration disk : virtDiskConfig) {
            uiVirtualDisk.add(convertBackendVirtualDiskConfigToUi(disk));
        }
        ret.externalvirtualdisks = uiVirtualDisk;
        ret.minimumssdexternal = conf.getMinimumssdexternal();
        ret.enableglobalhotsparesexternal = conf.isEnableglobalhotsparesexternal();
        ret.globalhotsparesexternal = conf.getGlobalhotsparesexternal();
        return ret;
    }

    /**
     * This also deals with legacy templates where we have fabrics and separation blade/rack server.
     * @param conf
     * @return
     */
    private static UIComponentNetwork convertBackendNetworkConfigToUi(NetworkConfiguration conf) {
        UIComponentNetwork ret = new UIComponentNetwork();
        ret.id = conf.getId();

        if (conf.getInterfaces() != null) {
            for (Fabric f : conf.getInterfaces()) {
                //if (f.isEnabled())
                ret.interfaces.add(convertFromFabric(f));
            }
        }

        return ret;
    }

    private static UIComponentFabric convertFromFabric(Fabric f) {
        UIComponentFabric uiFabric = new UIComponentFabric();
        uiFabric.id = f.getId();
        uiFabric.name = f.getName();
        uiFabric.enabled = f.isEnabled();
        uiFabric.nictype = f.getNictype();
        uiFabric.redundancy = f.isRedundancy();
        uiFabric.fabrictype = f.getFabrictype();

        boolean partitioned = false;
        int n_ports = f.getNPorts();
        for (int i = 0; i < f.getInterfaces().size(); ++i) {
            Interface iface = f.getInterfaces().get(i);
            UIComponentInterface uiInterface = convertBackendInterface(iface);
            uiFabric.interfaces.add(uiInterface);
            if (i < n_ports) {
                partitioned = partitioned || iface.isPartitioned();
            }
        }
        uiFabric.partitioned = partitioned;
        return uiFabric;
    }

    /**
     * Compose TemplateBuilder.
     *
     * @param template
     * @return
     */
    public static UITemplateBuilder parseTemplateBuilder(final ServiceTemplate template,
                                                         final ApplicationContext context,
                                                         final boolean scaleup) throws ControllerException {
        UITemplateBuilder tb = new UITemplateBuilder();
        if (template != null) {
            tb.createdBy = template.getCreatedBy();
            tb.createdDate = MappingUtils.getTime(template.getCreatedDate());
            tb.description = template.getTemplateDescription();
            tb.draft = template.isDraft();
            tb.isValid = template.getTemplateValid().isValid();
            tb.isLocked = template.isTemplateLocked();
            tb.inConfiguration = template.isInConfiguration();
            tb.id = template.getId();
            tb.originalId = template.getOriginalTemplateId();
            tb.name = template.getTemplateName();
            tb.source = template.getTemplateType();
            tb.updatedBy = template.getUpdatedBy();
            tb.updatedDate = MappingUtils.getDate(template.getUpdatedDate());
            tb.category = template.getCategory();
            tb.allStandardUsers = template.isAllUsersAllowed();
            tb.manageFirmware = template.isManageFirmware();
            if (template.getAttachments() != null) {
                for (String fileName : template.getAttachments()) {
                    //Attachments don't have real IDs, so just setting the ID to the file name, which should be unique on the file system anyhow.
                    tb.attachments.add(new UIListItem(fileName, fileName));
                }
            }
            if (template.isManageFirmware()) {
                if (template.isUseDefaultCatalog()) {
                    tb.firmwarePackageId = USE_DEFAULT_CATALOG_ID;
                    tb.firmwarePackageName = "Use VxFM appliance default catalog";
                } else if (template.getFirmwareRepository() != null) {
                    tb.firmwarePackageId = template.getFirmwareRepository().getId();
                    tb.firmwarePackageName = template.getFirmwareRepository().getName();
                }
            }
            if (template.getAssignedUsers() != null) {
                for (User u : template.getAssignedUsers()) {
                    UIUser uiuser = UsersController.getJSONObject(u, context);
                    tb.assignedUsers.add(uiuser);
                }
            }

            boolean isDefaultTemplate = tb.id != null && tb.id.equals(DEFAULT_TEMPLATE_ID);

            if (template.getComponents() != null && template.getComponents().size() > 0) {
                int cmpCnt = 0;
                for (ServiceTemplateComponent sc : template.getComponents()) {

                    if (sc.getType() == null) {
                        throw new ControllerException("Invalid template - no component type");
                    }
                    UITemplateBuilderComponent cmp = parseTemplateBuilderComponent(sc, context,
                                                                                   scaleup, cmpCnt,
                                                                                   isDefaultTemplate);
                    if (cmp != null) {
                        tb.components.add(cmp);
                        cmpCnt++;
                    }
                }
            }
            if (template.getConfiguration() != null) {
                ConfigureTemplateController.createUIConfigureTemplateConfiguration(tb,
                                                                                   template.getConfiguration());
            }
        }
        return tb;
    }

    public static UITemplateBuilderComponent parseTemplateBuilderComponent(ServiceTemplateComponent sc,
                                                                           ApplicationContext context,
                                                                           boolean scaleup,
                                                                           int cmpCnt,
                                                                           boolean isDeafultTemplate) {
        UITemplateBuilderComponent cmp = null;
        if (sc != null) {
            cmp = new UITemplateBuilderComponent();

            cmp.componentid = sc.getComponentID();
            if (cmp.componentid == null)
                cmp.componentid = "component" + cmpCnt; // cannot be null in UI
            cmp.helptext = sc.getHelpText();
            cmp.id = sc.getId();
            ServiceTemplateValid componentValid = sc.getComponentValid();
            cmp.isComponentValid = componentValid.isValid();
            if (CollectionUtils.isNotEmpty(componentValid.getMessages())) {
                ErrorObj errObj = new ErrorObj();
                buildErrorObj(errObj,
                              componentValid.getMessages(),
                              context);
                cmp.errorObj = errObj;
            }
            cmp.identifier = sc.getIdentifier();
            cmp.referenceid = sc.getRefId();
            cmp.referenceip = sc.getIP();
            cmp.referenceipurl = "https://" + sc.getIP();
            cmp.configfilename = sc.getConfigFile();
            cmp.cloned = sc.isCloned();
            cmp.clonedFromId = sc.getClonedFromId();
            cmp.instances = sc.getInstances();
            // cannot be 0
            if (cmp.instances == 0) {
                cmp.instances = 1;
            }

            cmp.name = sc.getName();
            if (sc.getType() != null) {
                cmp.type = getDeviceTypeFromTypeName(sc.getType().name());
            }
            if (sc.getSubType() != null) {
                cmp.subtype = sc.getSubType().getLabel();
            }
            cmp.AsmGUID = sc.getAsmGUID();
            cmp.puppetCertName = sc.getPuppetCertName();
            cmp.allowClone = false;


            // cmp.categories
            if (sc.getResources() != null && sc.getResources().size() > 0) {
                for (ServiceTemplateCategory cat : sc.getResources()) {
                    UITemplateBuilderCategory uiCat = new UITemplateBuilderCategory();


                    uiCat.id = cat.getId();
                    uiCat.name = cat.getDisplayName();

                    if (cat.getId().equals(
                            ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_IDRAC_RESOURCE)) {
                        cmp.allowClone = true;
                    }


                    if (cat.getParameters() != null && cat.getParameters().size() > 0) {
                        for (ServiceTemplateSetting set : cat.getParameters()) {
                            UITemplateBuilderSetting uiSet = new UITemplateBuilderSetting();


                            uiSet.componentid = cmp.componentid;
                            uiSet.id = set.getId();
                            uiSet.datatype = getUITemplateSettingDataType(set.getType());
                            if (set.getMax() != null)
                                uiSet.max = set.getMax();


                            if (set.getMin() != null)
                                uiSet.min = set.getMin();


                            if (set.getMaxLength() != null)
                                uiSet.maxlength = set.getMaxLength();


                            if (set.getStep() != null)
                                uiSet.step = set.getStep();


                            uiSet.name = set.getDisplayName();
                            uiSet.required = set.isRequired();


                            uiSet.requireatdeployment = set.isRequiredAtDeployment();
                            uiSet.hidefromtemplate = set.isHideFromTemplate();
                            uiSet.dependencyTarget = set.getDependencyTarget();
                            uiSet.dependencyValue = set.getDependencyValue();
                            uiSet.tooltip = set.getToolTip();
                            uiSet.readOnly = set.isReadOnly();
                            uiSet.generated = set.isGenerated();
                            uiSet.group = set.getGroup();
                            uiSet.useinfotooltip = set.isInfoIcon();
                            uiSet.isOptionsSortable = set.isOptionsSortable();
                            uiSet.isPreservedForDeployment = set.isPreservedForDeployment();

                            // if setType is list then do multiple is true else false

                            uiSet.multiple = set.getType() == ServiceTemplateSettingType.LIST || set.getType() == ServiceTemplateSettingType.VMVIRTUALDISKCONFIGURATION;

                            if (set.getOptions() != null && set.getOptions().size() > 0) {

                                for (ServiceTemplateOption value : set.getOptions()) {
                                    UITemplateBuilderListItem item = new UITemplateBuilderListItem();
                                    item.id = value.getValue();
                                    item.name = value.getName();
                                    item.dependencyTarget = value.getDependencyTarget();
                                    item.dependencyValue = value.getDependencyValue();

                                    if (!(((cmp.componentid != null && cmp.componentid.startsWith(
                                            ServiceTemplateSettingIDs.SERVICE_TEMPLATE_VCENTER_VM_ID_PREFIX)) ||
                                            (cmp.id != null && cmp.id.startsWith(
                                                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_VCENTER_VM_ID_PREFIX)))
                                            && value.getName().toLowerCase().contains("esx"))) {
                                        uiSet.options.add(item);
                                    }
                                }

                            }
                            if (ServiceTemplateSetting.ServiceTemplateSettingType.PASSWORD.equals(
                                    set.getType())) {

                                if (set.getValue() == null) {

                                    // replace nulls with empty string for default template or $CURRENT$ for existing on EDIT mode

                                    uiSet.value = isDeafultTemplate ? "" : UICredential.CURRENT_PASSWORD;

                                } else if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_PASSWORD_DEFAULT_TO_REMOVE.equals(
                                        set.getValue())) {
                                    uiSet.value = "";
                                } else {
                                    uiSet.value = set.getValue();
                                }

                            } else {

                                uiSet.value = set.getValue();
                                // strip leading coma if present. UI artifact.

                                if (uiSet.value != null && uiSet.value.startsWith(
                                        ",") && uiSet.value.length() > 1)

                                    uiSet.value = uiSet.value.substring(1);
                            }

                            uiCat.settings.add(uiSet);

                            if (set.getType() == ServiceTemplateSettingType.NETWORKCONFIGURATION &&

                                    !StringUtils.isEmpty(set.getValue())) {
                                cmp.showNetworkInfo = true;
                                ObjectMapper mapper = new ObjectMapper();
                                try {
                                    NetworkConfiguration backend = BACKEND_MAPPER.readValue(
                                            uiSet.value, NetworkConfiguration.class);
                                    cmp.network = convertBackendNetworkConfigToUi(backend);
                                    applyIncomingLabelFix(cmp.network, context);
                                    uiSet.value = mapper.writeValueAsString(cmp.network);
                                } catch (IOException e) {
                                    log.debug("networkconfig settings:" + uiSet.value);
                                    log.error("Cannot process JSON from networkconfig settings", e);
                                }
                            }
                            ObjectMapper mapper = new ObjectMapper();
                            if (ServiceTemplateSettingType.RAIDCONFIGURATION.equals(set.getType()) &&
                                    StringUtils.isNotEmpty(set.getValue())) {
                                try {
                                    TemplateRaidConfiguration backend = BACKEND_MAPPER.readValue(
                                            uiSet.value, TemplateRaidConfiguration.class);
                                    cmp.raid = convertBackendRAIDConfigToUi(backend);
                                    uiSet.value = mapper.writeValueAsString(cmp.raid);
                                } catch (IOException e) {
                                    log.debug("RAIDconfig settings:" + uiSet.value);
                                    log.error("Cannot process JSON from RAIDconfig settings", e);
                                } catch (InvalidConfigXmlException e) {
                                    log.debug(
                                            "Missing required attribute(s) while converting backend RAID configuration to UI component");
                                    throw new AsmRuntimeException(
                                            ASMCommonsMessages.invalidServerXmlConfigurationFound(
                                                    e.getFaultComponent(),
                                                    e.getRequiredAttribute()));
                                }
                            } else if (ServiceTemplateSettingType.STORAGEPOOLDISKSCONFIGURATION.equals(set.getType()) &&
                                    StringUtils.isNotEmpty(set.getValue())) {
                                try {
                                    ScaleIODiskConfiguration backend = BACKEND_MAPPER.readValue(uiSet.value,
                                                                                                ScaleIODiskConfiguration.class);
                                    uiSet.value = mapper.writeValueAsString(backend);
                                } catch (IOException e) {
                                    log.debug("RAIDconfig settings:" + uiSet.value);
                                    log.error("Cannot process JSON from RAIDconfig settings", e);
                                }
                            } else if (ServiceTemplateSettingType.PROTECTIONDOMAINSETTINGS.equals(set.getType()) &&
                                    StringUtils.isNotEmpty(set.getValue())) {
                                try {
                                    List<ProtectionDomain> backend = mapper.readValue(uiSet.value,
                                                                                      new TypeReference<List<ProtectionDomain>>() {});
                                    uiSet.value = mapper.writeValueAsString(backend);
                                } catch (IOException e) {
                                    log.debug("RAIDconfig settings:" + uiSet.value);
                                    log.error("Cannot process JSON from RAIDconfig settings", e);
                                }
                            }


                            if (scaleup) {
                                if ((ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_HOSTNAME_ID.equals(set.getId()) ||
                                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_VM_NAME.equals(set.getId()))) {
                                    uiSet.value = "";
                                }
                            }
                        }
                    }
                    cmp.categories.add(uiCat);
                }

                if (sc.getAssociatedComponents().size() > 0) {
                    for (Map.Entry<String, Map<String, String>> entry : sc.getAssociatedComponents().entrySet()) {
                        UIRelatedComponent rc = new UIRelatedComponent();
                        rc.id = entry.getKey();
                        rc.name = entry.getValue().get(
                                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_COMPONENT_NAME);
                        rc.installOrder = entry.getValue().get(
                                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_COMPONENT_INSTALL_ORDER) != null ?
                                Integer.parseInt(entry.getValue().get(
                                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_COMPONENT_INSTALL_ORDER)) : null;
                        rc.subtype = entry.getValue().get(
                                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_COMPONENT_SERVICE_TYPE);
                        cmp.relatedcomponents.add(rc);
                    }
                }
            }
        }
        return cmp;
    }

    /*
     *  Legacy code has labels like Fabric A, Fabric B etc
     */
    private static void applyIncomingLabelFix(UIComponentNetwork cmpNet,
                                              ApplicationContext context) {
        if (cmpNet.interfaces != null) {
            for (UIComponentFabric fab : cmpNet.interfaces) {
                fab.name = createFabricLabel(fab.name, context);
            }
        }
    }

    private static String createFabricLabel(String fabName, ApplicationContext context) {
        switch (fabName) {
        case "Fabric A":
            return context.getMessage("FrabricNameType.FabricA", null,
                                      LocaleContextHolder.getLocale());
        case "Fabric B":
            return context.getMessage("FrabricNameType.FabricB", null,
                                      LocaleContextHolder.getLocale());
        case "Fabric C":
            return context.getMessage("FrabricNameType.FabricC", null,
                                      LocaleContextHolder.getLocale());
        }
        return fabName;
    }

    public static ServiceTemplate createTemplate(UITemplateBuilder template,
                                                 FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter) throws ControllerException {
        if (template == null)
            return null;
        ServiceTemplate serviceTemplate = new ServiceTemplate();
        if (template.id != null) {
            serviceTemplate.setId(template.id);
        }
        if (template.originalId != null) {
            serviceTemplate.setOriginalTemplateId(template.originalId);
        }
        if (template.name != null) {
            serviceTemplate.setTemplateName(template.name.trim());
        }
        serviceTemplate.setTemplateDescription(template.description);
        serviceTemplate.setTemplateType(template.source);
        if (template.createdBy != null)
            serviceTemplate.setCreatedBy(template.createdBy);
        if (template.createdDate != null)
            serviceTemplate.setCreatedDate(
                    DateTime.parse(template.createdDate).toGregorianCalendar());

        serviceTemplate.setDraft(template.draft);
        serviceTemplate.setCategory(template.category);
        serviceTemplate.setInConfiguration(template.inConfiguration);

        serviceTemplate.setManageFirmware(template.manageFirmware);
        if (template.manageFirmware && template.firmwarePackageId != null) {
            if (USE_DEFAULT_CATALOG_ID.equals(template.firmwarePackageId)) {
                serviceTemplate.setUseDefaultCatalog(true);
            } else {
                updateFirmwareRepositoryOnTemplate(firmwareRepositoryServiceAdapter,
                                                   template.firmwarePackageId,
                                                   serviceTemplate);
            }
        }

        if (template.components != null && template.components.size() > 0) {
            if (serviceTemplate.getComponents() == null) {
                serviceTemplate.setComponents(new ArrayList<>());
            }
            serviceTemplate.getComponents().addAll(createTemplateComponentList(template.components));
        }

        serviceTemplate.setAllUsersAllowed(template.allStandardUsers);
        serviceTemplate.setAssignedUsers(new HashSet<>());
        for (UIUser user : template.assignedUsers) {
            serviceTemplate.getAssignedUsers().add(
                    UsersController.getApplicationObject(user));
        }

        if (template.configureTemplateConfiguration != null) {
            serviceTemplate.setConfiguration(
                    ConfigureTemplateController.createConfigureTemplate(template));
        }
        return serviceTemplate;
    }

    public static List<ServiceTemplateComponent> createTemplateComponentList(final List<UITemplateBuilderComponent> comps) throws ControllerException {
        final List<ServiceTemplateComponent> components = new ArrayList<>();
        for (final UITemplateBuilderComponent comp : comps) {
            components.add(createTemplateComponent(comp));
        }
        return components;
    }

    static ServiceTemplateComponent createTemplateComponent(final UITemplateBuilderComponent comp) throws ControllerException {
        ServiceTemplateComponent res = new ServiceTemplateComponent();
        res.setComponentID(comp.componentid);
        res.setId(comp.id);
        res.setName(comp.name);
        res.setHelpText(comp.helptext);
        res.setIdentifier(comp.identifier);
        res.setType(ServiceTemplateComponentType.valueOf(
                getDeviceTypeFromUIType(comp.type)));
        if (comp.instances > 0) {
            res.setInstances(comp.instances);
        }
        res.setAsmGUID(comp.AsmGUID);
        res.setRefId(comp.referenceid);
        res.setIP(comp.referenceip);
        res.setConfigFile(comp.configfilename);
        res.setCloned(comp.cloned);
        res.setClonedFromId(comp.clonedFromId);
        if (comp.subtype != null) {
            res.setSubType(ServiceTemplateComponentSubType.fromValue(comp.subtype));
        }
        parseRelatedComponents(res, comp);

        if (comp.categories != null && comp.categories.size() > 0) {
            if (res.getResources() == null)
                res.setResources(new ArrayList<>());

            for (UITemplateBuilderCategory cat : comp.categories) {
                ServiceTemplateCategory stc = createTemplateCategory(cat);
                if (stc != null)
                    res.getResources().add(stc);
            }
        }

        // When a user modifies a template that's been used to create a storage and changes the storage volume
        // from create new, to use existing, we need to clear out the old new value or it will cause problems at
        // deployment time with the storage (validation will fail).

        // TODO:  Temporarily disabled as part of the ASM-5374 temporary fix.
        //        Leaving here until we have solution after the 8.2 release.

//        if(ServiceTemplateComponentType.STORAGE.equals(res.getType())) {
//           ServiceTemplateSetting titleSetting = res.getTemplateSetting(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_TITLE_ID);
//           ServiceTemplateSetting createTitleSetting = res.getTemplateSetting(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_TITLE);
//
//           if(titleSetting != null && createTitleSetting != null) {
//               if(!ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX.equals(titleSetting.getValue())) {
//                   createTitleSetting.setValue(null);
//               }
//           }
//        }

        return res;
    }

    private static ServiceTemplateCategory createTemplateCategory(UITemplateBuilderCategory cat) throws ControllerException {
        ServiceTemplateCategory res = new ServiceTemplateCategory();

        res.setId(cat.id);
        res.setDisplayName(cat.name);

        if (cat.settings != null && cat.settings.size() > 0) {
            if (res.getParameters() == null)
                res.setParameters(new ArrayList<>());

            for (UITemplateBuilderSetting set : cat.settings) {
                ServiceTemplateSetting sts = createTemplateSetting(set);
                if (sts != null) {
                    res.getParameters().add(sts);
                }
            }
        }

        return res;
    }

    private static ServiceTemplateSetting createTemplateSetting(UITemplateBuilderSetting set) throws ControllerException {
        ServiceTemplateSetting res = new ServiceTemplateSetting();

        res.setDependencyTarget(set.dependencyTarget);
        res.setDependencyValue(set.dependencyValue);
        res.setRequiredAtDeployment(set.requireatdeployment);
        res.setHideFromTemplate(set.hidefromtemplate);
        res.setRequired(set.required);
        res.setDisplayName(set.name);
        res.setMax(set.max);
        res.setMin(set.min);
        res.setToolTip(set.tooltip);
        res.setReadOnly(set.readOnly);
        res.setGenerated(set.generated);
        res.setGroup(set.group);
        res.setInfoIcon(set.useinfotooltip);
        res.setMaxLength(set.maxlength);
        res.setStep(set.step);
        res.setOptionsSortable(set.isOptionsSortable);
        res.setPreservedForDeployment(set.isPreservedForDeployment);

        
        res.setId(set.id);
        res.setType(getTemplateSettingDataType(set.datatype));

        if (ServiceTemplateSetting.ServiceTemplateSettingType.PASSWORD.equals(res.getType()) &&
                UICredential.CURRENT_PASSWORD.equals(set.value)) {
            res.setValue(null);
        } else if (res.getType() == ServiceTemplateSettingType.NETWORKCONFIGURATION && !StringUtils.isEmpty(
                set.value)) {
            // sanity check to verify UI has passed a valid JSON
            ObjectMapper mapper = new ObjectMapper();
            UIComponentNetwork cmpNet;
            try {
                cmpNet = mapper.readValue(sanitizeString(set.value), UIComponentNetwork.class);
                applyOutgoingLabelFix(cmpNet);
                NetworkConfiguration backend = convertUiNetworkConfigToBackend(cmpNet);
                String json = BACKEND_MAPPER.writeValueAsString(backend);
                res.setValue(json);
            } catch (IOException e) {
                log.debug("networkconfig settings:" + set.value);
                log.error("Cannot process JSON from networkconfig settings", e);
                throw new ControllerException("Cannot process JSON from networkconfig settings");
            }
        } else if (ServiceTemplateSettingType.RAIDCONFIGURATION.equals(res.getType()) &&
                StringUtils.isNotEmpty(set.value)) {
            ObjectMapper mapper = new ObjectMapper();
            UIComponentRaid cmpRaid;
            try {
                cmpRaid = mapper.readValue(sanitizeString(set.value), UIComponentRaid.class);
                TemplateRaidConfiguration backend = convertUiRaidConfigToBackend(cmpRaid);
                String json = BACKEND_MAPPER.writeValueAsString(backend);
                res.setValue(json);
            } catch (IOException e) {
                log.debug("raidconfig settings:" + set.value);
                log.error("Cannot process JSON from raidconfig settings", e);
                throw new ControllerException("Cannot process JSON from raidconfig settings");
            }
        } else if (ServiceTemplateSettingType.STORAGEPOOLDISKSCONFIGURATION.equals(res.getType()) &&
                StringUtils.isNotEmpty(set.value)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                ScaleIODiskConfiguration diskConfiguration = mapper.readValue(sanitizeString(set.value), ScaleIODiskConfiguration.class);
                String json = BACKEND_MAPPER.writeValueAsString(diskConfiguration);
                res.setScaleIODiskConfiguration(diskConfiguration);
                res.setValue(json);
            } catch (IOException e) {
                log.debug("raidconfig settings:" + set.value);
                log.error("Cannot process JSON from scaleIODisckConfiguration settings", e);
                throw new ControllerException("Cannot process JSON from scaleIODisckConfiguration settings");
            }
        }  else if (ServiceTemplateSettingType.PROTECTIONDOMAINSETTINGS.equals(res.getType()) &&
                StringUtils.isNotEmpty(set.value)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<ProtectionDomain> protectionDomainSettings = mapper.readValue(set.value, new TypeReference<List<ProtectionDomain>>() {});
                res.setProtectionDomainSettings(protectionDomainSettings);
                res.setValue(sanitizeString(set.value));
            } catch (IOException e) {
                log.debug("raidconfig settings:" + set.value);
                log.error("Cannot process JSON from scaleIODisckConfiguration settings", e);
                throw new ControllerException("Cannot process JSON from scaleIODisckConfiguration settings");
            }
        } else {
            // strip leading coma if present. UI artifact.
            if (set.value != null && set.value.startsWith(",") && set.value.length() > 1)
                res.setValue(set.value.substring(1));
            else
                res.setValue(set.value);
        }

        if (set.options != null && set.options.size() > 0) {
            for (UITemplateBuilderListItem option : set.options) {
                ServiceTemplateOption opt = new ServiceTemplateOption();
                opt.setName(option.name);
                opt.setValue(option.id);
                opt.setDependencyTarget(option.dependencyTarget);
                opt.setDependencyValue(option.dependencyValue);

                res.getOptions().add(opt);
            }
        }

        return res;
    }

    private static void applyOutgoingLabelFix(UIComponentNetwork cmpNet) {
        if (cmpNet.interfaces != null) {
            for (UIComponentFabric fab : cmpNet.interfaces) {
                String[] fabs = fab.name.split("/");
                if (fabs.length > 1) {
                    fab.name = fabs[0].trim();
                }
            }
        }
    }

    public static void parseRelatedComponents(ServiceTemplateComponent component,
                                              UITemplateBuilderComponent uiComponent) {
        Map<Integer, String> installationOrder = new HashMap<>();
        int highestOrder = -1;
        if (uiComponent.relatedcomponents != null && uiComponent.relatedcomponents.size() > 0) {
            for (UIRelatedComponent rc : uiComponent.relatedcomponents) {
                component.addAssociatedComponentName(rc.id, rc.name);
                if (rc.installOrder != null && rc.installOrder >= 0) {
                    if (installationOrder.get(rc.installOrder) == null) {
                        installationOrder.put(rc.installOrder, rc.id);
                        if (highestOrder < rc.installOrder) {
                            highestOrder = rc.installOrder;
                        }
                    } else {
                        highestOrder++;
                        installationOrder.put(highestOrder, rc.id);
                    }
                }
                if (rc.subtype != null) {
                    Map<String, String> properties = component.getAssociatedComponents().get(rc.id);
                    properties.put(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_COMPONENT_SERVICE_TYPE,
                                   rc.subtype);
                }
            }
        }
        for (Map.Entry<Integer, String> entry : installationOrder.entrySet()) {
            Map<String, String> properties = component.getAssociatedComponents().get(entry.getValue());
            properties.put(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_COMPONENT_INSTALL_ORDER,
                           entry.getKey().toString());
        }
    }

    public static String getDeviceTypeFromTypeName(String type) {
        String deviceType = "";
        if (type != null) {
            switch (type) {
            case "TOR":
                deviceType = "switch";
                break;
            case "STORAGE":
                deviceType = "storage";
                break;
            case "SERVER":
                deviceType = "server";
                break;
            case "CLUSTER":
                deviceType = "cluster";
                break;
            case "VIRTUALMACHINE":
                deviceType = "vm";
                break;
            case "SERVICE":
                deviceType = "application";
                break;
            case "SCALEIO":
                deviceType = "scaleio";
                break;
            default:
                break;
            }
        }
        return deviceType;
    }

    public static String getDeviceTypeFromUIType(String type) {
        String deviceType = "";
        if (type != null) {
            switch (type) {
            case "switch":
                deviceType = "TOR";
                break;
            case "storage":
                deviceType = "STORAGE";
                break;
            case "server":
                deviceType = "SERVER";
                break;
            case "cluster":
                deviceType = "CLUSTER";
                break;
            case "vm":
                deviceType = "VIRTUALMACHINE";
                break;
            case "application":
                deviceType = "SERVICE";
                break;
            case "scaleio":
                deviceType = "SCALEIO";
                break;
            default:
                break;
            }
        }
        return deviceType;
    }

    private static String sanitizeString(String value) {
        String newValue = value.replace("\\\"", "\"");
        newValue = newValue.replace("\"{", "{");
        newValue = newValue.replace("}\"", "}");
        return newValue;
    }

    public static void updateFirmwareRepositoryOnTemplate(
            final FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter,
            final String firmwarePackageId, ServiceTemplate serviceTemplate) {
        if (firmwarePackageId != null) {
            FirmwareRepository firmware = firmwareRepositoryServiceAdapter.getById(
                    firmwarePackageId);
            if (firmware != null) {
                serviceTemplate.setUseDefaultCatalog(false);
                // Only want to save id and name at this time.  ServiceTemplate should not have full firmawarerepository object in it.
                FirmwareRepository newRepository = new FirmwareRepository();
                newRepository.setId(firmware.getId());
                newRepository.setName(firmware.getName());
                serviceTemplate.setFirmwareRepository(newRepository);
            }
        }
    }

    /**
     * Get template list.
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "gettemplatelist", method = RequestMethod.POST)
    public JobResponse getTemplates(@RequestBody JobRequest request) {

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_TEMPLATES, "name");
        JobResponse jobResponse = getTemplates(request, true, true, -1, options, Boolean.TRUE);
        return jobResponse;
    }

    /**
     * Get template list with draft = false.
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "getreadytemplatelist", method = RequestMethod.POST)
    public JobResponse getReadyTemplates(@RequestBody JobRequest request) {

       RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_TEMPLATES, "name");

        // Need to make sure we get all templates
        options.offset = 0;
        options.limit = MappingUtils.MAX_RECORDS;

        JobResponse jobResponse = getTemplates(request, false, false, -1, options,Boolean.FALSE);
        return jobResponse;
    }

    /**
     * Get quick template list - 5 latest sorted by name
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "getquicktemplatelist", method = RequestMethod.POST)
    public JobResponse getQuickTemplates(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            List<String> filter = new ArrayList<>();
            filter.add("eq,draft,false");
            filter.add("eq,templateLocked,false");
            ResourceList<ServiceTemplate> allTemplates = templateServiceAdapter.getAllTemplates("-updatedDate",
                                                                                                filter,
                                                                                                0,
                                                                                                5,
                                                                                                Boolean.FALSE,
                                                                                                Boolean.FALSE);
            if (allTemplates != null) {
                List<ServiceTemplate> filteredTemplates = Arrays.asList(allTemplates.getList());
                jobResponse.responseObj = convertServiceTemplateListToUITemplateList(filteredTemplates);
                jobResponse.criteriaObj = request.criteriaObj;
                if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                    Long total = Long.valueOf(allTemplates.getTotalRecords());
                    jobResponse.criteriaObj.paginationObj.totalItemsCount = total.intValue();
                }
            }

        } catch (Throwable t) {
            log.error("getTemplates() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private List<UITemplate> convertServiceTemplateListToUITemplateList(
            List<ServiceTemplate> resultList) {
        List<UITemplate> responseObj = new ArrayList<>();

        for (ServiceTemplate dto : resultList) {
            UITemplate template = parseTemplate(dto);
            responseObj.add(template);
        }
        return responseObj;
    }

    /**
     * Return template list.
     *
     * @param request
     * @param isDraftAllowed
     *            If true, return all templates, draft and published
     * @param limit
     *            if >0 return no more than set here
     * @param includeAttachments
     * @return
     */
    private JobResponse getTemplates(JobRequest request,
                                     boolean isDraftAllowed,
                                     boolean isSampleAllowed,
                                     int limit,
                                     RESTRequestOptions options,
                                     Boolean includeAttachments) {
        JobResponse jobResponse = new JobResponse();
        List<UITemplate> responseObj = new ArrayList<>();

        int cnt = 0;
        try {

            if (options.filterList == null ) {
                options.filterList = new ArrayList<>();
            }
            if (!isDraftAllowed) {
                options.filterList.add("eq,draft,false");
            }
            if (!isSampleAllowed) {
                options.filterList.add("eq,templateLocked,false");
            }
            ResourceList<ServiceTemplate> mList = templateServiceAdapter.getAllTemplates(
                    options.sortedColumns, options.filterList,
                    options.offset < 0 ? null : options.offset,
                    options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit, Boolean.FALSE, includeAttachments);
            if (mList != null && mList.getList() != null) {
                if (mList.getList().length > 0) {
                    // sort list by template name
                    List<ServiceTemplate> sList = Arrays.asList(mList.getList());

                    sList.sort(new TemplatesComparator());

                    for (ServiceTemplate serviceTemplate : sList) {
                        if (limit > 0 && cnt == limit)
                            break;

                        //serviceTemplate.sortParametersByDisplayName();
                        UITemplate template = parseTemplate(serviceTemplate);
                        responseObj.add(template);
                        cnt++;
                    }
                } else {
                    if (mList.getTotalRecords() > 0 && request.criteriaObj != null && request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    (int) mList.getTotalRecords());
                        return getTemplates(newRequest);
                    }
                }
            }

            jobResponse.responseObj = responseObj;
            jobResponse.criteriaObj = request.criteriaObj;
            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null && mList != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = cnt;
            }

        } catch (Throwable t) {
            log.error("getTemplates() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Helper to parse DTO.
     *
     * @param dto
     * @return
     */
    private UITemplate parseTemplate(ServiceTemplate dto) {
        UITemplate tmpl = new UITemplate();

        tmpl.id = dto.getId();
        tmpl.name = dto.getTemplateName();
        tmpl.createdBy = dto.getCreatedBy();
        tmpl.createdDate = MappingUtils.getTime(dto.getCreatedDate());
        tmpl.description = dto.getTemplateDescription();
        tmpl.type = dto.getTemplateType();
        tmpl.updatedBy = dto.getUpdatedBy();
        tmpl.updatedDate = MappingUtils.getTime(dto.getUpdatedDate());
        tmpl.draft = dto.isDraft();
        tmpl.lastDeployed = MappingUtils.getTime(dto.getLastDeployedDate());
        tmpl.category = dto.getCategory();
        tmpl.isTemplateValid = dto.getTemplateValid().isValid();
        tmpl.isLocked = dto.isTemplateLocked();
        tmpl.inConfiguration = dto.isInConfiguration();
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            for (String fileName : dto.getAttachments()) {
                //Attachments don't have real IDs, so just setting the ID to the file name, which should be unique on the file system anyhow.
                tmpl.attachments.add(new UIListItem(fileName, fileName));
            }
        }

        if (dto.getStorageCount() != null && dto.getStorageCount() > 0 ) {
            tmpl.containsStorage = true;
        }
        if (dto.getClusterCount() != null && dto.getClusterCount() > 0 ) {
            tmpl.containsCluster = true;
        }
        if (dto.getServerCount() != null && dto.getServerCount() > 0 ) {
            tmpl.containsServer = true;
        }
        if (dto.getServiceCount() != null && dto.getServiceCount() > 0 ) {
            tmpl.containsApplication = true;
        }
        if (dto.getSwitchCount() != null && dto.getSwitchCount() > 0 ) {
            tmpl.containsSwitch = true;
        }
        if (dto.getVmCount() != null && dto.getVmCount() > 0 ) {
            tmpl.containsVM = true;
        }
        return tmpl;
    }

    /**
     * Get template list.
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "createtemplate", method = RequestMethod.POST)
    public JobResponse createTemplate(@RequestBody JobCreateTemplateRequest request) {
        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();
        try {
            ServiceTemplate configuration;
            ServiceTemplate responseConfiguration;
            if (request.requestObj != null) {
                if (!StringUtils.isEmpty(request.requestObj.cloneexistingtemplateid)) {
                    configuration = createServiceTemplate(request.requestObj
                    );
                    responseConfiguration = templateServiceAdapter.copyTemplate(
                            request.requestObj.cloneexistingtemplateid, configuration);
                    jobResponse.responseObj = parseTemplateBuilder(responseConfiguration,
                                                                   getApplicationContext());
                } else {
                    configuration = createServiceTemplate(request.requestObj
                    );
                    responseConfiguration = templateServiceAdapter.createTemplate(configuration);
                    jobResponse.responseObj = parseTemplateBuilder(responseConfiguration,
                                                                   getApplicationContext());
                }

            }
        } catch (Throwable t) {
            log.error("createTemplate- Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;

    }

    @RequestMapping(value = "discardtemplate", method = RequestMethod.POST)
    public JobResponse discardTemplate(@RequestBody JobStringsRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            for (String id : request.requestObj) {
                try {
                    templateServiceAdapter.deleteTemplate(id);
                } catch (Throwable t) {
                    log.error("discardTemplate() - Exception from service call", t);
                    jobResponse = addFailureResponseInfo(t);
                }
            }

        } catch (Throwable t) {
            log.error("discardTemplate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private ServiceTemplate createServiceTemplate(UITemplateBuilderCreate template) {
        if (template == null)
            return null;
        ServiceTemplate configuration = new ServiceTemplate();
        if (template.id != null) {
            // only set id if found and not cloneexistingtemplateid
            configuration.setId(template.id);
        }
        configuration.setDraft(true);
        configuration.setTemplateName(template.name.trim());
        configuration.setTemplateDescription(template.description);
        configuration.setCategory(template.category);
        configuration.setAllUsersAllowed(template.allStandardUsers);
        configuration.setManageFirmware(template.manageFirmware);
        if (template.manageFirmware && template.firmwarePackageId != null) {
            if (USE_DEFAULT_CATALOG_ID.equals(template.firmwarePackageId)) {
                configuration.setUseDefaultCatalog(true);
            } else {
                updateFirmwareRepositoryOnTemplate(firmwareRepositoryServiceAdapter,
                                                   template.firmwarePackageId, configuration);
            }
        }

        configuration.setAssignedUsers(new HashSet<>());
        for (UIUser user : template.assignedUsers) {
            configuration.getAssignedUsers().add(
                    UsersController.getApplicationObject(user));
        }

        return configuration;

    }

    /**
     * templates/gettemplatebuilderbyid
     *
     * @param request
     *            the id request
     * @return device
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "gettemplatebuilderbyid", method = RequestMethod.POST)
    public JobResponse getTemplateBuilderById(@RequestBody JobTemplateBuilderRequest request) {

        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();

        try {
            ServiceTemplate sTemplate = templateServiceAdapter.getTemplate(request.requestObj.id,
                                                                           false,
                                                                           request.requestObj.deploy);
            if (request.requestObj.deploy) {
                sTemplate = templateServiceAdapter.updateParameters(sTemplate);
            }
            jobResponse.responseObj = parseTemplateBuilder(sTemplate,
                                                           getApplicationContext(),
                                                           false);
        } catch (Throwable t) {
            log.error("getTemplateBuilderById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * templates/getclonedcomponentfromtemplate
     *
     * @param request
     *            UITemplateBuilderComponent
     * @return device
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getclonedcomponentfromtemplate", method = RequestMethod.POST)
    public JobResponse getClonedComponentFromTemplate(
            @RequestBody JobGetImportConfigComponentRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            ServiceTemplate thisTemplate = templateServiceAdapter.getTemplate(
                    request.requestObj.referenceId);
            if (thisTemplate != null && CollectionUtils.isNotEmpty(thisTemplate.getComponents())) {
                for (ServiceTemplateComponent component : thisTemplate.getComponents()) {
                    if (component.getId().equals(request.requestObj.componentId)) {
                        for (ServiceTemplateCategory category : component.getResources()) {
                            for (ServiceTemplateSetting setting : category.getParameters()) {
                                if (ServiceTemplateSetting.ServiceTemplateSettingType.PASSWORD.equals(
                                        setting.getType())) {
                                    setting.setValue(
                                            ServiceTemplateSettingIDs.SERVICE_TEMPLATE_PASSWORD_DEFAULT_TO_REMOVE);
                                }
                            }
                        }
                        UITemplateBuilderComponent tbc = parseTemplateBuilderComponent(component,
                                                                                       getApplicationContext(),
                                                                                       false, 0,
                                                                                       false);
                        jobResponse.responseObj = tbc;
                        break;
                    }
                }
            } else {
                log.error("Missed template for device: " + request.requestObj.referenceId);
            }
        } catch (Throwable t) {
            log.error("getclonedcomponentfromtemplate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * templates/gettemplatebuilderbyid
     *
     * @param request
     *            the request
     * @return device
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "gettemplatebuilderlist", method = RequestMethod.POST)
    public JobResponse getTemplateBuilderList(@RequestBody JobRequest request) {

        JobResponse jobResponse = new JobResponse();
        List<UITemplateBuilder> list = new ArrayList<>();
        Locale locale = LocaleContextHolder.getLocale();

        try {
            ResourceList<ServiceTemplate> allTemplates = templateServiceAdapter.getAllTemplates(
                    null, null, null, MappingUtils.MAX_RECORDS, Boolean.TRUE, Boolean.TRUE);
            if (allTemplates != null && allTemplates.getList() != null) {
                for (ServiceTemplate sTemplate : allTemplates.getList()) {
//                    sTemplate.sortParametersByDisplayName();
                    list.addAll(getServerComponentsWithReferenceConfiguration(sTemplate, locale));
                }
            }
            jobResponse.responseObj = list;

        } catch (Throwable t) {
            log.error("getTemplateBuilderList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    public List<UITemplateBuilder> getServerComponentsWithReferenceConfiguration(
            ServiceTemplate sTemplate, Locale locale) throws ControllerException {
        ArrayList<UITemplateBuilder> list = new ArrayList<>();

        List<ServiceTemplateComponent> components = sTemplate.getComponents();
        for (ServiceTemplateComponent component : components) {
            if (component.getComponentID() != null) {
                if (component.getType().equals(ServiceTemplateComponentType.SERVER)) {
                    ServiceTemplateCategory resource = component.getTemplateResource(
                            ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_IDRAC_RESOURCE);
                    if (resource != null) {
                        List<ServiceTemplateSetting> parameters = resource.getParameters();
                        for (ServiceTemplateSetting parameter : parameters) {
                            if (parameter.getId().equals(
                                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_CONFIG_XML) && !StringUtils.isEmpty(
                                    parameter.getValue())) {
                                UITemplateBuilder builder = parseTemplateBuilder(sTemplate,
                                                                                 getApplicationContext()
                                                                                );
                                list.add(builder);
                            }
                        }
                    }
                }
            }
        }

        return list;
    }

    /**
     * templates/gettemplatebuildercomponents
     *
     * @param request
     *            the id request: component type i.e. server, vm, storage etc
     * @return device
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "gettemplatebuildercomponents", method = RequestMethod.POST)
    public JobResponse getTemplateBuilderComponents(@RequestBody JobTemplateBuilderRequest request) {

        JobResponse jobResponse = new JobResponse();
        try {
            List<UITemplateBuilderComponent> list = new ArrayList<>();
            if (request.requestObj.id != null) {
                // get the default template
                ServiceTemplate defaultTemplate = templateServiceAdapter.getCustomizedComponent(request.requestObj.templateId,
                                                                                                request.requestObj.serviceId,
                                                                                                request.requestObj.id);
                defaultTemplate.sortInternals();

                int cmpCnt = 0;
                String componentType;
                for (ServiceTemplateComponent component : defaultTemplate.getComponents()) {
                    switch(component.getType()) {
                    case SCALEIO:
                        componentType = ServiceTemplateComponentType.CLUSTER.getLabel();
                        break;
                    case STORAGE:
                        ServiceTemplateSetting volumeSetting = component.getParameter(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_FLEXOS_VOLUME_RESOURCE_ID,
                                                                                      ServiceTemplateSettingIDs.SERVICE_TEMPLATE_FLEXOS_VOLUME_NAME_ID);
                        //if missing volume name setting or options is empty return null for responseObj
                        if (null == volumeSetting ||
                                CollectionUtils.isEmpty(volumeSetting.getOptions())) {
                            list = null;
                            componentType = "";
                        } else {
                            componentType = component.getType().getLabel();
                        }
                        break;
                    default:
                        componentType = component.getType().getLabel();
                        break;
                    }
                    if (componentType.equalsIgnoreCase(request.requestObj.id)) {
                        UITemplateBuilderComponent tbc = parseTemplateBuilderComponent(component,
                                                                                       getApplicationContext(),
                                                                                       false, cmpCnt,
                                                                                       false);
                        if (tbc != null) {
                            cmpCnt++;
                            list.add(tbc);
                        }
                    }
                }
            }
            jobResponse.responseObj = list;
        } catch (Throwable t) {
            log.error("getTemplateBuilderComponents() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * templates/getupdatedtemplatebuildercomponent
     *
     * @param request
     *            the id request: component type i.e. server, vm, storage etc
     * @return device
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getupdatedtemplatebuildercomponent", method = RequestMethod.POST)
    public JobResponse getUpdatedTemplateBuilderComponent(
            @RequestBody JobUpdatedTemplateBuilderComponentRequest request) {

        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();

        try {
            // make up empty template with a single component
            ServiceTemplate template = new ServiceTemplate();
            template.setId("1");
            template.getComponents().add(createTemplateComponent(request.requestObj.component));

            template = templateServiceAdapter.updateComponents(request.requestObj.templateId,
                                                               request.requestObj.serviceId,
                                                               template);

            UITemplateBuilder builder = parseTemplateBuilder(template, getApplicationContext()
                                                            );

            for (UITemplateBuilderComponent component : builder.components) {
                // there will be only one
                jobResponse.responseObj = component;
            }

        } catch (Throwable t) {
            log.error("getUpdatedTemplateBuilderComponent() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * templates/getreferencecomponent
     *
     * @param request
     *            UITemplateBuilderComponent
     * @return device
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getreferencecomponent", method = RequestMethod.POST)
    public JobResponse getReferenceComponent(@RequestBody JobGetReferenceComponentRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            ServiceTemplate defaultTemplate = templateServiceAdapter.getCustomizedTemplate(
                    request.requestObj.referenceId);
            if (defaultTemplate != null && CollectionUtils.isNotEmpty(
                    defaultTemplate.getComponents())) {
                for (ServiceTemplateComponent component : defaultTemplate.getComponents()) {
                    if (component.getRefId() != null &&
                            component.getRefId().equals(request.requestObj.referenceId) &&
                            component.getId().equals(request.requestObj.componentId)) {
                        UITemplateBuilderComponent tbc = parseTemplateBuilderComponent(component,
                                                                                       getApplicationContext(),
                                                                                       false, 0,
                                                                                       false);
                        // must preserve component subtype currently stored as 'id', UI won't do it!
                        tbc.componentid = tbc.id;
                        // and generate new ID, because UI won't do it and if user adds the second server there will be dups
                        tbc.id = UUID.randomUUID().toString();
                        jobResponse.responseObj = tbc;
                        break;
                    }
                }
            } else {
                log.error("Missed template for device: " + request.requestObj.referenceId);
            }
        } catch (Throwable t) {
            log.error("getReferenceComponent() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Save template.
     *
     * @param request
     *            the request
     * @return the template details
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "savetemplate", method = RequestMethod.POST)
    public JobResponse saveTemplate(@RequestBody JobSaveTemplateRequest request) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseCode = 0;
        try {
            ServiceTemplate template = createTemplate(request.requestObj,
                                                      firmwareRepositoryServiceAdapter);
            templateServiceAdapter.updateTemplate(template.getId(), template);
            jobResponse.responseObj = request.requestObj;
        } catch (Throwable t) {
            log.error("saveTemplate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Validate template.
     *
     * @param request
     *            the request
     * @return the template details
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "validatetemplate", method = RequestMethod.POST)
    public JobResponse validateTemplate(@RequestBody JobSaveTemplateRequest request) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseCode = 0;
        try {
            ServiceTemplate template = createTemplate(request.requestObj,
                                                      firmwareRepositoryServiceAdapter);
            // TODO: validate
            jobResponse.responseObj = request.requestObj;
        } catch (Throwable t) {
            log.error("validateTemplate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "importtemplate", method = RequestMethod.POST)
    public JobResponse importTemplate(@RequestBody JobImportTemplateRequest request) {

        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();

        try {
            if (request.requestObj != null && request.requestObj.id != null
                    && request.requestObj.id.compareToIgnoreCase(
                    request.requestObj.importId) != 0) {
                ServiceTemplate template = templateServiceAdapter.getTemplate(
                        request.requestObj.id);

                if (template != null) {
                    if (request.requestObj.importId != null && request.requestObj.id.compareToIgnoreCase(
                            request.requestObj.importId) != 0) {
                        ServiceTemplate TemplateToImport = templateServiceAdapter.getTemplate(
                                request.requestObj.importId);
                        if (TemplateToImport != null && TemplateToImport.getComponents() != null) {
                            for (int i = 0; i < TemplateToImport.getComponents().size(); i++) {

                                //Need to set all passwords to default to asm becuase they will be passed as null from the back end to here
                                for (ServiceTemplateComponent component : TemplateToImport.getComponents()) {
                                    for (ServiceTemplateCategory category : component.getResources()) {
                                        for (ServiceTemplateSetting setting : category.getParameters()) {
                                            if (ServiceTemplateSetting.ServiceTemplateSettingType.PASSWORD.equals(
                                                    setting.getType()))
                                                setting.setValue("asm");
                                        }
                                    }
                                }

                                template.getComponents().add(
                                        TemplateToImport.getComponents().get(i));
                            }
                        }
                    }

                    templateServiceAdapter.updateTemplate(template.getId(), template);
                    jobResponse.responseObj = parseTemplateBuilder(template,
                                                                   getApplicationContext());
                }
            }

        } catch (Throwable t) {
            log.error("importTemplate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the Fabric Purposes.
     *
     * @return the Fabric Purposes
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getfabricpurposes", method = RequestMethod.POST)
    public JobResponse getFabricPurposes() {
        JobResponse jobResponse = new JobResponse();
        List<UIListItem> result = new ArrayList<>();
        Locale locale = LocaleContextHolder.getLocale();

        result.add(
                new UIListItem(
                        getApplicationContext().getMessage("FrabricPurposeID.AllLAN", null, locale),
                        getApplicationContext().getMessage("FrabricPurposeName.AllLAN", null,
                                                           locale))
        );

        result.add(
                new UIListItem(
                        getApplicationContext().getMessage("FrabricPurposeID.AllSAN", null, locale),
                        getApplicationContext().getMessage("FrabricPurposeName.AllSAN", null,
                                                           locale))
        );

        result.add(
                new UIListItem(
                        getApplicationContext().getMessage("FrabricPurposeID.PublicLAN", null,
                                                           locale),
                        getApplicationContext().getMessage("FrabricPurposeName.PublicLAN", null,
                                                           locale))
        );

        result.add(
                new UIListItem(
                        getApplicationContext().getMessage("FrabricPurposeID.PublicSAN", null,
                                                           locale),
                        getApplicationContext().getMessage("FrabricPurposeName.PublicSAN", null,
                                                           locale))
        );

        result.add(
                new UIListItem(
                        getApplicationContext().getMessage("FrabricPurposeID.PrivateLAN", null,
                                                           locale),
                        getApplicationContext().getMessage("FrabricPurposeName.PrivateLAN", null,
                                                           locale))
        );

        result.add(
                new UIListItem(getApplicationContext().getMessage("FrabricPurposeID.SANiSCSI", null,
                                                                  locale),
                               getApplicationContext().getMessage("FrabricPurposeName.SANiSCSI",
                                                                  null, locale))
        );

        result.add(
                new UIListItem(getApplicationContext().getMessage("FrabricPurposeID.SANFCoE", null,
                                                                  locale),
                               getApplicationContext().getMessage("FrabricPurposeName.SANFCoE",
                                                                  null, locale))
        );

        result.add(
                new UIListItem(
                        getApplicationContext().getMessage("FrabricPurposeID.Converged", null,
                                                           locale),
                        getApplicationContext().getMessage("FrabricPurposeName.Converged", null,
                                                           locale))
        );

        result.add(
                new UIListItem(
                        getApplicationContext().getMessage("FrabricPurposeID.None", null, locale),
                        getApplicationContext().getMessage("FrabricPurposeName.None", null, locale))
        );

        jobResponse.responseObj = result;
        return jobResponse;
    }

    /**
     *
     * @param response
     *
     * @deprecated - Use com.dell.asm.ui.controller.DownloadController - "downloads/getfile" instead
     */
    @Deprecated
    @RequestMapping(value = "exporttemplate", method = RequestMethod.POST)
    public JobResponse exportTemplate(MultipartHttpServletRequest request,
                                      HttpServletResponse response) {
        log.debug("exportTemplate - Called by UI");
        JobResponse jobResponse = new JobResponse();
        try {
            String encryptionPassword = request.getParameter("encryptionPassword");
            if (encryptionPassword == null) {
                encryptionPassword = ""; // Must be an empty String or will result in null pointer
            }
            String useEncPwdFromBackupStr = request.getParameter(
                    "useEncPwdFromBackup"); // cam be null
            boolean useEncPwdFromBackup = (useEncPwdFromBackupStr != null && useEncPwdFromBackupStr.equals(
                    "true"));
            String templateId = request.getParameter("templateId");

            ServiceTemplate svc = templateServiceAdapter.getTemplate(templateId);
            if (svc == null) {
                throw new ControllerException("Template not found!");
            }

            ServiceTemplateExportRequest serviceTemplateExportRequest = new ServiceTemplateExportRequest();
            serviceTemplateExportRequest.setServiceTemplate(svc);
            serviceTemplateExportRequest.setEncryptionPassword(encryptionPassword);
            serviceTemplateExportRequest.setUseEncPwdFromBackup(useEncPwdFromBackup);

            String tFileName = templateServiceAdapter.exportTemplate(serviceTemplateExportRequest);

            if (tFileName == null)
                throw new ControllerException("Export failed");

            File file = new File(tFileName);
            InputStream is = new FileInputStream(file);

            String fileName = (request.getParameter("fileName") != null) ? request.getParameter(
                    "fileName") :
                    getApplicationContext().getMessage("ExportTemplateFileName", null,
                                                       LocaleContextHolder.getLocale());

            response.setStatus(200);
            response.setContentLength((int) file.length());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                               "attachment; filename=\"" + fileName + ".gpg\"");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");

            ServletOutputStream out = response.getOutputStream();
            IOUtils.copy(is, out);

            out.flush();
            out.close();
            is.close();

            file.delete();

        } catch (Throwable t) {
            log.error("exportTemplate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "uploadtemplate", method = RequestMethod.POST)
    public JobResponse uploadTemplate(MultipartHttpServletRequest request) {
        log.debug("uploadTemplate - Called by UI");
        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();

        try {
            ServiceTemplateUploadRequest uRequest = new ServiceTemplateUploadRequest();
            List<String> assignedUsers = new ArrayList<>();
            uRequest.setAssignedUsers(assignedUsers);

            uRequest.setEncryptionPassword(request.getParameter("encryptionPassword"));
            String useEncPwdFromBackup = request.getParameter("useEncPwdFromBackup"); // cam be null
            if (useEncPwdFromBackup != null) {
                uRequest.setUseEncPwdFromBackup(useEncPwdFromBackup.equals("true"));
            }
            uRequest.setTemplateName(request.getParameter("templateName"));
            if (request.getParameter("category") != null && request.getParameter(
                    "category").length() > 0) {
                uRequest.setCategory(request.getParameter("category"));
            }
            String createCategory = request.getParameter("createCategory");
            if (createCategory != null && createCategory.length() > 0) {
                uRequest.setCreateCategory(true);
                uRequest.setCategory(createCategory);
            }
            uRequest.setDescription(request.getParameter("description"));

            String allStandardUsers = request.getParameter("allStandardUsers");
            if (allStandardUsers != null) {
                uRequest.setAllStandardUsers(allStandardUsers.equals("true"));
            }
            String managePermissions = request.getParameter("managePermissions");
            if (managePermissions != null) {
                uRequest.setManagePermissions(managePermissions.equals("on"));
            }
            String manageFirmware = request.getParameter("manageFirmware");
            if (manageFirmware != null) {
                uRequest.setManageFirmware(manageFirmware.equals("true"));
                if (uRequest.isManageFirmware()) {
                    String firmwarePackageId = request.getParameter("firmwarePackageId");
                    if (firmwarePackageId != null) {
                        if (USE_DEFAULT_CATALOG_ID.equals(firmwarePackageId)) {
                            uRequest.setUseDefaultCatalog(true);
                        } else {
                            uRequest.setFirmwarePackageId(firmwarePackageId);
                        }
                    }
                }
            }

            String[] users = request.getParameterValues("assignedUsers");
            if (users != null) {
                for (String user : users) {
                    uRequest.getAssignedUsers().add(user);
                }
            }

            MultipartFile file = request.getFile("file");
            if (file != null) {
                uRequest.setFileData(file.getBytes());
            }

            ServiceTemplate template = templateServiceAdapter.uploadTemplate(uRequest);
            if (template != null) {
                jobResponse.responseObj = parseTemplateBuilder(template, getApplicationContext()
                                                              );
                jobResponse.responseCode = 0;
            } else {
                throw new ControllerException("Upload template failed");
            }

        } catch (Throwable t) {
            log.error("uploadTemplate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "addattachment", method = RequestMethod.POST)
    public JobResponse addAttachment(@RequestParam("file") MultipartFile multipartFile,
                                     @RequestParam("id") String templateId,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {

        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = null;

        String fileName = multipartFile.getOriginalFilename();
        if (!multipartFile.isEmpty()) {

            try {
                //No file separators should be in the file name.
                if (fileName.contains(File.separator)) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.fileNameInvalid",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
                //Limit 50 mb on file uploads
                if (multipartFile.getSize() / (1024.0 * 1024.0) > 50) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.attachmentSize",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
                File templateDir = new File(TEMPLATE_ATTACHMENT_DIR + File.separator + templateId);

                templateDir.mkdirs();
                File file = new File(templateDir.getCanonicalFile() + File.separator + fileName);
                if (file.exists()) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.fileExists", null,
                                                               LocaleContextHolder.getLocale()));
                }

                byte[] bytes = multipartFile.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
                stream.write(bytes);
                stream.close();
                log.debug("Successfully uploaded " + fileName + " into " + file.getCanonicalPath());
            } catch (Throwable e) {
                log.error("Error uploading template attachment", e);
                jobResponse = addFailureResponseInfo(e);
            }
        }
        return jobResponse;
    }

    @RequestMapping(value = "downloadattachment", method = RequestMethod.GET)
    public JobResponse downloadAttachment(@RequestParam("templateId") String templateId,
                                          @RequestParam("name") String fileName,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        JobResponse jobResponse = new JobResponse();
        ServletOutputStream out = null;
        try {
            String templatePath = TEMPLATE_ATTACHMENT_DIR + File.separator + templateId;
            File templateDir = new File(templatePath);
            if (templateDir.list() != null) {
                if (!Arrays.asList(templateDir.list()).contains(fileName)) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.fileDoesNotExist",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
            }
            File file = new File(templatePath + File.separator + fileName);
            response.setContentType("application/octet-stream");
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition",
                               "attachment; filename=\"" + file.getName() + "\"");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            out = response.getOutputStream();
            out.write(FileUtils.readFileToByteArray(file));
        } catch (Throwable e) {
            log.error("Error downloading template attachment", e);
            jobResponse = addFailureResponseInfo(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return jobResponse;
    }

    @RequestMapping(value = "validateexport", method = RequestMethod.POST)
    public JobResponse validateExportTemplate(@RequestBody JobExportTemplateRequest request) {

        JobResponse jobResponse = new JobResponse();
        try {
            ServiceTemplate svc = templateServiceAdapter.getTemplate(request.requestObj.templateId);
            if (svc == null) {
                throw new ControllerException("Template not found!");
            }
            // call export to validate
            ServiceTemplateExportRequest serviceTemplateExportRequest = new ServiceTemplateExportRequest();
            serviceTemplateExportRequest.setServiceTemplate(svc);
            serviceTemplateExportRequest.setEncryptionPassword(request.requestObj.encryptionPassword);
            serviceTemplateExportRequest.setUseEncPwdFromBackup(request.requestObj.useEncPwdFromBackup);

            String fileName = templateServiceAdapter.exportTemplate(serviceTemplateExportRequest);

            if (fileName != null) {
                File f = new File(fileName);
                f.delete();
            }

            jobResponse.responseObj = "Success!";
        } catch (Throwable t) {
            log.error("validateExportTemplate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "deleteattachment", method = RequestMethod.POST)
    public JobResponse deleteAttachment(@RequestBody JobTemplateAttachmentRequest requestBody,
                                        HttpServletRequest request, HttpServletResponse response) {
        JobResponse jobResponse = new JobResponse();
        try {
            String templateId = requestBody.requestObj.id;
            String fileName = requestBody.requestObj.name;
            String templatePath = TEMPLATE_ATTACHMENT_DIR + File.separator + templateId;
            File templateDir = new File(templatePath);
            if (templateDir.list() != null) {
                if (!Arrays.asList(templateDir.list()).contains(fileName)) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.fileDoesNotExist",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
            }
            ServiceTemplate template = templateServiceAdapter.getTemplate(templateId);
            if (template != null && template.isTemplateLocked()) {
                throw new ControllerException(getApplicationContext().getMessage(
                        "validationError.sampleTemplateAttachment", null,
                        LocaleContextHolder.getLocale()));
            }
            File file = new File(templatePath + File.separator + fileName);
            file.delete();
            //Delete the directory if it's empty.
            if (templateDir.list() != null) {
                if (templateDir.list().length == 0) {
                    templateDir.delete();
                }
            }
        } catch (Throwable e) {
            log.error("Error deleting template attachment", e);
            jobResponse = addFailureResponseInfo(e);
        }
        return jobResponse;
    }

    @RequestMapping(value = "uploadconfigfile", method = RequestMethod.POST)
    public JobResponse uploadConfigFile(@RequestParam("file") MultipartFile multipartFile,
                                        HttpServletRequest request, HttpSession session,
                                        HttpServletResponse response) {

        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = null;

        String fileName = multipartFile.getOriginalFilename();
        if (!multipartFile.isEmpty()) {
            try {
                File file = File.createTempFile(
                        RandomStringUtils.random(10, true, true) + "_" + fileName, "");
                byte[] bytes = multipartFile.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
                stream.write(bytes);
                stream.close();
                log.debug("Successfully uploaded " + fileName + " into " + file.getCanonicalPath());
                jobResponse.responseObj = file.getAbsolutePath();
            } catch (Throwable e) {
                log.error("Error uploading template attachment", e);
                jobResponse = addFailureResponseInfo(e);
            }
        }

        return jobResponse;
    }

    @RequestMapping(value = "getparsedconfigfile", method = RequestMethod.POST)
    public JobResponse applyParsedConfigFile(@RequestBody JobGetReferenceComponentRequest request) {
        JobResponse jobResponse = new JobResponse();

        try {
            ServiceTemplate defaultTemplate = templateServiceAdapter.uploadConfiguration(
                    request.requestObj.referenceId);
            if (defaultTemplate != null) {
                String[] str_array = request.requestObj.referenceId.split("_", 2);
                setConfigFileName(defaultTemplate, request.requestObj.componentId,
                                  str_array[1].replaceAll("\\d*$", ""));

                for (ServiceTemplateComponent component : defaultTemplate.getComponents()) {
                    if (component.getId().equals(request.requestObj.componentId)) {
                        jobResponse.responseObj = parseTemplateBuilderComponent(component,
                                                                                getApplicationContext(),
                                                                                false, 0, false);
                        break;
                    }
                }
            } else {
                log.error(
                        "Did not get template back for configuration import: " + request.requestObj.referenceId);
            }
        } catch (Throwable t) {
            log.error("applyParsedConfigFile() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Validate template Server component and return list of available servers per filter condition.
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "validatesettings", method = RequestMethod.POST)
    public JobResponse validateTemplates(@RequestBody JobTemplateBuilderComponentRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            ServiceTemplate template = new ServiceTemplate();
            template.getComponents().add(createTemplateComponent(request.requestObj));

            DeploymentFilterResponse response = serviceAdapter.filterAvailableServers(template, -1);
            UIValidateResponse uiResponse = new UIValidateResponse();
            jobResponse.responseObj = uiResponse;
            for (SelectedServer server : response.getSelectedServers()) {
                ManagedDevice managedDevice = deviceInventoryServiceAdapter.getDeviceInventory(
                        server.getRefId());
                UIDevice device = DeviceController.getUIDeviceFromManagedDevice(managedDevice,
                                                                                null);
                uiResponse.devices.add(device);
            }
            uiResponse.totalservers = response.getSelectedServers().size() + response.getRejectedServers().size();

        } catch (Throwable t) {
            log.error("validateTemplates() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private void setConfigFileName(ServiceTemplate defaultTemplate, String componentId,
                                   String strFileName) {
        for (ServiceTemplateComponent component : defaultTemplate.getComponents()) {
            if (component.getId().equals(componentId)) {
                component.setConfigFile(strFileName);
            }
        }
    }

    /**
     * /templates/getvmwarecomponent
     *
     * @param request
     *            UITemplateBuilderComponent
     * @return device
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getvmwarecomponent", method = RequestMethod.POST)
    public JobResponse getVmwareComponent(@RequestBody JobGetImportConfigComponentRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            // get the default template
            ServiceTemplate defaultTemplate = templateServiceAdapter.getTemplate(
                    DEFAULT_TEMPLATE_ID, true, false);
            if (defaultTemplate == null) {
                log.error("Missed default template [ID=1000]!");
                throw new AsmRuntimeException(ASMCommonsMessages.internalServerError());
            }
            defaultTemplate.sortInternals();
            UITemplateBuilder uiTemplateBuilder = parseTemplateBuilder(defaultTemplate,
                                                                       getApplicationContext());

            UITemplateBuilderComponent uiTemplateBuilderComponentReturn = null;

            String clusterComponentType = getDeviceTypeFromTypeName(
                    ServiceTemplateComponentType.CLUSTER.name());
            for (UITemplateBuilderComponent uiTemplateBuilderComponent : uiTemplateBuilder.components) {
                if (clusterComponentType.equals(uiTemplateBuilderComponent.type) &&
                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMPONENT_ID.equals(uiTemplateBuilderComponent.id)) { // it's a cluster, now need to find if it's a VCenter or HyperV
                    uiTemplateBuilderComponentReturn = uiTemplateBuilderComponent;
                    break;
                }
            }

            jobResponse.responseObj = TemplateController.getClusterTemplateBuilderComponentWithoutNewOptions(
                    uiTemplateBuilderComponentReturn);

        } catch (Throwable t) {
            log.error("getVmwareComponent() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    public static UITemplateBuilderComponent getClusterTemplateBuilderComponentWithoutNewOptions(UITemplateBuilderComponent uiTemplateBuilderComponentReturn) {

        // Find and Remove the Create New Datacenter / Cluster options / VDS Settings
        if (uiTemplateBuilderComponentReturn != null) {
            UITemplateBuilderCategory categoryToRemove = null;

            for (UITemplateBuilderCategory uiTemplateBuilderCategory : uiTemplateBuilderComponentReturn.categories) {
                switch (uiTemplateBuilderCategory.id) {
                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMP_VDS_ID:
                    categoryToRemove = uiTemplateBuilderCategory;
                    break;
                default:
                    List<UITemplateBuilderSetting> settingToRemove = new ArrayList<>();

                    for (UITemplateBuilderSetting uiTemplateBuilderSetting : uiTemplateBuilderCategory.settings) {
                        switch (uiTemplateBuilderSetting.id) {
                        // Remove Settings
                        case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CLUSTER_CLUSTER_VDS_ID:
                        case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CLUSTER_CLUSTER_SDRS_ID:
                        case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CLUSTER_CLUSTER_VSAN_ID:
                            settingToRemove.add(uiTemplateBuilderSetting);
                            break;
                        default:
                            // Remove Options
                            List<UITemplateBuilderListItem> optionsToRemove = new ArrayList<>();
                            for (UITemplateBuilderListItem uiTemplateBuilderListItem : uiTemplateBuilderSetting.options) {
                                switch (uiTemplateBuilderListItem.id) {
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX:
                                    optionsToRemove.add(uiTemplateBuilderListItem);
                                    break;
                                default:
                                }
                            }
                            uiTemplateBuilderSetting.options.removeAll(optionsToRemove);
                        }
                    }
                    uiTemplateBuilderCategory.settings.removeAll(settingToRemove);
                    break;
                }

            }
            uiTemplateBuilderComponentReturn.categories.remove(categoryToRemove);
        }
        return uiTemplateBuilderComponentReturn;
    }

    @RequestMapping(value = "savetemplateadditionalsettings", method = RequestMethod.POST)
    public JobResponse saveTemplateAdditionalSettings(@RequestBody JobSaveTemplateRequest request) {

        JobResponse jobResponse = new JobResponse();
        try {
            ServiceTemplate serviceTemplate = createTemplate(request.requestObj,
                                                             firmwareRepositoryServiceAdapter);
            ServiceTemplate newServiceTemplate = templateServiceAdapter.cloneTemplate(
                    serviceTemplate);
            jobResponse.responseObj = newServiceTemplate.getId();
        } catch (Throwable t) {
            log.error("saveTemplateAdditionalSettings() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Gets the full service template by id and runs through the adjuster to add all options.
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "gettemplatedetails", method = RequestMethod.POST)
    public JobResponse getTemplateDetails(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();

        try {
            ServiceTemplate template = templateServiceAdapter.getTemplate(request.requestObj.id);
            if (template != null) {
                template = templateServiceAdapter.updateComponents(template.getId(), null,
                                                                   template);
                jobResponse.responseObj = parseTemplateBuilder(template, getApplicationContext()
                                                              );
            }
        } catch (Throwable t) {
            log.error("getTemplateDetails() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    public static String getUITemplateSettingDataType(final ServiceTemplateSettingType type) {
        String value = "";
        if (type != null) {
            switch (type) {
            case TEXT:
                value = "textarea";
                break;
            case ENUMERATED:
                value = "enum";
                break;
            case INTEGER:
                value = "number";
                break;
            default:
                value = type.name().toLowerCase();
                break;
            }
        }
        return value;
    }

    public static ServiceTemplateSettingType getTemplateSettingDataType(final String type) {
        ServiceTemplateSettingType value = null;
        if (type != null) {
            switch (type) {
            case "textarea":
                value = ServiceTemplateSettingType.TEXT;
                break;
            case "enum":
                value = ServiceTemplateSettingType.ENUMERATED;
                break;
            case "number":
                value = ServiceTemplateSettingType.INTEGER;
                break;
            default:
                value = ServiceTemplateSettingType.valueOf(type.toUpperCase());
                break;
            }
        }
        return value;
    }

    public class TemplatesComparator implements Comparator<ServiceTemplate> {
        @Override
        public int compare(ServiceTemplate x, ServiceTemplate y) {
            return x.getTemplateName().compareTo(y.getTemplateName());
        }
    }

    class TemplateUpdateCreateDateComparator implements Comparator<ServiceTemplate> {

        @Override
        public int compare(ServiceTemplate template1, ServiceTemplate template2) {
            GregorianCalendar mostRecentDate1 = getDateForComparison(template1);
            GregorianCalendar mostRecentDate2 = getDateForComparison(template2);
            if (null != mostRecentDate1) {
                return mostRecentDate1.compareTo(mostRecentDate2);
            }
            if (null == mostRecentDate2) {
                // lhs is null, rhs is not.
                return -1;
            }
            // both are null.
            return 0;
        }

        private GregorianCalendar getDateForComparison(ServiceTemplate template1) {
            GregorianCalendar result = template1.getUpdatedDate();
            if (null == result) {
                result = template1.getCreatedDate();
            }
            return result;
        }
    }
}
