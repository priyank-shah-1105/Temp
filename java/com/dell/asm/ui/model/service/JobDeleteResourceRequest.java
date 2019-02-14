package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobDeleteResourceRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobDeleteResourceRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIDeleteResourceRequest requestObj;


    public JobDeleteResourceRequest(UIDeleteResourceRequest requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobDeleteResourceRequest() {
        super();
    }

}
    

