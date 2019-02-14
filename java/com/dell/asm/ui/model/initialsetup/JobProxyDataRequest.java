package com.dell.asm.ui.model.initialsetup;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobProxyDataRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobProxyDataRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIProxyData requestObj;

    /**
     * Instantiates a new job proxy data request.
     */
    public JobProxyDataRequest() {
        super();
    }

}
