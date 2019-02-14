package com.dell.asm.ui.model.activedirectory;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobActiveDirectoryUserRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobActiveDirectoryUserRequest extends JobRequest {
    /** The request obj. */
    @JsonProperty
    public UIActiveDirectoryUser requestObj;
}




