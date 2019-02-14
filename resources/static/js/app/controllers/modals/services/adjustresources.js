var asm;
(function (asm) {
    var AdjustResourcesController = (function () {
        function AdjustResourcesController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.viewModel = {};
            this.serviceId = '';
            var self = this;
            self.serviceId = $scope.modal.params.serviceId,
                self.refresh();
        }
        AdjustResourcesController.prototype.activate = function () {
            var self = this;
        };
        AdjustResourcesController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.Loading(d.promise);
        };
        AdjustResourcesController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        AdjustResourcesController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return AdjustResourcesController;
    }());
    asm.AdjustResourcesController = AdjustResourcesController;
    angular
        .module("app")
        .controller("AdjustResourcesController", AdjustResourcesController);
})(asm || (asm = {}));
//# sourceMappingURL=adjustresources.js.map
