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
 * The Class UIPortViewZoneInfo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewZoneInfo extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String zoneConfig;

    @JsonProperty
    public String zone;

    /**
     * Instantiates a new uI port view port.
     */
    public UIPortViewZoneInfo() {
        super();
    }

}
