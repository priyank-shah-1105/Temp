var asm;
(function (asm) {
    var ServiceComponentModalController = (function () {
        function ServiceComponentModalController($scope, Modal, Dialog, $http, $q, $timeout, Loading, GlobalServices, FileUploader, $translate, constants, commands, $location, $filter, $anchorScroll) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$q = $q;
            this.$timeout = $timeout;
            this.Loading = Loading;
            this.GlobalServices = GlobalServices;
            this.FileUploader = FileUploader;
            this.$translate = $translate;
            this.constants = constants;
            this.commands = commands;
            this.$location = $location;
            this.$filter = $filter;
            this.$anchorScroll = $anchorScroll;
            this.settings = {
                newSetting: false,
                numInstances: 1,
                serviceToDuplicate: undefined
            };
            this.readOnlyMode = false;
            this.invalidFormElements = [];
            this.errors = new Array();
            var self = this;
            self.refresh();
        }
        ServiceComponentModalController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.readOnlyMode = self.$scope.modal.params.mode !== "edit";
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.availableComponents = _.filter(self.$scope.modal.params.service.components, { type: self.$scope.modal.params.type });
            //self.settings.newSetting =
            //    (
            //        self.$scope.modal.params.type !== "storage"
            //        || self.$scope.modal.params.type !== "cluster"
            //    )
            //    || !self.availableComponents.length;
            self.$http.post(self.commands.data.templates.getTemplateBuilderComponents, {
                id: self.$scope.modal.params.type,
                templateId: self.$scope.modal.params.templateId,
                serviceId: self.$scope.modal.params.serviceId
            })
                .then(function (data) {
                if (self.$scope.modal.params.type === "storage") {
                    self.hideChooseForm = true;
                    //compute only cannot create volumes here, so show message directing user on what to do
                    if (data.data.responseObj === null) {
                        self.showComputeOnlyMessage = true;
                    }
                    self.newComponents = [data.data.responseObj[0]];
                    self.settings.serviceToDuplicate = self.newComponents[0];
                    self.linkStorageToAllServers(self.settings.serviceToDuplicate);
                    self.settings.newSetting = false;
                }
                else {
                    self.$scope.modal.params.availableComponents = data.data.responseObj;
                }
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ServiceComponentModalController.prototype.continue = function () {
            var self = this, d = self.$q.defer();
            if (self.forms.choose.$invalid) {
                self.forms.choose._submitted = false;
                self.forms.choose._showValidation = true;
                return;
            }
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.getAdjustServiceComponents(self.settings.serviceToDuplicate.id, self.settings.numInstances, self.$scope.modal.params.serviceId)
                .then(function (response) {
                self.newComponents = response.data.responseObj;
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ServiceComponentModalController.prototype.prepForSave = function (components) {
            return angular.forEach(components, function (component) {
                angular.forEach(component.categories, function (category) {
                    angular.forEach(category.settings, function (setting) {
                        if (setting.value != null && typeof setting.value != 'string') {
                            setting.value = JSON.stringify(setting.value);
                        }
                    });
                });
            });
        };
        ServiceComponentModalController.prototype.adjustService = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.GlobalServices.scrollToInvalidElement("page_service_component_wrapper");
            if (self.requiredFieldsRemaining(self.newComponents))
                return d.reject();
            self.adjustServiceCall(self.settings.serviceToDuplicate.id, self.prepForSave(angular.copy(self.newComponents)), self.$scope.modal.params.serviceId)
                .then(function () {
                self.close();
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ServiceComponentModalController.prototype.requiredFieldsRemaining = function (componentArray) {
            var self = this, count = 0, numInvalidCustomFormElements = self.numInvalidCustomComponents(self.invalidFormElements);
            angular.forEach(componentArray, function (component) {
                angular.forEach(_.filter(component.categories, function (category) { return self.categoryVisible(category, component); }), function (category) {
                    var filteredSettings = self.$filter("serviceComponentFilter")(category.settings, component);
                    angular.forEach(filteredSettings, function (setting) {
                        if (!setting.value && setting.required) {
                            count += 1;
                        }
                    });
                });
            });
            return count + numInvalidCustomFormElements;
        };
        ServiceComponentModalController.prototype.categoryVisible = function (category, component) {
            var self = this;
            var filteredSettings = self.$filter("serviceComponentFilter")(category.settings, component);
            return filteredSettings.length;
        };
        ServiceComponentModalController.prototype.scrollTo = function (id) {
            var self = this;
            //wait for collapsing row to close, then only scroll to it if it's opening and not closing
            self.$timeout(function () {
                $("#" + id).find(".collapsed")[0] || self.$anchorScroll("" + id);
            }, 500);
        };
        ServiceComponentModalController.prototype.numInvalidCustomComponents = function (invalidFormElementsArray) {
            return _.filter(invalidFormElementsArray, { invalid: true }).length;
        };
        ServiceComponentModalController.prototype.linkStorageToAllServers = function (storage) {
            var self = this;
            var servers = _.filter(self.$scope.modal.params.service.components, { type: "server" });
            angular.forEach(servers, function (server) { return self.linkComponents(storage, server, true); });
        };
        ServiceComponentModalController.prototype.linkComponents = function (component1, component2, oneWay) {
            component1.relatedcomponents = angular.isUndefined(component1.relatedcomponents) ? [] : component1.relatedcomponents;
            component2.relatedcomponents = angular.isUndefined(component2.relatedcomponents) ? [] : component2.relatedcomponents;
            component1.relatedcomponents.push({ id: component2.id, name: component2.name, instances: component2.instances });
            oneWay || component2.relatedcomponents.push({ id: component1.id, name: component1.name, instances: component1.instances });
            var comp1 = _.map(component1.relatedcomponents);
            component1.relatedcomponents = _.uniq(comp1);
            var comp2 = _.map(component2.relatedcomponents);
            component2.relatedcomponents = _.uniq(comp2);
        };
        ServiceComponentModalController.prototype.getAdjustServiceComponents = function (componentId, instances, serviceId) {
            var self = this;
            return self.$http.post(self.commands.data.services.getAdjustServiceComponents, { source: 'duplicate', componentId: componentId, instances: instances, serviceId: serviceId });
        };
        ServiceComponentModalController.prototype.adjustServiceCall = function (componentId, components, serviceId) {
            var self = this;
            return self.$http.post(self.commands.data.services.adjustService, { componentId: componentId, components: components, serviceId: serviceId });
        };
        ServiceComponentModalController.prototype.saveService = function (data) {
            var self = this, newComponentCopy = angular.copy(data.newComponent);
            newComponentCopy.categories = self.GlobalServices.stringifyCategories(newComponentCopy.categories);
            var components = [];
            components.push(newComponentCopy);
            components.push(data.scaleIOComponent);
            return self.adjustServiceCall(data.newComponent.id, components, data.config.service.id);
        };
        ServiceComponentModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ServiceComponentModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ServiceComponentModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$q', '$timeout', 'Loading', 'GlobalServices', 'FileUploader',
            '$translate', 'constants', 'Commands', '$location', '$filter', "$anchorScroll"];
        return ServiceComponentModalController;
    }());
    asm.ServiceComponentModalController = ServiceComponentModalController;
    angular
        .module('app')
        .controller('ServiceComponentModalController', ServiceComponentModalController);
})(asm || (asm = {}));
//# sourceMappingURL=serviceComponentWrapper.js.map
