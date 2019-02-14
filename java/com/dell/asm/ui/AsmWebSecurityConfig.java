/**
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **/
package com.dell.asm.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.dell.asm.ui.adapter.service.ASMSetupStatusServiceAdapter;
import com.dell.asm.ui.authorization.ASMUIAuthenticationProvider;
import com.dell.asm.ui.authorization.ASMUILogoutSuccessHandler;
import com.dell.asm.ui.authorization.ASMUIRedirectStrategy;
import com.dell.asm.ui.authorization.ASMUIRestAuthenticationEntryPoint;
import com.dell.asm.ui.authorization.ASMUISessionInformationExpiredStrategy;
import com.dell.asm.ui.authorization.ASMUIUsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AsmWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private ASMSetupStatusServiceAdapter asmSetupStatusServiceAdapter;

    private ASMUIAuthenticationProvider asmuiAuthenticationProvider;

    private ASMUIRestAuthenticationEntryPoint asmuiRestAuthenticationEntryPoint;


    private ASMUISessionInformationExpiredStrategy asmuiSessionInformationExpiredStrategy;

    private MessageSource messageSource;

    private ObjectMapper objectMapper;

    @Autowired
    public AsmWebSecurityConfig(ASMSetupStatusServiceAdapter asmSetupStatusServiceAdapter,
                                ASMUIAuthenticationProvider asmuiAuthenticationProvider,
                                ASMUIRestAuthenticationEntryPoint asmuiRestAuthenticationEntryPoint,
                                ASMUISessionInformationExpiredStrategy asmuiSessionInformationExpiredStrategy,
                                MessageSource messageSource, ObjectMapper objectMapper) {
        super();
        this.asmSetupStatusServiceAdapter = asmSetupStatusServiceAdapter;
        this.asmuiAuthenticationProvider = asmuiAuthenticationProvider;
        this.asmuiRestAuthenticationEntryPoint = asmuiRestAuthenticationEntryPoint;
        this.asmuiSessionInformationExpiredStrategy = asmuiSessionInformationExpiredStrategy;
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.addFilterBefore(asmuiUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(concurrentSessionFilter(), ConcurrentSessionFilter.class);

        //disable CSRF
        http.csrf().disable();

        //setup authorizations
        http.authorizeRequests()
                .antMatchers("/",
                             "/login.html",
                             "/login",
                             "/login/dologout",
                             "/sitewidefunctions/**",
                             "/js/**",
                             "/images/**",
                             "/css/**",
                             "/appliance/getstatus",
                             "/status.html")
                .permitAll()
                .anyRequest().hasAnyAuthority("Administrator", "standard", "ReadOnly");

        // add exception handling
        http.exceptionHandling()
                .authenticationEntryPoint(asmuiRestAuthenticationEntryPoint);

        http.sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy());

        //add logout
        http.logout()
                .addLogoutHandler(logoutHandler())
                .logoutSuccessHandler(logoutSuccessHandler());
    }

    @Override
    protected AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(asmuiAuthenticationProvider));
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        return methodInvokingFactoryBean;
    }

    @Bean
    public ASMUIUsernamePasswordAuthenticationFilter asmuiUsernamePasswordAuthenticationFilter() {
        return new ASMUIUsernamePasswordAuthenticationFilter(asmSetupStatusServiceAdapter,
                                                             sessionAuthenticationStrategy(),
                                                             authenticationManager(),
                                                             authenticationSuccessHandler(),
                                                             authenticationFailureHandler(),
                                                             objectMapper);
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {

        List<SessionAuthenticationStrategy> sessionAuthenticationStrategies = new ArrayList<>();

        ConcurrentSessionControlAuthenticationStrategy controlAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        controlAuthenticationStrategy.setMaximumSessions(10);
        controlAuthenticationStrategy.setMessageSource(messageSource);
        sessionAuthenticationStrategies.add(controlAuthenticationStrategy);

        sessionAuthenticationStrategies.add(new SessionFixationProtectionStrategy());
        sessionAuthenticationStrategies.add(new RegisterSessionAuthenticationStrategy(sessionRegistry()));

        return new CompositeSessionAuthenticationStrategy(sessionAuthenticationStrategies);
    }

    @Bean
    public ConcurrentSessionFilter concurrentSessionFilter() {
        return new ConcurrentSessionFilter(sessionRegistry(), new ASMUISessionInformationExpiredStrategy());
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        authenticationSuccessHandler.setDefaultTargetUrl("/index.html");
        authenticationSuccessHandler.setRedirectStrategy(new ASMUIRedirectStrategy());
        return authenticationSuccessHandler;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/");
    }

    @Bean
    public LogoutHandler logoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new ASMUILogoutSuccessHandler();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}
