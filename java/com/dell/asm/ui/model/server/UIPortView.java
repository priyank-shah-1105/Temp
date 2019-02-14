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

/**
 * The Class UIPortView
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortView extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String hostname;

    @JsonProperty
    public String ipaddress;

    @JsonProperty
    public String type;

    @JsonProperty
    public String health = "green";

    @JsonProperty
    public String model;

    @JsonProperty
    public String powerState;

    @JsonProperty
    public String state;

    @JsonProperty
    public String statusMessage;

    @JsonProperty
    public List<UIPortViewNIC> nics;

    @JsonProperty
    public List<UIPortViewIOM> ioModules;

    @JsonProperty
    public List<UIPortViewSwitch> torSwitches;

    @JsonProperty
    public List<UIPortViewConnection> portConnections;


    /**
     * Instantiates a new port view port.
     */
    public UIPortView() {
        super();
        nics = new ArrayList<>();
        ioModules = new ArrayList<>();
        torSwitches = new ArrayList<>();
        portConnections = new ArrayList<>();
    }

}
