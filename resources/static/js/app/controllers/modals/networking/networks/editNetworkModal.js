var asm;
(function (asm) {
    var EditNetworkModalController = (function () {
        function EditNetworkModalController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, $filter, MessageBox, constants) {
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
            this.constants = constants;
            this.errors = new Array();
            this.calledFromDiscoverWizard = false;
            var self = this;
            //create or edit
            self.editMode = $scope.modal.params.editMode;
            //if create then id is blank, if edit then id is selectedNetworkId
            self.selectedNetworkId = $scope.modal.params.id;
            self.isScaleUp = false;
            self.networkTypes = [];
            //self.network = {};
            //type: { id: "", networkType: "", disableStaticOrDHCP: "", vlanidrequired: "" },
            self.network = {
                id: '',
                serverid: '',
                name: '',
                description: '',
                type: {},
                typeid: '',
                vlanid: '',
                staticordhcp: '',
                ipaddressranges: [],
                staticipaddressdetails: [],
                gateway: '',
                subnet: '',
                primarydns: '',
                secondarydns: '',
                dnssuffix: '',
                createdDate: '',
                createdBy: '',
                updatedDate: '',
                updatedBy: '',
                addressPools: [],
                totalAddressPools: 0,
                availableAddressPools: 0,
                assignedAddressPools: 0,
                networkTemplateUsages: [],
                configurestatic: false
            };
            self.ipRange = {
                id: '',
                startingIpAddress: '',
                endingIpAddress: '',
                totalIPAddresses: 0,
                inUseIPAddresses: 0,
                role: ''
            };
            //other fields from old asm used due to shared nature of this model between networks and discovery
            //resourcetype: '',
            //ipaddresstype: 'single',
            //serverCredentialId: '',
            //chassisCredentialId: '',
            //bladeCredentialId: '',
            //iomCredentialId: '',
            //storageCredentialId: '',
            //vcenterCredentialId: '',
            //scvmmCredentialId: '',
            //emCredentialId: '',
            //torCredentialId: '',
            //deviceGroupId: '',
            //includeServers: false,
            //includeChassis: false,
            //includeStorage: false,
            //includeVCenter: false,
            //includeSCVMM: false,
            //includeHypervisor: false,
            //includeTOR: false,
            //managedstate: 'managed',
            //serverPoolId: '',
            if ($scope.modal.params.type) {
                self.typeId = $scope.modal.params.type;
            }
            else {
                self.typeId = '';
            }
            if ($scope.modal.params.calledFromDiscoverWizard) {
                self.calledFromDiscoverWizard = $scope.modal.params.calledFromDiscoverWizard;
                self.typeId = 'HARDWARE_MANAGEMENT';
            }
            //testing
            //self.typeId = 'HARDWARE_MANAGEMENT';
            //self.typeId = 'HYPERVISOR_MANAGEMENT';
            if ($scope.modal.params.mgmtonly) {
                self.networktypelocked = true;
            }
            else {
                self.networktypelocked = false;
            }
            //testing
            //self.networktypelocked = true;
            self.submitForm = false;
            self.activate();
        }
        EditNetworkModalController.prototype.activate = function () {
            var self = this;
            self.loadNetworkTypes();
            self.setSelectedNetworkType();
            //console.log('activate, before get, self.network:');
            //console.log(self.network);
            if (self.selectedNetworkId) {
                self.editMode = 'edit';
                self.$http.post(self.Commands.data.networking.networks.getNetworkById, { 'id': self.selectedNetworkId })
                    .then(function (data) {
                    self.network = data.data.responseObj;
                    //console.log('activate, after get, self.network:');
                    //console.log(self.network);
                    //this must be called here so that it runs upon return from this call
                    self.setSelectedNetworkType();
                }).catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                });
            }
            else {
                self.editMode = 'create';
            }
        };
        EditNetworkModalController.prototype.loadNetworkTypes = function () {
            var self = this;
            self.$http.post(self.Commands.data.networking.networks.getNetworkTypes, { 'scaleup': self.isScaleUp })
                .then(function (data) {
                self.networkTypes = data.data.responseObj;
                //this must be called here so that it runs upon return from this call
                self.setSelectedNetworkType();
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        EditNetworkModalController.prototype.setSelectedNetworkType = function () {
            var self = this;
            if (self.networkTypes.length == 0)
                return;
            if (!self.network.typeid && self.typeId)
                self.network.typeid = self.typeId;
            var networkType = _.find(self.networkTypes, { id: self.network.typeid });
            if (networkType) {
                if (networkType.disableStaticOrDHCP == true) {
                    self.network.staticordhcp = 'DHCP';
                    self.network.configurestatic = false;
                }
                if (networkType.id == 'HARDWARE_MANAGEMENT') {
                    self.network.configurestatic = true;
                    self.network.vlanid = null;
                }
                if (networkType.id == 'HYPERVISOR_MANAGEMENT' || networkType.id == 'SCALEIO_MANAGEMENT' || networkType.id == 'SCALEIO_DATA' || networkType.id == 'SCALEIO_DATA_SDC' || networkType.id == 'SCALEIO_DATA_SDS') {
                    self.network.configurestatic = true;
                }
            }
            self.selectedNetworkType = networkType;
            //this must be called here so that it runs at the end of this call
            self.setConfigureStatic(self.network.configurestatic);
            //TODO:  JB NOT YET COMPLETE
            //added to html:  ; editNetwork.form._submitted = false;
            //on network type change, reset the form
            //self.$scope.form.editNetwork.form._submitted = false;
            //self.$scope.editNetwork.form.$setPristine();
            //self.$scope.editNetwork.form.$setUntouched();
            //$( "#edit_network_form" ).toggleClass( 'has-error', false );
        };
        EditNetworkModalController.prototype.setConfigureStatic = function (newVal) {
            var self = this;
            if (newVal == undefined)
                return;
            var form = self.network;
            if (newVal == false) {
                form.staticordhcp = 'DHCP';
                form.subnet = null;
                form.gateway = null;
                form.primarydns = null;
                form.secondarydns = null;
                form.dnssuffix = null;
                form.ipaddressranges = [];
            }
            else {
                form.staticordhcp = 'Static';
            }
        };
        EditNetworkModalController.prototype.disableStatic = function (networkType) {
            var result = (networkType == 'HYPERVISOR_MANAGEMENT' || networkType == 'SCALEIO_MANAGEMENT' || networkType == 'SCALEIO_DATA' || networkType == 'SCALEIO_DATA_SDC' || networkType == 'SCALEIO_DATA_SDS');
            return result;
        };
        EditNetworkModalController.prototype.doSave = function (formHasErrors) {
            var self = this;
            if (formHasErrors) {
                self.submitForm = true;
                return;
            }
            //if (self.network.type == null) {
            //    self.network.type = angular.copy(self.selectedNetworkType);
            //}
            var validIPrange = true;
            angular.forEach(self.network.ipaddressranges, function (ipRange) {
                if (ipRange.startingIpAddress && ipRange.endingIpAddress) {
                    var ipaddress1 = self.$filter('ip2long')(ipRange.startingIpAddress);
                    var ipaddress2 = self.$filter('ip2long')(ipRange.endingIpAddress);
                    if (ipaddress1 > ipaddress2) {
                        validIPrange = false;
                    }
                }
            });
            if (!validIPrange) {
                //note that this is asynchronous
                var confirm = self.MessageBox((self.$translate.instant('GENERIC_Alert')), (self.$translate.instant('NETWORKS_Edit_AlertSaveIPCheck')));
            }
            if (validIPrange) {
                var myScope = this.$scope;
                var deferred = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                //self.Loading( deferred.promise );
                //self.$http.post( self.Commands.data.networking.networks.saveNetwork, { requestObj: self.network })
                //console.log('doSave, before save, self.network:');
                //console.log(JSON.stringify(self.network));
                self.$http.post(self.Commands.data.networking.networks.saveNetwork, self.network)
                    .then(function (data) {
                    self.objectId = data.data.responseObj.id;
                    //console.log('doSave, after save, self.network:');
                    //console.log(JSON.stringify(data.data.responseObj));
                    deferred.resolve();
                    myScope.modal.close(data.data.responseObj.id);
                }).catch(function (data) {
                    deferred.resolve();
                    //error is in data
                    self.GlobalServices.DisplayError(data.data, self.errors);
                });
            }
        };
        EditNetworkModalController.prototype.iprange_add = function () {
            var self = this;
            var x = angular.copy(self.ipRange);
            //x.id = self.GlobalServices.NewGuid()
            self.network.ipaddressranges.push(x);
        };
        EditNetworkModalController.prototype.iprange_remove = function (range) {
            var self = this;
            var index = self.network.ipaddressranges.indexOf(range);
            self.network.ipaddressranges.splice(index, 1);
        };
        EditNetworkModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        EditNetworkModalController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog',
            'Commands', 'GlobalServices', '$filter', 'Messagebox', 'constants'];
        return EditNetworkModalController;
    }());
    asm.EditNetworkModalController = EditNetworkModalController;
    angular
        .module('app')
        .controller('EditNetworkModalController', EditNetworkModalController);
})(asm || (asm = {}));
//# sourceMappingURL=editNetworkModal.js.map
