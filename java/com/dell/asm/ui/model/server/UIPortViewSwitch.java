package com.dell.asm.ui.model.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPortViewTorSwitch.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewSwitch extends UIPortViewGenericSwitch {

    @JsonProperty
    public String portChannel;

    /**
     * Instantiates a new uI port view tor switch.
     */
    public UIPortViewSwitch() {
        super();
    }
}
