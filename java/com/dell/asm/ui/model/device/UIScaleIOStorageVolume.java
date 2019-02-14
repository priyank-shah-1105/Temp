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
public class UIScaleIOStorageVolume extends UIBaseObject {

    public String id;
    public String name;
    public String size;
    public String type;
    public Integer mappedSDCs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIScaleIOStorageVolume)) return false;
        UIScaleIOStorageVolume that = (UIScaleIOStorageVolume) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "UIScaleIOStorageVolume{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", type='" + type + '\'' +
                ", mappedSDCs=" + mappedSDCs +
                '}';
    }
}
