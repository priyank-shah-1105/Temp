package com.dell.asm.ui.model.users;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIUserSummary.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUserSummary extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The username. */
    @JsonProperty
    public String username;

    /** The role id. */
    @JsonProperty
    public String roleId;

    /** The lastname. */
    @JsonProperty
    public String lastname;

    /** The firstname. */
    @JsonProperty
    public String firstname;

    /** The enabled. */
    @JsonProperty
    public boolean enabled;

    /** AD Group */
    @JsonProperty
    public String groupName;

    /** AD Group DN */
    @JsonProperty
    public String groupDN;

    /** The state. */
    @JsonProperty
    public String state;

    /** The role display. */
    @JsonProperty
    public String roleDisplay;

    /** The canedit. */
    @JsonProperty
    public boolean canedit;

    /** The candelete. */
    @JsonProperty
    public boolean candelete;

    /** The candelete. */
    @JsonProperty
    public String lastlogin;

    /** The serverName. */
    @JsonProperty
    public String serverName;

}
