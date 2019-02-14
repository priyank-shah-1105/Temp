/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied
 * or disclosed except in accordance with the terms of that agreement.
 * Copyright (c) 2010-2014 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.impl;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.osrepository.OSRepository;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.OSRepositoryServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;

@Component("osRepositoryServiceAdapter")
public class RESTOSRepositoryServiceAdapter extends BaseServiceAdapter implements OSRepositoryServiceAdapter {
    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTOSRepositoryServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTOSRepositoryServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/osRepository");
    }

    @Override
    public OSRepository create(OSRepository osRepository) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(osRepository, OSRepository.class);
    }

    @Override
    public OSRepository update(String id, OSRepository osRepository, Boolean sync) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);
        if (sync != null) {
            webClient.query("sync", sync);
        }
        return webClient.put(osRepository, OSRepository.class);
    }

    @Override
    public OSRepository sync(String id, OSRepository osRepository) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("sync/" + id);
        return webClient.put(osRepository, OSRepository.class);
    }

    @Override
    public Response delete(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(id);
        Response response = webClient.delete();
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
        return response;
    }

    @Override
    public ResourceList<OSRepository> getAll(String sort, List<String> filter, Integer offset,
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

        OSRepository[] devs = webClient.get(OSRepository[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(devs, totalRecords);
    }

    @Override
    public OSRepository getById(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(id);

        return webClient.get(OSRepository.class);
    }


    @Override
    public Response testConnection(OSRepository osRepository) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("connection");
        Response response = webClient.post(osRepository);
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
        return response;
    }
}
