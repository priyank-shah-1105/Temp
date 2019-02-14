/**************************************************************************
 *   Copyright (c) 2017 Dell Inc. All rights reserved.                    *
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
import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIConfigureTemplateCategory extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String type;

    @JsonProperty
    public String value;

    @JsonProperty
    public List<UIListItem> options;

    @JsonProperty
    public List<UIConfigureTemplateComponent> components;

    @JsonProperty
    public String dependencyTarget;

    @JsonProperty
    public String dependencyValue;

    public UIConfigureTemplateCategory() {
        options = new ArrayList<>();
        components = new ArrayList<>();
    }

}
