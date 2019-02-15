var asm;
(function (asm) {
    var EditProxySettingsModalController = (function () {
        function EditProxySettingsModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            this.settings = {};
            this.errors = [];
            var self = this;
            console.log("ProxySettings.js");
            self.initialize();
        }
        //help url: ASM.urlConfig.help.EditingProxySettings
        EditProxySettingsModalController.prototype.initialize = function () {
            console.log("ProxySettings.js");
            var self = this;
            self.settings = self.$scope.modal.params.httpProxySettings;
        };
        EditProxySettingsModalController.prototype.testProxy = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.setProxyTest(self.settings)
                .then(function (response) {
                //test successful
                self.Dialog(self.$translate.instant('GENERIC_Success'), self.$translate.instant('SETTINGS_TestSuccessful'), true);
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        EditProxySettingsModalController.prototype.save = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.saveApplianceUpdate(self.settings).then(function (response) {
                d.resolve();
                self.close();
            }).catch(function (response) { d.resolve(); self.GlobalServices.DisplayError(response.data, self.errors); });
        };
        EditProxySettingsModalController.prototype.saveApplianceUpdate = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.submitProxyInfoForm, update);
        };
        EditProxySettingsModalController.prototype.setProxyTest = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.initialSetup.testProxy, update);
        };
        EditProxySettingsModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        EditProxySettingsModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        EditProxySettingsModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return EditProxySettingsModalController;
    }());
    asm.EditProxySettingsModalController = EditProxySettingsModalController;
    angular
        .module('app')
        .controller('EditProxySettingsModalController', EditProxySettingsModalController);
})(asm || (asm = {}));
//# sourceMappingURL=proxySettings.js.map
