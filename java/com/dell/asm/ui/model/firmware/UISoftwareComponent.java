package com.dell.asm.ui.model.firmware;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIChassisFirmware.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UISoftwareComponent extends UIBaseObject {

    @JsonProperty
    public String vendor;

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String type;

    @JsonProperty
    public UISoftware currentversion;

    @JsonProperty
    public UISoftware targetversion;
    @JsonProperty
    public boolean compliant;

}
