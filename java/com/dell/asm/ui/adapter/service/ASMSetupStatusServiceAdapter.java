package com.dell.asm.ui.adapter.service;

import com.dell.asm.alcm.client.model.WizardStatus;


/**
 * The Interface ASMSetupStatusServiceAdapter.
 */
public interface ASMSetupStatusServiceAdapter {

    /**
     * Sets the asm setup status.
     *
     * @param status
     *            the status
     * @return the aSM setup status
     */
    WizardStatus setASMSetupStatus(WizardStatus status);

    /**
     * Gets the aSM setup status.
     *
     * @return the aSM setup status
     */
    WizardStatus getASMSetupStatus();

}
