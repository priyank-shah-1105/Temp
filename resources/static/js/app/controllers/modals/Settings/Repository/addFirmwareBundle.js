var asm;
(function (asm) {
    var AddFirmwareBundleModalController = (function () {
        function AddFirmwareBundleModalController(modal, $http, $timeout, $scope, $q, $translate, loading, dialog, Commands, globalServices) {
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
            this.repoFile = { file: null };
            this.ftpSource = 'ftp.dell.com';
            this.errors = new Array();
            var self = this;
            self.initialize();
        }
        AddFirmwareBundleModalController.prototype.initialize = function () {
            var self = this;
            //Wrap in a timeout to give screen time to render.  ////TODO: Replace with $onInit or similar.
            self.$timeout(function () {
                document.getElementById('idLocationFileUpload').onchange = function (evt) {
                    console.log(evt);
                    var element = angular.element(evt.target)[0];
                    self.repoFile.fileData = element.value;
                    self.repoFile.file = element.files[0];
                    self.$timeout(function () { self.uploadFile(self.repoFile.fileData); }, 10); //Wrap in a timeout to trigger a digest cycle.
                };
            }, 500);
        };
        AddFirmwareBundleModalController.prototype.testConnection = function () {
            var self = this, d = self.$q.defer();
            self.globalServices.ClearErrors();
            self.loading(d.promise);
            self.setProxyTest(self.repo)
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
        AddFirmwareBundleModalController.prototype.containsFile = function () {
            return !!$('input#idLocationFileUpload').val().length;
        };
        AddFirmwareBundleModalController.prototype.uploadFile = function (file) {
            var self = this, d = self.$q.defer();
            self.loading(d.promise);
            self.upload(file)
                .then(function (response) {
                //self.repo = angular.extend(response.data.responseObj, { defaultPackage: self.repo.defaultpackage });
                var defaultpackage = self.repo.defaultpackage;
                self.repo = response.data.responseObj;
                self.repo.defaultpackage = defaultpackage;
            })
                .catch(function (response) {
                self.globalServices.DisplayError(response.data);
            })
                .finally(function () { return d.resolve(); });
        };
        AddFirmwareBundleModalController.prototype.cleanForms = function (repo) {
            var self = this;
            if (repo.packageSource === 'import' || repo.packageSource === 'file') {
                repo.filepath = repo.username = repo.password = "";
            }
            else {
                // 'network'
                repo.source = self.ftpSource;
            }
        };
        AddFirmwareBundleModalController.prototype.savePackage = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.saveFirmwarePackage, update);
        };
        AddFirmwareBundleModalController.prototype.setProxyTest = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.testFirmwarePackage, update);
        };
        AddFirmwareBundleModalController.prototype.upload = function (file) {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.uploadFirmwarePackage, { requestObj: file });
        };
        AddFirmwareBundleModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        AddFirmwareBundleModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        AddFirmwareBundleModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return AddFirmwareBundleModalController;
    }());
    asm.AddFirmwareBundleModalController = AddFirmwareBundleModalController;
    angular
        .module('app')
        .controller('AddFirmwareBundleModalController', AddFirmwareBundleModalController);
})(asm || (asm = {}));
//# sourceMappingURL=addFirmwareBundle.js.map
