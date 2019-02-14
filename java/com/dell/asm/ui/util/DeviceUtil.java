/**************************************************************************
 *   Copyright (c) 2014 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/

package com.dell.asm.ui.util;

public class DeviceUtil {

    public static final String DEFAULT_SWITCH_DEVICETYPE = "dellswitch";
    public static final String CISCO_SWITCH_DEVICETYPE = "ciscoswitch";
    public static final String AGGREGATOR_IOM_DEVICETYPE = "AggregatorIOM";
    public static final String MXL_IOM_DEVICETYPE = "MXLIOM";
    public static final String FX_IOM_DEVICETYPE = "FXIOM";


    public static String getSwitchDeviceType(String model) {
        String deviceType = DEFAULT_SWITCH_DEVICETYPE;
        if (model.toLowerCase().contains("aggregator")) {
            deviceType = AGGREGATOR_IOM_DEVICETYPE;
        } else if (model.toLowerCase().contains("mxl")) {
            deviceType = MXL_IOM_DEVICETYPE;
        } else if (model.toLowerCase().contains("2210") || model.toLowerCase().contains("410")) {
            deviceType = FX_IOM_DEVICETYPE;
        } else if (model.toLowerCase().contains("nexus")) {
            deviceType = CISCO_SWITCH_DEVICETYPE;
        }
        return deviceType;
    }
}
