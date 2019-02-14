package com.dell.asm.ui.model.dashboard;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UILicenseData extends UIBaseObject {
    @JsonProperty
    public String activationdate;

    @JsonProperty
    public int availablenodes;

    @JsonProperty
    public int currentWizardStep;

    @JsonProperty
    public String expirationdate;

    @JsonProperty
    public boolean expired;

    @JsonProperty
    public boolean expiressoon;

    @JsonProperty
    public String expiressoonmessage;

    @JsonProperty
    public boolean force;

    @JsonProperty
    public boolean isValid;

    @JsonProperty
    public String licensefile;

    @JsonProperty
    public String signature;

    @JsonProperty
    public String softwareservicetag;

    @JsonProperty
    public int totalnodes;

    @JsonProperty
    public String type;

    @JsonProperty
    public int usednodes;

    @JsonProperty
    public String warningmessage;

    public UILicenseData() {
        super();
    }
}
