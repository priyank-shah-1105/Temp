/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied
 * or disclosed except in accordance with the terms of that agreement.
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.model.network;


import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.server.UIServerSummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UINetworkIdentity.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UINetworkIdentity extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The type. */
    @JsonProperty
    public String type;

    /** The instanceid. */
    @JsonProperty
    public String instanceid;

    /** The permanentmacaddress. */
    @JsonProperty
    public String permanentmacaddress;

    /** The virtualmacaddress. */
    @JsonProperty
    public String virtualmacaddress;

    /** The permanentiscsimacaddress. */
    @JsonProperty
    public String permanentiscsimacaddress;

    /** The virtualiscsimacaddress. */
    @JsonProperty
    public String virtualiscsimacaddress;

    /** The permanentfcoemacaddress. */
    @JsonProperty
    public String permanentfcoemacaddress;

    /** The virtualfipsmacaddress. */
    @JsonProperty
    public String virtualfipsmacaddress;

    /** The initiatoriqn. */
    @JsonProperty
    public String initiatoriqn;

    /** The initiatorip. */
    @JsonProperty
    public String initiatorip;

    /** The virtualwwpn. */
    @JsonProperty
    public String virtualwwpn;

    /** The virtualwwnn. */
    @JsonProperty
    public String virtualwwnn;

    /** The wwpn. */
    @JsonProperty
    public String wwpn;

    /** The wwnn. */
    @JsonProperty
    public String wwnn;

    /** The enabletargetiqn. */
    @JsonProperty
    public boolean enabletargetiqn;

    /** The targetiqn. */
    @JsonProperty
    public String targetiqn;

    /** The enablebootlun. */
    @JsonProperty
    public boolean enablebootlun;

    /** The bootlun. */
    @JsonProperty
    public String bootlun;

    /** The server. */
    @JsonProperty
    public UIServerSummary server;

    /** The targetipaddress. */
    @JsonProperty
    public String targetipaddress;

    /** The targetwwpn. */
    @JsonProperty
    public String targetwwpn;

    /**
     * Instantiates a new uI network identity.
     */
    public UINetworkIdentity() {
        super();
        server = new UIServerSummary();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UINetworkIdentity [id=" + id + ", type=" + type;
    }

}
