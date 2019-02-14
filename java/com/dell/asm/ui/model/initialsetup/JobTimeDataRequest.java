package com.dell.asm.ui.model.initialsetup;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobTimeDataRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobTimeDataRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UITimeData requestObj;

    /**
     * Instantiates a new job time data request.
     */
    public JobTimeDataRequest() {
        super();
    }

}
