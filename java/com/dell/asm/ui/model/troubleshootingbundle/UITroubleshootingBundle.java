/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.troubleshootingbundle;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class hold the parameters entered by user required to generate and
 * copy the troubleshooting bundle
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITroubleshootingBundle extends UIBaseObject {

    private String serviceId;

    private String bundleDest;

    private String filepath;

    private String username;

    private String password;


    /**
     * Get the serviceId
     * This is when generate troubleshooting bundle is invoked from services page
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Set the serviceId
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Get the destination where the bundle has to be saved to
     * options are export or network
     */
    public String getBundleDest() {
        return bundleDest;
    }

    /**
     * Set the bundle destination
     */
    public void setBundleDest(String bundleDest) {
        this.bundleDest = bundleDest;
    }

    /**
     * Get file path if it is NFS or CIFS
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Set file path (NFS or CIFS)
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    /**
     * Get user name for CIFS share
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username for CIFS share
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the password for CIFS account
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password for CIFS account
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UITroubleshootingBundle [serviceId=" + serviceId + ", bundleDest=" + bundleDest + ", filePath="
                + filepath + ", username=" + username + ", password=" + password + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
        result = prime * result + ((bundleDest == null) ? 0 : bundleDest.hashCode());
        result = prime * result + ((filepath == null) ? 0 : filepath.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UITroubleshootingBundle other = (UITroubleshootingBundle) obj;
        if (serviceId == null) {
            if (other.serviceId != null)
                return false;
        } else if (!serviceId.equals(other.serviceId))
            return false;
        if (bundleDest == null) {
            if (other.bundleDest != null)
                return false;
        } else if (!bundleDest.equals(other.bundleDest))
            return false;
        if (filepath == null) {
            if (other.filepath != null)
                return false;
        } else if (!filepath.equals(other.filepath))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        return true;
    }
}
