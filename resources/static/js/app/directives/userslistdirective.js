var asm;
(function (asm) {
    'use strict';
    var UserslistController = (function () {
        function UserslistController(Modal, Dialog, $http, $timeout, $q, $compile, $translate, GlobalServices, Commands, $rootScope) {
            //this.jobs = [
            //    { isSelected: false, "id": 0, "name": 'Scheduled Job 30', "state": 'Scheduled', "starttime": 'Today at 12:00', "timeelapsed": 'none' },
            //    { isSelected: false, "id": 1, "name": 'Scheduled Job 26', "state": 'Scheduled', "starttime": 'Today at 12:00', "timeelapsed": 'none' },
            //    { isSelected: false, "id": 2, "name": 'Scheduled Job 27', "state": 'Scheduled', "starttime": 'Today at 12:00', "timeelapsed": 'none' }
            //];
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$compile = $compile;
            this.$translate = $translate;
            this.GlobalServices = GlobalServices;
            this.Commands = Commands;
            this.$rootScope = $rootScope;
            var self = this;
            self.buttonActive = false;
            self.checkAllBox = false;
            self.selectedStates = [];
            self.selectedUserObjects = [];
            self.activeTab = 'userinfo';
            self.getDirectoryGroups();
            self.refresh();
        }
        UserslistController.prototype.refresh = function () {
            var self = this;
            //Get All users
            self.$http.post(self.Commands.data.users.getUsers, {})
                .then(function (data) {
                self.results = data.data.responseObj;
                self.displayedresults = [].concat(self.results);
                //self.filterbyGroup();
                //console.log(self.displayedresults);
                if (self.selectedUsers().length == 0) {
                    self.buttonActive = false;
                }
                self.selectedDetail = self.results[0];
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        UserslistController.prototype.getDirectoryGroups = function () {
            var self = this;
            //Get directory groups
            self.$http.post(self.Commands.data.users.getDirectoryGroups, null)
                .then(function (data) {
                self.directoryGroups = data.data.responseObj;
                var item = { name: self.$translate.instant('SETTINGS_AllUsers'), id: 0 };
                self.directoryGroups.splice(0, 0, item);
                self.selectedGroup = self.directoryGroups[0];
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        UserslistController.prototype.filterbyGroup = function () {
            var self = this;
            if (self.selectedGroup.name == self.$translate.instant('SETTINGS_AllUsers')) {
                this.refresh();
            }
            else {
                self.$http.post(self.Commands.data.users.getDirectoryUsers, self.selectedGroup)
                    .then(function (data) {
                    self.results = data.data.responseObj;
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                });
            }
        };
        UserslistController.prototype.getUserDetails = function (user) {
            var self = this;
            self.$http.post(self.Commands.data.users.getUserById, user)
                .then(function (data) {
                self.selectedDetail = data.data.responseObj;
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        UserslistController.prototype.selectedUsers = function () {
            var self = this;
            return _.filter(self.results, { 'isSelected': true });
        };
        //TODO: Need pattern for check all boxes
        UserslistController.prototype.checkAll = function () {
            var self = this;
            self.results.forEach(function (user) {
                if (self.checkAllBox) {
                    user.isSelected = true;
                }
                else {
                    user.isSelected = false;
                }
            });
        };
        UserslistController.prototype.disableButtonActive = function () {
            var returnVal = true;
            var self = this;
            self.results.forEach(function (x) {
                if (x.isSelected) {
                    if (x.roleId.toLowerCase() === 'administrator' || x.state === self.$translate.instant('GENERIC_Disabled')) {
                        returnVal = false;
                    }
                }
            });
            return returnVal;
        };
        UserslistController.prototype.enableButtonActive = function () {
            var returnVal = true;
            var self = this;
            self.results.forEach(function (x) {
                if (x.isSelected && x.state === self.$translate.instant('GENERIC_Enabled')) {
                    returnVal = false;
                }
            });
            return returnVal;
        };
        UserslistController.prototype.deleteButtonActive = function () {
            var returnVal = true;
            var self = this;
            self.results.forEach(function (x) {
                if (x.isSelected &&
                    (x.username == self.$rootScope.ASM.CurrentUser.username || x.username == 'admin')) {
                    returnVal = false;
                }
            });
            return returnVal;
        };
        //delete user
        UserslistController.prototype.deleteUser = function () {
            var self = this;
            var userIds = _.map(self.selectedUsers(), 'id');
            var userNames = _.map(self.selectedUsers(), function (u) {
                return '<li>' + u.username + '</li>';
            });
            var Names = (userNames.toString()).replace(/[, ]+/g, " ").trim();
            //Confirmation Dialog box that fires delete on confirmation
            var confirm = self.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('SETTINGS_UserDeleteConfirm') +
                '<br> <ul class="selectedusers">' +
                Names +
                '</ul>'));
            confirm.then(function () {
                self.$http.post(self.Commands.data.users.deleteUser, userIds)
                    .then(function (data) {
                    self.refresh();
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                });
            });
        };
        UserslistController.prototype.disableUser = function () {
            var self = this;
            var userIds = _.map(self.selectedUsers(), 'id');
            self.$http.post(self.Commands.data.users.disableUser, userIds)
                .then(function (data) {
                self.refresh();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        UserslistController.prototype.enableUser = function () {
            var self = this;
            var userIds = _.map(self.selectedUsers(), 'id');
            self.$http.post(self.Commands.data.users.enableUser, userIds)
                .then(function (data) {
                self.refresh();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        UserslistController.prototype.createuser = function () {
            var self = this;
            var adduserModal = self.Modal({
                title: self.$translate.instant('SETTINGS_CreateUser'),
                onHelp: function () {
                    self.GlobalServices.showHelp('SettingsCreatingUser');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/createusermodal.html',
                controller: 'CreateUserModalController as createUser',
                params: {},
                onComplete: function () {
                    self.refresh();
                }
            });
            adduserModal.modal.show();
        };
        UserslistController.prototype.edituser = function () {
            var self = this;
            var modalUser = self.selectedUsers();
            var adduserModal = self.Modal({
                title: self.$translate.instant('SETTINGS_EditUser'),
                onHelp: function () {
                    self.GlobalServices.showHelp('SettingsUsersEditingUser');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/editusermodal.html',
                controller: 'EditUserModalController as editUser',
                params: {
                    selecteduser: angular.copy(modalUser)
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            adduserModal.modal.show();
        };
        UserslistController.prototype.importactivedirectoryusers = function () {
            var self = this;
            var importuserModal = self.Modal({
                title: self.$translate.instant('SETTINGS_ImportActiveDirectoryUsers'),
                onHelp: function () {
                    self.GlobalServices.showHelp('SettingsUsersImportingUsers');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/importactivedirectoryusers.html',
                controller: 'ImportDirUsersModalController as ImportUsersModal',
                params: {},
                onComplete: function () {
                    self.refresh();
                }
            });
            importuserModal.modal.show();
        };
        UserslistController.$inject = [
            'Modal', 'Dialog', '$http', '$timeout', '$q', '$compile', '$translate', 'GlobalServices', 'Commands', '$rootScope'
        ];
        return UserslistController;
    }());
    angular.module("app")
        .component("usersList", {
        templateUrl: "views/userslist.html",
        controller: UserslistController,
        controllerAs: "usersList",
        bindings: {
            errors: "="
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=userslistdirective.js.map
