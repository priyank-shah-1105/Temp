package com.dell.asm.ui.model.network;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GetNetworkTypesRequest with boolean scaleup 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetNetworkTypesRequest extends JobRequest {

    /** The request obj. */

    @JsonProperty
    public UINetworkScaleUp requestObj;

    public GetNetworkTypesRequest() {
        super();
    }

    public GetNetworkTypesRequest(UINetworkScaleUp requestObj) {
        super();
        this.requestObj = requestObj;
    }

    @Override
    public String toString() {
        return "GetNetworkTypesRequest [requestObj=" + requestObj + "]";
    }

}