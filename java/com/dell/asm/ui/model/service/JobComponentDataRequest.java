package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobComponentDataRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobComponentDataRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIComponentData requestObj;

    /**
     * Instantiates a new service request.
     */
    public JobComponentDataRequest() {
        super();
    }

}
