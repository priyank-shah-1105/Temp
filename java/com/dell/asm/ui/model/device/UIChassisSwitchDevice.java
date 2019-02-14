package com.dell.asm.ui.model.device;

import java.util.List;

import com.dell.asm.ui.model.UIActivityLog;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//This might need to be a more "specific" name like UIIomSwitchDevice, or UIIoaSwitchDevice if those need to be separated more
//FIXME:  This is extending UIDellSwitchDevice, as it seems there is code elsewhere that relies on what comes back from here to be that class with those variables.
// Check ConfigureChassisController, with "UIDellSwitchDevice uiSwitch = DellSwitchController.parseSwitch(pDevice, dto)" for example.
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassisSwitchDevice extends UIDellSwitchDevice {

    @JsonProperty
    public String id;

    @JsonProperty
    public String deviceType;

    @JsonProperty
    public String ipaddress;

    @JsonProperty
    public String servicetag;

    @JsonProperty
    public String iompowerstate;

    @JsonProperty
    public String assettag;

    @JsonProperty
    public String firmwareversion;

    @JsonProperty
    public String switchhostname;

    @JsonProperty
    public String slot;

    @JsonProperty
    public String fabricpurpose;

    @JsonProperty
    public List<UIActivityLog> activityLogs;

}
