var asm;
(function (asm) {
    "use strict";
    var TileController = (function () {
        function TileController(Modal, Dialog, $http, $timeout, $q, $translate, $scope, GlobalServices, $location) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.$scope = $scope;
            this.GlobalServices = GlobalServices;
            this.$location = $location;
            var self = this;
            self.isTemplate = self.type === "template";
            self.isService = self.type === "service";
        }
        TileController.prototype.clearToolTips = function () {
            $('.tooltip').remove();
        };
        TileController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', '$scope', 'GlobalServices', '$location'];
        return TileController;
    }());
    angular.module('app')
        .component('tile', {
        templateUrl: 'views/tiles/tile.html',
        controller: TileController,
        controllerAs: 'tileController',
        bindings: {
            selectedItem: '<',
            tileInfo: '<',
            hideActions: '<',
            actions: '<',
            type: '@',
            disabled: "<?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=tile.js.map
