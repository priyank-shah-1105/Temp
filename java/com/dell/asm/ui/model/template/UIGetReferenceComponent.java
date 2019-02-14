package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIGetReferenceComponent.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIGetReferenceComponent extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String referenceId;

    @JsonProperty
    public String componentId;

    public UIGetReferenceComponent() {
        super();
    }

}
