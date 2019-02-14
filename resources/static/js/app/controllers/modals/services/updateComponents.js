var asm;
(function (asm) {
    var UpdateComponentsController = (function () {
        function UpdateComponentsController($http, $timeout, $scope, $q, $translate, Modal, loading, Dialog, Commands, GlobalServices, constants, $filter) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.loading = loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            this.$filter = $filter;
            this.serviceId = this.$scope.modal.params.serviceId;
            this.mode = "edit";
            this.errors = new Array();
            var self = this;
            self.refresh();
        }
        UpdateComponentsController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.getUpdatableService(self.serviceId)
                .then(function (response) {
                self.serviceSettings = response.data.responseObj;
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        UpdateComponentsController.prototype.save = function (forceRetry) {
            var self = this, d = self.$q.defer();
            self.form._submitted = true;
            self.serviceSettings.forceRetry = forceRetry;
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.saveService(self.serviceSettings)
                .then(function () {
                d.resolve();
                self.close();
            })
                .catch(function (response) { return self.GlobalServices.DisplayError(response.data, self.errors); })
                .finally(function () { return d.resolve(); });
        };
        UpdateComponentsController.prototype.categoryVisible = function (category, component) {
            var self = this;
            return self.$filter("updateComponents")(category.settings, component).length;
        };
        ;
        UpdateComponentsController.prototype.componentVisible = function (component) {
            var self = this;
            return _.find(component.categories, function (category) { return self.categoryVisible(category, component); });
        };
        UpdateComponentsController.prototype.remainingFields = function () {
            var self = this, count = 0;
            angular.forEach(self.serviceSettings.components, function (component) {
                angular.forEach(component.categories, function (category) {
                    //filter for dependencies
                    var settingsVisible = self.$filter("updateComponents")(category.settings, component);
                    var blankFields = _.filter(settingsVisible, function (s) { return s.required && !s.value; });
                    count += blankFields.length;
                });
            });
            return count;
        };
        UpdateComponentsController.prototype.getUpdatableService = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.services.getUpdatableServiceSettingsById, { id: id });
        };
        UpdateComponentsController.prototype.saveService = function (service) {
            var self = this;
            return self.$http.post(self.Commands.data.services.updateComponents, service);
        };
        UpdateComponentsController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        UpdateComponentsController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        UpdateComponentsController.$inject = [
            '$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands',
            'GlobalServices', 'constants', "$filter"
        ];
        return UpdateComponentsController;
    }());
    asm.UpdateComponentsController = UpdateComponentsController;
    angular
        .module("app")
        .controller("UpdateComponentsController", UpdateComponentsController);
})(asm || (asm = {}));
//# sourceMappingURL=updateComponents.js.map
