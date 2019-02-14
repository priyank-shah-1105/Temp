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
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.deployment.DeploymentFilterResponse;
import com.dell.asm.asmcore.asmmanager.client.deployment.PuppetLogEntry;
import com.dell.asm.asmcore.asmmanager.client.deployment.ServerNetworkObjects;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareComplianceReport;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;


/**
 * The Class RESTDeploymentServiceAdapter, implements deployment calls in AsmManager aggregator.
 *
 */
@Component("deploymentServiceAdapter")
public class RESTDeploymentServiceAdapter extends BaseServiceAdapter implements DeploymentServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTDeploymentServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTDeploymentServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/Deployment");
    }

    @Override
    public Deployment createDeployment(Deployment deployment) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(deployment, Deployment.class);
    }

    @Override
    public DeploymentFilterResponse filterAvailableServers(ServiceTemplate template,
                                                           int numDeployments) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("filter/" + numDeployments);
        webClient.query("unique", "false");
        return webClient.post(template, DeploymentFilterResponse.class);
    }

    @Override
    public ResourceList<Deployment> getDeployments(String sort, List<String> filter, Integer offset,
                                                   Integer limit, Boolean full) {

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
        if (full != null) {
            webClient.query("full", full);
        }


        Deployment[] deps = webClient.get(Deployment[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(deps, totalRecords);

    }

    @Override
    public ResourceList<Deployment> getDeploymentSummaries(String sort,
                                                           List<String> filter,
                                                           Integer offset,
                                                           Integer limit,
                                                           Boolean full) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("summaries");

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
        if (full != null) {
            webClient.query("full", full);
        }


        Deployment[] deps = webClient.get(Deployment[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(deps, totalRecords);
    }

    @Override
    public ResourceList<Deployment> getDeploymentsFromDeviceId(String deviceId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("device/" + deviceId);

        Deployment[] deps = webClient.get(Deployment[].class);
        long totalRecords = deps.length;

        return new ResourceList<>(deps, totalRecords);
    }

    @Override
    public Deployment getDeployment(String deploymentId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(deploymentId);

        return webClient.get(Deployment.class);
    }

    @Override
    public Deployment updateDeployment(String deploymentId, Deployment mgmtDeployment) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(deploymentId);
        return webClient.put(mgmtDeployment, Deployment.class);
    }

    @Override
    public Deployment cancelDeployment(String deploymentId, Deployment mgmtDeployment) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(deploymentId + "/cancel");
        return webClient.post(mgmtDeployment, Deployment.class);
    }

    @Override
    public Response deleteDeployment(String deploymentId, String serversInInventory, String serversManagedState) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(deploymentId);
        webClient.query("serversInInventory", serversInInventory);
        if (serversManagedState != null) {
            webClient.query("serversManagedState", serversManagedState);
        }
        return webClient.delete();
    }

    //{deploymentId}/{certName}
    @Override
    public String getDeploymentLog(String deploymentId, String certName) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(deploymentId + "/" + certName);
        return webClient.get(String.class);
    }

    @Override
    public ResourceList<PuppetLogEntry> getPuppetLogs(String deploymentId, String certName,
                                                      List<String> filter) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        String path = deploymentId + "/puppetLogs/" + certName;
        webClient.path(path);

        if (filter != null && filter.size() > 0) {
            for (String sFilter : filter) {
                webClient.query("filter", sFilter);
            }
        }

        PuppetLogEntry[] logs = webClient.get(PuppetLogEntry[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(logs, totalRecords);
    }

    @Override
    public void exportAllDeployments(final File downloadFile) {
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
                        "Error closing output stream for (exportAllDeployments) GET /export/csv: " + ioe.getMessage());
            }
        }
    }

    @Override
    public ServerNetworkObjects getServerNetworkObjects(String deploymentId,
                                                        String serverComponentId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/serverNetworking/" + deploymentId + "/" + serverComponentId);

        return webClient.get(ServerNetworkObjects.class);
    }


    @Override
    public ResourceList<Deployment> getDeploymentsForNetworkId(String networkId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("network/" + networkId);

        Deployment[] deps = webClient.get(Deployment[].class);
        long totalRecords = deps.length;

        return new ResourceList<>(deps, totalRecords);
    }

    @Override
    public void deleteUsersFromDeployments(List<String> userIds) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("users");
        if (userIds != null && userIds.size() > 0) {
            for (String sFilter : userIds) {
                webClient.query("userId", sFilter);
            }
        }
        webClient.delete();
    }

    @Override
    public FirmwareComplianceReport[] getFirmwareComplianceReport(String deploymentId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(deploymentId + "/firmware/compliancereport/");

        return webClient.get(FirmwareComplianceReport[].class);
    }
}
