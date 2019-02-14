package com.dell.asm.ui.model.network;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  Returns network port groups for the selected Network.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobGetServicePortGroupRequest extends JobRequest {

    @JsonProperty
    public UIGetServicePortGroupRequest requestObj;

    /**
     * Constructor.
     */
    public JobGetServicePortGroupRequest() {
        super();
    }

}
