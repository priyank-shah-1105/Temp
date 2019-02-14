package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Activity Log
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIActivityLog extends UILog {

    @JsonProperty
    public String logDetailMessage;

    @JsonProperty
    public String logMessage;

    @JsonProperty
    public String logTimeStamp;

    @JsonProperty
    public String logUser;

    @JsonProperty
    public int progress;

    /**
     * Instantiates a new uI about data.
     */
    public UIActivityLog() {
        super();
    }
}
