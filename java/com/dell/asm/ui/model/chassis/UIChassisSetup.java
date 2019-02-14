package com.dell.asm.ui.model.chassis;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIChassisSetup.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassisSetup extends UIBaseObject {

    /** The chassisid. */
    @JsonProperty
    public List<UISetupChassisConfig> chassislist;

    /** The chassissetupfabrics. */
    @JsonProperty
    public List<UIChassisSetupFabric> chassissetupfabrics;

    /** The managementtemplateid. */
    @JsonProperty
    public String managementtemplateid;

    /** The firmwarelist. */
    @JsonProperty
    public List<UIChassisFirmware> firmwarelist;

    /**
     * Instantiates a new uI chassis setup.
     */
    public UIChassisSetup() {
        super();
        this.chassissetupfabrics = new ArrayList<UIChassisSetupFabric>();
        this.chassislist = new ArrayList<UISetupChassisConfig>();
        this.firmwarelist = new ArrayList<UIChassisFirmware>();
    }

}
