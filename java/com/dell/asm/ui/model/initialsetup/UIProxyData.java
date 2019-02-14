package com.dell.asm.ui.model.initialsetup;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIProxyData extends UIBaseObject {

    @JsonProperty
    public boolean enableProxy;

    @JsonProperty
    public String proxyServer;

    @JsonProperty
    public int proxyPort;

    @JsonProperty
    public boolean enableProxyCredentials;

    @JsonProperty
    public String proxyUsername;

    @JsonProperty
    public String proxyPassword;

    @JsonProperty
    public int currentWizardStep;

    @JsonProperty
    public String proxyVerifyPassword;

}
