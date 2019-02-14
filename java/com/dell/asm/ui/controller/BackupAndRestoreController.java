package com.dell.asm.ui.controller;

/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2014 Dell Inc. All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.alcm.client.model.DatabaseBackupSettings;
import com.dell.asm.alcm.client.model.RestoreStatus;
import com.dell.asm.alcm.client.model.RestoreStatusResponse;
import com.dell.asm.alcm.client.model.ScheduledBackupSettings;
import com.dell.asm.ui.adapter.service.ApplianceServiceAdapter;
import com.dell.asm.ui.adapter.service.BackupAndRestoreServiceAdapter;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.backuprestore.JobBackupScheduleRequest;
import com.dell.asm.ui.model.backuprestore.JobBackupSettingsRequest;
import com.dell.asm.ui.model.backuprestore.UIBackupScheduleInfo;
import com.dell.asm.ui.model.backuprestore.UIBackupSettings;

/**
 * Backup And Restore Controller
 *
 */

@RestController
@RequestMapping(value = "/backupandrestore/")
public class BackupAndRestoreController extends BaseController {
    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(NetworksController.class);

    private BackupAndRestoreServiceAdapter backupAndRestoreServiceAdapter;

    private ApplianceServiceAdapter applianceServiceAdapter;

    @Autowired
    public BackupAndRestoreController(BackupAndRestoreServiceAdapter backupAndRestoreServiceAdapter,
                                      ApplianceServiceAdapter applianceServiceAdapter) {
        this.backupAndRestoreServiceAdapter = backupAndRestoreServiceAdapter;
        this.applianceServiceAdapter = applianceServiceAdapter;
    }

