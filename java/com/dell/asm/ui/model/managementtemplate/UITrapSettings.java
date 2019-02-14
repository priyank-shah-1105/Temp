package com.dell.asm.ui.model.managementtemplate;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UITrapSettings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITrapSettings extends UIBaseObject {

    /** The destination ip address. */
    @JsonProperty
    public String destinationIpAddress;

    /** The community string. */
    @JsonProperty
    public String communityString;

    /**
     * Instantiates a new uI trap settings.
     */
    public UITrapSettings() {
        super();
    }

    /**
     * Instantiates a new uI trap settings.
     *
     * @param destinationIpAddress
     *            the destination ip address
     * @param communityString
     *            the community string
     */
    public UITrapSettings(String destinationIpAddress, String communityString) {
        super();
        this.destinationIpAddress = destinationIpAddress;
        this.communityString = communityString;
    }


}
