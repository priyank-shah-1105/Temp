var asm;
(function (asm) {
    "use strict";
    var ServiceComponentPopoverController = (function () {
        function ServiceComponentPopoverController(Modal, dialog, $http, $timeout, $q, $translate, $scope, GlobalServices, loading, Commands, $location, constants) {
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
            this.constants = constants;
        }
        ServiceComponentPopoverController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q',
            '$translate', '$scope', 'GlobalServices', 'Loading', 'Commands', '$location', 'constants'];
        return ServiceComponentPopoverController;
    }());
    angular.module('app')
        .component('serviceComponentPopover', {
        templateUrl: 'views/services/servicecomponentpopover.html',
        controller: ServiceComponentPopoverController,
        controllerAs: 'serviceComponentPopoverController',
        bindings: {
            selectedComponent: "<",
            viewLogs: "&",
            doMigrate: "&?",
            portviewServer: "&?",
            openFirmwareReport: "&?",
            service: "<"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=ServiceComponentPopover.js.map
