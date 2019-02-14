var asm;
(function (asm) {
    "use strict";
    var ClarityColumnSelectorController = (function () {
        //public config: any = {};
        function ClarityColumnSelectorController(Modal, Dialog, $http, $timeout, $q, $translate, $scope, $parse) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.$scope = $scope;
            this.$parse = $parse;
        }
        ClarityColumnSelectorController.prototype.activate = function () {
        };
        ClarityColumnSelectorController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', '$scope', '$parse'];
        return ClarityColumnSelectorController;
    }());
    function clarityColumnSelector() {
        return {
            restrict: 'A',
            replace: false,
            transclude: false,
            controller: ClarityColumnSelectorController,
            //controllerAs: 'ctrl',
            scope: {
                config: '=clarityColumnSelector'
            },
            link: function (scope, element, attributes, controller) {
            }
        };
    }
    function clarityColumnPicker() {
        return {
            restrict: 'A',
            replace: true,
            transclude: false,
            //controller: ClarityColumnSelectorController,
            //controllerAs: 'ctrl',
            scope: {
                config: '=clarityColumnPicker'
            },
            link: function (scope, element, attributes, controller) {
            },
            template: function () {
                var html = '';
                html += '                <div class="dropdown dropdown-filter btn-group">' +
                    '                    <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">' +
                    '                        Column Picker &nbsp;<span class="caret"></span>' +
                    '                    </button>' +
                    '                            <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">' +
                    '                                <li ng-repeat="column in config">' +
                    '                                    <input type="checkbox" ng-model="column.visible" style="margin-left: 3px; margin-right: 3px;"><span style="color: initial;">{{column.label}}</span>' +
                    '                               </li>' +
                    '                           </ul>' +
                    '                  </div>';
                return html;
            }
        };
    }
    function clarityColumnSelectorColumn() {
        return {
            require: '^clarityColumnSelector',
            restrict: 'A',
            replace: false,
            transclude: false,
            scope: {
                clarityColumnSelectorColumn: '='
            },
            link: function (scope, element, attributes, controller) {
                if (!scope.clarityColumnSelectorColumn)
                    return;
                var column = {
                    label: scope.clarityColumnSelectorColumn.label || scope.clarityColumnSelectorColumn.column || scope.clarityColumnSelectorColumn,
                    column: scope.clarityColumnSelectorColumn.column || scope.clarityColumnSelectorColumn.column || scope.clarityColumnSelectorColumn,
                    visible: true
                };
                if (!controller.$scope.config[column.column])
                    controller.$scope.config[column.column] = column;
                controller.$scope.$watch('config', function () {
                    if (controller.$scope.config[column.column].visible === false) {
                        element.hide();
                    }
                    else {
                        element.show();
                    }
                }, true);
            }
        };
    }
    angular.module('app').
        directive('clarityColumnSelector', clarityColumnSelector);
    angular.module('app').
        directive('clarityColumnSelectorColumn', clarityColumnSelectorColumn);
    angular.module('app').
        directive('clarityColumnPicker', clarityColumnPicker);
})(asm || (asm = {}));
//# sourceMappingURL=claritycolumnselector.js.map
