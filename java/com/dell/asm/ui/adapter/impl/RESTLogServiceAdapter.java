package com.dell.asm.ui.adapter.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.localizablelogger.LocalizedLogMessage;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.LogServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.BaseController;

/**
 * The Class RESTLogServiceAdapter.
 */
@Component("logServiceAdapter")
public class RESTLogServiceAdapter extends BaseServiceAdapter implements LogServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTLogServiceAdapter.class);

    /**
     * Instantiates a new service adapter.
     */
    @Autowired
    public RESTLogServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/UserLogManager/userlogmessage");
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public ResourceList<LocalizedLogMessage> getUserLogMessages(String sort,
                                                                List<String> filter,
                                                                Integer offset,
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

        LocalizedLogMessage[] devs = webClient.get(LocalizedLogMessage[].class);
        long totalRecords = BaseController.getTotalCount(webClient.getResponse().getMetadata());
        return new ResourceList<>(devs, totalRecords);
    }

    @Override
    public void exportAllUserLogMessages(final File downloadFile) {
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
                        " Error closing output stream for (exportAllUserLogMessages) GET /export/csv: " + ioe.getMessage());
            }
        }
    }

    @Override
    public Integer purgeLogs(String olderThan, String severity) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.TEXT_PLAIN);

        if (severity != null)
            webClient.path("/delete/" + severity);
        else
            webClient.path("/delete");

        return webClient.post(olderThan, Integer.class);
    }

}
