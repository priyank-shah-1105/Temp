package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.ProxySetting;
import com.dell.asm.alcm.client.model.TestProxyResponse;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ProxySettingsServiceAdapter;

/**
 * The Class RESTProxySettingsServiceAdapter.
 */
@Component("proxySettingsServiceAdapter")
public class RESTProxySettingsServiceAdapter extends BaseServiceAdapter implements ProxySettingsServiceAdapter {

    /**
     * Instantiates a new rEST proxy settings service adapter.
     */
    @Autowired
    public RESTProxySettingsServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        this.setServicePath("/alcm/proxy");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ProxySettingsServiceAdapter#getProxySettings()
     */
    @Override
    public ProxySetting getProxySettings() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.get(ProxySetting.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ProxySettingsServiceAdapter#setProxySettings(com.dell.asm.alcm.client.model.ProxySetting)
     */
    @Override
    public void setProxySettings(ProxySetting setting) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.put(setting, ProxySetting.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ProxySettingsServiceAdapter#testProxySettings(com.dell.asm.alcm.client.model.ProxySetting)
     */
    @Override
    public TestProxyResponse testProxySettings(ProxySetting setting) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        String path = "test";

        webClient.path(path);
        return webClient.post(setting, TestProxyResponse.class);
    }

}
