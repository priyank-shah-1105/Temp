/**************************************************************************
 * Copyright (c) 2013-2017 Dell Inc. All rights reserved.                 *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.Interface;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.Partition;
import com.dell.asm.asmcore.asmmanager.client.puppetdevice.PuppetDevice;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateCategory;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponent;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponentType;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateOption;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSetting;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSettingIDs;
import com.dell.asm.asmcore.asmmanager.client.util.ServiceTemplateClientUtil;
import com.dell.asm.asmcore.asmmanager.client.util.VcenterInventoryUtils;
import com.dell.asm.asmcore.asmmanager.client.vsphere.ClusterDTO;
import com.dell.asm.asmcore.asmmanager.client.vsphere.ManagedObjectDTO;
import com.dell.asm.asmcore.asmmanager.client.vsphere.NetworkDTO;
import com.dell.asm.asmcore.asmmanager.client.vsphere.PortGroupDTO;
import com.dell.asm.i18n2.exception.AsmRuntimeException;
import com.dell.asm.ui.ASMUIMessages;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.NetworkServiceAdapter;
import com.dell.asm.ui.adapter.service.PuppetDeviceServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.TemplateServiceAdapter;
import com.dell.asm.ui.asynchronous.deployment.DeploymentDeviceListCallable;
import com.dell.asm.ui.asynchronous.deployment.model.DeviceInfo;
import com.dell.asm.ui.asynchronous.networking.NetworkIpAddressListCallable;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.network.GetNetworkTypesRequest;
import com.dell.asm.ui.model.network.JobGetServicePortGroupRequest;
import com.dell.asm.ui.model.network.JobSaveNetworkRequest;
import com.dell.asm.ui.model.network.JobServiceIdRequest;
import com.dell.asm.ui.model.network.UIIPAddressRange;
import com.dell.asm.ui.model.network.UINetwork;
import com.dell.asm.ui.model.network.UINetworkTemplateUsage;
import com.dell.asm.ui.model.network.UINetworkType;
import com.dell.asm.ui.model.network.UIPortGroup;
import com.dell.asm.ui.model.network.UIStaticIPAddressDetails;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.asm.identitypool.api.common.model.NetworkType;
import com.dell.pg.asm.identitypool.api.network.model.IpRange;
import com.dell.pg.asm.identitypool.api.network.model.Network;
import com.dell.pg.asm.identitypool.api.network.model.StaticNetworkConfiguration;
import com.dell.pg.asm.identitypool.api.network.model.UsageIdList;
import com.dell.pg.orion.common.utilities.MarshalUtil;
import com.google.common.base.Joiner;

@RestController
@RequestMapping(value = "/networks/")
public class NetworksController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(NetworksController.class);

    private NetworkServiceAdapter networkServiceAdapter;
    private TemplateServiceAdapter templateServiceAdapter;
    private DeploymentServiceAdapter deploymentServiceAdapter;
    private PuppetDeviceServiceAdapter puppetDeviceServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;

    @Autowired
    public NetworksController(NetworkServiceAdapter networkServiceAdapter,
                              TemplateServiceAdapter templateServiceAdapter,
                              DeploymentServiceAdapter deploymentServiceAdapter,
                              PuppetDeviceServiceAdapter puppetDeviceServiceAdapter,
                              DeviceInventoryServiceAdapter deviceInventoryServiceAdapter) {
        this.networkServiceAdapter = networkServiceAdapter;
        this.templateServiceAdapter = templateServiceAdapter;
        this.deploymentServiceAdapter = deploymentServiceAdapter;
        this.puppetDeviceServiceAdapter = puppetDeviceServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
    }

    /**
     * UINetwork to Network.
     * @param network
     * @return
     */
    public static Network createNetwork(UINetwork network,
                                        ApplicationContext context) throws ControllerException {
        if (network == null) return null;
        Network configuration = new Network();

        if (network.id != null) {
            configuration.setId(network.id);
        }
        configuration.setName(network.name);
        configuration.setDescription(network.description);
        configuration.setType(NetworkType.fromValue(network.typeid));

        try {
            if (NumberUtils.isNumber(network.vlanid)) {
                configuration.setVlanId(Integer.valueOf(network.vlanid));
            }
        } catch (NumberFormatException ne) {
            log.debug("attempt to set VLAN to non-integer, skipped.");
            throw new ControllerException(
                    context.getMessage("validationError.invalidVLANNumber", null,
                                       LocaleContextHolder.getLocale()),
                    context.getMessage("validationError.invalidVLANNumberDetails", null,
                                       LocaleContextHolder.getLocale()));
        }

        if (!network.configurestatic)
            configuration.setStatic(false);
        else {
            if (NetworkType.PXE.equals(configuration.getType())) {
                log.debug("OS Installation Networks can not be Static!");
                throw new ControllerException(
                        context.getMessage("validationError.dhcpOnlyOsInstallation", null,
                                           LocaleContextHolder.getLocale()),
                        context.getMessage("validationError.dhcpOnlyOsInstallationDetails", null,
                                           LocaleContextHolder.getLocale()));

            }
            configuration.setStatic(true);
            StaticNetworkConfiguration staticConfig = new StaticNetworkConfiguration();
            configuration.setStaticNetworkConfiguration(staticConfig);
            if (StringUtils.isNotBlank(network.dnssuffix)) {
                staticConfig.setDnsSuffix(network.dnssuffix);
            }
            if (StringUtils.isNotBlank(network.gateway)) {
                staticConfig.setGateway(network.gateway);
            }
            if (StringUtils.isNotBlank(network.primarydns)) {
                staticConfig.setPrimaryDns(network.primarydns);
            }
            if (StringUtils.isNotBlank(network.secondarydns)) {
                staticConfig.setSecondaryDns(network.secondarydns);
            }
            if (StringUtils.isNotBlank(network.subnet)) {
                staticConfig.setSubnet(network.subnet);
            }
            if ((network.ipaddressranges != null) && !network.ipaddressranges.isEmpty()) {
                for (UIIPAddressRange range : network.ipaddressranges) {
                    IpRange iprange = new IpRange();
                    iprange.setId(range.id);
                    iprange.setStartingIp(range.startingIpAddress);
                    if (StringUtils.isBlank(range.endingIpAddress)) {
                        range.endingIpAddress = range.startingIpAddress;
                    }
                    iprange.setEndingIp(range.endingIpAddress);
                    if (StringUtils.isNotEmpty(range.role) &&
                            !com.dell.asm.asmcore.asmmanager.client.servicetemplate.IpRange.Role.sdsorsdc.name().equals(range.role)) {
                        iprange.setRole(range.role);
                    }
                    staticConfig.getIpRange().add(iprange);
                }
            }
        }
        return configuration;
    }

    /**
     * Checks if server uses network configuration
     * @param svcTemplate template to lookup
     * @param component server component
     * @param id Network ID
     * @return
     */
    public static boolean checkServerNetworkUsage(ServiceTemplate svcTemplate,
                                                  ServiceTemplateComponent component, String id) {
        ServiceTemplateSetting networking = ServiceTemplate.getTemplateSetting(component,
                                                                           ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_NETWORK_CONFIG_ID);
        if (networking == null) return false;

        String configString = "{ \"networkConfiguration\" : " + networking.getValue() + "}";
        com.dell.asm.asmcore.asmmanager.client.networkconfiguration.NetworkConfiguration networkConfig =
                MarshalUtil.fromJSON(
                        com.dell.asm.asmcore.asmmanager.client.networkconfiguration.NetworkConfiguration.class,
                        configString);

        if (networkConfig == null) {
            // xml screwed up?
            log.error("Cannot parse network configuration from string: " + configString);
            return false;
        }

        List<Interface> interfaces = networkConfig.getUsedInterfaces();

        for (Interface interfaceObject : interfaces) {
            List<Partition> partitions = interfaceObject.getPartitions();
            for (Partition partition : partitions) {
                List<String> networkIds = partition.getNetworks();
                if (networkIds != null) {
                    for (String networkId : networkIds) {

                        if (networkId.equals(id)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Gets the network types.
     *
     * @return the network types
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getnetworktypes", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getNetworkTypes(@RequestBody GetNetworkTypesRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UINetworkType> result = new ArrayList<>();

        if (request.requestObj.scaleup) {
            result.add(new UINetworkType(NetworkType.PRIVATE_LAN, false,true));
            result.add(new UINetworkType(NetworkType.PUBLIC_LAN, false,true));
        } else {
            result.add(new UINetworkType(NetworkType.PUBLIC_LAN, false,true));
            result.add(new UINetworkType(NetworkType.HYPERVISOR_MANAGEMENT, false, true));
            result.add(new UINetworkType(NetworkType.HYPERVISOR_MIGRATION, false, true));
            result.add(new UINetworkType(NetworkType.PXE,true, true));
            result.add(new UINetworkType(NetworkType.HARDWARE_MANAGEMENT, false, true));
            result.add(new UINetworkType(NetworkType.SCALEIO_DATA, false, true));
            result.add(new UINetworkType(NetworkType.SCALEIO_DATA_SDC, false, true));
            result.add(new UINetworkType(NetworkType.SCALEIO_DATA_SDS, false, true));
            result.add(new UINetworkType(NetworkType.SCALEIO_MANAGEMENT, false, true));
        }

        jobResponse.responseObj = result;
        return jobResponse;
    }

    /**
     * Gets the networks.
     *
     * @param request
     *            the request
     * @return the time zones
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getnetworks", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getNetworks(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UINetwork> responseObj = new ArrayList<>();
        long totalRecords = 0L;
        try {
            RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                                MappingUtils.COLUMNS_NETWORKS,
                                                                "name");
            ResourceList<Network> networkResourceList = networkServiceAdapter.getNetworks(
                    options.sortedColumns, options.filterList,
                    options.offset < 0 ? null : options.offset,
                    options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);

            if (networkResourceList != null && networkResourceList.getList() != null) {
                Network[] networkList = networkResourceList.getList();
                totalRecords = networkResourceList.getTotalRecords();

                log.debug("getNetworks() - totalrecords" + totalRecords);

                if (networkList.length > 0) {
                    for (Network network : networkList) {
                        UINetwork net = parseNetwork(network, null);
                        responseObj.add(net);
                    }
                } else {
                    if (totalRecords > 0 && request.criteriaObj != null && request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    (int) totalRecords);
                        return getNetworks(newRequest);
                    }
                }
            }

            jobResponse.responseObj = responseObj;
            jobResponse.criteriaObj = request.criteriaObj;
            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) totalRecords;
            }

        } catch (Throwable t) {
            log.error("getNetworks() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;

    }

    /**
     * Gets the hardware management networks.
     *
     * @return the hardware management networks
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "gethardwaremanagementnetworks", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getHardwareManagementNetworks() {
        JobResponse jobResponse = new JobResponse();
        try {
            List<String> filter = new ArrayList<>();
            filter.add("eq,type," + NetworkType.HARDWARE_MANAGEMENT);
            List<UINetwork> list = new ArrayList<>();
            ResourceList<Network> networks = networkServiceAdapter.getNetworks(null, filter, null,
                                                                               null);
            if (networks != null && networks.getList() != null) {
                for (Network net : networks.getList()) {
                    list.add(parseNetwork(net, null));
                }
            }

            jobResponse.responseObj = list;
        } catch (Throwable t) {
            log.error("getHardwareManagementNetworks() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the chassis hardware management networks.
     *
     * @return the chassis hardware management networks
     */
    @RequestMapping(value = "getchassishardwaremanagementnetworks", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getChassisHardwareManagementNetworks() {
        JobResponse jobResponse = new JobResponse();
        try {
            List<String> filter = new ArrayList<>();
            filter.add("eq,type," + NetworkType.HARDWARE_MANAGEMENT);
            List<UINetwork> list = new ArrayList<>();
            ResourceList<Network> networks = networkServiceAdapter.getNetworks(null, filter, null,
                                                                               null);
            if (networks != null && networks.getList() != null) {
                for (Network net : networks.getList()) {
                    list.add(parseNetwork(net, null));
                }
            }
            jobResponse.responseObj = list;
        } catch (Throwable t) {
            log.error("getChassisHardwareManagementNetworks() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the non hardware management networks.
     *
     * @param request
     *            the request
     * @return the non hardware management networks
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getnonhardwaremanagementnetworks", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getNonHardwareManagementNetworks(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            List<String> filter = new ArrayList<>();
            filter.add("eq,type," + NetworkType.OOB_OR_INFRASTRUCTURE_MANAGEMENT + "," +
                               NetworkType.PUBLIC_LAN + "," +
                               NetworkType.HYPERVISOR_MANAGEMENT + "," +
                               NetworkType.HYPERVISOR_MIGRATION + "," +
                               NetworkType.PXE + "," +
                               NetworkType.SCALEIO_DATA + "," +
                               NetworkType.SCALEIO_DATA_SDC + "," +
                               NetworkType.SCALEIO_DATA_SDS + "," +
                               NetworkType.SCALEIO_MANAGEMENT);

            List<UINetwork> list = new ArrayList<>();
            ResourceList<Network> networks = networkServiceAdapter.getNetworks(null, filter, null,
                                                                               null);
            if (networks != null && networks.getList() != null) {
                for (Network net : networks.getList()) {
                    list.add(parseNetwork(net, null));
                }
            }
            jobResponse.responseObj = list;
        } catch (Throwable t) {
            log.error("getNonHardwareManagementNetworks() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the service networks
     *
     * @return the service networks
     * @throws ServletException
     *              the servlet exception
     * @throws IOException
     *              Signals that an I/O excpetion has occured.
     */
    @RequestMapping(value = "getservicenetworkslist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServiceNetworkList(@RequestBody JobServiceIdRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            List<String> filter = new ArrayList<>();
            filter.add("eq,type," + NetworkType.PRIVATE_LAN + "," +
                               NetworkType.PUBLIC_LAN);
            List<UINetwork> list = new ArrayList<>();
            ResourceList<Network> networks = networkServiceAdapter.getNetworks(null, filter, null,
                                                                               null);

            if (networks != null && networks.getList() != null) {
                Map<String, Network> workloadNetworks = new HashMap<>();
                for (Network net : networks.getList()) {
                    workloadNetworks.put(net.getId(), net);
                }

                Deployment deployment = deploymentServiceAdapter.getDeployment(
                        request.requestObj.serviceId);
                boolean isVMOnlyDeployment = isVmOnlyDeploynent(deployment);

                if (isVMOnlyDeployment) {
                    // add network only if present on hosts connected to a cluster
                    List<String> clusterNetworks = new ArrayList<>();
                    for (ServiceTemplateComponent component : deployment.getServiceTemplate().getComponents()) {
                        if (ServiceTemplateComponentType.CLUSTER.equals(
                                component.getType())) {
                            clusterNetworks.addAll(
                                    getNetworksForCluster(component, networks.getList()));
                        }
                    }

                    for (String clusterNetwork : clusterNetworks) {
                        list.add(parseNetwork(workloadNetworks.get(clusterNetwork), null));
                    }

                } else {
                    // unconditional for Server deployment, return all workload networks
                    for (Network net : networks.getList()) {
                        list.add(parseNetwork(net, null));
                    }
                }
            }


            jobResponse.responseObj = list;
        } catch (Throwable t) {
            log.error("getServiceNetworkList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private boolean isVmOnlyDeploynent(Deployment deployment) {
        boolean vmFound = false;
        for (ServiceTemplateComponent component : deployment.getServiceTemplate().getComponents()) {
            if (ServiceTemplateComponentType.SERVER.equals(
                    component.getType())) {
                return false;
            } else if (ServiceTemplateComponentType.VIRTUALMACHINE.equals(
                    component.getType())) {
                vmFound = true;
            }
        }
        return vmFound;
    }

    /**
     * Gets the lan networks.
     *
     * @return the lan networks
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getlannetworks", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getLanNetworks() {
        JobResponse jobResponse = new JobResponse();

        try {
            List<String> filter = new ArrayList<>();
            filter.add("eq,type," +
                               NetworkType.PUBLIC_LAN + "," +
                               NetworkType.PRIVATE_LAN);

            List<UINetwork> list = new ArrayList<>();
            ResourceList<Network> networks = networkServiceAdapter.getNetworks(null, filter, null,
                                                                               null);
            if (networks != null && networks.getList() != null) {
                for (Network net : networks.getList()) {
                    list.add(parseNetwork(net, null));
                }
            }
            jobResponse.responseObj = list;
        } catch (Throwable t) {
            log.error("getLanNetworks() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the SAN iscsi networks.
     *
     * @return the sA ni scsi networks
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getsaniscsinetworks", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getSANiSCSINetworks() {
        JobResponse jobResponse = new JobResponse();
        try {
            List<String> filter = new ArrayList<>();
            filter.add("eq,type," + NetworkType.STORAGE_ISCSI_SAN);

            List<UINetwork> list = new ArrayList<>();
            ResourceList<Network> networks = networkServiceAdapter.getNetworks(null, filter, null,
                                                                               null);
            if (networks != null && networks.getList() != null) {
                for (Network net : networks.getList()) {
                    list.add(parseNetwork(net, null));
                }
            }
            jobResponse.responseObj = list;
        } catch (Throwable t) {
            log.error("getSANiSCSINetworks() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Submit change network.
     *
     * @param request the request
     * @return the job response
     * @throws ServletException the servlet exception
     * @throws IOException      Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "savenetwork", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse saveNetwork(@RequestBody JobSaveNetworkRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            Network configuration;
            if (request.requestObj != null) {
                configuration = createNetwork(request.requestObj, this.getApplicationContext());
                if (StringUtils.isNotBlank(request.requestObj.id)) {
                    networkServiceAdapter.updateNetwork(configuration.getId(), configuration);
                } else {
                    Network n = networkServiceAdapter.addNetwork(configuration);
                    request.requestObj.id = n.getId();
                }
            }
            jobResponse.responseObj = request.requestObj;
        } catch (Throwable t) {
            log.error("saveNetwork() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;

    }

    /**
     * Gets the network by id.
     *
     * @param request the request
     * @return the network by id
     * @throws ServletException the servlet exception
     * @throws IOException      Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getnetworkbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getNetworkByID(@RequestBody JobIDRequest request) {

        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        JobResponse jobResponse = new JobResponse();
        UINetwork responseObj;
        try {
            //Retrieve a list of deployment ids that use this network
            UsageIdList deploymentIds = networkServiceAdapter.getAllDeploymentIdsForNetwork(request.requestObj.id);

            responseObj = parseNetwork(networkServiceAdapter.getNetwork(request.requestObj.id),
                    null);

            // Retrieve the list of ip addresses on this static network asynchronously
            NetworkIpAddressListCallable refIdNetworkIpAddressList = new NetworkIpAddressListCallable(request.requestObj.id,
                                                                                                      responseObj.typeid,
                                                                                                      NetworkIpAddressListCallable.DEVICE_REF_SEARCH,
                                                                                                      deploymentIds,
                                                                                                      deviceInventoryServiceAdapter,
                                                                                                      networkServiceAdapter,
                                                                                                      SecurityContextHolder.getContext());
            Future refIdNetworkIpAddressesFuture = threadPool.submit(refIdNetworkIpAddressList);

            // Retrieve the list of ip addresses on this static network asynchronously
            NetworkIpAddressListCallable serviceTagNetworkIpAddressList = new NetworkIpAddressListCallable(request.requestObj.id,
                                                                                                           responseObj.typeid,
                                                                                                           NetworkIpAddressListCallable.DEVICE_SERVICE_TAG_SEARCH,
                                                                                                           deploymentIds,
                                                                                                           deviceInventoryServiceAdapter,
                                                                                                           networkServiceAdapter,
                                                                                                           SecurityContextHolder.getContext());
            Future serviceTagNetworkIpAddressesFuture = threadPool.submit(serviceTagNetworkIpAddressList);
            //Retrieve the list of in use ip addresses and the deployment and device information associated with them.
            DeploymentDeviceListCallable deviceList = new DeploymentDeviceListCallable(request.requestObj.id,
                                                                                       deploymentIds,
                                                                                       deploymentServiceAdapter,
                                                                                       SecurityContextHolder.getContext());
            Future deviceListFuture = threadPool.submit(deviceList);

            Map<Long, UIStaticIPAddressDetails> staticIPAddressDetailsMap = (Map<Long, UIStaticIPAddressDetails>) refIdNetworkIpAddressesFuture.get();
            //serviceTagNetworkIpAddressesFuture results must be added second always
            staticIPAddressDetailsMap.putAll((Map<Long, UIStaticIPAddressDetails>) serviceTagNetworkIpAddressesFuture.get());
            Map<Long, DeviceInfo> deviceInfoMap = (Map<Long, DeviceInfo>) deviceListFuture.get();

            //loop through in use ip addresses and apply deployment and device information to the static ip address details.
            for (Map.Entry<Long, DeviceInfo> entry : deviceInfoMap.entrySet()) {
                UIStaticIPAddressDetails details = staticIPAddressDetailsMap.get(entry.getKey());
                DeviceInfo deviceInfo = entry.getValue();
                if (details != null && deviceInfo != null) {
                    details.state = UIStaticIPAddressDetails.INUSE;
                    details.deviceId = deviceInfo.getDeviceId();
                    details.deviceName = deviceInfo.getDeviceServiceTag();
                    details.deviceType = deviceInfo.getDeviceType();
                    details.serviceId = deviceInfo.getDeploymentId();
                    details.serviceName = deviceInfo.getDeploymentName();
                }
            }
            if (!staticIPAddressDetailsMap.isEmpty()) {
                responseObj.staticipaddressdetails.addAll(staticIPAddressDetailsMap.values());
            }

            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("getUserById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        } finally {
            threadPool.shutdown();
        }
        return jobResponse;

    }

    /**
     * Delete network.
     *
     * @param request the request
     * @return the job response
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "deletenetwork", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse deleteNetwork(@RequestBody JobIDRequest request) {
        JobResponse jobResponse;
        try {
            jobResponse = validateIDRequest(request);

            if (jobResponse.errorObj != null) {
                return jobResponse;
            }
            try {
                networkServiceAdapter.deleteNetwork(request.requestObj.id);

            } catch (Throwable t) {
                log.error("deleteNetwork() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }

        } catch (Throwable t) {
            log.error("deleteUser() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * getservicenetworkportgrouplist
     *
     * @param request Service ID
     * @return port group list
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getservicenetworkportgrouplist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServiceNetworkPortGroupList(@RequestBody JobGetServicePortGroupRequest request) {

        JobResponse jobResponse = new JobResponse();
        List<UIPortGroup> portGroups = new ArrayList<>();
        jobResponse.responseObj = portGroups;

        try {
            // use default template customized for this service as a source for port groups
            ServiceTemplate defaultTemplate = templateServiceAdapter.getCustomizedComponent(null,
                                                                                            request.requestObj.serviceId,
                                                                                            ServiceTemplateComponentType.CLUSTER.getLabel());

            Deployment deployment = deploymentServiceAdapter.getDeployment(
                    request.requestObj.serviceId);
            Network selectedNetwork = networkServiceAdapter.getNetwork(
                    request.requestObj.networkId);
            int vlanId = 0;
            if (selectedNetwork != null) {
                vlanId = selectedNetwork.getVlanId();
            }

            if (deployment.isVDS()) {

                // the list of VDS Names where we have Workload networks
                List<String> vdsNames = new ArrayList<>();

                for (ServiceTemplateComponent clusterComponent : deployment.getServiceTemplate().getComponents()) {
                    if (clusterComponent.getType() == ServiceTemplateComponentType.CLUSTER) {
                        for (ServiceTemplateCategory category : clusterComponent.getResources()) {
                            if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMP_VDS_ID.equals(
                                    category.getId())) {
                                for (ServiceTemplateSetting param : category.getParameters()) {
                                    // find VDS PG, check is it is Workload
                                    String networkId = ServiceTemplateClientUtil.extractNetworkID(
                                            param.getId());
                                    if (networkId != null) {
                                        Network nw = networkServiceAdapter.getNetwork(networkId);
                                        if (nw.getType() == NetworkType.PUBLIC_LAN ||
                                                nw.getType() == NetworkType.PRIVATE_LAN) {
                                            // find corresponding VDS name
                                            String networksId = ServiceTemplateClientUtil.extractNetworksID(
                                                    param.getId());

                                            ServiceTemplateSetting vdsNameSet = category.getParameter(
                                                    ServiceTemplateClientUtil.createVDSID(
                                                            networksId));

                                            if (vdsNameSet != null && StringUtils.isNotEmpty(
                                                    vdsNameSet.getValue())) {
                                                String value = vdsNameSet.getValue();
                                                if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX.equals(
                                                        vdsNameSet.getValue())) {
                                                    ServiceTemplateSetting vdsNameSetNew = ServiceTemplate.getTemplateSetting(clusterComponent,
                                                                                                                              ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX + vdsNameSet.getId());
                                                    if (vdsNameSetNew != null) {
                                                        value = vdsNameSetNew.getValue();
                                                    }
                                                }
                                                if (!vdsNames.contains(value)) {
                                                    vdsNames.add(value);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // for options use refined default template
                for (ServiceTemplateComponent clusterComponent : defaultTemplate.getComponents()) {
                    if (clusterComponent.getId().equals(
                            ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMPONENT_ID)) {
                        ServiceTemplateCategory category =
                                clusterComponent.getTemplateResource(
                                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMP_VDS_ID);

                        if (category != null) {
                            for (ServiceTemplateSetting param : category.getParameters()) {
                                // we need any port group, they all have the same option set
                                if (param.getId().startsWith(
                                        ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CLUSTER_VDS_PG_ID + "::")) {
                                    for (ServiceTemplateOption option : param.getOptions()) {
                                        if (StringUtils.isEmpty(option.getValue()) ||
                                                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CREATE_NEW_PREFIX.equals(
                                                        option.getValue())) {
                                            continue;
                                        }

                                        // skip unrelated PGs
                                        if (!option.getAttributes().containsKey("vlan_id") || (
                                                option.getDependencyValue() != null &&
                                                        !vdsNames.contains(
                                                                option.getDependencyValue()))) {
                                            continue;
                                        }

                                        UIPortGroup newPortGroup = new UIPortGroup(option);
                                        if (!portGroups.contains(newPortGroup)) {
                                            // only pick port groups with the same VLAN ID
                                            if (vlanId > 0 && String.valueOf(vlanId).equals(
                                                    option.getAttributes().get("vlan_id"))) {
                                                portGroups.add(newPortGroup);
                                            }
                                        }
                                    }
                                    // all optiosn are the same
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }


            }

        } catch (Throwable t) {
            log.error("getServiceNetworkPortGroupList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;

    }

    private Set<String> getServiceTemplateNamesUsedByNetwork(String networkId) {
        Set<String> templateNames = new HashSet<>();
        // Get the Templates that depend on this Network
        ResourceList<ServiceTemplate> tList = templateServiceAdapter.getAllTemplates(null, null,
                                                                                     null,
                                                                                     MappingUtils.MAX_RECORDS,
                                                                                     Boolean.TRUE,
                                                                                     Boolean.TRUE);
        if (tList != null && tList.getTotalRecords() > 0) {
            for (ServiceTemplate serviceTemplate : tList.getList()) {
                Set<String> networkIds = ServiceTemplateClientUtil.getNetworkIds(serviceTemplate);
                if (networkIds.contains(networkId)) {
                    templateNames.add(serviceTemplate.getTemplateName());
                }
            }
        }
        return templateNames;
    }

    /**
     * Network to UINetwork.
     *
     * @param config network configuration
     * @return UI Network object
     */
    public UINetwork parseNetwork(Network config,
                                  ResourceList<ServiceTemplate> templateList) {
        UINetwork network = null;
        if (config != null) {
            network = new UINetwork();
            if (config.getId() != null) {
                network.id = config.getId();
            } else {
                network.id = ""; // eliminate possible NPE
            }
            if (config.getName() != null) {
                network.name = config.getName();
            }
            if (config.getDescription() != null) {
                network.description = config.getDescription();
            }
            if (config.getType() != null) {
                network.typeid = config.getType().toString();
            }
            if (config.getVlanId() != null) {
                network.vlanid = config.getVlanId().toString();
            }


            if (config.getCreatedBy() != null) {
                network.createdBy = config.getCreatedBy();
            }
            if (config.getUpdatedBy() != null) {
                network.updatedBy = config.getUpdatedBy();
            }
            if (config.getCreatedDate() != null) {
                config.getCreatedDate();
                // TODO - right conversion of java.util.date from service
                network.createdDate = com.dell.asm.ui.util.MappingUtils.getTime(
                        config.getCreatedDate());
            }
            if (config.getUpdatedDate() != null) {
                // TODO - right conversion of java.util.date from service
                network.updatedDate = com.dell.asm.ui.util.MappingUtils.getTime(
                        config.getUpdatedDate());
            }

            if (!config.isStatic()) {
                network.staticordhcp = "DHCP";
                network.configurestatic = false;
            } else {
                network.staticordhcp = "Static";
                network.configurestatic = true;
                if (config.getStaticNetworkConfiguration() != null) {
                    if (config.getStaticNetworkConfiguration().getDnsSuffix() != null) {
                        network.dnssuffix = config.getStaticNetworkConfiguration().getDnsSuffix();
                    }
                    if (config.getStaticNetworkConfiguration().getGateway() != null) {
                        network.gateway = config.getStaticNetworkConfiguration().getGateway();
                    }
                    if (config.getStaticNetworkConfiguration().getPrimaryDns() != null) {
                        network.primarydns = config.getStaticNetworkConfiguration().getPrimaryDns();
                    }
                    if (config.getStaticNetworkConfiguration().getSecondaryDns() != null) {
                        network.secondarydns = config.getStaticNetworkConfiguration().getSecondaryDns();
                    }
                    if (config.getStaticNetworkConfiguration().getSubnet() != null) {
                        network.subnet = config.getStaticNetworkConfiguration().getSubnet();
                    }
                    if ((config.getStaticNetworkConfiguration().getIpRange() != null) && !config.getStaticNetworkConfiguration().getIpRange().isEmpty()) {
                        for (IpRange range : config.getStaticNetworkConfiguration().getIpRange()) {
                            Integer total = null;
                            Integer inUse = null;
                            if (range.getAvailableIps() >= 0) {
                                total = range.getTotalIps();
                                inUse = total - range.getAvailableIps();
                            }
                            String role = range.getRole();
                            if (NetworkType.SCALEIO_DATA.equals(config.getType()) &&
                                    StringUtils.isEmpty(range.getRole())) {
                                role = com.dell.asm.asmcore.asmmanager.client.servicetemplate.IpRange.Role.sdsorsdc.name();
                            }
                            network.ipaddressranges.add(new UIIPAddressRange(range.getId(), range.getStartingIp(),
                                    range.getEndingIp(), total, inUse, role));

                        }
                    }
                }
            }

            // template usage
            if (templateList != null && templateList.getList() != null) {
                for (ServiceTemplate tmpl : templateList.getList()) {
                    for (ServiceTemplateComponent component : tmpl.getComponents()) {
                        if (component.getType() == ServiceTemplateComponentType.SERVER) {
                            if (checkServerNetworkUsage(tmpl, component, network.id)) {
                                UINetworkTemplateUsage tUsage = new UINetworkTemplateUsage();
                                tUsage.templateName = tmpl.getTemplateName();
                                tUsage.templateType = "";
                                // Need Template Id here
                                tUsage.templateId = tmpl.getId();
                                network.networkTemplateUsages.add(tUsage);
                            }
                        }
                    }
                }
            }

        }

        return network;
    }

    /**
     * Returns list of ASM workload networks found on vCenter cluster inventory.
     * @param clusterComp cluster component
     * @param worloadNetworks List of all workload networks
     * @return List of network IDs
     */
    private List<String> getNetworksForCluster(ServiceTemplateComponent clusterComp,
                                               Network[] worloadNetworks) {
        List<String> ret = new ArrayList<>();

        if (clusterComp != null) {
            ServiceTemplateSetting asmguidSetting = clusterComp.getParameter(
                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMP_ID,
                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ASM_GUID);

            ServiceTemplateSetting clusterSetting = clusterComp.getParameter(
                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_ESX_CLUSTER_COMP_ID,
                    ServiceTemplateSettingIDs.SERVICE_TEMPLATE_CLUSTER_CLUSTER_ID);

            if (asmguidSetting != null && StringUtils.isNotEmpty(asmguidSetting.getValue())
                    && clusterSetting != null && StringUtils.isNotEmpty(
                    clusterSetting.getValue())) {
                // refId of vCenter
                try {
                    PuppetDevice vCenterDevice = puppetDeviceServiceAdapter.getPuppetDevice(
                            asmguidSetting.getValue());
                    if (vCenterDevice != null) {
                        // each VLAN ID can have multiple networks
                        Map<Integer, List<String>> vlanMap = new HashMap<>();
                        if (worloadNetworks != null) {
                            for (com.dell.pg.asm.identitypool.api.network.model.Network net : worloadNetworks) {
                                List<String> netids = vlanMap.computeIfAbsent(net.getVlanId(),
                                                                              k -> new ArrayList<>());
                                netids.add(net.getId());
                            }
                        }

                        ManagedObjectDTO vCenter = VcenterInventoryUtils.convertPuppetDeviceDetailsToDto(
                                vCenterDevice.getData());

                        // we need the same cluster
                        ClusterDTO cluster = vCenter.getCluster(clusterSetting.getValue());
                        if (cluster != null) {
                            for (PortGroupDTO portGroup : cluster.getPortGroups()) {
                                Object vlanObj = portGroup.getAttribute("vlan_id");
                                processVlanAttribute(vlanObj, vlanMap, ret);
                            }
                            for (NetworkDTO net : cluster.getNetworks()) {
                                Object vlanObj = net.getAttribute("vlan_id");
                                processVlanAttribute(vlanObj, vlanMap, ret);
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("Cannot parse vCenter DTO", e);
                    return ret;
                }
            }
        }

        return ret;
    }

    private void processVlanAttribute(Object vlanObj, Map<Integer, List<String>> map,
                                      List<String> ret) {
        if (vlanObj != null && vlanObj instanceof Integer) {
            int vlanId = (Integer) vlanObj;
            List<String> vlanNetworks = map.get(vlanId);
            if (CollectionUtils.isNotEmpty(vlanNetworks)) {
                for (String id : vlanNetworks) {
                    if (!ret.contains(id)) {
                        ret.add(id);
                    }
                }
            }
        }
    }

}
