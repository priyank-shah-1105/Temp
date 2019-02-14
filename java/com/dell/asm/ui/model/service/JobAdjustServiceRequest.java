package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobSaveTemplateRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobAdjustServiceRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIAdjustService requestObj;


    public JobAdjustServiceRequest(UIAdjustService requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobAdjustServiceRequest() {
        super();
    }

}
    

