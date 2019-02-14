package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobTemplateBuilderComponentRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobTemplateBuilderComponentRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UITemplateBuilderComponent requestObj;


    public JobTemplateBuilderComponentRequest(UITemplateBuilderComponent requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobTemplateBuilderComponentRequest() {
        super();
    }

}
    

