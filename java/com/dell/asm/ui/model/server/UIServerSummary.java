package com.dell.asm.ui.model.server;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIServerSummary.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIServerSummary extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /**
     * The health. Proper values green, yellow, red
     */
    @JsonProperty
    public String health;

    /** The ipaddress. */
    @JsonProperty
    public String ipaddress;

    /** The servicetag. */
    @JsonProperty
    public String servicetag;

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

    /** The statedetails. */
    @JsonProperty
    public String statedetails;

    /** The slotnumber. */
    @JsonProperty
    public String slotnumber;

    /** The canremove. */
    @JsonProperty
    public boolean canremove;

    /** The iscompliant. */
    @JsonProperty
    public boolean iscompliant;

    /** The compliancestatus. */
    @JsonProperty
    public String compliancestatus = "";

    /** The deployment. */
    @JsonProperty
    public String deployment;

    /** The canpoweron. */
    @JsonProperty
    public boolean canpoweron;

    /** The canpoweroff. */
    @JsonProperty
    public boolean canpoweroff;

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

    /** The canresetcertificate. */
    @JsonProperty
    public boolean canresetcertificate;

    @JsonProperty
    public String profilename;

    @JsonProperty
    public String hostname;

    @JsonProperty
    public String location;

    @JsonProperty
    public String powerstate;


    public boolean ipStatic; // not a JSON property

    /**
     * Instantiates a new uI server summary.
     */
    public UIServerSummary() {
        super();
    }

}
