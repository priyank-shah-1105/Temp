var asm;
(function (asm) {
    var PurgeLogsController = (function () {
        function PurgeLogsController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.olderThanDisplayIsValid = true;
            this.severityinformation = false;
            this.severitywarning = false;
            this.severitycritical = false;
            this.errors = new Array();
            this.datePicker = {
                options: {
                    format: "L",
                    maxDate: moment()
                },
                selectedDate: moment().subtract(1, 'hour')
            };
            var self = this;
            self.olderThanDisplay = moment(new Date()).format('YYYY-MM-DD');
        }
        PurgeLogsController.prototype.doPurge = function () {
            var self = this;
            var date = moment(self.olderThanDisplay).toDate();
            self.olderThan = date;
            var formattedDate = moment(self.olderThanDisplay).format('YYYY-MM-DD');
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('PURGELOGS_Confirmation'))
                .then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.logs.purgeLogs, {
                    olderThan: self.olderThan,
                    olderThanDisplay: formattedDate,
                    severityinformation: self.severityinformation,
                    severitywarning: self.severitywarning,
                    severitycritical: self.severitycritical
                })
                    .then(function (data) {
                    self.close();
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        PurgeLogsController.prototype.validateForm = function () {
            var self = this;
            self.olderThanDisplayIsValid = moment(self.olderThanDisplay, ["MM/DD/YYYY", "MM-DD-YYYY", "YYYY-MM-DD"], true)
                .isValid();
        };
        PurgeLogsController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        PurgeLogsController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return PurgeLogsController;
    }());
    asm.PurgeLogsController = PurgeLogsController;
    angular
        .module('app')
        .controller('PurgeLogsController', PurgeLogsController);
})(asm || (asm = {}));
//# sourceMappingURL=purgelogs.js.map
