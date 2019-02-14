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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPortViewIOM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewIOM extends UIPortViewGenericSwitch {

    @JsonProperty
    public String wwpn;

    @JsonProperty
    public List<UIPortViewSwitchPort> uplinkPorts;

    @JsonProperty
    public String fabric;

    /**
     * Instantiates a new uI port view io module.
     */
    public UIPortViewIOM() {
        super();
        uplinkPorts = new ArrayList<>();
    }
}
