var asm;
(function (asm) {
    var AboutController = (function () {
        function AboutController($http, $timeout, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, $routeParams, constants, $location, $window) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$routeParams = $routeParams;
            this.constants = constants;
            this.$location = $location;
            this.$window = $window;
            this.someData = 'hello page';
            var self = this;
            self.refresh();
        }
        AboutController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.$http.post(self.Commands.data.about.getAboutData, {}).then(function (data) {
                self.about = data.data.responseObj;
                console.log(self.about);
            }).catch(function (data) {
                d.resolve();
                self.GlobalServices.DisplayError(data.data);
            });
        };
        AboutController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$routeParams', 'constants', '$location', '$window'];
        return AboutController;
    }());
    asm.AboutController = AboutController;
    angular
        .module("app")
        .controller("AboutController", AboutController);
})(asm || (asm = {}));
//# sourceMappingURL=about.js.map
