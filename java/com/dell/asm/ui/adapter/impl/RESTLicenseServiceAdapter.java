package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.LicenseDetails;
import com.dell.asm.alcm.client.model.UploadLicenseRequest;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.LicenseServiceAdapter;

/**
 * The Class RESTProxySettingsServiceAdapter.
 */
@Component("licenseServiceAdapter")
public class RESTLicenseServiceAdapter extends BaseServiceAdapter implements LicenseServiceAdapter {

    private final Logger log = Logger.getLogger(RESTLicenseServiceAdapter.class);

    /**
     * Instantiates a new rEST proxy settings service adapter.
     */
    @Autowired
    public RESTLicenseServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/alcm/license");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.LicenseServiceAdapter#getLicenseDetails
     */
    @Override
    public LicenseDetails getLicenseDetails() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.get(LicenseDetails.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.LicenseServiceAdapter#uploadLicense
     */
    @Override
    public LicenseDetails uploadLicense(UploadLicenseRequest uploadLicenseRequest,
                                        Boolean storeLicense, Boolean forceStoreWithWarnings) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.query("store", storeLicense);
        webClient.query("force", forceStoreWithWarnings);
        return webClient.put(uploadLicenseRequest, LicenseDetails.class);
    }

}
