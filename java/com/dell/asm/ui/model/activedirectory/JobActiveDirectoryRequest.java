package com.dell.asm.ui.model.activedirectory;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobActiveDirectoryRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobActiveDirectoryRequest extends JobRequest {
    /** The request obj. */
    @JsonProperty
    public UIActiveDirectory requestObj;
}




