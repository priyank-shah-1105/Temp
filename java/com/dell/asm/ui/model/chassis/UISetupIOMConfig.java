package com.dell.asm.ui.model.chassis;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UISetupServerConfig.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UISetupIOMConfig extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The ipaddress. */
    @JsonProperty
    public String ipaddress;

    /** The servicetag. */
    @JsonProperty
    public String servicetag;

    /** The slot. */
    @JsonProperty
    public String slot;

    /** The hostname. */
    @JsonProperty
    public String hostname;

    /** The assignedipaddress. */
    @JsonProperty
    public String assignedipaddress;

    public boolean ipStatic; // not a JSON property

}
