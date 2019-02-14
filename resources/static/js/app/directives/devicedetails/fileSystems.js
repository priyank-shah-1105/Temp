var asm;
(function (asm) {
    "use strict";
    var DeviceFileSystemsController = (function () {
        function DeviceFileSystemsController() {
            var self = this;
            self.refresh();
        }
        DeviceFileSystemsController.prototype.refresh = function () {
            var self = this;
            self.safeSrc = angular.copy(self.device);
        };
        DeviceFileSystemsController.$inject = [];
        return DeviceFileSystemsController;
    }());
    angular.module('app')
        .component('deviceFileSystems', {
        templateUrl: 'views/devicedetails/filesystems.html',
        controller: DeviceFileSystemsController,
        controllerAs: 'deviceFileSystemsController',
        bindings: {
            device: '<',
            deviceType: '<'
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=fileSystems.js.map
