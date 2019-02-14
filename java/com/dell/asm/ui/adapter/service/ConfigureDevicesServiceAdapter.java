package com.dell.asm.ui.adapter.service;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.configure.ConfigurationRequest;
import com.dell.asm.asmcore.asmmanager.client.configure.ConfigurationResponse;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;

public interface ConfigureDevicesServiceAdapter {

    public Response processConfiguration(ConfigurationRequest request);

    public ConfigurationResponse configurationAndDiscoveryRequest(
            ConfigurationRequest discoveryRequestList);

    public String configurationRequest(ServiceTemplate config);
}
