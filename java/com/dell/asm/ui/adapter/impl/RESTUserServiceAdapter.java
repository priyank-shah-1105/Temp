package com.dell.asm.ui.adapter.impl;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.user.model.AuthenticateRequest;
import com.dell.asm.asmcore.user.model.AuthenticateResponse;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.UserServiceAdapter;
import com.dell.asm.ui.exception.ASMUIAuthenticationException;
import com.dell.asm.usermanager.LocalUserDomain;

/**
 * The Class RESTUserServiceAdapter, implements login to NBI API.
 */
@Component("userServiceAdapter")
public class RESTUserServiceAdapter extends BaseServiceAdapter implements UserServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTUserServiceAdapter.class);

    /**
     * Instantiates a new service adapter.
     */
    @Autowired
    public RESTUserServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/authenticate");
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public AuthenticateResponse login(String userName,
                                      String password,
                                      String domain) throws AuthenticationException {
        final AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        if (userName.contains("/")) {
            String[] split = userName.split("/");
            authenticateRequest.setUserName(split[1]);
            authenticateRequest.setDomain(split[0]);
        } else if (userName.contains("\\")) {
            String[] split = userName.split("\\\\");
            authenticateRequest.setUserName(split[1]);
            authenticateRequest.setDomain(split[0]);
        } else {
            authenticateRequest.setUserName(userName);
            authenticateRequest.setDomain(LocalUserDomain.DOMAIN_NAME);
        }
        authenticateRequest.setPassword(password);

        WebClient webClient = createWebClient();
        // NOTE[fcarta] There is an odd bug in the Apache Http client code that removes the response entity
        // when the status is 401 unless the http client chunking prop is set to false. In order to get the
        // authentication error messages back we have added the "hack" below to turn off chunking and allow the
        // response entity to persist so we can get the auth error messages when a 401 error is thrown.  
        // references to bugs:
        // https://issues.apache.org/jira/browse/CXF-3304
        // https://issues.apache.org/jira/browse/CXF-5267
        WebClient.getConfig(webClient).getHttpConduit().getClient().setAllowChunking(false);
        // end of NOTE[fcarta]
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(this.getServiceUrl());

        try {
            return webClient.post(authenticateRequest, AuthenticateResponse.class);
        } catch (WebApplicationException ex) {
            log.error("Authentication error", ex);
            throw new ASMUIAuthenticationException(ex);
        }
    }

}
