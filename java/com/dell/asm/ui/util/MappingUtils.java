/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.dell.asm.encryptionmgr.client.AbstractCredential;
import com.dell.asm.encryptionmgr.client.ChassisCredential;
import com.dell.asm.encryptionmgr.client.CredentialType;
import com.dell.asm.encryptionmgr.client.CredentialVisitor;
import com.dell.asm.encryptionmgr.client.EMCredential;
import com.dell.asm.encryptionmgr.client.IomCredential;
import com.dell.asm.encryptionmgr.client.OSCredential;
import com.dell.asm.encryptionmgr.client.SCVMMCredential;
import com.dell.asm.encryptionmgr.client.ScaleIOCredential;
import com.dell.asm.encryptionmgr.client.ServerCredential;
import com.dell.asm.encryptionmgr.client.StorageCredential;
import com.dell.asm.encryptionmgr.client.VCenterCredential;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.dell.asm.alcm.client.model.ProxySetting;
import com.dell.asm.asmcore.asmmanager.client.credential.AsmCredentialDTO;
import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.CompliantState;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareComplianceReport;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareComplianceReportComponent;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareComplianceReportComponentVersionInfo;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedState;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoverIPRangeDeviceRequest;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoverIPRangeDeviceRequests;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveredDevices;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveryRequest;
import com.dell.asm.asmcore.asmmanager.client.discovery.DiscoveryStatus;
import com.dell.asm.asmcore.asmmanager.client.firmware.ComponentType;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareDeviceInventory;
import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareComponent;
import com.dell.asm.asmcore.asmmanager.client.firmware.SourceType;
import com.dell.asm.asmcore.asmmanager.client.perfmonitoring.PerformanceMetric;
import com.dell.asm.asmcore.asmmanager.client.perfmonitoring.PerformanceMetricSummary;
import com.dell.asm.asmcore.asmmanager.client.util.ClientUtils;
import com.dell.asm.asmcore.asmmanager.client.vsphere.VcenterUtils;
import com.dell.asm.localizablelogger.LocalizedLogMessage;
import com.dell.asm.rest.common.AsmConstants;
import com.dell.asm.server.client.policy.RaidLevel;
import com.dell.asm.ui.adapter.service.CredentialServiceAdapter;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.FirmwareRepositoryServiceAdapter;
import com.dell.asm.ui.adapter.service.FirmwareRepositoryServiceAdapter.FirmwareRepositoryType;
import com.dell.asm.ui.adapter.service.PerformanceMonitorServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.controller.DeviceController;
import com.dell.asm.ui.exception.MappingRequestException;
import com.dell.asm.ui.model.FabricNameTypes;
import com.dell.asm.ui.model.UIActivityLog;
import com.dell.asm.ui.model.UIDeviceState;
import com.dell.asm.ui.model.UIDeviceStatus;
import com.dell.asm.ui.model.chassis.UIChassisConfiguration;
import com.dell.asm.ui.model.chassis.UIChassisConnectionCheck;
import com.dell.asm.ui.model.chassis.UIRackConfiguration;
import com.dell.asm.ui.model.credential.UICredential;
import com.dell.asm.ui.model.credential.UICredentialSummary;
import com.dell.asm.ui.model.firmware.UIFirmwareComponent;
import com.dell.asm.ui.model.firmware.UIFirmwareReportDevice;
import com.dell.asm.ui.model.firmware.UISoftwareComponent;
import com.dell.asm.ui.model.initialsetup.UIProxyData;
import com.dell.asm.ui.model.network.UINetworkIdentity;
import com.dell.asm.ui.model.server.UICPU;
import com.dell.asm.ui.model.server.UIComponentUsage;
import com.dell.asm.ui.model.server.UIFirmware;
import com.dell.asm.ui.model.server.UILocalStorage;
import com.dell.asm.ui.model.server.UILogicalDisk;
import com.dell.asm.ui.model.server.UIMemory;
import com.dell.asm.ui.model.server.UINIC;
import com.dell.asm.ui.model.server.UIPhysicalDisk;
import com.dell.asm.ui.model.server.UIServer;
import com.dell.asm.ui.model.server.UISoftware;
import com.dell.asm.ui.model.server.UIUsageData;
import com.dell.asm.ui.model.server.UIUsageDataPoint;
import com.dell.asm.ui.model.server.UIUsageDataSeries;
import com.dell.pg.asm.server.client.device.Controller;
import com.dell.pg.asm.server.client.device.LogicalNetworkIdentityInventory;
import com.dell.pg.asm.server.client.device.LogicalNetworkInterface;
import com.dell.pg.asm.server.client.device.MemoryInventory;
import com.dell.pg.asm.server.client.device.PhysicalDisk;
import com.dell.pg.asm.server.client.device.ProcessorInventory;
import com.dell.pg.asm.server.client.device.RaidStatus;
import com.dell.pg.asm.server.client.device.Server;
import com.dell.pg.asm.server.client.device.VirtualDisk;
import com.dell.pg.orion.common.utilities.VersionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class MappingUtils.
 *
 * parse*** methods apply to incoming REST NBI API data and return Controller structures. create** methods apply to Controller data and return NBI API
 * data.
 */
public final class MappingUtils {

    public static final Map<String, String> COLUMNS_CREDENTIALS = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_DEVICES = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_DEVICES_FILTER = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_USERS = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_NETWORKS = new HashMap<>();
    public static final Map<String, String> COLUMNS_POOLS = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_DIRECTORIES = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_SERVER_POOL = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_DIRECTORIE_USERS = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_DIRECTORIE_GROUPS = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_TEMPLATES = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_LOGS = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_CHASSIS = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_JOBS = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_PUPPET_LOGS = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_ADDONMODULES = new HashMap<String, String>();
    public static final Map<String, String> COLUMNS_PORTVIEW = new HashMap<>();
    private static final Logger log = Logger.getLogger(MappingUtils.class);
    public static final Date EARLIEST_ALLOWED_FIRMWARE_DATE = buildDate("January 1, 2010");
    /**
     * it will be used to calculate number of returned records. Some reasonably large number that we may never reach.
     */
    public static int MAX_RECORDS = 1000000; // can't make Integer.MAX_VALUE as service may add offset to it. Integer.MAX_VALUE + 1 == Integer.MIN_VALUE

    static {
        COLUMNS_CREDENTIALS.put("credentialsName",
                                "label");
        COLUMNS_CREDENTIALS.put("numberOfDevices",
                                "numReferencingDevices");
        COLUMNS_CREDENTIALS.put("typeName",
                                "type");
        COLUMNS_CREDENTIALS.put("typeId",
                                "type");
        COLUMNS_CREDENTIALS.put("username",
                                "username");

        COLUMNS_DEVICES.put("id",
                            "refId");
        COLUMNS_DEVICES.put("deviceType",
                            "resourceType"); // use resource type for sorting only
        COLUMNS_DEVICES.put("ipAddress",
                            "ipAddress");
        COLUMNS_DEVICES.put("servicetag",
                            "serviceTag");
        COLUMNS_DEVICES.put("deviceid",
                            "serviceTag");
        COLUMNS_DEVICES.put("manufacturer",
                            "vendor");
        COLUMNS_DEVICES.put("dnsdracname",
                            "displayName");
        COLUMNS_DEVICES.put("state",
                            "managedState");

        COLUMNS_DEVICES_FILTER.put("id",
                                   "refId");
        COLUMNS_DEVICES_FILTER.put("ipAddress",
                                   "ipAddress");
        COLUMNS_DEVICES_FILTER.put("servicetag",
                                   "serviceTag");
        COLUMNS_DEVICES_FILTER.put("deviceid",
                                   "serviceTag");
        COLUMNS_DEVICES_FILTER.put("manufacturer",
                                   "vendor");
        COLUMNS_DEVICES_FILTER.put("state",
                                   "managedState");

        COLUMNS_USERS.put("id",
                          "userSeqId");
        COLUMNS_USERS.put("username",
                          "userName");
        COLUMNS_USERS.put("firstname",
                          "firstName");
        COLUMNS_USERS.put("lastname",
                          "lastName");
        COLUMNS_USERS.put("state",
                          "enabled");
        COLUMNS_USERS.put("email",
                          "email");
        COLUMNS_USERS.put("serverName",
                          "domainName");
        COLUMNS_USERS.put("phone",
                          "phoneNumber");
        COLUMNS_USERS.put("roleDisplay",
                          "role");
        COLUMNS_USERS.put("rolename",
                          "role");
        COLUMNS_USERS.put("roleId",
                          "role");
        COLUMNS_USERS.put("group",
                          "group");

        COLUMNS_NETWORKS.put("id",
                             "id");
        COLUMNS_NETWORKS.put("name",
                             "name");
        COLUMNS_NETWORKS.put("description",
                             "description");
        COLUMNS_NETWORKS.put("typeid",
                             "type");
        COLUMNS_NETWORKS.put("vlanid",
                             "vlanId");

        COLUMNS_POOLS.put("id",
                          "id");
        COLUMNS_POOLS.put("name",
                          "name");
        COLUMNS_POOLS.put("des",
                          "description");
        COLUMNS_POOLS.put("createddate",
                          "createdDate");
        COLUMNS_POOLS.put("createdby",
                          "createdBy");

        COLUMNS_SERVER_POOL.put("id",
                                "id");
        COLUMNS_SERVER_POOL.put("name",
                                "name");
        COLUMNS_SERVER_POOL.put("createddate",
                                "createdDate");
        COLUMNS_SERVER_POOL.put("createdby",
                                "createdBy");
        COLUMNS_SERVER_POOL.put("servers",
                                "servers");
        COLUMNS_SERVER_POOL.put("users",
                                "users");

        //AD directorty
        COLUMNS_DIRECTORIES.put("id",
                                "userId");
        COLUMNS_DIRECTORIES.put("servername",
                                "");
        COLUMNS_DIRECTORIES.put("username",
                                "name");
        COLUMNS_DIRECTORIES.put("state",
                                "");

        //AD user

        COLUMNS_DIRECTORIE_USERS.put("username",
                                     "userId");
        COLUMNS_DIRECTORIE_USERS.put("firstname",
                                     "firstName");
        COLUMNS_DIRECTORIE_USERS.put("lastname",
                                     "lastName");

        COLUMNS_DIRECTORIE_GROUPS.put("groupName",
                                      "name");
        COLUMNS_DIRECTORIE_GROUPS.put("groupDescription",
                                      "description");
        COLUMNS_DIRECTORIE_GROUPS.put("groupDN",
                                      "distinguishedName");
        COLUMNS_DIRECTORIE_GROUPS.put("groupCN",
                                      "cn");
        COLUMNS_DIRECTORIE_GROUPS.put("members",
                                      "members");
        COLUMNS_DIRECTORIE_GROUPS.put("memberOf",
                                      "memberOf");

        COLUMNS_TEMPLATES.put("name",
                              "name");
        COLUMNS_TEMPLATES.put("createddate",
                              "createdDate");
        COLUMNS_TEMPLATES.put("createdby",
                              "createdBy");
        COLUMNS_TEMPLATES.put("updateddate",
                              "updatedDate");
        COLUMNS_TEMPLATES.put("updatedby",
                              "updatedBy");

        COLUMNS_LOGS.put("severity",
                         "severity");
        COLUMNS_LOGS.put("category",
                         "category");
        COLUMNS_LOGS.put("description",
                         "messageCode");
        COLUMNS_LOGS.put("date",
                         "timeStamp");
        COLUMNS_LOGS.put("user",
                         "userName");

        COLUMNS_PUPPET_LOGS.put("category",
                                "category");

        COLUMNS_JOBS.put("name",
                         "name");
        COLUMNS_JOBS.put("startDate",
                         "startDate");
        COLUMNS_JOBS.put("description",
                         "messageCode");
        COLUMNS_JOBS.put("date",
                         "timeStamp");
        COLUMNS_JOBS.put("user",
                         "userName");
        COLUMNS_JOBS.put("status",
                         "status");

        COLUMNS_CHASSIS.put("id",
                            "refId");

        COLUMNS_ADDONMODULES.put("id",
                                 "id");
        COLUMNS_ADDONMODULES.put("name",
                                 "name");
        COLUMNS_ADDONMODULES.put("description",
                                 "description");
        COLUMNS_ADDONMODULES.put("filename",
                                 "fileName");
        COLUMNS_ADDONMODULES.put("filepath",
                                 "filePath");
        COLUMNS_ADDONMODULES.put("version",
                                 "version");
        COLUMNS_ADDONMODULES.put("isInUse",
                                 "active");
        COLUMNS_ADDONMODULES.put("uploadedBy",
                                 "uploadedBy");
        COLUMNS_ADDONMODULES.put("uploadedDate",
                                 "uploadedDate");
    }

