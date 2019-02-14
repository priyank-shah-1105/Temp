/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.server;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UILogicalDisk extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String logicalDiskName;

    @JsonProperty
    public String state;

    @JsonProperty
    public String layout;

    @JsonProperty
    public String size;

    @JsonProperty
    public String mediaType;

    @JsonProperty
    public String readPolicy;

    @JsonProperty
    public String writePolicy;

    @JsonProperty
    public List<UIPhysicalDisk> physicaldiskdata;

    public UILogicalDisk() {
        super();
        physicaldiskdata = new ArrayList<>();
    }
}
