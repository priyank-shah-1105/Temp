package com.dell.asm.ui.model.configure;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.firmware.UIFirmwareComponent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIConfigurableDevice.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIConfigurableDevice extends UIBaseObject {

    @JsonProperty
    public String id;
    @JsonProperty
    public String idForDelete;
    @JsonProperty
    public String ipAddress;
    @JsonProperty
    public String svctag;
    @JsonProperty
    public String resourceType;
    @JsonProperty
    public String model;
    @JsonProperty
    public String state;
    @JsonProperty
    public boolean updateFW;
    @JsonProperty
    public boolean isFWCompliant;
    @JsonProperty
    public List<UIFirmwareComponent> firmwarecomponents;
    @JsonProperty
    public UIChassisConfigurationSettings chassisConfiguration;
    @JsonProperty
    public String chassisId;
    @JsonProperty
    public String complianceDetails;
    @JsonProperty
    public String deviceid;
    @JsonProperty
    public boolean fnioa;
    @JsonProperty
    public boolean fnioaUpdateRequired = false;

    public UIConfigurableDevice() {
        super();
        firmwarecomponents = new ArrayList<>();
    }
}
