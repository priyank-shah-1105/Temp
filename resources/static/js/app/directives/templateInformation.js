var asm;
(function (asm) {
    "use strict";
    var TemplateInformationController = (function () {
        function TemplateInformationController(Modal, Dialog, $http, $timeout, $q, $translate, Commands, Loading, GlobalServices, $filter) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.Commands = Commands;
            this.Loading = Loading;
            this.GlobalServices = GlobalServices;
            this.$filter = $filter;
            this.categories = [];
            var self = this;
            self.activate();
        }
        TemplateInformationController.prototype.activate = function () {
            var self = this;
            self.uniqueId = self.GlobalServices.NewGuid();
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors();
            //self.Loading(d.promise);
            self.templates = self.$filter("filter")(self.templates, { 'isLocked': false }, true);
            if (!self.readOnly) {
                self.managePermissions = (self.template.allStandardUsers || (self.template.assignedUsers && self.template.assignedUsers.length));
                self.categoryOption = self.template.category || undefined;
            }
            self.categories = self.getCategories(self.templates, true);
            self.template._allStandardUsers = self.get_allStandardusers(self.template);
            //RCM is now required, so this field must be true (it used to be a checkbox)
            self.template.manageFirmware = true;
            self.initTemplateCategory();
            self.$q.all([
                self.getAvailableFirmwarePackages()
                    .then(function (response) {
                    self.firmwares = [
                        {
                            id: 'usedefaultcatalog',
                            name: self.$translate.instant("SERVICE_DETAIL_EditService_UseASMappliancedefaultcatalog"),
                            defaultpackage: false
                        }
                    ].concat(response.data.responseObj);
                })
            ])
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            })
                .finally(function () { d.resolve(); });
        };
        TemplateInformationController.prototype.getCategories = function (templates, allowNewCategory) {
            var self = this;
            var categories = [];
            var cats = _.groupBy(templates, "category");
            for (var categoryName in cats) {
                if (cats.hasOwnProperty(categoryName)) {
                    categories.push({ name: categoryName, id: categoryName });
                }
            }
            if (allowNewCategory)
                categories = categories.concat([
                    { name: "____________________", id: 123, disabled: true },
                    { name: self.$translate.instant("TEMPLATES_CreateNewCategory"), id: "new" }]);
            return categories;
        };
        TemplateInformationController.prototype.templateCategoryChanged = function () {
            var self = this;
            self.template.category = self.categoryOption === "new" ? undefined : self.categoryOption;
        };
        TemplateInformationController.prototype.initTemplateCategory = function () {
            var self = this;
            if (self.template.category) {
                if (self.template.category === "Sample Templates") {
                    self.template.category = null;
                    return;
                }
                self.categoryOption = _.find(self.categories, { id: self.template.category })
                    ? self.template.category
                    : "new";
            }
        };
        TemplateInformationController.prototype.isNewCat = function () {
            var self = this;
            return !_.find(self.categories, { id: self.template.category });
        };
        TemplateInformationController.prototype.updateAllStandardUsersProp = function (template) {
            switch (template._allStandardUsers) {
                case "admins":
                    template.assignedUsers = [];
                    template.allStandardUsers = false;
                    break;
                case "specific":
                    template.allStandardUsers = false;
                    break;
                case "allStandard":
                    template.assignedUsers = [];
                    template.allStandardUsers = true;
                    break;
            }
        };
        TemplateInformationController.prototype.get_allStandardusers = function (template) {
            return template.allStandardUsers
                ? "allStandard"
                : (template.assignedUsers && template.assignedUsers.length ? "specific" : "admins");
        };
        TemplateInformationController.prototype.getAvailableFirmwarePackages = function () {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.getAvailableFirmwarePackages, {});
        };
        TemplateInformationController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', 'Commands', 'Loading', 'GlobalServices', "$filter"];
        return TemplateInformationController;
    }());
    angular.module('app')
        .component('templateInformation', {
        templateUrl: 'views/templateinformation.html',
        controller: TemplateInformationController,
        controllerAs: 'templateInformationController',
        bindings: {
            template: "=",
            errors: "=",
            form: "=?",
            readOnly: "<?",
            templateUploadedName: "@",
            templates: "<"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=templateInformation.js.map
