package com.dell.asm.ui.model.configure;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobConfigureResourcesRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobGetConfigurableResourcesRequest extends JobRequest {

    /**
     * The request obj.
     */
    @JsonProperty
    public UIGetConfigurableResources requestObj;

    /**
     * Constructor.
     */
    public JobGetConfigurableResourcesRequest() {
        super();
    }
}
