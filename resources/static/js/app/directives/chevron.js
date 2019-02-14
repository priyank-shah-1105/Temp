var asm;
(function (asm) {
    "use strict";
    var ChevronController = (function () {
        function ChevronController() {
        }
        ChevronController.$inject = [];
        return ChevronController;
    }());
    angular.module('app')
        .component('chevron', {
        transclude: true,
        templateUrl: 'views/chevron.html',
        controller: ChevronController,
        controllerAs: 'chevronController',
        bindings: {
            item: '=',
            initValue: '=?',
            hideChevron: '=?'
        }
    });
})(asm || (asm = {}));
/*
Used for accordion style collapsable rows. Note this is only the header portion.
Parameters:
item - This is a boolean property that will be toggled on click of the chevron symbol or the title itself passed in
initValue - You can pass in an initial value of true for item, otherwise it will default to false
hideChevron - Hide a chevron when it has nothing to list below it

Transcluded items - The text for the heading
Example:
<chevron item="showContent"
         init-value="true"
         hide-chevron="!item.children.length">
Title of collapseable row
</chevron>
<div ng-if="showContent">
    Content to show
</div>

*/ 
//# sourceMappingURL=chevron.js.map
