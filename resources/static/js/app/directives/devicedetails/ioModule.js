var asm;
(function (asm) {
    "use strict";
    var DeviceIOModuleController = (function () {
        function DeviceIOModuleController() {
            var self = this;
            self.refresh();
        }
        DeviceIOModuleController.prototype.refresh = function () {
            var self = this;
            self.safeSource = angular.copy(self.device);
            self.deviceType = self.deviceType;
        };
        DeviceIOModuleController.$inject = [];
        return DeviceIOModuleController;
    }());
    angular.module('app')
        .component('deviceIoModule', {
        templateUrl: 'views/devicedetails/iomodule.html',
        controller: DeviceIOModuleController,
        controllerAs: 'deviceIOModuleController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=ioModule.js.map
