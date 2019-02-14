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
public class UIComponentInterface extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public List<UIComponentPartition> partitions;

    public UIComponentInterface() {
        super();
        partitions = new ArrayList<>();
    }
}
