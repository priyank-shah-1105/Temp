package com.dell.asm.ui.adapter.service;

import com.dell.asm.alcm.client.model.DatabaseBackupSettings;
import com.dell.asm.alcm.client.model.RestoreStatusResponse;
import com.dell.asm.alcm.client.model.ScheduledBackupSettings;

/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2014 Dell Inc. All Rights Reserved.
 */

/**
 * Backup And Restore Service Adapter Interface
 *
 */
public interface BackupAndRestoreServiceAdapter {
    DatabaseBackupSettings getDatabaseBackupSettings();

    DatabaseBackupSettings updateDatabaseBackupSettings(
            DatabaseBackupSettings databaseBackupSettings);

    void backupAppliance(DatabaseBackupSettings databaseBackupSettings);

    void restoreAppliance(DatabaseBackupSettings databaseBackupSettings);

    RestoreStatusResponse getRestoreStatus();

    ScheduledBackupSettings getBackupSchedule();

    ScheduledBackupSettings setBackupSchedule(ScheduledBackupSettings databaseBackupScheduleInfo);

    void testBackupConnection(DatabaseBackupSettings databaseBackupSettings);
}
