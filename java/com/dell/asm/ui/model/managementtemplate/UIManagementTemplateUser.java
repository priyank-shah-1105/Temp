package com.dell.asm.ui.model.managementtemplate;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIManagementTemplateUser.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIManagementTemplateUser extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The username. */
    @JsonProperty
    public String username;

    /** The password. */
    @JsonProperty
    public String password;

    /** The role. */
    @JsonProperty
    public String role;

    /** The enabled. */
    @JsonProperty
    public boolean enabled;

    /** The templateid. */
    @JsonProperty
    public String templateid;

    /** The confirmpassword. */
    @JsonProperty
    public String confirmpassword;

    /** The state. */
    @JsonProperty
    public boolean state;

    /** The lan. */
    @JsonProperty
    public String lan;

    /** The serialoverlan. */
    @JsonProperty
    public boolean serialoverlan;

}
