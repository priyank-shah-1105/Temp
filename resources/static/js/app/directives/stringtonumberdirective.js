var asm;
(function (asm) {
    "use strict";
    function stringToNumber() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attrs, ngModel) {
                ngModel.$parsers.push(function (value) {
                    return '' + value;
                });
                ngModel.$formatters.push(function (value) {
                    return parseFloat(value);
                });
            }
        };
    }
    angular
        .module('app')
        .directive('stringToNumber', stringToNumber);
})(asm || (asm = {}));
//# sourceMappingURL=stringtonumberdirective.js.map
