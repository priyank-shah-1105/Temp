var asm;
(function (asm) {
    var ServiceInformationController = (function () {
        function ServiceInformationController($scope, Modal, Dialog, $http, $q, $timeout, Loading, GlobalServices, FileUploader, $translate, constants, commands, $filter) {
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
            this.$filter = $filter;
            this.errors = new Array();
            this.templates = new Array();
            this.categories = new Array();
            this.firmwares = new Array();
            var self = this;
            self.activate();
        }
        ServiceInformationController.prototype.activate = function () {
            var self = this;
            //RCM is now required, so this field must be true (it used to be a checkbox)
            self.service.manageFirmware = true;
            self.uniqueId = self.GlobalServices.NewGuid();
            self.GlobalServices.ClearErrors(self.errors);
            self.getTemplates()
                .then(function (data) {
                self.templates = data.data.responseObj;
                //if service has a template tied to it already set that as default
                if (self.service.template && self.service.template.id) {
                    angular.forEach(self.templates, function (template) {
                        if (template.id === self.service.template.id) {
                            self.selectedDropdownTemplate = template;
                        }
                    });
                }
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
            self.getFirmwares();
            self.service._allStandardUsers = self.get_allStandardusers(self.service);
        };
        //Called on change of templates dropdown
        ServiceInformationController.prototype.getTemplate = function (id) {
            var self = this;
            if (!id) {
                return;
            }
            self.GlobalServices.ClearErrors(self.errors);
            return self.Loading(self.getTemplateById(id)
                .then(function (response) {
                self.service = self.updateTemplateInService(self.service, response.data.responseObj);
                self.service._allStandardUsers = self.get_allStandardusers(self.service);
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            }));
        };
        ServiceInformationController.prototype.updateTemplateInService = function (service, template) {
            //move properties from template (updated) into service and redefine service's "template" property to be template (updated)
            return angular.extend(service, template, { template: template });
        };
        ServiceInformationController.prototype.getFirmwares = function () {
            var self = this;
            self.$http.post(self.commands.data.firmwarepackages.getAvailableFirmwarePackages, {})
                .then(function (response) {
                self.firmwares = [
                    {
                        id: 'usedefaultcatalog',
                        name: self.$translate.instant("SERVICE_DETAIL_EditService_UseASMappliancedefaultcatalog"),
                        defaultpackage: false
                    }
                ].concat(response.data.responseObj);
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        ServiceInformationController.prototype.updateAllStandardUsersProp = function (context) {
            switch (context._allStandardUsers) {
                case "admins":
                    context.assignedUsers = [];
                    context.allStandardUsers = false;
                    break;
                case "specific":
                    context.allStandardUsers = false;
                    break;
                case "allStandard":
                    context.assignedUsers = [];
                    context.allStandardUsers = true;
                    break;
            }
        };
        ServiceInformationController.prototype.get_allStandardusers = function (context) {
            return context.allStandardUsers
                ? "allStandard"
                : (context.assignedUsers && context.assignedUsers.length ? "specific" : "admins");
        };
        ServiceInformationController.prototype.getTemplates = function () {
            var self = this;
            return self.$http.post(self.commands.data.templates.getReadyTemplateList, {});
        };
        ServiceInformationController.prototype.getTemplateById = function (id) {
            var self = this; //change whether it deploys to a parameter passed in, pass param in wherever this component is used
            return self.$http.post(self.commands.data.templates.getTemplateBuilderById, { id: id, deploy: self.deploying });
        };
        ServiceInformationController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$q', '$timeout', 'Loading', 'GlobalServices', 'FileUploader', '$translate', 'constants', 'Commands', '$filter'];
        return ServiceInformationController;
    }());
    asm.ServiceInformationController = ServiceInformationController;
    angular.module('app')
        .component('serviceInformation', {
        templateUrl: 'views/services/deployservice/serviceinformation.html',
        controller: ServiceInformationController,
        controllerAs: 'serviceInformationController',
        bindings: {
            service: '=',
            form: '=?',
            deploying: "=",
            readOnly: "<?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=serviceInformation.js.map
