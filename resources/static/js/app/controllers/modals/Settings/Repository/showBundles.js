var asm;
(function (asm) {
    var ViewBundlesController = (function () {
        //public availableCredentials: any[];
        //public editMode: any;
        //public credentialId: any;
        //public credentialName: any;
        //public submitForm: boolean;
        function ViewBundlesController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.viewModel = {};
            var self = this;
            this.viewData = {
                availableCredentials: [
                    { "id": "notselected", "name": "Select a Credential Type..." },
                    { "id": "chassis", "name": "Chassis" },
                    { "id": "server", "name": "Server" },
                    { "id": "iom", "name": "Switch" },
                    { "id": "vcenter", "name": "vCenter" },
                    { "id": "scvmm", "name": "SCVMM" },
                    { "id": "storage", "name": "Storage" },
                    { "id": "em", "name": "Element Manager" }
                ],
                credentialId: $scope.modal.params.id,
                credentialName: '',
                submitForm: false,
                model: $scope.modal.params.model
            };
            // notselected = 'Select a Credential Type...'
            self.viewModel.typeId = 'notselected';
            self.activate();
        }
        ViewBundlesController.prototype.activate = function () {
            var self = this;
            if (self.viewData.credentialId) {
                var deferred = self.$q.defer();
                self.Loading(deferred.promise);
                // exsits - go get it
                var data;
                data = { 'id': self.viewData.credentialId };
                self.$http.post(self.Commands.data.credential.getCredentialById, { requestObj: data }).then(function (data) {
                    //self.$http.post('bogus', { requestObj: data }).then(function(data:any){
                    self.viewModel = data.data.responseObj;
                    self.viewData.credentialName = _.find(self.viewData.availableCredentials, function (o) { return o.id == self.viewModel.typeId; }).name;
                    deferred.resolve();
                })
                    .catch(function (data) {
                    //need to handle error
                    deferred.resolve();
                    //error is in data
                    self.GlobalServices.DisplayError(data.data);
                });
            }
            else {
                // new item, set some defaults
                self.viewModel.candelete = true;
                self.viewModel.canedit = true;
                self.viewModel.communityString = "public";
                self.viewModel.credentialProtocol = "ssh";
                self.viewModel.enableCertificateCheck = false;
                self.viewModel.numberOfDevices = "0";
                self.viewModel.username = "root";
            }
        };
        ViewBundlesController.prototype.doSave = function (formHasErrors) {
            var self = this;
            if (formHasErrors)
                return;
            var myScope = this.$scope;
            var deferred = self.$q.defer();
            self.Loading(deferred.promise);
            //this will do an update/create
            self.$http.post(self.Commands.data.credential.saveCredential, { requestObj: self.viewModel }).then(function (data) {
                //self.$http.post('bogus', { requestObj: self.viewModel }).then(function (data: any) {
                deferred.resolve();
                myScope.modal.close();
            }).catch(function (data) {
                //need to handle error
                deferred.resolve();
                //error is in data
                self.GlobalServices.DisplayError(data.data);
            });
        };
        ViewBundlesController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'loading', 'dialog', 'Commands', 'GlobalServices'];
        return ViewBundlesController;
    }());
    asm.ViewBundlesController = ViewBundlesController;
    angular
        .module('app')
        .controller('ViewBundlesController', ViewBundlesController);
})(asm || (asm = {}));
//# sourceMappingURL=showBundles.js.map
