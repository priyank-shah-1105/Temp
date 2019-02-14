package com.dell.asm.ui.model.chassis;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UISetupServerConfig.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UISetupChassisConfig extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The ipaddress. */
    @JsonProperty
    public String ipaddress;

    /** The servicetag. */
    @JsonProperty
    public String servicetag;

    /** The name. */
    @JsonProperty
    public String name;

    /** The dnsname. */
    @JsonProperty
    public String dnsname;

    /** The datacenter. */
    @JsonProperty
    public String datacenter;

    /** The aisle. */
    @JsonProperty
    public String aisle;

    /** The rack. */
    @JsonProperty
    public String rack;

    /** The slot. */
    @JsonProperty
    public String slot;

    /** The powercap. */
    @JsonProperty
    public String powercap;

    /** The powercap measurement type. */
    @JsonProperty
    public String powercapMeasurementType;

    /** The serverlist. */
    @JsonProperty
    public List<UISetupServerConfig> serverlist;

    /** The iomlist. */
    @JsonProperty
    public List<UISetupIOMConfig> iomlist;

    /** The selected. */
    @JsonProperty
    public boolean selected;

    /** The assignedipaddress. */
    @JsonProperty
    public String assignedipaddress;

    /** The model. */
    @JsonProperty
    public String model;

    /** The activesystemname. */
    @JsonProperty
    public String activesystemname;

    /** The activesystemdatacenter. */
    @JsonProperty
    public String activesystemdatacenter;

    /** The activesystemaisle. */
    @JsonProperty
    public String activesystemaisle;

    /** The     activesystemrack. */
    @JsonProperty
    public String activesystemrack;

    @JsonProperty
    public String devicetype;

    /** The activesystemaisle. */
    @JsonProperty
    public boolean collapsed;

    public long defaultPowerCapUpperBoundWatts; // not a JSON property
    public long defaultPowerCapUpperBoundBTU; // not a JSON property
    public long defaultPowerCapLowerBoundWatts; // not a JSON property
    public long defaultPowerCapLowerBoundBTU; // not a JSON property    

    /**
     * Instantiates a new uI setup chassis config.
     */
    public UISetupChassisConfig() {
        super();
        this.serverlist = new ArrayList<UISetupServerConfig>();
        this.iomlist = new ArrayList<UISetupIOMConfig>();
    }

}
