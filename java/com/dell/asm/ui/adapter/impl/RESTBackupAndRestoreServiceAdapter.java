/**************************************************************************
 *   Copyright (c) 2017 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.adapter.impl;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.DatabaseBackupSettings;
import com.dell.asm.alcm.client.model.RestoreStatusResponse;
import com.dell.asm.alcm.client.model.ScheduledBackupSettings;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.BackupAndRestoreServiceAdapter;

@Component("backupAndRestoreServiceAdapter")
public class RESTBackupAndRestoreServiceAdapter extends BaseServiceAdapter implements BackupAndRestoreServiceAdapter {

    /**
     * Instantiates a new service adapter. Sets REST client's API key and secret from SecurityContext.
     */
    @Autowired
    public RESTBackupAndRestoreServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/alcm/database");
    }

    @Override
    public DatabaseBackupSettings getDatabaseBackupSettings() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/backupSettings");
        return webClient.get(DatabaseBackupSettings.class);
    }

    @Override
    public DatabaseBackupSettings updateDatabaseBackupSettings(
            DatabaseBackupSettings databaseBackupSettings) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/backupSettings");
        return webClient.put(databaseBackupSettings, DatabaseBackupSettings.class);
    }

    @Override
    public void backupAppliance(DatabaseBackupSettings databaseBackupSettings) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        if (databaseBackupSettings == null || StringUtils.isEmpty(
                databaseBackupSettings.getSharePath())) {
            webClient.path("/backupNow");
            webClient.post(null, DatabaseBackupSettings.class);
        } else {
            webClient.path("/backupNowWithSettings");
            webClient.post(databaseBackupSettings, DatabaseBackupSettings.class);
        }
    }

    @Override
    public void restoreAppliance(DatabaseBackupSettings databaseBackupSettings) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/restore");
        webClient.post(databaseBackupSettings, DatabaseBackupSettings.class);
    }

    @Override
    public RestoreStatusResponse getRestoreStatus() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/restore/status");
        return webClient.get(RestoreStatusResponse.class);
    }

    @Override
    public ScheduledBackupSettings getBackupSchedule() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/backupSchedule");
        return webClient.get(ScheduledBackupSettings.class);
    }

    @Override
    public ScheduledBackupSettings setBackupSchedule(
            ScheduledBackupSettings databaseBackupScheduleInfo) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/backupSchedule");
        return webClient.post(databaseBackupScheduleInfo, ScheduledBackupSettings.class);
    }


    @Override
    public void testBackupConnection(DatabaseBackupSettings databaseBackupSettings) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        webClient.path("/testconnection");
        if (databaseBackupSettings != null) {
            webClient.post(databaseBackupSettings, DatabaseBackupSettings.class);
        } else {
            webClient.post(new DatabaseBackupSettings(), DatabaseBackupSettings.class);
        }
    }

}
