/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.user.model.AddADGroupUsersRequest;
import com.dell.asm.asmcore.user.model.User;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.UserManagementServiceAdapter;


/**
 * The Class RESTUserManagementServiceAdapter, implements login to NBI API.
 */
@Component("userManagementServiceAdapter")
public class RESTUserManagementServiceAdapter extends BaseServiceAdapter implements UserManagementServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTUserManagementServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTUserManagementServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/user");
    }

    @Override
    public User createUser(com.dell.asm.asmcore.user.model.User user) {
        // TODO Auto-generated method stub
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);

        return webClient.post(user, User.class);
    }

    @Override
    public User[] createUsersInADGroups(AddADGroupUsersRequest requestBody) {
        // TODO Auto-generated method stub
        WebClient webClient = createWebClient();
        webClient.path("/group");
        webClient.accept(MediaType.APPLICATION_XML);

        return webClient.post(requestBody, User[].class);
    }

    @Override
    public ResourceList<User> getAllUsers(java.lang.String sort,
                                          java.util.List<java.lang.String> filter,
                                          java.lang.Integer offset, java.lang.Integer limit) {

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

        return getUserResourceList(webClient);
    }

    private ResourceList<User> getUserResourceList(WebClient webClient) {
        User[] users = webClient.get(User[].class);
        if (users == null) {
            users = new User[0];
        }
        MultivaluedMap<String, Object> headers = webClient.getResponse().getMetadata();

        long totalRecords;
        try {
            totalRecords = Long.parseLong(
                    headers.getFirst("x-dell-collection-total-count").toString());
        } catch (Exception e) {
            totalRecords = users.length;
        }
        return new ResourceList<>(users, totalRecords);
    }

    @Override
    public User getUserByID(java.lang.String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);
        return webClient.get(User.class);
    }


    @Override
    public void deleteUser(java.lang.String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);

        Response response = webClient.delete();
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }

    }

    @Override
    public User updateUser(java.lang.String id, User user) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);

        return webClient.put(user, User.class);
    }
}
