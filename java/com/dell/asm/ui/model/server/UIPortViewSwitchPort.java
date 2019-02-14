/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/

package com.dell.asm.ui.model.server;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPortViewSwitchPort.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewSwitchPort extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String port;

    @JsonProperty
    public String type;

    @JsonProperty
    public String health = "green";

    /**
     * Instantiates a new uI port view port.
     */
    public UIPortViewSwitchPort() {
        super();
    }

    public UIPortViewSwitchPort(String portName) {
        super();
        port = portName;
        id = portName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UIPortViewSwitchPort) {
            if (((UIPortViewSwitchPort) o).id == null &&
                    this.id != null)
                return false;

            return ((UIPortViewSwitchPort) o).id.equals(this.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.id == null)
            return 0;
        return this.id.hashCode();
    }

    public UIPortViewSwitchPort clone() {
        UIPortViewSwitchPort newObject = new UIPortViewSwitchPort(this.port);
        newObject.health = this.health;
        newObject.type = this.type;
        return newObject;
    }
}
