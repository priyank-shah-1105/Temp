package com.dell.asm.ui.adapter.service;

import java.io.InputStream;

import com.dell.asm.alcm.client.model.TroubleshootingBundleResponse;

/**
 * The Interface ApplianceTimeZoneServiceAdapter.
 */
public interface ApplianceTroubleshootingBundleServiceAdapter {

    /**
     * Generates bundke and returns the URL to download it.
     * @return
     */
    TroubleshootingBundleResponse generateBundle();

    /**
     * Returns the bundle zip file
     * @return
     */
    InputStream downloadBundle();

}
