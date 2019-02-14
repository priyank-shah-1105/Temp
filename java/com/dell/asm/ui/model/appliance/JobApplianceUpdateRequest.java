package com.dell.asm.ui.model.appliance;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobApplianceUpdateRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobApplianceUpdateRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIApplianceUpgradeSettings requestObj;

    /**
     * Instantiates a new job appliance update request.
     */
    public JobApplianceUpdateRequest() {
        super();
    }

}
