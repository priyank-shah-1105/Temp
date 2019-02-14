package com.dell.asm.ui.model.activedirectory;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Sood</a>
 *
 * Date Nov 29, 2013 @12:55:47 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIActiveDirectoryUser extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String distinguishedName;

    @JsonProperty
    public String username;

    @JsonProperty
    public String groupName;

    @JsonProperty
    public String roleId;

    @JsonProperty
    public String lastname;

    @JsonProperty
    public String firstname;

    @JsonProperty
    public String enabled;

    @JsonProperty
    public String state;

    @JsonProperty
    public String roleDisplay;

    @JsonProperty
    public String canedit;

    @JsonProperty
    public String candelete;

    @JsonProperty
    public String isselected;

    @JsonProperty
    public String type;

    @JsonProperty
    public String group;

    @JsonProperty
    public boolean isGroup;

    @JsonProperty
    public String lastlogin;

    @JsonProperty
    public String email;

    @JsonProperty
    public String serverName;

    @JsonProperty
    public String locale;

    @JsonProperty
    public String rolename;


}