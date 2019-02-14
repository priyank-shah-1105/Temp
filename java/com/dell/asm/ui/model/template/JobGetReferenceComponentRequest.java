package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobGetReferenceComponentRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobGetReferenceComponentRequest extends JobRequest {

    @JsonProperty
    public UIGetReferenceComponent requestObj;

    /**
     * Constructor.
     */
    public JobGetReferenceComponentRequest() {
        super();
    }
}
    

