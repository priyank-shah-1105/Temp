var asm;
(function (asm) {
    var ApplianceUpgradeSettingsModalController = (function () {
        function ApplianceUpgradeSettingsModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
            this.Modal = Modal;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.applianceUpdateInfo = {};
            this.errors = [];
            this.disabled = false;
            var self = this;
            self.initialize();
        }
        ApplianceUpgradeSettingsModalController.prototype.initialize = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.applianceUpdateInfo = self.$scope.modal.params.applianceUpdateInfo;
            self.Loading(d.promise);
            self.getGettingStarted()
                .then(function (data) {
                self.disabled = !data.data.responseObj.secureRemoteServicesConfigured;
                if (!self.disabled) {
                    if (!self.applianceUpdateInfo.source) {
                        self.applianceUpdateInfo.source = "remoteservice";
                    }
                }
                else {
                    self.applianceUpdateInfo.source = "local";
                }
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ApplianceUpgradeSettingsModalController.prototype.save = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.saveApplianceUpdate(self.applianceUpdateInfo)
                .then(function (response) {
                self.close();
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); })
                .finally(function () { return d.resolve(); });
        };
        ApplianceUpgradeSettingsModalController.prototype.saveApplianceUpdate = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.setApplianceUpgrade, update);
        };
        ApplianceUpgradeSettingsModalController.prototype.getGettingStarted = function () {
            var self = this;
            return self.$http.post(self.Commands.data.initialSetup.gettingStarted, null);
        };
        ApplianceUpgradeSettingsModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ApplianceUpgradeSettingsModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        ApplianceUpgradeSettingsModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return ApplianceUpgradeSettingsModalController;
    }());
    asm.ApplianceUpgradeSettingsModalController = ApplianceUpgradeSettingsModalController;
    angular
        .module('app')
        .controller('ApplianceUpgradeSettingsModalController', ApplianceUpgradeSettingsModalController);
})(asm || (asm = {}));
//# sourceMappingURL=applianceUpgradeSettingsModal.js.map
