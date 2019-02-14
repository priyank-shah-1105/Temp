package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.perfmonitoring.PerformanceMetric;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.PerformanceMonitorServiceAdapter;

@Component("performanceMonitorServiceAdapter")
public class RESTPerformanceMonitorServiceAdapter extends BaseServiceAdapter implements PerformanceMonitorServiceAdapter {

    @Autowired
    public RESTPerformanceMonitorServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/PerformanceMetric");
    }

    @Override
    public PerformanceMetric[] performanceMonitoring(String refId, String duration, String time) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_JSON);
        //webClient.getHeaders();
        webClient.path(refId);
        webClient.path(duration + "/" + time);
        return webClient.get(PerformanceMetric[].class);
    }

}
