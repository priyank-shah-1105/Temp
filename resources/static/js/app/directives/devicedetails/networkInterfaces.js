var asm;
(function (asm) {
    "use strict";
    var DeviceNetworkInterfacesController = (function () {
        function DeviceNetworkInterfacesController() {
            var self = this;
            self.safeSource = angular.copy(self.device);
        }
        DeviceNetworkInterfacesController.$inject = [];
        return DeviceNetworkInterfacesController;
    }());
    angular.module('app')
        .component('deviceNetworkInterfaces', {
        templateUrl: 'views/devicedetails/networkinterfaces.html',
        controller: DeviceNetworkInterfacesController,
        controllerAs: 'deviceNetworkInterfacesController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=networkInterfaces.js.map
