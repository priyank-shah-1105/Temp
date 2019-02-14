var asm;
(function (asm) {
    var DeleteServiceModalController = (function () {
        function DeleteServiceModalController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, $filter, MessageBox, $location, constants) {
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
            this.$location = $location;
            this.constants = constants;
            this.serverlist = [];
            this.vmlist = [];
            this.clusterlist = [];
            this.storagelist = [];
            this.scaleiolist = [];
            this.errors = [];
            this.removalTypeOptions = [];
            this.removalType = null;
            this.serversInInventory = "keep";
            this.resourceState = null;
            var self = this;
            self.service = $scope.modal.params.service;
            self.deleteRequest = {
                serviceId: '',
                deleteServers: true,
                serverList: [],
                deleteVMs: true,
                vmList: [],
                deleteClusters: false,
                clusterList: [],
                deleteStorageVolumes: false,
                volumeList: [],
                deleteScaleios: false,
                scaleioList: []
            };
            self.submitForm = false;
            self.removalTypeOptions = [
                { id: "delete", name: self.$translate.instant("SERVICE_DETAIL_DELETE_SERVICE_DeleteService") },
                { id: "remove", name: self.$translate.instant("SERVICE_DETAIL_RemoveService") }
            ];
            self.activate();
        }
        DeleteServiceModalController.prototype.setRemovalType = function () {
            var self = this;
            if (self.removalType == 'delete') {
                self.$scope.modal.title = self.$translate.instant('SERVICE_DETAIL_DELETE_SERVICE_DeleteService');
            }
            else {
                self.$scope.modal.title = self.$translate.instant('SERVICE_DETAIL_RemoveService');
            }
        };
        DeleteServiceModalController.prototype.activate = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.services.getServiceById, {
                id: self.$scope.modal.params.service.id,
                scaleup: true
            })
                .then(function (data) {
                self.service = data.data.responseObj;
                if (self.service != undefined) {
                    self.serviceId = self.service.id;
                    self.deleteRequest.serviceId = self.service.id;
                    self.deleteRequest.deleteServers = true;
                    $.each(self.service.components, function (idx, cmp) {
                        if (cmp.type == "server") {
                            self.serverlist.push(cmp);
                        }
                        if (cmp.type == "vm") {
                            self.vmlist.push(cmp);
                        }
                        if (cmp.type == "cluster") {
                            self.clusterlist.push(cmp);
                        }
                        if (cmp.type == "storage") {
                            self.storagelist.push(cmp);
                        }
                        if (cmp.type == "scaleio") {
                            self.scaleiolist.push(cmp);
                        }
                    });
                }
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        DeleteServiceModalController.prototype.pending = function () {
            var self = this;
            return angular.isDefined(self.service) && self.service.health === 'pending';
        };
        DeleteServiceModalController.prototype.delete = function (formHasErrors) {
            var self = this;
            if (formHasErrors) {
                self.submitForm = true;
                return;
            }
            var confirm = this.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('SERVICE_DELETE_SERVICE')));
            confirm.then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.deleteRequest.serverList = [];
                self.deleteRequest.vmList = [];
                self.deleteRequest.clusterList = [];
                self.deleteRequest.volumeList = [];
                self.deleteRequest.scaleioList = [];
                $.each(self.serverlist, function (idx, cmp) { if (cmp.isSelected) {
                    self.deleteRequest.serverList.push(cmp.id);
                } });
                $.each(self.vmlist, function (idx, cmp) { self.deleteRequest.vmList.push(cmp.id); });
                $.each(self.clusterlist, function (idx, cmp) { if (cmp.isSelected) {
                    self.deleteRequest.clusterList.push(cmp.id);
                } });
                $.each(self.storagelist, function (idx, cmp) { if (cmp.isSelected) {
                    self.deleteRequest.volumeList.push(cmp.id);
                } });
                $.each(self.scaleiolist, function (idx, cmp) { if (cmp.isSelected) {
                    self.deleteRequest.scaleioList.push(cmp.id);
                } });
                var request = self.deleteRequest;
                if (!self.deleteRequest.deleteServers) {
                    self.deleteRequest.serverList = [];
                }
                if (!self.deleteRequest.deleteVMs) {
                    self.deleteRequest.vmList = [];
                }
                if (!self.deleteRequest.deleteClusters) {
                    self.deleteRequest.clusterList = [];
                }
                if (!self.deleteRequest.deleteStorageVolumes) {
                    self.deleteRequest.volumeList = [];
                }
                if (!self.deleteRequest.deleteScaleios) {
                    self.deleteRequest.scaleioList = [];
                }
                self.$http.post(self.Commands.data.services.deleteService, request)
                    .then(function (data) {
                    d.resolve();
                    self.$scope.modal.close();
                    self.$timeout(function () {
                        self.$location.path('services/');
                    }, 1000);
                }).catch(function (data) {
                    d.resolve();
                    self.GlobalServices.DisplayError(data.data, self.errors);
                });
            });
        };
        DeleteServiceModalController.prototype.remove = function (formHasErrors) {
            var self = this;
            if (formHasErrors) {
                self.submitForm = true;
                return;
            }
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.services.removeService, { serviceId: self.serviceId, serversInInventory: self.serversInInventory, resourceState: self.resourceState })
                .then(function (data) {
                d.resolve();
                self.$scope.modal.close();
                self.$timeout(function () {
                    self.$location.path('services/');
                }, 1000);
            }).catch(function (data) {
                d.resolve();
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        DeleteServiceModalController.prototype.close = function () {
            var self = this;
            //self.$scope.modal.dismiss();
            self.$scope.modal.cancel();
        };
        DeleteServiceModalController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog',
            'Commands', 'GlobalServices', '$filter', 'Messagebox', '$location', 'constants'];
        return DeleteServiceModalController;
    }());
    asm.DeleteServiceModalController = DeleteServiceModalController;
    angular
        .module('app')
        .controller('DeleteServiceModalController', DeleteServiceModalController);
})(asm || (asm = {}));
//# sourceMappingURL=deleteserviceModal.js.map
