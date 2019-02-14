package com.dell.asm.ui.model.backuprestore;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Backup Settings UI request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobBackupSettingsRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIBackupSettings requestObj;

    public JobBackupSettingsRequest(UIBackupSettings requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobBackupSettingsRequest() {
        super();
    }

}
