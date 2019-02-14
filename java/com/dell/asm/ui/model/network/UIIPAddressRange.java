/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.model.network;

import com.dell.asm.asmcore.asmmanager.client.servicetemplate.IpRange;
import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class IP Address Range.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIIPAddressRange extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The starting ip address. */

    @JsonProperty
    public String startingIpAddress;

    /** The ending ip address. */
    @JsonProperty
    public String endingIpAddress;

    @JsonProperty
    public Integer totalIPAddresses;

    @JsonProperty
    public Integer inUseIPAddresses;

    @JsonProperty
    public String role;

    /**
     * Instantiates a new UI IP address range.
     */
    public UIIPAddressRange() {
        super();
    }

    /**
     * Constructor for UIIPAddressRange
     * @param id
     * @param startingIpAddress
     * @param endingIpAddress
     * @param totalIPAddresses
     * @param inUseIPAddresses
     */
    public UIIPAddressRange(String id, String startingIpAddress, String endingIpAddress,
                            Integer totalIPAddresses, Integer inUseIPAddresses, String role) {
        this.id = id;
        this.startingIpAddress = startingIpAddress;
        this.endingIpAddress = endingIpAddress;
        this.totalIPAddresses = totalIPAddresses;
        this.inUseIPAddresses = inUseIPAddresses;
        this.role = role;
    }

    @Override
    public String toString() {
        return "UIIPAddressRange{" +
                "id='" + id + '\'' +
                ", startingIpAddress='" + startingIpAddress + '\'' +
                ", endingIpAddress='" + endingIpAddress + '\'' +
                ", totalIPAddresses=" + totalIPAddresses +
                ", inUseIPAddresses=" + inUseIPAddresses +
                '}';
    }
}
