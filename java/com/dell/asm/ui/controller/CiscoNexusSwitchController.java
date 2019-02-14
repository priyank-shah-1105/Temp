/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.deviceinventory.CompliantState;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.asmcore.asmmanager.client.puppetdevice.PuppetDevice;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.PuppetDeviceServiceAdapter;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.device.UICiscoSwitch;
import com.dell.asm.ui.model.device.UIDevice;


/**
 * DellSwitchController Controller.
 *
 * @author <a href="mailto:Santosh_kumar13@dell.com">Santosh Kumar</a>
 *
 * Jan 15, 2014 8:54:56 PM
 */
@RestController
@RequestMapping(value = "/devices/")
public class CiscoNexusSwitchController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(DellSwitchController.class);

    private PuppetDeviceServiceAdapter puppetDeviceServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;

    @Autowired
    public CiscoNexusSwitchController(PuppetDeviceServiceAdapter puppetDeviceServiceAdapter,
                                      DeviceInventoryServiceAdapter deviceInventoryServiceAdapter) {
        this.puppetDeviceServiceAdapter = puppetDeviceServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
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
    @RequestMapping(value = "getciscoswitchbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getCiscoSwitchById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            UICiscoSwitch device = new UICiscoSwitch();
            if (request.requestObj != null && request.requestObj.id != null) {
                ManagedDevice dto = deviceInventoryServiceAdapter.getDeviceInventory(
                        request.requestObj.id);

                PuppetDevice puppetDevice = puppetDeviceServiceAdapter.getPuppetDevice(
                        request.requestObj.id);

                device.id = request.requestObj.id;
                device.deviceType = DeviceType.ciscoswitch.name();
                device.model = dto.getModel();
                device.ipaddress = dto.getIpAddress();
                device.ipaddressurl = ""; //https://" + device.ipaddress + "/admin";
                device.serviceTag = dto.getServiceTag();
                device.macaddress = (String) puppetDevice.getData().get("macaddress");
                device.state = (String) puppetDevice.getData().get("status");
                String str = (String) puppetDevice.getData().get("powerstate");
                if (str != null) {
                    if (str.equalsIgnoreCase("Ok"))
                        device.powerstate = "On";
                    else
                        device.powerstate = "Off";
                } else {
                    device.powerstate = "unknown";
                }
                device.hostname = (String) puppetDevice.getData().get("hostname");
                device.firmwareversion = (String) puppetDevice.getData().get("nxosversion");
                device.firmwarename = DeviceController.getComplianceReportName(dto);
                device.serialnumber = dto.getServiceTag();
                device.systemdescription = puppetDevice.getData().get("manufacturer") + " " + device.model;
                if (dto.getHealth() != null) {
                    device.health = dto.getHealth().getLabel();
                }
                if (dto.getFirmwareName() != null &&
                        dto.getDeviceType() != null &&
                        dto.getDeviceType().isFirmwareComplianceManaged() &&
                        dto.getCompliance() != null) {
                    device.compliant = DeviceController.mapToUICompliance(dto.getCompliance(), dto.getState());
                } else {
                    device.compliant = CompliantState.UNKNOWN.getLabel();
                }
                device.availability = DeviceController.mapToUIAvailability(dto.isInUse(), dto.getState());
            }

            jobResponse.responseObj = device;
        } catch (Throwable t) {
            log.error("getCiscoSwitchById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }
}
