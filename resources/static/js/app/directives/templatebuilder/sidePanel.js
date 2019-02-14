var asm;
(function (asm) {
    "use strict";
    var TemplateBuilderSidePanelController = (function () {
        function TemplateBuilderSidePanelController(Modal, dialog, $http, $timeout, $q, $translate, $scope, GlobalServices, loading, Commands, $location) {
            this.Modal = Modal;
            this.dialog = dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.$scope = $scope;
            this.GlobalServices = GlobalServices;
            this.loading = loading;
            this.Commands = Commands;
            this.$location = $location;
            this.refresh();
        }
        TemplateBuilderSidePanelController.prototype.refresh = function () {
            var self = this;
        };
        TemplateBuilderSidePanelController.prototype.editMode = function () {
            var self = this;
            self.$location.path("/templatebuilder/" + self.template.id + "/edit");
        };
        TemplateBuilderSidePanelController.prototype.viewDetails = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('TEMPLATES_TEMPLATESETTINGS_MODAL_TemplateSettings'),
                onHelp: function () {
                    self.GlobalServices.showHelp('templatesettings');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/templatebuilder/viewtemplatedetailsmodal.html',
                controller: 'ViewTemplateDetailsModalController as viewTemplateDetailsModalController',
                params: {
                    template: self.template
                },
                close: function () {
                    self.$scope.modal.close();
                },
            });
            modal.modal.show();
        };
        TemplateBuilderSidePanelController.prototype.editTemplateInformation = function () {
            var self = this;
            var createTemplateModal = self.Modal({
                title: self.$translate.instant('TEMPLATES_EditTemplateInformation'),
                onHelp: function () {
                    self.GlobalServices.showHelp('TemplateEditingTemplateInformation');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/createtemplate.html',
                controller: 'CreateTemplateModalController as createTemplateModalController',
                params: {
                    //selecteduser: modalUser
                    //templates: self.myTemplates,
                    template: self.template,
                    type: 'edit'
                },
                onComplete: function (id) {
                    self.refreshParent();
                },
                close: function () {
                    self.refreshParent();
                }
            });
            createTemplateModal.modal.show();
        };
        TemplateBuilderSidePanelController.prototype.publishTemplate = function () {
            var self = this;
            self.dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_DEPLOY_Confirm')).then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors();
                self.loading(d.promise);
                self.template.draft = false;
                self.publish(self.template).then(function (response) {
                    d.resolve();
                    self.$timeout(function () {
                        self.$location.path("templatebuilder/" + self.template.id + "/view");
                    }, 500);
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data);
                })
                    .finally(function () { return d.resolve(); });
            }).catch(function () { });
        };
        TemplateBuilderSidePanelController.prototype.publish = function (template) {
            var self = this;
            return self.$http.post(self.Commands.data.templates.saveTemplate, template);
        };
        TemplateBuilderSidePanelController.prototype.deleteTemplate = function () {
            var self = this;
            self.dialog(("Confirm"), self.$translate.instant("TEMPLATEBUILDER_DiscardConfirm"))
                .then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors();
                self.loading(d.promise);
                self.$http.post(self.Commands.data.templates.discardTemplate, [self.template.id.toString()])
                    .then(function (data) {
                    self.$location.path('templates');
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        TemplateBuilderSidePanelController.prototype.importTemplate = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('IMPORTTEMPLATE_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('ImportingTemplates');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/importtemplatemodal.html',
                controller: 'ImportTemplateModalController as importtemplate',
                params: {
                    template: self.template.id
                },
                onComplete: function () {
                    self.refreshParent();
                },
            });
            modal.modal.show();
        };
        TemplateBuilderSidePanelController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', '$scope', 'GlobalServices', 'Loading', 'Commands', '$location'];
        return TemplateBuilderSidePanelController;
    }());
    angular.module('app')
        .component('sidePanel', {
        templateUrl: 'views/templatebuilder/sidepanel.html',
        controller: TemplateBuilderSidePanelController,
        controllerAs: 't',
        bindings: {
            template: '=',
            mode: '=',
            jobRequest: '=',
            actions: '=',
            refreshParent: '&'
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=sidePanel.js.map
