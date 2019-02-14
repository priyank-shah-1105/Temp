var asm;
(function (asm) {
    var CancelDeploymentModalController = (function () {
        function CancelDeploymentModalController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, $location) {
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
            this.$location = $location;
            this.deleteRequest = {};
            this.deleteServiceBool = false;
            this.errors = new Array();
            var self = this;
            self.refresh();
        }
        CancelDeploymentModalController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.services.getServiceById, {
                id: self.$scope.modal.params.serviceId,
                scaleup: true
            })
                .then(function (data) {
                self.service = data.data.responseObj;
                self.markComponentsForDeletion();
                self.deleteServiceBool = self.pending();
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        CancelDeploymentModalController.prototype.markComponentsForDeletion = function () {
            var self = this;
            angular.forEach((self.service.components), function (component) { return component.isSelected = true; });
            self.deleteRequest.deleteClusters =
                self.deleteRequest.deleteServers =
                    self.deleteRequest.deleteStorageVolumes =
                        self.deleteRequest.deleteVMs = true;
        };
        CancelDeploymentModalController.prototype.pending = function () {
            var self = this;
            return angular.isDefined(self.service) && self.service.health === 'pending';
        };
        CancelDeploymentModalController.prototype.cancelDeployment = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            if (self.pending()) {
                self.deleteService()
                    .then(function () {
                    self.close();
                    self.goToServices();
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            }
            else {
                if (self.deleteServiceBool) {
                    self.deleteService()
                        .then(function () {
                        self.close();
                        self.goToServices();
                    })
                        .catch(function (data) {
                        self.GlobalServices.DisplayError(data.data, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
                else {
                    self.cancelService(self.service.id)
                        .then(function () { return self.close(); })
                        .catch(function (data) {
                        self.GlobalServices.DisplayError(data.data, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
            }
        };
        CancelDeploymentModalController.prototype.deleteService = function () {
            var self = this;
            //filter for each device type and where selected
            var deviceLists = {
                serverList: _.filter(self.service.components, { isSelected: true, type: "server" }),
                vmList: _.filter(self.service.components, { isSelected: true, type: "vm" }),
                clusterList: _.filter(self.service.components, { isSelected: true, type: "cluster" }),
                volumeList: _.filter(self.service.components, { isSelected: true, type: "storage" }),
                scaleioList: _.filter(self.service.components, { isSelected: true, type: "scaleio" })
            };
            //if not deleting, empty deletion array
            if (!self.deleteRequest.deleteServers)
                deviceLists.serverList = [];
            if (!self.deleteRequest.deleteVMs)
                deviceLists.vmList = [];
            if (!self.deleteRequest.deleteClusters)
                deviceLists.clusterList = [];
            if (!self.deleteRequest.deleteStorageVolumes)
                deviceLists.volumeList = [];
            if (!self.deleteRequest.deleteScaleios)
                deviceLists.scaleioList = [];
            // take only ids from each list
            deviceLists.serverList = _.map(deviceLists.serverList, "id");
            deviceLists.vmList = _.map(deviceLists.vmList, "id");
            deviceLists.clusterList = _.map(deviceLists.clusterList, "id");
            deviceLists.volumeList = _.map(deviceLists.volumeList, "id");
            deviceLists.scaleioList = _.map(deviceLists.scaleioList, "id");
            self.deleteRequest.serviceId = self.service.id;
            //add deviceLists properties to delete request
            angular.extend(self.deleteRequest, deviceLists);
            return self.$http.post(self.Commands.data.services.deleteService, self.deleteRequest);
        };
        CancelDeploymentModalController.prototype.goToServices = function () {
            var self = this;
            self.$timeout(function () {
                self.$location.path('services/');
            }, 1000);
        };
        CancelDeploymentModalController.prototype.cancelService = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.services.cancelService, { id: id });
        };
        CancelDeploymentModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        CancelDeploymentModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        CancelDeploymentModalController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', "$location"];
        return CancelDeploymentModalController;
    }());
    asm.CancelDeploymentModalController = CancelDeploymentModalController;
    angular
        .module("app")
        .controller("CancelDeploymentModalController", CancelDeploymentModalController);
})(asm || (asm = {}));
//# sourceMappingURL=cancelDeploymentModal.js.map
