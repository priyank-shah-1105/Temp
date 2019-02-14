package com.dell.asm.ui.model.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.dell.asm.ui.model.ErrorObj;
import com.dell.asm.ui.model.FieldError;
import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class UITemplateBuilderComponent.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITemplateBuilderComponent extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    @JsonProperty
    public String type;

    @JsonProperty
    public String subtype;

    @JsonProperty
    public String componentid;

    @JsonProperty
    public String identifier;

    @JsonProperty
    public String helptext;

    @JsonProperty
    public List<UIRelatedComponent> relatedcomponents;

    @JsonProperty
    public List<UITemplateBuilderCategory> categories;

    @JsonProperty
    public String referenceid;

    @JsonProperty
    public String referenceip;

    @JsonProperty
    public String referenceipurl;

    @JsonProperty
    public Boolean showNetworkInfo;

    @JsonProperty
    public UIComponentNetwork network;

    @JsonProperty
    public Boolean newComponent;

    @JsonProperty
    public Boolean cloned;

    @JsonProperty
    public Boolean continueClicked;

    @JsonProperty
    public String AsmGUID;

    @JsonProperty
    public String puppetCertName;

    @JsonProperty
    public String clonedFromId;

    @JsonProperty
    public boolean allowClone;

    @JsonProperty
    public boolean isComponentValid;

    @JsonProperty
    public UIComponentRaid raid;

    @JsonProperty
    public String configfilename;

    @JsonProperty
    public int instances;

    @JsonProperty
    public ErrorObj errorObj;

    public UITemplateBuilderComponent() {
        super();
        this.categories = new ArrayList<>();
        this.relatedcomponents = new ArrayList<>();
    }

    public static UITemplateBuilderComponent fromJSON(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, UITemplateBuilderComponent.class);
    }

    /**
     * Find tempate settings by category id and settings id.
     * @param categoryId
     * @param id
     * @return
     */
    public String findSetting(String categoryId, String id) {
        UITemplateBuilderCategory cat = findCategory(categoryId);
        for (UITemplateBuilderSetting set : cat.settings) {
            if (set.id.equals(id)) {
                return set.value;
            }
        }
        return null;
    }

    /**
     * Returns resource (aka category) by ID
     * @param resourceId
     * @return
     */
    public UITemplateBuilderCategory findCategory(String resourceId) {
        for (UITemplateBuilderCategory cat : categories) {
            if (cat.id.equals(resourceId)) {
                return cat;
            }
        }
        return null;
    }

    /**
     * Find related component by id.
     * @param id
     * @return
     */
    public UIRelatedComponent findRelatedComponent(String id) {
        if (id == null) return null;
        for (UIRelatedComponent li : relatedcomponents) {
            if (id.equals(li.id)) {
                return li;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UITemplateBuilderComponent)) return false;
        UITemplateBuilderComponent that = (UITemplateBuilderComponent) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }

    @Override
    public String toString() {
        return "UITemplateBuilderComponent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", subtype='" + subtype + '\'' +
                ", componentid='" + componentid + '\'' +
                ", helptext='" + helptext + '\'' +
                ", relatedcomponents=" + relatedcomponents +
                ", categories=" + categories +
                ", referenceid='" + referenceid + '\'' +
                ", referenceip='" + referenceip + '\'' +
                ", referenceipurl='" + referenceipurl + '\'' +
                ", showNetworkInfo=" + showNetworkInfo +
                ", network=" + network +
                ", newComponent=" + newComponent +
                ", cloned=" + cloned +
                ", continueClicked=" + continueClicked +
                ", AsmGUID='" + AsmGUID + '\'' +
                ", puppetCertName='" + puppetCertName + '\'' +
                ", clonedFromId='" + clonedFromId + '\'' +
                ", allowClone=" + allowClone +
                ", isComponentValid=" + isComponentValid +
                ", raid=" + raid +
                ", configfilename='" + configfilename + '\'' +
                '}';
    }
}
