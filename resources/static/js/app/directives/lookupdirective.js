//module asm {
//    "use strict";
//    export interface ILookupDirectiveScope
//    {
//        list: [any];
//        key: any;
//        propertyName: any;
//        val: any;
//    }
//    function listLookup(): ng.IDirective {
//        return {
//            restrict: 'E',
//            template: '<span>{{value}}</span>',
//            replace: true,
//            transclude: false,
//            link: (scope: ILookupDirectiveScope): void => {
//                var lookuppropertyname = scope.propertyName || 'name';
//                var match = _.find(scope.list, ['id', scope.key]);
//                scope.val = match[lookuppropertyname];
//            }
//        };
//    }
//    angular
//        .module('app')
//        .directive('listLookup', listLookup);
//}
var asm;
(function (asm) {
    "use strict";
    var ListLookupController = (function () {
        function ListLookupController() {
        }
        Object.defineProperty(ListLookupController.prototype, "list", {
            get: function () {
                return this._list;
            },
            set: function (theList) {
                this._list = theList;
                if (this._list && this._list.length > 0)
                    this.update();
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(ListLookupController.prototype, "key", {
            get: function () {
                return this._key;
            },
            set: function (theKey) {
                this._key = theKey;
                if (this._key && this._key.length > 0)
                    this.update();
            },
            enumerable: true,
            configurable: true
        });
        ListLookupController.prototype.update = function () {
            var self = this;
            if (self.key && self.list && self.list.length > 0) {
                var lookuppropertyname = self.propertyName || 'name';
                var match = _.find(self.list, { 'id': self.key });
                if (match) {
                    self.val = match[lookuppropertyname];
                }
                else {
                    self.val = "";
                }
            }
        };
        return ListLookupController;
    }());
    asm.ListLookupController = ListLookupController;
    angular
        .module('app')
        .component('listLookup', {
        template: '<span>{{vm.val}}</span>',
        controller: ListLookupController,
        controllerAs: 'vm',
        bindings: {
            propertyName: '=',
            list: '=',
            key: '='
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=lookupdirective.js.map
