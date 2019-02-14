package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobDeleteServiceRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobDeleteServiceRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIDeleteServiceRequest requestObj;


    public JobDeleteServiceRequest(UIDeleteServiceRequest requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobDeleteServiceRequest() {
        super();
    }

}
    

