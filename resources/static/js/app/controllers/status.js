var asm;
(function (asm) {
    var StatusController = (function () {
        function StatusController($window, $http, $timeout, $q, $router, Commands, $translate) {
            this.$window = $window;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$router = $router;
            this.Commands = Commands;
            this.$translate = $translate;
            this.refreshTimer = null;
            var self = this;
            self.copywriteStatement = self.$translate.instant("GENERIC_CopywriteStatement", { year: new Date().getFullYear() });
            this.getApplianceStatus();
        }
        StatusController.prototype.$onDestroy = function () {
            var self = this;
            if (self.refreshTimer)
                self.$timeout.cancel(self.refreshTimer);
        };
        StatusController.prototype.canDeactivate = function () {
            var self = this;
            var allow = true;
            //Any logic to prevent navigating away (change allow to false to prevent)
            return allow;
        };
        StatusController.prototype.getJobsInProgressTranslation = function (jobsInProgress) {
            var self = this;
            return self.$translate.instant("GETTINGSTARTED_jobsinprogress", { jobsInProgress: jobsInProgress });
        };
        StatusController.prototype.getApplianceStatus = function () {
            var self = this;
            self.$http.post(self.Commands.data.applianceManagement.getStatus, null)
                .success(function (data, status, headers, config) {
                self.status = data.responseObj;
                if (self.status === 'ready') {
                    self.$window.location.href = 'login.html';
                    return;
                }
                self.refreshTimer = self.$timeout(function () {
                    self.getApplianceStatus();
                }, 15000);
            })
                .error(function (data, status, headers, config) {
                self.status = 'notready';
                self.refreshTimer = self.$timeout(function () {
                    self.getApplianceStatus();
                }, 15000);
            });
        };
        StatusController.$inject = ['$window', '$http', '$timeout', '$q', '$router', 'Commands', "$translate"];
        return StatusController;
    }());
    asm.StatusController = StatusController;
    angular
        .module('app')
        .controller('StatusController', StatusController);
})(asm || (asm = {}));
//# sourceMappingURL=status.js.map
