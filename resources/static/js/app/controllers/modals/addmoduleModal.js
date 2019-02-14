var asm;
(function (asm) {
    var AddModuleModalController = (function () {
        function AddModuleModalController($scope, Modal, Dialog, $http, $q, $timeout, Loading, GlobalServices, FileUploader, Commands) {
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
            var self = this;
            self.deferred = self.$q.defer();
            var uploader = $scope.uploader = new FileUploader({
                url: self.Commands.data.addonmodules.uploadAddOnModule,
            });
            self.$timeout(function () {
                document.getElementById('addonmodulefile').onchange = function (evt) {
                    var element = angular.element(evt.target)[0];
                    self.$timeout(function () { self.fileModel = element.files[0]; });
                };
            }, 500);
        }
        AddModuleModalController.prototype.save = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(self.deferred.promise);
            angular.merge(self.$scope.uploader, {
                onCompleteItem: function (fileItem, response, status, headers) {
                    self.pollingId = response.responseObj.id;
                    self.poll();
                },
                onErrorItem: function (fileItem, response, status, headers) {
                    //TODO: catch error, doesn't catch here
                    self.deferred.resolve();
                    self.GlobalServices.DisplayError(response, self.errors);
                }
            });
            self.$scope.uploader.uploadAll();
        };
        AddModuleModalController.prototype.poll = function () {
            var self = this;
            self.$http.post(self.Commands.data.addonmodules.saveAddOnModule, { id: self.pollingId }).then(function (data) {
                if (data.data.responseObj == 'COMPLETE') {
                    console.log('Module successfully added');
                    self.$scope.modal.close();
                    self.deferred.resolve();
                }
                else {
                    self.$timeout(function () {
                        self.poll();
                    }, 5000);
                }
            }).catch(function (data) {
                self.deferred.resolve();
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        AddModuleModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        AddModuleModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$q', '$timeout', 'Loading', 'GlobalServices', 'FileUploader', 'Commands'];
        return AddModuleModalController;
    }());
    asm.AddModuleModalController = AddModuleModalController;
    angular
        .module('app')
        .controller('AddModuleModalController', AddModuleModalController);
})(asm || (asm = {}));
//# sourceMappingURL=addmoduleModal.js.map
