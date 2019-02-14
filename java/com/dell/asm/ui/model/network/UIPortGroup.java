/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/

package com.dell.asm.ui.model.network;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateOption;
import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The POort Group
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortGroup extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String portGroup;

    @JsonProperty
    public String vlan;

    @JsonProperty
    public String network;

    @JsonProperty
    public List<UIListItem> networks;

    /**
     * Instantiates a new UI address pool.
     */
    public UIPortGroup() {
        super();
        networks = new ArrayList<>();
    }

    public UIPortGroup(ServiceTemplateOption option) {
        id = option.getValue();
        portGroup = option.getName();
    }

    @Override
    public boolean equals(Object obj) {
        UIPortGroup sObj = null;
        if (obj instanceof UIPortGroup)
            sObj = (UIPortGroup) obj;
        else
            return false;

        return portGroup.equals(sObj.portGroup);
    }

    @Override
    public int hashCode() {
        return portGroup.hashCode();
    }
}
