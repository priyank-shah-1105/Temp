package com.dell.asm.ui.model.managementtemplate;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.chassis.UIChassisSummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIManagementTemplate.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIManagementTemplate extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The description. */
    @JsonProperty
    public String description;

    /** The chassis name. */
    @JsonProperty
    public String chassisName;

    /** The data center. */
    @JsonProperty
    public String dataCenter;

    /** The aisle. */
    @JsonProperty
    public String aisle;

    /** The rack. */
    @JsonProperty
    public String rack;

    /** The rack slot. */
    @JsonProperty
    public String rackSlot;

    /** The network id. */
    @JsonProperty
    public String networkId;

    /** The credentials id. */
    @JsonProperty
    public String credentialsId;

    /** The register chassis management controller on dns. */
    @JsonProperty
    public boolean registerChassisManagementControllerOnDns;

    /** The use dhcp for dns domain name. */
    @JsonProperty
    public boolean useDhcpForDnsDomainName;

    /** The dns domain name. */
    @JsonProperty
    public String dnsDomainName;

    /** The chain controller vlan id enabled. */
    @JsonProperty
    public boolean chainControllerVlanIdEnabled;

    /** The chain controller vlan id. */
    @JsonProperty
    public String chainControllerVlanId;

    /** The idrac vlan id enabled. */
    @JsonProperty
    public boolean idracVlanIdEnabled;

    /** The idrac vlan id. */
    @JsonProperty
    public String idracVlanId;

    /** The enable telnet. */
    @JsonProperty
    public boolean enableTelnet;

    /** The enable ssh. */
    @JsonProperty
    public boolean enableSsh;

    /** The enable chassis alerts. */
    @JsonProperty
    public boolean enableChassisAlerts;

    /** The trap settings. */
    @JsonProperty
    public List<UITrapSettings> trapSettings;

    /** The smtp server. */
    @JsonProperty
    public String smtpServer;

    /** The source email name. */
    @JsonProperty
    public String sourceEmailName;

    /** The destination emails. */
    @JsonProperty
    public List<UIDestinationEmail> destinationEmails;

    /** The users. */
    @JsonProperty
    public List<UIManagementTemplateUser> users;

    /** The system input powercap. */
    @JsonProperty
    public float systemInputPowercap;

    /** The powercap measurement type. */
    @JsonProperty
    public String powercapMeasurementType;

    /** The redundancy policy. */
    @JsonProperty
    public String redundancyPolicy;

    /** The server performance over power redundancy. */
    @JsonProperty
    public boolean serverPerformanceOverPowerRedundancy;

    /** The enable dynamic power supply engagement. */
    @JsonProperty
    public boolean enableDynamicPowerSupplyEngagement;

    /** The created by. */
    @JsonProperty
    public String createdBy;

    /** The creation time. */
    @JsonProperty
    public String creationTime;

    /** The update time. */
    @JsonProperty
    public String updateTime;

    /** The draft mode. */
    @JsonProperty
    public boolean draftMode;

    /** The current wizard step. */
    @JsonProperty
    public int currentWizardStep;

    /** The chassis. */
    @JsonProperty
    public List<UIChassisSummary> chassis;

    /** The includefirmwareupdate. */
    @JsonProperty
    public boolean includefirmwareupdate;

    /** The selectfirmwaremode. */
    @JsonProperty
    public String selectfirmwaremode;

    /** The updatefirmwaremode. */
    @JsonProperty
    public String updatefirmwaremode;

    /** The firmware repository. */
    @JsonProperty
    public String firmwareRepository;

    /** The update firmware. */
    @JsonProperty
    public String updateFirmware;

    @JsonProperty
    public String firmwareVersion;

    /** The catalog file location. */
    @JsonProperty
    public String catalogFileLocation;

    /** The firmwarebundlemodellist. */
    @JsonProperty
    public List<UIFirmwareBundleModels> firmwarebundlemodellist;

    /** The chassisaddressingmode. */
    @JsonProperty
    public String chassisaddressingmode;

    /** The chassisnetworkid. */
    @JsonProperty
    public String chassisnetworkid;

    /** The serveraddressingmode. */
    @JsonProperty
    public String serveraddressingmode;

    /** The servernetworkid. */
    @JsonProperty
    public String servernetworkid;

    /** The iomaddressingmode. */
    @JsonProperty
    public String iomaddressingmode;

    /** The iomnetworkid. */
    @JsonProperty
    public String iomnetworkid;

    /** The chassis credential id. */
    @JsonProperty
    public String chassisCredentialId;

    /** The server credential id. */
    @JsonProperty
    public String serverCredentialId;

    /** The iom credential id. */
    @JsonProperty
    public String iomCredentialId;

    /** The idracuserlist. */
    @JsonProperty
    public List<UIManagementTemplateUser> idracuserlist;

    /** The syslog destination. */
    @JsonProperty
    public String syslogDestination;

    /** The time zone. */
    @JsonProperty
    public String timeZone;

    /** The enable ntp server. */
    @JsonProperty
    public boolean enableNTPServer;

    /** The preferred ntp server. */
    @JsonProperty
    public String preferredNTPServer;

    /** The secondary ntp server. */
    @JsonProperty
    public String secondaryNTPServer;

    /** The registeri drac on dns. */
    @JsonProperty
    public boolean registeriDracOnDns;

    /** The updated by. */
    @JsonProperty
    public String updatedBy;

    /** The chassis credential name. */
    @JsonProperty
    public String chassisCredentialName;

    /** The server credential name. */
    @JsonProperty
    public String serverCredentialName;

    /** The iom credential name. */
    @JsonProperty
    public String iomCredentialName;

    /** The time zone name. */
    @JsonProperty
    public String timeZoneName;

    /** The chassisnetworkname. */
    @JsonProperty
    public String chassisnetworkname;

    /** The servernetworkname. */
    @JsonProperty
    public String servernetworkname;

    /** The iomnetworkname. */
    @JsonProperty
    public String iomnetworkname;

    /** The templatetype. */
    @JsonProperty
    public String templatetype;

    /** The smtp authentication. */
    @JsonProperty
    public boolean smtpAuthentication;

    /** The smtp username. */
    @JsonProperty
    public String smtpUsername;

    /** The smtp password. */
    @JsonProperty
    public String smtpPassword;

    /** The enableipmi. */
    @JsonProperty
    public boolean enableipmi;

    /**
     * Instantiates a new uI management template.
     */
    public UIManagementTemplate() {
        super();
        this.trapSettings = new ArrayList<UITrapSettings>();
        this.destinationEmails = new ArrayList<UIDestinationEmail>();
        this.users = new ArrayList<UIManagementTemplateUser>();
        this.chassis = new ArrayList<UIChassisSummary>();
        this.firmwarebundlemodellist = new ArrayList<UIFirmwareBundleModels>();
        this.idracuserlist = new ArrayList<UIManagementTemplateUser>();
    }

}
