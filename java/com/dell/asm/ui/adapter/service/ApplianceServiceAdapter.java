package com.dell.asm.ui.adapter.service;

import com.dell.asm.alcm.client.model.ASMVersion;
import com.dell.asm.alcm.client.model.ApplianceHealth;
import com.dell.asm.alcm.client.model.UpgradeStatus;

/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2014 Dell Inc. All Rights Reserved.
 */

/**
 * Appliance Service Adapter Interface
 *
 */
public interface ApplianceServiceAdapter {

    void restartAppliance();

    void updateAppliance();

    ASMVersion getASMVersion();

    boolean isRestarting();

    void setRestarting(boolean isRestarting);

    /**
     * Gets the appliance health.
     *
     * @return the appliance health
     */
    ApplianceHealth getApplianceHealth();

    UpgradeStatus getUpgradeStatus();

}
