var asm;
(function (asm) {
    "use strict";
    var ServiceTileController = (function () {
        function ServiceTileController(modal, dialog, $http, $timeout, $q, $translate) {
            this.modal = modal;
            this.dialog = dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.actions = {};
            var self = this;
            angular.extend(self.actions, { getClasses: function (param) { return self.getClasses(param); } });
        }
        //showTransparency comes as a boolean when coming from the filter by 'All' tiles which require transparency when their count is zero and a boolean comes from the side panel also which is always false
        //other service tiles will only need the translucent layer when their tile matches the side panel
        ServiceTileController.prototype.getClasses = function (tile) {
            var self = this;
            var classes = new Array();
            classes.push('thumbnail-service');
            switch (tile.dropdown) {
                case 'red':
                    classes.push('red');
                    break;
                case 'green':
                    classes.push('green');
                    break;
                case 'pending':
                    classes.push('pending');
                    break;
                case 'unknown':
                    classes.push('blue');
                    break;
                case 'yellow':
                    classes.push('yellow');
                    break;
                case 'cancelled':
                    classes.push('cancelled');
                    break;
            }
            return classes.join(" ");
        };
        ServiceTileController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate'];
        return ServiceTileController;
    }());
    angular.module('app')
        .component('serviceTile', {
        templateUrl: 'views/services/servicetile.html',
        controller: ServiceTileController,
        controllerAs: 'serviceTileController',
        bindings: {
            selectedItem: '=',
            tileInfo: '=',
            disabled: "<?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=serviceTile.js.map
