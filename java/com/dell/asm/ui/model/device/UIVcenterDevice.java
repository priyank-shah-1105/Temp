package com.dell.asm.ui.model.device;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UIVcenterDevice extends UIBaseObject {

    @JsonProperty
    public String deviceid;

    @JsonProperty
    public String name;

    @JsonProperty
    public String ipaddress;

    @JsonProperty
    public String type;


    @JsonProperty
    public List<UIVcenterDevice> children;

    /**
     * Instantiates a new uI device summary.
     */
    public UIVcenterDevice() {
        super();
        children = new ArrayList<UIVcenterDevice>();

    }


}
