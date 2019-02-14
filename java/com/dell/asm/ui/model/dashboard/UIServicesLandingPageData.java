package com.dell.asm.ui.model.dashboard;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIServicesLandingPageData extends UIBaseObject {

    @JsonProperty
    public int servicecount;

    @JsonProperty
    public int servicecriticalcount;

    @JsonProperty
    public int servicesuccesscount;

    @JsonProperty
    public int serviceunknowncount;

    @JsonProperty
    public int servicewarningcount;

    @JsonProperty
    public int servicependingcount;

    @JsonProperty
    public int servicecancelledcount;

    @JsonProperty
    public int serviceservicemodecount;

    @JsonProperty
    public int serviceincompletecount;

    public UIServicesLandingPageData() {
        super();
    }
}
