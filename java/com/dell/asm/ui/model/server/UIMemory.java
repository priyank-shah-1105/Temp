package com.dell.asm.ui.model.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UICPU.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIMemory {
    @JsonProperty
    public String id;

    @JsonProperty
    public String banklabel;

    @JsonProperty
    public String currentoperatingspeed;

    @JsonProperty
    public String devicetype;

    @JsonProperty
    public String devicedescription;

    @JsonProperty
    public String fqdd;

    @JsonProperty
    public String instanceid;

    @JsonProperty
    public String lastsysteminventorytime;

    @JsonProperty
    public String lastupdatetime;

    @JsonProperty
    public String manufacturedate;

    @JsonProperty
    public String manufacturer;

    @JsonProperty
    public String memorytype;

    @JsonProperty
    public String model;

    @JsonProperty
    public String partnumber;

    @JsonProperty
    public String primarystatus;

    @JsonProperty
    public String rank;

    @JsonProperty
    public String serialnumber;

    @JsonProperty
    public String size;

    @JsonProperty
    public String speed;
}
