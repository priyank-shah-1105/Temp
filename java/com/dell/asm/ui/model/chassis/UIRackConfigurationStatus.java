package com.dell.asm.ui.model.chassis;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIRackConfigurationStatus.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIRackConfigurationStatus extends UIBaseObject {

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
    public List<UIRackConfiguration> rackConfigurations;

    /**
     * Constructor.
     */
    public UIRackConfigurationStatus() {
        super();
        this.rackConfigurations = new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UIRackConfigurationStatus [jobId=" + jobId + ", pending=" + pending;
    }

}
