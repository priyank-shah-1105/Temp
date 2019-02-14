var asm;
(function (asm) {
    var AddLicenseModalController = (function () {
        function AddLicenseModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, $rootScope, FileUploader) {
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
            this.FileUploader = FileUploader;
            this.settings = {};
            this.countries = new Array();
            this.licenseFile = {};
            this.setupData = {};
            this.license = {};
            this.fileIsUploaded = false;
            this.errors = [];
            var self = this;
            self.initialize();
            //Wrap in a timeout to give screen time to render.  ////TODO: Replace with $onInit or similar.
            self.$timeout(function () {
                document.getElementById('idLocationFileUpload').onchange = function (evt) {
                    var element = angular.element(evt.target)[0];
                    self.licenseFile = element.files[0];
                    //self.setupData.licenseData.licensefile = element.value;
                    self.$timeout(function () { self.verifyLicense(self.licenseFile); }, 10); //Wrap in a timeout to trigger a digest cycle.
                };
            }, 500);
            self.uploader = self.$scope.uploader = new FileUploader({
                url: self.Commands.data.applianceManagement.verifylicense
            });
        }
        //help url: ASM.urlConfig.help.LicenseManagement
        AddLicenseModalController.prototype.initialize = function () {
            var self = this;
            self.setupData = self.$scope.modal.params.license;
        };
        AddLicenseModalController.prototype.verifyLicense = function (file) {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            //using angular uploader
            self.$scope.uploader.formData = [];
            self.$scope.uploader.formData.push(file);
            //set error and success callbacks 
            angular.extend(self.uploader, {
                onErrorItem: function (fileItem, response, status, headers) {
                    self.GlobalServices.DisplayError(response.errorObj, self.errors);
                    d.reject();
                },
                onCompleteAll: function (criteriaObj, errorObj, responseObj, responseCode) {
                    d.resolve();
                },
                onBeforeUploadItem: function (item) {
                    item.formData = angular.copy(item.uploader.formData);
                },
                onSuccessItem: function (item, response, status, headers) {
                    if (response.errorObj) {
                        self.GlobalServices.DisplayError(response.errorObj, self.errors);
                    }
                    else {
                        self.setupData = response.responseObj;
                        self.fileIsUploaded = true;
                        console.log(response.responseObj);
                    }
                }
            });
            self.uploader.uploadAll();
            return d.promise;
        };
        //verifyLicense() {
        //    var self: AddLicenseModalController = this;
        //    var d = self.$q.defer();
        //    self.GlobalServices.ClearErrors(self.errors);
        //    self.Loading(d.promise);
        //    self.$http.post(self.Commands.data.applianceManagement.verifylicense, self.setupData.licenseData)
        //        .then((data: any) => {
        //            self.setupData.licenseData = data.data.responseObj;
        //            self.fileIsUploaded = true;
        //        })
        //        .catch(response => {
        //            self.fileIsUploaded = false;
        //            self.GlobalServices.DisplayError(response.data, self.errors);
        //        })
        //        .finally(() => d.resolve());
        //}
        AddLicenseModalController.prototype.updateLicense = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.initialSetup.updateLicenseData, self.setupData)
                .then(function () {
                d.resolve();
                self.close();
            }).catch(function (response) {
                d.resolve();
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
            return d;
        };
        AddLicenseModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        AddLicenseModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        AddLicenseModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q',
            '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices',
            '$rootScope', 'FileUploader'];
        return AddLicenseModalController;
    }());
    asm.AddLicenseModalController = AddLicenseModalController;
    angular
        .module('app')
        .controller('AddLicenseModalController', AddLicenseModalController);
})(asm || (asm = {}));
//# sourceMappingURL=addLicense.js.map
