package com.dell.asm.ui.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.alcm.client.model.LicenseDetails;
import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.PuppetScaleIOSystem;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.SystemGeneral;
import com.dell.asm.asmcore.asmmanager.client.discovery.model.scaleio.SystemStatistics;
import com.dell.asm.localizablelogger.LocalizedLogMessage;
import com.dell.asm.rest.common.AsmConstants;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.LicenseServiceAdapter;
import com.dell.asm.ui.adapter.service.LogServiceAdapter;
import com.dell.asm.ui.adapter.service.PuppetDeviceServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.dashboard.UIDashboard;
import com.dell.asm.ui.model.dashboard.UIDashboardNotification;
import com.dell.asm.ui.model.dashboard.UIDashboardStorageData;
import com.dell.asm.ui.model.dashboard.UILicenseData;
import com.dell.asm.ui.model.dashboard.UIScaleIODashboard;
import com.dell.asm.ui.model.dashboard.UIScaleIOStorageComponent;
import com.dell.asm.ui.model.dashboard.UIServicesLandingPageData;
import com.dell.asm.ui.model.service.UIServiceHealth;
import com.dell.asm.ui.util.ConversionUtility;
import com.dell.asm.ui.util.MappingUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/dashboard/")
public class DashboardController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(NetworksController.class);

    private static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

    private static final Integer NUMBER_OF_LOG_MESSAGES_TO_DISPLAY = 5;
    private static final String DEVICETYPE_COLUMN = "deviceType";
    private static final String OPER_EQ = "eq";
    private static final String VCENTER_TARGET = "vcenter";

    private LicenseServiceAdapter licenseServiceAdapter;
    private LogServiceAdapter logServiceAdapter;
    private DeploymentServiceAdapter deploymentServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private PuppetDeviceServiceAdapter puppetDeviceServiceAdapter;

    @Autowired
    private ApplianceController applianceController;

    @Autowired
    public DashboardController(LicenseServiceAdapter licenseServiceAdapter,
                               LogServiceAdapter logServiceAdapter,
                               DeploymentServiceAdapter deploymentServiceAdapter,
                               DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                               PuppetDeviceServiceAdapter puppetDeviceServiceAdapter) {
        this.licenseServiceAdapter = licenseServiceAdapter;
        this.logServiceAdapter = logServiceAdapter;
        this.deploymentServiceAdapter = deploymentServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.puppetDeviceServiceAdapter = puppetDeviceServiceAdapter;
    }

    /**
     * Get Services Dashboard Data
     *
     * @param request
     *            the request
     * @return ServicesLangingPageData
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getservicesdashboarddata", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServicesDashboardData(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();
        UIServicesLandingPageData responseObject = new UIServicesLandingPageData();
        try {
            responseObject.servicesuccesscount = 0;
            RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj, null, null);
            ResourceList<Deployment> result = deploymentServiceAdapter.getDeployments(
                    options.sortedColumns, null,
                    options.offset < 0 ? null : options.offset,
                    options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit, Boolean.FALSE);

            if (result != null && result.getList() != null) {
                if (result.getList().length > 0) {

                    // parse filter
                    GregorianCalendar dateFilter = null;
                    if (options.filterList != null) {
                        for (String filter : options.filterList) {
                            String[] farr = filter.split(",");
                            if (farr.length != 3) continue;

                            if (farr[1].equals("deployedOn")) {
                                if (farr[0].equals(">=")) {
                                    String sdateFilter = farr[2];
                                    SimpleDateFormat df = new SimpleDateFormat(
                                            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                                            LocaleContextHolder.getLocale());
                                    Date date = df.parse(sdateFilter);
                                    dateFilter = (GregorianCalendar) GregorianCalendar.getInstance(
                                            TimeZone.getTimeZone("GMT"));
                                    dateFilter.setTime(date);

                                }
                            }
                        }
                    }
                    // 2014-05-24T18:04:12.619Z

                    for (Deployment deployment : result.getList()) {
                        if (dateFilter != null) {
                            if (deployment.getCreatedDate().compareTo(dateFilter) < 0) {
                                continue;
                            }
                        }

                        responseObject.servicecount++;

                        UIServiceHealth health = ServicesController.getServiceHealth(deployment);
                        switch (health) {
                        case green:
                            responseObject.servicesuccesscount++;
                            break;
                        case red:
                            responseObject.servicecriticalcount++;
                            break;
                        case yellow:
                            responseObject.servicewarningcount++;
                            break;
                        case pending:
                            responseObject.servicependingcount++;
                            break;
                        case unknown:
                            responseObject.serviceunknowncount++;
                            break;
                        case cancelled:
                            responseObject.servicecancelledcount++;
                            break;
                        case servicemode:
                            responseObject.serviceservicemodecount++;
                            break;
                        case incomplete:
                            responseObject.serviceincompletecount++;
                            break;
                        default:
                            break;
                        }
                    }
                }
            }

            jobResponse.responseObj = responseObject;

        } catch (Throwable t) {
            log.error("getServicesDashboardData() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "getdashboardlandingpagedata", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDashboardLandingPageData(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();
        UIDashboard uiDashboard = new UIDashboard();
        try {
            // Get LicenseDetails
            LicenseDetails licenseDetails = licenseServiceAdapter.getLicenseDetails();
            Integer usedLicenseCount = getUsedLicenseCount(request);
            uiDashboard.licenseData = parseLicenseDetailsToUiLicenseData(licenseDetails,
                                                                         usedLicenseCount,
                                                                         this.getApplicationContext());

            // Per discussion with Mohammad on 1/15/2014, we do not need to populate the deploymentTemplates, as it looks like the UI is not using them.
            // Per same discussion, do not need total IP/Servers fields, either - same reason.
            uiDashboard.chassisdiscovered = this.getTotalDeviceTypesDiscovered(
                    DeviceType.getAllChassis());
            uiDashboard.serversdiscovered = this.getTotalDeviceTypesDiscovered(
                    DeviceType.getAllServers());
            uiDashboard.storagediscovered = this.getTotalDeviceTypesDiscovered(
                    DeviceType.getAllStorage());
            uiDashboard.switchesdiscovered = this.getTotalDeviceTypesDiscovered(
                    DeviceType.getAllSwitches());

            jobResponse.responseObj = uiDashboard;
        } catch (Throwable t) {
            log.error("getDashboardLandingPageData() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "getdashboardnotifications", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDashboardNotifications(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIDashboardNotification> responseObject = new ArrayList<>();

        String thisUserName = LogsController.getLoggedUserName();
        String userRole = LogsController.getLoggedUserRole();

        try {
            // Get dashboard notifications and populate result list
            String sort = "-timeStamp";
            List<String> filter = new ArrayList<>();
            Integer offset = 0;
            Integer limit = NUMBER_OF_LOG_MESSAGES_TO_DISPLAY;

            if (userRole.equals(AsmConstants.USERROLE_OPERATOR)) {
                String sFilter = "eq,userName";
                sFilter = sFilter + "," + thisUserName;

                filter.add(sFilter);
            }


            ResourceList<LocalizedLogMessage> logMessages = logServiceAdapter.getUserLogMessages(
                    sort, filter, offset, limit);
            for (LocalizedLogMessage logMessage : logMessages.getList()) {
                log.info("Parsing message: " + logMessage.getLocalizedMessage());
                responseObject.add(parseLocalizedLogMessageToUIDashboardNotification(logMessage));
            }
            log.info(String.format("getDashboardNotifications(): returning list with %d entries",
                                   responseObject.size()));
            jobResponse.responseObj = responseObject;
        } catch (Throwable t) {
            log.error("getDashboardNotifications() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "getdashboardstoragedata", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDashboardStorageData(@RequestBody JobRequest request) {

        JobResponse jobResponse = new JobResponse();


        try {
            jobResponse.responseObj = getStorageData();
        } catch (Throwable t) {
            log.error("getDashboardStorageData() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private UIDashboardStorageData getStorageData() {
        UIDashboardStorageData ret = new UIDashboardStorageData();
        // The UI call is still there, return empty object
        return ret;
    }

    private double stripSizeLabel(String value) {
        if (StringUtils.isEmpty(value)) return 0;
        String[] arr = value.split("\\s");
        try {
            if (arr.length == 1) {
                return Double.parseDouble(value);
            } else {
                return Double.parseDouble(arr[0]) * ConversionUtility.getMultiplierForGBConversion(
                        arr[1]);
            }
        } catch (NumberFormatException e) {
            log.error("Cannot parse storage size value: " + value);
            return 0;
        }
    }

    private UIDashboardNotification parseLocalizedLogMessageToUIDashboardNotification(
            LocalizedLogMessage logMessage) {
        UIDashboardNotification result = new UIDashboardNotification();
        result.message = logMessage.getLocalizedMessage();
        result.userName = logMessage.getUserName();
        result.createdOn = MappingUtils.getTime(logMessage.getTimeStamp());
        return result;
    }

    private UILicenseData parseLicenseDetailsToUiLicenseData(LicenseDetails licenseDetails,
                                                             Integer usedLicenseCount,
                                                             ApplicationContext applicationContext) {
        UILicenseData result = new UILicenseData();
        if (licenseDetails == null) {
            return result;
        }

        if (licenseDetails.getActivationDate() != null) {
            result.activationdate = MappingUtils.getTime(
                    licenseDetails.getActivationDate().toGregorianCalendar());
        }
        result.usednodes = usedLicenseCount;
        result.totalnodes = licenseDetails.getNodes();
        result.availablenodes = licenseDetails.getNodes() - usedLicenseCount;
        if (licenseDetails.getExpirationDate() != null) {
            result.expirationdate = MappingUtils.getTime(
                    licenseDetails.getExpirationDate().toGregorianCalendar());
        }
        result.signature = licenseDetails.getSignature();
        result.type = licenseDetails.getType();
        result.expired = isLicenseExpired(licenseDetails);
        result.expiressoon = licenseExpiresSoon(licenseDetails);
        result.expiressoonmessage = getLicenseExpiresSoonMessage(licenseDetails);
        licenseDetails.getId();
        return result;
    }

    private String getLicenseExpiresSoonMessage(LicenseDetails licenseDetails) {
        String message = "";
        if (licenseExpiresSoon(licenseDetails)) {
            long daysRemaining = licenseTimeLeft(licenseDetails) / MILLISECONDS_PER_DAY;

            if (daysRemaining == 0) {
                message = "License expires today.";
            } else if (daysRemaining == 1) {
                message = "License expires in 1 day.";
            } else if (daysRemaining == -1) {
                message = "License expired 1 day ago.";
            } else if (daysRemaining > 1) {
                message = String.format("License expires in %d days.", daysRemaining);
            } else {
                message = String.format("License expired %d days ago.", Math.abs(daysRemaining));
            }
        }
        return message;
    }

    private boolean licenseExpiresSoon(LicenseDetails licenseDetails) {
        boolean result = false;
        final long soon = 30 * MILLISECONDS_PER_DAY; // milliseconds in 30 days
        if (licenseTimeLeft(licenseDetails) <= soon) {
            result = true;
        }
        return result;
    }

    private long licenseTimeLeft(LicenseDetails licenseDetails) {
        XMLGregorianCalendar expirationDate = licenseDetails.getExpirationDate();
        return timeLeft(expirationDate);
    }

    private boolean isLicenseExpired(LicenseDetails licenseDetails) {
        long daysRemaining = licenseTimeLeft(licenseDetails) / MILLISECONDS_PER_DAY;
        return (daysRemaining < 0);
    }

    private long timeLeft(XMLGregorianCalendar expirationDate) {
        long result = Long.MAX_VALUE;
        if (null != expirationDate) {
            result = expirationDate.toGregorianCalendar().getTime().getTime() - new Date().getTime();
        }
        return result;
    }

    private Integer getUsedLicenseCount(JobRequest request) {
        return applianceController.getUsedLicenseCount(request);
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

    // Returns a total of DeviceTypes discovered for the given list of DeviceTypes;
    private long getTotalDeviceTypesDiscovered(List<DeviceType> deviceTypes) {
        long totalServersDiscovered = 0;

        if (deviceTypes.size() > 0) {
            // Example Query: eq,deviceType,Chassis, ChassisM1000e, ChassisFX
            StringBuilder query = new StringBuilder("eq, deviceType");
            for (DeviceType deviceType : deviceTypes) {
                query.append(", ").append(deviceType.getValue());
            }
            ArrayList<String> filters = new ArrayList<>();
            filters.add(query.toString());
            totalServersDiscovered = deviceInventoryServiceAdapter.getTotalCount(filters);
        }

        return totalServersDiscovered;
    }

    @RequestMapping(value = "getdashboardscaleiodata", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDashboardScaleIOData(@RequestBody JobRequest request) {

        JobResponse jobResponse = new JobResponse();


        try {
            UIScaleIODashboard responseObject = getScaleIODashboardData();
            jobResponse.responseObj = responseObject;
        } catch (Throwable t) {
            log.error("getDashboardScaleIOData() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private UIScaleIODashboard getScaleIODashboardData() throws IOException {
        UIScaleIODashboard scaleIODashboard = new UIScaleIODashboard();

        List<String> scaleIOFilter = new ArrayList<>();
        scaleIOFilter.add("eq,deviceType,scaleio");
        scaleIOFilter.add("eq,state,READY");

        ResourceList<ManagedDevice> scaleIODeviceList = deviceInventoryServiceAdapter.getAllDeviceInventory(null,
                                                                                                            scaleIOFilter,
                                                                                                            0,
                                                                                                            MappingUtils.MAX_RECORDS);

        ObjectMapper mapper = new ObjectMapper();

        BigInteger used = BigInteger.ZERO;
        BigInteger total = BigInteger.ZERO;
        int pdCount = 0;
        int volumes = 0;
        int sdc = 0;
        int sds = 0;

        if (scaleIODeviceList != null && scaleIODeviceList.getTotalRecords() > 0) {
            for (ManagedDevice device : scaleIODeviceList.getList()) {
                if (device != null && device.getFacts() != null) {
                    PuppetScaleIOSystem scaleIOSystem = mapper.readValue(device.getFacts(), PuppetScaleIOSystem.class);
                    SystemGeneral general = scaleIOSystem.getGeneral();
                    SystemStatistics stats = scaleIOSystem.getStatistics();
                    if (general != null && stats != null) {
                        UIScaleIOStorageComponent component = new UIScaleIOStorageComponent();
                        component.id = device.getRefId();
                        component.name = general.getName();
                        if (stats.getCapacityInUseInKb() != null) {
                            component.usedInKb = BigInteger.valueOf(stats.getCapacityInUseInKb());
                            used = used.add(component.usedInKb);
                        }
                        if (stats.getMaxCapacityInKb() != null) {
                            component.totalInKb = BigInteger.valueOf(stats.getMaxCapacityInKb());
                            total = total.add(component.totalInKb);
                        }
                        scaleIODashboard.storageComponents.add(component);

                        if (stats.getNumOfProtectionDomains() != null) {
                            pdCount += stats.getNumOfProtectionDomains();
                        }
                        if (stats.getNumOfVolumes() != null) {
                            volumes += stats.getNumOfVolumes();
                        }
                        if (stats.getNumOfSdc() != null) {
                            sdc += stats.getNumOfSdc();
                        }
                        if (stats.getNumOfSds() != null) {
                            sds += stats.getNumOfSds();
                        }
                    }
                }
            }
        }

        scaleIODashboard.usedInKb = used;
        scaleIODashboard.totalInKb = total;
        scaleIODashboard.protectionDomains = pdCount;
        scaleIODashboard.volumes = volumes;
        scaleIODashboard.dataClients = sdc;
        scaleIODashboard.dataServers = sds;

        return scaleIODashboard;
    }


}
