var asm;
(function (asm) {
    var ViewBundlesController = (function () {
        function ViewBundlesController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, $rootScope) {
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
                displayedData: {},
                selectedPackageBundle: '',
                selectedCustomBundle: '',
                viewBundles: 'firmware'
            };
            this.viewData.firmwarePackageId = $scope.modal.params.id;
            self.refresh();
        }
        ViewBundlesController.prototype.activate = function () {
        };
        ViewBundlesController.prototype.refresh = function () {
            var self = this;
            self.$rootScope.$broadcast('clearErrors');
            //self.GlobalServices.ClearErrors(self.errors);
            var deferred = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(deferred.promise);
            var postData = { 'id': self.viewData.firmwarePackageId };
            self.$http.post(self.Commands.data.firmwarepackages.getFirmwarePackageById, { requestObj: postData }).then(function (data) {
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
        ViewBundlesController.prototype.onSelectedPackageBundle = function (selectedItem) {
            var self = this;
            self.viewData.selectedPackageBundle = selectedItem;
        };
        ViewBundlesController.prototype.onSelectedCustomBundle = function (selectedItem) {
            var self = this;
            self.viewData.selectedCustomBundle = selectedItem;
        };
        ViewBundlesController.prototype.onClickViewPackageBundleDetails = function (selectedItem) {
            var self = this;
            var theModal = this.Modal({
                title: selectedItem.bundleName,
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/viewbundledetails.html',
                controller: 'ViewBundleDetailsController as vbd',
                params: {
                    firmwarePackageId: self.viewData.firmwarePackageId,
                    firmwareBundleId: selectedItem.id,
                    viewType: self.viewData.viewBundles
                },
                onComplete: function (modalScope) {
                    self.refresh(false);
                }
            });
            theModal.modal.show();
        };
        ViewBundlesController.prototype.onClickViewCustomBundleDetails = function (selectedItem) {
            var self = this;
            var modal = this.Modal({
                title: self.$translate.instant('SETTINGS_Repositories_ViewCustomBundle'),
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/addeditviewbundle.html',
                controller: 'AddEditViewBundleModalController as c',
                params: {
                    repo: angular.copy(self.viewModel),
                    bundle: angular.copy(selectedItem),
                    type: 'view'
                },
                onComplete: function () {
                    self.refresh(false);
                }
            });
            modal.modal.show();
        };
        ViewBundlesController.prototype.onClickEditCustomBundleDetails = function (selectedItem) {
            var self = this;
            var modal = this.Modal({
                title: self.$translate.instant('SETTINGS_Repositories_EditCustomBundle'),
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/addeditviewbundle.html',
                controller: 'AddEditViewBundleModalController as c',
                params: {
                    repo: angular.copy(self.viewModel),
                    bundle: angular.copy(selectedItem),
                    type: 'edit'
                },
                onComplete: function () {
                    self.refresh(false);
                }
            });
            modal.modal.show();
        };
        ViewBundlesController.prototype.onClickDeleteCustomBundle = function (selectedItem) {
            var self = this;
            var deferred = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(deferred.promise);
            var postData = {
                'packageId': self.viewData.firmwarePackageId,
                'bundleId': selectedItem.id
            };
            //this will do an update/create
            self.$http.post(self.Commands.data.firmwarepackages.removeFirmwareBundle, { requestObj: postData }).then(function (data) {
                //self.$http.post('bogus', { requestObj: self.viewModel }).then(function (data: any) {
                deferred.resolve();
                self.refresh();
            }).catch(function (data) {
                //need to handle error
                deferred.resolve();
                //error is in data
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        ViewBundlesController.prototype.doSave = function (formHasErrors) {
            var self = this;
            if (formHasErrors)
                return;
            var myScope = this.$scope;
        };
        ViewBundlesController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ViewBundlesController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope'];
        return ViewBundlesController;
    }());
    asm.ViewBundlesController = ViewBundlesController;
    angular
        .module('app')
        .controller('ViewBundlesController', ViewBundlesController);
})(asm || (asm = {}));
//# sourceMappingURL=viewBundles.js.map
