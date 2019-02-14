var asm;
(function (asm) {
    var ViewTemplateDetailsModalController = (function () {
        function ViewTemplateDetailsModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, $filter) {
            this.Modal = Modal;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$filter = $filter;
            this.readOnly = true;
            this.errors = new Array();
            var self = this;
            self.initialize();
        }
        ViewTemplateDetailsModalController.prototype.initialize = function () {
            var self = this;
            self.$scope.modal.params.getTemplate = true;
            self.getTemplate()
                .then(function (template) {
                self.template = template;
            });
        };
        ViewTemplateDetailsModalController.prototype.getTemplate = function () {
            var self = this;
            //if template is passed in, use that, otherwise check if it is a service
            return self.$q(function (resolve, reject) {
                if (!!self.$scope.modal.params.template) {
                    return resolve(angular.copy(self.$scope.modal.params.template));
                }
                else if (self.$scope.modal.params.service) {
                    var d = self.$q.defer();
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.loadServiceSettings(self.$scope.modal.params.service.id)
                        .then(function (response) {
                        resolve(response.data.responseObj);
                    })
                        .catch(function (data) {
                        self.GlobalServices.DisplayError(data.data, self.errors);
                        reject();
                    })
                        .finally(function () { return d.resolve(); });
                }
            });
        };
        ViewTemplateDetailsModalController.prototype.componentVisible = function (component) {
            var self = this;
            return _.find(component.categories, function (category) {
                return self.categoryVisible(category, component);
            });
        };
        ViewTemplateDetailsModalController.prototype.categoryVisible = function (category, component) {
            var self = this;
            var templateSettings = self.$filter("templatesettings")(category.settings);
            var filteredDependentSettings = self.$filter("viewTemplateDetailsFilter")(templateSettings, component);
            return filteredDependentSettings.length;
        };
        ViewTemplateDetailsModalController.prototype.loadTemplateDetails = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.templates.loadTemplateDetails, { id: id });
        };
        ViewTemplateDetailsModalController.prototype.loadServiceSettings = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.services.getServiceSettingsById, { id: id });
        };
        ViewTemplateDetailsModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ViewTemplateDetailsModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        ViewTemplateDetailsModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices', "$filter"];
        return ViewTemplateDetailsModalController;
    }());
    asm.ViewTemplateDetailsModalController = ViewTemplateDetailsModalController;
    angular
        .module('app')
        .controller('ViewTemplateDetailsModalController', ViewTemplateDetailsModalController);
})(asm || (asm = {}));
//# sourceMappingURL=ViewTemplateDetails.js.map
