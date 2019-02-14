/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.brownfield.BrownfieldServiceType;
import com.dell.asm.asmcore.asmmanager.client.brownfield.BrownfieldStatus;
import com.dell.asm.asmcore.asmmanager.client.brownfield.NetworkSetting;
import com.dell.asm.asmcore.asmmanager.client.brownfield.PasswordSetting;
import com.dell.asm.asmcore.asmmanager.client.brownfield.PortGroupSetting;
import com.dell.asm.asmcore.asmmanager.client.brownfield.VDSSetting;
import com.dell.asm.asmcore.asmmanager.client.deployment.AsmDeployerLogEntry;
import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.deployment.DeploymentDevice;
import com.dell.asm.asmcore.asmmanager.client.deployment.DeploymentHealthStatusType;
import com.dell.asm.asmcore.asmmanager.client.deployment.DeploymentStatusType;
import com.dell.asm.asmcore.asmmanager.client.deployment.PuppetLogEntry;
import com.dell.asm.asmcore.asmmanager.client.devicegroup.DeviceGroup;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.CompliantState;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.DeviceHealth;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareUpdateRequest;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareUpdateRequest.UpdateType;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.Cluster;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.ProtectionDomain;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.PuppetScaleIOSystem;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.StoragePool;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.Volume;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareRepository;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.Interface;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.NetworkConfiguration;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.Partition;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.Network;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateCategory;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponent;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponentSubType;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponentType;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateOption;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSetting;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSettingIDs;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.StaticNetworkConfiguration;
import com.dell.asm.asmcore.asmmanager.client.util.ServiceTemplateClientUtil;
import com.dell.asm.asmcore.user.model.User;
import com.dell.asm.common.utilities.ASMCommonsMessages;
import com.dell.asm.common.utilities.ASMCommonsUtils;
import com.dell.asm.i18n2.exception.AsmRuntimeException;
import com.dell.asm.ui.adapter.service.BrownfieldServiceAdapter;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceGroupServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.FirmwareRepositoryServiceAdapter;
import com.dell.asm.ui.adapter.service.NetworkServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.TemplateServiceAdapter;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.model.FieldError;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringsRequest;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIActivityLog;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.device.UIDevice;
import com.dell.asm.ui.model.device.UINetworkResource;
import com.dell.asm.ui.model.device.UIScaleIOStoragePool;
import com.dell.asm.ui.model.device.UIScaleIOStorageVolume;
import com.dell.asm.ui.model.device.UIVolume;
import com.dell.asm.ui.model.network.UIPortGroup;
import com.dell.asm.ui.model.server.UIIPAddress;
import com.dell.asm.ui.model.service.JobAddExistingServiceRequest;
import com.dell.asm.ui.model.service.JobAdjustServiceComponentRequest;
import com.dell.asm.ui.model.service.JobAdjustServiceRequest;
import com.dell.asm.ui.model.service.JobComponentDataRequest;
import com.dell.asm.ui.model.service.JobCreateServiceRequest;
import com.dell.asm.ui.model.service.JobDeleteResourceRequest;
import com.dell.asm.ui.model.service.JobDeleteServiceRequest;
import com.dell.asm.ui.model.service.JobGetExistingServiceRequest;
import com.dell.asm.ui.model.service.JobPuppetLogRequest;
import com.dell.asm.ui.model.service.JobRemoveServiceRequest;
import com.dell.asm.ui.model.service.JobServiceIdRequest;
import com.dell.asm.ui.model.service.JobServiceRequest;
import com.dell.asm.ui.model.service.JobStopManagingRequest;
import com.dell.asm.ui.model.service.JobUpdateExistingServiceRequest;
import com.dell.asm.ui.model.service.JobUpdateServiceFirmwareRequest;
import com.dell.asm.ui.model.service.JobUpdateServiceNetworkResourceRequest;
import com.dell.asm.ui.model.service.JobUpdateServiceRequest;
import com.dell.asm.ui.model.service.UIComponentData;
import com.dell.asm.ui.model.service.UIDeleteResourceRequest;
import com.dell.asm.ui.model.service.UIDeleteServiceRequest;
import com.dell.asm.ui.model.service.UIDeploy;
import com.dell.asm.ui.model.service.UIExistingServiceNetwork;
import com.dell.asm.ui.model.service.UIOSCredential;
import com.dell.asm.ui.model.service.UIRemoveServiceRequest;
import com.dell.asm.ui.model.service.UIService;
import com.dell.asm.ui.model.service.UIServiceHealth;
import com.dell.asm.ui.model.service.UIServiceNetworkResource;
import com.dell.asm.ui.model.service.UIServiceSummary;
import com.dell.asm.ui.model.service.UIStopManagingRequest;
import com.dell.asm.ui.model.service.UIUpdateServiceFirmwareRequest;
import com.dell.asm.ui.model.service.UIVSwitch;
import com.dell.asm.ui.model.template.JobTemplateBuilderRequest;
import com.dell.asm.ui.model.template.UIComponentStatus;
import com.dell.asm.ui.model.template.UIRelatedComponent;
import com.dell.asm.ui.model.template.UITemplateBuilder;
import com.dell.asm.ui.model.template.UITemplateBuilderCategory;
import com.dell.asm.ui.model.template.UITemplateBuilderComponent;
import com.dell.asm.ui.model.template.UITemplateBuilderListItem;
import com.dell.asm.ui.model.template.UITemplateBuilderRequest;
import com.dell.asm.ui.model.template.UITemplateBuilderSetting;
import com.dell.asm.ui.model.users.UIUser;
import com.dell.asm.ui.util.ConversionUtility;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.asm.identitypool.api.common.model.NetworkType;
import com.dell.pg.orion.common.print.Dump;
import com.dell.pg.orion.common.utilities.MarshalUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class Service Controller.
 */
