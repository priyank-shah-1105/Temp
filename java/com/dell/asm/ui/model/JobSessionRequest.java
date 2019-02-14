package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobSessionRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobSessionRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UISession requestObj;

    /**
     * Instantiates a new job id request.
     */
    public JobSessionRequest() {
        super();
    }
}
