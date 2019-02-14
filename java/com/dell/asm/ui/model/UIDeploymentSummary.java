package com.dell.asm.ui.model;

import com.dell.asm.ui.model.server.UIServerSummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIDeploymentSummary.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDeploymentSummary extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The templatename. */
    @JsonProperty
    public String templatename;

    /** The serveripaddress. */
    @JsonProperty
    public String serveripaddress;

    /** The state. */
    @JsonProperty
    public String state;

    /** The statedetails. */
    @JsonProperty
    public String statedetails;

    /** The instantiatedby. */
    @JsonProperty
    public String instantiatedby;

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

    /** The canmigrate. */
    @JsonProperty
    public boolean canmigrate;

    /** The canpoweron. */
    @JsonProperty
    public boolean canpoweron;

    /** The canpoweroff. */
    @JsonProperty
    public boolean canpoweroff;

    /** The candeploy. */
    @JsonProperty
    public boolean candeploy;

    /** The canattach. */
    @JsonProperty
    public boolean canattach;

    /** The candetach. */
    @JsonProperty
    public boolean candetach;

    /** The candelete. */
    @JsonProperty
    public boolean candelete;

    @JsonProperty
    public UIServerSummary assignedserver;

    @JsonProperty
    public String createdOn;

    /**
     * Instantiates a new uI deployment summary.
     */
    public UIDeploymentSummary() {
        super();
        assignedserver = new UIServerSummary();
    }

}
