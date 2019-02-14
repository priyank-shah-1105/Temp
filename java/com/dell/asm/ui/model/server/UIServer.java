package com.dell.asm.ui.model.server;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIActivityLog;
import com.dell.asm.ui.model.UIAlertLog;
import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIServer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIServer extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The health. */
    @JsonProperty
    public String health;

    /** The ipaddress. */
    @JsonProperty
    public String ipaddress;

    @JsonProperty
    public String ipaddressurl;

    /** The servicetag. */
    @JsonProperty
    public String servicetag;

    /** The hostname */
    @JsonProperty
    public String hostname;

    /** The systemmodel. */
    @JsonProperty
    public String systemmodel;

    /** The cpus. */
    @JsonProperty
    public String cpus;

    /** The memory. */
    @JsonProperty
    public String memory;

    /** The lasttemplateapplied. */
    @JsonProperty
    public String lasttemplateapplied;

    /** The state. */
    @JsonProperty
    public String state;

    /** The healthstatus. */
    @JsonProperty
    public String healthstatus;

    /** The operationalstatus. */
    @JsonProperty
    public String operationalstatus;

    /** The serverpowerstate. */
    @JsonProperty
    public String serverpowerstate;

    /** The assettag. */
    @JsonProperty
    public String assettag;

    /** The slotnumber. */
    @JsonProperty
    public String slotnumber;

    /** The slotname. */
    @JsonProperty
    public String slotname;

    /** The chassisipaddress. */
    @JsonProperty
    public String chassisipaddress;

    /** The discoverytime. */
    @JsonProperty
    public String discoverytime;

    /** The lastinventorytime. */
    @JsonProperty
    public String lastinventorytime;

    /** The active deployment name. */
    @JsonProperty
    public String activeDeploymentName;

    /** The dns idrac name. */
    @JsonProperty
    public String dnsIdracName;

    /** The os name. */
    @JsonProperty
    public String os;

    /** The nics. */
    @JsonProperty
    public List<UINIC> nics;

    /** The firmwares. */
    @JsonProperty
    public List<UIFirmware> firmwares;

    /** The firmwares. */
    @JsonProperty
    public List<UISoftware> softwares;


    /** The associated credential. */
    @JsonProperty
    public String associatedcredential;

    /** The cpudata. */
    @JsonProperty
    public List<UICPU> cpudata;

    /** The memory in the server. */
    @JsonProperty
    public List<UIMemory> memorydata;

    /** The lastcompliancechecktime. */
    @JsonProperty
    public String lastcompliancechecktime;

    /** The ports. */
    @JsonProperty
    public List<UIPortViewPort> ports;

    /** The lasttemplateappliedtime. */
    @JsonProperty
    public String lasttemplateappliedtime;

    /** The chassiservicetag. */
    @JsonProperty
    public String chassisservicetag;

    /** The lastmanagementtemplateapplied. */
    @JsonProperty
    public String lastmanagementtemplateapplied = "";

    /** The lastmanagementtemplateapplieddate. */
    @JsonProperty
    public String lastmanagementtemplateapplieddate = "";

    /** The lastdeploymenttemplateapplied. */
    @JsonProperty
    public String lastdeploymenttemplateapplied = "";

    /** The lastdeploymenttemplateapplieddate. */
    @JsonProperty
    public String lastdeploymenttemplateapplieddate = "";

    @JsonProperty
    public List<UIPortViewPort> ports2;

    @JsonProperty
    public String createdBy;

    @JsonProperty
    public String createdOn;

    @JsonProperty
    public String bootLUN;

    @JsonProperty
    public String osBootInfo;

    @JsonProperty
    public List<UIAlertLog> alertLogs;

    @JsonProperty
    public List<UIActivityLog> activityLogs;

    @JsonProperty
    public UIUsageData systemusage;

    @JsonProperty
    public UIUsageData cpuusage;

    @JsonProperty
    public UIUsageData memoryusage;

    @JsonProperty
    public UIUsageData iousage;

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public String serviceName;

    @JsonProperty
    public List<UILocalStorage> localstoragedata;

    /**
     * Instantiates a new uI server.
     */
    public UIServer() {
        super();
        this.nics = new ArrayList<>();
        this.firmwares = new ArrayList<>();
        this.softwares = new ArrayList<>();
        this.cpudata = new ArrayList<>();
        this.ports = new ArrayList<>();
        this.ports2 = new ArrayList<>();
        this.alertLogs = new ArrayList<>();
        this.activityLogs = new ArrayList<>();
        this.memorydata = new ArrayList<>();
        this.localstoragedata = new ArrayList<>();
    }

}
