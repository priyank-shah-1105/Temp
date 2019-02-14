package com.dell.asm.ui.model.configure;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobConfigureResourcesRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobConfigureResourcesRequest extends JobRequest {

    /**
     * The request obj.
     */
    @JsonProperty
    public UIConfigureResources requestObj;

    /**
     * Constructor.
     */
    public JobConfigureResourcesRequest() {
        super();
    }
}
