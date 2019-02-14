package com.dell.asm.ui.model.device;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.users.UIUserSummary;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UIServerPool extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String description;

    @JsonProperty
    public List<UIUserSummary> users;

    @JsonProperty
    public List<UIDevice> servers;

    @JsonProperty
    public String createdby;

    @JsonProperty
    public int servercount;

    @JsonProperty
    public int usercount;

    @JsonProperty
    public String createddate;

    @JsonProperty
    public boolean isSelected = false;

    @JsonProperty
    public boolean canDelete;

    @JsonProperty
    public boolean canEdit;

    public UIServerPool() {
        super();
        servers = new ArrayList<UIDevice>();
        users = new ArrayList<UIUserSummary>();

    }

}
