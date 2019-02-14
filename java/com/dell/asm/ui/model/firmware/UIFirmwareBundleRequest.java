package com.dell.asm.ui.model.firmware;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIFirmwareBundleRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIFirmwareBundleRequest extends UIBaseObject {

    @JsonProperty
    public String firmwarePackageId;

    @JsonProperty
    public String firmwareBundleId;

}
