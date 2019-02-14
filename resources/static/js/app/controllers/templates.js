var asm;
(function (asm) {
    var TemplatesController = (function () {
        function TemplatesController(Modal, Dialog, $http, $timeout, $q, $compile, $translate, GlobalServices, Loading, Commands, $location, $filter, constants, $window, $routeParams, dialog, $rootScope, localStorageService) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$compile = $compile;
            this.$translate = $translate;
            this.GlobalServices = GlobalServices;
            this.Loading = Loading;
            this.Commands = Commands;
            this.$location = $location;
            this.$filter = $filter;
            this.constants = constants;
            this.$window = $window;
            this.$routeParams = $routeParams;
            this.dialog = dialog;
            this.$rootScope = $rootScope;
            this.localStorageService = localStorageService;
            this.activeTab = 'MyTemplates';
            this.filterBy = '';
            this.invalidTemplates = false;
            var self = this;
            self.view = 'ListView';
            if (!self.$routeParams.category || self.$routeParams.category === 'mytemplates') {
                self.activeTab = 'MyTemplates';
                self.filterBy = '';
            }
            else if (self.$routeParams.category === "sampletemplates") {
                self.activeTab = 'SampleTemplates';
                self.filterBy = '';
            }
            else {
                self.activeTab = 'MyTemplates';
                self.filterBy = self.$routeParams.category;
            }
            self.refresh();
            self.actions = {
                deleteTemplate: self.deleteTemplate,
                viewDetails: self.viewDetails,
                exportTemplate: self.exportTemplate,
                addAttachment: self.addAttachment,
                cloneTemplate: self.cloneTemplate
            };
        }
        TemplatesController.prototype.clearTooltips = function () {
            $('[data-toggle="tooltip"]').tooltip('hide');
        };
        TemplatesController.prototype.clickTab = function (tab) {
            var self = this;
            self.clearTooltips();
            if (tab == 'MyTemplates') {
                self.activeTab = tab;
                self.$rootScope.helpToken = 'templateshomepage';
                self.selectedItem = self.myTemplates[0];
            }
            else if (tab == 'SampleTemplates') {
                self.activeTab = tab;
                self.$rootScope.helpToken = 'sampletemplates';
                self.selectedItem = self.sampleTemplates[0];
            }
        };
        TemplatesController.prototype.storeView = function () {
            var self = this;
            if (self.view === 'GridView') {
                self.localStorageService.set('templatesTable_currentView', 'tileView');
            }
            else {
                self.localStorageService.set('templatesTable_currentView', 'listView');
            }
        };
        TemplatesController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.clearTooltips();
            self.GlobalServices.ClearErrors();
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.templates.getTemplateList, {})
                .then(function (data) {
                if (self.GlobalServices.cache.templates.length > 0) {
                    self.GlobalServices.cache.templates = data.data.responseObj;
                }
                self.myTemplates = self.$filter('filter')(data.data.responseObj, { 'isLocked': false }, true);
                self.myTemplatesSafe = angular.copy(self.myTemplates);
                self.myTemplatesConst = angular.copy(self.myTemplates);
                self.drafts = _.filter(self.myTemplates, { 'draft': true });
                self.draftsSafe = angular.copy(self.drafts);
                self.draftsConst = angular.copy(self.drafts);
                self.published = _.filter(self.myTemplates, { 'draft': false });
                self.publishedSafe = angular.copy(self.published);
                self.publishedConst = angular.copy(self.published);
                self.sampleTemplates = self.$filter('filter')(data.data.responseObj, { 'isLocked': true }, true);
                self.sampleTemplatesSafe = angular.copy(self.sampleTemplates);
                self.sampleTemplatesConst = angular.copy(self.sampleTemplates);
                if (self.myTemplates) {
                    self.selectedItem = self.selectedItem ? _.find(self.myTemplates, { id: self.selectedItem.id }) : self.myTemplates[0];
                }
                self.invalidTemplates = !!_.find(self.myTemplates, { isTemplateValid: false });
                self.templateCategories = [{ id: '', name: self.$translate.instant("TEMPLATES_AllCategories") }].concat(_.map(_.uniqBy(self.myTemplates, function (template) { return template.category || ''; }), function (category) { return { id: category.category, name: category.category }; }));
                self.updateArrays();
                var storedView = self.localStorageService.get('templatesTable_currentView');
                if (storedView) {
                    if (storedView === 'tileView') {
                        self.view = 'GridView';
                    }
                    else {
                        self.view = 'ListView';
                    }
                }
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            })
                .finally(function () { return d.resolve(); });
            return d.promise;
        };
        TemplatesController.prototype.updateArrays = function () {
            //this is called when category dropdown is changed or a category tile is clicked
            var self = this;
            //if last template in a category is deleted, change filterBy to be unfiltered
            self.filterBy = _.find(self.getUniqueCategories(), { id: self.filterBy }) ? self.filterBy : '';
            self.myTemplates = self.$filter('filter')(self.myTemplatesConst, { category: self.filterBy });
            self.myTemplatesSafe = angular.copy(self.myTemplates);
            self.drafts = self.$filter('filter')(self.draftsConst, { category: self.filterBy });
            self.draftsSafe = angular.copy(self.drafts);
            self.published = self.$filter('filter')(self.publishedConst, { category: self.filterBy });
            self.publishedSafe = angular.copy(self.published);
        };
        TemplatesController.prototype.editTemplate = function (template) {
            var self = this;
            self.editMode = true;
            template.inConfiguration
                ? self.createTemplateWizard(template)
                : self.$location.path("/templatebuilder/" + template.id + "/edit");
        };
        TemplatesController.prototype.viewDetails = function (template) {
            var self = this;
            template.inConfiguration
                ? self.createTemplateWizard(template)
                : self.$location.path("/templatebuilder/" + template.id + "/view");
        };
        TemplatesController.prototype.createTemplateWizard = function (template, templateInputType) {
            var self = this;
            self.clearTooltips();
            var modalTitle = '';
            if (self.editMode) {
                modalTitle = self.$translate.instant('TEMPLATES_EDIT_TEMPLATE_WIZARD_CreateTemplate');
                self.editMode = false;
            }
            else {
                modalTitle = self.$translate.instant('TEMPLATES_CREATE_TEMPLATE_WIZARD_AddaTemplate');
            }
            var createTemplateWizard = self.Modal({
                title: modalTitle,
                onHelp: function () {
                    self.GlobalServices.showHelp('addtemplate');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/templatewizard.html',
                controller: 'TemplateWizardController as templateWizardController',
                params: {
                    template: template ? angular.copy(template) : undefined,
                    templateInputType: templateInputType
                },
                onComplete: function () {
                },
                onCancel: function () {
                    self.refresh();
                    createTemplateWizard.modal.dismiss();
                }
            });
            createTemplateWizard.modal.show();
        };
        TemplatesController.prototype.getUniqueCategories = function () {
            var self = this;
            return self.templateCategories || [];
        };
        TemplatesController.prototype.exportTemplate = function () {
            var self = this;
            self.clearTooltips();
            var exportModal = self.Modal({
                title: self.$translate.instant('TEMPLATES_TEMPLATESETTINGS_MODAL_ExportTemplate'),
                onHelp: function () {
                    self.GlobalServices.showHelp('TemplateExportTemplate');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/templates/exporttemplate.html',
                controller: 'ExportTemplateController as ExportTemplateController',
                params: {
                    templateId: self.selectedItem.id
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            exportModal.modal.show();
        };
        TemplatesController.prototype.downloadAll = function () {
            var self = this;
            var deferred = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.Loading(deferred.promise);
            self.processDownloadRequests('initial', '', deferred);
        };
        TemplatesController.prototype.processDownloadRequests = function (type, id, deferred) {
            var self = this;
            var urlToCall;
            var data;
            urlToCall = self.Commands.data.downloads.status;
            data = { 'id': id };
            if (type == 'initial') {
                urlToCall = self.Commands.data.downloads.create;
                data = { 'type': 'templates' };
            }
            self.$http.post(urlToCall, { requestObj: data }).then(function (data) {
                switch (data.data.responseObj.status) {
                    case 'NOT_READY':
                        self.$timeout(function () {
                            self.processDownloadRequests('status', data.data.responseObj.id, deferred);
                        }, 5000);
                        break;
                    case 'READY':
                        self.$window.location.assign("downloads/getfile/" + data.data.responseObj.id);
                        deferred.resolve();
                        break;
                    case 'ERROR':
                        //handle error
                        var x = 0;
                        deferred.resolve();
                        self.GlobalServices.DisplayError(data.data);
                        break;
                }
            }).catch(function (data) {
                //need to handle error
                deferred.resolve();
                //error is in data
                self.GlobalServices.DisplayError(data.data);
            });
        };
        TemplatesController.prototype.deleteTemplate = function () {
            var self = this, d = self.$q.defer();
            self.clearTooltips();
            self.dialog(('Confirm'), self.$translate.instant('TEMPLATEBUILDER_DiscardConfirm'))
                .then(function () {
                self.GlobalServices.ClearErrors();
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.templates.discardTemplate, [self.selectedItem.id.toString()])
                    .then(function () {
                    self.refresh();
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        TemplatesController.prototype.uploadExternalTemplate = function () {
            var self = this;
            self.clearTooltips();
            var uploadtemplateModal = self.Modal({
                title: self.$translate.instant('TEMPLATES_UploadExternalModal_UploadExternalTemplate'),
                onHelp: function () {
                    self.GlobalServices.showHelp('Template_UploadExternalTemplate');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/templates/uploadtemplatemodal.html',
                controller: 'UploadTemplateModalController as UploadTemplateModal',
                params: {
                    templates: self.myTemplates
                },
                onComplete: function (category) {
                }
            });
            uploadtemplateModal.modal.show();
        };
        TemplatesController.prototype.addAttachment = function () {
            var self = this;
            self.clearTooltips();
            var addAttachmentModal = self.Modal({
                title: self.$translate.instant('TEMPLATES_AddAttachment'),
                onHelp: function () {
                    self.GlobalServices.showHelp('templateAddAttachment');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/templates/templateaddattachmentmodal.html',
                controller: 'AddAttachmentModalController as AddAttachmentModal',
                params: {
                    templateId: self.selectedItem.id
                },
                onComplete: function (attachments) {
                    self.refresh();
                }
            });
            addAttachmentModal.modal.show();
        };
        TemplatesController.prototype.deleteAttachment = function (attachment) {
            var self = this, d = self.$q.defer();
            self.clearTooltips();
            self.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('TEMPLATES_DeleteAttachments')))
                .then(function () {
                self.GlobalServices.ClearErrors();
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.templates.deleteAttachment, { id: self.selectedItem.id, name: attachment.name })
                    .then(function () {
                    _.remove(self.selectedItem.attachments, { id: attachment.id });
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        TemplatesController.prototype.downloadAttachment = function (attachment) {
            var self = this;
            self.$window.open("templates/downloadattachment?name=" + attachment.name + "&templateId=" + self.selectedItem.id, '_blank');
        };
        TemplatesController.prototype.cloneTemplate = function () {
            var self = this;
            self.GlobalServices.ClearErrors();
            self.$http.post(self.Commands.data.templates.getTemplateList, {})
                .then(function (response) {
                var createTemplateModal = self.Modal({
                    title: self.$translate.instant("TEMPLATES_CREATE_TEMPLATE_WIZARD_cloneTemplate"),
                    onHelp: function () {
                        self.GlobalServices.showHelp('cloningtemplate');
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/createtemplate.html',
                    controller: 'CreateTemplateModalController as createTemplateModalController',
                    params: {
                        type: 'clone',
                        template: angular.copy(self.selectedItem),
                        templates: response.data.responseObj
                    },
                    onComplete: function () {
                    },
                    close: function () {
                    }
                });
                createTemplateModal.modal.show();
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            });
        };
        TemplatesController.prototype.getTemplateData = function (templateId) {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.Loading(d.promise);
            //New call for data
            self.$http.post(self.Commands.data.templates.getTemplateBuilderById, { id: templateId }).then(function (data) {
                self.selectedItem = data.data.responseObj;
            }).catch(function (data) {
                d.resolve();
                self.GlobalServices.DisplayError(data.data);
            });
        };
        TemplatesController.prototype.deployNewService = function () {
            var self = this;
            self.clearTooltips();
            var addServiceWizard = self.Modal({
                title: self.$translate.instant('SERVICES_NEW_SERVICE_DeployService'),
                onHelp: function () {
                    self.GlobalServices.showHelp('deployingserviceoverview');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/deployservice/deployservicewizard.html',
                controller: 'DeployServiceWizard as deployServiceWizard',
                params: {
                    templateId: self.selectedItem.id
                },
                onCancel: function () {
                    self.dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_DEPLOY_ConfirmWizardClosing'))
                        .then(function () {
                        addServiceWizard.modal.close();
                    });
                }
            });
            addServiceWizard.modal.show();
        };
        TemplatesController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$compile', '$translate', 'GlobalServices', 'Loading', 'Commands', '$location', '$filter', 'constants', '$window', '$routeParams', 'Dialog', '$rootScope', 'localStorageService'];
        return TemplatesController;
    }());
    asm.TemplatesController = TemplatesController;
    angular
        .module('app')
        .controller('TemplatesController', TemplatesController);
})(asm || (asm = {}));
//# sourceMappingURL=templates.js.map
