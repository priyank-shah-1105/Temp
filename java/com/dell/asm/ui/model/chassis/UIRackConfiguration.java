package com.dell.asm.ui.model.chassis;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIRackConfiguration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIRackConfiguration extends UIBaseObject {
    /**
     * The rackConfigurationId.
     */
    @JsonProperty
    public String rackConfigurationId;

    /**
     * The ipAddress.
     */
    @JsonProperty
    public String ipaddress;

    /**
     * The status.
     */
    @JsonProperty
    public String status;

    /**
     * The model.
     */
    @JsonProperty
    public String model;

    /**
     * The servicetag.
     */
    @JsonProperty
    public String servicetag;

    /**
     * The servicetag.
     */
    @JsonProperty
    public String deviceid;

    /**
     * The device type.
     */
    @JsonProperty
    public String deviceType;

    @JsonProperty
    public boolean configureDevice;

    /**
     * Constructor.
     */
    public UIRackConfiguration() {
        super();
    }

}
