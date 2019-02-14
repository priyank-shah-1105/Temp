/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.device;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIScaleIOWorkload extends UIBaseObject {

    @JsonProperty
    public String currentIOWorkload;
    @JsonProperty
    public String currentIOPS;
    @JsonProperty
    public double status;
    @JsonProperty
    public String timestamp;

    public String getCurrentIOWorkload() {
        return currentIOWorkload;
    }

    public void setCurrentIOWorkload(String currentIOWorkload) {
        this.currentIOWorkload = currentIOWorkload;
    }

    public String getCurrentIOPS() {
        return currentIOPS;
    }

    public void setCurrentIOPS(String currentIOPS) {
        this.currentIOPS = currentIOPS;
    }

    public double getStatus() {
        return status;
    }

    public void setStatus(double status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentIOWorkload, currentIOPS, status, timestamp);
    }

    @Override
    public String toString() {
        return "UIScaleIOWorkload {" +
                "currentIOWorkload='" + currentIOWorkload + '\'' +
                ", currentIOPS='" + currentIOPS + '\'' +
                ", status='" + status + '\'' +
                ", timestamp='" + timestamp +
                '}';
    }
}
