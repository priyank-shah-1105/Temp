var asm;
(function (asm) {
    var ApplianceUpdateController = (function () {
        function ApplianceUpdateController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.currentVersion = '';
            var self = this;
            self.initialize();
        }
        ApplianceUpdateController.prototype.initialize = function () {
            var self = this;
            self.currentVersion = self.$scope.modal.params.currentVersion;
        };
        ApplianceUpdateController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ApplianceUpdateController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        ApplianceUpdateController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return ApplianceUpdateController;
    }());
    asm.ApplianceUpdateController = ApplianceUpdateController;
    angular
        .module('app')
        .controller('ApplianceUpdateController', ApplianceUpdateController);
})(asm || (asm = {}));
//# sourceMappingURL=applianceUpdateModal.js.map
