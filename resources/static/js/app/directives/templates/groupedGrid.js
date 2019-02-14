var asm;
(function (asm) {
    "use strict";
    var GroupedGridController = (function () {
        function GroupedGridController(Modal, Dialog, $http, $timeout, $q, $translate, $scope, GlobalServices) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.$scope = $scope;
            this.GlobalServices = GlobalServices;
            var self = this;
            self.refreshCategories();
        }
        GroupedGridController.prototype.refreshCategories = function () {
            var self = this;
            self.categories = [];
            var groupedCategories = _.groupBy(self.list, 'category');
            for (var category in groupedCategories) {
                if (groupedCategories.hasOwnProperty(category)) {
                    self.categories.push({ 'name': category, 'count': groupedCategories[category].length });
                }
            }
        };
        GroupedGridController.prototype.changeCategory = function (categoryName) {
            var self = this;
            self.filterBy = categoryName;
            self.$timeout(function () { return self.updateArrays(); });
        };
        GroupedGridController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', '$scope', 'GlobalServices'];
        return GroupedGridController;
    }());
    angular.module('app')
        .component('groupedGrid', {
        templateUrl: 'views/templates/groupedgrid.html',
        controller: GroupedGridController,
        controllerAs: 'group',
        bindings: {
            list: '<',
            filterBy: '=',
            updateArrays: '&'
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=groupedGrid.js.map
