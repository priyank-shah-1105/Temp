var asm;
(function (asm) {
    var VxRackFlexAlertConnectorComponentController = (function () {
        function VxRackFlexAlertConnectorComponentController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            this.ranges = {
                days: 31,
                hours: 24,
                minutes: 60,
                seconds: 60
            };
            this.alertFilterOptions = [];
            this.hours = [];
            this.deviceTypeOptions = [];
            this.connectionTypeOptions = [];
            var self = this;
            self.initialize();
        }
        VxRackFlexAlertConnectorComponentController.prototype.initialize = function () {
            var self = this;
            self.alertFilterOptions = [
                { id: "Critical", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Critical") },
                { id: "Warning", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Warning") },
                { id: "Info", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Info") }
            ];
            //create options array for hours
            _.times(24, function (n) { return self.hours.push(n); });
            self.connectionTypeOptions = [
                { id: "srs", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_ConnectionType_SRS") },
                { id: "phonehome", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_ConnectionType_PhoneHome") }
            ];
            self.deviceTypeOptions = [
                { id: "vxrackflex", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_VxRackFlex") },
                { id: "vxflexappliance", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_VxFlexAppliance") }
            ];
            //self.settings.connectionType = self.connectionType;
            self.setDeviceType();
            //hardcode the below fields since the user can't change them for now.
            //May need to remove these hard coded values when these values can be updated by the user in the future.
            self.settings.srsGatewayHostPort = 9443;
            self.editMode = self.settings.state !== "None";
        };
        VxRackFlexAlertConnectorComponentController.prototype.setDeviceType = function () {
            var self = this;
            self.settings.deviceType = self.settings.connectionType == "srs" ? "vxrackflex" : "vxflexappliance";
        };
        VxRackFlexAlertConnectorComponentController.prototype.getAlertMinutes = function (hours) {
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
        VxRackFlexAlertConnectorComponentController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return VxRackFlexAlertConnectorComponentController;
    }());
    asm.VxRackFlexAlertConnectorComponentController = VxRackFlexAlertConnectorComponentController;
    angular.module('app')
        .component('vxRackFlexAlertConnectorComponent', {
        templateUrl: 'views/settings/virtualappliancemanagement/vxrackflexalertconnectorcomponent.html',
        controller: VxRackFlexAlertConnectorComponentController,
        controllerAs: 'vxRackFlexAlertConnectorComponentController',
        bindings: {
            settings: "=",
            errors: "=?",
            form: "=?",
            submitted: "<?",
            readOnly: "<?",
            disabled: "<?",
            connectionType: "=",
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=vxRackFlexAlertConnectorComponent.js.map
