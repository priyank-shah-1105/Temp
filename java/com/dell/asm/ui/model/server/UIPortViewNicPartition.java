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
 * The Class UIPortViewNicPartition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewNicPartition extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String displayName;

    @JsonProperty
    public String health = "green";

    @JsonProperty
    public String ipaddress = "";

    @JsonProperty
    public String ipaddressurl;

    @JsonProperty
    public String macaddress;

    @JsonProperty
    public String wwpn;

    @JsonProperty
    public boolean iscsiEnabled;

    @JsonProperty
    public boolean fcoeEnabled;

    @JsonProperty
    public boolean pxeEnabled;

    @JsonProperty
    public List<UIPortViewNetwork> vlans;

    /**
     * Instantiates a new uI port view nic partition.
     */
    public UIPortViewNicPartition() {
        super();
        vlans = new ArrayList<>();
    }

}
