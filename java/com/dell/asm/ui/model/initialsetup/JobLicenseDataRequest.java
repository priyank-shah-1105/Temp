package com.dell.asm.ui.model.initialsetup;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobLicenseInfoRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobLicenseDataRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UILicenseData requestObj;

    /**
     * Instantiates a new job license info request.
     */
    public JobLicenseDataRequest() {
        super();
    }

}
