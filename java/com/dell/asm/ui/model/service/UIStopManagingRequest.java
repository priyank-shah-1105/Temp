/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.service;

import java.util.HashSet;
import java.util.Set;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIStopManagingRequest extends UIBaseObject {

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public String componentId;

    @JsonProperty
    public Set<String> applicationIds;

    public UIStopManagingRequest(String serviceId, String componentId, Set<String> applicationIds) {
        super();
        this.serviceId = serviceId;
        this.componentId = componentId;
        this.applicationIds = applicationIds;
    }

    public UIStopManagingRequest() {
        super();
        this.applicationIds = new HashSet<>();
    }

    @Override
    public String toString() {
        return "UIStopManagingRequest{" +
                "serviceId='" + serviceId + '\'' +
                ", componentId='" + componentId + '\'' + '}';
    }
}
