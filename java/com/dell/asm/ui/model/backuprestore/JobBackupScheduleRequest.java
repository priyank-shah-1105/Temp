package com.dell.asm.ui.model.backuprestore;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Backup Schedule Info UI request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobBackupScheduleRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIBackupScheduleInfo requestObj;

    public JobBackupScheduleRequest(UIBackupScheduleInfo requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobBackupScheduleRequest() {
        super();
    }

}
