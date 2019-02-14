/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.service;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.template.UITemplateBuilderComponent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIExistingServiceRequest extends UIBaseObject {

    @JsonProperty
    public UIDeploy deploy;

    @JsonProperty
    public List<UIOSCredential> osCredentials;

    @JsonProperty
    public List<UIVSwitch> vSwitches;

    @JsonProperty
    public List<UIExistingServiceNetwork> existingServiceNetworks;

    public UIExistingServiceRequest() {
        super();
        osCredentials = new ArrayList<>();
        vSwitches = new ArrayList<>();
        existingServiceNetworks = new ArrayList<>();
    }
}
