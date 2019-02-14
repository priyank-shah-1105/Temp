/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.log4j.Logger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UILocalStorage extends UIBaseObject {

    private static final Logger log = Logger.getLogger(UILocalStorage.class);
    public static Comparator<UILocalStorage> SORT_BY_PCI_SLOT_AND_NAME =
            new Comparator<UILocalStorage>() {
                @Override
                public int compare(UILocalStorage s1, UILocalStorage s2) {
                    // Ensure no Nulls before sorting
                    if (s1 != null && s2 == null) return 1;
                    if (s2 != null && s1 == null) return -1;
                    if (s2 == null && s1 == null) return 0;

                    if (!NumberUtils.isDigits(s1.raidPCISlot) && NumberUtils.isDigits(
                            s2.raidPCISlot))
                        return 1;
                    if (NumberUtils.isDigits(s1.raidPCISlot) && !NumberUtils.isDigits(
                            s2.raidPCISlot))
                        return -1;

                    if (s1.raidPCISlot.equals(s2.raidPCISlot)) {
                        return s1.raidControllerName.compareTo(s2.raidControllerName);
                    } else {
                        try {
                            Integer slot1 = Integer.valueOf(s1.raidPCISlot);
                            Integer slot2 = Integer.valueOf(s2.raidPCISlot);
                            return slot1.compareTo(slot2);
                        } catch (NumberFormatException e) {
                            log.error("Unexpected value for raidPCISlot: " + e.getMessage());
                            return s1.raidPCISlot.compareTo(s2.raidPCISlot);
                        }
                    }
                }
            };
    @JsonProperty
    public String id;
    @JsonProperty
    public String raidControllerName;
    @JsonProperty
    public String raidDeviceDescription;
    @JsonProperty
    public String raidPCISlot;
    @JsonProperty
    public String raidFirmwareVersion;
    @JsonProperty
    public String raidDriverVersion;
    @JsonProperty
    public String raidCacheMemorySize;
    @JsonProperty
    public List<UILogicalDisk> logicaldiskdata;
    @JsonProperty
    public List<UIPhysicalDisk> physicaldiskdata;

    public UILocalStorage() {
        super();
        logicaldiskdata = new ArrayList<>();
        physicaldiskdata = new ArrayList<>();
    }

}
