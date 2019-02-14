package com.dell.asm.ui.adapter.service;

import com.dell.asm.alcm.client.model.ProxySetting;
import com.dell.asm.alcm.client.model.TestProxyResponse;

/**
 * The Interface ProxySettingsServiceAdapter.
 */
public interface ProxySettingsServiceAdapter {

    /**
     * Gets the proxy settings.
     *
     * @return the proxy settings
     */
    ProxySetting getProxySettings();

    /**
     * Sets the proxy settings.
     *
     * @param setting
     *            the new proxy settings
     */
    void setProxySettings(ProxySetting setting);

    /**
     * Test proxy settings.
     *
     * @param setting
     *            the setting
     * @return the test proxy response
     */
    TestProxyResponse testProxySettings(ProxySetting setting);
}
