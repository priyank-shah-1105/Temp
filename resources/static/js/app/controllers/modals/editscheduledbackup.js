var asm;
(function (asm) {
    var EditScheduledBackupModalController = (function () {
        function EditScheduledBackupModalController($scope, Modal, Dialog, $http, $translate, $q, $filter, loading, Commands, GlobalServices) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$translate = $translate;
            this.$q = $q;
            this.$filter = $filter;
            this.loading = loading;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.errors = new Array();
            this.timeOptions = [
                '00:00', '00:30', '01:00', '01:30', '02:00', '02:30', '03:00', '03:30', '04:00', '04:30', '05:00', '05:30', '06:00', '06:30', '07:00', '07:30',
                '08:00', '08:30', '09:00', '09:30', '10:00', '10:30', '11:00', '11:30', '12:00', '12:30', '13:00', '13:30', '14:00', '14:30', '15:00', '15:30',
                '16:00', '16:30', '17:00', '17:30', '18:00', '18:30', '19:00', '19:30', '20:00', '20:30', '21:00', '21:30', '22:00', '22:30', '23:00', '23:30'
            ];
            var self = this;
            self.getBackupScheduleInfo();
        }
        EditScheduledBackupModalController.prototype.getBackupScheduleInfo = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.$http.post(self.Commands.data.backupAndRestore.getBackupScheduleInfo, null)
                .then(function (response) {
                self.backupScheduleInfo = response.data.responseObj;
                self.time = moment(response.data.responseObj.timeOfBackup).format("HH:mm");
                console.log(self.time);
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        EditScheduledBackupModalController.prototype.timeRequired = function () {
            var self = this;
            return self.backupScheduleInfo &&
                self.backupScheduleInfo.enabled === "true" &&
                !!_.find([
                    self.backupScheduleInfo.monday,
                    self.backupScheduleInfo.tuesday,
                    self.backupScheduleInfo.wednesday,
                    self.backupScheduleInfo.thursday,
                    self.backupScheduleInfo.friday,
                    self.backupScheduleInfo.saturday,
                    self.backupScheduleInfo.sunday
                ], function (day) { return day; });
        };
        EditScheduledBackupModalController.prototype.save = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            var time = moment(moment().format('YYYY-MM-DD') + "T" + self.time, "YYYY-MM-DDTHH:mm").toDate();
            self.backupScheduleInfo.timeOfBackup = (self.backupScheduleInfo.enabled === "true" ? time.toISOString() : null);
            self.$http.post(self.Commands.data.backupAndRestore.setBackupScheduleInfo, self.backupScheduleInfo)
                .then(function () {
                self.$scope.modal.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        EditScheduledBackupModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        EditScheduledBackupModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        EditScheduledBackupModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$translate', '$q', '$filter', 'Loading', 'Commands', 'GlobalServices'];
        return EditScheduledBackupModalController;
    }());
    asm.EditScheduledBackupModalController = EditScheduledBackupModalController;
    angular
        .module('app')
        .controller('EditScheduledBackupModalController', EditScheduledBackupModalController);
})(asm || (asm = {}));
//# sourceMappingURL=editscheduledbackup.js.map
