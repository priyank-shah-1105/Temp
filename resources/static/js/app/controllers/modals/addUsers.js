var asm;
(function (asm) {
    var AddUsersController = (function () {
        function AddUsersController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.errors = new Array();
            this.allSelected = false;
            var self = this;
            self.users = $scope.modal.params.users;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.getUsers().then(function (response) {
                self.userList = _.uniqBy(_.filter(response.data.responseObj, function (user) {
                    return !_.find(self.users, { id: user.id });
                }), "id");
                self.userListSafe = angular.copy(self.userList);
            }).catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            }).finally(function () {
                d.resolve();
            });
        }
        AddUsersController.prototype.toggleAll = function () {
            var self = this;
            angular.forEach(self.userListSafe, function (device) {
                device.selected = self.allSelected;
            });
        };
        AddUsersController.prototype.getUsers = function () {
            var self = this;
            return self.$http.post(self.Commands.data.users.getUsers, {
                criteriaObj: {
                    filterObj: [
                        {
                            field: "roleId",
                            op: "=",
                            opTarget: ["standard"]
                        }]
                }
            });
        };
        AddUsersController.prototype.save = function () {
            var self = this;
            self.close(_.union(_.filter(self.userListSafe, { selected: true }), self.users));
        };
        AddUsersController.prototype.close = function (params) {
            var self = this;
            self.$scope.modal.close(params);
        };
        AddUsersController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        AddUsersController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return AddUsersController;
    }());
    asm.AddUsersController = AddUsersController;
    angular
        .module('app')
        .controller('AddUsersController', AddUsersController);
})(asm || (asm = {}));
//# sourceMappingURL=addUsers.js.map
