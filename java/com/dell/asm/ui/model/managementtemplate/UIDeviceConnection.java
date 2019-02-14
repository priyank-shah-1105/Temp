package com.dell.asm.ui.model.managementtemplate;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIDeviceConnection.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDeviceConnection extends UIBaseObject {

    /** The job id. */
    @JsonProperty
    public String jobId;

    /** The credential id. */
    @JsonProperty
    public String credentialsId;

    /** The ipaddress. */
    @JsonProperty
    public String ipaddress;

    /** The pending. */
    @JsonProperty
    public boolean pending;

    /** The status. */
    @JsonProperty
    public String status;

    /** The status description. */
    @JsonProperty
    public String statusDescription;

    /** The successful connection. */
    @JsonProperty
    public boolean successfulConnection;

    /** The type. */
    @JsonProperty
    public String type;

    /** The template name. */
    @JsonProperty
    public String templateName;

    /** The template description. */
    @JsonProperty
    public String templateDescription;
}
