/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.service;

public enum UIServiceHealth {
    red,
    orange,
    yellow,
    blue,
    unknown,
    cancelled,
    green,
    pending,
    incomplete,
    servicemode;

    /**
     * Red goes over yellow, yellow over green etc.
     * @param newHealth
     * @return
     */
    public UIServiceHealth merge(UIServiceHealth newHealth) {
        int comparedTo = newHealth.compareTo(this);

        return comparedTo > 0 ? this : newHealth;
    }
}
