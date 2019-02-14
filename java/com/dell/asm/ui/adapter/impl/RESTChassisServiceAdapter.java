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
import com.dell.asm.ui.adapter.service.ChassisServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;
import com.dell.pg.asm.chassis.client.device.Chassis;

/**
 * The Class RESTChassisServiceAdapter, implements chassis RA client calls.
 *
 */
@Component("chassisServiceAdapter")
public class RESTChassisServiceAdapter extends BaseServiceAdapter implements ChassisServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTChassisServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTChassisServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/Chassis");
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Chassis createChassis(Chassis device) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(device, Chassis.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ResourceList<Chassis> getChassises(String sort, List<String> filter, Integer offset,
                                              Integer limit) {

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

        Chassis[] items = webClient.get(Chassis[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());

        //Chassis[]cArr = items.toArray(new Chassis[items.size()]);
        return new ResourceList<>(items, totalRecords);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Chassis getChassis(String refId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.get(Chassis.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void updateChassis(String refId, Chassis chassis) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        webClient.put(chassis, Chassis.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void deleteChassis(String refId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        webClient.delete();
    }

    @Override
    public Chassis getChassisByServiceTag(String serviceTag, String type) {

        if (serviceTag == null || type == null) return null;

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(serviceTag + "/" + type);
        webClient.query("serviceTag", serviceTag);
        webClient.query("type", type);
        return webClient.get(Chassis.class);

    }
}
