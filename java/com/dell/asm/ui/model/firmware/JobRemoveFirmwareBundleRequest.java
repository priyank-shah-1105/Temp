package com.dell.asm.ui.model.firmware;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobSaveTemplateRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobRemoveFirmwareBundleRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIRemoveFirmwareBundle requestObj;


    /**
     * Constructor.
     */
    public JobRemoveFirmwareBundleRequest() {
        super();
    }

}
    

