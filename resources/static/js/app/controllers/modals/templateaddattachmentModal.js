var asm;
(function (asm) {
    var AddAttachmentModalController = (function () {
        function AddAttachmentModalController($scope, Modal, Dialog, $http, $q, $timeout, Loading, GlobalServices, FileUploader, Commands) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$q = $q;
            this.$timeout = $timeout;
            this.Loading = Loading;
            this.GlobalServices = GlobalServices;
            this.FileUploader = FileUploader;
            this.Commands = Commands;
            this.errors = new Array();
            this.template = {
                id: ''
            };
            this.repo = { packageSource: 'import' };
            //this.deferred = this.$q.defer();
            var self = this;
            self.template.id = $scope.modal.params.templateId;
            $scope.uploader = new FileUploader({
                url: 'templates/addattachment',
                filters: [
                    {
                        name: 'fileSize', fn: function (file) {
                            return !(self.tooBigError = file.size > 52428800);
                        }
                    }
                ]
            });
            self.$timeout(function () {
                document.getElementById('templateattachmentfile').onchange = function (evt) {
                    var element = angular.element(evt.target)[0];
                    self.$timeout(function () { self.repoFile = element.files[0]; }, 10); //Wrap in a timeout to trigger a digest cycle.
                };
            }, 500);
        }
        AddAttachmentModalController.prototype.save = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            var deferred = self.$q.defer();
            self.Loading(deferred.promise);
            //using angular uploader
            self.$scope.uploader.formData = [];
            self.$scope.uploader.formData.push(self.template);
            //set error and success callbacks 
            angular.merge(self.$scope.uploader, {
                onErrorItem: function (fileItem, response, status, headers) {
                    //self.GlobalServices.DisplayError();
                    deferred.resolve();
                    fileItem.isUploaded = false;
                },
                onCompleteAll: function (criteriaObj, errorObj, responseObj, responseCode) {
                    deferred.resolve();
                },
                onCompleteItem: function (item, response, status, headers) {
                    deferred.resolve();
                    if (response.responseCode === 0) {
                        self.$scope.modal.close();
                    }
                    else if (response.errorObj) {
                        item.isUploaded = false;
                        self.GlobalServices.DisplayError(response.errorObj, self.errors);
                    }
                },
                onBeforeUploadItem: function (item) {
                    item.formData = angular.copy(item.uploader.formData);
                }
            });
            if (self.tooBigError) {
                deferred.resolve();
            }
            else {
                self.$scope.uploader.uploadAll();
            }
        };
        AddAttachmentModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        AddAttachmentModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        AddAttachmentModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$q', '$timeout', 'Loading', 'GlobalServices', 'FileUploader', 'Commands'];
        return AddAttachmentModalController;
    }());
    asm.AddAttachmentModalController = AddAttachmentModalController;
    angular
        .module('app')
        .controller('AddAttachmentModalController', AddAttachmentModalController);
})(asm || (asm = {}));
//# sourceMappingURL=templateaddattachmentModal.js.map
