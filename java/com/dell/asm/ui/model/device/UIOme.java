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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIOme extends UIBaseObject {

    public String id;
    public String serviceTag;
    public int discoveredDevices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIOme)) return false;
        UIOme uiOme = (UIOme) o;
        return Objects.equals(id, uiOme.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
