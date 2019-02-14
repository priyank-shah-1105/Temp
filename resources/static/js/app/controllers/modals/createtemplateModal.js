var asm;
(function (asm) {
    var CreateTemplateModalController = (function () {
        function CreateTemplateModalController($scope, Modal, Dialog, $http, $q, $timeout, Loading, GlobalServices, FileUploader, $translate, constants, commands, $location, $filter) {
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
            //public templates: any = this.$scope.modal.params.templates;
            this.templates = new Array();
            this.templatesDropdown = new Array();
            this.type = this.$scope.modal.params.type;
            this.template = {
                "id": null,
                "name": "",
                "description": "",
                "type": "new",
                "cloneexistingtemplateid": null,
                "category": null,
                "manageFirmware": false,
                "firmwarePackageId": null,
                "updateServerFirmware": false,
                "updateNetworkFirmware": false,
                "updateStorageFirmware": false,
                "enableApps": false,
                "enableVMs": false,
                "enableCluster": false,
                "enableServer": false,
                "enableStorage": false,
                "allStandardUsers": false,
                "assignedUsers": []
            };
            this.cloneoption = { "id": null };
            this.categories = new Array();
            this.firmwares = new Array();
            this.errors = new Array();
            this.credentials = [];
            var self = this;
            self.templates = self.$scope.modal.params.templates;
            if (self.type === "clone") {
                self.$scope.modal.params.template.name = "";
            }
            angular.extend(self.template, self.$scope.modal.params.template);
            self.activate();
        }
        CreateTemplateModalController.prototype.activate = function () {
            var self = this;
            self.templatesDropdown = self.$filter("filter")(self.templates, { 'isLocked': false }, true);
            if (self.type === "edit") {
                self.template = self.$scope.modal.params.template;
            }
            else {
                self.template.type = self.type;
            }
            if (self.type === "clone") {
                //self.getNetworkList();
                self.cloneoption = _.find(self.templates, { 'id': self.template.id });
                self.template.cloneexistingtemplateid = self.cloneoption.id;
                self.template.id = null;
            }
            self.managePermissions = (self.template.allStandardUsers || self.template.assignedUsers.length > 0);
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.getAvailableFirmwarePackages().then(function (response) {
                self.firmwares = [
                    {
                        id: 'usedefaultcatalog',
                        name: self.$translate.instant("SERVICE_DETAIL_EditService_UseASMappliancedefaultcatalog"),
                        defaultpackage: false
                    }
                ].concat(response.data.responseObj);
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            }).finally(function () { d.resolve(); });
        };
        CreateTemplateModalController.prototype.save = function (isValid, wizardFinish) {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            if (!self.template.manageFirmware) {
                self.template.firmwarePackageId = null;
            }
            if (self.template.allStandardUsers === true) {
                self.template.assignedUsers = [];
            }
            if (wizardFinish) {
                if (self.type == 'new') {
                    self.createTemplate(self.template)
                        .then(function (response) {
                        d.resolve();
                        self.goToTemplateBuilder(response.data.responseObj.id);
                        self.close();
                    })
                        .catch(function (data) {
                        d.resolve();
                        self.GlobalServices.DisplayError(data.data, self.errors);
                    });
                }
                else if (self.type === 'clone') {
                    self.saveTemplateAdditionalSettings(self.template)
                        .then(function (response) {
                        d.resolve();
                        self.goToTemplateBuilder(response.data.responseObj.id);
                        self.close();
                    })
                        .catch(function (data) {
                        d.resolve();
                        self.GlobalServices.DisplayError(data.data, self.errors);
                    });
                }
                else {
                    //edit?
                    self.saveTemplate(self.template)
                        .then(function (response) {
                        d.resolve();
                        self.goToTemplateBuilder(response.data.responseObj.id);
                        self.close();
                    })
                        .catch(function (data) {
                        d.resolve();
                        self.GlobalServices.DisplayError(data.data, self.errors);
                    });
                }
            }
            else {
                //clone
                self.createTemplate(self.template)
                    .then(function (data) {
                    self.template = data.data.responseObj;
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            }
        };
        CreateTemplateModalController.prototype.createNewTemplate = function () {
            var self = this;
            self.forms.createTemplate._submitted = true;
            if (self.forms.createTemplate.$valid) {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.createTemplate(self.template)
                    .then(function (response) {
                    d.resolve();
                    if (self.type !== 'clone') {
                        self.goToTemplateBuilder(response.data.responseObj.id);
                        self.close();
                    }
                })
                    .catch(function (data) {
                    d.resolve();
                    self.GlobalServices.DisplayError(data.data, self.errors);
                });
            }
        };
        CreateTemplateModalController.prototype.editTemplate = function () {
            var self = this;
            self.forms.createTemplate._submitted = true;
            if (self.forms.createTemplate.$valid) {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.saveTemplate(self.template)
                    .then(function (response) {
                    d.resolve();
                    self.goToTemplateBuilder(response.data.responseObj.id);
                    self.close();
                })
                    .catch(function (data) {
                    d.resolve();
                    self.GlobalServices.DisplayError(data.data, self.errors);
                });
            }
        };
        CreateTemplateModalController.prototype.enterStep1 = function () {
            var self = this;
            if (self.originalTemplate) {
                self.template = self.originalTemplate;
            }
        };
        CreateTemplateModalController.prototype.validateStep1 = function () {
            var self = this;
            self.forms.step1._submitted = true;
            return self.$q(function (resolve, reject) {
                self.forms.step1._submitted = true;
                if (self.forms.step1.$valid) {
                    var d = self.$q.defer();
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.createTemplate(self.template)
                        .then(function (data) {
                        resolve();
                        //hold on to original in case user goes back from step 2, we can resubmit it to create template
                        self.originalTemplate = angular.copy(self.template);
                        self.template = data.data.responseObj;
                    })
                        .catch(function (data) {
                        reject();
                        self.GlobalServices.DisplayError(data.data, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
                else {
                    reject();
                }
            });
        };
        CreateTemplateModalController.prototype.enterStep2 = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.getCredentialSummaryList()
                .then(function (response) {
                self.credentials = response.data.responseObj;
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
            ;
            return d.promise;
        };
        CreateTemplateModalController.prototype.addCredential = function (context, settingName) {
            var self = this;
            var theModal = self.Modal({
                title: self.$translate.instant('CREDENTIALS_CreateTitle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('creatingcredentials');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/credentials/editcredentials.html',
                controller: 'EditCredentialsController as editCredentialsController',
                params: {
                    editMode: false,
                    typeId: "os",
                    canChangeCredentialType: false
                },
                onComplete: function (credentialId) {
                    context[settingName] = credentialId;
                    var d = self.$q.defer();
                    self.Loading(d.promise);
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.getCredentialSummaryList()
                        .then(function (response) {
                        self.credentials = response.data.responseObj;
                    })
                        .catch(function (response) {
                        self.GlobalServices.DisplayError(response.data, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
            });
            theModal.modal.show();
        };
        CreateTemplateModalController.prototype.finishWizard = function () {
            var self = this;
            self.forms.step2._submitted = true;
            if (self.forms.step2.$valid) {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.saveTemplateAdditionalSettings(self.template)
                    .then(function (response) {
                    d.resolve();
                    self.goToTemplateBuilder(response.data.responseObj);
                    self.close();
                })
                    .catch(function (data) {
                    d.resolve();
                    self.GlobalServices.DisplayError(data.data, self.errors);
                });
            }
            ;
        };
        CreateTemplateModalController.prototype.goToTemplateBuilder = function (id) {
            var self = this;
            self.$timeout(function () {
                self.$location.path("templatebuilder/" + id + "/edit");
            }, 500);
        };
        CreateTemplateModalController.prototype.viewComponents = function (selected) {
            var self = this, modal = self.Modal({
                title: self.$translate.instant("TEMPLATES_ViewComponentsModalTitle", { name: selected.name }),
                modalSize: 'modal-lg',
                templateUrl: 'views/templates/viewcomponents.html',
                controller: 'ViewComponentsController as viewComponents',
                params: {
                    selected: selected
                }
            });
            modal.modal.show();
        };
        CreateTemplateModalController.prototype.getAvailableFirmwarePackages = function () {
            var self = this;
            return self.$http.post(self.commands.data.firmwarepackages.getAvailableFirmwarePackages, {});
        };
        CreateTemplateModalController.prototype.saveTemplate = function (template) {
            var self = this;
            return self.$http.post(self.commands.data.templates.saveTemplate, template);
        };
        CreateTemplateModalController.prototype.saveTemplateAdditionalSettings = function (template) {
            var self = this;
            return self.$http.post(self.commands.data.templates.saveTemplateAdditionalSettings, template);
        };
        CreateTemplateModalController.prototype.createTemplate = function (template) {
            var self = this;
            return self.$http.post(self.commands.data.templates.createTemplate, template);
        };
        CreateTemplateModalController.prototype.getCredentialSummaryList = function () {
            var self = this;
            return self.$http.post(self.commands.data.credential.getCredentialByType, { id: 'os' });
        };
        CreateTemplateModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        CreateTemplateModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        CreateTemplateModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$q', '$timeout', 'Loading', 'GlobalServices', 'FileUploader', '$translate', 'constants', 'Commands', '$location', '$filter'];
        return CreateTemplateModalController;
    }());
    asm.CreateTemplateModalController = CreateTemplateModalController;
    angular
        .module('app')
        .controller('CreateTemplateModalController', CreateTemplateModalController);
})(asm || (asm = {}));
//# sourceMappingURL=createtemplateModal.js.map
