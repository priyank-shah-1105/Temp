var asm;
(function (asm) {
    var MigrateController = (function () {
        function MigrateController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.errors = new Array();
            var self = this;
            self.refresh();
        }
        MigrateController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.$http.post(self.Commands.data.services.getServiceById, { id: self.$scope.modal.params.serviceId })
                    .then(function (data) {
                    self.viewModel = data.data.responseObj.serverlist;
                    self.displayedData = angular.copy(self.viewModel);
                }),
                self.$http.post(self.Commands.data.serverpools.getServerPools, null)
                    .then(function (data) {
                    self.serverPools = data.data.responseObj;
                })
            ])
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        MigrateController.prototype.doMigrate = function () {
            var self = this;
            console.log(self.selectedServer);
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('MIGRATE_SERVER_Confirm') +
                ("<br /><ul><li>\n                " + self.selectedServer.ipAddress + " - " + self.selectedServer._newServerPool.name + "\n                </li></ul>"))
                .then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.services.migrate, {
                    serviceId: self.$scope.modal.params.serviceId,
                    migrateAllServers: false,
                    targetServerPool: "",
                    migrateServers: [
                        {
                            id: self.selectedServer.id,
                            name: self.selectedServer._newServerPool.id
                        }
                    ]
                })
                    .then(function (data) {
                    d.resolve();
                    self.Dialog(self.$translate.instant('MIGRATE_SERVER_Submitted'), null, true);
                    self.$scope.modal.close();
                })
                    .catch(function (data) {
                    d.resolve();
                    self.GlobalServices.DisplayError(data.data, self.errors);
                });
            });
        };
        MigrateController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        MigrateController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        MigrateController.$inject = ["$http", "$timeout", "$scope", "$q", "$translate", "Modal", "Loading", "Dialog", "Commands", "GlobalServices"];
        return MigrateController;
    }());
    asm.MigrateController = MigrateController;
    angular
        .module("app")
        .controller("MigrateController", MigrateController);
})(asm || (asm = {}));
//# sourceMappingURL=migrate.js.map
