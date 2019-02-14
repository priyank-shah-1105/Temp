package com.dell.asm.ui.model.device;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIElementManager extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String model;

    @JsonProperty
    public String manufacturer;

    @JsonProperty
    public String state;

    @JsonProperty
    public String serviceTag;

    @JsonProperty
    public String health;

    @JsonProperty
    public String ipaddress;

    @JsonProperty
    public String deviceType;


    /**
     * Instantiates a new uI device summary.
     */
    public UIElementManager() {
        super();
    }

}
