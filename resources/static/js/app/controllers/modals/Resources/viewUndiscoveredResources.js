var asm;
(function (asm) {
    var ViewUndiscoveredResources = (function () {
        function ViewUndiscoveredResources($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, constants, $rootScope) {
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
        ViewUndiscoveredResources.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ViewUndiscoveredResources.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ViewUndiscoveredResources.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', 'constants', '$rootScope'];
        return ViewUndiscoveredResources;
    }());
    asm.ViewUndiscoveredResources = ViewUndiscoveredResources;
    angular
        .module("app")
        .controller("ViewUndiscoveredResources", ViewUndiscoveredResources);
})(asm || (asm = {}));
//# sourceMappingURL=viewUndiscoveredResources.js.map
