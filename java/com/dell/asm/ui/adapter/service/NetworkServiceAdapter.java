package com.dell.asm.ui.adapter.service;

import java.io.File;
import java.util.List;

import com.dell.pg.asm.identitypool.api.network.model.IpAddress;
import com.dell.pg.asm.identitypool.api.network.model.Network;
import com.dell.pg.asm.identitypool.api.network.model.UsageIdList;

/**
 * Interface for networks
 */
public interface NetworkServiceAdapter {

    ResourceList<Network> getNetworks(String sort, List<String> filter, Integer offset,
                                      Integer limit);

    List<Network> getNetworksByType(String networkType);

    Network getNetwork(String id);

    void updateNetwork(String id, Network network);

    void deleteNetwork(String id);

    Network addNetwork(Network network);

    void exportAllNetworks(final File downloadFile);

    List<IpAddress> getAllIpAddressesForNetwork(String networkId);

    UsageIdList getAllDeploymentIdsForNetwork(String networkId);
}
