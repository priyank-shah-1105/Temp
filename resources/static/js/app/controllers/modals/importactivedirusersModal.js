var asm;
(function (asm) {
    var ImportDirUsersModalController = (function () {
        //public showFindUserError: boolean;
        function ImportDirUsersModalController($scope, Modal, Dialog, $http, GlobalServices, $rootScope, $q, Loading, $translate, Commands) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.GlobalServices = GlobalServices;
            this.$rootScope = $rootScope;
            this.$q = $q;
            this.Loading = Loading;
            this.$translate = $translate;
            this.Commands = Commands;
            this.importDirectoryList = [];
            this.directoryUsers = [];
            this.importRoles = [];
            this.results = [];
            this.displayedresults = [];
            this.rightcol = [];
            this.leftcol = [];
            this.displayedleftcol = [];
            this.errors = new Array();
            var self = this;
            self.viewBy = 'usersgroups';
            //this.showFindUserError = false;
            //self.getImportDirectoryList();
            self.getImportRoles();
            self.jobRequest = { criteriaObj: { filterObj: [{ field: '', op: '', opTarget: '' }] } };
            self.updateSearch();
            self.activate();
        }
        ImportDirUsersModalController.prototype.activate = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.getDirectories()
                    .then(function (data) {
                    self.directories = data.data.responseObj;
                    var item = {
                        id: 'Select',
                        serverName: self.$translate.instant('SETTINGS_ActiveDirectorySelectARoleAlt')
                    };
                    self.directories.splice(0, 0, item);
                    self.selectedDirectory = self.directories[0].serverName;
                }),
                self.getImportRoles()
                    .then(function (data) {
                    self.importRoles = data.data.responseObj;
                    self.importRoles.unshift({
                        id: 'Select Role',
                        name: self.$translate.instant('SETTINGS_ActiveDirectorySelectARole')
                    });
                    self.selectedRoleId = self.importRoles[0].id;
                }),
            ])
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        //get directories drop down list
        ImportDirUsersModalController.prototype.getDirectories = function () {
            var self = this;
            return self.$http.post(self.Commands.data.users.getDirectoryList, null);
        };
        //get import roles dropdown list
        ImportDirUsersModalController.prototype.getImportRoles = function () {
            var self = this;
            return self.$http.post(self.Commands.data.users.getImportRoles, null);
        };
        ImportDirUsersModalController.prototype.getImportDirectoryList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.users.getImportDirectoryList, null);
        };
        ImportDirUsersModalController.prototype.updateSearch = function () {
            var self = this;
            var keyword = self.searchText;
            if (keyword && keyword.length > 2) {
                if (searchtimeout) {
                    clearTimeout(searchtimeout);
                }
                var searchtimeout = setTimeout(function () {
                    self.refresh();
                }, 200); // 200ms delay
            }
        };
        ImportDirUsersModalController.prototype.refresh = function () {
            var self = this;
            if (self.searchText.length >= 3) {
                //self.showFindUserError = false;
                self.jobRequest = { criteriaObj: { filterObj: [{ field: '', op: '', opTarget: '' }] } };
                self.directoryUsers = [];
                self.results = [];
                self.displayedresults = [];
                var qArray = [];
                var keyword = self.searchText;
                //console.log('keyword for search:  ' + keyword);
                if (keyword && keyword != '') {
                    self.jobRequest.criteriaObj.filterObj[0] = { field: 'keyword', op: '=', opTarget: [keyword] };
                }
                self.jobRequest.criteriaObj.filterObj.push({ field: 'server', op: '=', opTarget: [self.selectedDirectory] });
                //get all users and groups
                qArray.push(self.$http.post(self.Commands.data.users.getDirectoryUsers, { requestObj: null, criteriaObj: self.jobRequest.criteriaObj }).then(function (data) {
                    //console.log('sucessful call to getDirectoryUsers');
                    self.directoryUsers = data.data.responseObj;
                    if (self.viewBy == 'users') {
                        self.directoryUsers = _.filter(self.directoryUsers, { 'isGroup': false });
                    }
                    if (self.viewBy == 'groups') {
                        self.directoryUsers = _.filter(self.directoryUsers, { 'isGroup': true });
                    }
                    self.directoryUsers.forEach(function (user) {
                        if (user.roleId == null) {
                            user.roleId = self.importRoles[0].id;
                            user.rolename = self.importRoles[0].roleId;
                        }
                    });
                    //console.log('directoryUsers:');
                    //console.log(JSON.stringify(self.directoryUsers));
                }).catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                }));
                self.$q.all(qArray)
                    .then(function (respone) {
                    //console.log('successful execution of all');
                    self.leftcol = [].concat(self.directoryUsers);
                    self.displayedleftcol = [].concat(self.leftcol);
                    //console.log('displayedleftcol:');
                    //console.log(JSON.stringify(self.displayedleftcol));
                    if (self.movingLeft) {
                        self.removeRightColItems();
                    }
                    else {
                        self.removeLeftColItems();
                    }
                    self.movingLeft = false;
                });
            }
            //else
            //self.showFindUserError = true;
        };
        ImportDirUsersModalController.prototype.selectedItems = function () {
            var self = this;
            return _.filter(self.displayedleftcol, { 'isSelected': true });
        };
        ImportDirUsersModalController.prototype.selectedRightItems = function () {
            var self = this;
            return _.filter(self.rightcol, { 'isSelected': true });
        };
        ImportDirUsersModalController.prototype.noRoles = function () {
            var self = this;
            if (self.rightcol.length) {
                return _.filter(self.rightcol, { 'roleId': self.importRoles[0].id });
            }
        };
        //TODO: Need pattern for check all boxes
        ImportDirUsersModalController.prototype.leftCheckAll = function () {
            var self = this;
            this.leftcol.forEach(function (x) {
                if (self.leftCheckAllBox == true) {
                    x.isSelected = true;
                }
                else {
                    x.isSelected = false;
                }
            });
        };
        //TODO: Need pattern for check all boxes
        ImportDirUsersModalController.prototype.rightCheckAll = function () {
            var self = this;
            self.rightcol.forEach(function (x) {
                if (self.rightCheckAllBox == true) {
                    x.isSelected = true;
                }
                else {
                    x.isSelected = false;
                }
            });
        };
        ImportDirUsersModalController.prototype.removeLeftColItems = function () {
            var self = this;
            self.rightcol.forEach(function (rightitem) {
                self.leftcol.forEach(function (leftitem, index) {
                    if (rightitem.id == leftitem.id) {
                        self.leftcol.splice(index, 1);
                    }
                });
            });
            self.displayedleftcol = [].concat(self.leftcol);
            self.leftCheckAllBox = false;
            self.rightCheckAllBox = false;
        };
        ImportDirUsersModalController.prototype.removeRightColItems = function () {
            var self = this;
            self.rightcol.forEach(function (rightitem, index) {
                if (rightitem.isSelected) {
                    self.rightcol.splice(index, 1);
                }
            });
            self.removeLeftColItems();
        };
        ImportDirUsersModalController.prototype.moveRight = function () {
            var self = this;
            var items = self.selectedItems();
            items.forEach(function (x) {
                self.rightcol.push(x);
                x.isSelected = false;
            });
            this.removeLeftColItems();
        };
        ImportDirUsersModalController.prototype.moveLeft = function () {
            var self = this;
            var items = this.selectedRightItems();
            items.forEach(function (x, index) {
                self.rightcol.forEach(function (x, index) {
                    if (x.isSelected) {
                        self.rightcol.splice(index, 1);
                    }
                });
            });
            self.movingLeft = true;
            self.refresh();
        };
        ImportDirUsersModalController.prototype.changeRoles = function () {
            var self = this;
            var rightcolselected = self.selectedRightItems();
            rightcolselected.forEach(function (x) {
                x.rolename = self.selectedRoleId;
                self.importRoles.forEach(function (role) {
                    if (role.id == self.selectedRoleId) {
                        x.roleId = role.id;
                    }
                });
            });
        };
        ImportDirUsersModalController.prototype.saveUsers = function () {
            var self = this;
            var d = this.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.users.saveImportDirectoryUsers, self.rightcol)
                .then(function (data) {
                console.log('Directory User Update Success');
                self.$scope.modal.close();
                d.resolve();
            }).catch(function (data) {
                d.resolve();
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        ImportDirUsersModalController.prototype.viewdetails = function (item) {
            var self = this;
            if (item.firstname) {
                var user = true;
                var name = item.firstname;
            }
            else {
                var name = item.name;
                var user = false;
            }
            //console.log('item before viewdetails modal:');
            //console.log(JSON.stringify(item));
            var detailsModal = this.Modal({
                title: name,
                modalSize: 'modal-lg',
                templateUrl: 'views/importactivedirdetailsmodal.html',
                controller: 'ImportActiveDirDetailsModalController as detailsModal',
                params: {
                    selecteditem: item,
                    isuser: user
                },
                onComplete: function () {
                },
                close: function () {
                    var self = this;
                    self.$scope.modal.close();
                },
                cancel: function () {
                    var self = this;
                    self.$scope.modal.dismiss();
                }
            });
            detailsModal.modal.show();
        };
        ImportDirUsersModalController.prototype.getItemsFoundTranslation = function (subTotal, total) {
            var self = this;
            return self.$translate.instant("SETTINGS_ActiveDirectoryItemCount", { subTotal: subTotal, total: total });
        };
        ImportDirUsersModalController.prototype.getItemsSelectedTranslation = function (numSelectedItems) {
            var self = this;
            return self.$translate.instant("SETTINGS_ActiveDirectorySelectedItems", { numSelectedItems: numSelectedItems });
        };
        ImportDirUsersModalController.prototype.cancel = function () {
            var self = this;
            self.$rootScope.$broadcast('clearErrors');
            self.$scope.modal.cancel();
        };
        ImportDirUsersModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', 'GlobalServices', '$rootScope', '$q', 'Loading', '$translate', 'Commands'];
        return ImportDirUsersModalController;
    }());
    asm.ImportDirUsersModalController = ImportDirUsersModalController;
    angular
        .module('app')
        .controller('ImportDirUsersModalController', ImportDirUsersModalController);
})(asm || (asm = {}));
//# sourceMappingURL=importactivedirusersModal.js.map
