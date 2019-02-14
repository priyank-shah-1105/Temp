package com.dell.asm.ui.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import com.dell.asm.alcm.client.model.UpgradeStatus;
import com.dell.asm.asmcore.asmmanager.client.troubleshootingbundle.TroubleshootingBundleParams;
import com.dell.asm.ui.model.troubleshootingbundle.JobTroubleshootingBundleRequest;
import com.dell.asm.ui.model.troubleshootingbundle.UITroubleshootingBundle;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dell.asm.alcm.client.model.ASMVersion;
import com.dell.asm.alcm.client.model.Appliance;
import com.dell.asm.alcm.client.model.ApplianceSetting;
import com.dell.asm.alcm.client.model.ApplianceSettings;
import com.dell.asm.alcm.client.model.CertificateInfo;
import com.dell.asm.alcm.client.model.DHCPSetting;
import com.dell.asm.alcm.client.model.LicenseDetails;
import com.dell.asm.alcm.client.model.ProxySetting;
import com.dell.asm.alcm.client.model.SSLCertUploadRequest;
import com.dell.asm.alcm.client.model.SSLCertificateInfo;
import com.dell.asm.alcm.client.model.SSLcertificateResponse;
import com.dell.asm.alcm.client.model.TroubleshootingBundleResponse;
import com.dell.asm.alcm.client.model.UploadLicenseRequest;
import com.dell.asm.alcm.client.model.WizardStatus;
import com.dell.asm.alcm.client.util.ApplianceUtil;
import com.dell.asm.alcm.client.util.YumRepoUtil;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.asmcore.asmmanager.client.setting.Setting;
import com.dell.asm.asmcore.asmmanager.client.srsconnector.SRSConnectorSettings;
import com.dell.asm.asmcore.asmmanager.client.srsconnector.AlertConnectorConnectionTypes;
import com.dell.asm.libext.tomcat.AsmManagerInitLifecycleListener;
import com.dell.asm.libext.tomcat.ContainerLifecycleListener;
import com.dell.asm.ui.adapter.service.ASMSetupStatusServiceAdapter;
import com.dell.asm.ui.adapter.service.ApplianceCertificateServiceAdapter;
import com.dell.asm.ui.adapter.service.ApplianceInfoServiceAdapter;
import com.dell.asm.ui.adapter.service.ApplianceServiceAdapter;
import com.dell.asm.ui.adapter.service.ApplianceTroubleshootingBundleServiceAdapter;
import com.dell.asm.ui.adapter.service.VxfmTroubleshootingBundleServiceAdapter;
import com.dell.asm.ui.adapter.service.DHCPSettingsServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.JobsServiceAdapter;
import com.dell.asm.ui.adapter.service.LicenseServiceAdapter;
import com.dell.asm.ui.adapter.service.ProxySettingsServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.SRSConnectorAdapter;
import com.dell.asm.ui.adapter.service.SettingServiceAdapter;
import com.dell.asm.ui.authorization.ASMUIAuthenticationProvider;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.model.CriteriaObj;
import com.dell.asm.ui.model.FilterObj;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringRequest;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIAboutData;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.UILoginResult;
import com.dell.asm.ui.model.appliance.JobApplianceCertificateInfoRequest;
import com.dell.asm.ui.model.appliance.JobApplianceUpdateRequest;
import com.dell.asm.ui.model.appliance.JobUpdatePortDataRequest;
import com.dell.asm.ui.model.appliance.JobUpdateVirtualApplianceRequest;
import com.dell.asm.ui.model.appliance.UIApplianceCertificateInfo;
import com.dell.asm.ui.model.appliance.UIApplianceUpgradeSettings;
import com.dell.asm.ui.model.appliance.UICertificateInfo;
import com.dell.asm.ui.model.appliance.UICurrentUsersAndJobs;
import com.dell.asm.ui.model.appliance.UIDhcpData;
import com.dell.asm.ui.model.appliance.UIPortData;
import com.dell.asm.ui.model.initialsetup.JobLicenseDataRequest;
import com.dell.asm.ui.model.initialsetup.JobProxyDataRequest;
import com.dell.asm.ui.model.initialsetup.UILicenseData;
import com.dell.asm.ui.model.initialsetup.UIProxyData;
import com.dell.asm.ui.model.srsconnector.JobSRSConnectorRegistrationRequest;
import com.dell.asm.ui.model.srsconnector.UIVxRackFlexSRSConnector;
import com.dell.asm.ui.model.srsconnector.JobAlertConnectorDeregistrationRequest;
import com.dell.asm.ui.spring.ASMUserDetails;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.asm.repositorymgr.utilities.RepositoryConfigUtil;
import com.dell.pg.asm.repositorymgr.utilities.RepositoryConfigUtil.UpgradeReleaseInfo;
import com.dell.pg.jraf.client.jobmgr.JrafJobInfo;


/**
 * The Class UtilityController.
 */
