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
 * The Class UIUpdatePool.
 * autogenerate: true
 * id: "ff80808142dd4f090142e45c603c04f5"
 * identitycount: "4"
 * type: "mac"
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUpdatePool extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public int identitycount;

    @JsonProperty
    public String type;

    @JsonProperty
    public boolean autogenerate;
}
