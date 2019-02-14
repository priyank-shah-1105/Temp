var asm;
(function (asm) {
    var UpdateDeviceFirmwareModalController = (function () {
        function UpdateDeviceFirmwareModalController($scope, Modal, Dialog, $http, $q, $timeout, Loading, GlobalServices, FileUploader, $translate, constants, commands, $location, $filter) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$q = $q;
            this.$timeout = $timeout;
            this.Loading = Loading;
            this.GlobalServices = GlobalServices;
            this.FileUploader = FileUploader;
            this.$translate = $translate;
            this.constants = constants;
            this.commands = commands;
            this.$location = $location;
            this.$filter = $filter;
            //public helpUrl: ASM.urlConfig.help.resourcesupdatingfirmware,
            this.scheduletype = "updatenow";
            this.updateEstimate = '';
            this.now_CancelMaintenance = false;
            this.scheduled_CancelMaintenance = false;
            this.enteredDate = moment(new Date()).format('YYYY-MM-DD');
            this.enteredHour = '01';
            this.enteredMinute = '00';
            this.enteredMeridiem = 'a';
            this.hoursList = [];
            this.minutesList = [];
            this.confirmMessage = '';
            this.confirmIsAlert = false;
            this.confirmCallbackTrue = null;
            this.confirmCallbackFalse = null;
            //public devices: new Collection([], { model: Device });
            this.devices = [];
            this.deviceType = '';
            this.isUpdated = false;
            this.id = '';
            this.datePicker = {
                options: {
                    format: "L hh:mm A",
                    minDate: moment(),
                },
                selectedDate: moment().add(1, 'hour'),
            };
            this.jobRequest = {
                requestObj: {
                    idList: [],
                    scheduleType: 'updatenow',
                    exitMaintenanceMode: false,
                    scheduleDate: moment()
                }
            };
            this.submitted = false;
            this.errors = new Array();
            var self = this;
            self.resource = $scope.modal.params.resource;
            //            self.devices = $scope.modal.params.ids != undefined ? $scope.modal.params.ids : $scope.modal.params.resource.id;
            self.devices = [];
            self.update();
        }
        UpdateDeviceFirmwareModalController.prototype.update = function () {
            var self = this;
            //Fill the "Time" arrays.
            self.hoursList = [];
            self.minutesList = [];
            for (var c = 0; c < 60; c++) {
                var value = '00' + c;
                value = value.substr(value.length - 2);
                if (c > 0 && c < 13)
                    self.hoursList.push(value);
                self.minutesList.push(value);
            }
            if (self.resource) {
                self.devices.push(self.resource.id);
            }
            if (self.resource && self.devices.length > 0) {
                if (self.GlobalServices.IsDeviceType(self.resource.deviceType, "Server")) {
                    self.deviceType = 'Server';
                }
                if (self.GlobalServices.IsDeviceType(self.resource.deviceType, "Chassis")) {
                    self.deviceType = 'Chassis';
                }
                if (self.GlobalServices.IsDeviceType(self.resource.deviceType, "Switch")) {
                    self.deviceType = 'Switch';
                }
            }
        };
        UpdateDeviceFirmwareModalController.prototype.updateScheduleDate = function () {
            var self = this;
            var dateString = self.enteredDate;
            var timeWeekly = self.enteredHour + ':' + self.enteredMinute;
            var ampm = self.enteredMeridiem;
            var isotimeWeekly = moment(dateString + 'T' + timeWeekly + ampm, "YYYY-MM-DDThh:mmA").toDate();
            var scheduleDate = moment(isotimeWeekly) != undefined ? moment(isotimeWeekly).toISOString() : '';
            self.scheduledate = scheduleDate;
        };
        UpdateDeviceFirmwareModalController.prototype.submit = function () {
            var self = this;
            var d = self.$q.defer();
            //    var valid = true;
            self.submitted = true;
            //    var request = new UpdateDeviceFirmwareRequest();
            var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('UPDATE_SERVICE_FIRMWARE_Confirmation'));
            confirm.then(function () {
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.jobRequest.requestObj.scheduleType = self.scheduletype;
                if (self.jobRequest.requestObj.scheduleType == "updatenow") {
                    self.jobRequest.requestObj.exitMaintenanceMode = true;
                }
                if (self.jobRequest.requestObj.scheduleType == "schedule") {
                    self.jobRequest.requestObj.exitMaintenanceMode = false;
                    if (self.datePicker.selectedDate) {
                        self.validDate = moment().isBefore(self.datePicker.selectedDate.toISOString());
                        if (!self.validDate) {
                            return;
                        }
                    }
                    else {
                        return;
                    }
                }
                else {
                    //scheduleType = nextreboot
                    self.jobRequest.requestObj.exitMaintenanceMode = false;
                }
                self.jobRequest.requestObj.scheduleDate = self.jobRequest.requestObj.scheduleType === "schedule" ? self.datePicker.selectedDate.toISOString() : null;
                self.jobRequest.requestObj.idList = self.devices;
                //$.each(self.devices, function (idx, d) { selectedDevices.push(d.id); });
                self.$http.post(self.commands.data.devices.updatedevicefirmware, self.jobRequest.requestObj)
                    .then(function (data) {
                    self.$scope.modal.close();
                }).catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                }).finally(function () { return d.resolve(); });
            });
        };
        UpdateDeviceFirmwareModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        UpdateDeviceFirmwareModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$q', '$timeout', 'Loading', 'GlobalServices', 'FileUploader', '$translate', 'constants', 'Commands', '$location', '$filter'];
        return UpdateDeviceFirmwareModalController;
    }());
    asm.UpdateDeviceFirmwareModalController = UpdateDeviceFirmwareModalController;
    angular
        .module('app')
        .controller('UpdateDeviceFirmwareModalController', UpdateDeviceFirmwareModalController);
})(asm || (asm = {}));
//# sourceMappingURL=updatedevicefirmwareModal.js.map
