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

import com.dell.asm.asmcore.asmmanager.client.setting.Setting;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.SettingServiceAdapter;

@Component("settingServiceAdapter")
public class RESTSettingServiceAdapter extends BaseServiceAdapter implements SettingServiceAdapter {

    @Autowired
    public RESTSettingServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/setting");
    }

    @Override
    public Setting getSettingByName(String name) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(name);

        return webClient.get(Setting.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Setting update(Setting setting) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(setting.getId());

        return webClient.put(setting, Setting.class);
    }

    @Override
    public Setting create(Setting setting) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(setting, Setting.class);
    }

    @Override
    public void delete(final String id) {
        WebClient webClient = createWebClient();
        webClient.path(id);
        webClient.delete();
    }
}
