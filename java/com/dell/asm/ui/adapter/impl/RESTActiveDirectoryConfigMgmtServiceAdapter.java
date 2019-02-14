/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.impl;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryConfiguration;
import com.dell.asm.asmcore.adconfig.model.ResponseMessage;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ActiveDirectoryConfigMgmtServiceAdapter;

/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Sood</a>
 *
 * Oct 29, 20134:17:07 PM
 */

@Component("activeDirectoryConfigMgmtServiceAdapter")
public class RESTActiveDirectoryConfigMgmtServiceAdapter extends BaseServiceAdapter implements ActiveDirectoryConfigMgmtServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTActiveDirectoryConfigMgmtServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret
     * from SecurityContext.
     */
    @Autowired
    public RESTActiveDirectoryConfigMgmtServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/ActiveDirectoryConfiguration");
    }

    @Override
    public ActiveDirectoryConfiguration create(
            ActiveDirectoryConfiguration activeDirectoryConfiguration) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(activeDirectoryConfiguration,
                              ActiveDirectoryConfiguration.class);
    }

    @Override
    public ActiveDirectoryConfiguration update(String refId,
                                               ActiveDirectoryConfiguration activeDirectoryConfiguration) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.put(activeDirectoryConfiguration, ActiveDirectoryConfiguration.class);
    }

    @Override
    public ActiveDirectoryConfiguration get(String refId) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.get(ActiveDirectoryConfiguration.class);
    }

    @Override
    public Response delete(String refId) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.delete();
    }

    @Override
    public ActiveDirectoryConfiguration[] getActiveDirectoryConfigurations(
            List<String> filter, Integer offset, Integer limit, String sort) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);

        if (offset != null) {
            webClient.query("offset", offset);
        }
        if (limit != null) {
            webClient.query("limit", limit);
        }
        if (sort != null) {
            webClient.query("sort", sort);
        }
        if (filter != null && filter.size() > 0) {
            for (String sFilter : filter) {
                webClient.query("filter", sFilter);
            }
        }

        return webClient.get(ActiveDirectoryConfiguration[].class);
    }

    @Override
    public ResponseMessage validateActiveDirectoryWithId(String refId) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.get(ResponseMessage.class);
    }

    @Override
    public ResponseMessage validateActiveDirectoryWithPayLoad(
            ActiveDirectoryConfiguration activeDirectoryConfiguration) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(activeDirectoryConfiguration);
        return webClient.get(ResponseMessage.class);

    }

}
