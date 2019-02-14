var asm;
(function (asm) {
    "use strict";
    var PasswordMatcherDirective = (function () {
        function PasswordMatcherDirective($window) {
            // Directive parameters.
            this.restrict = 'A';
            this.require = 'ngModel';
            this.scope = { pwdString: '=passwordMatcher' };
        }
        // Link function
        // 3.21.2016 jek - this decalration requires extending angular.INgModelController which I could never get to work;
        // see asm.custom.d.ts for most recent attempt.  Changing to ngModel: any allows this to work.
        //public link(scope: myScope, element: angular.IAugmentedJQuery, attrs: angular.IAttributes, ngModel: angular.INgModelController) {
        PasswordMatcherDirective.prototype.link = function (scope, element, attrs, ngModel) {
            ngModel.$validators.passwordMatch = function (repeatValue) {
                if ($(element).is(':disabled'))
                    repeatValue = scope.pwdString;
                return repeatValue == scope.pwdString;
            };
            scope.$watch("pwdString", function () {
                ngModel.$validate();
            });
        };
        // Creates an instance of the PasswordMatcherDirective class.
        PasswordMatcherDirective.factory = function () {
            var directive = function ($window) { return new PasswordMatcherDirective($window); };
            directive.$inject = ['$window'];
            return directive;
        };
        // Constructor 
        PasswordMatcherDirective.$inject = ['$window'];
        return PasswordMatcherDirective;
    }());
    asm.PasswordMatcherDirective = PasswordMatcherDirective;
    angular.module('app').
        directive('passwordMatcher', PasswordMatcherDirective.factory());
})(asm || (asm = {}));
//# sourceMappingURL=passwordMatcher.js.map
