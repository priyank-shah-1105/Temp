package com.dell.asm.ui.adapter.impl;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.TroubleshootingBundleResponse;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ApplianceTroubleshootingBundleServiceAdapter;

/**
 * The Class RESTApplianceTimeZoneServiceAdapter.
 */
@Component("applianceTroubleshootingBundleServiceAdapter")
public class RESTApplianceTroubleshootingBundleServiceAdapter extends BaseServiceAdapter implements ApplianceTroubleshootingBundleServiceAdapter {

    private final Logger log = Logger.getLogger(
            RESTApplianceTroubleshootingBundleServiceAdapter.class);

    /**
     * Instantiates a new rEST appliance time zone service adapter.
     */
    @Autowired
    public RESTApplianceTroubleshootingBundleServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/alcm/troubleshootingBundle");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ApplianceTroubleshootingBundleServiceAdapter#generateBundle
     */
    @Override
    public TroubleshootingBundleResponse generateBundle() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("");
        return webClient.post(null, TroubleshootingBundleResponse.class);
    }

    @Override
    public InputStream downloadBundle() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/download");
        return webClient.get(InputStream.class);
    }
}
