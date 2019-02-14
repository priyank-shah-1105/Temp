package com.dell.asm.ui.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import com.dell.asm.asmcore.asmmanager.client.setting.Setting;
import com.dell.asm.rest.common.AsmConstants;
import com.dell.asm.ui.adapter.service.SettingServiceAdapter;
import com.dell.asm.ui.model.UIAboutData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.alcm.client.model.ApplianceHealth;
import com.dell.asm.alcm.client.model.AvailableTimeZones;
import com.dell.asm.alcm.client.model.DHCPSetting;
import com.dell.asm.alcm.client.model.LicenseDetails;
import com.dell.asm.alcm.client.model.NTPSetting;
import com.dell.asm.alcm.client.model.ProxySetting;
import com.dell.asm.alcm.client.model.TestProxyResponse;
import com.dell.asm.alcm.client.model.TimeZoneInfo;
import com.dell.asm.alcm.client.model.WizardStatus;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveredDevices;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveryRequest;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveryStatus;
import com.dell.asm.ui.adapter.service.ASMSetupStatusServiceAdapter;
import com.dell.asm.ui.adapter.service.ApplianceServiceAdapter;
import com.dell.asm.ui.adapter.service.ApplianceTimeZoneServiceAdapter;
import com.dell.asm.ui.adapter.service.DHCPSettingsServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.DiscoverDevicesServiceAdapter;
import com.dell.asm.ui.adapter.service.JobsServiceAdapter;
import com.dell.asm.ui.adapter.service.LicenseServiceAdapter;
import com.dell.asm.ui.adapter.service.NTPSettingsServiceAdapter;
import com.dell.asm.ui.adapter.service.ProxySettingsServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.model.ErrorObj;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.UIGettingStartedData;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.UIStorageUtilization;
import com.dell.asm.ui.model.appliance.JobUpdateDhcpRequest;
import com.dell.asm.ui.model.appliance.UIDhcpData;
import com.dell.asm.ui.model.initialsetup.JobGettingStartedRequest;
import com.dell.asm.ui.model.initialsetup.JobInitialSetupDataRequest;
import com.dell.asm.ui.model.initialsetup.JobProxyDataRequest;
import com.dell.asm.ui.model.initialsetup.JobTimeDataRequest;
import com.dell.asm.ui.model.initialsetup.UIIntialSetupData;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.jraf.client.jobmgr.JrafJobInfo;

/**
 * The Class UtilityController.
 */
@RestController
@RequestMapping(value = "/initialsetup/")
public class InitialSetupController extends BaseController {

    /** The Constant log. */
    private static final Logger log = Logger.getLogger(InitialSetupController.class);

    /** The appliance service adapter. */
    private ApplianceServiceAdapter applianceServiceAdapter;

    /** The appliance time zone service adapter. */
    private ApplianceTimeZoneServiceAdapter applianceTimeZoneServiceAdapter;

    /** The proxy settings service adapter. */
    private ProxySettingsServiceAdapter proxySettingsServiceAdapter;

    /** The ntp settings service adapter. */
    private NTPSettingsServiceAdapter ntpSettingsServiceAdapter;

    /** The asm setup status service adapter. */
    private ASMSetupStatusServiceAdapter asmSetupStatusServiceAdapter;

    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;

    private LicenseServiceAdapter licenseServiceAdapter;

    private DiscoverDevicesServiceAdapter discoverDevicesServiceAdapter;

    private DHCPSettingsServiceAdapter dhcpSettingsServiceAdapter;

    private JobsServiceAdapter jobServiceAdapter;

    private SettingServiceAdapter settingServiceAdapter;

