package com.dell.asm.ui.model.configure;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIBladeConfigurationSettings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIIOMConfigurationSettings extends UIBaseObject {

    @JsonProperty
    public String id;
    @JsonProperty
    public String idForDelete;
    @JsonProperty
    public String iomId;
    @JsonProperty
    public String svctag;
    @JsonProperty
    public String slot;
    @JsonProperty
    public String fabric;
    @JsonProperty
    public String model;
    @JsonProperty
    public boolean iompresent;
    @JsonProperty
    public boolean iomconfigurable;
    @JsonProperty
    public String managementIP;
    @JsonProperty
    public String hostName;
    @JsonProperty
    public boolean quadPortMode;
    @JsonProperty
    public boolean quadPortSupported = true;
    @JsonProperty
    public boolean hasSlot1;
    @JsonProperty
    public List<UIPortConfigurationSettings> slot1Config;
    @JsonProperty
    public boolean hasSlot2;
    @JsonProperty
    public List<UIPortConfigurationSettings> slot2Config;
    @JsonProperty
    public boolean hasSlot3;
    @JsonProperty
    public List<UIPortConfigurationSettings> slot3Config;
    @JsonProperty
    public int slot1Ports;
    @JsonProperty
    public int slot2Ports;
    @JsonProperty
    public int slot3Ports;
    @JsonProperty
    public boolean slot1QuadPortSupported;
    @JsonProperty
    public boolean slot2QuadPortSupported;
    @JsonProperty
    public boolean slot3QuadPortSupported;
    @JsonProperty
    public boolean slot1FCModule;
    @JsonProperty
    public boolean slot2FCModule;
    @JsonProperty
    public boolean slot3FCModule;
    @JsonProperty
    public boolean fnioa;

    /**
     * Constructor.
     */
    public UIIOMConfigurationSettings() {
        super();
        slot1Config = new ArrayList<>();
        slot2Config = new ArrayList<>();
        slot3Config = new ArrayList<>();
        // always 24 ports
        for (int i = 0; i < 8; i++) {
            slot1Config.add(new UIPortConfigurationSettings());
            slot2Config.add(new UIPortConfigurationSettings());
            slot3Config.add(new UIPortConfigurationSettings());
        }
    }

}
