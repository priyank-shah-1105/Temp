var asm;
(function (asm) {
    "use strict";
    var ServiceSvgHealthIconController = (function () {
        function ServiceSvgHealthIconController($scope) {
            this.$scope = $scope;
            var self = this;
            self.health = self.$scope.health;
        }
        ServiceSvgHealthIconController.$inject = ["$scope"];
        return ServiceSvgHealthIconController;
    }());
    angular.module('app').
        directive('serviceSvgHealthIcon', [function serviceSvgHealthIcon() {
            return {
                restrict: 'E',
                templateUrl: 'views/services/servicesvghealthicon.html',
                replace: true,
                transclude: false,
                controller: ServiceSvgHealthIconController,
                controllerAs: 'serviceSvgHealthIconController',
                templateNamespace: 'svg',
                scope: {
                    health: '<'
                }
            };
        }]);
})(asm || (asm = {}));
//# sourceMappingURL=serviceSvgHealthIcon.js.map
