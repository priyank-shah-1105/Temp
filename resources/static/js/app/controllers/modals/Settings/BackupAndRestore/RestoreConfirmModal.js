var asm;
(function (asm) {
    var RestoreConfirmModalController = (function () {
        function RestoreConfirmModalController($scope, Modal, Dialog, $http, $q, $timeout, Loading, GlobalServices, FileUploader, Commands, $translate) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$q = $q;
            this.$timeout = $timeout;
            this.Loading = Loading;
            this.GlobalServices = GlobalServices;
            this.FileUploader = FileUploader;
            this.Commands = Commands;
            this.$translate = $translate;
            this.bullets = {};
            this.errors = new Array();
            var self = this, d = self.$q.defer();
            self.Loading(d.promise);
            self.$q.all([
                self.getJobList()
                    .then(function (data) {
                    angular.merge(self.bullets, {
                        numScheduledJobs: _.filter(data.data.responseObj, { status: "scheduled" }).length
                    });
                }),
                self.getCurrentUsersAndJobs()
                    .then(function (data) {
                    angular.merge(self.bullets, {
                        numUsers: data.data.responseObj.currentusers,
                        numProgressJobs: data.data.responseObj.pendingjobs
                    });
                })
            ])
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        }
        RestoreConfirmModalController.prototype.getJobList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.jobs.getJobList, null);
        };
        RestoreConfirmModalController.prototype.getCurrentUsersAndJobs = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getCurrentUsersAndJobs, null);
        };
        RestoreConfirmModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        RestoreConfirmModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        RestoreConfirmModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$q', '$timeout', 'Loading', 'GlobalServices', 'FileUploader', 'Commands', '$translate'];
        return RestoreConfirmModalController;
    }());
    asm.RestoreConfirmModalController = RestoreConfirmModalController;
    angular
        .module('app')
        .controller('RestoreConfirmModalController', RestoreConfirmModalController);
})(asm || (asm = {}));
//# sourceMappingURL=RestoreConfirmModal.js.map
