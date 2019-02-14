package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobAdjustServiceComponentRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobAdjustServiceComponentRequest extends JobRequest {


    /** The request obj. */
    @JsonProperty
    public UIAdjustServiceComponent requestObj;


    public JobAdjustServiceComponentRequest(UIAdjustServiceComponent requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobAdjustServiceComponentRequest() {
        super();
    }

}
    

