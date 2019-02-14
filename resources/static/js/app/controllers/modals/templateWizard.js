var asm;
(function (asm) {
    /*
     Params for editing template:
    template: any,
    templateInputType: string
     */
    var TemplateWizardController = (function () {
        function TemplateWizardController($scope, Modal, Dialog, $http, $translate, Commands, GlobalServices, Loading, $q, $timeout, FileUploader, $location, $filter, $rootScope) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$translate = $translate;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.Loading = Loading;
            this.$q = $q;
            this.$timeout = $timeout;
            this.FileUploader = FileUploader;
            this.$location = $location;
            this.$filter = $filter;
            this.$rootScope = $rootScope;
            this.errors = new Array();
            this.wizardFinishText = this.$translate.instant("TEMPLATES_CREATE_TEMPLATE_WIZARD_Publish");
            var self = this;
            self.steps = {
                step1: {
                    uploadEmc: {
                        form: undefined,
                        id: "collapseOne",
                        fileModel: undefined,
                        encryptionPassword: "",
                        showSuccess: false,
                        uploader: self.$scope.uploader = self.newUploader(),
                        resetUploader: function () {
                            //clear this for validation
                            self.steps.step1.uploadEmc.fileModel = undefined;
                            //remove file name from input
                            var fileInput = document.getElementById("emcTemplatefile");
                            fileInput.value = "";
                            //reset form
                            this.form._submitted = false;
                        },
                        save: function (deferred) {
                            var uploadEmcScope = this;
                            self.GlobalServices.ClearErrors(self.errors);
                            self.Loading(deferred.promise);
                            //using angular uploader
                            self.$scope.uploader.formData = [];
                            self.$scope.uploader.formData.push({ encryptionPassword: this.encryptionPassword });
                            //set error and success callbacks 
                            var error = false;
                            angular.extend(self.$scope.uploader, {
                                onErrorItem: function (fileItem, response, status, headers) {
                                    fileItem.isUploaded = false;
                                    error = true;
                                    self.GlobalServices.DisplayError(response.data, self.errors);
                                    deferred.reject();
                                },
                                onCompleteAll: function (criteriaObj, errorObj, responseObj, responseCode) {
                                    if (!error) {
                                        deferred.resolve();
                                    }
                                },
                                onBeforeUploadItem: function (item) {
                                    item.formData = angular.copy(item.uploader.formData);
                                },
                                onSuccessItem: function (item, response, status, headers) {
                                    if (response.responseCode != 0) {
                                        item.isUploaded = false;
                                        error = true;
                                        self.GlobalServices.DisplayError(response.errorObj, self.errors);
                                        uploadEmcScope.resetUploader();
                                        self.GlobalServices.scrollTo();
                                        deferred.reject();
                                    }
                                    else {
                                        self.template = response.responseObj;
                                        //display green banner on next page
                                        self.steps.step1.uploadEmc.showSuccess = true;
                                        //copy file so we keep it after we reset uploader
                                        self.fileModel = self.steps.step1.uploadEmc.fileModel;
                                        uploadEmcScope.resetUploader();
                                    }
                                }
                            });
                            self.$scope.uploader.uploadAll();
                        }
                    },
                    clone: {
                        form: undefined,
                        id: "collapseTwo",
                        templates: [],
                        categoryOption: undefined,
                        categories: [],
                        selectedTemplate: undefined,
                        save: function () {
                            var createTemplateModal = self.Modal({
                                title: self.$translate.instant("TEMPLATES_CREATE_TEMPLATE_WIZARD_title", { templateName: this.selectedTemplate.name }),
                                onHelp: function () {
                                    self.GlobalServices.showHelp('cloningtemplate');
                                },
                                modalSize: 'modal-lg',
                                templateUrl: 'views/createtemplate.html',
                                controller: 'CreateTemplateModalController as createTemplateModalController',
                                params: {
                                    type: 'clone',
                                    template: this.selectedTemplate,
                                    templates: self.steps.step1.clone.templates
                                }
                            });
                            //need timeout for page title updates
                            self.$timeout(function () {
                                createTemplateModal.modal.show();
                            }, 500);
                            self.$timeout(function () {
                                self.$scope.modal.close();
                            }, 0);
                        },
                        init: function (templateToClone) {
                            this.categoryOption = templateToClone.category;
                            this.selectedTemplate = templateToClone;
                            this.save();
                        }
                    },
                    upload: {
                        form: {},
                        id: "collapseThree",
                        fileModel: undefined,
                        save: function (deferred) {
                            deferred.resolve();
                            var uploadtemplateModal = self.Modal({
                                title: self.$translate.instant('TEMPLATES_UploadExternalModal_UploadExternalTemplate'),
                                onHelp: function () {
                                    self.GlobalServices.showHelp('Template_UploadExternalTemplate');
                                },
                                modalSize: 'modal-lg',
                                templateUrl: 'views/templates/uploadtemplatemodal.html',
                                controller: 'UploadTemplateModalController as UploadTemplateModal',
                                params: {},
                                onComplete: function (category) {
                                }
                            });
                            //need timeout for page title updates
                            self.$timeout(function () {
                                uploadtemplateModal.modal.show();
                            }, 500);
                            self.close();
                        }
                    },
                    create: {
                        form: undefined,
                        id: "collapseFour",
                        save: function (deferred) {
                            deferred.resolve();
                            var createTemplateModal = self.Modal({
                                title: self.$translate.instant('TEMPLATES_CreateTemplate'),
                                onHelp: function () {
                                    self.GlobalServices.showHelp('creatingtemplate');
                                },
                                modalSize: 'modal-lg',
                                templateUrl: 'views/createtemplate.html',
                                controller: 'CreateTemplateModalController as createTemplateModalController',
                                params: {
                                    type: 'new',
                                    template: { name: self.template.name },
                                    templates: self.steps.step1.clone.templates
                                }
                            });
                            //need timeout for page title updates
                            self.$timeout(function () {
                                createTemplateModal.modal.show();
                            }, 500);
                            self.close();
                        }
                    },
                    validate: function () {
                        var d = self.$q.defer();
                        var section;
                        switch (self.templateInputType) {
                            case "uploadEmc":
                                section = this.uploadEmc;
                                break;
                            case "clone":
                                section = this.clone;
                                break;
                            case "upload":
                                section = this.upload;
                                break;
                            case "create":
                                section = this.create;
                                break;
                        }
                        section.form._submitted = true;
                        //if form is valid, pass deferred to section's save function and it will reject or resolve it accordingly 
                        self.GlobalServices.scrollToInvalidElement(section.id) ? d.reject() : section.save(d);
                        self.GlobalServices.ClearErrors(self.errors);
                        return d.promise;
                    },
                    hidden: false
                },
                step2A: {
                    tagId: "step2A",
                    form: undefined,
                    save: function () {
                        var _this = this;
                        var stepScope = this;
                        self.GlobalServices.ClearErrors(self.errors);
                        return self.$q(function (resolve, reject) {
                            _this.form._submitted = true;
                            self.GlobalServices.scrollToInvalidElement(stepScope.tagId);
                            return _this.form.$invalid ? reject() : resolve();
                        });
                    },
                    initialize: function () {
                        self.steps.step1.uploadEmc.showSuccess = false;
                        self.$timeout(function () { self.GlobalServices.scrollTo(); }, 500);
                    }
                },
                step2B: {
                    tagId: "step2B",
                    form: undefined,
                    save: function () {
                        var _this = this;
                        var stepScope = this;
                        self.GlobalServices.ClearErrors(self.errors);
                        return self.$q(function (resolve, reject) {
                            _this.form._submitted = true;
                            self.GlobalServices.scrollToInvalidElement(stepScope.tagId);
                            return _this.form.$invalid ? reject() : resolve();
                        });
                    },
                    initialize: function () {
                        self.GlobalServices.scrollTo(this.tagId);
                    }
                },
                step2C: {
                    tagId: "step2C",
                    form: undefined,
                    initialized: false,
                    networks: [],
                    refresh: function () {
                        var stepScope = this;
                        self.GlobalServices.ClearErrors(self.errors);
                        self.Loading(self.$http.post(self.Commands.data.networking.networks.getNetworksList, null)
                            .then(function (response) {
                            stepScope.networks = response.data.responseObj;
                        }));
                    },
                    initialize: function () {
                        self.steps.step1.uploadEmc.showSuccess = false;
                        this.refresh();
                        self.GlobalServices.scrollTo(this.tagId);
                    },
                    save: function () {
                        var _this = this;
                        var stepScope = this;
                        return self.$q(function (resolve, reject) {
                            _this.form._submitted = true;
                            self.GlobalServices.scrollToInvalidElement(stepScope.tagId);
                            //get asm::configuration::cluster_details::datacenter setting value and plug it into setting in vds with id of asm::configuration::cluster_details::datacenter
                            var dataCenter = _.find(self.template.configureTemplateConfiguration.clusterDetailsSettings, { id: "asm::configuration::cluster_details::datacenter" });
                            if (dataCenter) {
                                var vdsSetting = _.find(self.template.configureTemplateConfiguration.vdsSettings, { id: "asm::configuration::cluster_details::datacenter" });
                                if (vdsSetting) {
                                    vdsSetting.value = dataCenter.value;
                                }
                            }
                            return _this.form.$invalid ? reject() : resolve();
                        });
                    }
                },
                step3A: {
                    tagId: "step3A",
                    form: undefined,
                    initialized: false,
                    initialize: function () {
                        self.GlobalServices.scrollTo(this.tagId);
                        //only do it once
                        if (!this.initialized) {
                            this.appendOptions();
                            this.initialized = true;
                        }
                    },
                    appendOptions: function () {
                        _.forEach(self.template.configureTemplateConfiguration.vdsSettings, function (setting) {
                            return setting.portGroups = self.addNewOption(setting.portGroups, "TEMPLATES_CREATE_TEMPLATE_WIZARD_3a_Createnew");
                        });
                    },
                    save: function () {
                        var _this = this;
                        var stepScope = this;
                        self.GlobalServices.ClearErrors(self.errors);
                        return self.$q(function (resolve, reject) {
                            _this.form._submitted = true;
                            self.GlobalServices.scrollToInvalidElement(stepScope.tagId);
                            return _this.form.$invalid ? reject() : resolve();
                        });
                    }
                },
                step4A: {
                    tagId: "step4A",
                    form: undefined,
                    initialize: function () {
                        self.GlobalServices.scrollTo(this.tagId);
                    },
                    save: function () {
                        var _this = this;
                        var stepScope = this;
                        self.GlobalServices.ClearErrors(self.errors);
                        return self.$q(function (resolve, reject) {
                            _this.form._submitted = true;
                            self.GlobalServices.scrollToInvalidElement(stepScope.tagId);
                            return _this.form.$invalid ? reject() : resolve();
                        });
                    }
                },
                summary: {
                    safeNetworkTableData: undefined,
                    initialize: function () {
                        self.GlobalServices.scrollTo(this.tagId);
                        this.safeNetworkTableData = angular.copy(self.template.configureTemplateConfiguration.networkSettings);
                    }
                }
            };
            self.saveAsDraftButton = {
                click: function () {
                    self.saveAsDraft(self.template);
                },
                text: self.$translate.instant("GENERIC_SaveAsDraft"),
                disabled: function () {
                    return self.template && !self.template.category;
                }
            };
            self.activate();
        }
        TemplateWizardController.prototype.newUploader = function () {
            var self = this;
            return new self.FileUploader({
                url: self.Commands.data.configureTemplate.uploadConfigurableTemplate,
                removeAfterUpload: true
            });
        };
        TemplateWizardController.prototype.filteredoptions = function (params) {
            var self = this;
            var setting = params.setting;
            var settingsArray = params.settingsArray;
            if (!setting)
                return [];
            var returnVal = [];
            var radioGroup = self.GlobalServices.NewGuid();
            $.each(setting.options, function (optionIndex, option) {
                if (option.dependencyTarget && option.dependencyValue) {
                    var targetSetting = null;
                    var matchingSetting = _.find(settingsArray, function (s) {
                        return (s.id == option.dependencyTarget);
                    });
                    if (matchingSetting) {
                        targetSetting = matchingSetting;
                    }
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
        TemplateWizardController.prototype.settingVisible = function (setting, settingsList) {
            var self = this;
            var targetSetting = null;
            if (setting.hidefromtemplate)
                return false;
            if (setting && settingsList && settingsList.length && setting.dependencyTarget && setting.dependencyValue) {
                targetSetting = _.find(settingsList, { id: setting.dependencyTarget }) || null;
                var matchingValue = false;
                if (targetSetting && targetSetting.value != null) {
                    var settingvalues = setting.dependencyValue.split(',');
                    matchingValue = !!_.find(settingvalues, function (val) {
                        return (val.toString() == targetSetting.value.toString());
                    });
                }
                return matchingValue && self.settingVisible(targetSetting, settingsList);
            }
            return true;
        };
        TemplateWizardController.prototype.activate = function () {
            var self = this;
            self.templateInputType = self.$scope.modal.params.templateInputType || "uploadEmc";
            switch (self.templateInputType) {
                case "uploadEmc":
                    self.editEmcTemplate(self.$scope.modal.params.template);
                    break;
                case "clone":
                    var d = self.$q.defer();
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.initStep1CloneTemplate()
                        .then(function (data) {
                        self.steps.step1.clone.init(self.$scope.modal.params.template);
                    })
                        .catch(function (error) {
                        self.GlobalServices.DisplayError(error.data, self.errors);
                    })
                        .finally(function () {
                        d.resolve();
                    });
                    break;
                case "upload":
                    //launch upload modal
                    self.steps.step1.upload.save();
                    break;
                case "create":
                    break;
            }
        };
        TemplateWizardController.prototype.editEmcTemplate = function (template) {
            var self = this;
            if (template) {
                self.getConfigureTemplateById(self.$scope.modal.params.template.id);
                self.steps.step1.hidden = true;
            }
        };
        TemplateWizardController.prototype.cloneTemplate = function (template) {
            var self = this;
            if (template) {
                self.template = self.$scope.modal.params.template;
            }
        };
        TemplateWizardController.prototype.initStep1 = function () {
            var self = this;
            //self.initEMCUploader();
            self.initUploader();
            self.initStep1CloneTemplate();
        };
        //initEMCUploader(): void {
        //    var self: TemplateWizardController = this;
        //    //set file form element to value of file (truthy or undefined)
        //    self.$timeout(() => {
        //        document.getElementById('emcTemplatefile').onchange = (evt) => {
        //            var element: any = angular.element(evt.target)[0];
        //            self.$timeout(() => { self.steps.step1.uploadEmc.fileModel = element.files[0]; });
        //        };
        //    }, 500);
        //}
        TemplateWizardController.prototype.initUploader = function () {
            var self = this;
            //set file form element to value of file (truthy or undefined)
            self.$timeout(function () {
                if (document.getElementById('templatefile')) {
                    document.getElementById('templatefile').onchange = function (evt) {
                        var element = angular.element(evt.target)[0];
                        self.$timeout(function () { self.steps.step1.upload.fileModel = element.files[0]; });
                    };
                }
            }, 500);
        };
        TemplateWizardController.prototype.initStep1CloneTemplate = function () {
            var self = this;
            return self.getTemplateList()
                .then(function (data) {
                self.steps.step1.clone.templates = data.data.responseObj;
                self.steps.step1.clone.categories = self.getCategories(self.steps.step1.clone.templates);
            });
        };
        TemplateWizardController.prototype.getCategories = function (templates, allowNewCategory) {
            var self = this;
            var categories = [];
            var cats = _.groupBy(templates, "category");
            for (var categoryName in cats) {
                if (cats.hasOwnProperty(categoryName)) {
                    categories.push({ name: categoryName, id: categoryName });
                }
            }
            if (allowNewCategory)
                categories = self.addNewOption(categories, "TEMPLATES_CreateNewCategory");
            return categories;
        };
        TemplateWizardController.prototype.addNewOption = function (array, optionText) {
            var self = this;
            return _.concat(array, [
                { name: "____________________", disabled: true, id: 1 },
                { name: self.$translate.instant(optionText), id: "new" }
            ]);
        };
        TemplateWizardController.prototype.viewNetworks = function () {
            var self = this;
            var addNetworks = self.Modal({
                title: self.$translate.instant('GENERIC_Networks'),
                onHelp: function () {
                    self.GlobalServices.showHelp('networksaddingediting');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/networking/networks/definenetworksmodal.html',
                controller: 'DefineNetworksController as defineNetworksController',
                params: {},
                onCancel: function () {
                    self.steps.step2C.refresh();
                    addNetworks.modal.dismiss();
                },
                onComplete: function () {
                    self.steps.step2C.refresh();
                }
            });
            addNetworks.modal.show();
        };
        TemplateWizardController.prototype.switchSettingChanged = function (switchSetting) {
            switchSetting.value = switchSetting.valueSelection === "new" ? null : switchSetting.valueSelection;
        };
        TemplateWizardController.prototype.getTemplateList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.templates.getTemplateList, {});
        };
        TemplateWizardController.prototype.getAvailableFirmwarePackages = function () {
            var self = this;
            return self.$http.post(self.Commands.data.firmwarepackages.getAvailableFirmwarePackages, {});
        };
        TemplateWizardController.prototype.goToTemplateBuilder = function (id) {
            var self = this;
            self.$timeout(function () {
                self.$location.path("templatebuilder/" + id + "/edit");
            }, 500);
        };
        TemplateWizardController.prototype.getConfigureTemplateById = function (templateId) {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.configureTemplate.getConfigureTemplateById, {
                id: templateId
            })
                .then(function (data) {
                self.template = data.data.responseObj;
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        TemplateWizardController.prototype.saveAsDraft = function (template) {
            var self = this;
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('TEMPLATES_CREATE_TEMPLATE_WIZARD_SaveAsDraftConfirmMessage'))
                .then(function () {
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(self.$http.post(self.Commands.data.configureTemplate.saveConfigureTemplate, angular.extend(template, {
                    draft: true,
                    inConfiguration: true
                }))
                    .then(function (response) {
                    self.template = response.data.responseObj;
                    self.close();
                })
                    .catch(function (error) {
                    self.GlobalServices.DisplayError(error.data, self.errors);
                }));
            });
        };
        TemplateWizardController.prototype.finishWizard = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            var d = self.$q.defer();
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.configureTemplate.saveConfigureTemplate, angular.extend(self.template, { draft: false, inConfiguration: false }))
                .then(function (response) {
                self.$timeout(function () { return self.deployConfirmation(response.data.responseObj.id); }, 1000);
                self.close();
            })
                .catch(function (error) {
                self.GlobalServices.DisplayError(error.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        TemplateWizardController.prototype.deployConfirmation = function (templateId) {
            var self = this;
            var deployServiceConfirmModal = self.Modal({
                title: self.$translate.instant('TEMPLATES_CREATE_TEMPLATE_WIZARD_TemplateSuccessfullyPublished'),
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                titleIcon: "text-success ci-health-floating-check",
                modalSize: 'modal-lg',
                templateUrl: 'views/services/deployservice/confirmdeployservicemodal.html',
                controller: 'ConfirmDeployServiceModalController as confirmDeployServiceModalController',
                params: {
                    templateId: templateId
                },
                onCancel: function () {
                    self.close();
                },
                onComplete: function () {
                    self.close();
                }
            });
            deployServiceConfirmModal.modal.show();
        };
        TemplateWizardController.prototype.cancel = function () {
            var self = this;
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('TEMPLATES_CREATE_TEMPLATE_WIZARD_Cancel'))
                .then(function () { return self.$scope.modal.cancel(); });
        };
        TemplateWizardController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        TemplateWizardController.$inject = ['$scope', 'Modal', 'Dialog', '$http',
            '$translate', 'Commands', 'GlobalServices', 'Loading', '$q', "$timeout", "FileUploader", "$location", "$filter", "$rootScope"];
        return TemplateWizardController;
    }());
    asm.TemplateWizardController = TemplateWizardController;
    angular
        .module('app')
        .controller('TemplateWizardController', TemplateWizardController);
})(asm || (asm = {}));
//# sourceMappingURL=templateWizard.js.map
