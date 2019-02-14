package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UITemplate.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIImportTemplate extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String importId;


    public UIImportTemplate() {
        super();
    }

}
