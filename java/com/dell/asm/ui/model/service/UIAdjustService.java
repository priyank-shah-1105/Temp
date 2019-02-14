package com.dell.asm.ui.model.service;

import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.template.UITemplateBuilderComponent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIService.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIAdjustService extends UIBaseObject {

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public String componentId;

    @JsonProperty
    public List<UITemplateBuilderComponent> components;

    public UIAdjustService() {
        super();
    }


}
