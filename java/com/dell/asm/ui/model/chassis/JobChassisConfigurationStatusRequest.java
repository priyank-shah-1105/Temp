package com.dell.asm.ui.model.chassis;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobChassisConfigurationStatusRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobChassisConfigurationStatusRequest extends JobRequest {

    /**
     * The request obj.
     */
    @JsonProperty
    public UIChassisConfigurationStatus requestObj;

    /**
     * Constructor.
     */
    public JobChassisConfigurationStatusRequest() {
        super();
    }

}
