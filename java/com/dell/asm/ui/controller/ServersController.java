/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.ws.rs.WebApplicationException;

import com.dell.asm.ui.util.DeviceUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.deployment.FCInterface;
import com.dell.asm.asmcore.asmmanager.client.deployment.PortConnection;
import com.dell.asm.asmcore.asmmanager.client.deployment.ServerNetworkObjects;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.DeviceHealth;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.NicFQDD;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareDeviceInventory;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.NetworkConfiguration;
import com.dell.asm.asmcore.asmmanager.client.networkconfiguration.Partition;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.Network;
import com.dell.asm.asmcore.asmmanager.client.util.PuppetClientUtil;
import com.dell.asm.localizablelogger.LocalizedLogMessage;
import com.dell.asm.ui.adapter.service.ChassisServiceAdapter;
import com.dell.asm.ui.adapter.service.CredentialServiceAdapter;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.LogServiceAdapter;
import com.dell.asm.ui.adapter.service.PerformanceMonitorServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.ServerServiceAdapter;
import com.dell.asm.ui.asynchronous.deployment.DeploymentListCallable;
import com.dell.asm.ui.asynchronous.deviceinventory.ManagedDeviceCallable;
import com.dell.asm.ui.asynchronous.server.ServerCallable;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIDeviceStatus;
import com.dell.asm.ui.model.device.UIDellSwitchDevice;
import com.dell.asm.ui.model.server.UIIPAddress;
import com.dell.asm.ui.model.server.UIPortView;
import com.dell.asm.ui.model.server.UIPortViewConnection;
import com.dell.asm.ui.model.server.UIPortViewDeviceConnection;
import com.dell.asm.ui.model.server.UIPortViewGenericSwitch;
import com.dell.asm.ui.model.server.UIPortViewIOM;
import com.dell.asm.ui.model.server.UIPortViewIOMConnection;
import com.dell.asm.ui.model.server.UIPortViewNIC;
import com.dell.asm.ui.model.server.UIPortViewNetwork;
import com.dell.asm.ui.model.server.UIPortViewNicPartition;
import com.dell.asm.ui.model.server.UIPortViewPort;
import com.dell.asm.ui.model.server.UIPortViewSwitch;
import com.dell.asm.ui.model.server.UIPortViewSwitchPort;
import com.dell.asm.ui.model.server.UIPortViewZoneInfo;
import com.dell.asm.ui.model.server.UIServer;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.asm.chassis.client.device.Chassis;
import com.dell.pg.asm.chassis.client.device.TagType;
import com.dell.pg.asm.identitypool.api.common.model.NetworkType;
import com.dell.pg.asm.server.client.device.LogicalNetworkIdentityInventory;
import com.dell.pg.asm.server.client.device.LogicalNetworkInterface;
import com.dell.pg.asm.server.client.device.PowerState;
import com.dell.pg.asm.server.client.device.Server;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Servers Controller.
 */
@RestController
@RequestMapping(value = "/servers/")
public class ServersController extends BaseController {

    public static final String MAC_REGEX = "([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})";
    public static final Pattern MAC_PATTERN = Pattern.compile(MAC_REGEX);
    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(ServersController.class);
    private static final String BLADE_TYPE = "blade";
    private static final String RACK_TYPE = "rack";
    private CredentialServiceAdapter credentialServiceAdapter;
    private ServerServiceAdapter serverServiceAdapter;
    private ChassisServiceAdapter chassisServiceAdapter;
    private LogServiceAdapter logServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private PerformanceMonitorServiceAdapter performanceMonitorServiceAdapter;
    private DeploymentServiceAdapter serviceAdapter;

    @Autowired
    public ServersController(CredentialServiceAdapter credentialServiceAdapter,
                             ServerServiceAdapter serverServiceAdapter,
                             ChassisServiceAdapter chassisServiceAdapter,
                             LogServiceAdapter logServiceAdapter,
                             DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                             PerformanceMonitorServiceAdapter performanceMonitorServiceAdapter,
                             DeploymentServiceAdapter serviceAdapter) {
        this.credentialServiceAdapter = credentialServiceAdapter;
        this.serverServiceAdapter = serverServiceAdapter;
        this.chassisServiceAdapter = chassisServiceAdapter;
        this.logServiceAdapter = logServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.performanceMonitorServiceAdapter = performanceMonitorServiceAdapter;
        this.serviceAdapter = serviceAdapter;
    }

