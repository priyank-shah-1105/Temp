var asm;
(function (asm) {
    var NewServerPoolWizardController = (function () {
        function NewServerPoolWizardController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.commands = commands;
            this.GlobalServices = GlobalServices;
            this.pool = {
                canDelete: false,
                canEdit: false,
                createdby: "",
                createDate: moment(),
                description: "",
                id: null,
                isSelected: false,
                name: "",
                servers: [],
                users: [],
            };
            this.errors = new Array();
            this.allServersSelected = false;
            this.serverSelected = false;
            this.calledFromDiscoverWizard = false;
            this.editMode = false;
            this.allUsersSelected = false;
            this.smartTableOptions = {
                itemsPerPage: 20,
                pagesShown: 7
            };
            var self = this;
            self.calledFromDiscoverWizard = self.$scope.modal.params.calledFromDiscoverWizard;
            self.pool = self.$scope.modal.params.pool || self.pool;
            self.editMode = !!self.$scope.modal.params.editMode;
            self.refresh();
        }
        NewServerPoolWizardController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.getDeviceList().then(function (response) {
                    self.devices = response.data.responseObj;
                    self.safeSource = angular.copy(response.data.responseObj);
                }),
                self.getUsers().then(function (response) {
                    self.users = response.data.responseObj;
                    self.safeUsers = angular.copy(response.data.responseObj);
                }),
                self.getCurrentUser().then(function (response) {
                    self.pool.createdby = response.data.responseObj.username;
                })
            ]).then(function () {
                //if editing a pool, loop through servers and users and mark matching ones as being selected already
                if (self.editMode) {
                    self.safeUsers = _.map(self.safeUsers, function (user) {
                        return angular.extend(user, {
                            selected: !!_.find(self.pool.users, function (poolUser) {
                                return poolUser.id === user.id;
                            })
                        });
                    });
                    self.safeSource = _.map(self.safeSource, function (device) {
                        return angular.extend(device, {
                            selected: !!_.find(self.pool.servers, function (poolDevice) {
                                return poolDevice.id === device.id;
                            })
                        });
                    });
                }
            }).catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); })
                .finally(function () { d.resolve(); });
        };
        NewServerPoolWizardController.prototype.validatePoolInfo = function () {
            var self = this;
            self.forms.poolInfoForm._submitted = true;
            return self.$q(function (resolve, reject) {
                self.forms.poolInfoForm.$valid ? resolve() : reject();
            });
        };
        NewServerPoolWizardController.prototype.selectAllServers = function () {
            var self = this;
            var allSelected = self.pool.servers.length === self.safeSource.length;
            self.selectAll(self.safeSource, !allSelected);
            if (!allSelected) {
                self.pool.servers = angular.copy(self.safeSource);
            }
            else {
                self.pool.servers = [];
            }
        };
        NewServerPoolWizardController.prototype.selectAllUsers = function () {
            var self = this;
            var allSelected = self.pool.users.length === self.safeUsers.length;
            self.selectAll(self.safeUsers, !allSelected);
            if (!allSelected) {
                self.pool.users = angular.copy(self.safeUsers);
            }
            else {
                self.pool.users = [];
            }
        };
        NewServerPoolWizardController.prototype.selectAll = function (safeSource, selectAll) {
            angular.forEach(safeSource, function (device) {
                device.selected = selectAll;
            });
        };
        NewServerPoolWizardController.prototype.finishWizard = function () {
            var self = this, d = self.$q.defer();
            self.Loading(d.promise);
            self.GlobalServices.ClearErrors(self.errors);
            self.saveServerPool(self.pool).then(function (data) {
                self.objectId = data.data.responseObj.id;
                d.resolve();
                self.close();
            }).catch(function (response) {
                d.resolve();
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        NewServerPoolWizardController.prototype.updateServers = function () {
            var self = this;
            self.pool.servers = _.filter(self.safeSource, { selected: true });
        };
        NewServerPoolWizardController.prototype.updateUsers = function () {
            var self = this;
            self.pool.users = _.filter(self.safeUsers, { selected: true });
        };
        NewServerPoolWizardController.prototype.getUsers = function () {
            var self = this;
            return self.$http.post(self.commands.data.users.getUsers, {
                requestObj: [],
                criteriaObj: {
                    filterObj: [
                        {
                            field: "roleId",
                            op: "<>",
                            opTarget: ["readonly"]
                        }]
                }
            });
        };
        NewServerPoolWizardController.prototype.getDeviceList = function () {
            var self = this;
            return self.$http.post(self.commands.data.devices.getDeviceList, {
                requestObj: [],
                criteriaObj: {
                    filterObj: [
                        {
                            field: "deviceType",
                            op: "=",
                            opTarget: ["RackServer", "BladeServer", "Server", "FXServer"]
                        }]
                }
            });
        };
        NewServerPoolWizardController.prototype.getCurrentUser = function () {
            var self = this;
            return self.$http.post(self.commands.data.users.getCurrentUser, null);
        };
        NewServerPoolWizardController.prototype.saveServerPool = function (pool) {
            var self = this;
            return self.$http.post(self.commands.data.serverpools.saveServerPool, pool);
        };
        NewServerPoolWizardController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        NewServerPoolWizardController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        NewServerPoolWizardController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return NewServerPoolWizardController;
    }());
    asm.NewServerPoolWizardController = NewServerPoolWizardController;
    angular
        .module("app")
        .controller("NewServerPoolWizardController", NewServerPoolWizardController);
})(asm || (asm = {}));
//# sourceMappingURL=newServerPoolWizard.js.map
