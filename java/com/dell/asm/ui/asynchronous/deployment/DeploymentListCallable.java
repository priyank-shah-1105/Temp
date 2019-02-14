/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.asynchronous.deployment;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.util.MappingUtils;

public class DeploymentListCallable implements Callable {

    private final List<String> filterList;
    private final DeploymentServiceAdapter deploymentServiceAdapter;
    private final SecurityContext securityContext;

    public DeploymentListCallable(List<String> filterList,
                                  DeploymentServiceAdapter deploymentServiceAdapter,
                                  SecurityContext securityContext) {
        this.filterList = filterList;
        this.deploymentServiceAdapter = deploymentServiceAdapter;
        this.securityContext = securityContext;
    }

    @Override
    public ResourceList<Deployment> call() {
        SecurityContextHolder.setContext(securityContext);
        return deploymentServiceAdapter.getDeployments(null, filterList, null,
                                                       MappingUtils.MAX_RECORDS, Boolean.TRUE);
    }
}
