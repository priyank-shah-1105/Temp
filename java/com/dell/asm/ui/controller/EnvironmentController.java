package com.dell.asm.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.alcm.client.model.AvailableTimeZones;
import com.dell.asm.alcm.client.model.NTPSetting;
import com.dell.asm.alcm.client.model.TimeZoneInfo;
import com.dell.asm.ui.adapter.service.ApplianceTimeZoneServiceAdapter;
import com.dell.asm.ui.adapter.service.NTPSettingsServiceAdapter;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.environment.UIMonitoringSettings;
import com.dell.asm.ui.model.initialsetup.JobTimeDataRequest;
import com.dell.asm.ui.model.initialsetup.UITimeData;

/**
 * The Class EnvironmentController.
 */
@RestController
@RequestMapping(value = "/environment/")
public class EnvironmentController extends BaseController {

    private static final Logger log = Logger.getLogger(EnvironmentController.class);

    /** The appliance NTP settings service adapter. */
    private NTPSettingsServiceAdapter ntpSettingsServiceAdapter;

    /** The appliance Timezone service adapter. */
    private ApplianceTimeZoneServiceAdapter applianceTimeZoneServiceAdapter;

    @Autowired
    public EnvironmentController(NTPSettingsServiceAdapter ntpSettingsServiceAdapter,
                                 ApplianceTimeZoneServiceAdapter applianceTimeZoneServiceAdapter) {
        this.ntpSettingsServiceAdapter = ntpSettingsServiceAdapter;
        this.applianceTimeZoneServiceAdapter = applianceTimeZoneServiceAdapter;
    }

    /**
     * Gets the NTP &amp; TimeZone Settings on the appliance.
     *
     * @return the NTP &amp; TimeZone settings on the appliance.
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getntptimezonesettings", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getNTPAndTimeZoneSettings() {
        JobResponse jobResponse = new JobResponse();
        UITimeData responseObj;
        responseObj = new UITimeData();

        try {
            // Get the current NTP settings on the appliance.
            NTPSetting ntp = ntpSettingsServiceAdapter.getNTPSettings();
            if (ntp != null) {
                if (ntp.getPreferredNTPServer() != null && ntp.getPreferredNTPServer().length() > 0) {
                    responseObj.enableNTPServer = true;
                    responseObj.preferredNTPServer = ntp.getPreferredNTPServer();
                    if (ntp.getSecondaryNTPServer() != null)
                        responseObj.secondaryNTPServer = ntp.getSecondaryNTPServer();
                }
            }

            // Get the current timeZone of the appliance.
            TimeZoneInfo tz = applianceTimeZoneServiceAdapter.getTimeZone();
            if (tz != null) {
                if (tz.getTimeZoneId() == null) {
                    responseObj.timeZone = "11"; // default ( CST )
                } else {
                    responseObj.timeZone = tz.getTimeZoneId();
                }
            }
        } catch (Throwable t) {
            log.error(
                    "getNTPAndTimeZoneSettings() - An Exception occurred in the ntp and timezone settings service call",
                    t);
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Gets the NTP Settings on the appliance.
     *
     * @return the NTP settings on the appliance.
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getntpsettings", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getNTPSettings() {
        JobResponse jobResponse = new JobResponse();
        UITimeData responseObj;
        responseObj = new UITimeData();

        try {
            NTPSetting ntp = ntpSettingsServiceAdapter.getNTPSettings();
            if (ntp != null) {
                if (ntp.getPreferredNTPServer() != null && ntp.getPreferredNTPServer().length() > 0) {
                    responseObj.enableNTPServer = true;
                    responseObj.preferredNTPServer = ntp.getPreferredNTPServer();
                    if (ntp.getSecondaryNTPServer() != null)
                        responseObj.secondaryNTPServer = ntp.getSecondaryNTPServer();
                }
            }
        } catch (Throwable t) {
            log.error("getNTPSettings() - An Exception occurred in the ntp settings service call",
                      t);
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Gets the available time zones.
     *
     * @return the available time zones.
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
     * Update the NTP &amp; TimeZone Settings on the appliance.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "setntptimezonesettings", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateNTPAndTimeZoneSettings(@RequestBody JobTimeDataRequest request) {
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

        } catch (Throwable t) {
            log.error("updateNTPAndTimeZoneSettings() - An Exception occurred in the service call",
                      t);
            return addFailureResponseInfo(t);
        }
        jobResponse.responseObj = request.requestObj;
        return jobResponse;
    }

    /**
     * Gets the Monitoring Settings.
     *
     * @return the Monitoring Settings.
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getmonitoringsettings", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getMonitoringSettings() {
        JobResponse jobResponse = new JobResponse();
        UIMonitoringSettings responseObj = new UIMonitoringSettings();

        try {
            log.debug("getMonitoringSettings() - Missing data gathering");
        } catch (Throwable t) {
            log.error(
                    "getMonitoringSettings() - An Exception occurred in the monitoring settings service call",
                    t);
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

}