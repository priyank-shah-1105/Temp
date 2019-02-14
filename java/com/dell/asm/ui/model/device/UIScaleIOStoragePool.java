/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIScaleIOStoragePool extends UIBaseObject {

    public String id;
    public String name;
    public List<UIScaleIOStorageVolume> scaleIOStorageVolumes;

    public UIScaleIOStoragePool() {
        scaleIOStorageVolumes = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIScaleIOStoragePool)) return false;
        UIScaleIOStoragePool that = (UIScaleIOStoragePool) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "UIScaleIOStoragePool{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", scaleIOStorageVolumes=" + scaleIOStorageVolumes +
                '}';
    }
}
