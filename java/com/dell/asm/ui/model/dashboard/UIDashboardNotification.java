package com.dell.asm.ui.model.dashboard;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDashboardNotification extends UIBaseObject {
    @JsonProperty
    public String message;
    @JsonProperty
    public String userName;
    @JsonProperty
    public String createdOn;

    public UIDashboardNotification() {
        super();
    }
}
