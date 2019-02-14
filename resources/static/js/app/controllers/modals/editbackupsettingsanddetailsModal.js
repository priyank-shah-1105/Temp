var asm;
(function (asm) {
    var BackupSettingsAndDetailsModalController = (function () {
        function BackupSettingsAndDetailsModalController($scope, Modal, Dialog, $http, $translate, Commands, GlobalServices, loading, $q) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$translate = $translate;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.loading = loading;
            this.$q = $q;
            this.errors = new Array();
            var self = this;
            self.getBackupSettings();
        }
        BackupSettingsAndDetailsModalController.prototype.getBackupSettings = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.$http.post(self.Commands.data.backupAndRestore.getBackupSettings, null)
                .then(function (data) {
                self.backupSettings = data.data.responseObj;
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        BackupSettingsAndDetailsModalController.prototype.save = function (isValid) {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.$http.post(self.Commands.data.backupAndRestore.saveBackupSettings, self.backupSettings)
                .then(function (data) {
                self.$scope.modal.close();
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        BackupSettingsAndDetailsModalController.prototype.testConnection = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.$http.post(self.Commands.data.backupAndRestore.testBackupConnection, self.backupSettings)
                .then(function (data) {
                self.Dialog((self.$translate.instant('BACKUPNOW_testconnection_title')), ('<i class="text-success ci-health-square-check"></i> ' + self.$translate.instant('The test was successful')), true);
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        BackupSettingsAndDetailsModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        BackupSettingsAndDetailsModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        BackupSettingsAndDetailsModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$translate', 'Commands', 'GlobalServices', 'Loading', '$q'];
        return BackupSettingsAndDetailsModalController;
    }());
    asm.BackupSettingsAndDetailsModalController = BackupSettingsAndDetailsModalController;
    angular
        .module('app')
        .controller('BackupSettingsAndDetailsModalController', BackupSettingsAndDetailsModalController);
})(asm || (asm = {}));
//# sourceMappingURL=editbackupsettingsanddetailsModal.js.map
