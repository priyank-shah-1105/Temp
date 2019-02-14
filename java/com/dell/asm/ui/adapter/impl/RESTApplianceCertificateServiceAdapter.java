package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.CertificateInfo;
import com.dell.asm.alcm.client.model.SSLCertUploadRequest;
import com.dell.asm.alcm.client.model.SSLCertificateInfo;
import com.dell.asm.alcm.client.model.SSLcertificateResponse;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ApplianceCertificateServiceAdapter;

/**
 * The Class RESTApplianceTimeZoneServiceAdapter.
 */
@Component("applianceCertificateServiceAdapter")
public class RESTApplianceCertificateServiceAdapter extends BaseServiceAdapter implements ApplianceCertificateServiceAdapter {

    private final Logger log = Logger.getLogger(RESTApplianceCertificateServiceAdapter.class);

    /**
     * Instantiates a new rEST appliance time zone service adapter.
     */
    @Autowired
    public RESTApplianceCertificateServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/alcm/sslcertificate");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ApplianceCertificateServiceAdapterServiceAdapter#createCSR
     */
    @Override
    public SSLcertificateResponse createCSR(CertificateInfo certificateInfo) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.post(certificateInfo, SSLcertificateResponse.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ApplianceCertificateServiceAdapterServiceAdapter#uploadCertificate
     */
    @Override
    public void uploadCertificate(SSLCertUploadRequest uploadRequest) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.put(uploadRequest);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ApplianceCertificateServiceAdapterServiceAdapter#getCertificateInfo
     */
    @Override
    public SSLCertificateInfo getCertificateInfo() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.get(SSLCertificateInfo.class);
    }

}
