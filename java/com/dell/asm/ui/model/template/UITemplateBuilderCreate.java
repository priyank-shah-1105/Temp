package com.dell.asm.ui.model.template;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.users.UIUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UITemplateBuilderCreate extends UIBaseObject {


    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String description;

    @JsonProperty
    public String cloneexistingtemplateid;

    @JsonProperty
    public String category;

    @JsonProperty
    public boolean enableApps;

    @JsonProperty
    public boolean enableVMs;

    @JsonProperty
    public boolean enableCluster;

    @JsonProperty
    public boolean enableServer;

    @JsonProperty
    public boolean enableStorage;

    @JsonProperty
    public boolean manageFirmware;

    @JsonProperty
    public String firmwarePackageId;

    @JsonProperty
    public boolean updateServerFirmware;

    @JsonProperty
    public boolean updateNetworkFirmware;

    @JsonProperty
    public boolean updateStorageFirmware;

    @JsonProperty
    public boolean allStandardUsers;

    @JsonProperty
    public List<UIUser> assignedUsers;

    public UITemplateBuilderCreate() {
        super();
        assignedUsers = new ArrayList<>();
    }


}
