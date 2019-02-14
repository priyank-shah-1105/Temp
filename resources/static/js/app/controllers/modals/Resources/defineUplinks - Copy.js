var asm;
(function (asm) {
    var DefineUplinksController = (function () {
        function DefineUplinksController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, constants, $rootScope) {
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
            this.constants = constants;
            this.$rootScope = $rootScope;
            this.rangeDropdown = function () {
                var array = [], i = 1;
                for (i; i <= 128; i++) {
                    array.push(i);
                }
                return array;
            };
            this.errors = new Array();
            var self = this;
            self.refresh();
            self.rangeDropdown = self.initRangeDropdown();
        }
        DefineUplinksController.prototype.refresh = function () {
            var self = this;
            self.localUplinks = self.localUplinks || self.$scope.modal.params.uplinks || [];
            var d = self.$q.defer();
            self.Loading(d.promise);
            self.getNetworks().then(function (response) {
                self.networks = response.data.responseObj;
                angular.forEach(self.localUplinks, function (uplink) {
                    if (uplink.networks.length && !uplink._networkTypes) {
                        //if modal just opened and network is already associated with networks, push them into the uplink's _networkTypes array
                        uplink._networkTypes = angular.copy(self.networks);
                        angular.forEach(uplink._networkTypes, function (network) {
                            network.included = _.indexOf(uplink.networks, network.id) >= 0;
                        });
                    }
                    else {
                        //if modal has not just opened 
                        uplink._networkTypes = uplink._networkTypes
                            ? uplink._networkTypes.concat(_.filter(angular.copy(self.networks), function (network) { return !_.find(uplink._networkTypes, network.id); }))
                            : angular.copy(self.networks);
                    }
                });
            }).catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            }).finally(function () { d.resolve(); });
        };
        DefineUplinksController.prototype.initRangeDropdown = function () {
            var array = [], i = 1;
            for (i; i <= 128; i++) {
                array.push(i);
            }
            return array;
        };
        ;
        DefineUplinksController.prototype.newUplink = function () {
            var self = this;
            self.localUplinks.push({
                uplinkName: 'Uplink ' + (self.localUplinks.length + 1),
                _networkTypes: angular.copy(self.networks),
                networkNames: [],
                networks: [],
                portChannel: 1,
                uplinkId: self.$rootScope.ASM.NewGuid()
            });
        };
        DefineUplinksController.prototype.getSelectedNetworks = function (networkTypes) {
            return _.filter(networkTypes, { included: true });
        };
        DefineUplinksController.prototype.addNetwork = function () {
            var self = this;
            var editNetworkModal = self.Modal({
                title: self.$translate.instant('NETWORKS_Edit_CreateTitle'),
                modalSize: 'modal-lg',
                templateUrl: 'views/networking/networks/editNetwork.html',
                controller: 'EditNetworkModalController as editNetwork',
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
        DefineUplinksController.prototype.checkForm = function () {
            var self = this;
            return !self.localUplinks || !!_.find(self.localUplinks, function (uplink) {
                return (!uplink._networkTypes
                    || uplink._networkTypes.length === 0
                    || !self.getSelectedNetworks(uplink._networkTypes).length)
                    || _.find(self.localUplinks, function (searchUplink) {
                        return searchUplink.portChannel === uplink.portChannel &&
                            searchUplink.uplinkId !== uplink.uplinkId;
                    });
            });
        };
        DefineUplinksController.prototype.getRealUplinksLength = function () {
            var self = this;
            return _.filter(self.localUplinks, function (uplink) { return uplink.uplinkId !== self.$scope.modal.params.vltModel.uplinkId; }).length;
        };
        DefineUplinksController.prototype.getNetworks = function () {
            var self = this;
            return self.$http.post(self.Commands.data.networking.networks.getUplinkNetworksList, []);
        };
        DefineUplinksController.prototype.close = function () {
            var self = this;
            angular.forEach(self.localUplinks, function (uplink) {
                uplink.networks = _.map(self.getSelectedNetworks(uplink._networkTypes), "id");
                delete uplink._networkTypes;
            });
            self.$scope.modal.close(self.localUplinks);
        };
        DefineUplinksController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        DefineUplinksController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', 'constants', '$rootScope'];
        return DefineUplinksController;
    }());
    asm.DefineUplinksController = DefineUplinksController;
    angular
        .module("app")
        .controller("DefineUplinksController", DefineUplinksController);
})(asm || (asm = {}));
//# sourceMappingURL=defineUplinks - Copy.js.map
