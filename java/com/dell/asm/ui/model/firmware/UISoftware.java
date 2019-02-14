package com.dell.asm.ui.model.firmware;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIFirmware.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UISoftware extends UIBaseObject implements Comparable {

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

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        }

        UISoftware software = (UISoftware) o;
        if (this.softwaretype == null) {
            if (software.softwaretype == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (software.softwaretype == null) {
            return 1;
        } else {
            int compareType = this.softwaretype.compareToIgnoreCase(software.softwaretype);
            if (compareType == 0) {
                if (this.softwarename == null) {
                    if (software.softwarename == null) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else if (software.softwarename == null) {
                    return 1;
                } else {
                    return this.softwarename.compareToIgnoreCase(software.softwarename);
                }
            } else {
                return compareType;
            }
        }
    }
}
