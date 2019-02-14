package com.dell.asm.ui.model.chassis;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIChassisConfigurationStatus.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassisConfigurationStatus extends UIBaseObject {

    /**
     * The jobId.
     */
    @JsonProperty
    public String jobId;

    /**
     * The pending.
     */
    @JsonProperty
    public Boolean pending;

    /**
     * The chassisConfigurations.
     */
    @JsonProperty
    public List<UIChassisConfiguration> chassisConfigurations;

    /**
     * Constructor.
     */
    public UIChassisConfigurationStatus() {
        super();
        this.chassisConfigurations = new ArrayList<UIChassisConfiguration>();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UIChassisConfigurationStatus [jobId=" + jobId + ", pending=" + pending +
                ", chassisConfigurations=" + chassisConfigurations + "]";
    }

}
