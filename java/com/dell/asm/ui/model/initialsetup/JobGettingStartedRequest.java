package com.dell.asm.ui.model.initialsetup;

import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.UIGettingStartedData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobGettingStartedRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobGettingStartedRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIGettingStartedData requestObj;

    /**
     * Instantiates a new job initial setup data request.
     */
    public JobGettingStartedRequest() {
        super();
    }

}
