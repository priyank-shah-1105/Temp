/**************************************************************************
 * Copyright (c) 2017 Dell Inc. All rights reserved.                    *
 * *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.dell.asm.asmcore.asmmanager.client.configuretemplate.ConfigureTemplate;
import com.dell.asm.asmcore.asmmanager.client.configuretemplate.ConfigureTemplateCategory;
import com.dell.asm.asmcore.asmmanager.client.configuretemplate.ConfigureTemplateOption;
import com.dell.asm.asmcore.asmmanager.client.configuretemplate.ConfigureTemplateSetting;
import com.dell.asm.asmcore.asmmanager.client.configuretemplate.ConfigureTemplateSettingIDs;
import com.dell.asm.asmcore.asmmanager.client.discovery.DeviceType;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.Network;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSettingIDs;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateUploadRequest;
import com.dell.asm.ui.adapter.service.ConfigureTemplateServiceAdapter;
import com.dell.asm.ui.adapter.service.FirmwareRepositoryServiceAdapter;
import com.dell.asm.ui.adapter.service.TemplateServiceAdapter;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.configuretemplate.UIConfigureTemplateCategory;
import com.dell.asm.ui.model.configuretemplate.UIConfigureTemplateComponent;
import com.dell.asm.ui.model.configuretemplate.UIConfigureTemplateConfiguration;
import com.dell.asm.ui.model.configuretemplate.UIConfigureTemplateListItem;
import com.dell.asm.ui.model.configuretemplate.UIConfigureTemplateSetting;
import com.dell.asm.ui.model.configuretemplate.UINetworkSettings;
import com.dell.asm.ui.model.configuretemplate.UIServerSettings;
import com.dell.asm.ui.model.configuretemplate.UIStorageSettings;
import com.dell.asm.ui.model.credential.UICredential;
import com.dell.asm.ui.model.template.JobSaveTemplateRequest;
import com.dell.asm.ui.model.template.UITemplateBuilder;
import com.dell.pg.asm.identitypool.api.common.model.NetworkType;

/**
 * Configure Template Controller.
 */
