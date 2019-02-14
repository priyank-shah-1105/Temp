package com.dell.asm.ui.model.firmware;

import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareComponent;
import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIFirmware.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIFirmware extends UIBaseObject implements Comparable {

    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String firmwarename;

    @JsonProperty
    public String firmwaretype;

    @JsonProperty
    public String firmwareversion;

    @JsonProperty
    public String firmwarelastupdatetime;

    @JsonProperty
    public String criticality;

    @JsonProperty
    public String filename;

    public UIFirmware() {
        super();
    }

    public UIFirmware(SoftwareComponent softwareComponent) {
        if (softwareComponent.getUpdatedDate() != null)
            this.firmwarelastupdatetime = softwareComponent.getUpdatedDate().toString();
        this.firmwarename = softwareComponent.getName();
        this.firmwaretype = softwareComponent.getCategory();
        this.firmwareversion = softwareComponent.getVendorVersion();
        this.id = softwareComponent.getId();
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        }

        UIFirmware firmware = (UIFirmware) o;
        if (this.firmwaretype == null) {
            if (firmware.firmwaretype == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (firmware.firmwaretype == null) {
            return 1;
        } else {
            int compareType = this.firmwaretype.compareToIgnoreCase(firmware.firmwaretype);
            if (compareType == 0) {
                if (this.firmwarename == null) {
                    if (firmware.firmwarename == null) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else if (firmware.firmwarename == null) {
                    return 1;
                } else {
                    return this.firmwarename.compareToIgnoreCase(firmware.firmwarename);
                }
            } else {
                return compareType;
            }
        }
    }
}
