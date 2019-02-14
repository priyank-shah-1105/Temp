package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobsRequestObj.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobsRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIJobs requestObj;
}
