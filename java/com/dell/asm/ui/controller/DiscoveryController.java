/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.configure.AddressingMode;
import com.dell.asm.asmcore.asmmanager.client.configure.ConfigurationRequest;
import com.dell.asm.asmcore.asmmanager.client.configure.ConfigurationResponse;
import com.dell.asm.asmcore.asmmanager.client.configure.DiscoveredDeviceConfiguration;
import com.dell.asm.asmcore.asmmanager.client.configure.NetworkIdentity;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoverIPRangeDeviceRequest;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoverIPRangeDeviceRequests;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveredDevices;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveryRequest;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveryStatus;
import com.dell.asm.ui.adapter.service.ConfigureDevicesServiceAdapter;
import com.dell.asm.ui.adapter.service.DiscoverDevicesServiceAdapter;
import com.dell.asm.ui.adapter.service.FirmwareRepositoryServiceAdapter;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.exception.MappingRequestException;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.chassis.JobChassisConfigurationStatusRequest;
import com.dell.asm.ui.model.chassis.JobChassisConnectionCheckRequest;
import com.dell.asm.ui.model.chassis.JobDiscoveryDataRequest;
import com.dell.asm.ui.model.chassis.UIChassisConfiguration;
import com.dell.asm.ui.model.chassis.UIChassisConfigurationStatus;
import com.dell.asm.ui.model.chassis.UIChassisConnectionCheck;
import com.dell.asm.ui.model.chassis.UIDiscoveryData;
import com.dell.asm.ui.model.chassis.UIRackConfiguration;
import com.dell.asm.ui.model.chassis.UIRackConfigurationStatus;
import com.dell.asm.ui.model.configure.UIDeviceConfiguration;
import com.dell.asm.ui.util.MappingUtils;

/**
 * The Class DiscoveryController.
 */
