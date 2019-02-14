/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareComplianceReport;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareUpdateRequest;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ServiceModeAction;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ServiceModeRequest;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.UpdateDeviceInventoryResponse;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareDeviceInventory;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;


/**
 * The Class RESTInventoryServiceAdapter, implements device inventory calls in AsmManager aggregator.
 *
 */
@Component("deviceInventoryServiceAdapter")
public class RESTDeviceInventoryServiceAdapter extends BaseServiceAdapter implements DeviceInventoryServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTDeviceInventoryServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTDeviceInventoryServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/ManagedDevice");
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ResourceList<ManagedDevice> getAllDeviceInventory(
            String sort,
            List<String> filter,
            Integer offset,
            Integer limit) {

        return getAllDeviceInventoryInternal(sort, filter, offset, limit, false);
    }

    @Override
    public ResourceList<ManagedDevice> getAllDeviceInventoryWithCompliance(
            String sort,
            List<String> filter,
            Integer offset,
            Integer limit) {
        return getAllDeviceInventoryInternal(sort, filter, offset, limit, true);
    }

    private ResourceList<ManagedDevice> getAllDeviceInventoryInternal(
            String sort,
            List<String> filter,
            Integer offset,
            Integer limit,
            boolean complianceCheck) {

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
        if (complianceCheck) {
            webClient.path("/withcompliance");
        }

        ManagedDevice[] devs = webClient.get(ManagedDevice[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(devs, totalRecords);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Integer getTotalCount(List<String> filter) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.TEXT_PLAIN);

        if (filter != null && filter.size() > 0) {
            for (String sFilter : filter) {
                webClient.query("filter", sFilter);
            }
        }
        webClient.path("/count");

        return webClient.get(Integer.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ManagedDevice getDeviceInventory(String refId) {
        return getDeviceInventoryInternal(refId, false);
    }

    private ManagedDevice getDeviceInventoryInternal(String refId, boolean complianceCheck) {

        if (StringUtils.isEmpty(refId))
            return null;
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);

        if (complianceCheck) {
            webClient.path("/withcompliance/" + refId);
        } else {
            webClient.path(refId);
        }

        return webClient.get(ManagedDevice.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public UpdateDeviceInventoryResponse updateDeviceInventory(String refId,
                                                               ManagedDevice newDeviceInventory) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.put(newDeviceInventory, UpdateDeviceInventoryResponse.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public UpdateDeviceInventoryResponse[] updateDeviceInventory(List<String> refIds) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/update");

        if (refIds != null && refIds.size() > 0) {
            for (String refId : refIds) {
                webClient.query("deviceId", refId);
            }
        }

        return webClient.post(null, UpdateDeviceInventoryResponse[].class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Response deleteDeviceInventory(String refId,
                                          boolean forceDelete) {//added forceDelete parameter as respective IDeviceInventoryService method signature got changed

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);
        Response response = webClient.delete();
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
        return response;
    }

    private ResourceList<ManagedDevice> getManagedDeviceResourceList(WebClient webClient) {
        ManagedDevice[] users = webClient.get(ManagedDevice[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(users, totalRecords);
    }

    @Override
    public Response scheduleUpdate(FirmwareUpdateRequest updateRequest) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("firmware");

        Response response = webClient.put(updateRequest);
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
        return response;
    }

    @Override
    public void exportAllDevices(final File downloadFile) {
        final WebClient webClient = createStreamingWebClient();
        webClient.path("/export/csv");
        final InputStream response = webClient.get(InputStream.class);
        FileOutputStream downloadOutputStream = null;
        try {
            downloadOutputStream = new FileOutputStream(downloadFile);
            IOUtils.copy(response, downloadOutputStream);
        } catch (IOException e) {
            throw new WebApplicationException();
        } finally {
            try {
                if (null != downloadOutputStream) {
                    downloadOutputStream.close();
                }
            } catch (IOException ioe) {
                log.error(
                        " Error closing output stream for (exportAllDevices) GET /export/csv: " + ioe.getMessage());
            }
        }
    }

    @Override
    public FirmwareComplianceReport getFirmwareComplianceReportForDevice(String refId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId + "/firmware/compliancereport/");

        return webClient.get(FirmwareComplianceReport.class);
    }

    @Override
    public FirmwareDeviceInventory[] getFirmwareDeviceInventory(String refId) {

        if (StringUtils.isEmpty(refId))
            return null;
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + refId + "/firmware/");

        return webClient.get(FirmwareDeviceInventory[].class);
    }

    @Override
    public Response processServiceMode(String refId, String mode) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/servicemode");
        ServiceModeRequest serviceModeRequest = new ServiceModeRequest();
        serviceModeRequest.setRefId(refId);
        serviceModeRequest.setAction(ServiceModeAction.fromString(mode));
        return webClient.put(serviceModeRequest);
    }

    @Override
    public Response validateSoftwareComponentsForService(FirmwareUpdateRequest updateRequest) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/validatesoftwarecomponents");
        Response response = webClient.put(updateRequest);
        if (response != null && response.getStatus() != Response.Status.OK.getStatusCode())
        {
            throw new WebApplicationException(response);
        }
        return response;
    }

}
