package com.dell.asm.ui.adapter.service;

import com.dell.asm.alcm.client.model.DHCPSetting;

/**
 * The Interface DHCPSettingsServiceAdapter.
 */
public interface DHCPSettingsServiceAdapter {

    /**
     * Gets the DHCP settings.
     *
     * @return the DHCP settings
     */
    DHCPSetting getDHCPSettings();

    /**
     * Sets the DHCP settings.
     *
     * @param setting
     *            the new DHCP settings
     */
    void setDHCPSettings(DHCPSetting setting);

    /**
     * Delete dhcp settings.
     *
     */
    void deleteDHCPSettings();

}
