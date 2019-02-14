var asm;
(function (asm) {
    var UploadCertificateModalController = (function () {
        function UploadCertificateModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, $rootScope, FileUploader, $window) {
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
            this.$window = $window;
            this.errors = new Array();
            var self = this;
            self.initialize();
        }
        UploadCertificateModalController.prototype.initialize = function () {
            var self = this;
            self.$scope.uploader = self.newUploader();
            //Wrap in a timeout to give screen time to render.  ////TODO: Replace with $onInit or similar.
            self.$timeout(function () {
                document.getElementById('idLocationFileUpload').onchange = function (evt) {
                    var element = angular.element(evt.target)[0];
                    var file = element.files[0];
                    self.file = element.value;
                    self.$timeout(function () { self.uploadCert(); }, 10); //Wrap in a timeout to trigger a digest cycle.
                };
            }, 500);
        };
        UploadCertificateModalController.prototype.newUploader = function () {
            var self = this;
            return new self.FileUploader({
                url: self.Commands.data.applianceManagement.uploadCertificate,
                removeAfterUpload: true
            });
        };
        UploadCertificateModalController.prototype.uploadCert = function () {
            var self = this;
            //    fd = new FormData(),
            //    //uploader = self.$scope.uploader = self.newUploader(),
            //    d = self.$q.defer();
            //self.GlobalServices.ClearErrors(self.errors);
            //self.Loading(d.promise);
            //fd.append('file', self.file);
            //self.save(fd)
            //    .catch(response => { self.GlobalServices.DisplayError(response.data, self.errors) })
            //    .finally(() => d.resolve());
            self.$scope.uploader.formData = [];
            //set error and success callbacks 
            var error = false;
            angular.extend(self.$scope.uploader, {
                onErrorItem: function (fileItem, response, status, headers) {
                    fileItem.isUploaded = false;
                    error = true;
                    self.GlobalServices.DisplayError(response.data, self.errors);
                    d.resolve();
                },
                onCompleteAll: function (criteriaObj, errorObj, responseObj, responseCode) {
                    //if (!error) {
                    //    deferred.resolve();
                    //}
                },
                onBeforeUploadItem: function (item) {
                    item.formData = angular.copy(item.uploader.formData);
                },
                onSuccessItem: function (item, response, status, headers) {
                    if (response.responseCode != 0) {
                        d.resolve();
                        //item.isUploaded = false;
                        error = true;
                        self.GlobalServices.DisplayError(response.errorObj, self.errors);
                    }
                    else {
                        d.resolve();
                        self.cert = response.responseObj;
                    }
                }
            });
            var d = self.$q.defer();
            self.Loading(d.promise);
            self.$scope.uploader.uploadAll();
        };
        UploadCertificateModalController.prototype.saveUpdates = function () {
            var self = this, d = self.$q.defer();
            var modal = self.Modal({
                title: self.$translate.instant('GENERIC_Warning'),
                modalSize: 'modal-md',
                templateUrl: 'views/settings/virtualappliancemanagement/licensingconfirmmodal.html',
                controller: 'UploadCertificateConfirmModalController as uploadCertificateConfirmModalController',
                params: {},
                onComplete: function () {
                    self.Loading(d.promise);
                    self.uploadCertificateConfirmation()
                        .then(function () {
                        self.close();
                        self.$timeout(function () { self.$window.location.href = "status.html#/status"; }, 500);
                    })
                        .catch(function (error) {
                        self.GlobalServices.DisplayError(error.errorObj, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
            });
            modal.modal.show();
        };
        UploadCertificateModalController.prototype.uploadCertificateConfirmation = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.uploadCertificateConfirmation, self.cert);
        };
        //save(form) {
        //    var self: UploadCertificateModalController = this,
        //        config: any = {
        //            directPost: true,
        //            headers: { 'Content-Type': undefined },
        //            transformRequest: angular.identity
        //        };
        //    return self.$http.post(self.Commands.data.applianceManagement.uploadCertificate, form, config);
        //}
        UploadCertificateModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        UploadCertificateModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        UploadCertificateModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope', 'FileUploader', '$window'];
        return UploadCertificateModalController;
    }());
    asm.UploadCertificateModalController = UploadCertificateModalController;
    angular
        .module('app')
        .controller('UploadCertificateModalController', UploadCertificateModalController);
})(asm || (asm = {}));
//# sourceMappingURL=uploadCertificate.js.map
