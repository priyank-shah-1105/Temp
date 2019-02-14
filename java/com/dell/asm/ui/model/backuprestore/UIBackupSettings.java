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

/**
 * Class that Represents the model for the UI Backup Settings
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIBackupSettings extends UIBaseObject {

    /** The Backup Location */
    @JsonProperty
    public String backupLocation;

    /** The User Name */
    @JsonProperty
    public String userName;

    /** The Backup Location Password */
    @JsonProperty
    public String backupLocationPassword;

    /** The Encryption Password */
    @JsonProperty
    public String encryptionPassword;

    /** The Encryption VPassword */
    @JsonProperty
    public String encryptionVPassword;

    /** Indicator to enable/disable the use of the Backup Settings */
    @JsonProperty
    public boolean useBackupSettings;

    /**
     * Default Constructor
     */
    public UIBackupSettings() {
        super();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UIBackupSettings [backupLocation=");
        builder.append(backupLocation);
        builder.append(", userName=");
        builder.append(userName);
        builder.append(", useBackupSettings=");
        builder.append(useBackupSettings);
        builder.append("]");
        return builder.toString();
    }
}
