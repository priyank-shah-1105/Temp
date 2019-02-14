package com.dell.asm.ui.adapter.impl;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareBundle;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.SoftwareBundleServiceAdapter;
import com.dell.asm.ui.controller.BaseController;

@Component("softwareBundleServiceAdapter")
public class RESTSoftwareBundleServiceAdapter extends BaseServiceAdapter
        implements SoftwareBundleServiceAdapter {

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret
     * from SecurityContext.
     */
    @Autowired
    public RESTSoftwareBundleServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/softwareBundleFirmware");
    }

    @Override
    public ResourceList<SoftwareBundle> getAllSoftwareBundles(String sort,
                                                              List<String> filter, Integer offset,
                                                              Integer limit, String fwRepoId) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);

        if (sort != null) {
            webClient.query("sort", sort);
        }
        if (offset != null) {
            webClient.query("offset", offset);
        }
        if (limit != null) {
            webClient.query("limit", limit);
        }
        if (filter != null && filter.size() > 0) {
            for (String sFilter : filter) {
                webClient.query("filter", sFilter);
            }
        }
        webClient.path("/" + fwRepoId);
        SoftwareBundle[] networks = webClient.get(SoftwareBundle[].class);
        long totalRecords = BaseController.getTotalCount(webClient
                                                                 .getResponse().getMetadata());
        return new ResourceList<>(networks, totalRecords);
    }

    @Override
    public SoftwareBundle getSoftwareBundle(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);
        return webClient.get(SoftwareBundle.class);
    }

    @Override
    public void updateSoftwareBundle(String id, SoftwareBundle softwareBundle
    ) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);
        webClient.put(softwareBundle, SoftwareBundle.class);

    }

    @Override
    public void deleteSoftwareBundle(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);
        webClient.delete();

    }

    @Override
    public SoftwareBundle addSoftwareBundle(SoftwareBundle softwareBundle
    ) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(softwareBundle, SoftwareBundle.class);
    }

}
