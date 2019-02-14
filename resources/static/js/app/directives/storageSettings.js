var asm;
(function (asm) {
    "use strict";
    var StorageSettingsController = (function () {
        function StorageSettingsController($http, $timeout, $q, $translate, modal, loading, dialog, commands, globalServices, $rootScope, $interval) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.modal = modal;
            this.loading = loading;
            this.dialog = dialog;
            this.commands = commands;
            this.globalServices = globalServices;
            this.$rootScope = $rootScope;
            this.$interval = $interval;
            var self = this;
            self.activate();
        }
        StorageSettingsController.prototype.activate = function () {
            var self = this;
            self.uniqueId = self.globalServices.NewGuid();
        };
        StorageSettingsController.prototype.filteredOptions = function (storage, options) {
            var self = this;
            var settingsArray = storage;
            var returnVal = [];
            $.each(options, function (optionIndex, option) {
                if (option.dependencyTarget && option.dependencyValue) {
                    var targetSetting = null;
                    //sort through object key value pairs to find matching key, then check value
                    var matchingSetting = _.find(storage, function (value, key) {
                        return (key == option.dependencyTarget);
                    });
                    if (matchingSetting) {
                        targetSetting = matchingSetting;
                    }
                    var matchingValue = false;
                    if (targetSetting) {
                        var settingvalues = option.dependencyValue.split(',');
                        $.each(settingvalues, function (idx, val) {
                            if (val.toString() == targetSetting.toString())
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
        StorageSettingsController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope', '$interval'];
        return StorageSettingsController;
    }());
    angular.module("app")
        .component("storageSettings", {
        templateUrl: "views/storagesettings.html",
        controller: StorageSettingsController,
        controllerAs: "storageSettingsController",
        bindings: {
            storageSettings: "=",
            form: "=?",
            errors: "=",
            readOnly: "<?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=storageSettings.js.map
