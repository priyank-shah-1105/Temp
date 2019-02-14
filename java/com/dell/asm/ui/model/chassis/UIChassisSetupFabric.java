package com.dell.asm.ui.model.chassis;

import java.util.ArrayList;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIChassisSetupFabric.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassisSetupFabric extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The purpose. */
    @JsonProperty
    public String purpose;

    /** The customizenetworktraffic. */
    @JsonProperty
    public boolean customizenetworktraffic;

    /** The networks. */
    @JsonProperty
    public ArrayList<String> selectednetworkids;

    /** The enabledcb. */
    @JsonProperty
    public boolean enabledcb;

    /**
     * Instantiates a new uI chassis setup fabric.
     */
    public UIChassisSetupFabric() {
        super();
        this.selectednetworkids = new ArrayList<String>();
    }

}
