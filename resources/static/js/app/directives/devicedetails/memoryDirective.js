var asm;
(function (asm) {
    "use strict";
    var DeviceMemoryController = (function () {
        function DeviceMemoryController() {
            var self = this;
            self.refresh();
        }
        DeviceMemoryController.prototype.refresh = function () {
            var self = this;
            self.safeSource = angular.copy(self.device.memorydata);
            self.deviceType = self.deviceType;
        };
        DeviceMemoryController.$inject = [];
        return DeviceMemoryController;
    }());
    angular.module('app')
        .component('deviceMemory', {
        templateUrl: 'views/devicedetails/memory.html',
        controller: DeviceMemoryController,
        controllerAs: 'deviceMemoryController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=memoryDirective.js.map
