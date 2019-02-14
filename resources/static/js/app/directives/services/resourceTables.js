var asm;
(function (asm) {
    "use strict";
    var ResourceTablesController = (function () {
        function ResourceTablesController(modal, dialog, $http, $timeout, $q, $translate, $window, GlobalServices) {
            this.modal = modal;
            this.dialog = dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.$window = $window;
            this.GlobalServices = GlobalServices;
            this.warning = "warning";
        }
        Object.defineProperty(ResourceTablesController.prototype, "resources", {
            get: function () {
                var self = this;
                return self._resources;
            },
            set: function (newValue) {
                var self = this;
                if (!angular.equals(self.resources, newValue)) {
                    self._resources = newValue;
                    self.refresh();
                }
            },
            enumerable: true,
            configurable: true
        });
        ResourceTablesController.prototype.refresh = function () {
            var self = this;
            if (self.mode === "inventory" || self.mode === "detail") {
                self.resources = angular.copy(self.resources);
            }
            self.flattenVolumes();
            angular.extend(self.resources, {
                available: {
                    clusters: _.filter(self.resources.clusterlist, function (cluster) { return self.availableInventory(cluster.state); }).length,
                    servers: _.filter(self.resources.serverlist, function (server) { return self.availableInventory(server.state); }).length,
                    storages: _.filter(self.resources.storagelist, function (storage) { return self.availableInventory(storage.state); }).length,
                    scaleios: _.filter(self.resources.scaleiolist, function (scaleio) { return self.availableInventory(scaleio.state); }).length
                }
            });
            //get values for name and status
            angular.forEach([
                self.resources.applicationlist,
                self.resources.vmlist,
                self.resources.clusterlist,
                self.resources.serverlist,
                self.resources.storagelist,
                self.resources.scaleiolist
            ], function (array) {
                _.forEach(array, function (app) {
                    angular.extend(app, {
                        component: _.find(self.resources.components, { id: app.id }),
                        _status: self.getComponentStatus(app.id),
                        available: self.availableInventory(app.state)
                    });
                });
            });
            //testing code
            //console.log('self.resources:  ');
            //console.log(JSON.stringify(self.resources));
        };
        ResourceTablesController.prototype.flattenVolumes = function () {
            var self = this;
            angular.forEach(self.resources.scaleiolist, function (scaleio) {
                scaleio.volumes = [];
                angular.forEach(scaleio.storagePools, function (storagePool) {
                    angular.forEach(storagePool.scaleIOStorageVolumes, function (volume) { return scaleio.volumes.push(volume); });
                });
            });
        };
        ResourceTablesController.prototype.isNew = function (component) {
            var self = this;
            return component.brownfieldStatus === "new";
        };
        ResourceTablesController.prototype.availableInventory = function (state) {
            return !(state === 'unmanaged' || state === 'updating');
        };
        ResourceTablesController.prototype.getComponentStatus = function (id) {
            var self = this;
            var match = _.find(self.resources.componentstatus, function (cs) { return (cs.componentid === id); });
            switch (match ? match.resourcestate : "") {
                case 'pending':
                    return { icon: "pending", text: 'SETTINGS_Repositories_Pending' };
                case 'inprogress':
                    return { icon: "inprogress", text: 'GENERIC_InProgress' };
                case 'complete':
                    return { icon: "success", text: 'GENERIC_Deployed' };
                case 'error':
                    return { icon: "critical", text: 'GENERIC_Critical' };
                case 'cancelled':
                    return { icon: "cancelled", text: 'GENERIC_Cancelled' };
                case 'servicemode':
                    return { icon: "servicemode", text: 'GENERIC_ServiceMode' };
                case 'warning':
                    return { icon: "warning", text: 'GENERIC_Warning' };
            }
        };
        ;
        ResourceTablesController.prototype.goToIp = function (ip) {
            var self = this;
            self.$window.open(ip, "_blank");
        };
        ResourceTablesController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', "$window", "GlobalServices"];
        return ResourceTablesController;
    }());
    angular.module('app')
        .component('resourceTables', {
        templateUrl: 'views/services/resourcetables.html',
        controller: ResourceTablesController,
        controllerAs: 'resourceTablesController',
        bindings: {
            resources: '<',
            mode: '@'
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=resourceTables.js.map
