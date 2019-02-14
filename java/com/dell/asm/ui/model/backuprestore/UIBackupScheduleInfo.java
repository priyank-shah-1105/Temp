package com.dell.asm.ui.model.backuprestore;

/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2014 Dell Inc. All Rights Reserved.
 */

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIBackupScheduleInfo extends UIBaseObject {
    /** Indicates if the Schedule is enabled */
    @JsonProperty
    public String enabled;

    /** Indicates if schedule is set for Sunday */
    @JsonProperty
    public boolean sunday;

    /** Indicates if schedule is set for Monday */
    @JsonProperty
    public boolean monday;

    /** Indicates if schedule is set for Tuesday */
    @JsonProperty
    public boolean tuesday;

    /** Indicates if schedule is set for Wednesday */
    @JsonProperty
    public boolean wednesday;

    /** Indicates if schedule is set for Thursday */
    @JsonProperty
    public boolean thursday;

    /** Indicates if schedule is set for Friday */
    @JsonProperty
    public boolean friday;

    /** Indicates if schedule is set for Saturday */
    @JsonProperty
    public boolean saturday;

    /** The time of the Backup */
    @JsonProperty
    public String timeOfBackup;

    /** The Next Backup Date */
    @JsonProperty
    public String nextBackup;

    /** The last Backup Date */
    @JsonProperty
    public String lastBackupDate;

    /** The last Backup Status */
    @JsonProperty
    public String lastBackupStatus;

    /**
     * Default Constructor
     */
    public UIBackupScheduleInfo() {
        super();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UIBackupScheduleInfo [enabled=");
        builder.append(enabled);
        builder.append(", sunday=");
        builder.append(sunday);
        builder.append(", monday=");
        builder.append(monday);
        builder.append(", tuesday=");
        builder.append(tuesday);
        builder.append(", wednesday=");
        builder.append(wednesday);
        builder.append(", thursday=");
        builder.append(thursday);
        builder.append(", friday=");
        builder.append(friday);
        builder.append(", saturday=");
        builder.append(saturday);
        builder.append(", timeOfBackup=");
        builder.append(timeOfBackup);
        builder.append(", nextBackup=");
        builder.append(nextBackup);
        builder.append(", lastBackupDate=");
        builder.append(lastBackupDate);
        builder.append(", lastBackupStatus=");
        builder.append(lastBackupStatus);
        builder.append("]");
        return builder.toString();
    }

}