    /**
     * Get server from RA by id..
     *
     * @param request
     *            server ID
     * @return server
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getserverbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServerById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            Server serverInServerRA = null;
            UIServer server = null;
            try {
                serverInServerRA = serverServiceAdapter.getServer(request.requestObj.id);
                FirmwareDeviceInventory[] firmwareDeviceInventory = this.deviceInventoryServiceAdapter.getFirmwareDeviceInventory(
                        request.requestObj.id);

                server = MappingUtils.parseServerDevice(serverInServerRA,
                                                        firmwareDeviceInventory,
                                                        getApplicationContext(),
                                                        credentialServiceAdapter,
                                                        serviceAdapter,
                                                        true);
            } catch (Exception e) {
                log.error("serverId record is not present in serverRA", e);
            }

            if (serverInServerRA == null) {
                ManagedDevice dto = deviceInventoryServiceAdapter.getDeviceInventory(
                        request.requestObj.id);
                server = MappingUtils.parseBMCServerDevice(dto, getApplicationContext(),
                                                           credentialServiceAdapter, true);
            }
            if (server != null) {

                if (server.systemmodel != null && (server.systemmodel.contains("M") || server.systemmodel.contains("FC"))) {
                    Chassis chassis = null;
                    try {
                        chassis = chassisServiceAdapter.getChassisByServiceTag(server.servicetag,
                                                                               TagType.SERVER.value());
                    } catch (WebApplicationException wex) {
                        log.error(
                                "Cannot find chassis for blade: " + server.ipaddress + ", service tag=" + server.servicetag,
                                wex);
                    }

                    if (chassis != null) {
                        server.chassisipaddress = chassis.getManagementIP();
                        server.chassisservicetag = chassis.getServiceTag();

                        if (chassis.getServers() != null && chassis.getServers().size() > 0) {
                            for (com.dell.pg.asm.chassis.client.device.Server cServer : chassis.getServers()) {
                                if (cServer.getServiceTag().equals(server.servicetag)) {
                                    server.slotname = cServer.getSlotName();
                                    server.slotnumber = cServer.getSlot();
                                    break;
                                }
                            }
                        }
                    }
                }

                List<String> filter = new ArrayList<>();
                filter.add("co,marshalledLocalizableMessage," + server.servicetag);
                ManagedDevice dto = deviceInventoryServiceAdapter.getDeviceInventory(
                        request.requestObj.id);
                try {
                    MappingUtils.parsePerformanceMonitoring(server, dto,
                            performanceMonitorServiceAdapter);
                } catch (WebApplicationException wex) {
                    if (wex.getResponse().getStatus() == HttpStatus.NOT_FOUND.value()) {
                        log.warn("Not found Performance data for server ID=" + request.requestObj.id);
                    } else {
                        log.error(
                                "Performance data gathering failed for server Id=" + request.requestObj.id,
                                wex);
                    }
                } catch (Exception e) {
                    //ignore the performance monitoring error. This will ensure firmware and other tabs of view details gets displayed.
                    log.error("getServerById() - Exception from performance monitoring call", e);
                }

                ResourceList<LocalizedLogMessage> mList = logServiceAdapter.getUserLogMessages(
                        "-timeStamp", filter, 0, 5);
                if (mList != null && mList.getTotalRecords() > 0) {
                    for (LocalizedLogMessage msg : mList.getList()) {
                        server.activityLogs.add(
                                MappingUtils.parseLogEntry(msg, getApplicationContext()));
                    }
                }
            }
            jobResponse.responseObj = server;
        } catch (Throwable t) {
            log.error("getServerById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

       return jobResponse;
    }

    /**
     * Get server port view.
     *
     * @param request
     *            server ID
     * @return server
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getserverportviewbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServerPortViewById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();
        UIPortView portView = new UIPortView();
        jobResponse.responseObj = portView;

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_NETWORKS, null);

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        List<String> filterList = new ArrayList<>();
        filterList.add("eq,server," + request.requestObj.id);

        String serviceId = null;
        String serverComponentId = null;

        try {
            DeploymentListCallable deploymentCallable = new DeploymentListCallable(filterList,
                                                                                   serviceAdapter,
                                                                                   SecurityContextHolder.getContext());
            Future<ResourceList<Deployment>> futureDeployment = threadPool.submit(
                    deploymentCallable);

            ServerCallable serverCallable = new ServerCallable(request.requestObj.id,
                                                               serverServiceAdapter,
                                                               SecurityContextHolder.getContext());
            Future<Server> futureServer = threadPool.submit(serverCallable);

            ManagedDeviceCallable managedDeviceCallable = new ManagedDeviceCallable(
                    request.requestObj.id, deviceInventoryServiceAdapter,
                    SecurityContextHolder.getContext());
            Future<ManagedDevice> futureManagedDevice = threadPool.submit(managedDeviceCallable);

            // async 3 calls at once
            ManagedDevice managedDevice = futureManagedDevice.get();
            Server serverInServerRA = futureServer.get();
            ResourceList<Deployment> depList = futureDeployment.get();

            if (managedDevice != null) {
                portView.hostname = managedDevice.getHostname();
                portView.id = managedDevice.getRefId();
                portView.health = managedDevice.getHealth().getLabel();
                portView.ipaddress = managedDevice.getIpAddress();
                portView.model = managedDevice.getModel();
                portView.type = DeviceController.getDeviceType(managedDevice.getDeviceType());
                if (managedDevice.getState() != null) {
                    UIDeviceStatus deviceStatus = DeviceController.mapToUIStatus(
                            managedDevice.getState());
                    if (deviceStatus != null) {
                        portView.state = deviceStatus.getLabel();
                    }
                }
                portView.statusMessage = managedDevice.getHealthMessage();
                if (serverInServerRA.getPowerState() != null)
                    portView.powerState = serverInServerRA.getPowerState().value().toLowerCase();

                serverComponentId = PuppetClientUtil.toCertificateName(managedDevice);
            }

            if (serverInServerRA != null &&
                    CollectionUtils.isNotEmpty(serverInServerRA.getNetworkInterfaceList())) {
                // process NICs
                portView.nics = parseNICs(serverInServerRA.getNetworkInterfaceList(),
                                          options.filterList, portView.powerState);
            }
            if (depList != null && depList.getList() != null && depList.getList().length > 0) {
                serviceId = depList.getList()[0].getId();
            }

            if (serviceId != null && serverComponentId != null) {
                ServerNetworkObjects sNet = serviceAdapter.getServerNetworkObjects(serviceId,
                                                                                   serverComponentId);
                processPortView(sNet, portView, options.filterList);
            }

        } catch (Throwable t) {
            log.error("getServerPortViewById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        } finally {
            threadPool.shutdown();
        }

        return jobResponse;
    }

    private void processPortView(ServerNetworkObjects sNet, UIPortView portView,
                                 List<String> filterList) throws IOException {
        if (sNet == null || portView == null) {
            return;
        }

        Map<String, UIPortViewSwitch> torSwitches = new HashMap<>();
        Map<String, UIPortViewIOM> ioModules = new HashMap<>();

        // these 2 maps needed for filtering
        Map<String, List<UIPortViewGenericSwitch>> iom2torMap = new HashMap<>();

        // port connections
        if (sNet.getPortConnections() != null) {
            // need a list of IOMs and TORs
            for (PortConnection pc : sNet.getPortConnections()) {
                addSwitchesToPortView(pc, ioModules, torSwitches, iom2torMap,
                                      pc.getLocalDevice().equals(sNet.getServer()));
            }

            // make a hash of ioms/tor for faster operations
            portView.ioModules.addAll(ioModules.values());
            portView.torSwitches.addAll(torSwitches.values());

            for (PortConnection pc : sNet.getPortConnections()) {
                // start from server
                if (sNet.getServer().equals(pc.getLocalDevice())) {
                    NicFQDD nicFQDD = new NicFQDD(pc.getLocalPorts().get(0));

                    // for blades, it is IOM
                    // for racks, it is TOR
                    UIPortViewConnection uipc = new UIPortViewConnection();
                    uipc.connectedDevice = new UIPortViewDeviceConnection(pc);
                    uipc.nicId = nicFQDD.getCardKey();
                    uipc.nicPortId = nicFQDD.getPort();

                    // for FC connected, check we have corresponding record  in fc_interfaces
                    // otherwise skip it
                    if (nicFQDD.getPrefix().equals("FC")) {
                        boolean found = false;
                        if (CollectionUtils.isNotEmpty(sNet.getFcInterfaces())) {
                            for (FCInterface fci : sNet.getFcInterfaces()) {
                                if (fci.getFqdd() != null &&
                                        fci.getFqdd().equals(nicFQDD.toString())) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            log.debug(
                                    "Skip connected for " + nicFQDD.toString() + " - no fc_interface found");
                            continue;
                        }
                    }

                    if (uipc.matches(filterList)) {
                        portView.portConnections.add(uipc);
                    } else {
                        continue;
                    }


                    if (BLADE_TYPE.equals(pc.getRemoteDeviceType())) {
                        // the code below relates to IOM only
                        // find corresponding IOM <-> switch connected, passing IOM id
                        uipc.iomUplinkConnections = findIOMUplinkConnection(
                                sNet.getPortConnections(),
                                uipc.connectedDevice.deviceId);

                        UIPortViewIOM iomDevice = ioModules.get(uipc.connectedDevice.deviceId);
                        if (iomDevice != null && !iomDevice.downlinkPorts.contains(
                                uipc.connectedDevice.downlinkPort))
                            iomDevice.downlinkPorts.add(uipc.connectedDevice.downlinkPort);

                        for (UIPortViewIOMConnection iom : uipc.iomUplinkConnections) {
                            iomDevice = ioModules.get(iom.iomid);
                            iomDevice.uplinkPorts.add(iom.uplinkPort);

                            UIPortViewSwitch tor;
                            if (!torSwitches.containsKey(iom.connectedDevice.deviceId)) {
                                tor = new UIPortViewSwitch();
                                tor.id = iom.connectedDevice.deviceId;
                                torSwitches.put(iom.connectedDevice.deviceId, tor);
                                portView.torSwitches.add(tor);
                            } else {
                                tor = torSwitches.get(iom.connectedDevice.deviceId);
                            }

                            if (!tor.downlinkPorts.contains(iom.connectedDevice.downlinkPort)) {
                                tor.downlinkPorts.add(iom.connectedDevice.downlinkPort.clone());
                            }
                        }
                    } else {
                        // remote device is TOR
                        UIPortViewSwitch tor = torSwitches.get(pc.getRemoteDevice());
                        if (tor != null) {
                            if (!tor.downlinkPorts.contains(uipc.connectedDevice.downlinkPort)) {
                                tor.downlinkPorts.add(uipc.connectedDevice.downlinkPort.clone());
                            }
                        } else {
                            // bad data
                            log.error(
                                    "Invalid data received - no TOR found for " + pc.getRemoteDevice());
                        }
                    }
                }
            }
        }

        // grab some details for networking objects
        // and get rid of orphan devices
        List<UIPortViewIOM> unmatchedIOMs = new ArrayList<>();
        ResourceList<ManagedDevice> allSwitches = null;

        if (ioModules.size() > 0 || torSwitches.size() > 0) {
            // get all switches at once
            StringBuilder idList = new StringBuilder("eq,refId");
            for (UIPortViewIOM iom : ioModules.values()) {
                if (iom.downlinkPorts.isEmpty() &&
                        iom.uplinkPorts.isEmpty()) {
                    portView.ioModules.remove(ioModules.get(iom.id));
                    updateSwitchLinkedMap(iom, iom2torMap);
                }
                idList.append(",").append(iom.id);
            }
            for (UIPortViewSwitch sw : torSwitches.values()) {
                if (sw.downlinkPorts.isEmpty()) {
                    portView.torSwitches.remove(torSwitches.get(sw.id));
                    updateSwitchLinkedMap(sw, iom2torMap);
                }
                idList.append(",").append(sw.id);
            }
            List<String> switchFilterList = new ArrayList<>();
            switchFilterList.add(idList.toString());

            allSwitches = deviceInventoryServiceAdapter.getAllDeviceInventory(null,
                                                                              switchFilterList,
                                                                              null,
                                                                              MappingUtils.MAX_RECORDS);
        }

        for (UIPortViewIOM iom : ioModules.values()) {
            try {
                ManagedDevice device = findById(allSwitches, iom.id);
                if (device != null) {
                    iom.ipaddress = device.getIpAddress();
                    iom.ipaddressurl = "";
                    iom.hostname = device.getDisplayName();
                    iom.health = device.getHealth().getLabel();
                    iom.assettag = device.getServiceTag();
                    iom.model = device.getModel();
                    iom.deviceType = DeviceUtil.getSwitchDeviceType(device.getModel());
                    iom.statusMessage = device.getHealthMessage();

                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> data = mapper.readValue(device.getFacts(), Map.class);

                    UIDellSwitchDevice dellSwitch = DellSwitchController.parseSwitch(iom.id, data,
                                                                                     device);
                    iom.fabric = dellSwitch.fabric;

                    if (iom.health.equals(DeviceHealth.RED.getLabel()) ||
                            iom.health.equals(DeviceHealth.YELLOW.getLabel())) {
                        setPortsHealth(iom.downlinkPorts, iom.health);
                        setPortsHealth(iom.uplinkPorts, iom.health);
                    }

                }
            } catch (WebApplicationException wex) {
                log.error("Device not found", wex);
                iom.health = DeviceHealth.UNKNOWN.getLabel();
                iom.hostname = iom.id;
                iom.statusMessage = "Device not found";
            }

            // filter by health
            if (!iom.matches(filterList)) {
                unmatchedIOMs.add(iom);
                updateSwitchLinkedMap(iom, iom2torMap);
            }
        }

        List<UIPortViewSwitch> unmatchedTORs = new ArrayList<>();
        for (UIPortViewSwitch sw : torSwitches.values()) {
            try {
                ManagedDevice device = findById(allSwitches, sw.id);
                if (device != null) {
                    sw.ipaddress = device.getIpAddress();
                    sw.ipaddressurl = "";
                    sw.hostname = device.getDisplayName();
                    sw.health = device.getHealth().getLabel();
                    sw.assettag = device.getServiceTag();
                    sw.model = device.getModel();
                    sw.deviceType = DeviceUtil.getSwitchDeviceType(device.getModel());
                    sw.statusMessage = device.getHealthMessage();

                    if (sw.health.equals(DeviceHealth.RED.getLabel()) ||
                            sw.health.equals(DeviceHealth.YELLOW.getLabel())) {
                        setPortsHealth(sw.downlinkPorts, sw.health);
                    }
                }
            } catch (WebApplicationException wex) {
                log.error("Device not found", wex);
                sw.health = DeviceHealth.UNKNOWN.getLabel();
                sw.hostname = sw.id;
                sw.statusMessage = "Device not found";
            }

            // filter by health
            if (!sw.matches(filterList)) {
                unmatchedTORs.add(sw);
                updateSwitchLinkedMap(sw, iom2torMap);
            }
        }

        // do not leave orphan links
        for (UIPortViewSwitch switchToDelete : unmatchedTORs) {
            // verify it is not used by any of remaining IOMs
            if (iom2torMap.containsKey(switchToDelete.id) &&
                    iom2torMap.get(switchToDelete.id).size() > 0) {
                continue;
            }
            portView.torSwitches.remove(switchToDelete);
        }

        for (UIPortViewIOM iomToDelete : unmatchedIOMs) {
            // verify it is not used by any of remaining TORs
            if (iom2torMap.containsKey(iomToDelete.id) &&
                    iom2torMap.get(iomToDelete.id).size() > 0) {
                continue;
            }
            portView.ioModules.remove(iomToDelete);
        }

        // sort connections by connected IOM/TOR/NIC
        portView.portConnections.sort(new ConnectionComparator());

        int connIdx = 1;
        for (UIPortViewConnection conn : portView.portConnections) {
            for (UIPortViewNIC nic : portView.nics) {
                if (nic.id.equals(conn.nicId)) {
                    nic.rank = connIdx;
                    updateNicFabric(nic, conn, portView.ioModules);
                }
            }
            for (UIPortViewGenericSwitch sw : portView.ioModules) {
                if (sw.id.equals(conn.connectedDevice.deviceId)) {
                    sw.rank = connIdx;
                }
            }
            for (UIPortViewGenericSwitch sw : portView.torSwitches) {
                if (sw.id.equals(conn.connectedDevice.deviceId)) {
                    sw.rank = connIdx;
                }
            }

            connIdx++;
        }

        // sort NICs and switches to match sorted connections
        portView.nics.sort(new PortViewNICComparator());
        portView.ioModules.sort(new PortViewSwitchComparator());
        portView.torSwitches.sort(new PortViewSwitchComparator());

        // VLANS
        for (UIPortViewNIC nic : portView.nics) {
            for (UIPortViewPort port : nic.ports) {
                if (port.partitions.size() > 0) {
                    for (UIPortViewNicPartition partition : port.partitions) {
                        NetworkConfiguration netObj = sNet.getNetworkConfig();
                        for (Partition netPartition : netObj.getPartitions()) {
                            if (netPartition.getFqdd() != null && netPartition.getFqdd().equals(
                                    partition.name)) {
                                if (partition.wwpn == null)
                                    partition.wwpn = netPartition.getWwpn();
                                if (netPartition.getNetworkObjects() != null) {
                                    for (Network network : netPartition.getNetworkObjects()) {
                                        UIPortViewNetwork uiNet = new UIPortViewNetwork();
                                        uiNet.vlan = String.valueOf(network.getVlanId());
                                        uiNet.name = removeIscsiPartitionFromName(
                                                network.getName());
                                        uiNet.type = network.getType().name();
                                        partition.vlans.add(uiNet);
                                        if (!partition.iscsiEnabled)
                                            partition.iscsiEnabled = network.getType() == NetworkType.STORAGE_ISCSI_SAN;
                                        if (!partition.pxeEnabled)
                                            partition.pxeEnabled = network.getType() == NetworkType.PXE;

                                        if (network.getStaticNetworkConfiguration() != null) {
                                            if (nic.nparEnabled) {
                                                if (StringUtils.isNotEmpty(partition.ipaddress)) {
                                                    partition.ipaddress += ",";
                                                }
                                                partition.ipaddress += network.getStaticNetworkConfiguration().getIpAddress();
                                                if (StringUtils.isEmpty(partition.ipaddressurl)) {
                                                    partition.ipaddressurl = "https://" + network.getStaticNetworkConfiguration().getIpAddress();
                                                }
                                            } else {
                                                // non partitioned NIC - add IP address to NIC level instead
                                                UIIPAddress uiip = new UIIPAddress(
                                                        network.getStaticNetworkConfiguration().getIpAddress());
                                                if (!nic.ipaddresses.contains(uiip)) {
                                                    nic.ipaddresses.add(uiip);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // FC zones
                    if (sNet.getFcInterfaces() != null) {
                        for (FCInterface fcInterface : sNet.getFcInterfaces()) {
                            if (fcInterface.getFqdd().equals(port.fqdd)) {
                                port.zoneInfo = new UIPortViewZoneInfo();
                                port.zoneInfo.id = fcInterface.getActiveZoneset();
                                if (CollectionUtils.isNotEmpty(fcInterface.getConnectedZones())) {
                                    port.zoneInfo.zone = StringUtils.join(
                                            fcInterface.getConnectedZones(), ',');
                                }
                                port.zoneInfo.zoneConfig = fcInterface.getActiveZoneset();
                                if (StringUtils.isEmpty(port.wwpn))
                                    port.wwpn = fcInterface.getWwpn();
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateNicFabric(UIPortViewNIC nic, UIPortViewConnection portConnection,
                                 List<UIPortViewIOM> ioms) {
        if (ioms == null || nic == null || portConnection == null) return;
        for (UIPortViewIOM iom : ioms) {
            if (portConnection.connectedDevice != null && portConnection.connectedDevice.deviceId.equals(
                    iom.id)) {
                for (UIPortViewPort nicPort : nic.ports) {
                    if (nicPort.id.equals(portConnection.nicPortId)) {
                        nicPort.fabric = iom.fabric;
                    }
                }
            }
        }
    }

    /**
     * Remove switch from linked map.
     * @param switchToRemove
     * @param iom2torMap
     */
    private void updateSwitchLinkedMap(UIPortViewGenericSwitch switchToRemove,
                                       Map<String, List<UIPortViewGenericSwitch>> iom2torMap) {

        for (List<UIPortViewGenericSwitch> switches : iom2torMap.values()) {
            if (switches.contains(switchToRemove)) {
                switches.remove(switchToRemove);
            }
        }
    }

