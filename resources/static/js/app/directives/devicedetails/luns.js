var asm;
(function (asm) {
    "use strict";
    var DeviceLunsController = (function () {
        function DeviceLunsController() {
            var self = this;
            //self.deviceType = self.$routeParams.deviceType;
            self.refresh();
        }
        DeviceLunsController.prototype.refresh = function () {
            var self = this;
            self.safeSrc = angular.copy(self.device);
        };
        DeviceLunsController.$inject = [];
        return DeviceLunsController;
    }());
    angular.module('app')
        .component('deviceLuns', {
        templateUrl: 'views/devicedetails/luns.html',
        controller: DeviceLunsController,
        controllerAs: 'deviceLunsController',
        bindings: {
            device: '<',
            deviceType: '<'
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=luns.js.map
