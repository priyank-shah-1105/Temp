package com.dell.asm.ui.model.firmware;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIHierarchicalListItem extends UIBaseObject {
    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    @JsonProperty
    public List<UIListItem> children;

    public UIHierarchicalListItem() {
        super();
    }

    /**
     * Instantiates a new uI list item.
     *
     * @param id
     *            the id
     * @param name
     *            the name
     */
    public UIHierarchicalListItem(String id, String name, List C) {
        super();
        this.id = id;
        this.name = name;
        children = new ArrayList<>();
    }


}