    @Autowired
    public InitialSetupController(ApplianceServiceAdapter applianceServiceAdapter,
                                  ApplianceTimeZoneServiceAdapter applianceTimeZoneServiceAdapter,
                                  ProxySettingsServiceAdapter proxySettingsServiceAdapter,
                                  NTPSettingsServiceAdapter ntpSettingsServiceAdapter,
                                  ASMSetupStatusServiceAdapter asmSetupStatusServiceAdapter,
                                  DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                                  LicenseServiceAdapter licenseServiceAdapter,
                                  DiscoverDevicesServiceAdapter discoverDevicesServiceAdapter,
                                  DHCPSettingsServiceAdapter dhcpSettingsServiceAdapter,
                                  JobsServiceAdapter jobServiceAdapter,
                                  SettingServiceAdapter settingServiceAdapter) {
        this.applianceServiceAdapter = applianceServiceAdapter;
        this.applianceTimeZoneServiceAdapter = applianceTimeZoneServiceAdapter;
        this.proxySettingsServiceAdapter = proxySettingsServiceAdapter;
        this.ntpSettingsServiceAdapter = ntpSettingsServiceAdapter;
        this.asmSetupStatusServiceAdapter = asmSetupStatusServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.licenseServiceAdapter = licenseServiceAdapter;
        this.discoverDevicesServiceAdapter = discoverDevicesServiceAdapter;
        this.dhcpSettingsServiceAdapter = dhcpSettingsServiceAdapter;
        this.jobServiceAdapter = jobServiceAdapter;
        this.settingServiceAdapter = settingServiceAdapter;
    }

