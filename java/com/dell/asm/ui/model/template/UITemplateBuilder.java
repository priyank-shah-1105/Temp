package com.dell.asm.ui.model.template;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.configuretemplate.UIConfigureTemplateConfiguration;
import com.dell.asm.ui.model.users.UIUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UITemplate.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITemplateBuilder extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The description. */
    @JsonProperty
    public String description;

    @JsonProperty
    public String createdBy;

    @JsonProperty
    public String createdDate;

    @JsonProperty
    public String updatedBy;

    @JsonProperty
    public String updatedDate;

    @JsonProperty
    public boolean draft;

    @JsonProperty
    public List<UITemplateBuilderComponent> components;

    @JsonProperty
    public List<UIListItem> attachments;

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
    public boolean allStandardUsers;

    @JsonProperty
    public List<UIUser> assignedUsers;

    @JsonProperty
    public boolean isValid;

    @JsonProperty
    public boolean manageFirmware;

    @JsonProperty
    public String firmwarePackageId;

    @JsonProperty
    public String firmwarePackageName;

    @JsonProperty
    public boolean updateServerFirmware;

    @JsonProperty
    public boolean updateNetworkFirmware;

    @JsonProperty
    public boolean updateStorageFirmware;

    @JsonProperty
    public boolean isLocked;

    @JsonProperty
    public UIConfigureTemplateConfiguration configureTemplateConfiguration;

    @JsonProperty
    public String source;

    @JsonProperty
    public boolean inConfiguration = false;

    @JsonProperty
    public String originalId;

    public UITemplateBuilder() {
        super();
        components = new ArrayList<>();
        attachments = new ArrayList<>();
        assignedUsers = new ArrayList<>();
    }

}