@RestController
@RequestMapping(value = "/appliance/")
public class ApplianceController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(ApplianceController.class);

    private static final String DEVICETYPE_COLUMN = "deviceType";
    private static final String OPER_EQ = "eq";
    private static final String TroubleShootingFileName = "Dell_EMC_VxFM_Logs";
    private static final String TroubleShootingFileName_DatePattern = "YYYYMMdd_HHmm";
    private static final String TroubleShootingFileExtension = ".zip";
    private static final String ASM_PORTS_TO_PING = "portsToPing";

    private final Map<String, SSLCertUploadRequest> m_certificateContent = new HashMap<>();
    private final Map<String, ASMLicense> m_licenseContent = new HashMap<>();

    private ApplianceCertificateServiceAdapter applianceCertificateServiceAdapter;
    private ApplianceTroubleshootingBundleServiceAdapter applianceTroubleshootingBundleServiceAdapter;
    private VxfmTroubleshootingBundleServiceAdapter vxfmTroubleshootingBundleServiceAdapter;
    private ProxySettingsServiceAdapter proxySettingsServiceAdapter;
    private LicenseServiceAdapter licenseServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private DHCPSettingsServiceAdapter dhcpSettingsServiceAdapter;
    private ApplianceServiceAdapter applianceServiceAdapter;
    private ApplianceInfoServiceAdapter applianceInfoServiceAdapter;
    private SettingServiceAdapter settingServiceAdapter;
    private SRSConnectorAdapter srsConnectorAdapter;
    private JobsServiceAdapter jobsServiceAdapter;
    private ASMSetupStatusServiceAdapter asmSetupStatusServiceAdapter;

    /** The session registry.@Resource(name = "sessionRegistry") */
    private SessionRegistry sessionRegistry;
    private String m_certificateSigReq = null;

    @Autowired
    public ApplianceController(
            ApplianceCertificateServiceAdapter applianceCertificateServiceAdapter,
            ApplianceTroubleshootingBundleServiceAdapter applianceTroubleshootingBundleServiceAdapter,
            VxfmTroubleshootingBundleServiceAdapter vxfmTroubleshootingBundleServiceAdapter,
            ProxySettingsServiceAdapter proxySettingsServiceAdapter,
            LicenseServiceAdapter licenseServiceAdapter,
            DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
            DHCPSettingsServiceAdapter dhcpSettingsServiceAdapter,
            ApplianceServiceAdapter applianceServiceAdapter,
            ApplianceInfoServiceAdapter applianceInfoServiceAdapter,
            SettingServiceAdapter settingServiceAdapter,
            SRSConnectorAdapter srsConnectorAdapter,
            JobsServiceAdapter jobsServiceAdapter,
            ASMSetupStatusServiceAdapter asmSetupStatusServiceAdapter,
            SessionRegistry sessionRegistry) {
        this.applianceCertificateServiceAdapter = applianceCertificateServiceAdapter;
        this.applianceTroubleshootingBundleServiceAdapter = applianceTroubleshootingBundleServiceAdapter;
        this.vxfmTroubleshootingBundleServiceAdapter = vxfmTroubleshootingBundleServiceAdapter;
        this.proxySettingsServiceAdapter = proxySettingsServiceAdapter;
        this.licenseServiceAdapter = licenseServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.dhcpSettingsServiceAdapter = dhcpSettingsServiceAdapter;
        this.applianceServiceAdapter = applianceServiceAdapter;
        this.applianceInfoServiceAdapter = applianceInfoServiceAdapter;
        this.settingServiceAdapter = settingServiceAdapter;
        this.srsConnectorAdapter = srsConnectorAdapter;
        this.jobsServiceAdapter = jobsServiceAdapter;
        this.sessionRegistry = sessionRegistry;
        this.asmSetupStatusServiceAdapter = asmSetupStatusServiceAdapter;
    }

    public static UILicenseData parseLicense(LicenseDetails lic, Integer serverCount) {
        UILicenseData license = new UILicenseData();

        Calendar now = GregorianCalendar.getInstance();
        license.isValid = false;

        if (lic.getActivationDate() != null) {
            license.activationdate = MappingUtils.getDate(
                    lic.getActivationDate().toGregorianCalendar());
            license.isValid = lic.getActivationDate().toGregorianCalendar().compareTo(now) <= 0;
        }

        license.force = false;
        license.totalnodes = lic.getNodes();
        license.type = lic.getType();
        license.signature = lic.getSignature();
        license.usednodes = serverCount;
        license.availablenodes = lic.getNodes() - serverCount;

        if (lic.getExpirationDate() != null) {
            license.expirationdate = MappingUtils.getTime(
                    lic.getExpirationDate().toGregorianCalendar());
            license.expired = lic.getExpirationDate().toGregorianCalendar().compareTo(now) >= 0;
        } else {
            license.expired = false;
        }

        return license;
    }

    /**
     * Sets the appliance certificate info.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "setappliancecertificateinfo", method = RequestMethod.POST)
    public JobResponse setApplianceCertificateInfo(
            @RequestBody JobApplianceCertificateInfoRequest request) {
        JobResponse jobResponse = new JobResponse();
        UIApplianceCertificateInfo responseObj;
        try {
            SSLcertificateResponse response = applianceCertificateServiceAdapter.createCSR(
                    createCSR(request.requestObj));
            responseObj = request.requestObj;
            if (response != null) {
                responseObj.downloadCSRDisplay = true;
                String url = response.getCsrURL();
                // load file content
                responseObj.certificateSigReq = getCSRFile(url);
                m_certificateSigReq = responseObj.certificateSigReq;
            }
            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("setApplianceCertificateInfo() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the appliance certificate info.
     *
     * @return the appliance certificate info
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getappliancecertificateinfo", method = RequestMethod.POST)
    public JobResponse getApplianceCertificateInfo() {

        JobResponse jobResponse = new JobResponse();
        UIApplianceCertificateInfo responseObj = new UIApplianceCertificateInfo();
        try {
            SSLCertificateInfo certInfo = applianceCertificateServiceAdapter.getCertificateInfo();
            if (certInfo != null) {
                responseObj.issuedBy = certInfo.getIssuedBy();
                responseObj.issuedTo = certInfo.getIssuedTo();
                responseObj.validFrom = certInfo.getValidFrom();
                responseObj.validTo = certInfo.getValidTo();
                responseObj.distinguishedName = certInfo.getSubject();
            }

            if (m_certificateSigReq != null) {
                responseObj.certificateSigReq = m_certificateSigReq;
                responseObj.downloadCSRDisplay = true;
            }
            jobResponse.responseObj = responseObj;

        } catch (Throwable t) {
            log.error("getApplianceCertificateInfo() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
            if (jobResponse.errorObj.fldErrors != null && jobResponse.errorObj.fldErrors.size() > 0
                    && "ALCM_DISPLAY_CERTIFICATE_030".equals(
                    jobResponse.errorObj.fldErrors.get(0).errorCode)) {
                // ignore this error - no certificate
                jobResponse = new JobResponse();
                jobResponse.responseObj = responseObj;
            }
        }
        return jobResponse;
    }

    @RequestMapping(value = "setapplianceupgradesettings", method = RequestMethod.POST)
    public JobResponse setApplianceUpgradeSettings(@RequestBody JobApplianceUpdateRequest request) {
        JobResponse jobResponse = new JobResponse();

        try {
            final UIApplianceUpgradeSettings requestObj = request.requestObj;
            boolean fromSRS = false;
            if (ApplianceSetting.APPLIANCE_SETTING_REPO_CHOICE_ENUM.remoteservice.name().equals(requestObj.source)) {
                fromSRS = true;
            }

            if (fromSRS || isValidYumRepo(requestObj.repositoryPath)) {
                // Will throw exception if invalid repository location
                updateRepository(requestObj.repositoryPath, requestObj.source);
            }
        } catch (Throwable t) {
            log.error("setApplianceUpdate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private void updateRepository(String path, String source) {
        final ApplianceSettings applianceSettings = new ApplianceSettings();
        final List<ApplianceSetting> applianceSettingList = new ArrayList<>();

        ApplianceSetting applianceSetting;

        if (path != null) {
            applianceSetting =
                    ApplianceUtil.findApplianceSettingByName(
                            applianceInfoServiceAdapter.getApplianceSettings(),
                            ApplianceSetting.APPLIANCE_SETTING_REPO_LOCATION);
            if (applianceSetting == null) {
                applianceSetting = new ApplianceSetting();
                applianceSetting.setName(ApplianceSetting.APPLIANCE_SETTING_REPO_LOCATION);
                applianceSetting.setValue(path);
            } else {
                applianceSetting.setValue(path);
            }
            applianceSettingList.add(applianceSetting);
        }

        if (source != null) {
            applianceSetting =
                    ApplianceUtil.findApplianceSettingByName(
                            applianceInfoServiceAdapter.getApplianceSettings(),
                            ApplianceSetting.APPLIANCE_SETTING_REPO_CHOICE);
            if (applianceSetting == null) {
                applianceSetting = new ApplianceSetting();
                applianceSetting.setName(ApplianceSetting.APPLIANCE_SETTING_REPO_CHOICE);
                applianceSetting.setValue(source);
            } else {
                applianceSetting.setValue(source);
            }
            applianceSettingList.add(applianceSetting);
        }

        applianceSettings.setApplianceSettings(applianceSettingList);
        applianceInfoServiceAdapter.updateApplianceSettings(applianceSettings);
    }

    /**
     * Gets the appliance upgrade settings.
     *
     * @return the appliance update info
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getapplianceupgradesettings", method = RequestMethod.POST)
    public JobResponse getApplianceUpgradeSettings() {

        JobResponse jobResponse = new JobResponse();
        UIApplianceUpgradeSettings responseObj = new UIApplianceUpgradeSettings();
        try {
            jobResponse.responseObj = responseObj;

            final Appliance appliance = applianceInfoServiceAdapter.getAppliance();

            final ASMVersion asmversion = appliance.getAsmVersion();
            responseObj.currentVersion = asmversion.getVersion();
            if (!asmversion.getBuildNumber().isEmpty()) {
                responseObj.currentVersion += "." + asmversion.getBuildNumber();
            }

            // this can't be null, ALCM checks on startup and insert the value
            final ApplianceSetting repoLocationSetting =
                    ApplianceUtil.findApplianceSettingByName(appliance.getApplianceSettings(),
                            ApplianceSetting.APPLIANCE_SETTING_REPO_LOCATION);

            // this can't be null, ALCM checks on startup and insert the value
            final ApplianceSetting repoLocationRemoteSetting =
                    ApplianceUtil.findApplianceSettingByName(appliance.getApplianceSettings(),
                            ApplianceSetting.APPLIANCE_SETTING_REPO_LOCATION_REMOTE);

            // this can't be null, ALCM checks on startup and insert the value
            final ApplianceSetting repoChoiceSetting =
                    ApplianceUtil.findApplianceSettingByName(appliance.getApplianceSettings(),
                            ApplianceSetting.APPLIANCE_SETTING_REPO_CHOICE);

            boolean fromSRS = ApplianceSetting.APPLIANCE_SETTING_REPO_CHOICE_ENUM.remoteservice.name().equals(repoChoiceSetting.getValue());

            responseObj.source = repoChoiceSetting.getValue();
            // preserve "local" repositoryPath, UI will substitute it with STS text when needed
            if (StringUtils.isNotEmpty(repoLocationSetting.getValue())) {
                responseObj.repositoryPath = repoLocationSetting.getValue(); // this is real URL, ESRS or another http
            } else {
                responseObj.repositoryPath = ApplianceSetting.APPLIANCE_SETTING_REPO_LOCATION_DEFAULT;
            }

            String repoURL = "";
            if (fromSRS) {
                repoURL = repoLocationRemoteSetting.getValue();
                responseObj.releaseNotesLink = getApplicationContext().getMessage("RELEASE_NOTES_URL", null,
                        LocaleContextHolder.getLocale());
            } else {
                repoURL = responseObj.repositoryPath;
            }

            // based on URL, either download repo XML or get the version from file name
            final UpgradeReleaseInfo upgradeReleaseInfo =
                    RepositoryConfigUtil.getUpgradeReleaseInfo(repoURL);
            responseObj.availableVersion = upgradeReleaseInfo.getReleaseVersion();
            if (StringUtils.isNotEmpty(upgradeReleaseInfo.getReleaseNotesLink())) {
                responseObj.releaseNotesLink = upgradeReleaseInfo.getReleaseNotesLink();
            }

        } catch (Throwable t) {
            log.error("getApplianceUpgradeSettings() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the current users and jobs.
     *
     * @return the current users and jobs
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getcurrentusersandjobs", method = RequestMethod.POST)
    public JobResponse getCurrentUsersAndJobs() {

        JobResponse jobResponse = new JobResponse();
        UICurrentUsersAndJobs responseObj = new UICurrentUsersAndJobs();
        try {
            List<Object> principals = null;
            if (sessionRegistry != null) {
                principals = sessionRegistry.getAllPrincipals();
            }
            if ((principals != null) && !principals.isEmpty()) {
                for (Object principal : principals) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal,
                                                                                       false);
                    for (SessionInformation session : sessions) {
                        if (!session.isExpired()) {
                            responseObj.currentusers++;
                        }
                    }
                }
            }
            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("getCurrentUsersAndJobs() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Update virtual appliance.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "updatevirtualappliance", method = RequestMethod.POST)
    public JobResponse updateVirtualAppliance(@RequestBody JobUpdateVirtualApplianceRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            final ApplianceSettings applianceSettings = applianceInfoServiceAdapter.getApplianceSettings();

            // these settings are ensured to be not null by ASMApp init loop
            final ApplianceSetting repoLocationSetting =
                    ApplianceUtil.findApplianceSettingByName(applianceSettings,
                            ApplianceSetting.APPLIANCE_SETTING_REPO_LOCATION);

            final ApplianceSetting repoChoiceSetting =
                    ApplianceUtil.findApplianceSettingByName(applianceSettings,
                            ApplianceSetting.APPLIANCE_SETTING_REPO_CHOICE);

            WizardStatus wizardStatus = asmSetupStatusServiceAdapter.getASMSetupStatus();
            boolean updateFromSRS = wizardStatus.getSRSConfigured() &&
                    ApplianceSetting.APPLIANCE_SETTING_REPO_CHOICE_ENUM.remoteservice.name().equals(repoChoiceSetting.getValue());

            String repoLocation = null;
            if (ApplianceSetting.APPLIANCE_SETTING_REPO_CHOICE_ENUM.local.name().equals(repoChoiceSetting.getValue())) {
                repoLocation = repoLocationSetting.getValue();
            }

            if (updateFromSRS || isValidYumRepo(repoLocation)) {
                AsmManagerInitLifecycleListener.setStatus(AsmManagerInitLifecycleListener.UPGRADING);
                ASMUIAuthenticationProvider.setSTATUS(AsmManagerInitLifecycleListener.UPGRADING);
                applianceServiceAdapter.updateAppliance(); // this is asynchronous

                // wait 5 seconds. this is enough for ALCM to start upgrade thread (2 seconds delayed) and either get into
                // download process or fail on it, in case connection is broken
                Thread.sleep(5000);

                // now get the status
                UpgradeStatus upgradeStatus = applianceServiceAdapter.getUpgradeStatus();
                if (!upgradeStatus.isUpgrading()) {
                    throw new ControllerException(upgradeStatus.getError());
                }

                ASMUserDetails currentUserDetails = (ASMUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                List<Object> principals = null;
                if (sessionRegistry != null) {
                    principals = sessionRegistry.getAllPrincipals();
                }
                if ((principals != null) && !principals.isEmpty()) {
                    for (Object principal : principals) {
                        ASMUserDetails userDetails = (ASMUserDetails) principal;
                        if (userDetails != null &&
                                userDetails.getUsername() != null &&
                                !userDetails.getUsername().equals(currentUserDetails.getUsername())) {
                            List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal,
                                                                                               false);
                            if ((sessions != null) && !sessions.isEmpty()) {
                                for (SessionInformation session : sessions) {
                                    if (!session.isExpired()) {
                                        session.expireNow();
                                    }
                                    sessionRegistry.removeSessionInformation(session.getSessionId());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            AsmManagerInitLifecycleListener.setStatus(AsmManagerInitLifecycleListener.READY);
            ASMUIAuthenticationProvider.setSTATUS(AsmManagerInitLifecycleListener.READY);
            log.error("updateVirtualAppliance() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "uploadcertificate", method = RequestMethod.POST)
    public JobResponse uploadCertificate(@RequestParam("file") MultipartFile multipartFile,
                                         HttpSession session) {

        JobResponse jobResponse = new JobResponse();
        UICertificateInfo responseObj = new UICertificateInfo();
        try {
            if (multipartFile != null) {
                String content = new String(multipartFile.getBytes(), "ISO-8859-1");
                String sessionId = session.getId();
                synchronized (m_certificateContent) {
                    m_certificateContent.put(sessionId, createUploadCertRequest(content));
                }
                responseObj.content = content;
            }
            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("uploadCertificate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "uploadcertificateconfirmation", method = RequestMethod.POST)
    public JobResponse uploadCertificateConfirmation(HttpSession session) {

        JobResponse jobResponse = new JobResponse();
        try {
            synchronized (m_certificateContent) {
                if (m_certificateContent.containsKey(session.getId())) {
                    SSLCertUploadRequest req = m_certificateContent.get(session.getId());
                    applianceCertificateServiceAdapter.uploadCertificate(req);
                    m_certificateContent.remove(session.getId());
                }
            }

            applianceServiceAdapter.setRestarting(true);
            applianceServiceAdapter.restartAppliance(); // this is asynchronous, executes in a few seconds

            log.debug("doLogout() - Attempting to Logout");
            session.invalidate();
            SecurityContextHolder.clearContext();
            SecurityContextHolder.createEmptyContext();
            UILoginResult loginResult = new UILoginResult();
            loginResult.route = "";
            loginResult.success = true;
            loginResult.url = "login.html";
            jobResponse.responseObj = loginResult;

        } catch (Throwable t) {
            log.error("uploadCertificateConfirmation() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the available countries.
     *
     * @return the available countries
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getavailablecountries", method = RequestMethod.POST)
    public JobResponse getAvailableCountries() {

        JobResponse jobResponse = new JobResponse();
        List<UIListItem> responseObj = new ArrayList<>();
        try {
            Locale locale = LocaleContextHolder.getLocale();
            responseObj.add(new UIListItem("AF",
                                           getApplicationContext().getMessage("Afghanistan", null,
                                                                              locale)));
            responseObj.add(new UIListItem("AX",
                                           this.getApplicationContext().getMessage("AlandIslands",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("AL",
                                           this.getApplicationContext().getMessage("Albania", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("DZ",
                                           this.getApplicationContext().getMessage("Algeria", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("AS",
                                           this.getApplicationContext().getMessage("AmericanSamoa",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("AD",
                                           this.getApplicationContext().getMessage("Andorra", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("AO",
                                           this.getApplicationContext().getMessage("Angola", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("AI",
                                           this.getApplicationContext().getMessage("Aguilla", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("AQ",
                                           this.getApplicationContext().getMessage("Antarctica",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("AG", this.getApplicationContext().getMessage(
                    "AntiguaAndBarbuda", null, locale)));
            responseObj.add(new UIListItem("AR",
                                           this.getApplicationContext().getMessage("Argentina",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("AM",
                                           this.getApplicationContext().getMessage("Armenia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("AW",
                                           this.getApplicationContext().getMessage("Aruba", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("AU",
                                           this.getApplicationContext().getMessage("Australia",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("AT",
                                           this.getApplicationContext().getMessage("Austria", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("AZ",
                                           this.getApplicationContext().getMessage("Azerbaijan",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("BS",
                                           this.getApplicationContext().getMessage("Bahamas", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BH",
                                           this.getApplicationContext().getMessage("Bahrain", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BD",
                                           this.getApplicationContext().getMessage("Bangladesh",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("BB",
                                           this.getApplicationContext().getMessage("Barbados", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BY",
                                           this.getApplicationContext().getMessage("Belarus", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BE",
                                           this.getApplicationContext().getMessage("Belgium", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BZ",
                                           this.getApplicationContext().getMessage("Belize", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BJ",
                                           this.getApplicationContext().getMessage("Benin", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BM",
                                           this.getApplicationContext().getMessage("Bermuda", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BT",
                                           this.getApplicationContext().getMessage("Bhutan", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BO", this.getApplicationContext().getMessage(
                    "BoliviaPlurinationalStateOf", null, locale)));
            responseObj.add(new UIListItem("BQ", this.getApplicationContext().getMessage(
                    "BonaireSaintEustatiusAndSaba", null, locale)));
            responseObj.add(new UIListItem("BA", this.getApplicationContext().getMessage(
                    "BosniaAndHerzegovina", null, locale)));
            responseObj.add(new UIListItem("BW",
                                           this.getApplicationContext().getMessage("Botswana", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BV",
                                           this.getApplicationContext().getMessage("BouvetIsland",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("BR",
                                           this.getApplicationContext().getMessage("Brazil", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("IO", this.getApplicationContext().getMessage(
                    "BritishIndianOceanTerritory", null, locale)));
            responseObj.add(new UIListItem("BN", this.getApplicationContext().getMessage(
                    "BruneiDarussalam", null, locale)));
            responseObj.add(new UIListItem("BG",
                                           this.getApplicationContext().getMessage("Bulgaria", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BF",
                                           this.getApplicationContext().getMessage("BurkinaFaso",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("BI",
                                           this.getApplicationContext().getMessage("Burundi", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("KH",
                                           this.getApplicationContext().getMessage("Cambodia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CM",
                                           this.getApplicationContext().getMessage("Cameroon", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CA",
                                           this.getApplicationContext().getMessage("Canada", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CV",
                                           this.getApplicationContext().getMessage("CapeVerde",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("KY",
                                           this.getApplicationContext().getMessage("CaymanIslands",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("CF", this.getApplicationContext().getMessage(
                    "CentralAfricanRepublic", null, locale)));
            responseObj.add(new UIListItem("TD",
                                           this.getApplicationContext().getMessage("Chad", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CL",
                                           this.getApplicationContext().getMessage("Chile", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CN",
                                           this.getApplicationContext().getMessage("China", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CX", this.getApplicationContext().getMessage(
                    "ChristmasIsland", null, locale)));
            responseObj.add(new UIListItem("CC", this.getApplicationContext().getMessage(
                    "CocosKeelingIslands", null, locale)));
            responseObj.add(new UIListItem("CO",
                                           this.getApplicationContext().getMessage("Colombia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("KM",
                                           this.getApplicationContext().getMessage("Comoros", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CG",
                                           this.getApplicationContext().getMessage("Congo", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CD", this.getApplicationContext().getMessage(
                    "CongoTheDemocraticRepublicOfThe", null, locale)));
            responseObj.add(new UIListItem("CK",
                                           this.getApplicationContext().getMessage("CookIslands",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("CR",
                                           this.getApplicationContext().getMessage("CostaRica",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("CI",
                                           this.getApplicationContext().getMessage("CoteDIvoire",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("HR",
                                           this.getApplicationContext().getMessage("Croatia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CU",
                                           this.getApplicationContext().getMessage("Cuba", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CW",
                                           this.getApplicationContext().getMessage("Curacao", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CY",
                                           this.getApplicationContext().getMessage("Cyprus", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CZ",
                                           this.getApplicationContext().getMessage("CzechRepublic",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("DK",
                                           this.getApplicationContext().getMessage("Denmark", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("DJ",
                                           this.getApplicationContext().getMessage("Djibouti", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("DM",
                                           this.getApplicationContext().getMessage("Dominica", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("DO", this.getApplicationContext().getMessage(
                    "DominicanRepublic", null, locale)));
            responseObj.add(new UIListItem("EC",
                                           this.getApplicationContext().getMessage("Ecuador", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("EG",
                                           this.getApplicationContext().getMessage("Egypt", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("SV",
                                           this.getApplicationContext().getMessage("ElSalvador",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("GQ", this.getApplicationContext().getMessage(
                    "EquatorialGuinea", null, locale)));
            responseObj.add(new UIListItem("ER",
                                           this.getApplicationContext().getMessage("Eritrea", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("EE",
                                           this.getApplicationContext().getMessage("Estonia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("ET",
                                           this.getApplicationContext().getMessage("Ethiopia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("FK", this.getApplicationContext().getMessage(
                    "FalklandIslandsMalvinas", null, locale)));
            responseObj.add(new UIListItem("FO",
                                           this.getApplicationContext().getMessage("FaroeIslands",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("FJ",
                                           this.getApplicationContext().getMessage("Fiji", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("FI",
                                           this.getApplicationContext().getMessage("Finland", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("FR",
                                           this.getApplicationContext().getMessage("France", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("GF",
                                           this.getApplicationContext().getMessage("FrenchGuiana",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("PF", this.getApplicationContext().getMessage(
                    "FrenchPolynesia", null, locale)));
            responseObj.add(new UIListItem("TF", this.getApplicationContext().getMessage(
                    "FrenchSouthernTerritories", null, locale)));
            responseObj.add(new UIListItem("GA",
                                           this.getApplicationContext().getMessage("Gabon", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("GM",
                                           this.getApplicationContext().getMessage("Gambia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("GE",
                                           this.getApplicationContext().getMessage("Georgia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("DE",
                                           this.getApplicationContext().getMessage("Germany", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("GH",
                                           this.getApplicationContext().getMessage("Ghana", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("GI",
                                           this.getApplicationContext().getMessage("Gibraltar",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("GR",
                                           this.getApplicationContext().getMessage("Greece", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("GL",
                                           this.getApplicationContext().getMessage("Greenland",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("GD",
                                           this.getApplicationContext().getMessage("Grenada", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("GP",
                                           this.getApplicationContext().getMessage("Guadeloupe",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("GU",
                                           this.getApplicationContext().getMessage("Guam", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("GT",
                                           this.getApplicationContext().getMessage("Guatemala",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("GG",
                                           this.getApplicationContext().getMessage("Guernsey", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("GN",
                                           this.getApplicationContext().getMessage("Guinea", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("GW",
                                           this.getApplicationContext().getMessage("Guinea-Bissau",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("GY",
                                           this.getApplicationContext().getMessage("Guyana", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("HT",
                                           this.getApplicationContext().getMessage("Haiti", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("HM", this.getApplicationContext().getMessage(
                    "HeardIslandAndMcDonaldIslands", null, locale)));
            responseObj.add(new UIListItem("VA", this.getApplicationContext().getMessage(
                    "HolySeeVaticanCityState", null, locale)));
            responseObj.add(new UIListItem("HN",
                                           this.getApplicationContext().getMessage("Honduras", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("HK",
                                           this.getApplicationContext().getMessage("HongKong", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("HU",
                                           this.getApplicationContext().getMessage("Hungary", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("IS",
                                           this.getApplicationContext().getMessage("Iceland", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("IN",
                                           this.getApplicationContext().getMessage("India", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("ID",
                                           this.getApplicationContext().getMessage("Indonesia",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("IR", this.getApplicationContext().getMessage(
                    "IranIslamicRepublicOf", null, locale)));
            responseObj.add(new UIListItem("IQ",
                                           this.getApplicationContext().getMessage("Iraq", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("IE",
                                           this.getApplicationContext().getMessage("Ireland", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("IM",
                                           this.getApplicationContext().getMessage("IsleOfMan",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("IL",
                                           this.getApplicationContext().getMessage("Israel", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("IT",
                                           this.getApplicationContext().getMessage("Italy", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("JM",
                                           this.getApplicationContext().getMessage("Jamaica", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("JP",
                                           this.getApplicationContext().getMessage("Japan", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("JE",
                                           this.getApplicationContext().getMessage("Jersey", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("JO",
                                           this.getApplicationContext().getMessage("Jordan", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("KZ",
                                           this.getApplicationContext().getMessage("Kazakhstan",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("KE",
                                           this.getApplicationContext().getMessage("Kenya", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("KI",
                                           this.getApplicationContext().getMessage("Kiribati", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("KP", this.getApplicationContext().getMessage(
                    "KoreaDemocraticPeoplesRepublicOf", null, locale)));
            responseObj.add(new UIListItem("KR", this.getApplicationContext().getMessage(
                    "KoreaRepublicOf", null, locale)));
            responseObj.add(new UIListItem("KW",
                                           this.getApplicationContext().getMessage("Kuwait", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("KG",
                                           this.getApplicationContext().getMessage("Kyrgyzstan",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("LA", this.getApplicationContext().getMessage(
                    "LaoPeoplesDemocraticRepublic", null, locale)));
            responseObj.add(new UIListItem("LV",
                                           this.getApplicationContext().getMessage("Latvia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("LB",
                                           this.getApplicationContext().getMessage("Lebanon", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("LS",
                                           this.getApplicationContext().getMessage("Lesotho", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("LR",
                                           this.getApplicationContext().getMessage("Liberia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("LY", this.getApplicationContext().getMessage(
                    "LibyanArabJamahiriya", null, locale)));
            responseObj.add(new UIListItem("LI",
                                           this.getApplicationContext().getMessage("Liechtenstein",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("LT",
                                           this.getApplicationContext().getMessage("Lithuania",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("LU",
                                           this.getApplicationContext().getMessage("Luxembourg",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("MO",
                                           this.getApplicationContext().getMessage("Macao", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("MK", this.getApplicationContext().getMessage(
                    "MacedoniaTheFormerYugoslavRepublicOf", null, locale)));
            responseObj.add(new UIListItem("MG",
                                           this.getApplicationContext().getMessage("Madagascar",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("MW",
                                           this.getApplicationContext().getMessage("Malawi", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("MY",
                                           this.getApplicationContext().getMessage("Malaysia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("MV",
                                           this.getApplicationContext().getMessage("Maldives", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("ML",
                                           this.getApplicationContext().getMessage("Mali", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("MT",
                                           this.getApplicationContext().getMessage("Malta", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("MH", this.getApplicationContext().getMessage(
                    "MarshallIslands", null, locale)));
            responseObj.add(new UIListItem("MQ",
                                           this.getApplicationContext().getMessage("Martinique",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("MR",
                                           this.getApplicationContext().getMessage("Mauritania",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("MU",
                                           this.getApplicationContext().getMessage("Mauritius",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("YT",
                                           this.getApplicationContext().getMessage("Mayotte", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("MX",
                                           this.getApplicationContext().getMessage("Mexico", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("FM", this.getApplicationContext().getMessage(
                    "MicronesiaFederatedStatesOf", null, locale)));
            responseObj.add(new UIListItem("MD", this.getApplicationContext().getMessage(
                    "MoldovaRepublicOf", null, locale)));
            responseObj.add(new UIListItem("MC",
                                           this.getApplicationContext().getMessage("Monaco", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("MN",
                                           this.getApplicationContext().getMessage("Mongolia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("ME",
                                           this.getApplicationContext().getMessage("Montenegro",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("MS",
                                           this.getApplicationContext().getMessage("Montserrat",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("MA",
                                           this.getApplicationContext().getMessage("Morocco", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("MZ",
                                           this.getApplicationContext().getMessage("Mozambique",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("MM",
                                           this.getApplicationContext().getMessage("Myanmar", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("NA",
                                           this.getApplicationContext().getMessage("Namibia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("NR",
                                           this.getApplicationContext().getMessage("Nauru", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("NP",
                                           this.getApplicationContext().getMessage("Nepal", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("NL",
                                           this.getApplicationContext().getMessage("Netherlands",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("NC",
                                           this.getApplicationContext().getMessage("NewCaledonia",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("NZ",
                                           this.getApplicationContext().getMessage("NewZealand",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("NI",
                                           this.getApplicationContext().getMessage("Nicaragua",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("NE",
                                           this.getApplicationContext().getMessage("Niger", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("NG",
                                           this.getApplicationContext().getMessage("Nigeria", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("NU",
                                           this.getApplicationContext().getMessage("Niue", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("NF",
                                           this.getApplicationContext().getMessage("NorfolkIsland",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("MP", this.getApplicationContext().getMessage(
                    "NorthernMarianaIslands", null, locale)));
            responseObj.add(new UIListItem("NO",
                                           this.getApplicationContext().getMessage("Norway", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("OM",
                                           this.getApplicationContext().getMessage("Oman", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("PK",
                                           this.getApplicationContext().getMessage("Pakistan", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("PW",
                                           this.getApplicationContext().getMessage("Palau", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("PS", this.getApplicationContext().getMessage(
                    "PalestinianTerritoryOccupied", null, locale)));
            responseObj.add(new UIListItem("PA",
                                           this.getApplicationContext().getMessage("Panama", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("PG",
                                           this.getApplicationContext().getMessage("PapuaNewGuinea",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("PY",
                                           this.getApplicationContext().getMessage("Paraguay", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("PE",
                                           this.getApplicationContext().getMessage("Peru", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("PH",
                                           this.getApplicationContext().getMessage("Philippines",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("PN",
                                           this.getApplicationContext().getMessage("Pitcairn", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("PL",
                                           this.getApplicationContext().getMessage("Poland", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("PT",
                                           this.getApplicationContext().getMessage("Portugal", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("PR",
                                           this.getApplicationContext().getMessage("PuertoRico",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("QA",
                                           this.getApplicationContext().getMessage("Qatar", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("RE",
                                           this.getApplicationContext().getMessage("Reunion", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("RO",
                                           this.getApplicationContext().getMessage("Romania", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("RU", this.getApplicationContext().getMessage(
                    "RussianFederation", null, locale)));
            responseObj.add(new UIListItem("RW",
                                           this.getApplicationContext().getMessage("Rwanda", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("BL", this.getApplicationContext().getMessage(
                    "SaintBarthelemy", null, locale)));
            responseObj.add(new UIListItem("SH", this.getApplicationContext().getMessage(
                    "SaintHelenaAscensionAndTristanDaCunha", null, locale)));
            responseObj.add(new UIListItem("KN", this.getApplicationContext().getMessage(
                    "SaintKittsAndNevis", null, locale)));
            responseObj.add(new UIListItem("LC",
                                           this.getApplicationContext().getMessage("SaintLucia",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("MF", this.getApplicationContext().getMessage(
                    "SaintMartinFrenchPart", null, locale)));
            responseObj.add(new UIListItem("PM", this.getApplicationContext().getMessage(
                    "SaintPierreAndMiquelon", null, locale)));
            responseObj.add(new UIListItem("VC", this.getApplicationContext().getMessage(
                    "SaintVincentAndTheGrenadines", null, locale)));
            responseObj.add(new UIListItem("WS",
                                           this.getApplicationContext().getMessage("Samoa", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("SM",
                                           this.getApplicationContext().getMessage("SanMarino",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("ST", this.getApplicationContext().getMessage(
                    "SaoTomeAndPrincipe", null, locale)));
            responseObj.add(new UIListItem("SA",
                                           this.getApplicationContext().getMessage("SaudiArabia",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("SN",
                                           this.getApplicationContext().getMessage("Senegal", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("RS",
                                           this.getApplicationContext().getMessage("Serbia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("SC",
                                           this.getApplicationContext().getMessage("Seychelles",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("SL",
                                           this.getApplicationContext().getMessage("SierraLeone",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("SG",
                                           this.getApplicationContext().getMessage("Singapore",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("SX", this.getApplicationContext().getMessage(
                    "SintMaartenDutchPart", null, locale)));
            responseObj.add(new UIListItem("SK",
                                           this.getApplicationContext().getMessage("Slovakia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("SI",
                                           this.getApplicationContext().getMessage("Slovenia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("SB",
                                           this.getApplicationContext().getMessage("Solomon", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("SO",
                                           this.getApplicationContext().getMessage("Somalia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("ZA",
                                           this.getApplicationContext().getMessage("SouthAfrica",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("GS", this.getApplicationContext().getMessage(
                    "SouthGeorgiaAndTheSouthSandwichIslands", null, locale)));
            responseObj.add(new UIListItem("ES",
                                           this.getApplicationContext().getMessage("Spain", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("LK",
                                           this.getApplicationContext().getMessage("SriLanka", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("SD",
                                           this.getApplicationContext().getMessage("Sudan", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("SR",
                                           this.getApplicationContext().getMessage("Suriname", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("SJ", this.getApplicationContext().getMessage(
                    "SvalbardAndJanMayen", null, locale)));
            responseObj.add(new UIListItem("SZ",
                                           this.getApplicationContext().getMessage("Swaziland",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("SE",
                                           this.getApplicationContext().getMessage("Sweden", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("CH",
                                           this.getApplicationContext().getMessage("Switzerland",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("SY", this.getApplicationContext().getMessage(
                    "SyrianArabRepubic", null, locale)));
            responseObj.add(new UIListItem("TW", this.getApplicationContext().getMessage(
                    "TaiwanProvinceOfChina", null, locale)));
            responseObj.add(new UIListItem("TJ",
                                           this.getApplicationContext().getMessage("Tajikistan",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("TZ", this.getApplicationContext().getMessage(
                    "TanzaniaUnitedRepublicOf", null, locale)));
            responseObj.add(new UIListItem("TH",
                                           this.getApplicationContext().getMessage("Thailand", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("TL",
                                           this.getApplicationContext().getMessage("Timor-Leste",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("TG",
                                           this.getApplicationContext().getMessage("Togo", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("TK",
                                           this.getApplicationContext().getMessage("Tokelau", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("TO",
                                           this.getApplicationContext().getMessage("Tonga", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("TT", this.getApplicationContext().getMessage(
                    "TrinidadAndTobago", null, locale)));
            responseObj.add(new UIListItem("TN",
                                           this.getApplicationContext().getMessage("Tunisia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("TR",
                                           this.getApplicationContext().getMessage("Turkey", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("TM",
                                           this.getApplicationContext().getMessage("Turkmenistan",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("TC", this.getApplicationContext().getMessage(
                    "TurksAndCaicosIslands", null, locale)));
            responseObj.add(new UIListItem("TV",
                                           this.getApplicationContext().getMessage("Tuvalu", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("UG",
                                           this.getApplicationContext().getMessage("Uganda", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("UA",
                                           this.getApplicationContext().getMessage("Ukraine", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("AE", this.getApplicationContext().getMessage(
                    "UnitedArabEmirates", null, locale)));
            responseObj.add(new UIListItem("GB",
                                           this.getApplicationContext().getMessage("UnitedKingdom",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("US",
                                           this.getApplicationContext().getMessage("UnitedStates",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("UM", this.getApplicationContext().getMessage(
                    "UnitedStatesMinorOutlyingIslands", null, locale)));
            responseObj.add(new UIListItem("UY",
                                           this.getApplicationContext().getMessage("Uruguay", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("UZ",
                                           this.getApplicationContext().getMessage("Uzbekistan",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("VU",
                                           this.getApplicationContext().getMessage("Vanuatu", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("VE",
                                           this.getApplicationContext().getMessage("Venezuela",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("VN",
                                           this.getApplicationContext().getMessage("VietNam", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("VG", this.getApplicationContext().getMessage(
                    "VirginIslandsBritish", null, locale)));
            responseObj.add(new UIListItem("VI", this.getApplicationContext().getMessage(
                    "VirginIslandsUS", null, locale)));
            responseObj.add(new UIListItem("WF", this.getApplicationContext().getMessage(
                    "WallisAndFutuna", null, locale)));
            responseObj.add(new UIListItem("EH",
                                           this.getApplicationContext().getMessage("WesternSahara",
                                                                                   null, locale)));
            responseObj.add(new UIListItem("YE",
                                           this.getApplicationContext().getMessage("Yemen", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("ZM",
                                           this.getApplicationContext().getMessage("Zambia", null,
                                                                                   locale)));
            responseObj.add(new UIListItem("ZW",
                                           this.getApplicationContext().getMessage("Zimbabwe", null,
                                                                                   locale)));
            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("getAvailableCountries() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Export trouble shooting bundle.
     *
     * @param jobExportTroubleshootingBundleRequest
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "exporttroubleshootingbundle", method = RequestMethod.POST)
    public JobResponse exportTroubleShootingBundle(
            @RequestBody JobTroubleshootingBundleRequest jobExportTroubleshootingBundleRequest) {

        JobResponse jobResponse = new JobResponse();
        try {
            TroubleshootingBundleParams troubleshootingBundleParams = parseVxfmTroubleshootingBundleParams(
                    jobExportTroubleshootingBundleRequest.getRequestObj());

            // first call the test troubleshooting bundle to check if the path to upload the file exists
            // and the user has access to it. This will save time in displaying the corrections with respect
            // to path and user parameters instead of waiting till the logs are collected from devices and
            // then throwing the errors related to path and access
            Response response = vxfmTroubleshootingBundleServiceAdapter.
                    testTroubleshootingBundle(troubleshootingBundleParams);

            if(response != null && Response.Status.NO_CONTENT.getStatusCode() == response.getStatus()) {
                String generateBundleResponse = vxfmTroubleshootingBundleServiceAdapter.
                        exportTroubleShootingBundle(troubleshootingBundleParams);
                if (generateBundleResponse != null) {
                    jobResponse.responseObj = generateBundleResponse;
                }
            } else {
                // typically shouldn't get here - since WebClient will throw an exception which is caught by
                // REST service adapter that will be caught in the catch block in this method
                log.error("Test troubleshooting bundle failed with response : " +  response);
            }
        } catch (Throwable t) {
            log.error("exportTroubleShootingBundle() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }


    /**
     * Test trouble shooting bundle.
     *
     * @param jobTestTroubleshootingBundleRequest
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "testtroubleshootingbundle", method = RequestMethod.POST)
    public JobResponse testTroubleShootingBundle(
            @RequestBody JobTroubleshootingBundleRequest jobTestTroubleshootingBundleRequest) {

        JobResponse jobResponse = new JobResponse();
        try {
            TroubleshootingBundleParams troubleshootingBundleParams = parseVxfmTroubleshootingBundleParams(
                    jobTestTroubleshootingBundleRequest.getRequestObj());

            vxfmTroubleshootingBundleServiceAdapter.testTroubleshootingBundle(troubleshootingBundleParams);
        } catch (Throwable t) {
            log.error("testTroubleShootingBundle() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Export trouble shooting bundle.
     *
     * @param response
     *            the Servlet response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "downloadtroubleshootingbundle", method = RequestMethod.GET, produces = "application/zip")
    public void downloadTroubleShootingBundle(HttpServletResponse response) throws IOException {

        InputStream is = null;
        DateTime now = new DateTime(DateTimeZone.UTC);
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(
                TroubleShootingFileName_DatePattern);
        String troubleShootingFileName = TroubleShootingFileName + "_" + dateTimeFormatter.print(
                now) + TroubleShootingFileExtension;
        try {
            is = applianceTroubleshootingBundleServiceAdapter.downloadBundle();
            response.setHeader("Content-Disposition",
                               "attachment; filename=" + troubleShootingFileName);
            response.setStatus(HttpServletResponse.SC_OK);
            byte[] buffer = new byte[1024 * 1024]; // Default buffer size if 4k which is small for large files like logs zip
            IOUtils.copyLarge(is, response.getOutputStream(), buffer);
            response.flushBuffer();
        } catch (IOException ioe) {
            log.error(
                    "IOException accessing the troubleshooting bundle zip file: " + troubleShootingFileName + ". Exception: " + ioe.getMessage());
            response.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                               getApplicationContext().getMessage(
                                       "TroubleshootingLogs.FileException",
                                       new Object[] { troubleShootingFileName },
                                       LocaleContextHolder.getLocale()));
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Gets the proxy.
     *
     * @return the proxy
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getproxy", method = RequestMethod.POST)
    public JobResponse getProxy() {
        JobResponse jobResponse = new JobResponse();
        UIProxyData responseObj = new UIProxyData();
        try {
            ProxySetting proxy = proxySettingsServiceAdapter.getProxySettings();

            responseObj.enableProxy = proxy.isEnabled();
            responseObj.proxyServer = proxy.getProxyServer();
            if (proxy.getPort() != null) {
                responseObj.proxyPort = Integer.parseInt(proxy.getPort());
            }

            responseObj.enableProxyCredentials = proxy.isUserCredentialEnabled();
            if (proxy.isUserCredentialEnabled()) {
                responseObj.proxyUsername = proxy.getUserName();
                if (StringUtils.isNotEmpty(proxy.getPassword())) {
                    responseObj.proxyPassword = proxy.getPassword();
                    responseObj.proxyVerifyPassword = proxy.getPassword();
                } else {
                    responseObj.proxyPassword = MappingUtils.getPassword();
                    responseObj.proxyVerifyPassword = MappingUtils.getPassword();
                }
            } else {
                responseObj.proxyUsername = null;
                responseObj.proxyPassword = null;
                responseObj.proxyVerifyPassword = null;
            }

        } catch (Throwable t) {
            log.error("getProxy() - An Exception occurred in the proxy settings service call", t);
            return addFailureResponseInfo(t);
        }
        jobResponse.responseObj = responseObj;
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
    @RequestMapping(value = "setproxy", method = RequestMethod.POST)
    public JobResponse updateProxy(@RequestBody JobProxyDataRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            ProxySetting proxy = null;
            if (request.requestObj != null) {
                proxy = MappingUtils.createProxySetting(request.requestObj);
            }
            proxySettingsServiceAdapter.setProxySettings(proxy);
        } catch (Throwable t) {
            log.error("updateProxy() - An Exception occurred in the proxy settings service call",
                      t);
            return addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the license.
     *
     * @return the license
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getlicensedata", method = RequestMethod.POST)
    public JobResponse getLicense(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            LicenseDetails ld = licenseServiceAdapter.getLicenseDetails();
            Integer serverCount = getUsedLicenseCount(request);
            if (ld != null) {
                jobResponse.responseObj = parseLicense(ld, serverCount);
            }

        } catch (Throwable t) {
            log.error("getLicense() - An Exception occurred in the proxy settings service call", t);
            return addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Verify license.
     *
     * @param multipartFile
     *            the multipart file
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "verifylicense", method = RequestMethod.POST)
    public JobResponse verifyLicense(@RequestParam("file") MultipartFile multipartFile) {

        JobResponse jobResponse = new JobResponse();
        try {
            String licenseFileContent = new String(multipartFile.getBytes());
            String licenseFileName = multipartFile.getOriginalFilename();

            synchronized (m_licenseContent) {
                ASMLicense storedLicense = m_licenseContent.get(licenseFileName);

                if (storedLicense == null) {
                    m_licenseContent.clear();
                    m_licenseContent.put(licenseFileName,
                                         new ASMLicense(licenseFileName, licenseFileContent));
                }

                UploadLicenseRequest req = new UploadLicenseRequest();
                req.setContent(licenseFileContent);

                LicenseDetails details = licenseServiceAdapter.uploadLicense(req, false, true);

                if (details != null) {
                    UILicenseData uiLicense = parseLicense(details, 0);
                    //fake valid as there will never be a activation date.
                    if (!uiLicense.isValid &&
                            StringUtils.isBlank(uiLicense.activationdate)) {
                        uiLicense.isValid = true;
                    }
                    uiLicense.licensefile = licenseFileName;
                    jobResponse.responseObj = uiLicense;
                }
            }

        } catch (Throwable t) {
            log.error("verifyLicense() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Update the license.
     *
     * @return the licenses
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "updatelicense", method = RequestMethod.POST)
    public JobResponse updateLicense(@RequestBody JobLicenseDataRequest request) {
        JobResponse jobResponse = new JobResponse();

        try {

            UploadLicenseRequest req = new UploadLicenseRequest();

            UILicenseData ldata = request.requestObj;
            synchronized (m_licenseContent) {
                ASMLicense storedLicense = m_licenseContent.get(ldata.licensefile);

                if (storedLicense != null) {
                    req.setContent(storedLicense.getLicenseContent());
                    LicenseDetails details = licenseServiceAdapter.uploadLicense(req, true,
                                                                                 true); // TODO: ask UI if they use ldata.force
                    UILicenseData uld = parseLicense(details, 0);
                    uld.licensefile = ldata.licensefile;
                    jobResponse.responseObj = uld;
                }
            }

        } catch (Throwable t) {
            log.error("getLicense() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * REST helper.
     *
     * @param info
     * @return
     */
    private CertificateInfo createCSR(UIApplianceCertificateInfo info) {
        CertificateInfo details = new CertificateInfo();
        if (info.distinguishedName != null) {
            details.setCommonName(info.distinguishedName);
        }
        if (info.businessName != null) {
            details.setOrganization(info.businessName);
        }
        if (info.departmentName != null) {
            details.setOrganizationUnit(info.departmentName);
        }
        if (info.locality != null) {
            details.setLocality(info.locality);
        }
        if (info.state != null) {
            details.setState(info.state);
        }
        if (info.country != null) {
            details.setCountry(info.country);
        }
        if (info.email != null) {
            details.setEmailAddress(info.email);
        }
        return details;
    }

    /**
     * REST helper.
     *
     * @param content
     * @return
     */
    private SSLCertUploadRequest createUploadCertRequest(String content) {
        SSLCertUploadRequest req = new SSLCertUploadRequest();
        req.setCertificateContent(content);
        return req;
    }

    /**
     * Gets the cSR file.
     *
     * @param url
     *            the url
     * @return the cSR file
     */
    private String getCSRFile(String url) {
        String file = null;
        try {
            URL u = new URL(url);
            URLConnection uc = u.openConnection();
            InputStream is = uc.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) >= 0) {
                os.write(buf, 0, n);
            }
            os.close();
            is.close();
            byte[] data = os.toByteArray();
            file = new String(data);
        } catch (Throwable t) {
            log.error("Cannot read CSR file from URL" + url, t);
        }
        return file;
    }

    public Integer getUsedLicenseCount(JobRequest request) {
        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_DEVICES,
                                                            MappingUtils.COLUMNS_DEVICES_FILTER,
                                                            "ipAddress");
        Integer totalCount = deviceInventoryServiceAdapter.getTotalCount(options.filterList);
        long deviceCount = totalCount.longValue();
        //These devices should not consume an ASM license
        String[] unlicensedDevices = { DeviceType.vcenter.name(), DeviceType.scvmm.name(), DeviceType.ChassisM1000e.name(),
                DeviceType.ChassisFX.name(), DeviceType.em.name(), DeviceType.dellswitch.name(), DeviceType.genericswitch.name(),
                DeviceType.ciscoswitch.name(), DeviceType.TOR.name(), DeviceType.MXLIOM.name(), DeviceType.AggregatorIOM.name(),
                DeviceType.scaleio.name(), DeviceType.rhvm.name() };
        long unlicensedDeviceCount = getUnlicensedDeviceCount(unlicensedDevices);
        Integer usedLicenseCount = safeLongToInteger(deviceCount - unlicensedDeviceCount);
        log.info(String.format("getUsedLicenseCount(): returning %d.", usedLicenseCount));
        return usedLicenseCount;
    }

    private long getUnlicensedDeviceCount(String[] unlicensedDevices) {
        long unlicensedDeviceCount = 0;
        for (String thisDevice : unlicensedDevices) {
            List<String> filterList = new ArrayList<>();
            CriteriaObj criteria = new CriteriaObj();
            criteria.filterObj = new ArrayList<>();
            FilterObj filter = new FilterObj();
            filter.field = DEVICETYPE_COLUMN;
            filter.op = OPER_EQ;
            filterList.add(thisDevice);
            filter.opTarget = filterList;
            criteria.filterObj.add(filter);
            RESTRequestOptions deviceOptions = new RESTRequestOptions(criteria,
                                                                      MappingUtils.COLUMNS_DEVICES,
                                                                      MappingUtils.COLUMNS_DEVICES_FILTER,
                                                                      "ipAddress");
            unlicensedDeviceCount = deviceInventoryServiceAdapter.getTotalCount(
                    deviceOptions.filterList) + unlicensedDeviceCount;
            log.info(String.format("getUsedLicenseCount(): unlicensedDevicecount = %d",
                                   unlicensedDeviceCount));
        }
        return unlicensedDeviceCount;
    }

    private Integer safeLongToInteger(long longValue) {
        Integer result = Integer.MAX_VALUE;
        if (Integer.MAX_VALUE < longValue) {
            log.warn("safeLongToInteger(): Value exceeds integer max. Returning integer max.");
        } else {
            result = (int) longValue;
        }
        return result;
    }

    /**
     * Gets dhcp data.
     *
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getdhcpsettings", method = RequestMethod.POST)
    public JobResponse getDhcpSettings() {

        JobResponse jobResponse = new JobResponse();
        UIDhcpData responseObj = new UIDhcpData();
        try {
            DHCPSetting dhcpSettings = dhcpSettingsServiceAdapter.getDHCPSettings();
            if (dhcpSettings != null) {
                responseObj = new UIDhcpData(dhcpSettings);
            }
            jobResponse.responseObj = responseObj;

        } catch (Throwable t) {
            log.error("getDhcpSettings() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets stats.
     *
     * @return the status
     * @throws ServletException
     *              the servlet exception
     * @throws IOException
     *              Signals that an I/O exception has occured.
     *
     */
    @RequestMapping(value = "getstatus", method = RequestMethod.POST)
    public JobResponse getStatus() {
        JobResponse jobResponse = new JobResponse();
        String responseObj;

        UpgradeStatus upgradeStatus = null;
        try {
            upgradeStatus = applianceServiceAdapter.getUpgradeStatus();
        } catch (Exception e) {
            // no credentials to access ALCM - appliance restarted
            AsmManagerInitLifecycleListener.setStatus(AsmManagerInitLifecycleListener.READY);
            ASMUIAuthenticationProvider.setSTATUS(AsmManagerInitLifecycleListener.READY);
            jobResponse.responseObj = AsmManagerInitLifecycleListener.READY;
            return jobResponse;
        }

        boolean upgrading = upgradeStatus.isUpgrading();
        boolean restarting = applianceServiceAdapter.isRestarting();

        if (upgrading && !restarting) {
            responseObj = getApplicationContext().getMessage(
                    "appliance.upgrading", null, LocaleContextHolder.getLocale());

        } else if (restarting) {
            responseObj = getApplicationContext().getMessage(
                    "appliance.restarting", null, LocaleContextHolder.getLocale());
        } else if (StringUtils.isNotEmpty(upgradeStatus.getError())) {
            // error while upgrading
            AsmManagerInitLifecycleListener.setStatus(AsmManagerInitLifecycleListener.READY);
            ASMUIAuthenticationProvider.setSTATUS(AsmManagerInitLifecycleListener.READY);
            responseObj = AsmManagerInitLifecycleListener.READY;

        } else {
            responseObj = getApplicationContext().getMessage(
                    "appliance.initializing", null, LocaleContextHolder.getLocale());
            boolean containerStarted = ContainerLifecycleListener.isContainerStarted();
            if (containerStarted) {
                String status = AsmManagerInitLifecycleListener.getStatus();
                if (StringUtils.isNotBlank(status)) {
                    if (AsmManagerInitLifecycleListener.READY.equals(status)) {
                        //DO NOT LOCALIZE READY
                        responseObj = status;
                    } else {
                        String messageKey = "appliance." + status;
                        responseObj = getApplicationContext().getMessage(
                                messageKey, null, LocaleContextHolder.getLocale());
                    }
                }
            }
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    private boolean isValidYumRepo(String repoLocation) throws ControllerException {
        // Add a Valid RepoConfig.xml to the RepoLocation accounting for forward slash
        if (StringUtils.isEmpty(repoLocation)) {
            throw new ControllerException(getApplicationContext().getMessage(
                    "validationError.yumRepoInvalid", null, LocaleContextHolder.getLocale()));
        }
        try {
            return YumRepoUtil.isValidYumRepo(repoLocation);
        } catch (IOException E) {
            throw new ControllerException(getApplicationContext().getMessage(
                    "validationError.yumRepoInvalid", null, LocaleContextHolder.getLocale()));
        }
    }

    /**
     * Update virtual appliance service tag.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "updateservicetag", method = RequestMethod.POST)
    public JobResponse updateServiceTag(@RequestBody JobStringRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            Setting setting = settingServiceAdapter.getSettingByName(
                    UIAboutData.ASM_SERVICE_TAG_SETTING);
            if (setting == null) {
                setting = new Setting();
                setting.setName(UIAboutData.ASM_SERVICE_TAG_SETTING);
                setting.setValue(request.requestObj);
                settingServiceAdapter.create(setting);
            } else {
                setting.setValue(request.requestObj);
                settingServiceAdapter.update(setting);
            }

        } catch (Throwable t) {
            log.error("updateServiceTag() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Get the ports to verify by pinging
     * @return String of comma separated ports
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "getipverifyports", method = RequestMethod.POST)
    public JobResponse getIPVerifyPorts() {

        JobResponse jobResponse = new JobResponse();

        try {
            Setting portsToPingSetting = settingServiceAdapter.getSettingByName(ASM_PORTS_TO_PING);
            if (portsToPingSetting != null) {
                UIPortData uiPortData = new UIPortData();
                uiPortData.ipVerificationPorts = portsToPingSetting.getValue();
                jobResponse.responseObj = uiPortData;
            }
        } catch (Throwable t) {
            log.error("getIPVerifyPorts() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "updateipverifyports", method = RequestMethod.POST)
    public JobResponse updateIPVerifyPorts(@RequestBody JobUpdatePortDataRequest request) {

        JobResponse jobResponse = new JobResponse();
        try {
            if (request != null &&
                    request.requestObj != null &&
                    request.requestObj.ipVerificationPorts != null) {
                List<String> items = Arrays.asList(
                        request.requestObj.ipVerificationPorts.split(","));
                boolean invalid = false;
                if (items.size() > 0) {
                    for (String item : items) {
                        Integer port = null;
                        try {
                            port = Integer.valueOf(item.trim());
                        } catch (NumberFormatException e) {
                            log.warn("Invalid port " + item);
                            invalid = true;

                        }
                        if (port != null && (port < 1 || port > 65535)) {
                            invalid = true;
                        }
                        if (invalid) {
                            break;
                        }
                    }
                }
                if (invalid) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("validationError.invalidPorts",
                                                               null,
                                                               LocaleContextHolder.getLocale()));
                }

                Setting portsToPingSetting = settingServiceAdapter.getSettingByName(
                        ASM_PORTS_TO_PING);
                if (portsToPingSetting != null) {
                    portsToPingSetting.setValue(request.requestObj.ipVerificationPorts);
                    settingServiceAdapter.update(portsToPingSetting);
                }
            }
        } catch (Throwable t) {
            log.error("updateIPVerifyPorts() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * End point for getting configuration details from SRS connector.
     *
     * @return an object of type {@link JobResponse}
     */
    @RequestMapping(value = "getvxrackflexalertconnectorsettings", method = RequestMethod.POST)
    public @ResponseBody JobResponse getConfiguration() {
        JobResponse jobResponse = new JobResponse();
        SRSConnectorSettings srsConnectorSettings = null;

        try {
            log.debug("Getting SRS connector configuration details");
            srsConnectorSettings = this.srsConnectorAdapter.getConfiguration();
            populateJobResponse(srsConnectorSettings, jobResponse);
        } catch (Throwable t) {
            log.error("getConfiguration() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * End point for saving srs connector configuration.
     * @param jobSRSConnectorRegistrationRequest - Holds the srs connector registration data
     * @return {@link JobResponse}
     */
    @RequestMapping(value = "setvxrackflexalertconnectorsettings/register", method = RequestMethod.POST)
    @ResponseBody
    public JobResponse saveConfiguration(@RequestBody JobSRSConnectorRegistrationRequest
                                                     jobSRSConnectorRegistrationRequest) {
        JobResponse jobResponse = new JobResponse();
        try {
            log.debug("Saving srs collector configuration details");
            SRSConnectorSettings srsConnectorSettings = prepareSRSConnectorSettings
                    (jobSRSConnectorRegistrationRequest.getRequestObj());
            String response = this.srsConnectorAdapter.register(srsConnectorSettings);
            jobResponse.responseObj = response;
        } catch (Throwable t) {
            log.error("saveConfiguration() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * End point to suspend alerts for specified duration
     * @param jobSRSConnectorRegistrationRequest - Duration for which alerts have to be suspended
     * @return {@link JobResponse}
     */
    @RequestMapping(value = "setvxrackflexalertconnectorsettings/suspend", method = RequestMethod.POST)
    @ResponseBody
    public JobResponse suspendAlerts(@RequestBody JobSRSConnectorRegistrationRequest jobSRSConnectorRegistrationRequest) {
        JobResponse jobResponse = new JobResponse();
        try {
            log.debug("Suspend alerts");
            String response = this.srsConnectorAdapter.suspend(jobSRSConnectorRegistrationRequest.getRequestObj().getState());
            jobResponse.responseObj = response;
        } catch (Throwable t) {
            log.error("suspendAlerts() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * End point to deregister Alert connector
     * @return {@link JobResponse}
     */
    @RequestMapping(value = "deregistervxrack", method = RequestMethod.POST)
    @ResponseBody
    public JobResponse deregister(@RequestBody JobAlertConnectorDeregistrationRequest request)
    {
        JobResponse jobResponse = new JobResponse();
        try
        {
            log.debug("Deregistering Alert connector with connection type " + request.getRequestObj().getConnectionType());
            String response = this.srsConnectorAdapter.deregister(request.getRequestObj().getConnectionType());
            jobResponse.responseObj = response;
        }
        catch (Throwable t)
        {
            log.error("deregister() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }
    
    /**
     * End point to enable SNMP and IPMI
     * @return {@link JobResponse}
     */
    @RequestMapping(value = "configureserversforalertconnector", method = RequestMethod.POST)
    @ResponseBody
    public JobResponse enableSnmpAndIpmi() {
        JobResponse jobResponse = new JobResponse();
        try {
            log.debug("Enable SNMP and IPMI");
            this.srsConnectorAdapter.enableSnmpAndIpmi();
        } catch (Exception exception) {
            log.error("enableSnmpAndIpmi() - Exception from service call", exception);
            jobResponse = addFailureResponseInfo(exception);
        }
        return jobResponse;
    }

    @RequestMapping(value = "getconfigureserverstatus", method = RequestMethod.POST)
    @ResponseBody
    public JobResponse getConfigureServerStatus() {
        JobResponse jobResponse = new JobResponse();
        try {
            String filter = "eq,group,ConfigureServersForSnmpAndIpmiJob";
            ResourceList<JrafJobInfo> jobs = jobsServiceAdapter.getJobs(null, Arrays.asList(filter), null, null);
            if (jobs != null) {
                if (Arrays.stream(jobs.getList()).anyMatch((j) -> "running".equals(j.getStatus()) || "scheduled".equals(j.getStatus()))) {
                    jobResponse.responseObj = "inprogress";
                } else {
                    jobResponse.responseObj = "completed";
                }
            }
        } catch (Exception exception) {
            jobResponse = addFailureResponseInfo(exception);
        }
        return jobResponse;
    }

    private SRSConnectorSettings prepareSRSConnectorSettings(UIVxRackFlexSRSConnector srsConnector) {
        SRSConnectorSettings connectorSettings = new SRSConnectorSettings();
        connectorSettings.setOmeIP(srsConnector.getOmeIP());
        connectorSettings.setOmeUser(srsConnector.getOmeUsername());
        connectorSettings.setOmePassword(srsConnector.getOmePassword());
        connectorSettings.setOmeAlertFilter(srsConnector.getAlertFilter());
        connectorSettings.setDelayTimeH(String.valueOf(srsConnector.getAlertPollingIntervalHours()));
        connectorSettings.setDelayTimeM(String.valueOf(srsConnector.getAlertPollingIntervalMinutes()));
        connectorSettings.setDeviceType(srsConnector.getDeviceType());
        connectorSettings.setEsrsHost(srsConnector.getSrsGatewayHostIp());
        connectorSettings.setEsrsHostPort(String.valueOf(srsConnector.getSrsGatewayHostPort()));
        connectorSettings.setRegisterUserID(srsConnector.getSrsUserId());
        connectorSettings.setRegisterPassword(srsConnector.getSrsPassword());
        connectorSettings.setSoftware_id(srsConnector.getElmsSoftwareId());
        connectorSettings.setSolution_sn(srsConnector.getSolutionSerialNumber());
        connectorSettings.setState(srsConnector.getState());
        connectorSettings.setConnectionType(srsConnector.getConnectionType());
        connectorSettings.setPhoneHomeIp(srsConnector.getPhoneHomeIp());
        connectorSettings.setPhoneHomePort(String.valueOf(srsConnector.getPhoneHomePort()));
        return connectorSettings;
    }
	private void populateJobResponse(SRSConnectorSettings srsConnectorSettings, JobResponse jobResponse) {
			UIVxRackFlexSRSConnector uiVxRackFlexSRSConnector = parseToUIFormat(srsConnectorSettings);
			jobResponse.responseObj = uiVxRackFlexSRSConnector;
	}

	private UIVxRackFlexSRSConnector parseToUIFormat(SRSConnectorSettings srsConnectorSettings) {
		UIVxRackFlexSRSConnector uiVxRackFlexSrsConnector = new UIVxRackFlexSRSConnector();
		uiVxRackFlexSrsConnector.setSerialNumber((StringUtils.isNotBlank(srsConnectorSettings.getStatus()) && srsConnectorSettings.getStatus().equals("None")) ? "None":srsConnectorSettings.getSolution_sn());
		uiVxRackFlexSrsConnector.setModel(srsConnectorSettings.getDeviceType());
		uiVxRackFlexSrsConnector.setSrsAddress((StringUtils.isNotBlank(srsConnectorSettings.getStatus()) && srsConnectorSettings.getStatus().equals("None")) ? "None":srsConnectorSettings.getEsrsHost());
		uiVxRackFlexSrsConnector.setState(srsConnectorSettings.getState());
		uiVxRackFlexSrsConnector.setStatus(srsConnectorSettings.getStatus());
		uiVxRackFlexSrsConnector.setOmeIP(srsConnectorSettings.getOmeIP());
		uiVxRackFlexSrsConnector.setOmeUsername(srsConnectorSettings.getOmeUser());
		uiVxRackFlexSrsConnector.setOmePassword(srsConnectorSettings.getOmePassword());
		uiVxRackFlexSrsConnector.setAlertFilter(srsConnectorSettings.getOmeAlertFilter());
		uiVxRackFlexSrsConnector.setAlertPollingIntervalHours(StringUtils.isNotBlank(srsConnectorSettings.getDelayTimeH()) ? Integer.parseInt(srsConnectorSettings.getDelayTimeH()) : 0);
		uiVxRackFlexSrsConnector.setAlertPollingIntervalMinutes(StringUtils.isNotBlank(srsConnectorSettings.getDelayTimeM()) ? Integer.parseInt(srsConnectorSettings.getDelayTimeM()) : 0);
		uiVxRackFlexSrsConnector.setElmsSoftwareId(srsConnectorSettings.getSoftware_id());
		uiVxRackFlexSrsConnector.setSolutionSerialNumber(srsConnectorSettings.getSolution_sn());
		uiVxRackFlexSrsConnector.setSrsUserId(srsConnectorSettings.getRegisterUserID());
		uiVxRackFlexSrsConnector.setSrsPassword(srsConnectorSettings.getRegisterPassword());
		uiVxRackFlexSrsConnector.setSrsGatewayHostIp(srsConnectorSettings.getEsrsHost());
		uiVxRackFlexSrsConnector.setDeviceType(srsConnectorSettings.getDeviceType());
		uiVxRackFlexSrsConnector.setSrsGatewayHostPort(Integer.parseInt(srsConnectorSettings.getEsrsHostPort()));
		uiVxRackFlexSrsConnector.setSuspendedUntil(srsConnectorSettings.getRestart());
        //Disabling Phone Home for basalt. Connection type will always be 'srs'
        uiVxRackFlexSrsConnector.setConnectionType(StringUtils.isBlank(srsConnectorSettings.getConnectionType())?AlertConnectorConnectionTypes.SRS.getValue():srsConnectorSettings.getConnectionType());
        uiVxRackFlexSrsConnector.setPhoneHomeIp(srsConnectorSettings.getPhoneHomeIp());
        uiVxRackFlexSrsConnector.setPhoneHomePort(Integer.parseInt(srsConnectorSettings.getPhoneHomePort()));

		return uiVxRackFlexSrsConnector;
	}

    private TroubleshootingBundleParams parseVxfmTroubleshootingBundleParams(UITroubleshootingBundle uiTroubleshootingBundle) {
        TroubleshootingBundleParams troubleshootingBundleParams = new TroubleshootingBundleParams();
        troubleshootingBundleParams.setServiceId(uiTroubleshootingBundle.getServiceId());
        troubleshootingBundleParams.setBundleDest(uiTroubleshootingBundle.getBundleDest());
        troubleshootingBundleParams.setFilepath(uiTroubleshootingBundle.getFilepath());
        troubleshootingBundleParams.setUsername(uiTroubleshootingBundle.getUsername());
        troubleshootingBundleParams.setPassword(uiTroubleshootingBundle.getPassword());

        return troubleshootingBundleParams;
    }

    /**
     * Stores license content, file name and upload status.
     */
    private class ASMLicense {
        private String licenseContent;
        private String licenseFileName;

        public ASMLicense(String licenseFileName, String licenseFileContent) {
            this.licenseContent = licenseFileContent;
            this.licenseFileName = licenseFileName;
        }

        public String getLicenseContent() {
            return licenseContent;
        }

        public void setLicenseContent(String licenseContent) {
            this.licenseContent = licenseContent;
        }

        public String getLicenseFileName() {
            return licenseFileName;
        }

        public void setLicenseFileName(String licenseFileName) {
            this.licenseFileName = licenseFileName;
        }
    }


}
