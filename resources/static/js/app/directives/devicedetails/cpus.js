var asm;
(function (asm) {
    "use strict";
    var DeviceCpusController = (function () {
        function DeviceCpusController() {
            var self = this;
            self.refresh();
        }
        Object.defineProperty(DeviceCpusController.prototype, "device", {
            get: function () {
                var self = this;
                return self._device;
            },
            set: function (value) {
                var self = this;
                self._device = value;
                self.refresh();
            },
            enumerable: true,
            configurable: true
        });
        DeviceCpusController.prototype.refresh = function () {
            var self = this;
            self.safeSource = angular.copy(self.device.cpudata);
        };
        DeviceCpusController.$inject = [];
        return DeviceCpusController;
    }());
    angular.module('app')
        .component('deviceCpus', {
        templateUrl: 'views/devicedetails/cpus.html',
        controller: DeviceCpusController,
        controllerAs: 'deviceCpusController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=cpus.js.map
