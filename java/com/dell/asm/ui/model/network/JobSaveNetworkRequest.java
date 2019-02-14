package com.dell.asm.ui.model.network;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Save network UI request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobSaveNetworkRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UINetwork requestObj;

    public JobSaveNetworkRequest(UINetwork requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobSaveNetworkRequest() {
        super();
    }

}
