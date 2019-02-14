/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.impl;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryGroup;
import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryImportEntity;
import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryUser;
import com.dell.asm.asmcore.user.model.AddADGroupUsersRequest;
import com.dell.asm.asmcore.user.model.AddADGroupUsersResponse;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ActiveDirectoryUserServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;


/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Sood</a>
 *
 * Oct 29, 20134:17:07 PM
 */
@Component("activeDirectoryUserServiceAdapter")
public class RESTActiveDirectoryUserServiceAdapter extends
        BaseServiceAdapter implements ActiveDirectoryUserServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger
            .getLogger(RESTActiveDirectoryUserServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret
     * from SecurityContext.
     */
    @Autowired
    public RESTActiveDirectoryUserServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/ActiveDirectoryUser");
    }

    @Override
    public ResourceList<ActiveDirectoryUser> getActiveDirectoryUsers(
            String activeDirectoryName, String sort, List<String> filter,
            Integer offset, Integer limit) {
        WebClient webClient = createWebClientForADResource(activeDirectoryName, sort, filter,
                                                           offset, limit, "importentities");

        return getUserResourceList(webClient);
    }

    private ResourceList<ActiveDirectoryUser> getUserResourceList(WebClient webClient) {
        ActiveDirectoryUser[] users = webClient.get(ActiveDirectoryUser[].class);
        MultivaluedMap<String, Object> headers = webClient.getResponse().getMetadata();

        long totalRecords;
        try {
            totalRecords = Long.parseLong(
                    headers.getFirst("x-dell-collection-total-count").toString());
        } catch (NumberFormatException e) {
            totalRecords = users.length;
        }
        return new ResourceList<>(users, totalRecords);
    }

    @Override
    public ResourceList<ActiveDirectoryImportEntity> getActiveDirectoryImportEntities(
            String activeDirectoryName, String sort, List<String> filter,
            Integer offset, Integer limit) {
        WebClient webClient = createWebClientForADResource(activeDirectoryName, sort, filter,
                                                           offset, limit, "importentities");

        return getImportEntityResourceList(webClient);
    }

    private ResourceList<ActiveDirectoryImportEntity> getImportEntityResourceList(
            WebClient webClient) {
        ActiveDirectoryImportEntity[] entities = webClient.get(ActiveDirectoryImportEntity[].class);
        MultivaluedMap<String, Object> headers = webClient.getResponse().getMetadata();

        long totalRecords;
        try {
            totalRecords = Long.parseLong(
                    headers.getFirst("x-dell-collection-total-count").toString());
        } catch (NumberFormatException e) {
            totalRecords = entities.length;
        }
        return new ResourceList<>(entities, totalRecords);
    }


    @Override
    public ResourceList<ActiveDirectoryGroup> getActiveDirectoryGroupSummaries(
            String activeDirectoryName, String sort, List<String> filter,
            Integer offset, Integer limit, String searchString) {
        WebClient webClient = createWebClientForADResource(activeDirectoryName, sort, filter,
                                                           offset, limit, "group");
        webClient.query("contains", searchString);

        return getGroupResourceList(webClient);
    }

    private ResourceList<ActiveDirectoryGroup> getGroupResourceList(WebClient webClient) {
        ActiveDirectoryGroup[] groups = webClient.get(ActiveDirectoryGroup[].class);
        MultivaluedMap<String, Object> headers = webClient.getResponse().getMetadata();

        long totalRecords;
        try {
            totalRecords = Long.parseLong(
                    headers.getFirst("x-dell-collection-total-count").toString());
        } catch (NumberFormatException e) {
            totalRecords = groups.length;
        }
        return new ResourceList<>(groups, totalRecords);
    }

    @Override
    public ResourceList<ActiveDirectoryGroup> getActiveDirectoryGroupDetails(
            String activeDirectoryName, String sort, List<String> filter,
            Integer offset, Integer limit, String groupDN) {
        WebClient webClient = createWebClientForADResource(activeDirectoryName, sort, filter,
                                                           offset, limit, "group/" + groupDN);
        webClient.query("groupDN", groupDN);

        return getGroupDetailsResourceList(webClient);
    }

    private ResourceList<ActiveDirectoryGroup> getGroupDetailsResourceList(WebClient webClient) {
        ActiveDirectoryGroup[] groups = webClient.get(ActiveDirectoryGroup[].class);
        MultivaluedMap<String, Object> headers = webClient.getResponse().getMetadata();

        long totalRecords;
        try {
            totalRecords = Long.parseLong(
                    headers.getFirst("x-dell-collection-total-count").toString());
        } catch (NumberFormatException e) {
            totalRecords = groups[0].getUserList().size();
        }
        return new ResourceList<>(groups, totalRecords);
    }

    @Override
    public AddADGroupUsersResponse createUsersInADGroups(AddADGroupUsersRequest requestBody) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/group");

        return webClient.post(requestBody, AddADGroupUsersResponse.class);
    }


    /**
     * Utility to create a WebClient configured for AD user or group queries
     * @param activeDirectoryName
     * @param sort
     * @param filter
     * @param offset
     * @param limit
     * @param path
     * @return
     */
    private WebClient createWebClientForADResource(String activeDirectoryName, String sort,
                                                   List<String> filter,
                                                   Integer offset, Integer limit, String path) {
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

        webClient.path(activeDirectoryName + "/" + path);

        return webClient;
    }
}
