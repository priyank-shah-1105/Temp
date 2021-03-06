package com.dell.asm.ui.model.template;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class VSwitch.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIComponentVSwitch extends UIBaseObject {

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
    public String interfaceId;

    public UIComponentVSwitch() {
        super();
        networks = new ArrayList<>();
    }
}
