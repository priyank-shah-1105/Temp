package com.dell.asm.ui.model.environment;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIMonitoringSettings extends UIBaseObject {

    @JsonProperty
    public String smtpServer;

}
