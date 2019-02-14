package com.dell.asm.ui.model.managementtemplate;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIDestinationEmail.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDestinationEmail extends UIBaseObject {

    /** The email. */
    @JsonProperty
    public String email;

    /** The name. */
    @JsonProperty
    public String name;

    /**
     * Instantiates a new uI destination email.
     */
    public UIDestinationEmail() {
        super();
    }

    /**
     * Instantiates a new uI destination email.
     *
     * @param email
     *            the email
     * @param name
     *            the name
     */
    public UIDestinationEmail(String email, String name) {
        super();
        this.email = email;
        this.name = name;
    }

}
