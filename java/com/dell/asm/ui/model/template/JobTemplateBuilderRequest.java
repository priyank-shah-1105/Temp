package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobTemplateBuilderRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobTemplateBuilderRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UITemplateBuilderRequest requestObj;

    /**
     * Constructor.
     */
    public JobTemplateBuilderRequest() {
        super();
    }

}
    

