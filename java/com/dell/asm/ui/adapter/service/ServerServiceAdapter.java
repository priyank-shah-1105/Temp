package com.dell.asm.ui.adapter.service;

import java.util.List;

import com.dell.pg.asm.server.client.device.Server;

/**
 * Interface for servers.
 * @see com.dell.pg.asm.server.client.device.IServerDeviceService
 */
public interface ServerServiceAdapter {

    /**
     * Get servers list.
     * @param sort
     * @param filter
     * @param offset
     * @param limit
     * @return
     */
    Server[] getServers(String sort, List<String> filter, Integer offset, Integer limit);

    /**
     * Get server by Id.
     * @param id
     * @return
     */
    Server getServer(java.lang.String id);

    /**
     * Delete server by Id.
     * @param id
     */
    void deleteServer(java.lang.String id);

}
