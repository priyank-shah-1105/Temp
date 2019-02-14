package com.dell.asm.ui.model.managementtemplate;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UICopyTemplate.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UICopyTemplate extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The template name. */
    @JsonProperty
    public String templatename;

    /** The template description. */
    @JsonProperty
    public String templateDescription;

    /** The template description. */
    @JsonProperty
    public String templateid;

}
