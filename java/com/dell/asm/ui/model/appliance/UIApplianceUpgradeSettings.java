package com.dell.asm.ui.model.appliance;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIApplianceUpgradeSettings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIApplianceUpgradeSettings extends UIBaseObject {

    @JsonProperty
    public String source;

    @JsonProperty
    public String repositoryPath;

    @JsonProperty
    public String currentVersion;

    @JsonProperty
    public String availableVersion;

    @JsonProperty
    public String releaseNotesLink;

    public UIApplianceUpgradeSettings() {
        super();
    }

}
