package com.dell.asm.ui.model.device;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.asmcore.asmmanager.client.deviceinventory.CompliantState;
import com.dell.asm.ui.model.firmware.UIFirmware;
import com.dell.asm.ui.model.server.UIIPAddress;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIDevice. id; health; ipAddress; serviceTag; profile; deviceType; state; template
 *; compliant : boolean
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDevice extends UIDeviceSummary {

    @JsonProperty
    public String name;

    @JsonProperty
    public String resourcename;

    @JsonProperty
    public List<String> partitions;

    @JsonProperty
    public String profile;

    @JsonProperty
    public String statusText;

    @JsonProperty
    public String firmwareName;

    @JsonProperty
    public String template;

    @JsonProperty
    public String lastInventory;

    @JsonProperty
    public String lastComplianceCheck;

    @JsonProperty
    public String discoveredOn;

    @JsonProperty
    public int blades;

    @JsonProperty
    public boolean canpoweron;

    @JsonProperty
    public boolean canpoweroff;

    @JsonProperty
    public boolean candelete;

    @JsonProperty
    public String os;

    @JsonProperty
    public String storage;

    @JsonProperty
    public String ipaddresses;

    @JsonProperty
    public String ipaddresstype;

    @JsonProperty
    public String powerstate;

    @JsonProperty
    public String location;

    @JsonProperty
    public String systemstatus;

    @JsonProperty
    public String groupmembers;

    @JsonProperty
    public String volumes;

    @JsonProperty
    public String volumesup;

    @JsonProperty
    public String deviceidtype;

    @JsonProperty
    public String snapshots;

    @JsonProperty
    public String freegroupspace;

    @JsonProperty
    public String serialnumber;

    @JsonProperty
    public String datacenters;

    @JsonProperty
    public List<String> networks;

    @JsonProperty
    public String clusters;

    @JsonProperty
    public String hosts;

    @JsonProperty
    public String virtualmachines;

    @JsonProperty
    public String dnsname;

    @JsonProperty
    public String cpu;

    @JsonProperty
    public String freediskspace;

    @JsonProperty
    public String online;

    @JsonProperty
    public String luns;

    @JsonProperty
    public String hostgroups;

    @JsonProperty
    public List<UIVolume> volumelist;

    @JsonProperty
    public String serverpool;

    @JsonProperty
    public int nics = 0;

    @JsonProperty
    public String hypervisorIPAddress;

    @JsonProperty
    public String aggregates;

    @JsonProperty
    public String disks;

    @JsonProperty
    public String vm_hostname = "";

    @JsonProperty
    public String vm_ostype = "";

    @JsonProperty
    public String vm_cpus = "";

    @JsonProperty
    public String vm_disksize = "";

    @JsonProperty
    public String vm_memory = "";

    @JsonProperty
    public List<UIIPAddress> ipaddresslist;

    @JsonProperty
    public List<UIFirmware> firmwarecomponents;

    @JsonProperty
    public boolean showcompliancereport;

    @JsonProperty
    public String spaipaddress;

    @JsonProperty
    public String spaipaddressurl;

    @JsonProperty
    public String spbipaddress;

    @JsonProperty
    public String spbipaddressurl;

    @JsonProperty
    public String datacentername;

    @JsonProperty
    public String clustername;

    @JsonProperty
    public String asmGUID;

    @JsonProperty
    public Object deviceDetails;

    @JsonProperty
    public String protectionDomain;

    @JsonProperty
    public List<UIScaleIOStoragePool> storagePools;

    @JsonProperty
    public String osMode;

    @JsonProperty
    public String mdmRole;

    @JsonProperty
    public String vxflexosmanagementipaddress;

    @JsonProperty
    public String primarymdmipaddress;

    @JsonProperty
    public String brownfieldStatus;

    /**
     * Instantiates a new uI device summary.
     */
    public UIDevice() {
        super();
        volumelist = new ArrayList<>();
        firmwarecomponents = new ArrayList<>();
        networks = new ArrayList<>();
        servicelist = new ArrayList<>();
        ipaddresslist = new ArrayList<>();
        storagePools = new ArrayList<>();

        this.memory = "N/A";
        this.processorcount = -1;
        this.ipaddresstype = "management";
        this.deviceidtype = "servicetag";
        this.ipaddressurl = "";
        this.compliant = CompliantState.UNKNOWN.getLabel();
    }

    /**
     * Returns true if a Volume with the given name exists or false if it is not found.
     * @param name the name of the volume to find.
     * @return true if a Volume with the given name exists or false if it is not found.
     */
    public boolean containsVolumeWithName(String name) {
        boolean containsVolume = false;
        if (this.volumelist != null && name != null) {
            for (UIVolume uiVolume : this.volumelist) {
                if (name.equals(uiVolume.name)) {
                    containsVolume = true;
                    break;
                }
            }
        }
        return containsVolume;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UIDevice [id=" + id + ", health=" + health + ", ipaddress=" + ipAddress + ", serviceTag=" + serviceTag + ", profile=" + profile + "]";
    }

}
