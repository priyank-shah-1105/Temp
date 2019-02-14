/// <reference path="asmlogsdirective.ts" />
var asm;
(function (asm) {
    "use strict";
    var GettingStartedComponentController = (function () {
        function GettingStartedComponentController(Modal, $rootScope, $http, $timeout, Commands, $location, $translate, Dialog, GlobalServices, $interval, $q, Loading) {
            this.Modal = Modal;
            this.$rootScope = $rootScope;
            this.$http = $http;
            this.$timeout = $timeout;
            this.Commands = Commands;
            this.$location = $location;
            this.$translate = $translate;
            this.Dialog = Dialog;
            this.GlobalServices = GlobalServices;
            this.$interval = $interval;
            this.$q = $q;
            this.Loading = Loading;
            this.oobe = {};
            this.serviceExists = false;
            this.refreshTimer = null;
            this.firmwareInterval = null;
            this.urlList = {
                publishTemplates: "/templates",
                asm: "http://support.vce.com/",
                asmCommunity: "http://en.community.dell.com/techcenter/converged-infrastructure/w/wiki/4318.dell-active-system-manager"
            };
            this.firmwareCopying = false;
            this.watchFirmwareStatus = true;
            this.stopPresentingSetupWizard = false;
            var self = this;
            self.activate();
        }
        GettingStartedComponentController.prototype.activate = function () {
            var self = this;
            self.refresh();
            self.beginPolling();
            self.watchHeights();
        };
        GettingStartedComponentController.prototype.$onDestroy = function () {
            var self = this;
            self.stopPolling();
            if (angular.isDefined(self.heightCalculator))
                self.$interval.cancel(self.heightCalculator);
        };
        GettingStartedComponentController.prototype.refresh = function () {
            var self = this;
            //var d = self.$q.defer();
            self.GlobalServices.ClearErrors();
            //self.Loading(d.promise);
            self.$q.all([
                self.getGettingStarted()
                    .then(function (data) {
                    self.oobe = data.data.responseObj;
                    self.GlobalServices.showInitialSetup = !self.oobe.initialSetupCompleted;
                    if (!self.oobe.initialSetupCompleted && !self.stopPresentingSetupWizard) {
                        self.setupWizard();
                        self.stopPresentingSetupWizard = true;
                    }
                }),
                self.getServiceList()
                    .then(function (data) {
                    self.serviceExists = _.some(data.data.responseObj, { 'brownField': true });
                }),
                self.watchFirmwareStatus && self.getFirmwareStatus()
            ])
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data);
            }); //.finally(() => d.resolve());
        };
        GettingStartedComponentController.prototype.beginPolling = function () {
            var self = this;
            self.stopPolling();
            self.refreshTimer = self.$interval(function () { return self.refresh(); }, 30000);
        };
        GettingStartedComponentController.prototype.stopPolling = function () {
            var self = this;
            if (angular.isDefined(self.refreshTimer))
                self.$interval.cancel(self.refreshTimer);
        };
        GettingStartedComponentController.prototype.getFirmwareStatus = function () {
            var self = this;
            self.GlobalServices.ClearErrors();
            return self.getFirmwarePackages()
                .then(function (response) {
                self.firmwareCopying = _.find(response.data.responseObj, { state: "copying" });
                if (!self.firmwareCopying) {
                    self.watchFirmwareStatus = false;
                }
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); });
        };
        GettingStartedComponentController.prototype.watchHeights = function () {
            //sets where buttons like, "Define Networks" and, "Discover Resources" are placed vertically
            var self = this;
            return self.heightCalculator = self.$interval(function () {
                var allElementsToMeasure = $("#step-row")
                    .find(".height-to-measure");
                //add heights of 2 children found in each element
                var heights = _.map(allElementsToMeasure, function (element) { return element.children[0].clientHeight + element.children[1].clientHeight; });
                var max = _.max(heights);
                //set max height found to be height of all elements
                angular.forEach(allElementsToMeasure, function (element) { $(element).height(max); });
            }, 500);
        };
        GettingStartedComponentController.prototype.update = function () {
            var self = this;
            self.$http.post(self.Commands.data.initialSetup.updateGettingStarted, self.oobe)
                .then(function (data) {
                //console.log('updateGettingStarted responseObj:');
                //console.log(JSON.stringify(data.data.responseObj));
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); });
        };
        GettingStartedComponentController.prototype.launchDiscoverWizard = function () {
            var self = this;
            var discoveryWizard = self.Modal({
                title: self.$translate.instant('DISCOVERY_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/discoverywizard.html',
                controller: 'DiscoveryWizardController as DiscoveryWizard',
                params: {
                    mode: 'gettingstarted'
                },
                onCancel: function () {
                    var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('DISCOVERY_Cancel_Confirmation'));
                    confirm.then(function () {
                        discoveryWizard.modal.dismiss();
                    });
                }
            });
            discoveryWizard.modal.show();
        };
        GettingStartedComponentController.prototype.createTemplate = function () {
            var self = this;
            var createTemplateWizard = self.Modal({
                title: self.$translate.instant('TEMPLATES_CREATE_TEMPLATE_WIZARD_AddaTemplate'),
                onHelp: function () {
                    self.GlobalServices.showHelp('addtemplate');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/templatewizard.html',
                controller: 'TemplateWizardController as templateWizardController',
                params: {},
                onComplete: function () {
                    self.refresh();
                },
                onCancel: function () {
                    self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('TEMPLATES_CREATE_TEMPLATE_WIZARD_CancelConfirmation'))
                        .then(function () {
                        createTemplateWizard.modal.close();
                    });
                }
            });
            createTemplateWizard.modal.show();
        };
        GettingStartedComponentController.prototype.setupWizard = function () {
            var self = this;
            var setupWizard = self.Modal({
                title: self.$translate.instant('SETUPWIZARD_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/setupwizard.html',
                controller: 'SetupWizardController as SetupWizard',
                params: {},
                onComplete: function () {
                    self.refresh();
                },
                onCancel: function () {
                    self.refresh();
                    setupWizard.modal.dismiss();
                }
            });
            setupWizard.modal.show();
        };
        GettingStartedComponentController.prototype.addNetworks = function () {
            var self = this;
            var addNetworks = self.Modal({
                title: self.$translate.instant('GENERIC_Networks'),
                onHelp: function () {
                    self.GlobalServices.showHelp('networksaddingediting');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/networking/networks/definenetworksmodal.html',
                controller: 'DefineNetworksController as defineNetworksController',
                params: {},
                onComplete: function () {
                    self.refresh();
                }
            });
            addNetworks.modal.show();
        };
        GettingStartedComponentController.prototype.addExistingService = function () {
            var self = this;
            var addServiceWizard = self.Modal({
                title: self.$translate.instant('SERVICE_ADD_EXISTING_SERVICE_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('AddingExistingService');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/addexistingservice.html',
                controller: 'AddExistingServiceController as addExistingServiceController',
                onCancel: function () {
                    var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICE_ADD_EXISTING_SERVICE_Cancel_Confirmation'));
                    confirm.then(function (modalScope) {
                        addServiceWizard.modal.dismiss();
                        self.refresh();
                    });
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            addServiceWizard.modal.show();
        };
        GettingStartedComponentController.prototype.configureRcm = function () {
            var self = this;
            var configureRcmModal = self.Modal({
                title: self.$translate.instant('GETTINGSTARTED_ConfigureReleaseCertificationMartix'),
                onHelp: function () {
                    self.GlobalServices.showHelp('configureRcm');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/configurereleasecert.html',
                controller: 'ConfigureReleaseCertController as configureReleaseCertController',
                params: {},
                onComplete: function () {
                    self.getFirmwareStatus();
                },
                onCancel: function () {
                    self.getFirmwareStatus();
                    configureRcmModal.modal.dismiss();
                }
            });
            configureRcmModal.modal.show();
        };
        GettingStartedComponentController.prototype.restoreFromBackup = function () {
            var self = this;
            var backupAndRestore = self.Modal({
                title: self.$translate.instant('BACKUPANDRESTORE_btnrestorenow'),
                onHelp: function () {
                    self.GlobalServices.showHelp('RestoreNow');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/backupandrestore/restorenow.html',
                controller: 'RestoreNowModalController as restoreNow',
                params: {},
                onCancel: function () {
                    self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('DEVICES_Apply_Firmware_Updates_Cancel_Confirmation')).then(function () {
                        backupAndRestore.modal.dismiss();
                    });
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            backupAndRestore.modal.show();
        };
        GettingStartedComponentController.prototype.goTo = function (url) {
            var self = this;
            self.$location.path(url);
        };
        GettingStartedComponentController.prototype.getFirmwarePackages = function () {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.getFirmwarePackages, null);
        };
        GettingStartedComponentController.prototype.getGettingStarted = function () {
            var self = this;
            return self.$http.post(self.Commands.data.initialSetup.gettingStarted, null);
        };
        GettingStartedComponentController.prototype.getServiceList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.services.getServiceList, {});
        };
        GettingStartedComponentController.$inject = ['Modal', '$rootScope', '$http',
            '$timeout', 'Commands', '$location', '$translate', 'Dialog', 'GlobalServices', '$interval',
            '$q', 'Loading'
        ];
        return GettingStartedComponentController;
    }());
    angular.module('app')
        .component('gettingStartedComponent', {
        templateUrl: 'views/gettingstartedcomponent.html',
        controller: GettingStartedComponentController,
        controllerAs: 'gettingStartedComponentController',
        bindings: {}
    });
})(asm || (asm = {}));
//# sourceMappingURL=gettingStartedComponent.js.map
