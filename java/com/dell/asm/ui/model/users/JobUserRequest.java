package com.dell.asm.ui.model.users;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobUserRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobUserRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIUser requestObj;
}
