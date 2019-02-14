/**************************************************************************
 *   Copyright (c) 2012 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.network;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIStaticIPAddressDetails extends UIBaseObject {

    public static String INUSE = "inuse";
    public static String AVAILABLE = "available";

    @JsonProperty
    public String id;

    @JsonProperty
    public String state;

    @JsonProperty
    public String ipAddress;

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public String serviceName;

    @JsonProperty
    public String deviceId;

    @JsonProperty
    public String deviceName;

    @JsonProperty
    public String deviceType;

    public String ipRangeId;

    @JsonProperty
    public String role;


    public UIStaticIPAddressDetails() {
        super();
    }

    public UIStaticIPAddressDetails(String id,
                                    String state,
                                    String ipRangeId,
                                    String deviceId,
                                    String deviceName,
                                    String deviceType,
                                    String role) {
        super();
        this.id = id;
        this.ipAddress = id;
        this.state = state;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.ipRangeId = ipRangeId;
        this.role = role;
    }

    @Override
    public String toString() {
        return "UIStaticIPAddressDetails{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                '}';
    }
}
