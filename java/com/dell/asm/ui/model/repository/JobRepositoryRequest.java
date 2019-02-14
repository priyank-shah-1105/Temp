package com.dell.asm.ui.model.repository;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobRepositoryRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobRepositoryRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIRepository requestObj;
}
