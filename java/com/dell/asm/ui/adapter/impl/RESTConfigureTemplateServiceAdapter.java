package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateUploadRequest;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ConfigureTemplateServiceAdapter;

@Component("configureTemplateServiceAdapter")
public class RESTConfigureTemplateServiceAdapter extends BaseServiceAdapter implements ConfigureTemplateServiceAdapter {

    private final Logger logger = Logger.getLogger(RESTConfigureTemplateServiceAdapter.class);

    @Autowired
    public RESTConfigureTemplateServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/ConfigureTemplate");
    }

    @Override
    public ServiceTemplate uploadTemplate(ServiceTemplateUploadRequest uploadRequest) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/upload");

        return webClient.post(uploadRequest, ServiceTemplate.class);
    }

    @Override
    public ServiceTemplate createTemplate(ServiceTemplate serviceTemplate) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(serviceTemplate, ServiceTemplate.class);
    }

}
