package com.dell.asm.ui.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.template.UITemplateBuilder;
import com.dell.asm.ui.model.users.UIUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDeploy extends UIBaseObject {
    @JsonProperty
    public String id;

    @JsonProperty
    public String serviceName;

    @JsonProperty
    public String serviceDescription;

    @JsonProperty
    public UITemplateBuilder template;

    @JsonProperty
    public Date scheduleDate;

    @JsonProperty
    public String scheduleType;

    @JsonProperty
    public boolean sendnotification;

    @JsonProperty
    public String emaillist;

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

    @JsonProperty
    public String numberOfDeployments = "1";

    public UIDeploy() {
        super();
        assignedUsers = new ArrayList<>();
    }
}