@RestController
@RequestMapping(value = "/services/")
public class ServicesController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(ServicesController.class);
    private static final String MATCH_ANY = "*";
    private DeploymentServiceAdapter serviceAdapter;
    private TemplateServiceAdapter templateServiceAdapter;
    private NetworkServiceAdapter networkServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private DeviceGroupServiceAdapter deviceGroupServiceAdapter;
    private FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter;
    private BrownfieldServiceAdapter brownfieldServiceAdapter;
    private ObjectMapper objectMapper;

    @Autowired
    public ServicesController(DeploymentServiceAdapter serviceAdapter,
                              TemplateServiceAdapter templateServiceAdapter,
                              NetworkServiceAdapter networkServiceAdapter,
                              DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                              DeviceGroupServiceAdapter deviceGroupServiceAdapter,
                              FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter,
                              ObjectMapper objectMapper,
                              BrownfieldServiceAdapter brownfieldServiceAdapter) {
        this.serviceAdapter = serviceAdapter;
        this.templateServiceAdapter = templateServiceAdapter;
        this.networkServiceAdapter = networkServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.deviceGroupServiceAdapter = deviceGroupServiceAdapter;
        this.firmwareRepositoryServiceAdapter = firmwareRepositoryServiceAdapter;
        this.objectMapper = objectMapper;
        this.brownfieldServiceAdapter = brownfieldServiceAdapter;
    }

    /**
     * Parse DeploymentDevice into UIDevice. It is important to NOT make any calls to REST API in this method!
     * If you need something extra - add it to DeploymentDevice
     *
     * @param dto
     * @param applicationContext
     * @return
     */
    public static UIDevice parseManagedDeviceToDevice(DeploymentDevice dto,
                                                      ApplicationContext applicationContext,
                                                      Deployment deployment) {
        UIDevice dev = new UIDevice();

        if (dto.getDeviceType() != null) {
            dev.deviceType = DeviceController.getDeviceType(dto.getDeviceType());
        }

        dev.memory = "N/A";
        dev.processorcount = -1;
        dev.ipaddresstype = "management";
        dev.deviceidtype = "servicetag";

        if (!MappingUtils.isReadOnlyUser()) {
            if (DeviceType.isServer(dto.getDeviceType()) ||
                    DeviceType.isChassis(dto.getDeviceType()) ||
                    dto.getDeviceType() == DeviceType.vcenter ||
                    dto.getDeviceType() == DeviceType.rhvm ||
                    dto.getDeviceType() == DeviceType.scaleio) {
                if (ASMCommonsUtils.isValidIp(
                        dto.getIpAddress())) {  // Dont' want to make it a URL if it's "Not Found"
                    dev.ipaddressurl = "https://" + dto.getIpAddress();
                }
            } else if (dto.getDeviceType() == DeviceType.AggregatorIOM ||
                    dto.getDeviceType() == DeviceType.dellswitch ||
                    dto.getDeviceType() == DeviceType.MXLIOM ||
                    dto.getDeviceType() == DeviceType.scvmm ||
                    dto.getDeviceType() == DeviceType.storage) {
                dev.ipaddressurl = "";
            }
        }

        if (DeviceType.Server.equals(dto.getDeviceType())) {
            dev.ipaddresstype = "host";
            dev.deviceidtype = "serialnumber";
            dev.serialnumber = dto.getServiceTag();
        }

        dev.id = dto.getRefId();
        dev.asmGUID = dto.getRefId();
        dev.ipAddress = dto.getIpAddress();
        dev.serviceTag = dto.getServiceTag();
        if (StringUtils.isNotEmpty(dev.serviceTag) && dev.serviceTag.contains("INVALID_SVCTAG")) {
            dev.serviceTag = "";
        }
        dev.deviceid = dev.serviceTag;

        if (dto.getDeviceHealth() != null) {
            dev.health = dto.getDeviceHealth().getLabel();
        } else {
            dev.health = DeviceHealth.UNKNOWN.getLabel();
        }

        if (dto.getHealthMessage() != null) {
            dev.healthmessage = dto.getHealthMessage();
        } else {
            dev.healthmessage = "";
        }

        if (dto.getStatus() != null) {
            dev.status = dto.getStatus().getValue().toLowerCase();
        } else {
            dev.status = DeploymentStatusType.PENDING.getValue().toLowerCase();
        }

        if (deployment.getFirmwareRepository() != null && dto.getCompliantState() != null) {
            dev.compliant = dto.getCompliantState().getLabel();
        } else {
            dev.compliant = CompliantState.UNKNOWN.getLabel();
        }

        if (!BrownfieldStatus.NOT_APPLICABLE.equals(dto.getBrownfieldStatus())) {
            dev.state = "unmanaged";
            dev.brownfieldStatus = "removed";
            switch (dto.getBrownfieldStatus()) {
            case AVAILABLE:
                dev.state = "managed";
                dev.brownfieldStatus = "new";
                break;
            case NEWLY_AVAILABLE:
                dev.state = "managed";
                dev.brownfieldStatus = "new";
                break;
            case CURRENTLY_DEPLOYED_IN_BROWNFIELD:
                dev.brownfieldStatus = "existing";
                dev.state = "managed";
                break;
            case UNAVAILABLE_RELATED_SERVER_NOT_IN_INVENTORY:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableRelatedServerNotInInventory", null,
                        LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_RELATED_SERVER_IN_EXISTING_SERVICE:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableRelatedServerInExistingService", null,
                        LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_NOT_IN_INVENTORY:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableNotInInventory", null,
                        LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_IN_EXISTING_SERVICE:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableInExistingService", null,
                        LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_THIS_DEVICE_AND_RELATED_SERVER_NOT_IN_INVENTORY:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableThisDeviceAndRelatedServerNotInInventory",
                        null, LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_THIS_DEVICE_NOT_IN_INVENTORY_AND_RELATED_SERVER_IN_EXISTING_SERVICE:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableThisDeviceNotInInventoryAndRelatedServerInExistingService",
                        null, LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_NOT_MANAGED_OR_RESERVED:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableNotManagedOrReserved", null,
                        LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_RELATED_SERVER_NOT_MANAGED_OR_RESERVED:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableRelatedServerNotManagedOrReserved", null,
                        LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_THIS_DEVICE_AND_RELATED_SERVER_NOT_MANAGED_OR_RESERVED:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableThisDeviceAndRelatedServerNotManagedOrReserved",
                        null, LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_THIS_DEVICE_NOT_MANAGED_OR_RESERVED_AND_RELATED_SERVER_NOT_IN_INVENTORY:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavaialbleThisDeviceNotManagedOrReservedAndRelatedServerNotInInventory",
                        null, LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_THIS_DEVICE_NOT_MANAGED_OR_RESERVED_AND_RELATED_SERVER_IN_EXISTING_SERVICE:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableThisDeviceNotManagedOrServedAndRelatedServerInExistingService",
                        null, LocaleContextHolder.getLocale());
                break;
            case UNAVAILABLE_THIS_DEVICE_NOT_IN_INVENTORY_AND_RELATED_SERVER_NOT_MANAGED_OR_RESERVED:
                dev.brownfieldStatus = "unavailable";
                dev.state = "unmanaged";
                dev.healthmessage = applicationContext.getMessage(
                        "BrownfieldStatus.UnavailableThisDeviceNotInInventoryAndRelatedServerNotManagedOrReserved",
                        null, LocaleContextHolder.getLocale());
                break;
            default:
                // Do Nothing
                break;
            }
        }
        return dev;
    }

    /**
     * Returns a string list of IPs
     *
     * Only returns public/private LAN addresses. Only returns statically set IP's
     *
     *
     * @param setting ServiceTemplateSetting
     * @return List UIIPAddress
     */
    public static List<UIIPAddress> getStaticVMIps(ServiceTemplateSetting setting) {
        Set<UIIPAddress> ipSet = new HashSet<>();
        List<Network> networks = setting.getNetworks();
        if (networks != null) {
            for (Network network : networks) {
                if (network.isStatic()) {
                    if (NetworkType.PRIVATE_LAN.equals(network.getType()) ||
                            NetworkType.PUBLIC_LAN.equals(network.getType())) {
                        StaticNetworkConfiguration staticConfig = network.getStaticNetworkConfiguration();
                        UIIPAddress ipInfo = new UIIPAddress(staticConfig.getIpAddress());
                        ipSet.add(ipInfo);
                    }
                }
            }
        }
        return new ArrayList<>(ipSet);
    }


    public static String getServiceState(final Deployment deployment) {
        switch (deployment.getStatus()) {
        case ERROR:
        case CANCELLED:
        case COMPLETE:
        case INCOMPLETE:
        case PENDING:
        case SERVICE_MODE:
            return deployment.getStatus().getLabel();
        case CANCELLING:
        case FIRMWARE_UPDATING:
        case IN_PROGRESS:
        case POST_DEPLOYMENT_SOFTWARE_UPDATING:
            return DeploymentStatusType.IN_PROGRESS.getLabel();
        default:
            return StringUtils.EMPTY; // should never happen
        }
    }

    public static UIServiceHealth getServiceHealth(final Deployment deployment) {
        switch (deployment.getStatus()) {
        case ERROR:
            return UIServiceHealth.red;
        case CANCELLED:
            return UIServiceHealth.cancelled;
        case COMPLETE:
            if (DeploymentHealthStatusType.GREEN.equals(deployment.getDeploymentHealthStatusType()))
                return UIServiceHealth.green;
            else if (DeploymentHealthStatusType.YELLOW.equals(
                    deployment.getDeploymentHealthStatusType()))
                return UIServiceHealth.yellow;
            else if (DeploymentHealthStatusType.RED.equals(
                    deployment.getDeploymentHealthStatusType()))
                return UIServiceHealth.red;
            else if (DeploymentHealthStatusType.CANCELLED.equals(
                    deployment.getDeploymentHealthStatusType()))
                return UIServiceHealth.cancelled;
            else {
                log.warn("Unrecognized DeploymentHealthStatusType = " + deployment.getDeploymentHealthStatusType().name());
                return UIServiceHealth.unknown;
            }
        case INCOMPLETE:
            return UIServiceHealth.incomplete;
        case PENDING:
            return UIServiceHealth.pending;
        case SERVICE_MODE:
            return UIServiceHealth.servicemode;
        case FIRMWARE_UPDATING:
        case IN_PROGRESS:
        default:
            return UIServiceHealth.unknown;
        }
    }

    /**
     * Returns a list of UI Usable IP Addresses
     *
     * @param component a server ServiceTemplateComponent
     * @return UIAddress List
     */
    public static List<UIIPAddress> getOSIps(ServiceTemplateComponent component) {
        Set<UIIPAddress> ipSet = new HashSet<>();
        List<Network> networks = ServiceTemplateClientUtil.findStaticUINetworks(component);
        if (networks != null && networks.size() > 0) {
            for (Network network : networks) {
                if (network.isStatic() &&
                        network.getStaticNetworkConfiguration() != null &&
                        StringUtils.isNotBlank(network.getStaticNetworkConfiguration().getIpAddress())) {
                    ipSet.add(new UIIPAddress(network.getStaticNetworkConfiguration().getIpAddress()));
                }
            }
        } else {
            if (component.isBrownfield()) {
                ServiceTemplateSetting networkSetting = component.getParameter(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_NETWORKING_COMP_ID,
                                                                               ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_NETWORK_CONFIG_ID);
                Network network = null;
                UIIPAddress ipInfo;
                if (networkSetting != null &&
                        CollectionUtils.isNotEmpty(networkSetting.getNetworks())) {
                    network = networkSetting.getNetworks().stream()
                            .filter(n -> NetworkType.HYPERVISOR_MANAGEMENT.equals(n.getType()) ||
                                    NetworkType.SCALEIO_MANAGEMENT.equals(n.getType()))
                            .findFirst().orElse(null);
                }
                if (network != null &&
                        network.isStatic() &&
                        network.getStaticNetworkConfiguration() != null &&
                        StringUtils.isNotBlank(network.getStaticNetworkConfiguration().getIpAddress())) {
                    ipInfo = new UIIPAddress(network.getStaticNetworkConfiguration().getIpAddress());
                } else {
                    ipInfo = new UIIPAddress(component.getParameterValue(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_RESOURCE,
                                                                         ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_HOSTNAME_ID));
                }
                ipSet.add(ipInfo);
            } else {
                Network defStaticNetwork = ServiceTemplateClientUtil.findDefaultStaticNetwork(
                        component);
                if (defStaticNetwork != null) {
                    UIIPAddress ipInfo = new UIIPAddress(
                            defStaticNetwork.getStaticNetworkConfiguration().getIpAddress());
                    ipSet.add(ipInfo);
                }
            }
        }
        return new ArrayList<>(ipSet);
    }

    /**
     * Web service exposing generated csv export for all services
     * @param response
     */
    @RequestMapping(value = "exportservice", method = RequestMethod.GET)
    public void exportService(HttpServletResponse response) {
        generateServiceExport(null, response);
    }

    /**
     * Web service exposing generated csv export for a particular service
     * @param serviceId
     * @param response
     */
    @RequestMapping(value = "exportservice/{serviceId}", method = RequestMethod.GET)
    public void exportService(@PathVariable String serviceId, HttpServletResponse response) {
        generateServiceExport(serviceId, response);
    }

    /**
     * The actual method for generating the csv response
     *
     * @param serviceId
     * @param response
     *
     * @deprecated - Use com.dell.asm.ui.controller.DownloadController - "downloads/getfile" instead
     */
    @Deprecated
    private void generateServiceExport(String serviceId, HttpServletResponse response) {
        log.debug(
                "Beginning generateServiceExport(String, HttpServletResponse) for serviceId " + serviceId);

        try {
            Locale locale = LocaleContextHolder.getLocale();
            ArrayList<Deployment> deployments = this.getDeploymentsForExportToFile(serviceId);
            ArrayList<UIService> services = this.getUIServiceForDeployments(deployments, locale);

            // Define the Titles for all of the entries in the CSV File
            String serviceTitles = this.getServiceString(locale);
            String appTitles = serviceTitles + "\"App Name\",";
            String vmTitles = serviceTitles + "\"VM Name\",\"CPU\",\"Memory\",\"Disk\",\"OS Type\",";
            String clusterTitles = serviceTitles + "\"Component Name\",\"Ip Address\",\"Asset/Service Tag\",";
            String serverTitles = serviceTitles
                    + "\"Component Name\",\"Hostname\",\"IP Address\",\"Hypervisor IP Address\",\"Asset/Service Tag\",";
            String storageTitles = serviceTitles + "\"Component Name\",\"IP Address\",\"Asset/Service Tag\",";

            // Components in the deployment may be in varying orders, so we keep
            // a running total and then write at end
            StringBuilder appReportSection = new StringBuilder();
            StringBuilder vmReportSection = new StringBuilder();
            StringBuilder clusterReportSection = new StringBuilder();
            StringBuilder serverReportSection = new StringBuilder();
            StringBuilder storageReportSection = new StringBuilder();

            // Get the information for each deployment in the list: either one
            // deployment, or all deployments are expected.
            for (UIService service : services) {
                String lineStart = this.getExportServiceLineItemStart(service);
                appReportSection.append(this.getAppReport(service, appTitles, lineStart));
                vmReportSection.append(
                        this.getVmReport(service.vmlist, service.components, vmTitles, lineStart));
                clusterReportSection.append(
                        this.getClusterReport(service.clusterlist, service.components,
                                              clusterTitles, lineStart));
                serverReportSection.append(
                        this.getServerReport(service.serverlist, service.components, serverTitles,
                                             lineStart));
                storageReportSection.append(
                        this.getStorageReport(service.storagelist, service.components,
                                              storageTitles, lineStart));
            }

            // Put together the report form the various results/sections
            StringBuilder report = new StringBuilder();
            report.append(appReportSection);
            report.append(vmReportSection);
            report.append(clusterReportSection);
            report.append(serverReportSection);
            report.append(storageReportSection);

            // Replace last comma from ending of the file with end of line to
            // close out the report
            if (report.length() > 10) {
                report.deleteCharAt(report.length() - 1);
                report.append("\r\n");
            }

            // Write it to the response so it's sent back as a download
            response.setStatus(200);
            response.setContentLength(report.length());
            response.setContentType("text/csv; charset=utf-8");
            response.setHeader("Content-Disposition",
                               "attachment; filename=\"" + this.getReportFileName(services,
                                                                                  locale) + "\"");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");

            ServletOutputStream out = response.getOutputStream();
            out.println(report.toString());
            out.flush();
            out.close();
        } catch (Throwable t) {
            log.error(
                    "Generate logs error in generateServiceExport(String, HttpServletResponse) for serviceId "
                            + serviceId, t);
        }
    }

    private String getAppReport(UIService service, String headers, String lineStart) {
        StringBuilder appReport = new StringBuilder();

        List<UITemplateBuilderComponent> components = service.components;

        // Should only write headers if there are applications
        boolean headersWritten = false;

        for (UITemplateBuilderComponent component : components) {
            if (component.type.equals("application")) {
                if (!headersWritten) {
                    appReport.append(headers);
                    appReport.append("\r\n");
                    headersWritten = true;
                }
                appReport.append(lineStart);
                appReport.append(this.getAppReportLine(component, components));
                appReport.append("\r\n");
            }
        }
        return appReport.toString();
    }

    private String getNameFromComponentById(List<UITemplateBuilderComponent> components,
                                            String id) {
        String name = "";

        if (id != null) {
            for (UITemplateBuilderComponent component : components) {
                if (id.equals(component.id))
                    name = component.name;
            }
        }

        return name;
    }

    // Returns the Deployments for a given Service as an Array, if there is only
    // one Deployment then it is found as the [0] (or first) entry in the Deployment Array
    private ArrayList<Deployment> getDeploymentsForExportToFile(String serviceId) {
        ArrayList<Deployment> deployments = new ArrayList<>();

        if (serviceId == null) {
            ResourceList<Deployment> mList = serviceAdapter.getDeployments(null, null, null,
                                                                           MappingUtils.MAX_RECORDS,
                                                                           Boolean.FALSE);
            if (mList != null && mList.getList() != null)
                deployments.addAll(Arrays.asList(mList.getList()));
        } else {
            Deployment deployment = serviceAdapter.getDeployment(serviceId);
            if (deployment != null) {
                deployments.add(deployment);
            }
        }

        return deployments;
    }

    // Returns the serviceTitles line for the exported CSV File
    private String getServiceString(Locale locale) {
        String serviceTitles = "\"Name of Service\"," +
                "\"Status of Service\"," +
                "\"" + getApplicationContext().getMessage("ServiceController.createBy", null,
                                                          locale) + "\"," +
                "\"" + getApplicationContext().getMessage("ServiceController.createdDate", null,
                                                          locale) + "\"," +
                "\"Reference Template\",";

        return serviceTitles;
    }

    // Returns the String that Begins the first line of every Component entry
    // int he Export Log File CSV
    private String getExportServiceLineItemStart(UIService service) {
        String lineStart = "\"" + service.name + "\"," +
                "\"" + service.state + "\"," +
                "\"" + service.deployedBy + "\"," +
                "\"" + service.createddate + "\"," +
                "\"" + service.template + "\",";

        return lineStart;
    }

    // Gets the report portion for VMs
    private String getVmReport(List<UIDevice> vmDevices,
                               List<UITemplateBuilderComponent> components, String headers,
                               String lineStart) {
        StringBuilder vmReport = new StringBuilder();
        if (vmDevices.size() > 0) {
            // Write Headers
            vmReport.append(headers);
            vmReport.append("\r\n");
            for (UIDevice vmDevice : vmDevices) {
                vmReport.append(lineStart);
                vmReport.append(this.getVmReportLine(vmDevice, components));
                vmReport.append("\r\n");
            }
        }
        return vmReport.toString();
    }

    // Gets the report portion for Servers
    private String getServerReport(List<UIDevice> serverDevices,
                                   List<UITemplateBuilderComponent> components,
                                   String headers, String lineStart) {
        StringBuilder serverReport = new StringBuilder();
        if (serverDevices.size() > 0) {
            // Write Headers
            serverReport.append(headers);
            serverReport.append("\r\n");
            for (UIDevice serverDevice : serverDevices) {
                serverReport.append(lineStart);
                serverReport.append(this.getServerReportLine(serverDevice, components));
                serverReport.append("\r\n");
            }
        }
        return serverReport.toString();
    }

    // Returns the Server line entry for the UIDevice
    private String getServerReportLine(UIDevice serverDevice,
                                       List<UITemplateBuilderComponent> components) {
        StringBuilder serverLine = new StringBuilder();
        // COLUMNS: "\"Component Name\",\"Hostname\",\"IP Address\",\"Hypervisor IP Address\",\"Asset/Service Tag\",";

        serverLine.append("\"");
        // if(serverDevice.name != null) serverLine.append(serverDevice.name); // component name
        if (serverDevice.id != null) {
            serverLine.append(this.getNameFromComponentById(components, serverDevice.id));
        }
        serverLine.append("\",\"");
        if (serverDevice.hostname != null) {
            serverLine.append(serverDevice.hostname); // host name
        }
        serverLine.append("\",\"");
        if (serverDevice.ipAddress != null) {
            serverLine.append(serverDevice.ipAddress); // ip address
        }
        serverLine.append("\",\"");
        if (serverDevice.hypervisorIPAddress != null) {
            serverLine.append(serverDevice.hypervisorIPAddress); // hypervisor ip address
        }
        serverLine.append("\",\"");
        if (serverDevice.serviceTag != null) {
            serverLine.append(serverDevice.serviceTag); // asset / service tag
        }
        serverLine.append("\",");

        return serverLine.toString();
    }

    // Gets the report portion for Storage
    private String getStorageReport(List<UIDevice> storageDevices,
                                    List<UITemplateBuilderComponent> components, String headers,
                                    String lineStart) {
        StringBuilder serverReport = new StringBuilder();
        if (storageDevices.size() > 0) {
            // Write Headers
            serverReport.append(headers);
            serverReport.append("\r\n");
            for (UIDevice storageDevice : storageDevices) {
                serverReport.append(lineStart);
                serverReport.append(this.getStorageReportLine(storageDevice, components));
                serverReport.append("\r\n");
            }
        }
        return serverReport.toString();
    }

    // Returns the Storage line entry for the
    private String getStorageReportLine(UIDevice storageDevice,
                                        List<UITemplateBuilderComponent> components) {
        StringBuilder storageLine = new StringBuilder();

        // COLUMNS: "\"Component Name\",\"IP Address\",\"Asset/Service Tag\",";
        storageLine.append("\"");
        // if(storageDevice.storagecentername != null) storageLine.append(storageDevice.storagecentername); // component name
        if (storageDevice.id != null)
            storageLine.append(this.getNameFromComponentById(components, storageDevice.id));
        storageLine.append("\",\"");
        if (storageDevice.ipAddress != null)
            storageLine.append(storageDevice.ipAddress); // ip address
        storageLine.append("\",\"");
        if (storageDevice.serviceTag != null)
            storageLine.append(storageDevice.serviceTag); // asset / service tag
        storageLine.append("\",");

        return storageLine.toString();
    }

    // Returns the VM line entry for the
    private String getVmReportLine(UIDevice vmDevice, List<UITemplateBuilderComponent> components) {
        StringBuilder vmLine = new StringBuilder();
        // OLD COLUMNS:  "\"VM Name\",\"CPU\",\"Memory\",\"Disk\",\"Network Count\",\"OS Type\",\"OS Image\",";
        // COLUMNS: "\"VM Name\",\"CPU\",\"Memory\",\"Disk\",\",\"OS Type\",";

        vmLine.append("\"");
        if (vmDevice.name != null) {
            vmLine.append(vmDevice.name); // vmname
        }
        //	if(vmDevice.id != null) vmLine.append(this.getNameFromComponentById(components, vmDevice.id));
        vmLine.append("\",\"");
        if (vmDevice.vm_cpus != null) {
            vmLine.append(vmDevice.vm_cpus); // cpu
        }
        vmLine.append("\",\"");
        if (vmDevice.vm_memory != null) {
            vmLine.append(vmDevice.vm_memory); // mem
        }
        vmLine.append("\",\"");
        if (vmDevice.vm_disksize != null) {
            vmLine.append(vmDevice.vm_disksize); // disk
        }
        vmLine.append("\",\"");
        if (vmDevice.vm_ostype != null) {
            vmLine.append(vmDevice.vm_ostype); // osType
        }
        vmLine.append("\",");

        return vmLine.toString();
    }

    private String getAppReportLine(UITemplateBuilderComponent component,
                                    List<UITemplateBuilderComponent> components) {
        StringBuilder appLine = new StringBuilder();

        appLine.append("\"");
        if (component.name != null) appLine.append(component.name); // app name
        appLine.append("\"");

        return appLine.toString();
    }

    // Gets the report portion for VMs
    private String getClusterReport(List<UIDevice> clusterDevices,
                                    List<UITemplateBuilderComponent> components,
                                    String headers, String lineStart) {
        StringBuilder clusterReport = new StringBuilder();
        if (clusterDevices.size() > 0) {
            clusterReport.append(headers);
            clusterReport.append("\r\n");
            for (UIDevice clusterDevice : clusterDevices) {
                clusterReport.append(lineStart);
                clusterReport.append(this.getClusterReportLine(clusterDevice, components));
                clusterReport.append("\r\n");
            }
        }
        return clusterReport.toString();
    }

    private String getReportFileName(ArrayList<UIService> services, Locale locale) {
        StringBuilder reportFileName = new StringBuilder();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'_'HH:mm:ss");
        Date currentDate = Calendar.getInstance(locale).getTime();

        if (services.size() == 1) {
            reportFileName.append(services.get(0).name);
            reportFileName.append("_");
            reportFileName.append(simpleDateFormat.format(currentDate));
            reportFileName.append(".csv");
        } else {
            reportFileName.append("Dell_EMC_VxFM_services");
            reportFileName.append("_");
            reportFileName.append(simpleDateFormat.format(currentDate));
            reportFileName.append(".csv");
        }

        return reportFileName.toString();
    }

    // Returns a single cluster line entry for the report
    private String getClusterReportLine(UIDevice clusterDevice,
                                        List<UITemplateBuilderComponent> components) {
        StringBuilder clusterLine = new StringBuilder();
        // OLD COLUMNS:  "\"vCenter Name\",\"Datacenter\",\"Cluster Name\",";
        // COLUMNS:  "\"Component Name\",\"Ip Address\",\"Asset/Service Tag\

        clusterLine.append("\"");
        //if(clusterDevice.name != null) clusterLine.append(clusterDevice.name); // Component Name
        if (clusterDevice.id != null) {
            clusterLine.append(this.getNameFromComponentById(components, clusterDevice.id));
        }
        clusterLine.append("\",\"");
        if (clusterDevice.ipAddress != null) {
            clusterLine.append(clusterDevice.ipAddress); // Ip Address
        }
        clusterLine.append("\",\"");
        if (clusterDevice.serviceTag != null) {
            clusterLine.append(clusterDevice.serviceTag); // Asset / Service Tag
        }
        clusterLine.append("\",");

        return clusterLine.toString();
    }

    /**
     * Create service (deploy template).
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "createservice", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse createService(@RequestBody JobCreateServiceRequest request) {

        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();
        List<FieldError> errorFields = new ArrayList<>();
        UIService service;
        try {
            if (StringUtils.isEmpty(request.requestObj.serviceName)) {
                throw new ControllerException(
                        getApplicationContext().getMessage("validationError.serviceNoName", null,
                                                           LocaleContextHolder.getLocale()));
            }

            Deployment deploymentService = createDeployService(request.requestObj);
            Deployment result = serviceAdapter.createDeployment(deploymentService);
            Deployment inventoryDep = serviceAdapter.getDeployment(result.getId());
            service = createServiceFromDeployment(inventoryDep, false, false);

            jobResponse.responseObj = service;

        } catch (Throwable t) {
            log.error("createService() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
            parseJobResponse(jobResponse, errorFields);


        }
        clearOldFldErrors(jobResponse, errorFields);
        return jobResponse;
    }

    private void clearOldFldErrors(JobResponse jobResponse, List<FieldError> errorFields) {
        if (jobResponse.errorObj != null &&
                CollectionUtils.isNotEmpty(jobResponse.errorObj.fldErrors) &&
                jobResponse.errorObj.fldErrors.get(0).errorCode != null &&
                jobResponse.errorObj.fldErrors.get(0).errorCode.equals("VXFM00240")) {
            jobResponse.errorObj.fldErrors.clear();
            jobResponse.errorObj.errorDetails = "";
            jobResponse.errorObj.fldErrors.addAll(errorFields);
        }

    }

    private void parseJobResponse(JobResponse jobResponse, List<FieldError> errorFields) {
        if (jobResponse.errorObj != null && CollectionUtils.isNotEmpty(
                jobResponse.errorObj.fldErrors)
                && jobResponse.errorObj.fldErrors.get(0).errorCode != null
                && jobResponse.errorObj.fldErrors.get(0).errorCode.equals("VXFM00240")) {
            MultiMap myMap = new MultiValueMap();
            String errorMsg = jobResponse.errorObj.fldErrors.get(0).errorMessage;
            if (errorMsg != null) {

                String[] elements = errorMsg.split(",");
                for (String s1 : elements) {

                    String[] keyValue = s1.split("=");
                    if (StringUtils.isNotEmpty(keyValue[0]) && StringUtils.isNotEmpty(
                            keyValue[1])) {
                        keyValue[0] = keyValue[0].replaceAll("[{}]", " ");
                        keyValue[1] = keyValue[1].replaceAll("[{}]", " ");
                        myMap.put(keyValue[1].trim(), keyValue[0].trim());
                    }
                }

                Set<String> keys = myMap.keySet();
                if (CollectionUtils.isNotEmpty(keys)) {
                    for (String key : keys) {
                        FieldError f = new FieldError();

                        if (key != null && myMap.get(key) != null) {
                            if (Character.isDigit(key.charAt(0))) {
                                jobResponse.errorObj.errorMessage = "";
                                jobResponse.errorObj.errorMessage = key;
                            } else {
                                f.errorDetails = myMap.get(key).toString();
                                key = key.replaceAll("[{}]", " ");
                                f.errorMessage = key;
                            }

                        }
                        errorFields.add(f);

                    }
                }

            }
        }

    }

    /**
     * Adjust service (update template).
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "adjustservice", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse adjustService(@RequestBody JobAdjustServiceRequest request) {
        List<FieldError> errorFields = new ArrayList<>();
        String deploymentId = request.requestObj.serviceId;
        List<UITemplateBuilderComponent> uiComponents = request.requestObj.components;

        log.debug("adjustService() - JobRequest received");

        Locale locale = LocaleContextHolder.getLocale();
        JobResponse jobResponse = new JobResponse();
        try {
            Deployment deployment = serviceAdapter.getDeployment(deploymentId);

            Map<String, ServiceTemplateComponent> componentMap = deployment.getServiceTemplate().fetchComponentsMap();

            //Update the service template
            for (UITemplateBuilderComponent uiComponent : uiComponents) {
                if (ServiceTemplateComponentType.SCALEIO.getLabel().equals(uiComponent.type)) {
                       for (UITemplateBuilderCategory uiCategory : uiComponent.categories) {
                           for (UITemplateBuilderSetting uiSetting : uiCategory.settings) {
                               if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_SPARE_CAPACITY_ID.equals(uiSetting.id)) {
                                   ServiceTemplateComponent scaleIOComponent = componentMap.get(uiComponent.id);
                                   if (scaleIOComponent != null) {
                                       ServiceTemplateSetting storagePoolSpareCapacity =
                                               scaleIOComponent.getParameter(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_COMP_SCALEIO_ID,
                                                                             ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_SPARE_CAPACITY_ID);
                                       if (storagePoolSpareCapacity != null) {
                                           storagePoolSpareCapacity.setValue(uiSetting.value);
                                       }
                                   }
                                   break;
                               }
                           }
                       }
                } else {
                    log.debug("Adding component: " + uiComponent + " to service template: " + deployment.getServiceTemplate());
                    addComponentToServiceTemplate(componentMap,
                                                  uiComponent,
                                                  request.requestObj.componentId);
                }
            }

            for (ServiceTemplateComponent component : componentMap.values()) {
                for (String associatedComponentId : component.getAssociatedComponents().keySet()) {
                    ServiceTemplateComponent associatedComponent = componentMap.get(
                            associatedComponentId);
                    if (associatedComponent != null) {
                        if (!associatedComponent.getAssociatedComponents().keySet().contains(
                                component.getId())) {
                            associatedComponent.addAssociatedComponentName(component.getId(),
                                                                           component.getName());
                        }
                    }
                }
            }
            // set the updated components in the service template
            deployment.getServiceTemplate().setComponents(new ArrayList<>(componentMap.values()));
            // Persist and retry the updated service template
            deployment.setRetry(true);
            deployment.setScaleUp(true);
            deployment = serviceAdapter.updateDeployment(deployment.getId(), deployment);
            jobResponse.responseObj = createServiceFromDeployment(deployment, false, false);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            jobResponse = addFailureResponseInfo(t);
            parseJobResponse(jobResponse, errorFields);
        }
        clearOldFldErrors(jobResponse, errorFields);
        return jobResponse;
    }

    /**
     * Cancel service that is in progress.
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "cancelservice", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse cancelService(@RequestBody JobServiceIdRequest request,
                              HttpServletResponse response) {
        JobResponse jobResponse = new JobResponse();
        try {
            Locale locale = LocaleContextHolder.getLocale();
            Deployment deployment = serviceAdapter.getDeployment(request.requestObj.id);

            if (deployment == null) {
                log.error(
                        "No deployment found to cancel for requested id: " + request.requestObj.id);
            } else {
                deployment.setStatus(DeploymentStatusType.CANCELLING);
                deployment = serviceAdapter.cancelDeployment(request.requestObj.id, deployment);

                UIService service = createServiceFromDeployment(deployment, false, false);
                jobResponse.responseObj = service;
            }
            return jobResponse;
        } catch (Throwable t) {
            log.error("cancelService() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     *  Add a network to a service
     *  @param request
     *  @return
     *  @throws ServletException
     *  @throws IOException
     */
    @RequestMapping(value = "updateservicenetworkresources", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse addNetworkToService(@RequestBody JobUpdateServiceNetworkResourceRequest request) {

        List<UIServiceNetworkResource> newNetworks = request.requestObj.networks;
        log.debug(
                "addNetworkToService() - JobRequest received the following " + request.requestObj.toString());

        Locale locale = LocaleContextHolder.getLocale();
        JobResponse jobResponse = new JobResponse();
        Boolean workLoadNetworkOnHost = false;
        try {
            Deployment deployment = serviceAdapter.getDeployment(request.requestObj.serviceid);
            ServiceTemplate serviceTemplate = deployment.getServiceTemplate();
            List<UINetworkResource> serverDevices = getServerNetworks(deployment);
            Set<String> missingNetworks = missingVmNetworksOnHost(newNetworks, serverDevices);
            if (missingNetworks.size() != 0 && resourceHasNetworks(newNetworks, "0")) {
                String joined = StringUtils.join(",", missingNetworks);
                if (joined.startsWith(",")) {
                    joined = joined.substring(1);
                }
                throw new AsmRuntimeException(ASMCommonsMessages.networkNotAddedToHost(joined));
            }

            for (UIServiceNetworkResource resourceNetwork : newNetworks) {
                for (String resourceId : resourceNetwork.resources) {
                    // Device with id: 0 is all of the Server hosts
                    if (StringUtils.equals(resourceId, "0")) {

                        List<ServiceTemplateComponent> serverTemplateComponents = getServerTemplateComponents(
                                serviceTemplate);

                        for (ServiceTemplateComponent component : serverTemplateComponents) {
                            ServiceTemplateCategory resource = ServiceTemplate.getTemplateResource(
                                    component,
                                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_NETWORKING_COMP_ID);
                            ServiceTemplateSetting networkSettings = resource.getParameter(
                                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_NETWORK_CONFIG_ID);
                            NetworkConfiguration networkConfig = networkSettings.getNetworkConfiguration();
                            List<Interface> interfaces = networkConfig.getUsedInterfaces();

                            // need a list of all ISCSI VLANs before we start processing, doing 2 passes
                            List<String> iscsiNetworkIDs = new ArrayList<>();
                            List<Network> iscsiNetworks = networkConfig.getNetworks(
                                    NetworkType.STORAGE_ISCSI_SAN);
                            for (Network networkObject : iscsiNetworks) {
                                if (!iscsiNetworkIDs.contains(networkObject.getId())) {
                                    iscsiNetworkIDs.add(networkObject.getId());
                                }
                            }
                            Collections.sort(iscsiNetworkIDs);

                            // second pass to process workload networks
                            for (Interface iface : interfaces) {
                                List<Partition> partitions = iface.getPartitions();
                                if (!iface.isPartitioned()) {
                                    // Discard all except first partition
                                    partitions = new ArrayList<>(Arrays.asList(partitions.get(0)));
                                }

                                for (Partition partition : partitions) {
                                    String networkId = resourceNetwork.networkid;
                                    if (partition.getNetworks() != null
                                            && !partition.getNetworks().contains(networkId)) {
                                        List<String> networks = partition.getNetworks();
                                        List<Network> networkObjects = partition.getNetworkObjects();
                                        com.dell.pg.asm.identitypool.api.network.model.Network network = networkServiceAdapter.getNetwork(
                                                networkId);
                                        Network newNetwork = new Network(network);
                                        boolean isWorkloadNetworkPartition = false;
                                        if (networkObjects != null) {
                                            // we are looking for any partition with at least one workload network
                                            for (Network networkObject : networkObjects) {
                                                if (NetworkType.PRIVATE_LAN.equals(networkObject.getType()) ||
                                                        NetworkType.PUBLIC_LAN.equals(networkObject.getType()) ||
                                                        NetworkType.HYPERVISOR_MANAGEMENT.equals(networkObject.getType())) {
                                                    isWorkloadNetworkPartition = true;
                                                    break;
                                                }
                                            }
                                            if (isWorkloadNetworkPartition) {
                                                // make a copy - original needed untouched
                                                List<String> networksCopy = new ArrayList<>(
                                                        networks);
                                                // if we have multiple iscsi networks like "id1,id2" each such ID in "networks" must be replaced with
                                                // their combination: id1-id2, this is how we stored VDS name setting ID
                                                // for example
                                                // ff80808155bb0ad00155bcf717ed31ab:ff80808155bb0ad00155bcf89d8631bc-ff8080815631f48a01564690c8013975:
                                                // ff80808155bb0ad00155bcf93e0531cd:ff80808155bb0ad00155bcfa7c8d31ce:ff80808155bb0ad00155bcfb7a4131df
                                                replaceISCSIIDs(networksCopy, iscsiNetworkIDs);

                                                // add portgroup - use existing networks combination
                                                addPortGroup(serviceTemplate, component,
                                                             resourceNetwork.portgroupid,
                                                             resourceNetwork.portgroupname,
                                                             networksCopy, networkId,
                                                             newNetwork.getName());

                                                networkObjects.add(newNetwork);
                                                networks.add(networkId);
                                                partition.setNetworkObjects(networkObjects);
                                                partition.setNetworks(networks);
                                                workLoadNetworkOnHost = true;
                                            }
                                        }
                                    }

                                    // ignore all but first partitions
                                    if (!iface.isPartitioned()) break;
                                    // Different nic types have different number of partitions but data may include more that should be ignored
                                    if (partition.getName().equals(
                                            Integer.toString(iface.getMaxPartitions()))) {
                                        break;
                                    }
                                }
                            }
                            if (!workLoadNetworkOnHost) {
                                throw new AsmRuntimeException(
                                        ASMCommonsMessages.noWorkLoadNetwrokOnHost());
                            }
                            String componentNetworkString = objectMapper.writeValueAsString(
                                    networkConfig);
                            networkSettings.setValue(componentNetworkString);
                        }

                    } else {
                        ServiceTemplateComponent component = serviceTemplate.findComponentById(resourceId);
                        ServiceTemplateCategory resource = ServiceTemplate.getTemplateResource(
                                component, ServiceTemplateSettingIDs.SERVICE_TEMPLATE_VM_RESOURCE);
                        if (resource == null) {
                            resource = ServiceTemplate.getTemplateResource(component,
                                                                           ServiceTemplateSettingIDs.SERVICE_TEMPLATE_HV_VM_RESOURCE);
                        }

                        Boolean addNetwork = false;
                        ServiceTemplateSetting networkSetting = resource.getParameter(
                                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_VM_NETWORK_ID);
                        List<Network> currentNetworkList = networkSetting.getNetworks();
                        String existingNetworkString = networkSetting.getValue();
                        String networkId = resourceNetwork.networkid;
                        List<Network> networkList = new ArrayList<>();
                        com.dell.pg.asm.identitypool.api.network.model.Network network = networkServiceAdapter.getNetwork(
                                networkId);
                        if (currentNetworkList != null) {
                            networkList.addAll(currentNetworkList);
                            if (!existingNetworkString.contains(networkId)) {
                                addNetwork = true;
                            }
                        } else {
                            addNetwork = true;
                        }
                        if (addNetwork) {
                            String newNetworkString;
                            if (StringUtils.equals(networkSetting.getValue(), "")) {
                                newNetworkString = networkId;
                            } else {
                                newNetworkString = networkSetting.getValue() + "," + networkId;
                            }
                            Network newNetwork = new Network(network);
                            String networkName = network.getName();
                            log.debug(
                                    "Adding network: " + networkName + " to service template: " + deployment.getServiceTemplate());
                            networkList.add(newNetwork);
                            networkSetting.setValue(newNetworkString);
                            networkSetting.setNetworks(networkList);
                        }
                    }
                }
            }

            deployment.setRetry(true);
            deployment = serviceAdapter.updateDeployment(deployment.getId(), deployment);
            jobResponse.responseObj = createServiceFromDeployment(deployment, false, false);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private void addPortGroup(ServiceTemplate serviceTemplate, ServiceTemplateComponent component,
                              String portgroupid, String portgroupname, List<String> networks,
                              String networkId, String networkName) {

        // either one can be empty but can't be null
        if (portgroupid == null || portgroupname == null
                || (portgroupid.length() == 0 && portgroupname.length() == 0))
            return;

        for (String relCompId : component.getAssociatedComponents().keySet()) {
            ServiceTemplateComponent cluster = serviceTemplate.findComponentById(relCompId);
            if (cluster != null && cluster.getType() == ServiceTemplateComponentType.CLUSTER) {
                // check if it is vCenter cluster
                if (cluster.getTemplateResource(
                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMP_VDS_ID) == null)
                    continue;

                boolean isNewGroup = portgroupid.equals("-1");
                String groupName = isNewGroup ? portgroupname : portgroupid;

                // maximum 1 port group per workload network
                boolean pgAdded = ServiceTemplateClientUtil.scaleupNetworkPortGroups(cluster,
                                                                                     groupName,
                                                                                     isNewGroup,
                                                                                     networks,
                                                                                     networkId,
                                                                                     networkName,
                                                                                     1);

                if (!pgAdded) {
                    // this is internal error
                    throw new AsmRuntimeException(ASMCommonsMessages.internalServerError());
                }
                break;
            }
        }
    }

    private void replaceISCSIIDs(List<String> networks, List<String> iscsiNetworkIDs) {
        if (CollectionUtils.isEmpty(networks) ||
                CollectionUtils.isEmpty(iscsiNetworkIDs) ||
                iscsiNetworkIDs.size() != 2) {
            return;
        }
        String iscsiID = StringUtils.join(iscsiNetworkIDs, "-");
        Collections.replaceAll(networks, iscsiNetworkIDs.get(0), iscsiID);
        Collections.replaceAll(networks, iscsiNetworkIDs.get(1), iscsiID);
    }

    private Set<String> missingVmNetworksOnHost(List<UIServiceNetworkResource> networks,
                                                List<UINetworkResource> serverDevices) {
        Set<String> hostNetworks = new HashSet<>();
        Set<String> allVmNetworks = new HashSet<>();
        Set<String> newNetworkNames = new HashSet<>();

        for (UIServiceNetworkResource network : networks) {
            for (String resourceId : network.resources) {
                if (StringUtils.equals(resourceId, "0")) {
                    hostNetworks.add(network.networkid);
                } else {
                    allVmNetworks.add(network.networkid);
                }
            }
        }

        // add existing All Hosts networks
        for (UINetworkResource resorce : serverDevices) {
            hostNetworks.addAll(resorce.networks);
        }

        for (String network : allVmNetworks) {
            if (!hostNetworks.contains(network)) {
                com.dell.pg.asm.identitypool.api.network.model.Network networkInfo = networkServiceAdapter.getNetwork(
                        network);
                newNetworkNames.add(networkInfo.getName());
            }
        }
        return newNetworkNames;
    }

    private boolean resourceHasNetworks(List<UIServiceNetworkResource> networks, String id) {
        for (UIServiceNetworkResource network : networks) {
            for (String resourceid : network.resources) {
                if (id.equals(resourceid)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper to get all the componentID's of ESXI hosts
     * @param serviceTemplate
     */
    private List<ServiceTemplateComponent> getServerTemplateComponents(
            ServiceTemplate serviceTemplate) {
        List<ServiceTemplateComponent> serverTemplateComponents = new ArrayList<>();
        for (ServiceTemplateComponent component : serviceTemplate.getComponents()) {
            if (component.getType().equals(ServiceTemplateComponentType.SERVER)) {
                serverTemplateComponents.add(component);
            }
        }
        return serverTemplateComponents;
    }

    /**
     * Helper method to add a newly specified component to a deployment's ServiceTemplate
     * @param componentMap:  Map of components on the service template
     * @param uiComponent:  The ui specification of the component to add.
     * @throws ControllerException
     */
    private void addComponentToServiceTemplate(final Map<String, ServiceTemplateComponent> componentMap,
                                               final UITemplateBuilderComponent uiComponent,
                                               final String clonedFromId) throws ControllerException {
        if (componentMap == null || uiComponent == null)
            return;

        ServiceTemplateComponent component = componentMap.get(uiComponent.id);
        if (component == null) {

            component = TemplateController.createTemplateComponent(uiComponent);
            component.setId(UUID.randomUUID().toString());
            component.setComponentID(uiComponent.id);
            component.setPuppetCertName(uiComponent.puppetCertName);
            component.setAsmGUID(uiComponent.AsmGUID);
            component.setClonedFromId(clonedFromId);
            if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_FLEXOS_STORAGE_COMPONENT_ID.equals(uiComponent.id)) {
                ServiceTemplateSetting volumeNameSetting = ServiceTemplateClientUtil.getVolumeNameSetting(component);
                String volumeName = null;
                if (volumeNameSetting != null) {
                    volumeName = volumeNameSetting.getValue();
                }
                if (StringUtils.isNotBlank(volumeName)) {
                    component.setName(volumeName);
                }
            }


            componentMap.put(component.getId(), component);
        } else {
            TemplateController.parseRelatedComponents(component, uiComponent);
        }
    }

    private void getDeploymentInventory(DeploymentDevice device,
                                        ApplicationContext applicationContext,
                                        UIService service,
                                        Deployment deployment,
                                        ResourceList<DeviceGroup> spList) {

        UIDevice dev = parseManagedDeviceToDevice(device, applicationContext, deployment);
        // UIDevice.id is refId at this point. We need componentId to build UI graph correctly
        List<ServiceTemplateComponent> components = deployment.getServiceTemplate().getComponents();
        for (ServiceTemplateComponent component : components) {
            if ((component != null) && StringUtils.equals(dev.id, component.getAsmGUID())) {
                // verify it is the same device type. Flex OS Cluster will have the same Guid as Storage
                if (device.getRefType().equals(component.getType().name())) {
                    // check if already used, only within the storage case
                    boolean found = false;
                    for (UIDevice uidev : service.storagelist) {
                        if (uidev.id.equals(component.getId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        dev.id = component.getId();
                        dev.asmGUID = component.getAsmGUID();
                        break;
                    }
                }
            }
        }

        dev.candelete = deployment.isCanDeleteResources();

        if (device.getRefType().equals(ServiceTemplateComponentType.STORAGE.name())) {
            service.storagelist.add(dev);
            if (BrownfieldStatus.NOT_APPLICABLE.equals(device.getBrownfieldStatus()) ||
                    BrownfieldStatus.AVAILABLE.equals(device.getBrownfieldStatus()) ||
                    BrownfieldStatus.NEWLY_AVAILABLE.equals(device.getBrownfieldStatus()) ||
                    BrownfieldStatus.UNAVAILABLE_IN_EXISTING_SERVICE.equals(
                            device.getBrownfieldStatus()) ||
                    BrownfieldStatus.UNAVAILABLE_RELATED_SERVER_NOT_IN_INVENTORY.equals(
                            device.getBrownfieldStatus()) ||
                    BrownfieldStatus.UNAVAILABLE_RELATED_SERVER_IN_EXISTING_SERVICE.equals(
                            device.getBrownfieldStatus()) ||
                    BrownfieldStatus.CURRENTLY_DEPLOYED_IN_BROWNFIELD.equals(
                            device.getBrownfieldStatus())) {
                addVolumeInfo(device, dev, service);
            }
        } else if (device.getRefType().equals(ServiceTemplateComponentType.SERVER.name())) {
            for (ServiceTemplateComponent component : components) {
                if (dev.id.equals(component.getId())) {
                    dev.ipaddresslist = getOSIps(component);
                    addServerComponentSettings(dev, component, applicationContext);
                    break;
                }
            }
            addServerPool(device, dev, spList);
            service.serverlist.add(dev);
        } else if (device.getRefType().equals(ServiceTemplateComponentType.CLUSTER.name())) {
            for (ServiceTemplateComponent component : components) {
                if (dev.id.equals(component.getId())) {
                    addClusterDetails(dev, component);
                    break;
                }
            }
            service.clusterlist.add(dev);
        } else if (device.getRefType().equals(ServiceTemplateComponentType.TOR.name())) {
            service.networklist.add(dev);
        } else if (device.getRefType().equals(ServiceTemplateComponentType.SCALEIO.name())) {
            for (ServiceTemplateComponent component : components) {
                if (dev.id.equals(component.getId())) {
                    addScaleIODetails(dev, component, device);
                    break;
                }
            }
            service.scaleiolist.add(dev);
        }
    }

    private void addClusterDetails(UIDevice dev, ServiceTemplateComponent component) {
        // vCenter
        ServiceTemplateCategory resource = component.getTemplateResource(
                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMP_ID);
        String datacenterID = null;
        String clusterID = null;
        if (resource != null) {
            datacenterID = ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CLUSTER_DATACENTER_ID;
            clusterID = ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CLUSTER_CLUSTER_ID;
        } else {
            // hyperV
            resource = component.getTemplateResource(
                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCVMM_CLUSTER_COMP_ID);
            if (resource != null) {
                datacenterID = ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCVMM_CLUSTER_HOSTGROUP_ID;
                clusterID = ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCVMM_CLUSTER_CLUSTER_ID;
            }
        }


        if (datacenterID != null) {
            ServiceTemplateSetting datacenter = resource.getParameter(datacenterID);
            if (datacenter != null && ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX.equals(
                    datacenter.getValue())) {
                datacenter = resource.getParameter(
                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX + datacenterID);
            }
            if (datacenter != null)
                dev.datacentername = datacenter.getValue();
        }
        if (clusterID != null) {
            ServiceTemplateSetting clusterName = resource.getParameter(clusterID);
            if (clusterName != null && ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX.equals(
                    clusterName.getValue())) {
                clusterName = resource.getParameter(
                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX + clusterID);
            }
            if (clusterName != null)
                dev.clustername = clusterName.getValue();
        }
    }

    private void addScaleIODetails(final UIDevice uiDevice,
                                   final ServiceTemplateComponent component,
                                   final DeploymentDevice device) {
        if (component != null) {
            ServiceTemplateSetting protectionDomainSettings = component.getParameter(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_COMP_SCALEIO_ID,
                                                                                     ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_PROTECTION_DOMAIN_SETTINGS_ID);
            if (protectionDomainSettings != null) {
                List<ProtectionDomain> protectionDomains = null;
                try {
                    protectionDomains = objectMapper.readValue(protectionDomainSettings.getValue(),
                                                               new TypeReference<List<ProtectionDomain>>() {});
                } catch (Exception e) {
                    log.warn("Unexpected exception occurred parsing protection domain on Device " + component.getPuppetCertName());
                }
                if (CollectionUtils.isNotEmpty(protectionDomains)) {
                    ProtectionDomain protectionDomain = protectionDomains.get(0);
                    uiDevice.protectionDomain = protectionDomain.getGeneral().getName();
                    if (CollectionUtils.isNotEmpty(protectionDomain.getStoragePools())) {
                        for (StoragePool pool : protectionDomain.getStoragePools()) {
                            UIScaleIOStoragePool storagePool = new UIScaleIOStoragePool();
                            storagePool.id = pool.getName();
                            storagePool.name = pool.getName();
                            if (CollectionUtils.isNotEmpty(pool.getVolumes())) {
                                for (Volume v : pool.getVolumes()) {
                                    UIScaleIOStorageVolume volume = new UIScaleIOStorageVolume();
                                    volume.id = v.getName();
                                    volume.name = v.getName();
                                    storagePool.scaleIOStorageVolumes.add(volume);
                                }
                            }
                            uiDevice.storagePools.add(storagePool);
                        }
                    }
                }
            }

            if (device != null) {
                try {
                    PuppetScaleIOSystem system = objectMapper.readValue(device.getFacts(),
                                                                        PuppetScaleIOSystem.class);
                    if (system != null) {
                        if (system.getGeneral() != null) {
                            uiDevice.hostname = system.getGeneral().getName();
                            if (system.getGeneral().getMdmCluster() != null) {
                                Cluster mdmCluster = system.getGeneral().getMdmCluster();
                                if (mdmCluster.getMaster() != null && CollectionUtils.isNotEmpty(mdmCluster.getMaster().getManagementIPs())) {
                                    uiDevice.primarymdmipaddress = mdmCluster.getMaster().getManagementIPs().get(0);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Unexpected exception occurred parsing ScaleIO System on Device " + component.getPuppetCertName());
                }
            }
        }
    }

    private void addServerPool(DeploymentDevice ddevice,
                               UIDevice dev,
                               ResourceList<DeviceGroup> mList) {

        if (mList != null && mList.getTotalRecords() > 0) {
            for (DeviceGroup dg : mList.getList()) {
                if (dg.getManagedDeviceList() != null && dg.getManagedDeviceList().getManagedDevices() != null) {
                    for (ManagedDevice device : dg.getManagedDeviceList().getManagedDevices()) {
                        if (device.getRefId().equals(ddevice.getRefId())) {
                            dev.serverpool = dg.getGroupName();
                            dev.newserverpool = dev.serverpool;
                            break;
                        }
                    }
                    if (dev.serverpool != null)
                        break;
                }
            }
        }

        if (dev.serverpool == null) {
            dev.serverpool = ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_POOL_GLOBAL_NAME;
            dev.newserverpool = dev.serverpool;
        }
    }

    /**
     * For storage components find volumes data in template and attach to device.
     * @param dev
     * @param service
     */
    private void addVolumeInfo(DeploymentDevice device,
                               UIDevice dev,
                               UIService service) {
        if (device == null || device.getFacts() == null) {
            return;
        }

        try {
            // find corresponding ScaleIo gateway
            PuppetScaleIOSystem system = objectMapper.readValue(device.getFacts(),
                    PuppetScaleIOSystem.class);

            List<ServiceTemplateOption> volumes = ServiceTemplateClientUtil.getFlexosVolumes(system);
            List<UIVolume> volumelist = volumes.stream()
                    .map(v -> {
                        String volumeType = "ThickProvisioned".equalsIgnoreCase(v.getAttributes().get("type")) ? "Thick" : "Thin";
                        return new UIVolume(v.getAttributes().get("id"),
                                            v.getName(),
                                            Double.parseDouble(v.getAttributes().get("size")),
                                            volumeType);
                    })
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(volumelist)) {
                service.components.stream().filter(component -> component.id.equals(dev.id)).forEach(component -> {
                    UITemplateBuilderCategory resource =
                            component.findCategory(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_FLEXOS_VOLUME_RESOURCE_ID);
                    if (resource != null) {
                        String volumeName = "";
                        UITemplateBuilderSetting nameSetting =
                                resource.getUITemplateBuilderSetting(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_FLEXOS_VOLUME_NAME_ID);
                        if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX.equals(nameSetting.value)){
                            nameSetting =
                                    resource.getUITemplateBuilderSetting(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX +
                                            ServiceTemplateSettingIDs.SERVICE_TEMPLATE_FLEXOS_VOLUME_NAME_ID);
                        }
                        volumeName = nameSetting.value;

                        for (UIVolume vol : volumelist) {
                            if (vol.name.equals(volumeName)) {
                                dev.volumelist.add(vol);
                            }
                        }

                        UITemplateBuilderSetting volumeSize = resource.getUITemplateBuilderSetting(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_FLEXOS_VOLUME_SIZE_ID);
                        UITemplateBuilderSetting volumeType = resource.getUITemplateBuilderSetting(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_FLEXOS_VOLUME_TYPE_ID);
                        String type = ServiceTemplateSettingIDs.SERVICE_TEMPLATE_FLEXOS_VOLUME_TYPE_THICK.equals(volumeType.value) ? "Thick" : "Thin";

                        // volume does not yet exist - add a placeholder. Size in template is in GB, UIVolume needs Kb
                        if (dev.volumelist.size() == 0) {
                            dev.volumelist.add(new UIVolume(volumeName,
                                                            volumeName,
                                                            Double.parseDouble(volumeSize.value) * 1048576.0,
                                                            type));
                        }
                    }
                });
            }

        } catch (IOException e) {
            log.error("Cannot process volumes for device=" + dev.asmGUID + ", service=" + service.id, e);
        }
    }

    private Deployment createDeployService(UIDeploy requestObj) throws ControllerException {
        if (requestObj == null)
            return null;
        Deployment deployment = new Deployment();
        deployment.setUpdateServerFirmware(requestObj.manageFirmware);

        try {
            deployment.setNumberOfDeployments(Integer.parseInt(requestObj.numberOfDeployments));
        } catch (NumberFormatException nfe) {
            deployment.setNumberOfDeployments(1);
        }

        if (requestObj.id != null) {
            deployment.setId(requestObj.id);
        }
        deployment.setDeploymentName(requestObj.serviceName);
        deployment.setDeploymentDescription(requestObj.serviceDescription);

        if (requestObj.template != null) {
            deployment.setServiceTemplate(
                    TemplateController.createTemplate(requestObj.template,
                                                      firmwareRepositoryServiceAdapter));
        }

        if (requestObj.manageFirmware && requestObj.firmwarePackageId != null) {
            if (TemplateController.USE_DEFAULT_CATALOG_ID.equals(requestObj.firmwarePackageId)) {
                deployment.setUseDefaultCatalog(true);
                deployment.setFirmwareRepositoryId(null);
                deployment.setFirmwareRepository(null);
            } else {
                setDeploymentFirmwareRepository(requestObj.firmwarePackageId, deployment);
            }
            if (deployment.getServiceTemplate() != null) {
                ServiceTemplate serviceTemplate = deployment.getServiceTemplate();
                if (!serviceTemplate.isManageFirmware()) {
                    serviceTemplate.setManageFirmware(true);
                    if (deployment.isUseDefaultCatalog()) {
                        serviceTemplate.setUseDefaultCatalog(true);
                    } else {
                        TemplateController.updateFirmwareRepositoryOnTemplate(firmwareRepositoryServiceAdapter,
                                                                              requestObj.firmwarePackageId,
                                                                              serviceTemplate);
                    }
                }
            }
        }

        deployment.setAllUsersAllowed(requestObj.allStandardUsers);
        if (requestObj.assignedUsers != null) {
            deployment.setAssignedUsers(new HashSet<>());

            for (UIUser u : requestObj.assignedUsers) {
                deployment.getAssignedUsers().add(
                        UsersController.getApplicationObject(u));
            }
        }

        if ("schedule".equals(requestObj.scheduleType)) {
            deployment.setScheduleDate(requestObj.scheduleDate);
        }

        return deployment;
    }

    private Deployment createDeployService(UIServiceSummary service) throws ControllerException {
        if (service == null)
            return null;
        Deployment deployment = new Deployment();
        deployment.setNumberOfDeployments(1);

        if (service.id != null) {
            deployment.setId(service.id);
        }
        deployment.setDeploymentName(service.name);
        deployment.setDeploymentDescription(service.description);
        if (CollectionUtils.isNotEmpty(service.components)) {
            ServiceTemplate serviceTemplate = new ServiceTemplate();
            for (UITemplateBuilderComponent component : service.components) {
                serviceTemplate.getComponents().add(TemplateController.createTemplateComponent(component));
            }
            deployment.setServiceTemplate(serviceTemplate);
        }
        return deployment;
    }


    /**
     * Get service list.
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */

    @RequestMapping(value = "getservicelist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServices(@RequestBody JobRequest request) {

        JobResponse jobResponse = new JobResponse();
        List<UIServiceSummary> responseObj;

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj, null, null);
        if (StringUtils.isBlank(options.sortedColumns)) {
            options.sortedColumns = "-updatedDate";
        }
        log.debug("About to call serviceAdapter.getDeployments. options = " + Dump.toString(options));
        try {
            ResourceList<Deployment> mList = serviceAdapter.getDeploymentSummaries(options.sortedColumns,
                                                                                   null,
                                                                                   null,
                                                                                   MappingUtils.MAX_RECORDS,
                                                                                   Boolean.FALSE);

            List<UIServiceSummary> filteredList = getFilteredList(mList, options);

            responseObj = getPaginatedList(filteredList, options.offset, options.limit);

            jobResponse.criteriaObj = request.criteriaObj;

            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = filteredList.size();
            }
            jobResponse.responseObj = responseObj;

        } catch (Throwable t) {
            log.error("getServices() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private List<UIServiceSummary> getPaginatedList(List<UIServiceSummary> filteredList, int offset, int limit) {
        return filteredList.subList(Math.min(offset, filteredList.size()),
                                    Math.min(offset + limit, filteredList.size()));
    }

    /**
     * It returns list of incomplete (fast) UIService - no deployed devices.
     * @param unfilteredList
     * @param options
     * @return
     * @throws ControllerException
     */
    private List<UIServiceSummary> getFilteredList(ResourceList<Deployment> unfilteredList,
                                                   RESTRequestOptions options) {
        List<UIServiceSummary> filteredList = new ArrayList<>();
        if (unfilteredList != null && unfilteredList.getList() != null && unfilteredList.getList().length > 0) {

            String healthFilter = getFilter(options.filterList, "health");
            log.debug(String.format("back from serviceAdapter.getDeployments. %d results returned.",
                                    unfilteredList.getList().length));
            for (Deployment deployment : unfilteredList.getList()) {
                UIServiceSummary service = new UIServiceSummary();
                parseDeploymentSummary(service, deployment);
                if (healthFilter.equals(MATCH_ANY) || StringUtils.equals(service.health.name(),
                                                                         healthFilter)) {
                    filteredList.add(service);
                }
            }
        }
        return filteredList;
    }

    private ArrayList<UIService> getUIServiceForDeployments(ArrayList<Deployment> deployments,
                                                            Locale locale) throws ControllerException {
        ArrayList<UIService> uiServices = new ArrayList<>();

        for (Deployment deployment : deployments) {
            UIService service = this.createServiceFromDeployment(deployment, false, false);
            uiServices.add(service);
        }

        return uiServices;
    }

    private String getFilter(List<String> filterList, String string) {
        String result = MATCH_ANY;
        if (filterList != null) {
            for (String filter : filterList) {
                log.info(String.format("filter = %s", filter));
                String[] arr = filter.split(",");
                if (arr.length == 3 && arr[0].equals("eq") && arr[1].equals(string)) {
                    result = arr[2];
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Gets the service details.
     *
     * @param request
     *            the request
     * @return the service details
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getservicebyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServiceById(@RequestBody JobServiceIdRequest request,
                               HttpServletResponse response) throws IOException {

        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();
        try {

            Deployment deployment = serviceAdapter.getDeployment(request.requestObj.id);
            if (deployment == null) {
                log.error("No deployment job found: " + request.requestObj.id);
            } else {
                //set the id to 1000 because when adjusting/scaling up a service need to reset passwords for new components
                UIService service = createServiceFromDeployment(deployment, false, false);

                // must do this as the createServiceFromDeployment changes the service to much when scaleup flag is true.
                if (request.requestObj.scaleup) {
                    setFirmwareRepositoryOnService(deployment, service, true);
                }

                // drop service details for UI performance
                for (UITemplateBuilderComponent component : service.components) {
                    component.categories = new ArrayList<>();
                    component.network = null;
                }

                jobResponse.responseObj = service;
            }
        } catch (Throwable t) {
            log.error("getServiceById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
            if (jobResponse.responseCode == 404) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setContentLength(0);
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.getOutputStream().close();
                return null;
            }
        }

        return jobResponse;
    }

    /**
     * Gets the service details.
     *
     * @param request
     *            the request
     * @return the service details
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getservicesettingsbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServiceSettingsById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();

        Deployment deployment = serviceAdapter.getDeployment(request.requestObj.id);
        if (deployment == null) {
            log.error("No deployment job found: " + request.requestObj.id);
        } else {
            try {
                //set the id to 1000 because when adjusting/scaling up a service need to reset passwords for new components
                UIService service = createServiceFromDeployment(deployment, false, false);
                jobResponse.responseObj = service;

            } catch (Throwable t) {
                log.error("getServiceById() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }
        }

        return jobResponse;
    }

    /**
     * Get all the updating service settings for the given template id
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "getupdatableservicesettingsbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getUpdatableServiceSettingsById(@RequestBody JobIDRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        ApplicationContext context = getApplicationContext();
        JobResponse jobResponse = new JobResponse();
        final Deployment deployment = serviceAdapter.getDeployment(request.requestObj.id);
        if (deployment == null) {
            log.error("No deployment job found: " + request.requestObj.id);
        } else {
            try {
                if (deployment.isBrownfield() &&
                        StringUtils.isBlank(deployment.getServiceTemplate().getTemplateVersion())) {
                    throw new ControllerException(context.getMessage("validationError.obsoleteBrownfield",
                                                                     null,
                                                                     locale),
                                                  context.getMessage("validationError.obsoleteBrownfield.details",
                                                                     null,
                                                                     locale));
                }
                // need to refresh the settings in original template
                ServiceTemplate origTemplate = templateServiceAdapter.refreshTemplate(deployment.getServiceTemplate(),
                                                                                      true,
                                                                                      deployment.isBrownfield());
                if (origTemplate != null) {
                    ServiceTemplateClientUtil.filterServiceTemplateForOnlyUpgradableSettings(
                            deployment.getServiceTemplate());

                    // ASM-3395 Upgrade service fails with internal error. The createServiceFromDeployment was throwing
                    // a NPE because the filter method above removes any parameters not needed for upgrade. Some of these
                    // parameters are used in the createServiceFromDeployment method and since they are not present an
                    // NPE occurs. Calling parseDeployment here is a better approach as it will return the UI object with
                    // the data needed for the front end without adding the extra data or processing that
                    // createServiceFromDeployment requires/performs.
                    // UIService service  = createServiceFromDeployment(deployment,false);
                    jobResponse.responseObj = parseDeployment(deployment,
                                                              context,
                                                              Boolean.FALSE,
                                                              false);
                }
            } catch (Throwable t) {
                log.error("getServiceById() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }
        }

        return jobResponse;
    }

    private void restoreCreateNewOptions(ServiceTemplate serviceTemplate,
                                         ServiceTemplate origTemplate) {
        if (serviceTemplate == null || origTemplate == null)
            return;

        ServiceTemplateOption newOptionTemplate = new ServiceTemplateOption("",
                                                                            ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX,
                                                                            null, null);
        for (ServiceTemplateComponent component : serviceTemplate.getComponents()) {
            for (ServiceTemplateCategory category : component.getResources()) {
                for (ServiceTemplateSetting setting : category.getParameters()) {
                    ServiceTemplateSetting origSetting = serviceTemplate.getTemplateSetting(component.getId(),
                                                                                            category.getId(),
                                                                                            setting.getId());
                    if (origSetting != null) {
                        int idx = origSetting.getOptions().indexOf(newOptionTemplate);
                        ServiceTemplateOption newOption = null;
                        if (idx >= 0) {
                            newOption = origSetting.getOptions().get(idx);
                        }
                        if (newOption != null && setting.getOptions().size() > 0) {
                            setting.getOptions().add(1,
                                                     new ServiceTemplateOption(newOption.getName(),
                                                                               ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX,
                                                                               null, null));
                        }
                    }
                }
            }
        }

    }

    @RequestMapping(value = "updatecomponents", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateComponents(@RequestBody JobUpdateServiceRequest request) {
        JobResponse jobResponse = new JobResponse();
        final Deployment deployment = serviceAdapter.getDeployment(request.requestObj.id);
        List<ServiceTemplateComponent> upgradedComponents = null;
        try {
            upgradedComponents = TemplateController.createTemplateComponentList(request.requestObj.components);
        } catch (ControllerException e) {
            e.printStackTrace();
        }
        if (deployment == null) {
            log.error("No deployment job found: " + request.requestObj.id);
        } else if (CollectionUtils.isEmpty(upgradedComponents)) {
            log.warn("No upgraded components passed in request");
        } else {
            try {
                ServiceTemplateClientUtil.updateServiceTemplateWithUpgradedSettings(
                        deployment.getServiceTemplate(),
                        upgradedComponents);
                deployment.setTeardown(Boolean.FALSE);
                deployment.setRetry(request.requestObj.forceRetry);
                serviceAdapter.updateDeployment(deployment.getId(), deployment);
            } catch (Throwable t) {
                log.error("updateService() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }
        }
        return jobResponse;
    }

    /**
     * Gets the service networks list.
     *
     * @param request
     *          the request
     * @return the service details
     * @throws ServletException
     *          the servlet exception
     * @throws IOException
     *          Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getresourceswithnetworksbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getResourcesWithNetworksById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();
        List<UINetworkResource> serverDevices;
        List<UINetworkResource> vmDevices;

        Deployment deployment = serviceAdapter.getDeployment(request.requestObj.id);
        if (deployment == null) {
            log.error("No deployment job found: " + request.requestObj.id);
        } else {
            try {
                serverDevices = getServerNetworks(deployment);
                List<UINetworkResource> allResources = new ArrayList<>(serverDevices);
                /*
                // hiding the SVMs from the page per LUD-912
                vmDevices = getVMNetworks(deployment);
                allResources.addAll(vmDevices);
                */
                jobResponse.responseObj = allResources;
            } catch (Throwable t) {
                log.error("getResourcesWithNetworksById() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }
        }
        return jobResponse;
    }

    /**
     * Removes Application components and their references in the component they are applied to.
     * @param request
     * @return
     */
    @RequestMapping(value = "stopmanagingapplications", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse stopManagingApplications(@RequestBody JobStopManagingRequest request) {
        JobResponse jobResponse = new JobResponse();
        UIStopManagingRequest requestObj = request.requestObj;
        try {
            Deployment deployment = serviceAdapter.getDeployment(requestObj.serviceId);
            deployment.setTeardown(true);
            deployment.setIndividualTeardown(true);

            ServiceTemplate serviceTemplate = deployment.getServiceTemplate();
            for (ServiceTemplateComponent component : serviceTemplate.getComponents()) {
                if (component.getId().equals(requestObj.componentId)) {
                    for (String associatedId : requestObj.applicationIds) {
                        component.removeAssociatedComponent(associatedId);
                    }
                } else if (requestObj.applicationIds.contains(component.getId())) {
                    component.setTeardown(true);
                }
            }

            serviceAdapter.updateDeployment(deployment.getId(), deployment);
        } catch (Throwable t) {
            log.error("stopManagingApplications() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Creates a UIService from a deployment, including deployed devices, vms, status.
     * @param deployment
     * @return
     */
    private UIService createServiceFromDeployment(Deployment deployment,
                                                  boolean scaleup,
                                                  boolean keepVms) throws ControllerException {

        UIService service = parseDeployment(deployment, getApplicationContext(), scaleup, keepVms);

        ResourceList<DeviceGroup> spList = deviceGroupServiceAdapter.getAllDeviceGroups(null,
                                                                                        null,
                                                                                        null,
                                                                                        null);

        List<DeploymentDevice> mList = deployment.sortDeployedDevicesByComponentName();
        for (DeploymentDevice d : mList) {
            getDeploymentInventory(d, getApplicationContext(), service, deployment, spList);
        }

        //It appears that we are never setting/storing vm's
        //There is nowhere in the schema that we would be storing them either.
        //service.vmlist.addAll(getDevicesFromVMs(deployment));

        // at some point deployment devices may not include all components presented in template, i.e. storage volume
        // can be missed when service is scheduled in the future
        addMissedDevices(service, mList, deployment.getServiceTemplate());

        // update status
        updateServiceStatus(deployment, service);

        return service;
    }

    /**
     * At some point deployment devices may not include all components presented in template.
     * For now it affects only storage which can be missed when service is scheduled in the future.
     * @param service
     * @param mList
     * @param serviceTemplate
     */
    private void addMissedDevices(UIService service, List<DeploymentDevice> mList,
                                  ServiceTemplate serviceTemplate) {

        for (ServiceTemplateComponent component : serviceTemplate.getComponents()) {

            DeploymentDevice dto = null;
            if (mList != null) {
                for (DeploymentDevice deploymentDevice : mList) {
                    if (deploymentDevice.getRefId() != null &&
                            deploymentDevice.getRefId().equals(component.getAsmGUID())) {
                        dto = deploymentDevice;
                        break;
                    }
                }
            }

            if (component.getType() == ServiceTemplateComponentType.CLUSTER) {
                boolean found = false;
                for (UIDevice uidev : service.clusterlist) {
                    if (component.getId().equals(uidev.id)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // need to add to service inventory
                    UIDevice dev = new UIDevice();

                    dev.id = component.getId();
                    dev.asmGUID = component.getAsmGUID();
                    dev.ipAddress = component.getIP();
                    // state: managed, unmanaged, reserved - OLD STATES available, deployed, pending, unknown, errors, pending, deploying,
                    dev.state = "unmanaged";
                    dev.candelete = service.canDeleteResources;

                    DeviceType deviceType = null;
                    switch (component.getComponentID()) {
                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCVMM_CLUSTER_COMPONENT_ID:
                        deviceType = DeviceType.scvmm;
                        break;
                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMPONENT_ID:
                        deviceType = DeviceType.vcenter;
                        break;
                    }

                    dev.deviceType = DeviceController.getDeviceType(deviceType);

                    this.addClusterDetails(dev, component);
                    service.clusterlist.add(dev);
                }
            }
        }
    }

    /**
     * Rules for component status:
     * These two override all others
     * If in progress, resource is in progress
     * If cancelled, resource is cancelled
     * If neither of those are applicable, then it would follow these rules
     * If all statuses are green/complete/compliant, resource is green
     * If any are yellow, warning, noncompliant, resource is yellow
     * If any are error, red, resource is red
     * If any are cancelled, resource is cancelled
     *
     * @param deployment
     * @param service
     * @throws ControllerException
     */
    private void updateServiceStatus(Deployment deployment, UIService service) {
        if (deployment.getServiceTemplate() != null) {
            String sioRole = null;
            boolean plainEsx = false;
            int runningServers = 0;
            boolean hasServiceModeNodes = false;
            final Map<String, ServiceTemplateComponent> componentMap = deployment.getServiceTemplate().fetchComponentsMap();
            for (UITemplateBuilderComponent comp : service.components) {
                // Valid Statuses: pending, inprogress, complete, error, cancelled
                DeploymentDevice dd = findDeploymentDevice(comp.id, deployment.getDeploymentDevice());
                UIComponentStatus cs;
                if (dd != null) {
                    if (DeviceType.isServer(dd.getDeviceType())) {
                        ServiceTemplateComponent component = componentMap.get(comp.id);
                        if (component != null) {
                            if (sioRole == null) {
                                sioRole = ServiceTemplateClientUtil.getFlexosRole(component);
                            }
                            String imageType = component.getParameterValue(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_RESOURCE,
                                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_TYPE_ID);
                            plainEsx = sioRole == null && ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_ESXI_VALUE.equals(imageType);

                            for (String relCompId : component.getAssociatedComponents().keySet()) {
                                ServiceTemplateComponent relComp = componentMap.get(relCompId);
                                if (relComp != null &&
                                        ServiceTemplateComponentType.VIRTUALMACHINE.equals(relComp.getType())) {
                                    DeploymentDevice vmDevice = findDeploymentDevice(relCompId,
                                                                                     deployment.getDeploymentDevice());
                                    if (vmDevice != null &&
                                            !DeploymentStatusType.SERVICE_MODE.equals(dd.getStatus())) {
                                        if (DeploymentStatusType.IN_PROGRESS.equals(dd.getStatus()) ||
                                                DeploymentStatusType.IN_PROGRESS.equals(vmDevice.getStatus())) {
                                            dd.setStatus(DeploymentStatusType.IN_PROGRESS);
                                        } else if (DeploymentStatusType.ERROR.equals(dd.getStatus()) ||
                                                DeploymentStatusType.ERROR.equals(vmDevice.getStatus())) {
                                            dd.setStatus(DeploymentStatusType.ERROR);
                                        } else if (DeploymentStatusType.CANCELLED.equals(dd.getStatus()) ||
                                                DeploymentStatusType.CANCELLED.equals(vmDevice.getStatus())) {
                                            dd.setStatus(DeploymentStatusType.CANCELLED);
                                        } else if (DeploymentStatusType.COMPLETE.equals(dd.getStatus()) ||
                                                DeploymentStatusType.COMPLETE.equals(vmDevice.getStatus())) {
                                            dd.setStatus(DeploymentStatusType.COMPLETE);
                                        } else if (DeploymentStatusType.PENDING.equals(dd.getStatus()) &&
                                                DeploymentStatusType.PENDING.equals(vmDevice.getStatus())) {
                                            dd.setStatus(DeploymentStatusType.PENDING);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    cs = new UIComponentStatus();
                    cs.componentid = comp.id;
                    cs.statusmessage = null;
                    switch (dd.getStatus()) {
                    case PENDING:
                        cs.status = ResourceStatus.pending.name();
                        cs.resourcestate = ResourceStatus.pending.name();
                        break;
                    case COMPLETE:
                        cs.status = ResourceStatus.complete.name();
                        cs.resourcestate = ResourceStatus.complete.name();
                        if (DeviceType.isServer(dd.getDeviceType())) {
                            runningServers++;
                        }
                        // A Device's Health message will always be displayed first.
                        if (DeviceType.isServer(dd.getDeviceType()) ||
                                DeviceType.isStorage(dd.getDeviceType())) {
                            if (DeviceHealth.YELLOW.equals(dd.getDeviceHealth()) ||
                                    DeviceHealth.UNKNOWN.equals(dd.getDeviceHealth())) {
                                if (!ResourceStatus.error.name().equals(
                                        cs.resourcestate) && !ResourceStatus.inprogress.name().equals(
                                        cs.resourcestate))
                                    cs.resourcestate = ResourceStatus.warning.name();
                                cs.statusmessage = dd.getHealthMessage();
                            } else if (DeviceHealth.RED.equals(dd.getDeviceHealth())) {
                                if (!ResourceStatus.inprogress.name().equals(cs.resourcestate)) {
                                    cs.resourcestate = ResourceStatus.error.name();
                                    if (DeviceType.isServer(dd.getDeviceType())) {
                                        runningServers--;
                                    }
                                }
                                cs.statusmessage = dd.getHealthMessage();
                            }
                        }
                        // only process firmware compliance if service is associated with catalog
                        updateServiceFirmwareHealth(deployment, dd, cs);
                        break;
                    case ERROR:
                        cs.status = ResourceStatus.error.name();
                        cs.resourcestate = ResourceStatus.error.name();
                        break;
                    case CANCELLED:
                        cs.status = ResourceStatus.cancelled.name();
                        cs.resourcestate = ResourceStatus.cancelled.name();
                        break;
                    case FIRMWARE_UPDATING:
                    case POST_DEPLOYMENT_SOFTWARE_UPDATING:
                    case IN_PROGRESS:
                        cs.status = ResourceStatus.inprogress.name();
                        cs.resourcestate = ResourceStatus.inprogress.name();
                        break;
                    case SERVICE_MODE:
                        cs.status = ResourceStatus.servicemode.name();
                        cs.resourcestate = ResourceStatus.servicemode.name();
                        hasServiceModeNodes = true;
                        break;
                    default:
                        cs.status = ResourceStatus.pending.name();
                        cs.resourcestate = ResourceStatus.pending.name();
                        break;
                    }
                    if (cs.statusmessage == null) cs.statusmessage = dd.getStatusMessage();
                    cs.statustime = dd.getStatusEndTime();
                    cs.deviceid = cs.componentid; // not refId! Must use component.id instead
                    cs.devicetype = comp.type;

                    service.componentstatus.add(cs);

                } else {
                    if (DeploymentStatusType.IN_PROGRESS == deployment.getStatus() ||
                            DeploymentStatusType.FIRMWARE_UPDATING == deployment.getStatus() ||
                            DeploymentStatusType.PENDING == deployment.getStatus()) {
                        cs = new UIComponentStatus(comp.id, ResourceStatus.pending.name(), comp.type);
                        service.componentstatus.add(cs);
                    } else {
                        cs = new UIComponentStatus(comp.id, ResourceStatus.cancelled.name(), comp.type);
                        service.componentstatus.add(cs);
                    }
                }
            }

            service.enableServiceMode = !hasServiceModeNodes &&
                    isServiceModeButtonEnabled(runningServers, service.state, sioRole, plainEsx) &&
                    !service.componentUpdateRequired;
        }
    }

    /**
     * Service mode is enabled when:
     * - service is in terminal state (completed, cancelled, error)
     * - servers present and running:
     * It is supported for SO, HCI, compute-only and plain vmware / esxi deployments
     * For SO, compute-only and plain esxi it should require at least 2 servers. For HCI it should require 3 servers.
     * That is for RUNNING servers - do not count servers already placed in service mode
     *
     * @param runningServersCount
     * @param serviceState
     * @param sioRole
     * @param plainEsx
     */
    private boolean isServiceModeButtonEnabled(int runningServersCount, String serviceState, String sioRole, boolean plainEsx) {
        boolean enable = false;
        if (DeploymentStatusType.ERROR.getLabel().equals(serviceState) ||
                DeploymentStatusType.CANCELLED.getLabel().equals(serviceState) ||
                DeploymentStatusType.COMPLETE.getLabel().equals(serviceState) ||
                DeploymentStatusType.INCOMPLETE.getLabel().equals(serviceState) ||
                DeploymentStatusType.SERVICE_MODE.getLabel().equals(serviceState)) {
            if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_SDC.equals(sioRole) ||
                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_SDS.equals(sioRole) ||
                    plainEsx) {
                enable = runningServersCount > 1;
            } else if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_HYPER.equals(sioRole)) {
                enable = runningServersCount > 2;
            }
        }
        log.debug("Set enableServiceMode to [" + enable + "] for service state=" + serviceState + ", running servers: " +
                runningServersCount + ", SIO role=" + sioRole + ", plain ESX = " + plainEsx);
        return enable;
    }

    private void updateServiceFirmwareHealth(final Deployment deployment, final DeploymentDevice dd, final UIComponentStatus cs) {
        if (deployment.getFirmwareRepository() != null) {
            // Need to Check Compliance separately and update state/message accordingly
            if (CompliantState.UPDATEREQUIRED.equals(dd.getCompliantState())) {
                if (!ResourceStatus.error.name().equals(
                        cs.resourcestate) && !ResourceStatus.inprogress.name().equals(
                        cs.resourcestate))
                    cs.resourcestate = ResourceStatus.warning.name();  // Only change to warning if it's not already in error state
                if (cs.statusmessage == null)
                    cs.statusmessage = getApplicationContext().getMessage(
                            "complianceMessage.UPDATE_REQUIRED", null,
                            LocaleContextHolder.getLocale());
                else
                    cs.statusmessage = cs.statusmessage + "  " + getApplicationContext().getMessage(
                            "complianceMessage.UPDATE_REQUIRED", null,
                            LocaleContextHolder.getLocale());
            } else if (CompliantState.NONCOMPLIANT.equals(dd.getCompliantState())) {
                if (!ResourceStatus.error.name().equals(
                        cs.resourcestate) && !ResourceStatus.inprogress.name().equals(
                        cs.resourcestate))
                    cs.resourcestate = ResourceStatus.warning.name();  // Only change to warning if it's not already in error state
                if (cs.statusmessage == null)
                    cs.statusmessage = getApplicationContext().getMessage(
                            "complianceMessage.NONCOMPLIANT", null,
                            LocaleContextHolder.getLocale());
                else
                    cs.statusmessage = cs.statusmessage + "  " + getApplicationContext().getMessage(
                            "complianceMessage.NONCOMPLIANT", null,
                            LocaleContextHolder.getLocale());
            }
        }
    }

    /**
     * Resource health is NOT a device status. It is COLOR only. Collected by Nagios, reported to DeploymentDevice
     * @param status
     * @return
     */
    private UIServiceHealth getUIServiceHealth(DeviceHealth status) {
        UIServiceHealth health = UIServiceHealth.green;

        if (status != null) {
            switch (status) {
            case RED:
                return UIServiceHealth.red;

            case YELLOW:
                return UIServiceHealth.yellow;

            case GREEN:
                return UIServiceHealth.green;

            case UNKNOWN:
                return UIServiceHealth.unknown;

            case SERVICE_MODE:
                return UIServiceHealth.servicemode;

            default:
                return UIServiceHealth.unknown;
            }
        }

        return health;
    }

    /**
     * Returns the size of the first vm virtual disk
     *
     * @example value like: {"virtualdisks":[{"id":"C9A3B27D-98B5-4620-ACDD-31B1CB477A65","disksize":"32"},
     *          {"id":"B8571BF3-F737-4C05-85D4-4753436773CE","disksize":"20"}]}
     *
     * @param setting ServiceTemplateSetting
     * @return String size in GB
     */
    private String getFirstVirtualDiskSize(ServiceTemplateSetting setting) {
        final JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(setting.getValue());
            final JSONArray virtualdisks = jsonObject.getJSONArray("virtualdisks");
            final JSONObject firstDisk = virtualdisks.getJSONObject(0);
            return firstDisk.getString("disksize");
        } catch (JSONException e) {
            log.error("Error parsing vmVirtualDiskConfig: " + setting.getValue());
            return null;
        }
    }

    /**
     *
     * @param deployment
     */
    private List<UINetworkResource> getServerNetworks(Deployment deployment) {
        List<UINetworkResource> serverDevices = new ArrayList<>();
        List<String> ifaces = new ArrayList<>();

        if (deployment.getServiceTemplate() != null) {
            Map<String, ServiceTemplateComponent> componentsMap = deployment.getServiceTemplate().fetchComponentsMap();

            for (ServiceTemplateComponent component : deployment.getServiceTemplate().getComponents()) {
                if (ServiceTemplateComponentType.SERVER.equals(component.getType())) {

                    for (ServiceTemplateCategory category : component.getResources()) {
                        if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_NETWORKING_COMP_ID.equals(
                                category.getId())) {
                            for (ServiceTemplateSetting parameter : category.getParameters()) {
                                if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_NETWORK_CONFIG_ID.equals(
                                        parameter.getId())) {
                                    NetworkConfiguration networkConfig = parameter.getNetworkConfiguration();
                                    if (networkConfig != null) {
                                        List<Network> networkList = networkConfig.getNetworks(
                                                NetworkType.PUBLIC_LAN, NetworkType.PRIVATE_LAN);
                                        if (networkList != null) {
                                            for (Network network : networkList) {
                                                String ifaceId = network.getId();
                                                if (!ifaces.contains(ifaceId)) {
                                                    ifaces.add(ifaceId);
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (serverDevices.size() == 0) {
                        UINetworkResource device = new UINetworkResource();
                        device.id = "0";
                        device.resourcename = "All Hosts";
                        device.resourcetype = "Server";
                        device.serviceHasVDS = deployment.isVDS();
                        device.networks = ifaces;
                        serverDevices.add(device); // there is always only one device - All Hosts
                    }
                }
            }
        }
        return serverDevices;
    }

    @RequestMapping(value = "removeservice", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse removeService(@RequestBody JobRemoveServiceRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            UIRemoveServiceRequest requestObj = request.requestObj;
            String deploymentId = requestObj.serviceId;
            serviceAdapter.deleteDeployment(deploymentId, requestObj.serversInInventory, requestObj.resourceState);
        } catch (Throwable t) {
            log.error("removeService() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }


    @RequestMapping(value = "deleteservice", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse deleteService(@RequestBody JobDeleteServiceRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            UIDeleteServiceRequest requestObj = request.requestObj;
            String deploymentId = requestObj.serviceId;
            Deployment deployment = serviceAdapter.getDeployment(deploymentId);

            Set<String> teardownList = new HashSet<>();
            // NOTE: ignoring requestObj for servers, user is forced to delete all servers
            for (ServiceTemplateComponent component : deployment.getServiceTemplate().getComponents()) {
                if (ServiceTemplateComponentType.SERVER.equals(component.getType())) {
                    teardownList.add(component.getId());
                }
            }
            if (requestObj.deleteStorageVolumes && CollectionUtils.isNotEmpty(requestObj.volumeList)) {
                teardownList.addAll(requestObj.volumeList);
            }
            if (requestObj.deleteClusters && CollectionUtils.isNotEmpty(requestObj.clusterList)) {
                teardownList.addAll(requestObj.clusterList);
            }
            if (requestObj.deleteVMs && CollectionUtils.isNotEmpty(requestObj.vmList)) {
                teardownList.addAll(requestObj.vmList);
            }
            if (requestObj.deleteScaleios && CollectionUtils.isNotEmpty(requestObj.scaleioList)){
                teardownList.addAll(requestObj.scaleioList);
            }

            log.debug(
                    "Teardown list: " + teardownList.toString() + "; size=" + teardownList.size());
            // iterate through components and set ensure = absent for ones selected
            ServiceTemplate serviceTemplate = deployment.getServiceTemplate();
            setTeardownAndEnsureAbsent(serviceTemplate, teardownList);

            if (DeploymentStatusType.IN_PROGRESS.equals(
                    deployment.getStatus()) && deployment.isCanCancel()) {
                // Handle with cancel + teardown/delete
                deployment.setTeardownAfterCancel(true);
                deployment.setIndividualTeardown(false);
                deployment.setTeardown(false);
                serviceAdapter.cancelDeployment(deploymentId, deployment);
            } else {
                // Handle as normal teardown/delete
                deployment.setIndividualTeardown(false);//Not needed, but setting it explicitly
                deployment.setTeardown(true);
                serviceAdapter.updateDeployment(deploymentId, deployment);
            }

        } catch (Throwable t) {
            log.error("deleteService() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private void setTeardownAndEnsureAbsent(ServiceTemplate serviceTemplate,
                                            Set<String> teardownList) {
        if (teardownList == null || serviceTemplate == null)
            return;
        for (ServiceTemplateComponent component : serviceTemplate.getComponents()) {
            if (teardownList.contains(component.getId())) {
                component.setTeardown(true);
                for (ServiceTemplateCategory resource : component.getResources()) {
                    for (ServiceTemplateSetting param : resource.getParameters()) {
                        if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ENSURE.equals(param.getId())) {
                            param.setValue(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ENSURE_ABSENT);
                        }
                    }
                }
            }
        }
    }

    @RequestMapping(value = "retryservice", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse retryService(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();
        try {
            String deploymentId = request.requestObj.id;
            Deployment deployment = serviceAdapter.getDeployment(deploymentId);
            deployment.setRetry(true);
            deployment = serviceAdapter.updateDeployment(deploymentId, deployment);
            jobResponse.responseObj = createServiceFromDeployment(deployment, false, false);
        } catch (Throwable t) {
            log.error("retryService() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the service getcomponentdata.
     *
     * @param request
     *            the request
     * @return the service getcomponentdata
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getcomponentdata", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getComponentData(@RequestBody JobComponentDataRequest request) {

        JobResponse jobResponse = new JobResponse();
        UIComponentData cdata = new UIComponentData();
        cdata.serviceid = request.requestObj.serviceid;
        cdata.componentid = request.requestObj.componentid;

        jobResponse.responseObj = cdata;

        try {
            String certName;
            Deployment deployment = serviceAdapter.getDeployment(request.requestObj.serviceid);
            if (deployment == null) {
                log.error("No deployment job found: " + request.requestObj.serviceid);
            } else {
                certName = getPuppetCert(deployment, request.requestObj.componentid);

                if (certName != null) {
                    cdata.details = serviceAdapter.getDeploymentLog(request.requestObj.serviceid,
                                                                    certName);
                } else {
                    log.error(
                            "No puppet cert name for component ID=" + request.requestObj.componentid);
                }
            }

        } catch (Throwable t) {
            log.error("getComponentData() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Gets the service getcomponentdata.
     *
     * @param request
     *            the request
     * @return the service getcomponentdata
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getAdjustServiceComponents", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getAdjustServiceComponents(@RequestBody JobAdjustServiceComponentRequest request) {

        JobResponse jobResponse = new JobResponse();
        List<UITemplateBuilderComponent> list = new ArrayList<>();
        jobResponse.responseObj = list;
        Locale locale = LocaleContextHolder.getLocale();

        try {
            Deployment deployment = serviceAdapter.getDeployment(request.requestObj.serviceId);
            if (deployment == null) {
                log.error("No deployment found: " + request.requestObj.serviceId);
            } else {
                final ServiceTemplate currentTemplate = deployment.getServiceTemplate();
                ServiceTemplateComponent scaleIOComponent = null;
                final boolean duplicate = "duplicate".equals(request.requestObj.source);
                boolean checkForApplications = false;
                Map<String, ServiceTemplateComponent> componentsMap = new HashMap<>();
                for (ServiceTemplateComponent component : currentTemplate.getComponents()) {
                    switch (component.getType()) {
                    case SCALEIO:
                        scaleIOComponent = component;
                        break;
                    case SERVICE:
                        checkForApplications = true;
                        break;
                    }
                    componentsMap.put(component.getId(), component);
                }
                ServiceTemplate template = new ServiceTemplate();
                template.setId(UUID.randomUUID().toString());

                if (duplicate &&
                        StringUtils.isNotBlank(request.requestObj.componentId)) {
                    ServiceTemplateComponent originalComponent = componentsMap.get(request.requestObj.componentId);
                    String scaleioRole = originalComponent.getParameterValue(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_RESOURCE,
                            ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_ID);
                    if (scaleIOComponent != null &&
                            !ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_SDC.equalsIgnoreCase(scaleioRole)) {
                        template.getComponents().add(scaleIOComponent);
                    }
                    if (originalComponent != null) {
                        //remove VM associated components. Do this here to make the changes apply for all instances.
                        cleanupAssociatedVirtualMachines(originalComponent, currentTemplate);
                        for (int i = 1; i <= request.requestObj.instances; i++) {
                            String originalMarshalled = MarshalUtil.marshal(originalComponent);
                            ServiceTemplateComponent newComponent = MarshalUtil.unmarshal(ServiceTemplateComponent.class,
                                                                                          originalMarshalled);
                            newComponent.setId(UUID.randomUUID().toString());
                            newComponent.setName("Server " + i);
                            newComponent.setBrownfield(false);
                            template.getComponents().add(newComponent);
                            if (checkForApplications) {
                                if (originalComponent.getAssociatedComponents() != null && originalComponent.getAssociatedComponents().size() > 0) {
                                    for (Map.Entry<String, Map<String, String>> associatedComponentEntry : originalComponent.getAssociatedComponents().entrySet()) {
                                        ServiceTemplateComponent associatedComponent =
                                                componentsMap.get(associatedComponentEntry.getKey());
                                        if (associatedComponent != null &&
                                                ServiceTemplateComponentType.SERVICE.equals(associatedComponent.getType())) {
                                            Map<String, String> attributes = new HashMap<>();
                                            for (Map.Entry<String, String> attributeEntry : associatedComponentEntry.getValue().entrySet()) {
                                                attributes.put(attributeEntry.getKey(),
                                                               attributeEntry.getValue());
                                            }
                                            String associatedMarshalled = MarshalUtil.marshal(associatedComponent);
                                            ServiceTemplateComponent newAssociatedComponent =
                                                    MarshalUtil.unmarshal(ServiceTemplateComponent.class,
                                                                          associatedMarshalled);
                                            String newId = UUID.randomUUID().toString();
                                            newAssociatedComponent.setId(newId);
                                            newAssociatedComponent.setName(associatedComponent.getName() + " (" + i + ")");
                                            template.getComponents().add(newAssociatedComponent);
                                            newComponent.removeAssociatedComponent(associatedComponentEntry.getKey());
                                            newComponent.getAssociatedComponents().put(newId, attributes);
                                            newAssociatedComponent.removeAssociatedComponent(originalComponent.getId());
                                            newAssociatedComponent.addAssociatedComponentName(newComponent.getId(),
                                                                                              newComponent.getName());
                                        }
                                    }
                                }
                            }
                        }
                        template = templateServiceAdapter.updateParameters(template);
                    }
                } else {
                    ServiceTemplate defaultTemplate = templateServiceAdapter.getCustomizedComponent(null,
                                                                                                    request.requestObj.serviceId,
                                                                                                    ServiceTemplateComponentType.SERVER.getLabel());
                    defaultTemplate.sortInternals();
                    template.getComponents().addAll(defaultTemplate.getComponents());
                }

                UITemplateBuilder uiTemplateBuilder = TemplateController.parseTemplateBuilder(template,
                                                                                              getApplicationContext(),
                                                                                              true);
                for (UITemplateBuilderComponent component : uiTemplateBuilder.components) {
                    // strip everything related to old device
                    component.AsmGUID = null;
                    component.puppetCertName = null;
                    if (duplicate &&
                            (ServiceTemplateComponentType.VIRTUALMACHINE.getLabel().equals(component.type) ||
                                    ServiceTemplateComponentType.SERVER.getLabel().equals(component.type))) {

                        for (UITemplateBuilderCategory category : component.categories) {
                            List<UITemplateBuilderSetting> settingsToRemove = new ArrayList<>();
                            for (UITemplateBuilderSetting setting : category.settings) {
                                switch (setting.id) {
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_VM_GENERATE_NAME_ID:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_ASSIGN_HOSTNAME_ID:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_SVM_OS_ASSIGN_HOSTNAME_ID:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_TARGET_BOOTDEVICE_ID:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ID:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_ID:
                                    // we must set this, or UI will not display the field for the name because of
                                    // dependency chain.
                                    setting.requireatdeployment = true;
                                    setting.readOnly = true; // user must not be able to change it
                                    break;
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ASM_GUID:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_ATTEMPTED_SERVERS:
                                    settingsToRemove.add(setting);
                                    break;
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_CONFIGURE_MDM_ROLE_ID:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_INVENTORY_MDM_ROLE_ID:
                                    setting.value = ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_MDM_ROLE_NONE;
                                    break;
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_SVM_OS_HOSTNAME_ID:
                                    setting.requireatdeployment = true;
                                    setting.value = null;
                                    break;
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_SDC_GUID_ID:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_SDS_GUID_ID:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_INVENTORY_MDM_MANAGEMENT_IPS:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_INVENTORY_MDM_DATA_IPS:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_MDM_CONFIGURE_DATA_IPS:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_DISK_CONFIGURATION_ID:
                                    setting.value = null;
                                    break;
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_SOURCE:
                                    if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_SOURCE_MANUAL_BROWNFIELD.equals(setting.value)) {
                                        setting.value = ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_SOURCE_MANUAL;
                                    }
                                    break;
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_RAID_ID:
                                    setting.value = "";
                                    break;
                                }
                            }
                            if (!settingsToRemove.isEmpty()) {
                                category.settings.removeAll(settingsToRemove);
                            }
                        }
                    } else if (ServiceTemplateComponentType.SCALEIO.getLabel().equals(component.type)) {
                        for (UITemplateBuilderCategory category : component.categories) {
                            for (UITemplateBuilderSetting setting : category.settings) {
                                switch (setting.id) {
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_PROTECTION_DOMAIN_NEW_NAME_ID:
                                    setting.dependencyValue = ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_PROTECTION_DOMAIN_AUTO_GENERATE_ID +
                                            "," + ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_PROTECTION_DOMAIN_NEW_NAME_ID + "," +
                                            ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_PROTECTION_DOMAIN_SPECIFY_ID;
                                    setting.requireatdeployment = true;
                                    setting.readOnly = true;
                                    break;
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_PROTECTION_DOMAIN_TYPE_ID:
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_STORAGE_POOL_TYPE_ID:
                                    setting.requireatdeployment = true;
                                    setting.readOnly = true;
                                    break;
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_SPARE_CAPACITY_ID:
                                    setting.requireatdeployment = true;
                                    String currentSpareCapacity = "0";
                                    if (StringUtils.isNotBlank(setting.value)) {
                                        currentSpareCapacity = setting.value;
                                    }
                                    String recommendedSpareCapacity = calculateRecommendedSpareCapacity(
                                            scaleIOComponent,
                                            request.requestObj.instances,
                                            deployment.getDeploymentDevice());
                                    if (recommendedSpareCapacity == null) {
                                        recommendedSpareCapacity = currentSpareCapacity;
                                    }
                                    UITemplateBuilderListItem option = new UITemplateBuilderListItem();
                                    option.id = recommendedSpareCapacity;
                                    option.name = "Recommended Spare Capacity " + recommendedSpareCapacity + "%";
                                    setting.options.add(option);
                                    setting.value = recommendedSpareCapacity;

                                    option = new UITemplateBuilderListItem();
                                    option.id = currentSpareCapacity;
                                    option.name = "Current Spare Capacity " + currentSpareCapacity + "%";
                                    setting.options.add(option);
                                    break;
                                default:
                                    setting.requireatdeployment = false;
                                    break;
                                }
                            }
                        }
                    }
                    list.add(component);
                }
            }
        } catch (Throwable t) {
            log.error("getAdjustServiceComponents() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private String calculateRecommendedSpareCapacity(final ServiceTemplateComponent scaleIOComponent,
                                                     final int instances,
                                                     final Set<DeploymentDevice> devices) {
        String recommended = null;
        if (scaleIOComponent != null && CollectionUtils.isNotEmpty(devices)) {
            final String asmGuid = scaleIOComponent.getParameterValue(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_COMP_SCALEIO_ID,
                                                                      ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ASM_GUID);
            if (asmGuid != null) {
                Optional<DeploymentDevice> optional = devices.stream().filter(device -> asmGuid.equals(device.getRefId())).findFirst();
                if (optional.isPresent()) {
                    DeploymentDevice scaleIO = optional.get();
                    if (scaleIO.getFacts() != null) {
                        PuppetScaleIOSystem system = null;
                        try {
                            system = objectMapper.readValue(scaleIO.getFacts(),
                                                            PuppetScaleIOSystem.class);
                            if (system != null && CollectionUtils.isNotEmpty(system.getProtectionDomains())) {
                                final String protectionDomainName = scaleIOComponent.getParameterValue(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_COMP_SCALEIO_ID,
                                                                                                       ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_PROTECTION_DOMAIN_NEW_NAME_ID);
                                Optional<ProtectionDomain> optionalDomain =
                                        system.getProtectionDomains().stream().filter(pd -> protectionDomainName.equals(pd.getGeneral().getName())).findFirst();
                                int serverCount = 0;
                                if (optionalDomain.isPresent()) {
                                    ProtectionDomain protectionDomain = optionalDomain.get();
                                    serverCount = CollectionUtils.isNotEmpty(protectionDomain.getSdsList()) ? protectionDomain.getSdsList().size() : 0;
                                    float totalServers = instances + serverCount;
                                    int roundedSparePercentage = -1;
                                    if (totalServers > 0) {
                                        float percentage = (1 / totalServers) * 100;
                                        roundedSparePercentage = (int) Math.ceil(percentage);
                                    }
                                    if (roundedSparePercentage > 0) {
                                        recommended = Integer.toString(roundedSparePercentage);
                                    }
                                }
                            }
                            if (recommended == null) {
                                log.warn("Calculation for Spare Capacity could not be performed.\nScaleIO System = " + system.toString());
                            }
                        } catch (Exception e) {
                            log.warn("Could not parse Puppet ScaleIO System");
                        }
                    }
                }
            }
        }

        return recommended;
    }

    private void cleanupAssociatedVirtualMachines(final ServiceTemplateComponent originalComponent,
                                                  final ServiceTemplate serviceTemplate) {
        if (originalComponent == null || serviceTemplate == null) {
            return;
        }

        if (ServiceTemplateComponentType.SERVER.equals(originalComponent.getType()) ||
                ServiceTemplateComponentType.CLUSTER.equals(originalComponent.getType())) {
            final Set<String> compIdsToRemove = new HashSet<>();
            originalComponent.getAssociatedComponents().keySet().forEach(relCompId -> {
                ServiceTemplateComponent relComp = serviceTemplate.findComponentById(relCompId);
                if (relComp != null &&
                        ServiceTemplateComponentType.VIRTUALMACHINE.equals(relComp.getType())) {
                    compIdsToRemove.add(relCompId);
                }
            });
            if (!compIdsToRemove.isEmpty()) {
                compIdsToRemove.forEach(compId -> originalComponent.getAssociatedComponents().remove(compId));
            }
        }
    }

    /**
     * Gets the puppet logs for a component.
     *
     * @param request
     *            the request
     * @return the service getpuppetlogs
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getpuppetlogs", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getPuppetLogs(@RequestBody JobPuppetLogRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {

            RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                                MappingUtils.COLUMNS_PUPPET_LOGS,
                                                                MappingUtils.COLUMNS_PUPPET_LOGS,
                                                                null);

            String deploymentId = request.requestObj.deploymentId;
            Deployment deployment = serviceAdapter.getDeployment(deploymentId);
            if (deployment == null) {
                log.error("No deployment found: " + request.requestObj.deploymentId);
            } else {
                String puppetCert = getPuppetCert(deployment, request.requestObj.componentId);

                if (puppetCert != null) {
                    try {
                        ResourceList<PuppetLogEntry> puppetLogEntries = serviceAdapter.getPuppetLogs(
                                deploymentId, puppetCert, options.filterList);
                        jobResponse.responseObj = puppetLogEntries.getList();
                    } catch (WebApplicationException e) {
                        if (e.getResponse().getStatus() == 404) {
                            jobResponse.responseObj = new ArrayList<>();
                        } else {
                            throw e;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            log.error("getPuppetLogs() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private String getPuppetCert(Deployment deployment, String componentId) {
        for (ServiceTemplateComponent component : deployment.getServiceTemplate().getComponents()) {
            if (StringUtils.equals(component.getId(), componentId)) {
                if (ServiceTemplateComponentType.VIRTUALMACHINE == component.getType()) {
                    if (component.getTemplateSetting("os_host_name") != null) {
                        String osHostName = component.getTemplateSetting("os_host_name").getValue();
                        return "vm-" + osHostName;
                    } else
                        return null;
                } else {
                    return component.getPuppetCertName();
                }
            }
        }

        return null;
    }

    /**
     * Get UIService details from Deployment model.
     * It does NOT set the status per component.
     * @param deployment
     * @param applicationContext
     * @param keepVMs
     * @return
     */
    private UIService parseDeployment(Deployment deployment,
                                      ApplicationContext applicationContext,
                                      boolean scaleup,
                                      boolean keepVMs) throws ControllerException {
        UIService service = new UIService();

        if (deployment != null) {
            parseDeploymentSummary(service, deployment);
            service.createddate = MappingUtils.getTime(deployment.getCreatedDate());

            service.canMigrate = deployment.isCanMigrate();
            service.canScaleupStorage = deployment.isCanScaleupStorage();
            service.canScaleupServer = deployment.isCanScaleupServer();
            service.canScaleupVM = deployment.isCanScaleupVM();
            service.canScaleupCluster = deployment.isCanScaleupCluster();
            service.canScaleupApplication = deployment.isCanScaleupApplication();
            service.canScaleupNetwork = deployment.isCanScaleupNetwork();

            setFirmwareRepositoryOnService(deployment, service, scaleup);

            service.allStandardUsers = deployment.isAllUsersAllowed();
            service.owner = deployment.getOwner();
            service.canEdit = deployment.isCanEdit();
            service.canDelete = deployment.isCanDelete();
            service.canDeleteResources = deployment.isCanDeleteResources();
            service.canCancel = deployment.isCanCancel();
            service.canRetry = deployment.isCanRetry();
            service.canUpdateInventory = deployment.isCanUpdateInventory();
            service.hasVDS = deployment.isVDS();

            if (deployment.getAssignedUsers() != null) {
                for (User u : deployment.getAssignedUsers()) {
                    service.assignedUsers.add(UsersController.getJSONObject(u, applicationContext));
                }
            }

            if (deployment.getStatus() != null) {
                if (deployment.getStatus() == DeploymentStatusType.CANCELLING) {
                    service.cancelInprogress = true;
                }
                service.state = getServiceState(deployment);
                service.resourceHealth = getUIServiceHealth(deployment.getOverallDeviceHealth());
            }

            service.componentUpdateRequired = !deployment.isTemplateValid();
            if (deployment.getServiceTemplate() != null) {
                ServiceTemplate template = deployment.getServiceTemplate();
                service.templateId = template.getId();
                UITemplateBuilder builder = TemplateController.parseTemplateBuilder(template,
                                                                                    applicationContext,
                                                                                    scaleup);

                List<UITemplateBuilderComponent> componentsToKeep = new ArrayList<>();
                Map<String, UITemplateBuilderComponent> compToRemove = new HashMap<>();
                for (UITemplateBuilderComponent comp : builder.components) {
                    switch (comp.type) {
                    case "server":
                        for (UITemplateBuilderCategory category : comp.categories) {
                            if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_IDRAC_RESOURCE.equals(category.id)) {
                                for (UITemplateBuilderSetting param : category.settings) {
                                    if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_CONFIG_XML.equals(param.id)) {
                                        param.value = null;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        componentsToKeep.add(comp);
                        break;
                    case "cluster":
                        if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCVMM_CLUSTER_COMPONENT_ID.equals(
                                comp.componentid)) {
                            service.isHyperV = true;
                        }
                        componentsToKeep.add(comp);
                        break;
                    case "vm":
                        if (!keepVMs) {
                            //temporarily remove any vm from ui
                            compToRemove.put(comp.id, comp);
                        }
                        break;
                    default:
                        componentsToKeep.add(comp);
                        break;
                    }
                }
                if (!compToRemove.isEmpty()) {
                    if (!componentsToKeep.isEmpty()) {
                        Set<String> vmIds = compToRemove.keySet();
                        for (UITemplateBuilderComponent server : componentsToKeep) {
                            List<UIRelatedComponent> remove = new ArrayList<>();
                            for (UIRelatedComponent relComp : server.relatedcomponents) {
                                if (vmIds.contains(relComp.id)) {
                                    remove.add(relComp);
                                }
                            }
                            if (!remove.isEmpty()) {
                                server.relatedcomponents.removeAll(remove);
                            }
                        }
                    }
                    builder.components.removeAll(compToRemove.values());
                }
                service.components = builder.components;

                // logs
                if (deployment.getJobDetails() != null &&
                        deployment.getJobDetails().size() > 0) {

                    for (AsmDeployerLogEntry log : deployment.getJobDetails()) {
                        service.activityLogs.add(parseLogEntry(log));
                    }
                }
            }
        }
        return service;
    }

    /**
     * Get UIServiceSummary details from Deployment model.
     * @param deployment
     * @return
     */
    private void parseDeploymentSummary(UIServiceSummary service, Deployment deployment) {
        if (deployment != null) {
            service.brownField = deployment.isBrownfield();
            service.deployedBy = deployment.getCreatedBy();

            // deployment_started_date may be null to indicate deployment hasn't yet begun,
            // such as when the deployment is scheduled.
            service.deployedOn = MappingUtils.getTime(deployment.getDeploymentStartedDate());

            service.description = deployment.getDeploymentDescription();
            service.name = deployment.getDeploymentName();
            service.id = deployment.getId();

            // Set the Overall Service firmware status
            if (deployment.getFirmwareRepository() != null) {
                if (deployment.isCompliant()) {
                    service.firmwareCompliant = CompliantState.COMPLIANT.getLabel();
                } else {
                    service.firmwareCompliant = CompliantState.NONCOMPLIANT.getLabel();
                }
            } else {
                service.firmwareCompliant = CompliantState.UNKNOWN.getLabel();
            }

            if (deployment.getFirmwareRepository() != null) {
                final FirmwareRepository firmwareRepo = deployment.getFirmwareRepository();
                if (firmwareRepo != null) {
                    service.firmwarePackageName = firmwareRepo.getName();
                }
            }

            if (deployment.getStatus() != null) {
                service.health = getServiceHealth(deployment);
            }

            if (DeploymentStatusType.PENDING.equals(deployment.getStatus()) &&
                    UIServiceHealth.unknown.equals(service.health)) {
                // probably firmware update, override the overall health to sync with UI code
                service.health = UIServiceHealth.pending;
            }

            if (deployment.getServiceTemplate() != null) {
                service.template = deployment.getServiceTemplate().getTemplateName();
                for (ServiceTemplateComponent comp : deployment.getServiceTemplate().getComponents()) {
                    switch (comp.getType()) {
                    case STORAGE:
                        service.count_storage++;
                        break;
                    case SERVER:
                        service.count_server++;
                        break;
                    case TOR:
                        service.count_switch++;
                        break;
                    case CLUSTER:
                        service.count_cluster++;
                        break;
                    case VIRTUALMACHINE:
                        service.count_vm++;
                        break;
                    case SERVICE:
                        service.count_application++;
                        break;
                    case SCALEIO:
                        service.count_scaleio++;
                        service.count_cluster++;
                        break;
                    }
                }
            }
        }
    }

    private DeploymentDevice findDeploymentDevice(String componentId, Set<DeploymentDevice> deviceSet) {
        if (deviceSet == null) return null;
        if (componentId == null) return null;
        for (DeploymentDevice dd : deviceSet) {
            if (componentId.equals(dd.getComponentId())) {
                return dd;
            }
        }
        return null;
    }

    private UIActivityLog parseLogEntry(AsmDeployerLogEntry log) {
        UIActivityLog entry = new UIActivityLog();
        entry.logMessage = log.getMessage();
        entry.logTimeStamp = log.getTimestamp();
        if (entry.logTimeStamp != null) {
            entry.logTimeStamp = entry.logTimeStamp.substring(0, 22) + "Z";
        }
        return entry;
    }

    public void addServerComponentSettings(UIDevice dev,
                                           ServiceTemplateComponent component,
                                           ApplicationContext applicationContext) {
        List<ServiceTemplateCategory> resources = component.getResources();
        for (ServiceTemplateCategory resource : resources) {
            if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_RESOURCE.equals(
                    resource.getId())) {
                ServiceTemplateSetting hostnameSetting = resource.getParameter(
                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_HOSTNAME_ID);
                String hostName = hostnameSetting.getValue();
                dev.hostname = hostName;

                final Locale locale = LocaleContextHolder.getLocale();
                ServiceTemplateSetting scaleIORole = resource.getParameter(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_ID);
                if (scaleIORole != null && StringUtils.isNotBlank(scaleIORole.getValue())) {
                    switch (scaleIORole.getValue()) {
                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_HYPER:
                        dev.osMode = applicationContext.getMessage("scaleIOOSMode.hypercoverged", null, locale);
                        break;
                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_SDC:
                        dev.osMode = applicationContext.getMessage("scaleIOOSMode.computeonly", null, locale);
                        break;
                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_SDS:
                        dev.osMode = applicationContext.getMessage("scaleIOOSMode.storageonly", null, locale);
                        break;
                    }
                }
                ServiceTemplateSetting scaleIOMDMRole = resource.getParameter(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_INVENTORY_MDM_ROLE_ID);

                if (scaleIOMDMRole != null && StringUtils.isNotBlank(scaleIOMDMRole.getValue())) {
                    switch (scaleIOMDMRole.getValue()) {
                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_MDM_ROLE_PRIMARY:
                        dev.mdmRole = applicationContext.getMessage("scaleIOMDMRole.primary", null, locale);
                        break;
                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_MDM_ROLE_SECONDARY:
                        dev.mdmRole = applicationContext.getMessage("scaleIOMDMRole.secondary", null, locale);
                        break;
                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_MDM_ROLE_TIE_BREAKER:
                        dev.mdmRole = applicationContext.getMessage("scaleIOMDMRole.tieBreaker", null, locale);
                        break;
                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_MDM_ROLE_STANDBY_MDM:
                        dev.mdmRole = applicationContext.getMessage("scaleIOMDMRole.standbyMDM", null, locale);
                        break;
                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_MDM_ROLE_STANDBY_TIE_BREAKER:
                        dev.mdmRole = applicationContext.getMessage("scaleIOMDMRole.standbyTieBreaker", null, locale);
                        break;
                    }
                }

                ServiceTemplateSetting scaleioMDMInventoryManagementIP = resource.getParameter(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_INVENTORY_MDM_MANAGEMENT_IPS);
                if (scaleioMDMInventoryManagementIP != null && StringUtils.isNotBlank(scaleioMDMInventoryManagementIP.getValue())) {
                    dev.vxflexosmanagementipaddress = scaleioMDMInventoryManagementIP.getValue().split(",")[0];
                }
                break;
            }
        }
    }

    /**
     * updateservicefirmware.
     *
     * @param request
     *            the request
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ParseException
     */
    @RequestMapping(value = "updateservicefirmware", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateServiceFirmware(
            @RequestBody JobUpdateServiceFirmwareRequest request) throws IOException, WebApplicationException {
        UIUpdateServiceFirmwareRequest requestObj = request.requestObj;

        JobResponse jobResponse = new JobResponse();
        FirmwareUpdateRequest updateRequest = new FirmwareUpdateRequest();
        updateRequest.setExitMaintenanceMode(requestObj.exitMaintenanceMode);
        updateRequest.setIdList(requestObj.idList);

        updateRequest.setScheduleType(requestObj.scheduleType);
        updateRequest.setUpdateType(UpdateType.SERVICE);
        if ("schedule".equals(updateRequest.getScheduleType())) {
            try {
                updateRequest.setScheduleDate(
                        ConversionUtility.getDateFromGuiString(requestObj.scheduleDate));
            } catch (ParseException e) {
                log.warn("Invalid date string received from GUI: " + requestObj.scheduleDate);
                throw new ControllerException(
                        getApplicationContext().getMessage("validationError.invalidDateTime", null,
                                                           LocaleContextHolder.getLocale()));
            }
        }

        // ensure vCenter and ESX versions are compliant
        try {
            log.debug("Calling deviceInventoryServiceAdapter.validateSoftwareComponentsForService()...");
            Response r = deviceInventoryServiceAdapter.validateSoftwareComponentsForService(updateRequest);
            log.debug("Response from the validation: statusInfo=" + r.getStatusInfo() + "; status=" + r.getStatus() + "; entity=" + r.getEntity());
        }
        catch (Exception e) {
            log.error("updateServiceFirmware() - Exception from service call", e);
            jobResponse = addFailureResponseInfo(e);
            return jobResponse;
        }

        // proceed to schedule the job
        deviceInventoryServiceAdapter.scheduleUpdate(updateRequest);

        return jobResponse;
    }

    /**
     * estimatefirmwareupdate.
     *
     * @param request
     *            the request
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "estimatefirmwareupdate", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse estimateFirmwareUpdate(@RequestBody JobStringsRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            // TODO: implement estimateFirmwareUpdate
            jobResponse.responseObj = "";

        } catch (Throwable t) {
            log.error("estimateFirmwareUpdate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Update service.
     *
     * @param request
     * @return the job response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "updateservice", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateService(@RequestBody JobUpdateServiceRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            Deployment configuration = serviceAdapter.getDeployment(request.requestObj.id);

            configuration.setDeploymentName(request.requestObj.name);
            configuration.setDeploymentDescription(request.requestObj.description);
            configuration.setUpdateServerFirmware(request.requestObj.manageFirmware);

            if (request.requestObj.manageFirmware && request.requestObj.firmwarePackageId != null) {
                if (TemplateController.USE_DEFAULT_CATALOG_ID.equals(
                        request.requestObj.firmwarePackageId)) {
                    configuration.setUseDefaultCatalog(true);
                    configuration.setFirmwareRepositoryId(null);
                    configuration.setFirmwareRepository(null);
                } else {
                    configuration.setUseDefaultCatalog(false);
                    setDeploymentFirmwareRepository(request.requestObj.firmwarePackageId,
                                                    configuration);
                }
            } else {
                configuration.setUseDefaultCatalog(false);
                configuration.setFirmwareRepository(null);
            }

            configuration.setAllUsersAllowed(request.requestObj.allStandardUsers);
            if (request.requestObj.assignedUsers != null) {
                configuration.setAssignedUsers(new HashSet<>());

                for (UIUser u : request.requestObj.assignedUsers) {
                    configuration.getAssignedUsers().add(
                            UsersController.getApplicationObject(u));
                }
            }

            configuration.setConfigurationChange(true);
            serviceAdapter.updateDeployment(request.requestObj.id, configuration);
        } catch (Throwable t) {
            log.error("updateService() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "deleteresources", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse deleteResources(@RequestBody JobDeleteResourceRequest request) {
        JobResponse jobResponse = new JobResponse();
        UIDeleteResourceRequest requestObj = request.requestObj;
        try {
            Deployment deployment = serviceAdapter.getDeployment(requestObj.serviceId);
            deployment.setTeardown(true);
            Set<String> teardownList = new HashSet<>();
            if (CollectionUtils.isNotEmpty(requestObj.volumeList)) {
                teardownList.addAll(requestObj.volumeList);
            }
            if (CollectionUtils.isNotEmpty(requestObj.clusterList)) {
                teardownList.addAll(requestObj.clusterList);
            }
            if (CollectionUtils.isNotEmpty(requestObj.vmList)) {
                teardownList.addAll(requestObj.vmList);
            }
            if (CollectionUtils.isNotEmpty(requestObj.applicationList)) {
                teardownList.addAll(requestObj.applicationList);
            }
            if (CollectionUtils.isNotEmpty(requestObj.serverList)) {
                teardownList.addAll(requestObj.serverList);
            }
            if (CollectionUtils.isNotEmpty(requestObj.scaleioList)) {
                teardownList.addAll(requestObj.scaleioList);
            }

            // iterate through components and set ensure = absent for ones selected
            ServiceTemplate serviceTemplate = deployment.getServiceTemplate();
            setTeardownAndEnsureAbsent(serviceTemplate, teardownList);
            deployment.setIndividualTeardown(true);

            serviceAdapter.updateDeployment(deployment.getId(), deployment);

        } catch (Throwable t) {
            log.error("deleteResources() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Get service list.
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "getservicedropdown", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServicesDropDown(@RequestBody JobRequest request) {

        Locale locale = LocaleContextHolder.getLocale();

        JobResponse jobResponse = new JobResponse();
        List<UIListItem> responseObj = new ArrayList<>();

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj, null, null);
        if (StringUtils.isBlank(options.sortedColumns)) {
            options.sortedColumns = "-updatedDate";
        }
        log.debug(
                "About to call serviceAdapter.getDeployments. options = " + Dump.toString(options));
        try {
            ResourceList<Deployment> mList = serviceAdapter.getDeploymentSummaries(options.sortedColumns,
                                                                                   null,
                                                                                   null,
                                                                                   MappingUtils.MAX_RECORDS,
                                                                                   Boolean.FALSE);

            List<UIServiceSummary> filteredList = getFilteredList(mList, options);

            List<UIServiceSummary> sList = getPaginatedList(filteredList, options.offset, options.limit);
            if (CollectionUtils.isNotEmpty(sList)) {
                for (UIServiceSummary service : sList) {
                    responseObj.add(new UIListItem(service.id, service.name));
                }
            }
            jobResponse.criteriaObj = request.criteriaObj;

            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = filteredList.size();
            }
            jobResponse.responseObj = responseObj;

        } catch (Throwable t) {
            log.error("getServicesDropDown() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Export component logs as CSV
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "exportpuppetlogs", method = RequestMethod.GET)
    public void exportPuppetLogs(@RequestParam("componentId") String componentId,
                                 @RequestParam("deploymentId") String deploymentId,
                                 HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();

        try {
            Deployment deployment = serviceAdapter.getDeployment(deploymentId);

            PuppetLogEntry[] logs = null;
            if (deployment == null) {
                log.error("No deployment found: " + deploymentId);
            } else {
                String puppetCert = getPuppetCert(deployment, componentId);

                if (puppetCert != null) {
                    try {
                        ResourceList<PuppetLogEntry> puppetLogEntries = serviceAdapter.getPuppetLogs(
                                deploymentId, puppetCert, null);
                        logs = puppetLogEntries.getList();
                    } catch (WebApplicationException e) {
                        if (e.getResponse().getStatus() != 404) {
                            throw e;
                        }
                    }
                }
            }


            if (logs != null) {
                for (PuppetLogEntry logEntry : logs) {
                    builder.append("\"" + "Severity:").append(logEntry.getSeverity()).append("\",");
                    builder.append("\"" + "Timestamp:").append(logEntry.getDate()).append("\",");
                    builder.append("\"" + "Description:").append(logEntry.getDescription()).append(
                            "\", ");
                    builder.append("\"" + "Source:").append(logEntry.getSource()).append("\", ");
                    builder.append("\"" + "File:").append(logEntry.getFile()).append("\", ");
                    builder.append("\"" + "Line:").append(logEntry.getLine()).append("\"\n");
                }
            }

            response.setStatus(200);
            response.setContentLength(builder.toString().length());
            response.setContentType("text/csv; charset=utf-8");
            response.setHeader("Content-Disposition",
                               "attachment; filename=" + getApplicationContext().getMessage(
                                       "ExportPuppetLogsFileName", null,
                                       LocaleContextHolder.getLocale()));
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");

            ServletOutputStream out = response.getOutputStream();
            out.println(builder.toString());
            out.flush();
            out.close();

        } catch (Throwable t) {
            log.error("exportJobs() - Exception from service call", t);
        }
    }

    /**
     * Gets the details of an existing service as best as it can via brownfield discovery.
     *
     * @param request
     *            the request
     * @return the service details
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getexistingservice", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getExistingService(@RequestBody JobGetExistingServiceRequest request) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.criteriaObj = request.criteriaObj;
        jobResponse.requestObj = request.requestObj;

        try {
            UIDeploy deploy = request.requestObj.deploy;
            if (deploy != null && CollectionUtils.isNotEmpty(deploy.template.components)) {
                String subtype = null;
                List<ServiceTemplateComponent> components = new ArrayList<>();
                for (UITemplateBuilderComponent templateBuilderComponent : deploy.template.components) {
                    ServiceTemplateComponentSubType type = null;
                    if (templateBuilderComponent.subtype != null) {
                        type = ServiceTemplateComponentSubType.fromValue(templateBuilderComponent.subtype);
                    }
                    switch (templateBuilderComponent.type) {
                    case "cluster":
                        if (ServiceTemplateComponentSubType.HYPERCONVERGED.equals(type) ||
                                ServiceTemplateComponentSubType.COMPUTEONLY.equals(type)) {
                            // Get the necessary data from input
                            subtype = templateBuilderComponent.subtype;
                        }
                        break;
                    case "scaleio":
                        if (ServiceTemplateComponentSubType.STORAGEONLY.equals(type)) {
                            subtype = templateBuilderComponent.subtype;
                        }
                        break;
                    }
                    components.add(TemplateController.createTemplateComponent(templateBuilderComponent));
                }
                List<PasswordSetting> passwordSettings = getPasswordSettings(request.requestObj.osCredentials);
                Deployment brownfieldDeployment = brownfieldServiceAdapter.getServiceDefinition(subtype,
                                                                                                components,
                                                                                                passwordSettings);

                if (brownfieldDeployment != null) {
                    jobResponse.responseObj = createServiceFromDeployment(brownfieldDeployment,
                                                                          Boolean.FALSE,
                                                                          Boolean.TRUE);
                }
            }
        } catch (Throwable t) {
            log.error("getExistingService() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "addexistingservice", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse addExistingService(@RequestBody JobAddExistingServiceRequest request) {


        JobResponse jobResponse = new JobResponse();
        List<FieldError> errorFields = new ArrayList<>();

        try {
            Deployment deployment = createDeployService(request.requestObj.deploy);

            List<PasswordSetting> passwordSettings = getPasswordSettings(request.requestObj.osCredentials);

            List<VDSSetting> vdsSettings = null;
            if (request.requestObj.vSwitches != null) {
                vdsSettings = request.requestObj.vSwitches.stream().map(vswitch -> {
                    VDSSetting vdsSetting = new VDSSetting();
                    vdsSetting.setId(vswitch.id);
                    vdsSetting.setName(vswitch.name);
                    for (UIPortGroup pg : vswitch.portGroups) {
                        PortGroupSetting portGroup = new PortGroupSetting();
                        portGroup.setId(pg.id);
                        portGroup.setName(pg.portGroup);
                        portGroup.setNetworkId(pg.network);
                        if ("NA".equals(pg.vlan)) {
                            portGroup.setVlanId(0);
                        } else {
                            portGroup.setVlanId(Integer.parseInt(pg.vlan));
                        }
                        vdsSetting.getPortGroupSettings().add(portGroup);
                    }
                    return vdsSetting;
                }).collect(Collectors.toList());
            }

            List<NetworkSetting> networkSettings = null;
            if (request.requestObj.existingServiceNetworks != null) {
                networkSettings = request.requestObj.existingServiceNetworks.stream().map(setting -> {
                    NetworkSetting networkSetting = new NetworkSetting();
                    networkSetting.setId(setting.id);
                    networkSetting.setName(setting.network);
                    networkSetting.setNetworkId(setting.networkId);
                    networkSetting.setNetworkType(NetworkType.fromValue(setting.type));
                    return networkSetting;
                }).collect(Collectors.toList());
            }

            Deployment createdDeployment = brownfieldServiceAdapter.createExistingService(deployment,
                                                                                          passwordSettings,
                                                                                          vdsSettings,
                                                                                          networkSettings);
            if (createdDeployment != null) {
                jobResponse.responseObj = createdDeployment.getId();
            }
        } catch (Throwable t) {
            log.error("addExistingService() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
            parseJobResponse(jobResponse, errorFields);
        }


        return jobResponse;
    }

    @RequestMapping(value = "getexistingservicecomponent", method = RequestMethod.POST)
    public @ResponseBody JobResponse getExistingServiceComponent(@RequestBody JobTemplateBuilderRequest request) {
        JobResponse jobResponse = new JobResponse();
        if (request != null && request.requestObj != null) {
            try {
                UITemplateBuilderRequest builderRequest = request.requestObj;
                // get the default template
                ServiceTemplate defaultTemplate = templateServiceAdapter.getTemplate(TemplateController.DEFAULT_TEMPLATE_ID,
                                                                                     true,
                                                                                     false);
                if (defaultTemplate == null) {
                    log.error("Missed default template [ID=1000]!");
                    throw new AsmRuntimeException(ASMCommonsMessages.internalServerError());
                }
                defaultTemplate.sortInternals();
                ServiceTemplateComponent selectedComponent = null;
                ServiceTemplateComponent serverComponent = null;
                BrownfieldServiceType serviceType = BrownfieldServiceType.fromValue(builderRequest.type);
                if (StringUtils.isNotEmpty(builderRequest.type)) {
                    for (ServiceTemplateComponent component : defaultTemplate.getComponents()) {
                        switch (component.getType()) {
                        case CLUSTER:
                            if (BrownfieldServiceType.HYPERCONVERGED.equals(serviceType) ||
                                    BrownfieldServiceType.COMPUTEONLY.equals(serviceType)) {
                                Set<ServiceTemplateCategory> categoriesToRemove = new HashSet<>();
                                for (ServiceTemplateCategory resource : component.getResources()) {
                                    switch (resource.getId()) {
                                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMP_ID:
                                        Set<ServiceTemplateSetting> settingsToRemove = new HashSet<>();
                                        for (ServiceTemplateSetting setting : resource.getParameters()) {
                                            Set<ServiceTemplateOption> optionsToRemove = new HashSet<>();
                                            switch (setting.getId()) {
                                            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CLUSTER_CLUSTER_VDS_ID:
                                            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CLUSTER_CLUSTER_SDRS_ID:
                                            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CLUSTER_CLUSTER_VSAN_ID:
                                                settingsToRemove.add(setting);
                                                break;
                                            default:
                                                for (ServiceTemplateOption option : setting.getOptions()) {
                                                    if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX.equals(option.getValue())) {
                                                        optionsToRemove.add(option);
                                                    }
                                                }
                                            }
                                            if (!optionsToRemove.isEmpty()) {
                                                setting.getOptions().removeAll(optionsToRemove);
                                            }
                                        }
                                        if (!settingsToRemove.isEmpty()) {
                                            resource.getParameters().removeAll(settingsToRemove);
                                        }
                                        break;
                                    default:
                                        categoriesToRemove.add(resource);
                                        break;
                                    }
                                }
                                if (!categoriesToRemove.isEmpty()) {
                                    component.getResources().removeAll(categoriesToRemove);
                                }
                                selectedComponent = component;
                            }
                            break;
                        case SCALEIO:
                            if (BrownfieldServiceType.STORAGEONLY.equals(serviceType)) {
                                Set<ServiceTemplateCategory> categoriesToRemove = new HashSet<>();
                                for (ServiceTemplateCategory resource : component.getResources()) {
                                    switch (resource.getId()) {
                                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_COMP_SCALEIO_ID:
                                        Set<ServiceTemplateSetting> settingsToRemove = new HashSet<>();
                                        for (ServiceTemplateSetting setting : resource.getParameters()) {
                                            Set<ServiceTemplateOption> optionsToRemove = new HashSet<>();
                                            switch (setting.getId()) {
                                            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ASM_GUID:
                                                //do nothing
                                                break;
                                            case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_PROTECTION_DOMAIN_TYPE_ID:
                                                for (ServiceTemplateOption option : setting.getOptions()) {
                                                    switch (option.getValue()) {
                                                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_PROTECTION_DOMAIN_SPECIFY_ID:
                                                    case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SCALEIO_GATEWAY_PROTECTION_DOMAIN_AUTO_GENERATE_ID:
                                                        optionsToRemove.add(option);
                                                        break;
                                                    }
                                                }
                                                if (!optionsToRemove.isEmpty()) {
                                                    setting.getOptions().removeAll(optionsToRemove);
                                                }
                                                //add temp option
                                                setting.getOptions().add(0,
                                                                         new ServiceTemplateOption(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SELECT,
                                                                                                   ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SELECT_ID,
                                                                                                   null,
                                                                                                   null));
                                                break;
                                            default:
                                                settingsToRemove.add(setting);
                                                break;
                                            }
                                        }
                                        if (!settingsToRemove.isEmpty()) {
                                            resource.getParameters().removeAll(settingsToRemove);
                                        }
                                        break;
                                    default:
                                        categoriesToRemove.add(resource);
                                        break;
                                    }
                                }
                                if (!categoriesToRemove.isEmpty()) {
                                    component.getResources().removeAll(categoriesToRemove);
                                }
                                selectedComponent = component;
                            }
                            break;
                        case SERVER:
                            Set<ServiceTemplateCategory> categoriesToRemove = new HashSet<>();
                            ServiceTemplateSetting imageTypeSetting = null;
                            Set<String> imageTargets = new HashSet<>();
                            for (ServiceTemplateCategory resource : component.getResources()) {
                                Set<ServiceTemplateSetting> settingsToRemove = new HashSet<>();
                                switch (resource.getId()) {
                                case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_RESOURCE:
                                    for (ServiceTemplateSetting setting : resource.getParameters()) {
                                        switch (setting.getId()) {
                                        case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_IMAGE_ID:
                                            setting.setDependencyTarget(null);
                                            setting.setDependencyValue(null);
                                            imageTypeSetting = setting;
                                            break;
                                        case ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_ID:
                                            switch (serviceType) {
                                            case HYPERCONVERGED:
                                            case COMPUTEONLY:
                                                ServiceTemplateOption esxiOption = setting.getOptions().stream()
                                                        .filter(option -> ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_HYPER.equals(option.getValue()))
                                                        .findFirst().orElse(null);
                                                if (esxiOption != null && esxiOption.getDependencyValue() != null) {
                                                    String[] esxiOptions = esxiOption.getDependencyValue().split(",");
                                                    if (esxiOptions != null) {
                                                        imageTargets.addAll(Arrays.asList(esxiOptions));
                                                    }
                                                }
                                                break;
                                            case STORAGEONLY:
                                                ServiceTemplateOption linuxOption = setting.getOptions().stream()
                                                        .filter(option -> ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_SCALEIO_ROLE_SDS.equals(option.getValue()))
                                                        .findFirst().orElse(null);
                                                if (linuxOption != null && linuxOption.getDependencyValue() != null) {
                                                    String[] linuxOptions = linuxOption.getDependencyValue().split(",");
                                                    if (linuxOptions != null) {
                                                        imageTargets.addAll(Arrays.asList(linuxOptions));

                                                    }
                                                }
                                                break;
                                            }
                                            break;
                                        default:
                                            settingsToRemove.add(setting);
                                            break;
                                        }
                                    }
                                    if (!settingsToRemove.isEmpty()) {
                                        resource.getParameters().removeAll(settingsToRemove);
                                    }
                                    break;
                                default:
                                    categoriesToRemove.add(resource);
                                    break;
                                }
                            }
                            if (!categoriesToRemove.isEmpty()) {
                                component.getResources().removeAll(categoriesToRemove);
                            }
                            if (imageTypeSetting != null) {
                                Set<ServiceTemplateOption> optionsToRemove = new HashSet<>();
                                for (ServiceTemplateOption option : imageTypeSetting.getOptions()) {
                                    if (!ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SELECT_ID.equals(option.getValue()) &&
                                            !imageTargets.contains(option.getValue())) {
                                        optionsToRemove.add(option);
                                    } else {
                                        option.setDependencyTarget(null);
                                        option.setDependencyValue(null);
                                    }
                                }
                                if (!optionsToRemove.isEmpty()) {
                                    imageTypeSetting.getOptions().removeAll(optionsToRemove);
                                }
                            }
                            serverComponent = component;
                            break;
                        }
                    }
                }
                if (selectedComponent != null) {
                    ServiceTemplate serviceTemplate = new ServiceTemplate();
                    selectedComponent.setSubType(ServiceTemplateComponentSubType.fromValue(serviceType.name()));
                    serviceTemplate.getComponents().add(selectedComponent);
                    if (serverComponent != null) {
                        serviceTemplate.getComponents().add(serverComponent);
                    }
                    UITemplateBuilder templateBuilder =
                            TemplateController.parseTemplateBuilder(serviceTemplate,
                                                                             getApplicationContext(),
                                                                             false);
                    jobResponse.responseObj = templateBuilder;
                }
            } catch (Throwable t) {
                log.error("addExistingService() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }
        }
        return jobResponse;
    }

    @RequestMapping(value = "getexistingserviceoscredentials", method = RequestMethod.POST)
    @ResponseBody
    JobResponse getExistingServiceOSCredentials(@RequestBody JobServiceRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            Deployment deployment = createDeployService(request.requestObj.service);
            processOSCredentials(jobResponse, deployment);
        } catch (Throwable t) {
            log.error("getExistingServiceOSCredentials() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "getexistingsoserviceoscredentials", method = RequestMethod.POST)
    @ResponseBody
    JobResponse getExistingSOServiceOSCredentials(@RequestBody JobCreateServiceRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            Deployment deployment = createDeployService(request.requestObj);
            processOSCredentials(jobResponse, deployment);
        } catch (Throwable t) {
            log.error("getExistingServiceOSCredentials() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private void processOSCredentials(JobResponse jobResponse, Deployment deployment) {
        if (deployment != null) {
            List<PasswordSetting> passwordSettings = brownfieldServiceAdapter.getOSCredentials(deployment);
            if (CollectionUtils.isNotEmpty(passwordSettings)) {
                List<UIOSCredential> uiosCredentials = passwordSettings.stream().map(setting -> {
                    UIOSCredential osCred = new UIOSCredential();
                    osCred.id = setting.getId();
                    osCred.name = setting.getName();
                    osCred.type = TemplateController.getDeviceTypeFromTypeName(setting.getType().name());
                    osCred.ipAddress = setting.getIpAddress();
                    osCred.ipAddressUrl = setting.getIpAddressUrl();
                    return osCred;
                }).collect(Collectors.toList());
                jobResponse.responseObj = uiosCredentials;
            }
        }
    }

    @RequestMapping(value = "getexistingservicevswitches", method = RequestMethod.POST)
    @ResponseBody
    JobResponse getExistingServiceVSwitches(@RequestBody JobServiceRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            Deployment deployment = createDeployService(request.requestObj.service);
            if (deployment != null) {
                List<VDSSetting> vdsSettings = brownfieldServiceAdapter.getVDSSettings(deployment);
                if (CollectionUtils.isNotEmpty(vdsSettings)) {
                    List<UIVSwitch> uivSwitches = vdsSettings.stream().map(setting -> {
                        UIVSwitch vswitch = new UIVSwitch();
                        vswitch.id = setting.getId();
                        vswitch.name = setting.getName();
                        vswitch.portGroups = setting.getPortGroupSettings().stream().map(portGroupSetting -> {
                            UIPortGroup portGroup = new UIPortGroup();
                            portGroup.id = portGroupSetting.getId();
                            portGroup.portGroup = portGroupSetting.getName();
                            portGroup.network = portGroupSetting.getNetworkId();
                            if (portGroupSetting.getVlanId() == 0) {
                                portGroup.vlan = "NA";
                            } else {
                                portGroup.vlan = portGroupSetting.getVlanId().toString();
                            }
                            if(CollectionUtils.isNotEmpty(portGroupSetting.getPortGroupNetworkOptions())) {
                                portGroup.networks = portGroupSetting.getPortGroupNetworkOptions().stream()
                                        .map(option -> new UIListItem(option.getId(), option.getName()))
                                        .collect(Collectors.toList());
                            }
                            return portGroup;
                        }).collect(Collectors.toList());
                        return vswitch;
                    }).collect(Collectors.toList());
                    jobResponse.responseObj = uivSwitches;
                }
            }
        } catch (Throwable t) {
            log.error("getExistingServiceVSwitches() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "getexistingservicenetworks", method = RequestMethod.POST)
    @ResponseBody
    JobResponse getExistingServiceNetworks(@RequestBody JobServiceRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            Deployment deployment = createDeployService(request.requestObj.service);
            if (deployment != null) {
                List<NetworkSetting> networkSettings = brownfieldServiceAdapter.getNetworkSettings(deployment);
                if (CollectionUtils.isNotEmpty(networkSettings)) {
                    List<UIExistingServiceNetwork> networks = networkSettings.stream().map(net -> {
                        return new UIExistingServiceNetwork(net);
                    }).collect(Collectors.toList());
                    jobResponse.responseObj = networks;
                }
            }
        } catch (Throwable t) {
            log.error("getExistingServiceNetworks() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }


    /**
     * Gets the details of an existing service as best as it can via brownfield discovery.
     *
     * @param request
     *            the request
     * @return the service details
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "updateserviceinventory", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateServiceInventory(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();
        jobResponse.criteriaObj = request.criteriaObj;
        jobResponse.requestObj = request.requestObj;

        try {
            String serviceId = request.requestObj.id;
            Deployment brownfieldDeployment = brownfieldServiceAdapter.getExistingServiceDiff(serviceId);

            UIService uiService = createServiceFromDeployment(brownfieldDeployment,
                                                              Boolean.FALSE,
                                                              false);
            jobResponse.responseObj = uiService;

        } catch (Throwable t) {
            log.error("updateServiceInventory() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Updates the existing service with the any new services that are now available.
     *
     * @param request
     *            contains the request object with the service id necessary to load the Service to be updated.
     * @return an empty JobResponse as it's not required at this time for the UI.
     * @throws ServletException
     *             if there is an unhandled ServletException.
     * @throws IOException
     *             if there is an unhandled IOException.
     */
    @RequestMapping(value = "updateexistingservice", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateExistingService(@RequestBody JobUpdateExistingServiceRequest request) {

        JobResponse jobResponse = new JobResponse();
        jobResponse.criteriaObj = request.criteriaObj;
        jobResponse.requestObj = request.requestObj;

        try {
            String serviceId = request.requestObj.serviceId;

            List<PasswordSetting> passwordSettings = getPasswordSettings(request.requestObj.osCredentials);

            Deployment deployment = new Deployment();
            deployment.setId(serviceId);
            deployment = brownfieldServiceAdapter.updateEsxiServiceDefinition(deployment, passwordSettings, null);
            jobResponse.responseObj = deployment.getId();

        } catch (Throwable t) {
            log.error("updateExistingService() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private List<PasswordSetting> getPasswordSettings(List<UIOSCredential> osCredentials) {
        List<PasswordSetting> passwordSettings = null;
        if (CollectionUtils.isNotEmpty(osCredentials)) {
             passwordSettings = osCredentials.stream().map(credential -> {
                PasswordSetting setting = new PasswordSetting();
                setting.setId(credential.id);
                setting.setValue(credential.credentialId);
                return setting;
            }).collect(Collectors.toList());
        }
        return passwordSettings;
    }

    private void setDeploymentFirmwareRepository(final String firmwareRepositoryId,
                                                 Deployment deployment) {
        FirmwareRepository repo = firmwareRepositoryServiceAdapter.getById(firmwareRepositoryId);
        if (repo != null) {
            deployment.setUseDefaultCatalog(false);
            deployment.setFirmwareRepositoryId(repo.getId());
            FirmwareRepository firmwareRepository = new FirmwareRepository();
            firmwareRepository.setId(repo.getId());
            firmwareRepository.setName(repo.getName());
            deployment.setFirmwareRepository(firmwareRepository);
        }
    }

    private void setFirmwareRepositoryOnService(Deployment deployment,
                                                UIService service,
                                                boolean scaleUp) {
        if (deployment != null && service != null) {
            service.manageFirmware = true; // always true now
            if (deployment.isUpdateServerFirmware()) {
                if (scaleUp && deployment.isUseDefaultCatalog()) {
                    service.firmwarePackageId = TemplateController.USE_DEFAULT_CATALOG_ID;
                } else if (deployment.getFirmwareRepository() != null) {
                    final FirmwareRepository firmwareRepo = deployment.getFirmwareRepository();
                    if (firmwareRepo != null) {
                        service.firmwarePackageId = firmwareRepo.getId();
                        service.firmwarePackageName = firmwareRepo.getName();
                    }
                }
            }
        }
    }

    enum ResourceStatus {
        complete,
        pending,
        inprogress,
        cancelled,
        error,
        warning,
        servicemode
    }

}
