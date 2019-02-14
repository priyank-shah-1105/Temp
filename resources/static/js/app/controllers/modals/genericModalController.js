var asm;
(function (asm) {
    var GenericModalController = (function () {
        function GenericModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
        GenericModalController.prototype.initialize = function () {
            var self = this;
            self.params = self.$scope.modal.params.params;
        };
        GenericModalController.prototype.ok = function () {
            var self = this;
            self.$scope.modal.close();
        };
        GenericModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        GenericModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return GenericModalController;
    }());
    asm.GenericModalController = GenericModalController;
    angular
        .module('app')
        .controller('GenericModalController', GenericModalController);
})(asm || (asm = {}));
//# sourceMappingURL=genericModalController.js.map
