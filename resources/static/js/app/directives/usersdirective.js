var asm;
(function (asm) {
    "use strict";
    var UsersController = (function () {
        function UsersController(Modal, Dialog, $http, $timeout, $q, GlobalServices, $route, localStorageService, $rootScope) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.GlobalServices = GlobalServices;
            this.$route = $route;
            this.localStorageService = localStorageService;
            this.$rootScope = $rootScope;
            this.activeTab = 'users';
            //this.tab();
        }
        UsersController.prototype.clickTab = function (tab) {
            var self = this;
            if (tab == 'users') {
                self.activeTab = tab;
                self.$rootScope.helpToken = 'SettingsUsersHomePage';
            }
            else if (tab == 'directoryservices') {
                self.activeTab = tab;
                self.$rootScope.helpToken = 'SetingsUsersDirectoryServices';
            }
        };
        UsersController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', 'GlobalServices', '$route', 'localStorageService', '$rootScope'];
        return UsersController;
    }());
    function users() {
        return {
            restrict: 'E',
            templateUrl: 'views/users.html',
            replace: true,
            transclude: false,
            controller: UsersController,
            controllerAs: 'users',
            link: function (scope, element, attributes, controller) {
            }
        };
    }
    angular.module('app').
        directive('users', users);
})(asm || (asm = {}));
//# sourceMappingURL=usersdirective.js.map
