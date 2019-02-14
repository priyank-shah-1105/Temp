package com.dell.asm.ui.model.server;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIFirmware.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIFirmware extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The firmwarename. */
    @JsonProperty
    public String firmwarename;

    /** The firmwareversion. */
    @JsonProperty
    public String firmwareversion;

    /** The firmwarelastupdatetime. */
    @JsonProperty
    public String firmwarelastupdatetime;

    /**
     * Instantiates a new uI firmware.
     */
    public UIFirmware() {
        super();
    }

    /**
     * Instantiates a new uI firmware.
     *
     * @param id the id
     * @param firmwarename the firmwarename
     * @param firmwareversion the firmwareversion
     * @param firmwarelastupdatetime the firmwarelastupdatetime
     */
    public UIFirmware(String id, String firmwarename, String firmwareversion,
                      String firmwarelastupdatetime) {
        super();
        this.id = id;
        this.firmwarename = firmwarename;
        this.firmwareversion = firmwareversion;
        this.firmwarelastupdatetime = firmwarelastupdatetime;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UIFirmware [id=" + id + ", firmwarename=" + firmwarename + ", firmwareversion=" + firmwareversion
                + ", firmwarelastupdatetime=" + firmwarelastupdatetime + "]";
    }

}
