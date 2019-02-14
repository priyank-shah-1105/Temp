/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dell.asm.ui.util.DeviceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoverDeviceType;
import com.dell.asm.asmcore.asmmanager.client.puppetdevice.PuppetDevice;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.PuppetDeviceServiceAdapter;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.configure.UIPortConfigurationSettings;
import com.dell.asm.ui.model.device.UIChassisSwitchDevice;
import com.dell.asm.ui.model.device.UIDellSwitchDevice;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DellSwitchController Controller.
 */
@RestController
public class DellSwitchController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(DellSwitchController.class);
    private PuppetDeviceServiceAdapter puppetDeviceServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;

    @Autowired
    public DellSwitchController(PuppetDeviceServiceAdapter puppetDeviceServiceAdapter,
                                DeviceInventoryServiceAdapter deviceInventoryServiceAdapter) {
        this.puppetDeviceServiceAdapter = puppetDeviceServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
    }

    public static UIDellSwitchDevice parseSwitch(PuppetDevice puppetDevice, ManagedDevice dto) {
        UIDellSwitchDevice device = new UIDellSwitchDevice();
        if (puppetDevice != null) {
            device = parseSwitch(puppetDevice.getName(), puppetDevice.getData(), dto);
        }
        return device;
    }

    /**
     * Parse facts from puppet service.
     *
     * @param name
     * @param data
     * @param dto
     * @return
     */
    public static UIDellSwitchDevice parseSwitch(String name, Map<String, Object> data,
                                                 ManagedDevice dto) {
        UIDellSwitchDevice device = new UIDellSwitchDevice();
        ObjectMapper mapper = new ObjectMapper();
        if (data != null) {
            try {

                device.id = name;
                device.deviceType = "dellswitch";

                if (name.contains("powerconnect")) {
                    // For powerConnect
                    device.ipaddress = (String) data.get("managementip");
                    device.ipaddressurl = "";
                    device.macaddress = (String) data.get("macaddress");
                    device.servicetag = dto.getServiceTag();
                    device.state = (String) data.get("switchstate");
                    String str = (String) data.get("powerstate");
                    if (str != null) {
                        if (str.equals("OK"))
                            device.powerstate = "On";
                        else
                            device.powerstate = "Off";
                    } else
                        device.powerstate = "unknown";
                    device.hostname = (String) data.get("hostname");
                    device.assettag = (String) data.get("assettag");
                    device.firmwareversion = (String) data.get("Active_Software_Version");
                    device.softwareversion = "N/A";
                    device.systemdescription = (String) data.get("system_description");
                    device.serialnumber = (String) data.get("serialnumber");
                    device.model = (String) data.get("model");

                } else if (name.contains("iom_8x4")) {
                    device.servicetag = dto.getServiceTag();
                    String ps = (String) data.get("Switch State");
                    if (ps != null) {
                        if (ps.trim().toLowerCase().equals("online")) {
                            device.powerstate = "On";
                        } else {
                            device.powerstate = "Off";
                        }
                    } else {
                        device.powerstate = "unknown";
                    }

                    device.hostname = (String) data.get("Switch Name");
                    device.serialnumber = (String) data.get("Factory Serial Number");
                    device.ipaddress = (String) data.get("Ethernet IP Address");
                    device.ipaddressurl = "";
                    device.firmwareversion = (String) data.get("Fabric Os");
                    device.softwareversion = (String) data.get("clientversion");
                    device.macaddress = (String) data.get("Switch Wwn");
                    device.fabric = (String) data.get("fabric_id");

                } else if (name.contains("dell_ftos") ||
                        name.contains("dell_iom")) {
                    // For Force10
                    device.ipaddress = (String) data.get("management_ip");
                    device.ipaddressurl = "";
                    device.macaddress = (String) data.get("stack_mac");
                    device.servicetag = dto.getServiceTag();
                    device.state = (String) data.get("system_management_unit_status");

                    String str = (String) data.get("system_power_status");
                    if (str != null) {
                        switch (str) {
                        case "up":
                            device.powerstate = "On";
                            break;
                        case "down":
                            device.powerstate = "Off";
                            break;
                        default:
                            device.powerstate = "unknown";
                            break;
                        }
                    } else {
                        device.powerstate = "unknown";
                    }

                    device.hostname = (String) data.get("hostname");
                    device.assettag = dto.getServiceTag();
                    device.firmwareversion = (String) data.get("dell_force10_application_software_version");
                    device.softwareversion = (String) data.get("dell_force10_operating_system_version");
                    device.systemdescription = (String) data.get("system_description");
                    device.serialnumber = (String) data.get("system_management_unit_serial_number");
                    device.fabric = (String) data.get("fabric_id");
                    device.model = (String) data.get("model");
                    device.slot = (String) data.get("fabric_id");
                    String iompowerstate = (String) data.get("system_power_status");
                    if (iompowerstate != null) {
                        device.iompowerstate = iompowerstate.toUpperCase();
                    }
                    device.switchhostname = (String) data.get("hostname");

                    String modules = (String) data.get("modules");
                    if (modules != null) {
                        device.modules = mapper.readValue(modules, List.class);
                    }

                    String quad_port_interfaces = (String) data.get("quad_port_interfaces");
                    if (quad_port_interfaces != null) {
                        device.quad_port_interfaces = mapper.readValue(quad_port_interfaces,
                                                                       List.class);
                    }

                    String flexio_modules = (String) data.get("flexio_modules");
                    if (flexio_modules != null && flexio_modules.length() > 0) {
                        Map<String, List<String>> parsedJson = mapper.readValue(flexio_modules,
                                                                                Map.class);
                        List<String> m1i = parsedJson.get("module1_interface");

                        UIPortConfigurationSettings port;
                        if (m1i != null) {
                            device.slot3Config = new ArrayList<>();
                            for (String s : m1i) {
                                if (s.contains("Fc")) {
                                    device.slot3FCModule = true;
                                }
                                port = new UIPortConfigurationSettings(s);
                                device.slot3Config.add(port);
                            }
                        }
                        List<String> m2i = parsedJson.get("module2_interface");
                        if (m2i != null) {
                            device.slot2Config = new ArrayList<>();
                            for (String s : m2i) {
                                if (s.contains("Fc")) {
                                    device.slot2FCModule = true;
                                }
                                port = new UIPortConfigurationSettings(s);
                                device.slot2Config.add(port);
                            }
                        }

                        List<String> m3i = parsedJson.get("module3_interface");
                        if (m3i != null) {
                            device.slot1Config = new ArrayList<>();
                            for (String s : m3i) {
                                if (s.contains("Fc")) {
                                    device.slot1FCModule = true;
                                }
                                port = new UIPortConfigurationSettings(s);
                                device.slot1Config.add(port);
                            }
                        }

                    }


                } else if (name.contains("brocade_fos")) {
                    device.servicetag = dto.getServiceTag();
                    String ps = (String) data.get("Switch State");
                    if (ps != null)
                        if (ps.trim().toLowerCase().equals("online"))
                            device.powerstate = "On";
                        else
                            device.powerstate = "Off";
                    else
                        device.powerstate = "unknown";

                    device.hostname = (String) data.get("Switch Name");
                    device.serialnumber = (String) data.get("Factory Serial Number");
                    device.ipaddress = (String) data.get("Ethernet IP Address");
                    device.ipaddressurl = "";
                    device.firmwareversion = (String) data.get("Fabric Os");
                    device.softwareversion = (String) data.get("clientversion");
                    device.macaddress = (String) data.get("Switch Wwn");
                }
                if (data.get("model") != null) {
                    device.deviceType = DeviceUtil.getSwitchDeviceType((String) data.get("model"));
                }
            } catch (Exception e) {
                log.error("Error parsing puppet device for Dell Switch", e);
            }
        }

        return device;
    }

    /**
     * Parse facts from puppet service for IOM/IOA/MXLs
     *
     * @param puppetDevice
     * @param dto
     * @return
     */
    public static UIDellSwitchDevice parseChassisSwitch(PuppetDevice puppetDevice,
                                                        ManagedDevice dto, ManagedDevice chassis) {
        UIChassisSwitchDevice device = new UIChassisSwitchDevice();
        try {
            // these two must be from chassis object
            if (chassis != null) {
                device.chassisipaddress = chassis.getIpAddress();
                device.chassisservicetag = chassis.getServiceTag();
            }

            String str = (String) puppetDevice.getData().get("system_power_status");
            if (str != null) {
                switch (str) {
                case "up":
                    device.iompowerstate = "On";
                    break;
                case "down":
                    device.iompowerstate = "Off";
                    break;
                default:
                    device.iompowerstate = "unknown";
                    break;
                }
            } else
                device.iompowerstate = "unknown";

            device.switchhostname = (String) puppetDevice.getData().get("hostname");
            device.assettag = dto.getServiceTag();
            device.servicetag = dto.getServiceTag();
            device.ipaddress = dto.getIpAddress();
            device.firmwareversion = (String) puppetDevice.getData().get(
                    "dell_force10_application_software_version");
            //device.model = (String) puppetDevice.getData().get("system_type");

            device.slot = (String) puppetDevice.getData().get("fabric_id");

            if (puppetDevice.getData().get("model") != null) {
                final String model = (String) puppetDevice.getData().get("model");
                if (model.toLowerCase().contains("aggregator")) {
                    device.deviceType = "AggregatorIOM";
                } else if (model.toLowerCase().contains("mxl")) {
                    device.deviceType = "MXLIOM";
                }
            }

        } catch (Exception e) {
            log.error("Error parsing puppet device for Dell Switch", e);
        }
        return device;
    }

    /**
     * Get swicth details.
     *
     * @param request
     *            ID
     * @return switch details
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "/devices/getdellswitchbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDeviceByID(@RequestBody JobIDRequest request) {
        return fetchDevice(request);
    }

    /**
     * Get iom switch details.
     *
     * @param request
     *            ID
     * @return switch details
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "/iom/getiombyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getIomDeviceByID(@RequestBody JobIDRequest request) {
        return fetchDevice(request);
    }

    public JobResponse fetchDevice(JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            UIDellSwitchDevice device = new UIDellSwitchDevice();
            if (request.requestObj != null && request.requestObj.id != null) {
                PuppetDevice puppetDevice = puppetDeviceServiceAdapter.getPuppetDevice(
                        request.requestObj.id);
                ManagedDevice dto = deviceInventoryServiceAdapter.getDeviceInventory(
                        request.requestObj.id);

                if (DiscoverDeviceType.isIOM(dto.getDiscoverDeviceType())) {
                    ManagedDevice chassis = null;
                    if (StringUtils.isNotEmpty(dto.getChassisId())) {
                        chassis = deviceInventoryServiceAdapter.getDeviceInventory(
                                dto.getChassisId());
                    }
                    device = parseChassisSwitch(puppetDevice, dto, chassis);
                } else {
                    device = parseSwitch(puppetDevice, dto);
                }
            }

            jobResponse.responseObj = device;
        } catch (Throwable t) {
            log.error("getDeviceByID() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }
}
