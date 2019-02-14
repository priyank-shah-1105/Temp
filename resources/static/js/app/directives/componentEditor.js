var asm;
(function (asm) {
    "use strict";
    var ComponentEditorController = (function () {
        function ComponentEditorController($rootScope, $q, Loading, $http, GlobalServices, $translate, Modal, constants, commands, $location, dialog, $anchorScroll, $timeout, $filter) {
            this.$rootScope = $rootScope;
            this.$q = $q;
            this.Loading = Loading;
            this.$http = $http;
            this.GlobalServices = GlobalServices;
            this.$translate = $translate;
            this.Modal = Modal;
            this.constants = constants;
            this.commands = commands;
            this.$location = $location;
            this.dialog = dialog;
            this.$anchorScroll = $anchorScroll;
            this.$timeout = $timeout;
            this.$filter = $filter;
            this.formProcessing = false;
            this.newComponent = null;
            this.scaleIOComponent = null;
            this.instances = 1;
            this.editMode = true;
            this.associateAllResources = "selected";
            this.continueClicked = false;
            this.saving = false;
            this.action = 'add';
            this.backButton = false;
            var self = this;
            self.action = self.config.action;
            if (self.config.mode !== "edit") {
                self.editMode = false;
            }
            if (self.action == 'edit' || self.action == 'view') {
                self.newComponent = _.find(self.config.template.components, { id: self.config.componentId });
                self.originalSelectedComponents = angular.copy(self.newComponent.relatedcomponents);
                self.originalInstances = angular.copy(self.newComponent.instances);
                self.instances = self.newComponent.instances;
            }
            if (self.action == "add") {
                //give each available component a unique id before linking
                angular.forEach(self.config.availableComponents, function (component) {
                    component.componentid = component.id;
                    //set new unique ID for component
                    component.id = self.$rootScope.ASM.NewGuid();
                });
                if (self.config.type === "server") {
                    self.newComponent = self.config.availableComponents[0];
                    self.newComponent.name = GlobalServices.namePicker(self.config.template.components, [self.newComponent], self.GlobalServices.getBaseName(self.newComponent.name))[0].name;
                }
                if (self.type === "service") {
                    //Associate all resources
                    self.checkAllComponents();
                }
            }
            self.isService = (self.config.serviceId && self.config.serviceId !== null);
        }
        ComponentEditorController.prototype.isRelated = function (relatedId) {
            var self = this;
            return _.find(self.newComponent.relatedcomponents, { id: relatedId });
        };
        ComponentEditorController.prototype.linkComponents = function (component1, component2, oneWay) {
            component1.relatedcomponents = angular.isUndefined(component1.relatedcomponents) ? [] : component1.relatedcomponents;
            component2.relatedcomponents = angular.isUndefined(component2.relatedcomponents) ? [] : component2.relatedcomponents;
            component1.relatedcomponents.push({ id: component2.id, name: component2.name, instances: component2.instances });
            oneWay || component2.relatedcomponents.push({ id: component1.id, name: component1.name, instances: component1.instances });
            var comp1 = _.map(component1.relatedcomponents);
            component1.relatedcomponents = _.uniq(comp1);
            var comp2 = _.map(component2.relatedcomponents);
            component2.relatedcomponents = _.uniq(comp2);
        };
        ComponentEditorController.prototype.unlinkComponents = function (component1, component2) {
            var self = this;
            //component2 is null in cases where we haven't made it far enough to add the actual component and link it to anything
            if (!component2)
                return;
            //var relatedc = component1.relatedcomponents.get(component2.id);
            $.each(component1.relatedcomponents, function (index, modelx) {
                if (modelx.id == component2.id) {
                    component1.relatedcomponents.splice(index, 1);
                    index--;
                    return false;
                }
            });
            $.each(component2.relatedcomponents, function (indx, model) {
                if (model.id == component1.id) {
                    component2.relatedcomponents.splice(indx, 1);
                    indx--;
                    return false;
                }
            });
        };
        ComponentEditorController.prototype.checkClicked = function (c) {
            var self = this;
            var found = false;
            if (!c.isChecked) {
                self.unlinkComponents(c, self.newComponent);
                return;
            }
            ;
            if (!(self.newComponent.type == 'cluster' || self.newComponent.type == 'scaleio')) {
                //if (self.action == 'edit') {
                //    self.newComponent.instances = self.originalInstances;
                //}
                //else {
                self.newComponent.instances = self.instances;
            }
            $.each(self.newComponent.relatedcomponents, function (index, rc) {
                if (rc.id == c.id) {
                    found = true;
                    return;
                }
            });
            if (!found) {
                self.linkComponents(c, self.newComponent);
            }
        };
        ComponentEditorController.prototype.checkAllComponents = function () {
            var self = this;
            // self.newComponent.relatedcomponents = [];
            //    var self: ComponentEditorController = this;
            var components = _.filter(self.config.template.components, function (component) {
                return (component.id != self.newComponent.id && self.validType(component.type));
            });
            $.each(components, function (ix, component) {
                component.isChecked = true;
                self.checkClicked(component);
            });
        };
        ComponentEditorController.prototype.advance = function () {
            var self = this;
            var d = self.$q.defer();
            if (self.chooseForm && self.chooseForm.$invalid) {
                self.chooseForm._submitted = false;
                self.chooseForm._showValidation = true;
                return;
            }
            if (!self.formInvalid(self.preliminaryForm, false)) {
                if (self.newComponent) {
                    self.Loading(d.promise);
                    if (!self.backButton) {
                        self.originalComponent = self.newComponent;
                        self.originalId = self.config.templateId;
                    }
                    self.GlobalServices.ClearErrors(self.errors);
                    if (self.type === 'service') {
                        var sdata = {
                            serviceId: self.config.serviceId,
                            instances: self.instances,
                            componentType: self.newComponent.type,
                            component: self.newComponent
                        };
                        if (self.backButton && self.newComponent) {
                            tdata.component = self.originalComponent;
                            self.backButton = false;
                        }
                        self.getAdjustServiceComponents(sdata)
                            .then(function (response) {
                            self.$anchorScroll("#parentComponentEditor");
                            var tmp = self.newComponent.relatedcomponents;
                            var tmpid = self.newComponent.id;
                            var result = [];
                            result = response.data.responseObj;
                            //filter out scale io
                            self.newComponent = _.filter(result, function (item) {
                                return item.type != 'scaleio';
                            })[0];
                            //filter scaleio only
                            var scaleIOComponent = _.filter(result, function (item) {
                                return item.type == 'scaleio';
                            });
                            if (scaleIOComponent.length > 0) {
                                self.scaleIOComponent = scaleIOComponent[0];
                            }
                            self.newComponent.relatedcomponents = tmp;
                            self.newComponent.id = tmpid;
                            self.continueClicked = true;
                            d.resolve();
                        })
                            .catch(function (response) {
                            self.newComponent = response.data.responseObj;
                            self.GlobalServices.DisplayError(response.data, self.errors);
                        })
                            .finally(function () { return d.resolve(); });
                    }
                    else if (self.type === 'template') {
                        var tdata = {
                            templateId: self.config.templateId,
                            component: self.newComponent,
                            instances: self.instances
                        };
                        if (self.backButton && self.newComponent) {
                            tdata.component = self.originalComponent;
                            tdata.templateId = self.originalId;
                            self.backButton = false;
                        }
                        self.getUpdatedTemplateBuilder(tdata)
                            .then(function (data) {
                            self.$anchorScroll("#parentComponentEditor");
                            var tmp = self.newComponent.relatedcomponents;
                            var tmpid = self.newComponent.id;
                            self.newComponent = data.data.responseObj;
                            self.newComponent.relatedcomponents = tmp;
                            self.newComponent.id = tmpid;
                            self.continueClicked = true;
                            d.resolve();
                        })
                            .catch(function (response) {
                            self.newComponent = response.data.responseObj;
                            self.GlobalServices.DisplayError(response.data, self.errors);
                        })
                            .finally(function () { return d.resolve(); });
                    }
                }
            }
        };
        ComponentEditorController.prototype.categoryVisible = function (category, component) {
            var self = this;
            var filteredSettings = self.$filter("settingsVisibleComponentEditorFilter")(category.settings, component);
            var settingsShown = self.$filter("templatesettings")(filteredSettings, self.isService);
            return settingsShown.length;
        };
        ComponentEditorController.prototype.componentVisible = function (component) {
            var self = this;
            return _.find(component.categories, function (category) { return self.categoryVisible(category, component); });
        };
        ComponentEditorController.prototype.filteredoptions = function (setting, component) {
            var self = this;
            if (!setting || !component)
                return [];
            var returnVal = [];
            var radioGroup = self.GlobalServices.NewGuid();
            $.each(setting.options, function (optionIndex, option) {
                if (option.dependencyTarget && option.dependencyValue) {
                    var targetSetting = null;
                    $.each(component.categories, function (categoryIndex, c) {
                        var matchingSetting = _.find(c.settings, function (s) {
                            return (s.id == option.dependencyTarget);
                        });
                        if (matchingSetting) {
                            targetSetting = matchingSetting;
                            return;
                        }
                    });
                    var matchingValue = false;
                    if (targetSetting && targetSetting.value != null) {
                        var settingvalues = option.dependencyValue.split(',');
                        $.each(settingvalues, function (idx, val) {
                            if (val.toString() == targetSetting.value.toString())
                                matchingValue = true;
                        });
                    }
                    if (matchingValue) {
                        returnVal.push(option);
                    }
                }
                else {
                    returnVal.push(option);
                }
            });
            return returnVal;
        };
        ComponentEditorController.prototype.requiredFieldsRemaining = function () {
            var self = this;
            var component = self.newComponent;
            if (component == null || component.isDisposed)
                return;
            if (!component || !component.categories)
                return 0;
            //counts custom form elements
            var count = _.filter(self.invalidFormElements, { invalid: true }).length;
            $.each(component.categories, function (ix, category) {
                var filteredSettings = self.$filter("settingsVisibleComponentEditorFilter")(category.settings, self.newComponent);
                var settingsShown = self.$filter("templatesettings")(filteredSettings, self.isService);
                $.each(settingsShown, function (iy, s) {
                    if (s.required && !s.value) {
                        count++;
                    }
                });
            });
            return count;
        };
        ComponentEditorController.prototype.validType = function (type) {
            var self = this;
            switch (self.newComponent.type) {
                case 'application':
                    return false;
                case 'vm':
                    return type == 'cluster' || type == 'scaleio';
                case 'cluster':
                case 'scaleio':
                    return type == 'server' || type == 'vm';
                case 'server':
                    return type == 'storage' || type == 'cluster' || type == 'scaleio';
                case 'storage':
                    return type == 'server';
            }
        };
        ComponentEditorController.prototype.add = function () {
            var self = this;
            if (self.newComponent) {
                //clone object and set unique ID
                var clone = angular.copy(self.newComponent);
                clone.name = self.GlobalServices.namePicker(_.filter(self.config.template.components, { type: clone.type }), [clone], self.GlobalServices.getBaseName(clone.name))[0].name;
                if (self.action == 'add') {
                    self.newComponent = clone;
                    self.newComponent.relatedcomponents = [];
                }
                self.newComponent = clone;
            }
        };
        ComponentEditorController.prototype.formInvalid = function (form, checkRemainingFields) {
            var self = this, remFields = checkRemainingFields ? !!self.requiredFieldsRemaining() : false;
            form._submitted = true;
            if (form.$invalid) {
                form._showValidation = true;
            }
            self.GlobalServices.scrollToInvalidElement("templateBuilderDetails");
            return !form.$valid || remFields;
        };
        ComponentEditorController.prototype.addComponent = function () {
            var self = this;
            if (self.formInvalid(self.form, true) || self.saving)
                return;
            if (self.action != 'add') {
                self.updateComponent();
                return false;
            }
            self.saving = true;
            self.newComponent.newItem = true;
            var newComponent = angular.copy(self.newComponent);
            /*Below link components routine is adding duplicate related components, so commented out*/
            //link components
            ////angular.forEach(_.filter(
            ////    self.config.template.components,
            ////    (component: any) => self.validType(component.type) &&
            ////        _.find(self.newComponent.relatedcomponents, { id: component.id })),
            ////    (component) => {
            ////        self.linkComponents(component, newComponent, true);
            ////    });
            if (self.newComponent.type == 'cluster' || self.newComponent.type == 'scaleio') {
                newComponent.instances = 1;
            }
            else {
                newComponent.instances = self.instances;
            }
            self.config.template.components.push(newComponent);
            //save
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.updateRelatedName();
            self.save({ data: self })
                .then(function (data) {
                self.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () {
                d.resolve();
                self.saving = false;
            });
        };
        ComponentEditorController.prototype.deleteComponent = function () {
            var self = this;
            $('.popover').remove();
            self.dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('TEMPLATEBUILDER_Areyousureyouwanttoremovethiscomponent'))
                .then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.commands.data.templates.getTemplateBuilderById, { id: self.templateId, name: "" })
                    .then(function (data) {
                    var d2 = self.$q.defer(), template = data.data.responseObj;
                    self.Loading(d2.promise);
                    angular.forEach(template.components, function (component) {
                        _.remove(component.relatedcomponents, { id: self.newComponent.id });
                    });
                    _.remove(template.components, { id: self.newComponent.id });
                    angular.forEach(template.components, function (component) { return self.stringifyCategories(component.categories); });
                    self.$http.post(self.commands.data.templates.saveTemplate, template)
                        .then(function (data) {
                        d2.resolve();
                        self.close();
                    })
                        .catch(function (response) {
                        self.GlobalServices.DisplayError(response.data, self.errors);
                    })
                        .finally(function () { return d2.resolve(); });
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        ComponentEditorController.prototype.previewSettingUnequal = function (settings, index) {
            var self = this;
            var group = settings[index].group;
            var isUnequal = true;
            for (var i = index - 1; i > -1; i--) {
                if (settings[i].hidefromtemplate) {
                    if (settings[i].group == group) {
                        isUnequal = false;
                    }
                    i = -1;
                }
            }
            return isUnequal;
        };
        ComponentEditorController.prototype.updateRelatedName = function () {
            var self = this;
            //updates the name of this component for other related components
            angular.forEach(self.config.template.components, function (component) {
                angular.forEach(component.relatedcomponents, function (related) {
                    if (related.id == self.newComponent.id) {
                        related.name = self.newComponent.name;
                        related.instances = self.instances;
                    }
                });
            });
        };
        ComponentEditorController.prototype.updateComponent = function () {
            var self = this;
            if (self.action != 'edit' || self.formInvalid(self.form, true) || self.saving)
                return;
            self.saving = true;
            var d = self.$q.defer();
            self.newComponent.instances = self.instances;
            self.updateRelatedName();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            //replace component in template components with changed version
            self.config.template.components[self.config.template.components
                .indexOf(_.find(self.config.template
                .components, { id: self.config.componentId }))] = self.newComponent;
            //set template draft to true
            self.config.template.draft = true;
            self.save({ data: self })
                .then(function (data) {
                self.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () {
                d.resolve();
                self.saving = false;
            });
        };
        ComponentEditorController.prototype.stringifyCategories = function (categories) {
            return angular.forEach(categories, function (category) {
                angular.forEach(category.settings, function (setting) {
                    if (angular.isString(setting.value)) {
                        return;
                    }
                    else if (setting.value === null || angular.isUndefined(setting.value)) {
                        return;
                    }
                    else if (!angular.isString(setting.value)) {
                        return setting.value = JSON.stringify(setting.value);
                    }
                    else if (setting.value === true) {
                        return setting.value = "true";
                    }
                    else if (setting.value === false) {
                        return setting.value = "false";
                    }
                });
            });
        };
        ComponentEditorController.prototype.importConfigurationFromReferenceServer = function () {
            var self = this;
            var cloneModal = self.Modal({
                title: self.$translate.instant('TEMPLATEBUILDER_Clone_Title'),
                modalSize: 'modal-lg',
                templateUrl: 'views/templatebuilder/clone.html',
                controller: 'CloneController as cloneController',
                params: {
                    newComponentId: self.newComponent.componentid
                },
                onComplete: function (component) {
                    var componentCopy = angular.copy(self.newComponent);
                    self.newComponent = component;
                    angular.extend(self.newComponent, {
                        name: componentCopy.name,
                        relatedcomponents: componentCopy.relatedcomponents
                    });
                }
            });
            cloneModal.modal.show();
        };
        ComponentEditorController.prototype.importFromExistingTemplate = function () {
            var self = this;
            var importModal = self.Modal({
                title: self.$translate.instant('TEMPLATEBUILDER_Import_From_Template_Title'),
                modalSize: 'modal-lg',
                templateUrl: 'views/templatebuilder/importfromtemplate.html',
                controller: 'ImportFromTemplateController as importFromTemplateController',
                params: {
                    newComponentId: self.newComponent.componentid
                },
                onComplete: function (component) {
                    var componentCopy = angular.copy(self.newComponent);
                    self.newComponent = component;
                    angular.extend(self.newComponent, {
                        name: componentCopy.name,
                        relatedcomponents: componentCopy.relatedcomponents
                    });
                }
            });
            importModal.modal.show();
        };
        ComponentEditorController.prototype.uploadServerConfigurationProfile = function () {
            var self = this;
            var uploadModal = self.Modal({
                title: self.$translate.instant('TEMPLATEBUILDER_Upload_Title'),
                modalSize: 'modal-lg',
                templateUrl: 'views/templatebuilder/uploadconfig.html',
                controller: 'UploadConfigController as uploadConfigController',
                params: {
                    componentid: self.newComponent.componentid
                },
                onComplete: function (component) {
                    self.newComponent = component;
                }
            });
            uploadModal.modal.show();
        };
        ComponentEditorController.prototype.validateSettings = function () {
            var self = this;
            if (!self.formInvalid(self.form, true)) {
                var validateModal = self.Modal({
                    title: self.$translate.instant('COMPONENTEDITOR_ValidateSettings'),
                    modalSize: 'modal-lg',
                    templateUrl: 'views/templates/validatesettings.html',
                    controller: 'ValidateSettingsController as validateSettingsController',
                    params: {
                        component: angular.copy(self.newComponent)
                    },
                });
                validateModal.modal.show();
            }
        };
        ComponentEditorController.prototype.nameRequired = function () {
            var self = this;
            return self.newComponent.type === "cluster" || self.newComponent.type === "scaleio";
        };
        ComponentEditorController.prototype.scrollTo = function (id) {
            var self = this;
            //wait for collapsing row to close, then only scroll to it if it's opening and not closing
            self.$timeout(function () {
                $("#" + id).find(".collapsed")[0] || self.$anchorScroll("" + id);
            }, 500);
        };
        ComponentEditorController.prototype.getUpdatedTemplateBuilder = function (data) {
            var self = this;
            return self.$http.post(self.commands.data.templates.getUpdatedTemplateBuilderComponent, data);
        };
        ComponentEditorController.prototype.getAdjustServiceComponents = function (data) {
            var self = this;
            return self.$http.post(self.commands.data.services.getAdjustServiceComponents, { source: 'new', componentType: data.componentType, instances: data.instances, serviceId: data.serviceId });
        };
        ComponentEditorController.prototype.close = function () {
            var self = this;
            self.closeModal();
        };
        ComponentEditorController.prototype.cancel = function () {
            var self = this;
            //self.newComponent = self.originalSelectedComponents;
            angular.forEach(self.config.template.components, function (component) {
                component.isChecked = false;
                self.checkClicked(component);
            });
            self.cancelModal();
        };
        ComponentEditorController.$inject = ['$rootScope', '$q', 'Loading', '$http', 'GlobalServices', '$translate',
            'Modal', 'constants', 'Commands', '$location', 'Dialog', '$anchorScroll', "$timeout", "$filter"];
        return ComponentEditorController;
    }());
    angular.module('app')
        .component('componentEditor', {
        templateUrl: 'views/componenteditor.html',
        controller: ComponentEditorController,
        controllerAs: 'ComponentEditor',
        bindings: {
            config: '=',
            closeModal: '&',
            cancelModal: '&',
            save: '&',
            multipleInstancesNotAllowed: '=?',
            templateId: '@',
            form: '=?',
            errors: "=",
            type: "@",
            invalidFormElements: "=",
            chooseForm: "=?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=componentEditor.js.map
