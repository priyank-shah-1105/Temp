var asm;
(function (asm) {
    "use strict";
    var DeviceIkvmController = (function () {
        function DeviceIkvmController() {
            var self = this;
            self.refresh();
        }
        DeviceIkvmController.prototype.refresh = function () {
            var self = this;
            self.device = self.device;
            self.deviceType = self.deviceType;
        };
        DeviceIkvmController.$inject = [];
        return DeviceIkvmController;
    }());
    angular.module('app')
        .component('deviceIkvm', {
        templateUrl: 'views/devicedetails/ikvm.html',
        controller: DeviceIkvmController,
        controllerAs: 'deviceIkvmController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=ikvm.js.map
