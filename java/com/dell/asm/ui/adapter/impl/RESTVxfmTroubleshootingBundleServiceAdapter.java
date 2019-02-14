/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.adapter.impl;

import com.dell.asm.asmcore.asmmanager.client.troubleshootingbundle.TroubleshootingBundleParams;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.VxfmTroubleshootingBundleServiceAdapter;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The Class RESTVxfmTroubleshootingBundleServiceAdapter.
 */
@Component("vxfmTroubleshootingBundleServiceAdapter")
public class RESTVxfmTroubleshootingBundleServiceAdapter extends BaseServiceAdapter implements VxfmTroubleshootingBundleServiceAdapter {

    private final Logger log = Logger.getLogger(
            RESTVxfmTroubleshootingBundleServiceAdapter.class);

    /**
     * Instantiates a new TroubleshootingBundle service adapter. Sets REST client's API key
     */
    @Autowired
    public RESTVxfmTroubleshootingBundleServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/troubleshootingbundle");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ApplianceTroubleshootingBundleServiceAdapter#generateBundle
     */
    @Override
    public String exportTroubleShootingBundle(TroubleshootingBundleParams troubleshootingBundleParams) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("export");
        return webClient.post(troubleshootingBundleParams, String.class);
    }

    @Override
    public Response testTroubleshootingBundle(TroubleshootingBundleParams troubleshootingBundleParams) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("test");
        Response response = webClient.post(troubleshootingBundleParams);
        // have to add this code. Not sure why above put does not throw exception
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode())
        {
            throw new WebApplicationException(response);
        }
        return response;
    }
}
