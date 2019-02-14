package com.dell.asm.ui.adapter.service;

import com.dell.asm.alcm.client.model.Appliance;
import com.dell.asm.alcm.client.model.ApplianceSettings;

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
public interface ApplianceInfoServiceAdapter {

    public Appliance getAppliance();

    public void updateApplianceSettings(final ApplianceSettings applianceSettings);

    public ApplianceSettings getApplianceSettings();
}
