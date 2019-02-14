package com.dell.asm.ui.model.appliance;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUpdateVirtualAppliance extends UIBaseObject {

    @JsonProperty
    public boolean updateAppliance;

    @JsonProperty
    public String repositoryPath;

}
