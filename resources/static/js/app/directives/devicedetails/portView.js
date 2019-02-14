var asm;
(function (asm) {
    "use strict";
    var PortViewController = (function () {
        function PortViewController(Modal, Dialog, $http, $timeout, $q, $translate, $rootScope, GlobalServices, $scope, Loading, Commands, $interval) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.$rootScope = $rootScope;
            this.GlobalServices = GlobalServices;
            this.$scope = $scope;
            this.Loading = Loading;
            this.Commands = Commands;
            this.$interval = $interval;
            this.TorSwitchIntervals = 300;
            this.TorPortIntervals = 25;
            this.IOMintervals = 180;
            this.IomPortIntervals = 55;
            this.NicPortIntervals = 110;
            this.Allnics = 0;
            this.counter = 0;
            this.previousUplink = '';
            this.previousUplinkPort = '';
            this.SelectedConnectionType = '';
            this.SelectedConnectionId = '';
            this.visibleVLANS = [];
            this.servicePage = false;
            this.serversDropDown = [];
            this.firstLoad = true;
            this.portList = [];
            var self = this;
            if (!self.$scope.service) {
                //from device details
                self.selectedDevice = self.$scope.scopeSelectedDevice;
                //asmGUID is not populated by the back-end for a device detail device, so id is used here for the requestId
                self.requestId = self.selectedDevice.id;
            }
            else {
                //from service detail
                self.servicePage = true;
                self.serversDropDown = _.filter(self.$scope.service.components, { type: "server" });
                self.selectedServer = self.serversDropDown[0];
                self.selectedDevice = _.find(self.$scope.service.serverlist, { id: self.selectedServer.id });
                //asmGUID is populated by the back-end for a service.serverlist, and it is used here for the requestId
                self.requestId = self.selectedDevice.asmGUID;
            }
            if (self.$scope.server) {
                //from service detail
                self.selectedServer = _.find(self.serversDropDown, ['id', self.$scope.server.id]);
            }
            self.activate();
        }
        PortViewController.prototype.activate = function () {
            var self = this;
            self.promise = self.$interval(function () {
                self.refresh();
                //console.log('refresh!')
            }, 60000);
            self.refresh();
        };
        PortViewController.prototype.changeServer = function () {
            var self = this;
            self.viewSelectedServer = self.selectedServer;
            self.selectedDevice = _.find(self.$scope.service.serverlist, { id: self.viewSelectedServer.id });
            //asmGUID is populated by the back-end for a service.serverlist, and it is used here for the requestId
            self.requestId = self.selectedDevice.asmGUID;
            self.refresh();
        };
        PortViewController.prototype.$onDestroy = function () {
            var self = this;
            self.$interval.cancel(self.promise);
        };
        PortViewController.prototype.refresh = function () {
            var self = this;
            $('.popover').remove();
            $('#PortViewSVGLines').empty();
            $('#PortViewSelectedSVGLines').empty();
            //spinner
            //self.Loading(self.$q.defer().promise);
            var filterObj = [];
            var request;
            //update filter with dropdown on change
            if (self.selectedPorts && self.selectedPorts != '' && self.selectedPorts.nicId != 'all') {
                if (self.selectedPorts.nicId != null) {
                    filterObj.push({ field: 'nicId', op: '=', opTarget: [self.selectedPorts.nicId] });
                    filterObj.push({ field: 'nicPortId', op: '=', opTarget: [self.selectedPorts.nicPortId] });
                }
                if (self.selectedPorts.health != null) {
                    filterObj.push({ field: 'health', op: '=', opTarget: ['red', 'yellow'] });
                }
                request = {
                    requestObj: { id: self.requestId },
                    criteriaObj: { filterObj: filterObj }
                };
            }
            else {
                request = {
                    requestObj: { id: self.requestId }
                };
            }
            this.$http.post(self.Commands.data.servers.getServerPortViewById, request).then(function (data) {
                //data.data.responseObj = { "id": "ff80808165581fe701655854bf690bd1", "hostname": "aer1-host-c1", "ipaddress": "100.68.97.25", "type": "RackServer", "health": "green", "model": "R630 Base", "powerState": "on", "state": "ready", "statusMessage": "OK", "nics": [{ "id": "NIC.Integrated.1", "name": "NIC.Integrated.1", "modelDisplayName": "Intel X520+i350", "model": "Intel(R) Ethernet 10G 4P X520/I350 rNDC", "ipaddresses": [{ "ipaddress": "192.168.254.101", "ipaddressurl": "https://192.168.254.101" }, { "ipaddress": null, "ipaddressurl": "https://null" }, { "ipaddress": "192.168.206.1", "ipaddressurl": "https://192.168.206.1" }, { "ipaddress": "100.68.97.80", "ipaddressurl": "https://100.68.97.80" }], "macaddress": null, "location": "Integrated", "nparEnabled": false, "ports": [{ "id": "1", "number": 1, "name": "Port 1", "health": "green", "partitions": [{ "id": "1", "name": "NIC.Integrated.1-1-1", "displayName": "Partition 1", "health": "green", "ipaddress": "", "ipaddressurl": null, "macaddress": "24:6E:96:17:E9:10", "wwpn": "20:00:24:6E:96:17:E9:11", "iscsiEnabled": false, "fcoeEnabled": false, "pxeEnabled": false, "vlans": [{ "id": null, "name": "vcesys-sio-data2", "description": null, "type": "SCALEIO_DATA", "vlan": "154" }] }], "fabric": null, "zoneInfo": null, "wwpn": null, "fqdd": null }, { "id": "2", "number": 2, "name": "Port 2", "health": "green", "partitions": [{ "id": "1", "name": "NIC.Integrated.1-2-1", "displayName": "Partition 1", "health": "green", "ipaddress": "", "ipaddressurl": null, "macaddress": "24:6E:96:17:E9:12", "wwpn": "20:00:24:6E:96:17:E9:13", "iscsiEnabled": false, "fcoeEnabled": false, "pxeEnabled": true, "vlans": [{ "id": null, "name": "vcesys-sio-mgmt", "description": null, "type": "SCALEIO_MANAGEMENT", "vlan": "504" }, { "id": null, "name": "vcesys-vmotion", "description": null, "type": "HYPERVISOR_MIGRATION", "vlan": "106" }, { "id": null, "name": "vcesys-sio-data2-Arun", "description": null, "type": "PXE", "vlan": "242" }, { "id": null, "name": "vcesys-esx-mgmt", "description": null, "type": "HYPERVISOR_MANAGEMENT", "vlan": "502" }] }], "fabric": null, "zoneInfo": null, "wwpn": null, "fqdd": null }, { "id": "3", "number": 3, "name": "Port 3", "health": "green", "partitions": [{ "id": "1", "name": "NIC.Integrated.1-3-1", "displayName": "Partition 1", "health": "green", "ipaddress": "", "ipaddressurl": null, "macaddress": "24:6E:96:17:E9:14", "wwpn": null, "iscsiEnabled": false, "fcoeEnabled": false, "pxeEnabled": false, "vlans": [] }], "fabric": null, "zoneInfo": null, "wwpn": null, "fqdd": null }, { "id": "4", "number": 4, "name": "Port 4", "health": "green", "partitions": [{ "id": "1", "name": "NIC.Integrated.1-4-1", "displayName": "Partition 1", "health": "green", "ipaddress": "", "ipaddressurl": null, "macaddress": "24:6E:96:17:E9:15", "wwpn": null, "iscsiEnabled": false, "fcoeEnabled": false, "pxeEnabled": false, "vlans": [] }], "fabric": null, "zoneInfo": null, "wwpn": null, "fqdd": null }], "rank": 2 }, { "id": "NIC.Slot.1", "name": "NIC.Slot.1", "modelDisplayName": "Intel X520", "model": "Intel(R) Ethernet 10G 2P X520 Adapter", "ipaddresses": [{ "ipaddress": "192.168.252.101", "ipaddressurl": "https://192.168.252.101" }, { "ipaddress": null, "ipaddressurl": "https://null" }, { "ipaddress": "192.168.206.1", "ipaddressurl": "https://192.168.206.1" }, { "ipaddress": "100.68.97.80", "ipaddressurl": "https://100.68.97.80" }], "macaddress": null, "location": "Slot", "nparEnabled": false, "ports": [{ "id": "1", "number": 1, "name": "Port 1", "health": "green", "partitions": [{ "id": "1", "name": "NIC.Slot.1-1-1", "displayName": "Partition 1", "health": "green", "ipaddress": "", "ipaddressurl": null, "macaddress": "A0:36:9F:8A:EE:38", "wwpn": "20:00:A0:36:9F:8A:EE:39", "iscsiEnabled": false, "fcoeEnabled": false, "pxeEnabled": false, "vlans": [{ "id": null, "name": "vcesys-sio-data1", "description": null, "type": "SCALEIO_DATA", "vlan": "152" }] }], "fabric": null, "zoneInfo": null, "wwpn": null, "fqdd": null }, { "id": "2", "number": 2, "name": "Port 2", "health": "green", "partitions": [{ "id": "1", "name": "NIC.Slot.1-2-1", "displayName": "Partition 1", "health": "green", "ipaddress": "", "ipaddressurl": null, "macaddress": "A0:36:9F:8A:EE:3A", "wwpn": "20:00:A0:36:9F:8A:EE:3B", "iscsiEnabled": false, "fcoeEnabled": false, "pxeEnabled": true, "vlans": [{ "id": null, "name": "vcesys-sio-mgmt", "description": null, "type": "SCALEIO_MANAGEMENT", "vlan": "504" }, { "id": null, "name": "vcesys-vmotion", "description": null, "type": "HYPERVISOR_MIGRATION", "vlan": "106" }, { "id": null, "name": "vcesys-sio-data2-Arun", "description": null, "type": "PXE", "vlan": "242" }, { "id": null, "name": "vcesys-esx-mgmt", "description": null, "type": "HYPERVISOR_MANAGEMENT", "vlan": "502" }] }], "fabric": null, "zoneInfo": null, "wwpn": null, "fqdd": null }], "rank": 4 }], "ioModules": [], "torSwitches": [{ "id": "cisconexus5k-100.68.96.245", "hostname": "PGNET-RR5-RowE-Rack1-N9372_1", "model": "Nexus9000", "assettag": "SAL1945SFH5", "ipaddress": "100.68.96.245", "ipaddressurl": "", "deviceType": "ciscoswitch", "statusMessage": "OK", "health": "green", "downlinkPorts": [{ "id": "9", "port": "9", "type": null, "health": "green" }, { "id": "10", "port": "10", "type": null, "health": "green" }], "rank": 3, "portChannel": null }, { "id": "cisconexus5k-100.68.96.246", "hostname": "PGNET-RR5-RowE-Rack1-N9372_2", "model": "Nexus9000", "assettag": "SAL1934MP09", "ipaddress": "100.68.96.246", "ipaddressurl": "", "deviceType": "ciscoswitch", "statusMessage": "OK", "health": "green", "downlinkPorts": [{ "id": "10", "port": "10", "type": null, "health": "green" }, { "id": "9", "port": "9", "type": null, "health": "green" }], "rank": 4, "portChannel": null }], "portConnections": [{ "id": "1fb3ec4f-fedd-4a90-a791-1a3f1c8ac393", "nicId": "NIC.Integrated.1", "nicPortId": "1", "connectedDevice": { "deviceId": "cisconexus5k-100.68.96.246", "downlinkPort": { "id": "10", "port": "10", "type": null, "health": "green" } }, "iomUplinkConnections": [] }, { "id": "342bb4b9-76fe-4afa-9ca9-f38ad0769da5", "nicId": "NIC.Integrated.1", "nicPortId": "2", "connectedDevice": { "deviceId": "cisconexus5k-100.68.96.245", "downlinkPort": { "id": "9", "port": "9", "type": null, "health": "green" } }, "iomUplinkConnections": [] }, { "id": "850f94cb-63ca-4c1b-bbfb-0e87e2191e77", "nicId": "NIC.Slot.1", "nicPortId": "1", "connectedDevice": { "deviceId": "cisconexus5k-100.68.96.245", "downlinkPort": { "id": "10", "port": "10", "type": null, "health": "green" } }, "iomUplinkConnections": [] }, { "id": "c85eb085-20fd-4335-a484-28c971cc98b6", "nicId": "NIC.Slot.1", "nicPortId": "2", "connectedDevice": { "deviceId": "cisconexus5k-100.68.96.246", "downlinkPort": { "id": "9", "port": "9", "type": null, "health": "green" } }, "iomUplinkConnections": [] }] } ;
                self.portView = data.data.responseObj;
                if (self.portView.nics.length) {
                    if (self.portView.torSwitches.length) {
                        self.updateTorSwitch();
                    }
                    if (self.portList.length == 0) {
                        self.portList.push({ id: 'all', nicId: 'all', nicPortId: 'all', displayName: self.$translate.instant("PORTVIEW_PortViewSVG_ShowAllConnections"), health: 'all' });
                        $.each(self.portView.nics, function (i, nic) {
                            $.each(nic.ports, function (j, port) {
                                self.portList.push({ id: self.GlobalServices.NewGuid(), nicId: nic.id, nicPortId: port.id, displayName: nic.name + ' - ' + port.name, health: null });
                            });
                        });
                        self.portList.push({ id: self.GlobalServices.NewGuid(), nicId: null, nicPortId: null, health: 'red,yellow', displayName: self.$translate.instant("PORTVIEW_PortViewSVG_AllresourcesinWarningorErrorstate") });
                        //console.log(self.portList);
                        self.selectedPorts = self.portList[0];
                    }
                    if (self.portView.ioModules != null && self.portView.ioModules.length > 0) {
                        self.updateIOM();
                    }
                    //Caculate intervals between NIC groups
                    self.Allnics = 0;
                    $.each(self.portView.nics, function (i, nic) {
                        var Xatt = ((self.Allnics * 110));
                        nic.xcoord = Xatt;
                        var niclength = parseInt(nic.ports.length);
                        self.Allnics = self.Allnics + niclength;
                    });
                    //Gather VLans
                    $.each(self.portView.nics, function (i, nic) {
                        $.each(nic.ports, function (i, port) {
                            $.each(port.partitions, function (i, partition) {
                                $.each(partition.vlans, function (i, vlan) {
                                    var x = 0;
                                });
                            });
                        });
                    });
                    //self.$q.resolve();
                    self.cleanIds();
                }
            }).catch(function (data) {
                //self.$q.resolve();
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        PortViewController.prototype.updateTorSwitch = function () {
            //Update TOR switch with nic info to render vlans on top
            var self = this;
            if (self.portView.torSwitches.length) {
                $.each(self.portView.torSwitches, function (i, torSwitch) {
                    //-MH
                    torSwitch.nics = [];
                    torSwitch.vlans = [];
                    $.each(self.portView.portConnections, function (j, connection) {
                        if (connection.iomUplinkConnections.length) {
                            $.each(connection.iomUplinkConnections, function (k, iom) {
                                if (iom.connectedDevice.deviceId == torSwitch.id) {
                                    var nic = _.filter(self.portView.nics, { id: connection.nicId })[0];
                                    torSwitch.nics.push(nic);
                                    torSwitch.selectedPartition = '';
                                    var port = _.filter(nic.ports, { id: connection.nicPortId })[0];
                                    self.addVlansToTOR(port, torSwitch);
                                }
                            });
                        }
                        else {
                            if (connection.connectedDevice.deviceId == torSwitch.id) {
                                var nic = _.filter(self.portView.nics, { id: connection.nicId })[0];
                                torSwitch.nics.push(nic);
                                torSwitch.selectedPartition = '';
                                var port = _.filter(nic.ports, { id: connection.nicPortId })[0];
                                self.addVlansToTOR(port, torSwitch);
                            }
                        }
                    });
                });
            }
        };
        PortViewController.prototype.addVlansToTOR = function (port, torSwitch) {
            if (port.zoneInfo != null) {
                if (!_.find(torSwitch.vlans, { vlan: port.zoneInfo.vlan })) {
                    torSwitch.vlans.push(port.zoneInfo);
                }
            }
            $.each(port.partitions, function (m, partition) {
                $.each(partition.vlans, function (n, vlan) {
                    if (!_.find(torSwitch.vlans, { vlan: vlan.vlan })) {
                        torSwitch.vlans.push(vlan);
                    }
                });
            });
        };
        PortViewController.prototype.updateIOM = function () {
            //Update IOM with nic info to render vlans on top
            var self = this;
            if (self.portView.ioModules != null) {
                $.each(self.portView.portConnections, function (j, connection) {
                    $.each(self.portView.ioModules, function (i, iom) {
                        iom.nics = [];
                        if (connection.connectedDevice.deviceId == iom.id && connection.iomUplinkConnections != null) {
                            var nic = _.filter(self.portView.nics, { id: connection.nicId })[0];
                            iom.nics.push(nic);
                            iom.selectedPartition = '';
                        }
                    });
                });
            }
        };
        PortViewController.prototype.addPartition = function () {
            var self = this;
            $.each(self.portView.portConnections, function (i, connection) {
                connection.partitions = [];
                $.each(self.portView.nics, function (j, nic) {
                    if (connection.nicId == nic.id) {
                        $.each(nic.ports, function (k, port) {
                            if (connection.nicPortId == port.id) {
                                connection.nicPortHealth = port.health;
                                if (port.partitions) {
                                    //connection.partitions = port.partitions.length;
                                    connection.partitions.push(port.partitions);
                                    connection.partitions = connection.partitions[0];
                                }
                            }
                        });
                    }
                });
            });
        };
        PortViewController.prototype.iomData = function (nicId, portId) {
            var self = this;
            var iom = null;
            if (self.portView.ioModules) {
                var connection = _.filter(self.portView.portConnections, { nicId: nicId, nicPortId: portId });
                //var connection = self.portView.portConnections.where({ nicId: nicId, nicPortId: portId });
                if (connection.length) {
                    var deviceId = connection[0].connectedDevice.deviceId;
                    iom = _.filter(self.portView.ioModules, { id: deviceId });
                    //iom = self.portView.ioModules.where({ id: deviceId });
                    iom = iom.length ? iom[0] : null;
                }
            }
            return iom;
        };
        PortViewController.prototype.torData = function () {
            var self = this;
            $.each(self.portView.nics, function (i, nic) {
                $.each(nic.ports, function (j, port) {
                    var connection = _.filter(self.portView.portConnections, { nicId: nic.id, nicPortId: port.id });
                    port.torSwitches = [];
                    if (connection.length) {
                        if (connection[0].iomUplinkConnections) {
                            $.each(connection[0].iomUplinkConnections, function (i, uplink) {
                                var torSwitch = _.filter(self.portView.torSwitches, { id: uplink.connectedDevice.deviceId })[0];
                                if (torSwitch)
                                    port.torSwitches.push(torSwitch);
                            });
                        }
                        else {
                            var torSwitch1 = _.filter(self.portView.torSwitches, { id: connection[0].connectedDevice.deviceId })[0];
                            if (torSwitch1)
                                port.torSwitches.push(torSwitch1);
                        }
                    }
                });
            });
            self.detailData = [];
            //Aggregate data to allow repeated accordions to work
            angular.forEach(self.portView.nics, function (nic) {
                angular.forEach(nic.ports, function (port) {
                    port.nicInfo = nic;
                    self.detailData.push(port);
                });
            });
            if (self.firstLoad) {
                self.accordionData = self.detailData;
                self.firstLoad = false;
            }
            //Possible hack to prevent accordion closure when polled data has no changes.  May replace with 'track by' solution
            if (self.accordionData) {
                angular.forEach(self.detailData, function (detail, detailIndx) {
                    var x = self.accordionData[detailIndx];
                    var y = angular.copy(x);
                    var comparison = _.isEqual(detail, y);
                    if (!comparison) {
                        self.accordionData = self.detailData;
                        return;
                    }
                });
            }
            //Possible hack to prevent accordion closure when polled data has no changes.  May replace with 'track by' solution
            //if (self.accordionData) {
            //    angular.forEach(self.portView.nics, function (nic, nicIndx) {
            //        angular.forEach(nic.ports, function (port, portIndx) {
            //            var x = self.accordionData.nics[nicIndx].ports[portIndx];
            //            var y = angular.copy(x);
            //            var comparison = _.isEqual(port, y);
            //            if (!comparison) {
            //                self.accordionData = self.portView;
            //                return
            //            }
            //        })
            //    })
            //}
        };
        //Removes periods and adds port ids
        PortViewController.prototype.cleanIds = function () {
            var self = this;
            $.each(self.portView.nics, function (i, nic) {
                nic.id = (nic.id).replace(/\./g, '').replace(/\//g, '-');
                $.each(nic.ports, function (x, port) {
                    port.id = 'port' + port.id;
                });
            });
            $.each(self.portView.torSwitches, function (i, tor) {
                tor.originalId = angular.copy(tor.id);
                tor.id = (tor.id).replace(/\./g, '').replace(/\//g, '-');
                $.each(tor.downlinkPorts, function (j, downlink) {
                    downlink.id = 'port' + (downlink.port).replace(/\,/g, '-').replace(/\//g, '-');
                });
            });
            if (self.portView.ioModules != null) {
                $.each(self.portView.ioModules, function (i, iom) {
                    iom.originalId = angular.copy(iom.id);
                    iom.id = (iom.id).replace(/\./g, '').replace(/\//g, '-');
                    $.each(iom.downlinkPorts, function (j, downlink) {
                        downlink.id = 'port' + (downlink.port).replace(/\,/g, '-').replace(/\//g, '-');
                    });
                    $.each(iom.uplinkPorts, function (j, uplink) {
                        uplink.id = 'port' + (uplink.port).replace(/\,/g, '-').replace(/\//g, '-');
                    });
                });
            }
            $.each(self.portView.portConnections, function (i, connection) {
                connection.nicId = (connection.nicId).replace(/\./g, '').replace(/\//g, '-');
                connection.connectedDevice.deviceId = (connection.connectedDevice.deviceId).replace(/\./g, '').replace(/\//g, '-');
                connection.nicPortId = 'port' + (connection.nicPortId).replace(/\,/g, '-').replace(/\//g, '-');
                connection.connectedDevice.downlinkPort.id = 'port' + (connection.connectedDevice.downlinkPort.port).replace(/\,/g, '-').replace(/\//g, '-');
                if (connection.iomUplinkConnections != null) {
                    $.each(connection.iomUplinkConnections, function (j, iom) {
                        iom.connectedDevice.deviceId = (iom.connectedDevice.deviceId).replace(/\./g, '').replace(/\//g, '-');
                        iom.uplinkPort.id = 'port' + (iom.uplinkPort.port).replace(/\,/g, '-').replace(/\//g, '-');
                        iom.connectedDevice.downlinkPort.id = 'port' + (iom.connectedDevice.downlinkPort.port).replace(/\,/g, '-').replace(/\//g, '-');
                    });
                }
            });
            self.addPartition();
            self.torData();
            self.drawLines();
        };
        PortViewController.prototype.drawLines = function () {
            var self = this;
            self.$timeout(function () {
                self.counter = 0;
                $.each(self.portView.portConnections, function (i, connection) {
                    //If NIC - IOM - TOR
                    //IOM to Tor & Tor to VLAN CONNECTION
                    if (connection.iomUplinkConnections.length) {
                        $.each(connection.iomUplinkConnections, function (x, uplink) {
                            //Tor to VLAN
                            var VlanFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            var VlanHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            var VlanSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            var vlan_x1 = (parseInt($('svg#' + uplink.connectedDevice.deviceId).attr('x')) + 350).toString();
                            var vlan_y1 = '100';
                            var vlan_x2 = vlan_x1;
                            var vlan_y2 = '140';
                            VlanFirstVertical.setAttribute('x1', vlan_x1);
                            VlanFirstVertical.setAttribute('y1', vlan_y1);
                            VlanFirstVertical.setAttribute('x2', vlan_x2);
                            VlanFirstVertical.setAttribute('y2', vlan_y2);
                            //Difine line class names
                            VlanFirstVertical.className.baseVal = connection.nicId + connection.nicPortId + ' ' + uplink.connectedDevice.deviceId + ' ' + uplink.uplinkPort.health;
                            VlanFirstVertical.setAttribute('stroke-dasharray', '4 3'); // dotted line
                            $('#PortViewSVGLines').append(VlanFirstVertical);
                            $('#PortViewSelectedSVGLines').append(angular.copy(VlanFirstVertical));
                            //Partition to Port
                            /*
                            if (connection.partitions.length >= 1) {
                                var PartitionFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                                var PartitionHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                                var PartitionSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');

                                var partition_x1 = (parseInt($('svg#' + connection.nicId).attr('x')) + parseInt($('svg#' + connection.nicId + ' svg#' + connection.nicPortId).attr('x')) + 375).toString();
                                var partition_y1 = '522';
                                var partition_x2 = partition_x1;
                                var partition_y2 = '571';

                                PartitionFirstVertical.setAttribute('x1', partition_x1);
                                PartitionFirstVertical.setAttribute('y1', partition_y1);
                                PartitionFirstVertical.setAttribute('x2', partition_x2);
                                PartitionFirstVertical.setAttribute('y2', partition_y2);
                                //Difine line class names
                                PartitionFirstVertical.className.baseVal = connection.nicId + connection.nicPortId + ' ' + uplink.connectedDevice.deviceId + ' ' + connection.nicPortHealth;

                                PartitionFirstVertical.setAttribute('stroke-dasharray', '4 3'); // dotted line
                                $('#PortViewSVGLines').append(PartitionFirstVertical);

                                $('#PortViewSelectedSVGLines').append(angular.copy(PartitionFirstVertical));
                            }
                            */
                            //Nic to IOM Downlink
                            var newFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            var newHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            var newSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            //var NICx = parseInt($('.NICelements #' + connection.nicId).attr('x'));
                            //var Portx = parseInt($('svg#' + connection.nicPortId).attr('x'));
                            var x1 = (parseInt($('svg#' + connection.nicId).attr('x')) + parseInt($('svg#' + connection.nicId + ' svg#' + connection.nicPortId).attr('x')) + 375).toString();
                            //var x1 = (parseInt($('.NICelements #' + connection.nicId).attr('x')) + parseInt($('svg#' + connection.nicPortId).attr('x')) + 375).toString();
                            //var x1 = (parseInt($('g.NICelements svg.NICports svg.' + connection.nicId + connection.nicPortId).attr('x')) + 375).toString();
                            var y1 = '490';
                            var x2 = (parseInt($('.IOMSlotElements #' + connection.connectedDevice.deviceId + ' #' + connection.connectedDevice.downlinkPort.id).attr('x')) + (parseInt($('.IOMSlotElements #' + connection.connectedDevice.deviceId).attr('x'))) + 370).toString();
                            var y2 = '340';
                            var MidTurn = (390 + (6 * i)).toString();
                            newFirstVertical.setAttribute('x1', x1);
                            newFirstVertical.setAttribute('y1', y1);
                            newFirstVertical.setAttribute('x2', x1);
                            newFirstVertical.setAttribute('y2', MidTurn);
                            newHorizontal.setAttribute('x1', x1);
                            newHorizontal.setAttribute('y1', MidTurn);
                            newHorizontal.setAttribute('x2', x2);
                            newHorizontal.setAttribute('y2', MidTurn);
                            newSecondVertical.setAttribute('x1', x2);
                            newSecondVertical.setAttribute('y1', MidTurn);
                            newSecondVertical.setAttribute('x2', x2);
                            newSecondVertical.setAttribute('y2', y2);
                            ($('.NICelements #' + connection.nicId + ' #' + connection.nicPortId)).addClass(connection.nicId + connection.nicPortId);
                            ($('.NICelements #' + connection.nicId + ' #' + connection.nicPortId + ' .BackgroundSelection')).addClass(connection.nicId + connection.nicPortId);
                            ($('.IOMSlotElements #' + connection.connectedDevice.deviceId)).addClass(connection.nicId + connection.nicPortId);
                            ($('.IOMSlotElements #' + connection.connectedDevice.deviceId + ' .BackgroundSelection')).addClass(connection.nicId + connection.nicPortId);
                            ($('.vlanList #vlan-' + uplink.connectedDevice.deviceId)).addClass(connection.nicId + connection.nicPortId);
                            //Define line class names
                            newFirstVertical.className.baseVal = connection.nicId + connection.nicPortId + ' ' + uplink.connectedDevice.deviceId + ' ' + connection.nicPortHealth;
                            newHorizontal.className.baseVal = connection.nicId + connection.nicPortId + ' ' + uplink.connectedDevice.deviceId + ' ' + connection.nicPortHealth;
                            newSecondVertical.className.baseVal = connection.nicId + connection.nicPortId + ' ' + uplink.connectedDevice.deviceId + ' ' + connection.nicPortHealth;
                            $('#PortViewSVGLines').append(newFirstVertical);
                            $('#PortViewSVGLines').append(newHorizontal);
                            $('#PortViewSVGLines').append(newSecondVertical);
                            $('#PortViewSelectedSVGLines').append(angular.copy(newFirstVertical));
                            $('#PortViewSelectedSVGLines').append(angular.copy(newHorizontal));
                            $('#PortViewSelectedSVGLines').append(angular.copy(newSecondVertical));
                            //IOM to Tor
                            //The counter handles situations where a range of port connections are connected to a range of other port connections
                            if (self.previousUplink != connection.connectedDevice.deviceId || (self.previousUplinkPort != uplink.uplinkPort.id && self.previousUplink == connection.connectedDevice.deviceId)) {
                                self.counter++;
                            }
                            var TorFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            var TorHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            var TorSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            var x1 = (parseInt($('.IOMElements #' + connection.connectedDevice.deviceId + ' #' + uplink.uplinkPort.id).attr('x')) + (parseInt($('.IOMElements #' + connection.connectedDevice.deviceId).attr('x'))) + 370).toString();
                            var y1 = '287';
                            var x2 = (parseInt($('#' + uplink.connectedDevice.deviceId + ' #' + uplink.connectedDevice.downlinkPort.id).attr('x')) + (parseInt($('svg#' + uplink.connectedDevice.deviceId).attr('x'))) + 305).toString();
                            var y2 = '195';
                            var MidTurn = (215 + (6 * (self.counter))).toString();
                            TorFirstVertical.setAttribute('x1', x1);
                            TorFirstVertical.setAttribute('y1', y1);
                            TorFirstVertical.setAttribute('x2', x1);
                            TorFirstVertical.setAttribute('y2', MidTurn);
                            TorHorizontal.setAttribute('x1', x1);
                            TorHorizontal.setAttribute('y1', MidTurn);
                            TorHorizontal.setAttribute('x2', x2);
                            TorHorizontal.setAttribute('y2', MidTurn);
                            TorSecondVertical.setAttribute('x1', x2);
                            TorSecondVertical.setAttribute('y1', MidTurn);
                            TorSecondVertical.setAttribute('x2', x2);
                            TorSecondVertical.setAttribute('y2', y2);
                            //Define line class names
                            TorFirstVertical.className.baseVal = connection.nicId + connection.nicPortId + ' ' + uplink.connectedDevice.deviceId + ' ' + uplink.uplinkPort.health;
                            TorHorizontal.className.baseVal = connection.nicId + connection.nicPortId + ' ' + uplink.connectedDevice.deviceId + ' ' + uplink.uplinkPort.health;
                            TorSecondVertical.className.baseVal = connection.nicId + connection.nicPortId + ' ' + uplink.connectedDevice.deviceId + ' ' + uplink.uplinkPort.health;
                            $('#PortViewSVGLines').append(TorFirstVertical);
                            $('#PortViewSVGLines').append(TorHorizontal);
                            $('#PortViewSVGLines').append(TorSecondVertical);
                            $('#PortViewSelectedSVGLines').append(angular.copy(TorFirstVertical));
                            $('#PortViewSelectedSVGLines').append(angular.copy(TorHorizontal));
                            $('#PortViewSelectedSVGLines').append(angular.copy(TorSecondVertical));
                            ($('svg#' + uplink.connectedDevice.deviceId)).addClass(connection.nicId + connection.nicPortId);
                            self.previousUplinkPort = uplink.uplinkPort.id;
                            self.previousUplink = connection.connectedDevice.deviceId;
                            //Hide Selected lines
                            $('#PortViewSelectedSVGLines line').attr('display', 'none');
                        });
                    }
                    else {
                        //Tor to VLAN
                        var VlanFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var VlanHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var VlanSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var vlan_x1 = (parseInt($('svg#' + connection.connectedDevice.deviceId).attr('x')) + 350).toString();
                        var vlan_y1 = '100';
                        var vlan_x2 = vlan_x1;
                        var vlan_y2 = '140';
                        VlanFirstVertical.setAttribute('x1', vlan_x1);
                        VlanFirstVertical.setAttribute('y1', vlan_y1);
                        VlanFirstVertical.setAttribute('x2', vlan_x2);
                        VlanFirstVertical.setAttribute('y2', vlan_y2);
                        //Difine line class names
                        VlanFirstVertical.className.baseVal = connection.nicId + connection.nicPortId + ' ' + connection.connectedDevice.deviceId + ' ' + connection.nicPortHealth;
                        VlanFirstVertical.setAttribute('stroke-dasharray', '4 3'); // dotted line
                        $('#PortViewSVGLines').append(VlanFirstVertical);
                        $('#PortViewSelectedSVGLines').append(angular.copy(VlanFirstVertical));
                        //Partition to Port
                        /*
                        if (connection.partitions.length >= 1) {
                            var PartitionFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            var PartitionHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                            var PartitionSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');

                            var partition_x1 = (parseInt($('svg#' + connection.nicId).attr('x')) + parseInt($('svg#' + connection.nicId + ' svg#' + connection.nicPortId).attr('x')) + 375).toString();
                            var partition_y1 = '522';
                            var partition_x2 = partition_x1;
                            var partition_y2 = '571';

                            PartitionFirstVertical.setAttribute('x1', partition_x1);
                            PartitionFirstVertical.setAttribute('y1', partition_y1);
                            PartitionFirstVertical.setAttribute('x2', partition_x2);
                            PartitionFirstVertical.setAttribute('y2', partition_y2);
                            //Difine line class names
                            PartitionFirstVertical.className.baseVal = connection.nicId + connection.nicPortId + ' ' + connection.connectedDevice.deviceId + ' ' + connection.nicPortHealth;
                            
                            PartitionFirstVertical.setAttribute('stroke-dasharray', '4 3'); // dotted line

                            $('#PortViewSVGLines').append(PartitionFirstVertical);

                            $('#PortViewSelectedSVGLines').append(angular.copy(PartitionFirstVertical));
                        }
                        */
                        //TOR - NIC
                        var newFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var newHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var newSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var x1 = (parseInt($('svg#' + connection.nicId).attr('x')) + parseInt($('svg#' + connection.nicId + ' svg#' + connection.nicPortId).attr('x')) + 375).toString();
                        var y1 = '490';
                        var x2 = (parseInt($('#' + connection.connectedDevice.deviceId + ' #' + connection.connectedDevice.downlinkPort.id).attr('x')) + (parseInt($('svg#' + connection.connectedDevice.deviceId).attr('x'))) + 305).toString();
                        var y2 = '195';
                        var MidTurn = (390 + (6 * i)).toString();
                        newFirstVertical.setAttribute('x1', x1);
                        newFirstVertical.setAttribute('y1', y1);
                        newFirstVertical.setAttribute('x2', x1);
                        newFirstVertical.setAttribute('y2', MidTurn);
                        newHorizontal.setAttribute('x1', x1);
                        newHorizontal.setAttribute('y1', MidTurn);
                        newHorizontal.setAttribute('x2', x2);
                        newHorizontal.setAttribute('y2', MidTurn);
                        newSecondVertical.setAttribute('x1', x2);
                        newSecondVertical.setAttribute('y1', MidTurn);
                        newSecondVertical.setAttribute('x2', x2);
                        newSecondVertical.setAttribute('y2', y2);
                        ($('.NICelements #' + connection.nicId + ' #' + connection.nicPortId)).addClass(connection.nicId + connection.nicPortId);
                        ($('.NICelements #' + connection.nicId + ' #' + connection.nicPortId + ' .BackgroundSelection')).addClass(connection.nicId + connection.nicPortId);
                        ($('.IOMSlotElements #' + connection.connectedDevice.deviceId)).addClass(connection.nicId + connection.nicPortId);
                        ($('.IOMSlotElements #' + connection.connectedDevice.deviceId + ' .BackgroundSelection')).addClass(connection.nicId + connection.nicPortId);
                        ($('.vlanList #vlan-' + connection.connectedDevice.deviceId)).addClass(connection.nicId + connection.nicPortId);
                        ($('svg#' + connection.connectedDevice.deviceId)).addClass(connection.nicId + connection.nicPortId);
                        //Define line class names
                        newFirstVertical.className.baseVal = connection.nicId + connection.nicPortId + ' ' + connection.connectedDevice.deviceId + ' ' + connection.nicPortHealth;
                        newHorizontal.className.baseVal = connection.nicId + connection.nicPortId + ' ' + connection.connectedDevice.deviceId + ' ' + connection.nicPortHealth;
                        newSecondVertical.className.baseVal = connection.nicId + connection.nicPortId + ' ' + connection.connectedDevice.deviceId + ' ' + connection.nicPortHealth;
                        $('#PortViewSVGLines').append(newFirstVertical);
                        $('#PortViewSVGLines').append(newHorizontal);
                        $('#PortViewSVGLines').append(newSecondVertical);
                        $('#PortViewSelectedSVGLines').append(angular.copy(newFirstVertical));
                        $('#PortViewSelectedSVGLines').append(angular.copy(newHorizontal));
                        $('#PortViewSelectedSVGLines').append(angular.copy(newSecondVertical));
                        //Hide selected lines
                        $('#PortViewSelectedSVGLines line').attr('display', 'none');
                    }
                });
            }, 1000);
        };
        PortViewController.prototype.selectConnection = function (source, port, partition, type) {
            var self = this;
            var parentId = source.id;
            //Reset svg drawing
            if (self.SelectedConnectionId !== '') {
                //$('#PortViewSVGLines line.' + connection.nicId + connection.nicPortId).attr("style", "");
                $('.BackgroundSelection').attr("display", "none");
                $('#PortViewSelectedSVGLines line').attr("display", "none");
            }
            //Show all vlan lists and remove selected class from partition
            $('.vlan').attr('style', '');
            $('.selectedPartition').removeClass('selectedPartition');
            $.each(self.portView.portConnections, function (i, connection) {
                //Reset every line
                $('#PortViewSVGLines line.' + connection.nicId + connection.nicPortId).attr("style", "");
                //Clicked a vlan up top
                if (partition && !partition.ipaddress) {
                    self.SelectedConnectionId = connection.id;
                    var selectedvlan = partition;
                    var lineclass = $('ul.vlanList li#vlan-' + parentId).attr("class");
                    $('#PortViewSVGLines line.' + lineclass).attr("style", "");
                    $('ul.vlanList li#vlan-' + parentId + ' li.' + selectedvlan.vlan).attr("style", 'background-color: lightBlue');
                    if (connection.iomUplinkConnections.length > 0) {
                        $.each(connection.iomUplinkConnections, function (x, uplinkconnection) {
                            if (uplinkconnection.connectedDevice.deviceId == parentId) {
                                $.each(connection.partitions, function (y, partitiongroup) {
                                    $.each(partitiongroup.vlans, function (z, vlan) {
                                        if (vlan.vlan == partition.vlan) {
                                            $('svg#' + connection.nicId + ' #' + connection.nicPortId + ' .PartitionElements.' + partitiongroup.id).addClass('selectedPartition');
                                            $('#PortViewSelectedSVGLines line.' + connection.nicId + connection.nicPortId).attr("display", "inline");
                                            $('svg#PortView .BackgroundSelection.' + connection.nicId + connection.nicPortId).attr("display", "inline");
                                            $('svg#PortView #' + connection.connectedDevice.deviceId + ' .BackgroundSelection').attr("display", "inline");
                                        }
                                    });
                                });
                            }
                        });
                    }
                    else {
                        if (connection.connectedDevice.deviceId == parentId) {
                            $.each(connection.partitions, function (y, partitiongroup) {
                                $.each(partitiongroup.vlans, function (z, vlan) {
                                    if (vlan.vlan == partition.vlan) {
                                        $('svg#' + connection.nicId + ' #' + connection.nicPortId + ' .PartitionElements.' + partitiongroup.id).addClass('selectedPartition');
                                        $('#PortViewSelectedSVGLines line.' + connection.nicId + connection.nicPortId).attr("display", "inline");
                                        $('svg#PortView .BackgroundSelection.' + connection.nicId + connection.nicPortId).attr("display", "inline");
                                        $('svg#PortView #' + connection.connectedDevice.deviceId + ' .BackgroundSelection').attr("display", "inline");
                                    }
                                });
                            });
                        }
                    }
                }
                //Clicked a NIC 
                if (type == 'NIC') {
                    var childId = port.id;
                    if (connection.nicId == parentId && connection.nicPortId == childId) {
                        self.SelectedConnectionType = 'NIC';
                        self.SelectedConnectionId = connection.id;
                        $('#PortViewSelectedSVGLines line.' + connection.nicId + connection.nicPortId).attr("display", "inline");
                        $('svg#PortView .BackgroundSelection.' + connection.nicId + connection.nicPortId).attr("display", "inline");
                        $.each(connection.iomUplinkConnections, function (j, uplink) {
                            $('svg#PortView #' + uplink.connectedDevice.deviceId + ' .BackgroundSelection').attr("display", "inline");
                        });
                        $.each(self.portView.torSwitches, function (j, torSwitch) {
                            $.each(torSwitch.nics, function (j, nic) {
                                if (nic.id == parentId) {
                                    $.each(nic.ports, function (k, port) {
                                        if (port.id == childId) {
                                            //Filter vlan list up top
                                            self.visibleVLANS = port.partitions[0].vlans;
                                            $('svg#' + nic.id + ' #' + port.id + ' .PartitionElements.' + port.partitions[0].id).addClass('selectedPartition');
                                            $('ul.vlanList li#vlan-' + torSwitch.id + '.' + parentId + childId + ' .vlan').attr("style", "display: none");
                                            $.each(self.visibleVLANS, function (l, vlan) {
                                                $('#vlan-' + torSwitch.id + '.' + parentId + childId + ' .vlan.' + vlan.vlan).attr('style', '');
                                            });
                                        }
                                    });
                                }
                            });
                        });
                    }
                }
                //Clicked a IOM
                if (type == 'IOM') {
                    if (connection.iomUplinkConnections.length > 0 && connection.connectedDevice.deviceId == parentId) {
                        self.SelectedConnectionType = 'IOM';
                        self.SelectedConnectionId = connection.id;
                        $('#PortViewSelectedSVGLines line.' + connection.nicId + connection.nicPortId).attr("display", "inline");
                        $('svg#PortView .BackgroundSelection.' + connection.nicId + connection.nicPortId).attr("display", "inline");
                        $.each(connection.iomUplinkConnections, function (j, uplink) {
                            $('svg#PortView #' + uplink.connectedDevice.deviceId + ' .BackgroundSelection').attr("display", "inline");
                        });
                    }
                }
                //Clicked a TOR switch
                if (type == 'TOR') {
                    $.each(self.portView.portConnections, function (c, connection) {
                        $.each(source.downlinkPorts, function (s, port) {
                            if (connection.iomUplinkConnections.length > 0) {
                                $.each(connection.iomUplinkConnections, function (u, item) {
                                    if (item.connectedDevice.deviceId == source.id && item.connectedDevice.downlinkPort.port == port.port) {
                                        self.SelectedConnectionType = 'TOR';
                                        self.SelectedConnectionId = connection.id;
                                        $('#PortViewSelectedSVGLines line.' + connection.nicId + connection.nicPortId + '.' + item.connectedDevice.deviceId).attr("display", "inline");
                                        $('svg#PortView .BackgroundSelection.' + connection.nicId + connection.nicPortId).attr("display", "inline");
                                        $('svg#PortView #' + item.connectedDevice.deviceId + ' .BackgroundSelection').attr("display", "inline");
                                    }
                                });
                            }
                            else {
                                if (connection.connectedDevice.deviceId == source.id && connection.connectedDevice.downlinkPort.port == port.port) {
                                    self.SelectedConnectionType = 'TOR';
                                    self.SelectedConnectionId = connection.id;
                                    $('#PortViewSelectedSVGLines line.' + connection.nicId + connection.nicPortId + '.' + connection.connectedDevice.deviceId).attr("display", "inline");
                                    $('svg#PortView .BackgroundSelection.' + connection.nicId + connection.nicPortId).attr("display", "inline");
                                    $('svg#PortView #' + connection.connectedDevice.deviceId + ' .BackgroundSelection').attr("display", "inline");
                                }
                            }
                        });
                    });
                }
            });
        };
        PortViewController.prototype.convertToObj = function (obj) {
            return obj;
        };
        PortViewController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', '$rootScope', 'GlobalServices', '$scope', 'Loading', 'Commands', '$interval'];
        return PortViewController;
    }());
    function deviceportview() {
        return {
            restrict: 'E',
            templateUrl: 'views/deviceportview.html',
            replace: true,
            transclude: false,
            controller: PortViewController,
            controllerAs: 'deviceportview',
            scope: {
                scopeSelectedDevice: "=device",
                service: "=service",
                server: "=",
                errors: "="
            },
            link: function (scope, element, attributes, controller) {
            }
        };
    }
    angular.module('app').
        directive('deviceportview', deviceportview);
})(asm || (asm = {}));
//# sourceMappingURL=portView.js.map
