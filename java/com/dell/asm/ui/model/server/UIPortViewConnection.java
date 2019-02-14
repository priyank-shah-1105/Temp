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
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPortViewConnection.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewConnection extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String nicId;

    @JsonProperty
    public String nicPortId;

    @JsonProperty
    public UIPortViewDeviceConnection connectedDevice;

    @JsonProperty
    public List<UIPortViewIOMConnection> iomUplinkConnections;

    /**
     * Instantiates a new uI port view port.
     */
    public UIPortViewConnection() {
        super();
        id = UUID.randomUUID().toString();
        iomUplinkConnections = new ArrayList<>();
    }

    public boolean matches(List<String> filterList) {
        if (CollectionUtils.isNotEmpty(filterList)) {
            for (String filter : filterList) {
                String[] farr = filter.split(",");
                if (farr.length < 3) continue;

                if (farr[0].equals("eq") && farr[1].equals("nicId")) {
                    if (!this.nicId.equals(farr[2]))
                        return false;
                } else if (farr[0].equals("eq") && farr[1].equals("nicPortId")) {
                    if (!this.nicPortId.equals(farr[2]))
                        return false;
                }
            }
        }
        return true;
    }

}
