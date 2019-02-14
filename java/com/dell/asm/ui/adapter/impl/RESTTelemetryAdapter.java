/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.adapter.impl;

import com.dell.asm.asmcore.asmmanager.client.telemetryconnector.HistoricalPerformanceMetrics;
import com.dell.asm.asmcore.asmmanager.client.telemetryconnector.LatestPerformanceMetrics;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.TelemetryServiceAdapter;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component("telemetryServiceAdapter")
public class RESTTelemetryAdapter extends BaseServiceAdapter implements TelemetryServiceAdapter {

    private final Logger log = Logger.getLogger(RESTTelemetryAdapter.class);

    private static final String TELEMETRY_CONNECTOR_CONTEXT_PATH = "/AsmManager/telemetryconnector";
    private static final String LATEST_PERFORMANCE_METRICS_ENDPOINT = "/workload";

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret
     * from SecurityContext.
     */
    @Autowired
    public RESTTelemetryAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath(TELEMETRY_CONNECTOR_CONTEXT_PATH);
    }

    @Override
    public LatestPerformanceMetrics getLatestPerformanceMetrics(String host) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(LATEST_PERFORMANCE_METRICS_ENDPOINT);
        if (host != null) {
            webClient.query("host", host);
        }
        return webClient.get(LatestPerformanceMetrics.class);
    }

    @Override
    public HistoricalPerformanceMetrics getHistoricalPerformanceMetrics(
            String host, String frequency, String metricType) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + metricType);

        if (host != null) {
            webClient.query("host", host);
        }
        if(frequency != null) {
            webClient.query("frequency", frequency);
        }
        return webClient.get(HistoricalPerformanceMetrics.class);
    }
}
