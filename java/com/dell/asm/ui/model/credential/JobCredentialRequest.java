package com.dell.asm.ui.model.credential;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobCredentialRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobCredentialRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UICredential requestObj;

    /**
     * Constructor.
     */
    public JobCredentialRequest() {
        super();
    }

}
