package com.dell.asm.ui.model.template;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UITemplateBuilderCategory.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITemplateBuilderCategory extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    @JsonProperty
    public List<UITemplateBuilderSetting> settings;


    public UITemplateBuilderCategory() {
        super();
        settings = new ArrayList<UITemplateBuilderSetting>();
    }

    /**
     * Returns the UITemplateBuilderSetting with the given id or null if a match cannot be found.
     *
     * @param id of the UITemplateBuilderSetting to return.
     * @return the UITemplateBuilderSetting with the given id or null if a match cannot be found.
     */
    public UITemplateBuilderSetting getUITemplateBuilderSetting(String id) {
        UITemplateBuilderSetting foundUiBuilderTemplateSetting = null;

        for (UITemplateBuilderSetting uiTemplateBuilderSetting : this.settings) {
            if (id.equals(uiTemplateBuilderSetting.id)) {
                foundUiBuilderTemplateSetting = uiTemplateBuilderSetting;
                break;
            }
        }

        return foundUiBuilderTemplateSetting;
    }


}
