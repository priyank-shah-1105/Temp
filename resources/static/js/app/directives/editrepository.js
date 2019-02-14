var asm;
(function (asm) {
    "use strict";
    var EditRepositoryController = (function () {
        function EditRepositoryController($http, $timeout, $q, $translate, modal, loading, dialog, commands, globalServices, $rootScope, $interval, constants) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.modal = modal;
            this.loading = loading;
            this.dialog = dialog;
            this.commands = commands;
            this.globalServices = globalServices;
            this.$rootScope = $rootScope;
            this.$interval = $interval;
            this.constants = constants;
            var self = this;
            self.initialize();
        }
        EditRepositoryController.prototype.poll = function () {
            var self = this;
            self.$interval(function () { self.refresh(true); }, 120000);
        };
        EditRepositoryController.prototype.initialize = function () {
            var self = this;
            self.refresh();
            self.poll();
        };
        EditRepositoryController.prototype.refresh = function (hideLoading) {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            hideLoading || self.loading(d.promise);
            self.getRepos()
                .then(function (response) {
                angular.forEach(response.data.responseObj, function (repo) {
                    repo.options = self.repoActions(repo);
                    var imageMatch = _.find(self.constants.repositoryImageTypes, { id: repo.imagetype });
                    if (imageMatch) {
                        repo._imagetype = imageMatch.name;
                    }
                });
                self.repos = response.data.responseObj;
                self.backup = angular.copy(response.data.responseObj);
            })
                .catch(function (response) {
                self.globalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { d.resolve(); });
        };
        EditRepositoryController.prototype.repoActions = function (repo) {
            var self = this;
            var actions = [{ id: undefined, name: self.$translate.instant('SETTINGS_Repositories_SelectAction') }];
            /*possible actions:  Delete (delete), Edit (edit), Resynchronize (sync)
            if not in use, it can be deleted
            if state is copying or pending, it cannot be deleted */
            if (!repo.isInUse) {
                if ((repo.state !== 'copying') && (repo.state !== 'pending')) {
                    actions.push({ id: "delete", name: self.$translate.instant('SETTINGS_Repositories_Delete') });
                }
            }
            /* can only have edit and resynchronize if in state error
               if isRCM is true, don't have Resync as an option as per JIRA 844
               if isRCM is true, hide Edit as per JIRA 1264 */
            if (repo.state === 'errors') {
                if (!repo.isRCM) {
                    actions.push({ id: "sync", name: self.$translate.instant('SETTINGS_Repositories_Resynchronize') });
                    actions.push({ id: "edit", name: self.$translate.instant('SETTINGS_Repositories_Edit') });
                }
            }
            return angular.copy(actions);
        };
        EditRepositoryController.prototype.actionTaken = function (repo) {
            var self = this;
            switch (repo.selectedOption) {
                case 'delete':
                    self.deleteRepo(repo);
                    break;
                case 'edit':
                    self.editRepoModal(repo);
                    break;
                case 'sync':
                    self.resyncRepo(repo);
                    break;
            }
        };
        EditRepositoryController.prototype.editRepoModal = function (repo) {
            var self = this;
            var modal = self.modal({
                title: self.$translate.instant('SETTINGS_Repositories_EditOSImageRepo'),
                onHelp: function () {
                    self.globalServices.showHelp('EditOSImageRepository');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/editrepomodal.html',
                controller: 'EditRepoModalController as editRepoModalController',
                params: {
                    repo: angular.copy(repo),
                    type: repo.selectedOption
                },
                onComplete: function () {
                    self.refresh();
                },
                onCancel: function () {
                    self.refresh();
                    modal.modal.dismiss();
                }
            });
            modal.modal.show();
        };
        EditRepositoryController.prototype.deleteRepo = function (repo) {
            var self = this;
            var modal = self.modal({
                title: self.$translate.instant('SETTINGS_Repositories_Confirm'),
                modalSize: 'modal-md',
                templateUrl: 'views/settings/repositories/confirmdeleterepo.html',
                controller: 'GenericModalController as c',
                params: {},
                onComplete: function () {
                    var d = self.$q.defer();
                    self.globalServices.ClearErrors(self.errors);
                    self.loading(d.promise);
                    self.$http.post(self.commands.data.repository.deleteRepository, { id: repo.id })
                        .then(function (response) {
                        d.resolve();
                        self.refresh();
                    })
                        .catch(function (response) {
                        self.globalServices.DisplayError(response.data, self.errors);
                    })
                        .finally(function () { d.resolve(); });
                },
                onCancel: function () {
                    self.refresh();
                    modal.modal.dismiss();
                }
            });
            modal.modal.show();
        };
        EditRepositoryController.prototype.resyncRepo = function (repo) {
            var self = this;
            var modal = self.modal({
                title: self.$translate.instant('SETTINGS_Repositories_ResyncOSRepo'),
                onHelp: function () {
                    self.globalServices.showHelp('SyncOSImageRepository');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/editrepomodal.html',
                controller: 'EditRepoModalController as editRepoModalController',
                params: {
                    repo: angular.copy(repo),
                    type: repo.selectedOption
                },
                onComplete: function () {
                    self.refresh();
                },
                onCancel: function () {
                    self.refresh();
                    modal.modal.dismiss();
                }
            });
            modal.modal.show();
        };
        EditRepositoryController.prototype.addRepo = function () {
            var self = this;
            var modal = self.modal({
                title: self.$translate.instant('SETTINGS_Repositories_AddOSRepo'),
                onHelp: function () {
                    self.globalServices.showHelp('AddOSImageRepository');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/editrepomodal.html',
                controller: 'EditRepoModalController as editRepoModalController',
                params: {
                    repo: {},
                    type: 'add'
                },
                onComplete: function () {
                    self.refresh();
                },
                onCancel: function () {
                    self.refresh();
                    modal.modal.dismiss();
                }
            });
            modal.modal.show();
        };
        EditRepositoryController.prototype.getRepos = function () {
            var self = this;
            return self.$http.post(self.commands.data.repository.getRepositoryList, { requestObj: [], criteriaObj: {} });
        };
        EditRepositoryController.prototype.getRepoById = function (id) {
            var self = this;
            return self.$http.post(self.commands.data.repository.getRepositoryById, { id: id });
        };
        EditRepositoryController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope', '$interval', "constants"];
        return EditRepositoryController;
    }());
    angular.module("app")
        .component("editrepository", {
        templateUrl: "views/editrepository.html",
        controller: EditRepositoryController,
        controllerAs: "editRepositoryController",
        bindings: {
            errors: "="
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=editrepository.js.map
