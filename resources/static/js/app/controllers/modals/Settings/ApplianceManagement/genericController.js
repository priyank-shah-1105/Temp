var asm;
(function (asm) {
    var UpdateApplianceModalController = (function () {
        function UpdateApplianceModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            self.initialize();
        }
        //help url: ASM.urlConfig.help.EditingProxySettings
        UpdateApplianceModalController.prototype.initialize = function () {
            var self = this;
            self.settings = self.$scope.modal.params.settings;
        };
        UpdateApplianceModalController.prototype.testProxy = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.setProxyTest(self.settings).then(function (response) {
                //test successful
                d.resolve();
            }).catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); });
        };
        UpdateApplianceModalController.prototype.save = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.saveApplianceUpdate(self.settings)
                .then(function (response) {
                self.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        UpdateApplianceModalController.prototype.saveApplianceUpdate = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.submitDhcpSettingsForm, update);
        };
        UpdateApplianceModalController.prototype.setProxyTest = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.initialSetup.testProxy, update);
        };
        UpdateApplianceModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        UpdateApplianceModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        UpdateApplianceModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return UpdateApplianceModalController;
    }());
    asm.UpdateApplianceModalController = UpdateApplianceModalController;
    angular
        .module('app')
        .controller('UpdateApplianceModalController', UpdateApplianceModalController);
})(asm || (asm = {}));
//# sourceMappingURL=genericController.js.map
