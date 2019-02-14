package com.dell.asm.ui.model.chassis;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobDiscoveryDataRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobDiscoveryDataRequest extends JobRequest {

    /**
     * The request obj.
     */
    @JsonProperty
    public UIDiscoveryData requestObj;

    /**
     * Constructor.
     */
    public JobDiscoveryDataRequest() {
        super();
    }

}
