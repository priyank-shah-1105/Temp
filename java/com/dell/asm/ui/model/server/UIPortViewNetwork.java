/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/

package com.dell.asm.ui.model.server;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPortViewConnection.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewNetwork extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String description;

    @JsonProperty
    public String type;

    @JsonProperty
    public String vlan;

    /**
     * Instantiates a new uI port view network.
     */
    public UIPortViewNetwork() {
        super();
    }

}
