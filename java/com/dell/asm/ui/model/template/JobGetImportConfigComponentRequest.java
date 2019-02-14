package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobGetImportConfigComponentRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobGetImportConfigComponentRequest extends JobRequest {

    @JsonProperty
    public UIGetReferenceComponent requestObj;

    /**
     * Constructor.
     */
    public JobGetImportConfigComponentRequest() {
        super();
    }
}
