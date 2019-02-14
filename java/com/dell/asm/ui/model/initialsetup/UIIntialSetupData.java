package com.dell.asm.ui.model.initialsetup;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.appliance.UIDhcpData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIIntialSetupData.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIIntialSetupData extends UIBaseObject {

    /** The initial setup complete. */
    @JsonProperty
    public boolean initialSetupComplete;

    /** The current wizard step. */
    @JsonProperty
    public int currentWizardStep;

    /** The license data. */
    @JsonProperty
    public UILicenseData licenseData;

    /** The proxy data. */
    @JsonProperty
    public UIProxyData proxyData;

    /** The time data. */
    @JsonProperty
    public UITimeData timeData;

    /** The dhcp data. */
    @JsonProperty
    public UIDhcpData dhcpData;

    public UIIntialSetupData() {
        super();
        licenseData = new UILicenseData();
        proxyData = new UIProxyData();
        timeData = new UITimeData();
        dhcpData = new UIDhcpData();
    }

}
