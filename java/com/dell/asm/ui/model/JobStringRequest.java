package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobStringsRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobStringRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public String requestObj;

}
