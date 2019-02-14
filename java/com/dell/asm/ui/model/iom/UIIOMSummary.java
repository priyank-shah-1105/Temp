package com.dell.asm.ui.model.iom;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIIOMSummary.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIIOMSummary extends UIBaseObject {

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
    public String ipaddressurl = "";

    /** The servicetag. */
    @JsonProperty
    public String servicetag;

    /** The systemmodel. */
    @JsonProperty
    public String systemmodel;

    /** The numberofports. */
    @JsonProperty
    public Integer numberofports;

    /** The lasttemplateapplied. */
    @JsonProperty
    public String lasttemplateapplied;

    /** The state. */
    @JsonProperty
    public String state = "";

    /** The statedetails. */
    @JsonProperty
    public String statedetails;

    /** The canremove. */
    @JsonProperty
    public boolean canremove;

    /** The iscompliant. */
    @JsonProperty
    public boolean iscompliant;

    /** The compliancestatus. */
    @JsonProperty
    public String compliancestatus;

    /** The unsupported. */
    @JsonProperty
    public boolean unsupported;

    /** The jobid. */
    @JsonProperty
    public String jobid;

    /** The itemid. */
    @JsonProperty
    public String itemid;

    /** The canretry. */
    @JsonProperty
    public boolean canretry;

    /** The action. */
    @JsonProperty
    public String action;

    /** The canforce. */
    @JsonProperty
    public boolean canforce;

    /** The canruninventory. */
    @JsonProperty
    public boolean canruninventory;

    /** The cancheckcompliance. */
    @JsonProperty
    public boolean cancheckcompliance;

    /** The canreapplytemplate. */
    @JsonProperty
    public boolean canreapplytemplate;

    public String slot; // not a JsonProperty yet.....
    public boolean ipStatic; // not a JSON property


    /**
     * Instantiates a new uIIOM summary.
     */
    public UIIOMSummary() {
        super();
    }

}
