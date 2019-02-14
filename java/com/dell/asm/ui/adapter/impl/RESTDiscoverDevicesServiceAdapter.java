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

import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveryRequest;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveryResult;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.DiscoverDevicesServiceAdapter;


/**
 * The Class RESTUserServiceAdapter, implements login to NBI API.
 *
 */
@Component("discoverDevicesServiceAdapter")
public class RESTDiscoverDevicesServiceAdapter extends BaseServiceAdapter implements DiscoverDevicesServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTDiscoverDevicesServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTDiscoverDevicesServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/DiscoveryRequest");
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public DiscoveryRequest deviceIPRangeDiscoveryRequest(DiscoveryRequest discoveryRequestList) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(discoveryRequestList, DiscoveryRequest.class);
    }

    @Override
    public DiscoveryRequest listDevicesForIPRangeDiscoveryRequest(DiscoveryRequest discoveryRequestList) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/listdevices");
        return webClient.post(discoveryRequestList, DiscoveryRequest.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public DiscoveryRequest getDiscoveryResult(String parentJobId, String sort, List<String> filter,
                                               Integer offset, Integer limit) {

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
        if (parentJobId != null) {
            webClient.path(parentJobId);
        }

        return webClient.get(DiscoveryRequest.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Response deleteDiscoveryResult(String parentJobId) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);

        webClient.path(parentJobId);
        return webClient.delete();
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public DiscoveryRequest[] getDiscoveryRequests(String sort,
                                                   List<String> filter,
                                                   Integer offset,
                                                   Integer limit) {
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

        return webClient.get(DiscoveryRequest[].class);
    }

    @Override
    public DiscoveryResult getDiscoveryResult(String refId) {
        // TODO Auto-generated method stub
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("discoveryresult/" + refId);

        return webClient.get(DiscoveryResult.class);
    }

}
