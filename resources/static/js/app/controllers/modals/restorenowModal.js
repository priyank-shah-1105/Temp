var asm;
(function (asm) {
    var RestoreNowModalController = (function () {
        function RestoreNowModalController($scope, $rootScope, $window, $timeout, Modal, Dialog, $http, $translate, $q, $filter, loading, GlobalServices, Commands) {
            this.$scope = $scope;
            this.$rootScope = $rootScope;
            this.$window = $window;
            this.$timeout = $timeout;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$translate = $translate;
            this.$q = $q;
            this.$filter = $filter;
            this.loading = loading;
            this.GlobalServices = GlobalServices;
            this.Commands = Commands;
            this.runningJobs = 0;
            this.scheduledJobs = 0;
            this.currentUsers = 0;
            this.refreshTimer = null;
            this.errors = new Array();
            var self = this;
            self.submitform = false;
            self.getBackupSettings();
            self.deferred = self.$q.defer();
            self.statusMessage = "Loading Content...";
        }
        RestoreNowModalController.prototype.$onDestroy = function () {
            var self = this;
            if (self.refreshTimer)
                self.$timeout.cancel(self.refreshTimer);
        };
        RestoreNowModalController.prototype.canDeactivate = function () {
            var self = this;
            var allow = true;
            return allow;
        };
        RestoreNowModalController.prototype.getBackupSettings = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.$http.post(self.Commands.data.backupAndRestore.getBackupSettings, null)
                .then(function (response) {
                self.backupSettings = response.data.responseObj;
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        RestoreNowModalController.prototype.getJobs = function () {
            var self = this;
            //Get jobs
            var jobs = self.$http.post('jobs/getjoblist', null)
                .then(function (response) {
                self.runningJobs = self.$filter('filter')(response.data.responseObj, { status: "running" }).length;
                self.scheduledJobs = self.$filter('filter')(response.data.responseObj, { status: "scheduled" }).length;
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); });
            return jobs;
        };
        RestoreNowModalController.prototype.getUsers = function () {
            var self = this;
            //Get users
            var users = self.$http.post('appliance/getcurrentusersandjobs', null)
                .then(function (response) {
                self.currentUsers = response.data.responseObj.currentusers;
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); });
            return users;
        };
        RestoreNowModalController.prototype.showLoader = function () {
            var self = this;
            //////var modal = self.Modal({
            //////    title: self.$translate.instant('RESTORENOW_confirm_title'),
            //////    modalSize: 'modal-lg',
            //////    templateUrl: 'views/settings/backupandrestore/restoreconfirmmodal.html',
            //////    controller: 'RestoreConfirmModalController as restoreConfirmModalController',
            //////    params: {},
            //////    onComplete() {
            //////        var d = self.$q.defer();
            //////        self.GlobalServices.ClearErrors(self.errors);
            //////        self.loading(d.promise);
            //////        self.$http.post(self.Commands.data.backupAndRestore.restore, self.backupSettings)
            //////            .then(function (data: any) {
            //////                self.restoreFailed = false;
            //////                self.getRestoreStatus();
            //////            })
            //////            .catch((response) => {
            //////                self.GlobalServices.DisplayError(response.data, self.errors);
            //////            })
            //////            .finally(() => d.resolve());
            //////    },
            //////    onCancel() {
            //////    }
            //////});
            //////modal.modal.show();
            //modal.modal.dismiss();
            self.loader_modal = self.Modal({
                //title: self.$translate.instant('CLARITY_Loading'),
                //modalSize: 'modal-lg',
                type: 'bare',
                className: 'loading',
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'views/settings/backupandrestore/loadermodal.html',
                controller: 'LoaderModalController as loaderModalController',
                params: { loaderMessage: self.statusMessage },
                onComplete: function () {
                    //var d = self.$q.defer();
                    //self.GlobalServices.ClearErrors(self.errors);
                    //self.loading(d.promise);
                    //self.$http.post(self.Commands.data.backupAndRestore.restore, self.backupSettings)
                    //    .then(function (data: any) {
                    //        self.restoreFailed = false;
                    //        self.getRestoreStatus();
                    //    })
                    //    .catch((response) => {
                    //        self.GlobalServices.DisplayError(response.data, self.errors);
                    //    })
                    //    .finally(() => d.resolve());
                },
                onCancel: function () {
                }
            });
            self.loader_modal.modal.show();
            //self.loader_modal.modal.dismiss();
        };
        RestoreNowModalController.prototype.restore = function (isValid) {
            var self = this;
            var jobs = self.getJobs();
            var users = self.getUsers();
            var promise = self.$q.all([jobs, users])
                .then(function (data) {
                var confirm = self.Dialog(self.$translate.instant('RESTORENOW_confirm_title'), self.$translate.instant('RESTORENOW_confirm_text') + '<br/><br/><ul><li>' +
                    self.$translate.instant('RESTORENOW_confirm_userinfo') + self.currentUsers + '</li><li>' +
                    self.$translate.instant('RESTORENOW_confirm_jobinfo') + self.runningJobs + '</li><li>' +
                    self.$translate.instant('RESTORENOW_confirm_scheduledjobinfo') + self.scheduledJobs + '</li></ul>' +
                    self.$translate.instant('RESTORENOW_confirm_footnote'));
                confirm.then(function () {
                    self.$http.post(self.Commands.data.backupAndRestore.restore, self.backupSettings)
                        .then(function (data) {
                        //console.log( 'restore started' );
                        //self.loading( self.deferred.promise );
                        self.statusMessage = 'Starting Restoration';
                        self.showLoader();
                        self.restoreFailed = false;
                        self.getRestoreStatus();
                    });
                });
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); });
        };
        RestoreNowModalController.prototype.getRestoreStatus = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.$http.post(self.Commands.data.backupAndRestore.getRestoreStatus, self.backupSettings)
                .then(function (data) {
                //console.log( 'restore status:  ' + data.data.responseObj );
                //self.statusMessage = data.data.responseObj;
                if (data.data.responseObj == "waiting_for_restart") {
                    //console.log( 'restore finished' );
                    //self.deferred.resolve();
                    self.loader_modal.modal.dismiss();
                    self.$window.location.href = 'status.html#/status';
                }
                else if (data.data.responseObj == "not_in_progress") {
                    //self.loader.setMessage('Starting Restoration');
                    self.statusMessage = 'Starting Restoration';
                }
                else if (data.data.responseObj == "preparing_for_download") {
                    //self.loader.setMessage('Downloading Archive');
                    self.statusMessage = 'Downloading Archive';
                }
                else if (data.data.responseObj == "downloading") {
                    //self.loader.setMessage('Downloading Archive');
                    self.statusMessage = 'Downloading Archive';
                }
                else if (data.data.responseObj == "unpacking") {
                    //self.loader.setMessage('Downloading Archive');
                    self.statusMessage = 'Downloading Archive';
                }
                else if (data.data.responseObj == "verifying") {
                    //self.loader.setMessage('Verifying Archive');
                    self.statusMessage = 'Verifying Archive';
                }
                else if (data.data.responseObj == "failed") {
                    //note: status of failed from the back-end is not a successful response of the status failed--it is an error that sends us to our catch to be displayed as an error
                    self.restoreFailed = true;
                    //self.loader.setMessage('Failed');
                    self.statusMessage = 'Failed';
                }
                //update the watch variable
                self.loader_modal.modal.params.loaderMessage = self.statusMessage;
                if (!self.restoreFailed) {
                    self.refreshTimer = self.$timeout(function () {
                        self.getRestoreStatus();
                    }, 5000);
                }
            })
                .catch(function (response) {
                //console.log( 'restore status:  ' + response.data.details );
                //console.log( 'restore error routine' );
                self.restoreFailed = true;
                //self.deferred.resolve();
                self.loader_modal.modal.dismiss();
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () {
                //do nothing, do not close the loader here since we call this iteratively
            });
        };
        RestoreNowModalController.prototype.testConnection = function (isValid) {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.$http.post(self.Commands.data.backupAndRestore.testRestoreConnection, self.backupSettings)
                .then(function (data) {
                self.Dialog((self.$translate.instant('RESTORENOW_testconnection_success_title')), ('<i class="text-success ci-health-square-check"></i> ' + self.$translate.instant('RESTORENOW_testconnection_success_message')), true);
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        RestoreNowModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        RestoreNowModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        RestoreNowModalController.$inject = ['$scope', '$rootScope', '$window', '$timeout', 'Modal', 'Dialog', '$http', '$translate', '$q', '$filter', 'Loading', 'GlobalServices', 'Commands'];
        return RestoreNowModalController;
    }());
    asm.RestoreNowModalController = RestoreNowModalController;
    angular
        .module('app')
        .controller('RestoreNowModalController', RestoreNowModalController);
})(asm || (asm = {}));
//# sourceMappingURL=restorenowModal.js.map
