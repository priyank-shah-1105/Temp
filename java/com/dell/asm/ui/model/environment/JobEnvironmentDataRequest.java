package com.dell.asm.ui.model.environment;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobUpdateVirtualApplianceRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobEnvironmentDataRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIMonitoringSettings requestObj;
}
