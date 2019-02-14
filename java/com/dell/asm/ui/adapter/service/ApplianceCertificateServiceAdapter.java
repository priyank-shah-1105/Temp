package com.dell.asm.ui.adapter.service;

import com.dell.asm.alcm.client.model.CertificateInfo;
import com.dell.asm.alcm.client.model.SSLCertUploadRequest;
import com.dell.asm.alcm.client.model.SSLCertificateInfo;
import com.dell.asm.alcm.client.model.SSLcertificateResponse;

/**
 * The Interface ApplianceTimeZoneServiceAdapter.
 */
public interface ApplianceCertificateServiceAdapter {

    /**
     * Generates CSR for generating the Certificate request.
     * @param certificateInfo
     * @return
     */
    SSLcertificateResponse createCSR(CertificateInfo certificateInfo);

    /**
     * upload Certificate method for validating (with private key) and uploading the certificate.
     * @param uploadRequest
     */
    void uploadCertificate(SSLCertUploadRequest uploadRequest);

    /**
     * Get SSL Cert info.
     * @return
     */
    SSLCertificateInfo getCertificateInfo();

}
