var asm;
(function (asm) {
    var DeregisterWarningController = (function () {
        function DeregisterWarningController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            var self = this;
            self.connectionType = self.$scope.modal.params.connectionType;
            self.initialize();
        }
        DeregisterWarningController.prototype.initialize = function () {
            var self = this;
        };
        DeregisterWarningController.prototype.deregister = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.Loading(d.promise);
            self.deregisterVxRack()
                .then(function (data) {
                self.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data);
            })
                .finally(function () { return d.resolve(); });
        };
        DeregisterWarningController.prototype.deregisterVxRack = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.deregisterVxRack, { connectionType: self.connectionType });
        };
        DeregisterWarningController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        DeregisterWarningController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        DeregisterWarningController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return DeregisterWarningController;
    }());
    asm.DeregisterWarningController = DeregisterWarningController;
    angular
        .module('app')
        .controller('DeregisterWarningController', DeregisterWarningController);
})(asm || (asm = {}));
//# sourceMappingURL=deregisterWarning.js.map
