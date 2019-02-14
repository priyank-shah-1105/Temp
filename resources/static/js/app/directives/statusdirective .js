var asm;
(function (asm) {
    'use strict';
    var StatusController = (function () {
        function StatusController(Modal, Dialog, $http, $timeout, $q, $compile, $scope, $translate, GlobalServices, Commands, $rootScope) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$compile = $compile;
            this.$scope = $scope;
            this.$translate = $translate;
            this.GlobalServices = GlobalServices;
            this.Commands = Commands;
            this.$rootScope = $rootScope;
            this.showStatuses = false;
            this.oobe = {};
            var self = this;
            self.testMessage = 'Statuses go here';
            //self.refresh();
            self.$rootScope.ASM.jobsInterval = window.setInterval(function () { self.refresh(); }, 30000);
            self.$rootScope.$on('$locationChangeSuccess', function () { self.showStatuses = false; });
        }
        StatusController.prototype.$onDestroy = function () {
            var self = this;
            if (self.$rootScope.ASM.jobsInterval)
                window.clearInterval(self.$rootScope.ASM.jobsInterval);
        };
        StatusController.prototype.refresh = function () {
            var self = this;
            self.$http.post(self.Commands.data.initialSetup.gettingStarted, null)
                .then(function (data) {
                self.oobe = data.data.responseObj;
                self.GlobalServices.gettingStarted = data.data.responseObj;
                self.GlobalServices.showInitialSetup = !self.GlobalServices.gettingStarted.initialSetupCompleted;
                self.$http.post(self.Commands.data.jobs.getJobList, null)
                    .then(function (data) {
                    var jobs = data.data.responseObj;
                    var x = _.filter(jobs, { 'endDate': null, 'status': 'running' });
                    self.jobsinprogress = x.length;
                })
                    .catch(function (response) { self.GlobalServices.DisplayError(response.data); });
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); });
        };
        StatusController.prototype.getJobsInProgressTranslation = function (jobsInProgress) {
            var self = this;
            return self.$translate.instant("GETTINGSTARTED_jobsinprogress", { jobsInProgress: jobsInProgress });
        };
        StatusController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$compile', '$scope', '$translate', 'GlobalServices', 'Commands', '$rootScope'];
        return StatusController;
    }());
    function status() {
        return {
            restrict: 'E',
            templateUrl: 'views/statusmasthead.html',
            replace: true,
            transclude: false,
            controller: StatusController,
            controllerAs: 'status',
            link: function (scope, element, attributes) {
            }
        };
    }
    angular.module('app').
        directive('status', status);
})(asm || (asm = {}));
//# sourceMappingURL=statusdirective .js.map
