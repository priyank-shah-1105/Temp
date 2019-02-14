var asm;
(function (asm) {
    "use strict";
    var TemplateTileController = (function () {
        function TemplateTileController(modal, dialog, $http, $timeout, $q, $translate) {
            this.modal = modal;
            this.dialog = dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            var self = this;
            angular.extend(self.actions, {
                getClasses: function (param) { return self.getClasses(param); }
            });
        }
        TemplateTileController.prototype.getClasses = function (tile) {
            var self = this;
            var classes = new Array();
            classes.push('thumbnail-template');
            //May need to add this back in if visual design direction changes -MH
            //if (tile.isTemplateValid === false) {
            //    classes.push('template-warning');
            //}
            //else if (tile.isLocked === true) {
            //    classes.push('template-example');
            //} else if (tile.draft === true) {
            //    classes.push('draft');
            //}
            if (tile.isLocked === true) {
                classes.push('template-example');
            }
            else if (tile.draft === true) {
                classes.push('draft');
            }
            return classes.join(" ");
        };
        TemplateTileController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate'];
        return TemplateTileController;
    }());
    angular.module('app')
        .component('templateTile', {
        templateUrl: 'views/templates/templatetile.html',
        controller: TemplateTileController,
        controllerAs: 'templateTileController',
        bindings: {
            selectedItem: '=',
            tileInfo: '=',
            actions: '=',
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=templateTile.js.map
