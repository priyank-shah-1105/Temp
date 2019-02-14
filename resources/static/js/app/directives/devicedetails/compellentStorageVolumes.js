var asm;
(function (asm) {
    "use strict";
    var DeviceCompellentStorageController = (function () {
        function DeviceCompellentStorageController() {
            var self = this;
            self.refresh();
        }
        DeviceCompellentStorageController.prototype.refresh = function () {
            var self = this;
            self.safeSource = angular.copy(self.device.volumes);
        };
        DeviceCompellentStorageController.$inject = [];
        return DeviceCompellentStorageController;
    }());
    angular.module('app')
        .component('deviceCompellent', {
        templateUrl: 'views/devicedetails/compellentstoragevolumes.html',
        controller: DeviceCompellentStorageController,
        controllerAs: 'deviceCompellentStorageController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=compellentStorageVolumes.js.map
