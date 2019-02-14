package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UILoginResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UILoginRequest {

    @JsonProperty
    public String Username;

    @JsonProperty
    public String Password;

    /**
     * Instantiates a new uI login request.
     */
    public UILoginRequest() {
        super();
    }

}
