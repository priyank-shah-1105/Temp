package com.dell.asm.ui.model;

import com.dell.asm.ui.model.users.JobMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobResponse extends JobMessage {

    /** The response code. */
    @JsonProperty
    public int responseCode = 0;

    /** The error object. */
    @JsonProperty
    public ErrorObj errorObj;

    /** The request obj. */
    @JsonProperty
    public Object requestObj;

    /** The response object. */
    @JsonProperty
    public Object responseObj;

    /**
     * Instantiates a new job response.
     */
    public JobResponse() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "JobResponse [responseCode=" + responseCode + ", errorObj="
                + errorObj + ", criteriaObj=" + criteriaObj + "]";
    }
}
