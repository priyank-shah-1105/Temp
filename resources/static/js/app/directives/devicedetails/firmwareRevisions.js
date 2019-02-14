var asm;
(function (asm) {
    "use strict";
    var DeviceFirmwareController = (function () {
        function DeviceFirmwareController() {
            this.componentSelection = 'fw';
            var self = this;
            self.refresh();
        }
        DeviceFirmwareController.prototype.refresh = function () {
            var self = this;
            self.firmwaressafeSource = angular.copy(self.device.firmwares);
            self.softwaressafeSource = angular.copy(self.device.softwares);
        };
        DeviceFirmwareController.$inject = [];
        return DeviceFirmwareController;
    }());
    angular.module('app')
        .component('deviceFirmware', {
        templateUrl: 'views/devicedetails/firmwarerevisions.html',
        controller: DeviceFirmwareController,
        controllerAs: 'deviceFirmwareController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=firmwareRevisions.js.map
