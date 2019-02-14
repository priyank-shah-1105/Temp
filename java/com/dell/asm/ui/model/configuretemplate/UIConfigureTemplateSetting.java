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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIConfigureTemplateSetting extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The value. */
    @JsonProperty
    public String value;

    @JsonProperty
    public String datatype;

    @JsonProperty
    public String componentid;

    @JsonProperty
    public String name;

    @JsonProperty
    public String tooltip;

    @JsonProperty
    public boolean required;

    @JsonProperty
    public boolean hidefromtemplate;

    @JsonProperty
    public int min;

    @JsonProperty
    public int max;

    @JsonProperty
    public boolean multiple;

    @JsonProperty
    public List<UIConfigureTemplateListItem> options;

    @JsonProperty
    public String dependencyTarget;

    @JsonProperty
    public String dependencyValue;

    @JsonProperty
    public Boolean readOnly;

    @JsonProperty
    public String group;

    @JsonProperty
    public int maxlength;

    @JsonProperty
    public Boolean useinfotooltip;

    @JsonProperty
    public int step;

    @JsonProperty
    public boolean isOptionsSortable;

    public UIConfigureTemplateSetting() {
        super();
        options = new ArrayList<>();
    }
}
