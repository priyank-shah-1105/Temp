package com.dell.asm.ui.model.chassis;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobChassisConnectionCheckRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobChassisSetupRequest extends JobRequest {

    /**
     * The request obj.
     */
    @JsonProperty
    public UIChassisSetup requestObj;

    /**
     * Constructor.
     */
    public JobChassisSetupRequest() {
        super();
    }
}
