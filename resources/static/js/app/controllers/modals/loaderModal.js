var asm;
(function (asm) {
    var LoaderModalController = (function () {
        function LoaderModalController($scope, $rootScope, $window, $timeout, Modal, Dialog, $http, $translate, $q, $filter, loading, GlobalServices, Commands) {
            this.$scope = $scope;
            this.$rootScope = $rootScope;
            this.$window = $window;
            this.$timeout = $timeout;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$translate = $translate;
            this.$q = $q;
            this.$filter = $filter;
            this.loading = loading;
            this.GlobalServices = GlobalServices;
            this.Commands = Commands;
            var self = this;
            self.spinnerColor = '#007db8';
            $scope.modal.params.loaderMessage = $scope.modal.params.loaderMessage || 'Loading Content...';
            self.loaderMessage = $scope.modal.params.loaderMessage;
            //the watch was necessary to update the message below the loader svg
            $scope.$watch('modal.params.loaderMessage', function (newVal, oldVal) {
                self.loaderMessage = newVal;
            });
            //self.activate();
        }
        LoaderModalController.prototype.activate = function () {
            var self = this;
        };
        LoaderModalController.prototype.setMessage = function (message) {
            var self = this;
            //$( "#loader-restore-text" ).html( message );
            self.loaderMessage = message;
        };
        LoaderModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        LoaderModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        LoaderModalController.$inject = ['$scope', '$rootScope', '$window', '$timeout', 'Modal', 'Dialog', '$http', '$translate', '$q', '$filter', 'Loading', 'GlobalServices', 'Commands'];
        return LoaderModalController;
    }());
    asm.LoaderModalController = LoaderModalController;
    angular
        .module('app')
        .controller('LoaderModalController', LoaderModalController);
})(asm || (asm = {}));
//# sourceMappingURL=loaderModal.js.map
