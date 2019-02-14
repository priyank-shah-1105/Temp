/**************************************************************************
 *   Copyright (c) 2015 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.service;

import java.util.List;

import com.dell.asm.ui.model.device.UIDevice;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UIAdjustResourceComponent {

    @JsonProperty
    public String serviceid;

    @JsonProperty
    public List<UIDevice> resources;
}