    private ManagedDevice findById(ResourceList<ManagedDevice> allSwitches, String id) {
        if (allSwitches == null || allSwitches.getList() == null)
            return null;
        for (ManagedDevice md : allSwitches.getList()) {
            if (md.getRefId().equals(id))
                return md;
        }
        return null;
    }

    private void setPortsHealth(List<UIPortViewSwitchPort> ports, String health) {
        for (UIPortViewSwitchPort port : ports) {
            port.health = health;
        }
    }

    private void addSwitchesToPortView(PortConnection pc, Map<String, UIPortViewIOM> ioModules,
                                       Map<String, UIPortViewSwitch> torSwitches,
                                       Map<String, List<UIPortViewGenericSwitch>> iom2torMap,
                                       boolean isLocalDeviceServer) {
        if (isLocalDeviceServer) {
            // local is server, the other side is either IOM or TOR
            if (BLADE_TYPE.equals(pc.getRemoteDeviceType())) {
                if (!ioModules.containsKey(pc.getRemoteDevice())) {
                    UIPortViewIOM iom = new UIPortViewIOM();
                    iom.id = pc.getRemoteDevice();
                    ioModules.put(iom.id, iom);
                }
            } else {
                if (!torSwitches.containsKey(pc.getRemoteDevice())) {
                    UIPortViewSwitch tor = new UIPortViewSwitch();
                    tor.id = pc.getRemoteDevice();
                    torSwitches.put(tor.id, tor);
                }
            }
        } else {
            // iom -> tor
            // need to add both parts
            String key;
            UIPortViewGenericSwitch value;

            if (BLADE_TYPE.equals(pc.getLocalDeviceType())) {
                if (!ioModules.containsKey(pc.getLocalDevice())) {
                    UIPortViewIOM iom = new UIPortViewIOM();
                    iom.id = pc.getLocalDevice();
                    ioModules.put(iom.id, iom);
                    key = iom.id;
                } else {
                    key = pc.getLocalDevice();
                }
            } else {
                if (!torSwitches.containsKey(pc.getLocalDevice())) {
                    UIPortViewSwitch tor = new UIPortViewSwitch();
                    tor.id = pc.getLocalDevice();
                    torSwitches.put(tor.id, tor);
                    key = tor.id;
                } else {
                    key = pc.getLocalDevice();
                }
            }

            if (BLADE_TYPE.equals(pc.getRemoteDeviceType())) {
                if (!ioModules.containsKey(pc.getRemoteDevice())) {
                    UIPortViewIOM iom = new UIPortViewIOM();
                    iom.id = pc.getRemoteDevice();
                    ioModules.put(iom.id, iom);
                    value = iom;
                } else {
                    value = ioModules.get(pc.getRemoteDevice());
                }
            } else {
                if (!torSwitches.containsKey(pc.getRemoteDevice())) {
                    UIPortViewSwitch tor = new UIPortViewSwitch();
                    tor.id = pc.getRemoteDevice();
                    torSwitches.put(tor.id, tor);
                    value = tor;
                } else {
                    value = torSwitches.get(pc.getRemoteDevice());
                }
            }

            if (key != null && value != null) {
                List<UIPortViewGenericSwitch> switches;
                if (iom2torMap.containsKey(key)) {
                    switches = iom2torMap.get(key);
                } else {
                    switches = new ArrayList<>();
                    iom2torMap.put(key, switches);
                }
                if (!switches.contains(value)) {
                    switches.add(value);
                }
            }
        }
    }

