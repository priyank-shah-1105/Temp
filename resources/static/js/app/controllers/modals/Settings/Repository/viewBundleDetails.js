var asm;
(function (asm) {
    var ViewBundleDetailsController = (function () {
        function ViewBundleDetailsController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, $rootScope) {
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
            this.$rootScope = $rootScope;
            this.viewModel = {};
            var self = this;
            this.viewData = {
                firmwarePackageId: '',
                firmwareBundleId: '',
                displayedData: {},
                viewType: ''
            };
            this.viewData.firmwarePackageId = $scope.modal.params.firmwarePackageId;
            this.viewData.firmwareBundleId = $scope.modal.params.firmwareBundleId;
            this.viewData.viewType = $scope.modal.params.viewType;
            self.refresh();
        }
        ViewBundleDetailsController.prototype.activate = function () {
        };
        ViewBundleDetailsController.prototype.refresh = function () {
            var self = this;
            var deferred = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(deferred.promise);
            //{ "requestObj":{ "firmwarePackageId":"a4685379-4124-4264-8cb7-79643b496b92", "firmwareBundleId":"a13d9cf6-9fe0-420b-a230-13d8c8a43176" } }
            var postData = {
                'firmwarePackageId': self.viewData.firmwarePackageId,
                'firmwareBundleId': self.viewData.firmwareBundleId
            };
            self.$http.post(self.Commands.data.firmwarepackages.getFirmwareBundleById, { requestObj: postData }).then(function (data) {
                self.viewModel = data.data.responseObj;
                self.viewData.displayedData = [].concat(self.viewModel);
                //self.viewData.displayedData = data.data.responseObj;
                deferred.resolve();
            }).catch(function (data) {
                //need to handle error
                deferred.resolve();
                //data object is always empty
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        ViewBundleDetailsController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ViewBundleDetailsController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope'];
        return ViewBundleDetailsController;
    }());
    asm.ViewBundleDetailsController = ViewBundleDetailsController;
    angular
        .module('app')
        .controller('ViewBundleDetailsController', ViewBundleDetailsController);
})(asm || (asm = {}));
//# sourceMappingURL=viewBundleDetails.js.map
