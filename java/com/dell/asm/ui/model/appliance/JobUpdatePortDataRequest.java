package com.dell.asm.ui.model.appliance;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JobUpdateDhcpRequest
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobUpdatePortDataRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIPortData requestObj;

    /**
     * Instantiates a new job appliance update request.
     */
    public JobUpdatePortDataRequest() {
        super();
    }

}
