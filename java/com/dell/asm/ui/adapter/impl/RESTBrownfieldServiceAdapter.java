/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.adapter.impl;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.brownfield.BrownfieldServiceType;
import com.dell.asm.asmcore.asmmanager.client.brownfield.DefineServiceDefinition;
import com.dell.asm.asmcore.asmmanager.client.brownfield.ExistingService;
import com.dell.asm.asmcore.asmmanager.client.brownfield.NetworkSetting;
import com.dell.asm.asmcore.asmmanager.client.brownfield.PasswordSetting;
import com.dell.asm.asmcore.asmmanager.client.brownfield.VDSSetting;
import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponent;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.BrownfieldServiceAdapter;

@Component("brownfieldServiceAdapter")
public class RESTBrownfieldServiceAdapter extends BaseServiceAdapter implements BrownfieldServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTBrownfieldServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTBrownfieldServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/Brownfield");
    }

    /**
     * Calls out to the DeploymentService's defineService method and returns Deployment.
     */
    @Override
    public Deployment getServiceDefinition(String type,
                                           List<ServiceTemplateComponent> components,
                                           List<PasswordSetting> passwordSettings) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/defineService");

        DefineServiceDefinition defineServiceDefinition = new DefineServiceDefinition();
        defineServiceDefinition.setType(BrownfieldServiceType.fromValue(type));
        defineServiceDefinition.setComponents(components);
        defineServiceDefinition.setPasswordSettings(passwordSettings);
        return webClient.post(defineServiceDefinition, Deployment.class);
    }

    /**
     * Calls out to the DeploymentService's defineServiceDiff method and returns a Deployment.
     */
    @Override
    public Deployment getExistingServiceDiff(String deploymentId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/defineServiceDiff");
        webClient.query("serviceId", deploymentId);

        return webClient.post(null, Deployment.class);
    }

    @Override
    public Deployment createExistingService(Deployment deployment,
                                            List<PasswordSetting> passwordSettings,
                                            List<VDSSetting> vdsSettings,
                                            List<NetworkSetting> networkSettings) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/createService");

        ExistingService existingService = new ExistingService();
        existingService.setDeployment(deployment);
        existingService.setPasswordSettings(passwordSettings);
        existingService.setVdsSettings(vdsSettings);
        existingService.setNetworkSettings(networkSettings);
        return webClient.post(existingService, Deployment.class);
    }

    @Override
    public Deployment updateEsxiServiceDefinition(Deployment deployment,
                                                  List<PasswordSetting> passwordSettings,
                                                  List<VDSSetting> vdsSettings) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/updateService");

        ExistingService existingService = new ExistingService();
        existingService.setDeployment(deployment);
        existingService.setPasswordSettings(passwordSettings);
        existingService.setVdsSettings(vdsSettings);

        return webClient.post(existingService, Deployment.class);
    }

    @Override
    public List<PasswordSetting> getOSCredentials(Deployment deployment) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/getOSCredentials");

        PasswordSetting[] settings = webClient.post(deployment, PasswordSetting[].class);
        if (settings != null) {
            return Arrays.asList(settings);
        }
        return null;
    }

    @Override
    public List<VDSSetting> getVDSSettings(Deployment deployment) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/getVDSSettings");

        VDSSetting[] settings = webClient.post(deployment, VDSSetting[].class);
        if (settings != null) {
            return Arrays.asList(settings);
        }
        return null;
    }

    @Override
    public List<NetworkSetting> getNetworkSettings(Deployment deployment) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/getNetworkSettings");

        NetworkSetting[] settings = webClient.post(deployment, NetworkSetting[].class);
        if (settings != null) {
            return Arrays.asList(settings);
        }
        return null;
    }
}
