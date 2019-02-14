package com.dell.asm.ui.model.configure;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIChassisConfigurationSettings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassisConfigurationSettings extends UIBaseObject {

    @JsonProperty
    public String id;
    @JsonProperty
    public boolean configChassis;
    @JsonProperty
    public boolean configAllIOM;
    @JsonProperty
    public UIDeviceConfiguration deviceConfiguration;
    @JsonProperty
    public List<UIBladeConfigurationSettings> bladeConfiguration;
    @JsonProperty
    public List<UIUplink> uplinks;
    @JsonProperty
    public UIIOMConfigurationSettings commonIOMConfiguration;
    @JsonProperty
    public List<UIIOMConfigurationSettings> iomConfiguration;
    @JsonProperty
    public boolean vltenabled;
    @JsonProperty
    public int bladeCount;
    @JsonProperty
    public int iomCount;
    @JsonProperty
    public boolean enableConfigAllIOM = true;


    /**
     * Constructor.
     */
    public UIChassisConfigurationSettings() {
        super();
        bladeConfiguration = new ArrayList<>();
        uplinks = new ArrayList<>();
        iomConfiguration = new ArrayList<>();
    }


}
