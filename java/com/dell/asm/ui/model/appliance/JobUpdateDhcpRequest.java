package com.dell.asm.ui.model.appliance;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JobUpdateDhcpRequest
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobUpdateDhcpRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIDhcpData requestObj;

    /**
     * Instantiates a new job appliance update request.
     */
    public JobUpdateDhcpRequest() {
        super();
    }

}
