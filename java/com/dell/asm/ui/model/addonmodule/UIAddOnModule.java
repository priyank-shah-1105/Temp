package com.dell.asm.ui.model.addonmodule;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIAddOnModule extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String description;

    @JsonProperty
    public String filename;

    @JsonProperty
    public String filepath;

    @JsonProperty
    public String version;

    @JsonProperty
    public boolean isInUse;

    @JsonProperty
    public String uploadedBy;

    @JsonProperty
    public String uploadedDate;

    @JsonProperty
    public String username;

    @JsonProperty
    public String password;

    @JsonProperty
    public List<UIListItem> templates;

    @JsonProperty
    public List<UIListItem> services;

    public UIAddOnModule() {
        super();
        templates = new ArrayList<>();
        services = new ArrayList<>();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UIAddOnModule [id=").append(id)
                .append(", name=").append(name)
                .append(", description=").append(description)
                .append(", filename=").append(filename)
                .append(", filepath=").append(filepath)
                .append(", version=").append(version)
                .append(", isInUse=").append(isInUse)
                .append(", uploadedBy=").append(uploadedBy)
                .append(", uploadedDate=").append(uploadedDate)
                .append(", username=").append(username)
                .append(", password=").append(password)
                .append("]");
        return builder.toString();
    }


}
