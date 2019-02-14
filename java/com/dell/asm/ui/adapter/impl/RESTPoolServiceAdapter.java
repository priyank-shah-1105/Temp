package com.dell.asm.ui.adapter.impl;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.PoolServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;
import com.dell.pg.asm.identitypool.api.ioidentity.model.VirtualIdentity;
import com.dell.pg.asm.identitypool.api.iopool.model.Pool;

@Component("poolServiceAdapter")
public class RESTPoolServiceAdapter extends BaseServiceAdapter implements PoolServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTPoolServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTPoolServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/VirtualServices/Pool");
    }

    @Override
    public ResourceList<Pool> getPools(String sort, List<String> filter, Integer offset,
                                       Integer limit) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        if (sort != null)
            webClient.query("sort", sort);
        if (offset != null)
            webClient.query("offset", offset);
        if (limit != null)
            webClient.query("limit", limit);
        if (filter != null && filter.size() > 0) {
            for (String sFilter : filter) {
                webClient.query("filter", sFilter);
            }
        }

        Pool[] pools = webClient.get(Pool[].class);

        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());

        return new ResourceList<>(pools, totalRecords);


    }

    @Override
    public Pool getPool(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);
        return webClient.get(Pool.class);
    }

    @Override
    public void updatePool(String poolId, Pool pool) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + poolId);
        webClient.put(pool, Pool.class);
    }

    @Override
    public void deletePool(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);
        webClient.delete();
    }

    @Override
    public Pool addPool(Pool pool) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(pool, Pool.class);
    }


    /**
     * TODO: This needs to be moved to a different adapter for service /VirtualServices/VirtualIdentity
     *
     * /pools/{poolId}/IdentityPools/{identityPoolType}/identities
     *
     * @param poolId
     *            the id of the pool.
     * @param idType
     *            identity type.
     *
     * @return
     */
    @Override
    public VirtualIdentity[] getVirtualIdentities(String poolId, String idType) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + poolId + "/IdentityPools/" + idType + "/identities");
        return webClient.get(VirtualIdentity[].class);
    }


    @Override
    public void generateVirtualIdentity(String poolId, String type, Integer count) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + poolId + "/generateVirtualIdentity");
        webClient.query("type", type);
        webClient.query("count", count);
        Response response = webClient.put(poolId);
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            throw new WebApplicationException(response);
        }

    }
}
