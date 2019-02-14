var asm;
(function (asm) {
    var EditUserModalController = (function () {
        function EditUserModalController($scope, Modal, Dialog, $http, GlobalServices, $rootScope, Commands, $q, loading) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.GlobalServices = GlobalServices;
            this.$rootScope = $rootScope;
            this.Commands = Commands;
            this.$q = $q;
            this.loading = loading;
            this.submitform = false;
            this.roles = new Array();
            this.errors = new Array();
            var self = this;
            self.modalUser = $scope.modal.params.selecteduser[0];
            self.refresh();
        }
        EditUserModalController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.$q.all([
                self.getRoles()
                    .then(function (response) {
                    self.roles = response.data.responseObj;
                }),
                self.getUser()
                    .then(function (response) {
                    self.modalUser = response.data.responseObj;
                    self.modalUser.confirmationPassword = self.modalUser.password;
                })
            ])
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        EditUserModalController.prototype.saveUser = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.modalUser.rolename = _.find(self.roles, { id: self.modalUser.roleId }).name;
            self.$http.post(self.Commands.data.users.saveUser, self.modalUser)
                .then(function (data) {
                self.$scope.modal.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        EditUserModalController.prototype.getRoles = function () {
            var self = this;
            return self.$http.post(self.Commands.data.users.getRoles, []);
        };
        EditUserModalController.prototype.getUser = function () {
            var self = this;
            return self.$http.post(self.Commands.data.users.getUserById, self.modalUser);
        };
        EditUserModalController.prototype.cancel = function () {
            var self = this;
            self.$rootScope.$broadcast('clearErrors');
            self.$scope.modal.cancel();
        };
        EditUserModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', 'GlobalServices', '$rootScope', 'Commands', '$q', 'Loading'];
        return EditUserModalController;
    }());
    asm.EditUserModalController = EditUserModalController;
    angular
        .module('app')
        .controller('EditUserModalController', EditUserModalController);
})(asm || (asm = {}));
//# sourceMappingURL=edituserModal.js.map