    @RequestMapping(value = "getbackupsettings", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getBackupSettings() {
        log.debug("getBackupSettings()");
        JobResponse jobResponse = new JobResponse();
        UIBackupSettings responseObj;
        try {
            responseObj = parseBackupSettings(
                    backupAndRestoreServiceAdapter.getDatabaseBackupSettings());
            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("getBackupSettings() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "getbackupschedule", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getBackupSchedule() {
        log.debug("getBackupSchedule()");
        JobResponse jobResponse = new JobResponse();
        UIBackupScheduleInfo responseObj;
        try {
            responseObj = parseBackupSchedule(backupAndRestoreServiceAdapter.getBackupSchedule());
            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("getBackupSchedule() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "savebackupsettings", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse saveBackupSettings(@RequestBody JobBackupSettingsRequest request) {
        log.debug("saveBackupSettings() - JobBackupSettingsRequest received");
        JobResponse jobResponse = new JobResponse();

        try {
            DatabaseBackupSettings backupSettings;
            if (request.requestObj != null) {
                backupSettings = parseBackupSettings(request.requestObj);
                backupAndRestoreServiceAdapter.updateDatabaseBackupSettings(backupSettings);
            }
            jobResponse.responseObj = request.requestObj;
        } catch (Throwable t) {
            log.error("saveBackupSettings() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "setbackupschedule", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse setBackupSchedule(@RequestBody JobBackupScheduleRequest request) {
        JobResponse jobResponse = new JobResponse();

        try {
            ScheduledBackupSettings backupSettings;
            if (request.requestObj != null) {
                backupSettings = parseBackupSchedule(request.requestObj);
                backupAndRestoreServiceAdapter.setBackupSchedule(backupSettings);
            }
            jobResponse.responseObj = request.requestObj;
        } catch (Throwable t) {
            log.error("setBackupSchedule() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "backupnow", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse backupNow(@RequestBody JobBackupSettingsRequest request) {
        log.debug("backupNow() - JobBackupSettingsRequest received");
        JobResponse jobResponse = new JobResponse();

        try {
            DatabaseBackupSettings backupSettings;
            if (request.requestObj != null) {

                if ((request.requestObj.useBackupSettings)) {
                    backupAndRestoreServiceAdapter.backupAppliance(null);
                } else {
                    backupSettings = parseBackupSettings(request.requestObj);
                    backupAndRestoreServiceAdapter.backupAppliance(backupSettings);
                }
            }
        } catch (Throwable t) {
            log.error("backupNow() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "restore", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse restore(@RequestBody JobBackupSettingsRequest request, HttpSession session) {
        log.debug("restore() - JobBackupSettingsRequest received");
        JobResponse jobResponse = new JobResponse();

        try {
            DatabaseBackupSettings backupSettings;
            if (request.requestObj != null) {
                backupSettings = parseBackupSettings(request.requestObj);
                log.debug(
                        "Going to Restore the Appliance with DB: " + backupSettings.getSharePath());
                backupAndRestoreServiceAdapter.restoreAppliance(backupSettings);
            }
        } catch (Throwable t) {
            log.error("restore() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "testbackupconnection", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse testBackupConnection(@RequestBody JobBackupSettingsRequest request) {
        log.debug("testBackupConnection() - JobBackupSettingsRequest received");
        JobResponse jobResponse = new JobResponse();

        try {
            DatabaseBackupSettings backupSettings;
            if (request.requestObj != null) {

                if ((request.requestObj.useBackupSettings)) {
                    backupAndRestoreServiceAdapter.testBackupConnection(null);
                } else {
                    backupSettings = parseBackupSettings(request.requestObj);
                    backupAndRestoreServiceAdapter.testBackupConnection(backupSettings);
                }
            }
        } catch (Throwable t) {
            log.error("testBackupConnection() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "testrestoreconnection", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse testRestoreConnection(@RequestBody JobBackupSettingsRequest request) {
        return testBackupConnection(request);
    }

    @RequestMapping(value = "getrestorestatus", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getRestoreStatus(HttpSession session) {
        JobResponse jobResponse = new JobResponse();

        try {
            final RestoreStatusResponse restoreStatusRes = backupAndRestoreServiceAdapter.getRestoreStatus();
            if (restoreStatusRes != null && restoreStatusRes.getStatus() != null) {
                if (RestoreStatus.WAITING_FOR_RESTART.equals(restoreStatusRes.getStatus())) {
                    log.debug("Restore Completed. Going to Restart the Appliance");
                    applianceServiceAdapter.setRestarting(true);
                }
                jobResponse.responseObj = restoreStatusRes.getStatus().name().toLowerCase();
            }
        } catch (Throwable t) {
            log.error("getRestoreStatus() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * UIBackupSettings to DatabaseBackupSettings
     *
     * @param uiBackupSettings
     * @return
     */
    private DatabaseBackupSettings parseBackupSettings(UIBackupSettings uiBackupSettings) {
        if (uiBackupSettings == null)
            return null;

        DatabaseBackupSettings backupSettings = new DatabaseBackupSettings();

        if (StringUtils.isNotBlank(uiBackupSettings.backupLocation)) {
            backupSettings.setSharePath(uiBackupSettings.backupLocation);
        }
        if (StringUtils.isNotBlank(uiBackupSettings.userName)) {
            backupSettings.setShareUsername(uiBackupSettings.userName);
        }
        if (StringUtils.isNotBlank(uiBackupSettings.backupLocationPassword)) {
            backupSettings.setSharePassword(uiBackupSettings.backupLocationPassword);
        }
        if (StringUtils.isNotBlank(uiBackupSettings.encryptionPassword)) {
            backupSettings.setEncryptionPassword(uiBackupSettings.encryptionPassword);
        }

        return backupSettings;
    }

    /**
     * DatabaseBackupSettings to UIBackupSettings
     *
     * @param backupSettings
     * @return
     */
    private UIBackupSettings parseBackupSettings(DatabaseBackupSettings backupSettings) {
        UIBackupSettings ui = new UIBackupSettings();
        ui.backupLocation = backupSettings.getSharePath();
        ui.userName = backupSettings.getShareUsername();
        ui.backupLocationPassword = backupSettings.getSharePassword();
        ui.encryptionPassword = backupSettings.getEncryptionPassword();
        ui.encryptionVPassword = backupSettings.getEncryptionPassword();

        return ui;
    }

    /**
     * DatabaseBackupSettings to UIBackupSettings
     *
     * @param backupSchedule - the schedule backup settings
     * @return
     */
    private UIBackupScheduleInfo parseBackupSchedule(ScheduledBackupSettings backupSchedule) {
        UIBackupScheduleInfo ui = new UIBackupScheduleInfo();

        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

        if (backupSchedule == null ||
                backupSchedule.getBackupDaysOfWeek() == null || backupSchedule.getBackupDaysOfWeek().isEmpty()) {
            ui.enabled = "false";
        } else {
            List<String> backupDays = backupSchedule.getBackupDaysOfWeek();
            ui.enabled = "true";
            ui.sunday = backupDays.contains("SUN");
            ui.monday = backupDays.contains("MON");
            ui.tuesday = backupDays.contains("TUE");
            ui.wednesday = backupDays.contains("WED");
            ui.thursday = backupDays.contains("THU");
            ui.friday = backupDays.contains("FRI");
            ui.saturday = backupDays.contains("SAT");

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, backupSchedule.getBackupHour());
            cal.set(Calendar.MINUTE, backupSchedule.getBackupMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            ui.timeOfBackup = formatter.withZoneUTC().print(cal.getTimeInMillis());
        }

        if (backupSchedule != null) {
            if (backupSchedule.getLastBackupTime() != null) {
                ui.lastBackupDate = formatter.withZoneUTC().print(
                        backupSchedule.getLastBackupTime().getTime());
            }

            ui.lastBackupStatus = backupSchedule.getLastBackupStatus();
        }
        return ui;
    }

    /**
     * DatabaseBackupSettings to UIBackupSettings
     *
     * @param ui - The backup schedule info
     * @return
     */
    private ScheduledBackupSettings parseBackupSchedule(UIBackupScheduleInfo ui) {
        ScheduledBackupSettings scheduledBackupSettings = new ScheduledBackupSettings();
        List<String> backupDays = new ArrayList<>();

        if (ui.enabled.equals("true")) {
            if (ui.sunday)
                backupDays.add("SUN");
            if (ui.monday)
                backupDays.add("MON");
            if (ui.tuesday)
                backupDays.add("TUE");
            if (ui.wednesday)
                backupDays.add("WED");
            if (ui.thursday)
                backupDays.add("THU");
            if (ui.friday)
                backupDays.add("FRI");
            if (ui.saturday)
                backupDays.add("SAT");
            scheduledBackupSettings.setBackupDaysOfWeek(backupDays);

            if (ui.timeOfBackup != null && ui.timeOfBackup.length() > 0) {
                DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
                DateTime backupDate = formatter.parseDateTime(ui.timeOfBackup);

                Calendar cal = Calendar.getInstance();
                cal.setTime(backupDate.toDate());
                scheduledBackupSettings.setBackupHour(cal.get(Calendar.HOUR_OF_DAY));
                scheduledBackupSettings.setBackupMinute(cal.get(Calendar.MINUTE));
            }
        }
        return scheduledBackupSettings;
    }
}
