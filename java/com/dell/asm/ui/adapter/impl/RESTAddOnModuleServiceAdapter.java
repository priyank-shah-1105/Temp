package com.dell.asm.ui.adapter.impl;

import java.net.URL;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.addonmodule.AddOnModule;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.AddOnModuleServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;

@Component("addOnModuleServiceAdapter")
public class RESTAddOnModuleServiceAdapter extends BaseServiceAdapter implements AddOnModuleServiceAdapter {

    @Autowired
    public RESTAddOnModuleServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/AsmManager/addOnModule");
    }

    @Override
    public AddOnModule createAddOnModule(final URL addOnModuleUrl) {
        final WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        final AddOnModule addOnModule = new AddOnModule();
        addOnModule.setUploadUrl(addOnModuleUrl);
        return webClient.post(addOnModule, AddOnModule.class);
    }

    @Override
    public AddOnModule getAddOnModule(final String addOnModuleId) {
        final WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(addOnModuleId);
        return webClient.get(AddOnModule.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResourceList<AddOnModule> getAddOnModules(final String sort, final List<String> filter,
                                                     final Integer offset,
                                                     final Integer limit) {
        final WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);

        if (offset != null) {
            webClient.query("offset", offset);
        }
        if (limit != null) {
            webClient.query("limit", limit);
        }
        if (sort != null) {
            webClient.query("sort", sort);
        }
        if (filter != null && filter.size() > 0) {
            for (String sFilter : filter) {
                webClient.query("filter", sFilter);
            }
        }
        AddOnModule[] addOnModules = webClient.get(AddOnModule[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(addOnModules, totalRecords);
    }

    @Override
    public Response deleteAddOnModule(final String addOnModuleId) {
        final WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(addOnModuleId);
        final Response response = webClient.delete();
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            // have to add this code. Not sure why above put does not throw exception
            throw new WebApplicationException(response);
        }
        return response;
    }

}
