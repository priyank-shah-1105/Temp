package com.dell.asm.ui.model.managementtemplate;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIManagementTemplateSummary.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIManagementTemplateSummary extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The status. */
    @JsonProperty
    public String status;

    /** The name. */
    @JsonProperty
    public String name;

    /** The description. */
    @JsonProperty
    public String description;

    /** The last updated by. */
    @JsonProperty
    public String lastUpdatedBy;

    /** The state. */
    @JsonProperty
    public String state;

    /** The usage. */
    @JsonProperty
    public int usage;

    /** The draft mode. */
    @JsonProperty
    public boolean draftMode;

    /** The candelete. */
    @JsonProperty
    public boolean candelete;

    /** The cancopy. */
    @JsonProperty
    public boolean cancopy;

    /** The canedit. */
    @JsonProperty
    public boolean canedit;

    /** The templatetype. */
    @JsonProperty
    public String templatetype;

}
