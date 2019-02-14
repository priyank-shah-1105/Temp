var asm;
(function (asm) {
    "use strict";
    var JobsController = (function () {
        function JobsController(Modal, Dialog, $http, $timeout, $q, $translate, $rootScope, GlobalServices, commands, loading) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.$rootScope = $rootScope;
            this.GlobalServices = GlobalServices;
            this.commands = commands;
            this.loading = loading;
            var self = this;
            self.buttonActive = false;
            self.checkAllBox = false;
            self.refresh();
            self.activate();
        }
        JobsController.prototype.activate = function () {
            var self = this;
            self.selectedjobs = self.checkselected();
            var jobstimer = self.$timeout(function () {
                self.refresh();
                self.activate();
            }, 60000);
            self.$rootScope.$on('$locationChangeSuccess', function () {
                self.$timeout.cancel(jobstimer);
            });
        };
        JobsController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.loading(d.promise);
            //Get All Jobs
            self.$http.post(self.commands.data.jobs.getJobList, {}).then(function (data) {
                self.results = data.data.responseObj;
                self.displayedresults = [].concat(self.results);
                if (self.checkselected().length == 0) {
                    self.buttonActive = false;
                }
                //when finished getting jobs, reselect the selected and include elapsed time
                angular.forEach(self.results, function (job) {
                    var now;
                    var then = job.startDate;
                    if (job.status !== 'running') {
                        now = job.endDate;
                        var duration = moment(now).diff(moment(then));
                    }
                    if (job.status == 'running') {
                        now = moment();
                        var duration = moment(now).diff(moment(then));
                    }
                    if (duration) {
                        var d = moment.duration(duration).humanize();
                        job.elapsedTime = d;
                    }
                    job.isSelected = self.selectedjobs.length >= 1 && !!_.find(self.selectedjobs, { id: job.id });
                });
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            }).finally(function () {
                d.resolve();
            });
        };
        ;
        JobsController.prototype.checkselected = function () {
            var self = this;
            return _.filter(self.results, { 'isSelected': true });
        };
        JobsController.prototype.cancelButtonActive = function () {
            //verify that all selected jobs are 'scheduled' and that at least one selected value is checked
            var self = this, returnVal = true;
            if (self.results) {
                self.results.forEach(function (x) {
                    if (x.isSelected && x.status !== 'scheduled') {
                        returnVal = false;
                    }
                });
            }
            return returnVal;
        };
        //check all checkbox
        JobsController.prototype.checkAll = function () {
            var self = this;
            self.results.forEach(function (job) {
                if (self.checkAllBox) {
                    job.isSelected = true;
                }
                else {
                    job.isSelected = false;
                }
            });
        };
        ;
        JobsController.prototype.cancelJob = function () {
            var self = this, selectedusers = _.map(self.checkselected(), 'id'), d = self.$q.defer();
            self.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('SETTINGS_ConfirmJobCancel')))
                .then(function () {
                self.GlobalServices.ClearErrors();
                self.loading(d.promise);
                self.$http.post(self.commands.data.jobs.deleteJob, selectedusers)
                    .then(function (data) {
                    self.refresh();
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                }).finally(function () { return d.resolve(); });
                //self.checkselected() = [];
            });
        };
        JobsController.prototype.modalTest = function () {
            var self = this;
            var modalJobs = [];
            //angular loop
            //Move Selected jobs into an array
            self.displayedresults.forEach(function (job) {
                if (job.isSelected) {
                    modalJobs.push(job.id);
                }
            });
            var testModal = self.Modal({
                title: 'Jobs Test Modal Title',
                modalSize: 'modal-lg',
                templateUrl: 'views/testmodal.html',
                controller: 'TestModalController as TestModal',
                params: {
                    jobs: modalJobs
                },
                close: function (modalScope) {
                    self.refresh();
                }
            });
            testModal.modal.show();
        };
        JobsController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', '$rootScope', 'GlobalServices', "Commands", "Loading"];
        return JobsController;
    }());
    angular.module('app')
        .component('jobs', {
        templateUrl: 'views/jobs.html',
        controller: JobsController,
        controllerAs: 'jobs',
        bindings: {}
    });
})(asm || (asm = {}));
//# sourceMappingURL=jobsdirective.js.map
