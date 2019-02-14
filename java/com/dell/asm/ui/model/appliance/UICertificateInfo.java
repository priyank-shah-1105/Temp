package com.dell.asm.ui.model.appliance;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UICertificateInfo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UICertificateInfo extends UIBaseObject {

    /** The content. */
    @JsonProperty
    public String content;
}
