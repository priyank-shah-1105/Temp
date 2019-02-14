/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.model.network;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UINetwork.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UINetwork extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The serverid. */
    @JsonProperty
    public String serverid;

    /** The name. */
    @JsonProperty
    public String name;

    /** The description. */
    @JsonProperty
    public String description;

    /** The type. */
    @JsonProperty
    public String typeid;

    /** The vlanid. */
    @JsonProperty
    public String vlanid;

    /** The staticordhcp. */
    @JsonProperty
    public String staticordhcp;

    /** The ipaddressranges. */
    @JsonProperty
    public List<UIIPAddressRange> ipaddressranges;

    @JsonProperty
    public List<UIStaticIPAddressDetails> staticipaddressdetails;

    /** The gateway. */
    @JsonProperty
    public String gateway;

    /** The subnet. */
    @JsonProperty
    public String subnet;

    /** The primarydns. */
    @JsonProperty
    public String primarydns;

    /** The secondarydns. */
    @JsonProperty
    public String secondarydns;

    /** The dnssuffix. */
    @JsonProperty
    public String dnssuffix;

    /** The created date. */
    @JsonProperty
    public String createdDate;

    /** The created by. */
    @JsonProperty
    public String createdBy;

    /** The updated date. */
    @JsonProperty
    public String updatedDate;

    /** The updated by. */
    @JsonProperty
    public String updatedBy;

    /** The address pools. */
    @JsonProperty
    public List<UIAddressPool> addressPools;

    /** The total address pools. */
    @JsonProperty
    public int totalAddressPools;

    /** The available address pools. */
    @JsonProperty
    public int availableAddressPools;

    /** The assigned address pools. */
    @JsonProperty
    public int assignedAddressPools;

    /** The network template usages. */
    @JsonProperty
    public List<UINetworkTemplateUsage> networkTemplateUsages;

    /** Is configurestatic. */
    @JsonProperty
    public boolean configurestatic;

    /** Is selected. */
    @JsonProperty
    public boolean selected;

    @JsonProperty
    public UINetworkType type;

    /**
     * Instantiates a new uI network.
     */
    public UINetwork() {
        super();
        ipaddressranges = new ArrayList<UIIPAddressRange>();
        addressPools = new ArrayList<UIAddressPool>();
        networkTemplateUsages = new ArrayList<UINetworkTemplateUsage>();
        staticipaddressdetails = new ArrayList<UIStaticIPAddressDetails>();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UINetwork [id=" + id + ", name=" + name + ", description=" + description + ", type=" + typeid + ", vlanid=" + vlanid
                + ", staticordhcp=" + staticordhcp + ", ipaddressranges=" + ipaddressranges + ", gateway=" + gateway + ", subnet=" + subnet
                + ", primarydns=" + primarydns + ", secondarydns=" + secondarydns + ", dnssuffix=" + dnssuffix + ", createdDate=" + createdDate
                + ", createdBy=" + createdBy + ", updatedDate=" + updatedDate + ", updatedBy=" + updatedBy + ", addressPools=" + addressPools
                + ", totalAddressPools=" + totalAddressPools + ", availableAddressPools=" + availableAddressPools + ", assignedAddressPools="
                + assignedAddressPools + ", networkTemplateUsages=" + networkTemplateUsages + ", serverid=" + serverid + "]";
    }

}
