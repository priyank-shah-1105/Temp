var asm;
(function (asm) {
    "use strict";
    var ScaleServersController = (function () {
        function ScaleServersController() {
            var self = this;
            self.refresh();
        }
        ScaleServersController.prototype.refresh = function () {
            var self = this;
            self.safeSrc = angular.copy(self.device);
        };
        ScaleServersController.$inject = [];
        return ScaleServersController;
    }());
    angular.module('app')
        .component('scaleServers', {
        templateUrl: 'views/devicedetails/scaleservers.html',
        controller: ScaleServersController,
        controllerAs: 'scaleServersController',
        bindings: {
            device: '=',
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=scaleServers.js.map
