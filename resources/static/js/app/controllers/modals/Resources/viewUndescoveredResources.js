var asm;
(function (asm) {
    var ViewUndescoveredResources = (function () {
        function ViewUndescoveredResources($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, constants, $rootScope) {
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
            this.constants = constants;
            this.$rootScope = $rootScope;
            this.no = false;
            var self = this;
        }
        ViewUndescoveredResources.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ViewUndescoveredResources.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ViewUndescoveredResources.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', 'constants', '$rootScope'];
        return ViewUndescoveredResources;
    }());
    asm.ViewUndescoveredResources = ViewUndescoveredResources;
    angular
        .module("app")
        .controller("ViewUndescoveredResources", ViewUndescoveredResources);
})(asm || (asm = {}));
//# sourceMappingURL=viewUndescoveredResources.js.map
