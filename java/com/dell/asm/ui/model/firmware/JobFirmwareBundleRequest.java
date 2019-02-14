package com.dell.asm.ui.model.firmware;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobManagementTemplateRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobFirmwareBundleRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIFirmwareBundleRequest requestObj;
}
