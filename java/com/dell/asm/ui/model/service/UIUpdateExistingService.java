/**************************************************************************
 *   Copyright (c) 2015-2018 Dell Inc. All rights reserved.               *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to update a Brownfield Service that has already been discovered and created in the system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUpdateExistingService {

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public List<UIOSCredential> osCredentials;

    public UIUpdateExistingService() {
        super();
        osCredentials = new ArrayList<>();
    }

}
