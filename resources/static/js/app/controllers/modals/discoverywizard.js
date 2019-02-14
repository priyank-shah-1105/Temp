var asm;
(function (asm) {
    var DiscoverDevice = (function () {
        function DiscoverDevice() {
            this.id = _.uniqueId();
            this.resourcetype = '';
            this.ipaddresstype = 'single';
            this.startingIpAddress = '';
            this.endingIpAddress = '';
            this.serverCredentialId = '';
            this.chassisCredentialId = '';
            this.bladeCredentialId = '';
            this.iomCredentialId = '';
            this.storageCredentialId = '';
            this.vcenterCredentialId = '';
            this.scvmmCredentialId = '';
            this.emCredentialId = '';
            this.scaleioCredentialId = '';
            this.torCredentialId = '';
            this.deviceGroupId = '';
            this.includeServers = false;
            this.includeChassis = false;
            this.includeStorage = false;
            this.includeVCenter = false;
            this.includeSCVMM = false;
            this.includeHypervisor = false;
            this.includeTOR = false;
            this.managedstate = '';
            this.serverPoolId = '';
            this.totalIPAddresses = 0;
            this.inUseIPAddresses = 0;
        }
        return DiscoverDevice;
    }());
    asm.DiscoverDevice = DiscoverDevice;
    var DiscoveryWizardController = (function () {
        function DiscoveryWizardController(Modal, Dialog, $http, $translate, GlobalServices, $timeout, $scope, constants, $q, commands, loading, $rootScope, $interval) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$translate = $translate;
            this.GlobalServices = GlobalServices;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.constants = constants;
            this.$q = $q;
            this.commands = commands;
            this.loading = loading;
            this.$rootScope = $rootScope;
            this.$interval = $interval;
            this.iresources = [];
            this.serverpools = [];
            this.credentials = {
                server: [],
                chassis: [],
                storage: [],
                vcenter: [],
                scvm: [],
                iom: [],
                em: [],
                scaleio: []
            };
            this.networks = [];
            this.hasChassis = false;
            this.hasRack = false;
            this.chassisDevices = {};
            this.rackDevices = {};
            this.configureDeviceInvalid = true;
            this.timeout = {
                chassis: undefined,
                rack: undefined
            };
            this.globalConfiguration = { configurationmode: "onboarding" };
            this.steps = {
                stepInitialChassisConfig: { invalid: true }
            };
            this.errors = {
                chassis: [],
                rack: [],
                global: [],
                identifyResources: []
            };
            var self = this;
            self.GlobalServices.ClearErrors(self.errors.global);
            self.$q.all([
                self.getNetworks()
                    .then(function (data) {
                    self.networks = data.data.responseObj;
                }),
                self.$http.post(self.commands.data.serverpools.getServerPools, null)
                    .then(function (data) {
                    self.serverpools = data.data.responseObj;
                }),
                self.getCredentials('server')
                    .then(function (data) {
                    self.credentials.server = data.data.responseObj;
                }),
                self.getCredentials('chassis')
                    .then(function (data) {
                    self.credentials.chassis = data.data.responseObj;
                }),
                self.getCredentials('storage')
                    .then(function (data) {
                    self.credentials.storage = data.data.responseObj;
                }),
                self.getCredentials('vcenter')
                    .then(function (data) {
                    self.credentials.vcenter = data.data.responseObj;
                }),
                self.getCredentials('scvmm')
                    .then(function (data) {
                    self.credentials.scvmm = data.data.responseObj;
                }),
                self.getCredentials('iom')
                    .then(function (data) {
                    self.credentials.iom = data.data.responseObj;
                }),
                self.getCredentials('em')
                    .then(function (data) {
                    self.credentials.em = data.data.responseObj;
                }),
                self.getCredentials('scaleio')
                    .then(function (data) {
                    self.credentials.scaleio = data.data.responseObj;
                })
            ])
                .catch(function (data) {
                //TODO:  handle errors object
                self.GlobalServices.DisplayError(data.data, self.errors.global);
            });
        }
        DiscoveryWizardController.prototype.getType = function (type) {
            //converts detailed type into device type
            return type === 'blade' || type === "rack"
                ? 'server'
                : type === 'switch' || type === 'tor' ? 'iom' : type;
        };
        DiscoveryWizardController.prototype.getCredentials = function (type) {
            var self = this;
            return self.$http.post(self.commands.data.credential.getCredentialByType, { id: type });
        };
        DiscoveryWizardController.prototype.getNetworks = function () {
            var self = this;
            return self.$http.post(self.commands.data.networking.networks.getHardwareManagementNetworks, null);
        };
        DiscoveryWizardController.prototype.enterWelcome = function () {
            var self = this;
            self.$rootScope.helpToken = 'discoverywizardwelcome';
        };
        DiscoveryWizardController.prototype.enterIdentifyResources = function () {
            var self = this;
            self.$rootScope.helpToken = 'discoverydiscoverresourcespage';
        };
        DiscoveryWizardController.prototype.enterInitialChassisConfig = function () {
            var self = this;
            self.$rootScope.helpToken = 'discoveryinitialchassisconfiguration';
            //disable the Next button in case the Back button was used to get here
            self.steps.stepInitialChassisConfig.invalid = false;
        };
        DiscoveryWizardController.prototype.nextButtonDisabled = function () {
            var self = this;
            return self.steps.stepInitialChassisConfig.invalid;
        };
        DiscoveryWizardController.prototype.enterSummary = function () {
            var self = this;
            self.$rootScope.helpToken = 'discoverywizardwelcome';
            self.chassisDevices.selectedArray = _.filter(self.chassisDevices.noErrorArray, { configureDevice: true });
            self.rackDevices.selectedArray = _.filter(self.rackDevices.noErrorArray, { configureDevice: true });
        };
        DiscoveryWizardController.prototype.getPlaceholder = function (row) {
            var self = this;
            switch (row.resourcetype) {
                case 'server': return self.$translate.instant('DISCOVERY_PlaceholderServer');
                case 'storage': return self.$translate.instant('DISCOVERY_PlaceholderStorage');
                case 'switch': return self.$translate.instant('DISCOVERY_PlaceholderSwitch');
                case 'vcenter': return self.$translate.instant('DISCOVERY_PlaceholderVCenter');
                case 'scvmm': return self.$translate.instant('DISCOVERY_PlaceholderSCVMM');
                case 'chassis': return self.$translate.instant('DISCOVERY_PlaceholderChassis');
                default:
                    return '';
            }
        };
        DiscoveryWizardController.prototype.getServerPoolValue = function (row) {
            var self = this, match = _.find(self.serverpools, { id: row.serverPoolId });
            return match ? match.name : '';
        };
        DiscoveryWizardController.prototype.getSelectedCredential = function (id, type) {
            var self = this;
            var match = _.find(self.credentials[self.getType(type)], { id: id });
            return match ? match.credentialsName : "";
        };
        DiscoveryWizardController.prototype.getSelectedNetwork = function (id) {
            var self = this;
            var match = _.find(self.networks, { id: id });
            return match.credentialsName;
        };
        DiscoveryWizardController.prototype.showChassisConfig = function () {
            var self = this;
            return !!(_.find(self.iresources, function (resource) {
                return (resource.resourcetype === "chassis" || resource.resourcetype === "all");
            }) ||
                _.find(self.chassisDevices.chassisConfigurations, { configureDevice: true }));
        };
        DiscoveryWizardController.prototype.configurableDevice = function (array) {
            return !!_.find(array, { configureDevice: true });
        };
        DiscoveryWizardController.prototype.checkForChassis = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors.identifyResources);
            self.GlobalServices.ClearErrors(self.errors.global);
            //On entrance of Initial Resource Configuration, check whether user has asked for chassis and/or servers or any device types at all
            //if no resources have been added, go back to Identify Resources step with a message saying to add resources
            self.hasChassis = !!_.find(self.iresources, function (resource) {
                return (resource.resourcetype === "all" || resource.resourcetype === "chassis");
            });
            self.hasRack = !!_.find(self.iresources, function (resource) {
                return (resource.resourcetype === "all" || resource.resourcetype === "server");
            });
            return self.$q(function (resolve, reject) {
                if (!self.iresources.length) {
                    self.GlobalServices.DisplayError({
                        message: self.$translate.instant("DISCOVERY_IDResourcesErrorNoResources"),
                        severity: "CRITICAL"
                    }, self.errors.identifyResources);
                    return reject();
                }
                //Check whether forms on Identify Resources step are all valid, reject if not
                angular.forEach(self.iresources, function (iresource) { return iresource._form._submitted = true; });
                //return _.find(self.iresources, (iresource: any) => { return iresource._form.$invalid }) ? reject() : resolve();
                if (_.find(self.iresources, function (iresource) { return iresource._form.$invalid; })) {
                    self.GlobalServices.DisplayError({
                        message: self.$translate.instant("DISCOVERY_IDResourcesErrorIncomplete"),
                        severity: "CRITICAL"
                    }, self.errors.identifyResources);
                    return reject();
                }
                else {
                    return resolve();
                }
            });
        };
        DiscoveryWizardController.prototype.validateInitialChassisConfig = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors.global);
            return self.$q(function (resolve, reject) {
                angular.forEach(self.configForms, function (obj) { if (obj)
                    obj._submitted = true; });
                if (_.find(self.configForms, { $invalid: true })) {
                    reject();
                    self.GlobalServices.scrollToInvalidElement("page_discoveryWizard");
                }
                else
                    resolve();
            });
        };
        DiscoveryWizardController.prototype.showUndiscovered = function (mode, list) {
            var self = this;
            var createServerPoolModal = self.Modal({
                title: self.$translate.instant('SETTINGS_DirectoryServicesWizard_ViewUndiscoveredResources'),
                modalSize: 'modal-lg',
                templateUrl: 'views/resources/modals/viewundiscoveredresources.html',
                controller: 'ViewUndiscoveredResources as viewUndiscoveredResources',
                params: {
                    mode: mode,
                    list: _.filter(list, { status: "error" })
                },
                onCancel: function () {
                },
                onComplete: function () {
                }
            });
            createServerPoolModal.modal.show();
        };
        DiscoveryWizardController.prototype.configureDeviceShown = function () {
            var self = this;
            //disable next button
            self.steps.stepInitialChassisConfig.invalid = true;
            self.chassisDevices = { pending: self.hasChassis };
            self.rackDevices = { pending: self.hasRack };
            self.configureDeviceInvalid = true;
            self.errors.chassis = [];
            self.errors.rack = [];
            //delete form off of each list item because it contains circular references
            var iresourcesCopy = angular.forEach(angular.copy(self.iresources), function (iresource) { return delete iresource._form; });
            var getChassisStatuses = function () {
                return self.$http.post(self.commands.data.discovery.getChassisListStatus, self.chassisDevices)
                    .then(function (data) {
                    self.chassisDevices = data.data.responseObj;
                    //find all chassis with errors
                    self.chassisDevices.noErrorArray = _.filter(self.chassisDevices.chassisConfigurations, function (chassis) { return chassis.status !== "error"; });
                    //don't display errors until all devices are resolved
                    if (self.chassisDevices.pending) {
                        return;
                    }
                    //clear error
                    self.errors.chassis = [];
                    //push chassis error if any
                    var undiscovered = self.chassisDevices.chassisConfigurations.length - self.chassisDevices.noErrorArray.length;
                    undiscovered && self.errors.chassis.push({
                        message: self.$translate.instant("SETTINGS_DirectoryServicesWizard_numResourcesUndiscovered", { num: undiscovered }),
                        action: function () { self.showUndiscovered("chassis", angular.copy(self.chassisDevices.chassisConfigurations)); }
                    });
                })
                    .catch(function (error) {
                    self.GlobalServices.DisplayError(error.data, self.errors.chassis);
                });
            };
            var getRackStatuses = function () {
                return self.$http.post(self.commands.data.discovery.getRackListStatus, self.rackDevices)
                    .then(function (data) {
                    self.rackDevices = data.data.responseObj;
                    //find all racks with errors
                    self.rackDevices.noErrorArray = _.filter(self.rackDevices.rackConfigurations, function (rack) { return rack.status !== "error"; });
                    //don't display errors until all devices are resolved
                    if (self.rackDevices.pending) {
                        return;
                    }
                    //clear error
                    self.errors.rack = [];
                    //push chassis error if any
                    var undiscovered = self.rackDevices.rackConfigurations.length - self.rackDevices.noErrorArray.length;
                    undiscovered && self.errors.rack.push({
                        message: self.$translate.instant("SETTINGS_DirectoryServicesWizard_numResourcesUndiscovered", { num: undiscovered }),
                        action: function () { self.showUndiscovered("rack", angular.copy(self.rackDevices.rackConfigurations)); }
                    });
                })
                    .catch(function (error) {
                    self.GlobalServices.DisplayError(error.data, self.errors.rack);
                });
            };
            var statuses = function () { return [
                self.hasChassis && getChassisStatuses(),
                self.hasRack && getRackStatuses()
            ]; };
            self.$q.all([
                self.hasChassis &&
                    self.$http.post(self.commands.data.discovery.getChassisList, iresourcesCopy)
                        .then(function (data) {
                        self.chassisDevices = data.data.responseObj;
                        self.chassisDevices.noErrorArray = _.filter(self.chassisDevices.chassisConfigurations, function (chassis) { return chassis.status !== "error"; });
                    })
                        .catch(function (error) {
                        self.chassisDevices.pending = false;
                        self.GlobalServices.DisplayError(error.data, self.errors.chassis);
                    }),
                self.hasRack &&
                    self.$http.post(self.commands.data.discovery.getRackList, iresourcesCopy)
                        .then(function (data) {
                        self.rackDevices = data.data.responseObj;
                        self.rackDevices.noErrorArray = _.filter(self.rackDevices.rackConfigurations, function (rack) { return rack.status !== "error"; });
                    })
                        .catch(function (error) {
                        self.rackDevices.pending = false;
                        self.GlobalServices.DisplayError(error.data, self.errors.rack);
                    })
            ])
                .then(function () {
                angular.isDefined(self.statusInterval) && self.$interval.cancel(self.statusInterval);
                self.statusInterval = self.$interval(function () {
                    //run all functions in 'statuses' array on interval
                    self.$q.all(statuses())
                        .then(function () {
                        if (!self.chassisDevices.pending && !self.rackDevices.pending) {
                            if (angular.isDefined(self.statusInterval)) {
                                self.$interval.cancel(self.statusInterval);
                            }
                            if ((self.chassisDevices.noErrorArray && self.chassisDevices.noErrorArray.length >= 1)
                                || (self.rackDevices.noErrorArray && self.rackDevices.noErrorArray.length >= 1)) {
                                //enable the Next button after configureDeviceShown is complete
                                self.steps.stepInitialChassisConfig.invalid = false;
                            }
                        }
                    });
                }, 10000);
            });
            self.$scope.$on("$destroy", function () {
                angular.isDefined(self.statusInterval) && self.$interval.cancel(self.statusInterval);
            });
        };
        DiscoveryWizardController.prototype.doCreateServerPool = function (row) {
            var self = this;
            var createServerPoolModal = self.Modal({
                title: self.$translate.instant('RESOURCE_CREATE_SERVER_POOL_CreateServerPool'),
                modalSize: 'modal-lg',
                templateUrl: 'views/resources/modals/newserverpoolwizard.html',
                controller: 'NewServerPoolWizardController as newServerPoolWizardController',
                params: {
                    calledFromDiscoverWizard: true
                },
                onCancel: function () {
                    self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_DEPLOY_ConfirmWizardClosing'))
                        .then(function () {
                        createServerPoolModal.modal.close();
                    });
                },
                onComplete: function (modalScope) {
                    if (modalScope.newServerPoolWizardController.objectId) {
                        self.$http.post(self.commands.data.serverpools.getServerPools, null)
                            .then(function (data) {
                            self.serverpools = data.data.responseObj;
                            row.serverPoolId = modalScope.newServerPoolWizardController.objectId;
                        });
                    }
                }
            });
            createServerPoolModal.modal.show();
        };
        DiscoveryWizardController.prototype.getIdProperty = function (credentialType) {
            switch (credentialType) {
                case 'chassis':
                    return "chassisCredentialId";
                case 'blade':
                    return "bladeCredentialId";
                case 'iom':
                    return "iomCredentialId";
                case "tor":
                case "switch":
                    return "torCredentialId";
                case 'server':
                    return "serverCredentialId";
                case 'storage':
                    return "storageCredentialId";
                case 'scvmm':
                    return "scvmmCredentialId";
                case 'vcenter':
                    return "vcenterCredentialId";
                case 'em':
                    return "emCredentialId";
                case 'scaleio':
                    return "scaleioCredentialId";
                case 'rack':
                    return "rackCredentialId";
            }
        };
        DiscoveryWizardController.prototype.doManageCredentials = function (updateType, credentialType, row) {
            var self = this, editMode = updateType.toUpperCase() === "EDIT";
            var theModal = self.Modal({
                title: self.$translate.instant(editMode
                    ? 'CREDENTIALS_EditTitle'
                    : 'CREDENTIALS_CreateTitle'),
                modalSize: 'modal-lg',
                templateUrl: 'views/credentials/editcredentials.html',
                controller: 'EditCredentialsController as editCredentialsController',
                params: {
                    credential: editMode ? { id: row[self.getIdProperty(credentialType)] } : undefined,
                    editMode: editMode,
                    canChangeCredentialType: false,
                    //blade needs to be server
                    //switch needs to be iom
                    typeId: self.getType(credentialType)
                },
                onComplete: function (credentialId) {
                    self.getCredentials(self.getType(credentialType))
                        .then(function (data) {
                        self.credentials[self.getType(credentialType)] = data.data.responseObj;
                        row[self.getIdProperty(credentialType)] = credentialId;
                    });
                }
            });
            theModal.modal.show();
        };
        DiscoveryWizardController.prototype.chassisConfigurationSelected = function () {
            var self = this;
            return !!_.find(self.chassisDevices.chassisConfigurations, { configureDevice: true });
        };
        DiscoveryWizardController.prototype.addResourceRow = function () {
            var x = new DiscoverDevice(), self = this;
            self.iresources.push(x);
        };
        DiscoveryWizardController.prototype.selectResourceType = function (row) {
            var self = this;
            //set managedstate if not already set
            //by resourcetype, set default value for managedstate
            //console.log('before, row.resourcetype:  ' + row.resourcetype);
            //console.log('before, row.managedstate:  ' + row.managedstate);
            if (row.managedstate == null || row.managedstate == '') {
                if (row.resourcetype === 'server') {
                    row.managedstate = 'unmanaged';
                }
                else {
                    row.managedstate = 'managed';
                }
            }
        };
        DiscoveryWizardController.prototype.deleteResourceRow = function (i) {
            var self = this;
            $('.tooltip').remove();
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('DISCOVERY_InitialResourceConfig_ConfirmDeleteResourceType'))
                .then(function () { return _.remove(self.iresources, i); });
        };
        DiscoveryWizardController.prototype.finishWizard = function () {
            var self = this;
            self.Dialog((self.$translate.instant('GENERIC_Confirm')), self.$translate.instant(self.chassisConfigurationSelected()
                ? 'DISCOVERY_LongRunningNotice'
                : "DISCOVERY_LongRunningNotice_NoChassis"))
                .then(function () {
                //delete form off of each list item because it contains circular references
                var iresourcesCopy = angular.copy(angular.forEach(self.iresources, function (iresource) { return delete iresource._form; }));
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors.chassis);
                self.GlobalServices.ClearErrors(self.errors.rack);
                self.GlobalServices.ClearErrors(self.errors.global);
                self.GlobalServices.ClearErrors(self.errors.identifyResources);
                self.loading(d.promise);
                self.$http.post(self.commands.data.discovery.submitDiscovery, {
                    chassisdata: {
                        jobId: self.chassisDevices.jobId,
                        pending: self.chassisDevices.pending,
                        chassisConfigurations: _.map(self.chassisDevices.chassisConfigurations, function (chassis) {
                            return {
                                chassisConfigurationId: chassis.chassisConfigurationId,
                                deviceid: chassis.deviceid,
                                ipAddress: chassis.ipAddress,
                                configureDevice: chassis.configureDevice
                            };
                        })
                    },
                    rackdata: {
                        jobId: self.rackDevices.jobId,
                        pending: self.rackDevices.pending,
                        rackConfigurations: _.map(self.rackDevices.rackConfigurations, function (rack) {
                            return {
                                rackConfigurationId: rack.rackConfigurationId,
                                deviceid: rack.deviceid,
                                ipaddress: rack.ipaddress,
                                configureDevice: rack.configureDevice
                            };
                        })
                    },
                    ipdata: iresourcesCopy,
                    chassisconfig: self.globalConfiguration
                })
                    .then(function () {
                    self.closeWizard();
                })
                    .catch(function (data) {
                    //TODO:  handle errors object
                    self.GlobalServices.DisplayError(data.data, self.errors.chassis);
                    self.GlobalServices.DisplayError(data.data, self.errors.rack);
                    self.GlobalServices.DisplayError(data.data, self.errors.global);
                    self.GlobalServices.DisplayError(data.data, self.errors.identifyResources);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        DiscoveryWizardController.prototype.configNetworks = function (editMode, networkId) {
            var self = this;
            var editNetworkModal = self.Modal({
                title: self.$translate.instant(editMode ? "NETWORKS_Edit_EditTitle" : "NETWORKS_Edit_CreateTitle"),
                modalSize: 'modal-lg',
                templateUrl: 'views/networking/networks/editnetwork.html',
                controller: 'EditNetworkModalController as editNetwork',
                params: {
                    editMode: editMode ? 'edit' : 'create',
                    id: networkId,
                    calledFromDiscoverWizard: true
                },
                onComplete: function (networkId) {
                    var d = self.$q.defer();
                    self.GlobalServices.ClearErrors(self.errors.chassis);
                    self.GlobalServices.ClearErrors(self.errors.rack);
                    self.GlobalServices.ClearErrors(self.errors.global);
                    self.GlobalServices.ClearErrors(self.errors.identifyResources);
                    self.loading(d.promise);
                    self.getNetworks()
                        .then(function (data) {
                        self.networks = data.data.responseObj;
                        self.globalConfiguration.rackNetworkId = networkId;
                    })
                        .catch(function (data) {
                        //TODO:  handle errors object
                        self.GlobalServices.DisplayError(data.data, self.errors.chassis);
                        self.GlobalServices.DisplayError(data.data, self.errors.rack);
                        self.GlobalServices.DisplayError(data.data, self.errors.global);
                        self.GlobalServices.DisplayError(data.data, self.errors.identifyResources);
                    })
                        .finally(function () { return d.resolve(); });
                }
            });
            editNetworkModal.modal.show();
        };
        DiscoveryWizardController.prototype.cancelWizard = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        DiscoveryWizardController.prototype.closeWizard = function () {
            var self = this;
            self.$scope.modal.close();
        };
        DiscoveryWizardController.$inject = [
            'Modal', 'Dialog', '$http', '$translate',
            'GlobalServices', '$timeout', '$scope', 'constants',
            '$q', 'Commands', 'Loading', '$rootScope', "$interval"];
        return DiscoveryWizardController;
    }());
    asm.DiscoveryWizardController = DiscoveryWizardController;
    angular
        .module('app')
        .controller('DiscoveryWizardController', DiscoveryWizardController);
})(asm || (asm = {}));
//# sourceMappingURL=discoverywizard.js.map
