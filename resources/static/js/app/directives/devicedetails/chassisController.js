var asm;
(function (asm) {
    "use strict";
    var DeviceChassisController = (function () {
        function DeviceChassisController() {
        }
        DeviceChassisController.prototype.isFX2 = function () {
            //note:  easier to just re-calculate this value than to pass it in
            //https://docs.angularjs.org/guide/interpolation
            return (this.deviceType === 'ChassisFX');
        };
        DeviceChassisController.$inject = [];
        return DeviceChassisController;
    }());
    angular.module('app')
        .component('deviceChassisController', {
        templateUrl: 'views/devicedetails/chassiscontroller.html',
        controller: DeviceChassisController,
        controllerAs: 'deviceChassisController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=chassisController.js.map
