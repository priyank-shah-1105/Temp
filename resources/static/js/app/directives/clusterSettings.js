var asm;
(function (asm) {
    "use strict";
    var ClusterSettingsController = (function () {
        function ClusterSettingsController($http, $timeout, $q, $translate, modal, loading, dialog, commands, globalServices, $rootScope, $interval) {
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
            self.readOnly || self.activate();
        }
        ClusterSettingsController.prototype.activate = function () {
            var self = this;
            self.clusterSettings._dataCenters = self.addNewOption(self.clusterSettings.dataCenters);
            self.initDataCenter();
            self.clusterSettings._clusters = self.addNewOption(self.clusterSettings.clusters);
            self.initCluster();
            self.clusterSettings.switchType = self.clusterSettings.switchType || "standard";
        };
        ClusterSettingsController.prototype.dataCenterChanged = function () {
            var self = this;
            self.clusterSettings.dataCenter = self.dataCenterSelection === "new" ? undefined : self.dataCenterSelection;
        };
        ClusterSettingsController.prototype.initDataCenter = function () {
            var self = this;
            if (self.clusterSettings.dataCenter) {
                self.dataCenterSelection = _.find(self.clusterSettings._dataCenters, { id: self.clusterSettings.dataCenter })
                    ? self.clusterSettings.dataCenter
                    : "new";
            }
        };
        ClusterSettingsController.prototype.clusterChanged = function () {
            var self = this;
            self.clusterSettings.cluster = self.clusterSelection === "new" ? undefined : self.clusterSelection;
        };
        ClusterSettingsController.prototype.initCluster = function () {
            var self = this;
            if (self.clusterSettings.cluster) {
                self.clusterSelection = _.find(self.clusterSettings._clusters, { id: self.clusterSettings.cluster })
                    ? self.clusterSettings.cluster
                    : "new";
            }
        };
        ClusterSettingsController.prototype.addNewOption = function (array) {
            var self = this;
            return _.concat(array, [
                { name: "____________________", id: 123, disabled: true },
                { name: self.$translate.instant("TEMPLATES_CreateNewCategory"), id: "new" }
            ]);
        };
        ClusterSettingsController.prototype.isNew = function (array, value) {
            return !_.find(array, { id: value });
        };
        ClusterSettingsController.prototype.joinCapabilities = function () {
            var capabilities = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                capabilities[_i - 0] = arguments[_i];
            }
            return _.join(_.filter(capabilities, function (c) { return c; }), capabilities.length ? "; " : undefined);
        };
        ClusterSettingsController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope', '$interval'];
        return ClusterSettingsController;
    }());
    angular.module("app")
        .component("clusterSettings", {
        templateUrl: "views/clustersettings.html",
        controller: ClusterSettingsController,
        controllerAs: "clusterSettingsController",
        bindings: {
            clusterSettings: "=",
            form: "=?",
            errors: "=",
            readOnly: "<?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=clusterSettings.js.map
