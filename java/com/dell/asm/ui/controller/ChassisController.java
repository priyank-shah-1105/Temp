/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.credential.AsmCredentialDTO;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.DeviceState;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedState;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.i18n2.exception.AsmRuntimeException;
import com.dell.asm.localizablelogger.LocalizedLogMessage;
import com.dell.asm.ui.ASMUIMessages;
import com.dell.asm.ui.adapter.service.ChassisServiceAdapter;
import com.dell.asm.ui.adapter.service.CredentialServiceAdapter;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.LogServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.ServerServiceAdapter;
import com.dell.asm.ui.model.FabricNameTypes;
import com.dell.asm.ui.model.FabricPurposeTypes;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringsRequest;
import com.dell.asm.ui.model.chassis.JobChassisSetupRequest;
import com.dell.asm.ui.model.chassis.UIChassis;
import com.dell.asm.ui.model.chassis.UIChassisFirmware;
import com.dell.asm.ui.model.chassis.UIChassisSetup;
import com.dell.asm.ui.model.chassis.UIChassisSetupFabric;
import com.dell.asm.ui.model.chassis.UIChassisSummary;
import com.dell.asm.ui.model.chassis.UIPowerSupply;
import com.dell.asm.ui.model.chassis.UISetupChassisConfig;
import com.dell.asm.ui.model.chassis.UISetupIOMConfig;
import com.dell.asm.ui.model.chassis.UISetupServerConfig;
import com.dell.asm.ui.model.iom.UIIOMSummary;
import com.dell.asm.ui.model.server.UIServer;
import com.dell.asm.ui.model.server.UIServerSummary;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.asm.chassis.client.device.Chassis;
import com.dell.pg.asm.chassis.client.device.IOM;
import com.dell.pg.asm.chassis.client.device.KVM;
import com.dell.pg.asm.chassis.client.device.PowerSupply;
import com.dell.pg.asm.chassis.client.device.Server;

/**
 * Chassis Controller.
 */
