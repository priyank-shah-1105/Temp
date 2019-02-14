package com.dell.asm.ui.model.device;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UINetworkResource
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UINetworkResource extends UIBaseObject {


    @JsonProperty
    public String resourcename;

    /**
     * Server. vm
     */
    @JsonProperty
    public String resourcetype;

    @JsonProperty
    public List<String> networks;

    @JsonProperty
    public String id;

    @JsonProperty
    public boolean serviceHasVDS;

    public UINetworkResource() {
        super();
        networks = new ArrayList<>();
    }
}
