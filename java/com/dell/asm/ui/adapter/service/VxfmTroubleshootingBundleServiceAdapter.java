package com.dell.asm.ui.adapter.service;

import com.dell.asm.asmcore.asmmanager.client.troubleshootingbundle.TroubleshootingBundleParams;

import javax.ws.rs.core.Response;

/**
 * This class provides APIs to make a rest call to the TroubleshootingBundle backend scripts
 */
public interface VxfmTroubleshootingBundleServiceAdapter {
    /**
     * Method to export TroubleshootingBundle
     *
     * @param troubleshootingBundleParams an instance of type {@link TroubleshootingBundleParams}
     * @return An object of type {@link String}
     */
	String exportTroubleShootingBundle(TroubleshootingBundleParams troubleshootingBundleParams);

    /**
     * Method to test if TroubleshootingBundle can be exported
     *
     * @param troubleshootingBundleParams an instance of type {@link TroubleshootingBundleParams}
     * @return An object of type {@link String}
     */
    Response testTroubleshootingBundle(TroubleshootingBundleParams troubleshootingBundleParams);
}

