package com.dell.asm.ui.adapter.service;

import java.util.List;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveryRequest;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveryResult;

public interface DiscoverDevicesServiceAdapter {

    public DiscoveryRequest deviceIPRangeDiscoveryRequest(DiscoveryRequest discoveryRequestList);

    public DiscoveryRequest listDevicesForIPRangeDiscoveryRequest(DiscoveryRequest discoveryRequestList);

    public DiscoveryRequest getDiscoveryResult(
            String discoveryId,
            String sort,
            List<String> filter,
            Integer offset,
            Integer limit);

    public Response deleteDiscoveryResult(String discoveryId);

    public DiscoveryRequest[] getDiscoveryRequests(String sort,
                                                   List<String> filter,
                                                   Integer offset,
                                                   Integer limit);

    public DiscoveryResult getDiscoveryResult(String refId);

}
