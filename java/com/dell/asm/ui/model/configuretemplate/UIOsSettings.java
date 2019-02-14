/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.configuretemplate;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIOsSettings extends UIBaseObject {

    @JsonProperty
    public boolean hyperconverged;

    @JsonProperty
    public String adminOSCredential;

    @JsonProperty
    public String svmAdminOSCredential;

    @JsonProperty
    public List<UIConfigureTemplateCategory> osRepositories;

    public UIOsSettings() {
        osRepositories = new ArrayList<>();
    }
}
