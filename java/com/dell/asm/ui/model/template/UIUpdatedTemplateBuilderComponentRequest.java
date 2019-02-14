package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UITemplateBuilderRequest
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUpdatedTemplateBuilderComponentRequest extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String templateId;

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public UITemplateBuilderComponent component;

    public UIUpdatedTemplateBuilderComponentRequest() {
        super();
    }
}
