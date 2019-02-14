package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.asmmanager.client.srsconnector.SRSConnectorSettings;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.SRSConnectorAdapter;

/**
 * Implementation class for {@link SRSConnectorAdapter}
 */
@Component("srsConnectorServiceAdapter")
public class RESTSRSConnectorAdapter extends BaseServiceAdapter implements SRSConnectorAdapter {

    /**
     * Logger for this class
     */
    private final Logger log = Logger.getLogger(RESTSRSConnectorAdapter.class);
    private static final String READ_SETTINGS_ENDPOINT = "/settings";
    private static final String REGISTER_SETTINGS_ENDPOINT = "/registersettings";
    private static final String SRS_CONNECTOR_CONTEXT_PATH = "/AsmManager/srsconnector";
    private static final String SUSPEND_ENDPOINT = "/suspend";
    private static final String DEREGISTER_ENDPOINT = "/deregister";
    private static final String ENABLE_SNMP_AND_IPMI_ENDPOINT = "/enablesnmpandipmi";
    

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret
     * from SecurityContext.
     */
    @Autowired
    public RESTSRSConnectorAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath(SRS_CONNECTOR_CONTEXT_PATH);
    }

    @Override
    public SRSConnectorSettings getConfiguration() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(READ_SETTINGS_ENDPOINT);
        return webClient.get(SRSConnectorSettings.class);
    }

    @Override
    public String register(SRSConnectorSettings request) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(REGISTER_SETTINGS_ENDPOINT);
        return webClient.post(request, String.class);

    }

	@Override
	public String suspend(String duration) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(SUSPEND_ENDPOINT);
        return webClient.post(duration, String.class);
	}

    @Override
    public String deregister(String connectionType) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(DEREGISTER_ENDPOINT);
        return webClient.post(connectionType, String.class);
    }

    @Override
    public Response enableSnmpAndIpmi() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path(ENABLE_SNMP_AND_IPMI_ENDPOINT);
        return webClient.post(null, Response.class);
    }

}
