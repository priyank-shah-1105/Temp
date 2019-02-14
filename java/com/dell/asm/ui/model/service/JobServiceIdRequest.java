package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobServiceIdRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobServiceIdRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIServiceId requestObj;

    /**
     * Instantiates a new job id request.
     */
    public JobServiceIdRequest() {
        super();
    }

    /**
     * Instantiates a new job id request.
     *
     * @param requestObj
     *            the request obj
     */
    public JobServiceIdRequest(UIServiceId requestObj) {
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
