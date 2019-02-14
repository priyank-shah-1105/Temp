/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.model.server;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class IP Address.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIIPAddress extends UIBaseObject {

    @JsonProperty
    public String ipaddress;

    @JsonProperty
    public String ipaddressurl;

    public UIIPAddress() {
    }

    public UIIPAddress(String address) {
        ipaddress = address;
        ipaddressurl = "https://" + address;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UIIPAddress))
            return false;

        if (ipaddress == null) {
            return ((UIIPAddress) o).ipaddress == null;
        } else
            return ipaddress.equals(((UIIPAddress) o).ipaddress);
    }

    @Override
    public int hashCode() {
        if (ipaddress == null)
            return 0;
        return ipaddress.hashCode();
    }
}
