package com.dell.asm.ui.model.activedirectory;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Sood</a>
 *
 * Date Nov 29, 2013 @12:55:47 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIImportUser extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String userName;

    @JsonProperty
    public String roleId;

    @JsonProperty
    public String lastname;

    @JsonProperty
    public String firstname;

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