var asm;
(function (asm) {
    var ConfigureReleaseCertController = (function () {
        function ConfigureReleaseCertController(Modal, Dialog, $http, Loading, $q, $timeout, $scope, GlobalServices, $translate, Commands, $rootScope) {
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
            this.errors = new Array();
            var self = this;
        }
        ConfigureReleaseCertController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ConfigureReleaseCertController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ConfigureReleaseCertController.$inject = ['Modal', 'Dialog', '$http', 'Loading', '$q', '$timeout', '$scope', 'GlobalServices', '$translate', 'Commands', '$rootScope'];
        return ConfigureReleaseCertController;
    }());
    asm.ConfigureReleaseCertController = ConfigureReleaseCertController;
    angular
        .module('app')
        .controller('ConfigureReleaseCertController', ConfigureReleaseCertController);
})(asm || (asm = {}));
//# sourceMappingURL=configureReleaseCert.js.map
