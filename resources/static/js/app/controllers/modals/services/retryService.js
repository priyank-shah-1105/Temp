var asm;
(function (asm) {
    var RetryServiceController = (function () {
        function RetryServiceController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            this.doAltAction = false;
            this.showAltAction = false;
            var self = this;
            if ($scope.modal.params.service) {
                self.service = $scope.modal.params.service;
                self.serviceId = self.service.id;
            }
            if ($scope.modal.params.showAlternateAction) {
                self.showAltAction = angular.copy($scope.modal.params.showAlternateAction);
            }
            self.initialize();
        }
        RetryServiceController.prototype.initialize = function () {
            var self = this;
        };
        RetryServiceController.prototype.retryService = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.httpRetry()
                .then(function (data) {
                self.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        RetryServiceController.prototype.openFirmwareReport = function () {
            var self = this;
            var firmwareReportModal = self.Modal({
                title: self.$translate.instant('SERVICES_SERVICE_FirmwareReportTitle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('viewfirmwarecomplianceservice');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/servicecompliancereport.html',
                controller: 'ServiceComplianceReportController as serviceComplianceReportController',
                params: {
                    type: "service",
                    id: self.serviceId
                },
                onComplete: function () {
                },
                onCancel: function () {
                    firmwareReportModal.modal.dismiss();
                }
            });
            firmwareReportModal.modal.show();
        };
        RetryServiceController.prototype.showAlternateAction = function () {
            var self = this;
            return self.showAltAction;
        };
        RetryServiceController.prototype.doAlternateAction = function () {
            var self = this;
            //set a return value, check it on return, call doUpdateFirmware() in parent as necessary/true
            self.doAltAction = true;
            self.$scope.modal.close(self.doAltAction);
        };
        RetryServiceController.prototype.httpRetry = function () {
            var self = this;
            return self.$http.post(self.Commands.data.services.retryService, { id: self.serviceId });
        };
        RetryServiceController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        RetryServiceController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        RetryServiceController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return RetryServiceController;
    }());
    asm.RetryServiceController = RetryServiceController;
    angular
        .module('app')
        .controller('RetryServiceController', RetryServiceController);
})(asm || (asm = {}));
//# sourceMappingURL=retryService.js.map
