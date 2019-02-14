package com.dell.asm.ui.model.initialsetup;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobInitialSetupDataRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobInitialSetupDataRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIIntialSetupData requestObj;

    /**
     * Instantiates a new job initial setup data request.
     */
    public JobInitialSetupDataRequest() {
        super();
    }

}
