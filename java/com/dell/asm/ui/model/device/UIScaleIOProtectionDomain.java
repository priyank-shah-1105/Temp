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
public class UIScaleIOProtectionDomain extends UIBaseObject {

    public String id;
    public String protectionDomainName;
    public int storageVolumes;
    public String storageSize;
    public int mappedSDCs;

    public List<UIScaleIOServerType> scaleIOServerTypes;
    public List<UIScaleIOStoragePool> scaleIOStoragePools;

    public UIScaleIOProtectionDomain() {
        scaleIOServerTypes = new ArrayList<>();
        scaleIOStoragePools = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIScaleIOProtectionDomain)) return false;
        UIScaleIOProtectionDomain that = (UIScaleIOProtectionDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UIScaleIOProtectionDomain{" +
                "id='" + id + '\'' +
                ", protectionDomainName='" + protectionDomainName + '\'' +
                ", storageVolumes=" + storageVolumes +
                ", storageSize='" + storageSize + '\'' +
                ", mappedSDCs=" + mappedSDCs +
                ", scaleIOServerTypes=" + scaleIOServerTypes +
                ", scaleIOStoragePools=" + scaleIOStoragePools +
                '}';
    }
}
