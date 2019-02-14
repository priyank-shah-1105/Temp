package com.dell.asm.ui.model.chassis;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIActivityLog;
import com.dell.asm.ui.model.UIAlertLog;
import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.iom.UIIOMSummary;
import com.dell.asm.ui.model.server.UIServerSummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Chassis
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassis extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The health. */
    @JsonProperty
    public String health;

    /** The ipaddress. */
    @JsonProperty
    public String ipaddress;

    /** The ipaddress URL. */
    @JsonProperty
    public String ipaddressurl;

    /** The servicetag. */
    @JsonProperty
    public String servicetag;

    /** The numberofblades. */
    @JsonProperty
    public int numberofblades;

    /** The lasttemplateapplied. */
    @JsonProperty
    public String lasttemplateapplied;

    /** The state. */
    @JsonProperty
    public String state;

    /** The hardwarehealth. */
    @JsonProperty
    public String hardwarehealth;

    /** The operationalstatus. */
    @JsonProperty
    public String operationalstatus;

    /** The chassisname. */
    @JsonProperty
    public String chassisname;

    /** The datacenter. */
    @JsonProperty
    public String datacenter;

    /** The aisle. */
    @JsonProperty
    public String aisle;

    /** The rack. */
    @JsonProperty
    public String rack;

    /** The rackslot. */
    @JsonProperty
    public String rackslot;

    /** The chassisservicetag. */
    @JsonProperty
    public String chassisservicetag;

    /** The chassismodel. */
    @JsonProperty
    public String chassismodel;

    /** The assettag. */
    @JsonProperty
    public String assettag;

    /** The chassismidplaneversion. */
    @JsonProperty
    public String chassismidplaneversion;

    /** The chassislocation. */
    @JsonProperty
    public String chassislocation;

    /** The discoverytime. */
    @JsonProperty
    public String discoverytime;

    /** The lastinventorytime. */
    @JsonProperty
    public String lastinventorytime;

    /** The lastprofileappliedtime. */
    @JsonProperty
    public String lasttemplateappliedtime;

    /** The blades. */
    @JsonProperty
    public List<UIServerSummary> blades;

    /** The ioms. */
    @JsonProperty
    public List<UIIOMSummary> ioms;

    /** The primarychassiscontrollername. */
    @JsonProperty
    public String primarychassiscontrollername;

    /** The primarychassiscontrollerfirmwareversion. */
    @JsonProperty
    public String primarychassiscontrollerfirmwareversion;

    /** The primarychassiscontrollerlastupdatetime. */
    @JsonProperty
    public String primarychassiscontrollerlastupdatetime;

    /** The standbychassiscontrollerpresent. */
    @JsonProperty
    public String standbychassiscontrollerpresent;

    /** The standbychassiscontrollerfirmwareversion. */
    @JsonProperty
    public String standbychassiscontrollerfirmwareversion;

    /** The ikvmpresent. */
    @JsonProperty
    public String ikvmpresent;

    /** The ikvmname. */
    @JsonProperty
    public String ikvmname;

    /** The ikvmmanufacturer. */
    @JsonProperty
    public String ikvmmanufacturer;

    /** The ikvmpartnumber. */
    @JsonProperty
    public String ikvmpartnumber;

    /** The ikvmfirmwareversion. */
    @JsonProperty
    public String ikvmfirmwareversion;

    /** The powersupplies. */
    @JsonProperty
    public List<UIPowerSupply> powersupplies;

    /** The associatedcredential. */
    @JsonProperty
    public String associatedcredential;

    /** The chassisdnsname. */
    @JsonProperty
    public String chassisdnsname;

    /** The lastcompliancechecktime. */
    @JsonProperty
    public String lastcompliancechecktime;

    /** The chassispowerstate. */
    @JsonProperty
    public String chassispowerstate;

    /** The fabrica. */
    @JsonProperty
    public String fabrica;

    /** The fabricb. */
    @JsonProperty
    public String fabricb;

    /** The fabricc. */
    @JsonProperty
    public String fabricc;

    /** The powercap. */
    @JsonProperty
    public String powercap;

    /** The isactivesystem. */
    @JsonProperty
    public boolean isactivesystem;

    /** The activesystemname. */
    @JsonProperty
    public String activesystemname;

    /** The activesystemmodel. */
    @JsonProperty
    public String activesystemmodel;

    /** The israckbased. */
    @JsonProperty
    public boolean israckbased;

    @JsonProperty
    public List<UIAlertLog> alertLogs;

    @JsonProperty
    public List<UIActivityLog> activityLogs;


    public long defaultPowerCapUpperBoundWatts; // not a JSON property
    public long defaultPowerCapUpperBoundBTU; // not a JSON property
    public long defaultPowerCapLowerBoundWatts; // not a JSON property
    public long defaultPowerCapLowerBoundBTU; // not a JSON property


    /**
     * Instantiates a new uI chassis.
     */
    public UIChassis() {
        super();
        blades = new ArrayList<UIServerSummary>();
        ioms = new ArrayList<UIIOMSummary>();
        powersupplies = new ArrayList<UIPowerSupply>();
        alertLogs = new ArrayList<UIAlertLog>();
        activityLogs = new ArrayList<UIActivityLog>();
        chassispowerstate = "Unknown";
    }

}
