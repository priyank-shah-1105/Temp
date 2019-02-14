var asm;
(function (asm) {
    var EditRepoModalController = (function () {
        function EditRepoModalController(modal, $http, $timeout, $scope, $q, $translate, loading, dialog, commands, globalServices, constants, $rootScope) {
            this.modal = modal;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.loading = loading;
            this.dialog = dialog;
            this.commands = commands;
            this.globalServices = globalServices;
            this.constants = constants;
            this.$rootScope = $rootScope;
            this.errors = [];
            var self = this;
            self.initialize();
        }
        EditRepoModalController.prototype.initialize = function () {
            var self = this;
            self.repo = self.$scope.modal.params.repo;
            self.type = self.$scope.modal.params.type;
        };
        EditRepoModalController.prototype.testConnection = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            //self.testFileRepo(self.repo)
            //    .then((response: any) => {
            //        if (response.data.responseObj === "Success") {
            //            self.dialog(self.$translate.instant('SETTINGS_Repositories_Success'), self.$translate.instant('SETTINGS_Repositories_TestSuccess'), true);
            //        } else {
            //            self.dialog(self.$translate.instant('SETTINGS_Repositories_Failure'), self.$translate.instant('SETTINGS_Repositories_TestFailed'), true);
            //        }
            //    })
            //    .catch(response => { self.globalServices.DisplayError(response.data, self.errors) })
            //    .finally(() => d.resolve());
            self.testFileRepo(self.repo)
                .then(function (data) {
                self.dialog((self.$translate.instant('SETTINGS_Repositories_testconnection_success_title')), ('<i class="text-success ci-health-square-check"></i> ' + self.$translate.instant('SETTINGS_Repositories_testconnection_success_message')), true);
            })
                .catch(function (data) {
                self.globalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        EditRepoModalController.prototype.save = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            if (self.type === 'sync') {
                self.syncRepo(self.repo)
                    .then(function (response) {
                    self.close();
                })
                    .catch(function (response) {
                    self.globalServices.DisplayError(response.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            }
            else {
                self.saveRepo(self.repo)
                    .then(function (response) {
                    self.close();
                })
                    .catch(function (response) {
                    self.globalServices.DisplayError(response.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            }
        };
        EditRepoModalController.prototype.saveRepo = function (repo) {
            var self = this;
            return self.$http.post(self.commands.data.repository.saveRepository, repo);
        };
        EditRepoModalController.prototype.testFileRepo = function (repo) {
            var self = this;
            return self.$http.post(self.commands.data.repository.testFileRepository, repo);
        };
        EditRepoModalController.prototype.syncRepo = function (repo) {
            var self = this;
            return self.$http.post(self.commands.data.repository.syncRepository, repo);
        };
        EditRepoModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        EditRepoModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        EditRepoModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices', 'constants', '$rootScope'];
        return EditRepoModalController;
    }());
    asm.EditRepoModalController = EditRepoModalController;
    angular
        .module('app')
        .controller('EditRepoModalController', EditRepoModalController);
})(asm || (asm = {}));
//# sourceMappingURL=editRepoModal.js.map
