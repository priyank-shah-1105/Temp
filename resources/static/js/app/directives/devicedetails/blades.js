var asm;
(function (asm) {
    "use strict";
    var DeviceBladesController = (function () {
        function DeviceBladesController($http) {
            this.$http = $http;
            var self = this;
            self.refresh();
        }
        DeviceBladesController.prototype.refresh = function () {
            var self = this;
            self.safeSrc = angular.copy(self.device);
            self.deviceType = self.deviceType;
        };
        DeviceBladesController.$inject = ['$http'];
        return DeviceBladesController;
    }());
    angular.module('app')
        .component('deviceBlades', {
        templateUrl: 'views/devicedetails/blades.html',
        controller: DeviceBladesController,
        controllerAs: 'deviceBladesController',
        bindings: {
            device: '=',
            deviceType: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=blades.js.map
