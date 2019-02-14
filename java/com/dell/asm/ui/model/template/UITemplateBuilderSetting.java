package com.dell.asm.ui.model.template;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UITemplateBuilderSetting.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITemplateBuilderSetting extends UIBaseObject {

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
    public int min;

    @JsonProperty
    public int max;

    @JsonProperty
    public boolean multiple;

    @JsonProperty
    public List<UITemplateBuilderListItem> options;

    @JsonProperty
    public boolean requireatdeployment;

    @JsonProperty
    public boolean hidefromtemplate;

    @JsonProperty
    public String dependencyTarget;

    @JsonProperty
    public String dependencyValue;

    @JsonProperty
    public String addAction;

    @JsonProperty
    public Boolean readOnly;

    @JsonProperty
    public String group;

    @JsonProperty
    public Boolean generated;

    @JsonProperty
    public Boolean useinfotooltip;

    @JsonProperty
    public int maxlength;

    @JsonProperty
    public int step;

    @JsonProperty
    public boolean isOptionsSortable;

    @JsonProperty
    public boolean isPreservedForDeployment;

    public UITemplateBuilderSetting() {
        super();
        options = new ArrayList<>();
    }

}