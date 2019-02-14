var asm;
(function (asm) {
    var EditDeviceModalController = (function () {
        function EditDeviceModalController($scope, Modal, Dialog, $http, globalServices, $rootScope, commands, loading, $q, $translate, constants) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.globalServices = globalServices;
            this.$rootScope = $rootScope;
            this.commands = commands;
            this.loading = loading;
            this.$q = $q;
            this.$translate = $translate;
            this.constants = constants;
            this.options = {};
            var self = this;
            self.refresh();
        }
        EditDeviceModalController.prototype.refresh = function () {
            var self = this;
            self.type = self.$scope.modal.params.type;
            self.mode = self.$scope.modal.params.mode;
            self.user = self.$scope.modal.params.user || { enabled: true };
            angular.extend(self.options, {
                availableCMCUserRoles: self.constants.availableCMCUserRoles,
                availableLanRoles: self.constants.availableLanRoles,
                availableiDracUserRoles: self.constants.availableiDracUserRoles
            });
        };
        EditDeviceModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close(self.user);
        };
        EditDeviceModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        EditDeviceModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', 'GlobalServices',
            '$rootScope', "Commands", "Loading", "$q", "$translate", "constants"];
        return EditDeviceModalController;
    }());
    asm.EditDeviceModalController = EditDeviceModalController;
    angular
        .module('app')
        .controller('EditDeviceModalController', EditDeviceModalController);
})(asm || (asm = {}));
//# sourceMappingURL=editDeviceUserModal.js.map
