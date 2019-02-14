var asm;
(function (asm) {
    var AddExistingServiceController = (function () {
        function AddExistingServiceController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, constants, $anchorScroll, $filter, $location) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            this.$anchorScroll = $anchorScroll;
            this.$filter = $filter;
            this.$location = $location;
            this.errors = [];
            this.serviceCreator = {
                "id": null,
                "name": null,
                "description": '',
                "type": '',
                "cloneexistingtemplateid": null,
                "category": '',
                "manageFirmware": true,
                "firmwarePackageId": null,
                "serviceName": '',
                "serviceDescription": '',
                "numberOfDeployments": '1',
                "scheduleType": 'deploynow',
                "scheduleDate": '',
                "emaillist": '',
                "sendnotification": false,
                "notificationtext": '',
                "updateServerFirmware": false,
                "updateNetworkFirmware": false,
                "updateStorageFirmware": false,
                "enableApps": false,
                "enableVMs": false,
                "enableCluster": false,
                "enableServer": false,
                "enableStorage": false,
                "allStandardUsers": false,
                "assignedUsers": [],
                "_allStandardUsers": "admins",
                "template": {
                    "id": null,
                    "name": '',
                    "description": '',
                    "draft": true,
                    "category": '',
                    "manageFirmware": false,
                    "firmwarePackageId": '',
                    "firmwarePackageName": '',
                    "updateServerFirmware": true,
                    "updateNetworkFirmware": false,
                    "updateStorageFirmware": false,
                    "enableApps": false,
                    "enableVMs": false,
                    "enableCluster": false,
                    "enableServer": false,
                    "enableStorage": false,
                    "isTemplateValid": true,
                    "isLocked": false,
                    "components": [],
                    "attachments": [],
                    "allStandardUsers": false,
                    "assignedUsers": [],
                    "templateAdditionalSetting": null
                }
            };
            this.warning = 'warning';
            this.types = [];
            this.osCredentials = [];
            this.vSwitches = [];
            this.credentials = [];
            this.networks = [];
            this.serviceNetworks = [];
            var self = this;
            self.refresh();
            self.types = constants.serviceTypes;
        }
        AddExistingServiceController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.getFirmwarePackages()
                .then(function (response) {
                self.firmwares = [
                    {
                        id: 'usedefaultcatalog',
                        name: self.$translate.instant('SERVICE_DETAIL_EditService_UseASMappliancedefaultcatalog'),
                        defaultpackage: false
                    }
                ].concat(response.data.responseObj);
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        AddExistingServiceController.prototype.step1Complete = function () {
            var self = this, d = self.$q.defer();
            return self.$q(function (resolve, reject) {
                if (self.forms.step1.$valid) {
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.getExistingServiceComponent()
                        .then(function (response) {
                        self.serviceCreator.template = response.data.responseObj;
                        resolve();
                        self.$timeout(function () { return $('#collapse0').collapse('show'); }, 500);
                    })
                        .catch(function (response) {
                        self.GlobalServices.DisplayError(response.data, self.errors);
                        reject();
                    })
                        .finally(function () { return d.resolve(); });
                }
                else {
                    self.forms.step1._submitted = true;
                    self.GlobalServices.scrollToInvalidElement('step1');
                    reject();
                }
            });
        };
        AddExistingServiceController.prototype.enterInventorySummary = function () {
            var self = this, d = self.$q.defer();
            return self.$q(function (resolve, reject) {
                if (self.forms.step2.$valid) {
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.getExistingService({
                        deploy: self.serviceCreator,
                        osCredentials: self.osCredentials,
                        vSwitches: self.vSwitches
                    })
                        .then(function (response) {
                        self.existingService = response.data.responseObj;
                        self.existingServiceCopy = angular.copy(self.existingService);
                        //testing code
                        //will have to force the creationg of scaleiolist since it would take too long to create it 'the right way' elsewhere in the code
                        //borrow serverlist to force the creation of scaleiolist since they share enough of the same data
                        //angular.copy(self.existingService.serverlist, self.existingService.scaleiolist);
                        angular.extend(self.existingService, {
                            available: {
                                clusters: _.filter(self.existingService.clusterlist, function (cluster) { return self.availableInventory(cluster.state); })
                                    .length,
                                servers: _.filter(self.existingService.serverlist, function (server) { return self.availableInventory(server.state); })
                                    .length,
                                storages: _.filter(self.existingService.storagelist, function (storage) { return self.availableInventory(storage.state); })
                                    .length,
                                scaleios: _.filter(self.existingService.scaleiolist, function (scaleio) { return self.availableInventory(scaleio.state); })
                                    .length
                            }
                        });
                        resolve();
                    })
                        .catch(function (response) {
                        self.GlobalServices.DisplayError(response.data, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
                else {
                    self.forms.step2._submitted = true;
                    self.GlobalServices.scrollToInvalidElement('step2');
                    reject();
                }
            });
        };
        AddExistingServiceController.prototype.enterOsCredentials = function () {
            var self = this;
            self.credentialsRequestObj = self.getCredentialsRequestObj();
            //on first pass, directive will refresh. On subsequent passes, this will manually trigger one in OSCredentials
            if (!self.hasEnteredCredentialsStep) {
                self.hasEnteredCredentialsStep = true;
            }
            else {
                self.refreshService = !self.refreshService;
            }
        };
        AddExistingServiceController.prototype.validateosCredentials = function () {
            var self = this, d = self.$q.defer();
            self.forms.osCredentials._submitted = true;
            self.forms.osCredentials.$invalid ? d.reject() : d.resolve();
            return d.promise;
        };
        AddExistingServiceController.prototype.getCredentialsRequestObj = function () {
            var self = this;
            return self.isStorageOnly() ? { template: self.serviceCreator.template } : { service: self.existingService };
        };
        AddExistingServiceController.prototype.isStorageOnly = function () {
            var self = this;
            return self.serviceCreator.type === 'storageonly';
        };
        AddExistingServiceController.prototype.enterNetworkMapping = function () {
            var self = this;
            if (self.vSwitches.length > 0)
                return false;
            var d = self.$q.defer();
            self.Loading(d.promise);
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.getExistingServiceSwitches()
                .then(function (response) {
                self.vSwitches = response.data.responseObj;
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        AddExistingServiceController.prototype.addNetwork = function () {
            var self = this;
            var editNetworkModal = self.Modal({
                title: self.$translate.instant('NETWORKS_Edit_CreateTitle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('networksaddingediting');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/networking/networks/editnetwork.html',
                controller: 'EditNetworkModalController as editNetwork',
                params: {},
                onComplete: function () {
                    var serviceNetworksCopy = angular.copy(self.serviceNetworks);
                    self.enterStorageNetworkMapping()
                        .then(function () {
                        //fill in the form with what was there originally
                        angular.forEach(serviceNetworksCopy, function (networkCopy) {
                            var match = _.find(self.serviceNetworks, { id: networkCopy.id });
                            if (match) {
                                match.networkId = networkCopy.networkId;
                            }
                        });
                    });
                }
            });
            editNetworkModal.modal.show();
        };
        AddExistingServiceController.prototype.enterStorageNetworkMapping = function () {
            var self = this;
            var d = self.$q.defer();
            self.Loading(d.promise);
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.getNetworks()
                    .then(function (response) {
                    self.networks = response.data.responseObj;
                }),
                self.getExistingServiceNetworks()
                    .then(function (response) {
                    self.serviceNetworks = response.data.responseObj;
                })
            ])
                .then(function () {
                _.forEach(self.serviceNetworks, function (serviceNetwork) {
                    serviceNetwork._networks = _.filter(self.networks, function (network) {
                        return serviceNetwork.type === network.typeid;
                    });
                });
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
            return d.promise;
        };
        AddExistingServiceController.prototype.finishWizard = function () {
            var self = this, d = self.$q.defer();
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_ExistingServiceConfirmationMessage'), false)
                .then(function () {
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.serviceCreator.template.components = self.existingService.components;
                self.addExistingService({
                    deploy: self.serviceCreator,
                    osCredentials: self.osCredentials,
                    vSwitches: !self.isStorageOnly() ? self.vSwitches : null,
                    existingServiceNetworks: self.isStorageOnly() ? self.serviceNetworks : null
                })
                    .then(function (response) {
                    self.close();
                    self.$location.path("service/" + response.data.responseObj + "/details");
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        AddExistingServiceController.prototype.updateAllStandardUsersProp = function (context) {
            switch (context._allStandardUsers) {
                case "admins":
                    context.assignedUsers = [];
                    context.allStandardUsers = false;
                    break;
                case "specific":
                    context.allStandardUsers = false;
                    break;
                case "allStandard":
                    context.assignedUsers = [];
                    context.allStandardUsers = true;
                    break;
            }
        };
        AddExistingServiceController.prototype.get_allStandardusers = function (context) {
            return context.allStandardUsers
                ? "allStandard"
                : (context.assignedUsers && context.assignedUsers.length ? "specific" : "admins");
        };
        AddExistingServiceController.prototype.categoryVisible = function (category, component) {
            var self = this;
            var filteredSettings = self.$filter("settingVisible")(category.settings, component);
            var settingsShown = self.$filter("templatesettings")(filteredSettings, component);
            return settingsShown.length;
        };
        ;
        AddExistingServiceController.prototype.availableInventory = function (state) {
            return !(state === 'unmanaged' || state === 'updating');
        };
        AddExistingServiceController.prototype.getExistingServiceSwitches = function () {
            var self = this;
            return self.$http.post(self.Commands.data.services.getExistingServiceSwitches, { service: self.existingService });
        };
        AddExistingServiceController.prototype.getExistingServiceNetworks = function () {
            var self = this;
            return self.$http.post(self.Commands.data.services.getExistingServiceNetworks, { service: self.existingService });
        };
        AddExistingServiceController.prototype.getNetworks = function () {
            var self = this;
            return self.$http.post(self.Commands.data.networking.networks.getNetworksList, null);
        };
        AddExistingServiceController.prototype.getFirmwarePackages = function () {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.getAvailableFirmwarePackages, {});
        };
        AddExistingServiceController.prototype.getExistingServiceComponent = function () {
            var self = this;
            return self.$http.post(self.Commands.data.services.getExistingServiceComponent, { type: self.serviceCreator.type }); //returns templatebuildercomponent
        };
        AddExistingServiceController.prototype.getExistingService = function (deployObject) {
            var self = this;
            return self.$http.post(self.Commands.data.services.getExistingService, deployObject); //returns service object that needs to be converted to deploy object
        };
        AddExistingServiceController.prototype.addExistingService = function (deployObject) {
            var self = this;
            return self.$http.post(self.Commands.data.services.addExistingService, deployObject); //returns success
        };
        AddExistingServiceController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        AddExistingServiceController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        AddExistingServiceController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', 'constants', '$anchorScroll', "$filter", "$location"];
        return AddExistingServiceController;
    }());
    asm.AddExistingServiceController = AddExistingServiceController;
    angular
        .module('app')
        .controller('AddExistingServiceController', AddExistingServiceController);
})(asm || (asm = {}));
//# sourceMappingURL=addExistingService.js.map
