var asm;
(function (asm) {
    var ConfigureServersForAlertConnectorController = (function () {
        function ConfigureServersForAlertConnectorController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            var self = this;
            self.initialize();
        }
        ConfigureServersForAlertConnectorController.prototype.initialize = function () {
            var self = this;
        };
        ConfigureServersForAlertConnectorController.prototype.ok = function () {
            var self = this;
            self.close();
        };
        ConfigureServersForAlertConnectorController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ConfigureServersForAlertConnectorController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        ConfigureServersForAlertConnectorController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return ConfigureServersForAlertConnectorController;
    }());
    asm.ConfigureServersForAlertConnectorController = ConfigureServersForAlertConnectorController;
    angular
        .module('app')
        .controller('ConfigureServersForAlertConnectorController', ConfigureServersForAlertConnectorController);
})(asm || (asm = {}));
//# sourceMappingURL=configureServersForAlertConnector.js.map
