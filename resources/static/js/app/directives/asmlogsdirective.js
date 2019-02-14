var asm;
(function (asm) {
    "use strict";
    var LogsController = (function () {
        //public availableLogCategories: Array<any> = [
        //    //{ name: 'All' },
        //    { name: 'Security' },
        //    { name: 'Appliance Configuration' },
        //    { name: 'Template Configuration' },
        //    { name: 'Network Configuration' },
        //    { name: 'Hardware Configuration' },
        //    { name: 'Monitoring' },
        //    { name: 'Deployment' },
        //    { name: 'Licensing' },
        //    { name: 'Miscellaneous' }
        //];
        function LogsController($http, $timeout, $q, $translate, Modal, Loading, Commands, GlobalServices, constants, $window) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            this.$window = $window;
            this.selectorConfig = {};
            this.viewModel = {};
            this.currentView = '';
            this.displayedData = [];
            var self = this;
            self.refresh();
        }
        LogsController.prototype.activate = function () {
            var self = this;
            this.$timeout(function () {
                self.refresh();
            }, 10000);
        };
        LogsController.prototype.refresh = function () {
            var self = this;
            var request = self.$http.post(self.Commands.data.logs.getLogs, null)
                .then(function (response) {
                self.viewModel = response.data.responseObj;
                self.displayedData = [].concat(self.viewModel);
                //self.availableLogCategories = _.sortBy(_.uniqBy(response.data.responseObj, 'category'), 'category');
                self.availableLogCategories = _.map(_.sortBy(_.uniqBy(response.data.responseObj, 'category'), 'category'), function (item) {
                    return {
                        name: item.category
                    };
                });
                //self.availableLogCategories = {};
            })
                .catch(function (error) {
                self.GlobalServices.DisplayError(error.data);
            });
            self.Loading(request);
        };
        LogsController.prototype.doDownload = function () {
            var self = this;
            var deferred = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.Loading(deferred.promise);
            self.processDownloadRequests('initial', '', deferred);
        };
        LogsController.prototype.processDownloadRequests = function (type, id, deferred) {
            var self = this;
            var urlToCall = '';
            var data;
            urlToCall = self.Commands.data.downloads.status;
            data = { 'id': id };
            if (type == 'initial') {
                urlToCall = self.Commands.data.downloads.create;
                //urlToCall = 'bogus';
                data = { 'type': 'logs' };
            }
            self.$http.post(urlToCall, { requestObj: data }).then(function (data) {
                switch (data.data.responseObj.status) {
                    case 'NOT_READY':
                        self.$timeout(function () {
                            self.processDownloadRequests('status', data.data.responseObj.id, deferred);
                        }, 5000);
                        break;
                    case 'READY':
                        self.$window.location.assign("downloads/getfile/" + data.data.responseObj.id);
                        deferred.resolve();
                        break;
                    case 'ERROR':
                        //handle error
                        var x = 0;
                        deferred.resolve();
                        self.GlobalServices.DisplayError(data.data);
                        break;
                }
            }).catch(function (data) {
                //need to handle error
                deferred.resolve();
                //error is in data
                self.GlobalServices.DisplayError(data.data);
            });
        };
        LogsController.prototype.doPurge = function () {
            var self = this;
            var purgeModal = self.Modal({
                title: self.$translate.instant('PURGELOGS_Title'),
                modalSize: 'modal-lg',
                templateUrl: 'views/logs/purgelogs.html',
                controller: 'PurgeLogsController as PurgeLogsController',
                params: {},
                onComplete: function () {
                    self.refresh();
                }
            });
            purgeModal.modal.show();
        };
        LogsController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Commands', 'GlobalServices', 'constants', "$window"];
        return LogsController;
    }());
    angular.module('app')
        .component('asmlogs', {
        templateUrl: 'views/logs.html',
        controller: LogsController,
        controllerAs: 'logs',
        bindings: {}
    });
})(asm || (asm = {}));
//# sourceMappingURL=asmlogsdirective.js.map
