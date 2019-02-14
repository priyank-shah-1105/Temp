/*
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied
 * or disclosed except in accordance with the terms of that agreement.
 * Copyright (c) 2010-2011 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.authorization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.dell.asm.alcm.client.model.WizardStatus;
import com.dell.asm.i18n2.AsmDetailedMessage;
import com.dell.asm.i18n2.AsmDetailedMessageList;
import com.dell.asm.rest.common.AsmConstants;
import com.dell.asm.ui.adapter.service.ASMSetupStatusServiceAdapter;
import com.dell.asm.ui.exception.ASMUIAuthenticationException;
import com.dell.asm.ui.model.ErrorObj;
import com.dell.asm.ui.model.JobLoginRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.UILoginResult;
import com.dell.asm.ui.spring.ASMUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class ASMUIUsernamePasswordAuthenticationFilter.
 */
public class ASMUIUsernamePasswordAuthenticationFilter extends
        UsernamePasswordAuthenticationFilter implements ApplicationContextAware {
    private static final Logger log = Logger.getLogger(
            ASMUIUsernamePasswordAuthenticationFilter.class);
    /**
     * The context.
     */
    private ApplicationContext context;
    private ASMSetupStatusServiceAdapter asmSetupStatusServiceAdapter;
    private ObjectMapper objectMapper;

    private String jsonUsername;
    private String jsonPassword;

    public ASMUIUsernamePasswordAuthenticationFilter(
            ASMSetupStatusServiceAdapter asmSetupStatusServiceAdapter,
            SessionAuthenticationStrategy sessionAuthenticationStrategy,
            AuthenticationManager authenticationManager,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler,
            ObjectMapper objectMapper) {
        super();
        setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        setAuthenticationManager(authenticationManager);
        setAuthenticationFailureHandler(authenticationFailureHandler);
        setAuthenticationSuccessHandler(authenticationSuccessHandler);

        setUsernameParameter("requestObj[username]");
        setPasswordParameter("requestObj[password]");
        setFilterProcessesUrl("/login");

        this.asmSetupStatusServiceAdapter = asmSetupStatusServiceAdapter;
        this.objectMapper = objectMapper;


    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {

        String contentType = request.getContentType();
        if (contentType == null) {
            contentType = request.getHeader("Content-Type");
        }

        if (contentType != null && contentType.contains("application/json")) {
            try {
                /*
                 * HttpServletRequest can be read only once
                 */
                StringBuilder sb = new StringBuilder();
                String line;

                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                //json transformation
                JobLoginRequest loginRequest = objectMapper.readValue(sb.toString(),
                                                                      JobLoginRequest.class);

                if (loginRequest != null && loginRequest.requestObj != null) {
                    this.jsonUsername = loginRequest.requestObj.Username;
                    this.jsonPassword = loginRequest.requestObj.Password;
                }
            } catch (Exception e) {
                log.error("Error marshalling login request!", e);
            }
        }

        return super.attemptAuthentication(request, response);
    }

    /**
     * @see org.springframework.security.web.authentication
     * .AbstractAuthenticationProcessingFilter#successfulAuthentication(javax.servlet.http
     * .HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, org.springframework.security.core
     *      .Authentication)
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);

        // Set<GrantedAuthority> authorities = new
        // HashSet<GrantedAuthority>(authResult.getAuthorities());

        log.debug("Building Successful Authentication response");
        JobResponse jobResponse = new JobResponse();
        UILoginResult result = new UILoginResult();
        jobResponse.responseCode = 0;
        result.url = "index.html";

        // check for show getting started
        boolean showGettingStarted = true;
        try {
            asmSetupStatusServiceAdapter = (ASMSetupStatusServiceAdapter) context.getBean(
                    "asmSetupStatusServiceAdapter");

            WizardStatus wizardStatus = asmSetupStatusServiceAdapter.getASMSetupStatus();
            if (wizardStatus != null) {
                showGettingStarted = wizardStatus.getShowGettingStarted();
            }
        } catch (Exception e) {
            log.error(
                    "Error talking to ASMSetupStatusServiceAdapter in attempt to get ShowGettingStarted condition",
                    e);
        }

        ASMUserDetails userDetails =
                (ASMUserDetails) authResult.getPrincipal();

        String role = "";
        for (GrantedAuthority auth : userDetails.getAuthorities()) {
            if (auth.getAuthority() != null) {
                role = auth.getAuthority();
                break;
            }
        }

        if (showGettingStarted && role.equals(AsmConstants.USERROLE_ADMINISTRATOR)) {
            result.route = "gettingstarted/menu";
        } else {
            result.route = "home";
        }

        result.success = true;
        jobResponse.responseObj = result;
        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);
        responseWrapper.setContentType("application/json");
        Writer out = responseWrapper.getWriter();
        out.write(objectMapper.writeValueAsString(jobResponse));
        out.close();

    }

    private void handleLoginFailure(final HttpServletResponse response,
                                    final String responseMessage,
                                    final AsmDetailedMessageList errorMessages)
            throws IOException {
        JobResponse jobResponse = new JobResponse();
        UILoginResult result = new UILoginResult();
        jobResponse.responseCode = 0;
        result.url = "login.html";
        result.route = "";
        result.success = false;
        if (StringUtils.isNotBlank(responseMessage)) {
            jobResponse.responseCode = -1;
            final ErrorObj errorObj = new ErrorObj();
            errorObj.errorMessage = responseMessage;
            jobResponse.errorObj = errorObj;
        }
        if (errorMessages != null) {
            jobResponse.responseCode = -1;
            final ErrorObj errorObj = new ErrorObj();
            final StringBuilder errorMessage = new StringBuilder();
            for (AsmDetailedMessage msg : errorMessages.getMessages()) {
                // we just want display messages here
                errorMessage.append(msg.getDisplayMessage());
            }
            errorObj.errorMessage = errorMessage.toString();
            jobResponse.errorObj = errorObj;
        }
        jobResponse.responseObj = result;
        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);
        responseWrapper.setContentType("application/json");
        Writer out = responseWrapper.getWriter();
        out.write(objectMapper.writeValueAsString(jobResponse));
        out.close();
    }

    /**
     * @see org.springframework.security.web.authentication
     * .AbstractAuthenticationProcessingFilter#unsuccessfulAuthentication(javax.servlet.http
     * .HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, org.springframework.security.core
     *      .AuthenticationException)
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        String message = StringUtils.EMPTY;
        AsmDetailedMessageList errorMessages = null;
        if (failed != null) {
            if (failed instanceof ASMUIAuthenticationException) {
                errorMessages = ((ASMUIAuthenticationException) failed).getErrorMessageList();
            }
            if (StringUtils.isNotBlank(failed.getMessage())) {
                if (failed.getMessage().startsWith("error.")) {
                    message = messages.getMessage(failed.getMessage());

                } else {
                    message = failed.getMessage();
                }
            }
        }
        handleLoginFailure(response, message, errorMessages);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String password;

        if (this.jsonPassword != null) {
            password = this.jsonPassword;
        } else {
            password = super.obtainPassword(request);
        }

        return password;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String username;

        if (this.jsonUsername != null) {
            username = this.jsonUsername;
        } else {
            username = super.obtainUsername(request);
        }
        return username;
    }

}
