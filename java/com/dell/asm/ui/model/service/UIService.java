package com.dell.asm.ui.model.service;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIActivityLog;
import com.dell.asm.ui.model.device.UIDevice;
import com.dell.asm.ui.model.template.UIComponentStatus;
import com.dell.asm.ui.model.users.UIUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIService.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIService extends UIServiceSummary {

    @JsonProperty
    public String expires;
    @JsonProperty
    public int servers;
    @JsonProperty
    public int ram;
    @JsonProperty
    public int processors;
    @JsonProperty
    public int arrays;
    @JsonProperty
    public int volumes;
    @JsonProperty
    public int vlans;
    @JsonProperty
    public String networktype;
    @JsonProperty
    public int vms;
    @JsonProperty
    public int clusters;
    @JsonProperty
    public String type;
    @JsonProperty
    public String state;
    @JsonProperty
    public String createddate;
    @JsonProperty
    public String createdBy;
    @JsonProperty
    public String priority;
    @JsonProperty
    public int compute;
    @JsonProperty
    public int storage;
    @JsonProperty
    public int network;
    @JsonProperty
    public List<UIDevice> serverlist;
    @JsonProperty
    public List<UIDevice> storagelist;
    @JsonProperty
    public List<UIDevice> networklist;
    @JsonProperty
    public List<UIDevice> clusterlist;
    @JsonProperty
    public List<UIDevice> applicationlist;
    @JsonProperty
    public List<UIDevice> vmlist;
    @JsonProperty
    public List<UIActivityLog> activityLogs;
    @JsonProperty
    public String profile;
    @JsonProperty
    public List<UIComponentStatus> componentstatus;
    @JsonProperty
    public boolean canMigrate;
    @JsonProperty
    public boolean canScaleupStorage;
    @JsonProperty
    public boolean canScaleupServer;
    @JsonProperty
    public boolean canScaleupVM;
    @JsonProperty
    public boolean canScaleupCluster;
    @JsonProperty
    public boolean canScaleupApplication;
    @JsonProperty
    public boolean forceRetry;
    @JsonProperty
    public boolean allStandardUsers;
    @JsonProperty
    public List<UIUser> assignedUsers;
    @JsonProperty
    public String owner;
    @JsonProperty
    public boolean canEdit;
    @JsonProperty
    public boolean canDelete;
    @JsonProperty
    public boolean canCancel;
    @JsonProperty
    public boolean canRetry;
    @JsonProperty
    public boolean canDeleteResources;
    @JsonProperty
    public boolean manageFirmware;
    @JsonProperty
    public String firmwarePackageId;
    @JsonProperty
    public boolean updateServerFirmware;
    @JsonProperty
    public boolean updateNetworkFirmware;
    @JsonProperty
    public boolean updateStorageFirmware;
    @JsonProperty
    public boolean componentUpdateRequired;
    @JsonProperty
    public boolean canScaleupNetwork;
    @JsonProperty
    public String templateId;
    @JsonProperty
    public UIServiceHealth resourceHealth; // same value set as for "health"
    @JsonProperty
    public boolean isHyperV;
    @JsonProperty
    public boolean hasVDS;
    @JsonProperty
    public boolean cancelInprogress = false;
    @JsonProperty
    public List<UIDevice> scaleiolist;
    @JsonProperty
    public boolean enableServiceMode;

    @JsonProperty
    public boolean canUpdateInventory;

    public UIService() {
        super();
        serverlist = new ArrayList<>();
        storagelist = new ArrayList<>();
        networklist = new ArrayList<>();
        clusterlist = new ArrayList<>();
        vmlist = new ArrayList<>();
        activityLogs = new ArrayList<>();
        applicationlist = new ArrayList<>();
        componentstatus = new ArrayList<>();
        assignedUsers = new ArrayList<>();
        scaleiolist = new ArrayList<>();
    }

    public UIComponentStatus getComponentStatus(String componentId) {
        UIComponentStatus uiCompStatus = null;

        if (componentId != null && this.componentstatus != null && !this.componentstatus.isEmpty()) {
            for (UIComponentStatus uics : this.componentstatus) {
                if (componentId.equals(uics.componentid)) {
                    uiCompStatus = uics;
                    break;
                }
            }
        }

        return uiCompStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIService)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "UIService{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", template='" + template + '\'' +
                ", health=" + health +
                ", deployedBy='" + deployedBy + '\'' +
                ", deployedOn='" + deployedOn + '\'' +
                ", description='" + description + '\'' +
                ", errors='" + errors + '\'' +
                ", firmwareCompliant='" + firmwareCompliant + '\'' +
                ", firmwarePackageName='" + firmwarePackageName + '\'' +
                ", brownField=" + brownField +
                ", count_application=" + count_application +
                ", count_cluster=" + count_cluster +
                ", count_server=" + count_server +
                ", count_storage=" + count_storage +
                ", count_switch=" + count_switch +
                ", count_vm=" + count_vm +
                ", expires='" + expires + '\'' +
                ", servers=" + servers +
                ", ram=" + ram +
                ", processors=" + processors +
                ", arrays=" + arrays +
                ", volumes=" + volumes +
                ", vlans=" + vlans +
                ", networktype='" + networktype + '\'' +
                ", vms=" + vms +
                ", clusters=" + clusters +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                ", createddate='" + createddate + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", priority='" + priority + '\'' +
                ", compute=" + compute +
                ", storage=" + storage +
                ", network=" + network +
                ", serverlist=" + serverlist +
                ", storagelist=" + storagelist +
                ", networklist=" + networklist +
                ", clusterlist=" + clusterlist +
                ", applicationlist=" + applicationlist +
                ", vmlist=" + vmlist +
                ", activityLogs=" + activityLogs +
                ", profile='" + profile + '\'' +
                ", componentstatus=" + componentstatus +
                ", canMigrate=" + canMigrate +
                ", canScaleupStorage=" + canScaleupStorage +
                ", canScaleupServer=" + canScaleupServer +
                ", canScaleupVM=" + canScaleupVM +
                ", canScaleupCluster=" + canScaleupCluster +
                ", canScaleupApplication=" + canScaleupApplication +
                ", forceRetry=" + forceRetry +
                ", allStandardUsers=" + allStandardUsers +
                ", assignedUsers=" + assignedUsers +
                ", owner='" + owner + '\'' +
                ", canEdit=" + canEdit +
                ", canDelete=" + canDelete +
                ", canCancel=" + canCancel +
                ", canRetry=" + canRetry +
                ", canDeleteResources=" + canDeleteResources +
                ", manageFirmware=" + manageFirmware +
                ", firmwarePackageId='" + firmwarePackageId + '\'' +
                ", updateServerFirmware=" + updateServerFirmware +
                ", updateNetworkFirmware=" + updateNetworkFirmware +
                ", updateStorageFirmware=" + updateStorageFirmware +
                ", componentUpdateRequired=" + componentUpdateRequired +
                ", canScaleupNetwork=" + canScaleupNetwork +
                ", templateId='" + templateId + '\'' +
                ", resourceHealth=" + resourceHealth +
                ", isHyperV=" + isHyperV +
                ", hasVDS=" + hasVDS +
                ", cancelInprogress=" + cancelInprogress +
                ", scaleiolist=" + scaleiolist +
                ", canUpdateInventory=" + canUpdateInventory +
                '}';
    }

}
