var asm;
(function (asm) {
    var EditDhcpSettingsModalController = (function () {
        function EditDhcpSettingsModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            this.ranges = {
                days: 31,
                hours: 24,
                minutes: 60,
                seconds: 60
            };
            this.errors = [];
            var self = this;
            self.initialize();
        }
        EditDhcpSettingsModalController.prototype.initialize = function () {
            var self = this;
            self.settings = self.$scope.modal.params.settings;
        };
        EditDhcpSettingsModalController.prototype.testProxy = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.setProxyTest(self.settings)
                .then(function (response) {
                //test successful
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); })
                .finally(function () { return d.resolve(); });
        };
        EditDhcpSettingsModalController.prototype.save = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.saveApplianceUpdate(self.settings)
                .then(function (response) {
                self.close();
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); })
                .finally(function () { return d.resolve(); });
        };
        EditDhcpSettingsModalController.prototype.saveApplianceUpdate = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.submitDhcpSettingsForm, update);
        };
        EditDhcpSettingsModalController.prototype.setProxyTest = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.initialSetup.testProxy, update);
        };
        EditDhcpSettingsModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        EditDhcpSettingsModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        EditDhcpSettingsModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return EditDhcpSettingsModalController;
    }());
    asm.EditDhcpSettingsModalController = EditDhcpSettingsModalController;
    angular
        .module('app')
        .controller('EditDhcpSettingsModalController', EditDhcpSettingsModalController);
})(asm || (asm = {}));
//# sourceMappingURL=dhcp.js.map
