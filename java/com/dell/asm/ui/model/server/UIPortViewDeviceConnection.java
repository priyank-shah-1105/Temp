/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/

package com.dell.asm.ui.model.server;

import java.util.List;

import com.dell.asm.asmcore.asmmanager.client.deployment.PortConnection;
import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPortViewDeviceConnection.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewDeviceConnection extends UIBaseObject {

    @JsonProperty
    public String deviceId;

    @JsonProperty
    public UIPortViewSwitchPort downlinkPort;

    /**
     * Instantiates a new uI port view port.
     */
    public UIPortViewDeviceConnection() {
        super();
    }

    public UIPortViewDeviceConnection(PortConnection pc) {
        super();
        this.deviceId = pc.getRemoteDevice();
        String portId = concatPorts(pc.getRemotePorts());
        this.downlinkPort = new UIPortViewSwitchPort(portId);
    }

    public static String concatPorts(List<String> ports) {
        String portId = "";
        if (ports == null)
            return portId;
        for (String pid : ports) {
            String[] arr = pid.split(" ");
            pid = arr[arr.length - 1]; // skipp all to the last member

            if (pid.contains("/")) {
                int index = pid.indexOf("/");
                pid = index == pid.length() - 1 ? pid.substring(0, index) : pid.substring(index + 1);
            }

            if (portId.length() > 0)
                portId += ",";
            portId += pid;

        }
        return portId;
    }

}