@RestController
@RequestMapping(value = "/discovery/")
public class DiscoveryController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(DiscoveryController.class);

    private DiscoverDevicesServiceAdapter discoverDevicesServiceAdapter;
    private FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter;
    private ConfigureDevicesServiceAdapter configureDevicesServiceAdapter;

    @Autowired
    public DiscoveryController(DiscoverDevicesServiceAdapter discoverDevicesServiceAdapter,
                               FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter,
                               ConfigureDevicesServiceAdapter configureDevicesServiceAdapter) {
        this.discoverDevicesServiceAdapter = discoverDevicesServiceAdapter;
        this.firmwareRepositoryServiceAdapter = firmwareRepositoryServiceAdapter;
        this.configureDevicesServiceAdapter = configureDevicesServiceAdapter;
    }

    /**
     * Gets the chassis configurations status.
     *
     * @param request
     *            the request
     * @return the chassis configurations status
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getchassisconfigurationsstatus", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getChassisConfigurationsStatus(@RequestBody JobChassisConfigurationStatusRequest request) {

        JobResponse jobResponse = new JobResponse();
        UIChassisConfigurationStatus responseObj = new UIChassisConfigurationStatus();

        if (request.requestObj.jobId != null) {

            try {
                DiscoveryRequest jobResult = this.discoverDevicesServiceAdapter.getDiscoveryResult(
                        request.requestObj.jobId, null, null, null, null);
                boolean pending = false;

                if (jobResult.getStatus() == DiscoveryStatus.FAILED ||
                        jobResult.getStatus() == DiscoveryStatus.ERROR ||
                        jobResult.getStatus() == DiscoveryStatus.UNSUPPORTED) {
                    log.warn("Discovery failed for at least one device");
                } else {
                    pending = jobResult.getStatus() == DiscoveryStatus.INPROGRESS ||
                            jobResult.getStatus() == DiscoveryStatus.PENDING;
                }

                if (jobResult.getDevices() != null) {
                    UIChassisConfiguration uic;
                    for (DiscoveredDevices result : jobResult.getDevices()) {
                        if (DeviceType.isChassis(result.getDeviceType())) {
                            uic = MappingUtils.parseDiscoveryResult(result,
                                                                    firmwareRepositoryServiceAdapter);
                            responseObj.chassisConfigurations.add(uic);
                        }
                        // request is pending if any chassis still pending on discovery
                        if (result.getStatus() == DiscoveryStatus.PENDING) {
                            pending = true;
                        }
                    }
                }

                responseObj.pending = pending;
                responseObj.jobId = request.requestObj.jobId;

            } catch (Throwable t) {
                log.error("getChassisConfigurations() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Gets the chassis list - quick discovery.
     *
     * @param request
     *            the request
     * @return the chassis configurations
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getchassislist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getChassisList(@RequestBody JobChassisConnectionCheckRequest request) {

        JobResponse jobResponse = new JobResponse();
        UIChassisConfigurationStatus responseObj = new UIChassisConfigurationStatus();
        responseObj.pending = true;

        try {
            List<UIChassisConnectionCheck> checks = new ArrayList<>();
            for (UIChassisConnectionCheck check : request.requestObj) {
                if ("chassis".equals(check.resourcetype) && (
                        check.chassisCredentialId == null ||
                                check.bladeCredentialId == null ||
                                check.iomCredentialId == null)) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.noCredentials",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
                if ("chassis".equals(check.resourcetype) || "all".equals(check.resourcetype)) {
                    checks.add(check);
                }
            }

            DiscoveryRequest requests = MappingUtils.createDiscoverIPRangeDeviceRequests(checks);
            if (CollectionUtils.isNotEmpty(
                    requests.getDiscoveryRequestList().getDiscoverIpRangeDeviceRequests())) {
                DiscoveryRequest discoveryStatus = this.discoverDevicesServiceAdapter
                        .listDevicesForIPRangeDiscoveryRequest(requests);

                responseObj.jobId = discoveryStatus.getId();
            } else {
                responseObj.pending = false;
            }

        } catch (MappingRequestException ex) {
            jobResponse = addErrorMessage(jobResponse, ex.getMessage());
            return jobResponse;
        } catch (Throwable t) {
            log.error("getChassisList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Gets the rack list - quick discovery.
     *
     * @param request
     *            the request
     * @return the rack configurations
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getracklist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getRackList(@RequestBody JobChassisConnectionCheckRequest request) {

        JobResponse jobResponse = new JobResponse();
        UIRackConfigurationStatus responseObj = new UIRackConfigurationStatus();
        responseObj.pending = true;

        try {
            List<UIChassisConnectionCheck> checks = new ArrayList<>();
            for (UIChassisConnectionCheck check : request.requestObj) {
                if ("server".equals(check.resourcetype) && check.serverCredentialId == null) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.noCredentials",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
                if ("server".equals(check.resourcetype) || "all".equals(check.resourcetype)) {
                    checks.add(check);
                }
            }

            DiscoveryRequest requests = MappingUtils.createDiscoverIPRangeDeviceRequests(checks);
            if (CollectionUtils.isNotEmpty(
                    requests.getDiscoveryRequestList().getDiscoverIpRangeDeviceRequests())) {
                DiscoveryRequest discoveryStatus = this.discoverDevicesServiceAdapter
                        .listDevicesForIPRangeDiscoveryRequest(requests);

                responseObj.jobId = discoveryStatus.getId();
            } else {
                responseObj.pending = false;
            }

        } catch (MappingRequestException ex) {
            jobResponse = addErrorMessage(jobResponse, ex.getMessage());
            return jobResponse;
        } catch (Throwable t) {
            log.error("getRackList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Gets the chassis configurations status.
     *
     * @param request
     *            the request
     * @return the chassis configurations status
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getchassisliststatus", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getChassisListStatus(@RequestBody JobChassisConfigurationStatusRequest request) {
        return getChassisConfigurationsStatus(request);
    }

    /**
     * Gets the rack configurations status.
     *
     * @param request
     *            the request
     * @return the chassis configurations status
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getrackliststatus", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getRackListStatus(@RequestBody JobChassisConfigurationStatusRequest request,
                                  HttpSession session) {

        JobResponse jobResponse = new JobResponse();
        UIRackConfigurationStatus responseObj = new UIRackConfigurationStatus();
        if (request.requestObj.jobId != null) {
            try {
                DiscoveryRequest jobResult = this.discoverDevicesServiceAdapter.getDiscoveryResult(
                        request.requestObj.jobId, null, null, null, null);
                boolean pending = false;

                if (jobResult.getStatus() == DiscoveryStatus.FAILED ||
                        jobResult.getStatus() == DiscoveryStatus.ERROR ||
                        jobResult.getStatus() == DiscoveryStatus.UNSUPPORTED) {
                    log.warn("Discovery failed for at least one device");
                } else {
                    pending = jobResult.getStatus() == DiscoveryStatus.INPROGRESS || jobResult.getStatus() == DiscoveryStatus.PENDING;
                }

                if (jobResult.getDevices() != null) {
                    UIRackConfiguration uic;
                    for (DiscoveredDevices result : jobResult.getDevices()) {
                        if (DeviceType.isServer(result.getDeviceType())) {
                            if (!DiscoveryStatus.IGNORE.equals(result.getStatus()) &&
                                    !DiscoveryStatus.UNSUPPORTED.equals(result.getStatus())) {
                                uic = MappingUtils.parseDiscoveryResultForRack(result,
                                                                               this.getApplicationContext());
                                responseObj.rackConfigurations.add(uic);

                                // request is pending if any server still pending on discovery
                                if (result.getStatus() == DiscoveryStatus.PENDING) {
                                    pending = true;
                                }
                            }
                        }
                    }
                }

                responseObj.pending = pending;
                responseObj.jobId = request.requestObj.jobId;

            } catch (Throwable t) {
                log.error("getRackListStatus() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Save chassis discovery result and delete discovery job.
     *
     * @param request
     *            the request: JobChassisConfigurationStatusRequest
     * @return none
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "submitdiscovery", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse submitDiscovery(@RequestBody JobDiscoveryDataRequest request, HttpSession session) {

        JobResponse jobResponse = new JobResponse();

        try {
            DiscoveryRequest originalRequests = MappingUtils.createDiscoverIPRangeDeviceRequests(
                    request.requestObj.ipdata);

            // expand ranges
            DiscoveryRequest requests = expandRanges(originalRequests);

            // these chassis and racks passed "quick" discovery and have been presented by UI.
            // User does not expect other devices to be discovered.
            List<String> racksAndChassisToDiscover = new ArrayList<>();

            // this will create a discovery requests for selected chassis.
            // it will also remove used IPs from passed DiscoveryRequest

            // include only IP requests that belong to chassis in configuration spec
            boolean needConfiguration = false;
            if (request.requestObj.chassisdata != null && CollectionUtils.isNotEmpty(
                    request.requestObj.chassisdata.chassisConfigurations)) {
                for (UIChassisConfiguration cConfig : request.requestObj.chassisdata.chassisConfigurations) {
                    if (cConfig.configureDevice) {
                        Iterator<DiscoverIPRangeDeviceRequest> it = requests.getDiscoveryRequestList().getDiscoverIpRangeDeviceRequests().iterator();
                        while (it.hasNext()) {
                            DiscoverIPRangeDeviceRequest r = it.next();

                            if (r.getDeviceStartIp().equals(cConfig.ipAddress)) {
                                needConfiguration = true;
                                it.remove();
                                break;
                            }
                        }
                    } else {
                        racksAndChassisToDiscover.add(cConfig.ipAddress);
                    }
                }
            }

            // include only IP requests that belong to racks in configuration spec
            if (request.requestObj.rackdata != null && CollectionUtils.isNotEmpty(
                    request.requestObj.rackdata.rackConfigurations)) {
                for (UIRackConfiguration cConfig : request.requestObj.rackdata.rackConfigurations) {
                    if (cConfig.configureDevice) {
                        needConfiguration = true;
                        Iterator<DiscoverIPRangeDeviceRequest> it = requests.getDiscoveryRequestList().getDiscoverIpRangeDeviceRequests().iterator();
                        while (it.hasNext()) {
                            DiscoverIPRangeDeviceRequest r = it.next();

                            if (r.getDeviceStartIp().equals(cConfig.ipaddress)) {
                                needConfiguration = true;
                                it.remove();
                                break;
                            }
                        }
                    } else {
                        racksAndChassisToDiscover.add(cConfig.ipaddress);
                    }
                }
            }

            if (needConfiguration) {
                ConfigurationRequest configurationRequest = createDeviceConfiguration(
                        request.requestObj);
                ConfigurationResponse response = this.configureDevicesServiceAdapter.configurationAndDiscoveryRequest(
                        configurationRequest);
                log.debug(
                        "Started configure & discovery job for chassis. Job ID: " + Arrays.toString(
                                response.getJobNames().toArray(
                                        new String[response.getJobNames().size()])));
            }

            // for remaining list
            // exclude server and chassis devices not present in  requestObj.chassisdata and requestObj.rackdata
            // because they did not pass quick discovery
            requests.getDiscoveryRequestList().getDiscoverIpRangeDeviceRequests().removeIf(
                    r -> (DeviceType.Server.equals(r.getDeviceType()) || DeviceType.Chassis.equals(
                            r.getDeviceType())) &&
                            !racksAndChassisToDiscover.contains(r.getDeviceStartIp()));

            // at this point only non-rack/chassis requests should remain in the list
            // and racks / chassis that do not require configuration but passed quick discovery test
            // call a regular discovery on those.
            if (requests.getDiscoveryRequestList().getDiscoverIpRangeDeviceRequests().size() > 0) {
                this.discoverDevicesServiceAdapter.deviceIPRangeDiscoveryRequest(requests);
            }
        } catch (Throwable t) {
            log.error("submitDiscovery() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Replace each range with a set of singular IPs
     * @param requests
     * @return
     */
    private DiscoveryRequest expandRanges(DiscoveryRequest requests) {
        DiscoveryRequest ret = new DiscoveryRequest();

        DiscoverIPRangeDeviceRequests iprequests = new DiscoverIPRangeDeviceRequests();
        Set<DiscoverIPRangeDeviceRequest> reqs = iprequests.getDiscoverIpRangeDeviceRequests();
        ret.setDiscoveryRequestList(iprequests);

        for (DiscoverIPRangeDeviceRequest r : requests.getDiscoveryRequestList().getDiscoverIpRangeDeviceRequests()) {
            List<String> expandedIpRange = DiscoverIPRangeDeviceRequest.expandIpAddresses(r);

            for (String ip : expandedIpRange) {
                DiscoverIPRangeDeviceRequest exReq = new DiscoverIPRangeDeviceRequest();
                exReq.setDeviceStartIp(ip);
                exReq.setDeviceEndIp(ip);
                exReq.setDeviceServerCredRef(r.getDeviceServerCredRef());
                exReq.setDeviceChassisCredRef(r.getDeviceChassisCredRef());
                exReq.setDeviceSwitchCredRef(r.getDeviceSwitchCredRef());
                exReq.setDeviceSCVMMCredRef(r.getDeviceSCVMMCredRef());
                exReq.setDeviceStorageCredRef(r.getDeviceStorageCredRef());
                exReq.setDeviceVCenterCredRef(r.getDeviceVCenterCredRef());
                exReq.setDeviceEMCredRef(r.getDeviceEMCredRef());
                exReq.setDeviceScaleIOCredRef(r.getDeviceScaleIOCredRef());
                exReq.setUnmanaged(r.isUnmanaged());
                exReq.setReserved(r.isReserved());
                exReq.setConfig(r.getConfig());
                exReq.setDeviceType(r.getDeviceType());
                exReq.setServerPoolId(r.getServerPoolId());
                reqs.add(exReq);
            }

        }
        return ret;
    }

    /**
     * Reserve IPs, save configuration.
     *
     * @param request
     *            the request: JobDiscoveryDataRequest
     * @return none
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "verifyconfiguration", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse verifyConfiguration(@RequestBody JobDiscoveryDataRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            boolean configureChassis = false;
            boolean configureServer = false;
            if (request.requestObj.chassisdata != null &&
                    CollectionUtils.isNotEmpty(
                            request.requestObj.chassisdata.chassisConfigurations)) {
                for (UIChassisConfiguration chassisConfiguration : request.requestObj.chassisdata.chassisConfigurations) {
                    if (chassisConfiguration.configureDevice) {
                        configureChassis = true;
                        break;
                    }
                }
            }
            if (request.requestObj.rackdata != null &&
                    CollectionUtils.isNotEmpty(request.requestObj.rackdata.rackConfigurations)) {
                for (UIRackConfiguration rackConfiguration : request.requestObj.rackdata.rackConfigurations) {
                    if (rackConfiguration.configureDevice) {
                        configureServer = true;
                        break;
                    }
                }
            }

            for (UIChassisConnectionCheck check : request.requestObj.ipdata) {
                if (configureChassis && check.resourcetype.equals("chassis") && (
                        check.chassisCredentialId == null ||
                                check.bladeCredentialId == null ||
                                check.iomCredentialId == null)) {
                    throw new ControllerException(getApplicationContext().getMessage(
                            "validationError.noCredentialsChassis", null,
                            LocaleContextHolder.getLocale()));
                } else if (configureServer && check.resourcetype.equals("server") && (
                        check.serverCredentialId == null)) {
                    throw new ControllerException(getApplicationContext().getMessage(
                            "validationError.noCredentialsServer", null,
                            LocaleContextHolder.getLocale()));
                }
            }

            if (request.requestObj.chassisconfig != null) {
                if (configureChassis && AddressingMode.Static.equals(AddressingMode.fromLabel(
                        request.requestObj.chassisconfig.chassisaddressingmode)) &&
                        request.requestObj.chassisconfig.chassisnetworkid == null) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.noChassisNetwork",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
                if (configureChassis && AddressingMode.Static.equals(AddressingMode.fromLabel(
                        request.requestObj.chassisconfig.serveraddressingmode)) &&
                        request.requestObj.chassisconfig.servernetworkid == null) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.noBladeNetwork",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
                if (configureChassis && AddressingMode.Static.equals(AddressingMode.fromLabel(
                        request.requestObj.chassisconfig.iomaddressingmode)) &&
                        request.requestObj.chassisconfig.iomnetworkid == null) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.noIOMNetwork", null,
                                                               LocaleContextHolder.getLocale()));
                }
                if (configureServer && AddressingMode.Static.equals(AddressingMode.fromLabel(
                        request.requestObj.chassisconfig.rackAddressingMode)) &&
                        request.requestObj.chassisconfig.rackNetworkId == null) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.noServerNetwork",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }
            }

            if (configureChassis || configureServer) {

                ConfigurationRequest configurationRequest = createDeviceConfiguration(
                        request.requestObj);

                Response r = configureDevicesServiceAdapter.processConfiguration(
                        configurationRequest);
                if (r.getStatus() != HttpStatus.NO_CONTENT_204) {
                    jobResponse = addFailureResponseInfo(r);
                }
            }

        } catch (Throwable t) {
            log.error("verifyConfiguration() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }


    private ConfigurationRequest createDeviceConfiguration(UIDiscoveryData requestObj) {

        UIDeviceConfiguration uiConfig = requestObj.chassisconfig;

        List<UIChassisConfiguration> chassisConfigurations = null;
        List<UIRackConfiguration> rackConfigurations = null;
        if (requestObj.chassisdata != null) {
            chassisConfigurations = requestObj.chassisdata.chassisConfigurations;
        }
        if (requestObj.rackdata != null) {
            rackConfigurations = requestObj.rackdata.rackConfigurations;
        }

        DiscoveredDeviceConfiguration devConfig = new DiscoveredDeviceConfiguration();
        if (StringUtils.isNotEmpty(uiConfig.chassisCredentialId) &&
                StringUtils.isNotEmpty(uiConfig.chassisaddressingmode)) {
            devConfig.setChassisNetworkIdentity(new NetworkIdentity());
            devConfig.getChassisNetworkIdentity().setAddressingMode(
                    AddressingMode.fromLabel(uiConfig.chassisaddressingmode));
            devConfig.getChassisNetworkIdentity().setNetworkId(uiConfig.chassisnetworkid);
            devConfig.setChassisCredentialId(uiConfig.chassisCredentialId);
        }

        if (StringUtils.isNotEmpty(uiConfig.iomCredentialId) &&
                StringUtils.isNotEmpty(uiConfig.iomaddressingmode)) {
            devConfig.setIomNetworkIdentity(new NetworkIdentity());
            devConfig.getIomNetworkIdentity().setAddressingMode(
                    AddressingMode.fromLabel(uiConfig.iomaddressingmode));
            devConfig.getIomNetworkIdentity().setNetworkId(uiConfig.iomnetworkid);
            devConfig.setIomCredentialId(uiConfig.iomCredentialId);
        }

        if (StringUtils.isNotEmpty(uiConfig.bladeCredentialId) &&
                StringUtils.isNotEmpty(uiConfig.serveraddressingmode)) {
            devConfig.setBladeNetworkIdentity(new NetworkIdentity());
            devConfig.getBladeNetworkIdentity().setAddressingMode(
                    AddressingMode.fromLabel(uiConfig.serveraddressingmode));
            devConfig.getBladeNetworkIdentity().setNetworkId(uiConfig.servernetworkid);
            devConfig.setBladeCredentialId(uiConfig.bladeCredentialId);
        }

        if (StringUtils.isNotEmpty(uiConfig.rackCredentialId) &&
                StringUtils.isNotEmpty(uiConfig.rackAddressingMode)) {
            devConfig.setServerNetworkIdentity(new NetworkIdentity());
            devConfig.getServerNetworkIdentity().setAddressingMode(
                    AddressingMode.fromLabel(uiConfig.rackAddressingMode));
            devConfig.getServerNetworkIdentity().setNetworkId(uiConfig.rackNetworkId);
            devConfig.setServerCredentialId(uiConfig.rackCredentialId);
        }

        ConfigurationRequest request = new ConfigurationRequest();
        request.setConfiguration(devConfig);

        List<String> devices = new ArrayList<>();
        request.setDevices(devices);

        if (chassisConfigurations != null) {
            for (UIChassisConfiguration cConfig : chassisConfigurations) {
                if (cConfig.configureDevice) {
                    devices.add(cConfig.chassisConfigurationId);
                }
            }
        }

        if (rackConfigurations != null) {
            for (UIRackConfiguration cConfig : rackConfigurations) {
                if (cConfig.configureDevice) {
                    devices.add(cConfig.rackConfigurationId);
                }
            }
        }

        return request;
    }
}
