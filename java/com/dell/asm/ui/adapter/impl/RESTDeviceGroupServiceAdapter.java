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

import com.dell.asm.asmcore.asmmanager.client.devicegroup.DeviceGroup;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.DeviceGroupServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;


/**
 * The Class RESTInventoryServiceAdapter, implements device inventory calls in AsmManager aggregator.
 *
 */
@Component("deviceGroupServiceAdapter")
public class RESTDeviceGroupServiceAdapter extends BaseServiceAdapter implements DeviceGroupServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTDeviceGroupServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTDeviceGroupServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/DeviceGroup");
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public DeviceGroup createDeviceGroup(DeviceGroup device) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);

        return webClient.post(device, DeviceGroup.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ResourceList<DeviceGroup> getAllDeviceGroups(
            String sort,
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

        DeviceGroup[] devs = webClient.get(DeviceGroup[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(devs, totalRecords);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public DeviceGroup getDeviceGroup(String refId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.get(DeviceGroup.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public DeviceGroup updateDeviceGroup(String refId, DeviceGroup newDeviceInventory) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.put(newDeviceInventory, DeviceGroup.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Response deleteDeviceGroup(String refId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.delete();
    }

}
