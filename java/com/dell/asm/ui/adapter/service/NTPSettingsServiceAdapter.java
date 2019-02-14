package com.dell.asm.ui.adapter.service;

import com.dell.asm.alcm.client.model.NTPSetting;

/**
 * The Interface NTPSettingsServiceAdapter.
 */
public interface NTPSettingsServiceAdapter {

    /**
     * Gets the nTP settings.
     *
     * @return the nTP settings
     */
    NTPSetting getNTPSettings();

    /**
     * Sets the nTP settings.
     *
     * @param setting
     *            the new nTP settings
     */
    void setNTPSettings(NTPSetting setting);

    /**
     * Delete ntp settings.
     *
     */
    void deleteNTPSettings();

}
