/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.impl;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.credential.AsmCredentialDTO;
import com.dell.asm.asmcore.asmmanager.client.credential.AsmCredentialListDTO;
import com.dell.asm.encryptionmgr.client.CredentialType;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.CredentialServiceAdapter;
import com.dell.asm.ui.model.credential.UICredential;
import com.dell.asm.ui.util.MappingUtils;


/**
 * The Class RESTUserServiceAdapter, implements login to NBI API.
 */
@Component("credentialServiceAdapter")
public class RESTCredentialServiceAdapter extends BaseServiceAdapter implements CredentialServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTCredentialServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTCredentialServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/credential");
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public AsmCredentialListDTO getAllCredentials(CredentialType typeFilter, java.lang.String sort,
                                                  java.util.List<java.lang.String> filter,
                                                  java.lang.Integer offset,
                                                  java.lang.Integer limit) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        if (typeFilter != null) {
            switch (typeFilter) {
            case CHASSIS:
                webClient.query("type", UICredential.TYPE_CHASSIS.toUpperCase());
                break;
            case SERVER:
                webClient.query("type", UICredential.TYPE_SERVER.toUpperCase());
                break;
            case IOM:
                webClient.query("type", UICredential.TYPE_IOM.toUpperCase());
                break;
            case STORAGE:
                webClient.query("type", UICredential.TYPE_STORAGE.toUpperCase());
                break;
            case VCENTER:
                webClient.query("type", UICredential.TYPE_VCENTER.toUpperCase());
                break;
            case SCVMM:
                webClient.query("type", UICredential.TYPE_SCVMM.toUpperCase());
                break;
            case EM:
                webClient.query("type", UICredential.TYPE_EM.toUpperCase());
                break;
            case SCALEIO:
                webClient.query("type", UICredential.TYPE_SCALEIO.toUpperCase());
                break;
            case OS:
                webClient.query("type", UICredential.TYPE_OS.toUpperCase());
                break;
            }
        }
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

        return webClient.get(AsmCredentialListDTO.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public AsmCredentialDTO createCredential(UICredential credentialData) {

        AsmCredentialDTO dto = MappingUtils.createDTOFromUICredential(credentialData);
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(dto, AsmCredentialDTO.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void deleteCredential(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(id);
        Response response = webClient.delete();
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public AsmCredentialDTO getCredential(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(id);

        return webClient.get(AsmCredentialDTO.class);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public AsmCredentialDTO updateCredential(String id, UICredential credentialData) {
        AsmCredentialDTO dto = MappingUtils.createDTOFromUICredential(credentialData);

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(id);

        return webClient.put(dto, AsmCredentialDTO.class);
    }


}
