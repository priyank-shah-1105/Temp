package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIComponentData extends UIBaseObject {

    @JsonProperty
    public String serviceid;

    @JsonProperty
    public String componentid;

    @JsonProperty
    public String details;

    public UIComponentData() {
        super();
    }
}
