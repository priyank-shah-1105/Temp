package com.dell.asm.ui.model;

import com.dell.asm.ui.model.users.JobMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobRequest extends JobMessage {

    /**
     * The anti csrf id.
     */
    @JsonProperty
    public String antiCSRFId;


    /**
     * Instantiates a new job request.
     */
    public JobRequest() {
        super();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "JobRequest [criteriaObj=" + criteriaObj + "]";
    }
}
