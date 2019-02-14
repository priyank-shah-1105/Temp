var asm;
(function (asm) {
    /*
    Notes:
    use self.selectedDevices() to get checked items
     */
    var DevicesController = (function () {
        function DevicesController($http, $window, $translate, $timeout, Dialog, Loading, $q, Modal, Commands, GlobalServices, MessageBox, $rootScope, $routeParams, $filter, constants, localStorageService) {
            this.$http = $http;
            this.$window = $window;
            this.$translate = $translate;
            this.$timeout = $timeout;
            this.Dialog = Dialog;
            this.Loading = Loading;
            this.$q = $q;
            this.Modal = Modal;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.MessageBox = MessageBox;
            this.$rootScope = $rootScope;
            this.$routeParams = $routeParams;
            this.$filter = $filter;
            this.constants = constants;
            this.localStorageService = localStorageService;
            this.selectorConfig = {};
            this.safeSources = { serverPoolsTab: { servers: [], users: [] } };
            this.deviceTimeout = null;
            this.pauseRefreshDevices = false;
            this.getters = {};
            this.debugMode = false;
            var self = this;
            //self.currentView = '';
            //self.currentHealthView = '';
            //self.currentStateView = '';
            self.availableManagedStates = angular.copy(self.constants.availableManagedStates);
            self.availableManagedStates_buttonoverflow = angular.copy(self.constants.availableManagedStates);
            self.availableManagedStates.unshift({
                name: self.$translate.instant('DEVICES_ChangeResourceTypeTo'), id: ''
            });
            self.resourceState = '';
            self.activeTab = 'AllResources';
            self.serverPoolActiveTab = 'Servers';
            self.removeEnabled = true;
            self.loadingDetails = false;
            self.showFilters = false;
            self.selectedDevice = null;
            self.selectedServerPool = {};
            self.selectedServerPoolId = '';
            self.serverpoolslist = [];
            //self.$watch('selectedDevice', function (newValue, oldValue) {
            //    alert("here");
            //});
            self.buttonoverflow = [];
            self.getters = {
                ipaddress: function (field) {
                    return function (object) {
                        return self.$filter('ip2long')(object[field]);
                    };
                }
            };
            if (self.$routeParams.resourceType === 'serverpools') {
                self.clickTab('ServerPools');
                self.showFilters = false;
                if (self.$routeParams.health) {
                    self.selectedServerPoolId = self.$routeParams.health;
                }
            }
            else {
                if (self.$routeParams.resourceType) {
                    self.clickTab('AllResources');
                    self.showFilters = true;
                    self.currentView = self.$routeParams.resourceType;
                    self.currentHealthView = '';
                }
                if (self.$routeParams.health) {
                    self.clickTab('AllResources');
                    self.showFilters = true;
                    self.currentHealthView = self.$routeParams.health;
                }
            }
        }
        Object.defineProperty(DevicesController.prototype, "currentView", {
            get: function () { var self = this; return self.localStorageService.get('devicesTable_currentView') || ''; },
            set: function (val) {
                var self = this;
                self.localStorageService.set('devicesTable_currentView', val);
                self.filterDevices();
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DevicesController.prototype, "currentHealthView", {
            get: function () { var self = this; return self.localStorageService.get('devicesTable_currentHealthView') || ''; },
            set: function (val) { var self = this; self.localStorageService.set('devicesTable_currentHealthView', val); self.filterDevices(); },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DevicesController.prototype, "currentServiceView", {
            get: function () { var self = this; return self.localStorageService.get('devicesTable_currentServiceView') || ''; },
            set: function (val) { var self = this; self.localStorageService.set('devicesTable_currentServiceView', val); self.filterDevices(); },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DevicesController.prototype, "currentStateView", {
            get: function () { var self = this; return self.localStorageService.get('devicesTable_currentStateView') || ''; },
            set: function (val) { var self = this; self.localStorageService.set('devicesTable_currentStateView', val); self.filterDevices(); },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DevicesController.prototype, "currentServerPoolView", {
            get: function () { var self = this; return self.localStorageService.get('devicesTable_currentServerPoolView') || ''; },
            set: function (val) { var self = this; self.localStorageService.set('devicesTable_currentServerPoolView', val); self.filterDevices(); },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DevicesController.prototype, "searchText", {
            get: function () {
                var self = this;
                return self._searchText;
            },
            set: function (value) {
                var self = this;
                self._searchText = value;
                self.filterDevices();
            },
            enumerable: true,
            configurable: true
        });
        DevicesController.prototype.calcButtonOverflow = function () {
            var self = this;
            self.buttonoverflow = [];
            var collapsibleBars = $('.collapsible-button-bar');
            angular.forEach(collapsibleBars, function (b) {
                var taskbar = $(b);
                var currentWidth = 0;
                var taskbarWidth = taskbar.width();
                var buttons = taskbar.find('li');
                angular.forEach(buttons, function (button) {
                    var bWidth;
                    if ($(button).data('current-width'))
                        bWidth = parseInt($(button).data('current-width'));
                    else {
                        bWidth = $(button).width();
                        $(button).data('current-width', bWidth);
                    }
                    currentWidth += bWidth;
                    if (button.id && currentWidth > (taskbarWidth - 375))
                        self.buttonoverflow.push(button.id);
                });
            });
        };
        DevicesController.prototype.activate = function () {
            var self = this;
            // self.checkSelected = self.checkselected();
            $(window).resize(function () {
                if (self.resizetimer)
                    self.$timeout.cancel(self.resizetimer);
                self.resizetimer = self.$timeout(function () {
                    self.calcButtonOverflow();
                }, 300);
            });
            self.refresh();
            self.$http.post(self.Commands.data.services.getServiceDropdown, {
                criteriaObj: {
                    currentPage: 1,
                    filterObj: [],
                    firstDisplayedRowNumber: 0,
                    itemCount: '0',
                    lastDisplayedRowNumber: '0',
                    lastPage: 0,
                    nextPageEnabled: false,
                    paginationObj: { currentPage: 0, rowCountPerPage: 9999, currentCount: 0, totalItemsCount: 0 },
                    previousPageEnabled: false,
                    sortObj: [{ field: 'name', order: 1 }]
                },
                requestObj: []
            })
                .then(function (data) {
                //sort by name, then add All at top
                var sortedData = _.sortBy(data.data.responseObj, function (n) {
                    return n.name.toLowerCase();
                });
                self.services = sortedData;
                //self.currentServiceView = '';
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data);
            });
            angular.element(self.$window).bind('scroll', function () {
                self.scrollPos();
            });
            angular.element(self.$window).bind('resize', function () {
                self.scrollPos();
            });
            self.$rootScope.$on('$locationChangeSuccess', function () {
                self.$timeout.cancel(self.resizetimer);
                self.$timeout.cancel(self.deviceTimeout);
            });
            self.$rootScope.$watch('ASM.gettingStarted', function (newVal, oldVal) {
                if (newVal && oldVal) {
                    if (newVal.pendingResources < oldVal.pendingResources) {
                        self.refresh();
                    }
                }
            });
        };
        DevicesController.prototype.clearTooltips = function () {
            $('.tooltip').remove();
        };
        DevicesController.prototype.scrollPos = function () {
            var self = this;
            self.$timeout.cancel(self.animate);
            self.animate = self.$timeout(function () {
                var currentPos = self.$window.pageYOffset;
                var panelHeight = $('#devicePanel').height();
                var sidebarHeight = $('#deviceInfo').height();
                var adjust = (sidebarHeight + currentPos) - panelHeight;
                if (adjust > 0 && currentPos >= adjust)
                    currentPos -= adjust;
                if (sidebarHeight > panelHeight)
                    currentPos = 0;
                $('#deviceInfo').animate({ 'margin-top': currentPos + 'px' }, 'slow');
            }, 100);
        };
        DevicesController.prototype.clickTab = function (tab) {
            var self = this;
            self.activeTab = tab;
            if (tab === 'AllResources') {
                self.$rootScope.helpToken = 'resources';
            }
            else if (tab === 'ServerPools') {
                self.$rootScope.helpToken = 'serverpools';
            }
        };
        DevicesController.prototype.launchManagementIP = function (ipAddressUrl) {
            window.open(ipAddressUrl);
        };
        DevicesController.prototype.selectedDevices = function () {
            return _.filter(this.devices, { 'isChecked': true });
        };
        DevicesController.prototype.removeDisabled = function () {
            var self = this;
            return !!_.find(self.selectedDevices(), function (device) {
                return ['deploying', 'inuse', 'pendingupdates', 'pendingdelete', 'updating'].indexOf(device.status) !== -1;
            });
        };
        DevicesController.prototype.filterDevices = function () {
            var self = this;
            if (!self.alldevices || self.alldevices.length === 0) {
                self.devices = [];
                self.displayeddevices = [];
                return;
            }
            //self.devices = self.$filter('filter')(self.alldevices, {
            //    deviceType: self.currentView || '',
            //    health: self.currentHealthView || '',
            //    displayservicelist: self.currentServiceView || '',
            //    state: self.currentStateView || '',
            //    displayserverpools: self.currentServerPoolView || ''
            //});
            var filteredDevices = angular.copy(self.alldevices);
            if (self.searchText) {
                self.showSearch = true;
                filteredDevices = self.$filter("filter")(filteredDevices, self.searchText);
            }
            //filter on selected property
            if (self.currentHealthView) {
                self.showFilters = true;
                filteredDevices = _.filter(filteredDevices, function (d) {
                    return d.health === self.currentHealthView;
                });
            }
            if (self.currentServiceView) {
                self.showFilters = true;
                filteredDevices = _.filter(filteredDevices, function (d) {
                    return _.find(d.servicelist, { id: self.currentServiceView });
                });
            }
            if (self.currentStateView) {
                self.showFilters = true;
                filteredDevices = _.filter(filteredDevices, function (d) {
                    return d.state === self.currentStateView;
                });
            }
            if (self.currentServerPoolView) {
                self.showFilters = true;
                filteredDevices = _.filter(filteredDevices, function (d) {
                    return _.find(d.displayserverpools.split(","), function (pool) {
                        return pool === self.currentServerPoolView;
                    });
                });
            }
            //filter based on deviceType
            switch (self.currentView) {
                case 'chassis':
                    self.devices = _.filter(filteredDevices, function (d) {
                        self.showFilters = true;
                        return d.deviceType === 'ChassisM1000e' || d.deviceType === 'ChassisVRTX' || d.deviceType === 'ChassisFX';
                    });
                    break;
                case 'server':
                    self.devices = _.filter(filteredDevices, function (d) {
                        self.showFilters = true;
                        return d.deviceType === 'RackServer' || d.deviceType === 'TowerServer' || d.deviceType === 'BladeServer'
                            || d.deviceType === 'Server' || d.deviceType === 'FXServer';
                    });
                    break;
                case 'switch':
                    self.devices = _.filter(filteredDevices, function (d) {
                        self.showFilters = true;
                        return d.deviceType === 'AggregatorIOM' || d.deviceType === 'MXLIOM' || d.deviceType === 'FXIOM'
                            || d.deviceType === 'genericswitch' || d.deviceType === 'dellswitch' || d.deviceType === 'ciscoswitch';
                    });
                    break;
                case 'unsupported':
                    self.devices = _.filter(filteredDevices, function (d) {
                        self.showFilters = true;
                        return d.deviceType === 'Unsupported';
                    });
                    break;
                case 'vcenter':
                    self.devices = _.filter(filteredDevices, function (d) {
                        self.showFilters = true;
                        return d.deviceType === 'vcenter';
                    });
                    break;
                case 'scvmm':
                    self.devices = _.filter(filteredDevices, function (d) {
                        self.showFilters = true;
                        return d.deviceType === 'scvmm';
                    });
                    break;
                case 'vmm':
                    self.devices = _.filter(filteredDevices, function (d) {
                        self.showFilters = true;
                        return d.deviceType === 'scvmm' || d.deviceType === 'vcenter';
                    });
                    break;
                case 'em':
                    self.devices = _.filter(filteredDevices, function (d) {
                        self.showFilters = true;
                        return d.deviceType === 'em';
                    });
                    break;
                case 'storage':
                    self.devices = _.filter(filteredDevices, function (d) {
                        self.showFilters = true;
                        return d.deviceType === 'storage' || d.deviceType === 'compellent' || d.deviceType === 'equallogic'
                            || d.deviceType === 'netapp' || d.deviceType === 'emcvnx' || d.deviceType === 'emcunity';
                    });
                    break;
                case 'scaleio':
                    self.devices = _.filter(filteredDevices, function (d) {
                        self.showFilters = true;
                        return d.deviceType === 'scaleio';
                    });
                    break;
                default:
                    self.devices = filteredDevices;
                    break;
            }
            self.displayeddevices = [].concat(self.devices);
        };
        //Reusable refresh method that we can put on a timeout
        DevicesController.prototype.refresh = function () {
            var self = this;
            //old part of the test:  || self.selectedDevices().length > 0
            if (self.pauseRefreshDevices) {
                //skip call and wait for next refresh cycle
                self.$timeout.cancel(self.deviceTimeout);
                self.deviceTimeout = self.$timeout(function () {
                    self.refresh();
                }, 120000); //2 minutes
                return;
            }
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.Loading(d.promise).then(function () {
                self.$timeout(self.calcButtonOverflow, 500);
            });
            self.$q.all([
                self.$http.post(self.Commands.data.devices.getDeviceList, null)
                    .then(function (data) {
                    if (self.GlobalServices.cache.devices.length > 0) {
                        self.GlobalServices.cache.devices = data.data.responseObj;
                    }
                    //testing code
                    //console.log('getDeviceList response:');
                    //console.log(JSON.stringify(data.data.responseObj));
                    //sort by deviceType
                    var sortedData = _.sortBy(data.data.responseObj, function (n) {
                        return n.deviceType.toLowerCase();
                    });
                    angular.forEach(sortedData, function (device) {
                        var match = _.find(self.devices, { id: device.id });
                        if (match)
                            device.isChecked = match.isChecked;
                    });
                    self.alldevices = sortedData;
                    angular.forEach(self.alldevices, function (device) {
                        device.displayservicelist = device.servicelist.join(',');
                        device.displayserverpools = device.displayserverpools || '';
                    });
                    self.filterDevices();
                    if (!self.selectedDevice && self.displayeddevices.length) {
                        self.selectedDevice = angular.copy(self.displayeddevices[0]);
                        self.selectedDeviceId = self.selectedDevice.id;
                        self.selectRow(self.selectedDevice);
                    }
                }),
                self.$http.post(self.Commands.data.serverpools.getServerPools, null)
                    .then(function (data) {
                    //sort by name, then add All at top
                    var sortedData = _.sortBy(data.data.responseObj, function (n) {
                        return n.name.toLowerCase();
                    });
                    self.serverpools = sortedData;
                    self.safeSources.serverPoolsTab.serverPools = [].concat(self.serverpools);
                    //load up drop down list for server filter
                    angular.copy(self.serverpools, self.serverpoolslist);
                    //self.currentServerPoolView = '';
                    if (self.serverpools.length) {
                        self.selectServerPoolRow(self.selectedServerPoolId
                            ? _.find(self.serverpools, { id: self.selectedServerPoolId })
                            : self.serverpools[0]);
                    }
                })
            ]).catch(function (response) {
                self.GlobalServices.DisplayError(response.data);
            }).finally(function () {
                self.$timeout.cancel(self.deviceTimeout);
                self.deviceTimeout = self.$timeout(function () {
                    self.refresh();
                }, 120000); //2 minutes
                d.resolve();
            });
        };
        DevicesController.prototype.getManageStateOptions = function () {
            var self = this;
            if (!self.devices)
                return null;
            //var states = self.constants.availableManagedStates.slice();
            var states = self.availableManagedStates.slice();
            /* updated 2/8/17 jb
               removed the logic to determine the state options dynamically based on selection/checked status
               because the back-end now does validation on this anyway so we do not have to handle this on the front end;
               we will now just show all options in ths list all the time */
            //var manage = true;
            //var unmanage = true;
            //var reserved = true;
            //$.each(self.devices, function (index, device) {
            //    if (device.isChecked) {
            //        if (manage && device.state === 'managed') {
            //            manage = false;
            //        } else if (unmanage && device.state === 'unmanaged') {
            //            unmanage = false;
            //        } else if (reserved && device.state === 'reserved') {
            //            reserved = false;
            //        }
            //    }
            //});
            //if (!reserved)
            //    states.splice(3, 1);
            //if (!unmanage)
            //    states.splice(2, 1);
            //if (!manage)
            //    states.splice(1, 1);
            return states;
        };
        DevicesController.prototype.getManageStateOptions_buttonoverflow = function () {
            var self = this;
            if (!self.devices)
                return null;
            var states = self.availableManagedStates_buttonoverflow.slice();
            return states;
        };
        DevicesController.prototype.updateManagedState = function () {
            var self = this;
            var confirmMsg = '';
            var action = '';
            if (self.resourceState === '')
                return;
            else if (self.resourceState === 'managed') {
                action = 'manage';
                confirmMsg = self.$translate.instant('DEVICES_ConfirmManage');
            }
            else if (self.resourceState === 'unmanaged') {
                action = 'unmanage';
                confirmMsg = self.$translate.instant('DEVICES_ConfirmUnmanage');
            }
            else if (self.resourceState === 'reserved') {
                action = 'reserve';
                confirmMsg = self.$translate.instant('DEVICES_ConfirmReserve');
            }
            var confirm = self.Dialog((self.$translate.instant('GENERIC_Confirm')), confirmMsg);
            confirm.then(function () {
                var selectedDevices = [];
                $.each(self.devices, function (index, model) {
                    if (model.isChecked)
                        selectedDevices.push(model.id);
                });
                self.$http.post('devices/' + action, selectedDevices)
                    .then(function () {
                    //refresh also clears the checked items
                    self.refresh();
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                });
            });
            //reset change resource state message whether confirmed or not
            self.resourceState = '';
        };
        //figures out whether to select the first device, no device, or keep the existing device on filter change
        DevicesController.prototype.selectFirstDevice = function () {
            var self = this;
            if (self.selectedDevice.id && self.displayeddevices.length) {
                var deviceFound = self.$filter('filter')(self.displayeddevices, { id: self.selectedDevice.id }, true).length;
                if (deviceFound) {
                    return true;
                }
                else {
                    if (self.displayeddevices.length) {
                        self.selectRow(self.displayeddevices[0]);
                    }
                    else {
                        self.selectedDevice = { id: '' };
                    }
                }
            }
            else {
                if (self.displayeddevices.length) {
                    self.selectRow(self.displayeddevices[0]);
                }
                else {
                    self.selectedDevice = { id: '' };
                }
            }
        };
        DevicesController.prototype.selectRow = function (device) {
            var self = this;
            var deferred = self.$q.defer();
            self.Loading(deferred.promise);
            //if called from refresh() q$
            //an error is trapped during 
            //the copy: Can't copy! Source and 
            //destination are identical.
            if (device.id != self.selectedDeviceId)
                angular.copy(device, self.selectedDevice);
            //            self.loadingDetails = true;
            self.$http.post(self.Commands.data.devices.getDeviceById, { id: device.id }).then(function (data) {
                //data.data.responseObj = { "id": "e31c2cad-4c24-4787-a1a4-5be4a7bdc2e4", "state": "Normal", "deviceType": "scaleio", "deviceDetails": { "id": null, "protectedInKb": 122234, "inMaintenanceInKb": 222133, "degradedInKb": 333345, "failedInKb": 222243, "unusedInKb": 333345, "spareInKb": 345435, "decreasedInKb": 223344, "unavailableUnusedInKb": 333455, "maxCapacityInKb": 444555 }, "scaleIOProtectionDomains": [{ "id": "75765465-6eb5-49ea-96a7-eb880aecbf71", "protectionDomainName": "SDS", "scaleIOServerTypes": [{ "id": "27a692d1-8d5a-492c-bd3b-d52bb486b79e", "name": "Hostname 1", "scaleIOServerDetails": [{ "id": "c60458cd-4df0-4d64-8ed5-3d00a13cfdba", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.1"] }, { "id": "8925d926-9a69-4658-b0da-dcb975f5c7c5", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.2"] }, { "id": "e7ad1ec5-3379-4f2e-9d99-0689b6a52f6c", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.3"] }, { "id": "c7222183-ab43-4c37-bff5-374a2a9c4d24", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.4"] }] }, { "id": "0fa221ff-2a52-4b40-891c-45b02f43ea1b", "name": "Hostname 2", "scaleIOServerDetails": [{ "id": "c60458cd-4df0-4d64-8ed5-3d00a13cfdba", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.1"] }, { "id": "8925d926-9a69-4658-b0da-dcb975f5c7c5", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.2"] }, { "id": "e7ad1ec5-3379-4f2e-9d99-0689b6a52f6c", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.3"] }, { "id": "c7222183-ab43-4c37-bff5-374a2a9c4d24", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.4"] }] }], "scaleIOStoragePools": [] }, { "id": "1e9d1606-5b16-4407-b929-c9402c8323e5", "protectionDomainName": "SDS", "scaleIOServerTypes": [{ "id": "27a692d1-8d5a-492c-bd3b-d52bb486b79e", "name": "Hostname 1", "scaleIOServerDetails": [{ "id": "c60458cd-4df0-4d64-8ed5-3d00a13cfdba", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.1"] }, { "id": "8925d926-9a69-4658-b0da-dcb975f5c7c5", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.2"] }, { "id": "e7ad1ec5-3379-4f2e-9d99-0689b6a52f6c", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.3"] }, { "id": "c7222183-ab43-4c37-bff5-374a2a9c4d24", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.4"] }] }, { "id": "0fa221ff-2a52-4b40-891c-45b02f43ea1b", "name": "Hostname 2", "scaleIOServerDetails": [{ "id": "c60458cd-4df0-4d64-8ed5-3d00a13cfdba", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.1"] }, { "id": "8925d926-9a69-4658-b0da-dcb975f5c7c5", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.2"] }, { "id": "e7ad1ec5-3379-4f2e-9d99-0689b6a52f6c", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.3"] }, { "id": "c7222183-ab43-4c37-bff5-374a2a9c4d24", "name": "Hostname 1", "connected": null, "ipAddresses": ["10.10.10.4"] }] }], "scaleIOStoragePools": [] }, { "id": "eac9333e-0b7d-4a1a-9df8-06114321766b", "protectionDomainName": "StoragePool 1", "scaleIOServerTypes": [], "scaleIOStoragePools": [{ "id": "02a4ef89-1af9-41c3-96cd-3baf8370378f", "name": "Storage Pool 1", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }, { "id": "0c095624-9480-4200-8f17-3821a2e09328", "name": "Storage Pool 2", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }, { "id": "86e35c80-5a63-4bdd-b912-591776ef943e", "name": "Storage Pool 3", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }, { "id": "caebec8c-4e89-4bc3-b98b-998abfc49822", "name": "Storage Pool 4", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }] }, { "id": "a09a5a69-6e88-4237-8fb6-4ee0f47ac107", "protectionDomainName": "StoragePool 2", "scaleIOServerTypes": [], "scaleIOStoragePools": [{ "id": "02a4ef89-1af9-41c3-96cd-3baf8370378f", "name": "Storage Pool 1", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }, { "id": "0c095624-9480-4200-8f17-3821a2e09328", "name": "Storage Pool 2", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }, { "id": "86e35c80-5a63-4bdd-b912-591776ef943e", "name": "Storage Pool 3", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }, { "id": "caebec8c-4e89-4bc3-b98b-998abfc49822", "name": "Storage Pool 4", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }] }, { "id": "e5fe487b-e2ca-4d72-825a-d9ac4d2b2d92", "protectionDomainName": "StoragePool 3", "scaleIOServerTypes": [], "scaleIOStoragePools": [{ "id": "02a4ef89-1af9-41c3-96cd-3baf8370378f", "name": "Storage Pool 1", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }, { "id": "0c095624-9480-4200-8f17-3821a2e09328", "name": "Storage Pool 2", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }, { "id": "86e35c80-5a63-4bdd-b912-591776ef943e", "name": "Storage Pool 3", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }, { "id": "caebec8c-4e89-4bc3-b98b-998abfc49822", "name": "Storage Pool 4", "scaleIOStorageVolumes": [{ "id": "9f92d3c4-ba8c-4a25-8e1e-2b7d7ff4d209", "name": "Volume1", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "f484245d-f938-477f-ab8e-f964fd1787c9", "name": "Volume2", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "48b4eade-7aed-4234-9177-80b8640b4252", "name": "Volume3", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }, { "id": "15a53c4f-149b-430f-94f6-262d671c2676", "name": "Volume4", "size": "5.5TB", "type": "Thin", "mappedSDCs": 3 }] }] }], "connectionState": "Joined", "membershipState": "Connected", "protectionDomainCount": 6, "serverCount": 10, "volumeCount": 215, "sDCCount": 10, "sDSCount": 15, "management": "Clustered" };
                //                self.selectedDevice = data.data.responseObj;
                angular.extend(self.selectedDevice, data.data.responseObj);
                self.selectedDeviceId = self.selectedDevice.id;
                //testing code
                //console.log('self.selectedDeviceId:');
                //console.log(JSON.stringify(self.selectedDeviceId));
                //                self.loadingDetails = false;
            }).catch(function (response) {
                self.GlobalServices.DisplayError(response.data);
            }).finally(function () { deferred.resolve(); });
            if (self.selectedDevice.deviceType === 'vcenter') {
                self.$http.post(self.Commands.data.devices.getVCenter, { id: device.id }).then(function (data) {
                    self.vcenter = data.data.responseObj;
                }).catch(function (response) {
                    self.vcenter = null;
                    self.GlobalServices.DisplayError(response.data);
                }).finally(function () { deferred.resolve(); });
                ;
            }
            if (self.selectedDevice.deviceType === 'scvmm') {
                self.$http.post(self.Commands.data.devices.getSCVMM, { id: device.id }).then(function (data) {
                    self.scvmm = data.data.responseObj;
                }).catch(function (response) {
                    self.scvmm = null;
                    self.GlobalServices.DisplayError(response.data);
                }).finally(function () { deferred.resolve(); });
                ;
            }
            //if (self.selectedDevice.deviceType === 'ciscoswitch') {
            //    self.selectedDeviceImageClass = self.selectedDevice.modelid;
            //}
            switch (self.selectedDevice.deviceType) {
                case 'ChassisM1000e':
                case 'ChassisVRTX':
                case 'ChassisFX':
                case 'BladeServer':
                case 'RackServer':
                case 'TowerServer':
                case 'FXServer':
                case 'AggregatorIOM':
                case 'MXLIOM':
                case 'FXIOM':
                case 'Server':
                case 'dellswitch':
                case 'ciscoswitch':
                case 'compellent':
                case 'equallogic':
                case 'emcvnx':
                case 'emcunity':
                case 'scaleio':
                    self.viewDetailsAvailable = true;
                    break;
                default:
                    self.viewDetailsAvailable = false;
            }
        };
        DevicesController.prototype.selectServerPoolRow = function (serverPool) {
            var self = this;
            self.serverPoolActiveTab = 'Servers';
            //testing code                
            //angular.forEach(serverPool.servers, (server: any) => {
            //    server.ipaddressurl = 'http://1.1.1.1';
            //})
            angular.copy(serverPool, self.selectedServerPool);
            angular.extend(self.safeSources.serverPoolsTab, {
                servers: angular.copy(serverPool.servers),
                users: angular.copy(serverPool.users)
            });
        };
        DevicesController.prototype.remove = function () {
            var self = this;
            var confirm = self.Dialog(('Confirm'), self.$translate.instant('DEVICES_RemoveConfirm'));
            confirm.then(function () {
                var ids = [];
                self.displayeddevices.forEach(function (device) {
                    if (device.isChecked) {
                        ids.push(device.id);
                    }
                });
                self.$http.post(self.Commands.data.devices.remove, ids)
                    .then(function () {
                    //clear out selected
                    self.selectedDevice = null;
                    self.selectedDeviceId = null;
                    self.refresh();
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data);
                });
            });
        };
        DevicesController.prototype.launchGUI = function () {
            var self = this;
            window.open(self.selectedDevice.ipaddressurl);
        };
        DevicesController.prototype.clearFilter = function () {
            var self = this;
            self.currentView =
                self.currentHealthView =
                    self.currentServiceView =
                        self.currentStateView =
                            self.currentServerPoolView = '';
        };
        DevicesController.prototype.createNewServerPool = function () {
            var self = this;
            self.pauseRefreshDevices = true;
            var createnewServerPoolModal = self.Modal({
                title: self.$translate.instant('RESOURCE_CREATE_SERVER_POOL_CreateServerPool'),
                onHelp: function () {
                    self.GlobalServices.showHelp('serverpoolcreate');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/resources/modals/newserverpoolwizard.html',
                controller: 'NewServerPoolWizardController as newServerPoolWizardController',
                params: {},
                onCancel: function () {
                    self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_DEPLOY_ConfirmWizardClosing')).then(function () {
                        console.log('closing');
                        createnewServerPoolModal.modal.close();
                        self.pauseRefreshDevices = false;
                    });
                },
                //TODO: make onfinish actually fire
                onComplete: function () {
                    self.pauseRefreshDevices = false;
                    self.refresh();
                }
            });
            createnewServerPoolModal.modal.show();
        };
        DevicesController.prototype.editServerPool = function () {
            var self = this;
            self.pauseRefreshDevices = true;
            var createnewServerPoolModal = self.Modal({
                title: self.$translate.instant('RESOURCE_EDIT_SERVER_POOL_EditServerPool'),
                onHelp: function () {
                    self.GlobalServices.showHelp('serverpooledit');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/resources/modals/newserverpoolwizard.html',
                controller: 'NewServerPoolWizardController as newServerPoolWizardController',
                params: {
                    pool: angular.copy(self.getChecked(self.serverpools)[0]),
                    editMode: true
                },
                onCancel: function (noConfirm) {
                    if (noConfirm) {
                        self.pauseRefreshDevices = false;
                        createnewServerPoolModal.modal.close();
                    }
                    else {
                        self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_DEPLOY_ConfirmWizardClosing')).then(function () {
                            self.pauseRefreshDevices = false;
                            createnewServerPoolModal.modal.close();
                        });
                    }
                },
                onComplete: function () {
                    self.pauseRefreshDevices = false;
                    self.refresh();
                }
            });
            createnewServerPoolModal.modal.show();
        };
        DevicesController.prototype.removeServerPool = function () {
            var self = this;
            var confirm = self.Dialog(('Confirm'), self.$translate.instant('SERVERPOOLS_RemoveConfirm'));
            confirm.then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors();
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.serverpools.remove, self.getCheckedIds(self.serverpools)).then(function () {
                    self.refresh();
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                })
                    .finally(function () {
                    d.resolve();
                });
            });
        };
        DevicesController.prototype.toggleAll = function (array) {
            var self = this, allSelected = self.getNumChecked(array) === array.length;
            angular.forEach(array, function (user) {
                user.isChecked = !allSelected;
            });
        };
        DevicesController.prototype.canDeleteAll = function (array) {
            var self = this;
            return !_.find(self.getChecked(array), { canDelete: false });
        };
        DevicesController.prototype.getChecked = function (array) {
            return _.filter(array, { isChecked: true });
        };
        DevicesController.prototype.getCheckedIds = function (array) {
            var self = this;
            return self.getChecked(array).map(function (pool) { return pool.id; });
        };
        DevicesController.prototype.isTypeChassis = function (type) {
            return !!_.find(['ChassisM1000e', 'ChassisVRTX', 'ChassisFX'], function (val) { return val === type; });
        };
        DevicesController.prototype.configureChassisButtonDisabled = function () {
            var self = this;
            var configurableCheckedChassis = self.getConfigurableCheckedChassis();
            return !configurableCheckedChassis || configurableCheckedChassis.length !== self.getChecked(self.devices).length;
        };
        DevicesController.prototype.getConfigurableCheckedChassis = function () {
            var self = this;
            var checkedDevices = self.getChecked(self.devices);
            return checkedDevices.length ? _.filter(checkedDevices, function (device) {
                return self.isTypeChassis(device.deviceType) && device.status !== 'updating';
            }).map(function (chassis) { return chassis.id; }) : undefined;
        };
        DevicesController.prototype.getNumChecked = function (array) {
            var self = this;
            return self.getChecked(array).length;
        };
        DevicesController.prototype.launchDiscoverWizard = function () {
            var self = this;
            self.pauseRefreshDevices = true;
            var discoveryWizard = self.Modal({
                title: self.$translate.instant('DISCOVERY_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/discoverywizard.html',
                controller: 'DiscoveryWizardController as DiscoveryWizard',
                params: {
                    mode: 'resources'
                },
                onCancel: function () {
                    var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('DISCOVERY_Cancel_Confirmation'));
                    confirm.then(function () {
                        self.pauseRefreshDevices = false;
                        discoveryWizard.modal.dismiss();
                    });
                },
                onComplete: function () {
                    self.pauseRefreshDevices = false;
                }
            });
            discoveryWizard.modal.show();
        };
        DevicesController.prototype.configureChassis = function () {
            var self = this;
            self.pauseRefreshDevices = true;
            var configChassis = self.Modal({
                title: self.$translate.instant('DEVICES_CONFIGURE_CHASSIS_ConfigureChassis'),
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/resources/modals/configurechassiswizard.html',
                controller: 'ConfigureChassisWizardController as ConfigureChassis',
                params: {
                    id: self.selectedDeviceId,
                    devices: self.getConfigurableCheckedChassis()
                },
                onCancel: function () {
                    self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('DEVICES_Apply_Firmware_Updates_Cancel_Confirmation')).then(function () {
                        self.pauseRefreshDevices = false;
                        configChassis.modal.dismiss();
                    });
                },
                onComplete: function () {
                    self.pauseRefreshDevices = false;
                    self.refresh();
                }
            });
            configChassis.modal.show();
        };
        DevicesController.prototype.runInventory = function () {
            var self = this;
            var selectedDevices = [];
            $.each(self.devices, function (index, model) {
                if (model.isChecked)
                    selectedDevices.push(model.id);
            });
            self.$http.post(self.Commands.data.devices.runInventory, { requestObj: selectedDevices })
                .then(function () {
                //note that this is asynchronous
                self.MessageBox((self.$translate.instant('GENERIC_Alert')), (self.$translate.instant('DEVICES_Alert_RunInventorySuccess')));
                self.refresh();
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            });
        };
        DevicesController.prototype.updateFirmware = function () {
            var self = this;
            self.pauseRefreshDevices = true;
            var selectedDeviceIds = [];
            $.each(self.devices, function (index, model) {
                if (model.isChecked)
                    selectedDeviceIds.push(model.id);
            });
            var updateFirmware = self.Modal({
                title: self.$translate.instant('DEVICES_Apply_Resource_Updates_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('resourcesupdatingfirmware');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/updateservicefirmware.html',
                controller: 'UpdateServiceFirmwareController as UpdateServiceFirmware',
                params: {
                    id: self.selectedDeviceId,
                    ids: selectedDeviceIds,
                    mode: 'device'
                },
                onCancel: function () {
                    var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('DEVICES_Apply_Firmware_Updates_Cancel_Confirmation'));
                    confirm.then(function () {
                        self.pauseRefreshDevices = false;
                        updateFirmware.modal.dismiss();
                        self.refresh();
                    });
                },
                onComplete: function () {
                    self.pauseRefreshDevices = false;
                    self.refresh(); //When the modal is closed, update the data.
                }
            });
            updateFirmware.modal.show();
        };
        DevicesController.prototype.exportAll = function () {
            var self = this;
            var deferred = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.Loading(deferred.promise);
            self.doDownloadRequests('initial', '', deferred);
        };
        DevicesController.prototype.doDownloadRequests = function (call, id, deferred) {
            var self = this;
            var urlToCall;
            var data;
            urlToCall = self.Commands.data.downloads.status;
            data = { 'id': id };
            if (call === 'initial') {
                urlToCall = self.Commands.data.downloads.create;
                data = { 'type': 'devices' };
            }
            self.$http.post(urlToCall, { requestObj: data })
                .then(function (data) {
                switch (data.data.responseObj.status) {
                    case 'NOT_READY':
                        self.$timeout(function () {
                            self.doDownloadRequests('status', data.data.responseObj.id, deferred);
                        }, 5000);
                        break;
                    case 'READY':
                        self.$window.location.assign("downloads/getfile/" + data.data.responseObj.id);
                        deferred.resolve();
                        break;
                    case 'ERROR':
                        deferred.resolve();
                        var errorObj = { message: self.$translate.instant('DEVICES_ExportAll_ErrorExportingDevices'), details: '' };
                        self.GlobalServices.DisplayError(errorObj);
                        break;
                }
            }).catch(function (data) {
                deferred.resolve();
                self.GlobalServices.DisplayError(data.data);
            });
        };
        DevicesController.prototype.openFirmwareReport = function (deviceId) {
            var self = this;
            self.pauseRefreshDevices = true;
            var firmwareReportModal = self.Modal({
                title: self.$translate.instant('SERVICES_RESOURCE_FirmwareReportTitle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('viewfirmwarecompliance');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/resources/modals/resourcecompliancereport.html',
                controller: 'ResourceComplianceReportController as resourceComplianceReportController',
                params: {
                    id: deviceId
                },
                onComplete: function () {
                    self.pauseRefreshDevices = false;
                    self.refresh();
                }
            });
            firmwareReportModal.modal.show();
        };
        DevicesController.prototype.jobsModal = function () {
            var self = this;
            self.pauseRefreshDevices = true;
            var jobsModal = self.Modal({
                title: 'Jobs',
                modalSize: 'modal-lg',
                templateUrl: 'views/jobsmodal.html',
                controller: 'JobsModalController as jobs',
                params: {},
                onComplete: function () {
                    self.pauseRefreshDevices = false;
                }
            });
            jobsModal.modal.show();
        };
        DevicesController.prototype.disableUpdateResourcesButton = function () {
            var self = this, count, selectedDevices = self.selectedDevices();
            return !selectedDevices.length || !!_.find(selectedDevices, function (device) {
                if (!self.canUpdateFirmware(device)) {
                    return true;
                }
                if ((device.availability !== 'notinuse' && device.status !== 'ready') || device.state === 'unmanaged')
                    return true;
                if (device.availability === 'inuse' && self.isServer(device))
                    return true;
                if (device.compliant === 'compliant' || device.compliant === 'updating')
                    return true;
                if (self.isChassis(device) || self.isDellSwitch(device) || self.isCiscoSwitch(device) || self.isServer(device)) {
                    count++;
                    if (count > 1)
                        return true;
                }
                if (self.isStorage(device))
                    return true;
                //if (self.isFirmwareUpdated()) {
                //    returnVal = false;
                //    self.isFirmwareUpdated(false);
                //}
                //return returnVal;
                return false;
            });
        };
        DevicesController.prototype.logSelectedDeviceDetails = function () {
            var self = this;
            console.log('logSelectedDeviceDetails()');
            console.log('self.configureResources.devices:');
            console.log(JSON.stringify(self.selectedDevice));
            console.log('done');
        };
        DevicesController.prototype.isChassis = function (device) {
            return (device.deviceType === 'ChassisM1000e' || device.deviceType === 'ChassisVRTX' || device.deviceType === 'ChassisFX');
        };
        DevicesController.prototype.isFX2 = function (device) {
            return (device.deviceType === 'ChassisFX');
        };
        DevicesController.prototype.isIOM = function (device) {
            return (device.deviceType === 'AggregatorIOM' || device.deviceType === 'MXLIOM' || device.deviceType === 'FXIOM');
        };
        DevicesController.prototype.isServer = function (device) {
            return (device.deviceType === 'RackServer' || device.deviceType === 'TowerServer' || device.deviceType === 'BladeServer' || device.deviceType === 'FXServer' || device.deviceType === 'Server');
        };
        DevicesController.prototype.isStorage = function (device) {
            return device.deviceType === 'storage' || device.deviceType === 'compellent' || device.deviceType === 'equallogic' || device.deviceType === 'netapp' || device.deviceType === 'emcvnx' || device.deviceType === 'emcunity';
        };
        DevicesController.prototype.isEqualLogic = function (device) {
            return (device.deviceType === 'equallogic');
        };
        DevicesController.prototype.isCompellent = function (device) {
            return (device.deviceType === 'compellent');
        };
        DevicesController.prototype.isEmcvnx = function (device) {
            return device.deviceType === 'emcvnx';
        };
        DevicesController.prototype.isEmcUnity = function (device) {
            return device.deviceType === 'emcunity';
        };
        DevicesController.prototype.isNetApp = function (device) {
            return (device.deviceType === 'netapp');
        };
        DevicesController.prototype.isDellSwitch = function (device) {
            return (device.deviceType === 'dellswitch' || device.deviceType === 'genericswitch');
        };
        DevicesController.prototype.isCiscoSwitch = function (device) {
            return (device.deviceType === 'ciscoswitch');
        };
        DevicesController.prototype.isScaleIO = function (device) {
            return (device.deviceType === 'scaleio');
        };
        DevicesController.prototype.canUpdateFirmware = function (device) {
            return !(device.deviceType === 'genericswitch' || device.deviceType === 'scaleio' || device.deviceType === 'vcenter' || device.deviceType === 'em');
        };
        DevicesController.$inject = ['$http', '$window', '$translate',
            '$timeout', 'Dialog', 'Loading',
            '$q', 'Modal', 'Commands',
            'GlobalServices', 'Messagebox', '$rootScope',
            '$routeParams', '$filter', 'constants', 'localStorageService'];
        return DevicesController;
    }());
    asm.DevicesController = DevicesController;
    angular
        .module('app')
        .controller('DevicesController', DevicesController);
})(asm || (asm = {}));
//# sourceMappingURL=devices.js.map