    /**
     * For specified IOM find corresponding uoplink switch. If not provided by service, make up one
     * with "unknown" attributes - otherwise UI won't work as expected
     * @param portConnections
     * @param iomId
     * @return
     */
    private List<UIPortViewIOMConnection> findIOMUplinkConnection(
            List<PortConnection> portConnections, String iomId) {
        List<UIPortViewIOMConnection> ret = new ArrayList<>();

        for (PortConnection pc : portConnections) {
            if (pc.getLocalDevice().equals(iomId)) {
                String portId = UIPortViewDeviceConnection.concatPorts(pc.getLocalPorts());
                UIPortViewIOMConnection conn = new UIPortViewIOMConnection();
                conn.connectedDevice = new UIPortViewDeviceConnection(pc); // this is switch
                conn.iomid = iomId;
                conn.uplinkPort = new UIPortViewSwitchPort(portId); // this is IOM uplink ports
                ret.add(conn);
            }
        }

        return ret;
    }

    private List<UIPortViewNIC> parseNICs(List<LogicalNetworkInterface> networkInterfaceList,
                                          List<String> filterList, String powerState) {
        List<UIPortViewNIC> nics = new ArrayList<>();

        Map<String, UIPortViewNIC> cards = new HashMap<>();
        for (LogicalNetworkInterface nic : networkInterfaceList) {
            // each nic here is either port or partition
            NicFQDD nicFQDD = new NicFQDD(nic.getFqdd());

            UIPortViewNIC card;
            if (cards.containsKey(nicFQDD.getCardKey())) {
                card = cards.get(nicFQDD.getCardKey());
            } else {
                card = new UIPortViewNIC();
                card.id = nicFQDD.getCardKey();
                card.name = nicFQDD.getCardKey();
                card.location = nicFQDD.getLocator();
                card.model = stripMAC(nic.getProductName());
                card.modelDisplayName = MappingUtils.NIC_NAMES.findByName(
                        nic.getProductName()).getNicName();

                cards.put(nicFQDD.getCardKey(), card);
            }

            UIPortViewPort port = null;
            for (UIPortViewPort p : card.ports) {
                if (p.id.equals(nicFQDD.getPort())) {
                    port = p;
                    break;
                }
            }

            if (port == null) {
                port = new UIPortViewPort();

                port.id = nicFQDD.getPort();
                port.number = Integer.parseInt(nicFQDD.getPort());
                port.name = "Port " + nicFQDD.getPort();
                port.health = PowerState.OFF.value().toLowerCase().equals(
                        powerState) ? "unknown" : "green";
                if (port.matches(filterList))
                    card.ports.add(port);
            }


            if (nicFQDD.getPartition() != null) {
                UIPortViewNicPartition partition = new UIPortViewNicPartition();
                port.partitions.add(partition);
                if (port.partitions.size() > 1)
                    card.nparEnabled = true;

                partition.name = nic.getFqdd();

                partition.id = nicFQDD.getPartition();
                partition.displayName = "Partition " + nicFQDD.getPartition();

                for (LogicalNetworkIdentityInventory inv : nic.getIdentityList()) {
                    switch (inv.getIdentityType()) {
                    case WWPN:
                        partition.wwpn = inv.getPermanentIdentity();
                        break;
                    case LAN_MAC:
                        partition.macaddress = inv.getPermanentIdentity();
                        break;
                    }
                }

            } else {
                // need this to link zone info
                port.fqdd = nic.getFqdd();
                // for FC cards save wwpn
                for (LogicalNetworkIdentityInventory inv : nic.getIdentityList()) {
                    switch (inv.getIdentityType()) {
                    case WWPN:
                        port.wwpn = inv.getPermanentIdentity();
                        break;
                    }
                }
            }

        }

        List<String> keySet = new ArrayList<>(cards.keySet());
        keySet.sort(new NICComparator());

        for (String cardKey : keySet) {
            UIPortViewNIC nic = cards.get(cardKey);
            if (nic.matches(filterList))
                nics.add(nic);
        }

        return nics;
    }

