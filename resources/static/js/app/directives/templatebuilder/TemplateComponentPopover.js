var asm;
(function (asm) {
    "use strict";
    var TemplateComponentPopoverController = (function () {
        function TemplateComponentPopoverController(Modal, dialog, $http, $timeout, $q, $translate, $scope, GlobalServices, loading, Commands, $location) {
            this.Modal = Modal;
            this.dialog = dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.$scope = $scope;
            this.GlobalServices = GlobalServices;
            this.loading = loading;
            this.Commands = Commands;
            this.$location = $location;
        }
        TemplateComponentPopoverController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q',
            '$translate', '$scope', 'GlobalServices', 'Loading', 'Commands', '$location'];
        return TemplateComponentPopoverController;
    }());
    angular.module('app')
        .component('templateComponentPopover', {
        templateUrl: 'views/templatebuilder/templatecomponentpopover.html',
        controller: TemplateComponentPopoverController,
        controllerAs: 'templateComponentPopoverController',
        bindings: {
            component: "=",
            mode: "=",
            editComponent: "&",
            cloneComponent: "&",
            deleteComponent: "&"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=TemplateComponentPopover.js.map
