var asm;
(function (asm) {
    var AddNetworkModalController = (function () {
        function AddNetworkModalController($http, $timeout, $scope, $q, $translate, loading, Dialog, commands, GlobalServices, $filter, MessageBox, modal, constants) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.loading = loading;
            this.Dialog = Dialog;
            this.commands = commands;
            this.GlobalServices = GlobalServices;
            this.$filter = $filter;
            this.MessageBox = MessageBox;
            this.modal = modal;
            this.constants = constants;
            this.errors = new Array();
            this.toBeAdded = new Array();
            this.serviceHasVDS = true;
            var self = this;
            self.serviceHasVDS = self.$scope.modal.params.service.hasVDS;
            self.refresh();
        }
        //Please note that networks attached to resources in mock back-end are completely random
        AddNetworkModalController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            self.$q.all([
                self.getServiceNetworksList(self.$scope.modal.params.serviceId).then(function (response) {
                    self.serviceNetworks = response.data.responseObj;
                }),
                self.getResourcesWithNetworksById(self.$scope.modal.params.serviceId)
                    .then(function (response) {
                    self.resources = response.data.responseObj;
                    //test to fill all resources with both test networks
                    //angular.forEach(self.resources, (resource) => { self.resources.indexOf(resource) % 2 === 0 ? resource.networks = ["f3b21fbc-afb9-433a-ad59-29e94243e8d3", "608799be-bbbd-47b0-b602-7310632c2784"]: null });
                })
            ]).then(function () {
                self.resources = self.getCorespondingNetworkNames(self.resources, self.serviceNetworks);
                self.uniqueDeployedNetworks = _.uniqBy(_.flatMap(self.resources, function (resource) { return resource.networks; }), "id");
                self.disableFullOrUsedNetworks();
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            }).finally(function () { return d.resolve(); });
        };
        AddNetworkModalController.prototype.newNetwork = function () {
            var self = this;
            self.toBeAdded.push({
                id: self.GlobalServices.NewGuid(),
                networkid: self.selectedNetwork.id,
                networkname: self.selectedNetwork.name,
                portgroupid: null,
                portgroupname: null,
                //all resources with those that are already using this network removed
                resourceNames: self.getResources(self.selectedNetwork, angular.copy(self.resources)),
                //model of which resources were choosen
                resources: [],
                _portGroups: angular.copy(self.portGroups)
            });
            self.disableFullOrUsedNetworks();
        };
        AddNetworkModalController.prototype.defineNewNetwork = function () {
            var self = this;
            var editNetworkModal = self.modal({
                title: self.$translate.instant('NETWORKS_Edit_CreateTitle'),
                modalSize: 'modal-lg',
                templateUrl: "views/networking/networks/editnetwork.html",
                controller: "EditNetworkModalController as editNetwork",
                params: {
                    editMode: "CREATE",
                    id: ""
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            editNetworkModal.modal.show();
        };
        AddNetworkModalController.prototype.removeNetwork = function (resource) {
            var self = this;
            self.toBeAdded.splice(self.toBeAdded.indexOf(resource), 1);
            self.disableFullOrUsedNetworks();
        };
        AddNetworkModalController.prototype.selectedNetworkChanged = function () {
            var self = this;
            if (self.selectedNetwork && self.selectedNetwork.id) {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.loading(d.promise);
                self.networkChanging = true;
                self.getServiceNetworkPortGroupList(self.$scope.modal.params.serviceId, self.selectedNetwork.id)
                    .then(function (response) {
                    //place title option as first in array of options
                    self.portGroups = [
                        {
                            portGroup: self.$translate.instant("NETWORKS_ADD_NewPortGroup"),
                            newPortGroup: true
                        }
                    ].concat(response.data.responseObj);
                    self.networkChanging = false;
                }).catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                }).finally(function () { return d.resolve(); });
            }
        };
        AddNetworkModalController.prototype.getResources = function (selectedNetwork, resources) {
            //resource will be removed as an option for a network if it is already in use by the network
            //returns an array of resources marked on whether they're disabled
            var array = [];
            angular.forEach(resources, function (resource) {
                //only include if it's not already in use
                if (!_.find(resource.networks, { name: selectedNetwork.name })) {
                    //set checkbox to false
                    resource.included = false;
                    array.push(resource);
                }
            });
            return array;
        };
        AddNetworkModalController.prototype.disableFullOrUsedNetworks = function () {
            var self = this;
            //A network is disabled from being selected if one of each kind of resource is using it OR it is in the toBeAdded array
            _.forEach(self.serviceNetworks, function (network) {
                network.disabled = !_.find(self.resources, function (resource) {
                    return !_.find(resource.networks, { id: network.id });
                }) || !!_.find(self.toBeAdded, { networkid: network.id });
            });
        };
        AddNetworkModalController.prototype.getCorespondingNetworkNames = function (resources, networks) {
            //Each resource comes with an array of ids for networks, this function replaces the ids with the id's full coresponding network object
            //Converts resource.networks from ["id", "id", "id"] To [networkObj, networkObj, networkObj]
            angular.forEach(resources, function (resource) {
                resource.networks = _.map(resource.networks, function (network) {
                    return _.find(networks, { id: network });
                });
            });
            return resources;
        };
        AddNetworkModalController.prototype.enableSave = function () {
            var self = this;
            var enableSaveButton = false;
            if (self.toBeAdded.length > 0) {
                enableSaveButton = _.find(self.toBeAdded, function (network) {
                    if ((network.portgroup && !network.portgroup.newPortGroup) || (network.portgroup && network.portgroup.newPortGroup && network.portgroupname)) {
                        return true;
                    }
                    else {
                        return false;
                    }
                });
            }
            else {
                enableSaveButton = false;
            }
            return enableSaveButton;
        };
        AddNetworkModalController.prototype.validateResourceSelections = function () {
            var self = this;
            //returns whether any row has no selected resources
            return _.find(self.toBeAdded, function (network) {
                //if new portgroup, don't check it
                if (network.portgroup && network.portgroup.newPortGroup) {
                    return false;
                }
                return network._missingResource = !_.find(network.resourceNames, { included: true });
            });
        };
        AddNetworkModalController.prototype.save = function () {
            var self = this;
            if (self.validateResourceSelections())
                return;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            var request = {
                networks: [],
                serviceid: self.serviceHasVDS ? self.$scope.modal.params.serviceId : ""
            };
            angular.forEach(self.toBeAdded, function (network, index) {
                if (network.portgroup && network.portgroup.newPortGroup && self.serviceHasVDS) {
                    network.resourceNames = angular.copy([self.resources[0]]);
                    network.resourceNames[0].included = true;
                }
                var requestNetwork = {
                    id: network.id,
                    networkid: network.networkid,
                    networkname: network.networkname,
                    portgroupid: null,
                    portgroupname: null,
                    resources: network.resources
                };
                if (self.serviceHasVDS) {
                    if (network.portgroup && network.portgroup.newPortGroup) {
                        requestNetwork.portgroupid = "-1";
                        requestNetwork.portgroupname = network.portgroupname;
                    }
                    else {
                        requestNetwork.portgroupname = network.portgroup.portGroup;
                        requestNetwork.portgroupid = network.portgroup.id;
                    }
                }
                request.networks.push(requestNetwork);
                //remove all resource names that have included of false
                var includedResources = _.filter(network.resourceNames, { included: true });
                //push all remaining networks' ids into resourceNames array
                angular.forEach(includedResources, function (resource) {
                    request.networks[index].resources.push(resource.id);
                });
            });
            self.updateServiceNetworkResources(self.$scope.modal.params.serviceId, request.networks)
                .then(function (data) {
                self.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        AddNetworkModalController.prototype.getServiceNetworksList = function (serviceId) {
            var self = this;
            return self.$http.post(self.commands.data.networking.networks.getServiceNetworksList, { serviceId: serviceId });
        };
        AddNetworkModalController.prototype.getServiceNetworkPortGroupList = function (serviceId, networkId) {
            var self = this;
            return self.$http.post(self.commands.data.networking.networks.getServiceNetworkPortGroupList, { serviceId: serviceId, networkId: networkId });
        };
        AddNetworkModalController.prototype.getResourcesWithNetworksById = function (id) {
            var self = this;
            return self.$http.post(self.commands.data.services.getResourcesWithNetworksById, { id: id });
        };
        AddNetworkModalController.prototype.updateServiceNetworkResources = function (serviceId, networks) {
            var self = this;
            return self.$http.post(self.commands.data.services.updateServiceNetworkResources, { serviceid: serviceId, networks: networks });
        };
        AddNetworkModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        AddNetworkModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        AddNetworkModalController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog',
            'Commands', 'GlobalServices', '$filter', 'Messagebox', 'Modal', 'constants'];
        return AddNetworkModalController;
    }());
    asm.AddNetworkModalController = AddNetworkModalController;
    angular
        .module('app')
        .controller('AddNetworkModalController', AddNetworkModalController);
})(asm || (asm = {}));
//# sourceMappingURL=addNetworkModal.js.map
