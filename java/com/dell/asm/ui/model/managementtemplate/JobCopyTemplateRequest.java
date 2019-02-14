package com.dell.asm.ui.model.managementtemplate;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobCopytemplateRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobCopyTemplateRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UICopyTemplate requestObj;

}
