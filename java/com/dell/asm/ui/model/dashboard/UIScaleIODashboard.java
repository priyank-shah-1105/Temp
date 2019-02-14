/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.dashboard;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIScaleIODashboard extends UIBaseObject {
    public String id;
    public BigInteger usedInKb;
    public BigInteger totalInKb;
    public int protectionDomains;
    public int volumes;
    public int dataClients;
    public int dataServers;
    public List<UIScaleIOStorageComponent> storageComponents;

    public UIScaleIODashboard() {
        storageComponents = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "UIScaleIODashboard{" +
                "id='" + id + '\'' +
                ", usedInKb=" + usedInKb +
                ", totalInKb=" + totalInKb +
                ", protectionDomains=" + protectionDomains +
                ", volumes=" + volumes +
                ", dataClients=" + dataClients +
                ", dataServers=" + dataServers +
                ", storageComponents=" + storageComponents +
                '}';
    }
}
