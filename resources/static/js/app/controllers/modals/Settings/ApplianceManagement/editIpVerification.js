var asm;
(function (asm) {
    var EditIpVerificationController = (function () {
        function EditIpVerificationController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            var self = this;
            self.initialize();
        }
        EditIpVerificationController.prototype.initialize = function () {
            var self = this;
            self.ipVerificationPorts = angular.copy(self.$scope.modal.params.ipVerificationPorts);
        };
        EditIpVerificationController.prototype.save = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.setIpVerification(self.ipVerificationPorts)
                .then(function () {
                self.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        EditIpVerificationController.prototype.setIpVerification = function (ipVerificationPorts) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.updateIpVerifyPorts, { ipVerificationPorts: ipVerificationPorts });
        };
        EditIpVerificationController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        EditIpVerificationController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        EditIpVerificationController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return EditIpVerificationController;
    }());
    asm.EditIpVerificationController = EditIpVerificationController;
    angular
        .module('app')
        .controller('EditIpVerificationController', EditIpVerificationController);
})(asm || (asm = {}));
//# sourceMappingURL=editIpVerification.js.map
