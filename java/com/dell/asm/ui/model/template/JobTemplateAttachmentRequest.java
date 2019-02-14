package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobTemplateBuilderRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobTemplateAttachmentRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UITemplateAttachmentRequest requestObj;


    /**
     * Constructor.
     */
    public JobTemplateAttachmentRequest() {
        super();
    }

}
    

