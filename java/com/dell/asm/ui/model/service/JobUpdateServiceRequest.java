package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobUpdateServiceRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobUpdateServiceRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIService requestObj;

    /**
     * Instantiates a new service request.
     */
    public JobUpdateServiceRequest() {
        super();
    }

}
