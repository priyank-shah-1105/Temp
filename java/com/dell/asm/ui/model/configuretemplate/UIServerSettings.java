package com.dell.asm.ui.model.configuretemplate;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIServerSettings extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public List<UIListItem> numServersList;

    @JsonProperty
    public String numServers;

    @JsonProperty
    public String existingOS;

    @JsonProperty
    public List<UIListItem> osList;

    @JsonProperty
    public String recommended;

    @JsonProperty
    public String osCredential;

    @JsonProperty
    public String serverPoolId;

    @JsonProperty
    public List<UIListItem> serverPoolOptions;

    @JsonProperty
    public String serverName;

    @JsonProperty
    public String serverNameSuffix;

    @JsonProperty
    public List<UIListItem> serverFormatOptions;

    public UIServerSettings() {
        this.numServersList = new ArrayList<>();
        this.osList = new ArrayList<>();
        this.serverPoolOptions = new ArrayList<>();
        this.serverFormatOptions = new ArrayList<>();
    }

}
