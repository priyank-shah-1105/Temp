package com.dell.asm.ui.model.firmware;


import com.fasterxml.jackson.annotation.JsonProperty;

public class UIFirmwareBundleFormPost {

    @JsonProperty
    public String packageId;

    /** The bundle name. */
    @JsonProperty
    public String bundleId;

    /** The bundle description. */
    @JsonProperty
    public String bundleName;

    @JsonProperty
    public String bundleDescription;

    @JsonProperty
    public String bundleVersion;

    @JsonProperty
    public String bundleSize;

    @JsonProperty
    public String deviceType;

    @JsonProperty
    public String deviceModel;

    @JsonProperty
    public String criticality;

    @JsonProperty
    public String file;

    public UIFirmwareBundleFormPost() {
        super();

    }

}
