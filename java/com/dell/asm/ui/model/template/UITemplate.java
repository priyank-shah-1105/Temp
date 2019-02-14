package com.dell.asm.ui.model.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.dell.asm.ui.model.UIActivityLog;
import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.UIDeploymentSummary;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.network.UIVirtualNIC;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UITemplate.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITemplate extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The description. */
    @JsonProperty
    public String description;

    /** The state. */
    @JsonProperty
    public String state;

    /** The state. */
    @JsonProperty
    public String type;

    @JsonProperty
    public String createdBy;

    @JsonProperty
    public String createdDate;

    @JsonProperty
    public String updatedBy;

    @JsonProperty
    public String updatedDate;

    @JsonProperty
    public boolean isLocked;

    @JsonProperty
    public boolean draft;

    @JsonProperty
    public boolean inConfiguration;

    @JsonProperty
    public boolean containsApplication = false;

    @JsonProperty
    public boolean containsCluster = false;

    @JsonProperty
    public boolean containsServer = false;

    @JsonProperty
    public boolean containsVM = false;

    @JsonProperty
    public boolean containsSwitch = false;

    @JsonProperty
    public boolean containsStorage = false;

    @JsonProperty
    public boolean isTemplateValid;

    @JsonProperty
    public String lastDeployed;

    @JsonProperty
    public String category;

    @JsonProperty
    public List<UIListItem> attachments;

    public UITemplate() {
        attachments = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UITemplate)) return false;
        UITemplate that = (UITemplate) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "UITemplate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", state='" + state + '\'' +
                ", type='" + type + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                ", isLocked=" + isLocked +
                ", draft=" + draft +
                ", inConfiguration=" + inConfiguration +
                ", containsApplication=" + containsApplication +
                ", containsCluster=" + containsCluster +
                ", containsServer=" + containsServer +
                ", containsVM=" + containsVM +
                ", containsSwitch=" + containsSwitch +
                ", containsStorage=" + containsStorage +
                ", isTemplateValid=" + isTemplateValid +
                ", lastDeployed='" + lastDeployed + '\'' +
                ", category='" + category + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
