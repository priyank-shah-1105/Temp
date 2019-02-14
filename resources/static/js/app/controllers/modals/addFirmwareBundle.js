var asm;
(function (asm) {
    var AddFirmwareBundleModalController = (function () {
        function AddFirmwareBundleModalController(modal, $http, $timeout, $scope, $q, $translate, loading, dialog, Commands, globalServices, $location) {
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
            this.repo = {
                fwrepo_select_actions: [],
                settingsfw_repo_select_actions: [],
                id: '',
                state: '',
                name: '',
                description: '',
                source: '',
                defaultpackage: true,
                filepath: '',
                username: '',
                password: '',
                deployed: false,
                isSelected: false,
                bundles: 0,
                customBundles: 0,
                components: 0,
                created: moment(),
                updated: moment(),
                services: [],
                firmwarebundles: [],
                userbundles: [],
                softwarebundles: [],
                packageSource: 'import',
                updateInterval: 'month',
                fwrepo_selected_action: '',
                settingsfw_repo_selected_action: ''
            };
            this.ftpSource = 'ftp.dell.com';
            this.errors = new Array();
            this.rcmList = [];
            this.disabled = false;
            var self = this;
            self.initialize();
        }
        AddFirmwareBundleModalController.prototype.initialize = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.$q.all([
                self.getGettingStarted()
                    .then(function (data) {
                    self.disabled = !data.data.responseObj.secureRemoteServicesConfigured;
                    if (!self.disabled) {
                        self.$http.post(self.Commands.data.firmwarepackages.getAvailableRCMs, {})
                            .then(function (data) {
                            self.rcmList = data.data.responseObj;
                        })
                            .catch(function (response) {
                            self.globalServices.DisplayError(response.data, self.errors);
                        })
                            .finally(function () { return d.resolve(); });
                    }
                    else {
                        d.resolve();
                    }
                })
                    .catch(function (response) {
                    self.globalServices.DisplayError(response.data, self.errors);
                    d.resolve();
                })
            ]);
        };
        AddFirmwareBundleModalController.prototype.testConnection = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.testFirmwarePackage(self.repo)
                .then(function (data) {
                self.dialog((self.$translate.instant('SETTINGS_Repositories_testconnection_success_title')), ('<i class="text-success ci-health-square-check"></i> ' + self.$translate.instant('SETTINGS_Repositories_testconnection_success_message')), true);
            })
                .catch(function (data) {
                self.globalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        AddFirmwareBundleModalController.prototype.save = function () {
            var self = this, d = self.$q.defer();
            self.cleanForms(self.repo);
            self.globalServices.ClearErrors();
            self.loading(d.promise);
            self.savePackage(self.repo)
                .then(function (response) {
                self.close();
            })
                .catch(function (response) {
                self.globalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        AddFirmwareBundleModalController.prototype.goToSettings = function () {
            var self = this;
            self.cancel();
            self.$timeout(function () {
                self.$location.path('settings/VirtualApplianceManagement');
            }, 500);
        };
        AddFirmwareBundleModalController.prototype.cleanForms = function (repo) {
            var self = this;
            if (repo.packageSource === 'network') {
                repo.source = self.ftpSource;
            }
        };
        AddFirmwareBundleModalController.prototype.testFirmwarePackage = function (repo) {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.testFirmwarePackage, repo);
        };
        AddFirmwareBundleModalController.prototype.savePackage = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.saveFirmwarePackage, update);
        };
        AddFirmwareBundleModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        AddFirmwareBundleModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        AddFirmwareBundleModalController.prototype.getGettingStarted = function () {
            var self = this;
            return self.$http.post(self.Commands.data.initialSetup.gettingStarted, null);
        };
        AddFirmwareBundleModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$location'];
        return AddFirmwareBundleModalController;
    }());
    asm.AddFirmwareBundleModalController = AddFirmwareBundleModalController;
    angular
        .module('app')
        .controller('AddFirmwareBundleModalController', AddFirmwareBundleModalController);
})(asm || (asm = {}));
//# sourceMappingURL=addFirmwareBundle.js.map
