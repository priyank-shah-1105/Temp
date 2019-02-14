var asm;
(function (asm) {
    var ConstantsFactory = (function () {
        function ConstantsFactory($translate) {
            this.$translate = $translate;
            this.genericSelectOption = { name: this.$translate.instant("GENERIC_select"), id: undefined };
            this.availableCMCUserRoles = [
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_Administrator"), id: "Administrator" },
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_PowerUser"), id: "PowerUser" },
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_GuestUser"), id: "GuestUser" },
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_None"), id: "None" }
            ];
            this.availableDeviceViews = [
                { name: this.$translate.instant('GENERIC_All'), id: '' },
                { name: this.$translate.instant('DEVICES_ElementManager'), id: 'em' },
                { name: this.$translate.instant('GENERIC_Servers'), id: 'server' },
                { name: this.$translate.instant('GENERIC_Switches'), id: 'switch' },
                //{ name: this.$translate.instant('DEVICES_VMManager'), id: 'vmm' },
                { name: this.$translate.instant('DEVICES_VMManager'), id: 'vcenter' },
                { name: this.$translate.instant('DEVICES_ScaleIO'), id: 'scaleio' }
            ];
            this.availableDeviceHealthViews = [
                { name: this.$translate.instant('GENERIC_All'), id: '' },
                { name: this.$translate.instant('GENERIC_Healthy'), id: 'green' },
                { name: this.$translate.instant('GENERIC_Warning'), id: 'yellow' },
                { name: this.$translate.instant('GENERIC_Critical'), id: 'red' },
                { name: this.$translate.instant('GENERIC_Unknown'), id: 'unknown' },
                { name: this.$translate.instant('GENERIC_ServiceMode'), id: 'servicemode' }
            ];
            this.availableiDracUserRoles = [
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_Administrator"), id: "Administrator" },
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_Operator"), id: "Operator" },
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_ReadOnly"), id: "ReadOnly" },
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_None"), id: "None" }
            ];
            this.availableLanRoles = [
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_User"), id: "User" },
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_Operator"), id: "Operator" },
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_Administrator"), id: "Administrator" },
                { name: this.$translate.instant("CREATE_EDIT_DEVICE_USERS_NoAccess"), id: "No Access" }
            ];
            this.availableLogCategories = [
                { name: this.$translate.instant('SERVICES_Logs_Security') },
                { name: this.$translate.instant('SERVICES_Logs_Appliance_Configuration') },
                { name: this.$translate.instant('SERVICES_Logs_Template_Configuration') },
                { name: this.$translate.instant('SERVICES_Logs_Network_Configuration') },
                { name: this.$translate.instant('SERVICES_Logs_Hardware_Configuration') },
                { name: this.$translate.instant('SERVICES_Logs_Monitoring') },
                { name: this.$translate.instant('SERVICES_Logs_Deployment') },
                { name: this.$translate.instant('SERVICES_Logs_Licensing') },
                { name: this.$translate.instant('SERVICES_Logs_Miscellaneous') }
            ];
            this.availableManagedStates = [
                { name: this.$translate.instant('DEVICES_Managed'), id: "managed" },
                { name: this.$translate.instant('DEVICES_Unmanaged'), id: "unmanaged" },
                { name: this.$translate.instant('DEVICES_Reserved'), id: "reserved" }
            ];
            this.availableNicTypes = [
                { name: this.$translate.instant('NICTYPE_2port'), id: "2x10Gb" },
                //{ name: this.$translate.instant('NICTYPE_4port'), id: "4x10Gb" },
                { name: this.$translate.instant('NICTYPE_2port25Gb'), id: "2x25Gb" },
                { name: this.$translate.instant('NICTYPE_2x1Gb2x10Gb'), id: "2x10Gb,2x1Gb" }
            ];
            this.availablePowercapMeasurementTypes = [
                { name: this.$translate.instant("GENERIC_Watts"), id: "watts" },
                { name: this.$translate.instant("GENERIC_BtuHour"), id: "btuh" },
                { name: this.$translate.instant("GENERIC_PercentSymbol"), id: "percentage" }
            ];
            this.availableRedundancyPolicies = [
                { name: this.$translate.instant("CONFIGURECHASSIS_DEVICE_CONFIG_NoRedundancy"), id: "none" },
                { name: this.$translate.instant("CONFIGURECHASSIS_DEVICE_CONFIG_GridRedundancy"), id: "grid" },
                { name: this.$translate.instant("CONFIGURECHASSIS_DEVICE_CONFIG_PowerSupplyRedundancy"), id: "powersupply" },
                { name: this.$translate.instant("CONFIGURECHASSIS_DEVICE_CONFIG_RedundancyAlertingOnly"), id: "alertonly" }
            ];
            this.availableResourceTypes = [
                { name: this.$translate.instant('GENERIC_ElementManager'), id: "em" },
                { name: this.$translate.instant('GENERIC_Server'), id: "server" },
                { name: this.$translate.instant('GENERIC_Switch'), id: "switch" },
                //{ name: this.$translate.instant('DEVICES_VMManager'), id: 'vmm' },
                { name: this.$translate.instant('DEVICES_VMManager'), id: 'vcenter' },
                { name: this.$translate.instant('DEVICES_ScaleIO'), id: "scaleio" },
            ];
            this.availableCredentialTypes = [
                { name: this.$translate.instant('GENERIC_Server'), id: "server" },
                { name: this.$translate.instant('GENERIC_Switch'), id: "iom" },
                { name: this.$translate.instant('GENERIC_VCenter'), id: "vcenter" },
                { name: this.$translate.instant('GENERIC_ElementManager'), id: "em" },
                { name: this.$translate.instant('DEVICES_ScaleIO'), id: "scaleio" },
                { name: this.$translate.instant('GENERIC_OS'), id: "os" },
            ];
            this.availableStorageModeOptions = [
                { name: this.$translate.instant("CONFIGURECHASSIS_StorageOption_Single"), id: "single" },
                { name: this.$translate.instant("CONFIGURECHASSIS_StorageOption_Dual"), id: "dual" },
                { name: this.$translate.instant("CONFIGURECHASSIS_StorageOption_Joined"), id: "joined" }
            ];
            this.basicRaidOptions = [
                { name: this.$translate.instant('RAIDCONFIGURATION_raid0'), id: "raid0" },
                { name: this.$translate.instant('RAIDCONFIGURATION_raid1'), id: "raid1" },
                { name: this.$translate.instant('RAIDCONFIGURATION_raid5'), id: "raid5" },
                { name: this.$translate.instant('RAIDCONFIGURATION_raid6'), id: "raid6" },
                { name: this.$translate.instant('RAIDCONFIGURATION_raid10'), id: "raid10" },
                { name: this.$translate.instant('RAIDCONFIGURATION_raid50'), id: "raid50" },
                { name: this.$translate.instant('RAIDCONFIGURATION_raid60'), id: "raid60" }
            ];
            this.comparators = [
                { name: this.$translate.instant('COMPARATOR_Minimum'), id: "minimum" },
                { name: this.$translate.instant('COMPARATOR_Exactly'), id: "exact" }
            ];
            this.resourceStates = [
                { name: this.$translate.instant('SERVICE_DETAIL_Ready'), id: "deployed", icon: "success ci-health-square-check", color: "green" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_InProgress'), id: "inprogress", icon: "info ci-schedule-clock-o", color: "standby" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Pending'), id: "pending", icon: "info ci-schedule-clock-o", color: "standby" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Warning'), id: "warning", icon: "ci-health-warning-tri-bang text-warning", color: "yellow" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Error'), id: "error", icon: "critical ci-action-circle-remove", color: "red" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Cancelled'), id: "cancelled", icon: "danger ci-action-circle-remove-slash-o", color: "cancelled" },
                { name: this.$translate.instant('GENERIC_ServiceMode'), id: "servicemode", icon: "warning ci-action-circle-wrench", color: "servicemode" }
            ];
            this.componentStatus = [
                { name: this.$translate.instant('SERVICE_DETAIL_Deployed'), id: "complete", icon: "success ci-health-square-check", color: "green" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_InProgress'), id: "inprogress", icon: "info ci-schedule-clock-o", color: "standby" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Pending'), id: "pending", icon: "info ci-schedule-clock-o", color: "standby" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Warning'), id: "warning", icon: "ci-health-warning-tri-bang text-warning", color: "yellow" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Error'), id: "error", icon: "critical ci-action-circle-remove", color: "red" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Cancelled'), id: "cancelled", icon: "danger ci-action-circle-remove-slash-o", color: "cancelled" },
                { name: this.$translate.instant('GENERIC_ServiceMode'), id: "servicemode", icon: "warning ci-action-circle-wrench", color: "servicemode" }
            ];
            this.deviceTypes = [
                { id: 'AggregatorIOM', name: this.$translate.instant('DEVICETYPE_AggregatorIOM') },
                { id: 'BladeServer', name: this.$translate.instant('DEVICETYPE_BladeServer') },
                { id: 'ChassisFX', name: this.$translate.instant('DEVICETYPE_ChassisFX') },
                { id: 'ChassisM1000e', name: this.$translate.instant('DEVICETYPE_Chassis') },
                { id: 'ChassisVRTX', name: this.$translate.instant('DEVICETYPE_VRTX') },
                { id: 'compellent', name: this.$translate.instant('DEVICETYPE_Compellent') },
                { id: 'dellswitch', name: this.$translate.instant('DEVICETYPE_DellSwitch') },
                { id: 'ciscoswitch', name: this.$translate.instant('DEVICETYPE_CiscoSwitch') },
                { id: 'em', name: this.$translate.instant('DEVICETYPE_EM') },
                { id: 'emcvnx', name: this.$translate.instant('DEVICETYPE_EMCVNX') },
                { id: 'emcunity', name: this.$translate.instant('DEVICETYPE_EMCUnity') },
                { id: 'equallogic', name: this.$translate.instant('DEVICETYPE_Equallogic') },
                { id: 'FXIOM', name: this.$translate.instant('DEVICETYPE_FXIOM') },
                { id: 'FXServer', name: this.$translate.instant('DEVICETYPE_FXServer') },
                { id: 'genericswitch', name: this.$translate.instant('DEVICETYPE_GenericSwitch') },
                { id: 'MXLIOM', name: this.$translate.instant('DEVICETYPE_MXLIOM') },
                { id: 'netapp', name: this.$translate.instant('DEVICETYPE_NetApp') },
                { id: 'RackServer', name: this.$translate.instant('DEVICETYPE_RackServer') },
                { id: 'scvmm', name: this.$translate.instant('DEVICETYPE_SCVMM') },
                { id: 'Server', name: this.$translate.instant('DEVICETYPE_Server') },
                { id: 'storage', name: this.$translate.instant('DEVICETYPE_Storage') },
                { id: 'TowerServer', name: this.$translate.instant('DEVICETYPE_TowerServer') },
                { id: 'unknown', name: this.$translate.instant('DEVICETYPE_Unknown') },
                { id: 'vcenter', name: this.$translate.instant('DEVICETYPE_VCenter') },
                { id: 'vm', name: this.$translate.instant('DEVICETYPE_VM') },
                { id: 'scaleio', name: this.$translate.instant('DEVICETYPE_ScaleIO') },
            ];
            this.deviceState = [
                { id: 'available', name: this.$translate.instant('DEVICESTATE_Available') },
                { id: 'deployed', name: this.$translate.instant('DEVICESTATE_Deployed') },
                { id: 'unknown', name: this.$translate.instant('DEVICESTATE_Unknown') },
                { id: 'copying', name: this.$translate.instant('DEVICESTATE_Copying') },
                { id: 'error', name: this.$translate.instant('DEVICESTATE_Error') },
                { id: 'pending', name: this.$translate.instant('DEVICESTATE_Pending') },
                { id: 'updating', name: this.$translate.instant('DEVICESTATE_Updating') },
                { id: 'deploying', name: this.$translate.instant('DEVICESTATE_Deploying') },
                { id: 'poweringoff', name: this.$translate.instant('DEVICESTATE_PoweringOff') },
                { id: 'poweringon', name: this.$translate.instant('DEVICESTATE_PoweringOn') },
                { id: 'reserved', name: this.$translate.instant('DEVICESTATE_Reserved') },
                { id: 'unmanaged', name: this.$translate.instant('DEVICESTATE_Unmanaged') },
                { id: 'online', name: this.$translate.instant('DEVICESTATE_Online') }
            ];
            this.diskTypes = [
                { name: this.$translate.instant('DISKTYPES_Any'), id: "any" },
                { name: this.$translate.instant('DISKTYPES_First'), id: "first" },
                { name: this.$translate.instant('DISKTYPES_Last'), id: "last" },
                { name: this.$translate.instant('DISKTYPES_Hdd'), id: "requirehdd" },
                { name: this.$translate.instant('DISKTYPES_Ssd'), id: "requiressd" }
            ];
            this.firmwareCriticality = [
                { name: this.$translate.instant('SETTINGS_Repositories_Urgent'), id: "urgent" },
                { name: this.$translate.instant('SETTINGS_Repositories_Recommended'), id: "recommended" },
                { name: this.$translate.instant('SETTINGS_Repositories_Optional'), id: "optional" }
            ];
            this.firmwareStatus = [
                { id: 'unknown', name: this.$translate.instant('DEVICES_CompliantStatus_Unknown'), icon: "unknown" },
                { id: 'updaterequired', name: this.$translate.instant('DEVICES_CompliantStatus_UpdateRequired'), icon: "yellow" },
                { id: 'compliant', name: this.$translate.instant('DEVICES_CompliantStatus_Compliant'), icon: "green" },
                { id: 'noncompliant', name: this.$translate.instant('DEVICES_CompliantStatus_NonCompliant'), icon: "yellow" },
                { id: 'updating', name: this.$translate.instant('DEVICES_Updating'), icon: "blue" },
                { id: 'updatefailed', name: this.$translate.instant('DEVICES_UpdateFailed'), icon: "red" },
            ];
            this.logSeverities = [
                { name: this.$translate.instant("LOGS_RESOURCE_SEVERITIES_Healthy"), id: "success" },
                { name: this.$translate.instant("LOGS_RESOURCE_SEVERITIES_Critical"), id: "critical" },
                { name: this.$translate.instant("LOGS_RESOURCE_SEVERITIES_Warning"), id: "warning" },
                { name: this.$translate.instant("LOGS_RESOURCE_SEVERITIES_Info"), id: "info" }
            ];
            this.repositoryImageTypes = [
                { name: this.$translate.instant('SETTINGS_Repositories_imagetype_vmware'), id: "vmware_esxi" },
                //{ name: this.$translate.instant('SETTINGS_Repositories_imagetype_redhat'), id: "redhat" },
                { name: this.$translate.instant('SETTINGS_Repositories_imagetype_redhat7'), id: "redhat7" }
            ];
            this.resourceStateFilter = [
                { name: this.$translate.instant('GENERIC_All'), id: '' },
                { name: this.$translate.instant('DEVICES_Managed'), id: 'managed' },
                { name: this.$translate.instant('DEVICES_Unmanaged'), id: 'unmanaged' },
                { name: this.$translate.instant('DEVICES_Reserved'), id: 'reserved' }
            ];
            this.serviceHealths = [
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Healthy'), id: "green", alias: this.$translate.instant("GENERIC_Healthy") },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Warning'), id: "yellow", alias: this.$translate.instant("GENERIC_Warning") },
                { name: this.$translate.instant('GENERIC_Critical'), id: "red", alias: this.$translate.instant("GENERIC_Critical") },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Pending'), id: "pending", alias: this.$translate.instant("GENERIC_Pending") },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_InProgress'), id: "unknown", alias: this.$translate.instant("GENERIC_InProgress") },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Cancelled'), id: "cancelled", alias: this.$translate.instant("GENERIC_Cancelled") },
                { name: this.$translate.instant('GENERIC_Incomplete'), id: "incomplete", alias: this.$translate.instant("GENERIC_Incomplete") },
                { name: this.$translate.instant('GENERIC_ServiceMode'), id: "servicemode", alias: this.$translate.instant("GENERIC_ServiceMode") }
            ];
            this.serviceStates = [
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Error'), id: "Error", alias: 1, health: "red" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Healthy'), id: "Healthy", alias: 2, health: "green" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_InProgress'), id: "In Progress", alias: 3, health: "unknown" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Pending'), id: "Pending", alias: 4, health: "pending" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Warning'), id: "Warning", alias: 5, health: "yellow" },
                { name: this.$translate.instant('SERVICES_DEPLOY_STATE_Cancelled'), id: "Cancelled", alias: 6, health: "cancelled" },
                { name: this.$translate.instant('GENERIC_ServiceMode'), id: "Service Mode", alias: 7 },
            ];
            this.serviceTypes = [
                { name: this.$translate.instant("SERVICE_ADD_EXISTING_Service_Types_Hyperconverged"), id: "hyperconverged" },
                { name: this.$translate.instant("SERVICE_ADD_EXISTING_Service_Types_ComputeOnly"), id: "computeonly" },
                { name: this.$translate.instant("SERVICE_ADD_EXISTING_Service_Types_StorageOnly"), id: "storageonly" }
            ];
            this.spanningTreeMode = [
                { name: this.$translate.instant("DEFINE_UPLINKS_RSTP"), id: "1" },
                { name: this.$translate.instant("DEFINE_UPLINKS_MSTP"), id: "2" },
                { name: this.$translate.instant("DEFINE_UPLINKS_PVST"), id: "3" },
                { name: this.$translate.instant("DEFINE_UPLINKS_None"), id: "4" }
            ];
            this.staticIPState = [
                { id: 'inuse', name: this.$translate.instant('NETWORKSSTATE_InUse') },
                { id: 'available', name: this.$translate.instant('NETWORKSSTATE_Available') }
            ];
            this.staticIPAddressDetailsViews = [
                { id: "all", name: this.$translate.instant('NETWORKSVIEW_All') },
                { id: "inuse", name: this.$translate.instant('NETWORKSVIEW_InUse') },
                { id: "available", name: this.$translate.instant('NETWORKSVIEW_Available') }
            ];
            this.ipAddressRangeRole = [
                { id: 'sdsorsdc', name: this.$translate.instant('NETWORKS_Edit_IPAddressRangeRole_SDSorSDC') },
                { id: 'sdsonly', name: this.$translate.instant('NETWORKS_Edit_IPAddressRangeRole_SDSOnly') },
                { id: 'sdconly', name: this.$translate.instant('NETWORKS_Edit_IPAddressRangeRole_SDCOnly') }
            ];
        }
        return ConstantsFactory;
    }());
    asm.ConstantsFactory = ConstantsFactory;
})(asm || (asm = {}));
angular.module('ASM.constants').service('constants', ['$translate', function ($translate) {
        return new asm.ConstantsFactory($translate);
    }]);
//# sourceMappingURL=constants.js.map
