/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dell.asm.alcm.client.model.WizardStatus;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.CompliantState;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.DeviceState;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareComplianceReport;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareComplianceReportComponent;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareUpdateRequest;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareUpdateRequest.UpdateType;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedState;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.asmcore.asmmanager.client.puppetdevice.PuppetDevice;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateCategory;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponent;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponentType;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSetting;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSettingIDs;
import com.dell.asm.asmcore.asmmanager.client.setting.Setting;
import com.dell.asm.business.timezonemanager.TimeZoneConfigurationMgr;
import com.dell.asm.common.model.TimeZoneDto;
import com.dell.asm.ui.adapter.service.ASMSetupStatusServiceAdapter;
import com.dell.asm.ui.adapter.service.ChassisServiceAdapter;
import com.dell.asm.ui.adapter.service.ConfigureDevicesServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.SettingServiceAdapter;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIDeviceStatus;
import com.dell.asm.ui.model.chassis.UIConfigureDeviceUser;
import com.dell.asm.ui.model.configure.JobConfigureResourcesRequest;
import com.dell.asm.ui.model.configure.JobGetConfigurableResourcesRequest;
import com.dell.asm.ui.model.configure.TemplateUplink;
import com.dell.asm.ui.model.configure.UIBladeConfigurationSettings;
import com.dell.asm.ui.model.configure.UIChassisConfigurationSettings;
import com.dell.asm.ui.model.configure.UIConfigurableDevice;
import com.dell.asm.ui.model.configure.UIConfigureResources;
import com.dell.asm.ui.model.configure.UIDeviceConfiguration;
import com.dell.asm.ui.model.configure.UIIOMConfigurationSettings;
import com.dell.asm.ui.model.configure.UIPortConfigurationSettings;
import com.dell.asm.ui.model.configure.UIUplink;
import com.dell.asm.ui.model.device.UIDellSwitchDevice;
import com.dell.asm.ui.model.firmware.UIFirmwareReportDevice;
import com.dell.asm.ui.util.DeviceUtil;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.asm.chassis.client.device.Chassis;
import com.dell.pg.asm.chassis.client.device.IOM;
import com.dell.pg.asm.chassis.client.device.Server;
import com.dell.pg.orion.common.utilities.MarshalUtil;
import com.dell.pg.orion.common.utilities.VersionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Chassis Controller.
 */
