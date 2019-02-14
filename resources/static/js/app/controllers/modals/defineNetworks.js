var asm;
(function (asm) {
    var DefineNetworksController = (function () {
        function DefineNetworksController($http, $timeout, $q, Modal, Dialog, $filter, Loading, $translate, Commands, GlobalServices, constants, $scope, $window) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$filter = $filter;
            this.Loading = Loading;
            this.$translate = $translate;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            this.$scope = $scope;
            this.$window = $window;
            this.errors = new Array();
            var self = this;
            self.networkTypes = [];
            self.currentView = "all";
            self.StaticIPAddressDetailsViews = constants.staticIPAddressDetailsViews;
            self.deferred = self.$q.defer();
            self.refresh();
        }
        DefineNetworksController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.loadNetworkTypes()
                    .then(function (data) {
                    self.networkTypes = data.data.responseObj;
                }),
                self.getNetworksList().then(function (data) {
                    //apply default sort so selecting first will work
                    var sortedData = _.sortBy(data.data.responseObj, function (n) {
                        return n.name.toLowerCase();
                    });
                    self.networkData = sortedData;
                    self.displayedNetworkData = [].concat(self.networkData);
                    var selectedNet = null;
                    if (self.selectedNetworkId != null) {
                        selectedNet = _.find(self.networkData, { id: self.selectedNetworkId });
                    }
                    if (selectedNet == null) {
                        selectedNet = self.displayedNetworkData[0];
                    }
                    self.onNetworkSelected(selectedNet);
                })
            ])
                .then(function () {
                self.networkData = self.mapNetworkNames(self.networkData, self.networkTypes);
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); })
                .finally(function () { return d.resolve(); });
        };
        ;
        DefineNetworksController.prototype.mapNetworkNames = function (networkData, networkTypes) {
            return angular.forEach(networkData, function (network) {
                var match = _.find(networkTypes, { id: network.typeid });
                if (match) {
                    network._networkName = match.networkType;
                }
            });
        };
        DefineNetworksController.prototype.onNetworkSelected = function (network, ipRange) {
            var self = this;
            if (network) {
                var d = self.$q.defer();
                //self.Loading(d.promise);
                //this call must be async: false
                self.getNetworkById(network.id)
                    .then(function (data) {
                    self.selectedNetwork = data.data.responseObj;
                    self.selectedNetworkId = self.selectedNetwork.id;
                    if (self.selectedNetwork.staticordhcp == 'Static') {
                        //update static ip address details counts on network selection, based on selectedNetwork
                        self.totalIPAddresses_display = self.selectedNetwork.staticipaddressdetails.length;
                        self.inUseIPAddresses_display = _.filter(self.selectedNetwork.staticipaddressdetails, { state: 'inuse' }).length;
                        self.staticipaddressdetails_copy = self.selectedNetwork.staticipaddressdetails.slice(0, self.selectedNetwork.staticipaddressdetails.length);
                        if (self.selectedElement == 'filterDetails') {
                            self.totalIPAddresses_display = ipRange.totalIPAddresses;
                            self.inUseIPAddresses_display = ipRange.inUseIPAddresses;
                            //if one of the in use links was clicked, reset filter to inuse
                            self.currentView = 'inuse';
                            self.updateClientFilter();
                        }
                        else {
                            //reset the filter to all with each network row change or same-row click that is not on a link
                            //if one of the in use links was not clicked, reset filter to all
                            self.currentView = 'all';
                            self.updateClientFilter();
                        }
                    }
                }).catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); })
                    .finally(function () { return d.resolve(); });
            }
        };
        DefineNetworksController.prototype.updateClientFilter = function () {
            var self = this;
            var ipaddress1;
            var ipaddress2;
            if (self.staticipaddressdetails_copy.length == 0)
                return;
            self.selectedNetwork.staticipaddressdetails = [];
            self.selectedNetwork.staticipaddressdetails = self.staticipaddressdetails_copy.slice(0, self.staticipaddressdetails_copy.length);
            if (self.startingIpAddress != null && self.endingIpAddress != null) {
                ipaddress1 = self.$filter('ip2long')(self.startingIpAddress);
                ipaddress2 = self.$filter('ip2long')(self.endingIpAddress);
            }
            var filteredData = self.selectedNetwork.staticipaddressdetails.filter(function (item) {
                return (((self.currentView == 'all' && (item.state == 'available' || item.state == 'inuse')) || (item.state == self.currentView)) &&
                    ((ipaddress1 == '' && ipaddress2 == '') || (self.$filter('ip2long')(item.ipAddress) >= ipaddress1 && self.$filter('ip2long')(item.ipAddress) <= ipaddress2)));
            });
            self.selectedNetwork.staticipaddressdetails = [];
            self.selectedNetwork.staticipaddressdetails = filteredData.slice(0, filteredData.length);
            //smart table works by starting with data from st-safe-src (treating that one as safe), and then uses data from st-table for the actual display and paging of the data,
            //so another array is needed for this displayed data source
            self.selectedNetwork.staticipaddressdetails_displayed = [].concat(self.selectedNetwork.staticipaddressdetails);
        };
        DefineNetworksController.prototype.click_tableRow = function (clickedItem) {
            //click on TR
            //self.selectedNetwork = network;
            var self = this;
            var network = clickedItem;
            self.selectedElement = 'row';
            self.startingIpAddress = '';
            self.endingIpAddress = '';
            //var currentTarget = evt.target;
            //while (currentTarget.nodeName != 'TR') {
            //    //we are on a child node
            //    currentTarget = currentTarget.parentNode;
            //}
            ////we are now on the TR node so dataFor will now apply to the correct element
            //var network = ko.dataFor(currentTarget);
            self.onNetworkSelected(network);
        };
        DefineNetworksController.prototype.click_IPAddressRange = function (clickedItem1, clickedItem2) {
            //click on class filterDetails
            var self = this;
            var network = clickedItem1;
            var ipRange = clickedItem2;
            self.selectedElement = 'filterDetails';
            self.startingIpAddress = ipRange.startingIpAddress;
            self.endingIpAddress = ipRange.endingIpAddress;
            //self.Loading(self.deferred.promise);
            self.onNetworkSelected(network, ipRange);
            //self.totalIPAddresses_display = ipRange.totalIPAddresses;
            //self.inUseIPAddresses_display = ipRange.inUseIPAddresses;
        };
        DefineNetworksController.prototype.deleteNetwork = function () {
            var self = this;
            //Confirmation Dialog box that fires delete on confirmation
            self.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('NETWORKS_ConfirmDelete'))).then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.deleteNetworkById(self.selectedNetwork.id)
                    .then(function (data) {
                    self.refresh();
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        DefineNetworksController.prototype.doDownload = function (type) {
            var self = this;
            var deferred = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(deferred.promise);
            self.processDownloadRequests('initial', '', type, deferred);
        };
        DefineNetworksController.prototype.processDownloadRequests = function (call, id, type, deferred) {
            var self = this;
            var urlToCall;
            var data;
            urlToCall = self.Commands.data.downloads.status;
            data = { 'id': id };
            if (call == 'initial') {
                urlToCall = self.Commands.data.downloads.create;
                if (type == 'networkdetails') {
                    data = { 'type': type, 'id': self.selectedNetworkId };
                }
                else {
                    data = { 'type': type };
                }
            }
            self.$http.post(urlToCall, { requestObj: data })
                .then(function (data) {
                switch (data.data.responseObj.status) {
                    case 'NOT_READY':
                        self.$timeout(function () {
                            self.processDownloadRequests('status', data.data.responseObj.id, type, deferred);
                        }, 5000);
                        break;
                    case 'READY':
                        self.$window.location.assign("downloads/getfile/" + data.data.responseObj.id);
                        deferred.resolve();
                        break;
                    case 'ERROR':
                        deferred.resolve();
                        self.GlobalServices.DisplayError(data.data);
                        break;
                }
            }).catch(function (data) {
                deferred.resolve();
                //error is in data
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        DefineNetworksController.prototype.doUpdates = function (updateType) {
            var self = this;
            var title;
            var helptoken;
            //add/create or edit
            switch (updateType.toUpperCase()) {
                case 'CREATE':
                    //title = 'Define Network';
                    title = self.$translate.instant('NETWORKS_Edit_CreateTitle');
                    helptoken = 'networksaddingediting';
                    self.selectedNetworkId = '';
                    break;
                case 'EDIT':
                    //title = 'Edit Network';
                    title = self.$translate.instant('NETWORKS_Edit_EditTitle');
                    helptoken = 'networksaddingediting';
                    break;
            }
            var editNetworkModal = self.Modal({
                title: title,
                onHelp: function () {
                    self.GlobalServices.showHelp(helptoken);
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/networking/networks/editnetwork.html',
                controller: 'EditNetworkModalController as editNetwork',
                params: {
                    editMode: updateType,
                    id: self.selectedNetworkId
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            editNetworkModal.modal.show();
        };
        DefineNetworksController.prototype.loadNetworkTypes = function () {
            var self = this;
            return self.$http.post(self.Commands.data.networking.networks.getNetworkTypes, { 'scaleup': false });
        };
        DefineNetworksController.prototype.getNetworksList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.networking.networks.getNetworksList, {});
        };
        DefineNetworksController.prototype.getNetworkById = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.networking.networks.getNetworkById, { id: id });
        };
        DefineNetworksController.prototype.deleteNetworkById = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.networking.networks.deleteNetwork, { id: id });
        };
        DefineNetworksController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        DefineNetworksController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        DefineNetworksController.$inject = ['$http', '$timeout', '$q', 'Modal', 'Dialog', '$filter',
            'Loading', '$translate', 'Commands', 'GlobalServices', 'constants', '$scope', "$window"];
        return DefineNetworksController;
    }());
    asm.DefineNetworksController = DefineNetworksController;
    angular
        .module('app')
        .controller('DefineNetworksController', DefineNetworksController);
})(asm || (asm = {}));
//# sourceMappingURL=defineNetworks.js.map
