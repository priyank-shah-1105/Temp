/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.model.pool;


import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPools.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPools extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The available address pools. */
    @JsonProperty
    public int availableAddressPools;

    /** The assigned address pools. */
    @JsonProperty
    public int assignedAddressPools;

    /** The reserved address pools. */
    @JsonProperty
    public int reservedAddressPools;

    /** The deployment template count. */
    @JsonProperty
    public int deploymentTemplateCount;

    /** The lan reserved address pools. */
    @JsonProperty
    public int lanReservedAddressPools;

    /** The lan assigned address pools. */
    @JsonProperty
    public int lanAssignedAddressPools;

    /** The lan available address pools. */
    @JsonProperty
    public int lanAvailableAddressPools;

    /** The iscsi reserved address pools. */
    @JsonProperty
    public int iscsiReservedAddressPools;

    /** The iscsi assigned address pools. */
    @JsonProperty
    public int iscsiAssignedAddressPools;

    @JsonProperty
    public int iscsiAvailableAddressPools;

    @JsonProperty
    public int wwpnReservedAddressPools;

    @JsonProperty
    public int wwpnAssignedAddressPools;

    @JsonProperty
    public int wwpnAvailableAddressPools;

    @JsonProperty
    public int wwnnReservedAddressPools;

    @JsonProperty
    public int wwnnAssignedAddressPools;

    @JsonProperty
    public int wwnnAvailableAddressPools;

    @JsonProperty
    public boolean virtualMACAutoGenerateOnDeploy;

    @JsonProperty
    public boolean virtualIQNAutoGenerateOnDeploy;

    @JsonProperty
    public boolean virtualWWPNAutoGenerateOnDeploy;

    @JsonProperty
    public boolean virtualWWNNAutoGenerateOnDeploy;

    /** The can delete. */
    @JsonProperty
    public boolean candelete;

    /** The pool template usages. */
    @JsonProperty
    public List<UIPoolTemplateUsage> poolTemplateUsages;

    @JsonProperty
    public String description;

    @JsonProperty
    public String createddate;

    @JsonProperty
    public String createdby;

    @JsonProperty
    public String virtualMACUserPrefixSelection;

    @JsonProperty
    public String virtualIQNUserPrefix;

    @JsonProperty
    public String virtualWWPNUserPrefixSelection;

    @JsonProperty
    public String virtualWWNNUserPrefixSelection;


    /**
     * Instantiates a new uI pools.
     */
    public UIPools() {
        super();
        this.poolTemplateUsages = new ArrayList<UIPoolTemplateUsage>();
    }

}
