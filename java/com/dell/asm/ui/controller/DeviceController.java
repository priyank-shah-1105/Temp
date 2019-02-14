/**************************************************************************
 *   Copyright (c) 2010-2018 Dell Inc. All rights reserved.               *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/

package com.dell.asm.ui.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.discovery.model.rhv.VirtualizationManager;
import com.dell.asm.asmcore.asmmanager.client.util.PuppetDbUtil;
import com.dell.asm.rest.common.exception.LocalizedWebApplicationException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.devicegroup.DeviceGroup;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.CompliantState;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.DeviceHealth;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.DeviceState;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareUpdateRequest;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareUpdateRequest.UpdateType;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedState;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ServiceReference;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.UpdateDeviceInventoryResponse;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoverDeviceType;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.ome.OmeDiscoveryJobs;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.ome.PuppetOmeApplication;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.Cluster;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.ProtectionDomainGeneral;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.ProtectionDomainStatistics;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.PuppetScaleIOSystem;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.SystemGeneral;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.SystemStatistics;
import com.dell.asm.asmcore.asmmanager.client.firmware.Component;
import com.dell.asm.asmcore.asmmanager.client.firmware.Device;
import com.dell.asm.asmcore.asmmanager.client.puppetdevice.PuppetDevice;
import com.dell.asm.asmcore.asmmanager.client.telemetryconnector.LatestPerformanceMetrics;
import com.dell.asm.asmcore.asmmanager.client.telemetryconnector.HistoricalPerformanceMetrics;
import com.dell.asm.asmcore.asmmanager.client.telemetryconnector.PerformanceData;
import com.dell.asm.common.utilities.ASMCommonsMessages;
import com.dell.asm.i18n2.exception.AsmRuntimeException;
import com.dell.asm.rest.common.RestCommonMessages;
import com.dell.asm.ui.adapter.service.ChassisServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.PuppetDeviceServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.ServerServiceAdapter;
import com.dell.asm.ui.adapter.service.TelemetryServiceAdapter;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.model.FilterObj;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringsRequest;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIDeviceState;
import com.dell.asm.ui.model.UIDeviceStatus;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.device.JobServiceModeRequest;
import com.dell.asm.ui.model.device.JobUpdateDeviceFirmwareRequest;
import com.dell.asm.ui.model.device.UIDellSwitchDevice;
import com.dell.asm.ui.model.device.UIDevice;
import com.dell.asm.ui.model.device.UIDeviceSummary;
import com.dell.asm.ui.model.device.UIFirmwareDevice;
import com.dell.asm.ui.model.device.UIOme;
import com.dell.asm.ui.model.device.UISCVMMDevice;
import com.dell.asm.ui.model.device.UIScaleIO;
import com.dell.asm.ui.model.device.UIScaleIOData;
import com.dell.asm.ui.model.device.UIScaleIOInformation;
import com.dell.asm.ui.model.device.UIScaleIOProtectionDomain;
import com.dell.asm.ui.model.device.UIScaleIOServerDetail;
import com.dell.asm.ui.model.device.UIScaleIOServerType;
import com.dell.asm.ui.model.device.UIScaleIOStoragePool;
import com.dell.asm.ui.model.device.UIScaleIOStorageVolume;
import com.dell.asm.ui.model.device.UIScaleIOWorkload;
import com.dell.asm.ui.model.device.UIUpdateDeviceFirmwareRequest;
import com.dell.asm.ui.model.device.UIVcenterDevice;
import com.dell.asm.ui.model.server.UIUsageData;
import com.dell.asm.ui.model.server.UIUsageDataPoint;
import com.dell.asm.ui.model.server.UIUsageDataSeries;
import com.dell.asm.ui.util.ChassisCache;
import com.dell.asm.ui.util.ConversionUtility;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.asm.chassis.client.device.Chassis;
import com.dell.pg.asm.chassis.client.device.IOM;
import com.dell.pg.asm.chassis.client.device.Server;
import com.dell.pg.asm.chassis.client.device.TagType;
import com.dell.pg.asm.server.client.device.FirmwareInventory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class Device Controller.
 */
