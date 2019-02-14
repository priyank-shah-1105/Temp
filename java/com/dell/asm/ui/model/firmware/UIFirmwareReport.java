/**************************************************************************
 *   Copyright (c) 2015 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.firmware;

import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIFirmwareReport extends UIBaseObject {
    @JsonProperty
    private String id;

    @JsonProperty
    private String firmwareCompliant;

    @JsonProperty
    private String firmwareCompliantText;

    @JsonProperty
    private List<UIFirmwareReportDevice> devices;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the fimwareCompliant
     */
    public String getFirmwareCompliant() {
        return firmwareCompliant;
    }

    /**
     * @param fimwareCompliant the fimwareCompliant to set
     */
    public void setFirmwareCompliant(String fimwareCompliant) {
        this.firmwareCompliant = fimwareCompliant;
    }

    /**
     * @return the fimwareCompliantText
     */
    public String getFirmwareCompliantText() {
        return firmwareCompliantText;
    }

    /**
     * @param fimwareCompliantText the fimwareCompliantText to set
     */
    public void setFirmwareCompliantText(String fimwareCompliantText) {
        this.firmwareCompliantText = fimwareCompliantText;
    }

    /**
     * @return the devices
     */
    public List<UIFirmwareReportDevice> getDevices() {
        return devices;
    }

    /**
     * @param devices the devices to set
     */
    public void setDevices(List<UIFirmwareReportDevice> devices) {
        this.devices = devices;
    }
}
