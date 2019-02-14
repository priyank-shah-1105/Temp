package com.dell.asm.ui.model.firmware;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIChassisFirmware.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIFirmwareComponent extends UIBaseObject {

    @JsonProperty
    public String model;

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String type;

    @JsonProperty
    public UIFirmware currentversion;

    @JsonProperty
    public UIFirmware targetversion;
    @JsonProperty
    public boolean compliant;

}
