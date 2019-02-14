package com.dell.asm.ui.model.appliance;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobApplianceCertificateInfoRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobApplianceCertificateInfoRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIApplianceCertificateInfo requestObj;

    /**
     * Instantiates a new job appliance certificate info request.
     */
    public JobApplianceCertificateInfoRequest() {
        super();
        this.requestObj = new UIApplianceCertificateInfo();
    }

}
