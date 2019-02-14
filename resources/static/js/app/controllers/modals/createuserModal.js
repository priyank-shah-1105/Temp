var asm;
(function (asm) {
    var CreateUserModalController = (function () {
        function CreateUserModalController($scope, Modal, Dialog, $http, GlobalServices, $rootScope, commands, loading, $q, $translate) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.GlobalServices = GlobalServices;
            this.$rootScope = $rootScope;
            this.commands = commands;
            this.loading = loading;
            this.$q = $q;
            this.$translate = $translate;
            this.errors = new Array();
            var self = this;
            self.newUser = {
                currentpassword: '',
                email: '',
                enabled: false,
                firstname: '',
                id: '',
                lastname: '',
                locale: '',
                password: '',
                phone: '',
                roleId: '',
                rolename: '',
                serverName: '',
                showdefaultpasswordprompt: false,
                state: '',
                username: '',
            };
            self.submitform = false;
            self.currentPassword = false;
            self.refresh();
        }
        CreateUserModalController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.getRoles().then(function (data) {
                self.roles = data.data.responseObj;
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            }).finally(function () { d.resolve(); });
        };
        CreateUserModalController.prototype.saveUser = function () {
            var self = this;
            angular.extend(self.newUser, {
                roleId: self.userRole.id,
                rolename: self.userRole.name
            });
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.postUser(self.newUser).then(function (data) {
                d.resolve();
                self.$scope.modal.close();
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            }).finally(function () { d.resolve(); });
        };
        CreateUserModalController.prototype.getRoles = function () {
            var self = this;
            return self.$http.post(self.commands.data.users.getRoles, {});
        };
        CreateUserModalController.prototype.postUser = function (user) {
            var self = this;
            return self.$http.post(self.commands.data.users.saveUser, user);
        };
        CreateUserModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        CreateUserModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', 'GlobalServices', '$rootScope', "Commands", "Loading", "$q", "$translate"];
        return CreateUserModalController;
    }());
    asm.CreateUserModalController = CreateUserModalController;
    angular
        .module('app')
        .controller('CreateUserModalController', CreateUserModalController);
})(asm || (asm = {}));
//# sourceMappingURL=createuserModal.js.map
