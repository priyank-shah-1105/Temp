package com.dell.asm.ui.model.firmware;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIRemoveFirmwareBundle extends UIBaseObject {


    @JsonProperty
    public String packageId;

    @JsonProperty
    public String bundleId;


    public UIRemoveFirmwareBundle() {
        super();
    }
}