    /**
     * Gets the initial setup data.
     *
     * @return the initial setup data
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getinitialsetupdata", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getInitialSetupData() {
        JobResponse jobResponse = new JobResponse();
        UIIntialSetupData responseObj;
        responseObj = new UIIntialSetupData();

        // Get the current timeZone of the appliance.
        try {
            TimeZoneInfo tz = applianceTimeZoneServiceAdapter.getTimeZone();
            if (tz != null) {
                if (tz.getTimeZoneId() == null) {
                    responseObj.timeData.timeZone = "11"; // default ( CST )
                } else {
                    responseObj.timeData.timeZone = tz.getTimeZoneId();
                }
            }
        } catch (Throwable t) {
            log.error("getTimeZones() - An Exception occurred in the timezone service call", t);
        }

        // Get the current NTP settings.
        try {
            NTPSetting ntp = ntpSettingsServiceAdapter.getNTPSettings();
            if (ntp != null) {
                if (ntp.getPreferredNTPServer() != null && ntp.getPreferredNTPServer().length() > 0) {
                    responseObj.timeData.enableNTPServer = true;
                    responseObj.timeData.preferredNTPServer = ntp.getPreferredNTPServer();
                    if (ntp.getSecondaryNTPServer() != null)
                        responseObj.timeData.secondaryNTPServer = ntp.getSecondaryNTPServer();
                }
            }
        } catch (Throwable t) {
            log.error("getTimeZones() - An Exception occurred in the ntp settings service call", t);
        }

        // Get the current Proxy settings.
        try {
            ProxySetting proxy = proxySettingsServiceAdapter.getProxySettings();
            if (proxy != null) {
                responseObj.proxyData.enableProxy = proxy.isEnabled();
                if (proxy.getProxyServer() != null) {
                    responseObj.proxyData.proxyServer = proxy.getProxyServer();
                }
                if (proxy.getPort() != null) {
                    responseObj.proxyData.proxyPort = Integer.parseInt(proxy.getPort());
                }

                responseObj.proxyData.enableProxyCredentials = proxy.isUserCredentialEnabled();
                if (proxy.isUserCredentialEnabled()) {
                    responseObj.proxyData.proxyUsername = proxy.getUserName();
                    if (StringUtils.isNotEmpty(proxy.getPassword())) {
                        responseObj.proxyData.proxyPassword = proxy.getPassword();
                        responseObj.proxyData.proxyVerifyPassword = proxy.getPassword();
                    } else {
                        responseObj.proxyData.proxyPassword = null;
                        responseObj.proxyData.proxyVerifyPassword = null;
                    }
                } else {
                    responseObj.proxyData.proxyUsername = null;
                    responseObj.proxyData.proxyPassword = null;
                    responseObj.proxyData.proxyVerifyPassword = null;
                }
            }
        } catch (Throwable t) {
            log.error("getTimeZones() - An Exception occurred in the proxy settings service call",
                      t);
        }

        try {
            LicenseDetails ld = licenseServiceAdapter.getLicenseDetails();
            if (ld != null) {
                responseObj.licenseData = ApplianceController.parseLicense(ld, 0);
            }
        } catch (Throwable t) {
            log.error(
                    "getLicenseDetails() - An Exception occurred in the proxy settings service call",
                    t);
        }

        // service tag
        Setting setting = settingServiceAdapter.getSettingByName(
                UIAboutData.ASM_SERVICE_TAG_SETTING);
        if (setting != null) {
            responseObj.licenseData.softwareservicetag = setting.getValue();
        }

        responseObj.dhcpData = new UIDhcpData();
        DHCPSetting dhcpSettings = dhcpSettingsServiceAdapter.getDHCPSettings();
        if (dhcpSettings != null) {
            responseObj.dhcpData = new UIDhcpData(dhcpSettings);
        } else {
            responseObj.dhcpData = new UIDhcpData();
        }

        try {
            WizardStatus status = asmSetupStatusServiceAdapter.getASMSetupStatus();
            if (status != null) {
                if (status.getSeqId() != null)
                    responseObj.currentWizardStep = status.getSeqId().intValue();
                responseObj.initialSetupComplete = status.getIsSetupCompleted();
            }
        } catch (Throwable t) {
            log.error("getTimeZones() - An Exception occurred in the ntp settings service call", t);
        }
        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Gets the time zones.
     *
     * @return the time zones
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "gettimezones", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getTimeZones() {
        JobResponse jobResponse = new JobResponse();
        List<UIListItem> responseObj = new ArrayList<>();
        try {
            AvailableTimeZones timezones = applianceTimeZoneServiceAdapter.getAvailableTimeZones();
            if (timezones != null && !timezones.getAvailableTimeZones().isEmpty()) {
                for (TimeZoneInfo tz : timezones.getAvailableTimeZones()) {
                    responseObj.add(new UIListItem(tz.getTimeZoneId(), tz.getTimeZone()));
                }
            }
        } catch (Throwable t) {
            log.error("getTimeZones() - An Exception occurred in the service call", t);
            return addFailureResponseInfo(t);
        }
        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Update time.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "updatetime", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateTime(@RequestBody JobTimeDataRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            TimeZoneInfo tz = new TimeZoneInfo();
            tz.setTimeZoneId(request.requestObj.timeZone);
            applianceTimeZoneServiceAdapter.setTimeZone(tz);

            if (request.requestObj.enableNTPServer) {
                NTPSetting ntp = new NTPSetting();
                ntp.setPreferredNTPServer(request.requestObj.preferredNTPServer);
                if (request.requestObj.secondaryNTPServer != null)
                    ntp.setSecondaryNTPServer(request.requestObj.secondaryNTPServer);
                ntpSettingsServiceAdapter.setNTPSettings(ntp);
            } else {
                ntpSettingsServiceAdapter.deleteNTPSettings();
            }

            WizardStatus status = asmSetupStatusServiceAdapter.getASMSetupStatus();
            //status.setSeqId(Long.parseLong(Integer.toString(request.requestObj.currentWizardStep)));
            status.setIsSetupCompleted(false);
            asmSetupStatusServiceAdapter.setASMSetupStatus(status);
        } catch (Throwable t) {
            log.error("updateTime() - An Exception occurred in the service call", t);
            return addFailureResponseInfo(t);
        }
        jobResponse.responseObj = request.requestObj;
        return jobResponse;
    }

    /**
     * Test proxy settings.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "testproxysettings", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse testProxySettings(@RequestBody JobProxyDataRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            ProxySetting proxy = null;
            if (request.requestObj != null) {
                proxy = MappingUtils.createProxySetting(request.requestObj);
            }

            TestProxyResponse testProxyResponse;
            if (proxy != null) {
                testProxyResponse = proxySettingsServiceAdapter.testProxySettings(proxy);
                if (testProxyResponse != null) {
                    if (testProxyResponse.getTestProxyResult() != null && testProxyResponse.getTestProxyResult()) {
                        log.info("Test Proxy success");
                        jobResponse.responseObj = request.requestObj;
                    } else {
                        log.info("Test Proxy failed");
                        ErrorObj errObj = new ErrorObj();
                        errObj.errorMessage = testProxyResponse.getTestProxyDescription();
                        jobResponse.responseCode = -1;
                        jobResponse.errorObj = errObj;
                    }
                } else {
                    log.info("Test Proxy returned NULL obj");
                    ErrorObj errObj = new ErrorObj();
                    errObj.errorMessage = "Unknown Error";
                    jobResponse.responseCode = -1;
                    jobResponse.errorObj = errObj;
                }
            }
        } catch (Throwable t) {
            log.error("getTimeZones() - An Exception occurred in the proxy settings service call",
                      t);
            return addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Update proxy.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "updateproxy", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateProxy(@RequestBody JobProxyDataRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            ProxySetting proxy = null;
            if (request.requestObj != null) {
                proxy = MappingUtils.createProxySetting(request.requestObj);
            }
            proxySettingsServiceAdapter.setProxySettings(proxy);

            WizardStatus status = asmSetupStatusServiceAdapter.getASMSetupStatus();
            //status.setSeqId(Long.parseLong(Integer.toString(request.requestObj.currentWizardStep)));
            status.setIsSetupCompleted(false);
            asmSetupStatusServiceAdapter.setASMSetupStatus(status);
        } catch (Throwable t) {
            log.error("getTimeZones() - An Exception occurred in the proxy settings service call",
                      t);
            return addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Complete initial setup.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "completeinitialsetup", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse completeInitialSetup(@RequestBody JobInitialSetupDataRequest request) {
        JobResponse jobResponse = new JobResponse();

        WizardStatus status = asmSetupStatusServiceAdapter.getASMSetupStatus();
        status.setIsSetupCompleted(true);
        asmSetupStatusServiceAdapter.setASMSetupStatus(status);

        return jobResponse;
    }

    /**
     * Getting started.
     *
     * @return GettingStartedData
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "gettingstarted", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse gettingStarted(@RequestBody JobGettingStartedRequest request) {
        JobResponse jobResponse = new JobResponse();
        UIGettingStartedData responseObj = new UIGettingStartedData();
        try {
            WizardStatus wizardStatus = asmSetupStatusServiceAdapter.getASMSetupStatus();

            if (wizardStatus != null) {
                responseObj.initialSetupCompleted = wizardStatus.getIsSetupCompleted();
                responseObj.configurationCompleted = wizardStatus.getIsConfigureCompleted();
                responseObj.networksCompleted = wizardStatus.getIsNetworkCompleted();
                responseObj.showgettingstarted = wizardStatus.getShowGettingStarted();
                responseObj.templateCompleted = wizardStatus.getIsTemplateCompleted();
                responseObj.secureRemoteServicesConfigured = wizardStatus.getSRSConfigured();
                responseObj.srsOrPhoneHomeConfigured = wizardStatus.getSrsOrPhoneHomeConfigured();
                // banner makes sense only for admin as readonly user can't download RCM and standard user does not
                // even have an access to settings page
                if (AsmConstants.USERROLE_ADMINISTRATOR.equals(LogsController.getLoggedUserRole())) {
                    responseObj.rcmVersionAlert = wizardStatus.getNewRCMAvailable();
                    responseObj.newVirtualApplianceVersionAlert = wizardStatus.getNewApplianceVersionAvailable();
                }
            }

            // appliance health
            ApplianceHealth health = applianceServiceAdapter.getApplianceHealth();

            UIStorageUtilization storageUtilization = new UIStorageUtilization();
            storageUtilization.setPercentageUsed(health.getFullestPartitionUtilization());
            storageUtilization.setPartitionName(health.getFullestPartitionName());
            responseObj.storageUtilization = storageUtilization;

            boolean pendingDiscoveryRecords = false;
            try {
                DiscoveryRequest[] dList = discoverDevicesServiceAdapter.getDiscoveryRequests(null,
                                                                                              null,
                                                                                              null,
                                                                                              null);
                if (dList != null && dList.length > 0) {
                    for (DiscoveryRequest dr : dList) {
                        if (dr.getDevices() != null) {
                            for (DiscoveredDevices dd : dr.getDevices()) {
                                if (dr.getStatus() == DiscoveryStatus.FAILED ||
                                        dr.getStatus() == DiscoveryStatus.ERROR ||
                                        dr.getStatus() == DiscoveryStatus.UNSUPPORTED) {
                                    responseObj.errorsResources++;
                                } else if (dr.getStatus() == DiscoveryStatus.PENDING ||
                                        dr.getStatus() == DiscoveryStatus.INPROGRESS) {
                                    responseObj.pendingResources++;
                                }
                            }
                        } else {
                            if (dr.getDiscoveryRequestList() != null &&
                                    CollectionUtils.isNotEmpty(
                                            dr.getDiscoveryRequestList().getDiscoverIpRangeDeviceRequests()) &&
                                    (DiscoveryStatus.INPROGRESS.equals(dr.getStatus()) ||
                                            DiscoveryStatus.PENDING.equals(dr.getStatus()))) {
                                responseObj.pendingResources += dr.getDiscoveryRequestList().getDiscoverIpRangeDeviceRequests().size();
                                pendingDiscoveryRecords = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // sometimes jobs API throws exception when job is completed.
                log.warn("gettingStarted getDiscoveryRequests returned error", e);
            }

            // jobs in progress
            responseObj.initialConfigurationCompleted = true;
            responseObj.firmwareUpdateCompleted = true;
            responseObj.inventoryUpdateCompleted = true;
            responseObj.deploymentCompleted = true;
            boolean discoveryJobInProgress = false;

            try {
                ResourceList<JrafJobInfo> jobs = jobServiceAdapter.getJobs(null, null, null, null);
                if (jobs != null) {
                    for (JrafJobInfo job : jobs.getList()) {
                        // consider only started jobs
                        if (job.getStartDate().before(new Date())) {
                            switch (job.getJobKey().getGroup()) {
                            case "InitialConfigurationJob":
                            case "DeviceConfigurationJob":
                                responseObj.initialConfigurationCompleted = false;
                                break;
                            case "FirmwareUpdateJob":
                                responseObj.firmwareUpdateCompleted = false;
                                break;
                            case "DeviceInventoryJob":
                                responseObj.inventoryUpdateCompleted = false;
                                break;
                            case "DiscoverIpRangeJob":
                                discoveryJobInProgress = true;
                                break;
                            case "ServiceDeploymentJob":
                                responseObj.deploymentCompleted = false;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // sometimes jobs API throws exception when job is completed.
                log.warn("gettingStarted getJobs returned error", e);
            }

            // there will be records left in discovery tables when user never went past finish screen in UI wizard
            // ignore these stale records when no actual discovery job detected
            if (pendingDiscoveryRecords && !discoveryJobInProgress) {
                responseObj.pendingResources = 0;
            }

            responseObj.discoveredResources = deviceInventoryServiceAdapter.getTotalCount(null);
            if (responseObj.discoveredResources > 0 && responseObj.pendingResources == 0) {
                responseObj.discoveryCompleted = true;
            }


        } catch (Throwable t) {
            log.error("gettingStarted error:", t);
            return addFailureResponseInfo(t);
        }
        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Updating getting started attributes.
     *
     * @return GettingStartedData
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "updategettingstarted", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateGettingStarted(@RequestBody JobGettingStartedRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            WizardStatus status = asmSetupStatusServiceAdapter.getASMSetupStatus();
            status.setIsSetupCompleted(request.requestObj.initialSetupCompleted);
            status.setShowGettingStarted(request.requestObj.showgettingstarted);
            status.setIsConfigureCompleted(request.requestObj.configurationCompleted);

            asmSetupStatusServiceAdapter.setASMSetupStatus(status);

            log.debug("updateGettingStarted() - Completed updating the gettingstarted status");
        } catch (Throwable t) {
            log.error(
                    "updateGettingStarted() - An Exception occurred in the gettingstarted settings service call",
                    t);
            return addFailureResponseInfo(t);
        }

        return jobResponse;

    }

    /**
     * Updating getting started attributes.
     *
     * @return GettingStartedData
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "updatedhcp", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateDhcpData(@RequestBody JobUpdateDhcpRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            jobResponse.responseObj = request.requestObj;
            dhcpSettingsServiceAdapter.setDHCPSettings(
                    request.requestObj.createDhcpSettingFromUiData());


        } catch (Throwable t) {
            log.error("updateDhcpData() - An Exception occurred in the service call", t);
            return addFailureResponseInfo(t);
        }

        return jobResponse;

    }


}
