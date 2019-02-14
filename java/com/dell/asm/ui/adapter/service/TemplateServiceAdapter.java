/**************************************************************************
 *   Copyright (c) 2014 - 2015 Dell Inc. All rights reserved.             *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.adapter.service;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateExportRequest;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateUploadRequest;

public interface TemplateServiceAdapter {

    ServiceTemplate getTemplate(String templateId);

    ServiceTemplate getCustomizedTemplate(String deviceId);

    ServiceTemplate getCustomizedComponent(String templateId,
                                           String serviceId,
                                           String componentType);

    ResourceList<ServiceTemplate> getAllTemplates(String sort,
                                                  List<String> filter,
                                                  Integer offset,
                                                  Integer limit,
                                                  Boolean full,
                                                  Boolean includeAttachments);

    ServiceTemplate createTemplate(ServiceTemplate mgmtTemplate);

    ServiceTemplate copyTemplate(String templateId, ServiceTemplate configuration);

    Response updateTemplate(String templateId, ServiceTemplate mgmtTemplate);

    Response deleteTemplate(String templateId);

    String exportTemplate(ServiceTemplateExportRequest template);

    ServiceTemplate uploadTemplate(ServiceTemplateUploadRequest uploadRequest);

    ServiceTemplate updateParameters(ServiceTemplate template);

    ServiceTemplate refreshTemplate(ServiceTemplate template,
                                    Boolean inDeployment,
                                    Boolean isBrownfield);

    ServiceTemplate uploadConfiguration(String configFilePath);

    void exportAllTemplates(final File downloadFile);

    ServiceTemplate updateComponents(String templateId, String serviceId, ServiceTemplate template);

    ServiceTemplate getTemplate(String templateId, boolean includeBrownfieldVmManagers,
                                boolean forDeployment);

    void deleteUsersFromTemplates(List<String> userIds);

    ServiceTemplate cloneTemplate(ServiceTemplate serviceTemplate);
}