@RestController
@RequestMapping(value = "/configurechassis/")
public class ConfigureChassisController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(ConfigureChassisController.class);

    private static final String SWITCH_CONFIG_NAME_SETTING = "switchConfigFileName";
    private static final String SWITCH_CONFIG_CONTENT_SETTING = "switchConfigFileContent";
    private static Map<HttpSession, Map> sessionRefIds = Collections.synchronizedMap(
            new HashMap<HttpSession, Map>());
    private ChassisServiceAdapter chassisServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private ConfigureDevicesServiceAdapter configureServiceAdapter;
    private ASMSetupStatusServiceAdapter asmSetupStatusServiceAdapter;
    private SettingServiceAdapter settingServiceAdapter;
    private Map<String, String> vltMap = Collections.synchronizedMap(new HashMap<String, String>());

    @Autowired
    public ConfigureChassisController(ChassisServiceAdapter chassisServiceAdapter,
                                      DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                                      ConfigureDevicesServiceAdapter configureServiceAdapter,
                                      ASMSetupStatusServiceAdapter asmSetupStatusServiceAdapter,
                                      SettingServiceAdapter settingServiceAdapter) {
        this.chassisServiceAdapter = chassisServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.configureServiceAdapter = configureServiceAdapter;
        this.asmSetupStatusServiceAdapter = asmSetupStatusServiceAdapter;
        this.settingServiceAdapter = settingServiceAdapter;
    }

    @RequestMapping(value = "uploadportconfiguration", method = RequestMethod.POST)
    public JobResponse uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        log.debug("uploadTemplate - Called by UI");
        JobResponse jobResponse = new JobResponse();
        try {
            if (multipartFile != null) {
                try {
                    byte[] buffer = new byte[(int) multipartFile.getSize()];
                    IOUtils.readFully(multipartFile.getInputStream(), buffer);
                    jobResponse.responseObj = multipartFile.getOriginalFilename();

                    updateSwitchConfigFile(multipartFile.getOriginalFilename(), buffer);

                } catch (IOException ioe) {
                    log.error("Upload Uplink Config: can't read file from the message", ioe);
                    jobResponse.responseObj = "";
                } finally {
                    IOUtils.closeQuietly(multipartFile.getInputStream());
                }
            }
            jobResponse.responseCode = 0;

        } catch (Throwable t) {
            log.error("uploadFile() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private void updateSwitchConfigFile(String name, byte[] buffer) {
        Setting setting = settingServiceAdapter.getSettingByName(SWITCH_CONFIG_NAME_SETTING);
        if (setting == null) {
            setting = new Setting();
            setting.setName(SWITCH_CONFIG_NAME_SETTING);
            setting.setValue(name);
            settingServiceAdapter.create(setting);
        } else {
            setting.setValue(name);
            settingServiceAdapter.update(setting);
        }

        setting = settingServiceAdapter.getSettingByName(SWITCH_CONFIG_CONTENT_SETTING);
        if (setting == null) {
            setting = new Setting();
            setting.setName(SWITCH_CONFIG_CONTENT_SETTING);
            setting.setValue(Base64.encodeBase64String(buffer));
            settingServiceAdapter.create(setting);
        } else {
            setting.setValue(Base64.encodeBase64String(buffer));
            settingServiceAdapter.update(setting);
        }

    }

    /**
     * getconfigurableresources.
     *
     * @return new credential
     * @throws javax.servlet.ServletException the servlet exception
     * @throws java.io.IOException            Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getconfigurableresources", method = RequestMethod.POST)
    public JobResponse getConfigurableResources(
            @RequestBody JobGetConfigurableResourcesRequest request, HttpSession session) {

        JobResponse jobResponse = new JobResponse();

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_CHASSIS, "id");

        try {
            UIConfigureResources config = new UIConfigureResources();

            config.canConfigAllIOM = true;
            config.globalConfiguration = new UIDeviceConfiguration();

            Map<String, String> refIds = sessionRefIds.get(session);
            if (refIds != null) {
                refIds.clear();
            } else {
                refIds = new HashMap<>();
                sessionRefIds.put(session, refIds);
            }

            ResourceList<ManagedDevice> mList;

            if (request.requestObj.requireComplianceCheck)
                mList = deviceInventoryServiceAdapter.getAllDeviceInventoryWithCompliance(null,
                                                                                          options.filterList,
                                                                                          0,
                                                                                          MappingUtils.MAX_RECORDS);
            else
                mList = deviceInventoryServiceAdapter.getAllDeviceInventory(null,
                                                                            options.filterList, 0,
                                                                            MappingUtils.MAX_RECORDS);

            List<ManagedDevice> mNonChassis = new ArrayList<>();
            List<ManagedDevice> mChassis = new ArrayList<>();

            int numChs = 0;
            if (mList != null && mList.getList() != null && mList.getList().length > 0) {

                // sort devices by chassis and non-chassis
                // always add chassis. For other devices only add "available".
                for (ManagedDevice dto : mList.getList()) {
                    if (!DeviceType.isChassis(dto.getDeviceType())) {
                        // skip non-configurable
                        if (DeviceType.scvmm.equals(dto.getDeviceType()) ||
                                DeviceType.rhvm.equals(dto.getDeviceType()) ||
                                DeviceType.vcenter.equals(dto.getDeviceType()) ||
                                DeviceType.unknown.equals(dto.getDeviceType()) ||
                                ManagedState.UNMANAGED.equals(dto.getManagedState()) ||
                                (!DeviceState.READY.equals(dto.getState()) &&
                                        !DeviceState.UPDATE_FAILED.equals(dto.getState()) &&
                                        !DeviceState.CONFIGURATION_ERROR.equals(dto.getState()))
                                || (dto.getDeviceType().isServer() && dto.isInUse())) {
                            continue;
                        }
                        mNonChassis.add(dto);
                    } else {
                        mChassis.add(dto);
                        numChs++;
                    }
                }

                // first process chassis
                for (ManagedDevice dto : mChassis) {
                    // if we have chassis only, we need to get also related IOMs and servers
                    // it is faster to get them all at once
                    List<ManagedDevice> mNonChassisInventory;
                    if (mNonChassis.size() == 0) {
                        mNonChassisInventory = fetchServersAndBlades(dto.getRefId(),
                                                                     request.requestObj.requireComplianceCheck);
                    } else
                        mNonChassisInventory = mNonChassis;

                    processResource(mNonChassisInventory, dto, config, refIds,
                                    request.requestObj.requireComplianceCheck);
                }

                // rest of the objects
                for (ManagedDevice dto : mNonChassis) {
                    if (!deviceAlreadyProcessed(dto, config.devices, refIds)) {
                        processResource(mNonChassis, dto, config, refIds,
                                        request.requestObj.requireComplianceCheck);
                    }
                }
            }

            if (numChs < 2)
                config.canConfigAllIOM = false;

            Setting setting = settingServiceAdapter.getSettingByName(SWITCH_CONFIG_NAME_SETTING);
            if (setting != null) {
                config.configIOMXMLSettingsExistingFileName = setting.getValue();
            }

            jobResponse.responseObj = config;

        } catch (Throwable t) {
            log.error("getConfigurableResources() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Get related servers and IOMs from inventory
     * @param refId Chassis ID
     */
    private List<ManagedDevice> fetchServersAndBlades(String refId, boolean withCompliance) {
        List<String> filter = new ArrayList<>();
        filter.add("eq,chassisId," + refId);
        filter.add(
                "eq,state," + DeviceState.READY.getValue() + "," + DeviceState.UPDATE_FAILED.getValue() + "," + DeviceState.CONFIGURATION_ERROR.getValue());
        filter.add(
                "eq,managedState," + ManagedState.MANAGED.getValue() + "," + ManagedState.RESERVED.getValue());

        ResourceList<ManagedDevice> mList;
        if (withCompliance)
            mList = deviceInventoryServiceAdapter.getAllDeviceInventoryWithCompliance(null, filter,
                                                                                      0,
                                                                                      MappingUtils.MAX_RECORDS);
        else
            mList = deviceInventoryServiceAdapter.getAllDeviceInventory(null, filter, 0,
                                                                        MappingUtils.MAX_RECORDS);

        List<ManagedDevice> result;
        if (mList != null && mList.getList() != null)
            result = Arrays.asList(mList.getList());
        else
            result = new ArrayList<>();

        return result;
    }

    private boolean deviceAlreadyProcessed(ManagedDevice dto, List<UIConfigurableDevice> devices,
                                           Map<String, String> refIds) {
        for (UIConfigurableDevice device : devices) {
            String dRefId = refIds.get(device.id);
            if (dRefId.equals(dto.getRefId()))
                return true;
        }
        return false;
    }

    private ManagedDevice locateDeviceByServiceTag(List<ManagedDevice> allDevices,
                                                   String serviceTag) {
        if (serviceTag == null)
            return null;
        for (ManagedDevice device : allDevices) {
            if (serviceTag.equals(device.getServiceTag()))
                return device;
        }
        return null;
    }

    private ManagedDevice locateDeviceByRefId(List<ManagedDevice> allDevices, List<String> refIds) {
        if (refIds == null || refIds.size() == 0)
            return null;
        for (ManagedDevice device : allDevices) {
            if (refIds.contains(device.getRefId()))
                return device;
        }
        return null;
    }

    /**
     *
     * @param dto
     * @param config
     * @param refIds
     * @param fullConfiguration If the flag is NOT set, we don't need full processing. Only list of devices for optional removal page
     * @throws IOException
     */
    private void processResource(List<ManagedDevice> allDevices, ManagedDevice dto,
                                 UIConfigureResources config, Map<String, String> refIds,
                                 boolean fullConfiguration) throws IOException {

        Chassis chs = null;
        UIFirmwareReportDevice report;
        if (DeviceType.isChassis(dto.getDeviceType())) {
            try {
                chs = chassisServiceAdapter.getChassis(dto.getRefId());
            } catch (Exception e) {
                log.error("Failed to get chassis", e);
            }
        }
        UIConfigurableDevice chassisCD = getConfigurableDevice(dto, null, fullConfiguration,
                                                               refIds);
        if (fullConfiguration && dto.getCompliance() != CompliantState.COMPLIANT) {
            FirmwareComplianceReport firmwareComplianceReport =
                    this.deviceInventoryServiceAdapter.getFirmwareComplianceReportForDevice(
                            dto.getRefId());
            report = MappingUtils.getUIFirmwareDeviceComplianceReport(firmwareComplianceReport);

            chassisCD.firmwarecomponents = report.firmwareComponents;
        }
        config.devices.add(chassisCD);

        // rest of the processing applies to chassis only
        if (DeviceType.isChassis(dto.getDeviceType()) && chs != null) {

            Map<String, Boolean> ioms = new HashMap<>();
            ioms.put("A1", false);
            ioms.put("A2", false);
            ioms.put("B1", false);
            ioms.put("B2", false);
            ioms.put("C1", false);
            ioms.put("C2", false);


            ManagedDevice mDevice;
            UIConfigurableDevice uiCD;

            for (Server server : chs.getServers()) {
                mDevice = locateDeviceByServiceTag(allDevices, server.getServiceTag());
                if (mDevice == null) {
                    log.error(
                            "Server found in Chassis RA but not in the inventory: " + server.getServiceTag());
                    continue;
                }

                uiCD = getConfigurableDevice(mDevice, chassisCD.id, fullConfiguration, refIds);
                if (fullConfiguration && mDevice.getCompliance() != CompliantState.COMPLIANT) {
                    FirmwareComplianceReport firmwareComplianceReport =
                            this.deviceInventoryServiceAdapter.getFirmwareComplianceReportForDevice(
                                    mDevice.getRefId());
                    report = MappingUtils.getUIFirmwareDeviceComplianceReport(
                            firmwareComplianceReport);
                    uiCD.firmwarecomponents = report.firmwareComponents;
                }

                config.devices.add(uiCD);

                if (fullConfiguration) {

                    // for each blade
                    UIBladeConfigurationSettings bladeS = new UIBladeConfigurationSettings();
                    bladeS.id = uiCD.id;
                    bladeS.idForDelete = uiCD.idForDelete;
                    bladeS.iDRACName = mDevice.getDisplayName();
                    bladeS.managementIP = server.getManagementIP();
                    bladeS.serverId = mDevice.getRefId();
                    bladeS.slot = server.getSlot();
                    bladeS.svctag = server.getServiceTag();

                    chassisCD.chassisConfiguration.bladeConfiguration.add(bladeS);
                }
            }

            chassisCD.chassisConfiguration.bladeCount = chassisCD.chassisConfiguration.bladeConfiguration.size();

            boolean addCommonIom = false;
            boolean identicalIOMs = true;
            int iomCount = 0;
            for (IOM iom : chs.getIOMs()) {

                // this is the only way to guess relation between chassis RA IOM and puppet device
                List<String> iomRefIds = new ArrayList<>();
                iomRefIds.add("dell_iom-" + iom.getManagementIP());
                iomRefIds.add("iom_8x4-" + iom.getManagementIP());

                mDevice = locateDeviceByRefId(allDevices, iomRefIds);

                if (mDevice == null) {
                    log.error(
                            "IOM found in Chassis RA but not in the inventory: " + iom.getServiceTag());
                    continue;
                }
                uiCD = getConfigurableDevice(mDevice, chassisCD.id, fullConfiguration, refIds);
                final String deviceType = DeviceUtil.getSwitchDeviceType(
                        mDevice.getModel());
                FirmwareComplianceReport firmwareComplianceReport = null;
                boolean fnioa = false;
                if (fullConfiguration && DeviceUtil.FX_IOM_DEVICETYPE.equals(
                        deviceType)) {
                    fnioa = true;
                    uiCD.fnioa = true;
                }
                if (fullConfiguration && (fnioa || mDevice.getCompliance() != CompliantState.COMPLIANT)) {
                    firmwareComplianceReport = this.deviceInventoryServiceAdapter.getFirmwareComplianceReportForDevice(
                            mDevice.getRefId());
                    report = MappingUtils.getUIFirmwareDeviceComplianceReport(
                            firmwareComplianceReport);
                    uiCD.firmwarecomponents = report.firmwareComponents;
                }
                config.devices.add(uiCD);

                if (fullConfiguration) {

                    // Puppet facts
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mDevice.getFacts();
                    PuppetDevice pDevice = new PuppetDevice();
                    pDevice.setName(mDevice.getRefId());
                    pDevice.setData(mapper.readValue(json, Map.class));

                    UIDellSwitchDevice uiSwitch = DellSwitchController.parseSwitch(pDevice, dto);

                    // for each IOM
                    UIIOMConfigurationSettings iomS = new UIIOMConfigurationSettings();
                    iomS.id = uiCD.id;
                    iomS.idForDelete = uiCD.idForDelete;
                    iomS.iomId = mDevice.getRefId();
                    iomS.managementIP = iom.getManagementIP();
                    iomS.slot = iom.getSlot() + "";
                    iomS.svctag = iom.getServiceTag();
                    iomS.fabric = uiSwitch.fabric;
                    iomS.fnioa = fnioa;

                    iomS.slot1FCModule = uiSwitch.slot1FCModule;
                    iomS.slot2FCModule = uiSwitch.slot2FCModule;
                    iomS.slot3FCModule = uiSwitch.slot3FCModule;

                    iomS.hostName = uiSwitch.hostname;
                    iomS.iompresent = true;
                    iomS.model = uiSwitch.model;
                    iomS.iomconfigurable = true;

                    if (fnioa && firmwareComplianceReport != null) {
                        for (FirmwareComplianceReportComponent component : firmwareComplianceReport.getFirmwareComplianceReportComponents()) {
                            if (component.getCurrentVersion() != null && component.getCurrentVersion().getFirmwareVersion() != null) {
                                if (VersionUtils.compareVersions(
                                        component.getCurrentVersion().getFirmwareVersion(),
                                        "9.9") < 0) {
                                    iomS.iomconfigurable = false;
                                    uiCD.fnioaUpdateRequired = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (uiSwitch.model != null && (uiSwitch.model.toLowerCase().contains(
                            "fn-410") ||
                            uiSwitch.model.toLowerCase().contains("fn-2210"))) {
                        iomS.quadPortSupported = false;
                    }

                    if (uiSwitch.slot1Config != null) {
                        iomS.hasSlot1 = true;
                        iomS.slot1Config = uiSwitch.slot1Config;
                        iomS.slot1Ports = iomS.slot1Config.size();
                    }
                    if (uiSwitch.slot2Config != null) {
                        iomS.hasSlot2 = true;
                        iomS.slot2Config = uiSwitch.slot2Config;
                        iomS.slot2Ports = iomS.slot2Config.size();
                    }
                    if (uiSwitch.slot3Config != null) {
                        iomS.hasSlot3 = true;
                        iomS.slot3Config = uiSwitch.slot3Config;
                        iomS.slot3Ports = iomS.slot3Config.size();
                    }

                    // if any ports are in quad port mode then all quad ports should be represented in quad port mode
                    if (uiSwitch.quad_port_interfaces != null && uiSwitch.quad_port_interfaces.size() > 0) {
                        iomS.quadPortMode = true;
                    }

                    // quad ports
                    List<UIPortConfigurationSettings> slotClone = cloneSlot(iomS.slot1Config);
                    for (UIPortConfigurationSettings p : slotClone) {
                        if (p.portId != null && p.portId.toLowerCase().contains("fo")) {
                            iomS.slot1QuadPortSupported = true;
                            if (iomS.quadPortMode) {
                                // replace with Ten Gb
                                p.portId = "Te 0/" + p.portNumber;
                                p.portType = "Te";
                            }
                            addQuadPorts(iomS.slot1Config, p.portNumber,
                                         iomS.slot1Config.indexOf(p));
                        }
                        if (uiSwitch != null && uiSwitch.quad_port_interfaces != null &&
                                uiSwitch.quad_port_interfaces.contains(
                                        String.valueOf(p.portNumber))) {
                            iomS.slot1QuadPortSupported = true;
                            iomS.slot1Ports -= 3;
                        }
                    }

                    slotClone = cloneSlot(iomS.slot2Config);
                    for (UIPortConfigurationSettings p : slotClone) {
                        if (p.portId != null && p.portId.toLowerCase().contains("fo")) {
                            iomS.slot2QuadPortSupported = true;
                            if (iomS.quadPortMode) {
                                // replace with Ten Gb
                                p.portId = "Te 0/" + p.portNumber;
                                p.portType = "Te";
                            }
                            addQuadPorts(iomS.slot2Config, p.portNumber,
                                         iomS.slot2Config.indexOf(p));
                        }
                        if (uiSwitch != null && uiSwitch.quad_port_interfaces != null &&
                                uiSwitch.quad_port_interfaces.contains(
                                        String.valueOf(p.portNumber))) {
                            iomS.slot2QuadPortSupported = true;
                            iomS.slot2Ports -= 3;
                        }
                    }

                    slotClone = cloneSlot(iomS.slot3Config);
                    for (UIPortConfigurationSettings p : slotClone) {
                        if (p.portId != null && p.portId.toLowerCase().contains("fo")) {
                            iomS.slot3QuadPortSupported = true;
                            if (iomS.quadPortMode) {
                                // replace with Ten Gb
                                p.portId = "Te 0/" + p.portNumber;
                                p.portType = "Te";
                            }
                            addQuadPorts(iomS.slot3Config, p.portNumber,
                                         iomS.slot3Config.indexOf(p));
                        }
                        if (uiSwitch != null && uiSwitch.quad_port_interfaces != null &&
                                uiSwitch.quad_port_interfaces.contains(
                                        String.valueOf(p.portNumber))) {
                            iomS.slot3QuadPortSupported = true;
                            iomS.slot3Ports -= 3;
                        }
                    }

                    // reverse order for UI
                    iomS.slot1Config.sort(new SlotComparator());
                    iomS.slot2Config.sort(new SlotComparator());
                    iomS.slot3Config.sort(new SlotComparator());

                    if (iomS.slot1Config.size() < 8) {// && iomS.slot1QuadPortSupported) {
                        addEmptySlots(iomS.slot1Config);
                    }
                    if (iomS.slot2Config.size() < 8) {// && iomS.slot2QuadPortSupported) {
                        addEmptySlots(iomS.slot2Config);
                    }
                    if (iomS.slot3Config.size() < 8) {// && iomS.slot3QuadPortSupported) {
                        addEmptySlots(iomS.slot3Config);
                    }

                    // map uplinks
                    if (uiCD.chassisConfiguration != null && uiCD.chassisConfiguration.commonIOMConfiguration != null &&
                            uiCD.chassisConfiguration.commonIOMConfiguration.slot1Config != null) {
                        for (UIPortConfigurationSettings uiport : uiCD.chassisConfiguration.commonIOMConfiguration.slot1Config) {
                            if (iomS.hasSlot1) {
                                for (UIPortConfigurationSettings p : iomS.slot1Config) {
                                    if (p.portId.equals(uiport.portId)) {
                                        p.uplinkName = uiport.uplinkName;
                                        p.uplinkId = uiport.uplinkId;
                                    }
                                }
                            }
                            if (iomS.hasSlot2) {
                                for (UIPortConfigurationSettings p : iomS.slot2Config) {
                                    if (p.portId.equals(uiport.portId)) {
                                        p.uplinkName = uiport.uplinkName;
                                        p.uplinkId = uiport.uplinkId;
                                    }
                                }
                            }
                            if (iomS.hasSlot3) {
                                for (UIPortConfigurationSettings p : iomS.slot3Config) {
                                    if (p.portId.equals(uiport.portId)) {
                                        p.uplinkName = uiport.uplinkName;
                                        p.uplinkId = uiport.uplinkId;
                                    }
                                }
                            }
                        }

                        // do not confuse UI
                        uiCD.chassisConfiguration = null;
                    }


                    chassisCD.chassisConfiguration.iomConfiguration.add(iomS);
                    ioms.put(iomS.fabric, true);

                    // if slots are identical across all IOMS we can configure them at once
                    if (iomCount == 0) {
                        chassisCD.chassisConfiguration.commonIOMConfiguration = iomS;
                    } else if (identicalIOMs &&
                            !isIOMSlotsIdentical(
                                    chassisCD.chassisConfiguration.commonIOMConfiguration, iomS)) {
                        chassisCD.chassisConfiguration.enableConfigAllIOM = false;
                        chassisCD.chassisConfiguration.configAllIOM = true;
                        identicalIOMs = false;
                    }

                    if (!iomS.iomconfigurable)
                        config.canConfigAllIOM = false;

                    if (config.commonIOMConfiguration == null) {
                        config.commonIOMConfiguration = iomS;
                        addCommonIom = true;
                    } else {
                        if ((config.canConfigAllIOM ||
                                config.enableConfigAllIOM) &&
                                !isIOMSlotsIdentical(config.commonIOMConfiguration, iomS)) {
                            config.canConfigAllIOM = false;
                            config.enableConfigAllIOM = false;
                            config.configAllIOM = true;
                        }
                    }


                    if (addCommonIom) {
                        config.iomConfiguration.add(iomS);
                    }
                }

                iomCount++;
            }

            if (fullConfiguration) {
                chassisCD.chassisConfiguration.iomCount = iomCount;

                if (iomCount < 6) {
                    for (String fabric : ioms.keySet()) {
                        if (!ioms.get(fabric)) {
                            UIIOMConfigurationSettings iomS = new UIIOMConfigurationSettings();
                            iomS.id = fabric;
                            iomS.fabric = fabric;
                            iomS.iompresent = false;
                            iomS.iomconfigurable = false;
                            chassisCD.chassisConfiguration.iomConfiguration.add(iomS);
                            if (addCommonIom) {
                                config.iomConfiguration.add(iomS);
                            }
                        }
                    }
                }

                int commonIOMCount = 0;
                for (UIIOMConfigurationSettings i : config.iomConfiguration) {
                    if (i.iompresent)
                        commonIOMCount++;
                }

                if (commonIOMCount != chassisCD.chassisConfiguration.iomCount)
                    config.canConfigAllIOM = false;


                chassisCD.chassisConfiguration.iomConfiguration.sort(new IomsComparator());
                config.iomConfiguration.sort(new IomsComparator());
            }
        }
    }

    /**
     * clone slot to be used with quad port mode insertions. Only List is allocated, ports are reused.
     * @param slot
     * @return
     */
    private List<UIPortConfigurationSettings> cloneSlot(List<UIPortConfigurationSettings> slot) {
        List<UIPortConfigurationSettings> ret = new ArrayList<>(slot);
        return ret;
    }

    private void addEmptySlots(List<UIPortConfigurationSettings> slotConfig) {
        for (int i = 0; i < 8 - slotConfig.size(); i++) {
            slotConfig.add(new UIPortConfigurationSettings());
        }
    }

    /**
     * compares two IOM slots. Slots are identical if all ports are identical.
     * @param commonIOMConfiguration
     * @param iomS
     * @return
     */
    private boolean isIOMSlotsIdentical(UIIOMConfigurationSettings commonIOMConfiguration,
                                        UIIOMConfigurationSettings iomS) {

        if (iomS.hasSlot1 == commonIOMConfiguration.hasSlot1) {
            if (iomS.hasSlot1) {
                if (iomS.slot1Config.size() != commonIOMConfiguration.slot1Config.size())
                    return false;

                for (int i = 0; i < iomS.slot1Config.size(); i++) {
                    if (!commonIOMConfiguration.slot1Config.get(i).equals(
                            iomS.slot1Config.get(i))) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        if (iomS.hasSlot2 == commonIOMConfiguration.hasSlot2) {
            if (iomS.hasSlot2) {
                if (iomS.slot2Config.size() != commonIOMConfiguration.slot2Config.size())
                    return false;

                for (int i = 0; i < iomS.slot2Config.size(); i++) {
                    if (!commonIOMConfiguration.slot2Config.get(i).equals(
                            iomS.slot2Config.get(i))) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        if (iomS.hasSlot3 == commonIOMConfiguration.hasSlot3) {
            if (iomS.hasSlot3) {
                if (iomS.slot3Config.size() != commonIOMConfiguration.slot3Config.size())
                    return false;

                for (int i = 0; i < iomS.slot3Config.size(); i++) {
                    if (!commonIOMConfiguration.slot3Config.get(i).equals(
                            iomS.slot3Config.get(i))) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    // Fo 0/33 -> Te 0/33, Te 0/34, Te 0/35, Te 0/36
    private void addQuadPorts(List<UIPortConfigurationSettings> slotConfig, int portNum,
                              int position) {
        for (int i = 1; i < 4; i++) {
            slotConfig.add(i + position, new UIPortConfigurationSettings("Te 0/" + (portNum + i)));
        }
    }

    /**
     * Currently used for Chassi sonly.
     *
     * @param dto
     * @param chassisId Chassis refId
     * @param fullConfiguration
     * @return
     */
    private UIConfigurableDevice getConfigurableDevice(ManagedDevice dto, String chassisId,
                                                       boolean fullConfiguration,
                                                       Map<String, String> refIds) {

        UIConfigurableDevice device = new UIConfigurableDevice();
        switch (dto.getDeviceType()) {
        case BladeServer:
        case RackServer:
        case TowerServer:
        case FXServer:
        case Server:
            //discoverDeviceType will be "idrac" for each of these servers, but for the certname, we need bladeserver/rackserver/fxserver
            device.id = dto.getDeviceType().getValue().toLowerCase() + "-" + dto.getServiceTag().toLowerCase();
            device.idForDelete = dto.getRefId();
            break;
        case dellswitch:
            device.id = dto.getRefId();
            device.idForDelete = dto.getRefId();
            break;
        default:
            String moduleName = dto.getDiscoverDeviceType().getPuppetModuleName();
            if (StringUtils.isEmpty(moduleName)) {
                moduleName = dto.getDiscoverDeviceType().name();
            }
            device.id = moduleName.toLowerCase() + "-" + dto.getServiceTag().toLowerCase();
            device.idForDelete = dto.getRefId();
        }
        refIds.put(device.id, dto.getRefId());

        device.ipAddress = dto.getIpAddress();
        device.isFWCompliant = dto.getCompliance() == CompliantState.COMPLIANT;
        device.model = dto.getModel();
        device.resourceType = DeviceController.getDeviceType(dto.getDeviceType());
        device.chassisId = chassisId;
        if (dto.getCompliance() != null)
            device.complianceDetails = dto.getCompliance().getLabel();
        else
            device.complianceDetails = CompliantState.UNKNOWN.getLabel();

        if (dto.getState() != null) {
            UIDeviceStatus state = DeviceController.mapToUIStatus(dto.getState());
            if (state != null) {
                device.state = state.getLabel();
            }
        }

        device.svctag = dto.getServiceTag();
        if (device.svctag != null && device.svctag.contains("INVALID_SVCTAG")) {
            device.deviceid = "";
        } else {
            device.deviceid = device.svctag;
        }

        device.updateFW = false;
        device.chassisConfiguration = new UIChassisConfigurationSettings();
        device.chassisConfiguration.deviceConfiguration = new UIDeviceConfiguration();
        device.chassisConfiguration.commonIOMConfiguration = new UIIOMConfigurationSettings();

        // read configuration from template, if any
        if (fullConfiguration) {
            String configTemplate = dto.getConfig();
            if (configTemplate != null) {
                ServiceTemplate template = MarshalUtil.unmarshal(ServiceTemplate.class,
                                                                 configTemplate);

                for (ServiceTemplateComponent component : template.getComponents()) {
                    for (ServiceTemplateCategory resource : component.getResources()) {
                        if (resource.getId().equals(
                                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_RESOURCE_ID)) {
                            parseChassisConfigTemplate(resource, device);
                        } else if (resource.getId().equals(
                                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_UPLINK_CONFIG_RESOURCE_ID)) {
                            parseUplinkConfigTemplate(resource, device);
                        }
                    }
                }
            }
        }

        return device;
    }

    private void parseChassisConfigTemplate(ServiceTemplateCategory resource,
                                            UIConfigurableDevice device) {
        UIDeviceConfiguration local = device.chassisConfiguration.deviceConfiguration;

        for (ServiceTemplateSetting setting : resource.getParameters()) {
            switch (setting.getId()) {
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_NAME:
                local.name = setting.getValue();
                break;
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_DNS_NAME:
                local.dnsDomainName = setting.getValue();
                break;
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_POWER_CAP:
                if (setting.getValue() != null)
                    local.systemInputPowercap = Double.parseDouble(setting.getValue());
                break;
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_POWER_CAP_TYPE:
                local.powercapMeasurementType = setting.getValue();
                break;
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_DATACENTER:
                local.dataCenter = setting.getValue();
                break;
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_AISLE:
                local.aisle = setting.getValue();
                break;
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_RACK:
                local.rack = setting.getValue();
                break;
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_RACKSLOT:
                local.rackSlot = setting.getValue();
                break;
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_STASH_MODE:
                local.storageMode = setting.getValue();
                break;
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_CONTROLS_CONFIGALLIOM:
                if (setting.getValue() != null)
                    device.chassisConfiguration.configAllIOM = Boolean.valueOf(setting.getValue());
                break;
            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_CONTROLS_UPLINKDEFINITIONS:
                if (setting.getValue() != null)
                    device.chassisConfiguration.uplinks = (List) fromJSON(List.class,
                                                                          setting.getValue());
                break;
            }
        }
    }


    private void parseUplinkConfigTemplate(ServiceTemplateCategory resource,
                                           UIConfigurableDevice device) {

        // we will store all configured ports under slot1
        device.chassisConfiguration.commonIOMConfiguration = new UIIOMConfigurationSettings();
        device.chassisConfiguration.commonIOMConfiguration.slot1Config.clear();

        List<String> uplinkIds = new ArrayList<>();
        for (ServiceTemplateSetting setting : resource.getParameters()) {
            if (setting.getId().equals(
                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_UPLINK)) {
                if (setting.getValue() != null) {
                    uplinkIds = (List) fromJSON(List.class, setting.getValue());
                }
            } else if (CollectionUtils.isNotEmpty(uplinkIds) &&
                    uplinkIds.contains(setting.getId()) &&
                    setting.getValue() != null) {
                TemplateUplink tu = (TemplateUplink) fromJSON(TemplateUplink.class,
                                                              setting.getValue());
                if (tu != null) {
                    for (String port : tu.portMembers) {
                        UIPortConfigurationSettings uiport = new UIPortConfigurationSettings(port);
                        uiport.uplinkId = tu.uplinkId;
                        uiport.uplinkName = tu.uplinkName;
                        device.chassisConfiguration.commonIOMConfiguration.slot1Config.add(uiport);
                    }
                }
            }
        }
    }


    /**
     * Apply configuration.
     *
     * @param request Config
     * @return chassis
     * @throws ServletException the servlet exception
     * @throws IOException      Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "configureresources", method = RequestMethod.POST)
    public JobResponse configureResources(@RequestBody JobConfigureResourcesRequest request,
                                          HttpSession session) {

        JobResponse jobResponse = new JobResponse();

        try {
            Map<String, String> refIds = sessionRefIds.get(session);
            if (refIds == null) {
                // sanity check, don't think it would ever happen
                throw new ControllerException(
                        "Invalid request. Please close the wizard and start again.");
            }

            List<String> ids = new ArrayList<>();
            for (UIConfigurableDevice uidevice : request.requestObj.devices)
                if (uidevice.updateFW) {
                    ids.add(refIds.get(uidevice.id));
                }

            ServiceTemplate config = createConfigurationTemplate(request.requestObj, ids, refIds);

            scheduleRemaining(ids);

            if (config.getComponents() != null && config.getComponents().size() > 0) {
                this.configureServiceAdapter.configurationRequest(config);
            }

            WizardStatus status = asmSetupStatusServiceAdapter.getASMSetupStatus();
            status.setIsConfigureCompleted(true);
            asmSetupStatusServiceAdapter.setASMSetupStatus(status);

            // cleanup - this must be the last call in wizard
            sessionRefIds.remove(session);

        } catch (Throwable t) {
            log.error("configureResources() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Apply uplink validation rules.
     * @throws ControllerException
     */
    private void validateUplinks(UIIOMConfigurationSettings iom, String vltPorts,
                                 List<TemplateUplink> tUplinks,
                                 boolean vltEnabled) throws ControllerException {

        /**
         * For all VLT links between two IOMs, we should not allow the user to mismatch ports on a VLT.
         * So if the user has a pair of IOMs, and they select ports 0/9 and 0/10 for VLT link, they should only be allowed
         * to select 0/9 and 0/10 on the other IOM in that fabric
         */
        if (vltPorts != null && StringUtils.isNotBlank(iom.fabric)) {
            String vPorts = vltMap.get(iom.fabric.substring(0, 1));
            if (vPorts != null) {
                if (!vPorts.equals(vltPorts)) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("error.VLT_PORTS_NOT_MATCHING", null,
                                                               LocaleContextHolder.getLocale()));
                }
            } else {
                vltMap.put(iom.fabric.substring(0, 1), vltPorts);
            }
        }
    }

    private void scheduleRemaining(List<String> ids) {
        if (ids == null || ids.size() == 0)
            return;

        FirmwareUpdateRequest updateRequest = new FirmwareUpdateRequest();
        updateRequest.setExitMaintenanceMode(true);
        updateRequest.setIdList(ids);
        updateRequest.setUpdateType(UpdateType.DEVICE);
        updateRequest.setScheduleType("updatenow");

        deviceInventoryServiceAdapter.scheduleUpdate(updateRequest);
    }

    /**
     * Process UI data and create configuration template.
     *
     * @param uiconfig
     * @return
     */
    private ServiceTemplate createConfigurationTemplate(UIConfigureResources uiconfig,
                                                        List<String> firmwareIDs,
                                                        Map<String, String> refIds) throws ControllerException {
        ServiceTemplate config = new ServiceTemplate();
        vltMap.clear();

        if (uiconfig.configGlobalChassisSettings) {
            for (UIConfigurableDevice uidevice : uiconfig.devices) {

                if (DeviceType.isChassis(uidevice.resourceType)) {

                    if (!uidevice.chassisConfiguration.configChassis) continue;

                    createConfigComponent(uidevice, config, uiconfig, null, null, null, firmwareIDs,
                                          refIds);
                }
            }
        }

        return config;
    }

    private void createConfigComponent(UIConfigurableDevice uidevice, ServiceTemplate config,
                                       UIConfigureResources uiconfig,
                                       UIIOMConfigurationSettings iomCommon,
                                       UIIOMConfigurationSettings iom,
                                       UIBladeConfigurationSettings blade,
                                       List<String> firmwareIDs,
                                       Map<String, String> refIds) throws ControllerException {
        ServiceTemplateComponent component = new ServiceTemplateComponent();
        component.setPuppetCertName(uidevice.id);
        component.setType(ServiceTemplateComponentType.CONFIGURATION);
        component.setIP(uidevice.ipAddress);

        // need refID
        component.setId(refIds.get(uidevice.id));
        component.setComponentID(uidevice.svctag);
        component.setName(uidevice.svctag);
        config.getComponents().add(component);

        // Determine whether to update the firmware
        boolean manageFirmware = false;
        if (firmwareIDs != null) {
            manageFirmware = firmwareIDs.remove(refIds.get(uidevice.id));
        }
        component.setManageFirmware(manageFirmware);


        if (DeviceType.isChassis(uidevice.resourceType)) {

            createChassisConfigurationResource(component, uiconfig, uidevice);

            if (uiconfig.configIOMMode != null &&
                    uiconfig.configIOMMode.equals("all")) {

                for (UIIOMConfigurationSettings iomS : uiconfig.iomConfiguration) {
                    if (!iomS.iompresent) continue;
                    UIConfigurableDevice iomDevice = new UIConfigurableDevice();
                    iomDevice.id = iomS.id;
                    iomDevice.ipAddress = iomS.managementIP;
                    iomDevice.svctag = iomS.svctag;
                    iomDevice.chassisId = uidevice.chassisId;
                    iomDevice.resourceType = DeviceController.getDeviceType(DeviceType.dellswitch);
                    iomDevice.chassisConfiguration = uidevice.chassisConfiguration;

                    // false means config all IOMs the same. It is confusing but this is how UI sends it.
                    if (!uiconfig.configAllIOM) {
                        createConfigComponent(iomDevice, config, uiconfig,
                                              uiconfig.commonIOMConfiguration, iomS, null,
                                              firmwareIDs, refIds);
                    } else {
                        createConfigComponent(iomDevice, config, uiconfig, iomS, iomS, null,
                                              firmwareIDs, refIds);
                    }
                }
            } else {

                for (UIIOMConfigurationSettings iomS : uidevice.chassisConfiguration.iomConfiguration) {
                    if (!iomS.iompresent) continue;
                    UIConfigurableDevice iomDevice = new UIConfigurableDevice();
                    iomDevice.id = iomS.id;
                    iomDevice.ipAddress = iomS.managementIP;
                    iomDevice.svctag = iomS.svctag;
                    iomDevice.chassisId = uidevice.chassisId;
                    iomDevice.resourceType = DeviceController.getDeviceType(DeviceType.dellswitch);
                    iomDevice.chassisConfiguration = uidevice.chassisConfiguration;

                    // false means config all IOMs the same. It is confusing but this is how UI sends it.
                    if (uiconfig.configUplinks && !uidevice.chassisConfiguration.configAllIOM) {
                        createConfigComponent(iomDevice, config, uiconfig,
                                              uidevice.chassisConfiguration.commonIOMConfiguration,
                                              iomS, null, firmwareIDs, refIds);
                    } else {
                        createConfigComponent(iomDevice, config, uiconfig, iomS, iomS, null,
                                              firmwareIDs, refIds);
                    }
                }
            }

            for (UIBladeConfigurationSettings bS : uidevice.chassisConfiguration.bladeConfiguration) {
                UIConfigurableDevice bladeDevice = new UIConfigurableDevice();
                bladeDevice.id = bS.id;
                bladeDevice.ipAddress = bS.managementIP;
                bladeDevice.svctag = bS.svctag;
                bladeDevice.chassisId = uidevice.chassisId;
                bladeDevice.resourceType = DeviceController.getDeviceType(DeviceType.BladeServer);
                bladeDevice.chassisConfiguration = uidevice.chassisConfiguration;
                createConfigComponent(bladeDevice, config, uiconfig, null, null, bS, firmwareIDs,
                                      refIds);
            }

        }

        if ((uidevice.resourceType.equals(
                DeviceController.getDeviceType(DeviceType.BladeServer)))) {
            createBladeConfigurationResource(component, uiconfig, blade, uidevice);
        }

        if (uidevice.resourceType.startsWith(
                DeviceController.getDeviceType(DeviceType.dellswitch))) {
            createIOMConfigurationResource(component, uiconfig, iomCommon, iom, uidevice);
        }

    }

    private void createChassisConfigurationResource(ServiceTemplateComponent component,
                                                    UIConfigureResources uiconfig,
                                                    UIConfigurableDevice uidevice) {

        ServiceTemplateCategory category = new ServiceTemplateCategory();
        ServiceTemplateSetting setting;

        category.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_RESOURCE_ID);
        component.getResources().add(category);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_REGISTER_CMC_DNS);
        setting.setValue(String.valueOf(
                uiconfig.globalConfiguration.registerChassisManagementControllerOnDns));
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.BOOLEAN);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_RED_POLICY);
        setting.setValue(uiconfig.globalConfiguration.redundancyPolicy);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_PERF);
        setting.setValue(
                String.valueOf(uiconfig.globalConfiguration.serverPerformanceOverPowerRedundancy));
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.BOOLEAN);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_DYNAMIC_POWER);
        setting.setValue(
                String.valueOf(uiconfig.globalConfiguration.enableDynamicPowerSupplyEngagement));
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.BOOLEAN);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_NTP_ENABLED);
        setting.setValue(String.valueOf(uiconfig.globalConfiguration.enableNTPServer));
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.BOOLEAN);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_NTP_PREF);
        setting.setValue(uiconfig.globalConfiguration.preferredNTPServer);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_NTP_SEC);
        setting.setValue(uiconfig.globalConfiguration.secondaryNTPServer);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_TIME_ZONE);
        if (uiconfig.globalConfiguration.timeZone != null)
            setting.setValue(
                    getTimeZoneValue(Integer.parseInt(uiconfig.globalConfiguration.timeZone)));

        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_USERS);
        if (uiconfig.globalConfiguration.users != null) {
            stripConfirmPasswords(uiconfig.globalConfiguration.users);
            setting.setValue(toJSON(uiconfig.globalConfiguration.users));
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        }
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_ALERT_DEST);
        if (uiconfig.globalConfiguration.trapSettings.length > 0) {
            setting.setValue(toJSON(uiconfig.globalConfiguration.trapSettings));
        }
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_EMAIL_DEST);
        setting.setValue(toJSON(uiconfig.globalConfiguration.destinationEmails));
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_SMTP_SERVER);
        setting.setValue(uiconfig.globalConfiguration.smtpServer);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_PROVIDER);
        setting.setValue(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_PROVIDER_ASM_DECRYPT);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        if (uiconfig.configUniqueChassisSettings) {
            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_NAME);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.name);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_DNS_NAME);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.dnsDomainName);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_POWER_CAP);
            setting.setValue(String.valueOf(
                    uidevice.chassisConfiguration.deviceConfiguration.systemInputPowercap));
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_POWER_CAP_TYPE);
            setting.setValue(
                    uidevice.chassisConfiguration.deviceConfiguration.powercapMeasurementType);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_DATACENTER);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.dataCenter);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_AISLE);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.aisle);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);


            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_STASH_MODE);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.storageMode);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);


            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_RACK);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.rack);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_RACKSLOT);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.rackSlot);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);
        }

        ServiceTemplateSetting controlParam = new ServiceTemplateSetting();
        controlParam.setId(
                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_CONTROLS_CONFIGALLIOM);
        controlParam.setValue(String.valueOf(uidevice.chassisConfiguration.configAllIOM));
        controlParam.setType(ServiceTemplateSetting.ServiceTemplateSettingType.BOOLEAN);
        controlParam.setHideFromTemplate(true);
        category.getParameters().add(controlParam);

        ServiceTemplateSetting uplinkSet;
        uplinkSet = new ServiceTemplateSetting();
        uplinkSet.setId(
                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_CONTROLS_UPLINKDEFINITIONS);
        uplinkSet.setValue(toJSON(uidevice.chassisConfiguration.uplinks));
        uplinkSet.setHideFromTemplate(true);
        uplinkSet.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(uplinkSet);


        String title = component.getPuppetCertName();
        ServiceTemplateSetting titleParam = new ServiceTemplateSetting();
        titleParam.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_TITLE_ID);
        titleParam.setValue(title);
        titleParam.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        titleParam.setRequired(false);
        titleParam.setHideFromTemplate(false);
        category.getParameters().add(titleParam);
    }

    private void stripConfirmPasswords(List<UIConfigureDeviceUser> users) {
        for (UIConfigureDeviceUser user : users) {
            user.confirmpassword = "";
        }
    }

    private void createBladeConfigurationResource(ServiceTemplateComponent component,
                                                  UIConfigureResources uiconfig,
                                                  UIBladeConfigurationSettings blade,
                                                  UIConfigurableDevice uidevice) {
        ServiceTemplateCategory category = new ServiceTemplateCategory();
        ServiceTemplateSetting setting;

        category.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_BLADE_CONFIG_RESOURCE_ID);
        component.getResources().add(category);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_REGISTER_IDRAC_DNS);
        setting.setValue(String.valueOf(uiconfig.globalConfiguration.registeriDracOnDns));
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.BOOLEAN);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_NTP_ENABLED);
        setting.setValue(String.valueOf(uiconfig.globalConfiguration.enableNTPServer));
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.BOOLEAN);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_NTP_PREF);
        setting.setValue(uiconfig.globalConfiguration.preferredNTPServer);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_NTP_SEC);
        setting.setValue(uiconfig.globalConfiguration.secondaryNTPServer);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_TIME_ZONE);
        if (uiconfig.globalConfiguration.timeZone != null)
            setting.setValue(
                    getTimeZoneValue(Integer.parseInt(uiconfig.globalConfiguration.timeZone)));
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_USERS);
        if (uiconfig.globalConfiguration.idracuserlist.size() > 0) {
            stripConfirmPasswords(uiconfig.globalConfiguration.idracuserlist);
            setting.setValue(toJSON(uiconfig.globalConfiguration.idracuserlist));
        }
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_ALERT_DEST);
        if (uiconfig.globalConfiguration.trapSettings != null) {
            setting.setValue(toJSON(uiconfig.globalConfiguration.trapSettings));
        }
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_EMAIL_DEST);
        setting.setValue(toJSON(uiconfig.globalConfiguration.destinationEmails));
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_BLADE_IPMI);
        setting.setValue(String.valueOf(uiconfig.globalConfiguration.enableipmi));
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.BOOLEAN);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_PROVIDER);
        setting.setValue(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_PROVIDER_ASM_DECRYPT);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        if (uiconfig.configUniqueChassisSettings) {
            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_NAME);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.name);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_DATACENTER);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.dataCenter);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_AISLE);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.aisle);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_RACK);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.rack);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_RACKSLOT);
            setting.setValue(uidevice.chassisConfiguration.deviceConfiguration.rackSlot);
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
            category.getParameters().add(setting);

        }

        if (uiconfig.configUniqueServerSettings) {
            if (blade.iDRACName != null) {
                setting = new ServiceTemplateSetting();
                setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_DNS_NAME);
                setting.setValue(blade.iDRACName);
                setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
                category.getParameters().add(setting);
            }
        }


        String title = component.getPuppetCertName();
        ServiceTemplateSetting titleParam = new ServiceTemplateSetting();
        titleParam.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_TITLE_ID);
        titleParam.setValue(title);
        titleParam.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        titleParam.setRequired(false);
        titleParam.setHideFromTemplate(false);
        category.getParameters().add(titleParam);
    }

    private String getTimeZoneValue(int i) {
        TimeZoneDto tdo = TimeZoneConfigurationMgr.getInstance().findDeviceZoneByAsmId(i);
        return tdo.getDisplayString();
    }

    private void createIOMConfigurationResource(ServiceTemplateComponent component,
                                                UIConfigureResources uiConfig,
                                                UIIOMConfigurationSettings iom,
                                                UIIOMConfigurationSettings iomUnique,
                                                UIConfigurableDevice uiDevice) throws ControllerException {
        /**
         * Uplinks.
         */
        boolean vlt_enabled = false;
        List<UIUplink> uplinks;
        boolean upload = false;
        if (uiConfig.configIOMMode != null &&
                uiConfig.configIOMMode.equals("all")) {
            uplinks = uiConfig.uplinks;
            vlt_enabled = uiConfig.vltenabled;
        } else if (uiConfig.configIOMMode != null &&
                uiConfig.configIOMMode.equals("upload")) {
            uplinks = new ArrayList<>();
            upload = true;
        } else {
            uplinks = uiDevice.chassisConfiguration.uplinks;
            vlt_enabled = uiDevice.chassisConfiguration.vltenabled;
        }

        if (vlt_enabled) {
            UIUplink vlt = new UIUplink();
            vlt.id = "vlt";
            vlt.uplinkId = "vlt";
            vlt.uplinkName = "VLT";
            if (!uplinks.contains(vlt)) {
                uplinks.add(vlt);
            }
        }

        ServiceTemplateCategory category;
        ServiceTemplateSetting setting;

        ServiceTemplateSetting titleParam = new ServiceTemplateSetting();
        titleParam.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_TITLE_ID);
        titleParam.setValue(component.getPuppetCertName());
        titleParam.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        titleParam.setRequired(false);
        titleParam.setHideFromTemplate(false);

        if (iom.iomconfigurable && uiConfig.configUplinks) {
            category = new ServiceTemplateCategory();
            category.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_UPLINK_CONFIG_RESOURCE_ID);
            component.getResources().add(category);

            setting = new ServiceTemplateSetting();
            setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_VLT);
            setting.setValue(String.valueOf(vlt_enabled));
            setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.BOOLEAN);
            category.getParameters().add(setting);

            if (upload) {
                Setting storedConfigSetting = settingServiceAdapter.getSettingByName(
                        SWITCH_CONFIG_CONTENT_SETTING);
                if (storedConfigSetting != null) {
                    setting = new ServiceTemplateSetting();
                    setting.setId(
                            ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_UPLINK_FILE);
                    setting.setValue(storedConfigSetting.getValue());
                    setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
                    category.getParameters().add(setting);
                } else {
                    throw new ControllerException(
                            getApplicationContext().getMessage("error.SWITCH_CONFIG_NOT_AVAILABLE",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
            } else {
                List<String> vltPorts = new ArrayList<>();
                // filter out uplinks by IOM
                List<TemplateUplink> tUplinks = new ArrayList<>();
                List<String> tUplinkIdList = new ArrayList<>();
                for (UIUplink uplink : uplinks) {
                    List<String> members = findUplinkMembers(iom, uplink);
                    if (members.size() > 0) {

                        TemplateUplink tu = new TemplateUplink();
                        tu.uplinkId = uplink.uplinkId;
                        tu.uplinkName = uplink.uplinkName;
                        tu.portChannel = uplink.portChannel;
                        tu.portMembers = members;
                        tu.portNetworks = uplink.networks;

                        tUplinks.add(tu);
                        tUplinkIdList.add(tu.uplinkId);

                        if (uplink.uplinkId.equals("vlt")) {
                            vltPorts.addAll(members);
                        }
                    }
                }

                if (tUplinkIdList.size() > 0) {
                    Collections.sort(vltPorts);

                    validateUplinks(iom,
                                    Arrays.toString(vltPorts.toArray(new String[vltPorts.size()])),
                                    tUplinks, vlt_enabled);

                    setting = new ServiceTemplateSetting();
                    setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_UPLINK);
                    setting.setValue(toJSON(tUplinkIdList));
                    setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
                    category.getParameters().add(setting);

                    for (TemplateUplink tu : tUplinks) {
                        setting = new ServiceTemplateSetting();
                        setting.setId(tu.uplinkId);
                        setting.setValue(toJSON(tu));
                        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
                        category.getParameters().add(setting);
                    }
                } else {
                    if (StringUtils.isNotBlank(iom.fabric))
                        vltMap.put(iom.fabric.substring(0, 0), "[]");
                }

                setting = new ServiceTemplateSetting();
                setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_IOM_QUAD);
                setting.setValue(String.valueOf(iom.quadPortMode));
                setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.BOOLEAN);
                category.getParameters().add(setting);
            }
            category.getParameters().add(titleParam);
        }

        /*
         * IOM individual and common settings
         */

        category = new ServiceTemplateCategory();
        category.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_IOM_CONFIG_RESOURCE_ID);
        component.getResources().add(category);

        if (uiConfig.configUniqueIOMSettings) {
            if (iomUnique.hostName != null) {
                setting = new ServiceTemplateSetting();
                setting.setId(
                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_IOM_HOSTNAME);
                setting.setValue(iomUnique.hostName);
                setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
                category.getParameters().add(setting);
            }
        }

        if (iom.iomconfigurable && uiConfig.configUplinks) {
            if (uiConfig.spanningTreeMode != null) {
                setting = new ServiceTemplateSetting();
                setting.setId(
                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_SPANNING_TREE_MODE);
                setting.setValue(uiConfig.spanningTreeMode);
                setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
                category.getParameters().add(setting);
            }
        }

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_IOM_SYSLOG);
        setting.setValue(uiConfig.globalConfiguration.syslogDestination);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_IOM_NTP1);
        setting.setValue(uiConfig.globalConfiguration.preferredNTPServer);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        setting = new ServiceTemplateSetting();
        setting.setId(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CHASSIS_CONFIG_IOM_NTP2);
        setting.setValue(uiConfig.globalConfiguration.secondaryNTPServer);
        setting.setType(ServiceTemplateSetting.ServiceTemplateSettingType.STRING);
        category.getParameters().add(setting);

        // close by title
        category.getParameters().add(titleParam);

    }

    private List<String> findUplinkMembers(UIIOMConfigurationSettings iom, UIUplink uplink) {
        List<String> result = new ArrayList<>();
        if (iom.hasSlot1) {
            for (UIPortConfigurationSettings port : iom.slot1Config) {
                if (port.uplinkId != null && port.uplinkId.equalsIgnoreCase(uplink.uplinkId)) {
                    result.add(adjustPortId(port.portId, port.portType));
                }
            }
        }
        if (iom.hasSlot2) {
            for (UIPortConfigurationSettings port : iom.slot2Config) {
                if (port.uplinkId != null && port.uplinkId.equalsIgnoreCase(uplink.uplinkId)) {
                    result.add(adjustPortId(port.portId, port.portType));
                }
            }
        }
        if (iom.hasSlot3) {
            for (UIPortConfigurationSettings port : iom.slot3Config) {
                if (port.uplinkId != null && port.uplinkId.equalsIgnoreCase(uplink.uplinkId)) {
                    result.add(adjustPortId(port.portId, port.portType));
                }
            }
        }

        return result;
    }

    private String adjustPortId(String portId, String portType) {
        String port = portId;
        if (portId != null && portType != null) {
            if (!portId.startsWith(portType)) {
                port = portType + portId.substring(portId.indexOf(" "));
            }
        }
        return port;
    }

    private String toJSON(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Cannot write as JSON chassis user list", e);
            return "";
        }
    }

    private Object fromJSON(Class claz, String source) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(source, claz);
        } catch (IOException e) {
            log.error("Cannot read JSON [" + source + "] for class " + claz.getName(), e);
        }
        return null;
    }

    public class IomsComparator implements Comparator<UIIOMConfigurationSettings> {
        @Override
        public int compare(UIIOMConfigurationSettings x, UIIOMConfigurationSettings y) {
            if (x == null && y == null)
                return 0;
            if (x == null || x.fabric == null)
                return -1;
            if (y == null || y.fabric == null)
                return 1;

            return x.fabric.compareTo(y.fabric);
        }
    }

    public class SlotComparator implements Comparator<UIPortConfigurationSettings> {
        @Override
        public int compare(UIPortConfigurationSettings x, UIPortConfigurationSettings y) {
            if (x == null && y == null)
                return 0;
            if (x == null || x.portNumber == 0)
                return -1;
            if (y == null || y.portNumber == 0)
                return 1;
            return Integer.compare(y.portNumber, x.portNumber);
        }
    }
}