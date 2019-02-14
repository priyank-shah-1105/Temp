package com.dell.asm.ui.model.configure;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIGetConfigurableResources.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIGetConfigurableResources extends UIBaseObject {

    @JsonProperty
    public boolean requireComplianceCheck;

    public UIGetConfigurableResources() {
        super();
    }
}
