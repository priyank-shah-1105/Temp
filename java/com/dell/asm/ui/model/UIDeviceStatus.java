/**************************************************************************
 * Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 * *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model;

public enum UIDeviceStatus {

    READY("ready"),

    /**
     * State the device goes in when the Firmware is being upgraded.
     */
    UPDATING("updating"),

    DEPLOYING("deploying"),

    /**
     * State the device goes in when the Firmware upgrade failed.
     */
    // Nothing currently maps to this
    UPDATE_FAILED("error"),

    /**
     * State a device is in when it is unable to deploy as part of a service successfully.
     */
    DEPLOYMENT_ERROR("error"),

    /**
     * State the device is in when there is an error during the discovery process or when updating the firmware.
     */
    CONFIGURATION_ERROR("error"),

    PENDING("pendingupdates"),

    PENDING_CONFIGURATION_TEMPLATE("pending"),

    /**
     * State the device goes into prior to being deleted and removed from the device inventory.
     */
    PENDING_DELETE("pendingdelete"),

    /**
     * State the device is cancelled as a result of the service cancellation.
     */
    CANCELLED("cancelled"),

    SERVICE_MODE("servicemode");


    //DISCOVERY_FAILED("Discovery Failed"),
    //DELETE_FAILED("Delete Failed"),
    //DELETED("Deleted");


    private String label;

    private UIDeviceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return name();
    }

    @Override
    public String toString() {
        return label;
    }
}
