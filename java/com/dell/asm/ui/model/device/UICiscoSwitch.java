/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.device;

import java.util.List;
import java.util.Objects;

import com.dell.asm.ui.model.UIActivityLog;
import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UICiscoSwitch extends UIBaseObject {

    public String id;
    public String deviceType;
    public String model;
    public String ipaddress;
    public String ipaddressurl;
    public String serviceTag;
    public String state;
    public String powerstate;
    public String hostname;
    public String macaddress;
    public String assettag;
    public String firmwareversion;
    public String firmwarename;
    public String softwareversion;
    public String systemdescription;
    public String serialnumber;
    public String health;
    public String compliant;
    public String availability;
    public List<UIActivityLog> activityLogs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UICiscoSwitch)) return false;
        UICiscoSwitch that = (UICiscoSwitch) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UICiscoSwitch{" +
                "id='" + id + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", model='" + model + '\'' +
                ", ipaddress='" + ipaddress + '\'' +
                ", ipaddressurl='" + ipaddressurl + '\'' +
                ", serviceTag='" + serviceTag + '\'' +
                ", state='" + state + '\'' +
                ", powerstate='" + powerstate + '\'' +
                ", hostname='" + hostname + '\'' +
                ", macaddress='" + macaddress + '\'' +
                ", assettag='" + assettag + '\'' +
                ", firmwareversion='" + firmwareversion + '\'' +
                ", firmwarename='" + firmwarename + '\'' +
                ", softwareversion='" + softwareversion + '\'' +
                ", systemdescription='" + systemdescription + '\'' +
                ", serialnumber='" + serialnumber + '\'' +
                ", health='" + health + '\'' +
                ", compliant='" + compliant + '\'' +
                ", availability='" + availability + '\'' +
                ", activityLogs=" + activityLogs +
                '}';
    }
}
