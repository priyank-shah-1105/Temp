package com.dell.asm.ui.adapter.impl;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.JobsServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;
import com.dell.asm.ui.model.UIJobs;
import com.dell.pg.jraf.client.jobmgr.JrafJobInfo;

@Component("jobServiceAdapter")
public class RESTJobServiceAdapter extends BaseServiceAdapter implements JobsServiceAdapter {

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTJobServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/JRAF/job");
    }

    @Override
    public ResourceList<JrafJobInfo> getJobs(String sort, List<String> filter, Integer offset,
                                             Integer limit) {
        WebClient webClient = createWebClient();
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

        JrafJobInfo[] devs = webClient.get(JrafJobInfo[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(devs, totalRecords);
    }

    @Override
    public UIJobs getById(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(id);

        return webClient.get(UIJobs.class);
    }

    @Override
    public Response deleteJobs(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("group");
        webClient.path(id);
        Response response = webClient.delete();
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
        return response;
    }
}