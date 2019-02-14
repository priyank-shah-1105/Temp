var asm;
(function (asm) {
    var VxRackFlexAlertConnectorModalController = (function () {
        function VxRackFlexAlertConnectorModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            this.settings = {};
            this.ranges = {
                days: 31,
                hours: 24,
                minutes: 60,
                seconds: 60
            };
            this.errors = [];
            this.alertFilterOptions = [];
            this.hours = [];
            this.deviceTypeOptions = [];
            var self = this;
            self.initialize();
        }
        VxRackFlexAlertConnectorModalController.prototype.initialize = function () {
            var self = this;
            self.alertFilterOptions = [
                { id: "Critical", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Critical") },
                { id: "Warning", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Warning") },
                { id: "Info", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Info") }
            ];
            //create options array for hours
            _.times(24, function (n) { return self.hours.push(n); });
            self.deviceTypeOptions = [
                { id: "vxrackflex", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_VxRackFlex") }
            ];
            self.settings = self.$scope.modal.params.settings;
        };
        VxRackFlexAlertConnectorModalController.prototype.getAlertMinutes = function (hours) {
            //return an array of only valid values to enforce minimum number of time allowed for polling
            var self = this;
            var minimumMinutes = 0;
            if (hours == 0) {
                minimumMinutes = 5;
                if (self.settings.alertPollingIntervalMinutes < minimumMinutes) {
                    self.settings.alertPollingIntervalMinutes = minimumMinutes;
                }
            }
            //create new array with a size of 60 - excluded values, push numbers into it
            return _.map(new Array(60 - minimumMinutes), function (n, index) { return (index + minimumMinutes); });
        };
        VxRackFlexAlertConnectorModalController.prototype.save = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.applianceManagement.setVxRackSettingsRegister, self.settings)
                .then(function (data) {
                self.close();
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); })
                .finally(function () { return d.resolve(); });
        };
        VxRackFlexAlertConnectorModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        VxRackFlexAlertConnectorModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        VxRackFlexAlertConnectorModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return VxRackFlexAlertConnectorModalController;
    }());
    asm.VxRackFlexAlertConnectorModalController = VxRackFlexAlertConnectorModalController;
    angular
        .module('app')
        .controller('VxRackFlexAlertConnectorModalController', VxRackFlexAlertConnectorModalController);
})(asm || (asm = {}));
//# sourceMappingURL=vxRackFlexAlertConnector.js.map
