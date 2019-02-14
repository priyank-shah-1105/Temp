package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobUpdateServiceFirmwareRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobUpdateServiceFirmwareRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIUpdateServiceFirmwareRequest requestObj;

    public JobUpdateServiceFirmwareRequest(UIUpdateServiceFirmwareRequest requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobUpdateServiceFirmwareRequest() {
        super();
    }

}
    

