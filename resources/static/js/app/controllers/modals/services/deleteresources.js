var asm;
(function (asm) {
    var DeleteResourcesController = (function () {
        function DeleteResourcesController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices) {
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
            this.errors = new Array();
            var self = this;
            self.serviceId = $scope.modal.params.serviceId,
                self.refresh();
        }
        DeleteResourcesController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.services.getServiceById, {
                id: self.$scope.modal.params.serviceId,
                scaleup: true
            })
                .then(function (data) {
                self.viewModel = data.data.responseObj;
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        DeleteResourcesController.prototype.doDeleteResources = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            var deleteRequest = {
                serviceId: self.$scope.modal.params.serviceId,
                applicationList: _.map(self.getSelectedItems(self.viewModel.applicationlist), 'id'),
                serverList: _.map(self.getSelectedItems(self.viewModel.serverlist), 'id'),
                vmList: _.map(self.getSelectedItems(self.viewModel.vmlist), 'id'),
                clusterList: _.map(self.getSelectedItems(self.viewModel.clusterlist), 'id'),
                volumeList: _.map(self.getSelectedItems(self.viewModel.storagelist), 'id'),
                scaleioList: _.map(self.getSelectedItems(self.viewModel.scaleiolist), 'id')
            };
            angular.forEach(deleteRequest.serverList, function (id) {
                var c = _.find(self.viewModel.components, { id: id });
                if (c) {
                    angular.forEach(c.relatedcomponents, function (rcItem) {
                        if (rcItem.installOrder > 0) {
                            deleteRequest.applicationList.push(rcItem.id);
                        }
                    });
                }
            });
            angular.forEach(deleteRequest.vmList, function (id) {
                var c = _.find(self.viewModel.components, { id: id });
                if (c) {
                    angular.forEach(c.relatedcomponents, function (rcItem) {
                        if (rcItem.installOrder > 0) {
                            deleteRequest.applicationList.push(rcItem.id);
                        }
                    });
                }
            });
            self.$http.post(self.Commands.data.services.deleteResources, deleteRequest)
                .then(function (data) {
                d.resolve();
                self.$scope.modal.close();
            })
                .catch(function (data) {
                d.resolve();
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        DeleteResourcesController.prototype.getSelectedItems = function (items) {
            return _.filter(items, { 'isSelected': true });
        };
        DeleteResourcesController.prototype.setSelectedServer = function (servers) {
            var self = this;
            if (self.viewModel.selectedServer && self.viewModel.selectedServer != null) {
                angular.forEach(servers, function (server) {
                    if (server.id == self.viewModel.selectedServer) {
                        server.isSelected = true;
                    }
                    else {
                        server.isSelected = false;
                    }
                });
            }
            return servers;
        };
        DeleteResourcesController.prototype.disableDeleteButton = function () {
            var self = this;
            if (self.getSelectedItems(self.viewModel.applicationlist).length +
                self.getSelectedItems(self.setSelectedServer(self.viewModel.serverlist)).length +
                self.getSelectedItems(self.viewModel.vmlist).length +
                self.getSelectedItems(self.viewModel.clusterlist).length +
                self.getSelectedItems(self.viewModel.storagelist).length +
                self.getSelectedItems(self.viewModel.scaleiolist).length <= 0)
                return true;
            else
                return false;
        };
        DeleteResourcesController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        DeleteResourcesController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return DeleteResourcesController;
    }());
    asm.DeleteResourcesController = DeleteResourcesController;
    angular
        .module("app")
        .controller("DeleteResourcesController", DeleteResourcesController);
})(asm || (asm = {}));
//# sourceMappingURL=deleteresources.js.map
