var asm;
(function (asm) {
    var ResourceComplianceReportController = (function () {
        function ResourceComplianceReportController($http, $timeout, $scope, $q, $translate, modal, Loading, Dialog, commands, globalServices, FileUploader, constants, $filter, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.modal = modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.commands = commands;
            this.globalServices = globalServices;
            this.FileUploader = FileUploader;
            this.constants = constants;
            this.$filter = $filter;
            this.GlobalServices = GlobalServices;
            this.compliant = "green";
            this.errors = new Array();
            var self = this;
            self.activate();
        }
        ResourceComplianceReportController.prototype.activate = function () {
            var self = this, d = self.$q.defer(), firmwareCompliant, softwareCompliant;
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.getFirmwareReport(self.$scope.modal.params.id, "resource")
                    .then(function (response) {
                    self.report = response.data.responseObj;
                    self.reportObj = response.data.responseObj.devices[0];
                    self.reportObj.firmwareComponents = _.orderBy(self.reportObj.firmwareComponents, ["compliant"]);
                    self.reportObj.softwareComponents = _.orderBy(self.reportObj.softwareComponents, ["compliant"]);
                    firmwareCompliant = !_.find(self.reportObj.firmwareComponents, { compliant: false }),
                        softwareCompliant = !_.find(self.reportObj.softwareComponents, { compliant: false });
                    //logic to determine radio button default view selection of firmware or software
                    //default to firmware, unless there is a reson to view software due to greater importance/criticality
                    //possible compliance values, in increasting order of importance, are unknown (n/a), compliant (success), noncompliant (warning), updaterequired (critical)
                    //note:  this only applies to resources (not services) because there would have to be a way to determine which firmware reports mattered with the results of the tests below
                    angular.extend(self.reportObj, {
                        softwareComponentsSafe: angular.copy(self.reportObj.softwareComponents),
                        firmwareComponentsSafe: angular.copy(self.reportObj.firmwareComponents)
                    });
                    self.componentSelection = (firmwareCompliant && !softwareCompliant) ? "sw" : "fw";
                }),
                self.getDeviceById(self.$scope.modal.params.id)
                    .then(function (data) {
                    self.resource = data.data.responseObj;
                })
            ])
                .catch(function (data) {
                self.globalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () {
                if (!firmwareCompliant || !softwareCompliant) {
                    self.compliant = "yellow";
                }
                if (self.resource.compliant === "noncompliant") {
                    self.compliant = "yellow";
                }
                self.updateDisabled = self.disableUpdate();
                d.resolve();
            });
        };
        ResourceComplianceReportController.prototype.isServer = function (device) {
            return (device.deviceType === 'RackServer' || device.deviceType === 'TowerServer' || device.deviceType === 'BladeServer' || device.deviceType === 'FXServer' || device.deviceType === 'Server');
        };
        ResourceComplianceReportController.prototype.isDellSwitch = function (device) {
            return (device.deviceType === 'dellswitch' || device.deviceType === 'genericswitch');
        };
        ResourceComplianceReportController.prototype.isCiscoSwitch = function (device) {
            return (device.deviceType === 'ciscoswitch');
        };
        ResourceComplianceReportController.prototype.canUpdateFirmware = function (device) {
            return !(device.deviceType === 'genericswitch' || device.deviceType === 'scaleio' || device.deviceType === 'vcenter' || device.deviceType === 'em');
        };
        ResourceComplianceReportController.prototype.disableUpdate = function () {
            var self = this;
            if (!self.GlobalServices.IsInRole('administrator')) {
                return true;
            }
            if (!self.resource) {
                return true;
            }
            if (!self.canUpdateFirmware(self.resource)) {
                return true;
            }
            if (self.resource.availability != 'notinuse' && self.resource.status != 'ready') {
                return true;
            }
            if (self.$filter("isTypeChassis")(self.resource) && self.resource.availability == 'inuse') {
                return true;
            }
            if (self.resource.availability === 'inuse' && self.isServer(self.resource)) {
                return true;
            }
            if (self.resource.compliant == 'compliant' || self.resource.compliant == 'updating') {
                return true;
            }
            if (self.resource.state === 'unmanaged') {
                return true;
            }
            if (self.isStorage(self.resource)) {
                return true;
            }
            if (angular.isDefined(self.reportObj.status) && self.reportObj.status === "pendingupdates" || self.reportObj.status === "pendingdelete") {
                return true;
            }
            return false;
        };
        ResourceComplianceReportController.prototype.updateResource = function () {
            var self = this;
            ;
            var updatedeviceModal = self.modal({
                title: self.$translate.instant('SERVICE_APPLY_RESOURCE_UPDATES_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('updatefirmwarewizard');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/updatedevicefirmware.html',
                controller: 'UpdateDeviceFirmwareModalController as updatedeviceFirmware',
                params: {
                    resource: self.resource
                },
                onComplete: function () {
                    self.$scope.modal.close();
                }
            });
            updatedeviceModal.modal.show();
        };
        ResourceComplianceReportController.prototype.isStorage = function (device) {
            return device.deviceType == 'storage' || device.deviceType == 'compellent' || device.deviceType == 'equallogic' || device.deviceType == 'netapp' || device.deviceType == 'emcvnx' || device.deviceType == 'emcunity';
        };
        ResourceComplianceReportController.prototype.getDeviceById = function (id) {
            var self = this;
            return self.$http.post(self.commands.data.devices.getDeviceById, { id: id });
        };
        ResourceComplianceReportController.prototype.getFirmwareReport = function (id, type) {
            var self = this;
            return self.$http.post(self.commands.data.firmwareReport.getfirmwarereport, { id: id, type: type });
        };
        ResourceComplianceReportController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ResourceComplianceReportController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ResourceComplianceReportController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal',
            'Loading', 'Dialog', 'Commands', 'GlobalServices', 'FileUploader', 'constants', '$filter', 'GlobalServices'];
        return ResourceComplianceReportController;
    }());
    asm.ResourceComplianceReportController = ResourceComplianceReportController;
    angular
        .module("app")
        .controller("ResourceComplianceReportController", ResourceComplianceReportController);
})(asm || (asm = {}));
//# sourceMappingURL=resourceComplianceReport.js.map
