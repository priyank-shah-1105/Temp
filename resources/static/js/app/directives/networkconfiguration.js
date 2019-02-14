var asm;
(function (asm) {
    "use strict";
    var Fabric = (function () {
        function Fabric() {
            this.id = '';
            this.name = '';
            this.enabled = true;
            this.redundancy = false;
            this.fabrictype = 'ethernet';
            this.nictype = '2x10Gb';
            this.partitioned = false;
            this.interfaces = [];
            this.deleteState = false;
        }
        Fabric.prototype.Fabric = function (data) {
            if (data === void 0) { data = {}; }
            var self = this;
            self.id = data.id || '';
            self.name = data.name || '';
            self.enabled = data.enabled || true;
            self.redundancy = data.redundancy || false;
            self.fabrictype = data.fabrictype || 'ethernet';
            self.nictype = data.nictype || '2x10Gb';
            self.partitioned = data.partitioned || false;
            self.interfaces = data.interfaces || [];
            self.deleteState = data.deleteState || false;
        };
        return Fabric;
    }());
    asm.Fabric = Fabric;
    var Interface = (function () {
        function Interface() {
            this.id = '';
            this.name = '';
            this.partitions = [];
        }
        Interface.prototype.Interface = function (data) {
            if (data === void 0) { data = {}; }
            var self = this;
            self.id = data.id || '';
            self.name = data.name || '';
            self.partitions = data.partitions || [];
        };
        return Interface;
    }());
    asm.Interface = Interface;
    var Partition = (function () {
        function Partition() {
            this.id = '';
            this.name = '';
            this.networks = [];
            this.minimum = 0;
            this.maximum = 100;
        }
        Partition.prototype.Partition = function (data) {
            if (data === void 0) { data = {}; }
            var self = this;
            self.id = data.id || '';
            self.name = data.name || '';
            self.networks = data.networks || [];
            self.minimum = data.minimum || 0;
            self.maximum = data.maximum || 100;
        };
        return Partition;
    }());
    asm.Partition = Partition;
    var NetworkConfigurationController = (function () {
        function NetworkConfigurationController(GlobalServices, constants, $filter, $http, commands) {
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            this.$filter = $filter;
            this.$http = $http;
            this.commands = commands;
            this.editMode = true;
            this.availableNicTypes = [];
            var self = this;
            if (typeof self.setting.value === 'string' && self.setting.value) {
                self.setting.value = angular.fromJson(self.setting.value);
            }
            if (self.setting.readOnly || self.readOnlyMode) {
                self.editMode = false;
            }
            self.availableNicTypes = constants.availableNicTypes;
            if (self.setting.value == "")
                self.updateSetting();
            self.getNetworksList()
                .then(function (response) {
                self.serviceNetworks = response.data.responseObj;
                self.wireUpNetworks();
            });
            self.id = "setting_" + self.setting.id + "_" + (self.setting.category ? self.setting.category.id : '') + "_";
            self.id += self.GlobalServices.NewGuid();
        }
        NetworkConfigurationController.prototype.DisplayNetworks = function (networks) {
            var self = this;
            var returnVal = '';
            angular.forEach(networks, function (network) {
                if (network.checked) {
                    if (returnVal.length > 0)
                        returnVal += ', ';
                    returnVal += network.name;
                }
            });
            returnVal = self.$filter('ellipsis')(returnVal, 50);
            return returnVal;
        };
        NetworkConfigurationController.prototype.addInterface = function (networkconfig) {
            var self = this;
            var fabric = new Fabric();
            fabric.id = self.GlobalServices.NewGuid();
            fabric.name = 'Interface';
            for (var j = 0; j < 4; j++) {
                var _interface = new Interface();
                _interface.id = self.GlobalServices.NewGuid();
                _interface.name = 'Port ' + (j + 1);
                for (var i = 0; i < 4; i++) {
                    var partition = new Partition();
                    partition.id = self.GlobalServices.NewGuid();
                    partition.name = (i + 1).toString();
                    _interface.partitions.push(partition);
                }
                fabric.interfaces.push(_interface);
            }
            self.setting.value.interfaces.push(fabric);
            self.wireUpNetworks();
        };
        NetworkConfigurationController.prototype.updateSetting = function () {
            var defaultnetwork = {
                id: this.GlobalServices.NewGuid(),
                servertype: 'rack',
                interfaces: [],
                teams: [],
                vswitches: [],
                fabrics: [
                    {
                        id: this.GlobalServices.NewGuid(), name: 'Fabric A / onboard NIC', redundancy: true, enabled: true, fabrictype: 'ethernet', nictype: '2x10Gb', partitioned: true, usedforfc: false,
                        interfaces: [
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 1',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            },
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 2',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            },
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 3',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            },
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 4',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            }
                        ]
                    }, {
                        id: this.GlobalServices.NewGuid(), name: 'Fabric B / PCIe slot 1', redundancy: false, enabled: false, fabrictype: 'ethernet', nictype: '2x10Gb', usedforfc: false, partitioned: true,
                        interfaces: [
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 1',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            },
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 2',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            },
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 3',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            },
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 4',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            }
                        ]
                    }, {
                        id: this.GlobalServices.NewGuid(), name: 'Fabric C / PCIe slot 2', redundancy: false, enabled: false, fabrictype: 'ethernet', nictype: '2x10Gb', usedforfc: false, partitioned: true,
                        interfaces: [
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 1',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            },
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 2',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            },
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 3',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            },
                            {
                                id: this.GlobalServices.NewGuid(),
                                name: 'Port 4',
                                partitions: [
                                    { id: this.GlobalServices.NewGuid(), name: '1', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '2', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '3', networks: [], minimum: 0, maximum: 100 },
                                    { id: this.GlobalServices.NewGuid(), name: '4', networks: [], minimum: 0, maximum: 100 }
                                ]
                            }
                        ]
                    }
                ]
            };
            this.setting.value = defaultnetwork;
        };
        NetworkConfigurationController.prototype.wireUpNetworks = function () {
            var self = this;
            //interface is a reserved word
            angular.forEach(self.setting.value.interfaces, function (fabric) {
                angular.forEach(fabric.interfaces, function (interface1) {
                    angular.forEach(interface1.partitions, function (partition) {
                        //networks to display in dropdown
                        partition._networks = angular.copy(self.serviceNetworks);
                        //mark any that are already included
                        var match;
                        angular.forEach(partition.networks, function (network) {
                            match = _.find(partition._networks, { id: network });
                            if (match) {
                                match.checked = true;
                            }
                        });
                    });
                });
            });
        };
        NetworkConfigurationController.prototype.toggleNetwork = function (partition, _network) {
            _network.checked = !_network.checked;
            if (_network.checked) {
                partition.networks.push(_network.id);
            }
            else {
                partition.networks.splice(_.indexOf(partition.networks, _.find(partition.networks, function (network) { return network === _network.id; })), 1);
            }
        };
        NetworkConfigurationController.prototype.getNetworksList = function () {
            var self = this;
            return self.$http.post(self.commands.data.networking.networks.getUplinkNetworksList, {});
        };
        NetworkConfigurationController.$inject = ['GlobalServices', 'constants', "$filter", "$http", "Commands"];
        return NetworkConfigurationController;
    }());
    angular.module('app')
        .component('networkConfiguration', {
        templateUrl: 'views/networkconfiguration.html',
        controller: NetworkConfigurationController,
        controllerAs: 'networkConfigurationController',
        bindings: {
            setting: '=',
            readOnlyMode: '<?',
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=networkconfiguration.js.map
