var asm;
(function (asm) {
    var VxRackFlexAlertConnectorModalController = (function () {
        function VxRackFlexAlertConnectorModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            this.errors = [];
            this.formSubmitted = false;
            this.connectionTypeOptions = [];
            var self = this;
            self.settings = self.$scope.modal.params.settings;
            self.initialize();
        }
        VxRackFlexAlertConnectorModalController.prototype.initialize = function () {
            var self = this;
            self.connectionTypeOptions = [
                { id: "srs", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_ConnectionType_SRS") },
                { id: "phonehome", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_ConnectionType_PhoneHome") }
            ];
            self.connectionType = self.settings.connectionType;
        };
        VxRackFlexAlertConnectorModalController.prototype.save = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.applianceManagement.setVxRackSettingsRegister, self.settings)
                .then(function (data) {
                self.close();
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); })
                .finally(function () { return d.resolve(); });
        };
        VxRackFlexAlertConnectorModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        VxRackFlexAlertConnectorModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        VxRackFlexAlertConnectorModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return VxRackFlexAlertConnectorModalController;
    }());
    asm.VxRackFlexAlertConnectorModalController = VxRackFlexAlertConnectorModalController;
    angular
        .module('app')
        .controller('VxRackFlexAlertConnectorModalController', VxRackFlexAlertConnectorModalController);
})(asm || (asm = {}));
//# sourceMappingURL=vxRackFlexAlertConnectorModal.js.map
