package com.dell.asm.ui.model.chassis;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIChassisFirmware.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassisFirmware extends UIBaseObject {

    /** The model. */
    @JsonProperty
    public String model;

    /** The component. */
    @JsonProperty
    public String component;

    /** The version. */
    @JsonProperty
    public String version;

}
