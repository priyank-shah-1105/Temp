/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.asynchronous.deployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.deployment.DeploymentDevice;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponent;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponentType;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSetting;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSettingIDs;
import com.dell.asm.asmcore.asmmanager.client.util.ServiceTemplateClientUtil;
import com.dell.asm.common.utilities.Inet4ConverterValidator;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.asynchronous.deployment.model.DeviceInfo;
import com.dell.asm.ui.controller.DeviceController;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.asm.identitypool.api.network.model.UsageIdList;

/**
 * Callable class that returns a map of in use ipaddreses as longs and the deviceinfo object associated.
 */
public class DeploymentDeviceListCallable implements Callable {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(DeploymentDeviceListCallable.class);

    private final String id;
    private DeploymentServiceAdapter deploymentServiceAdapter;
    private UsageIdList usageIdList;
    private final SecurityContext securityContext;

    public DeploymentDeviceListCallable(String id,
                                        UsageIdList usageIdList,
                                        DeploymentServiceAdapter deploymentServiceAdapter,
                                        SecurityContext securityContext) {
        this.id = id;
        this.deploymentServiceAdapter = deploymentServiceAdapter;
        this.usageIdList = usageIdList;
        this.securityContext = securityContext;
    }

    @Override
    public Map<Long, DeviceInfo> call() {
        SecurityContextHolder.setContext(securityContext);

        List<String> filterIds = new ArrayList<>();
        String filter = "eq,id," + String.join(",", usageIdList.getUsageIds());
        filterIds.add(filter);
        //Get the deployment details of all ids returned
        ResourceList<Deployment> deploymentResourceList = deploymentServiceAdapter.getDeployments(null,
                                                                                                  filterIds,
                                                                                                  null,
                                                                                                  MappingUtils.MAX_RECORDS,
                                                                                                  Boolean.TRUE);

        Map<Long, DeviceInfo> ipDeviceMap = new HashMap<>();
        try {
            if (deploymentResourceList != null) {
                for (Deployment deployment : deploymentResourceList.getList()) {
                    // loop through all devices on deployment and arange for easy access
                    Map<String, DeploymentDevice> deviceMap = new HashMap<>();
                    for (DeploymentDevice device : deployment.getDeploymentDevice()) {
                        deviceMap.put(device.getRefId(), device);
                    }
                    //loop through components searching for servers and vms
                    for (ServiceTemplateComponent component : deployment.getServiceTemplate().getComponents()) {
                        if (ServiceTemplateComponentType.SERVER.equals(component.getType()) ||
                                ServiceTemplateComponentType.VIRTUALMACHINE.equals(component.getType()) ||
                                ServiceTemplateComponentType.SCALEIO.equals(component.getType())) {
                            //Search component for static networks and return any ip addresses assigned
                            Set<String> ipAddresses = getComponentIpAddresses(id, component);
                            // for each ip address assigned build the deviceinfo object referencing deployment and device if exists
                            for (String ipAddress : ipAddresses) {
                                DeviceInfo deviceInfo = new DeviceInfo(deployment.getId(),
                                                                       deployment.getDeploymentName(),
                                                                       null,
                                                                       null,
                                                                       null);
                                ipDeviceMap.put(
                                        Inet4ConverterValidator.convertIpStringToLong(ipAddress),
                                        deviceInfo);
                                //if component references a device
                                if (component.getAsmGUID() != null) {
                                    DeploymentDevice device = deviceMap.get(component.getAsmGUID());
                                    if (device != null) {
                                        deviceInfo.setDeviceId(device.getRefId());
                                        deviceInfo.setDeviceServiceTag(device.getServiceTag());
                                        deviceInfo.setDeviceType(DeviceController.getDeviceType(device.getDeviceType()));
                                    }
                                } else if (ServiceTemplateComponentType.VIRTUALMACHINE.equals(component.getType())) {
                                    ServiceTemplateSetting osHostName = component.getParameter(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_RESOURCE,
                                                                                               ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_OS_HOSTNAME_ID);
                                    if (osHostName != null && StringUtils.isNotBlank(osHostName.getValue())) {
                                        deviceInfo.setDeviceServiceTag(osHostName.getValue());
                                        deviceInfo.setDeviceType(DeviceController.getDeviceType(DeviceType.vm));
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Could not retrieve list of deployments for network details", e);
        }
        return ipDeviceMap;
    }

    private Set<String> getComponentIpAddresses(String networkId,
                                                ServiceTemplateComponent component) {
        Set<String> ipAddresses = new HashSet<>();
        if (ServiceTemplateComponentType.SERVER.equals(component.getType()) ||
                ServiceTemplateComponentType.VIRTUALMACHINE.equals(component.getType()) ||
                ServiceTemplateComponentType.SCALEIO.equals(component.getType())) {
            List<com.dell.asm.asmcore.asmmanager.client.servicetemplate.Network> networks =
                    ServiceTemplateClientUtil.findStaticNetworks(component);
            if (networks != null && !networks.isEmpty()) {
                for (com.dell.asm.asmcore.asmmanager.client.servicetemplate.Network current : networks) {
                    if (current != null && current.isStatic() && current.getId().equals(
                            networkId)) {
                        if (StringUtils.isNotBlank(
                                current.getStaticNetworkConfiguration().getIpAddress())) {
                            ipAddresses.add(current.getStaticNetworkConfiguration().getIpAddress());
                        }
                    }
                }
            }
        }
        return ipAddresses;
    }

}
