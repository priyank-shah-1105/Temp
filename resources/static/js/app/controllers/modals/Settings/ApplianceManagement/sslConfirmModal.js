var asm;
(function (asm) {
    var UploadCertificateConfirmModalController = (function () {
        function UploadCertificateConfirmModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
            this.Modal = Modal;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.errors = [];
            var self = this;
            self.initialize();
        }
        UploadCertificateConfirmModalController.prototype.initialize = function () {
            var self = this, d = self.$q.defer(), bullets = {};
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.getJobList()
                    .then(function (data) {
                    angular.merge(bullets, {
                        numProgressJobs: _.filter(data.data.responseObj, { status: "running" }).length,
                        numScheduledJobs: _.filter(data.data.responseObj, { status: "scheduled" }).length
                    });
                }),
                self.getCurrentUsersAndJobs()
                    .then(function (data) {
                    angular.merge(bullets, {
                        numUsers: data.data.responseObj.currentusers,
                    });
                })
            ])
                .then(function () {
                //passing bullets as a parameter to $translate to interpolate values into translation
                self.message = self.$translate.instant("UPDATE_VIRTUAL_APPLIANCE_Description", bullets);
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        UploadCertificateConfirmModalController.prototype.getJobList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.jobs.getJobList, null);
        };
        UploadCertificateConfirmModalController.prototype.getCurrentUsersAndJobs = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getCurrentUsersAndJobs, null);
        };
        UploadCertificateConfirmModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        UploadCertificateConfirmModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        UploadCertificateConfirmModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return UploadCertificateConfirmModalController;
    }());
    asm.UploadCertificateConfirmModalController = UploadCertificateConfirmModalController;
    angular
        .module('app')
        .controller('UploadCertificateConfirmModalController', UploadCertificateConfirmModalController);
})(asm || (asm = {}));
//# sourceMappingURL=sslConfirmModal.js.map
