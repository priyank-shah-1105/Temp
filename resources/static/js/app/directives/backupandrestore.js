/// <reference path="asmlogsdirective.ts" />
var asm;
(function (asm) {
    "use strict";
    var BackupAndRestoreController = (function () {
        function BackupAndRestoreController(Modal, Dialog, $http, $timeout, $q, $translate, Commands, Loading, GlobalServices) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.Commands = Commands;
            this.Loading = Loading;
            this.GlobalServices = GlobalServices;
            this.errors = new Array();
            this.editScheduledBackupInfo = function () {
                var self = this;
                var editScheduledBackupModal = self.Modal({
                    title: self.$translate.instant('BACKUPANDRESTORE_scheduledbackup_title'),
                    onHelp: function () {
                        self.GlobalServices.showHelp('EditingAutomaticallyScheduledBackups');
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/settings/backupandrestore/editscheduledbackup.html',
                    controller: 'EditScheduledBackupModalController as editScheduledBackup',
                    params: {},
                    onComplete: function () {
                        self.refresh();
                    }
                });
                editScheduledBackupModal.modal.show();
            };
            this.activate();
            this.refresh();
        }
        BackupAndRestoreController.prototype.activate = function () {
            var self = this;
            self.pollRefresh = self.$timeout(function () {
                self.refresh();
                self.activate();
            }, 30000);
        };
        BackupAndRestoreController.prototype.$onDestroy = function () {
            var self = this;
            if (self.pollRefresh)
                self.$timeout.cancel(self.pollRefresh);
        };
        BackupAndRestoreController.prototype.refresh = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            //removed loader here so that it does not continually show up due to the timeout running this in the background
            self.$q.all([
                self.$http.post(self.Commands.data.backupAndRestore.getBackupScheduleInfo, null)
                    .then(function (response) {
                    self.backupScheduleInfo = response.data.responseObj;
                }),
                self.$http.post(self.Commands.data.backupAndRestore.getBackupSettings, null)
                    .then(function (response) {
                    self.backupSettings = response.data.responseObj;
                })
            ]).catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { });
        };
        ;
        BackupAndRestoreController.prototype.backupNow = function () {
            var self = this;
            var backupNowModal = self.Modal({
                title: self.$translate.instant('BACKUPANDRESTORE_btnbackupnow'),
                onHelp: function () {
                    self.GlobalServices.showHelp('BackupNow');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/backupandrestore/backupnow.html',
                controller: 'BackupNowModalController as backupNow',
                params: {}, onComplete: function () {
                    self.refresh();
                }
            });
            backupNowModal.modal.show();
        };
        BackupAndRestoreController.prototype.restoreNow = function () {
            var self = this;
            var restoreNowModal = self.Modal({
                title: self.$translate.instant('BACKUPANDRESTORE_btnrestorenow'),
                onHelp: function () {
                    self.GlobalServices.showHelp('RestoreNow');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/backupandrestore/restorenow.html',
                controller: 'RestoreNowModalController as restoreNow',
                params: {},
                onComplete: function () {
                    self.refresh();
                }
            });
            restoreNowModal.modal.show();
        };
        ;
        BackupAndRestoreController.prototype.editBackupSettingsAndDetails = function () {
            var self = this;
            var editBackupSettingsModal = self.Modal({
                title: self.$translate.instant('BACKUPSETTINGSANDDETAILS_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('EditingBackupSettingsAndDetails');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/backupandrestore/editsettingsanddetails.html',
                controller: 'BackupSettingsAndDetailsModalController as editBackupSettings',
                params: {},
                onComplete: function () {
                    self.refresh();
                }
            });
            editBackupSettingsModal.modal.show();
        };
        ;
        BackupAndRestoreController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', 'Commands', 'Loading', 'GlobalServices'];
        return BackupAndRestoreController;
    }());
    angular.module('app')
        .component('backupAndRestore', {
        templateUrl: 'views/settings/backupandrestore/backupandrestore.html',
        controller: BackupAndRestoreController,
        controllerAs: 'backupAndRestore',
        bindings: {}
    });
})(asm || (asm = {}));
//# sourceMappingURL=backupandrestore.js.map
