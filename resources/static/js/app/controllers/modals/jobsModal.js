var asm;
(function (asm) {
    var JobsModalController = (function () {
        function JobsModalController(Modal, Dialog, $http, $timeout, $q, $translate, $rootScope, GlobalServices, commands, loading) {
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
            this.errors = new Array();
            var self = this;
            self.buttonActive = false;
            self.checkAllBox = false;
            self.refresh();
            self.activate();
        }
        JobsModalController.prototype.activate = function () {
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
        JobsModalController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            //Get All Jobs
            self.$http.post(self.commands.data.jobs.getJobList, {}).then(function (data) {
                self.results = data.data.responseObj;
                self.displayedresults = [].concat(self.results);
                if (self.checkselected().length == 0) {
                    self.buttonActive = false;
                }
                //when finished getting jobs, reselect the selected and include elapsed time
                angular.forEach(_.filter(self.results, function (job) {
                    return job.endDate || job.status === 'running';
                }), function (job) {
                    var now = job.endDate ? moment(job.endDate) : moment(), then = moment(job.startDate), duration = moment.duration(now.diff(then)), days = Math.floor(duration.asDays()), hours = Math.floor(duration.asHours()), minutes = Math.floor(duration.asMinutes());
                    if (days === 0) {
                        job.elapsedTime = self.$translate.instant('SETTINGS_aday');
                    }
                    else if (days > 0) {
                        job.elapsedTime = self.$translate.instant('SETTINGS_numDays', { number: days });
                    }
                    else if (hours === 0) {
                        job.elapsedTime = self.$translate.instant('SETTINGS_anhour');
                    }
                    else if (hours > 0) {
                        job.elapsedTime = self.$translate.instant('SETTINGS_numHours', { number: hours });
                    }
                    else if (minutes === 0) {
                        job.elapsedTime = self.$translate.instant('SETTINGS_amimute');
                    }
                    else if (minutes > 0) {
                        job.elapsedTime = self.$translate.instant('SETTINGS_numMinutes', { number: minutes });
                    }
                    job.isSelected = self.selectedjobs.length >= 1 && !!_.find(self.selectedjobs, { id: job.id });
                });
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            }).finally(function () {
                d.resolve();
            });
        };
        ;
        JobsModalController.prototype.checkselected = function () {
            var self = this;
            return _.filter(self.results, { 'isSelected': true });
        };
        JobsModalController.prototype.cancelButtonActive = function () {
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
        JobsModalController.prototype.checkAll = function () {
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
        //cancel job
        JobsModalController.prototype.cancelJob = function () {
            var self = this, selectedusers = _.map(self.checkselected(), 'id'), d = self.$q.defer();
            self.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('SETTINGS_ConfirmJobCancel')))
                .then(function () {
                self.GlobalServices.ClearErrors(self.errors);
                self.loading(d.promise);
                self.$http.post(self.commands.data.jobs.deleteJob, selectedusers)
                    .then(function (data) {
                    self.refresh();
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                }).finally(function () { return d.resolve(); });
                //self.checkselected() = [];
            });
        };
        JobsModalController.prototype.modalTest = function () {
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
        JobsModalController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', '$rootScope', 'GlobalServices', "Commands", "Loading"];
        return JobsModalController;
    }());
    asm.JobsModalController = JobsModalController;
    angular
        .module('app')
        .controller('JobsModalController', JobsModalController);
})(asm || (asm = {}));
//# sourceMappingURL=jobsModal.js.map
