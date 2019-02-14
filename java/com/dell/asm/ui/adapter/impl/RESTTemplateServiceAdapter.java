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

import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateExportRequest;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateUploadRequest;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.TemplateServiceAdapter;
import com.dell.asm.ui.controller.BaseController;


/**
 * The Class RESTInventoryServiceAdapter, implements device inventory calls in AsmManager aggregator.
 *
 */
@Component("templateServiceAdapter")
public class RESTTemplateServiceAdapter extends BaseServiceAdapter implements TemplateServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTTemplateServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTTemplateServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/ServiceTemplate");
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ServiceTemplate copyTemplate(String templateId, ServiceTemplate configuration) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + templateId + "/copy");
//        webClient.query("newName", name);          
        return webClient.post(configuration, ServiceTemplate.class);

    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ServiceTemplate createTemplate(ServiceTemplate mgmtTemplate) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(mgmtTemplate, ServiceTemplate.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public String exportTemplate(ServiceTemplateExportRequest template) {
        WebClient webClient = createWebClient();
        webClient.path("/export");
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(template, String.class);
    }


    @Override
    public void exportAllTemplates(final File downloadFile) {
        final WebClient webClient = createStreamingWebClient();
        webClient.path("/export/csv");
        final InputStream response = webClient.get(InputStream.class);

        FileOutputStream downloadFileOutputStream = null;
        try {
            downloadFileOutputStream = new FileOutputStream(downloadFile);
            IOUtils.copy(response, downloadFileOutputStream);
        } catch (IOException e) {
            throw new WebApplicationException();
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException ioe) {
                log.error(
                        "Error closing input stream for HTTP response to GET /export/csv: " + ioe.getMessage());
            }
            try {
                if (null != downloadFileOutputStream) {
                    downloadFileOutputStream.close();
                }
            } catch (IOException ioe) {
                log.error(
                        "Error closing download output stream for HTTP response to GET /export/csv: " + ioe.getMessage());
            }
        }
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ServiceTemplate uploadTemplate(ServiceTemplateUploadRequest uploadRequest) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/upload");

        return webClient.post(uploadRequest, ServiceTemplate.class);
    }

    @Override
    public ServiceTemplate updateParameters(ServiceTemplate template) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/updateParameters");
        return webClient.post(template, ServiceTemplate.class);
    }

    @Override
    public ServiceTemplate refreshTemplate(ServiceTemplate template,
                                           Boolean inDeployment,
                                           Boolean isBrownfield) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/refreshTemplate");
        webClient.query("inDeployment", inDeployment);
        webClient.query("isBrownfield", isBrownfield);
        return webClient.post(template, ServiceTemplate.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ResourceList<ServiceTemplate> getAllTemplates(
            String sort,
            List<String> filter,
            Integer offset,
            Integer limit,
            Boolean full,
            Boolean includeAttachments) {

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
        if (full != null) {
            webClient.query("full", full);
        }
        if (includeAttachments != null) {
            webClient.query("includeAttachments", includeAttachments);
        }
        if (filter != null && filter.size() > 0) {
            for (String sFilter : filter) {
                webClient.query("filter", sFilter);
            }
        }

        ServiceTemplate[] devs = webClient.get(ServiceTemplate[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(devs, totalRecords);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ServiceTemplate getTemplate(String templateId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(templateId);

        return webClient.get(ServiceTemplate.class);
    }

    @Override
    public ServiceTemplate getTemplate(String templateId, boolean includeBrownfieldVmManagers,
                                       boolean forDeployment) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(templateId);
        webClient.query("includeBrownfieldVmMangers", includeBrownfieldVmManagers);
        webClient.query("forDeployment", forDeployment);

        return webClient.get(ServiceTemplate.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ServiceTemplate getCustomizedTemplate(String deviceId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/device/" + deviceId);

        return webClient.get(ServiceTemplate.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ServiceTemplate getCustomizedComponent(String templateId, String serviceId,
                                                  String componentType) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        if (serviceId != null)
            webClient.path("/service/" + serviceId + "/" + componentType);
        else
            webClient.path("/template/" + templateId + "/" + componentType);

        return webClient.get(ServiceTemplate.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Response updateTemplate(String templateId, ServiceTemplate template) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(templateId);

        Response response = webClient.put(template);

        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
        return response;

    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Response deleteTemplate(String templateId) {

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
    public ServiceTemplate uploadConfiguration(String configFile) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/uploadConfig");
        webClient.query("configPath", configFile);

        return webClient.get(ServiceTemplate.class);
    }

    @Override
    public ServiceTemplate updateComponents(String templateId, String serviceId,
                                            ServiceTemplate template) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        if (serviceId != null) {
            webClient.path("/components/service/" + serviceId);
        } else {
            webClient.path("/components/template/" + templateId);
        }

        return webClient.put(template, ServiceTemplate.class);
    }

    @Override
    public void deleteUsersFromTemplates(List<String> userIds) {
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
    public ServiceTemplate cloneTemplate(ServiceTemplate serviceTemplate) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/cloneTemplate");
        return webClient.post(serviceTemplate, ServiceTemplate.class);
    }

}
