package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIGettingStartedData.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIGettingStartedData extends UIBaseObject {

    @JsonProperty
    public boolean deploymentCompleted;

    @JsonProperty
    public boolean discoveryCompleted;

    @JsonProperty
    public boolean initialSetupCompleted;

    @JsonProperty
    public boolean showgettingstarted;

    @JsonProperty
    public boolean templateCompleted;

    @JsonProperty
    public boolean configurationCompleted;

    @JsonProperty
    public int discoveredResources = 0;

    @JsonProperty
    public int pendingResources = 0;

    @JsonProperty
    public int errorsResources = 0;

    @JsonProperty
    public boolean networksCompleted;

    @JsonProperty
    public boolean initialConfigurationCompleted;

    @JsonProperty
    public boolean firmwareUpdateCompleted;

    @JsonProperty
    public boolean inventoryUpdateCompleted;

    @JsonProperty
    public UIStorageUtilization storageUtilization;

    @JsonProperty
    public boolean secureRemoteServicesConfigured;

    @JsonProperty
    public boolean srsOrPhoneHomeConfigured;

    @JsonProperty
    public boolean rcmVersionAlert;

    @JsonProperty
    public boolean newVirtualApplianceVersionAlert;

    /**
     * Instantiates a new instance.
     */
    public UIGettingStartedData() {
        super();
    }
}
