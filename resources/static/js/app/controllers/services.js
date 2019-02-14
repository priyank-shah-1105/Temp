var asm;
(function (asm) {
    var ServicesController = (function () {
        function ServicesController($http, $timeout, $q, $translate, Modal, Loading, dialog, Commands, GlobalServices, $location, constants, $routeParams, $rootScope, $route, $window, localStorageService) {
            var _this = this;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.dialog = dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$location = $location;
            this.constants = constants;
            this.$routeParams = $routeParams;
            this.$rootScope = $rootScope;
            this.$route = $route;
            this.$window = $window;
            this.localStorageService = localStorageService;
            this.smartTableOptions = {
                pages: 7,
                itemsPerPage: 20
            };
            this.tileView = false;
            this.actions = {
                viewDetails: function () { return _this.viewDetails(); },
                exportService: function () { return _this.exportService(); },
                updateFirmware: function () { return _this.updateFirmware(); }
            };
            this.dropdownOptions = angular.copy(this.constants.serviceHealths);
            var self = this;
            var health = _.find(self.constants.serviceHealths, { id: self.$routeParams.health });
            self.filterBy = health ? health.id : undefined;
            self.dropdownOptions.unshift({ id: undefined, name: self.$translate.instant("SERVICES_All") });
            self.refresh();
        }
        Object.defineProperty(ServicesController.prototype, "safeSource", {
            get: function () {
                var self = this;
                return self._safeSource;
            },
            set: function (value) {
                var self = this;
                self._safeSource = value;
            },
            enumerable: true,
            configurable: true
        });
        ServicesController.prototype.clearTooltips = function () {
            $('[data-toggle="tooltip"]').tooltip('hide');
        };
        ServicesController.prototype.storeView = function () {
            var self = this;
            if (self.tileView) {
                self.localStorageService.set('servicesTable_currentView', 'tileView');
            }
            else {
                self.localStorageService.set('servicesTable_currentView', 'listView');
            }
        };
        ServicesController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.clearTooltips();
            var storedView = self.localStorageService.get('servicesTable_currentView');
            if (storedView) {
                if (storedView === 'tileView') {
                    self.tileView = true;
                }
                else {
                    self.tileView = false;
                }
            }
            self.GlobalServices.ClearErrors();
            self.Loading(d.promise);
            // get all services
            self.$q.all([
                self.getServiceList().then(function (data) {
                    if (self.GlobalServices.cache.services.length > 0) {
                        self.GlobalServices.cache.services = data.data.responseObj;
                    }
                    //Copy for filter to reference to reset safe source
                    self.unFilteredServiceList = angular.copy(data.data.responseObj);
                    //copy for smart table to sort on and for controller to filter on (final result)
                    self.displayedData = angular.copy(data.data.responseObj);
                    //copy for smart table to reference that will be filtered
                    self.safeSource = angular.copy(data.data.responseObj);
                    self.groupStates();
                    self.filterSafeSource();
                }),
                self.getReadyTemplateList().then(function (data) {
                    self.readyTemplateList = data.data.responseObj;
                })
            ]).catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            }).finally(function () { return d.resolve(); });
        };
        ServicesController.prototype.viewDetails = function (id) {
            var self = this;
            self.$location.path("service/" + (id || self.selectedService.id) + "/details");
        };
        //filters smart table's safe source, allows paging to be accurate
        ServicesController.prototype.filterSafeSource = function () {
            var self = this;
            //reset safeSource to original value
            self.safeSource = angular.copy(self.unFilteredServiceList);
            //filter safeSource
            if (self.filterBy) {
                self.safeSource = _.filter(self.safeSource, { health: self.filterBy });
            }
        };
        ServicesController.prototype.exportService = function () {
            var self = this;
            self.$window.location.assign("services/exportservice/" + self.selectedService.id);
        };
        ServicesController.prototype.downloadAll = function () {
            var self = this;
            var deferred = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.Loading(deferred.promise);
            self.processDownloadRequests('initial', '', deferred);
        };
        ServicesController.prototype.processDownloadRequests = function (type, id, deferred) {
            var self = this;
            var urlToCall;
            var data;
            urlToCall = self.Commands.data.downloads.status;
            data = { 'id': id };
            if (type == 'initial') {
                urlToCall = self.Commands.data.downloads.create;
                data = { 'type': 'services' };
            }
            self.$http.post(urlToCall, { requestObj: data }).then(function (data) {
                switch (data.data.responseObj.status) {
                    case 'NOT_READY':
                        self.$timeout(function () {
                            self.processDownloadRequests('status', data.data.responseObj.id, deferred);
                        }, 5000);
                        break;
                    case 'READY':
                        self.$window.location.assign("downloads/getfile/" + data.data.responseObj.id);
                        deferred.resolve();
                        break;
                    case 'ERROR':
                        //handle error
                        var x = 0;
                        deferred.resolve();
                        self.GlobalServices.DisplayError(data.data);
                        break;
                }
            }).catch(function (data) {
                //need to handle error
                deferred.resolve();
                //error is in data
                self.GlobalServices.DisplayError(data.data);
            });
        };
        //creates the display of tiles for the "all" filter
        ServicesController.prototype.groupStates = function () {
            var self = this;
            self.groups = [];
            var id = 0;
            self.groups = _.map(_.map(self.constants.serviceHealths, function (health) {
                return {
                    count: _.filter(self.unFilteredServiceList, { health: health.id }).length,
                    name: health.id,
                    dropdown: health.id,
                    id: id++,
                    health: health.id,
                    alias: health.alias
                };
            }), function (finalHealth) {
                return angular.extend(finalHealth, { name: finalHealth.alias + " (" + finalHealth.count + ")" });
            });
        };
        ServicesController.prototype.updateFirmware = function () {
            var self = this;
            self.clearTooltips();
            var updateFirmware = self.Modal({
                title: self.$translate.instant('SERVICE_APPLY_FIRMWARE_UPDATES_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('resourcesupdatingfirmware');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/updateservicefirmware.html',
                controller: 'UpdateServiceFirmwareController as UpdateServiceFirmware',
                params: {
                    id: self.selectedService.id,
                    mode: 'service'
                },
                onCancel: function () {
                    //THIS FUNCTION IS CALLED ON MODAL.CANCEL, not WIZARD.CANCEL
                    //var confirm : self.Dialog(self.$translate.instant('GENERIC_Confirm'), 'Are you sure you want to cancel?');
                    var confirm = self.dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICE_APPLY_FIRMWARE_UPDATES_Cancel_Confirmation'));
                    confirm.then(function (modalScope) {
                        updateFirmware.modal.dismiss();
                        self.refresh();
                    });
                },
                onComplete: function (modalScope) {
                    self.refresh(); //When the modal is closed, update the data.
                }
            });
            updateFirmware.modal.show();
        };
        ServicesController.prototype.addExistingService = function () {
            var self = this;
            self.clearTooltips();
            var addServiceWizard = self.Modal({
                title: self.$translate.instant('SERVICE_ADD_EXISTING_SERVICE_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('AddingExistingService');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/addexistingservice.html',
                controller: 'AddExistingServiceController as addExistingServiceController',
                params: {},
                onCancel: function () {
                    //THIS FUNCTION IS CALLED ON MODAL.CANCEL, not WIZARD.CANCEL
                    //var confirm : self.Dialog(self.$translate.instant('GENERIC_Confirm'), 'Are you sure you want to cancel?');
                    var confirm = self.dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICE_ADD_EXISTING_SERVICE_Cancel_Confirmation'));
                    confirm.then(function (modalScope) {
                        addServiceWizard.modal.dismiss();
                    });
                },
                onComplete: function (modalScope) {
                    self.refresh(); //When the modal is closed, update the data.
                }
            });
            addServiceWizard.modal.show();
        };
        ServicesController.prototype.deployNewService = function () {
            var self = this;
            self.clearTooltips();
            var addServiceWizard = self.Modal({
                title: self.$translate.instant('SERVICES_NEW_SERVICE_DeployService'),
                onHelp: function () {
                    self.GlobalServices.showHelp('deployingserviceoverview');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/deployservice/deployservicewizard.html',
                controller: 'DeployServiceWizard as deployServiceWizard',
                params: {},
                onCancel: function () {
                    self.dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_DEPLOY_ConfirmWizardClosing')).then(function () {
                        console.log('closing');
                        addServiceWizard.modal.close();
                    });
                }
            });
            addServiceWizard.modal.show();
        };
        ServicesController.prototype.getServiceList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.services.getServiceList, {});
        };
        ServicesController.prototype.getReadyTemplateList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.templates.getReadyTemplateList, {});
        };
        ServicesController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog',
            'Commands', 'GlobalServices', '$location', 'constants', '$routeParams', "$rootScope", "$route", "$window", "localStorageService"];
        return ServicesController;
    }());
    asm.ServicesController = ServicesController;
    angular
        .module("app")
        .controller("ServicesController", ServicesController);
})(asm || (asm = {}));
//# sourceMappingURL=services.js.map
