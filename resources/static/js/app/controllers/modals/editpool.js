var asm;
(function (asm) {
    var EditPoolController = (function () {
        function EditPoolController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            //used in form
            this.id = '';
            this.type = '';
            this.identitycount = 0;
            this.autogenerate = false;
            this.errors = new Array();
            var self = this;
            self.id = $scope.modal.params.id;
            self.type = $scope.modal.params.type;
            self.activate();
        }
        EditPoolController.prototype.activate = function () {
            var self = this;
            if (self.id) {
                var deferred = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(deferred.promise);
                var results;
                var data;
                data = { 'id': self.id };
                self.$http.post(self.Commands.data.pools.getPoolById, { requestObj: data }).then(function (data) {
                    //self.$http.post('bogus', { requestObj: data }).then(function(data:any){
                    results = data.data.responseObj;
                    switch (self.type) {
                        case "mac":
                            self.autogenerate = results.virtualMACAutoGenerateOnDeploy;
                            break;
                        case "iqn":
                            self.autogenerate = results.virtualIQNAutoGenerateOnDeploy;
                            break;
                        case "wwpn":
                            self.autogenerate = results.virtualWWPNAutoGenerateOnDeploy;
                            break;
                        case "wwnn":
                            self.autogenerate = results.virtualWWNNAutoGenerateOnDeploy;
                            break;
                    }
                    deferred.resolve();
                })
                    .catch(function (response) {
                    //need to handle error
                    deferred.resolve();
                    //error is in data
                    self.GlobalServices.DisplayError(response.data, self.errors);
                });
            }
        };
        EditPoolController.prototype.doSave = function (formHasErrors) {
            var self = this;
            if (formHasErrors)
                return;
            var myScope = this.$scope;
            var deferred = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(deferred.promise);
            //this will do an update/create
            self.$http.post(self.Commands.data.pools.updatePool, {
                requestObj: {
                    'id': self.id,
                    'type': self.type,
                    'identitycount': self.identitycount,
                    'autogenerate': self.autogenerate
                }
            }).then(function (data) {
                //self.$http.post('bogus', { requestObj: self }).then(function (data: any) {
                deferred.resolve();
                myScope.modal.close();
            }).catch(function (response) {
                //need to handle error
                deferred.resolve();
                //error is in data
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        EditPoolController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        EditPoolController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return EditPoolController;
    }());
    asm.EditPoolController = EditPoolController;
    angular
        .module('app')
        .controller('EditPoolController', EditPoolController);
})(asm || (asm = {}));
//# sourceMappingURL=editpool.js.map
