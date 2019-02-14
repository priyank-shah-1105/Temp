var asm;
(function (asm) {
    "use strict";
    var EditFirmwareController = (function () {
        function EditFirmwareController($http, $timeout, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, $rootScope) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$rootScope = $rootScope;
            var self = this;
            self.refresh();
            self.activate();
        }
        EditFirmwareController.prototype.activate = function () {
            var self = this;
            var fwtimer = self.$timeout(function () {
                self.refresh();
                self.activate();
            }, 120000);
            self.$rootScope.$on('$locationChangeSuccess', function () {
                self.$timeout.cancel(fwtimer);
            });
        };
        EditFirmwareController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.silenceLoading || self.Loading(d.promise);
            self.$http.post(self.Commands.data.firmwarepackages.getFirmwarePackages, {})
                .then(function (data) {
                self.safeSource = data.data.responseObj;
                self.firmwarePackages = angular.copy(self.safeSource);
                self.firmwareDropdownList = self.getRepositoryDropdownList(self.firmwarePackages);
                var match = _.find(self.firmwareDropdownList, { defaultpackage: true });
                self.selectedDropdownItem = match ? match.id : match;
                self.selectedRow = self.getCurrentPackageListItem(self.firmwareDropdownList, self.selectedDropdownItem, self.firmwarePackages, self.selectedRow);
                angular.forEach(self.safeSource, function (repo) { return angular.extend(repo, { options: self.repoActions(repo) }); });
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        EditFirmwareController.prototype.repoActions = function (repo) {
            var self = this;
            var actions = [
                {
                    id: undefined,
                    name: self.$translate.instant('SETTINGS_Repositories_SelectAction')
                }];
            /*
            for this directive, parm mode is 'standard' for listrepositories.html (and View Bundles is not an action in the dropdown since it is in the aside),
            and parm mode is 'minimal' for configurechassiswizard.html (and View Bundles is an action in the dropdown)
            possible actions:  Delete (delete), Resynchronize (sync), View Bundles (view)
            if packageSource is not equal to embedded, and not the default package, it can be deleted
            if not in use by a service, it can be deleted
            if state is copying or pending, its source is embedded, or it's a default package, it cannot be deleted
             */
            if (repo.services == null || repo.services.length == 'undefined' || repo.services.length == 0) {
                if (repo.state !== "copying"
                    && repo.state !== "pending"
                    && repo.packageSource !== "embedded"
                    && !repo.defaultpackage) {
                    actions.push({
                        id: "delete",
                        name: self.$translate.instant("SETTINGS_Repositories_Delete"),
                        action: function (firmware) {
                            self.doDelete(firmware);
                        }
                    });
                }
            }
            /* can only have resynchronize is in state error */
            if (repo.state === "errors") {
                actions.push({
                    id: "sync",
                    name: self.$translate.instant("SETTINGS_Repositories_Resynchronize"),
                    action: function (firmware) {
                        self.resync(firmware);
                    }
                });
            }
            if ((self.mode === "minimal") && (repo.firmwarebundles != null && repo.firmwarebundles.length || (repo.softwarebundles != null && repo.softwarebundles.length))) {
                actions.push({
                    id: "view",
                    name: self.$translate.instant("SETTINGS_Repositories_ViewBundles"),
                    action: function () {
                        self.doViewBundles();
                    }
                });
            }
            ;
            repo.selectedOption = actions[0];
            return actions;
        };
        EditFirmwareController.prototype.resync = function (firmware) {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.saveFirmware(firmware)
                .then(function () {
                self.refresh();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { d.resolve(); });
        };
        EditFirmwareController.prototype.getRepositoryDropdownList = function (fullRepoList) {
            // used to populate default firmware/software repository drop down
            return angular.copy(_.filter(fullRepoList, function (fp) {
                return (fp.packageSource !== 'embedded' &&
                    fp.state &&
                    (fp.state === "available"));
            }));
        };
        EditFirmwareController.prototype.getCurrentPackageListItem = function (firmwarePackageList, defaultRepoId, tableList, selectedFirmware) {
            return selectedFirmware
                || _.find(firmwarePackageList, { id: defaultRepoId })
                || tableList[0];
        };
        EditFirmwareController.prototype.doDelete = function (firmware) {
            var self = this;
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('EDIT_FIRMWARE_Delete_Confirmation'))
                .then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.firmwarepackages.remove, [firmware.id])
                    .then(function () {
                    self.refresh();
                })
                    .catch(function (error) {
                    self.GlobalServices.DisplayError(error.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        EditFirmwareController.prototype.newCustomBundle = function (repo) {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_Repositories_AddCustomBundle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('addCustomBundle');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/addeditviewbundle.html',
                controller: 'AddEditViewBundleModalController as c',
                params: {
                    repo: angular.copy(repo),
                    type: 'add'
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        EditFirmwareController.prototype.doViewBundles = function () {
            var self = this;
            if (self.selectedRow) {
                var theModal = self.Modal({
                    title: self.selectedRow.name,
                    onHelp: function () {
                        self.GlobalServices.showHelp('viewbundles');
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/settings/repositories/viewbundles.html',
                    controller: 'ViewBundlesController as vb',
                    params: {
                        id: self.selectedRow.id
                    },
                    onComplete: function () {
                        self.refresh();
                    }
                });
                theModal.modal.show();
            }
        };
        EditFirmwareController.prototype.onDefaultFirmwareChanged = function () {
            var self = this;
            if (self.selectedDropdownItem) {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.setDefaultFirmwarePackage(self.selectedDropdownItem)
                    .then(function (data) {
                    self.refresh();
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            }
        };
        EditFirmwareController.prototype.setDefaultFirmwarePackage = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.setDefaultFirmwarePackage, { id: self.selectedDropdownItem });
        };
        EditFirmwareController.prototype.addFirmwareRepo = function () {
            var self = this;
            var theModal = self.Modal({
                title: self.$translate.instant('SETTINGS_Repositories_AddFirmwareRepository'),
                onHelp: function () {
                    self.GlobalServices.showHelp('Addingfirmwarerepositories');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/addfirmwarebundlemodal.html',
                controller: 'AddFirmwareBundleModalController as addFirmwareBundleModalController',
                params: {},
                onComplete: function () {
                    self.refresh();
                }
            });
            theModal.modal.show();
        };
        EditFirmwareController.prototype.saveFirmware = function (firmware) {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.saveFirmwarePackage, firmware);
        };
        EditFirmwareController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope'];
        return EditFirmwareController;
    }());
    angular.module('app')
        .component("editfirmware", {
        templateUrl: "views/editfirmware.html",
        controller: EditFirmwareController,
        controllerAs: 'editFirmwareController',
        bindings: {
            mode: "@",
            silenceLoading: "@?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=editfirmware.js.map
