package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic log.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UILog extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The severity. */
    @JsonProperty
    public String severity;

    /** The category. */
    @JsonProperty
    public String category;

    /** The description. */
    @JsonProperty
    public String description;

    /** The date. */
    @JsonProperty
    public String date;

    /** The user. */
    @JsonProperty
    public String user;

    /**
     * Instantiates a new uI log data.
     */
    public UILog() {
        super();
    }
}
