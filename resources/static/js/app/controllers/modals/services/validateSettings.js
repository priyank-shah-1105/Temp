var asm;
(function (asm) {
    var ValidateSettingsController = (function () {
        function ValidateSettingsController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.commands = commands;
            this.GlobalServices = GlobalServices;
            this.errors = new Array();
            var self = this;
            self.activate();
        }
        ValidateSettingsController.prototype.activate = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.stringifyCategories(self.$scope.modal.params.component.categories);
            self.testSettingsValidity(self.$scope.modal.params.component)
                .then(function (response) {
                self.devices = response.data.responseObj.devices;
                self.devicesSafe = angular.copy(self.devices);
                self.totalServers = response.data.responseObj.totalservers;
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ValidateSettingsController.prototype.stringifyCategories = function (categories) {
            return angular.forEach(categories, function (category) {
                angular.forEach(category.settings, function (setting) {
                    if (setting.value != null && typeof setting.value != 'string') {
                        setting.value = JSON.stringify(setting.value);
                    }
                });
            });
        };
        ValidateSettingsController.prototype.testSettingsValidity = function (template) {
            var self = this;
            return self.$http.post(self.commands.data.templates.validateSettings, template);
        };
        ValidateSettingsController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        ValidateSettingsController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return ValidateSettingsController;
    }());
    asm.ValidateSettingsController = ValidateSettingsController;
    angular
        .module("app")
        .controller("ValidateSettingsController", ValidateSettingsController);
})(asm || (asm = {}));
//# sourceMappingURL=validateSettings.js.map
