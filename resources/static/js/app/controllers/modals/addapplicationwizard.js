var asm;
(function (asm) {
    var AddApplicationWizardController = (function () {
        function AddApplicationWizardController(Modal, $scope, Dialog, $http, $translate, Loading, $q, $timeout, Commands, $rootScope, globalServices, $filter) {
            this.Modal = Modal;
            this.$scope = $scope;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$translate = $translate;
            this.Loading = Loading;
            this.$q = $q;
            this.$timeout = $timeout;
            this.Commands = Commands;
            this.$rootScope = $rootScope;
            this.globalServices = globalServices;
            this.$filter = $filter;
            //this wizard is used in both template builder svg and service svg pages
            this.template = null;
            this.editComponent = null;
            this.service = null;
            this.mode = 'template';
            this.action = 'edit';
            this.id = '';
            this.targetComponents = [];
            this.safeTargetComponents = [];
            this.selectedApplication = -1;
            this.applications = [];
            this.applicationsCopy = [];
            this.addedApplications = [];
            this.errors = new Array();
            //Index offset is created for services so that we can keep install orders correct because already added applications are hidden
            this.indexOffset = 0;
            var self = this;
            if (self.$scope.modal.params.template) {
                self.template = self.$scope.modal.params.template;
            }
            else {
                self.service = angular.copy(self.$scope.modal.params.service);
                self.template = self.service;
                self.mode = "service";
                self.serviceId = self.service.id;
                //remove circular references
                angular.forEach(self.service.components, function (component) { return delete component.deviceTypeListData; });
            }
            self.action = self.$scope.modal.params.action;
            if (self.action == 'edit') {
                self.id = self.$scope.modal.params.id;
                angular.forEach(self.template.components, function (comp) {
                    if (self.id == comp.id) {
                        comp.duplicateApplication = true;
                        self.editComponent = comp;
                    }
                });
                if (self.service != null) {
                    $.each(self.editComponent.relatedcomponents, function (index, model) {
                        if (model.installOrder > 0)
                            self.indexOffset++;
                    });
                }
            }
            else {
                $.each(self.template.components, function (index, comp) {
                    if (comp.type == 'vm' || comp.type == 'server') {
                        var hasApplication = false;
                        $.each(comp.relatedcomponents, function (index, rc) {
                            if (rc.installOrder > 0) {
                                hasApplication = true;
                                return;
                            }
                        });
                        if (!hasApplication) {
                            comp.duplicateApplication = false;
                            self.targetComponents.push(comp);
                        }
                    }
                });
                self.safeTargetComponents = angular.copy(self.targetComponents);
            }
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.templates.getTemplateBuilderComponents, { id: 'application', templateId: self.template.id, serviceId: self.serviceId })
                .then(function (data) {
                self.applications = data.data.responseObj;
                self.applicationsCopy = angular.copy(self.applications);
                self.applications.unshift({
                    id: -1, name: self.$translate.instant('GENERIC_select')
                });
                if (self.action === 'edit')
                    self.filterApplications();
            })
                .catch(function (data) {
                self.globalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        }
        AddApplicationWizardController.prototype.filterApplications = function () {
            var self = this;
            if (self.service == null) {
                var apps = new Array(self.template.components.length - 1);
                $.each(self.editComponent.relatedcomponents, function (index, rc) {
                    if (rc.installOrder > 0) {
                        var r = rc;
                        $.each(self.template.components, function (index2, comp) {
                            if (rc.id == comp.id) {
                                //comp.isNew = false;
                                apps[rc.installOrder - 1] = comp;
                            }
                        });
                    }
                });
                $.each(apps, function (index, model) {
                    if (model) {
                        self.addedApplications.push(model);
                        if (model.subtype != 'type')
                            self.removeApp(model.name);
                    }
                    else {
                        return false;
                    }
                });
            }
            else {
                $.each(self.editComponent.relatedcomponents, function (index, model) {
                    if (model.installOrder > 0) {
                        var x = model;
                        $.each(self.applications, function (index2, app) {
                            if (app.name == model.name && model.subtype != 'type') {
                                self.applications.splice(index2, 1);
                                return false;
                            }
                        });
                    }
                });
            }
        };
        AddApplicationWizardController.prototype.removeApp = function (name) {
            var self = this;
            $.each(self.applications, function (index, model) {
                if (model.name == name) {
                    self.applications.splice(index, 1);
                    return false;
                }
            });
        };
        AddApplicationWizardController.prototype.getVisibleSettings = function (category, component) {
            var self = this;
            return self.$filter("addApplicationSettingsFilter")(category.settings, component);
        };
        AddApplicationWizardController.prototype.categoryVisible = function (category, component) {
            var self = this;
            return self.getVisibleSettings(category, component).length;
        };
        ;
        AddApplicationWizardController.prototype.addApplication = function () {
            var self = this;
            var app = _.findIndex(self.applications, function (o) { return o.id == self.selectedApplication; });
            var x = angular.copy(self.applications[app]);
            var componentId = x.id;
            x.id = self.$rootScope.ASM.NewGuid();
            x.componentid = componentId;
            x.isNew = true;
            self.addedApplications.push(x);
            if (x.subtype != "type") {
                self.applications.splice(app, 1);
                self.selectedApplication = -1;
            }
        };
        AddApplicationWizardController.prototype.removeApplication = function (app) {
            var self = this;
            var index = _.findIndex(self.addedApplications, function (o) { return o.id == app.id; });
            var cloneIndex = _.findIndex(self.applicationsCopy, function (o) { return o.name == app.name; });
            self.applications.push(angular.copy(self.applicationsCopy[cloneIndex]));
            self.addedApplications.splice(index, 1);
        };
        AddApplicationWizardController.prototype.moveUp = function (index) {
            var self = this;
            var x = self.addedApplications[index];
            self.addedApplications[index] = self.addedApplications[index - 1];
            self.addedApplications[index - 1] = x;
        };
        AddApplicationWizardController.prototype.moveDown = function (index) {
            var self = this;
            var x = self.addedApplications[index];
            self.addedApplications[index] = self.addedApplications[index + 1];
            self.addedApplications[index + 1] = x;
        };
        AddApplicationWizardController.prototype.identifyResourcesInvalid = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                if (self.resourceSelected()) {
                    resolve();
                    self.globalServices.ClearErrors(self.errors);
                }
                else {
                    reject();
                    self.globalServices.DisplayError({
                        severity: "critical",
                        message: self.$translate.instant("TEMPLATES_AddApplication_SelectResourcesToContinue")
                    }, self.errors);
                }
                return self.resourceSelected()
                    ? resolve()
                    : reject();
            });
        };
        AddApplicationWizardController.prototype.resourceSelected = function () {
            var self = this;
            return !!_.find(self.targetComponents, { duplicateApplication: true });
        };
        AddApplicationWizardController.prototype.resourcesToDuplicate = function () {
            var self = this;
            return _.filter(self.targetComponents, { duplicateApplication: true });
        };
        AddApplicationWizardController.prototype.requiredFieldsRemaining = function () {
            var self = this, count = 0;
            angular.forEach(self.addedApplications, function (application) {
                angular.forEach(application.categories, function (category) {
                    var visibleSettings = self.getVisibleSettings(category, application);
                    count += _.filter(category.settings, function (setting) {
                        return setting.required && !setting.value;
                    })
                        .length;
                });
            });
            self.formInvalid = !!count || self.globalServices.IsInRole('standard') || self.globalServices.IsInRole('readonly');
            return count;
        };
        AddApplicationWizardController.prototype.validateStep2 = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                self.addedApplications.length ? resolve() : reject();
            });
        };
        AddApplicationWizardController.prototype.cancelWizard = function () {
            var self = this;
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('TEMPLATES_Areyousureyouwanttocancel'))
                .then(function () { return self.$scope.modal.cancel(); });
        };
        AddApplicationWizardController.prototype.finishWizard = function () {
            var self = this;
            if (self.formInvalid) {
                self.globalServices.scrollToInvalidElement("parentAppWizard");
                self.deployApplicationForm._submitted = true;
                return;
            }
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('ADDAPPLICATION_Areyousureyouwanttofinishconfiguration'))
                .then(function () {
                var newComponents = [];
                if (self.service == null) {
                    if (self.action == 'add') {
                        //find all components that we are relating to
                        angular.forEach(self.template.components, function (component, index) {
                            if (component.duplicateApplication) {
                                //clone each application as a new component to add to template/service
                                angular.forEach(self.addedApplications, function (application, index2) {
                                    console.log(application);
                                    var applicationCopy = angular.copy(application);
                                    applicationCopy.id = self.$rootScope.ASM.NewGuid();
                                    newComponents.push(applicationCopy);
                                    //add corresponding related component to existing component
                                    component.relatedcomponents.push({
                                        id: applicationCopy.id,
                                        name: applicationCopy.name,
                                        installOrder: index2 + 1,
                                        subtype: applicationCopy.subtype
                                    });
                                });
                                component.duplicateApplication = false;
                            }
                        });
                        $.each(newComponents, function (index, newComponent) {
                            self.template.components.push(newComponent);
                        });
                    }
                    else if (self.action == 'edit') {
                        //for edit, we will clean out all existing application objects and recreate. Existing ones should retain same id
                        var idsToDelete = [];
                        //find target ids
                        var newRelatedComponents = [];
                        $.each(self.editComponent.relatedcomponents, function (index, relatedComponent) {
                            if (relatedComponent.installOrder > 0)
                                idsToDelete.push(relatedComponent.id);
                            else
                                newRelatedComponents.push(relatedComponent);
                        });
                        //reset the related
                        self.editComponent.relatedcomponents = newRelatedComponents;
                        //delete all old components
                        var newComponents = [];
                        $.each(self.template.components, function (index, model) {
                            if (idsToDelete.indexOf(model.id) == -1)
                                newComponents.push(model);
                        });
                        //reset components
                        self.template.components = newComponents;
                        //readd correct components and relatedcomponents
                        $.each(self.addedApplications, function (index, application) {
                            if (application.isNew) {
                                application.id = self.$rootScope.ASM.NewGuid();
                            }
                            self.template.components.push(application);
                            self.editComponent.relatedcomponents.push({
                                id: application.id,
                                name: application.name,
                                installOrder: index + 1,
                                subtype: application.subtype
                            });
                        });
                    }
                    var d = self.$q.defer();
                    self.globalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.$http.post(self.Commands.data.templates.saveTemplate, self.template)
                        .then(function (data) {
                        d.resolve();
                        self.$scope.modal.close();
                    })
                        .catch(function (data) {
                        self.globalServices.DisplayError(data.data, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
                else {
                    var listOfChanged = [];
                    if (self.action == 'add') {
                        //find all components that we are relating to
                        $.each(self.template.components, function (index, component) {
                            if (component.duplicateApplication) {
                                var c = component;
                                //clone each application as a new component to add to template/service
                                $.each(self.addedApplications, function (index2, model) {
                                    var x = angular.copy(model);
                                    x.id = self.$rootScope.ASM.NewGuid();
                                    //add new app to changed items
                                    listOfChanged.push(x);
                                    //add corresponding related component to existing component
                                    c.relatedcomponents.push({
                                        id: x.id,
                                        name: x.name,
                                        installOrder: index2 + 1,
                                        subtype: x.subtype
                                    });
                                });
                                //add component as changed item
                                listOfChanged.push(c);
                            }
                        });
                    }
                    else if (self.action == 'edit') {
                        //add new related components and update original component
                        $.each(self.addedApplications, function (index, model) {
                            model.id = self.$rootScope.ASM.NewGuid();
                            self.editComponent.relatedcomponents.push({
                                id: model.id,
                                name: model.name,
                                installOrder: index + 1 + self.indexOffset,
                                subtype: model.subtype
                            });
                            listOfChanged.push(model);
                        });
                        listOfChanged.push(self.editComponent);
                    }
                    $.each(listOfChanged, function (index, model) {
                        if (model.device)
                            model.device = {};
                    });
                    var d = self.$q.defer();
                    self.globalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.$http.post(self.Commands.data.services.adjustService, {
                        serviceId: self.service.id,
                        componentId: null,
                        components: listOfChanged
                    })
                        .then(function (data) {
                        d.resolve();
                        self.$scope.modal.close();
                    })
                        .catch(function (data) {
                        d.resolve();
                        self.globalServices.DisplayError(data.data, self.errors);
                    });
                }
            });
        };
        ;
        AddApplicationWizardController.$inject = ['Modal', '$scope', 'Dialog', '$http', '$translate', 'Loading', '$q', '$timeout', 'Commands', '$rootScope', "GlobalServices", "$filter"];
        return AddApplicationWizardController;
    }());
    asm.AddApplicationWizardController = AddApplicationWizardController;
    angular
        .module('app')
        .controller('AddApplicationWizardController', AddApplicationWizardController);
})(asm || (asm = {}));
//# sourceMappingURL=addapplicationwizard.js.map
