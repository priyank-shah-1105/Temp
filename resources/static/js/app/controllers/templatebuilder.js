var asm;
(function (asm) {
    var TemplateBuilderController = (function () {
        function TemplateBuilderController($http, $translate, $routeParams, GlobalServices, $timeout, $rootScope) {
            this.$http = $http;
            this.$translate = $translate;
            this.$routeParams = $routeParams;
            this.GlobalServices = GlobalServices;
            this.$timeout = $timeout;
            this.$rootScope = $rootScope;
            var self = this;
            self.activate();
        }
        TemplateBuilderController.prototype.activate = function () {
            var self = this;
            self.selectedTemplateId = self.$routeParams.id;
            self.mode = self.$routeParams.mode;
            self.errors = self.$rootScope.errors;
        };
        TemplateBuilderController.$inject = ['$http', '$translate', '$routeParams', 'GlobalServices', '$timeout', '$rootScope'];
        return TemplateBuilderController;
    }());
    asm.TemplateBuilderController = TemplateBuilderController;
    angular
        .module("app")
        .controller("TemplatebuilderController", TemplateBuilderController);
})(asm || (asm = {}));
//# sourceMappingURL=templatebuilder.js.map
