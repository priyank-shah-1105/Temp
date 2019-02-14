package com.dell.asm.ui.model.template;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.device.UIDevice;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIValidateResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIValidateResponse extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public int totalservers;

    @JsonProperty
    public List<UIDevice> devices;

    public UIValidateResponse() {
        super();
        devices = new ArrayList<>();
    }
}