@RestController
@RequestMapping(value = "/configuretemplate/")
public class ConfigureTemplateController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(ConfigureTemplateController.class);

    private ConfigureTemplateServiceAdapter configureTemplateServiceAdapter;
    private TemplateServiceAdapter templateServiceAdapter;
    private FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter;

    @Autowired
    public ConfigureTemplateController(
            ConfigureTemplateServiceAdapter configureTemplateServiceAdapter,
            TemplateServiceAdapter templateServiceAdapter,
            FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter) {
        this.configureTemplateServiceAdapter = configureTemplateServiceAdapter;
        this.templateServiceAdapter = templateServiceAdapter;
        this.firmwareRepositoryServiceAdapter = firmwareRepositoryServiceAdapter;
    }

    public static void createUIConfigureTemplateConfiguration(UITemplateBuilder templateBuilder,
                                                              final ConfigureTemplate configuration) {
        if (configuration != null && configuration.getCategories().size() > 0) {

            final Map<String, ConfigureTemplateCategory> categoriesMap = configuration.getCategoriesMap();

            Map<String, List<UIConfigureTemplateComponent>> componentAssociationsMap = new HashMap<>();

            ConfigureTemplateCategory associationCategory = categoriesMap.get(
                    ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_NETWORKING_ASSOCIATIONS_RESOURCE);
            if (associationCategory != null) {
                createUIConfigureTemplateComponents(componentAssociationsMap, associationCategory);
            }
            associationCategory = categoriesMap.get(
                    ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_OS_ASSOCIATIONS_RESOURCE);
            if (associationCategory != null) {
                createUIConfigureTemplateComponents(componentAssociationsMap, associationCategory);
            }
            associationCategory = categoriesMap.get(
                    ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_CLUSTER_ASSOCIATIONS_RESOURCE);
            if (associationCategory != null) {
                createUIConfigureTemplateComponents(componentAssociationsMap, associationCategory);
            }
            associationCategory = categoriesMap.get(ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SCALEIO_ASSOCIATIONS_RESOURCE);
            if (associationCategory != null) {
                createUIConfigureTemplateComponents(componentAssociationsMap, associationCategory);
            }
            associationCategory = categoriesMap.get(
                    ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_POOL_ASSOCIATIONS_RESOURCE);
            if (associationCategory != null) {
                createUIConfigureTemplateComponents(componentAssociationsMap, associationCategory);
            }
            associationCategory = categoriesMap.get(
                    ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_ASSOCIATIONS_RESOURCE);
            if (associationCategory != null) {
                createUIConfigureTemplateComponents(componentAssociationsMap, associationCategory);
            }

            UIConfigureTemplateConfiguration configurationTemplate = new UIConfigureTemplateConfiguration();
            for (ConfigureTemplateCategory category : configuration.getCategories()) {
                switch (category.getId()) {
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_NETWORKING_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        configurationTemplate.networkSettings =
                                createUINetworkSettingsList(category, componentAssociationsMap);
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_OS_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        configurationTemplate.osSettings.osRepositories = createUIConfigureTemplateCategoryList(
                                category, componentAssociationsMap);
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_OS_CREDENTIAL_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        ConfigureTemplateSetting svmOSCredentialSetting = category.getParameter(ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SVM_OS_CREDENTIAL);
                        if (svmOSCredentialSetting != null) {
                            configurationTemplate.osSettings.hyperconverged = true;
                        }
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_CLUSTER_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        configurationTemplate.clusterSettings = createUIConfigureTemplateCategoryList(
                                category, componentAssociationsMap);
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SCALEIO_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        configurationTemplate.scaleIOSettings = createUIConfigureTemplateCategoryList(
                                category, componentAssociationsMap);
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_POOL_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        configurationTemplate.serverPoolSettings = createUIConfigureTemplateCategoryList(
                                category, componentAssociationsMap);
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        configurationTemplate.storageSettings = createUIStorageSettingsList(
                                category, componentAssociationsMap);
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_CLUSTER_DETAILS_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        configurationTemplate.clusterDetailsSettings = createUIConfigureTemplateSettingsList(
                                category);
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        configurationTemplate.serverSettings = createUIServerSettingsSetting(
                                category);
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        configurationTemplate.storageSettings.add(
                                createUIStorageDetailsSettings(category));
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_VDS_SETTINGS_RESOURCE:
                    if (category.getParameters() != null && category.getParameters().size() > 0) {
                        configurationTemplate.vdsSettings = createUIConfigureTemplateSettingsList(
                                category);
                    }
                default:
                    break;
                }
            }
            templateBuilder.configureTemplateConfiguration = configurationTemplate;
        }
    }

    private static List<UIConfigureTemplateCategory> createUIConfigureTemplateCategoryList(
            ConfigureTemplateCategory category,
            Map<String, List<UIConfigureTemplateComponent>> componentAssociationsMap) {
        List<UIConfigureTemplateCategory> categoryList = null;
        if (category != null) {
            categoryList = new ArrayList<>();
            for (ConfigureTemplateSetting setting : category.getParameters()) {
                UIConfigureTemplateCategory entry = new UIConfigureTemplateCategory();
                entry.id = setting.getId();
                entry.name = setting.getDisplayName();
                entry.value = setting.getValue();
                if (setting.getDeviceType() != null) {
                    switch (setting.getDeviceType()) {
                    case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_CLUSTER_VCENTER:
                        entry.type = DeviceController.getDeviceType(DeviceType.vcenter);
                        break;
                    case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_CLUSTER_SCVMM:
                        entry.type = DeviceController.getDeviceType(DeviceType.scvmm);
                        break;
                    case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_CLUSTER_RHVM:
                        entry.type = DeviceController.getDeviceType(DeviceType.rhvm);
                        break;
                    case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SCALEIO:
                        entry.type = DeviceController.getDeviceType(DeviceType.scaleio);
                        break;
                    default:
                        break;
                    }
                }
                for (ConfigureTemplateOption option : setting.getOptions()) {
                    entry.options.add(new UIListItem(option.getId(), option.getName()));
                }
                if (componentAssociationsMap.get(setting.getId()) != null) {
                    entry.components = componentAssociationsMap.get(setting.getId());
                }
                categoryList.add(entry);
            }
        }
        return categoryList;
    }

    private static List<UINetworkSettings> createUINetworkSettingsList(
            ConfigureTemplateCategory category,
            Map<String, List<UIConfigureTemplateComponent>> componentAssociationsMap) {
        List<UINetworkSettings> categoryList = null;
        if (category != null) {
            categoryList = new ArrayList<>();
            for (ConfigureTemplateSetting setting : category.getParameters()) {
                UINetworkSettings entry = new UINetworkSettings();
                entry.id = setting.getId();
                entry.name = setting.getDisplayName();
                entry.value = setting.getValue();
                if (setting.getNetworks() != null && setting.getNetworks().size() > 0) {
                    for (Network network : setting.getNetworks()) {
                        entry.type = com.dell.pg.asm.identitypoolmgr.network.util.NetworkType.fromValue(network.getType().name()).getLabel();
                        entry.network = network.getType().name();
                    }
                }
                for (ConfigureTemplateOption option : setting.getOptions()) {
                    entry.options.add(new UIListItem(option.getId(), option.getName()));
                }
                if (componentAssociationsMap.get(setting.getId()) != null) {
                    entry.components = componentAssociationsMap.get(setting.getId());
                }
                categoryList.add(entry);
            }
        }
        return categoryList;
    }

    private static List<UIConfigureTemplateSetting> createUIConfigureTemplateSettingsList(
            final ConfigureTemplateCategory category) {
        List<UIConfigureTemplateSetting> settingList = null;
        if (category != null) {
            settingList = new ArrayList<>();
            for (ConfigureTemplateSetting setting : category.getParameters()) {
                UIConfigureTemplateSetting cts = createUIConfigureTemplateSetting(setting);
                settingList.add(cts);
            }
        }
        return settingList;
    }

    private static List<UIStorageSettings> createUIStorageSettingsList(
            ConfigureTemplateCategory category,
            Map<String, List<UIConfigureTemplateComponent>> componentAssociationsMap) {
        List<UIStorageSettings> categoryList = null;
        if (category != null) {
            categoryList = new ArrayList<>();
            for (ConfigureTemplateSetting setting : category.getParameters()) {
                UIStorageSettings entry = new UIStorageSettings();
                entry.id = setting.getId();
                entry.name = setting.getDisplayName();
                entry.value = setting.getValue();
                if (setting.getDeviceType() != null) {
                    // TODO: detect type
                }
                for (ConfigureTemplateOption option : setting.getOptions()) {
                    entry.options.add(new UIListItem(option.getId(), option.getName()));
                }
                if (componentAssociationsMap.get(setting.getId()) != null) {
                    entry.components = componentAssociationsMap.get(setting.getId());
                }
                categoryList.add(entry);
            }
        }
        return categoryList;
    }

    private static UIServerSettings createUIServerSettingsSetting(
            final ConfigureTemplateCategory category) {
        UIServerSettings serverSettings = null;
        if (category != null && category.getParameters() != null && category.getParameters().size() > 0) {
            serverSettings = new UIServerSettings();
            for (ConfigureTemplateSetting setting : category.getParameters()) {
                switch (setting.getId()) {
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_NUMBER_OF_ID:
                    if (setting.getOptions() != null && setting.getOptions().size() > 0) {
                        for (ConfigureTemplateOption option : setting.getOptions()) {
                            serverSettings.numServersList.add(
                                    new UIListItem(option.getId(), option.getName()));
                        }
                    }
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        serverSettings.numServers = setting.getValue();
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_SERVER_POOL_ID:
                    if (setting.getOptions() != null && setting.getOptions().size() > 0) {
                        for (ConfigureTemplateOption option : setting.getOptions()) {
                            serverSettings.serverPoolOptions.add(
                                    new UIListItem(option.getId(), option.getName()));
                        }
                    }
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        serverSettings.serverPoolId = setting.getValue();
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_OS_IMAGE_ID:
                    if (setting.getOptions() != null && setting.getOptions().size() > 0) {
                        for (ConfigureTemplateOption option : setting.getOptions()) {
                            serverSettings.osList.add(
                                    new UIListItem(option.getId(), option.getName()));
                        }
                    }
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        serverSettings.existingOS = setting.getValue();
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_RECOMMENDED_ESXI_ID:
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        serverSettings.recommended = setting.getValue();
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_NAME_PREFIX_ID:
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        serverSettings.serverName = setting.getValue();
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_NAME_SUFFIX_ID:
                    if (setting.getOptions() != null && setting.getOptions().size() > 0) {
                        for (ConfigureTemplateOption option : setting.getOptions()) {
                            serverSettings.serverFormatOptions.add(
                                    new UIListItem(option.getId(), option.getName()));
                        }
                    }
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        serverSettings.serverNameSuffix = setting.getValue();
                    }
                    break;
                default:
                    break;
                }
            }
        }
        return serverSettings;
    }

    private static UIStorageSettings createUIStorageDetailsSettings(
            final ConfigureTemplateCategory category) {
        UIStorageSettings storageSetting = null;
        if (category != null) {
            storageSetting = new UIStorageSettings();
            storageSetting.id = category.getId();
            storageSetting.name = category.getDisplayName();
            if (category.getDeviceType() != null) {
                // no storage yet
            }
            for (ConfigureTemplateSetting setting : category.getParameters()) {
                switch (setting.getId()) {
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_STORAGE_ARRAY_ID:
                    if (setting.getOptions() != null && setting.getOptions().size() > 0) {
                        for (ConfigureTemplateOption option : setting.getOptions()) {
                            storageSetting.storageArrays.add(
                                    new UIListItem(option.getId(), option.getName()));
                        }
                    }
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        storageSetting.storageArrayId = setting.getValue();
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_STORAGE_POOL_ID:
                    if (setting.getOptions() != null && setting.getOptions().size() > 0) {
                        for (ConfigureTemplateOption option : setting.getOptions()) {
                            storageSetting.storagePools.add(
                                    new UIConfigureTemplateListItem(option.getId(),
                                                                    option.getName(),
                                                                    option.getDependencyTarget(),
                                                                    option.getDependencyValue()));
                        }
                    }
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        storageSetting.storagePoolId = setting.getValue();
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_VOLUME_COUNT_ID:
                    if (setting.getOptions() != null && setting.getOptions().size() > 0) {
                        for (ConfigureTemplateOption option : setting.getOptions()) {
                            storageSetting.numVolumesList.add(
                                    new UIListItem(option.getId(), option.getName()));
                        }
                    }
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        storageSetting.numStorageVolumes = setting.getValue();
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_VOLUME_SIZE_ID:
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        storageSetting.storageVolumeSize = Integer.parseInt(setting.getValue());
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_VOLUME_SIZE_MEASURE_ID:
                    if (setting.getOptions() != null && setting.getOptions().size() > 0) {
                        for (ConfigureTemplateOption option : setting.getOptions()) {
                            storageSetting.storageVolumeUnits.add(
                                    new UIListItem(option.getId(), option.getName()));
                        }
                    }
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        storageSetting.storageVolumeUnit = setting.getValue();
                    }
                    break;
                case ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_VOLUME_NAME_TEMPLATE_ID:
                    if (StringUtils.isNotBlank(setting.getValue())) {
                        storageSetting.volumeName = setting.getValue();
                    }
                    break;
                }
            }
        }
        return storageSetting;
    }

    private static UIConfigureTemplateSetting createUIConfigureTemplateSetting(
            final ConfigureTemplateSetting setting) {
        UIConfigureTemplateSetting configureTemplateSetting = null;
        if (setting != null) {
            configureTemplateSetting = new UIConfigureTemplateSetting();
            configureTemplateSetting.id = setting.getId();
            configureTemplateSetting.datatype = getUITemplateSettingDataType(setting.getType());
            if (setting.getMax() != null)
                configureTemplateSetting.max = setting.getMax();
            if (setting.getMin() != null)
                configureTemplateSetting.min = setting.getMin();
            if (setting.getMaxLength() != null)
                configureTemplateSetting.maxlength = setting.getMaxLength();
            if (setting.getStep() != null)
                configureTemplateSetting.step = setting.getStep();
            configureTemplateSetting.name = setting.getDisplayName();
            configureTemplateSetting.required = setting.isRequired();
            configureTemplateSetting.hidefromtemplate = setting.isHideFromTemplate();
            configureTemplateSetting.dependencyTarget = setting.getDependencyTarget();
            configureTemplateSetting.dependencyValue = setting.getDependencyValue();
            configureTemplateSetting.tooltip = setting.getToolTip();
            configureTemplateSetting.readOnly = setting.isReadOnly();
            configureTemplateSetting.group = setting.getGroup();
            configureTemplateSetting.useinfotooltip = setting.isInfoIcon();
            configureTemplateSetting.isOptionsSortable = setting.isOptionsSortable();
            configureTemplateSetting.multiple = setting.getType().equals(
                    ConfigureTemplateSetting.ConfigureTemplateSettingType.LIST);

            if (setting.getOptions() != null && setting.getOptions().size() > 0) {
                for (ConfigureTemplateOption value : setting.getOptions()) {
                    UIConfigureTemplateListItem item = new UIConfigureTemplateListItem();
                    item.id = value.getId();
                    item.name = value.getName();
                    item.dependencyTarget = value.getDependencyTarget();
                    item.dependencyValue = value.getDependencyValue();
                    configureTemplateSetting.options.add(item);
                }
            }
            if (ConfigureTemplateSetting.ConfigureTemplateSettingType.PASSWORD.equals(
                    setting.getType())) {
                if (setting.getValue() == null) {
                    configureTemplateSetting.value = "";
                } else if (ServiceTemplateSettingIDs.SERVICE_TEMPLATE_PASSWORD_DEFAULT_TO_REMOVE.equals(
                        setting.getValue())) {
                    configureTemplateSetting.value = "";
                } else {
                    configureTemplateSetting.value = setting.getValue();
                }
            } else {
                configureTemplateSetting.value = setting.getValue();
                if (configureTemplateSetting.value != null && configureTemplateSetting.value.startsWith(
                        ",") && configureTemplateSetting.value.length() > 1) {
                    configureTemplateSetting.value = configureTemplateSetting.value.substring(1);
                }
            }
        }
        return configureTemplateSetting;
    }

    private static void createUIConfigureTemplateComponents(
            Map<String, List<UIConfigureTemplateComponent>> componentAssociationsMap,
            ConfigureTemplateCategory associationCategory) {
        if (associationCategory != null) {
            for (ConfigureTemplateSetting setting : associationCategory.getParameters()) {
                List<UIConfigureTemplateComponent> componentList = componentAssociationsMap.computeIfAbsent(
                        setting.getId(), k -> new ArrayList<>());
                for (ConfigureTemplateOption option : setting.getOptions()) {
                    componentList.add(new UIConfigureTemplateComponent(
                            TemplateController.getDeviceTypeFromTypeName(option.getId()),
                            option.getName()));
                }
            }
        }
    }

    public static ConfigureTemplate createConfigureTemplate(final UITemplateBuilder template) {

        ConfigureTemplate configuration = null;
        if (template != null && template.configureTemplateConfiguration != null) {
            UIConfigureTemplateConfiguration configureTemplateConfiguration = template.configureTemplateConfiguration;

            ConfigureTemplate tempComponent = new ConfigureTemplate();
            tempComponent.setId(ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_ID);

            if (StringUtils.isNotBlank(configureTemplateConfiguration.osSettings.adminOSCredential)) {
                // Add OS Password Setting
                ConfigureTemplateCategory osCredentialsCategory = new ConfigureTemplateCategory();
                osCredentialsCategory.setId(
                        ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_OS_CREDENTIAL_RESOURCE);
                osCredentialsCategory.setDisplayName("OS Credential Settings");
                ConfigureTemplateSetting credentialSetting = new ConfigureTemplateSetting();
                credentialSetting.setId(
                        ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_OS_CREDENTIAL);
                credentialSetting.setDisplayName("OS Credential");
                credentialSetting.setValue(configureTemplateConfiguration.osSettings.adminOSCredential);
                osCredentialsCategory.getParameters().add(credentialSetting);
                if (configureTemplateConfiguration.osSettings.hyperconverged &&
                StringUtils.isNotBlank(configureTemplateConfiguration.osSettings.svmAdminOSCredential)) {
                    ConfigureTemplateSetting svmCredentialSetting = new ConfigureTemplateSetting();
                    svmCredentialSetting.setId(
                            ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SVM_OS_CREDENTIAL);
                    svmCredentialSetting.setDisplayName("SVM OS Credential");
                    svmCredentialSetting.setValue(configureTemplateConfiguration.osSettings.svmAdminOSCredential);
                    osCredentialsCategory.getParameters().add(svmCredentialSetting);
                }
                tempComponent.getCategories().add(osCredentialsCategory);
            }

            if (configureTemplateConfiguration.osSettings.osRepositories != null && configureTemplateConfiguration.osSettings.osRepositories.size() > 0) {
                // Add OS Settings
                createConfigureTemplateCategoryWithCategoryList(tempComponent,
                                                                configureTemplateConfiguration.osSettings.osRepositories,
                                                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_OS_RESOURCE,
                                                                "OS Settings");
            }

            if (configureTemplateConfiguration.networkSettings != null && configureTemplateConfiguration.networkSettings.size() > 0) {
                //Add netwrokSettings
                createNetworkConfigureTemplateCategory(tempComponent,
                                                       configureTemplateConfiguration.networkSettings,
                                                       ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_NETWORKING_RESOURCE
                );
            }

            if (configureTemplateConfiguration.serverPoolSettings != null && configureTemplateConfiguration.serverPoolSettings.size() > 0) {
                //Add serverPoolSettings
                createConfigureTemplateCategoryWithCategoryList(tempComponent,
                                                                configureTemplateConfiguration.serverPoolSettings,
                                                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_POOL_RESOURCE,
                                                                "Server Pool Settings");
            }

            if (configureTemplateConfiguration.serverSettings != null) {
                createServerSettingsConfigureTemplateCategory(tempComponent,
                                                              configureTemplateConfiguration.serverSettings,
                                                              ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_RESOURCE
                );
            }

            if (configureTemplateConfiguration.clusterSettings != null && configureTemplateConfiguration.clusterSettings.size() > 0) {
                //Add clusterSettings
                createConfigureTemplateCategoryWithCategoryList(tempComponent,
                                                                configureTemplateConfiguration.clusterSettings,
                                                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_CLUSTER_RESOURCE,
                                                                "Cluster Settings");
            }

            if (configureTemplateConfiguration.scaleIOSettings != null && configureTemplateConfiguration.scaleIOSettings.size() > 0) {
                //Add clusterSettings
                createConfigureTemplateCategoryWithCategoryList(tempComponent,
                                                                configureTemplateConfiguration.scaleIOSettings,
                                                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SCALEIO_RESOURCE,
                                                                "VxFlex OS Settings");
            }

            if (configureTemplateConfiguration.clusterDetailsSettings != null && configureTemplateConfiguration.clusterDetailsSettings.size() > 0) {
                //Add Cluster Detail settings
                createConfigureTemplateCategory(tempComponent,
                                                configureTemplateConfiguration.clusterDetailsSettings,
                                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_CLUSTER_DETAILS_RESOURCE,
                                                "Cluster Details Settings"
                );
            }

            if (configureTemplateConfiguration.vdsSettings != null && configureTemplateConfiguration.vdsSettings.size() > 0) {
                //Add VDS settings
                createConfigureTemplateCategory(tempComponent,
                                                configureTemplateConfiguration.vdsSettings,
                                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_VDS_SETTINGS_RESOURCE,
                                                "VDS Settings"
                );
            }

            if (configureTemplateConfiguration.storageSettings != null && configureTemplateConfiguration.storageSettings.size() > 0) {
                //Add storageSettings
                createStorageSettingCategories(tempComponent,
                                               configureTemplateConfiguration.storageSettings);
            }

            if (tempComponent.getCategories() != null && tempComponent.getCategories().size() > 0) {
                configuration = tempComponent;
            }
        }
        return configuration;
    }

    private static void createConfigureTemplateCategoryWithCategoryList(
            final ConfigureTemplate configuration,
            final List<UIConfigureTemplateCategory> categorySettings,
            final String categoryId,
            final String categoryName) {
        if (configuration != null) {
            if (categorySettings != null) {
                ConfigureTemplateCategory category = new ConfigureTemplateCategory();
                category.setId(categoryId);
                category.setDisplayName(categoryName);
                for (UIConfigureTemplateCategory categorySetting : categorySettings) {
                    ConfigureTemplateSetting setting = new ConfigureTemplateSetting();
                    setting.setId(categorySetting.id);
                    setting.setDisplayName(categorySetting.name);
                    setting.setValue(categorySetting.value);
                    for (UIListItem categoryOption : categorySetting.options) {
                        setting.getOptions().add(
                                new ConfigureTemplateOption(categoryOption.id, categoryOption.name,
                                                            null, null));
                    }
                    category.getParameters().add(setting);
                }
                configuration.getCategories().add(category);
            }
        }
    }

    private static void createConfigureTemplateCategory(final ConfigureTemplate configuration,
                                                        final List<UIConfigureTemplateSetting> categorySettings,
                                                        final String categoryId,
                                                        final String categoryName) {
        if (configuration != null) {
            if (categorySettings != null) {
                ConfigureTemplateCategory category = new ConfigureTemplateCategory();
                category.setId(categoryId);
                category.setDisplayName(categoryName);
                for (UIConfigureTemplateSetting categorySetting : categorySettings) {
                    category.getParameters().add(createConfigureTemplateSetting(categorySetting
                    ));
                }
                configuration.getCategories().add(category);
            }
        }
    }

    private static ConfigureTemplateSetting createConfigureTemplateSetting(
            final UIConfigureTemplateSetting set) {
        ConfigureTemplateSetting res = new ConfigureTemplateSetting();
        res.setId(set.id);
        res.setDisplayName(set.name);
        res.setType(getTemplateSettingDataType(set.datatype.toUpperCase()));
        res.setDependencyTarget(set.dependencyTarget);
        res.setDependencyValue(set.dependencyValue);
        res.setHideFromTemplate(set.hidefromtemplate);
        res.setRequired(set.required);
        res.setMax(set.max);
        res.setMin(set.min);
        res.setToolTip(set.tooltip);
        res.setReadOnly(set.readOnly);
        res.setGroup(set.group);
        res.setInfoIcon(set.useinfotooltip);
        res.setMaxLength(set.maxlength);
        res.setStep(set.step);
        res.setOptionsSortable(set.isOptionsSortable);


        // strip leading coma if present. UI artifact.
        if (ConfigureTemplateSetting.ConfigureTemplateSettingType.PASSWORD.equals(res.getType()) &&
                UICredential.CURRENT_PASSWORD.equals(set.value)) {
            res.setValue(null);
        } else if (set.value != null && set.value.startsWith(",") && set.value.length() > 1)
            res.setValue(set.value.substring(1));
        else
            res.setValue(set.value);

        if (set.options != null && set.options.size() > 0) {
            for (UIConfigureTemplateListItem option : set.options) {
                res.getOptions().add(new ConfigureTemplateOption(option.id,
                                                                 option.name,
                                                                 option.dependencyTarget,
                                                                 option.dependencyValue));
            }
        }

        return res;
    }

    private static void createNetworkConfigureTemplateCategory(
            final ConfigureTemplate configuration,
            final List<UINetworkSettings> categorySettings,
            final String categoryId) {
        if (configuration != null) {
            if (categorySettings != null) {
                ConfigureTemplateCategory category = new ConfigureTemplateCategory();
                category.setId(categoryId);
                category.setDisplayName("Network Settings");
                for (UINetworkSettings categorySetting : categorySettings) {
                    ConfigureTemplateSetting setting = new ConfigureTemplateSetting();
                    setting.setId(categorySetting.id);
                    setting.setDisplayName(categorySetting.name);
                    setting.setValue(categorySetting.value);
                    if (categorySetting.network != null) {
                        Network network = new Network();
                        network.setId(categorySetting.id);
                        NetworkType type = NetworkType.fromValue(categorySetting.network);
                        network.setType(type);
                        setting.getNetworks().add(network);
                    }
                    for (UIListItem categoryOption : categorySetting.options) {
                        setting.getOptions().add(
                                new ConfigureTemplateOption(categoryOption.id, categoryOption.name,
                                                            null, null));
                    }
                    category.getParameters().add(setting);
                }
                configuration.getCategories().add(category);
            }
        }
    }

    private static void createServerSettingsConfigureTemplateCategory(
            final ConfigureTemplate configuration,
            final UIServerSettings serverSettings,
            final String categoryId) {
        if (configuration != null) {
            if (serverSettings != null) {
                ConfigureTemplateCategory category = new ConfigureTemplateCategory();
                category.setId(categoryId);
                category.setDisplayName("Server Settings");

                ConfigureTemplateSetting numberOfSetting = new ConfigureTemplateSetting();
                numberOfSetting.setId(
                        ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_NUMBER_OF_ID);
                numberOfSetting.setDisplayName("Number of Servers / Hosts");
                numberOfSetting.setValue(serverSettings.numServers);
                for (UIListItem item : serverSettings.numServersList) {
                    numberOfSetting.getOptions().add(
                            new ConfigureTemplateOption(item.id, item.name));
                }
                category.getParameters().add(numberOfSetting);

                ConfigureTemplateSetting serverPoolSetting = new ConfigureTemplateSetting();
                serverPoolSetting.setId(
                        ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_SERVER_POOL_ID);
                serverPoolSetting.setDisplayName(
                        "Which Server Pool should VxFM use\nto pull servers?");
                serverPoolSetting.setValue(serverSettings.serverPoolId);
                for (UIListItem item : serverSettings.serverPoolOptions) {
                    serverPoolSetting.getOptions().add(
                            new ConfigureTemplateOption(item.id, item.name));
                }
                category.getParameters().add(serverPoolSetting);

                ConfigureTemplateSetting osImageSetting = new ConfigureTemplateSetting();
                osImageSetting.setId(
                        ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_OS_IMAGE_ID);
                osImageSetting.setValue(serverSettings.existingOS);
                for (UIListItem item : serverSettings.osList) {
                    osImageSetting.getOptions().add(
                            new ConfigureTemplateOption(item.id, item.name));
                }
                category.getParameters().add(osImageSetting);

                ConfigureTemplateSetting recommendedESXISetting = new ConfigureTemplateSetting();
                recommendedESXISetting.setId(
                        ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_RECOMMENDED_ESXI_ID);
                recommendedESXISetting.setDisplayName(
                        "ASM recommends " + serverSettings.recommended);
                recommendedESXISetting.setValue(serverSettings.recommended);
                category.getParameters().add(recommendedESXISetting);

                ConfigureTemplateSetting serverNamePrefixSetting = new ConfigureTemplateSetting();
                serverNamePrefixSetting.setId(
                        ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_NAME_PREFIX_ID);
                serverNamePrefixSetting.setDisplayName("Server Name Template Prefix");
                serverNamePrefixSetting.setValue(serverSettings.serverName);
                category.getParameters().add(serverNamePrefixSetting);

                ConfigureTemplateSetting serverNameSuffixSetting = new ConfigureTemplateSetting();
                serverNameSuffixSetting.setId(
                        ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_SERVER_SETTINGS_NAME_SUFFIX_ID);
                serverNameSuffixSetting.setDisplayName("Server Name Template Suffix");
                serverNameSuffixSetting.setValue(serverSettings.serverNameSuffix);
                for (UIListItem item : serverSettings.serverFormatOptions) {
                    serverNameSuffixSetting.getOptions().add(
                            new ConfigureTemplateOption(item.id, item.name));
                }
                category.getParameters().add(serverNameSuffixSetting);

                ConfigureTemplateSetting osCredentialSetting = new ConfigureTemplateSetting();
                osCredentialSetting.setId(ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_OS_CREDENTIAL);
                osCredentialSetting.setType(
                        ConfigureTemplateSetting.ConfigureTemplateSettingType.PASSWORD);
                osCredentialSetting.setValue(serverSettings.osCredential);
                category.getParameters().add(osCredentialSetting);

                configuration.getCategories().add(category);
            }
        }
    }

    private static void createStorageSettingCategories(final ConfigureTemplate configuration,
                                                       final List<UIStorageSettings> categorySettings) {
        if (configuration != null) {
            if (categorySettings != null && categorySettings.size() > 0) {
                ConfigureTemplateCategory category = new ConfigureTemplateCategory();
                category.setId(ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_RESOURCE);
                category.setDisplayName("Storage Settings");
                for (UIStorageSettings setting : categorySettings) {
                    if (setting.storageVolumeUnits != null && setting.storageVolumeUnits.size() > 0 ||
                            setting.storageArrays != null && setting.storageArrays.size() > 0 ||
                            setting.numVolumesList != null && setting.numVolumesList.size() > 0) {
                        category.setId(setting.id);
                        category.setDisplayName(setting.name);
                        if (setting.type != null) {
                            DeviceType deviceType = DeviceType.valueOf(setting.type);
                            category.setDeviceType(deviceType.name());
                        }
                        ConfigureTemplateSetting storageArrayId = new ConfigureTemplateSetting();
                        storageArrayId.setId(
                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_STORAGE_ARRAY_ID);
                        storageArrayId.setDisplayName(
                                "Which of your storage arrays do you want to use?");
                        storageArrayId.setValue(setting.storageArrayId);
                        for (UIListItem item : setting.storageArrays) {
                            storageArrayId.getOptions().add(
                                    new ConfigureTemplateOption(item.id, item.name));
                        }
                        category.getParameters().add(storageArrayId);

                        ConfigureTemplateSetting storeagePoolSetiing = new ConfigureTemplateSetting();
                        storeagePoolSetiing.setId(
                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_STORAGE_POOL_ID);
                        storeagePoolSetiing.setDisplayName(
                                "Which of your storage pools do you want to use?");
                        storeagePoolSetiing.setType(
                                ConfigureTemplateSetting.ConfigureTemplateSettingType.ENUMERATED);
                        storeagePoolSetiing.setValue(setting.storagePoolId);
                        for (UIConfigureTemplateListItem item : setting.storagePools) {
                            storeagePoolSetiing.getOptions().add(
                                    new ConfigureTemplateOption(item.id, item.name,
                                                                item.dependencyTarget,
                                                                item.dependencyValue));
                        }
                        category.getParameters().add(storeagePoolSetiing);

                        ConfigureTemplateSetting numberOfSetting = new ConfigureTemplateSetting();
                        numberOfSetting.setId(
                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_VOLUME_COUNT_ID);
                        numberOfSetting.setDisplayName("Storage Volumes to be Created");
                        numberOfSetting.setValue(setting.numStorageVolumes);
                        for (UIListItem item : setting.numVolumesList) {
                            numberOfSetting.getOptions().add(
                                    new ConfigureTemplateOption(item.id, item.name));
                        }
                        category.getParameters().add(numberOfSetting);

                        ConfigureTemplateSetting storageSizePrefixSetting = new ConfigureTemplateSetting();
                        storageSizePrefixSetting.setId(
                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_VOLUME_SIZE_ID);
                        storageSizePrefixSetting.setDisplayName("Size of Storage Volumes");
                        storageSizePrefixSetting.setValue(setting.storageVolumeSize.toString());
                        category.getParameters().add(storageSizePrefixSetting);

                        ConfigureTemplateSetting storageSizeSuffixSetting = new ConfigureTemplateSetting();
                        storageSizeSuffixSetting.setId(
                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_VOLUME_SIZE_MEASURE_ID);
                        storageSizeSuffixSetting.setValue(setting.storageVolumeUnit);
                        for (UIListItem item : setting.storageVolumeUnits) {
                            storageSizeSuffixSetting.getOptions().add(
                                    new ConfigureTemplateOption(item.id, item.name));
                        }
                        category.getParameters().add(storageSizeSuffixSetting);

                        ConfigureTemplateSetting volumeNameTemplateSetting = new ConfigureTemplateSetting();
                        volumeNameTemplateSetting.setId(
                                ConfigureTemplateSettingIDs.CONFIGURE_TEMPLATE_STORAGE_DETAILS_VOLUME_NAME_TEMPLATE_ID);
                        volumeNameTemplateSetting.setDisplayName("Volume Name Template");
                        volumeNameTemplateSetting.setValue(setting.volumeName);
                        category.getParameters().add(volumeNameTemplateSetting);
                    } else {
                        ConfigureTemplateSetting storageSetting = new ConfigureTemplateSetting();
                        storageSetting.setId(setting.id);
                        storageSetting.setDisplayName(setting.name);
                        storageSetting.setValue(setting.value);
                        for (UIListItem categoryOption : setting.options) {
                            storageSetting.getOptions().add(
                                    new ConfigureTemplateOption(categoryOption.id,
                                                                categoryOption.name, null, null));
                        }
                        category.getParameters().add(storageSetting);
                    }
                }
                configuration.getCategories().add(category);
            }
        }
    }

    @RequestMapping(value = "uploadconfigurabletemplate", method = RequestMethod.POST)
    public JobResponse uploadConfigurableTemplate(MultipartHttpServletRequest request) {
        log.debug("uploadConfigurableTemplate - Called by UI");
        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();

        try {
            ServiceTemplateUploadRequest uRequest = new ServiceTemplateUploadRequest();

            uRequest.setEncryptionPassword(request.getParameter("encryptionPassword"));

            MultipartFile file = request.getFile("file");
            if (file != null) {
                uRequest.setFileData(file.getBytes());
            }

            ServiceTemplate template = getConfigureTemplateServiceAdapter().uploadTemplate(
                    uRequest);
            if (template != null) {
                jobResponse.responseObj = TemplateController.parseTemplateBuilder(template,
                                                                                  getApplicationContext()
                                                                                 );
            } else {
                throw new ControllerException("uploadConfigurableTemplate() failed");
            }

        } catch (Throwable t) {
            log.error("uploadConfigurableTemplate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "saveconfiguretemplate", method = RequestMethod.POST)
    public JobResponse saveConfigureTemplate(@RequestBody JobSaveTemplateRequest request) {

        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();

        try {
            if (request.requestObj != null) {
                ServiceTemplate serviceTemplate = TemplateController.createTemplate(request.requestObj,
                                                                                    getFirmwareRepositoryServiceAdapter());
                if (serviceTemplate.getId() == null) {
                    serviceTemplate = getConfigureTemplateServiceAdapter().createTemplate(
                            serviceTemplate);
                } else {
                    getTemplateServiceAdapter().updateTemplate(serviceTemplate.getId(),
                                                               serviceTemplate);
                    // retrieve the service template again to get updated passwords.
                    serviceTemplate = getTemplateServiceAdapter().getTemplate(
                            serviceTemplate.getId());
                }
                jobResponse.responseObj = TemplateController.parseTemplateBuilder(serviceTemplate,
                                                                                  getApplicationContext(),
                                                                                  false);
            }
        } catch (Throwable t) {
            log.error("saveConfigureTemplate() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "getconfiguretemplatebyid", method = RequestMethod.POST)
    public JobResponse getConfigureTemplateById(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();
        Locale locale = LocaleContextHolder.getLocale();

        try {
            ServiceTemplate sTemplate = templateServiceAdapter.getTemplate(request.requestObj.id);
            jobResponse.responseObj = TemplateController.parseTemplateBuilder(sTemplate,
                                                                              getApplicationContext(),
                                                                              false);
        } catch (Throwable t) {
            log.error("getConfigureTemplateById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    public static String getUITemplateSettingDataType(ConfigureTemplateSetting.ConfigureTemplateSettingType type) {
        String value = "";
        if (type != null) {
            switch (type) {
            case TEXT:
                value = "textarea";
                break;
            case ENUMERATED:
                value = "enum";
                break;
            case INTEGER:
                value = "number";
                break;
            default:
                value = type.name().toLowerCase();
                break;
            }
        }
        return value;
    }

    public static ConfigureTemplateSetting.ConfigureTemplateSettingType getTemplateSettingDataType(final String type) {
        ConfigureTemplateSetting.ConfigureTemplateSettingType value = null;
        if (type != null) {
            switch (type) {
            case "textarea":
                value = ConfigureTemplateSetting.ConfigureTemplateSettingType.TEXT;
                break;
            case "enum":
                value = ConfigureTemplateSetting.ConfigureTemplateSettingType.ENUMERATED;
                break;
            case "number":
                value = ConfigureTemplateSetting.ConfigureTemplateSettingType.INTEGER;
                break;
            default:
                value = ConfigureTemplateSetting.ConfigureTemplateSettingType.valueOf(type.toUpperCase());
                break;
            }
        }
        return value;
    }

    public ConfigureTemplateServiceAdapter getConfigureTemplateServiceAdapter() {
        return configureTemplateServiceAdapter;
    }

    public void setConfigureTemplateServiceAdapter(
            ConfigureTemplateServiceAdapter configureTemplateServiceAdapter) {
        this.configureTemplateServiceAdapter = configureTemplateServiceAdapter;
    }

    public TemplateServiceAdapter getTemplateServiceAdapter() {
        return templateServiceAdapter;
    }

    public void setTemplateServiceAdapter(TemplateServiceAdapter templateServiceAdapter) {
        this.templateServiceAdapter = templateServiceAdapter;
    }

    public FirmwareRepositoryServiceAdapter getFirmwareRepositoryServiceAdapter() {
        return firmwareRepositoryServiceAdapter;
    }

    public void setFirmwareRepositoryServiceAdapter(
            FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter) {
        this.firmwareRepositoryServiceAdapter = firmwareRepositoryServiceAdapter;
    }
}
