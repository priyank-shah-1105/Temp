package com.dell.asm.ui.model.server;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UICPU.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UICPU extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The manufacturer. */
    @JsonProperty
    public String manufacturer;

    /** The model. */
    @JsonProperty
    public String model;

    /** The cores. */
    @JsonProperty
    public String cores;

    /** The enabledcores. */
    @JsonProperty
    public String enabledcores;

    /** The maxclockspeed. */
    @JsonProperty
    public String maxclockspeed;

    /** The currentspeed. */
    @JsonProperty
    public String currentspeed;

}
