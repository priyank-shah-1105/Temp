/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied
 * or disclosed except in accordance with the terms of that agreement.
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.model.network;


import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UINetworkSummary.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UINetworkSummary extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The description. */
    @JsonProperty
    public String description;

    /** The network type. */
    @JsonProperty
    public UINetworkType type;

    /** The vlanid. */
    @JsonProperty
    public String vlanid;

    /** The staticordhcp. */
    @JsonProperty
    public String staticordhcp;

    /** The canedit. */
    @JsonProperty
    public boolean canedit;

    /** The candelete. */
    @JsonProperty
    public boolean candelete;

    /**
     * Instantiates a new uI network summary.
     */
    public UINetworkSummary() {
        super();
        type = new UINetworkType();
    }

    /**
     * Instantiates a new uI network summary.
     *
     * @param id
     *            the id
     * @param name
     *            the name
     * @param description
     *            the description
     * @param type
     *            the network type
     * @param vlanid
     *            the vlanid
     */
    public UINetworkSummary(String id, String name, String description, UINetworkType type,
                            String vlanid) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.vlanid = vlanid;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UINetworkSummary [id=" + id + ", name=" + name + ", description=" + description + ", type=" + type + ", vlanid=" + vlanid
                + "]";
    }

}
