package com.dell.asm.ui.model.configuretemplate;

import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIListItem.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIConfigureTemplateListItem extends UIListItem {

    @JsonProperty
    public String dependencyTarget;

    @JsonProperty
    public String dependencyValue;

    /**
     * Instantiates a new uI list item.
     */
    public UIConfigureTemplateListItem() {
        super();
    }

    public UIConfigureTemplateListItem(String id,
                                       String name,
                                       String dependencyTarget,
                                       String dependencyValue) {
        super(id, name);
        this.dependencyTarget = dependencyTarget;
        this.dependencyValue = dependencyValue;
    }
}
