package com.dell.asm.ui.model.chassis;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.firmware.UIFirmwareComponent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ChassisConfiguration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassisConfiguration extends UIBaseObject {

    /**
     * The chassisConfigurationId.
     */
    @JsonProperty
    public String chassisConfigurationId;

    /**
     * The ipAddress.
     */
    @JsonProperty
    public String ipAddress;

    /**
     * The numberOfIOMs.
     */
    @JsonProperty
    public String numberOfIOMs;

    /**
     * The wiringSchemeId.
     */
    @JsonProperty
    public String wiringSchemeId;

    /**
     * The status.
     */
    @JsonProperty
    public String status;

    /**
     * The valid.
     */
    @JsonProperty
    public Boolean valid;

    /**
     * The selected.
     */
    @JsonProperty
    public Boolean selected;

    /**
     * The statusDescription.
     */
    @JsonProperty
    public String statusDescription;

    /**
     * The blades.
     */
    @JsonProperty
    public String blades;

    /**
     * The version.
     */
    @JsonProperty
    public String version;

    /**
     * The isCompliant.
     */
    @JsonProperty
    public Boolean isCompliant;

    /**
     * The isUnsupported.
     */
    @JsonProperty
    public Boolean isUnsupported;

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
     * The device type.
     */
    @JsonProperty
    public String deviceType;

    @JsonProperty
    public String deviceid;

    @JsonProperty
    public String state;

    // not a JsonProperty
    public String manufacturer;

    @JsonProperty
    public String firmwareStatus;

    @JsonProperty
    public List<UIFirmwareComponent> firmwarecomponents;

    @JsonProperty
    public boolean configureDevice;

    /**
     * Constructor.
     */
    public UIChassisConfiguration() {
        super();
        firmwarecomponents = new ArrayList<>();
    }

}
