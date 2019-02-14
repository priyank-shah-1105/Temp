package com.dell.asm.ui.adapter.service;

import com.dell.asm.asmcore.asmmanager.client.puppetdevice.PuppetDevice;

/**
 * Interface for servers.
 * @see com.dell.pg.asm.server.client.device.IServerDeviceService
 */
public interface PuppetDeviceServiceAdapter {

    PuppetDevice getPuppetDevice(String id);


}
