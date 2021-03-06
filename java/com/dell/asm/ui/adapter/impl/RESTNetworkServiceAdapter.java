package com.dell.asm.ui.adapter.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.NetworkServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;
import com.dell.pg.asm.identitypool.api.network.model.IpAddress;
import com.dell.pg.asm.identitypool.api.network.model.Network;
import com.dell.pg.asm.identitypool.api.network.model.UsageIdList;

@Component("networkServiceAdapter")
public class RESTNetworkServiceAdapter extends BaseServiceAdapter implements NetworkServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTNetworkServiceAdapter.class);

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTNetworkServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/VirtualServices/Network");
    }

    @Override
    public ResourceList<Network> getNetworks(String sort, List<String> filter, Integer offset,
                                             Integer limit) {
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

        Network[] networks = webClient.get(Network[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(networks, totalRecords);
    }

    @Override
    public List<Network> getNetworksByType(String networkType) {

        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/type/" + networkType);
        Network[] networks = webClient.get(Network[].class);
        if (networks != null && networks.length > 0) {
            return Arrays.asList(networks);
        }
        return null;
    }

    @Override
    public Network getNetwork(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);
        return webClient.get(Network.class);
    }

    @Override
    public void updateNetwork(String id, Network network) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);
        webClient.put(network, Network.class);
    }

    @Override
    public void deleteNetwork(String id) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + id);
        Response response = webClient.delete();
        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
    }

    @Override
    public Network addNetwork(Network network) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(network, Network.class);
    }

    @Override
    public void exportAllNetworks(final File downloadFile) {
        final WebClient webClient = createStreamingWebClient();
        webClient.path("/export/csv");
        final InputStream response = webClient.get(InputStream.class);

        FileOutputStream downloadOutputStream = null;
        try {
            downloadOutputStream = new FileOutputStream(downloadFile);
            IOUtils.copy(response, downloadOutputStream);
        } catch (IOException e) {
            throw new WebApplicationException();
        } finally {
            try {
                if (null != downloadOutputStream) {
                    downloadOutputStream.close();
                }
            } catch (IOException ioe) {
                log.error(
                        " Error closing output stream for (exportAllNetworks) GET /export/csv: " + ioe.getMessage());
            }
        }
    }

    @Override
    public List<IpAddress> getAllIpAddressesForNetwork(String networkId) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + networkId + "/ipaddresses");
        IpAddress[] addresses = webClient.get(IpAddress[].class);
        return Arrays.asList(addresses);
    }

    @Override
    public UsageIdList getAllDeploymentIdsForNetwork(String networkId) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/" + networkId + "/usageids");
        return webClient.get(UsageIdList.class);
    }
}
