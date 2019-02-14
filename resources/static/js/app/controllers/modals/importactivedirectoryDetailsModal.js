var asm;
(function (asm) {
    var ImportActiveDirDetailsModalController = (function () {
        function ImportActiveDirDetailsModalController($scope, Modal, Dialog, $http, GlobalServices, $rootScope, Commands, loading, $q) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.GlobalServices = GlobalServices;
            this.$rootScope = $rootScope;
            this.Commands = Commands;
            this.loading = loading;
            this.$q = $q;
            this.errors = new Array();
            var self = this;
            self.selectedItem = $scope.modal.params.selecteditem;
            self.isuser = $scope.modal.params.isuser;
            self.groupdetails = '';
            self.displayedgroupdetails = '';
            self.getdetails();
        }
        ImportActiveDirDetailsModalController.prototype.getdetails = function () {
            var self = this, d = self.$q.defer();
            //console.log('self.isuser:  ' + self.isuser);
            //console.log('self.selectedItem:  ' + JSON.stringify(self.selectedItem));
            //note:  the call below to getGroupDetails is only made for groups, not users
            if (!self.isuser) {
                self.GlobalServices.ClearErrors(self.errors);
                self.loading(d.promise);
                //JB TEMP:  might have to just pass in id here?  might just be the formatting of the object passed in
                //self.$http.post(self.Commands.data.users.getGroupDetails, { requestObj: self.selectedItem })
                self.$http.post(self.Commands.data.users.getGroupDetails, self.selectedItem)
                    .then(function (data) {
                    self.groupdetails = data.data.responseObj;
                    self.displayedgroupdetails = [].concat(self.groupdetails);
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            }
        };
        ;
        ImportActiveDirDetailsModalController.prototype.cancel = function () {
            var self = this;
            self.$rootScope.$broadcast('clearErrors');
            self.$scope.modal.cancel();
        };
        ImportActiveDirDetailsModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', 'GlobalServices', '$rootScope', 'Commands', 'Loading', '$q'];
        return ImportActiveDirDetailsModalController;
    }());
    asm.ImportActiveDirDetailsModalController = ImportActiveDirDetailsModalController;
    angular
        .module('app')
        .controller('ImportActiveDirDetailsModalController', ImportActiveDirDetailsModalController);
})(asm || (asm = {}));
//# sourceMappingURL=importactivedirectoryDetailsModal.js.map
