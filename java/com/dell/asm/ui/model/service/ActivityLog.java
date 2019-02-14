package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic log.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityLog extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String severity;

    /** The logMessage. */
    @JsonProperty
    public String msg;

    @JsonProperty
    public String timestamp;

    /**
     * Instantiates a new uI log data.
     */
    public ActivityLog() {
        super();
    }
}
