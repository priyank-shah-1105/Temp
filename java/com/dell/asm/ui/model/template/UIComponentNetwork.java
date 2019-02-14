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
public class UIComponentNetwork extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public List<UIComponentFabric> interfaces;

    public UIComponentNetwork() {
        super();
        interfaces = new ArrayList<>();
    }
}
