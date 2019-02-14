/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.configure.ConfigurationRequest;
import com.dell.asm.asmcore.asmmanager.client.configure.ConfigurationResponse;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ConfigureDevicesServiceAdapter;


/**
 * The Class RESTUserServiceAdapter, implements login to NBI API.
 *
 */
@Component("configureDevicesServiceAdapter")
public class RESTConfigureDevicesServiceAdapter extends BaseServiceAdapter implements ConfigureDevicesServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTConfigureDevicesServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTConfigureDevicesServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/Configure");
    }

    @Override
    public Response processConfiguration(ConfigurationRequest request) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/process");

        return webClient.post(request);
    }

    @Override
    public ConfigurationResponse configurationAndDiscoveryRequest(
            ConfigurationRequest discoveryRequestList) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/discover");

        return webClient.post(discoveryRequestList, ConfigurationResponse.class);
    }

    @Override
    public String configurationRequest(ServiceTemplate request) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/configure");

        return webClient.post(request, String.class);
    }

}
