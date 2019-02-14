var asm;
(function (asm) {
    var AssignUsersModalController = (function () {
        function AssignUsersModalController($http, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, MessageBox) {
            this.$http = $http;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.MessageBox = MessageBox;
            var self = this;
            self.availableUsers = {};
            self.displayedAvailableUsers = {};
            self.includeUsers = false;
            if ($scope.modal.params.selectedUsers) {
                self.selectedUsers = $scope.modal.params.selectedUsers;
            }
            else {
                self.selectedUsers = [];
            }
            if ($scope.modal.params.roleId) {
                self.roleId = $scope.modal.params.roleId;
            }
            else {
                self.roleId = 'standard';
            }
            self.checkAllBox = false;
            self.activate();
        }
        AssignUsersModalController.prototype.activate = function () {
            var self = this;
            if (self.selectedUsers) {
                //TODO: need to confirm how to handle the filter; the below is taken from the old code; refer to ImportDirUsersModalController.ts
                //var filterObj = [{ field: 'roleId', op: '=', opTarget: [self.roleId] }];
                //self.availableUsers.criteriaObj().filterObj( filterObj );
                self.$http.post(self.Commands.data.users.getUsers, null)
                    .then(function (data) {
                    //remove any user that has already been selected (i.e. passed in)
                    var filtered_users = [];
                    angular.forEach(data.data.responseObj, function (u, idx) {
                        //TODO: change the way this is done with the use of jquery grep?
                        var match = $.grep(self.selectedUsers, function (s, i) { return u.id == s.id; });
                        if (match == undefined || match.length == 0) {
                            filtered_users.push(u);
                        } //if not matched, include this user in the list
                    });
                    //apply sort on username
                    var sortedData = _.sortBy(filtered_users, function (n) {
                        return n.username.toLowerCase();
                    });
                    self.availableUsers = angular.copy(sortedData);
                    self.displayedAvailableUsers = [].concat(self.availableUsers);
                }).catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                });
            }
        };
        AssignUsersModalController.prototype.checkAll = function () {
            var self = this;
            self.displayedAvailableUsers.forEach(function (user) {
                if (self.checkAllBox) {
                    user.isSelected = true;
                }
                else {
                    user.isSelected = false;
                }
            });
        };
        AssignUsersModalController.prototype.getSelectedUsers = function () {
            var self = this;
            return _.filter(self.availableUsers, { 'isSelected': true });
        };
        AssignUsersModalController.prototype.save = function () {
            var self = this;
            //pass list of selected users back to calling page
            self.includeUsers = true;
            self.$scope.modal.params.includeUsers = self.includeUsers;
            self.$scope.modal.params.selectedUsers = self.getSelectedUsers();
            self.$scope.modal.close();
        };
        AssignUsersModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        AssignUsersModalController.$inject = ['$http', '$scope', '$q', '$translate', 'Loading', 'Dialog',
            'Commands', 'GlobalServices', 'Messagebox'];
        return AssignUsersModalController;
    }());
    asm.AssignUsersModalController = AssignUsersModalController;
    angular
        .module('app')
        .controller('AssignUsersModalController', AssignUsersModalController);
})(asm || (asm = {}));
//# sourceMappingURL=assignUsersModal.js.map
