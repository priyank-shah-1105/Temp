package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.ALCMResponse;
import com.dell.asm.alcm.client.model.AvailableTimeZones;
import com.dell.asm.alcm.client.model.TimeZoneInfo;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ApplianceTimeZoneServiceAdapter;

/**
 * The Class RESTApplianceTimeZoneServiceAdapter.
 */
@Component("applianceTimeZoneServiceAdapter")
public class RESTApplianceTimeZoneServiceAdapter extends BaseServiceAdapter implements ApplianceTimeZoneServiceAdapter {

    private final Logger log = Logger.getLogger(RESTApplianceTimeZoneServiceAdapter.class);

    /**
     * Instantiates a new rEST appliance time zone service adapter.
     */
    @Autowired
    public RESTApplianceTimeZoneServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        this.setServicePath("/alcm/timezone");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ApplianceTimeZoneServiceAdapter#getAvailableTimeZones()
     */
    @Override
    public AvailableTimeZones getAvailableTimeZones() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        String path = "all";
        webClient.path(path);
        return webClient.get(AvailableTimeZones.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ApplianceTimeZoneServiceAdapter#getTimeZone()
     */
    @Override
    public TimeZoneInfo getTimeZone() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.get(TimeZoneInfo.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ApplianceTimeZoneServiceAdapter#setTimeZone(com.dell.asm.alcm.client.model.UITimeZoneInfo)
     */
    @Override
    public void setTimeZone(TimeZoneInfo timezone) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.put(timezone, ALCMResponse.class);
    }

}
