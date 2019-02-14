package com.dell.asm.ui.model.activedirectory;

import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Mike Condon
 * Date Nov 29, 2013 @12:55:47 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIActiveDirectoryGroup extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String distinguishedName;

    @JsonProperty
    public String description;

    @JsonProperty
    public List<UIActiveDirectoryUser> users;

    @JsonProperty
    public String roleId;

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
    public String locale;

    @JsonProperty
    public String rolename;

    @JsonProperty
    public String serverName;

}