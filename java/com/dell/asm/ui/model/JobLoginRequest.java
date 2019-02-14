package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobIDRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobLoginRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UILoginRequest requestObj;

    /**
     * Instantiates a new job id request.
     */
    public JobLoginRequest() {
        super();
    }

    /**
     * Instantiates a new job id request.
     *
     * @param requestObj
     *            the request obj
     */
    public JobLoginRequest(UILoginRequest requestObj) {
        super();
        this.requestObj = requestObj;
    }

}
