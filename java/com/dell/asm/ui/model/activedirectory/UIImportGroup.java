package com.dell.asm.ui.model.activedirectory;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author <a href="mailto:Mike_Condon@dell.com">Mike Condon</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIImportGroup extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String roleId;

    @JsonProperty
    public String dn;

    @JsonProperty
    public String description;

    @JsonProperty
    public String enabled;

    @JsonProperty
    public String state;

    @JsonProperty
    public String roleDisplay;

    @JsonProperty
    public String canedit;

    @JsonProperty
    public String candelete;

}