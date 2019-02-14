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
 * The Class Address Pool.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIAddressPool extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The ipaddress. */
    @JsonProperty
    public String ipaddress;

    /** The status. */
    @JsonProperty
    public String status;

    /** The deploymentinstance. */
    @JsonProperty
    public String deploymentinstance;

    /** The deploymenttemplate. */
    @JsonProperty
    public String deploymenttemplate;

    /** The serveripaddress. */
    @JsonProperty
    public String serveripaddress;

    /**
     * Instantiates a new UI address pool.
     */
    public UIAddressPool() {
        super();
    }

    /**
     * Instantiates a new UI address pool.
     *
     * @param id
     *            the id
     * @param ipaddress
     *            the ipaddress
     * @param status
     *            the status
     * @param deploymentinstance
     *            the deploymentinstance
     * @param deploymenttemplate
     *            the deploymenttemplate
     * @param serveripaddress
     *            the serveripaddress
     */
    public UIAddressPool(String id, String ipaddress, String status,
                         String deploymentinstance, String deploymenttemplate,
                         String serveripaddress) {
        super();
        this.id = id;
        this.ipaddress = ipaddress;
        this.status = status;
        this.deploymentinstance = deploymentinstance;
        this.deploymenttemplate = deploymenttemplate;
        this.serveripaddress = serveripaddress;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UIAddressPool [id=" + id + ", ipaddress=" + ipaddress
                + ", status=" + status + ", deploymentinstance="
                + deploymentinstance + ", deploymenttemplate="
                + deploymenttemplate + ", serveripaddress=" + serveripaddress
                + "]";
    }

}
