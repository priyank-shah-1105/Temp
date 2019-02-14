package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobRemoveServiceRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobRemoveServiceRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public com.dell.asm.ui.model.service.UIRemoveServiceRequest requestObj;

    public JobRemoveServiceRequest(com.dell.asm.ui.model.service.UIRemoveServiceRequest requestObj) {
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobRemoveServiceRequest(){ super(); }
}
