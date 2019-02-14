var asm;
(function (asm) {
    "use strict";
    var PasswordController = (function () {
        function PasswordController($compile, $timeout) {
            this.$compile = $compile;
            this.$timeout = $timeout;
            this.showIcon = false;
            this.blankInput = true;
            this.showPassword = false;
            this.touched = false;
            var self = this;
            self.blankInput = true;
        }
        PasswordController.prototype.toggle = function (show, evt) {
            var self = this;
            self.showPassword = show;
            if (show)
                self.showIcon = true;
            evt.stopImmediatePropagation();
        };
        PasswordController.$inject = ['$compile', '$timeout'];
        return PasswordController;
    }());
    function passwordViewer() {
        return {
            restrict: 'A',
            controller: PasswordController,
            controllerAs: 'password',
            scope: {},
            link: function (scope, element, attributes, controller) {
                if (attributes['passwordViewer'] === "false" || attributes['passwordViewer'] === false) {
                    return;
                }
                controller.blankInput = element.val().length === 0;
                element.off('input').on('input', function () {
                    controller.blankInput = element.val().length === 0;
                    controller.showIcon = !controller.blankInput;
                    scope.$apply();
                });
                element.off('focusin').on('focusin', function () {
                    if (controller.touched === false) {
                        controller.touched = true;
                        controller.originalValue = element.val();
                        element.val("");
                    }
                    controller.showIcon = true;
                    scope.$apply();
                });
                element.off('focusout').on('focusout', function () {
                    if (element.val() == "") {
                        element.val(controller.originalValue);
                        controller.touched = false;
                    }
                    controller.showIcon = false;
                    scope.$apply();
                });
                var addonHtml = '<span class="input-group-addon"><i style="top: 0;" class="glyphicon glyphicon-eye-open" ng-show="!password.blankInput && (password.showIcon || password.showPassword)" ng-mousedown="password.toggle(true, $event)" ng-mouseleave="password.toggle(false, $event)" ng-mouseup="password.toggle(false, $event)" data-toggle="tooltip" title="{{\'GENERIC_PWToolTip\'|translate}}"></i></span>';
                var addon = angular.element(addonHtml);
                controller.$timeout(function () {
                    addon.insertAfter(element);
                });
                controller.$timeout(function () {
                    controller.$compile(addon)(scope);
                });
                controller.$timeout(function () {
                    element.parent().addClass('input-group');
                });
                scope.$watch('password.showPassword', function (newVal, oldVal) {
                    controller.$timeout(function () {
                        if (newVal) {
                            element.prop('type', 'text');
                        }
                        else {
                            element.prop('type', 'password');
                        }
                    });
                    if (oldVal === true && newVal === false) {
                        controller.$timeout(function () {
                            element.focus();
                        });
                    }
                });
                scope.$watch('password.showIcon', function (newVal) {
                    if (newVal === false) {
                        addon.find('i').tooltip('hide');
                    }
                });
            }
        };
    }
    angular.module('app').
        directive('passwordViewer', passwordViewer);
})(asm || (asm = {}));
//# sourceMappingURL=password.js.map
