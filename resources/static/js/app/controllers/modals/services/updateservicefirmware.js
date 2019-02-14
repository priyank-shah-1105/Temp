var asm;
(function (asm) {
    /*
    Parameters:
    id : string
     */
    var UpdateServiceFirmwareController = (function () {
        function UpdateServiceFirmwareController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, modal) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.modal = modal;
            this.datePicker = {
                options: {
                    format: "L hh:mm A",
                    minDate: moment(),
                },
                selectedDate: null,
            };
            this.update = {
                idList: [],
                scheduleType: 'updatenow',
                exitMaintenanceMode: false,
                scheduleDate: null
            };
            this.id = [];
            this.devices = [];
            this.errors = new Array();
            var self = this;
            self.mode = self.$scope.modal.params.mode;
            self.devices = $scope.modal.params.ids;
            self.id = $scope.modal.params.id;
            if (self.mode == "device") {
                self.update.idList = self.devices;
            }
            else {
                self.update.idList = [self.id];
            }
        }
        UpdateServiceFirmwareController.prototype.doSave = function () {
            var self = this, d = self.$q.defer();
            if (self.update.scheduleType === "schedule") {
                self.update.scheduleDate = self.datePicker.selectedDate.toISOString();
            }
            var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('UPDATE_SERVICE_FIRMWARE_Confirmation'));
            confirm.then(function () {
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                if (self.mode == 'device') {
                    self.$http.post(self.Commands.data.devices.updatedevicefirmware, self.update)
                        .then(function (data) {
                        self.$scope.modal.close();
                    })
                        .catch(function (data) {
                        self.GlobalServices.DisplayError(data.data, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
                if (self.mode == 'service') {
                    self.$http.post(self.Commands.data.services.updateservicefirmware, self.update)
                        .then(function (data) {
                        self.$scope.modal.close();
                    })
                        .catch(function (data) {
                        self.GlobalServices.DisplayError(data.data, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
                if (self.mode == 'undefined') {
                    console.log('mode is undefined');
                }
            })
                .catch(function () { self.form._submitted = false; });
        };
        UpdateServiceFirmwareController.prototype.dateIsValid = function () {
            var self = this;
            return self.update.scheduleType === "updatenow" || (self.datePicker.selectedDate && moment().isBefore(self.datePicker.selectedDate.toISOString()));
        };
        UpdateServiceFirmwareController.prototype.formIsValid = function () {
            var self = this;
            return self.form.$valid && self.dateIsValid();
        };
        UpdateServiceFirmwareController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        UpdateServiceFirmwareController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices', "Modal"];
        return UpdateServiceFirmwareController;
    }());
    asm.UpdateServiceFirmwareController = UpdateServiceFirmwareController;
    angular
        .module('app')
        .controller('UpdateServiceFirmwareController', UpdateServiceFirmwareController);
})(asm || (asm = {}));
//# sourceMappingURL=updateservicefirmware.js.map
