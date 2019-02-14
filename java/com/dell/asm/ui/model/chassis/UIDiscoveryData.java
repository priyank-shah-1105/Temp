package com.dell.asm.ui.model.chassis;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.configure.UIDeviceConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIDiscoveryData.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDiscoveryData extends UIBaseObject {

    @JsonProperty
    public List<UIChassisConnectionCheck> ipdata;

    @JsonProperty
    public UIChassisConfigurationStatus chassisdata;

    @JsonProperty
    public UIRackConfigurationStatus rackdata;

    @JsonProperty
    public UIDeviceConfiguration chassisconfig;

    /**
     * Instantiates a new discovery data.
     */
    public UIDiscoveryData() {
        super();
        this.ipdata = new ArrayList<>();
    }

}