    private String stripMAC(String productName) {
        if (productName == null)
            return null;

        Matcher matcher = MAC_PATTERN.matcher(productName);
        if (matcher.find()) {
            String model = productName.substring(0, matcher.start() - 1);
            if (model.endsWith("-"))
                model = model.substring(0, model.length() - 2).trim();
            return model;
        }
        return productName;
    }

    private String removeIscsiPartitionFromName(String name) {
        if (name != null) {
            int index = name.indexOf(" IP Partition ");
            if (index >= 0) {
                name = name.substring(0, index);
            }
        }
        return name;
    }

    public class NICComparator implements Comparator<String> {
        @Override
        public int compare(String x, String y) {
            if (x == null || y == null)
                return 0;
            try {
                NicFQDD nfx = new NicFQDD(x);
                NicFQDD nfy = new NicFQDD(y);

                if (nfx.getPrefix().equals(nfy.getPrefix()))
                    return nfx.toString().compareTo(nfy.toString());
                if (nfx.getPrefix().equals("FC"))
                    return 1;
                else if (nfy.getPrefix().equals("FC"))
                    return -1;

                return nfx.toString().compareTo(nfy.toString());
            } catch (Exception e) {
                return x.compareTo(y);
            }
        }
    }

    /**
     * NIC/FC  -&gt; IOM -&gt; TOR - VLANs
     * NIC/FC -&gt; IOM - VLANs
     * NIC/FC -&gt; TOR - VLANs
     * NIC/FC with no connections or disabled
     */
    public class ConnectionComparator implements Comparator<UIPortViewConnection> {
        @Override
        public int compare(UIPortViewConnection x, UIPortViewConnection y) {
            if (x == null || y == null)
                return 0;

            // both IOM abd TOR
            if (x.iomUplinkConnections != null && y.iomUplinkConnections == null)
                return 1;

            if (x.iomUplinkConnections == null && y.iomUplinkConnections != null)
                return -1;

            if (x.iomUplinkConnections == null) {
                // either TOR or IOM, IOM goes first
                if (x.connectedDevice.deviceId.contains("iom") &&
                        !y.connectedDevice.deviceId.contains("iom")) {
                    return 1;
                }
                if (!x.connectedDevice.deviceId.contains("iom") &&
                        y.connectedDevice.deviceId.contains("iom")) {
                    return -1;
                }
            }

            return new NICComparator().compare(x.nicId, y.nicId);
        }
    }

    public class PortViewNICComparator implements Comparator<UIPortViewNIC> {
        @Override
        public int compare(UIPortViewNIC x, UIPortViewNIC y) {
            if (x.rank != y.rank)
                return Integer.compare(x.rank, y.rank);
            else
                return new NICComparator().compare(x.id, y.id);
        }
    }

    public class PortViewSwitchComparator implements Comparator<UIPortViewGenericSwitch> {
        @Override
        public int compare(UIPortViewGenericSwitch x, UIPortViewGenericSwitch y) {
            if (x.rank != y.rank)
                return Integer.compare(x.rank, y.rank);
            else
                return x.id.compareTo(y.id);
        }
    }

}

