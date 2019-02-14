package com.dell.asm.ui.model.managementtemplate;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIFirmwareBundles.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIFirmwareBundles extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The bundle name. */
    @JsonProperty
    public String bundleName;

    /** The bundle description. */
    @JsonProperty
    public String bundleDescription;


}
