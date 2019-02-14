package com.dell.asm.ui.model.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDashboard extends UIBaseObject {
    @JsonProperty
    public List<UIDeploymentTemplate> deploymentTemplates;
    @JsonProperty
    public UILicenseData licenseData;
    @JsonProperty
    public long totalIpAddressesAvailable;
    @JsonProperty
    public long totalIpAddressesInUse;
    @JsonProperty
    public long totalServersAvailable;
    @JsonProperty
    public long totalServersInDeployments;
    @JsonProperty
    public long chassisdiscovered;
    @JsonProperty
    public long serversdiscovered;
    @JsonProperty
    public long switchesdiscovered;
    @JsonProperty
    public long storagediscovered;


    public UIDashboard() {
        super();
        this.deploymentTemplates = new ArrayList<UIDeploymentTemplate>();
    }
}
