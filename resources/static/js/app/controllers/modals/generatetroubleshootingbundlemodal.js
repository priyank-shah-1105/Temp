var asm;
(function (asm) {
    var GenerateTroubleshootingBundleModalController = (function () {
        function GenerateTroubleshootingBundleModalController(modal, $http, $timeout, $scope, $q, $translate, loading, dialog, Commands, globalServices, $location) {
            this.modal = modal;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.loading = loading;
            this.dialog = dialog;
            this.Commands = Commands;
            this.globalServices = globalServices;
            this.$location = $location;
            //troubleshootingBundle
            this.tsBundle = {
                id: '',
                serviceId: '',
                bundleDest: 'export',
                filepath: '',
                username: '',
                password: '',
            };
            this.errors = new Array();
            this.disabled = false;
            this.callingPage = '';
            var self = this;
            self.callingPage = self.$scope.modal.params.callingPage;
            if (self.callingPage == 'Service') {
                //Service detail page must provide the serviceId in the call to exportTroubleshootingBundle
                self.tsBundle.serviceId = self.$scope.modal.params.serviceId;
            }
            else {
                //other pages do not provide the serviceId in the call to exportTroubleshootingBundle; VirtualApplianceManagement
                self.tsBundle.serviceId = '';
            }
            self.initialize();
        }
        GenerateTroubleshootingBundleModalController.prototype.initialize = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.getGettingStarted()
                .then(function (data) {
                self.disabled = !data.data.responseObj.srsOrPhoneHomeConfigured;
                if (self.disabled) {
                    //if SRS or Phone Home is not configured, default selection to download locally (i.e. unselect 'export' and select 'network' by default for the radio buttons)
                    self.tsBundle.bundleDest = 'network';
                }
                else {
                    //if SRS or Phone Home is configured, default selection to send to configured SRS or Phone Home (i.e. unselect 'network' and select 'export' by default for the radio buttons)
                    self.tsBundle.bundleDest = 'export';
                }
            })
                .catch(function (response) {
                self.globalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        GenerateTroubleshootingBundleModalController.prototype.generateTsBundle = function () {
            var self = this;
            var d = self.$q.defer();
            //self.cleanForms(self.tsBundle);
            self.globalServices.ClearErrors();
            self.loading(d.promise);
            self.generateBundle(self.tsBundle)
                .then(function (response) {
                self.close();
            })
                .catch(function (response) {
                self.globalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        GenerateTroubleshootingBundleModalController.prototype.testConnection = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.testConn(self.tsBundle)
                .then(function (data) {
                self.dialog((self.$translate.instant('SETTINGS_GenerateTroubleshootingBundle_testconnection_success_title')), ('<i class="text-success ci-health-square-check"></i> ' + self.$translate.instant('SETTINGS_GenerateTroubleshootingBundle_testconnection_success_message')), true);
            })
                .catch(function (data) {
                self.globalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        // Add community String
        GenerateTroubleshootingBundleModalController.prototype.testAddCS = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.testAddCSBundle(self.addCS)
                .then(function (data) {
                    self.dialog((self.$translate.instant('SUCCESS')), ('<i class="text-success ci-health-square-check"></i> ' + self.$translate.instant('The test was successful')), true);
                })
                .catch(function (data) {
                    self.globalServices.DisplayError(data.data, self.errors);
                })
                .finally(function () { return d.resolve(); });
        };

        //Delete Commuinty String
        GenerateTroubleshootingBundleModalController.prototype.testDeleteCS = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.testDeleteCSBundle(self.deleteCS)
                .then(function (data) {
                    self.dialog((self.$translate.instant('SUCCESS')), ('<i class="text-success ci-health-square-check"></i> ' + self.$translate.instant('The test was successful')), true);
                })
                .catch(function (data) {
                    self.globalServices.DisplayError(data.data, self.errors);
                })
                .finally(function () { return d.resolve(); });
        };

        // Add Forwarding Details
        GenerateTroubleshootingBundleModalController.prototype.testAddFowarding = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.testAddForwardingBundle(self.addForwarding)
                .then(function (data) {
                    self.dialog((self.$translate.instant('SUCCESS')), ('<i class="text-success ci-health-square-check"></i> ' + self.$translate.instant('The test was successful')), true);
                })
                .catch(function (data) {
                    self.globalServices.DisplayError(data.data, self.errors);
                })
                .finally(function () { return d.resolve(); });
        };

        // delete Forwarding Details
        GenerateTroubleshootingBundleModalController.prototype.testDeleteFowarding = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.testDeleteForwardingBundle(self.deleteForwarding)
                .then(function (data) {
                    self.dialog((self.$translate.instant('SUCCESS')), ('<i class="text-success ci-health-square-check"></i> ' + self.$translate.instant('The test was successful')), true);
                })
                .catch(function (data) {
                    self.globalServices.DisplayError(data.data, self.errors);
                })
                .finally(function () { return d.resolve(); });
        };

        GenerateTroubleshootingBundleModalController.prototype.goToSettings = function () {
            var self = this;
            self.cancel();
            self.$timeout(function () {
                self.$location.path('settings/VirtualApplianceManagement');
            }, 500);
        };
        GenerateTroubleshootingBundleModalController.prototype.cleanForms = function (bundle) {
            var self = this;
            if (bundle.bundleDest === 'export') {
            }
            if (bundle.bundleDest === 'network') {
            }
        };
        GenerateTroubleshootingBundleModalController.prototype.getGettingStarted = function () {
            var self = this;
            return self.$http.post(self.Commands.data.initialSetup.gettingStarted, null);
        };
        GenerateTroubleshootingBundleModalController.prototype.generateBundle = function (bundle) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.exportTroubleshootingBundle, bundle);
        };
        GenerateTroubleshootingBundleModalController.prototype.testConn = function (bundle) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.testTroubleshootingBundle, bundle);
        };
        GenerateTroubleshootingBundleModalController.prototype.testAddCSBundle = function (bundle) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.addCommunityString, bundle);
        };
        GenerateTroubleshootingBundleModalController.prototype.testDeleteCSBundle = function (bundle) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.deleteCommunityString, bundle);
        };
        GenerateTroubleshootingBundleModalController.prototype.testAddForwardingBundle = function (bundle) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.addForwardingDetails, bundle);
        };
        GenerateTroubleshootingBundleModalController.prototype.testDeleteForwardingBundle = function (bundle) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.deleteForwardingDetails, bundle);
        };
        GenerateTroubleshootingBundleModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        GenerateTroubleshootingBundleModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        GenerateTroubleshootingBundleModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$location'];
        return GenerateTroubleshootingBundleModalController;
    }());
    asm.GenerateTroubleshootingBundleModalController = GenerateTroubleshootingBundleModalController;
    angular
        .module('app')
        .controller('GenerateTroubleshootingBundleModalController', GenerateTroubleshootingBundleModalController);
})(asm || (asm = {}));
//# sourceMappingURL=generatetroubleshootingbundlemodal.js.map
