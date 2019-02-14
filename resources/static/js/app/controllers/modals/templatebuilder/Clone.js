var asm;
(function (asm) {
    var CloneController = (function () {
        function CloneController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            this.errors = new Array();
            var self = this;
            self.initialize();
        }
        CloneController.prototype.initialize = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.devices.getAvailableCloneDeviceList, null)
                .then(function (data) {
                self.devices = data.data.responseObj;
                self.displayedData = angular.copy(self.devices);
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        CloneController.prototype.save = function () {
            var self = this, d = self.$q.defer();
            self.Loading(d.promise);
            self.getReferenceComponent(self.GlobalServices.NewGuid(), self.$scope.modal.params.newComponentId, self.selectedDevice.id)
                .then(function (data) {
                self.close(data.data.responseObj);
            })
                .catch(function (error) {
                self.GlobalServices.DisplayError(error.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        CloneController.prototype.getReferenceComponent = function (id, componentId, referenceId) {
            var self = this;
            return self.$http.post(self.Commands.data.templates.getReferenceComponent, { id: id, componentId: componentId, referenceId: referenceId });
        };
        CloneController.prototype.onSelectedDevice = function (selectedDevice) {
            var self = this;
            self.selectedDevice = selectedDevice;
        };
        CloneController.prototype.close = function (data) {
            var self = this;
            self.$scope.modal.close(data);
        };
        CloneController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        CloneController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return CloneController;
    }());
    asm.CloneController = CloneController;
    angular
        .module('app')
        .controller('CloneController', CloneController);
})(asm || (asm = {}));
//# sourceMappingURL=Clone.js.map
