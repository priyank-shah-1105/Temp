var asm;
(function (asm) {
    "use strict";
    var DeviceEquallogicController = (function () {
        function DeviceEquallogicController() {
            var self = this;
            self.refresh();
        }
        DeviceEquallogicController.prototype.refresh = function () {
            var self = this;
            self.safeSource = angular.copy(self.device.volumes);
            self.deviceType = self.deviceType;
        };
        DeviceEquallogicController.$inject = [];
        return DeviceEquallogicController;
    }());
    angular.module('app')
        .component('deviceEquallogic', {
        templateUrl: 'views/devicedetails/equallogicvolumes.html',
        controller: DeviceEquallogicController,
        controllerAs: 'deviceEquallogicController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=equallogicVolumes.js.map
