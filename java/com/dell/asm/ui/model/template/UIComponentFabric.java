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
public class UIComponentFabric extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public List<UIComponentInterface> interfaces;

    @JsonProperty
    public boolean enabled;

    @JsonProperty
    public boolean redundancy;

    @JsonProperty
    public String nictype;

    @JsonProperty
    public boolean partitioned;

    @JsonProperty
    public String fabrictype;

    public UIComponentFabric() {
        super();
        interfaces = new ArrayList<>();
    }
}
