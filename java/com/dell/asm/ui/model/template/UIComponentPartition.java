package com.dell.asm.ui.model.template;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UINode.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIComponentPartition extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public List<String> networks;

    @JsonProperty
    public String virtualidentitypool;

    @JsonProperty
    public int minimum;

    @JsonProperty
    public int maximum;

    @JsonProperty
    public boolean boot;

    @JsonProperty
    public String personality;

    public UIComponentPartition() {
        super();
        networks = new ArrayList<>();
    }
}
