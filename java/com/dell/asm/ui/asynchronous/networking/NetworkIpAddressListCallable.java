/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.asynchronous.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.NetworkServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.DeviceController;
import com.dell.asm.ui.model.network.UIStaticIPAddressDetails;
import com.dell.pg.asm.identitypool.api.common.model.NetworkType;
import com.dell.pg.asm.identitypool.api.network.model.IpAddress;
import com.dell.pg.asm.identitypool.api.network.model.UsageIdList;

public class NetworkIpAddressListCallable implements Callable {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(NetworkIpAddressListCallable.class);
    public static final String DEVICE_REF_SEARCH = "refId";
    public static final String DEVICE_SERVICE_TAG_SEARCH = "serviceTag";

    private final String id;
    private final String networkType;
    private final String searchType;
    private final DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private final NetworkServiceAdapter networkServiceAdapter;
    private final SecurityContext securityContext;
    private final UsageIdList usageIdList;
    private final Map<String, ManagedDevice> deviceMap = new HashMap<>();

    public NetworkIpAddressListCallable(String id,
                                        String networkType,
                                        String searchType,
                                        UsageIdList usageIdList,
                                        DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                                        NetworkServiceAdapter networkServiceAdapter,
                                        SecurityContext securityContext) {
        this.id = id;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.networkServiceAdapter = networkServiceAdapter;
        this.securityContext = securityContext;
        this.usageIdList = usageIdList;
        this.networkType = networkType;
        this.searchType = searchType;
    }

    @Override
    public Map<Long, UIStaticIPAddressDetails> call() {
        SecurityContextHolder.setContext(securityContext);

        boolean serachByRefId = DEVICE_REF_SEARCH.equals(searchType);
        if (usageIdList != null && !usageIdList.getUsageIds().isEmpty()) {
            List<String> filterIds = new ArrayList<>();
            String filter = "eq," + searchType + "," + String.join(",", usageIdList.getUsageIds());
            filterIds.add(filter);
            ResourceList<ManagedDevice> deviceResourceList = deviceInventoryServiceAdapter.getAllDeviceInventory(null,
                                                                                                                 filterIds,
                                                                                                                 null,
                                                                                                                 null);
            if (deviceResourceList != null &&
                    deviceResourceList.getList() != null &&
                    deviceResourceList.getList().length > 0) {
                for (ManagedDevice device : deviceResourceList.getList()) {
                    if (serachByRefId) {
                        deviceMap.put(device.getRefId(), device);
                    } else {
                        deviceMap.put(device.getServiceTag(), device);
                    }
                }
            }
        }

        Map<Long, UIStaticIPAddressDetails> staticIpDetails = new TreeMap<>();
        try {
            List<IpAddress> ipAddresses = networkServiceAdapter.getAllIpAddressesForNetwork(id);
            for (IpAddress address : ipAddresses) {
                String state = UIStaticIPAddressDetails.AVAILABLE;
                if (!address.getState().equalsIgnoreCase("AVAILABLE")) {
                    state = UIStaticIPAddressDetails.INUSE;
                }
                ManagedDevice device = deviceMap.get(address.getUsageId());
                String deviceName = null;
                String deviceType = null;
                String deviceId = address.getUsageId();
                if (device != null) {
                    deviceType = DeviceController.getDeviceType(device.getDeviceType());
                    deviceName = device.getServiceTag();
                    deviceId = device.getRefId();
                }
                String role = address.getRole();
                if (NetworkType.SCALEIO_DATA.name().equals(networkType) &&
                        StringUtils.isEmpty(address.getRole())) {
                    role = com.dell.asm.asmcore.asmmanager.client.servicetemplate.IpRange.Role.sdsorsdc.name();
                }
                if (serachByRefId || device != null) {
                    staticIpDetails.put(address.getIPAsLong(),
                                        new UIStaticIPAddressDetails(address.getIPAddress(),
                                                                     state,
                                                                     address.getIpRangeId(),
                                                                     deviceId,
                                                                     deviceName,
                                                                     deviceType,
                                                                     role));
                }
            }
        } catch (Exception e) {
            log.error("Could not retrieve list of deployments for network details", e);
        }
        return staticIpDetails;
    }
}
