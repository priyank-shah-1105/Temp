var asm;
(function (asm) {
    "use strict";
    var ScaleStoragesController = (function () {
        function ScaleStoragesController() {
            var self = this;
            self.refresh();
        }
        ScaleStoragesController.prototype.refresh = function () {
            var self = this;
            self.safeSrc = angular.copy(self.device);
        };
        ScaleStoragesController.$inject = [];
        return ScaleStoragesController;
    }());
    angular.module('app')
        .component('scaleStorages', {
        templateUrl: 'views/devicedetails/scalestorages.html',
        controller: ScaleStoragesController,
        controllerAs: 'scaleStoragesController',
        bindings: {
            device: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=scaleStorages.js.map
