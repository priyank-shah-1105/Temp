package com.dell.asm.ui.model.network;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIGetServicePortGroupRequest
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIGetServicePortGroupRequest extends JobRequest {

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public String networkId;

    public UIGetServicePortGroupRequest() {
        super();
    }
}