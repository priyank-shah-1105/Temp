package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UITemplateBuilderRequest
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITemplateBuilderRequest extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String templateId;

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public boolean deploy;

    @JsonProperty
    public String type;

    public UITemplateBuilderRequest() {
        super();
    }

    @Override
    public String toString() {
        return "UITemplateBuilderRequest{" +
                "id='" + id + '\'' +
                ", templateId='" + templateId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", deploy=" + deploy +
                ", type='" + type + '\'' +
                '}';
    }
}
