/**************************************************************************
 * Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 * *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model;

public enum UIDeviceState {

    /**
     * State when a device is first brought into the system.
     */
    MANAGED("managed"),

    /**
     * State when a device is not to be managed by ASM.
     */
    UNMANAGED("unmanaged"),

    /**
     * Special reserved state.
     */
    RESERVED("reserved");

    private String label;

    private UIDeviceState(String label) {
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
