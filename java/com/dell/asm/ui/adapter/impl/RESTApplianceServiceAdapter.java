/**************************************************************************
 *   Copyright (c) 2017 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.adapter.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.core.MediaType;

import com.dell.asm.alcm.client.model.UpgradeStatus;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.ASMVersion;
import com.dell.asm.alcm.client.model.ApplianceHealth;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ApplianceServiceAdapter;

@Component("applianceServiceAdapter")
public class RESTApplianceServiceAdapter extends BaseServiceAdapter implements ApplianceServiceAdapter {

    final private AtomicBoolean restarting = new AtomicBoolean(false);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTApplianceServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/alcm/appliance");
    }

    @Override
    public void restartAppliance() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/reboot");
        webClient.post(null);
        restarting.set(true);
    }

    @Override
    public void updateAppliance() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/update");
        webClient.post(null);
    }

    @Override
    public ASMVersion getASMVersion() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/asmVersion");
        return webClient.get(ASMVersion.class);
    }

    @Override
    public boolean isRestarting() {
        return restarting.get();
    }

    @Override
    public void setRestarting(boolean isRestarting) {
        restarting.set(isRestarting);
    }

    @Override
    public ApplianceHealth getApplianceHealth() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/health");
        return webClient.get(ApplianceHealth.class);
    }

    @Override
    public UpgradeStatus getUpgradeStatus() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/upgradeStatus");
        return webClient.get(UpgradeStatus.class);
    }
}
