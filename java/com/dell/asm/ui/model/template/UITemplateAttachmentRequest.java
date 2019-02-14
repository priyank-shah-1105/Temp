package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UITemplateBuilderRequest
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITemplateAttachmentRequest extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    public UITemplateAttachmentRequest() {
        super();
    }
}
