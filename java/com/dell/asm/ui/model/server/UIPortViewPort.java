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
 * The Class UIPortViewPort.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewPort extends UIBaseObject {

    @JsonProperty
    public String id;

    /**
     * The number.
     */
    @JsonProperty
    public int number;

    /**
     * The name.
     */
    @JsonProperty
    public String name;

    /**
     * The health.
     */
    @JsonProperty
    public String health = "green";

    /**
     * The nic partitions.
     */
    @JsonProperty
    public List<UIPortViewNicPartition> partitions;

    @JsonProperty
    public String fabric;

    @JsonProperty
    public UIPortViewZoneInfo zoneInfo;

    @JsonProperty
    public String wwpn;

    // not UI model
    public String fqdd;

    /**
     * Instantiates a new uI port view port.
     */
    public UIPortViewPort() {
        super();
        partitions = new ArrayList<>();
    }

    public boolean matches(List<String> filterList) {
        if (CollectionUtils.isNotEmpty(filterList)) {
            for (String filter : filterList) {
                String[] farr = filter.split(",");
                if (farr.length < 3) continue;

                if (farr[0].equals("eq") && farr[1].equals("nicPortId")) {
                    if (!this.id.equals(farr[2]))
                        return false;
                    // heakth not ready yet
                    //} else if (farr[0].equals("eq") && farr[1].equals("health")) {
                    //    if (!filter.contains(this.health))
                    //        return false;
                }

            }
        }
        return true;
    }
}
