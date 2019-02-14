var asm;
(function (asm) {
    var DeployServiceWizard = (function () {
        function DeployServiceWizard($scope, Modal, dialog, $http, $q, $timeout, Loading, GlobalServices, $translate, constants, commands, $location, $filter) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.dialog = dialog;
            this.$http = $http;
            this.$q = $q;
            this.$timeout = $timeout;
            this.Loading = Loading;
            this.GlobalServices = GlobalServices;
            this.$translate = $translate;
            this.constants = constants;
            this.commands = commands;
            this.$location = $location;
            this.$filter = $filter;
            /**
             Date picker docs:
            http://eonasdan.github.io/bootstrap-datetimepicker/
             */
            this.service = {
                scheduleType: "deploynow"
            };
            this.datePicker = {
                options: {
                    format: "L hh:mm A",
                    minDate: moment()
                },
                selectedDate: moment().add(1, 'hour')
            };
            this.forms = { step3: {} };
            this.invalidArray = [];
            this.errors = new Array();
            var self = this;
            //if deploying an existing template, otherwise we're creating a new template to deploy
            if (self.$scope.modal.params.templateId) {
                var d = self.$q.defer();
                self.Loading(d.promise);
                self.getTemplateBuilder(self.$scope.modal.params.templateId, true)
                    .then(function (response) {
                    self.service.template = response.data.responseObj;
                    angular.extend(self.service, self.service.template);
                })
                    .catch(function (error) { return self.GlobalServices.DisplayError(error.data, self.errors); })
                    .finally(function () { return d.resolve(); });
            }
        }
        DeployServiceWizard.prototype.validateStep1 = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                if (self.forms.step1.$valid) {
                    self.GlobalServices.ClearErrors(self.errors);
                    return resolve();
                }
                self.forms.step1._submitted = true;
                reject();
            });
        };
        DeployServiceWizard.prototype.settingVisible = function (setting, component) {
            var self = this;
            if (!setting || !component)
                return true;
            if (setting.dependencyTarget && setting.dependencyValue) {
                var targetSetting = null;
                $.each(component.categories, function (ix, c) {
                    var matchingSetting = _.find(c.settings, function (s) { return (s.id == setting.dependencyTarget); });
                    if (matchingSetting) {
                        targetSetting = matchingSetting;
                        return;
                    }
                });
                var matchingValue = false;
                if (targetSetting && targetSetting.value != null) {
                    var settingvalues = setting.dependencyValue.split(',');
                    $.each(settingvalues, function (idx, val) {
                        if (val.toString() === targetSetting.value.toString())
                            matchingValue = true;
                    });
                }
                return matchingValue && self.settingVisible(targetSetting, component);
            }
            return true;
        };
        DeployServiceWizard.prototype.categoryVisible = function (category, component) {
            var self = this;
            var deploySettings = self.$filter('deploysettings')(category.settings);
            return _.find(deploySettings, function (setting) { return self.settingVisible(setting, component); });
        };
        DeployServiceWizard.prototype.componentVisible = function (component) {
            var self = this;
            return !!_.find(component.categories, function (category) { return self.categoryVisible(category, component); });
        };
        DeployServiceWizard.prototype.viewDetails = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('TEMPLATES_TEMPLATESETTINGS_MODAL_TemplateSettings'),
                modalSize: 'modal-lg',
                templateUrl: 'views/templatebuilder/viewtemplatedetailsmodal.html',
                controller: 'ViewTemplateDetailsModalController as viewTemplateDetailsModalController',
                params: {
                    template: self.service.template
                }
            });
            modal.modal.show();
        };
        DeployServiceWizard.prototype.validateStep2 = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                if (self.forms.step2.$valid) {
                    self.GlobalServices.ClearErrors(self.errors);
                    resolve();
                }
                else {
                    self.forms.step2._submitted = true;
                    reject();
                }
            });
        };
        DeployServiceWizard.prototype.validateStep3 = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                if (self.forms.step3.$valid && !self.timeInvalid()) {
                    self.GlobalServices.ClearErrors(self.errors);
                    resolve();
                }
                else {
                    self.forms.step3._submitted = true;
                    reject();
                }
            });
        };
        DeployServiceWizard.prototype.timeInvalid = function () {
            var self = this;
            return self.service.scheduleType === 'schedule' && (!self.datePicker.selectedDate || moment().isAfter(self.datePicker.selectedDate.toISOString()));
        };
        DeployServiceWizard.prototype.finishWizard = function () {
            var self = this;
            if (self.timeInvalid()) {
                return;
            }
            ;
            self.service.scheduleDate = self.service.scheduleType === "schedule"
                ? self.datePicker.selectedDate.toISOString()
                : "";
            self.generateService();
        };
        DeployServiceWizard.prototype.generateService = function () {
            var self = this, d = self.$q.defer();
            self.dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_Areyousureyouwishtodeploythisservice'))
                .then(function () {
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.createService(self.service)
                    .then(function (response) {
                    self.$timeout(function () {
                        self.$location.path("service/" + response.data.responseObj.id + "/details");
                    }, 500);
                    self.close();
                })
                    .catch(function (error) {
                    self.GlobalServices.DisplayError(error.data, self.errors);
                    //if (error.data.fldErrors.length) {
                    //    self.customErrors.push(error.data);
                    self.$timeout(function () {
                        $("#erroripverification_link")
                            .click(function (e) {
                            self.close();
                            self.$location.path("settings/VirtualApplianceManagement");
                        });
                    });
                    //} else {
                    //    self.GlobalServices.DisplayError(error.data, self.errors);
                    //}
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        DeployServiceWizard.prototype.createService = function (service) {
            var self = this;
            return self.$http.post(self.commands.data.services.createService, service);
        };
        DeployServiceWizard.prototype.getTemplateBuilder = function (templateId, deploy) {
            var self = this;
            return self.$http.post(self.commands.data.templates.getTemplateBuilderById, { id: templateId, deploy: deploy });
        };
        DeployServiceWizard.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        DeployServiceWizard.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        DeployServiceWizard.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$q', '$timeout', 'Loading', 'GlobalServices', '$translate', 'constants', 'Commands', '$location', '$filter'];
        return DeployServiceWizard;
    }());
    asm.DeployServiceWizard = DeployServiceWizard;
    angular
        .module("app")
        .controller("DeployServiceWizard", DeployServiceWizard);
})(asm || (asm = {}));
//# sourceMappingURL=deployServiceWizard.js.map
