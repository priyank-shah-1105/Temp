/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.model.puppet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Puppet certificate status.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CertFingerprints {
    @JsonProperty("default")
    public String _default;

    @JsonProperty
    public String SHA1;

    @JsonProperty
    public String SHA256;

    @JsonProperty
    public String SHA512;
}