@RestController
@RequestMapping(value = "/devices/")
public class DeviceController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(DeviceController.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private final ChassisServiceAdapter chassisServiceAdapter;
    private final ServerServiceAdapter serverServiceAdapter;
    private final PuppetDeviceServiceAdapter puppetDeviceServiceAdapter;
    private final TelemetryServiceAdapter telemetryServiceAdapter;

    @Autowired
    public DeviceController(DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                            ChassisServiceAdapter chassisServiceAdapter,
                            ServerServiceAdapter serverServiceAdapter,
                            PuppetDeviceServiceAdapter puppetDeviceServiceAdapter,
                            TelemetryServiceAdapter telemetryServiceAdapter) {
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.chassisServiceAdapter = chassisServiceAdapter;
        this.serverServiceAdapter = serverServiceAdapter;
        this.puppetDeviceServiceAdapter = puppetDeviceServiceAdapter;
        this.telemetryServiceAdapter = telemetryServiceAdapter;
    }

    /**
     * Parse ManagedDevice into UIDevice
     *
     * @param dto
     * @param mList
     * @return
     */

    public static UIDevice getUIDeviceFromManagedDevice(ManagedDevice dto,
                                                        ResourceList<Deployment> mList) {

        UIDevice dev = new UIDevice();

        dev = (UIDevice) getUIDeviceSummaryFromManagedDevice(dev,
                                                             dto,
                                                             mList);

        // If firmwarename is null it means that device is part of a service that is not managing firmware
        if (!CompliantState.UNKNOWN.getLabel().equals(dev.compliant)) {
            if (dto.getDeviceType() != null &&
                    DeviceType.isServer(dto.getDeviceType()) ||
                    !(DeviceType.vcenter.equals(dto.getDeviceType()) ||
                            DeviceType.scvmm.equals(dto.getDeviceType()) ||
                            DeviceType.rhvm.equals(dto.getDeviceType()) ||
                            DiscoverDeviceType.OME.equals(dto.getDiscoverDeviceType()))) {
                dev.showcompliancereport = true;
            }
        }

        dev.ipaddresstype = "management";
        dev.deviceidtype = "servicetag";

        if (DeviceType.isServer(dto.getDeviceType())) {
            dev.canpoweroff = true;
            dev.canpoweron = true;
            dev.nics = dto.getNics();
        }


        if (DeviceType.Server.equals(dto.getDeviceType())) {
            dev.ipaddresstype = "host";
            dev.deviceidtype = "serialnumber";
            dev.serialnumber = dto.getServiceTag();
        }

        dev.asmGUID = dto.getRefId();

        dev.firmwareName = DeviceController.getComplianceReportName(dto);

        dev.template = dto.getServerTemplateId();

        if (dto.getInventoryDate() != null)
            dev.lastInventory = MappingUtils.getDate(dto.getInventoryDate());

        if (dto.getDiscoveredDate() != null)
            dev.discoveredOn = MappingUtils.getDate(dto.getDiscoveredDate());

        if (dto.getComplianceCheckDate() != null)
            dev.lastComplianceCheck = MappingUtils.getDate(dto.getComplianceCheckDate());

        dev.name = dto.getDisplayName();
        dev.resourcename = dto.getDisplayName();

        return dev;
    }

    private static UIDeviceSummary getUIDeviceSummaryFromManagedDevice(UIDeviceSummary dev,
                                                                      ManagedDevice dto,
                                                                      ResourceList<Deployment> mList) {

        // If firmwarename is null it means that device is part of a service that is not managing firmware
        if (dto.getFirmwareName() != null &&
                dto.getDeviceType() != null &&
                dto.getDeviceType().isFirmwareComplianceManaged() &&
                dto.getCompliance() != null) {
            dev.compliant = mapToUICompliance(dto.getCompliance(), dto.getState());
        } else {
            dev.compliant = CompliantState.UNKNOWN.getLabel();
        }

        if (dto.getDeviceType() != null) {
            dev.deviceType = DeviceController.getDeviceType(dto.getDeviceType());
        }

        dev.memory = "N/A";
        dev.processorcount = -1;

        if (DeviceType.isServer(dto.getDeviceType())) {
            dev.memory = String.valueOf(dto.getMemoryInGB());
            dev.processorcount = dto.getNumberOfCPUs();

            // find server pools that contain this device
            StringBuilder groups = new StringBuilder();
            if (dto.getDeviceGroupList() != null && dto.getDeviceGroupList().getDeviceGroup() != null) {
                for (DeviceGroup dg : dto.getDeviceGroupList().getDeviceGroup()) {
                    if (groups.length() > 0)
                        groups.append(",");

                    groups.append(dg.getGroupName());
                }
            }

            dev.displayserverpools = groups.toString();
        }

        if (DeviceType.isServer(dto.getDeviceType()) ||
                DeviceType.isChassis(dto.getDeviceType()) ||
                DeviceType.vcenter.equals(dto.getDeviceType()) ||
                DeviceType.scaleio.equals(dto.getDeviceType()) ||
                DeviceType.rhvm.equals(dto.getDeviceType()) ||
                DiscoverDeviceType.OME.equals(dto.getDiscoverDeviceType())) {
            dev.ipaddressurl = "https://" + dto.getIpAddress();
            dev.nics = Integer.toString(dto.getNics());
        } else if (DeviceType.AggregatorIOM.equals(dto.getDeviceType()) ||
                DeviceType.dellswitch.equals(dto.getDeviceType()) ||
                DeviceType.MXLIOM.equals(dto.getDeviceType()) ||
                DeviceType.scvmm.equals(dto.getDeviceType()) ||
                DeviceType.storage.equals(dto.getDeviceType())) {
            dev.ipaddressurl = "";
        }

        if (DeviceType.Server.equals(dto.getDeviceType())) {
            if (dto.getMemoryInGB() > 0)
                dev.memory = String.valueOf(dto.getMemoryInGB());
            dev.processorcount = dto.getNumberOfCPUs();
            if (dto.getModel().contains("C6220"))
                dev.ipaddressurl = "https://" + dto.getIpAddress() + "/login.html";
            else
                dev.ipaddressurl = "https://" + dto.getIpAddress();
        }

        if (dto.getState() != null &&
                DeviceState.SERVICE_MODE.equals(dto.getState())) {
            dev.health = DeviceHealth.SERVICE_MODE.getLabel();
        } else if (dto.getHealth() != null) {
            dev.health = dto.getHealth().getLabel();
        }
        if (dto.getHealthMessage() != null)
            dev.healthmessage = dto.getHealthMessage();
        if (dto.getModel() != null)
            dev.model = dto.getModel().trim();
        if (dto.getManufacturer() != null)
            dev.manufacturer = dto.getManufacturer().trim();
        if (dto.getCpuType() != null)
            dev.processor = dto.getCpuType();

        dev.id = dto.getRefId();
        dev.ipAddress = dto.getIpAddress();
        dev.serviceTag = dto.getServiceTag();
        if (StringUtils.isNotEmpty(dev.serviceTag) && dev.serviceTag.contains("INVALID_SVCTAG")) {
            dev.serviceTag = "";
        }
        dev.deviceid = dev.serviceTag;

        /**
         *  As of 8.3.1 managed state was separated out of state
         */
        if (dto.getManagedState() != null) {
            UIDeviceState state = mapToUIState(dto.getManagedState());

            dev.state = state.getLabel();
        }

        /**
         *  Status is still mapped from remaining state values
         */
        if (dto.getState() != null) {
            UIDeviceStatus status = mapToUIStatus(dto.getState());
            dev.status = status.getLabel();
        }

        dev.availability = mapToUIAvailability(dto.isInUse(), dto.getState());

        if (MappingUtils.isReadOnlyUser()) {
            dev.ipaddressurl = "";
        }

        setServicesForDeployedDevices(dto, dev, mList);

        // display name for Resources use different attributes for different types
        dev.dnsdracname = dto.getDisplayName();
        if (DeviceType.isSwitch(dto.getDeviceType()) ||
                DeviceType.scaleio.equals(dto.getDeviceType())) {
            dev.hostname = dto.getDisplayName(); // Display Name is HostName and avoids a PuppetDB Lookup/call
        } else {
            dev.hostname = dto.getHostname();
        }

        // for UI sorting
        if (dev.hostname == null) {
            dev.hostname = "";
        } else {
            dev.hostname = dev.hostname.trim();
        }

        dev.chassisname = dto.getDisplayName();
        dev.groupname = dto.getDisplayName();
        dev.storagecentername = dto.getDisplayName();

        return dev;
    }

    /**
     * Map to UI Compliance which is (compliant,noncompliant,updating,updatefailed)
     * which is a superset of CompliantState
     *
     * @param state
     * @return
     */
    public static String mapToUICompliance(CompliantState compliance, DeviceState state) {
        String uiCompliance;
        switch (compliance) {
        case UPDATEREQUIRED:
            switch (state) {
            case UPDATING:
                uiCompliance = "updating";
                break;
            case UPDATE_FAILED:
                uiCompliance = "updatefailed";
                break;
            default:
                uiCompliance = compliance.toString();
                break;
            }
            break;
        case NONCOMPLIANT:
            switch (state) {
            case UPDATING:
                uiCompliance = "updating";
                break;
            case UPDATE_FAILED:
                uiCompliance = "updatefailed";
                break;
            default:
                uiCompliance = compliance.toString();
                break;
            }
            break;
        default:
            uiCompliance = compliance.toString();
            break;
        }
        return uiCompliance.toLowerCase();
    }

    /**
     * Map to UI State which is only (MANAGED,UNMANAGED,RESERVED)
     * which was originally only a subset of DeviceState prior to 8.3.1
     *
     * @param state
     * @return
     */
    public static UIDeviceState mapToUIState(ManagedState state) {
        UIDeviceState uiState;
        switch (state) {
        case UNMANAGED:
            uiState = UIDeviceState.UNMANAGED;
            break;
        case RESERVED:
            uiState = UIDeviceState.RESERVED;
            break;
        default:
            uiState = UIDeviceState.MANAGED;
            break;
        }
        return uiState;
    }

    /**
     * Map to UI Status which is only (READY,DEPLOYING,UPDATING,PENDINGUPDATES,ERROR,...)
     * which is the subset of DeviceState excluding UIState ( see above ).
     *
     * @param state
     * @return
     */
    public static UIDeviceStatus mapToUIStatus(DeviceState state) {
        UIDeviceStatus uiStatus;
        switch (state) {
        case DEPLOYED:
        case SERVICE_MODE:
        case READY:
            uiStatus = UIDeviceStatus.READY;
            break;
        case UPDATING:
            uiStatus = UIDeviceStatus.UPDATING;
            break;
        case DEPLOYING:
            uiStatus = UIDeviceStatus.DEPLOYING;
            break;
        case CANCELLED:
            uiStatus = UIDeviceStatus.CANCELLED;
            break;
        case PENDING:
        case PENDING_CONFIGURATION_TEMPLATE:
        case PENDING_SERVICE_MODE:
            uiStatus = UIDeviceStatus.PENDING;
            break;
        case PENDING_DELETE:
            uiStatus = UIDeviceStatus.PENDING_DELETE;
            break;
        case UPDATE_FAILED:
            // This no longer has any effect on status.  Only compliance state.
            uiStatus = UIDeviceStatus.READY;
            break;
        case DEPLOYMENT_ERROR:
            uiStatus = UIDeviceStatus.DEPLOYMENT_ERROR;
            break;
        case CONFIGURATION_ERROR:
            uiStatus = UIDeviceStatus.CONFIGURATION_ERROR;
            break;
        default:
            uiStatus = UIDeviceStatus.READY;
            break;
        }
        return uiStatus;
    }

    /**
     * UI defines this as ("inuse","notinuse")
     *
     * @param isInUse boolean indicating if device is used by a Service
     * @return
     */
    public static String mapToUIAvailability(boolean isInUse, DeviceState state) {
        String value;
        // inUse wasn't being set during a fresh deployment.
        // so I was forced to check for deploying state.
        if (isInUse || state == DeviceState.DEPLOYING) {
            value = "inuse";
        } else {
            value = "notinuse";
        }
        return value;
    }

    /**
     *
     * @param dto
     * @param dev
     * @param mList
     */
    private static void setServicesForDeployedDevices(ManagedDevice dto, UIDeviceSummary dev,
                                                      ResourceList<Deployment> mList) {
        List<UIListItem> servicelist = new ArrayList<>();
        if (mList != null) {
            List<Deployment> deployments = Arrays.asList(mList.getList());
            if (deployments != null) {
                for (Deployment deployment : deployments) {
                    UIListItem serviceTemp = new UIListItem();
                    serviceTemp.id = deployment.getId();
                    serviceTemp.name = deployment.getDeploymentName();
                    servicelist.add(serviceTemp);
                }
            }
        } else {
            List<ServiceReference> serviceReferences = dto.getServiceReferences();
            if (serviceReferences != null) {
                for (ServiceReference ref : serviceReferences) {
                    UIListItem serviceTemp = new UIListItem();
                    serviceTemp.id = ref.getId();
                    serviceTemp.name = ref.getName();
                    servicelist.add(serviceTemp);
                }
            }
        }
        dev.servicelist = servicelist;
    }

    public static UIDevice getChassisLocation(ManagedDevice dto,
                                              ChassisServiceAdapter chassisServiceAdapter,
                                              ChassisCache cache) {
        UIDevice dev = new UIDevice();
        dev.model = dto.getModel();

        if (DeviceType.isChassis(dto.getDeviceType())) {
            String refId = dto.getRefId();
            ChassisCache.Value value = cache.getByChassisRefId(refId);
            if (value == null) {
                Chassis chassis = chassisServiceAdapter.getChassis(refId);
                cache.addChassis(chassis);
                value = cache.getByChassisRefId(refId);
            }

            dev.chassisname = value.getChassisName();
            dev.location = value.getLocation();
        } else if (DeviceType.isBlade(dto.getDeviceType())) {
            String serviceTag = dto.getServiceTag();
            ChassisCache.Value value = cache.getByBladeServiceTag(serviceTag);

            if (value == null) {
                try {
                    Chassis chassis = chassisServiceAdapter.getChassisByServiceTag(serviceTag,
                                                                                   TagType.SERVER.value());
                    cache.addChassis(chassis);
                    value = cache.getByBladeServiceTag(serviceTag);
                } catch (WebApplicationException wex) {
                    log.error(
                            "Cannot find chassis for blade: " + dto.getIpAddress() + ", service tag=" + serviceTag,
                            wex);
                    value = null;
                }
            }

            if (value == null) {
                dev.location = "Unknown";
                dev.chassisname = "Unknown";
            } else {
                dev.chassisname = value.getChassisName();
                dev.location = value.getLocation();
            }
        }
        return dev;
    }

    /**
     * Create class for ASM service from UI request.
     *
     * @param uidev
     * @return
     */
    public static ManagedDevice createManagedDevice(UIDevice uidev) {
        ManagedDevice dev = new ManagedDevice();

        dev.setDeviceType(MappingUtils.getManagedDeviceType(uidev.deviceType));
        dev.setIpAddress(uidev.ipAddress);
        dev.setModel(uidev.model);
        dev.setRefId(uidev.id);
        dev.setServiceTag(uidev.serviceTag);
        dev.setManufacturer(uidev.manufacturer);

        return dev;
    }

    public static String getDeviceType(final DeviceType type) {
        String deviceType = "unknown";
        if (type != null) {
            deviceType = type.name();
        }
        return deviceType;
    }

    public static String getComplianceReportName(ManagedDevice managedDevice) {
        return MappingUtils.getComplianceReportName(managedDevice.getCompliance(),
                                                    managedDevice.getFirmwareName());
    }

    /**
     * Get credential by ID.
     *
     * @param request
     *            the request
     * @return device
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getdevicebyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDeviceById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            ManagedDevice dto = deviceInventoryServiceAdapter.getDeviceInventory(
                    request.requestObj.id);

            UIDevice dev = getUIDeviceFromManagedDevice(dto, null);

            if (DeviceType.isChassis(dto.getDeviceType()) ||
                    DeviceType.isBlade(dto.getDeviceType())) {
                UIDevice chassisLoc = getChassisLocation(dto, chassisServiceAdapter,
                                                         new ChassisCache());
                if (chassisLoc != null) {
                    if (chassisLoc.location != null)
                        dev.location = chassisLoc.location;
                    if (chassisLoc.chassisname != null)
                        dev.chassisname = chassisLoc.chassisname;
                }
            }

            if (dto.getDeviceType() != null) {
                switch (dto.getDeviceType()) {
                case vcenter:
                    getVCenterDetails(dto, dev);
                    break;
                case dellswitch:
                    getDellSwitchDetails(dto, dev);
                    break;
                case Server:
                case RackServer:
                case BladeServer:
                case FXServer:
                case TowerServer:
                    getServerDetails(dev);
                    break;
                case scvmm:
                    getSCVMMDetails(dto, dev);
                    break;
                case rhvm:
                    getRHVMDetails(dto, dev);
                    break;
                case em:
                    if(DiscoverDeviceType.OME.equals(dto.getDiscoverDeviceType())){
                        getOmeDetails(dto, dev);
                    } else {
                        getEMDetails(dto, dev);
                    }
                    break;
                case genericswitch:
                    if (DiscoverDeviceType.BROCADE.equals(dto.getDiscoverDeviceType())) {
                        getBrocadeSwitchDetails(dto, dev);
                    }
                    break;
                case ciscoswitch:
                    getCiscoNexusSwitchDetails(dto, dev);
                    break;
                case scaleio:
                    PuppetScaleIOSystem system = objectMapper.readValue(dto.getFacts(),
                                                                        PuppetScaleIOSystem.class);
                    dev.deviceDetails = parseScaleIO(system, true);
                    break;
                }
                jobResponse.responseObj = dev;
            }
        } catch (Throwable t) {
            log.error("getDeviceById() - Exception from service call", t);
            // suppress the error to get rid of top red box on Resource page
            //jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "estimatefirmwareupdate", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse
    estimatefirmwareupdate(@RequestBody JobStringsRequest request) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = "N/A";
        return jobResponse;
    }

    @RequestMapping(value = "updatedevicefirmware", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateDeviceFirmware(@RequestBody JobUpdateDeviceFirmwareRequest request) {
        UIUpdateDeviceFirmwareRequest requestObj = request.requestObj;

        JobResponse jobResponse = new JobResponse();
        FirmwareUpdateRequest updateRequest = new FirmwareUpdateRequest();
        updateRequest.setExitMaintenanceMode(requestObj.exitMaintenanceMode);
        updateRequest.setIdList(requestObj.idList);

        updateRequest.setScheduleType(requestObj.scheduleType);
        updateRequest.setUpdateType(UpdateType.DEVICE);

        try {

            if ("schedule".equals(updateRequest.getScheduleType())) {
                try {
                    updateRequest.setScheduleDate(
                            ConversionUtility.getDateFromGuiString(requestObj.scheduleDate));
                } catch (ParseException e) {
                    log.warn("Invalid date string received from GUI: " + requestObj.scheduleDate);
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.invalidDateTime",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
            }
            deviceInventoryServiceAdapter.scheduleUpdate(updateRequest);

        } catch (Throwable t) {
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "runinventory", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse runInventory(@RequestBody JobStringsRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            if (request.requestObj != null && !request.requestObj.isEmpty()) {
                UpdateDeviceInventoryResponse[] response = deviceInventoryServiceAdapter.updateDeviceInventory(
                        request.requestObj);
                log.debug("Device inventory run for [" + response.length + "] number of devices.");
            }
        } catch (Throwable t) {
            log.error("runInventory() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Gets the device list.
     *
     * @return the device list
     * @throws javax.servlet.ServletException the servlet exception
     * @throws java.io.IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getdevicelist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDeviceList(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIDeviceSummary> responseObj;
        responseObj = new ArrayList<>();

        if (request.criteriaObj != null && request.criteriaObj.filterObj != null) {
            for (FilterObj f : request.criteriaObj.filterObj) {
                if (f.field.equals("health")) {
                    for (int i = 0; i < f.opTarget.size(); i++) {
                        f.opTarget.set(i, f.opTarget.get(i).toUpperCase());
                    }
                }
            }
        }

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_DEVICES,
                                                            MappingUtils.COLUMNS_DEVICES_FILTER,
                                                            "ipAddress");

        try {
            // without device type it would return not only servers for Global pool
            if (!CollectionUtils.isEmpty(options.filterList)
                    && options.filterList.contains("eq,serverpool,-1")) {
                options.filterList.add(
                        "eq,deviceType,RackServer, TowerServer, BladeServer, Server, FXServer");
            }

            ResourceList<ManagedDevice> mList = deviceInventoryServiceAdapter.getAllDeviceInventory(
                    options.sortedColumns, options.filterList,
                    options.offset < 0 ? null : options.offset,
                    options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);


            if (mList != null && mList.getList() != null) {
                if (mList.getList().length > 0) {
                    ChassisCache cache = new ChassisCache();
                    for (ManagedDevice dto : mList.getList()) {

                        UIDeviceSummary dev = new UIDeviceSummary();
                        dev = getUIDeviceSummaryFromManagedDevice(dev,
                                                                  dto,
                                                                  null);
                        if (DeviceType.isBlade(dto.getDeviceType())) {
                            UIDevice chassisLoc = null;
                            try {
                                chassisLoc = getChassisLocation(dto, chassisServiceAdapter, cache);
                            } catch (Exception e) {
                                log.warn("getDeviceList() - Exception while calling getChassisLocation(ManagedDevice, " +
                                                "ChassisServiceAdapter chassisServiceAdapter, ChassisCache",
                                        e);
                                // ERROR may occur while trying to obtain Chassis information due to an earlier Chassis
                                // removal.  It's a timing issue.  This error will be ignored for now to allow processing
                                // to continue successfully.
                            }
                            if (chassisLoc != null) {
                                if (chassisLoc.chassisname != null)
                                    dev.chassisname = chassisLoc.chassisname;
                            }
                        }
                        responseObj.add(dev); // Always add even if the chassis is null as many devices will not have a chassis
                    }
                } else {
                    if (mList.getTotalRecords() > 0 && request.criteriaObj != null && request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    (int) mList.getTotalRecords());
                        return getDeviceList(newRequest);
                    }
                }

                jobResponse.criteriaObj = request.criteriaObj;
                if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                    jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) mList.getTotalRecords();

                    if (jobResponse.criteriaObj.paginationObj.currentPage * jobResponse.criteriaObj.paginationObj.rowCountPerPage > mList.getTotalRecords()) {
                        jobResponse.criteriaObj.paginationObj.currentPage = 0;
                    }
                }
            }
        } catch (Throwable t) {
            log.error("getDeviceList() - Exception from service call", t);

            String matchingMessageCode = RestCommonMessages.invalidoffSet(
                    options.offset).getMessageCode();


            // May have requested a bad page that is not accessible due to inventory items being removed by other users
            if (t instanceof WebApplicationException &&
                    Response.Status.BAD_REQUEST.getStatusCode() == ((WebApplicationException) t).getResponse().getStatus() &&
                    this.matchesMessageCode((WebApplicationException) t, matchingMessageCode)) {

                // If the same message code as when an invalid offset is thrown then recurse
                // Error may occur due to requesting a page that does not exist.  Attempt to lower the page count and process
                if (request.criteriaObj != null && request.criteriaObj.currentPage > 0 && options.offset > 0) {
                    // Since the request for the offset may fail, we will not have a mList.getTotalRecords() to work with,
                    // thus we must try to guess as to the page location we should be on next.  We do this by taking the
                    // offset and subtracting the limit request (which should equal one page of results).  This
                    // should allow us to go back one page at a time.

                    if (options.limit <= options.offset) {
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    options.offset - options.limit);
                        return getDeviceList(newRequest);
                    }
                }
            }
            jobResponse = addFailureResponseInfo(t);
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Gets the device list.
     *
     * @return the device list
     * @throws javax.servlet.ServletException the servlet exception
     * @throws java.io.IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getavailableclonedevicelist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getAvailableDeviceRefList(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIDevice> responseObj;
        responseObj = new ArrayList<>();

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_DEVICES,
                                                            MappingUtils.COLUMNS_DEVICES_FILTER,
                                                            "ipAddress");
        try {
            List<String> filter = new ArrayList<>();
            filter.add("eq,deviceType,RackServer, TowerServer, BladeServer, Server, FXServer");

            ResourceList<ManagedDevice> mList = deviceInventoryServiceAdapter.getAllDeviceInventory(
                    options.sortedColumns, filter,
                    options.offset < 0 ? null : options.offset,
                    options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);


            if (mList != null && mList.getList() != null) {
                if (mList.getList().length > 0) {
                    for (ManagedDevice dto : mList.getList()) {
                        UIDevice dev = getUIDeviceFromManagedDevice(dto,
                                                                    null);
                        responseObj.add(dev); // add device to UI even if details were not found.

                    }
                } else {
                    if (mList.getTotalRecords() > 0 && request.criteriaObj != null && request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    (int) mList.getTotalRecords());
                        return getDeviceList(newRequest);
                    }
                }

                jobResponse.criteriaObj = request.criteriaObj;
                if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                    jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) mList.getTotalRecords();
                }
            }
        } catch (Throwable t) {
            log.error("getAvailableDeviceRefList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Gets the firmware device list.
     *
     * @return the firmware device list
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getfirmwaredevicelist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getFirmwareDeviceList(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIFirmwareDevice> responseObj = new ArrayList<>();

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_DEVICES,
                                                            MappingUtils.COLUMNS_DEVICES_FILTER,
                                                            "servicetag");
        try {

            ResourceList<ManagedDevice> mList = deviceInventoryServiceAdapter.getAllDeviceInventory(
                    options.sortedColumns, options.filterList,
                    options.offset < 0 ? null : options.offset,
                    options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);

            if (mList != null && mList.getList() != null) {
                if (mList.getList().length > 0) {
                    for (ManagedDevice dto : mList.getList()) {
                        responseObj.add(parseDeviceFirmware(dto, this.getApplicationContext()));
                    }
                } else {
                    if (mList.getTotalRecords() > 0 && request.criteriaObj != null && request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    (int) mList.getTotalRecords());
                        return getDeviceList(newRequest);
                    }
                }
                jobResponse.criteriaObj = request.criteriaObj;
                if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                    jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) mList.getTotalRecords();
                }
            }
            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("getFirmwareDeviceList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Remove devices from inventory by ids..
     *
     * @param request
     *            array of IDs
     * @return chassis
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse removeDevices(@RequestBody JobStringsRequest request) {

        JobResponse jobResponse = new JobResponse();
        try {
            for (String id : request.requestObj) {
                try {
                    deviceInventoryServiceAdapter.deleteDeviceInventory(id,
                                                                        false);// TODO: For the time being passing forceDelete option as false; this option
                } catch (Exception e) {
                    // Try catch and ignore of NOT_FOUND error added around deleting devices due to
                    // synchronization issues when removing multiple items from Resources page as documented in ASM-2559
                    if (e instanceof WebApplicationException &&
                            Response.Status.NOT_FOUND.getStatusCode() == ((WebApplicationException) e).getResponse().getStatus()) {
                        log.warn("Unable to delete device with id of: " + id);
                    } else throw e;
                }
                // should be provided by end user
            }
        } catch (Throwable t) {
            log.error("removeDevices() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "manage", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse manage(@RequestBody JobStringsRequest request) {

        JobResponse jobResponse = new JobResponse();

        boolean addError = false;
        try {
            for (String id : request.requestObj) {
                ManagedDevice dev = deviceInventoryServiceAdapter.getDeviceInventory(id);
                if (dev != null && !dev.isInUse()) {
                    dev.setManagedState(ManagedState.MANAGED);
                    deviceInventoryServiceAdapter.updateDeviceInventory(id, dev);
                } else {
                    addError = true;
                }
            }
            if (addError) {
                // NOTE: This is a TEMPORARY fix as States and Management states are being changed
                throw new AsmRuntimeException(ASMCommonsMessages.invalidStateChangeForDevices());
            }
        } catch (Throwable t) {
            log.error("manage() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "unmanage", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse unmanage(@RequestBody JobStringsRequest request) {

        JobResponse jobResponse = new JobResponse();

        boolean addError = false;
        try {
            for (String id : request.requestObj) {
                ManagedDevice dev = deviceInventoryServiceAdapter.getDeviceInventory(id);
                if (dev != null && !dev.isInUse()) {
                    dev.setManagedState(ManagedState.UNMANAGED);
                    deviceInventoryServiceAdapter.updateDeviceInventory(id, dev);
                } else {
                    addError = true;
                }
            }
            if (addError) {
                // NOTE: This is a TEMPORARY fix as States and Management states are being changed
                throw new AsmRuntimeException(ASMCommonsMessages.invalidStateChangeForDevices());
            }
        } catch (Throwable t) {
            log.error("unmanage() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "reserve", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse reserve(@RequestBody JobStringsRequest request) {

        JobResponse jobResponse = new JobResponse();

        boolean addError = false;
        try {
            for (String id : request.requestObj) {
                ManagedDevice dev = deviceInventoryServiceAdapter.getDeviceInventory(id);
                if (dev != null && !dev.isInUse()) {
                    dev.setManagedState(ManagedState.RESERVED);
                    deviceInventoryServiceAdapter.updateDeviceInventory(id, dev);
                } else {
                    addError = true;
                }
            }
            if (addError) {
                // NOTE: This is a TEMPORARY fix as States and Management states are being changed
                throw new AsmRuntimeException(ASMCommonsMessages.invalidStateChangeForDevices());
            }
        } catch (Throwable t) {
            log.error("reserve() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private UIFirmwareDevice parseDeviceFirmware(ManagedDevice dev,
                                                 ApplicationContext applicationContext) {

        // prepare the wrapper
        Device cDev = new Device();
        cDev.setRefId(dev.getRefId());
        cDev.setRefType(dev.getRefType());
        cDev.setServiceTag(dev.getServiceTag());
        cDev.setDeviceType(dev.getDeviceType());
        cDev.setModel(dev.getModel());
        cDev.setState(dev.getState());
        cDev.setInfraTemplateId(dev.getInfraTemplateId());
        cDev.setInfraTemplateDate(dev.getInfraTemplateDate());
        cDev.setIpAddress(dev.getIpAddress());
        cDev.setHealth(dev.getHealth());
        cDev.setComplianceCheckDate(dev.getComplianceCheckDate());
        cDev.setCompliance(dev.getCompliance());
        cDev.setDiscoveredDate(dev.getDiscoveredDate());
        cDev.setDeviceGroupList(dev.getDeviceGroupList());
        cDev.setHealthMessage(dev.getHealthMessage());
        cDev.setInventoryDate(dev.getInventoryDate());
        cDev.setServerTemplateDate(dev.getServerTemplateDate());
        cDev.setServerTemplateId(dev.getServerTemplateId());
        cDev.setStatusMessage(dev.getStatusMessage());

        if (dev.getDeviceType().equals(DeviceType.ChassisM1000e) ||
                dev.getDeviceType().equals(DeviceType.ChassisFX)) {
            // Chassis
            Chassis chassis = chassisServiceAdapter.getChassis(dev.getRefId());
            UIFirmwareDevice uif = getFirmwareDevice(cDev);

            uif.name = chassis.getName();

            List<com.dell.pg.asm.chassis.client.device.Controller> controllersSet = chassis.getControllers();
            for (com.dell.pg.asm.chassis.client.device.Controller controller : controllersSet) {
                // finding the primary controller
                if (controller.isControllerPrimary()) {
                    uif.version = controller.getControllerFWVersion();
                    break;
                }
            }

            // IOMs
            for (IOM iom : chassis.getIOMs()) {
                Device eachIOMDevice;

                eachIOMDevice = new Device();
                eachIOMDevice.setServiceTag(iom.getServiceTag());
                eachIOMDevice.setDeviceType(DeviceType.AggregatorIOM);
                eachIOMDevice.setModel(iom.getModel());

                // add child
                uif.devices.add(getFirmwareDevice(eachIOMDevice));
            }

            // Blades
            for (Server eachBladeserver : chassis.getServers()) {
                com.dell.pg.asm.server.client.device.Server bladeServerDevice;
                Device serverDevice = new Device();

                try {
                    List<String> filter = new ArrayList<>();
                    filter.add("eq,serviceTag," + eachBladeserver.getServiceTag());

                    com.dell.pg.asm.server.client.device.Server[] dList = serverServiceAdapter.getServers(
                            null, filter, null, null);
                    if (dList != null && dList.length > 0) {
                        bladeServerDevice = dList[0];
                        setServerFirmwareInfo(bladeServerDevice, serverDevice);
                    }

                } catch (Exception e) {
                    log.error(
                            " The Blade Server device with tage " + eachBladeserver.getServiceTag() + " is not found. ",
                            e);
                    serverDevice.setRefId(eachBladeserver.getId());
                    serverDevice.setModel(eachBladeserver.getModel());
                    serverDevice.setServiceTag(eachBladeserver.getServiceTag());
                }

                serverDevice.setDeviceType(DeviceType.BladeServer);
                serverDevice.setIpAddress(eachBladeserver.getManagementIP());
                // serverDevice.setHealth(applicationContext.getMessage("deviceHealth." + each, null, LocaleContextHolder.getLocale()));

                // add child
                uif.devices.add(getFirmwareDevice(serverDevice));
            }

            return uif;

        } else if (dev.getDeviceType() == DeviceType.RackServer || dev.getDeviceType() == DeviceType.TowerServer ||
                dev.getDeviceType() == DeviceType.Server) {
            // RackServer
            com.dell.pg.asm.server.client.device.Server rackServerDevice;

            try {
                rackServerDevice = serverServiceAdapter.getServer(dev.getRefId());
                setServerFirmwareInfo(rackServerDevice, cDev);

            } catch (Exception e) {
                log.error(
                        " The Rack Server device with Ref Id " + dev.getRefId() + " is not found. ",
                        e);
                cDev.setComponent(new ArrayList<>()); // adding empty component list
            }

            return getFirmwareDevice(cDev);

        } else if (!(dev.getDeviceType().equals(DeviceType.AggregatorIOM) ||
                (dev.getDeviceType().equals(DeviceType.MXLIOM)) ||
                DeviceType.isBlade(dev.getDeviceType()) ||
                (dev.getDeviceType().equals(DeviceType.unknown)))) {
            log.error(
                    "Received firmware request for unknown device:" + dev.getDeviceType().getValue() + " with service tag: " + dev.getServiceTag());
        }

        return getFirmwareDevice(cDev);
    }

    private void getServerDetails(UIDevice serverDevice) {
        com.dell.pg.asm.server.client.device.Server serverFromASMDB = serverServiceAdapter.getServer(serverDevice.id);
        if (serverFromASMDB != null) {
            serverDevice.dnsdracname = serverFromASMDB.getIdracName();
            serverDevice.os = serverFromASMDB.getOperatingSystemName();
            serverDevice.powerstate = getApplicationContext().getMessage(
                    "powerState." + serverFromASMDB.getPowerState().value(), null,
                    LocaleContextHolder.getLocale());
            if (serverDevice.lastInventory != null) {
                serverDevice.powerstate += ", ";
                serverDevice.powerstate += MappingUtils.formatDateForUI(
                        serverDevice.lastInventory, LocaleContextHolder.getLocale(),
                        false);
            }
        }
    }

    private UIFirmwareDevice getFirmwareDevice(Device dev) {
        UIFirmwareDevice uif = new UIFirmwareDevice();
        UIDevice uidev = getUIDeviceFromManagedDevice(dev, null);

        uif.id = uidev.id;
        uif.model = dev.getModel();
        uif.name = dev.getModel() + " " + dev.getServiceTag();
        uif.servicetag = dev.getServiceTag();
        uif.status = uidev.state;
        uif.template = uidev.template;
        uif.type = uidev.deviceType;

        uif.version = dev.getFirmwareVersion();
        if (dev.getComponent() != null) {
            for (Component comp : dev.getComponent()) {
                uif.components.add(comp.getName());
            }
        }

        return uif;
    }

    private void getVCenterDetails(ManagedDevice dto, UIDevice dev) {
        PuppetDevice vCenter = puppetDeviceServiceAdapter.getPuppetDevice(dto.getRefId());
        dev.datacenters = "0";
        dev.clusters = "0";
        dev.hosts = "0";
        dev.virtualmachines = "0";
        if (vCenter != null) {
            dev.deviceType = "vcenter";
            dev.datacenters = (String) vCenter.getData().get("datacenter_count");
            dev.clusters = (String) vCenter.getData().get("cluster_count");
            dev.hosts = (String) vCenter.getData().get("host_count");
            dev.virtualmachines = (String) vCenter.getData().get("vm_count");
        }
    }

    private void getSCVMMDetails(ManagedDevice dto, UIDevice dev) {
        PuppetDevice device = puppetDeviceServiceAdapter.getPuppetDevice(dto.getRefId());
        UISCVMMDevice scvmm = parseSCVMM(device, dto.getRefId());

        dev.ipaddressurl = "";
        dev.deviceType = "scvmm";
        dev.hostgroups = String.valueOf(scvmm.hostgroupsNum());
        dev.clusters = String.valueOf(scvmm.clustersNum());
        dev.hosts = String.valueOf(scvmm.hostsNum());
        dev.virtualmachines = String.valueOf(scvmm.vmsNum());
    }

    /**
     * TODO: populate
     * @param dto
     * @param dev
     */
    private void getRHVMDetails(ManagedDevice dto, UIDevice dev) throws IOException {
        Map<String, Object> factsMap = (HashMap<String, Object>) objectMapper.readValue(dto.getFacts(),
                new TypeReference<HashMap<String, Object>>() {});
        VirtualizationManager vm = PuppetDbUtil.convertToRHVM(factsMap);

        dev.ipaddressurl = dto.getIpAddress();
        dev.deviceType = DeviceType.rhvm.name();
    }

    private void getOmeDetails(ManagedDevice dto, UIDevice dev) throws IOException {
        PuppetOmeApplication omeData = objectMapper.readValue(dto.getFacts(), PuppetOmeApplication.class);
        dev.deviceDetails = parseOme(dto.getServiceTag(), omeData);
        dev.model = "";
        dev.hostname = "";
    }

    private void getEMDetails(ManagedDevice dto, UIDevice dev) {
        PuppetDevice puppetDevice = puppetDeviceServiceAdapter.getPuppetDevice(dto.getRefId());
        if (puppetDevice != null) {

            dev.serviceTag = dto.getServiceTag();
            dev.storagecentername = dto.getDisplayName();

            String timestamp = (String) puppetDevice.getData().get("timestamp");
            if (timestamp != null && dev.systemstatus != null && dev.systemstatus.length() > 0) {
                dev.systemstatus += ", " + MappingUtils.formatDateForUI(timestamp,
                                                                        LocaleContextHolder.getLocale(),
                                                                        true);
            }

        }
    }

    private void getDellSwitchDetails(ManagedDevice dto, UIDevice dev) {
        PuppetDevice puppetDevice = puppetDeviceServiceAdapter.getPuppetDevice(dto.getRefId());
        String timestamp = null;

        if (puppetDevice != null) {
            UIDellSwitchDevice uisw = DellSwitchController.parseSwitch(puppetDevice, dto);

            dev.systemstatus = uisw.powerstate.toLowerCase();
            dev.powerstate = dev.systemstatus;
            dev.hostname = uisw.hostname;
            dev.serviceTag = dto.getServiceTag();
            if (StringUtils.isNotEmpty(dev.serviceTag) && dev.serviceTag.contains(
                    "INVALID_SVCTAG")) {
                dev.serviceTag = "";
            }
            dev.ipAddress = uisw.ipaddress;
            dev.ipaddresstype = "management";
            dev.deviceidtype = "servicetag";
            dev.ipaddressurl = uisw.ipaddressurl;
            dev.deviceType = uisw.deviceType;

            timestamp = (String) puppetDevice.getData().get("timestamp");
        }

        if (timestamp != null && dev.powerstate != null && dev.powerstate.length() > 0) {
            dev.powerstate += ", " + MappingUtils.formatDateForUI(timestamp,
                                                                  LocaleContextHolder.getLocale(),
                                                                  true);
        }
    }

    private void getCiscoNexusSwitchDetails(ManagedDevice dto, UIDevice dev) {
        PuppetDevice puppetDevice = puppetDeviceServiceAdapter.getPuppetDevice(dto.getRefId());
        if (puppetDevice != null) {
            if (puppetDevice.getName().contains("cisconexus")) {
                log.debug("It is a Cisco Nexus switch");
            }

            String str = (String) puppetDevice.getData().get("status");
            if (str != null) {
                if (str.equals("down"))
                    dev.systemstatus = "Off";
                else
                    dev.systemstatus = "On";
            } else
                dev.systemstatus = "unknown";
            dev.powerstate = dev.systemstatus;
            dev.hostname = (String) puppetDevice.getData().get("hostname");
            dev.serviceTag = (String) puppetDevice.getData().get("servicetag");
            dev.ipAddress = (String) puppetDevice.getData().get("managementip");
            dev.ipaddresstype = "management";
            dev.serialnumber = dto.getServiceTag();
            if (MappingUtils.isReadOnlyUser()) {
                dev.ipaddressurl = "";
            } else {
                dev.ipaddressurl = "http://" + dev.ipAddress;
            }
            // dev.model = (String) puppetDevice.getData().get("name");

            String timestamp = (String) puppetDevice.getData().get("timestamp");
            if (timestamp != null && dev.powerstate != null && dev.powerstate.length() > 0) {
                dev.powerstate += ", " + MappingUtils.formatDateForUI(timestamp,
                                                                      LocaleContextHolder.getLocale(),
                                                                      true);
            }

        }
    }

    private void getBrocadeSwitchDetails(ManagedDevice dto, UIDevice dev) {
        PuppetDevice puppetDevice = puppetDeviceServiceAdapter.getPuppetDevice(dto.getRefId());
        String timestamp = null;

        if (puppetDevice != null) {
            dev.powerstate = (String) puppetDevice.getData().get("Switch State");
            dev.hostname = (String) puppetDevice.getData().get("Switch Name");
            dev.serialnumber = (String) puppetDevice.getData().get("Serial Number");
            dev.manufacturer = (String) puppetDevice.getData().get("Manufacturer");
            timestamp = (String) puppetDevice.getData().get("timestamp");
        }

        if (timestamp != null && dev.powerstate != null && dev.powerstate.length() > 0) {
            dev.powerstate += ", " + MappingUtils.formatDateForUI(timestamp,
                                                                  LocaleContextHolder.getLocale(),
                                                                  true);
        }
    }

    private void setServerFirmwareInfo(com.dell.pg.asm.server.client.device.Server serverDevice,
                                       Device firmwareServerDevice) {
        if (firmwareServerDevice == null || serverDevice == null)
            return;

        // get firmware inventory
        List<FirmwareInventory> firmwareInventoryList = serverDevice.getFirmwareList();
        List<Component> componentList = new ArrayList<>();

        for (FirmwareInventory firmwareInventory : firmwareInventoryList) {
            Component component = new Component();
            component.setFirmwareVersion(firmwareInventory.getVersion());
            component.setName(firmwareInventory.getName());
            componentList.add(component);
        }

        firmwareServerDevice.setComponent(componentList);
        firmwareServerDevice.setRefId(serverDevice.getRefId());
        firmwareServerDevice.setRefType(serverDevice.getRefType());
        firmwareServerDevice.setModel(serverDevice.getModel());
        firmwareServerDevice.setServiceTag(serverDevice.getServiceTag());
    }

    @RequestMapping(value = "getvcenter", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getvCenter(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();

        UIVcenterDevice responseObj = new UIVcenterDevice();

        try {
            PuppetDevice vCenter = puppetDeviceServiceAdapter.getPuppetDevice(
                    request.requestObj.id);
            responseObj = parseVCenter(vCenter);

        } catch (Throwable t) {
            log.error("getvCenter() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        jobResponse.responseObj = responseObj;
        return jobResponse;

    }

    /**
     * Get SCVMM tree.
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "getscvmm", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getSCVMM(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();

        UISCVMMDevice responseObj = new UISCVMMDevice();

        try {
            PuppetDevice device = puppetDeviceServiceAdapter.getPuppetDevice(request.requestObj.id);
            responseObj = parseSCVMM(device, request.requestObj.id);

        } catch (Throwable t) {
            log.error("getSCVMM() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        jobResponse.responseObj = responseObj;
        return jobResponse;

    }

    private UIVcenterDevice buildUiVCenterDevice(HashMap<String, Object> inventory) {
        UIVcenterDevice device = parseUiVcenterDevice(inventory);
        if (device != null) {
            ArrayList<HashMap<String, Object>> children = (ArrayList<HashMap<String, Object>>) inventory.get("children");
            for (HashMap<String, Object> child : children) {
                UIVcenterDevice childDevice = buildUiVCenterDevice(child);
                if (childDevice != null)
                    device.children.add(buildUiVCenterDevice(child));
            }
        }
        return device;
    }

    /*
        interpret object types and set UI model.
     */
    private UIVcenterDevice parseUiVcenterDevice(HashMap<String, Object> obj) {
        HashMap data = obj;
        UIVcenterDevice vDevice = new UIVcenterDevice();
        vDevice.name = (String) data.get("name");
        vDevice.ipaddress = "";
        vDevice.deviceid = (String) data.get("id");
        String type = (String) obj.get("type");
        switch (type) {
        case "Datacenter":
            vDevice.type = "datacenter";
            break;
        case "ComputeResource":
        case "ClusterComputeResource":
            vDevice.type = "cluster";
            break;
        case "HostSystem":
            vDevice.type = "host";
            break;
        case "ResourcePool":
            vDevice.type = "resourcepool";
            break;
        case "Folder":
            vDevice.type = "folder";
            break;
        case "VirtualMachine":
            HashMap<String, Object> attributes = (HashMap) data.get("attributes");
            boolean isTemplate = (Boolean) attributes.get("template");
            if (isTemplate)
                vDevice.type = "template";
            else
                vDevice.type = "vm";
            break;
        case "VirtualApp":
            vDevice.type = "app";
            break;
        default:
            log.warn("Unrecognized vcenter device type: " + type + "; not showing in UI");
            return null;
        }
        return vDevice;

    }

    private UIVcenterDevice parseVCenter(PuppetDevice vCenter) throws IOException {
        UIVcenterDevice ret;
        String inventoryJsonString = (String) vCenter.getData().get("inventory");
        //If there is no inventory, we just return an empty object to suppress any errors.
        if (inventoryJsonString == null) {
            ret = new UIVcenterDevice();
        } else {
            HashMap inventory = objectMapper.readValue(inventoryJsonString,
                                                       HashMap.class);
            if (inventory.get("children") == null) {
                ret = new UIVcenterDevice();
            } else {
                ret = buildUiVCenterDevice(inventory);
            }
            ret.deviceid = vCenter.getName();
            ret.type = "vcenter"; // force vcenter, coming back as folder
            ret.name = (String) vCenter.getData().get("vcenter_name");
        }

        return ret;
    }

    /**
     * Parse SCVMM
     * @param scvmm
     * @param id
     * @return
     */
    private UISCVMMDevice parseSCVMM(PuppetDevice scvmm, String id) {
        UISCVMMDevice root = new UISCVMMDevice(scvmm.getName(), "scvmm");
        if ("unknown".equals(root.name)) {
            // no name in facts, just use device IP
            String[] dev = id.split("-");
            if (dev.length == 2)
                root.name = dev[1];
            else
                root.name = "SCVMM";
        }
        //root.deviceid = id; // do not set: for some reason this ID is deadly for UI

        HashMap<String, Object> scvmmMap;

        ArrayList<Map<String, String>> hostGroups = null;
        ArrayList<Map<String, String>> hosts = null;
        ArrayList<Map<String, String>> vms = null;
        ArrayList<Map<String, String>> templates = null;
        try {
            String retStr = (String) scvmm.getData().get("value");

            scvmmMap = (HashMap<String, Object>) objectMapper.readValue(retStr, HashMap.class);
            hostGroups = (ArrayList<Map<String, String>>) scvmmMap.get("hostgroup");
            hosts = (ArrayList<Map<String, String>>) scvmmMap.get("host");
            vms = (ArrayList<Map<String, String>>) scvmmMap.get("vm");
            templates = (ArrayList<Map<String, String>>) scvmmMap.get("template");

        } catch (IOException e) {
            log.error("Unexpected format for scvmm tree", e);
        }

        if (hostGroups != null || hosts != null || vms != null) {

            // build a tree
            if (hostGroups != null) {
                for (Map<String, String> hg : hostGroups) {
                    String[] nArr = hg.get("Path").split("\\\\");
                    ArrayList<String> pathElements = new ArrayList<>(Arrays.asList(nArr));

                    mergeToSCVMMTree(root, pathElements,
                                     "folder"); // need to work on hostgroups styles
                }
            }

            if (hosts != null) {
                for (Map<String, String> hs : hosts) {
                    String[] nArr = hs.get("$_.VMHostGroup.Path").split("\\\\");
                    ArrayList<String> pathElements = new ArrayList<>(Arrays.asList(nArr));
                    String cluster = hs.get("$_.HostCluster.ClusterName");
                    if (StringUtils.isNotEmpty(cluster)) {
                        pathElements.add(cluster);
                        mergeToSCVMMTree(root, pathElements, "cluster");
                    }

                    String host = hs.get("Name");
                    if (StringUtils.isNotEmpty(host)) {
                        pathElements.add(host);
                        mergeToSCVMMTree(root, pathElements, "host");
                    }
                }
            }


            if (vms != null) {
                // root for VMs
                ArrayList<String> pathElements = new ArrayList<>();
                pathElements.add("VMs");
                mergeToSCVMMTree(root, pathElements, "folder");

                for (Map<String, String> vm : vms) {
                    String vmName = vm.get("Name");
                    if (StringUtils.isNotEmpty(vmName)) {
                        pathElements = new ArrayList<>();
                        pathElements.add("VMs");
                        pathElements.add(vmName);
                        mergeToSCVMMTree(root, pathElements, "vm");
                    }
                }
            }

            if (templates != null) {
                // root for templates which are also vm type
                ArrayList<String> pathElements = new ArrayList<>();
                pathElements.add("Templates");
                mergeToSCVMMTree(root, pathElements, "folder");

                for (Map<String, String> vm : templates) {
                    String vmName = vm.get("Name");
                    if (StringUtils.isNotEmpty(vmName)) {
                        pathElements = new ArrayList<>();
                        pathElements.add("Templates");
                        pathElements.add(vmName);
                        mergeToSCVMMTree(root, pathElements, "vm");
                    }
                }
            }

        }
        return root;
    }

    private void mergeToSCVMMTree(UISCVMMDevice node, ArrayList<String> pathElementsOriginal,
                                  String deviceType) {

        // clone array list because we will change it
        ArrayList<String> pathElements = new ArrayList<>(pathElementsOriginal);

        Iterator<String> iterator = pathElements.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            iterator.remove();

            UISCVMMDevice p = node.findNode(name);
            if (p == null) {
                UISCVMMDevice tree = new UISCVMMDevice(name, deviceType);
                node.children.add(tree);
                node = tree;
            } else {
                mergeToSCVMMTree(p, pathElements, deviceType);
            }
        }
    }

    @RequestMapping(value = "getscaleioworkload", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getScaleIOWorkload(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();

        try {
            ManagedDevice dto = deviceInventoryServiceAdapter.getDeviceInventory(request.requestObj.id);
            if(dto != null) {
                LatestPerformanceMetrics latestPerformanceMetrics =
                        telemetryServiceAdapter.getLatestPerformanceMetrics(dto.getIpAddress());
                if(latestPerformanceMetrics != null) {
                    populateScaleIOWorkloadJobResponse(latestPerformanceMetrics, jobResponse);
                }
            }
        } catch(Throwable t) {
            log.error("getScaleIOWorkload() - Exception from service call",t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "getscaleiobyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getScaleIOById(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();

        try {
            ManagedDevice dto = deviceInventoryServiceAdapter.getDeviceInventory(request.requestObj.id);
            PuppetScaleIOSystem system = objectMapper.readValue(dto.getFacts(),
                                                                PuppetScaleIOSystem.class);
            if (system != null && system.getGeneral() != null) {
                UIScaleIO scaleIO = parseScaleIO(system, false);
                scaleIO.id = dto.getRefId();
                scaleIO.ipaddressurl = "https://" + dto.getIpAddress();

                UIUsageData[] iopsUsageData = requestScaleIOUsageData(dto.getIpAddress(), "iops");
                if(iopsUsageData != null && iopsUsageData.length == 3) {
                    UIScaleIOData uiIOPSScaleIOData = new UIScaleIOData();
                    uiIOPSScaleIOData.read = iopsUsageData[0];
                    uiIOPSScaleIOData.write = iopsUsageData[1];
                    uiIOPSScaleIOData.total = iopsUsageData[2];
                    if (uiIOPSScaleIOData != null) {
                        scaleIO.ioData = uiIOPSScaleIOData;
                    }
                } else {
                    scaleIO.ioData = null;
                }

                UIUsageData[] bwUsageData = requestScaleIOUsageData(dto.getIpAddress(),"bw");
                if(bwUsageData != null && bwUsageData.length == 3) {
                    UIScaleIOData uiBWScaleIOData = new UIScaleIOData();
                    uiBWScaleIOData.read = bwUsageData[0];
                    uiBWScaleIOData.write = bwUsageData[1];
                    uiBWScaleIOData.total = bwUsageData[2];
                    if (uiBWScaleIOData != null) {
                        scaleIO.bandwidthData = uiBWScaleIOData;
                    }
                } else {
                    scaleIO.bandwidthData = null;
                }

                jobResponse.responseObj = scaleIO;
            }
        } catch (Throwable t) {
            log.error("getScaleIOById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private UIScaleIO parseScaleIO(PuppetScaleIOSystem system, boolean summary) {
        UIScaleIO scaleIO = null;
        if (system != null && system.getGeneral() != null) {
            final ApplicationContext context = getApplicationContext();
            final Locale locale = LocaleContextHolder.getLocale();
            scaleIO = new UIScaleIO();
            SystemGeneral general = system.getGeneral();
            scaleIO.systemId = general.getId();
            scaleIO.name = general.getName();
            Cluster cluster = system.getGeneral().getMdmCluster();
            if (cluster != null && cluster.getClusterState() != null) {
                if (cluster.getClusterState().contains("Cluster")) {
                    scaleIO.management = context.getMessage("scaleIO.Cluster",
                                                            null,
                                                            locale);
                } else if (cluster.getClusterState().contains("NotClustered")) {
                    scaleIO.management = context.getMessage("scaleIO.Single",
                                                            null,
                                                            locale);
                }
            } else {
                scaleIO.management = context.getMessage("scaleIO.Unknown",
                                                        null,
                                                        locale);
            }
            SystemStatistics systemStatistics = system.getStatistics();
            if (systemStatistics != null) {
                if (systemStatistics.getNumOfProtectionDomains() != null) {
                    scaleIO.protectionDomainCount = systemStatistics.getNumOfProtectionDomains();
                }
                if (systemStatistics.getNumOfSds() != null) {
                    scaleIO.sDSCount = systemStatistics.getNumOfSds();
                }
                if (systemStatistics.getNumOfSdc() != null) {
                    scaleIO.sDCCount = systemStatistics.getNumOfSdc();
                }
                if (systemStatistics.getNumOfVolumes() != null) {
                    scaleIO.volumeCount = systemStatistics.getNumOfVolumes();
                }

                UIScaleIOInformation information = new UIScaleIOInformation();
                if (systemStatistics.getProtectedCapacityInKb() != null) {
                    information.protectedInKb = systemStatistics.getProtectedCapacityInKb();
                }
                if (systemStatistics.getInMaintenanceCapacityInKb() != null) {
                    information.inMaintenanceInKb = systemStatistics.getInMaintenanceCapacityInKb();
                }
                if (systemStatistics.getDegradedHealthyCapacityInKb() != null) {
                    information.degradedInKb += systemStatistics.getDegradedHealthyCapacityInKb();
                }
                if (systemStatistics.getDegradedFailedCapacityInKb() != null) {
                    information.degradedInKb += systemStatistics.getDegradedFailedCapacityInKb();
                }
                if (systemStatistics.getFailedCapacityInKb() != null) {
                    information.failedInKb = systemStatistics.getFailedCapacityInKb();
                }
                if (systemStatistics.getUnusedCapacityInKb() != null) {
                    information.unusedInKb = systemStatistics.getUnusedCapacityInKb();
                }
                if (systemStatistics.getSpareCapacityInKb() != null) {
                    information.spareInKb = systemStatistics.getSpareCapacityInKb();
                }
                if (systemStatistics.getUnreachableUnusedCapacityInKb() != null) {
                    information.unavailableUnusedInKb = systemStatistics.getUnreachableUnusedCapacityInKb();
                }
                if (systemStatistics.getMovingCapacityInKb() != null) {
                    information.decreasedInKb = systemStatistics.getMovingCapacityInKb();
                }
                if (systemStatistics.getMaxCapacityInKb() != null) {
                    information.maxCapacityInKb = systemStatistics.getMaxCapacityInKb();
                }
                scaleIO.scaleIOInformation = information;
            }
            if (!summary) {
                final Map<String, UIScaleIOServerDetail> sdcMap = new LinkedHashMap<>();
                if (CollectionUtils.isNotEmpty(system.getSdcList())) {
                    system.getSdcList().stream().forEach(sdc -> {
                        UIScaleIOServerDetail detail = new UIScaleIOServerDetail();
                        detail.id = sdc.getId();
                        detail.ipAddresses.add(sdc.getSdcIp());
                        if (StringUtils.isNotEmpty(sdc.getName())) {
                            detail.name = sdc.getName();
                        } else {
                            detail.name = sdc.getSdcIp();
                        }
                        detail.connected = sdc.getMdmConnectionState();
                        sdcMap.put(sdc.getId(), detail);
                    });
                }

                if (CollectionUtils.isNotEmpty(system.getProtectionDomains())) {
                    UIScaleIO finalScaleIO = scaleIO;
                    system.getProtectionDomains().stream().forEach(pd -> {
                        ProtectionDomainGeneral pdGeneral = pd.getGeneral();
                        if (pdGeneral != null) {
                            UIScaleIOProtectionDomain protectionDomain = new UIScaleIOProtectionDomain();
                            protectionDomain.id = pdGeneral.getId();
                            protectionDomain.protectionDomainName = pdGeneral.getName();
                            ProtectionDomainStatistics statistics = pd.getStatistics();
                            if (statistics != null) {
                                protectionDomain.storageSize = convertToString(statistics.getMaxCapacityInKb());
                            }
                            if (CollectionUtils.isNotEmpty(pd.getSdsList())) {
                                UIScaleIOServerType serverType = new UIScaleIOServerType();
                                serverType.id = "sds";
                                serverType.name = context.getMessage("scaleIO.SDS",
                                                                     null,
                                                                     locale);
                                pd.getSdsList().stream().forEach(sds -> {
                                    UIScaleIOServerDetail detail = new UIScaleIOServerDetail();
                                    detail.id = sds.getId();
                                    detail.name = sds.getName();
                                    sds.getIpList().stream().forEach(ip -> {
                                        detail.ipAddresses.add(ip.getIp());
                                    });
                                    detail.connected = sds.getMdmConnectionState();
                                    serverType.scaleIOServerDetails.add(detail);
                                });
                                if (CollectionUtils.isNotEmpty(serverType.scaleIOServerDetails)) {
                                    protectionDomain.scaleIOServerTypes.add(serverType);
                                }
                            }
                            if (CollectionUtils.isNotEmpty(pd.getStoragePools())) {
                                UIScaleIOServerType serverType = new UIScaleIOServerType();
                                serverType.id = "sdc";
                                serverType.name = context.getMessage("scaleIO.SDC",
                                                                     null,
                                                                     locale);
                                pd.getStoragePools().stream().forEach(sp -> {
                                    UIScaleIOStoragePool storagePool = new UIScaleIOStoragePool();
                                    storagePool.id = sp.getId();
                                    storagePool.name = sp.getName();
                                    if (CollectionUtils.isNotEmpty(sp.getVolumes())) {
                                        sp.getVolumes().stream().forEach(volume -> {
                                            UIScaleIOStorageVolume sv = new UIScaleIOStorageVolume();
                                            sv.id = volume.getId();
                                            sv.name = volume.getName();
                                            if (volume.getSizeInKb() != null) {
                                                sv.size = convertToString(volume.getSizeInKb());
                                            }
                                            sv.type = volume.getVolumeType();
                                            sv.mappedSDCs = 0;
                                            if (CollectionUtils.isNotEmpty(volume.getMappedSdcInfo())) {
                                                sv.mappedSDCs = volume.getMappedSdcInfo().size();
                                                volume.getMappedSdcInfo().stream().forEach(sdc -> {
                                                    UIScaleIOServerDetail detail = sdcMap.get(sdc.getSdcId());
                                                    if (detail != null) {
                                                        serverType.scaleIOServerDetails.add(detail);
                                                    }
                                                });
                                            }
                                            protectionDomain.storageVolumes++;
                                            storagePool.scaleIOStorageVolumes.add(sv);
                                        });
                                    } else {
                                        if (CollectionUtils.isNotEmpty(system.getSdcList())) {
                                            system.getSdcList().stream().forEach(sdc -> {
                                                UIScaleIOServerDetail detail = sdcMap.get(sdc.getId());
                                                if (detail != null) {
                                                    serverType.scaleIOServerDetails.add(detail);
                                                }
                                            });
                                        }
                                    }
                                    protectionDomain.scaleIOStoragePools.add(storagePool);
                                });
                                if (CollectionUtils.isNotEmpty(serverType.scaleIOServerDetails)) {
                                    protectionDomain.scaleIOServerTypes.add(serverType);
                                    protectionDomain.mappedSDCs += serverType.scaleIOServerDetails.size();
                                }
                            }
                            finalScaleIO.scaleIOProtectionDomains.add(protectionDomain);
                        }
                    });
                }
            }
        }
        return scaleIO;
    }

    private UIOme parseOme(String serviceTag, PuppetOmeApplication ome) {
        UIOme uiOme = new UIOme();
        uiOme.serviceTag = serviceTag;
        if (ome != null) {
            uiOme.id = ome.getOmeSession().getId();
            List<OmeDiscoveryJobs> omeDiscoveryJobs = ome.getOmeDiscoveryJobs();
            if (omeDiscoveryJobs != null) {
                uiOme.discoveredDevices = omeDiscoveryJobs.stream()
                        .mapToInt(OmeDiscoveryJobs::getDiscoveryConfigDiscoveredDeviceCount).sum();
            }
        }
        return uiOme;
    }

    private String convertToString(long size) {
        String hrSize = "";
        long k = size;
        double m = size / 1024;
        double g = size / 1048576;
        double t = size / 1073741824;

        DecimalFormat dec = new DecimalFormat("0.0");
        if (t > 0) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 0) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 0) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 0) {
            hrSize = dec.format(k).concat(" KB");
        }
        return hrSize;
    }

    @RequestMapping(value = "servicemode", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse serviceMode(@RequestBody JobServiceModeRequest request) {

        JobResponse jobResponse = null;
        try {
            Response response = deviceInventoryServiceAdapter.processServiceMode(request.requestObj.deviceId,
                                                                                 request.requestObj.mode);
            jobResponse = new JobResponse();
        } catch (Throwable t) {
            log.error("serviceMode() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private void populateScaleIOWorkloadJobResponse(LatestPerformanceMetrics latestPerformanceMetrics, JobResponse jobResponse) {
        UIScaleIOWorkload uiScaleIOWorkload  = parseToUIFormat(latestPerformanceMetrics);
        jobResponse.responseObj = uiScaleIOWorkload;
    }

    private UIScaleIOWorkload parseToUIFormat(LatestPerformanceMetrics latestPerformanceMetrics) {
        UIScaleIOWorkload uiScaleIOWorkload = new UIScaleIOWorkload();
        uiScaleIOWorkload.setCurrentIOWorkload(latestPerformanceMetrics.getCurrentIOWorkload());
        uiScaleIOWorkload.setCurrentIOPS(latestPerformanceMetrics.getCurrentIOPS());
        uiScaleIOWorkload.setStatus(latestPerformanceMetrics.getStatus());
        uiScaleIOWorkload.setTimestamp(latestPerformanceMetrics.getTimestamp());

        return uiScaleIOWorkload;
    }

    private UIUsageData[] requestScaleIOUsageData(String host, String metricType) {
        UIUsageData[] usageData = {new UIUsageData(), new UIUsageData(), new UIUsageData()};
        for(int i = 0; i < usageData.length;i++){
            usageData[i].historicaldata = new ArrayList<>();
        }

        String[] timeFrameIdentifiers = {"Last Hour", "Last Day", "Last Week", "Last Month", "Last Year"};
        String[] timeFrameLabels = {"Per 5 Minutes", "Per Hour", "Per Day", "Per Day", "Per Month"};
        String[] timeFrames = {"hourly","daily","weekly","monthly","yearly"};

        for(int i = 0; i < timeFrames.length; i++){
            UIUsageDataSeries read = new UIUsageDataSeries();
            UIUsageDataSeries write = new UIUsageDataSeries();
            UIUsageDataSeries total = new UIUsageDataSeries();

            HistoricalPerformanceMetrics historicalPerformanceMetrics = null;

            try {
                historicalPerformanceMetrics = telemetryServiceAdapter.getHistoricalPerformanceMetrics(
                        host, timeFrames[i], metricType);
            } catch (Throwable t) {
                log.error("Failed to retrieve historical performance metrics " + t);
                // return null instead of throwing an exception so that other scaleio data can be displayed
                return null;
            }

            if(historicalPerformanceMetrics != null && CollectionUtils.isNotEmpty(historicalPerformanceMetrics.getTotalPerformances())) {
                List<UIUsageDataPoint> totalReadPerformancesUI = new ArrayList<>();
                for(PerformanceData readPerformanceData : historicalPerformanceMetrics.getTotalReadPerformances()) {
                    UIUsageDataPoint dp = new UIUsageDataPoint();
                    dp.timestamp = readPerformanceData.getTimestamp();
                    dp.value = NumberUtils.toDouble(readPerformanceData.getValue(), 0.0);

                    totalReadPerformancesUI.add(dp);
                }
                read.data = totalReadPerformancesUI;

                List<UIUsageDataPoint> totalWritePerformancesUI = new ArrayList<>();
                for(PerformanceData writePerformanceData : historicalPerformanceMetrics.getTotalWritePerformances()) {
                    UIUsageDataPoint dp = new UIUsageDataPoint();
                    dp.timestamp = writePerformanceData.getTimestamp();
                    dp.value = NumberUtils.toDouble(writePerformanceData.getValue(), 0.0);

                    totalWritePerformancesUI.add(dp);
                }
                write.data = totalWritePerformancesUI;

                List<UIUsageDataPoint> totalPerformancesUI = new ArrayList<>();
                for(PerformanceData totalPerformanceData : historicalPerformanceMetrics.getTotalPerformances()) {
                    UIUsageDataPoint dp = new UIUsageDataPoint();
                    dp.timestamp = totalPerformanceData.getTimestamp();
                    dp.value = NumberUtils.toDouble(totalPerformanceData.getValue(), 0.0);

                    totalPerformancesUI.add(dp);
                }
                total.data = totalPerformancesUI;

                String[] labels = {timeFrameIdentifiers[i], timeFrames[i], timeFrameLabels[i]};

                read = setSeriesAttributes(read, labels);
                write = setSeriesAttributes(write, labels);
                total = setSeriesAttributes(total, labels);
            }
            else{
                log.error("requestScaleIOUsageData() - no historical data found");
                return null;
            }

            usageData[0].historicaldata.add(read);
            usageData[1].historicaldata.add(write);
            usageData[2].historicaldata.add(total);
        }

        for(int i = 0; i < usageData.length; i++){
            usageData[i] = setUsageDataAttributes(usageData[i], metricType);
        }

        return usageData;
    }

    private UIUsageData setUsageDataAttributes(UIUsageData usageData, String type){
        usageData.currentvaluelabel = "Value";
        List<UIUsageDataSeries> uiUsageDataSeries = usageData.historicaldata;
        if(!CollectionUtils.isEmpty(uiUsageDataSeries)) {
            List<UIUsageDataPoint> dataPoints = uiUsageDataSeries.get(uiUsageDataSeries.size() - 1).data;
            if(!CollectionUtils.isEmpty(dataPoints)) {
                usageData.currentvalue = dataPoints.get(dataPoints.size() - 1).value;
                usageData.starttime = dataPoints.get(0).timestamp;
            }
        }

        if(type.equals("iops")) {
            usageData.category = "IOPS";
            usageData.id = "IOPS";
        }
        else {
            usageData.category = "KB/s";
            usageData.id = "KB/s";
        }

        usageData = setPeak(usageData.historicaldata, usageData);
        usageData.threshold = usageData.peakvalue * 1.05;

        return usageData;
    }


    private UIUsageDataSeries setSeriesAttributes(UIUsageDataSeries dataSeries, String[] labels){
        dataSeries.timeframelabel = labels[0];
        dataSeries.timeframe = labels[1];
        dataSeries.chartlabel = labels[2];

        double[] minMax = minMaxAvg(dataSeries.data);

        dataSeries.averagevalue = minMax[0];
        dataSeries.minimumvalue = minMax[1];
        dataSeries.maximumvalue = minMax[2];

        return dataSeries;
    }

    //sets the peak time and peak value of the Usage Data object
    private static UIUsageData setPeak(List<UIUsageDataSeries> data, UIUsageData outputObj){
        double max = 0.0;
        int index = 0;

        outputObj.peakvalue = 1.0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        outputObj.peaktime = df.format(new Date());

        if(!CollectionUtils.isEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                data.get(i).id = "performance_data";

                if (data.get(i).maximumvalue > max) {
                    max = data.get(i).maximumvalue;
                    index = i;
                }
            }

            List<UIUsageDataPoint> peakList = data.get(index).data;
            for (UIUsageDataPoint x : peakList) {
                if (x.value != null && x.value == max) {
                    outputObj.peaktime = x.timestamp;
                    outputObj.peakvalue = x.value;
                }
            }
        }
        log.debug("output max value is - " + outputObj.peakvalue);
        return outputObj;
    }

    //gets the min, max, and average values in UIUsageData
    private static double[] minMaxAvg(List<UIUsageDataPoint> points){
        double avg = 0.0;
        double min = 10000000.0;
        double max = 0.0;
        int pointsSize = 0;

        if(!CollectionUtils.isEmpty(points)) {
            pointsSize = points.size();

            for(UIUsageDataPoint x : points) {
                //check if value is infinity with arbitrarily large number (it gets reported as 9.22x10^16)
                if (x.value > 10000000000.0) {
                    x.value = null;
                } else {
                    avg += x.value;
                    if (x.value < min) {
                        min = x.value;
                    }
                    if (x.value > max) {
                        max = x.value;
                    }
                }
            }
        }

        return new double[]{ (avg / pointsSize), min, max };
    }

}

