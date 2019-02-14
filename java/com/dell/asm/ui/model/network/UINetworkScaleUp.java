package com.dell.asm.ui.model.network;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UINetworkScaleUp.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UINetworkScaleUp extends UIBaseObject {

    /** is scale up flag */
    @JsonProperty
    public boolean scaleup;

}
