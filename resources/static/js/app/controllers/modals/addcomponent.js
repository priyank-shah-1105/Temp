var asm;
(function (asm) {
    var AddComponentController = (function () {
        function AddComponentController($scope, $http, $q, Loading, Commands, GlobalServices) {
            this.$scope = $scope;
            this.$http = $http;
            this.$q = $q;
            this.Loading = Loading;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.errors = new Array();
            this.componentsLoaded = false;
            var self = this;
            self.$scope.modal.params.availableComponents = [];
            self.loadComponents();
        }
        AddComponentController.prototype.loadComponents = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            var params = {
                id: self.$scope.modal.params.type,
                templateId: self.$scope.modal.params.templateId,
                serviceId: self.$scope.modal.params.serviceId,
            };
            self.$http.post(self.Commands.data.templates.getTemplateBuilderComponents, params)
                .then(function (data) {
                self.$scope.modal.params.availableComponents = data.data.responseObj;
                self.componentsLoaded = true;
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        AddComponentController.prototype.save = function (data) {
            var self = this, 
            //operate on copy in order to prevent dom from reacting to changes made in stringify
            templateCopy = angular.copy(data.config.template);
            angular.forEach(templateCopy.components, function (component) {
                component.categories = self.GlobalServices.stringifyCategories(component.categories);
            });
            return self.$http.post(self.Commands.data.templates.saveTemplate, templateCopy);
        };
        AddComponentController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        AddComponentController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        AddComponentController.$inject = ['$scope', '$http', '$q', 'Loading', 'Commands', 'GlobalServices'];
        return AddComponentController;
    }());
    asm.AddComponentController = AddComponentController;
    angular
        .module('app')
        .controller('AddComponentController', AddComponentController);
})(asm || (asm = {}));
//# sourceMappingURL=addcomponent.js.map