    /**
     * Utility classes should not have default constructor.
     */
    private MappingUtils() {
    }

    public static Date buildDate(String dateString) {
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy",
                                                 Locale.ENGLISH);
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            log.error("buildDate() - Exception while parsing date string");
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the date.
     *
     * @param calendar
     *            the calendar
     * @return the date
     */
    public static String getDate(GregorianCalendar calendar) {
        if (calendar == null) return null;
        DateTime date = new DateTime(calendar.getTimeInMillis(),
                                     DateTimeZone.UTC);
        // DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        return date.toString();
    }

    /**
     * Gets the time.
     *
     * @param calendar
     *            the calendar
     * @return the time
     */
    public static String getTime(GregorianCalendar calendar) {
        if (calendar == null) return null;
        DateTime date = new DateTime(calendar.getTimeInMillis(),
                                     DateTimeZone.UTC);
        // DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        return date.toString();
    }

    public static String getTime(Date input) {
        if (input == null) return null;
        DateTime date = new DateTime(input,
                                     DateTimeZone.UTC);
        return date.toString();
    }

    /**
     * Gets the time short.
     *
     * @param calendar
     *            the calendar
     * @return the time short
     */
    public static String getTimeShort(GregorianCalendar calendar) {
        if (calendar == null) return null;
        DateTime date = new DateTime(calendar.getTimeInMillis(),
                                     DateTimeZone.UTC);
        // DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        return date.toString();
    }

    /**
     * Gets the checks if is o8601 time.
     *
     * @param calendar
     *            the calendar
     * @return the checks if is o8601 time
     */
    public static String getISO8601Time(GregorianCalendar calendar) {
        DateTime date = new DateTime(calendar.getTimeInMillis(),
                                     DateTimeZone.UTC);
        // DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        return date.toString();
    }

