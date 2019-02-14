package com.dell.asm.ui.model.device;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Save server pool UI request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobSaveServerPoolRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIServerPool requestObj;

    public JobSaveServerPoolRequest(UIServerPool requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobSaveServerPoolRequest() {
        super();
    }

}
