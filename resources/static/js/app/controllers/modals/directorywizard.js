var asm;
(function (asm) {
    var DirectoryWizardController = (function () {
        function DirectoryWizardController(Modal, Dialog, $http, Loading, $q, $timeout, $scope, GlobalServices, $translate, Commands, $rootScope) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.Loading = Loading;
            this.$q = $q;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.GlobalServices = GlobalServices;
            this.$translate = $translate;
            this.Commands = Commands;
            this.$rootScope = $rootScope;
            this.directorymodalcopy = {};
            this.errors = new Array();
            var self = this;
            self.editmode = self.$scope.modal.params.editmode;
            if (self.editmode) {
                self.directory = self.$scope.modal.params.directory[0];
            }
            self.refresh();
        }
        DirectoryWizardController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.$http.post(self.Commands.data.users.getDirectoryType, null)
                    .then(function (data) {
                    self.directorytypelist = data.data.responseObj;
                    self.directorymodalcopy.directoryName = self.directorytypelist[0].directoryName;
                }),
                //Get protocol list
                self.$http.post(self.Commands.data.users.getProtocolType, null)
                    .then(function (data) {
                    self.protocollist = data.data.responseObj;
                    self.directorymodalcopy.protocolName = self.protocollist[0].protocolName;
                }),
                self.editmode &&
                    self.$http.post(self.Commands.data.users.getDirectoryById, self.directory)
                        .then(function (data) {
                        self.directory = data.data.responseObj;
                        self.directorymodalcopy = angular.copy(self.directory);
                    })
            ])
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { d.resolve(); });
        };
        DirectoryWizardController.prototype.validateConnectionSettings = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                self.forms.connectionSettingsForm._submitted = true;
                self.forms.connectionSettingsForm.$valid ? resolve() : reject();
            });
        };
        DirectoryWizardController.prototype.validateAttributeSettings = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                self.forms.directoryAttributeSettings._submitted = true;
                self.forms.directoryAttributeSettings.$valid ? resolve() : reject();
            });
        };
        DirectoryWizardController.prototype.enterConnectionSettings = function () {
            var self = this;
            self.$rootScope.helpToken = 'SetingsUsersDirectoryServicesConnectionSettings';
        };
        DirectoryWizardController.prototype.enterAttributeSettings = function () {
            var self = this;
            self.$rootScope.helpToken = 'SettingsUsersDirectoryServicesAttributeSettings';
        };
        DirectoryWizardController.prototype.enterSummary = function () {
            var self = this;
            self.$rootScope.helpToken = 'SettingsUsersDirectoryServicesSummary';
        };
        DirectoryWizardController.prototype.finishWizard = function () {
            var self = this, d = self.$q.defer(), match = _.find(self.directorytypelist, { directoryName: self.directorymodalcopy.directoryName });
            self.directorymodalcopy.typeId = match.typeId;
            self.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('SETTINGS_DirectorySerivcesConfirmDir')))
                .then(function () {
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.users.saveDirectory, self.directorymodalcopy)
                    .then(function (data) {
                    self.$scope.modal.close();
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                })
                    .finally(function () { d.resolve(); });
            });
        };
        DirectoryWizardController.prototype.cancelWizard = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        DirectoryWizardController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        DirectoryWizardController.$inject = ['Modal', 'Dialog', '$http', 'Loading', '$q', '$timeout', '$scope', 'GlobalServices', '$translate', 'Commands', '$rootScope'];
        return DirectoryWizardController;
    }());
    asm.DirectoryWizardController = DirectoryWizardController;
    angular
        .module('app')
        .controller('DirectoryWizardController', DirectoryWizardController);
})(asm || (asm = {}));
//# sourceMappingURL=directorywizard.js.map
