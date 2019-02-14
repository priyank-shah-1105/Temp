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
public class UIScaleIO extends UIBaseObject {

    public String id;
    public String systemId;
    public String name;
    public String ipaddressurl;
    public UIScaleIOInformation scaleIOInformation;
    public List<UIScaleIOProtectionDomain> scaleIOProtectionDomains;
    public int protectionDomainCount;
    public int volumeCount;
    public int sDCCount;
    public int sDSCount;
    public String management;

    public UIScaleIOData ioData;
    public UIScaleIOData bandwidthData;

    public UIScaleIO() {
        scaleIOProtectionDomains = new ArrayList<>();
        ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIScaleIO)) return false;
        UIScaleIO uiScaleIO = (UIScaleIO) o;
        return Objects.equals(id, uiScaleIO.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UIScaleIO{" +
                "id='" + id + '\'' +
                ", systemId='" + systemId + '\'' +
                ", name='" + name + '\'' +
                ", ipaddressurl='" + ipaddressurl + '\'' +
                ", scaleIOInformation=" + scaleIOInformation +
                ", scaleIOProtectionDomains=" + scaleIOProtectionDomains +
                ", protectionDomainCount=" + protectionDomainCount +
                ", volumeCount=" + volumeCount +
                ", sDCCount=" + sDCCount +
                ", sDSCount=" + sDSCount +
                ", management='" + management + '\'' +
                ", ioData='" + ioData + '\'' +
                ", bandwidthData='" + bandwidthData + '\'' +
                '}';
    }
}
