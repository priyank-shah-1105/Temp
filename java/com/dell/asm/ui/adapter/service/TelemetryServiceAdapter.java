/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.adapter.service;

import com.dell.asm.asmcore.asmmanager.client.telemetryconnector.LatestPerformanceMetrics;
import com.dell.asm.asmcore.asmmanager.client.telemetryconnector.HistoricalPerformanceMetrics;

public interface TelemetryServiceAdapter {
    LatestPerformanceMetrics getLatestPerformanceMetrics(String host);
    HistoricalPerformanceMetrics getHistoricalPerformanceMetrics(String host, String frequency, String metricType);
}
