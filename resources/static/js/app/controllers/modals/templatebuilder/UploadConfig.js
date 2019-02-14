var asm;
(function (asm) {
    var UploadConfigController = (function () {
        function UploadConfigController(Modal, $scope, Dialog, $http, Loading, $q, $timeout, globalServices, FileUploader, Commands) {
            this.Modal = Modal;
            this.$scope = $scope;
            this.Dialog = Dialog;
            this.$http = $http;
            this.Loading = Loading;
            this.$q = $q;
            this.$timeout = $timeout;
            this.globalServices = globalServices;
            this.FileUploader = FileUploader;
            this.Commands = Commands;
            this.errors = new Array();
            var self = this;
            var uploader = $scope.uploader = new FileUploader({
                url: self.Commands.data.templates.uploadConfigFile,
            });
        }
        UploadConfigController.prototype.isFileSelected = function () {
            return !!document.getElementById("configFile").value;
        };
        UploadConfigController.prototype.uploadConfigFile = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            //using angular uploader
            //set error and success callbacks 
            angular.extend(self.$scope.uploader, {
                onErrorItem: function (fileItem, response, status, headers) {
                    d.resolve();
                    self.globalServices.DisplayError(response);
                },
                onSuccessItem: function (item, response, status, headers) {
                    self.getParsedConfig(response.responseObj);
                    d.resolve();
                }
            });
            self.$scope.uploader.uploadAll();
        };
        UploadConfigController.prototype.getParsedConfig = function (referenceId) {
            var self = this;
            var d = self.$q.defer();
            self.Loading(d.promise);
            self.getParsedConfigFile(self.globalServices.NewGuid(), referenceId, self.$scope.modal.params.componentid)
                .then(function (response) {
                self.close(response.data.responseObj);
            })
                .catch(function (error) {
                self.globalServices.DisplayError(error.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        UploadConfigController.prototype.getParsedConfigFile = function (newId, refrenceId, componentId) {
            var self = this;
            return self.$http.post(self.Commands.data.templates.getParsedConfigFile, {
                id: newId,
                referenceId: refrenceId,
                componentId: componentId
            });
        };
        UploadConfigController.prototype.close = function (component) {
            var self = this;
            self.$scope.modal.close(component);
        };
        UploadConfigController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        UploadConfigController.$inject = ['Modal', '$scope', 'Dialog', '$http', 'Loading', '$q', '$timeout', 'GlobalServices', 'FileUploader', "Commands"];
        return UploadConfigController;
    }());
    asm.UploadConfigController = UploadConfigController;
    angular
        .module('app')
        .controller('UploadConfigController', UploadConfigController);
})(asm || (asm = {}));
//# sourceMappingURL=UploadConfig.js.map
