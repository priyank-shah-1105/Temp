/**************************************************************************
 *   Copyright (c) 2019 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.service;

import org.apache.commons.lang3.StringUtils;

import com.dell.asm.asmcore.asmmanager.client.brownfield.NetworkSetting;
import com.dell.asm.ui.model.UIBaseObject;
import com.dell.pg.asm.identitypoolmgr.network.util.NetworkType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIExistingServiceNetwork extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String network;

    @JsonProperty
    public String type;

    @JsonProperty
    public String displayType;

    @JsonProperty
    public String networkId;

    public UIExistingServiceNetwork() {
    }

    public UIExistingServiceNetwork(NetworkSetting networkSetting) {
        this.id = networkSetting.getId();
        this.network = networkSetting.getName();
        if (networkSetting.getNetworkType() != null) {
            this.displayType = NetworkType.fromValue(networkSetting.getNetworkType().name()).getLabel();
            this.type = networkSetting.getNetworkType().name();
        } else {
            this.displayType = StringUtils.EMPTY;
            this.type = StringUtils.EMPTY;
        }
        this.networkId = networkSetting.getNetworkId();
    }
}
