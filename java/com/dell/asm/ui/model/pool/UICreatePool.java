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
 * The Class UICreatePool.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UICreatePool extends UIBaseObject {

    @JsonProperty
    public String name;

    @JsonProperty
    public String description;

    @JsonProperty
    public int virtualMACIdentityCount;

    @JsonProperty
    public String virtualMACUserPrefixSelection;

    @JsonProperty
    public boolean virtualMACAutoGenerateOnDeploy;

    @JsonProperty
    public int virtualIQNIdentityCount;

    @JsonProperty
    public boolean virtualIQNAutoGenerateOnDeploy;

    @JsonProperty
    public String virtualIQNUserPrefix;

    @JsonProperty
    public int virtualWWPNIdentityCount;

    @JsonProperty
    public String virtualWWPNUserPrefixSelection;

    @JsonProperty
    public boolean virtualWWPNAutoGenerateOnDeploy;

    @JsonProperty
    public int virtualWWNNIdentityCount;

    @JsonProperty
    public String virtualWWNNUserPrefixSelection;

    @JsonProperty
    public boolean virtualWWNNAutoGenerateOnDeploy;
}