    /**
     * Gets the gregorian calendar.
     *
     * @param date
     *            the date
     * @param time
     *            the time
     * @param hour
     *            the hour
     * @return the gregorian calendar
     */
    public static XMLGregorianCalendar getGregorianCalendar(String date,
                                                            String time,
                                                            boolean hour) {
        XMLGregorianCalendar result = null;
        String timeFormat = "hh:mm:ss";
        if (hour) {
            timeFormat = "HH:mm:ss";
        }
        String vDate = date;
        if (vDate == null) {
            Date temp = new Date();
            SimpleDateFormat dformat = new SimpleDateFormat("MM/dd/yyyy");
            vDate = dformat.format(temp);
        }
        String vTime = time;
        if (vTime == null) {
            Date temp = new Date();
            SimpleDateFormat tformat = new SimpleDateFormat(timeFormat);
            vTime = tformat.format(temp);
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy " + timeFormat);
        LocalDateTime dateTime = formatter.parseLocalDateTime(vDate + " " + vTime);
        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(
                TimeZone.getTimeZone("GMT"));
        cal.setTime(dateTime.toDate());
        try {
            result = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Checks if is new password.
     *
     * @param password
     *            the password
     * @return true, if is new password
     */
    public static boolean isNewPassword(String password) {
        boolean newPassword = false;
        if (password != null && !password.equals("$PASSWORD$"))
            newPassword = true;
        return newPassword;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public static String getPassword() {
        return "$PASSWORD$";
    }

    /**
     * Creates the proxy setting.
     *
     * @param data
     *            the data
     * @return the proxy setting
     */
    public static ProxySetting createProxySetting(UIProxyData data) {
        ProxySetting proxy = null;
        if (data != null) {
            proxy = new ProxySetting();
            if (data.enableProxy)
                proxy.setEnabled(data.enableProxy);
            if (data.proxyServer != null)
                proxy.setProxyServer(data.proxyServer);
            proxy.setPort(Integer.toString(data.proxyPort));
            proxy.setUserCredentialEnabled(data.enableProxyCredentials);
            if (data.proxyUsername != null)
                proxy.setUserName(data.proxyUsername);
            if (MappingUtils.isNewPassword(data.proxyPassword))
                proxy.setPassword(data.proxyPassword);
        }
        return proxy;
    }

    /**
     * Helper for DiscoverIPRangeDeviceRequests.
     *
     * @param checks
     *            of IP ranges to discover devices.
     * @return
     */
    public static DiscoveryRequest createDiscoverIPRangeDeviceRequests(List<UIChassisConnectionCheck> checks)
            throws MappingRequestException {

        DiscoveryRequest dReq = new DiscoveryRequest();

        DiscoverIPRangeDeviceRequests requests = new DiscoverIPRangeDeviceRequests();
        Set<DiscoverIPRangeDeviceRequest> reqs = requests.getDiscoverIpRangeDeviceRequests();
        DiscoverIPRangeDeviceRequest req = null;

        for (UIChassisConnectionCheck check : checks) {

            if (StringUtils.isEmpty(check.resourcetype)) continue;

            long starting = -1;
            long ending = -1;

            String startingIpAddress = "";
            String endingIpAddress = "";

            if (StringUtils.isBlank(check.startingIpAddress)) {
                throw new MappingRequestException("validationError.InvalidIPAddress");
            }

            startingIpAddress = check.startingIpAddress.trim();

            if (StringUtils.isBlank(check.endingIpAddress)) {
                endingIpAddress = startingIpAddress;
            } else {
                endingIpAddress = check.endingIpAddress.trim();
            }

            try {
                starting = ConversionUtility.convertIpStringToLong(startingIpAddress);
            } catch (IllegalArgumentException ex) {
                throw new MappingRequestException("validationError.InvalidIPAddress");
            }

            try {
                ending = ConversionUtility.convertIpStringToLong(endingIpAddress);
            } catch (IllegalArgumentException ex) {
                throw new MappingRequestException("validationError.InvalidIPAddress");
            }

            if (ending > 0) {
                if (ending < starting) {
                    throw new MappingRequestException("validationError.InvalidIPRange");
                }

                req = new DiscoverIPRangeDeviceRequest();
                req.setDeviceStartIp(startingIpAddress);
                req.setDeviceEndIp(endingIpAddress);
                req.setServerPoolId(check.serverPoolId);

                if (check.resourcetype.equalsIgnoreCase("chassis")) {
                    req.setDeviceChassisCredRef(check.chassisCredentialId);
                    req.setDeviceServerCredRef(check.bladeCredentialId);
                    req.setDeviceSwitchCredRef(check.iomCredentialId);
                    req.setDeviceType(DeviceType.Chassis);
                } else if (check.resourcetype.equalsIgnoreCase("server")) {
                    req.setDeviceServerCredRef(check.serverCredentialId);
                    req.setDeviceType(DeviceType.Server);
                } else if (check.resourcetype.equalsIgnoreCase("storage")) {
                    req.setDeviceStorageCredRef(check.storageCredentialId);
                    req.setDeviceType(DeviceType.storage);
                } else if (check.resourcetype.equalsIgnoreCase("vcenter")) {
                    req.setDeviceVCenterCredRef(check.vcenterCredentialId);
                    req.setDeviceType(DeviceType.vcenter);
                } else if (check.resourcetype.equalsIgnoreCase("switch")) {
                    req.setDeviceSwitchCredRef(check.torCredentialId);
                    req.setDeviceType(DeviceType.genericswitch);
                } else if (check.resourcetype.equalsIgnoreCase("scvmm")) {
                    req.setDeviceSCVMMCredRef(check.scvmmCredentialId);
                    req.setDeviceType(DeviceType.scvmm);
                } else if (check.resourcetype.equalsIgnoreCase("em")) {
                    req.setDeviceEMCredRef(check.emCredentialId);
                    req.setDeviceType(DeviceType.em);
                } else if (check.resourcetype.equalsIgnoreCase("scaleio")) {
                    req.setDeviceScaleIOCredRef(check.scaleioCredentialId);
                    req.setDeviceType(DeviceType.scaleio);
                }

                else {
                    // bogus request, ignore
                    continue;
                }

                if (check.managedstate != null) {
                    if (check.managedstate.equals(ManagedState.UNMANAGED.getLabel())) {
                        req.setUnmanaged(true);
                    } else if (check.managedstate.equals(ManagedState.RESERVED.getLabel())) {
                        req.setReserved(true);
                    }
                }
                reqs.add(req);
            } else {
                throw new MappingRequestException("validationError.InvalidIPRange");
            }
        }

        dReq.setDiscoveryRequestList(requests);
        return dReq;
    }

    /**
     * Parse result of discovery devices by IP range into chassis configuration.
     *
     * @param result
     *            Discovery Result
     * @return UI Chassis Configuration
     */
    public static UIChassisConfiguration parseDiscoveryResult(DiscoveredDevices result,
                                                              FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter) {
        UIChassisConfiguration uic = new UIChassisConfiguration();

        uic.deviceType = DeviceController.getDeviceType(result.getDeviceType());
        uic.numberOfIOMs = String.valueOf(result.getIomCount());
        uic.ipAddress = result.getIpAddress();
        uic.model = result.getModel();
        uic.chassisConfigurationId = result.getDeviceRefId();
        uic.blades = String.valueOf(result.getServerCount());
        uic.servicetag = result.getServiceTag();
        if (DiscoveryStatus.FAILED.equals(result.getStatus()) ||
                DiscoveryStatus.ERROR.equals(result.getStatus()) ||
                DiscoveryStatus.UNSUPPORTED.equals(result.getStatus())) {
            uic.status = DiscoveryStatus.ERROR.getValue().toLowerCase();
        } else {
            uic.status = result.getStatus().getValue();
        }
        uic.statusDescription = result.getStatusMessage();
        uic.isCompliant = true;
        uic.isUnsupported = false;
        uic.selected = false;
        uic.firmwareStatus = CompliantState.COMPLIANT.getLabel();
        uic.manufacturer = result.getVendor();

        // need it for discovery results table
        if (result.getServiceTag() != null)
            uic.deviceid = result.getServiceTag();

        if (result.getStatus().equals(DiscoveryStatus.UNSUPPORTED)) {
            uic.valid = false;
            uic.isUnsupported = true;
        } else if (result.getStatus().equals(DiscoveryStatus.FAILED) || result.getStatus().equals(
                DiscoveryStatus.ERROR)) {
            uic.valid = false;
        } else {
            uic.valid = true;
            uic.selected = true;
        }

        if (result.isUnmanaged()) {
            uic.state = UIDeviceState.UNMANAGED.getLabel();
        } else if (result.isReserved()) {
            uic.state = UIDeviceState.RESERVED.getLabel();
        } else {
            uic.state = UIDeviceState.MANAGED.getLabel();
        }


        List<UIFirmwareComponent> firmwareComponents = new ArrayList<UIFirmwareComponent>();
        uic.firmwarecomponents = firmwareComponents;

        if (result.getFirmwareDeviceInventories() != null && firmwareRepositoryServiceAdapter != null)
            for (FirmwareDeviceInventory fdi : result.getFirmwareDeviceInventories()) {
                mapFirmware(firmwareRepositoryServiceAdapter,
                            firmwareComponents,
                            fdi,
                            uic,
                            result.getModel(),
                            result.getDeviceType().getValue());
            }


        return uic;
    }

    /**
     * Parse result of discovery devices by IP range into rack configuration.
     *
     * @param result
     *            Discovery Result
     * @return UI Chassis Configuration
     */
    public static UIRackConfiguration parseDiscoveryResultForRack(DiscoveredDevices result,
                                                                  ApplicationContext aContext) {
        UIRackConfiguration uic = new UIRackConfiguration();

        uic.rackConfigurationId = result.getDeviceRefId();
        uic.ipaddress = result.getIpAddress();
        uic.model = result.getModel();
        uic.servicetag = uic.deviceid = result.getServiceTag();
        if (DiscoveryStatus.FAILED.equals(result.getStatus()) ||
                DiscoveryStatus.ERROR.equals(result.getStatus()) ||
                DiscoveryStatus.UNSUPPORTED.equals(result.getStatus())) {
            uic.status = DiscoveryStatus.ERROR.getValue().toLowerCase();
        } else {
            uic.status = result.getStatus().getValue();
        }
        uic.deviceType = DeviceController.getDeviceType(result.getDeviceType());

        return uic;
    }

    public static void mapFirmware(
            FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter,
            List<UIFirmwareComponent> firmwareResult,
            FirmwareDeviceInventory firmwareDeviceInventory,
            UIChassisConfiguration uic,
            String model,
            String type) {
        UIFirmwareComponent uifc = new UIFirmwareComponent();
        firmwareResult.add(uifc);

        uifc.compliant = true;
        uifc.id = firmwareDeviceInventory.getId();
        uifc.name = firmwareDeviceInventory.getName();
        uifc.model = model;
        uifc.type = type;

        com.dell.asm.ui.model.firmware.UIFirmware fw = new com.dell.asm.ui.model.firmware.UIFirmware();

        fw.firmwarename = firmwareDeviceInventory.getName();
        fw.firmwareversion = firmwareDeviceInventory.getVersion();
        fw.firmwaretype = firmwareDeviceInventory.getComponentType();
        fw.id = firmwareDeviceInventory.getId();
        uifc.currentversion = fw;

        fw = new com.dell.asm.ui.model.firmware.UIFirmware();
        uifc.targetversion = fw;

        fw.firmwarename = firmwareDeviceInventory.getName();
        fw.firmwaretype = firmwareDeviceInventory.getComponentType();
        fw.id = firmwareDeviceInventory.getId();

        setDesiredCompliance(firmwareRepositoryServiceAdapter,
                             firmwareDeviceInventory,
                             fw,
                             uic,
                             uifc);
    }

    /**
     * Utility method to break out the logic for setting compliance levels of various objects.  It's a bit complex.
     * @param firmwareRepositoryServiceAdapter
     * @param fdi
     * @param fw
     * @param uic
     * @param uifc
     */
    private static void setDesiredCompliance(
            FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter,
            FirmwareDeviceInventory fdi,
            com.dell.asm.ui.model.firmware.UIFirmware fw,
            UIChassisConfiguration uic,
            UIFirmwareComponent uifc) {

        SoftwareComponent embeded = null;
        SoftwareComponent defaultSC = null;

        List<SoftwareComponent> components = firmwareRepositoryServiceAdapter.getSoftwareComponents(
                fdi.getComponentID(),
                fdi.getDeviceID(),
                fdi.getSubdeviceID(),
                fdi.getVendorID(),
                fdi.getSubvendorID(),
                FirmwareRepositoryType.EMBEDED.toString(),
                fdi.getSystemId());
        if (components != null && components.size() > 0)
            embeded = components.get(0);

        components = firmwareRepositoryServiceAdapter.getSoftwareComponents(fdi.getComponentID(),
                                                                            fdi.getDeviceID(),
                                                                            fdi.getSubdeviceID(),
                                                                            fdi.getVendorID(),
                                                                            fdi.getSubvendorID(),
                                                                            FirmwareRepositoryType.DEFAULT.toString(),
                                                                            fdi.getSystemId());
        if (components != null && components.size() > 0)
            defaultSC = components.get(0);


        if (defaultSC != null)
            fw.firmwareversion = defaultSC.getVendorVersion();
        else if (embeded != null)
            fw.firmwareversion = embeded.getVendorVersion();
        else
            fw.firmwareversion = "NA";

        if (embeded != null) {
            if (VersionUtils.compareVersions(fdi.getVersion(),
                                             embeded.getVendorVersion()) >= 0) {
                if (defaultSC != null) {
                    if (VersionUtils.compareVersions(fdi.getVersion(),
                                                     defaultSC.getVendorVersion()) == 0) {
                        //ok
                    } else {
                        uic.isCompliant = false;
                        uifc.compliant = false;
                        if (!"updaterequired".equals(uic.firmwareStatus))
                            uic.firmwareStatus = "noncompliant";
                    }
                } else {
                    //ok
                }
            } else {
                log.debug(
                        "Update required embededversion " + embeded.getVendorVersion() + " device version: " + fdi.getVersion() + " comparison " + VersionUtils.compareVersions(
                                fdi.getVersion(),
                                embeded.getDellVersion()));
                uic.isCompliant = false;
                uifc.compliant = false;
                uic.firmwareStatus = "updaterequired";
            }
        } else {
            uic.firmwareStatus = "updaterequired";
        }

    }

    /**
     * do not translate!
     * @param type
     * @return
     */
    public static DeviceType getManagedDeviceType(String type) {

        if (type != null) {
            // the type.equals must match case as specified in Asm Documentation.pdf
            if (type.equals("ChassisM1000e"))
                return DeviceType.ChassisM1000e;
            else if (type.equals("RackServer"))
                return DeviceType.RackServer;
            else if (type.equals("AggregatorIOM"))
                return DeviceType.AggregatorIOM;
            else if (type.equals("MXLIOM"))
                return DeviceType.MXLIOM;
            else if (type.equals("BladeServer"))
                return DeviceType.BladeServer;
            else if (type.equals("vcenter"))
                return DeviceType.vcenter;
            else if (type.equals("Server"))
                return DeviceType.Server;
            else if (type.equals("genericswitch"))
                return DeviceType.genericswitch;
            else if (type.equals("TOR"))
                return DeviceType.TOR;
            else if (type.equals("dellswitch"))
                return DeviceType.dellswitch;
            else if (type.equals("storage"))
                return DeviceType.storage;
            else if (type.equals("scvmm"))
                return DeviceType.scvmm;
            else if (type.equals("ChassisFX"))
                return DeviceType.ChassisFX;
            else if (type.equals("FXServer"))
                return DeviceType.FXServer;
            else if (type.equals("TowerServer"))
                return DeviceType.TowerServer;
            else if (type.equals("scaleio"))
                return DeviceType.scaleio;
            else if (type.equals("rhvm"))
                return DeviceType.rhvm;

        }
        return DeviceType.unknown;
    }

    /**
     * Helper for ADD/UPDATE credentials.
     *
     *
     * @param credentialData
     *            the credential data
     * @return the asm credential dto
     */
    public static AsmCredentialDTO createDTOFromUICredential(UICredential credentialData) {
        AsmCredentialDTO dto = new AsmCredentialDTO();

        AbstractCredential cdto;

        switch (credentialData.typeId) {
        case UICredential.TYPE_CHASSIS:
            cdto = new ChassisCredential();
            cdto.setLabel(credentialData.credentialsName);
            cdto.setUsername(credentialData.username);
            if (!UICredential.CURRENT_PASSWORD.equals(credentialData.password))
                cdto.setPassword(credentialData.password);
            cdto.setId(credentialData.id);
            dto.setCredential(cdto);
            break;
        case UICredential.TYPE_SERVER:
            cdto = new ServerCredential();
            cdto.setLabel(credentialData.credentialsName);
            cdto.setUsername(credentialData.username);
            if (!UICredential.CURRENT_PASSWORD.equals(credentialData.password))
                cdto.setPassword(credentialData.password);
            cdto.setId(credentialData.id);
            dto.setCredential(cdto);
            break;
        case UICredential.TYPE_IOM:
            IomCredential iomc = new IomCredential();
            iomc.setLabel(credentialData.credentialsName);
            iomc.setUsername(credentialData.username);
            if (!UICredential.CURRENT_PASSWORD.equals(credentialData.password))
                iomc.setPassword(credentialData.password);
            iomc.setId(credentialData.id);
            iomc.setSnmpCommunityString(credentialData.communityString);
            if (StringUtils.isNotBlank(credentialData.credentialProtocol))
                iomc.setProtocol(credentialData.credentialProtocol.toUpperCase());
            dto.setCredential(iomc);
            break;
        case UICredential.TYPE_STORAGE:
            StorageCredential storc = new StorageCredential();
            storc.setLabel(credentialData.credentialsName);
            storc.setUsername(credentialData.username);
            if (!UICredential.CURRENT_PASSWORD.equals(credentialData.password))
                storc.setPassword(credentialData.password);
            storc.setSnmpCommunityString(credentialData.communityString);
            storc.setId(credentialData.id);
            dto.setCredential(storc);
            break;
        case UICredential.TYPE_VCENTER:
            cdto = new VCenterCredential();
            cdto.setLabel(credentialData.credentialsName);
            cdto.setUsername(credentialData.username);
            if (!UICredential.CURRENT_PASSWORD.equals(credentialData.password))
                cdto.setPassword(credentialData.password);
            cdto.setId(credentialData.id);
            cdto.setDomain(credentialData.domain);
            dto.setCredential(cdto);
            break;
        case UICredential.TYPE_SCVMM:
            cdto = new SCVMMCredential();
            cdto.setLabel(credentialData.credentialsName);
            cdto.setUsername(credentialData.username);
            if (!UICredential.CURRENT_PASSWORD.equals(credentialData.password))
                cdto.setPassword(credentialData.password);
            cdto.setId(credentialData.id);
            cdto.setDomain(credentialData.domain);
            dto.setCredential(cdto);
            break;
        case UICredential.TYPE_EM:
            cdto = new EMCredential();
            cdto.setLabel(credentialData.credentialsName);
            cdto.setUsername(credentialData.username);
            cdto.setDomain(credentialData.domain);
            if (!UICredential.CURRENT_PASSWORD.equals(credentialData.password))
                cdto.setPassword(credentialData.password);
            cdto.setId(credentialData.id);
            dto.setCredential(cdto);
            break;
        case UICredential.TYPE_SCALEIO:
            ScaleIOCredential sioc = new ScaleIOCredential();
            sioc = new ScaleIOCredential();
            sioc.setLabel(credentialData.credentialsName);
            sioc.setUsername(credentialData.username);
            if (credentialData.password != null &&
                    !UICredential.CURRENT_PASSWORD.equals(credentialData.password))
                sioc.setPassword(credentialData.password);
            sioc.setOsUsername(credentialData.gatewayosusername);
            if (credentialData.gatewayospassword != null &&
                    !UICredential.CURRENT_PASSWORD.equals(credentialData.gatewayospassword))
                sioc.setOsPassword(credentialData.gatewayospassword);
            sioc.setId(credentialData.id);
            dto.setCredential(sioc);
            break;
        case UICredential.TYPE_OS:
            cdto = new OSCredential();
            cdto.setLabel(credentialData.credentialsName);
            cdto.setUsername(credentialData.username);
            if (!UICredential.CURRENT_PASSWORD.equals(credentialData.password))
                cdto.setPassword(credentialData.password);
            cdto.setId(credentialData.id);
            dto.setCredential(cdto);
            break;
        }

        return dto;
    }

    /**
     * AsmCredentialDTO to UICredentialSummary.
     *
     * @param dto
     *            the dto
     * @param aContext
     *            the a context
     * @return the uI credential summary
     */
    public static UICredentialSummary parseAsmCredentialDTO(final AsmCredentialDTO dto,
                                                            final ApplicationContext aContext) {
        final UICredentialSummary uic = new UICredentialSummary();

        final AbstractCredential credential = dto.getCredential();
        credential.accept(new CredentialVisitor() {
            @Override
            public void visit(ChassisCredential credential) {
                uic.typeName = aContext.getMessage("credType.Chassis",
                                                   null,
                                                   LocaleContextHolder.getLocale());
                uic.id = credential.getId();
            }

            @Override
            public void visit(IomCredential credential) {
                uic.typeName = aContext.getMessage("credType.Iom",
                                                   null,
                                                   LocaleContextHolder.getLocale());
                uic.id = credential.getId();
            }

            @Override
            public void visit(ServerCredential credential) {
                uic.typeName = aContext.getMessage("credType.Server",
                                                   null,
                                                   LocaleContextHolder.getLocale());
                uic.id = credential.getId();
            }

            @Override
            public void visit(StorageCredential credential) {
                uic.typeName = aContext.getMessage("credType.Storage",
                                                   null,
                                                   LocaleContextHolder.getLocale());
                uic.id = credential.getId();
            }

            @Override
            public void visit(VCenterCredential credential) {
                uic.typeName = aContext.getMessage("credType.vCenter",
                                                   null,
                                                   LocaleContextHolder.getLocale());
                uic.id = credential.getId();
            }

            @Override
            public void visit(SCVMMCredential credential) {
                uic.typeName = aContext.getMessage("credType.SCVMM",
                                                   null,
                                                   LocaleContextHolder.getLocale());
                uic.id = credential.getId();
            }

            @Override
            public void visit(EMCredential credential) {
                uic.typeName = aContext.getMessage("credType.EM",
                                                   null,
                                                   LocaleContextHolder.getLocale());
                uic.id = credential.getId();
            }

            @Override
            public void visit(ScaleIOCredential credential) {
                uic.typeName = aContext.getMessage("credType.scaleIO",
                                                   null,
                                                   LocaleContextHolder.getLocale());
                uic.id = credential.getId();
            }

            @Override
            public void visit(OSCredential credential) {
                uic.typeName = aContext.getMessage("credType.OS",
                        null,
                        LocaleContextHolder.getLocale());
                uic.id = credential.getId();
            }

        });

        uic.domain = credential.getDomain();
        uic.credentialsName = credential.getLabel();
        uic.candelete = true;
        uic.canedit = true;
        int nDevices = dto.getReferences() == null ? 0 : dto.getReferences().getDevices();
        uic.numberOfDevices = nDevices;

        return uic;
    }

    /**
     * AsmCredentialDTO to UICredential.
     *
     * @param dto
     *            the dto
     * @return the uI credential
     */
    public static UICredential parseAsmCredentialDTO(AsmCredentialDTO dto) {
        final UICredential uic = new UICredential();

        final AbstractCredential cdto = dto.getCredential();
        cdto.accept(new CredentialVisitor() {
            @Override
            public void visit(ChassisCredential credential) {
                uic.typeId = UICredential.TYPE_CHASSIS;
                uic.username = credential.getUsername();
                if (credential.getPassword() != null)
                    uic.password = credential.getPassword();

                if (credential.getLabel().equals(
                        com.dell.pg.asm.chassis.client.ClientUtils.DEFAULT_CREDENTIAL_LABEL)) {
                    uic.canedit = false;
                    uic.candelete = false;
                }
            }

            @Override
            public void visit(IomCredential credential) {
                uic.typeId = UICredential.TYPE_IOM;

                uic.username = credential.getUsername();
                if (credential.getPassword() != null)
                    uic.password = credential.getPassword();

                uic.communityString = credential.getSnmpCommunityString();
                if (StringUtils.isNotBlank(credential.getProtocol()))
                    uic.credentialProtocol = credential.getProtocol().toLowerCase();
                else
                    uic.credentialProtocol = UICredential.PROTOCOL_SSH.toLowerCase();

                if (credential.getLabel().equals(ClientUtils.DEFAULT_IOM_CREDENTIAL_LABEL)) {
                    uic.canedit = false;
                    uic.candelete = false;
                }

            }

            @Override
            public void visit(ServerCredential credential) {
                uic.typeId = UICredential.TYPE_SERVER;
                uic.username = credential.getUsername();
                if (credential.getPassword() != null)
                    uic.password = credential.getPassword();

                if (credential.getLabel().equals(
                        com.dell.pg.asm.server.client.ClientUtils.DEFAULT_CREDENTIAL_LABEL) ||
                        credential.getLabel().equals(
                                com.dell.pg.asm.server.client.ClientUtils.DEFAULT_BMC_CREDENTIAL_LABEL)) {
                    uic.canedit = false;
                    uic.candelete = false;
                }

            }

            @Override
            public void visit(StorageCredential credential) {
                uic.typeId = UICredential.TYPE_STORAGE;
                uic.username = credential.getUsername();
                if (credential.getPassword() != null)
                    uic.password = credential.getPassword();

                uic.communityString = credential.getSnmpCommunityString();

            }

            @Override
            public void visit(VCenterCredential credential) {
                uic.typeId = UICredential.TYPE_VCENTER;
                uic.username = credential.getUsername();
                if (credential.getPassword() != null)
                    uic.password = credential.getPassword();

                if (credential.getLabel().equals(VcenterUtils.DEFAULT_CREDENTIAL_LABEL)) {
                    uic.canedit = false;
                    uic.candelete = false;
                }
            }

            @Override
            public void visit(SCVMMCredential credential) {
                uic.typeId = UICredential.TYPE_SCVMM;
                uic.username = credential.getUsername();
                if (credential.getPassword() != null)
                    uic.password = credential.getPassword();
            }

            @Override
            public void visit(EMCredential credential) {
                uic.typeId = UICredential.TYPE_EM;
                uic.username = credential.getUsername();
                if (credential.getPassword() != null)
                    uic.password = credential.getPassword();
            }

            @Override
            public void visit(ScaleIOCredential credential) {
                uic.typeId = UICredential.TYPE_SCALEIO;
                uic.username = credential.getUsername();
                uic.gatewayosusername = credential.getOsUsername();
                if (credential.getPassword() != null)
                    uic.password = credential.getPassword();
                if (credential.getOsPassword() != null)
                    uic.gatewayospassword = credential.getOsPassword();
            }

            @Override
            public void visit(OSCredential credential) {
                uic.typeId = UICredential.TYPE_OS;
                uic.username = credential.getUsername();
                if (credential.getPassword() != null)
                    uic.password = credential.getPassword();
            }
        });

        uic.id = cdto.getId();
        uic.domain = cdto.getDomain();
        uic.credentialsName = cdto.getLabel();
        int nDevices = dto.getReferences() == null ? 0 : dto.getReferences().getDevices();
        uic.numberOfDevices = nDevices;
        if (cdto.getCreatedBy() != null) {
            uic.createdBy = cdto.getCreatedBy();
        }
        if (cdto.getUpdatedBy() != null) {
            uic.updatedBy = cdto.getUpdatedBy();
        }
        if (cdto.getCreatedDate() != null) {
            uic.creationTime = getTime(cdto.getCreatedDate());
        }
        if (cdto.getUpdatedDate() != null) {
            uic.updateTime = getTime(cdto.getUpdatedDate());
        }
        return uic;
    }

    /**
     * Asm Credential Type value for the passed string.
     *
     * @param typeId
     *            String value
     * @return the asm dto type
     */
    public static CredentialType getAsmDTOType(String typeId) {
        CredentialType asmType = null;

        if (typeId != null) {
            if (typeId.toLowerCase().equals(UICredential.TYPE_SERVER)) {
                asmType = CredentialType.SERVER;
            } else if (typeId.toLowerCase().equals(UICredential.TYPE_CHASSIS)) {
                asmType = CredentialType.CHASSIS;
            } else if (typeId.toLowerCase().equals(UICredential.TYPE_IOM)) {
                asmType = CredentialType.IOM;
            } else if (typeId.toLowerCase().equals(UICredential.TYPE_VCENTER)) {
                asmType = CredentialType.VCENTER;
            } else if (typeId.toLowerCase().equals(UICredential.TYPE_STORAGE)) {
                asmType = CredentialType.STORAGE;
            } else if (typeId.toLowerCase().equals(UICredential.TYPE_SCVMM)) {
                asmType = CredentialType.SCVMM;
            } else if (typeId.toLowerCase().equals(UICredential.TYPE_EM)) {
                asmType = CredentialType.EM;
            } else if (typeId.toLowerCase().equals(UICredential.TYPE_SCALEIO)) {
                asmType = CredentialType.SCALEIO;
            } else if (typeId.toLowerCase().equals(UICredential.TYPE_OS)) {
                asmType = CredentialType.OS;
            }

        }
        return asmType;
    }

    public static void parsePerformanceMonitoring(UIServer server,
                                                      ManagedDevice dto,
                                                      PerformanceMonitorServiceAdapter monitorServiceAdapter) throws Exception {

        PerformanceMetric[] perfMetricCurrent = monitorServiceAdapter.performanceMonitoring(
                dto.getRefId(),
                "-1h",
                "5min");
        PerformanceMetric[] perfMetricHour = monitorServiceAdapter.performanceMonitoring(
                dto.getRefId(),
                "-1h",
                "5min");
        PerformanceMetric[] perfMetricDay = monitorServiceAdapter.performanceMonitoring(
                dto.getRefId(),
                "-1d",
                "1h");
        PerformanceMetric[] perfMetricWeek = monitorServiceAdapter.performanceMonitoring(
                dto.getRefId(),
                "-1w",
                "1d");
        PerformanceMetric[] perfMetricMonth = monitorServiceAdapter.performanceMonitoring(
                dto.getRefId(),
                "-1mon",
                "1d");
        PerformanceMetric[] perfMetricYear = monitorServiceAdapter.performanceMonitoring(
                dto.getRefId(),
                "-1y",
                "1mon");

        //get current data first and populate to populate usage and then call historic data for each component
        if ((perfMetricCurrent != null && perfMetricCurrent.length > 0) && (perfMetricWeek != null && perfMetricWeek.length > 0) && (perfMetricMonth != null && perfMetricMonth.length > 0) && (perfMetricYear != null && perfMetricYear.length > 0)) {
            for (UIComponentUsage compUsage : UIComponentUsage.values()) {
                UIUsageData usageData = getPerformanceCurrentMetric(perfMetricCurrent,
                                                                    compUsage);
                UIUsageData usageDataHour = getPerformanceHistoricMetric(usageData,
                                                                         perfMetricHour,
                                                                         compUsage,
                                                                         "Last Hour",
                                                                         "Per 5 Minutes");
                UIUsageData usageDataDay = getPerformanceHistoricMetric(usageDataHour,
                                                                        perfMetricDay,
                                                                        compUsage,
                                                                        "Last Day",
                                                                        "Hours");
                UIUsageData usageDataWeek = getPerformanceHistoricMetric(usageDataDay,
                                                                         perfMetricWeek,
                                                                         compUsage,
                                                                         "Last Week",
                                                                         "Days");
                UIUsageData usageDataMonth = getPerformanceHistoricMetric(usageDataWeek,
                                                                          perfMetricMonth,
                                                                          compUsage,
                                                                          "Last Month",
                                                                          "Days");
                UIUsageData usageDataYear = getPerformanceHistoricMetric(usageDataMonth,
                                                                         perfMetricYear,
                                                                         compUsage,
                                                                         "Last Year",
                                                                         "Months");
                if (compUsage.getLabel().contains("CPU"))
                    server.cpuusage = usageDataYear;
                else if (compUsage.getLabel().contains("Memory"))
                    server.memoryusage = usageDataYear;
                else if (compUsage.getLabel().contains("System"))
                    server.systemusage = usageDataYear;
                else
                    server.iousage = usageDataYear;

            }
        }
    }

    private static UIUsageData getPerformanceHistoricMetric(
            UIUsageData usageData,
            PerformanceMetric[] perfMetric,
            UIComponentUsage compUsage,
            String label,
            String xAxisLabel) {

        usageData.category = compUsage.getLabel();

        for (PerformanceMetric perf : perfMetric) {
            UIUsageDataSeries dataSeries = new UIUsageDataSeries();
            PerformanceMetricSummary summary = perf.getSummary();
            String target = perf.getTarget();
            if (target.contains(compUsage.getValue())) {
                List<List<String>> dataPoint = perf.getDatapoints();
                List<UIUsageDataPoint> dpList = new ArrayList<UIUsageDataPoint>();
                for (List<String> dataReturn : dataPoint) {
                    UIUsageDataPoint dp = new UIUsageDataPoint();
                    if (dataReturn != null) {
                        if (dataReturn.get(0) != null)
                            dp.value = Double.valueOf(dataReturn.get(0)).doubleValue();
                        else
                            dp.value = null;
                        Long timeISO = Long.valueOf(dataReturn.get(1));
                        DateTime startDate = new DateTime(timeISO * 1000L);
                        dp.timestamp = startDate.toString();
                        dpList.add(dp);
                    }

                }
                dataSeries.data = dpList;
                dataSeries.id = label;
                dataSeries.timeframe = label;
                dataSeries.chartlabel = xAxisLabel;
                dataSeries.averagevalue = summary.getAverage();
                if (!summary.getMax().isEmpty()
                        && summary.getMax().get(0) != null)
                    dataSeries.maximumvalue = Double.valueOf(summary.getMax()
                                                                     .get(0)).doubleValue();
                if (!summary.getMax().isEmpty()
                        && summary.getMax().get(0) != null)
                    dataSeries.minimumvalue = Double.valueOf(summary.getMin()
                                                                     .get(0)).doubleValue();
                usageData.historicaldata.add(dataSeries);

            }

        }
        return usageData;

    }

    private static UIUsageData getPerformanceCurrentMetric(
            PerformanceMetric[] perfMetric,
            UIComponentUsage compUsage) {
        // get current utilization data. Returns only one data point
        UIUsageData usageData = new UIUsageData();
        for (PerformanceMetric perf : perfMetric) {
            String target = perf.getTarget();
            usageData.id = target;
            if (target.contains(compUsage.getValue())) {
                List<List<String>> datapoints = perf.getDatapoints();
                PerformanceMetricSummary perfSummary = perf.getSummary();
                // Reverse the order to get the latest value
                // Skip nulls, return the first good data point
                Collections.reverse(datapoints);

                for (List<String> datapoint : datapoints) {
                    usageData.currentvalue = null;
                    usageData.peakvalue = null;

                    if (datapoint.get(0) == null)
                        continue;

                    usageData.currentvalue = Double.valueOf(datapoint.get(0)).doubleValue();

                    if (perfSummary.getAll_time_peak().get(0) != null) {
                        usageData.peakvalue = Double.valueOf(
                                perfSummary.getAll_time_peak().get(0)).doubleValue();
                    }
                    if (perfSummary.getAll_time_peak().get(1) != null) {
                        long data = Long.valueOf(perfSummary.getAll_time_peak().get(1));
                        DateTime peakDateTime = new DateTime(data * 1000L);
                        usageData.peaktime = peakDateTime.toString();
                    }

                    long startData = perfSummary.getDevice_first_seen();
                    DateTime startDateTime = new DateTime(startData * 1000L);
                    usageData.starttime = startDateTime.toString();

                    Map<Object, Object> thresholds = perf.getThresholds();
                    for (Object key : thresholds.keySet()) {
                        String value = null;
                        if (key != null) {
                            if (thresholds.get(key) != null)
                                value = thresholds.get(key).toString();
                            if (value != null)
                                usageData.threshold = Double.valueOf(value).doubleValue();
                            else
                                usageData.threshold = 0.0;
                        }

                    }
                    usageData.currentvaluelabel = "Current Utilization";
                    usageData.category = compUsage.getLabel();
                    return usageData;

                }

            }
        }

        return usageData;

    }

    /**
     * Parse ServerDevice into UI class.
     *
     * @param inventory
     * @param applicationContext
     * @return
     */
    public static UIServer parseServerDevice(Server inventory,
                                             FirmwareDeviceInventory[] firmwareDeviceInventory,
                                             ApplicationContext applicationContext,
                                             CredentialServiceAdapter credentialServiceAdapter,
                                             DeploymentServiceAdapter serviceAdapter,
                                             boolean bTranslateCredentials) {
        UIServer responseObj = new UIServer();
        responseObj.id = inventory.getRefId(); // inventory.getDeviceId();
        responseObj.ipaddress = inventory.getManagementIP();
        responseObj.ipaddressurl = "https://" + responseObj.ipaddress;
        responseObj.servicetag = inventory.getServiceTag();
        responseObj.hostname = inventory.getHostName();

        if (inventory.getHealth() != null) {
            //responseObj.health = applicationContext.getMessage("deviceHealth." + inventory.getHealth(), null, LocaleContextHolder.getLocale());
            // TODO: enum
            responseObj.health = inventory.getHealth().value();
        }
        responseObj.healthstatus = inventory.getHealthStatusMessage();
        responseObj.systemmodel = inventory.getModel();

        if (inventory.getProcessorList() != null) {
            responseObj.cpus = String.valueOf(inventory.getProcessorList().size());
            int cnt = 1;
            for (ProcessorInventory processor : inventory.getProcessorList()) {
                UICPU cpu = new UICPU();
                cpu.id = String.valueOf(cnt);
                cnt++;

                if (processor.getManufacturer() != null) {
                    cpu.manufacturer = processor.getManufacturer();
                }
                if (processor.getModel() != null) {
                    cpu.model = processor.getModel();
                }
                cpu.cores = Integer.toString(processor.getCores());
                cpu.enabledcores = Integer.toString(processor.getEnabledCores());
                if (processor.getMaxClockSpeed() != 0) {
                    cpu.maxclockspeed = String.valueOf(processor.getMaxClockSpeed());
                }
                if (processor.getCurrentClockSpeed() != 0) {
                    cpu.currentspeed = String.valueOf(processor.getCurrentClockSpeed());
                }
                responseObj.cpudata.add(cpu);
            }

        }

        if (inventory.getMemoryList() != null) {
            responseObj.memorydata = getMemoryUIFromServer(inventory);
        }

        if (inventory.getControllers() != null && inventory.getControllers().size() > 0) {
            responseObj.localstoragedata = getLocalStorageDataFromServer(inventory,
                                                                         applicationContext);
        }

        if (inventory.getMemory() != 0) {
            responseObj.memory = String.valueOf(inventory.getMemory());
        }

        responseObj.serverpowerstate = applicationContext.getMessage(
                "powerState." + inventory.getPowerState().value(),
                null,
                LocaleContextHolder.getLocale());


        if (inventory.getDiscoveryTime() != null)
            responseObj.discoverytime = getTime(inventory.getDiscoveryTime());
        if (inventory.getInventoryTime() != null)
            responseObj.lastinventorytime = getTime(inventory.getInventoryTime());

        responseObj.assettag = inventory.getAssetTag();
        responseObj.dnsIdracName = inventory.getIdracName();
        responseObj.os = inventory.getOperatingSystemName();

        responseObj.associatedcredential = inventory.getCredentialId();
        if (inventory.getCredentialId() != null && bTranslateCredentials) {
            // replace cred ID with name
            if (credentialServiceAdapter != null) {
                AsmCredentialDTO dto = credentialServiceAdapter.getCredential(
                        inventory.getCredentialId());
                if (dto != null && dto.getCredential() != null) {
                    responseObj.associatedcredential = dto.getCredential().getLabel();
                }
            }
        }

        if (firmwareDeviceInventory != null) {
            for (FirmwareDeviceInventory firmware : firmwareDeviceInventory) {
                // SourceType.Catalog are from the catalog, only SourceType.Device should be shown here.
                if (SourceType.Device.equals(firmware.getSourceType()) &&
                        firmware.getLastUpdateTime().after(EARLIEST_ALLOWED_FIRMWARE_DATE))

                    if (ComponentType.DRIVER.getValue().equals(
                            firmware.getComponentType())) { // Then it's software
                        UISoftware uiSoftware = new UISoftware();
                        uiSoftware.softwarelastupdatetime = getTime(firmware.getLastUpdateTime());
                        uiSoftware.softwarename = firmware.getName();
                        uiSoftware.softwareversion = firmware.getVersion();
                        uiSoftware.id = firmware.getId();

                        uiSoftware.criticality = "";
                        uiSoftware.filename = firmware.getFqdd();
                        uiSoftware.vendor = firmware.getVendorID();
                        uiSoftware.softwaretype = "";

                        responseObj.softwares.add(uiSoftware);
                    } else { // it's a Firmware
                        UIFirmware uif = new UIFirmware();
                        uif.firmwarelastupdatetime = getTime(firmware.getLastUpdateTime());

                        uif.firmwarename = firmware.getName();
                        uif.firmwareversion = firmware.getVersion();
                        uif.id = firmware.getId();
                        responseObj.firmwares.add(uif);
                    }
            }
        }

        //inventory.getHarddisk();
        if (inventory.getNetworkInterfaceList() != null) {
            for (LogicalNetworkInterface networking : inventory.getNetworkInterfaceList()) {
                UINIC nic = new UINIC();
                UINetworkIdentity identity = nic.networkInformation;
                nic.id = networking.getId();
                identity.id = networking.getId();
                nic.instanceId = networking.getFqdd();
                identity.instanceid = networking.getFqdd();
                nic.product = networking.getProductName();
                nic.vendor = networking.getVendorName();

                nic.networktype = getNetworkType(networking.getNetworkMode().value(),
                                                 applicationContext);

                for (LogicalNetworkIdentityInventory inv : networking.getIdentityList()) {
                    switch (inv.getIdentityType()) {
                    case IP_INITIATOR:
                        identity.initiatorip = inv.getCurrentIdentity();
                        break;
                    case BOOTLUN:
                        identity.bootlun = inv.getCurrentIdentity();
                        identity.enablebootlun = inv.isbPresent();
                        break;
                    case FIPS_MAC:
                        identity.virtualfipsmacaddress = inv.getCurrentIdentity();
                        break;
                    case IP_TARGET:
                        identity.targetipaddress = inv.getCurrentIdentity();
                        break;
                    case IQN_INITIATOR:
                        identity.initiatoriqn = inv.getCurrentIdentity();
                        break;
                    case IQN_TARGET:
                        identity.targetiqn = inv.getCurrentIdentity();
                        identity.enabletargetiqn = inv.isbPresent();
                        break;
                    case ISCSI_MAC:
                        identity.virtualiscsimacaddress = inv.getCurrentIdentity();
                        identity.permanentiscsimacaddress = inv.getPermanentIdentity();
                        if ("00:00:00:00:00:00".equals(identity.virtualiscsimacaddress)) {
                            identity.virtualiscsimacaddress = identity.permanentiscsimacaddress;
                        }
                        break;
                    case WWNN:
                        identity.wwnn = inv.getPermanentIdentity();
                        identity.virtualwwnn = inv.getCurrentIdentity();
                        break;
                    case WWPN:
                        identity.wwpn = inv.getPermanentIdentity();
                        identity.virtualwwpn = inv.getCurrentIdentity();
                        break;
                    case WWPN_TARGET:
                        identity.targetwwpn = inv.getCurrentIdentity();
                        break;
                    case LAN_MAC:
                        identity.virtualmacaddress = inv.getCurrentIdentity();
                        identity.permanentmacaddress = inv.getPermanentIdentity();
                        if ("00:00:00:00:00:00".equals(identity.virtualmacaddress)) {
                            identity.virtualmacaddress = identity.permanentmacaddress;
                        }
                        break;
                    case UNKNOWN:
                        //identity.permanentfcoemacaddress
                        break;
                    }
                }
                responseObj.nics.add(nic);
            }
        }

        // find a service
        List<String> filterList = new ArrayList<>();
        filterList.add("eq,server," + responseObj.id);

        ResourceList<Deployment> depList = serviceAdapter.getDeployments(null,
                                                                         filterList,
                                                                         null,
                                                                         1,
                                                                         Boolean.TRUE);

        if (depList != null && depList.getList() != null && depList.getList().length > 0) {
            Deployment deployment = depList.getList()[0];
            responseObj.serviceId = deployment.getId();
            responseObj.serviceName = deployment.getDeploymentName();
        }
        // template deployed on/by
        responseObj.createdBy = "";
        responseObj.createdOn = "";


        return responseObj;
    }

    public static List<UILocalStorage> getLocalStorageDataFromServer(final Server inventory,
                                                                     final ApplicationContext applicationContext) {
        List<UILocalStorage> localStorages = new ArrayList<>();
        if (inventory != null) {
            Locale locale = LocaleContextHolder.getLocale();
            if (inventory.getControllers() != null && inventory.getControllers().size() > 0) {
                for (Controller controller : inventory.getControllers()) {
                    UILocalStorage uls = new UILocalStorage();
                    uls.id = controller.getId();
                    uls.raidControllerName = controller.getProductName();
                    if (uls.raidControllerName != null &&
                            (uls.raidControllerName.contains("PERC") ||
                                    uls.raidControllerName.contains("EmbSata"))) {
                        uls.raidPCISlot = applicationContext.getMessage(
                                "raidController.notApplicable",
                                null,
                                locale);
                    } else {
                        uls.raidPCISlot = controller.getPciSlot() != null ? controller.getPciSlot().toString() : "";
                    }
                    uls.raidDeviceDescription = controller.getDeviceDescription();
                    uls.raidFirmwareVersion = controller.getFirmwareVersion();
                    uls.raidDriverVersion = controller.getDriverVersion();
                    uls.raidCacheMemorySize = controller.getCacheSizeInMB() != null ? controller.getCacheSizeInMB().toString() + " MB" : "";

                    Map<String, UIPhysicalDisk> physicalDisksMap = new HashMap<>();
                    if (controller.getPhysicalDisks() != null && controller.getPhysicalDisks().size() > 0) {
                        for (PhysicalDisk physicalDisk : controller.getPhysicalDisks()) {
                            UIPhysicalDisk upd = createPhysicalDiskForUI(physicalDisk,
                                                                         applicationContext,
                                                                         locale);
                            physicalDisksMap.put(upd.id,
                                                 upd);
                        }
                    }

                    if (controller.getVirtualDisks() != null && controller.getVirtualDisks().size() > 0) {
                        for (VirtualDisk virtualDisk : controller.getVirtualDisks()) {
                            UILogicalDisk uld = new UILogicalDisk();
                            uld.id = virtualDisk.getFqdd();
                            uld.logicalDiskName = virtualDisk.getName();
                            uld.state = getRaidStatusToUI(virtualDisk.getRaidStatus(),
                                                          applicationContext,
                                                          locale);
                            if (virtualDisk.getDiskSizeInBytes() != null) {
                                uld.size = humanReadableByteCount(virtualDisk.getDiskSizeInBytes(),
                                                                  false);
                            }
                            uld.layout = getVirtualDiskLayoutToUI(virtualDisk.getRaidLevel(),
                                                                  applicationContext,
                                                                  locale);
                            uld.mediaType = getVirtualDiskMediaTypeToUI(virtualDisk.getMediaType(),
                                                                        applicationContext,
                                                                        locale);
                            uld.readPolicy = getVirtalDiskReadCachePolicyToUI(
                                    virtualDisk.getReadCachePolicy(),
                                    applicationContext,
                                    locale);
                            uld.writePolicy = getVirtalDiskWriteCachePolicyToUI(
                                    virtualDisk.getWriteCachePolicy(),
                                    applicationContext,
                                    locale);
                            uls.logicaldiskdata.add(uld);

                            if (virtualDisk.getPhysicalDisks() != null && virtualDisk.getPhysicalDisks().size() > 0) {
                                for (PhysicalDisk pd : virtualDisk.getPhysicalDisks()) {
                                    UIPhysicalDisk upd = physicalDisksMap.get(pd.getFqdd());
                                    if (pd != null) {
                                        uld.physicaldiskdata.add(upd);
                                    } else {
                                        upd = createPhysicalDiskForUI(pd,
                                                                      applicationContext,
                                                                      locale);
                                        uld.physicaldiskdata.add(upd);
                                        physicalDisksMap.put(upd.id,
                                                             upd);
                                    }
                                }
                                Collections.sort(uld.physicaldiskdata,
                                                 UIPhysicalDisk.SORT_BY_SLOT_AND_NAME);
                            }
                        }
                    }
                    if (physicalDisksMap.size() > 0) {
                        uls.physicaldiskdata.addAll(physicalDisksMap.values());
                        Collections.sort(uls.physicaldiskdata,
                                         UIPhysicalDisk.SORT_BY_SLOT_AND_NAME);
                    }
                    localStorages.add(uls);
                }
            }
        }
        Collections.sort(localStorages,
                         UILocalStorage.SORT_BY_PCI_SLOT_AND_NAME);
        return localStorages;
    }

    public static UIPhysicalDisk createPhysicalDiskForUI(final PhysicalDisk physicalDisk,
                                                         final ApplicationContext applicationContext,
                                                         final Locale locale) {
        UIPhysicalDisk upd = null;
        if (physicalDisk != null) {
            upd = new UIPhysicalDisk();
            upd.id = physicalDisk.getFqdd();
            upd.physicalDiskName = getPhysicalDiskNameToUI(physicalDisk);
            upd.state = getRaidStatusToUI(physicalDisk.getRaidStatus(),
                                          applicationContext,
                                          locale);
            upd.size = humanReadableByteCount(physicalDisk.getSize(),
                                              false);
            upd.slotNumber = Integer.toString(physicalDisk.getDriveNumber());
            upd.securityStatus = getRaidSecurityStateToUI(physicalDisk.getSecurityState(),
                                                          applicationContext,
                                                          locale);
            upd.mediaType = getPhysicalMediaTypeToUI(physicalDisk.getMediaType(),
                                                     applicationContext,
                                                     locale);
            upd.busProtocol = getPhysicalBusProtocolToUI(physicalDisk.getBusProtocol(),
                                                         applicationContext,
                                                         locale);
            upd.hotSpare = getPhysicalHotSpareToUI(physicalDisk.getHotSpareStatus(),
                                                   applicationContext,
                                                   locale);
        }
        return upd;
    }

    public static String getPhysicalDiskNameToUI(final PhysicalDisk physicalDisk) {
        String diskName = physicalDisk.getDeviceDescription();
        if (physicalDisk.getFqdd() != null) {
            String fqdd = physicalDisk.getFqdd();
            int diskNumber = -1;
            int connector = -1;
            int enclosureId = -1;
            final String dbString = "Disk.Bay.";
            final String encString = "Enclosure.Internal.";
            String[] parts = fqdd.split(":");
            for (String part : parts) {
                if (part.startsWith(dbString)) {
                    String disk = part.substring(dbString.length());
                    if (NumberUtils.isDigits(disk)) {
                        diskNumber = Integer.valueOf(disk);
                    }
                } else if (part.startsWith(encString)) {
                    String enclosure = part.substring(encString.length());
                    if (enclosure != null) {
                        String[] eParts = enclosure.split("-");
                        if (eParts != null && eParts.length == 2) {
                            if (NumberUtils.isDigits(eParts[0])) {
                                connector = Integer.valueOf(eParts[0]);
                            }
                            if (NumberUtils.isDigits(eParts[1])) {
                                enclosureId = Integer.valueOf(eParts[1]);
                            }
                        }
                    }
                }
            }
            if (diskNumber >= 0 &&
                    connector >= 0 &&
                    enclosureId >= 0) {
                diskName = "Physical Disk " + connector + ":" + enclosureId + ":" + diskNumber;
            }
        }
        return diskName;
    }

    public static String getVirtalDiskWriteCachePolicyToUI(
            final VirtualDisk.WriteCachePolicy writeCachePolicy,
            final ApplicationContext applicationContext,
            final Locale locale) {
        String type = applicationContext.getMessage("virtualWriteCache.unknown",
                                                    null,
                                                    locale);
        if (writeCachePolicy != null) {
            switch (writeCachePolicy) {
            case WRITE_THROUGH:
                type = applicationContext.getMessage("virtualWriteCache.writeThrough",
                                                     null,
                                                     locale);
                break;
            case WRITE_BACK:
                type = applicationContext.getMessage("virtualWriteCache.writeBack",
                                                     null,
                                                     locale);
                break;
            case WRITE_BACK_FOCE:
                type = applicationContext.getMessage("virtualWriteCache.writeBackForce",
                                                     null,
                                                     locale);
                break;
            }
        }
        return type;
    }

    public static String getVirtalDiskReadCachePolicyToUI(
            final VirtualDisk.ReadCachePolicy readCachePolicy,
            final ApplicationContext applicationContext,
            final Locale locale) {
        String type = applicationContext.getMessage("virtualReadCache.unknown",
                                                    null,
                                                    locale);
        if (readCachePolicy != null) {
            switch (readCachePolicy) {
            case NO_READ_AHEAD:
                type = applicationContext.getMessage("virtualReadCache.noReadAhead",
                                                     null,
                                                     locale);
                break;
            case READ_AHEAD:
                type = applicationContext.getMessage("virtualReadCache.readAhead",
                                                     null,
                                                     locale);
                break;
            case ADAPTIVE_READ_AHEAD:
                type = applicationContext.getMessage("virtualReadCache.adaptiveReadAhead",
                                                     null,
                                                     locale);
                break;
            }
        }
        return type;
    }

    public static String getVirtualDiskMediaTypeToUI(final VirtualDisk.VirtualMediaType mediaType,
                                                     final ApplicationContext applicationContext,
                                                     final Locale locale) {
        String type = applicationContext.getMessage("virtualMediaType.unknown",
                                                    null,
                                                    locale);
        if (mediaType != null) {
            switch (mediaType) {
            case SSD:
                type = applicationContext.getMessage("virtualMediaType.SSD",
                                                     null,
                                                     locale);
                break;
            case HDD:
                type = applicationContext.getMessage("virtualMediaType.HDD",
                                                     null,
                                                     locale);
                break;
            }
        }
        return type;
    }

    public static String getVirtualDiskLayoutToUI(RaidLevel raidLevel,
                                                  ApplicationContext applicationContext,
                                                  Locale locale) {
        String type = applicationContext.getMessage("raidLayout.nonRaid",
                                                    null,
                                                    locale);
        if (raidLevel != null) {
            switch (raidLevel) {
            case RAID_0:
                type = applicationContext.getMessage("raidLayout.raid0",
                                                     null,
                                                     locale);
                break;
            case RAID_1:
                type = applicationContext.getMessage("raidLayout.raid1",
                                                     null,
                                                     locale);
                break;
            case RAID_5:
                type = applicationContext.getMessage("raidLayout.raid5",
                                                     null,
                                                     locale);
                break;
            case RAID_6:
                type = applicationContext.getMessage("raidLayout.raid6",
                                                     null,
                                                     locale);
                break;
            case RAID_10:
                type = applicationContext.getMessage("raidLayout.raid10",
                                                     null,
                                                     locale);
                break;
            case RAID_50:
                type = applicationContext.getMessage("raidLayout.raid50",
                                                     null,
                                                     locale);
                break;
            case RAID_60:
                type = applicationContext.getMessage("raidLayout.raid60",
                                                     null,
                                                     locale);
                break;
            }
        }
        return type;
    }

    private static String getPhysicalHotSpareToUI(final PhysicalDisk.HotSpareStatus hotSpareStatus,
                                                  final ApplicationContext applicationContext,
                                                  final Locale locale) {
        String type = applicationContext.getMessage("physicalHotSpareStatus.no",
                                                    null,
                                                    locale);
        if (hotSpareStatus != null) {
            switch (hotSpareStatus) {
            case Dedicated:
                type = applicationContext.getMessage("physicalHotSpareStatus.dedicated",
                                                     null,
                                                     locale);
                break;
            case Global:
                type = applicationContext.getMessage("physicalHotSpareStatus.global",
                                                     null,
                                                     locale);
                break;
            }
        }
        return type;
    }

    public static String getPhysicalBusProtocolToUI(final PhysicalDisk.BusProtocol busProtocol,
                                                    final ApplicationContext applicationContext,
                                                    final Locale locale) {
        String type = applicationContext.getMessage("physicalBusProtocol.unknown",
                null,
                locale);
        if (busProtocol != null) {
            switch (busProtocol) {
                case SCSI:
                    type = applicationContext.getMessage("physicalBusProtocol.scsi",
                            null,
                            locale);
                    break;
                case SATA:
                    type = applicationContext.getMessage("physicalBusProtocol.sata",
                            null,
                            locale);
                    break;
                case PATA:
                    type = applicationContext.getMessage("physicalBusProtocol.pata",
                            null,
                            locale);
                    break;
                case FIBRE:
                    type = applicationContext.getMessage("physicalBusProtocol.fibre",
                            null,
                            locale);
                    break;
                case USB:
                    type = applicationContext.getMessage("physicalBusProtocol.usb",
                            null,
                            locale);
                    break;
                case SAS:
                    type = applicationContext.getMessage("physicalBusProtocol.sas",
                            null,
                            locale);
                    break;
                case PCIe:
                    type = applicationContext.getMessage("physicalBusProtocol.pcie",
                            null,
                            locale);
                    break;
            }
        }
        return type;
    }

    public static String getPhysicalMediaTypeToUI(final PhysicalDisk.PhysicalMediaType mediaType,
                                                  final ApplicationContext applicationContext,
                                                  final Locale locale) {
        String type = applicationContext.getMessage("physicalMediaType.any",
                null,
                locale);
        if (mediaType != null) {
            switch (mediaType) {
                case SSD:
                    type = applicationContext.getMessage("physicalMediaType.SSD",
                            null,
                            locale);
                    break;
                case HDD:
                    type = applicationContext.getMessage("physicalMediaType.HDD",
                            null,
                            locale);
                    break;
                case NVMe:
                    type = applicationContext.getMessage("physicalMediaType.NVMe",
                            null,
                            locale);
                    break;
            }
        }
        return type;
    }
    public static String getRaidSecurityStateToUI(final PhysicalDisk.SecurityState securityState,
                                                  final ApplicationContext applicationContext,
                                                  final Locale locale) {
        String status = applicationContext.getMessage("raidSecurityStatus.notCapable",
                                                      null,
                                                      locale);
        if (securityState != null) {
            switch (securityState) {
            case SECURED:
                status = applicationContext.getMessage("raidSecurityStatus.secured",
                                                       null,
                                                       locale);
                break;
            case LOCKED:
                status = applicationContext.getMessage("raidSecurityStatus.locked",
                                                       null,
                                                       locale);
                break;
            case FOREIGN:
                status = applicationContext.getMessage("raidSecurityStatus.foreign",
                                                       null,
                                                       locale);
                break;

            }
        }
        return status;
    }

    public static String getRaidStatusToUI(final RaidStatus raidStatus,
                                           final ApplicationContext applicationContext,
                                           final Locale locale) {
        String status = applicationContext.getMessage("raidStatus.unknown",
                                                      null,
                                                      locale);
        if (raidStatus != null) {
            switch (raidStatus) {
            case READY:
                status = applicationContext.getMessage("raidStatus.ready",
                                                       null,
                                                       locale);
                break;
            case ONLINE:
                status = applicationContext.getMessage("raidStatus.online",
                                                       null,
                                                       locale);
                break;
            case FOREIGN:
                status = applicationContext.getMessage("raidStatus.foreign",
                                                       null,
                                                       locale);
                break;
            case OFFLINE:
                status = applicationContext.getMessage("raidStatus.offline",
                                                       null,
                                                       locale);
                break;
            case BLOCKED:
                status = applicationContext.getMessage("raidStatus.blocked",
                                                       null,
                                                       locale);
                break;
            case FAILED:
                status = applicationContext.getMessage("raidStatus.failed",
                                                       null,
                                                       locale);
                break;
            case DEGRADED:
                status = applicationContext.getMessage("raidStatus.degraded",
                                                       null,
                                                       locale);
                break;
            case NON_RAID:
                status = applicationContext.getMessage("raidStatus.nonRaid",
                                                       null,
                                                       locale);
                break;
            }
        }

        return status;
    }

    public static UIServer parseBMCServerDevice(ManagedDevice dto,
                                                ApplicationContext applicationContext,
                                                CredentialServiceAdapter credentialServiceAdapter,
                                                boolean bTranslateCredentials) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        UIServer responseObj = new UIServer();
        if (dto != null) {
            // check if this is the cseries server which has no reference in server RA
            responseObj.id = dto.getRefId(); // inventory.getDeviceId();
            responseObj.ipaddress = dto.getIpAddress();
            responseObj.ipaddressurl = "https://" + responseObj.ipaddress + "/login.html";
            responseObj.servicetag = dto.getServiceTag();
            responseObj.dnsIdracName = dto.getDisplayName();
            responseObj.assettag = dto.getServiceTag();
            if (dto.getHealth() != null) {
                responseObj.health = dto.getHealth().getValue();
            }
            responseObj.systemmodel = dto.getModel();
            // get the cpu from facts
            String razorFacts = dto.getFacts();
            Map<String, String> razorInv = new HashMap<String, String>();
            if (razorFacts != null)
                razorInv = mapper.readValue(razorFacts,
                                            Map.class);
            if (razorInv != null && !razorInv.isEmpty()) {

                int processorCount = Integer.valueOf(razorInv.get("processorcount"));
                int cpuCount = dto.getNumberOfCPUs();
                responseObj.cpus = String.valueOf(cpuCount);

                for (int i = 1; i <= cpuCount; i++) {
                    UICPU cpu = new UICPU();
                    cpu.cores = String.valueOf(processorCount / cpuCount);
                    cpu.id = String.valueOf(i);
                    cpu.manufacturer = StringUtils.substringBefore(
                            razorInv.get("processor0").trim(),
                            " ").replaceAll("\\(.+\\)",
                                            "");
                    cpu.id = String.valueOf(i);
                    cpu.model = razorInv.get("processor0");
                    responseObj.cpudata.add(cpu);
                }

                // get the nic data and populate here
                for (Entry<String, String> razorFact : razorInv.entrySet()) {

                    if (razorFact.getKey() != null && razorFact.getKey().startsWith("macaddress")) {
                        UINIC nic = new UINIC();
                        String nicValue = razorFact.getKey();
                        String[] nics = nicValue.split("_");
                        for (int i = 0; i < nics.length; i++) {
                            if (i == 1) {
                                UINetworkIdentity identity = nic.networkInformation;
                                nic.instanceId = nics[1];
                                identity.instanceid = nics[1];
                                String macValue = razorFact.getValue();
                                identity.permanentmacaddress = macValue;
                                responseObj.nics.add(nic);
                            }

                        }

                    }

                }
            }

//           // get the firmware data
//           if (dto.getFirmwareDeviceInventories() != null) {
//               for (FirmwareDeviceInventory firmware : dto.getFirmwareDeviceInventories() ) {
//                   UIFirmware uif = new UIFirmware();
//
//                   uif.firmwarelastupdatetime = "";
//                   if(firmware.getLastUpdateTime() != null && firmware.getLastUpdateTime().after(EARLIEST_ALLOWED_FIRMWARE_DATE))
//                       uif.firmwarelastupdatetime = getTime(firmware.getLastUpdateTime());
//
//                   uif.firmwarename = firmware.getName();
//                   uif.firmwareversion = firmware.getVersion();
//                   uif.id = firmware.getId();
//                   responseObj.firmwares.add(uif);
//               }
//           }

        }

        // template deployed on/by
        responseObj.createdBy = "";
        responseObj.createdOn = "";
        return responseObj;
    }

    /**
     * Gets the network type.
     *
     * @param networkMode
     *            the network mode
     * @return the network type
     */
    private static String getNetworkType(Integer networkMode,
                                         ApplicationContext context) {
        String type = null;
        switch (networkMode) {
        case 1:
            type = context.getMessage("ServerController.NetworkType.Lan",
                                      null,
                                      LocaleContextHolder.getLocale());
            break;
        case 2:
            type = context.getMessage("ServerController.NetworkType.SanISCSI",
                                      null,
                                      LocaleContextHolder.getLocale());
            break;
        case 3:
            type = context.getMessage("ServerController.NetworkType.LanISCSI",
                                      null,
                                      LocaleContextHolder.getLocale());
            break;
        case 4:
            type = context.getMessage("ServerController.NetworkType.SanFCOE",
                                      null,
                                      LocaleContextHolder.getLocale());
            break;
        case 5:
            type = context.getMessage("ServerController.NetworkType.LanFCOE",
                                      null,
                                      LocaleContextHolder.getLocale());
            break;
        case 0:
        default:
            break;
        }
        return type;
    }

    /**
     * Gets the fabric name type.
     *
     * @param type
     *            the type
     * @param context
     *            the context
     * @return the fabric name type
     */
    public static String getFabricNameType(FabricNameTypes type,
                                           ApplicationContext context) {
        String name = null;
        if (type != null) {
            switch (type) {
            case FABRIC_A:
                name = context.getMessage("FrabricNameType.FabricA",
                                          null,
                                          LocaleContextHolder.getLocale());
                break;
            case FABRIC_B:
                name = context.getMessage("FrabricNameType.FabricB",
                                          null,
                                          LocaleContextHolder.getLocale());
                break;
            case FABRIC_C:
                name = context.getMessage("FrabricNameType.FabricC",
                                          null,
                                          LocaleContextHolder.getLocale());
                break;
            }
        }
        return name;
    }

    /**
     * Format Date as January 23 2014 5:56 PM
     * @param lastInventory
     * @param locale
     * @return
     */
    public static String formatDateForUI(String lastInventory,
                                         Locale locale,
                                         boolean isPuppetTimestamp) {
        if (isPuppetTimestamp) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z",
                                                       LocaleContextHolder.getLocale());
            try {
                Date dt = df.parse(lastInventory);
                lastInventory = MappingUtils.getTime(dt);
            } catch (Exception e) {
                log.error("Parse timestamp failed: " + lastInventory,
                          e);
            }
        }
        return DateTime.parse(lastInventory).toString("MMMMM dd yyyy h:mm a",
                                                      locale);
    }

    /**
     * Prepare log message for device page.
     * @param message
     * @param context
     * @return
     */
    public static UIActivityLog parseLogEntry(LocalizedLogMessage message,
                                              ApplicationContext context) {
        UIActivityLog entry = new UIActivityLog();
        entry.id = String.valueOf(message.getLogId());
        entry.logMessage = message.getLocalizedMessage();
        entry.logTimeStamp = getTime(message.getTimeStamp());
        entry.logUser = message.getUserName();
        if (message.getSeverity() != null) {
            switch (message.getSeverity()) {
            case INFO:
                entry.severity = "info";
                break;
            case CRITICAL:
                entry.severity = "critical";
                break;
            case WARNING:
                entry.severity = "warning";
                break;
            case ERROR:
                entry.severity = "error";
                break;
            }
        }
        if (message.getCategory() != null) {
            entry.category = context.getMessage("logCategory." + message.getCategory().name(),
                                                null,
                                                LocaleContextHolder.getLocale());
        }
        return entry;
    }

    /**
     * Returns true if the User is a ReadOnly User or false if not a ReadOnly User.
     *
     * @return true if the User is a ReadOnly User or false if not a ReadOnly User.
     */
    public static boolean isReadOnlyUser() {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String role = "";
        for (GrantedAuthority auth : userDetails.getAuthorities()) {
            if (auth.getAuthority() != null) {
                role = auth.getAuthority();
                break;
            }
        }

        return role.equals(AsmConstants.USERROLE_READONLY);
    }

    /**
     * Returns true if the User is a Standard User or false if not a standard User.
     *
     * @return true if the User is a Standard User or false if not a standard User.
     */
    public static boolean isStandardUser() {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String role = "";
        for (GrantedAuthority auth : userDetails.getAuthorities()) {
            if (auth.getAuthority() != null) {
                role = auth.getAuthority();
                break;
            }
        }

        return role.equals(AsmConstants.USERROLE_OPERATOR);
    }

    /**
     * Returns true if the User is an Administrator User or false if not an Administrator User.
     *
     * @return true if the User is an Administrator User or false if not an Administrator User.
     */
    public static boolean isAdministratorUser() {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String role = "";
        for (GrantedAuthority auth : userDetails.getAuthorities()) {
            if (auth.getAuthority() != null) {
                role = auth.getAuthority();
                break;
            }
        }

        return role.equals(AsmConstants.USERROLE_ADMINISTRATOR);
    }

    private static List<UIMemory> getMemoryUIFromServer(Server server) {
        ArrayList<UIMemory> uiMemoryList = new ArrayList<UIMemory>();

        for (MemoryInventory memoryInventory : server.getMemoryList()) {
            UIMemory uiMemory = new UIMemory();
            uiMemory.banklabel = memoryInventory.getBankLabel();
            uiMemory.currentoperatingspeed = "" + memoryInventory.getCurrentOperatingSpeed() + " MHz";
            uiMemory.devicedescription = memoryInventory.getInstanceID();
            uiMemory.devicetype = "Memory";
            uiMemory.fqdd = memoryInventory.getFqdd();
            uiMemory.id = memoryInventory.getId();
            uiMemory.instanceid = memoryInventory.getInstanceID();
            GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(
                    TimeZone.getTimeZone("GMT"));
            if (memoryInventory.getLastSystemInventoryTime() != null) {
                cal.setTime(memoryInventory.getLastSystemInventoryTime());
                uiMemory.lastsysteminventorytime = MappingUtils.getDate(cal);
            }
            if (memoryInventory.getLastUpdateTime() != null) {
                cal.setTime(memoryInventory.getLastUpdateTime());
                uiMemory.lastupdatetime = MappingUtils.getDate(cal);
            }
            if (memoryInventory.getManufactureDate() != null) {
                cal.setTime(memoryInventory.getManufactureDate());
                uiMemory.manufacturedate = MappingUtils.getDate(cal);
            }
            uiMemory.manufacturer = memoryInventory.getManufacturer();
            uiMemory.memorytype = memoryInventory.getMemoryType();
            uiMemory.model = memoryInventory.getModel();
            uiMemory.partnumber = memoryInventory.getPartNumber();
            uiMemory.primarystatus = memoryInventory.getPrimaryStatus();
            uiMemory.rank = memoryInventory.getRank();
            uiMemory.serialnumber = memoryInventory.getSerialNumber();
            uiMemory.size = "" + memoryInventory.getSize() + " MB";
            uiMemory.speed = "" + memoryInventory.getSpeed() + " MHz";

            uiMemoryList.add(uiMemory);
        }

        return uiMemoryList;
    }

    public static UIFirmwareReportDevice getUIFirmwareDeviceComplianceReport(
            FirmwareComplianceReport firmwareComplianceReport) {
        UIFirmwareReportDevice uiFirmwareReport = new UIFirmwareReportDevice();
        // No separate value/requirement for Software at this time.  Added softwareCompliant per Murali D's request.
        uiFirmwareReport.softwareCompliant = firmwareComplianceReport.isCompliant();
        uiFirmwareReport.firmwareCompliant = firmwareComplianceReport.isCompliant();

        CompliantState complianceState = CompliantState.UNKNOWN;
        if (firmwareComplianceReport.getFirmwareRepositoryName() != null) {
            if (firmwareComplianceReport.isCompliant()) {
                complianceState = CompliantState.COMPLIANT;
            } else {
                complianceState = CompliantState.NONCOMPLIANT;
            }
        }

        uiFirmwareReport.repositoryName = getComplianceReportName(
                complianceState,
                firmwareComplianceReport.getFirmwareRepositoryName());

        uiFirmwareReport.ipAddress = firmwareComplianceReport.getIpAddress();
        uiFirmwareReport.name = firmwareComplianceReport.getServiceTag();
        uiFirmwareReport.serviceTag = firmwareComplianceReport.getServiceTag();
        uiFirmwareReport.deviceType = firmwareComplianceReport.getDeviceType().getValue();
        uiFirmwareReport.model = firmwareComplianceReport.getModel();
        uiFirmwareReport.availability = firmwareComplianceReport.isAvailable() ? "notinuse" : "inuse";
        UIDeviceState managedState = DeviceController.mapToUIState(
                firmwareComplianceReport.getManagedState());
        if (managedState != null) {
            uiFirmwareReport.state = managedState.getLabel();
        }
        UIDeviceStatus status = DeviceController.mapToUIStatus(
                firmwareComplianceReport.getDeviceState());
        if (status != null) {
            uiFirmwareReport.status = status.getLabel();
        }

        if (firmwareComplianceReport.getFirmwareComplianceReportComponents() != null) {
            for (FirmwareComplianceReportComponent fcrc : firmwareComplianceReport.getFirmwareComplianceReportComponents()) {
                if (fcrc.isSoftware()) {
                    UISoftwareComponent uisc = new UISoftwareComponent();
                    uisc.name = fcrc.getName();
                    uisc.compliant = fcrc.isCompliant();
                    uisc.vendor = fcrc.getVendor();
                    uisc.currentversion = MappingUtils.getUISoftware(fcrc.getCurrentVersion());
                    uisc.targetversion = MappingUtils.getUISoftware(fcrc.getTargetVersion());
                    uiFirmwareReport.softwareComponents.add(uisc);
                } else {
                    UIFirmwareComponent uifc = new UIFirmwareComponent();
                    uifc.name = fcrc.getName();
                    uifc.compliant = fcrc.isCompliant();
                    uifc.currentversion = MappingUtils.getUIFirmware(fcrc.getCurrentVersion());
                    uifc.targetversion = MappingUtils.getUIFirmware(fcrc.getTargetVersion());
                    uiFirmwareReport.firmwareComponents.add(uifc);
                }
            }
        }

        return uiFirmwareReport;
    }

    public static com.dell.asm.ui.model.firmware.UIFirmware getUIFirmware(
            FirmwareComplianceReportComponentVersionInfo fcrcVersionInfo) {
        com.dell.asm.ui.model.firmware.UIFirmware uiFirmware = new com.dell.asm.ui.model.firmware.UIFirmware();
        uiFirmware.firmwarename = fcrcVersionInfo.getFirmwareName();
        uiFirmware.firmwareversion = fcrcVersionInfo.getFirmwareVersion();
        // uiFirmware.firmwaretype = fcrcVersionInfo.getFirmwareType();
        uiFirmware.id = fcrcVersionInfo.getId();

        if (fcrcVersionInfo.getFirmwareLastUpdateTime() != null) {
            uiFirmware.firmwarelastupdatetime = MappingUtils.getTime(
                    fcrcVersionInfo.getFirmwareLastUpdateTime());
        }

        return uiFirmware;
    }

    public static com.dell.asm.ui.model.firmware.UISoftware getUISoftware(
            FirmwareComplianceReportComponentVersionInfo fcrcVersionInfo) {
        com.dell.asm.ui.model.firmware.UISoftware uiSoftware = new com.dell.asm.ui.model.firmware.UISoftware();
        uiSoftware.softwarename = fcrcVersionInfo.getFirmwareName();
        uiSoftware.softwareversion = fcrcVersionInfo.getFirmwareVersion();
        uiSoftware.id = fcrcVersionInfo.getId();

        if (fcrcVersionInfo.getFirmwareLastUpdateTime() != null) {
            uiSoftware.softwarelastupdatetime = MappingUtils.getTime(
                    fcrcVersionInfo.getFirmwareLastUpdateTime());
        }

        return uiSoftware;
    }

    public static String getComplianceReportName(CompliantState state,
                                                 String packageName) {
        String reportName = "Unknown Compliance Status";

        if (packageName != null) {
            if (state != null) {
                if (CompliantState.COMPLIANT.equals(state)) {
                    reportName = "Compliant with " + packageName;
                } else if (CompliantState.NONCOMPLIANT.equals(state)) {
                    reportName = "Non-Compliant with " + packageName;
                } else if (CompliantState.UPDATEREQUIRED.equals(state)) {
                    reportName = "Update Required with " + packageName;
                }
            }
        }

        return reportName;
    }

    public static String humanReadableByteCount(long bytes,
                                                boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + "";
        return String.format("%.1f %sB",
                             bytes / Math.pow(unit,
                                              exp),
                             pre);
    }

    public static enum NIC_NAMES {
        Broadcom57800("Broadcom.*57800.*",
                      "Broadcom 57800"),
        Broadcom57810("Broadcom.*57810.*",
                      "Broadcom 57810"),
        Broadcom57840("Broadcom.*57840.*",
                      "Broadcom 57840"),
        IntelX520I350(".*X520/I350.*",
                      "Intel X520+i350"),
        IntelX520(".*Intel.*X520.*",
                  "Intel X520"),
        QLogicQLE2562(".*QLogic.*QLE.*2562.*",
                      "QLogic QLE 2562"),
        QLogicQME2572(".*QLogic.*QME.*2572.*",
                      "QLogic QME 2572"),
        QLogicQLE2662(".*QLogic.*QLE.*2662.*",
                      "QLogic QLE 2662"),
        QLogicQME2662(".*QLogic.*QME.*2662.*",
                      "QLogic QME 2662"),
        QLogic57800(".*QLogic.*57800.*",
                    "QLogic 57800"),
        QLogic57810(".*QLogic.*57810.*",
                    "QLogic 57810"),
        QLogic57840(".*QLogic.*57840.*",
                    "QLogic 57840"),
        QLogicFC(".*QLogic.*Fibre.*",
                 "QLogic FC"),
        QLogic(".*QLogic.*",
               "QLogic"),
        Broadcom(".*Broadcom.*",
                 "Broadcom"),
        Intel(".*Intel.*",
              "Intel"),
        Mellanox(".*Mellanox.*",
                "Mellanox"),
        Unknown("Unknown",
                "Unknown");

        private String pattern;
        private String nicName;

        NIC_NAMES(String pattern,
                  String nicName) {
            this.pattern = pattern;
            this.nicName = nicName;
        }

        public static NIC_NAMES findByName(String name) {
            if (name == null)
                return Unknown;
            for (NIC_NAMES nic : values()) {
                if (name.matches(nic.pattern)) {
                    return nic;
                }
            }
            return Unknown;
        }

        public String getNicName() {
            return nicName;
        }
    }

}
