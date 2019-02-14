package com.dell.asm.ui.model.server;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUsageDataPoint extends UIBaseObject {


    /** The id. */
    @JsonProperty
    public String timestamp;

    @JsonProperty
    public Double value;

}
