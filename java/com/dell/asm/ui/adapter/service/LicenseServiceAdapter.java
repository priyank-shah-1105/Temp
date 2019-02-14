package com.dell.asm.ui.adapter.service;

import com.dell.asm.alcm.client.model.LicenseDetails;
import com.dell.asm.alcm.client.model.UploadLicenseRequest;

/**
 * The Interface LicenseServiceAdapter.
 */
public interface LicenseServiceAdapter {

    /**
     * Gets the license settings.
     *
     * @return the license settings
     */
    LicenseDetails getLicenseDetails();

    /**
     * Sets the license settings.
     *
     * @param uploadLicenseRequest
     *            the new license settings
     */
    LicenseDetails uploadLicense(UploadLicenseRequest uploadLicenseRequest, Boolean storeLicense,
                                 Boolean forceStoreWithWarnings);
}
