package com.dell.asm.ui.adapter.service;

import java.util.List;

import com.dell.pg.asm.identitypool.api.ioidentity.model.VirtualIdentity;
import com.dell.pg.asm.identitypool.api.iopool.model.Pool;

/**
 * Interface for pools
 */
public interface PoolServiceAdapter {

    /**
     * Get Pools.
     *
     * @return Pools.
     */
    ResourceList<Pool> getPools(String sort, List<String> filter, Integer offset, Integer limit);

    /**
     * Find a pool.
     *
     * @param poolId
     *            the id of the pool.
     * @return the pool.
     */
    Pool getPool(String poolId);

    /**
     * Update a pool.
     *
     * @param poolId
     *            the id of the pool to update.
     * @param pool
     *            the pool object that contains the update fields.
     */
    void updatePool(String poolId, Pool pool);

    /**
     * Delete a pool.
     *
     * @param id
     *            the id of the pool.
     */
    void deletePool(String id);

    /**
     * Add a pool.
     *
     * @param pool
     *            the pool object that needs to be added.
     */
    Pool addPool(Pool pool);

    /**
     * Find all pool identities.
     *
     * @param poolId
     *            the id of the pool.
     * @param idType
     *            identity type.
     *
     * @return the pool.
     */
    VirtualIdentity[] getVirtualIdentities(String poolId, String idType);

    /**
     * Generate Virtual Identities for different pool type for a given count
     *
     * @param poolId
     * @param type
     * @param count
     */
    void generateVirtualIdentity(String poolId, String type, Integer count);

}
