package com.dell.asm.ui.model.configure;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.chassis.UIConfigureDeviceUser;
import com.dell.asm.ui.model.managementtemplate.UIDestinationEmail;
import com.dell.asm.ui.model.managementtemplate.UITrapSettings;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ChassisConfiguration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDeviceConfiguration extends UIBaseObject {

    @JsonProperty
    public String name;
    @JsonProperty
    public String dnsDomainName;
    @JsonProperty
    public String dataCenter;
    @JsonProperty
    public String aisle;
    @JsonProperty
    public String rack;
    @JsonProperty
    public String rackSlot;
    @JsonProperty
    public boolean registerChassisManagementControllerOnDns;
    @JsonProperty
    public boolean enableTelnet;
    @JsonProperty
    public boolean enableSsh;
    @JsonProperty
    public UITrapSettings[] trapSettings;
    @JsonProperty
    public String smtpServer;
    @JsonProperty
    public boolean smtpAuthentication;
    @JsonProperty
    public String smtpUsername;
    @JsonProperty
    public String smtpPassword;
    @JsonProperty
    public String sourceEmailName;
    @JsonProperty
    public UIDestinationEmail[] destinationEmails;
    @JsonProperty
    public List<UIConfigureDeviceUser> users;
    @JsonProperty
    public String redundancyPolicy;
    @JsonProperty
    public boolean serverPerformanceOverPowerRedundancy;
    @JsonProperty
    public boolean enableDynamicPowerSupplyEngagement;
    @JsonProperty
    public String chassisaddressingmode;
    @JsonProperty
    public String chassisnetworkid;
    @JsonProperty
    public String serveraddressingmode;
    @JsonProperty
    public String servernetworkid;
    @JsonProperty
    public String iomaddressingmode;
    @JsonProperty
    public String iomnetworkid;

    @JsonProperty
    public String chassisCredentialId;
    @JsonProperty
    public String bladeCredentialId;
    @JsonProperty
    public String iomCredentialId;
    @JsonProperty
    public String syslogDestination;
    @JsonProperty
    public String timeZone;
    @JsonProperty
    public boolean enableNTPServer;
    @JsonProperty
    public String preferredNTPServer;
    @JsonProperty
    public String secondaryNTPServer;

    @JsonProperty
    public List<UIConfigureDeviceUser> idracuserlist;
    @JsonProperty
    public boolean registeriDracOnDns;
    @JsonProperty
    public boolean enableipmi;
    @JsonProperty
    public double systemInputPowercap;
    @JsonProperty
    public String powercapMeasurementType;
    @JsonProperty
    public String configurationmode;
    @JsonProperty
    public String storageMode;
    @JsonProperty
    public String rackCredentialId;
    @JsonProperty
    public String rackAddressingMode;
    @JsonProperty
    public String rackNetworkId;

    /**
     * Constructor.
     */
    public UIDeviceConfiguration() {
        super();
        destinationEmails = new UIDestinationEmail[0];
        users = new ArrayList<>();
        idracuserlist = new ArrayList<>();
        trapSettings = new UITrapSettings[0];
    }

}
