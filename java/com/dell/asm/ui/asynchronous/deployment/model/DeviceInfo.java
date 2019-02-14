/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.asynchronous.deployment.model;

public class DeviceInfo {
    private String deploymentId;
    private String deploymentName;
    private String deviceId;
    private String deviceServiceTag;
    private String deviceType;

    public DeviceInfo(String deploymentId, String deploymentName, String deviceId,
                      String deviceServiceTag, String deviceType) {
        this.deploymentId = deploymentId;
        this.deploymentName = deploymentName;
        this.deviceId = deviceId;
        this.deviceServiceTag = deviceServiceTag;
        this.deviceType = deviceType;
    }

    public String getDeploymentId() {
        return deploymentId;
    }


    public String getDeploymentName() {
        return deploymentName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceServiceTag() {
        return deviceServiceTag;
    }

    public void setDeviceServiceTag(String deviceServiceTag) {
        this.deviceServiceTag = deviceServiceTag;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}