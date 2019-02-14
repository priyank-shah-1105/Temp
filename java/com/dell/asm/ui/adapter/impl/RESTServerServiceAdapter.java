/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.impl;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ServerServiceAdapter;
import com.dell.pg.asm.server.client.device.Server;


/**
 * The Class RESTServerServiceAdapter, implements server RA client calls.
 *
 */
@Component("serverServiceAdapter")
public class RESTServerServiceAdapter extends BaseServiceAdapter implements ServerServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTServerServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTServerServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/Server");
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Server[] getServers(String sort, List<String> filter, Integer offset, Integer limit) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        if (sort != null)
            webClient.query("sort", sort);
        if (offset != null)
            webClient.query("offset", offset);
        if (limit != null)
            webClient.query("limit", limit);
        if (filter != null && filter.size() > 0) {
            for (String sFilter : filter) {
                webClient.query("filter", sFilter);
            }
        }

        return webClient.get(Server[].class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Server getServer(String refId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.get(Server.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void deleteServer(String refId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        webClient.delete();
    }

}
