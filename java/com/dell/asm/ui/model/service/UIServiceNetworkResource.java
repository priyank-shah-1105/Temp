/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIServiceNetworkResource {
    @JsonProperty
    public String id;

    @JsonProperty
    public String networkid;

    @JsonProperty
    public String networkname;

    @JsonProperty
    public String portgroupid;

    @JsonProperty
    public String portgroupname;

    @JsonProperty
    public List<String> resourceNames;

    @JsonProperty
    public List<String> resources;


}
