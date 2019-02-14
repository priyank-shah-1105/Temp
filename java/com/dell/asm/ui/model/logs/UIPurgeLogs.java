package com.dell.asm.ui.model.logs;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPurgeLogs.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPurgeLogs extends UIBaseObject {

    /** The older than. */
    @JsonProperty
    public String olderThan;

    /** The older than (display). */
    @JsonProperty
    public String olderThanDisplay;

    /** The severityinformation. */
    @JsonProperty
    public boolean severityinformation;

    /** The severitywarning. */
    @JsonProperty
    public boolean severitywarning;

    /** The severitycritical. */
    @JsonProperty
    public boolean severitycritical;

}
