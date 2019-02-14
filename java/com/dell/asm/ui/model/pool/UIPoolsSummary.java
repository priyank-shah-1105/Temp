/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.model.pool;


import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPoolsSummary.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPoolsSummary extends UIBaseObject {

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

    /** The can delete. */
    @JsonProperty
    public boolean canDelete;

    @JsonProperty
    public String description;

    @JsonProperty
    public String createddate;

    @JsonProperty
    public String createdby;


}
