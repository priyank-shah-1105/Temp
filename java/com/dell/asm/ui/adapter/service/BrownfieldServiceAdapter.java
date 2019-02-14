/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.adapter.service;

import java.util.List;

import com.dell.asm.asmcore.asmmanager.client.brownfield.NetworkSetting;
import com.dell.asm.asmcore.asmmanager.client.brownfield.PasswordSetting;
import com.dell.asm.asmcore.asmmanager.client.brownfield.VDSSetting;
import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateComponent;

public interface BrownfieldServiceAdapter {

    Deployment getServiceDefinition(String type,
                                    List<ServiceTemplateComponent> components,
                                    List<PasswordSetting> passwordSettings);

    Deployment getExistingServiceDiff(String serviceId);

    Deployment createExistingService(Deployment deployment,
                                     List<PasswordSetting> passwordSettings,
                                     List<VDSSetting> vdsSettings,
                                     List<NetworkSetting> networkSettings);

    Deployment updateEsxiServiceDefinition(Deployment deployment,
                                           List<PasswordSetting> passwordSettings,
                                           List<VDSSetting> vdsSettings);

    List<PasswordSetting> getOSCredentials(Deployment deployment);

    List<VDSSetting> getVDSSettings(Deployment deployment);

    List<NetworkSetting> getNetworkSettings(Deployment deployment);
}
