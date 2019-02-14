package com.dell.asm.ui.model.managementtemplate;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobManagementTemplateUserRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobManagementTemplateUserRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIManagementTemplateUser requestObj;

    /**
     * Do not expose JSON properties because of sensitive data.
     * @return toString()
     */
    public String toJSON() {
        return toString();
    }

}
