package com.dell.asm.ui.model.dashboard;

import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDeploymentTemplate extends UIBaseObject {

    @JsonProperty
    public String id;
    @JsonProperty
    public String templateName;
    @JsonProperty
    public String templateDescription;
    @JsonProperty
    public String serverTemplateModelId;
    @JsonProperty
    public String numberofcpus_operator;
    @JsonProperty
    public int numberofcpus;
    @JsonProperty
    public String memory_operator;
    @JsonProperty
    public int memory;
    @JsonProperty
    public String cpustype;
    @JsonProperty
    public int repositoryId;
    @JsonProperty
    public String firmwareVersion;
    @JsonProperty
    public String updateFirmware;
    @JsonProperty
    public boolean includefirmwareupdate;
    @JsonProperty
    public String state;
    @JsonProperty
    public String serverHardwareTemplateId;
    @JsonProperty
    public String osBootTypeId;
    @JsonProperty
    public String osBootTypeDisplay;
    @JsonProperty
    public String osBootVirtualNicConfigId;
    @JsonProperty
    public String networkShareTypeId;
    @JsonProperty
    public String networkShareLocation;
    @JsonProperty
    public String networkShareUsername;
    @JsonProperty
    public String networkSharePassword;
    @JsonProperty
    public List<UIVirtualNIC> virtualnics;
    @JsonProperty
    public String nativeVlanId;
    @JsonProperty
    public String storageDeviceId;
    @JsonProperty
    public String createdBy;
    @JsonProperty
    public String creationTime;
    @JsonProperty
    public String lastUpdatedBy;
    @JsonProperty
    public String updateTime;
    @JsonProperty
    public boolean draft; // (On create this should be true...)
    @JsonProperty
    public int currentWizardStep;
    @JsonProperty
    public List<UIDeploymentSummary> activedeployments;
    @JsonProperty
    public boolean enabled;
    @JsonProperty
    public boolean canEnable;
    @JsonProperty
    public boolean canDisable;
    @JsonProperty
    public boolean canDeploy;
    @JsonProperty
    public boolean includebootorder;
    @JsonProperty
    public String enablebootorderretry;
    @JsonProperty
    public String bootmode;
    @JsonProperty
    public String primarybootdevice;
    @JsonProperty
    public String secondarybootdevice;
    @JsonProperty
    public List<UIBootDeviceSequence> bootdevicesequencelist;
    @JsonProperty
    public boolean includebiosconfiguration;
    @JsonProperty
    public List<UIBiosSettings> biossettings;
    @JsonProperty
    public boolean includeraidconfiguration;
    @JsonProperty
    public String raidconfigurationtype;
    @JsonProperty
    public String basicraidconfiguration;
    @JsonProperty
    public String selectedcloneraidconfigurationid;
    @JsonProperty
    public UIRaidConfiguration cloneraidconfiguration;
    @JsonProperty
    public UIDiskConfiguration advanceddiskconfiguration;
    @JsonProperty
    public boolean includenetwork;
    @JsonProperty
    public boolean includebandwidthoversub;
    @JsonProperty
    public String mappingorder;
    @JsonProperty
    public List<UIListItem> mappingorderlist;
    @JsonProperty
    public String virtualbootnic;
    @JsonProperty
    public String initiatoripaddress;
    @JsonProperty
    public boolean associateddeployments;
    @JsonProperty
    public String serversavailable;
    @JsonProperty
    public boolean includebootinfo;
    /*
     * Date Added: 11/08/2013
     * Notes: numberofcpus_operator : "exact", "atleast", or "atmost"
     * memory_operator : "exact", "atleast", or "atmost"
     * cpustype : "sockets" or "cores"
     * firmwareVersion : value options are "latest", "oneversionbehind"
     * updateFirmware : value options are "alwaysupdate", "keepfirmware"
     * Added serversavailable (servers available to deploy)
     * Added advanceddiskconfiguration
     * Added primary/secondary boot devices
     * Added includefirmwareupdate
     */
}
