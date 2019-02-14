package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobTemplateBuilderComponentRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobUpdatedTemplateBuilderComponentRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIUpdatedTemplateBuilderComponentRequest requestObj;


    public JobUpdatedTemplateBuilderComponentRequest(
            UIUpdatedTemplateBuilderComponentRequest requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobUpdatedTemplateBuilderComponentRequest() {
        super();
    }

}
    

