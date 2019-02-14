package com.dell.asm.ui.model.managementtemplate;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobDeviceConnectionRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobDeviceConnectionRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIDeviceConnection requestObj;

    /**
     * Instantiates a new job device connection request.
     */
    public JobDeviceConnectionRequest() {
        super();
    }

}
