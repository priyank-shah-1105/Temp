var asm;
(function (asm) {
    var ServiceComplianceReportController = (function () {
        function ServiceComplianceReportController($http, $timeout, $scope, $q, $translate, modal, Loading, Dialog, commands, globalServices, constants, GlobalServices) {
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
            this.constants = constants;
            this.GlobalServices = GlobalServices;
            this.tableView = 'firmware';
            this.errors = new Array();
            var self = this;
            self.id = $scope.modal.params.id || "";
            self.activate();
        }
        ServiceComplianceReportController.prototype.activate = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(self.$q.all([
                self.getFirmwareReport(self.id, self.$scope.modal.params.type || "")
                    .then(function (response) {
                    self.complianceReport = response.data.responseObj;
                    //filter devices so that we only have devices that have either firmwareComponents or softwareComponents
                    self.complianceReport.devices = _.filter(self.complianceReport.devices, function (device) {
                        return (device.firmwareComponents.length > 0 || device.softwareComponents.length > 0);
                    });
                    //fw and sw compliant determined by looking at each firmwarereport and each component therein
                    //if any one component is not compliant, that fr is noncompliant; and even one noncompliant fr will show the messages on the html page
                    //old code:  fwComponents_compcode, swComponents_compcode value of 2 is compliant, value of 1 is not compliant
                    angular.forEach(self.complianceReport.devices, function (report) {
                        angular.extend(report, {
                            firmwareComponents: _.orderBy(report.firmwareComponents, ["type", "compliant"]),
                            softwareComponents: _.orderBy(report.softwareComponents, ["type", "compliant"]),
                            fwComponents_compcode: !!_.find([report.firmwareComponents], function (array) {
                                return _.find(array, function (comp) { return comp.compliant === false; });
                            }) ? "yellow" : "green",
                            swComponents_compcode: !!_.find([report.softwareComponents], function (array) {
                                return _.find(array, function (comp) { return comp.compliant === false; });
                            }) ? "yellow" : "green"
                        });
                    });
                    self.reportsSafe = angular.copy(self.complianceReport.devices);
                    //self.compliant = _.find(self.complianceReport.devices,
                    //        (report: any) => {
                    //            return !!_.find([report.firmwareComponents, report.softwareComponents],
                    //                (array: Array<any>) => _
                    //                .find(array, (report: any) => report.compliant === false));
                    //        })
                    //    ? "yellow"
                    //    : "green";
                }),
                self.getServiceById(self.id, true)
                    .then(function (data) {
                    self.service = data.data.responseObj;
                })
            ])
                .catch(function (data) {
                self.globalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () {
                self.updateDisabled = self.disableUpdate();
            }));
        };
        ServiceComplianceReportController.prototype.disableUpdate = function () {
            var self = this;
            if (!self.GlobalServices.IsInRole('administrator')) {
                return true;
            }
            if (self.service.firmwareCompliant === "compliant") {
                return true;
            }
            if (self.service.health === "unknown" || self.service.health === "servicemode") {
                return true;
            }
            if (_.find(self.complianceReport.devices, function (device) {
                return device.status === "pendingupdates" || device.status === "pendingdelete";
            })) {
                return true;
            }
            return false;
        };
        ServiceComplianceReportController.prototype.updateServiceResource = function () {
            var self = this;
            var updateserviceModal = self.modal({
                title: self.$translate.instant('SERVICE_APPLY_RESOURCE_UPDATES_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('updatefirmwarewizard');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/updateservicefirmware.html',
                controller: 'UpdateServiceFirmwareController as UpdateServiceFirmware',
                params: {
                    id: self.id,
                    mode: 'service'
                },
                onComplete: function () {
                }
            });
            updateserviceModal.modal.show();
            self.$scope.modal.close();
        };
        ServiceComplianceReportController.prototype.getFirmwareReport = function (id, type) {
            var self = this;
            return self.$http.post(self.commands.data.firmwareReport.getfirmwarereport, { id: id, type: type });
        };
        ServiceComplianceReportController.prototype.getServiceById = function (id, scaleup) {
            var self = this;
            return self.$http.post(self.commands.data.services.getServiceById, { id: id, scaleup: scaleup });
        };
        ServiceComplianceReportController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ServiceComplianceReportController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ServiceComplianceReportController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', 'constants', 'GlobalServices'];
        return ServiceComplianceReportController;
    }());
    asm.ServiceComplianceReportController = ServiceComplianceReportController;
    angular
        .module("app")
        .controller("ServiceComplianceReportController", ServiceComplianceReportController);
})(asm || (asm = {}));
//# sourceMappingURL=serviceComplianceReport.js.map
