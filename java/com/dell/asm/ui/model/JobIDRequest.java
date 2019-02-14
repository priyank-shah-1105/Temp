package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobIDRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobIDRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIID requestObj;

    /**
     * Instantiates a new job id request.
     */
    public JobIDRequest() {
        super();
    }

    /**
     * Instantiates a new job id request.
     *
     * @param requestObj
     *            the request obj
     */
    public JobIDRequest(UIID requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.pg.skyhawk.ui.model.JobRequest#toString()
     */
    @Override
    public String toString() {
        return "JobIDRequest [requestObj=" + requestObj + ", criteriaObj="
                + criteriaObj + ", antiCSRFId=" + antiCSRFId + "]";
    }

}
