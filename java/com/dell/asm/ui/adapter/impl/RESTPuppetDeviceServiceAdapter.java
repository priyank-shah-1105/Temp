/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.puppetdevice.PuppetDevice;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.PuppetDeviceServiceAdapter;

/**
 * The Class RESTServerServiceAdapter, implements server RA client calls.
 *
 */
@Component("puppetDeviceServiceAdapter")
public class RESTPuppetDeviceServiceAdapter extends BaseServiceAdapter implements PuppetDeviceServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTPuppetDeviceServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTPuppetDeviceServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/PuppetDevice");
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public PuppetDevice getPuppetDevice(String refId) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(refId);

        return webClient.get(PuppetDevice.class);
    }

}
