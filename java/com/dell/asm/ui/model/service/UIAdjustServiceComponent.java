package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIAdjustServiceComponent.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIAdjustServiceComponent extends UIBaseObject {

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public String componentId;

    @JsonProperty
    public int instances;

    @JsonProperty
    public String source;

    public UIAdjustServiceComponent() {
        super();
    }

    @Override
    public String toString() {
        return "UIAdjustServiceComponent{" +
                "serviceId='" + serviceId + '\'' +
                ", componentId='" + componentId + '\'' +
                ", instances=" + instances +
                ", source='" + source + '\'' +
                '}';
    }
}
