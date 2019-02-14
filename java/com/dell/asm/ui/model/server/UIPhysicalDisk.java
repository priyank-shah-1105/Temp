/**************************************************************************
 *   Copyright (c) 2016 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.server;

import java.util.Comparator;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPhysicalDisk extends UIBaseObject {

    public static Comparator<UIPhysicalDisk> SORT_BY_SLOT_AND_NAME =
            new Comparator<UIPhysicalDisk>() {
                @Override
                public int compare(UIPhysicalDisk s1, UIPhysicalDisk s2) {
                    // Ensure no Nulls before sorting
                    if (s1 != null && s2 == null) return 1;
                    if (s2 != null && s1 == null) return -1;
                    if (s2 == null && s1 == null) return 0;

                    if (s1.slotNumber.equals(s2.slotNumber)) {
                        return s1.physicalDiskName.compareTo(s2.physicalDiskName);
                    } else {
                        final Integer slot1 = Integer.valueOf(s1.slotNumber);
                        final Integer slot2 = Integer.valueOf((s2.slotNumber));
                        return slot1.compareTo(slot2);
                    }
                }
            };
    @JsonProperty
    public String id;
    @JsonProperty
    public String physicalDiskName;
    @JsonProperty
    public String state;
    @JsonProperty
    public String slotNumber;
    @JsonProperty
    public String size;
    @JsonProperty
    public String securityStatus;
    @JsonProperty
    public String busProtocol;
    @JsonProperty
    public String mediaType;
    @JsonProperty
    public String hotSpare;


}