@RestController
@RequestMapping(value = "/chassis/")
public class ChassisController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(ChassisController.class);

    private CredentialServiceAdapter credentialServiceAdapter;
    private ChassisServiceAdapter chassisServiceAdapter;
    private ServerServiceAdapter serverServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private LogServiceAdapter logServiceAdapter;
    private DeploymentServiceAdapter serviceAdapter;

    @Autowired
    public ChassisController(CredentialServiceAdapter credentialServiceAdapter,
                             ChassisServiceAdapter chassisServiceAdapter,
                             ServerServiceAdapter serverServiceAdapter,
                             DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                             LogServiceAdapter logServiceAdapter,
                             DeploymentServiceAdapter serviceAdapter) {
        this.credentialServiceAdapter = credentialServiceAdapter;
        this.chassisServiceAdapter = chassisServiceAdapter;
        this.serverServiceAdapter = serverServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.logServiceAdapter = logServiceAdapter;
        this.serviceAdapter = serviceAdapter;
    }

    /**
     * Parse PowerSupply into UIPowerSupply
     *
     * @param inventory
     * @param applicationContext
     * @return
     */
    public static UIPowerSupply parsePowerSupply(PowerSupply inventory,
                                                 ApplicationContext applicationContext) {
        UIPowerSupply summary = new UIPowerSupply();
        if (inventory.getId() != null) {
            summary.id = inventory.getId();
        }
        if (inventory.getCapacity() != null) {
            summary.capacity = inventory.getCapacity();
        }
        if (inventory.getPowerStatus() != null) {
            summary.powerstatus = inventory.getPowerStatus();
        }
        if (inventory.getSlot() != null) {
            //summary.slot = inventory.getSlot();
            summary.name = "Power Supply " + inventory.getSlot();
        }

        return summary;
    }

    /**
     * Parse IOM into UIIOMSummary
     *
     * @param inventory
     * @param applicationContext
     * @return
     */
    public static UIIOMSummary parseIOM(IOM inventory, ApplicationContext applicationContext) {
        UIIOMSummary summary = new UIIOMSummary();
        if (inventory.getId() != null) {
            summary.id = inventory.getId();
        }
        summary.ipStatic = inventory.isManagementIPStatic();

        if (inventory.getManagementIP() != null) {
            summary.ipaddress = inventory.getManagementIP();
        }
        if (inventory.getServiceTag() != null) {
            summary.servicetag = inventory.getServiceTag();
        }
        if (StringUtils.isNotEmpty(summary.servicetag) && summary.servicetag.contains(
                "INVALID_SVCTAG")) {
            summary.servicetag = "";
        }
        if (inventory.getHealth() != null) {
            summary.health = applicationContext.getMessage(
                    "deviceHealth." + inventory.getHealth().value(), null,
                    LocaleContextHolder.getLocale());
        }
        if (inventory.getModel() != null) {
            summary.systemmodel = inventory.getModel();
        }
        if (inventory.getLocation() != null) {
            summary.slot = inventory.getLocation().toString();
        }

        return summary;
    }

    /**
     * Parse Server into UIServerSummary
     *
     * @param inventory
     * @param applicationContext
     * @return
     */
    public static UIServerSummary parseServer(UIServer inventory,
                                              ApplicationContext applicationContext) {

        UIServerSummary summary = new UIServerSummary();
        if (inventory.id != null) {
            summary.id = inventory.id;
        }
        // summary.ipStatic = inventory.ipaddress.isManagementIPStatic();
        if (inventory.ipaddress != null) {
            summary.ipaddress = inventory.ipaddress;
        }
        if (inventory.servicetag != null) {
            summary.servicetag = inventory.servicetag;
        }
        if (inventory.health != null) {
            summary.health = inventory.health;
        }
        if (inventory.systemmodel != null) {
            summary.systemmodel = inventory.systemmodel;
        }
        if (inventory.slotnumber != null) {
            summary.slotnumber = inventory.slotnumber;
        }
        if (inventory.cpus != null)
            summary.cpus = inventory.cpus;

        if (inventory.memory != null)
            summary.memory = inventory.memory;

        if (inventory.operationalstatus != null)
            summary.slotnumber = inventory.operationalstatus;

        summary.powerstate = inventory.serverpowerstate;

        return summary;
    }

    /**
     * Get chassis summaries for credential.
     *
     * @param request
     *            Credential ID
     * @return new credential
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getchassissummariesbycredentialid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getChassisSummariesByCredentialId(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        List<UIChassisSummary> list = new ArrayList<>();

        try {
            if (request.requestObj != null && request.requestObj.id != null) {

                List<String> filter = new ArrayList<>();
                filter.add("eq,credentialRefId," + request.requestObj.id);
                ResourceList<Chassis> cList = chassisServiceAdapter.getChassises(null, filter, null,
                                                                                 null);
                if (cList != null && cList.getList() != null) {
                    for (Chassis cDev : cList.getList()) {
                        if (request.requestObj.id.equals(cDev.getCredentialRefId())) {
                            UIChassisSummary uic = parseChassisDeviceForSummary(cDev,
                                                                                getApplicationContext());
                            list.add(uic);
                        }
                    }
                }
            }
            jobResponse.responseObj = list;
        } catch (Throwable t) {
            log.error("createCredential() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Get chassis from RA by id..
     *
     * @param request
     *            chassis ID
     * @return chassis
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getchassisbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getChassisById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            UIChassis chassis = parseChassisDevice(
                    chassisServiceAdapter.getChassis(request.requestObj.id),
                    getApplicationContext(), true);

            List<String> filter = new ArrayList<>();
            filter.add("co,marshalledLocalizableMessage," + chassis.servicetag);

            ResourceList<LocalizedLogMessage> mList = logServiceAdapter.getUserLogMessages(
                    "-timeStamp", filter, 0, 5);
            if (mList != null && mList.getTotalRecords() > 0) {
                for (LocalizedLogMessage msg : mList.getList()) {
                    chassis.activityLogs.add(
                            MappingUtils.parseLogEntry(msg, getApplicationContext()));
                }
            }

            jobResponse.responseObj = chassis;
        } catch (Throwable t) {
            log.error("getChassisById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Get chassis from RA by id. NOT Used at this time 12/8/2014 ASM 8.1
     *
     * @param request
     *            array of chassis ID
     * @return chassis
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getchassissetupbychassisid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getChassisSetupById(@RequestBody JobStringsRequest request) {

        JobResponse jobResponse = new JobResponse();

        UIChassisSetup responseObj = new UIChassisSetup();

        try {
            if (request.requestObj.size() == 0) {
                List<String> filter = new ArrayList<>();
                filter.add("eq,state," + DeviceState.READY.getValue());
                filter.add("eq,managedState," + ManagedState.MANAGED.getValue());
                ResourceList<ManagedDevice> mList = deviceInventoryServiceAdapter.getAllDeviceInventory(
                        null, filter, 0, MappingUtils.MAX_RECORDS);
                if (mList != null && mList.getTotalRecords() > 0) {
                    for (ManagedDevice mDev : mList.getList()) {
                        addDeviceToResponse(responseObj, mDev);
                    }
                }
            }
            for (String id : request.requestObj) {
                ManagedDevice mDev = deviceInventoryServiceAdapter.getDeviceInventory(id);
                addDeviceToResponse(responseObj, mDev);
            }

            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("getChassisById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private void addDeviceToResponse(UIChassisSetup responseObj, ManagedDevice mDev) {
        if (DeviceType.isChassis(mDev.getDeviceType())) {

            UIChassis chassis = parseChassisDevice(
                    chassisServiceAdapter.getChassis(mDev.getRefId()), getApplicationContext(),
                    false);

            UISetupChassisConfig chassisConfig = getSetupChassisConfig(chassis);
            responseObj.chassislist.add(chassisConfig);
            responseObj.chassissetupfabrics = setupDefaultFabrics(); // TODO: fabrics from wiring scheme
            responseObj.firmwarelist = setupFirmwareList(); // TODO: pass versions from service
        } else if (mDev.getDeviceType().equals(
                DeviceType.RackServer) || mDev.getDeviceType().equals(DeviceType.TowerServer)) {

            UIServer server = MappingUtils.parseServerDevice(
                    serverServiceAdapter.getServer(mDev.getRefId()), null, getApplicationContext(),
                    credentialServiceAdapter,
                    serviceAdapter, false);
            UISetupChassisConfig chassisConfig = getSetupChassisConfig(server);
            responseObj.chassislist.add(chassisConfig);
            responseObj.chassissetupfabrics = setupDefaultFabrics();
            responseObj.firmwarelist = setupFirmwareList(); // TODO: pass versions from service
        }
    }

    /**
     * Stub for future code.
     *
     * @param request
     *            the request
     * @return the template details
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "validatechassisidentities", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse validateChassisIdentities(@RequestBody JobChassisSetupRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            for (UISetupChassisConfig config : request.requestObj.chassislist) {
                if (!config.selected) continue;

                if (StringUtils.isNotEmpty(config.slot)) {
                    boolean bSlotIsValid = false;
                    int iSlot = Integer.parseInt(config.slot);
                    if (iSlot > 0 && iSlot < 100) {
                        bSlotIsValid = true;
                    }
                    if (!bSlotIsValid) {
                        log.error(
                                "validateChassisIdentities() - rack slot " + config.slot + " is invalid.");
                        throw new AsmRuntimeException("Rack Slot is invalid",
                                                      ASMUIMessages.invalidChassisSlot(config));
                    }
                }
                if (config.powercapMeasurementType != null &&
                        config.powercap != null &&
                        config.defaultPowerCapLowerBoundBTU != 0 &&
                        config.defaultPowerCapUpperBoundBTU != 0 &&
                        config.defaultPowerCapLowerBoundWatts != 0 &&
                        config.defaultPowerCapUpperBoundWatts != 0) {
                    int iMinumumPercentage = (int) (config.defaultPowerCapLowerBoundWatts * 100 / config.defaultPowerCapUpperBoundWatts);
                    int iPowerCap = Integer.parseInt(config.powercap);

                    if (config.powercapMeasurementType.compareToIgnoreCase("percentage") == 0) {
                        if ((iPowerCap < iMinumumPercentage) || (iPowerCap > 100)) {
                            log.error("validateChassisIdentities() -  Power cap " + config.powercap
                                              + " " + config.powercapMeasurementType + " is invalid.");
                            throw new AsmRuntimeException("Power Cap is invalid.",
                                                          ASMUIMessages.invalidPowerCapRange(config,
                                                                                             iMinumumPercentage,
                                                                                             100,
                                                                                             "%"));
                        }
                    }
                    if (config.powercapMeasurementType.compareToIgnoreCase("watts") == 0) {
                        if ((iPowerCap < config.defaultPowerCapLowerBoundWatts)
                                || (iPowerCap > config.defaultPowerCapUpperBoundWatts)) {
                            log.error("validateChassisIdentities() -  Power cap " + config.powercap
                                              + " " + config.powercapMeasurementType + " is invalid.");
                            throw new AsmRuntimeException("Power Cap is invalid.",
                                                          ASMUIMessages.invalidPowerCapRange(config,
                                                                                             config.defaultPowerCapLowerBoundWatts,
                                                                                             config.defaultPowerCapUpperBoundWatts,
                                                                                             "Watts"));
                        }
                    }
                    if (config.powercapMeasurementType.compareToIgnoreCase("btuh") == 0) {
                        if ((iPowerCap < config.defaultPowerCapLowerBoundBTU)
                                || (iPowerCap > config.defaultPowerCapUpperBoundBTU)) {
                            log.error("validateChassisIdentities() -  Power cap " + config.powercap
                                              + " " + config.powercapMeasurementType + " is invalid.");
                            throw new AsmRuntimeException("Power Cap is invalid.",
                                                          ASMUIMessages.invalidPowerCapRange(config,
                                                                                             config.defaultPowerCapLowerBoundBTU,
                                                                                             config.defaultPowerCapUpperBoundBTU,
                                                                                             "BTU/h"));
                        }
                    }
                }
            }
        } catch (Throwable t) {
            log.error("validateChassisIdentities() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Stub for future code.
     *
     * @param request
     *            the request
     * @return the template details
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "validateiomidentities", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse validateIomIdentities(@RequestBody JobChassisSetupRequest request) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseCode = 0;
        /*try
        {
            for (UISetupChassisConfig config : request.requestObj.chassislist)
            {
            }
        }
        catch (Throwable t) {
            log.error("validateChassisIdentities() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }*/
        return jobResponse;
    }

    /**
     * Stub for future code.
     *
     * @param request
     *            the request
     * @return the template details
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "validateserveridentities", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse validateServerIdentities(@RequestBody JobChassisSetupRequest request) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseCode = 0;

        /*try
        {
            for (UISetupChassisConfig config : request.requestObj.chassislist)
            {
            }
        }
        catch (Throwable t) {
            log.error("validateChassisIdentities() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }*/
        return jobResponse;
    }

    /************** helpers **************/

    private UISetupChassisConfig getSetupChassisConfig(UIChassis inventory) {
        if (inventory == null) return null;

        UISetupChassisConfig summary = new UISetupChassisConfig();
        summary.devicetype = DeviceType.ChassisM1000e.getValue();
        summary.id = inventory.id;
        summary.ipaddress = inventory.ipaddress;
        summary.servicetag = inventory.servicetag;
        //summary.activesystemaisle;
        //summary.activesystemdatacenter;
        summary.activesystemname = inventory.activesystemname;
        //summary.activesystemrack;
        summary.aisle = inventory.aisle;
        summary.assignedipaddress = inventory.ipaddress; // dup?
        summary.datacenter = inventory.datacenter;
        summary.dnsname = inventory.chassisdnsname;
        summary.model = inventory.chassismodel;
        summary.name = inventory.chassisname;
        summary.powercap = inventory.powercap;
        summary.powercapMeasurementType = "percentage"; // always a percent when getting from inventory.
        summary.rack = inventory.rack;
        summary.slot = inventory.rackslot;
        summary.selected = true;
        summary.defaultPowerCapLowerBoundBTU = inventory.defaultPowerCapLowerBoundBTU;
        summary.defaultPowerCapUpperBoundBTU = inventory.defaultPowerCapUpperBoundBTU;
        summary.defaultPowerCapLowerBoundWatts = inventory.defaultPowerCapLowerBoundWatts;
        summary.defaultPowerCapUpperBoundWatts = inventory.defaultPowerCapUpperBoundWatts;

        if (inventory.ioms != null) {
            for (UIIOMSummary iom : inventory.ioms) {
                UISetupIOMConfig ioc = new UISetupIOMConfig();
                ioc.id = iom.id;
                ioc.assignedipaddress = iom.ipaddress;
                ioc.ipaddress = iom.ipaddress;
                ioc.ipStatic = iom.ipStatic;
                ioc.servicetag = iom.servicetag;
                ioc.slot = iom.slot;
                summary.iomlist.add(ioc);
            }
        }

        if (inventory.blades != null) {
            for (UIServerSummary ss : inventory.blades) {
                UISetupServerConfig sc = new UISetupServerConfig();
                sc.id = ss.id;
                sc.ipaddress = ss.ipaddress;
                sc.assignedipaddress = ss.ipaddress;
                sc.servicetag = ss.servicetag;
                sc.slot = ss.slotnumber;
                sc.ipStatic = ss.ipStatic;
                summary.serverlist.add(sc);
            }
        }


        return summary;
    }

    /**
     * Setup default fabrics.
     *
     * @return the list
     */
    private List<UIChassisSetupFabric> setupDefaultFabrics() {
        Map<String, UIChassisSetupFabric> fabrics = new LinkedHashMap<>();
        for (FabricNameTypes type : FabricNameTypes.values()) {
            if (type != null) {
                UIChassisSetupFabric fabric = new UIChassisSetupFabric();
                fabric.id = type.value();
                fabric.name = MappingUtils.getFabricNameType(type, getApplicationContext());
                fabric.purpose = FabricPurposeTypes.NONE.value();
                fabrics.put(fabric.id, fabric);
            }
        }
        return new ArrayList<>(fabrics.values());
    }

    /**
     * Setup firmware list.
     * param versions List<BaselineFirmwareVersion> versions
     *            the versions
     * @return the list
     */
    private List<UIChassisFirmware> setupFirmwareList() {
        List<UIChassisFirmware> firmwares = new ArrayList<>();
        /*
        if ((versions != null) && !versions.isEmpty()) {
            for (BaselineFirmwareVersion version : versions) {
                UIChassisFirmware firmware = new UIChassisFirmware();
                if (version.getModel() != null) {
                    firmware.model = version.getModel();
                }
                if (version.getVersion() != null) {
                    firmware.version = version.getVersion();
                }
                if (version.getType() != null) {
                    firmware.component = MappingUtils.getBaselineFirmwareTypeName(version.getType(), context);
                }
                firmwares.add(firmware);
            }
        }
        */
        return firmwares;
    }

    /**
     * Gets the percentage from watts.
     *
     * @param watts
     *            the watts
     * @return the percentage from watts
     */
    private Integer getPercentageFromWatts(double watts) {
        int percent = 100;
        if ((watts < 16685) && (watts > 2715)) {
            percent = (int) (watts * 100) / 16685;
        } else if (watts <= 2715) {
            percent = 16;
        }
        return percent;
    }

    private UISetupChassisConfig getSetupChassisConfig(UIServer inventory) {
        if (inventory == null) return null;

        UISetupChassisConfig summary = new UISetupChassisConfig();
        summary.devicetype = DeviceType.RackServer.getValue();
        summary.id = inventory.id;
        summary.ipaddress = inventory.ipaddress;
        summary.servicetag = inventory.servicetag;
        summary.dnsname = inventory.dnsIdracName;
        summary.model = inventory.systemmodel;
        summary.selected = true;
        return summary;
    }

    /**
     * Parse ChassisDevice into UIChassisSummary class.
     *
     * @param result
     * @param applicationContext
     * @return
     */
    public UIChassisSummary parseChassisDeviceForSummary(Chassis result,
                                                         ApplicationContext applicationContext) {
        UIChassisSummary res = new UIChassisSummary();
        UIChassis chs = parseChassisDevice(result, applicationContext, false);

        res.health = chs.health;
        res.id = chs.id;
        res.ipaddress = chs.ipaddress;
        res.lasttemplateapplied = chs.lasttemplateapplied;
        res.numberofblades = chs.numberofblades;
        res.servicetag = chs.servicetag;
        res.state = chs.state;

        return res;
    }

    /**
     * Parse ChassisDevice into UI class.
     *
     * @param result
     * @param applicationContext
     * @return
     */
    public UIChassis parseChassisDevice(Chassis result, ApplicationContext applicationContext,
                                        boolean bTranslateCredentials) {
        UIChassis responseObj = new UIChassis();

        if (result.getRefId() != null) {
            responseObj.id = result.getRefId();
        }
        if (result.getHealth() != null) {
            responseObj.health = applicationContext.getMessage(
                    "deviceHealth." + result.getHealth().value(), null,
                    LocaleContextHolder.getLocale());
            responseObj.hardwarehealth = responseObj.health;
        }
        if (result.getManagementIP() != null) {
            responseObj.ipaddress = result.getManagementIP();
            responseObj.ipaddressurl = MappingUtils.isReadOnlyUser() ? "" : "https://" + responseObj.ipaddress;
        }
        if (result.getServiceTag() != null) {
            responseObj.servicetag = result.getServiceTag();
            responseObj.chassisservicetag = result.getServiceTag();
        }
        if (result.getName() != null) {
            responseObj.chassisname = result.getName();
        }
        if (result.getDatacenter() != null) {
            responseObj.datacenter = result.getDatacenter();
        }
        if (result.getRack() != null) {
            responseObj.rack = result.getRack();
        }
        if (result.getRackslot() != null) {
            responseObj.rackslot = result.getRackslot();
        }
        if (result.getAisle() != null) {
            responseObj.aisle = result.getAisle();
        }
        if (result.getMidPlaneVersion() != null) {
            responseObj.chassismidplaneversion = result.getMidPlaneVersion();
        }
        responseObj.powercap = "" + result.getPowerCapPercent();

        if (result.getCredentialRefId() != null && bTranslateCredentials) {
            // replace cred ID with name
            AsmCredentialDTO dto = credentialServiceAdapter.getCredential(
                    result.getCredentialRefId());
            if (dto != null && dto.getCredential() != null) {
                responseObj.associatedcredential = dto.getCredential().getLabel();
            }
        }

        if (result.getDnsName() != null) {
            responseObj.chassisdnsname = result.getDnsName();
        }
        if (result.getModel() != null) {
            responseObj.chassismodel = result.getModel();
        }
        if (result.getAssetTag() != null) {
            responseObj.assettag = result.getAssetTag();
        }
        if ((result.getServers() != null) && !result.getServers().isEmpty()) {
            com.dell.pg.asm.server.client.device.Server bladeServerDevice;
            responseObj.numberofblades = result.getServers().size();
            responseObj.blades = new ArrayList<>();

            for (Server inventory : result.getServers()) {
                if (inventory.getServiceTag() != null) {
                    List<String> serviceTag = new ArrayList<>();
                    serviceTag.add("eq,serviceTag," + inventory.getServiceTag());
                    com.dell.pg.asm.server.client.device.Server[] dList = serverServiceAdapter.getServers(
                            null, serviceTag, null, null);
                    if (dList != null && dList.length > 0) {
                        bladeServerDevice = dList[0];

                        UIServer s = MappingUtils.parseServerDevice(bladeServerDevice, null,
                                                                    applicationContext,
                                                                    credentialServiceAdapter,
                                                                    serviceAdapter, false);
                        UIServerSummary sSum = parseServer(s, applicationContext);
                        sSum.state = s.health;
                        sSum.hostname = s.hostname;
                        sSum.location = inventory.getSlot();

                        responseObj.blades.add(sSum);

                    }

                }
            }


        } else {
            responseObj.numberofblades = 0;
        }

        if ((result.getIOMs() != null) && !result.getIOMs().isEmpty()) {
            responseObj.ioms = new ArrayList<>();
            for (IOM iom : result.getIOMs()) {
                responseObj.ioms.add(parseIOM(iom, applicationContext));
            }
        }
        if ((result.getControllers() != null) && !result.getControllers().isEmpty()) {
            for (com.dell.pg.asm.chassis.client.device.Controller inventory : result.getControllers()) {
                if (inventory.isControllerPrimary()) {
                    if (inventory.getControllerName() != null) {
                        responseObj.primarychassiscontrollername = inventory.getControllerName();
                    }
                    if (inventory.getControllerFWVersion() != null) {
                        responseObj.primarychassiscontrollerfirmwareversion = inventory.getControllerFWVersion();
                    }
                } else {
                    if (inventory.getControllerName() != null) {
                        responseObj.standbychassiscontrollerpresent = (inventory.isControllerPresent() ? "Yes" : "No");
                    }
                    if (inventory.getControllerFWVersion() != null) {
                        responseObj.standbychassiscontrollerfirmwareversion = inventory.getControllerFWVersion();
                    }
                }
            }
        }
        if (result.getKVMs() != null && !result.getKVMs().isEmpty()) {
            for (KVM kvm : result.getKVMs()) {
                responseObj.ikvmpresent = applicationContext.getMessage("yes", null,
                                                                        LocaleContextHolder.getLocale());
                if (kvm.getName() != null) {
                    responseObj.ikvmname = kvm.getName();
                }
                if (kvm.getManufacturer() != null) {
                    responseObj.ikvmmanufacturer = kvm.getManufacturer();
                }
                if (kvm.getPartNumber() != null) {
                    responseObj.ikvmpartnumber = kvm.getPartNumber();
                }
                if (kvm.getFirmwareVersion() != null) {
                    responseObj.ikvmfirmwareversion = kvm.getFirmwareVersion();
                }
            }
        } else {
            responseObj.ikvmpresent = applicationContext.getMessage("no", null,
                                                                    LocaleContextHolder.getLocale());
        }

        if ((result.getPowerSupplies() != null) && !result.getPowerSupplies().isEmpty()) {
            responseObj.powersupplies = new ArrayList<>();
            for (PowerSupply pws : result.getPowerSupplies()) {
                responseObj.powersupplies.add(parsePowerSupply(pws, applicationContext));
            }
        }

        responseObj.defaultPowerCapUpperBoundWatts = result.getDefaultPowerCapUpperBoundWatts();
        responseObj.defaultPowerCapLowerBoundWatts = result.getDefaultPowerCapLowerBoundWatts();
        responseObj.defaultPowerCapUpperBoundBTU = result.getDefaultPowerCapUpperBoundBTU();
        responseObj.defaultPowerCapLowerBoundBTU = result.getDefaultPowerCapLowerBoundBTU();

        return responseObj;
    }

}
