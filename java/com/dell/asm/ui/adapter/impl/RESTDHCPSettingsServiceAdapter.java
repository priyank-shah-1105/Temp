package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.DHCPSetting;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.DHCPSettingsServiceAdapter;


/**
 * The Class RESTDHCPSettingsServiceAdapter.
 */
@Component("dhcpSettingsServiceAdapter")
public class RESTDHCPSettingsServiceAdapter extends BaseServiceAdapter implements DHCPSettingsServiceAdapter {

    /**
     * Instantiates a new rESTDHCP settings service adapter.
     */
    @Autowired
    public RESTDHCPSettingsServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/alcm/dhcp");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.DHCPSettingsServiceAdapter#getDHCPSettings()
     */
    @Override
    public DHCPSetting getDHCPSettings() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.get(DHCPSetting.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.DHCPSettingsServiceAdapter#setDHCPSettings(com.dell.asm.alcm.client.model.DHCPSetting)
     */
    @Override
    public void setDHCPSettings(DHCPSetting setting) {
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
     * @see com.dell.asm.ui.adapter.service.DHCPSettingsServiceAdapter#deleteDHCPSettings()
     */
    @Override
    public void deleteDHCPSettings() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        Response response = webClient.delete();

        if (response != null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) // have to add this code. Not sure why above put does not throw exception
        {
            throw new WebApplicationException(response);
        }
    }

}
