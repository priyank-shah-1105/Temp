package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobCreatePoolRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobCreateTemplateRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UITemplateBuilderCreate requestObj;


    public JobCreateTemplateRequest(UITemplateBuilderCreate requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobCreateTemplateRequest() {
        super();
    }

}
    

