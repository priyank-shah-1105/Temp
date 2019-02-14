package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobSaveTemplateRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobSaveTemplateRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UITemplateBuilder requestObj;


    public JobSaveTemplateRequest(UITemplateBuilder requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobSaveTemplateRequest() {
        super();
    }

}
    

