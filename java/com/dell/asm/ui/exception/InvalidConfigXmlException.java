/**************************************************************************
 *   Copyright (c) 2015 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.exception;

/**
 * @author kdaniel
 *
 */
public class InvalidConfigXmlException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String faultComponent;
    private String requiredAttribute;

    public InvalidConfigXmlException(String faultComponent, String requiredAttribute) {
        this.faultComponent = faultComponent;
        this.requiredAttribute = requiredAttribute;
    }

    /**
     * @return the faultComponent
     */
    public String getFaultComponent() {
        return faultComponent;
    }

    /**
     * @param faultComponent the faultComponent to set
     */
    public void setFaultComponent(String faultComponent) {
        this.faultComponent = faultComponent;
    }

    /**
     * @return the requiredAttribute
     */
    public String getRequiredAttribute() {
        return requiredAttribute;
    }

    /**
     * @param requiredAttribute the requiredAttribute to set
     */
    public void setRequiredAttribute(String requiredAttribute) {
        this.requiredAttribute = requiredAttribute;
    }
}
