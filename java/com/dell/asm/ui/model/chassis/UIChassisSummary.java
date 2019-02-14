package com.dell.asm.ui.model.chassis;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ChassisConfiguration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassisSummary extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The health. */
    @JsonProperty
    public String health;

    /** The ipaddress. */
    @JsonProperty
    public String ipaddress;

    /** The servicetag. */
    @JsonProperty
    public String servicetag;

    /** The numberofblades. */
    @JsonProperty
    public Integer numberofblades;

    /** The lasttemplateapplied. */
    @JsonProperty
    public String lasttemplateapplied;

    /** The state. */
    @JsonProperty
    public String state;

    /** The statedetails. */
    @JsonProperty
    public String statedetails;

    /** The iscompliant. */
    @JsonProperty
    public boolean iscompliant;

    /** The compliancestatus. */
    @JsonProperty
    public String compliancestatus;

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

    /** The canremove. */
    @JsonProperty
    public boolean canremove;

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

    /** The canconfigure. */
    @JsonProperty
    public boolean canconfigure;

    /**
     * Instantiates a new uI chassis summary.
     */
    public UIChassisSummary() {
        super();
    }

}
