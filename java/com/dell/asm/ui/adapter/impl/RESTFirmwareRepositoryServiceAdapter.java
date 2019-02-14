/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.firmwarerepository.ESRSFile;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareRepository;
import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareBundle;
import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareComponent;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.FirmwareRepositoryServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;


/**
 * The Class RESTInventoryServiceAdapter, implements device inventory calls in AsmManager aggregator.
 *
 */
@Component("firmwareRepositoryServiceAdapter")
public class RESTFirmwareRepositoryServiceAdapter extends BaseServiceAdapter implements FirmwareRepositoryServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTFirmwareRepositoryServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTFirmwareRepositoryServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/firmwareRepository");
    }
//
//    /**
//     * (non-Javadoc)
//     */
//    @Override
//    public ServiceTemplate copyTemplate(String templateId, ServiceTemplate configuration) {
//        WebClient webClient = createWebClient();
//        webClient.accept(MediaType.APPLICATION_XML);
//          webClient.path("/" + templateId+ "/copy");
////        webClient.query("newName", name);          
//        return webClient.post(configuration, ServiceTemplate.class);
//       
//    }

    /**
     * (non-Javadoc)
     */
    @Override
    public FirmwareRepository create(FirmwareRepository firmwareRepository) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(firmwareRepository, FirmwareRepository.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ResourceList<FirmwareRepository> getAll(
            String sort,
            List<String> filter,
            Integer offset,
            Integer limit) {
        return getAll(sort, filter, offset, limit, false, false);
    }

    @Override
    public ResourceList<FirmwareRepository> getAll(
            String sort,
            List<String> filter,
            Integer offset,
            Integer limit,
            boolean bundles,
            boolean components) {

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
        webClient.query("bundles", bundles);
        webClient.query("components", components);

        FirmwareRepository[] devs = webClient.get(FirmwareRepository[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(devs, totalRecords);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public FirmwareRepository getById(String id) {
        return getById(id, false, false);
    }

    public FirmwareRepository getById(String id, boolean bundles, boolean components) {
        if (id == null)
            return null;

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(id);
        webClient.query("bundles", bundles);
        webClient.query("components", components);

        return webClient.get(FirmwareRepository.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public SoftwareBundle getBundleById(String id) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("softwarebundle/" + id);

        return webClient.get(SoftwareBundle.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public List<ESRSFile> getESRSRepositories() {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("esrslist");

        ESRSFile[] files = webClient.get(ESRSFile[].class);
        return new ArrayList<>(Arrays.asList(files));
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public FirmwareRepository update(FirmwareRepository firmwareRepository) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(firmwareRepository.getId());

        return webClient.put(firmwareRepository, FirmwareRepository.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Response delete(String templateId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(templateId);
        Response response = webClient.delete();
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
        return response;

    }

    @Override
    public ResourceList<FirmwareRepository> getByEqualsAttributes(
            HashMap<String, Object> attributeMap) {


        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);

        if (attributeMap != null) {
            for (String key : attributeMap.keySet()) {
                webClient.query(key, attributeMap.get(key));
            }
        }

        FirmwareRepository[] devs = webClient.get(FirmwareRepository[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(devs, totalRecords);
    }

    @Override
    public List<SoftwareComponent> getSoftwareComponents(String componentId,
                                                         String deviceId, String subDeviceId,
                                                         String vendorId,
                                                         String subVendorId, String type,
                                                         String systemId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("softwarecomponent");

        if (componentId != null && !componentId.trim().equals(""))
            webClient.query("componentId", componentId);
        if (deviceId != null && !deviceId.trim().equals(""))
            webClient.query("deviceId", deviceId);
        if (subDeviceId != null && !subDeviceId.trim().equals(""))
            webClient.query("subDeviceId", subDeviceId);
        if (vendorId != null && !vendorId.trim().equals(""))
            webClient.query("vendorId", vendorId);
        if (subVendorId != null && !subVendorId.trim().equals(""))
            webClient.query("subVendorId", subVendorId);
        if (systemId != null && !systemId.trim().equals(""))
            webClient.query("systemId", systemId);

        webClient.query("type", type);

        SoftwareComponent[] scs = webClient.get(SoftwareComponent[].class);
        if (scs != null && scs.length > 0)
            return Arrays.asList(scs);

        return null;
    }

    @Override
    public Response testConnection(FirmwareRepository firmwareRepository) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("connection");
        Response response = webClient.post(firmwareRepository);
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
        return response;
    }
}
