angular.module('ASM.dataservices')
    .factory('Commands', function () {

        _.templateSettings = {
            interpolate: /\{(.+?)\}/g
        };

        var server = ''; //contains mock data

        //var server = 'https://172.31.59.168/'; //VM environment but has servers and some mock data in database
        //var server = 'https://172.31.62.233/'; //real environment, made by Hepfer
        //var server = 'https://172.31.59.190/'; //environment with jobs
        //var server = 'https://172.31.62.134/'; //environment with storage
        //var server = 'https://172.31.62.250/'; //Barsana's test environment (no fabric)
        //var server = 'https://172.31.62.249/'; //Barsana's test environment (has fabric)

        return {

            data: {
                about: {
                    getAboutData: 'about/getaboutdata'
                },
                svg: {
                    getData: 'topology/getTopologyData',
                    getTemplateData: 'topology/gettemplatetopology',
                    getServiceData: 'topology/getservicetopology',
                    chassissvg: 'images/svg/chassis.svg',
                    networksvg: 'images/svg/network.svg',
                    bladesvg: 'images/svg/blade.svg',
                    bladeexpandedsvg: 'images/svg/blade_expanded.svg',
                    iomsvg: 'images/svg/io_module.svg',
                    clustersvg: 'images/svg/cluster.svg',
                    hostsvg: 'images/svg/resource.svg',
                    vmsvg: 'images/svg/vm.svg',
                    canvassvg: 'images/svg/TempBuilder-canvas.svg',
                    componentsvg: 'images/svg/TempBuilder-components.svg',
                    networkiconsvg: 'images/svg/TB-network-barnacle.svg',
                    addiconsvg: 'images/svg/TB-addicon.svg',
                    serverPortView: 'images/svg/serverportview.svg',
                    applicationtextsvg: 'images/svg/applicationText.svg'
                },
                devices: {
                    getFirmwareById: 'devices/getfirmwarebyid',
                    saveFirmware: 'devices/savefirmware',
                    getComplianceCheckById: 'devices/getcompliancecheckbyid',
                    getFirmwareDeviceList: 'devices/getfirmwaredevicelist',
                    exportFirmware: 'devices/exportfirmware',
                    getDeviceList: 'devices/getdevicelist',
                    getAvailableCloneDeviceList: 'devices/getavailableclonedevicelist',
                    getVCenter: 'devices/getvcenter',
                    getSCVMM: 'devices/getscvmm',
                    getDeviceById: 'devices/getdevicebyid',
                    remove: 'devices/remove',
                    manage: 'devices/manage',
                    unmanage: 'devices/unmanage',
                    reserve: 'devices/reserve',			
                    powerOn: 'devices/poweron',
                    powerOff: 'devices/poweroff',
                    runInventory: 'devices/runinventory',
                    estimateFirmwareUpdate: 'devices/estimatefirmwareupdate',
                    updatedevicefirmware: 'devices/updatedevicefirmware',
                    getDeviceConfiguration: 'devices/getdeviceconfiguration',
                    configureDevice: 'devices/configuredevice',
                    setServiceMode: 'devices/servicemode'
                },
                equalLogicStorage: {
                    getEqualLogicStorageById: 'devices/getequallogicstoragebyid'
                },
                compellentStorage: {
                    getCompellentStorageById: 'devices/getcompellentstoragebyid'
                },
                emcStorage: {
                    getEmcUnityStorageById: 'devices/getemcunitystoragebyid',
                    getEmcvnxStorageById: 'devices/getemcvnxstoragebyid'
                },
                netappStorage: {
                    getNetAppStorageById: 'devices/getnetappstoragebyid'
                },
                scaleIO: {
                    getScaleIObyId: 'devices/getscaleiobyid',
                    getScaleIOWorkload: 'devices/getscaleioworkload',
                },
                dellSwitch: {
                    getDellSwitchById: 'devices/getdellswitchbyid'
                },
                ciscoSwitch: {
                    getCiscoSwitchById: 'devices/getciscoswitchbyid'
                },
                serverpools: {
                    getServerPools: 'serverpool/getserverpools',
                    getServerPoolById: 'serverpool/getserverpoolbyid',
                    saveServerPool: 'serverpool/saveserverpool',
                    remove: 'serverpool/remove'
                },
                firmwarepackages: {
                    getFirmwareBundleDevices: 'firmware/getfirmwarebundledevices',
                    getFirmwarePackages: 'firmware/getfirmwarepackages',
                    getAvailableFirmwarePackages: 'firmware/getavailablefirmwarepackages',
                    getFirmwarePackageById: 'firmware/getfirmwarepackagebyid',
                    getFirmwarePackageNameById: 'firmware/getfirmwarepackagenamebyid',
                    getFirmwareBundleById: 'firmware/getfirmwarebundlebyid',
                    getDefaultFirmwarePackage: 'firmware/getdefaultfirmwarepackage',
                    saveFirmwarePackage: 'firmware/savefirmwarepackage',
                    testFirmwarePackage: 'firmware/testfirmwarepackage',
                    setDefaultFirmwarePackage: 'firmware/setdefaultfirmwarepackage',
                    remove: 'firmware/remove',
                    removeFirmwareBundle: 'firmware/removefirmwarebundle',
                    saveFirmwareBundle: 'firmware/savefirmwarebundle',
                    uploadFirmwarePackage: 'firmware/uploadfirmwarepackage',
                    getAvailableRCMs: 'firmware/getavailablercms'
                },
                addonmodules: {
                    getAddOnModules: 'addonmodule/getaddonmodules',
                    getAddOnModuleById: 'addonmodule/getaddonmodulebyid',
                    removeAddOnModule: 'addonmodule/removeaddonmodule',
                    saveAddOnModule: 'addonmodule/saveaddonmodule',
                    testAddOnModule: 'addonmodule/testaddonmodule',
                    uploadAddOnModule: 'addonmodule/uploadaddonmodule'
                },
                applianceManagement: {
                    getStatus: 'appliance/getstatus',
                    getCurrentUsersAndJobs: 'appliance/getcurrentusersandjobs',
                    setApplianceUpdate: 'appliance/setapplianceupdate',
                    submitUpdateVirtualAppliance: 'appliance/updatevirtualappliance',
                    submitLicenseUpdateForm: 'appliance/updatelicense',
                    verifylicense: 'appliance/verifylicense',
                    submitNtpSettingsForm: 'appliance/saventpsettings',
                    submitProxyInfoForm: 'appliance/setproxy',
                    submitDhcpSettingsForm: 'initialsetup/updatedhcp',
                    testProxySettings: 'appliance/testproxysettings',
                    submitCertificateSigReqForm: 'appliance/setappliancecertificateinfo',
                    getAvailableCountries: 'appliance/getavailablecountries',
                    uploadCertificate: 'appliance/uploadcertificate',
                    uploadCertificateConfirmation: 'appliance/uploadcertificateconfirmation',
                    updateServiceTag: 'appliance/updateservicetag',
                    getIpVerifyPorts: 'appliance/getipverifyports',
                    updateIpVerifyPorts: 'appliance/updateipverifyports',
                    configureServersForAlertConnector: 'appliance/configureserversforalertconnector',
                    getConfigureServerStatus: 'appliance/getconfigureserverstatus',
                    exportTroubleshootingBundle: 'appliance/exporttroubleshootingbundle',
                    testTroubleshootingBundle: 'appliance/testtroubleshootingbundle',
                    addCommunityString: 'appliance/addcommunitystring',
                    deleteCommunityString: 'appliance/deletecommunitystring',
                    addForwardingDetails: 'appliance/addforwardingdetails',
                    deleteForwardingDetails: 'appliance/deleteforwardingdetails',
                    getApplianceUpdateInfo: 'appliance/getapplianceupdateinfo',
                    getLicenseData: 'appliance/getlicensedata',
                    getGeneralSettings: 'appliance/getnetworkinfo',
                    getHttpProxySettings: 'appliance/getproxy',
                    getDhcpSettings: 'appliance/getdhcpsettings',
                    getVxRackSettings: 'appliance/getvxrackflexalertconnectorsettings',
                    setVxRackSettingsRegister: 'appliance/setvxrackflexalertconnectorsettings/register',
                    setVxRackSettingsSuspend: 'appliance/setvxrackflexalertconnectorsettings/suspend',
                    getNtpSettings: 'appliance/getntpsettings',
                    getCertificateInfo: 'appliance/getappliancecertificateinfo',
                    deregisterVxRack: 'appliance/deregistervxrack',
                    setApplianceUpgrade: 'appliance/setapplianceupgradesettings',
                    getApplianceUpgrade: 'appliance/getapplianceupgradesettings'
                },
                backupAndRestore: {
                    backupNow: 'backupandrestore/backupnow',
                    restore: 'backupandrestore/restore',
                    getRestoreStatus: 'backupandrestore/getrestorestatus',
                    getBackupSettings: 'backupandrestore/getbackupsettings',
                    getBackupScheduleInfo: 'backupandrestore/getbackupschedule',
                    saveBackupSettings: 'backupandrestore/savebackupsettings',
                    setBackupScheduleInfo: 'backupandrestore/setbackupschedule',
                    testBackupConnection: 'backupandrestore/testbackupconnection',
                    testRestoreConnection: 'backupandrestore/testrestoreconnection'
                },

                downloads: {
                    create: 'downloads/create',
                    status: 'downloads/status'
                },

                logs: {
                    getLogs: 'logs/getlogs',
                    purgeLogs: 'logs/purgelogs',
                    exportAllLogs: 'logs/exportalllogs',
                    exportFilteredLogs: 'logs/exportfilteredlogs'
                },

                chassis: {
                    getChassisSummariesByCredentialId: 'chassis/getchassissummariesbycredentialid',
                    getManagementTemplatesByCredentialId: 'chassis/getmanagementtemplatesbycredentialid',
                    getChassisById: 'chassis/getchassisbyid',
                    runInventory: 'chassis/runinventory',
                    //Where is configure and reapply template?
                    checkCompliance: 'chassis/checkcompliance',
                    removeChassis: 'chassis/removechassis',
                    resetCertificate: 'chassis/resetcertificate',

                },
                configureChassis: {
                    getChassisSetup: 'chassis/getchassissetupbychassisid',
                    getTemplates: 'managementtemplate/getreadymanagementtemplatesummaries',
                    getFabricPurposes: 'managementtemplate/getfabricpurposes',
                    getIdentityData: 'chassis/getidentitydata',
                    validateChassisIdentities: 'chassis/validatechassisidentities',
                    validateServerIdentities: 'chassis/validateserveridentities',
                    validateIOMIdentities: 'chassis/validateiomidentities',
                    saveConfigureChassis: 'chassis/saveconfigurechassis',
                    getConfigurableResources: 'configurechassis/getconfigurableresources',
                    uploadPortConfiguration: 'configurechassis/uploadportconfiguration',
                    configureResources: 'configurechassis/configureresources'
                },
                repository: {
                    getRepositoryList: 'repository/getrepositorylist',
                    getRepositoryById: 'repository/getrepositorybyid',
                    syncRepositoryById: 'repository/syncrepositorybyid',
                    syncRepository: 'repository/syncrepository',
                    saveRepository: 'repository/saverepository',
                    testRepository: 'repository/testrepository',
                    testFileRepository: 'repository/testfilerepository',
                    deleteRepository: 'repository/deleterepository',
                    deleteRepositoryList: 'repository/deleterepositorylist',
                    getRepositoryTypes: 'repository/getrepositorytypes'
                },
                serverTemplate: {
                    getDefaultTemplate: 'deploymenttemplate/getdefaulttemplate',
                    getServerTemplate: 'deploymenttemplate/getdeploymenttemplatebyid',
                    createServerTemplate: 'deploymenttemplate/createdeploymenttemplate',
                    saveServerTemplate: 'deploymenttemplate/savedeploymenttemplate',
                    getSystemProfiles: 'deploymenttemplate/getsystemprofiles',
                    getBootSequenceByBootType: 'deploymenttemplate/getbootsequencebyboottype'
                },
                serviceTemplate: {
                    getDefaultTemplate: 'servicetemplate/getdefaulttemplate',
                    getServiceTemplate: 'servicetemplate/getservicetemplatebyid',
                    createServiceTemplate: 'servicetemplate/createservicetemplate',
                    saveServiceTemplate: 'servicetemplate/saveservicetemplate',
                    getSystemProfiles: 'servicetemplate/getsystemprofiles',
                    getBootSequenceByBootType: 'servicetemplate/getbootsequencebyboottype'
                },
                configurationTemplate: {
                    getConfigurationTemplate: 'managementtemplate/getmanagementtemplate',
                    createConfigurationTemplate: 'managementtemplate/createmanagementtemplate',
                    saveConfigurationTemplate: 'managementtemplate/savemanagementtemplate',
                    getUserRoles: 'managementtemplate/getmanagementtemplateuserroles',

                    addTemplateUser: 'managementtemplate/addtemplateuser',
                    removeTemplateUser: 'managementtemplate/removetemplateuser',
                    updateTemplateUser: 'managementtemplate/updatetemplateuser',
                    addCMCTemplateUser: 'managementtemplate/addcmctemplateuser',
                    removeCMCTemplateUser: 'managementtemplate/removecmctemplateuser',
                    updateCMCTemplateUser: 'managementtemplate/updatecmctemplateuser'
                },
                credential: {
                    getCredentialList: 'credentials/getcredentiallist',
                    getCredentialSummaryList: 'credentials/getcredentialsummaries',
                    getCredentialByType: 'credentials/getcredentialsbytype',
                    //getCredentialDevices: 'credentials/getcredentialdevicesbyidandtype',
                    getCredentialById: 'credentials/getcredentialbyid',
                    saveCredential: 'credentials/savecredential',
                    deleteCredential: 'credentials/deletecredential'
                },
                dashboard: {
                    getDashboardLandingPageData: 'dashboard/getdashboardlandingpagedata',
                    getDashboardNotifications: 'dashboard/getdashboardnotifications',
                    getServicesDashboardData: 'dashboard/getservicesdashboarddata',
                    getDashboardStorageData: 'dashboard/getdashboardstoragedata',
                    getDashboardScaleIOData: 'dashboard/getdashboardscaleiodata',
                },
                deployment: {
                    attach: 'deployments/attach',
                    detach: 'deployments/detach',
                    migrate: 'deployments/migrate',
                    powerOn: 'deployments/poweron',
                    powerOff: 'deployments/poweroff',
                    getDeploymentSummaries: 'deployments/getdeploymentsummaries',
                    getDeploymentById: 'deployments/getdeploymentbyid',
                    deleteDeployment: 'deployments/deletedeployment',
                    getServerListByTemplateId: 'deployments/getserverlistbytemplateid'
                },
                deviceGroup: {
                    createDeviceGroup: 'discovery/createdevicegroup',
                    getDeviceGroupList: 'discovery/getdevicegrouplist'
                },
                discovery: {
                    getChassisConfigurations: 'discovery/getchassisconfigurations',
                    getChassisList: 'discovery/getchassislist',
                    getRackList: 'discovery/getracklist',
                    saveChassisDiscovery: 'discovery/savechassisdiscovery',
                    submitDiscovery: 'discovery/submitdiscovery',
                    verifyConfiguration: 'discovery/verifyconfiguration',
                    getChassisConfigurationStatus: 'discovery/getchassisconfigurationsstatus',
                    getChassisListStatus: 'discovery/getchassisliststatus',
                    getRackListStatus: 'discovery/getrackliststatus',
                },
                environment: {
                    getMonitoringSettings: 'environment/getmonitoringsettings',
                    setMonitoringSettings: 'environment/setmonitoringsettings',
                    getNtpTimeZoneSettings: 'environment/getntptimezonesettings',
                    getTimeZones: 'environment/gettimezones',
                    setNtpTimeZoneSettings: 'environment/setntptimezonesettings'
                },
                hardwareInventory: {
                    getHardwareInventoryLandingPage: 'hardwareinventory/gethardwareinventorylandingpagedata'
                },
                initialSetup: {
                    getInitialSetup: 'initialsetup/getinitialsetupdata',
                    completeInitialSetup: 'initialsetup/completeinitialsetup',
                    updateTimeData: 'initialsetup/updatetime',
                    updateDhcpData: 'initialsetup/updatedhcp',
                    updateLicenseData: 'appliance/updatelicense',
                    updateProxyData: 'initialsetup/updateproxy',
                    testProxy: 'initialsetup/testproxysettings',
                    gettingStarted: 'initialsetup/gettingstarted',
                    updateGettingStarted: 'initialsetup/updategettingstarted',
                },
                iom: {
                    getIOMSummariesByCredentialId: 'iom/getiomsummariesbycredentialid',
                    getManagementTemplatesByCredentialId: 'iom/getmanagementtemplatesbycredentialid',
                    getIOMById: 'iom/getiombyid',
                    runInventory: 'iom/runinventory',
                    remove: 'iom/remove',
                    //Where is reApplyTemplate?
                    checkCompliance: 'iom/checkcompliance',
                },
                jobs: {
                    getJobList: 'jobs/getjoblist',
                    getJobById: 'jobs/getjobbyid',
                    saveJobSchedule: 'jobs/savejobschedule',
                    getRecurrences: 'jobs/getrecurrences',
                    deleteJob: 'jobs/deletejob',
                    getSelectedJobId: 'jobs/getselectedjobid',
                    exportjobs: 'jobs/exportjobs'
                },
                managementTemplate: {
                    reApplyTemplate: 'managementtemplate/reapplytemplate'
                },
                pollingIntervals: {
                    getChassisInventoryPollingSettings: 'pollingintervals/getchassisinventorypollingsettings',
                    saveChassisInventoryPollingSettings: 'pollingintervals/savechassisinventorypollingsettings',

                    getStatusPollingSettings: 'pollingintervals/getstatuspollingsettings',
                    saveStatusPollingSettings: 'pollingintervals/savestatuspollingsettings'
                },

                servers: {
                    getServerSummariesByCredentialId: 'servers/getserversummariesbycredentialid',
                    getManagementTemplatesByCredentialId: 'servers/getmanagementtemplatesbycredentialid',
                    getServerById: 'servers/getserverbyid',
                    powerOn: 'servers/poweron',
                    powerOff: 'servers/poweroff',
                    runInventory: 'servers/runinventory',
                    checkCompliance: 'servers/checkcompliance',
                    resetCertificate: 'servers/resetcertificate',
                    remove: 'servers/remove',
                    resetPeakValues: 'servers/resetpeakvalues',
                    getServerPortViewById: 'servers/getserverportviewbyid',
                },

                serverTemplateModels: {
                    getServerTemplateModelList: 'servertemplatemodels/getservertemplatemodellist'
                },

                session: {
                    doLogin: 'login',
                    doLogout: 'login/dologout',
                    getSession: 'session/getsession',
                    updateSession: 'session/updatesession'
                },

                pools: {
                    getPools: 'pools/getpools',
                    getPoolById: 'pools/getpoolbyid',
                    deletePools: 'pools/deletepools',
                    validatePool: 'pools/validatepool',
                    createPool: 'pools/createpool',
                    updatePool: 'pools/updatepool',
                    getPoolPrefixList: 'pools/getpoolprefixlist',
                    exportPools: 'pools/exportpools'
                },

                services: {
                    getAdjustServiceComponents: 'services/getAdjustServiceComponents',
                    getServiceById: 'services/getservicebyid',
                    getServiceSettingsById: 'services/getservicesettingsbyid',
                    getUpdatableServiceSettingsById: 'services/getupdatableservicesettingsbyid',
                    getResourcesWithNetworksById: 'services/getresourceswithnetworksbyid',
                    adjustService: 'services/adjustservice',
                    updateComponents: 'services/updatecomponents',
                    getMigrateServersByServiceId: 'services/getmigrateserversbyserviceid',
                    getServiceList: 'services/getservicelist',
                    getServiceDropdown: 'services/getservicedropdown',
                    createService: 'services/createservice',
                    updateService: 'services/updateservice',
                    deleteService: 'services/deleteservice',
                    removeService: 'services/removeservice',
                    cancelService: 'services/cancelservice',
                    deleteResources: 'services/deleteresources',
                    retryService: 'services/retryservice',
                    previewService: 'services/previewservice',
                    getComponentData: 'services/getcomponentdata',
                    migrate: 'services/migrate',
                    updateservicefirmware: 'services/updateservicefirmware',
                    estimateFirmwareUpdate: 'services/estimatefirmwareupdate',
                    exportService: 'services/exportservice',
                    addNetworkToService: 'services/addnetworktoservice',
                    updateServiceNetworkResources: 'services/updateservicenetworkresources',
                    getPuppetLogs: 'services/getpuppetlogs',
                    exportPuppetLogs: 'services/exportpuppetlogs',
                    getExistingInfo: 'services/getexistinginfo',
                    getExistingService: 'services/getexistingservice',
                    addExistingService: 'services/addexistingservice',
                    updateServiceInventory: 'services/updateserviceinventory',
                    updateExistingService: 'services/updateexistingservice',
                    stopManagingApplications: 'services/stopmanagingapplications',
                    getExistingServiceComponent: 'services/getexistingservicecomponent',
                    getExistingServiceOSCredentials: 'services/getexistingserviceoscredentials',
                    getExistingSOServiceOSCredentials: 'services/getexistingsoserviceoscredentials',
                    getExistingServiceSwitches: 'services/getexistingservicevswitches',
                    getExistingServiceNetworks: 'services/getexistingservicenetworks'
                },

                templates: {
                    getParsedConfigFile: 'templates/getparsedconfigfile',
                    getClonedComponentFromTemplate: 'templates/getclonedcomponentfromtemplate',
                    getTemplateById: 'templates/gettemplatebyid',
                    getManagementTemplateById: 'templates/getmanagementtemplatebyid',
                    getTemplateList: 'templates/gettemplatelist',
                    getTemplateBuilderList: 'templates/gettemplatebuilderlist',
                    getQuickTemplateList: 'templates/getquicktemplatelist',
                    getReadyTemplateList: 'templates/getreadytemplatelist',
                    deleteTemplate: 'servicetemplate/deleteservicetemplate',
                    copyTemplate: 'servicetemplate/copyservicetemplate',
                    createReferenceTemplate: 'servicetemplate/createreferenceservicetemplate',
                    addAttachment: 'templates/addattachment',
                    getTemplateBuilderById: 'templates/gettemplatebuilderbyid',
                    uploadTemplates: 'templates/uploadtemplate',
                    uploadConfigFile: 'templates/uploadconfigfile',


                    getTemplateBuilderComponents: 'templates/gettemplatebuildercomponents',
                    getUpdatedTemplateBuilderComponent: 'templates/getupdatedtemplatebuildercomponent',
                    getReferenceComponent: 'templates/getreferencecomponent',
                    loadTemplate: 'templates/gettemplatebuilderbyid',
                    loadTemplateDetails: 'templates/gettemplatedetails',
                    saveTemplate: 'templates/savetemplate',
                    saveTemplateAdditionalSettings: 'templates/savetemplateadditionalsettings',
                    createTemplate: 'templates/createtemplate',
                    validateTemplate: 'templates/validatetemplate',
                    validateSettings: 'templates/validatesettings',
                    discardTemplate: 'templates/discardtemplate',
                    deleteAttachment: 'templates/deleteattachment',
                    importTemplate: 'templates/importtemplate',
                    getFabricPurposes: 'templates/getfabricpurposes',
                    exportTemplate: 'templates/exporttemplate',
                    validateExport: 'templates/validateexport',
                    getVMWareComponent: 'templates/getvmwarecomponent'
                },

                configureTemplate: {
                    uploadConfigurableTemplate: 'configuretemplate/uploadconfigurabletemplate',
                    saveConfigureTemplate: 'configuretemplate/saveconfiguretemplate',
                    getConfigureTemplateById: 'configuretemplate/getconfiguretemplatebyid',
                },

                users: {
                    getCurrentUser: 'users/getcurrentuser',
                    getUsers: 'users/getusers',
                    deleteUser: 'users/deleteuser',
                    disableUser: 'users/disableuser',
                    enableUser: 'users/enableuser',
                    getUserById: 'users/getuserbyid',
                    getUserByRole: 'users/getuserbyrole',
                    saveUser: 'users/saveuser',
                    getRoles: 'users/getroles',
                    getDirectoryList: 'users/getdirectorylist',
                    getDirectoryById: 'users/getdirectorybyid',
                    getDirectoryType: 'users/getdirectorytype',
                    getProtocolType: 'users/getprotocoltype',
                    saveDirectory: 'users/savedirectory',
                    deleteADUser: 'users/deleteaduser',
                    enabledisableDirectories: 'users/enabledisableDirectories',
                    saveImportUser: 'users/saveimportuser',
                    getImportDirectoryList: 'users/getimportdirectorylist',
                    getImportUserById: 'users/getimportuserbyid',
                    deleteImportUser: 'users/deleteimportuser',
                    saveImportDirectoryUsers: 'users/saveimportdirectoryusers',
                    getDirectoryUsers: 'users/getdirectoryusers',
                    getDirectoryGroups: 'users/getdirectorygroups',
                    //getDirectoryUserById: 'users/getdirectoryuserbyid',
                    getImportRoles: 'users/getimportroles',
                    updateImportUser: 'users/updateimportuser',
                    getGroupDetails: 'users/getgroupdetails',
                    updateUserPreferences: 'users/updateuserpreferences'
                },
                networking: {
                    networks: {
                        saveNetwork: 'networks/savenetwork',
                        getNetworksList: 'networks/getnetworks',
                        getUplinkNetworksList: 'networks/getnonhardwaremanagementnetworks',
                        getServiceNetworksList: 'networks/getservicenetworkslist',
                        getServiceNetworkPortGroupList: 'networks/getservicenetworkportgrouplist',
                        getNetworkTypes: 'networks/getnetworktypes',
                        deleteNetwork: 'networks/deletenetwork',
                        getNetworkById: 'networks/getnetworkbyid',
                        getLanNetworks: 'networks/getlannetworks',
                        getSanIscsiNetworks: 'networks/getsaniscsinetworks',
                        getSanFcoeNetworks: 'networks/getsanfcoenetworks',
                        getNetworksByPurpose: 'networks/getnetworksbypurpose',
                        getNetworkTemplatesByNetworkId: '',
                        getHardwareManagementNetworks: 'networks/gethardwaremanagementnetworks'
                    }
                },
                deployTemplate: {
                    getDeploymentTemplateList: 'deploymenttemplate/getdeploymenttemplatelist',
                    getChassisListByTemplateId: 'deployments/getchassislistbytemplateid',
                    getNetworkListByTemplateId: 'deployments/getnetworklistbytemplateid',
                    saveDeployment: 'deployments/submitdeployment',
                    exportDeployTemplate: 'deployments/exportdeploytemplate',

                    addVirtualNic: 'deploymenttemplate/addvirtualnic',
                    removeVirtualNic: 'deploymenttemplate/removevirtualnic',
                    updateVirtualNic: 'deploymenttemplate/updatevirtualnic'
                },
                firmwareReport: {
                    getfirmwarereport: "firmware/getfirmwarereport"
                }


            },
            templates: {},

            help: {
                //this is the index, which is GUID-5B8DE7B7-879F-45A4-88E0-732155904029.html, which happens to be Notes, cautions, and warnings
                //none: 'help/index.html',

                //this is gettingstarted
                none: 'help/GUID-C6B6B706-1D45-458E-B47D-1D14FC416C3D.html',

                //added for page level help
                resources: "help/GUID-DCE48024-1840-480A-823F-1C926332134A.html",
                backupandrestore: "help/GUID-D2C936AF-C242-45C1-BFE6-867819245ADE.html",
                gettingstarted: "help/GUID-C6B6B706-1D45-458E-B47D-1D14FC416C3D.html",

                serverpoolcreate: "help/GUID-0D3A9AE2-28E7-45E4-8407-F42A951D7E6A.html",
                serverpooledit: "help/GUID-982DFF2E-83DA-41B4-80FD-33EBC76A9B6A.html",
                discoverywizardwelcome: "help/GUID-4B948DC5-05D0-4B66-A887-1EA4C980EF58.html",
                discoverywizardfirmwaredefault: "help/GUID-4B948DC5-05D0-4B66-A887-1EA4C980EF58.html",
                discoverydiscoverresourcespage: "help/GUID-4B948DC5-05D0-4B66-A887-1EA4C980EF58.html",
                discoveryaddiprangedialog: "help/GUID-279D9EB4-C9C1-4895-8F2C-2B56156CE2F5.html",
                discoverydiscovereddevices: "help/GUID-339DE4CE-CCB1-445D-8C54-54A9184C8C2E.html",
                discoverywizardfirmwarecompliance: "help/GUID-4B948DC5-05D0-4B66-A887-1EA4C980EF58.html",
                Settings: "help/GUID-92176BC4-A06B-4521-84A3-2644070F5F8C.html",
                networkslandingpage: "help/GUID-2002C7A9-44BF-473E-92AA-3A8364AF746D.html",
                networksaddingediting: "help/GUID-C89539DD-AAC1-4CA2-9DE1-9BB4F5ECB242.html",
                virtualidentitypoolslandingpage: "help/GUID-D748E7CD-9409-4C67-BFB6-647A7A111C09.html",
                virtualidentitypoolcreate: "help/GUID-BE4F327D-2A22-4DEC-8F2F-7EE0A0C1F39D.html",
                virtualidentitiespooladdingMAC: "help/GUID-B5AA5F58-D738-4D0A-A5D5-A61D29817004.html",
                virtualidentitiespooladdingIQN: "help/GUID-E45E5AE5-3472-4BFB-8A4F-B19AE3CB8A19.html",
                virualidentitypooladdingWWPN: "help/GUID-ED147BBA-A7BE-4AD5-85EC-663A15A0C4B5.html",
                virtualidentitypooladdingWWNN: "help/GUID-5B3464DB-8BA2-4676-9EBF-4C50D0B24702.html",
                SettingsUsersHomePage: "help/GUID-DE302F08-A5B3-4380-9138-8380C2009144.html",
                SettingsCreatingUser: "help/GUID-867D0995-352C-4DB4-B65A-602440BA6A94.html",
                SettingsUsersEditingUser: "help/GUID-1A4B7486-665C-42DE-BCEA-1E9511176DBA.html",
                SettingsUsersImportingUsers: "help/GUID-07289072-3699-4567-AA67-8D7AF94880A0.html",
                SetingsUsersDirectoryServices: "help/GUID-98858DAC-3947-4DA9-ADCD-9C71BBBDC9B1.html",
                SetingsUsersDirectoryServicesConnectionSettings: "help/GUID-4EAFC0F8-4BD8-4A01-B5A5-A123D49C00A1.html",
                SettingsUsersDirectoryServicesAttributeSettings: "help/GUID-CB18BA64-A917-4A49-8534-F2A3791FB3CE.html",
                SettingsUsersDirectoryServicesSummary: "help/GUID-34C736F1-E753-4380-8D74-EB6F0E91B80D.html",
                credentialslandingpage: "help/GUID-B4C4600A-1B8A-49D1-9BE0-D8B75961FD65.html",
                creatingcredentials: "help/GUID-152B27C5-8165-4121-A126-5701E9046597.html",
                editingcredentials: "help/GUID-8C279792-C3A3-447F-BAA7-E90459206A7C.html",
                Logs: "help/GUID-9D0A415E-869C-4795-B5E6-E9906BCC2370.html",
                appliancemanagementhomepage: "help/GUID-3820906D-846E-4A8A-BD74-222B96F2412C.html",
                appliancemanagementgeneratecert: "help/GUID-92AB3E3B-1D37-4EBC-A667-9DF659BAA322.html",

                initialsetupwelcome: "help/GUID-0D32AB59-08F7-4EFE-9287-7992D615F2E2.html",
                initialsetuplicensing: "help/GUID-70E23B3C-854B-4867-ABF6-F44E3E111ED9.html",
                initialsetuptimezone: "help/GUID-432EC241-8D28-4EFB-A751-280A9FCA150B.html",
                initialsetupproxysettings: "help/GUID-879CE912-BE7B-4260-B65C-CA63B8A9C5CC.html",
                initalsetupnetworks: "help/GUID-13AC0B2C-05DB-4CD8-99C2-624DC01E82F9.html",
                initialsetupsummary: "help/GUID-F40CD627-3AA7-44C6-B110-0635A77BCDA6.html",
                dashboardoverview: "help/GUID-A23B7FFC-9E91-42D5-A3FE-1B32757B2AA9.html",
                dashboarddeploynewservice: "help/GUID-4F98D4A9-4E41-4831-B348-D59B46016463.html",
                templatesoverview: "help/GUID-BF3B9047-373C-486F-9D52-8A30133FBECE.html",
                templateshomepage: "help/GUID-CBFFF4B6-E4AD-475C-98C0-0A46371929B5.html",
                sampletemplates: "help/GUID-69B22C4F-C0BA-4CA1-9FD4-62E8CA50C8E8.html",
                creatingtemplate: "help/GUID-90061F56-B1A1-4664-A3A8-3F7457BAFAB9.html",
                cloningtemplate: "help/GUID-10CD78FB-ED63-4B82-AF27-642386E3B299.html",
                deployingserviceoverview: "help/GUID-6C347A4C-1851-446D-877B-B97E3C21985E.html",
                deployingservice: "help/GUID-08692DCB-BDB7-44A9-A78F-7CBF5E525E9F.html",
                deployservicesummary: "help/GUID-08337D59-513C-407B-A463-22740A1CFD6B.html",
                deployservicetemplatedetails: "help/GUID-B281A7F0-66B7-49B9-961A-85EA1C9B508B.html",

                services: "help/GUID-ECA74D5D-7553-463E-A847-62C6FE5447E3.html",
                servicesaddserver: "help/GUID-0B19223E-5C8A-41BE-B4CE-0ECD6064C157.html",
                servicesaddstorage: "help/GUID-0877BB1F-5A6C-44F0-851A-0A658040CFA4.html",
                servicesaddvm: "help/GUID-E584CE9F-30C1-4CEE-9ED6-F42A1A3CE272.html",

                adjustresources: "help/GUID-DA55E711-6BD6-4AEE-B3B0-DF00087B855C.html",
                migrateserver: "help/GUID-176D8738-1FDD-4211-A751-0945CD50255E.html",
                templatebuilder: "help/GUID-E95E031D-97A8-4AB2-8C20-9B9A767F8F3C.html",
                templatesettings: "help/GUID-10F9BC7E-8A2B-48E5-A9E5-3301C6866B64.html",
                addstorage: "help/GUID-8F6DFB36-A15F-461B-B05B-C24C3A5F587A.html",
                addserver: "help/GUID-2F45EC04-0CFA-4B42-90EF-C2F4C6F3C0B1.html",
                addcluster: "help/GUID-EB2075D8-B964-4603-859F-A1AF4304C7F4.html",
                addappliance: "help/GUID-234AE139-8EDE-4742-AC73-68D22B18FF3B.html",
                addapplication: "help/GUID-DDFD7386-D614-4AB8-9DC2-40993F007B10.html",
                addapplicationservice: "help/GUID-6B9AFFA6-BFA5-4782-A396-B06AD1724DC6.html",
                importreferenceserver: "help/GUID-32CDCA7A-D22D-4F8D-9127-F3EB21F5CA1E.html",
                importtemplate: "help/GUID-EA0F30F3-7610-4E6B-88AD-DCD6FC48B983.html",
                createtemplate: "help/GUID-AD053629-1EB2-4129-9B03-0C1053EFB7FC.html",
                addtemplate: "help/GUID-90061F56-B1A1-4664-A3A8-3F7457BAFAB9.html",

                dhcp: "help/GUID-402DAFDC-4465-461B-A3D0-E0F11C2DC3C2.html",
                resourcesupdatingfirmware: "help/GUID-98DAF563-A44D-4C04-BA55-805A62994E4B.html",
                updatefirmwarewizard: "help/GUID-98DAF563-A44D-4C04-BA55-805A62994E4B.html",
                configurechassis: "help/GUID-7CB57D8F-2E95-4A8B-AD28-A4D97FEAEB34.html",
                repositories: "help/GUID-29778BCC-D293-4885-9B5A-786AEF8CF107.html",

                discoveryinitialchassisconfiguration: "help/GUID-44C740DE-D5B7-4DDC-AE67-C344A8FDC889.html",
                scheduledjobs: "help/GUID-DB7868BF-0260-47B6-99B5-E67542B3097C.html",

                //10-21-2014 Help Drop Integration

                configureDHCPsettings: "help/GUID-402DAFDC-4465-461B-A3D0-E0F11C2DC3C2.html",

                resourcedetails: "help/GUID-3DA30A19-1C38-4E26-960E-7AB6CF45952A.html",
                serverpools: "help/GUID-3CB7C7BE-8BE9-47DB-8117-98F28573B672.html",
                servicedetails: "help/GUID-09061359-0CFA-4480-9E81-BC461839BFC5.html",
                ServicesAddClusters: "help/GUID-ABAFCE3E-AABE-4BAD-BC8F-C06FB08B4B88.html",
                ServicesAddApplications: "help/GUID-DDFD7386-D614-4AB8-9DC2-40993F007B10.html",
                deleteservice: "help/GUID-6B1FEB89-27E7-4720-9D7C-566403BFD433.html",
                DeleteResources: "help/GUID-F1FCD4CA-8A26-4967-BBDD-4A50D9AC93F9.html",
                EditServiceInformation: "help/GUID-6FBA007F-1F1F-4B87-85E2-718DD23C981D.html",
                ViewDeploymentSettings: "help/GUID-072C70BF-F26C-4B48-8A15-1D3E7E6A362F.html",

                initialchassisconfiguration: "help/GUID-44C740DE-D5B7-4DDC-AE67-C344A8FDC889.html",

                configuringchassis: "help/GUID-7CB57D8F-2E95-4A8B-AD28-A4D97FEAEB34.html",
                configurechassisdiscoveredresources: "help/GUID-2768FB1F-FC4D-432D-99CA-BDBDB3EE0FCD.html",
                configurechassisconfiguredefaultrepository: "help/GUID-73665B98-3C81-4B29-87A6-AB986ED5E821.html",
                configurechassisfirmwarecompliance: "help/GUID-74403F65-DED9-4BBF-A6C0-B89A67EB011E.html",
                configurechassischassisglobal: "help/GUID-3416E41B-1961-44B7-8C15-45EFB7BCBBE5.html",
                ConfigureChassisAddCMCUser: "help/GUID-759745A8-B4D3-4B28-804E-26F31824EC72.html",
                ConfigureChassisiDRACUser: "help/GUID-DC1CAE28-9E4B-4DA4-BCDB-EFB5A011D180.html",
                configurechassisconfigurechassis: "help/GUID-55862FC4-5A15-4830-9C05-E6DDD2B91EC8.html",
                configurechassisconfigureserver: "help/GUID-64A7DFAF-BA33-4D57-9452-587390FEFDBC.html",
                configurechassisconfigureIOmodules: "help/GUID-D8071F08-4F4A-419A-8BC5-B7DB4D1DAD8A.html",
                configurechassiconfigureuplinks: "help/GUID-3E7F5240-5A9A-498F-BD6E-D3241D166BAB.html",
                configuechassisDefineuplinkdialog: "help/GUID-3C2E9886-1281-45A8-91AA-6A30E46CA0BF.html",
                ConfigureChassisSummary: "help/GUID-7E1DCE1D-CB66-4682-A3D8-A1C55F649528.html",

                viewfirmwarecompliance: "help/GUID-57681C39-16A9-49E8-AAA5-B83064D8CD31.html",
                viewfirmwarecomplianceservice: "help/GUID-B3DF3974-3512-4027-AD3C-06A23D397984.html",

                ImportingTemplates: "help/GUID-8B9AA354-34DE-4336-979A-D42D800748D3.html",
                TemplateEditingTemplateInformation: "help/GUID-24F76008-81FE-4F36-98BE-10284C22F296.html",

                repositorieshomepage: "help/GUID-29778BCC-D293-4885-9B5A-786AEF8CF107.html",
                AddOSImageRepository: "help/GUID-F2AFCFE6-F28E-4EA6-A26C-7E5CE94E09F1.html",
                EditOSImageRepository: "help/GUID-F13B2694-A391-4BD6-8D57-82F75CAC8E71.html",
                SyncOSImageRepository: "help/GUID-00C6E147-FF71-4EE3-851E-7EFFD89AD400.html",
                UnderstandingFirmwareRepositories: "help/GUID-F4A1D04F-0649-41C4-8EA1-0B79610504AF.html",
                viewbundles: "help/GUID-95330B4B-072A-46E6-B0C7-2D085959F921.html",
                Addingfirmwarerepositories: "help/GUID-74B9EE18-16EE-4A35-99FA-5E449BDCA479.html",
                configureRcm: "help/GUID-F4A1D04F-0649-41C4-8EA1-0B79610504AF.html",

                BackupNow: "help/GUID-25DAB7DA-314F-4FAE-927E-47F9A8ED1055.html",
                RestoreNow: "help/GUID-D46DFFA2-2CAC-44F1-9073-D031E1AA8FFF.html",
                EditingBackupSettingsAndDetails: "help/GUID-347E4F95-E735-466D-80E4-E5DE5E714F76.html",
                EditingAutomaticallyScheduledBackups: "help/GUID-0EA99976-B62D-4AC2-8A49-6A8529357678.html",

                EditingDefaultNTPSettings: "help/GUID-5635F5F5-CBAA-422B-BA36-A1047EE0C29A.html",
                EditingProxySettings: "help/GUID-E223F488-0D92-45A8-8F05-56F24B756FD2.html",
                EditingVxRackFLEXAlertConnectorSettings: "help/GUID-C044F94E-7E6B-4946-A50C-A6574927C20E.html",
                InitialSetupAlertConnectorSettings: "help/GUID-5B2EC1A9-9328-432E-B9C3-96AC3EAA36C0.html",
                EditingDHCPSettings: "help/GUID-1B478D09-7D69-4B87-9A82-E23A44993D22.html",
                UploadinganSSLCertificate: "help/GUID-90E8E840-A825-408C-8BEF-1F03A39F1F29.html",
                DownloadinganSSLCertificate: "help/GUID-FFC60E06-0254-493D-AE4B-D9F664CF5739.html",
                LicenseManagement: "help/GUID-AB959B7A-5D13-4ABD-8730-1118772710D2.html",

                exportingtemplate: 'help/GUID-0A6214EF-4D65-4612-BDE1-D522525C2EB1.html',
                uploadingtemplate: 'help/GUID-C14F691C-340C-4A1C-89EB-77E3B82F2E46.html',

                templateAddAttachment: 'help/GUID-30B43E75-29F5-4346-8153-7DA9DF532BD3.html',
                TemplateExportTemplate: 'help/GUID-0A6214EF-4D65-4612-BDE1-D522525C2EB1.html',
                Template_UploadExternalTemplate: 'help/GUID-C14F691C-340C-4A1C-89EB-77E3B82F2E46.html',
                Template_DeployService: 'help/GUID-E0E98DCC-F837-496C-8284-69612DDEDF68.html',

                DeployServiceAddNetwork: 'help/GUID-0957E277-D2F7-41C4-A718-CD1E15E3B71E.html',
                UpdateRepositoryPath: 'help/GUID-B476B03D-47C0-4562-919A-8ACD7DFD03D5.html',
                uploadServerConfigProfile: 'help/GUID-33E8F572-A039-48CC-8F15-E4D84F784F3F.html',
                addCustomBundle: 'help/GUID-1A989EA1-F5F0-486D-866A-8BDF30AF59BA.html',
                updateComponents: 'help/GUID-ADE94816-9B37-4E0F-80D8-3A28F1349E87.html',
                UpdateInventory: 'help/GUID-644FC7DF-7677-4831-BF6A-7DB6F4BECE67.html',

                AddingAddonModules: "help/GUID-793C54ED-C3EC-446C-A108-FCEB2DE8D826.html",
                AddingExistingService: "help/GUID-5CCF9C2B-052F-4AA5-A70C-D1BA793E3FCE.html",
                addonmodules: "help/GUID-23A231BB-B4EA-4FA1-97BE-662CF68021A2.html",
                storageAlertLearnMore: "help/GUID-8E4E473E-1C57-47E4-BE41-A81E33B2A935.html"
            },

            GenerateUrl: function (command, params) {

                if (!command) return '';

                var url = this[command];
                if (!url) url = command;

                var template = _.template(url);
                return template(params);

            }

        };
    });
