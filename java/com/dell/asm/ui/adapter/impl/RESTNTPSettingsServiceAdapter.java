package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.NTPSetting;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.NTPSettingsServiceAdapter;


/**
 * The Class RESTNTPSettingsServiceAdapter.
 */
@Component("ntpSettingsServiceAdapter")
public class RESTNTPSettingsServiceAdapter extends BaseServiceAdapter implements NTPSettingsServiceAdapter {

    /**
     * Instantiates a new rESTNTP settings service adapter.
     */
    @Autowired
    public RESTNTPSettingsServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        this.setServicePath("/alcm/ntp");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.NTPSettingsServiceAdapter#getNTPSettings()
     */
    @Override
    public NTPSetting getNTPSettings() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.get(NTPSetting.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.NTPSettingsServiceAdapter#setNTPSettings(com.dell.asm.alcm.client.model.NTPSetting)
     */
    @Override
    public void setNTPSettings(NTPSetting setting) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        Response response = webClient.put(setting);

        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.NTPSettingsServiceAdapter#deleteNTPSettings()
     */
    @Override
    public void deleteNTPSettings() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        Response response = webClient.delete();

        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
    }

}
