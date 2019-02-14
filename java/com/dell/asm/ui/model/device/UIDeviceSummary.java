/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.device;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDeviceSummary extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String health;

    @JsonProperty
    public String healthmessage;

    @JsonProperty
    public String ipAddress;

    @JsonProperty
    public String serviceTag;

    @JsonProperty
    public String deviceType;

    @JsonProperty
    public String compliant;

    @JsonProperty
    public String state;

    @JsonProperty
    public String model;

    @JsonProperty
    public String displayserverpools;

    @JsonProperty
    public String manufacturer;

    @JsonProperty
    public int processorcount;

    @JsonProperty
    public String processor;

    @JsonProperty
    public String memory;

    @JsonProperty
    public String ipaddressurl;

    @JsonProperty
    public String deviceid;

    @JsonProperty
    public String hostname;

    @JsonProperty
    public String chassisname;

    @JsonProperty
    public String groupname;

    @JsonProperty
    public String dnsdracname;

    @JsonProperty
    public String storagecentername;

    @JsonProperty
    public String replayprofile;

    @JsonProperty
    public String newserverpool;

    @JsonProperty
    public String availability;

    @JsonProperty
    public String status;

    @JsonProperty
    public String nics;

    @JsonProperty
    public List<UIListItem> servicelist;

    public UIDeviceSummary() {
        servicelist = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "UIDeviceSummary{" +
                "id='" + id + '\'' +
                ", health='" + health + '\'' +
                ", healthmessage='" + healthmessage + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", serviceTag='" + serviceTag + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", compliant='" + compliant + '\'' +
                ", state='" + state + '\'' +
                ", model='" + model + '\'' +
                ", displayserverpools='" + displayserverpools + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", processorcount=" + processorcount +
                ", processor='" + processor + '\'' +
                ", memory='" + memory + '\'' +
                ", ipaddressurl='" + ipaddressurl + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", hostname='" + hostname + '\'' +
                ", chassisname='" + chassisname + '\'' +
                ", groupname='" + groupname + '\'' +
                ", dnsdracname='" + dnsdracname + '\'' +
                ", storagecentername='" + storagecentername + '\'' +
                ", replayprofile='" + replayprofile + '\'' +
                ", newserverpool='" + newserverpool + '\'' +
                ", availability='" + availability + '\'' +
                ", status='" + status + '\'' +
                ", nics='" + nics + '\'' + 
                ", servicelist=" + servicelist +
                '}';
    }


}
