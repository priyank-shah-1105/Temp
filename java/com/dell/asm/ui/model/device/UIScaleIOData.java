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
import com.dell.asm.ui.model.server.UIUsageData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIScaleIOData extends UIBaseObject {

    @JsonProperty
    public UIUsageData total = new UIUsageData();
    @JsonProperty
    public UIUsageData read = new UIUsageData();
    @JsonProperty
    public UIUsageData write = new UIUsageData();

    public UIUsageData getTotal() {
        return total;
    }

    public void setTotal(UIUsageData total) {
        this.total = total;
    }

    public UIUsageData getRead() {
        return read;
    }

    public void setRead(UIUsageData read) {
        this.read = read;
    }

    public UIUsageData getWrite() {
        return write;
    }

    public void setWrite(UIUsageData write) {
        this.write = write;
    }

    @Override
    public String toString() {
        return "UIScaleIOData{" +
                "total=" + total +
                ", read=" + read +
                ", write=" + write +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIScaleIOData that = (UIScaleIOData) o;
        return Objects.equals(total, that.total) &&
                Objects.equals(read, that.read) &&
                Objects.equals(write, that.write);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, read, write);
    }
}
