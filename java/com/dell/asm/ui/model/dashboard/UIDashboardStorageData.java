package com.dell.asm.ui.model.dashboard;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDashboardStorageData extends UIBaseObject {

    @JsonProperty
    public int total_pctused = 0;

    @JsonProperty
    public int total_pctfree = 0;

    @JsonProperty
    public String total_label = "0 % used of 0 GB";

    @JsonProperty
    public int equallogic_pctused = 0;

    @JsonProperty
    public int equallogic_pctfree = 0;

    @JsonProperty
    public String equallogic_label = "0 % used of 0 GB";

    @JsonProperty
    public int compellent_pctused = 0;

    @JsonProperty
    public int compellent_pctfree = 0;

    @JsonProperty
    public String compellent_label = "0 % used of 0 GB";

    @JsonProperty
    public int emcvnx_pctused = 0;

    @JsonProperty
    public int emcvnx_pctfree = 0;

    @JsonProperty
    public String emcvnx_label = "0 % used of 0 GB";

    @JsonProperty
    public int emcunity_pctused = 0;

    @JsonProperty
    public int emcunity_pctfree = 0;

    @JsonProperty
    public String emcunity_label = "0 % used of 0 GB";


    public UIDashboardStorageData() {
        super();
    }
}
