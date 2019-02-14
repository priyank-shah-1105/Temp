var asm;
(function (asm) {
    "use strict";
    var PhysicalDisksController = (function () {
        function PhysicalDisksController() {
            var self = this;
            self.refresh();
        }
        PhysicalDisksController.prototype.refresh = function () {
            var self = this;
            self.safeSource = angular.copy(self.disks);
        };
        PhysicalDisksController.$inject = [];
        return PhysicalDisksController;
    }());
    angular.module('app')
        .component('physicalDisks', {
        templateUrl: 'views/devicedetails/physicaldisks.html',
        controller: PhysicalDisksController,
        controllerAs: 'physicalDisksController',
        bindings: {
            disks: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=physicalDisks.js.map
