package com.dell.asm.ui.model.configure;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIUplink.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateUplink extends UIBaseObject {

    @JsonProperty
    public String uplinkId;
    @JsonProperty
    public String uplinkName;
    @JsonProperty
    public String portChannel;
    @JsonProperty
    public List<String> portMembers;
    @JsonProperty
    public List<String> portNetworks;

    /**
     * Constructor.
     */
    public TemplateUplink() {
        super();
        portNetworks = new ArrayList<>();
        portMembers = new ArrayList<>();
    }

}
