package com.dell.asm.ui.adapter.impl;

/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2014 Dell Inc. All Rights Reserved.
 */

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.Appliance;
import com.dell.asm.alcm.client.model.ApplianceSettings;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ApplianceInfoServiceAdapter;

@Component("applianceInfoServiceAdapter")
public class RESTApplianceInfoServiceAdapter extends BaseServiceAdapter implements ApplianceInfoServiceAdapter {

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTApplianceInfoServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/alcm/Appliance");
    }

    @Override
    public Appliance getAppliance() {
        final WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/");
        return webClient.get(Appliance.class);
    }

    @Override
    public void updateApplianceSettings(final ApplianceSettings applianceSettings) {
        Appliance appliance = getAppliance();
        if (appliance == null) {
            appliance = new Appliance();
        }
        appliance.setApplianceSettings(applianceSettings);

        final WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/");
        webClient.put(appliance);
    }

    @Override
    public ApplianceSettings getApplianceSettings() {
        final Appliance appliance = getAppliance();
        return (appliance != null) ? appliance.getApplianceSettings() : null;
    }

}
