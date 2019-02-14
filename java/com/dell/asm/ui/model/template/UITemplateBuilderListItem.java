package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIListItem.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITemplateBuilderListItem extends UIListItem {

    @JsonProperty
    public String dependencyTarget;

    @JsonProperty
    public String dependencyValue;

    /**
     * Instantiates a new uI list item.
     */
    public UITemplateBuilderListItem() {
        super();
    }
}
