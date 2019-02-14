package com.dell.asm.ui.model.server;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UISoftware.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UISoftware extends UIBaseObject {


    /** The id. */
    @JsonProperty
    public String id;

    /** The softwarename. */
    @JsonProperty
    public String softwarename;

    /** The softwareversion. */
    @JsonProperty
    public String softwareversion;

    /** The softwarelastupdatetime. */
    @JsonProperty
    public String softwarelastupdatetime;

    /** The vendor. */
    @JsonProperty
    public String vendor;

    /** The softwaretype. */
    @JsonProperty
    public String softwaretype;

    /** The criticality. */
    @JsonProperty
    public String criticality;

    /** The filename. */
    @JsonProperty
    public String filename;


    /**
     * Instantiates a new uI firmware.
     */
    public UISoftware() {
        super();
    }
}
