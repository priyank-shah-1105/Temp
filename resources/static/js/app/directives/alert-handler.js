var asm;
(function (asm) {
    "use strict";
    var AlertHandlerController = (function () {
        function AlertHandlerController($http, $timeout, $q, $translate, modal, loading, dialog, commands, globalServices, $rootScope, $interval, constants) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.modal = modal;
            this.loading = loading;
            this.dialog = dialog;
            this.commands = commands;
            this.globalServices = globalServices;
            this.$rootScope = $rootScope;
            this.$interval = $interval;
            this.constants = constants;
            var self = this;
            self.initialize();
        }
        AlertHandlerController.prototype.initialize = function () {
            var self = this;
        };
        AlertHandlerController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope', '$interval', "constants"];
        return AlertHandlerController;
    }());
    angular.module("app")
        .component("alertHandler", {
        templateUrl: "views/alerthandler.html",
        controller: AlertHandlerController,
        controllerAs: "alertHandlerController",
        bindings: {
            errorObj: "<",
            popoverView: "<",
            viewDetails: "&?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=alert-handler.js.map
