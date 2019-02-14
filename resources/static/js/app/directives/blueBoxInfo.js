var asm;
(function (asm) {
    "use strict";
    var BlueBoxInfoController = (function () {
        function BlueBoxInfoController() {
        }
        BlueBoxInfoController.$inject = [];
        return BlueBoxInfoController;
    }());
    angular.module('app')
        .component('blueBoxInfo', {
        transclude: true,
        templateUrl: 'views/blueboxinfo.html',
        controller: BlueBoxInfoController,
        controllerAs: 'blueBoxInfoController',
        bindings: {}
    });
})(asm || (asm = {}));
//# sourceMappingURL=blueBoxInfo.js.map
