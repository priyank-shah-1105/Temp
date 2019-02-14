var asm;
(function (asm) {
    "use strict";
    var DevicePowerSuppliesController = (function () {
        function DevicePowerSuppliesController() {
            var self = this;
            self.refresh();
        }
        DevicePowerSuppliesController.prototype.refresh = function () {
            var self = this;
            self.safeSource = angular.copy(self.device);
            self.deviceType = self.deviceType;
        };
        DevicePowerSuppliesController.$inject = [];
        return DevicePowerSuppliesController;
    }());
    angular.module('app')
        .component('devicePowerSupplies', {
        templateUrl: 'views/devicedetails/powersupplies.html',
        controller: DevicePowerSuppliesController,
        controllerAs: 'devicePowerSuppliesController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=powerSupplies.js.map
