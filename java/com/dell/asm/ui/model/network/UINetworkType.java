/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.model.network;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.pg.asm.identitypool.api.common.model.NetworkType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Network Type.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UINetworkType extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The network Type. */
    @JsonProperty
    public String networkType;

    /** disable Static Or DHCP. */
    @JsonProperty
    public boolean disableStaticOrDHCP;

    /** vlanid required. */
    @JsonProperty
    public boolean vlanidrequired;

    /**
     * Default constructor.
     */
    public UINetworkType() {
        super();
    }

    /**
     * Instantiates a new uI network type.
     *
     * @param networkType the network type
     * @param disableStaticOrDHCP the disable static or dhcp
     * @param vlanidrequired the vlanidrequired
     */
    public UINetworkType(NetworkType networkType, boolean disableStaticOrDHCP,
                         boolean vlanidrequired) {
        super();
        this.id = networkType.value();
        this.networkType = com.dell.pg.asm.identitypoolmgr.network.util.NetworkType.fromValue(networkType.name()).getLabel();
        this.disableStaticOrDHCP = disableStaticOrDHCP;
        this.vlanidrequired = vlanidrequired;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UINetworkType [id=" + id + ", networkType=" + networkType + ", disableStaticOrDHCP=" + disableStaticOrDHCP
                + ", vlanidrequired=" + vlanidrequired + "]";
    }

}
