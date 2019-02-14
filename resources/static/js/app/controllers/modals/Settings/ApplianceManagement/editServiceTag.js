var asm;
(function (asm) {
    var EditServiceTagController = (function () {
        function EditServiceTagController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, $rootScope) {
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
            this.$rootScope = $rootScope;
            this.serviceTag = '';
            this.errors = [];
            var self = this;
            self.initialize();
        }
        EditServiceTagController.prototype.initialize = function () {
            var self = this;
            self.serviceTag = self.$scope.modal.params.serviceTag ? self.$scope.modal.params.serviceTag : '';
        };
        EditServiceTagController.prototype.updateServiceTag = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.applianceManagement.updateServiceTag, self.serviceTag)
                .then(function () {
                self.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
            return d;
        };
        EditServiceTagController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        EditServiceTagController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        EditServiceTagController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope'];
        return EditServiceTagController;
    }());
    asm.EditServiceTagController = EditServiceTagController;
    angular
        .module('app')
        .controller('EditServiceTagController', EditServiceTagController);
})(asm || (asm = {}));
//# sourceMappingURL=editServiceTag.js.map
