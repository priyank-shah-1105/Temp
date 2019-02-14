package com.dell.asm.ui.model.chassis;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ChassisConnectionCheck.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassisConnectionCheck extends UIBaseObject {


    @JsonProperty
    public String id;

    /**
     * The startingIpAddress.
     */
    @JsonProperty
    public String startingIpAddress;

    /**
     * The endingIpAddress.
     */
    @JsonProperty
    public String endingIpAddress;

    /**
     * The server credentialId.
     */
    @JsonProperty
    public String serverCredentialId;

    /**
     * The chassis credentialId.
     */
    @JsonProperty
    public String chassisCredentialId;


    @JsonProperty
    public String storageCredentialId;

    @JsonProperty
    public String vcenterCredentialId;

    @JsonProperty
    public String torCredentialId;

    @JsonProperty
    public boolean includeServers;

    @JsonProperty
    public boolean includeChassis;

    @JsonProperty
    public boolean includeStorage;

    @JsonProperty
    public boolean includeVCenter;

    @JsonProperty
    public boolean includeTOR;

    @JsonProperty
    public boolean includeHypervisor;

    @JsonProperty
    public boolean includeSCVMM;

    @JsonProperty
    public String resourcetype;

    @JsonProperty
    public String ipaddresstype;
    /**
     * The deviceGroupId.
     */
    @JsonProperty
    public String deviceGroupId;

    @JsonProperty
    public String bladeCredentialId;

    @JsonProperty
    public String iomCredentialId;

    @JsonProperty
    public String scvmmCredentialId;

    @JsonProperty
    public String emCredentialId;

    @JsonProperty
    public String managedstate;

    @JsonProperty
    public String serverPoolId;

    @JsonProperty
    public String scaleioCredentialId;

    /**
     * Constructor.
     */
    public UIChassisConnectionCheck() {
        super();
    }

    @Override
    public String toString() {
        return "UIChassisConnectionCheck{" +
                "id='" + id + '\'' +
                ", startingIpAddress='" + startingIpAddress + '\'' +
                ", endingIpAddress='" + endingIpAddress + '\'' +
                ", serverCredentialId='" + serverCredentialId + '\'' +
                ", chassisCredentialId='" + chassisCredentialId + '\'' +
                ", storageCredentialId='" + storageCredentialId + '\'' +
                ", vcenterCredentialId='" + vcenterCredentialId + '\'' +
                ", torCredentialId='" + torCredentialId + '\'' +
                ", includeServers=" + includeServers +
                ", includeChassis=" + includeChassis +
                ", includeStorage=" + includeStorage +
                ", includeVCenter=" + includeVCenter +
                ", includeTOR=" + includeTOR +
                ", includeHypervisor=" + includeHypervisor +
                ", includeSCVMM=" + includeSCVMM +
                ", resourcetype='" + resourcetype + '\'' +
                ", ipaddresstype='" + ipaddresstype + '\'' +
                ", deviceGroupId='" + deviceGroupId + '\'' +
                ", bladeCredentialId='" + bladeCredentialId + '\'' +
                ", iomCredentialId='" + iomCredentialId + '\'' +
                ", scvmmCredentialId='" + scvmmCredentialId + '\'' +
                ", emCredentialId='" + emCredentialId + '\'' +
                ", managedstate='" + managedstate + '\'' +
                ", serverPoolId='" + serverPoolId + '\'' +
                ", scaleioCredentialId='" + scaleioCredentialId + '\'' +
                '}';
    }
}
