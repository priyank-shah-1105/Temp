package com.dell.asm.ui.model.appliance;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobCertificateInfoRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobCertificateInfoRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UICertificateInfo requestObj;

    /**
     * Instantiates a new job certificate info request.
     */
    public JobCertificateInfoRequest() {
        super();
    }

}
