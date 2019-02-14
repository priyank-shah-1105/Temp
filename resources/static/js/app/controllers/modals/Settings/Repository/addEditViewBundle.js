var asm;
(function (asm) {
    /*
    View-
    Parameters: Bundle, type

    Edit-
    Parameters: Bundle, repo, type

    Create-
    Parameters: Repo, type
    */
    var AddEditViewBundleModalController = (function () {
        function AddEditViewBundleModalController(modal, $http, $timeout, $scope, $q, $translate, loading, dialog, Commands, GlobalServices, constants, FileUploader) {
            this.modal = modal;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.loading = loading;
            this.dialog = dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            this.FileUploader = FileUploader;
            this.bundle = {};
            this.errors = new Array();
            var self = this;
            self.initialize();
            var uploader = $scope.uploader = new FileUploader({
                url: self.Commands.data.firmwarepackages.saveFirmwareBundle,
            });
            self.$timeout(function () {
                document.getElementById('firmwarepackagefile').onchange = function (evt) {
                    var element = angular.element(evt.target)[0];
                    self.$timeout(function () { self.file = element.files[0]; }, 10); //Wrap in a timeout to trigger a digest cycle.
                };
            }, 500);
        }
        AddEditViewBundleModalController.prototype.initialize = function () {
            var self = this;
            self.repo = self.$scope.modal.params.repo;
            self.type = self.$scope.modal.params.type;
            if (self.$scope.modal.params.bundle && self.$scope.modal.params.bundle.firmwarecomponents) {
                delete self.$scope.modal.params.bundle.firmwarecomponents[0].id;
                self.bundle = angular.extend({}, self.bundle, self.$scope.modal.params.bundle, self.$scope.modal.params.bundle.firmwarecomponents[0], { bundleId: self.$scope.modal.params.bundle.id });
            }
            angular.extend(self.bundle, {
                packageId: self.repo.id
            });
            //send in repo
            self.getBundleDevices()
                .then(function (response) {
                self.bundleDevices = angular.copy(response.data.responseObj);
                if (self.type !== 'view') {
                    self.bundleDeviceChanged(true);
                }
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); });
        };
        AddEditViewBundleModalController.prototype.bundleDeviceChanged = function (preventClearOfDeviceModel) {
            var self = this;
            if (!preventClearOfDeviceModel) {
                self.bundle.deviceModel = undefined;
            }
            var match = _.find(self.bundleDevices, { id: self.bundle.deviceType });
            self.bundleChildrenOptions = angular.isDefined(match) ? angular.copy(match.children) : [];
        };
        AddEditViewBundleModalController.prototype.save = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            if (self.bundle.deviceType.toLowerCase() === 'storage' || !self.file) {
                //do old fashioned post (not angular-upload) if no file to upload
                self.postFormNoFile(d, self.bundle);
            }
            else {
                //using angular uploader
                self.$scope.uploader.formData = [];
                self.$scope.uploader.formData.push(self.bundle);
                //set error and success callbacks 
                angular.extend(self.$scope.uploader, {
                    onErrorItem: function (fileItem, response, status, headers) {
                        d.resolve();
                        self.GlobalServices.DisplayError(response.data, self.errors);
                    },
                    onCompleteAll: function (fileItem, response, status, headers) {
                        self.close(d);
                    },
                    onBeforeUploadItem: function (item) {
                        item.formData = angular.copy(item.uploader.formData);
                    }
                });
                self.$scope.uploader.uploadAll();
            }
        };
        AddEditViewBundleModalController.prototype.postFormNoFile = function (d, bundle) {
            var self = this;
            var fd = new FormData();
            fd.append('packageId', bundle.packageId);
            //fd.append('bundleId', bundle.bundleId);
            fd.append('bundleId', bundle.bundleId == undefined ? '' : bundle.bundleId);
            fd.append('bundleName', bundle.bundleName);
            fd.append('bundleDescription', bundle.bundleDescription);
            fd.append('deviceType', bundle.deviceType);
            fd.append('deviceModel', bundle.deviceModel);
            fd.append('bundleVersion', bundle.bundleVersion);
            bundle.deviceType.toLowerCase() === 'switch' && fd.append('criticality', bundle.criticality);
            fd.append('file', "");
            self.saveBundle(fd).then(function () {
                self.close(d);
            }).catch(function (data) {
                d.resolve();
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        AddEditViewBundleModalController.prototype.getBundleDevices = function () {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.getFirmwareBundleDevices, {});
        };
        AddEditViewBundleModalController.prototype.saveBundle = function (form) {
            var self = this;
            var config = {
                directPost: true,
                headers: { 'Content-Type': undefined },
                transformRequest: angular.identity
            };
            return self.$http.post(self.Commands.data.firmwarepackages.saveFirmwareBundle, form, config);
        };
        AddEditViewBundleModalController.prototype.setProxyTest = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.initialSetup.testProxy, update);
        };
        AddEditViewBundleModalController.prototype.close = function (d) {
            var self = this;
            d && d.resolve();
            self.$scope.modal.close();
        };
        AddEditViewBundleModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        AddEditViewBundleModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices', 'constants', 'FileUploader'];
        return AddEditViewBundleModalController;
    }());
    asm.AddEditViewBundleModalController = AddEditViewBundleModalController;
    angular
        .module('app')
        .controller('AddEditViewBundleModalController', AddEditViewBundleModalController);
})(asm || (asm = {}));
//# sourceMappingURL=addEditViewBundle.js.map
