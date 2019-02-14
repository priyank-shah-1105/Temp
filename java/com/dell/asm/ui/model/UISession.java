package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UISession.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UISession extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /**
     * Instantiates a new uIID.
     */
    public UISession() {
        super();
    }

}
