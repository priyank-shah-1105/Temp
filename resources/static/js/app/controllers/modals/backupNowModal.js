var asm;
(function (asm) {
    var BackupNowModalController = (function () {
        function BackupNowModalController($scope, Modal, Dialog, $http, $translate, Commands, GlobalServices, Loading, $q) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$translate = $translate;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.Loading = Loading;
            this.$q = $q;
            this.errors = new Array();
            var self = this;
            self.getBackupSettings();
        }
        BackupNowModalController.prototype.getBackupSettings = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.backupAndRestore.getBackupSettings, null)
                .then(function (data) {
                self.backupSettings = data.data.responseObj;
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        BackupNowModalController.prototype.backup = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.backupAndRestore.backupNow, self.backupSettings)
                .then(function (data) {
                d.resolve();
                self.$scope.modal.close();
            })
                .catch(function (data) {
                d.resolve();
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        BackupNowModalController.prototype.testBackupNowConnection = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.backupAndRestore.testBackupConnection, self.backupSettings)
                .then(function (data) {
                self.Dialog((self.$translate.instant('BACKUPNOW_testconnection_success_title')), ('<i class="text-success ci-health-square-check"></i> ' + self.$translate.instant('BACKUPNOW_testconnection_success_message')), true);
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        BackupNowModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        BackupNowModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        BackupNowModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$translate', 'Commands', 'GlobalServices', 'Loading', '$q'];
        return BackupNowModalController;
    }());
    asm.BackupNowModalController = BackupNowModalController;
    angular
        .module('app')
        .controller('BackupNowModalController', BackupNowModalController);
})(asm || (asm = {}));
//# sourceMappingURL=backupNowModal.js.map
