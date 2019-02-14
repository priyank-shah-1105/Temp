/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.device;

import java.util.Objects;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIScaleIOInformation extends UIBaseObject {

    public long protectedInKb;
    public long inMaintenanceInKb;
    public long degradedInKb;
    public long failedInKb;
    public long unusedInKb;
    public long spareInKb;
    public long decreasedInKb;
    public long unavailableUnusedInKb;
    public long maxCapacityInKb;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIScaleIOInformation)) return false;
        UIScaleIOInformation that = (UIScaleIOInformation) o;
        return Long.compare(that.protectedInKb, protectedInKb) == 0 &&
                Long.compare(that.inMaintenanceInKb, inMaintenanceInKb) == 0 &&
                Long.compare(that.degradedInKb, degradedInKb) == 0 &&
                Long.compare(that.failedInKb, failedInKb) == 0 &&
                Long.compare(that.unusedInKb, unusedInKb) == 0 &&
                Long.compare(that.spareInKb, spareInKb) == 0 &&
                Long.compare(that.decreasedInKb, decreasedInKb) == 0 &&
                Long.compare(that.unavailableUnusedInKb, unavailableUnusedInKb) == 0 &&
                Long.compare(that.maxCapacityInKb, maxCapacityInKb) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(protectedInKb,
                            inMaintenanceInKb,
                            degradedInKb,
                            failedInKb,
                            unusedInKb,
                            spareInKb,
                            decreasedInKb,
                            unavailableUnusedInKb,
                            maxCapacityInKb);
    }

    @Override
    public String toString() {
        return "UIScaleIOInformation{" +
                "protectedInKb=" + protectedInKb +
                ", inMaintenanceInKb=" + inMaintenanceInKb +
                ", degradedInKb=" + degradedInKb +
                ", failedInKb=" + failedInKb +
                ", unusedInKb=" + unusedInKb +
                ", spareInKb=" + spareInKb +
                ", decreasedInKb=" + decreasedInKb +
                ", unavailableUnusedInKb=" + unavailableUnusedInKb +
                ", maxCapacityInKb=" + maxCapacityInKb +
                '}';
    }
}
