var asm;
(function (asm) {
    "use strict";
    var PersistController = (function () {
        function PersistController(localStorageService) {
            this.localStorageService = localStorageService;
        }
        PersistController.prototype.save = function (key, value) {
            var self = this;
            self.localStorageService.set(key, value);
        };
        PersistController.prototype.load = function (key) {
            var self = this;
            return self.localStorageService.get(key);
        };
        PersistController.$inject = ['localStorageService'];
        return PersistController;
    }());
    function persist() {
        return {
            restrict: 'A',
            controller: PersistController,
            // controllerAs: 'vm',
            // scope: {},
            link: function (scope, element, attributes, controller) {
                var model = attributes['ngModel'], key = "__scopePersist_" + model, value = controller.load(key), type = attributes['persist'] || 'string', castValue;
                if (value) {
                    switch (type) {
                        case 'bool':
                        case 'boolean':
                            castValue = 'true' === value;
                            break;
                        case 'int':
                        case 'integer':
                            castValue = parseInt(value, 10);
                            break;
                        case 'object':
                        case 'json':
                            castValue = JSON.parse(value);
                            break;
                        default:
                            castValue = value;
                    }
                    setTimeout(function () { scope[model] = castValue; }, 500);
                }
                scope.$watch(model, function (newValue, oldValue) {
                    if (newValue !== oldValue)
                        controller.save(key, newValue);
                });
            }
        };
    }
    angular.module('app').
        directive('persist', persist);
})(asm || (asm = {}));
//# sourceMappingURL=persistDirective.js.map
