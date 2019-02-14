var asm;
(function (asm) {
    var EditServiceModalController = (function () {
        function EditServiceModalController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, $filter, MessageBox, Modal) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$filter = $filter;
            this.MessageBox = MessageBox;
            this.Modal = Modal;
            this.service = {};
            this.firmwarepackages = [];
            this.errors = new Array();
            var self = this;
            self.serviceId = $scope.modal.params.id || '';
            self.activate();
        }
        EditServiceModalController.prototype.activate = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            if (self.serviceId) {
                self.$q.all([
                    self.$http.post(self.Commands.data.firmwarepackages.getAvailableFirmwarePackages, null)
                        .then(function (data) {
                        self.firmwarepackages = [
                            {
                                id: 'usedefaultcatalog',
                                name: self.$translate
                                    .instant("SERVICE_DETAIL_EditService_UseASMappliancedefaultcatalog"),
                                defaultpackage: false
                            }
                        ].concat(data.data.responseObj);
                    }),
                    self.$http.post(self.Commands.data.services.getServiceById, { id: self.serviceId, scaleup: true })
                        .then(function (data) {
                        self.service = data.data.responseObj;
                        self.service._allStandardUsers = self.get_allStandardusers(self.service);
                    })
                ])
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            }
        };
        EditServiceModalController.prototype.setDefaultFirmware = function () {
            var self = this;
            if (self.service && !self.service.firmwarePackageId) {
                if (self.service.manageFirmware) {
                    var pkg = _.find(self.firmwarepackages, { defaultpackage: true });
                    if (pkg) {
                        self.service.firmwarePackageId = pkg.id;
                    }
                }
                else {
                    self.service.firmwarePackageId = "";
                }
            }
        };
        EditServiceModalController.prototype.submit = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            if (self.get_allStandardusers(self.service) === "admins") {
                //if not managing permissions, clear out the settings
                self.service.assignedUsers = [];
            }
            angular.forEach(self.service.components, function (component) {
                angular.forEach(component.settings, function (setting) {
                    if (setting.value != null && typeof setting.value != 'string') {
                        setting.value = JSON.stringify(setting.value);
                    }
                });
            });
            self.$http.post(self.Commands.data.services.updateService, self.service)
                .then(function (data) {
                //wait for loading spinner clears because a new one is going to go up as soon as this closes
                self.$timeout(function () { self.$scope.modal.close(); }, 500);
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        EditServiceModalController.prototype.updateAllStandardUsersProp = function (context) {
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
        EditServiceModalController.prototype.get_allStandardusers = function (context) {
            return context.allStandardUsers
                ? "allStandard"
                : (context.assignedUsers && context.assignedUsers.length ? "specific" : "admins");
        };
        EditServiceModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        EditServiceModalController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog',
            'Commands', 'GlobalServices', '$filter', 'Messagebox', 'Modal'];
        return EditServiceModalController;
    }());
    asm.EditServiceModalController = EditServiceModalController;
    angular
        .module('app')
        .controller('EditServiceModalController', EditServiceModalController);
})(asm || (asm = {}));
//# sourceMappingURL=editServiceModal.js.map
