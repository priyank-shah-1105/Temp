package com.dell.asm.ui.model.device;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIActivityLog;
import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.configure.UIPortConfigurationSettings;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDellSwitchDevice extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String deviceType;

    @JsonProperty
    public String ipaddress;

    @JsonProperty
    public String ipaddressurl;

    @JsonProperty
    public String servicetag;

    @JsonProperty
    public String state;

    @JsonProperty
    public String powerstate;

    @JsonProperty
    public String iompowerstate;

    @JsonProperty
    public String hostname;

    @JsonProperty
    public String macaddress;

    @JsonProperty
    public String assettag;

    @JsonProperty
    public String firmwareversion;

    @JsonProperty
    public String softwareversion;

    @JsonProperty
    public String systemdescription;

    @JsonProperty
    public String serialnumber;

    @JsonProperty
    public String slot;

    @JsonProperty
    public String switchhostname;

    @JsonProperty
    public List<UIActivityLog> activityLogs;

    public String fabric;

    public String model;

    public List<UIPortConfigurationSettings> slot1Config;
    public List<UIPortConfigurationSettings> slot2Config;
    public List<UIPortConfigurationSettings> slot3Config;

    public boolean slot1FCModule;
    public boolean slot2FCModule;
    public boolean slot3FCModule;

    public List<List<String>> modules;

    public List<String> quad_port_interfaces;

    public String chassisipaddress;
    public String chassisservicetag;

    /**
     * Instantiates a new uI device summary.
     */
    public UIDellSwitchDevice() {
        super();
        activityLogs = new ArrayList<>();
    }


}
