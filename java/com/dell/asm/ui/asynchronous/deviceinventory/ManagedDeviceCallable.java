/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.asynchronous.deviceinventory;

import java.util.concurrent.Callable;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;

public class ManagedDeviceCallable implements Callable {

    private final String id;
    private final DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private final SecurityContext securityContext;

    public ManagedDeviceCallable(String id,
                                 DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                                 SecurityContext securityContext) {
        this.id = id;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.securityContext = securityContext;
    }

    @Override
    public com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice call() {
        SecurityContextHolder.setContext(securityContext);
        return deviceInventoryServiceAdapter.getDeviceInventory(id);
    }
}
