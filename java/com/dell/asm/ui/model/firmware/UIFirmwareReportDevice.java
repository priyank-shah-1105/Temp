package com.dell.asm.ui.model.firmware;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIFirmwareReportDevice extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String serviceTag;

    @JsonProperty
    public String ipAddress;

    @JsonProperty
    public String name;

    @JsonProperty
    public String lastUpdateTime;

    @JsonProperty
    public String repositoryName;

    @JsonProperty
    public boolean softwareCompliant = true;

    @JsonProperty
    public boolean firmwareCompliant = true;

    @JsonProperty
    public List<UIFirmwareComponent> firmwareComponents;

    @JsonProperty
    public List<UISoftwareComponent> softwareComponents;

    @JsonProperty
    public String deviceType;

    @JsonProperty
    public String model;

    @JsonProperty
    public String availability;

    @JsonProperty
    public String state;

    @JsonProperty
    public String status;

    /**
     * Constructor.
     */
    public UIFirmwareReportDevice() {
        super();
        firmwareComponents = new ArrayList<UIFirmwareComponent>();
        softwareComponents = new ArrayList<UISoftwareComponent>();
    }
}
