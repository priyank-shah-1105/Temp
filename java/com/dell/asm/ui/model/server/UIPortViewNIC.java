/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/

package com.dell.asm.ui.model.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPortViewNicPartition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewNIC extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String modelDisplayName;

    @JsonProperty
    public String model;

    @JsonProperty
    public List<UIIPAddress> ipaddresses;

    @JsonProperty
    public String macaddress;

    @JsonProperty
    public String location;

    @JsonProperty
    public boolean nparEnabled;

    @JsonProperty
    public List<UIPortViewPort> ports;

    public int rank = Integer.MAX_VALUE;

    /**
     * Instantiates a new uI port view nic partition.
     */
    public UIPortViewNIC() {
        super();
        ports = new ArrayList<>();
        ipaddresses = new ArrayList<>();
    }

    public boolean matches(List<String> filterList) {
        if (CollectionUtils.isNotEmpty(filterList)) {
            for (String filter : filterList) {
                String[] farr = filter.split(",");
                if (farr.length < 3) continue;

                if (farr[0].equals("eq") && farr[1].equals("nicId")) {
                    if (!this.id.equals(farr[2]))
                        return false;
                }
            }
        }
        return true;
    }


}
