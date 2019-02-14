package com.dell.asm.ui.model.network;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIVirtualNIC.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIVirtualNIC extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The template id. */
    @JsonProperty
    public String templateId;

    /** The connection type id. */
    @JsonProperty
    public String connectionTypeId;

    /** The bandwidth. */
    @JsonProperty
    public String bandwidth;

    /** The redundancy. */
    @JsonProperty
    public boolean redundancy;

    /** The selected networks. */
    @JsonProperty
    public List<UINetworkSummary> selectedNetworks;

    /** The subpool. */
    @JsonProperty
    public String subpool;

    /** The selectidentity. */
    @JsonProperty
    public boolean selectidentity;

    /** The name. */
    @JsonProperty
    public String name;

    /** The rel bandwidth weight. */
    @JsonProperty
    public String relBandwidthWeight;

    /** The native vlan id. */
    @JsonProperty
    public String nativeVlanId;

    /** The staticordhcp. */
    @JsonProperty
    public String staticordhcp;

    /** The minbandwidth. */
    @JsonProperty
    public String minbandwidth;

    /**
     * Instantiates a new uI virtual nic.
     */
    public UIVirtualNIC() {
        super();
        this.selectedNetworks = new ArrayList<UINetworkSummary>();
    }

}
