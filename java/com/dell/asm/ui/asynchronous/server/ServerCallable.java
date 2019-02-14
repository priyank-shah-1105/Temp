/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.asynchronous.server;

import java.util.concurrent.Callable;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dell.asm.ui.adapter.service.ServerServiceAdapter;
import com.dell.pg.asm.server.client.device.Server;

public class ServerCallable implements Callable {

    private final String id;
    private final ServerServiceAdapter serverServiceAdapter;
    private final SecurityContext securityContext;

    public ServerCallable(String id, ServerServiceAdapter serverServiceAdapter,
                          SecurityContext securityContext) {
        this.id = id;
        this.serverServiceAdapter = serverServiceAdapter;
        this.securityContext = securityContext;
    }

    @Override
    public Server call() {
        SecurityContextHolder.setContext(securityContext);
        return serverServiceAdapter.getServer(id);
    }
}
