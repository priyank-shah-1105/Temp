var asm;
(function (asm) {
    "use strict";
    var DeviceNetAppVolumesController = (function () {
        function DeviceNetAppVolumesController() {
            var self = this;
            self.refresh();
        }
        DeviceNetAppVolumesController.prototype.refresh = function () {
            var self = this;
            self.safeSource = angular.copy(self.device.volumes);
            self.deviceType = self.deviceType;
        };
        DeviceNetAppVolumesController.$inject = [];
        return DeviceNetAppVolumesController;
    }());
    angular.module('app')
        .component('deviceNetappVolumes', {
        templateUrl: 'views/devicedetails/netappstoragevolumes.html',
        controller: DeviceNetAppVolumesController,
        controllerAs: 'deviceNetAppVolumesController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=netappStorageVolumes.js.map
