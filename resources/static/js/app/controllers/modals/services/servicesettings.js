var asm;
(function (asm) {
    var ServiceSettingsController = (function () {
        function ServiceSettingsController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.serviceId = '';
            var self = this;
            self.serviceId = $scope.modal.params.serviceId,
                self.refresh();
        }
        ServiceSettingsController.prototype.activate = function () {
            var self = this;
        };
        ServiceSettingsController.prototype.refresh = function () {
            var self = this;
            var deferred = self.$q.defer();
            self.Loading(deferred.promise);
            self.$http.post(self.Commands.data.services.getServiceSettingsById, { requestObj: { "id": self.serviceId } }).then(function (data) {
                self.serviceSettings = data.data.responseObj;
                deferred.resolve();
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
                deferred.resolve();
            });
        };
        ServiceSettingsController.prototype.isSettingVisible = function (setting, component) {
            var self = this;
            if (!setting || !component)
                return true;
            if (setting.dependencyTarget && setting.dependencyValue()) {
                var targetSetting = null;
                angular.forEach(component.categories, function (value, key) {
                    var matchingSetting = _.find(key.settings, function (s) {
                        return (s.id == setting.dependencyTarget());
                    });
                    if (matchingSetting) {
                        targetSetting = matchingSetting;
                        return;
                    }
                });
                var matchingValue = false;
                if (targetSetting && targetSetting.value() != null) {
                    var settingvalues = setting.dependencyValue().split(',');
                    angular.forEach(settingvalues, function (value, key) {
                        if (value.toString() == targetSetting.toString())
                            matchingValue = true;
                    });
                }
                return matchingValue && self.isSettingVisible(targetSetting, component);
            }
            return true;
        };
        ServiceSettingsController.prototype.isCategoryVisible = function (category, component) {
            var self = this;
            if (!category || !component)
                return true;
            var visible = false;
            angular.forEach(category.settings, function (value, key) {
                if (!visible && self.isSettingVisible(value, component))
                    visible = true;
            });
            return visible;
        };
        ServiceSettingsController.prototype.isAppSettingsVisible = function (components) {
            if (components) {
                var apps = components.filter(function (comp) {
                    return comp.installOrder > 0;
                });
                return apps.length > 0;
            }
            else
                return false;
        };
        ServiceSettingsController.prototype.getAppSettings = function (id) {
            var self = this;
            var apps = self.serviceSettings.components.where({ id: id });
            return apps;
        };
        ServiceSettingsController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        ServiceSettingsController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return ServiceSettingsController;
    }());
    asm.ServiceSettingsController = ServiceSettingsController;
    angular
        .module("app")
        .controller("ServiceSettingsController", ServiceSettingsController);
})(asm || (asm = {}));
//# sourceMappingURL=servicesettings.js.map
