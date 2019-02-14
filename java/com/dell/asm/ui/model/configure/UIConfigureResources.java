package com.dell.asm.ui.model.configure;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIConfigureResources.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIConfigureResources extends UIBaseObject {

    @JsonProperty
    public String id;
    @JsonProperty
    public List<UIConfigurableDevice> devices;
    @JsonProperty
    public boolean configGlobalChassisSettings = true;
    @JsonProperty
    public UIDeviceConfiguration globalConfiguration;
    @JsonProperty
    public boolean configUniqueChassisSettings;
    @JsonProperty
    public boolean configUniqueServerSettings;
    @JsonProperty
    public boolean configUniqueIOMSettings;
    @JsonProperty
    public String configIOMMode = "independent";
    @JsonProperty
    public boolean canConfigAllIOM;
    @JsonProperty
    public boolean configAllIOM;
    @JsonProperty
    public List<UIUplink> uplinks;
    @JsonProperty
    public UIIOMConfigurationSettings commonIOMConfiguration;
    @JsonProperty
    public List<UIIOMConfigurationSettings> iomConfiguration;
    @JsonProperty
    public String configIOMXMLSettings;
    @JsonProperty
    public boolean configUplinks;
    @JsonProperty
    public String configIOMXMLSettingsName;
    @JsonProperty
    public String configIOMXMLSettingsDescription;
    @JsonProperty
    public String configIOMXMLSettingsExistingFileName;
    @JsonProperty
    public boolean vltenabled = true;
    @JsonProperty
    public boolean enableConfigAllIOM = true;


    /**
     * Valid options are "RSTP", "PVST", "None"
     */
    @JsonProperty
    public String spanningTreeMode;

    /**
     * Constructor.
     */
    public UIConfigureResources() {
        super();
        uplinks = new ArrayList<>();
        iomConfiguration = new ArrayList<>();
        devices = new ArrayList<>();
    }

}
