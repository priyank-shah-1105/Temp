var asm;
(function (asm) {
    "use strict";
    var ServerSettingsController = (function () {
        function ServerSettingsController($http, $timeout, $q, $translate, modal, loading, dialog, commands, globalServices, $rootScope, $interval) {
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
            var self = this;
        }
        ServerSettingsController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope', '$interval'];
        return ServerSettingsController;
    }());
    angular.module("app")
        .component("serverSettings", {
        templateUrl: "views/serversettings.html",
        controller: ServerSettingsController,
        controllerAs: "serverSettingsController",
        bindings: {
            serverSettings: "=",
            form: "=?",
            errors: "=",
            readOnly: "<?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=serverSettings.js.map
