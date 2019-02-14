package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobCreateServiceRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobCreateServiceRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIDeploy requestObj;

    /**
     * Instantiates a new service request.
     */
    public JobCreateServiceRequest() {
        super();
    }

}
