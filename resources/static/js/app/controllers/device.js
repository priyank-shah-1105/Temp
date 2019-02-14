var asm;
(function (asm) {
    var DeviceController = (function () {
        function DeviceController($http, $translate, $routeParams, GlobalServices, $timeout, constants, Commands, Loading, $q, $filter, Modal) {
            this.$http = $http;
            this.$translate = $translate;
            this.$routeParams = $routeParams;
            this.GlobalServices = GlobalServices;
            this.$timeout = $timeout;
            this.constants = constants;
            this.Commands = Commands;
            this.Loading = Loading;
            this.$q = $q;
            this.$filter = $filter;
            this.Modal = Modal;
            this.tabs = {
                summary: "Summary",
                portView: "PortView",
                networkInterfaces: "NetworkInterfaces",
                firmwareRevisions: "FirmwareRevisions",
                cpus: "CPUs",
                memory: "Memory",
                blades: "Blades",
                iOModules: "IOModules",
                chassisController: "ChassisController",
                iKVM: "IKVM",
                powerSupplies: "PowerSupplies",
                equallogicVolumes: "EquallogicVolumes",
                compellentVolumes: "CompellentVolumes",
                netAppVolumes: "NetAppVolumes",
                localStorage: "LocalStorage",
                luns: "Luns",
                fileSystems: "FileSystems",
                scaleIOStorage: "ScaleIOStorage",
                scaleIOServers: "ScaleIOServers",
                performance: "Performance"
            };
            this.errors = new Array();
            //pie chart
            this.overallSystemUsage = null;
            this.memorySystemUsage = null;
            this.cpuSystemUsage = null;
            this.ioSystemUsage = null;
            //line charts
            this.systemUsageHistoricalData = null;
            this.memoryUsageHistoricalData = null;
            this.cpuUsageHistoricalData = null;
            this.ioUsageHistoricalData = null;
            //dropdown values & data populating text below charts (Whole timeframe object)
            this.usages = {
                system: undefined,
                cpu: undefined,
                ram: undefined,
                io: undefined
            };
            var self = this;
            self.id = self.$routeParams.id;
            self.deviceType = self.$routeParams.resourceType;
        }
        DeviceController.prototype.activate = function () {
            var self = this, callToMake, url = "", callback;
            if (self.deviceType) {
                if (self.isServer()) {
                    self.setTab(self.tabs.portView);
                    url = self.Commands.data.servers.getServerById;
                    callback = function () {
                        //set pie charts
                        if (self.device.systemusage) {
                            self.setTab(self.tabs.summary);
                            self.overallSystemUsage = angular.merge(self.getPieChartObject(self.device.systemusage, { innerSize: "90%" }), {
                                size: {
                                    height: 230,
                                    width: undefined
                                }
                            });
                            self.overallSystemUsage2 = self.getPieChartObject(self.device.systemusage);
                        }
                        if (self.device.cpuusage) {
                            self.cpuSystemUsage = self.getPieChartObject(self.device.cpuusage);
                        }
                        if (self.device.memoryusage) {
                            self.memorySystemUsage = self.getPieChartObject(self.device.memoryusage);
                        }
                        if (self.device.iousage) {
                            self.ioSystemUsage = self.getPieChartObject(self.device.iousage);
                        }
                        //set dropdowns with initial value
                        if (self.device.systemusage) {
                            if (_.find(self.device.systemusage.historicaldata, { id: "Last Month" })) {
                                self.usages.system = _.find(self.device.systemusage.historicaldata, { id: "Last Month" });
                            }
                            else {
                                self.usages.system = self.device.systemusage.historicaldata[0];
                            }
                            self.updateSystemUsage();
                        }
                        if (self.device.memoryusage) {
                            self.usages.ram = self.device.memoryusage.historicaldata[0];
                            self.updateMemoryUsage();
                        }
                        if (self.device.cpuusage) {
                            self.usages.cpu = self.device.cpuusage.historicaldata[0];
                            self.updateCPUUsage();
                        }
                        if (self.device.iousage) {
                            self.usages.io = self.device.iousage.historicaldata[0];
                            self.updateIOUsage();
                        }
                    };
                }
                if (self.isChassis()) {
                    self.setTab(self.tabs.blades);
                    url = self.Commands.data.chassis.getChassisById;
                }
                if (self.isIOM()) {
                    url = self.Commands.data.iom.getIOMById;
                }
                if (self.isDellSwitch()) {
                    url = self.Commands.data.dellSwitch.getDellSwitchById;
                }
                if (self.isCiscoSwitch()) {
                    url = self.Commands.data.ciscoSwitch.getCiscoSwitchById;
                }
                if (self.isEqualLogic()) {
                    self.setTab(self.tabs.equallogicVolumes);
                    url = self.Commands.data.equalLogicStorage.getEqualLogicStorageById;
                }
                if (self.isCompellent()) {
                    self.setTab(self.tabs.summary);
                    url = self.Commands.data.compellentStorage.getCompellentStorageById;
                    callback = function () {
                        // set pie charts
                        self.storageCapacity = angular.merge(self.getGenericPie(), {
                            series: [
                                {
                                    data: [
                                        { name: self.$translate.instant("DEVICEDETAILS_UsedDiskSpace"), y: (parseFloat(self.device.useddiskspacepercent) / parseFloat(/([0-9]*)/.exec(self.device.diskspacetotal)[0])) * 100, color: "#007db8", num: _.round(parseFloat(self.device.useddiskspacepercent), 2).toString() + " GB" },
                                        { name: self.$translate.instant("DEVICEDETAILS_FreeDiskSpace"), y: (parseFloat(self.device.freediskspacepercent) / parseFloat(/([0-9]*)/.exec(self.device.diskspacetotal)[0])) * 100, color: "#dddddd", num: _.round(parseFloat(self.device.freediskspacepercent), 2).toString() + " GB" },
                                    ]
                                }
                            ],
                            legend: {
                                align: 'right',
                                layout: 'vertical',
                                verticalAlign: 'top',
                                x: 0,
                                y: 100
                            }
                        });
                    };
                }
                if (self.isEmcUnity()) {
                    self.setTab(self.tabs.summary);
                    url = self.Commands.data.emcStorage.getEmcUnityStorageById;
                    callback = function () {
                        // set pie charts
                        self.storagePoolCapacity = angular.merge(self.getGenericPie(), {
                            series: [
                                {
                                    data: [
                                        { name: self.$translate.instant("DEVICEDETAILS_UsedDiskSpace"), y: (parseFloat(self.device.useddiskspacepercent) / parseFloat(/([0-9]*)/.exec(self.device.diskspacetotal)[0])) * 100, color: "#007db8", num: _.round(parseFloat(self.device.useddiskspacepercent), 2).toString() + " GB" },
                                        { name: self.$translate.instant("DEVICEDETAILS_FreeDiskSpace"), y: (parseFloat(self.device.freediskspacepercent) / parseFloat(/([0-9]*)/.exec(self.device.diskspacetotal)[0])) * 100, color: "#dddddd", num: _.round(parseFloat(self.device.freediskspacepercent), 2).toString() + " GB" },
                                    ]
                                }
                            ],
                            legend: {
                                align: 'right',
                                layout: 'vertical',
                                verticalAlign: 'top',
                                x: 0,
                                y: 100
                            }
                        });
                        var sizeTotal = 0;
                        angular.forEach(self.device.storagepools, function (storagePool) {
                            storagePool.intSize = parseFloat(/([0-9]*)/.exec(storagePool.size)[0]);
                            sizeTotal += storagePool.intSize;
                        });
                        self.storagePools = angular.merge(self.getGenericPie(), {
                            series: [
                                {
                                    data: _.map(self.device.storagepools, function (storagePool) {
                                        return {
                                            name: storagePool.name,
                                            y: Math.round(storagePool.intSize),
                                            sizeUsed: storagePool.size,
                                            percent: (storagePool.intSize / sizeTotal) * 100,
                                            showInLegend: true
                                        };
                                    })
                                }
                            ],
                            legend: { enabled: true }
                        });
                    };
                }
                if (self.isEmcvnx()) {
                    self.setTab(self.tabs.summary);
                    url = self.Commands.data.emcStorage.getEmcvnxStorageById;
                    callback = function () {
                        // set pie charts
                        self.storagePoolCapacity = angular.merge(self.getGenericPie(), {
                            series: [
                                {
                                    data: [
                                        { name: self.$translate.instant("DEVICEDETAILS_UsedDiskSpace"), y: (parseFloat(self.device.useddiskspacepercent) / parseFloat(/([0-9]*)/.exec(self.device.diskspacetotal)[0])) * 100, color: "#007db8", num: _.round(parseFloat(self.device.useddiskspacepercent), 2).toString() + " GB" },
                                        { name: self.$translate.instant("DEVICEDETAILS_FreeDiskSpace"), y: (parseFloat(self.device.freediskspacepercent) / parseFloat(/([0-9]*)/.exec(self.device.diskspacetotal)[0])) * 100, color: "#dddddd", num: _.round(parseFloat(self.device.freediskspacepercent), 2).toString() + " GB" },
                                    ]
                                }
                            ],
                            legend: {
                                align: 'right',
                                layout: 'vertical',
                                verticalAlign: 'top',
                                x: 0,
                                y: 100
                            }
                        });
                        var sizeTotal = 0;
                        angular.forEach(self.device.storagepools, function (storagePool) {
                            storagePool.intSize = parseFloat(/([0-9]*)/.exec(storagePool.size)[0]);
                            sizeTotal += storagePool.intSize;
                        });
                        self.storagePools = angular.merge(self.getGenericPie(), {
                            series: [
                                {
                                    data: _.map(self.device.storagepools, function (storagePool) {
                                        return {
                                            name: storagePool.name,
                                            y: Math.round(storagePool.intSize),
                                            sizeUsed: storagePool.size,
                                            percent: (storagePool.intSize / sizeTotal) * 100,
                                            showInLegend: true
                                        };
                                    })
                                }
                            ],
                            legend: { enabled: true }
                        });
                    };
                }
                if (self.isNetApp()) {
                    self.setTab(self.tabs.netAppVolumes);
                    url = self.Commands.data.netappStorage.getNetAppStorageById;
                }
                if (self.isScaleIO()) {
                    self.setTab(self.tabs.performance);
                    url = self.Commands.data.scaleIO.getScaleIObyId;
                    callback = function () {
                        self.scaleIODonut = {
                            options: {
                                chart: {
                                    type: 'pie',
                                    renderTo: 'scaleIODonut',
                                    animation: false
                                },
                                title: {
                                    text: ''
                                },
                                tooltip: {
                                    enabled: false,
                                    useHTML: true,
                                    style: { zIndex: 99999 },
                                    positioner: function (labelWidth, labelHeight, point) { return { x: point.plotX + 10, y: point.plotY + 10 }; }
                                },
                                plotOptions: {
                                    pie: {
                                        allowPointSelect: true,
                                        size: '100%',
                                        //innerSize: '75%',
                                        dataLabels: { enabled: false },
                                        states: { select: { borderColor: '#0085c3' } },
                                        borderWidth: 3,
                                    }
                                }
                            },
                            size: {
                                height: 200,
                                width: 200
                            },
                            spacingBottom: 0,
                            spacingTop: 0,
                            spacingLeft: 0,
                            spacingRight: 0,
                            series: [{
                                    data: [
                                        {
                                            name: self.$translate.instant('DEVICES_EMC_SCALEIO_Protected_DonutTitle'),
                                            y: angular.copy(self.device.scaleIOInformation.protectedInKb),
                                            color: '#007db8'
                                        },
                                        {
                                            name: self.$translate.instant('DEVICES_EMC_SCALEIO_InMaintenance_DonutTitle'),
                                            y: angular.copy(self.device.scaleIOInformation.inMaintenanceInKb),
                                            color: '#6bacde'
                                        },
                                        {
                                            name: self.$translate.instant('DEVICES_EMC_SCALEIO_Degraded_DonutTitle'),
                                            y: angular.copy(self.device.scaleIOInformation.degradedInKb),
                                            color: '#f2af00'
                                        },
                                        {
                                            name: self.$translate.instant('DEVICES_EMC_SCALEIO_Failed_DonutTitle'),
                                            y: angular.copy(self.device.scaleIOInformation.failedInKb),
                                            color: '#CE1126'
                                        },
                                        {
                                            name: self.$translate.instant('DEVICES_EMC_SCALEIO_Unused_DonutTitle'),
                                            y: angular.copy(self.device.scaleIOInformation.unusedInKb),
                                            color: '#EEEEEE'
                                        },
                                        {
                                            name: self.$translate.instant('DEVICES_EMC_SCALEIO_Spare_DonutTitle'),
                                            y: angular.copy(self.device.scaleIOInformation.spareInKb),
                                            color: '#cce5f1'
                                        },
                                        {
                                            name: self.$translate.instant('DEVICES_EMC_SCALEIO_Decreased_DonutTitle'),
                                            y: angular.copy(self.device.scaleIOInformation.decreasedInKb),
                                            color: '#6e2585'
                                        },
                                        {
                                            name: self.$translate.instant('DEVICES_EMC_SCALEIO_Unavailable_DonutTitle'),
                                            y: angular.copy(self.device.scaleIOInformation.unavailableUnusedInKb),
                                            color: '#FFFFFF'
                                        }
                                    ],
                                    innerSize: '80%'
                                }]
                        };
                        self.scaleIODonutDataCopy = angular.copy(self.scaleIODonut.series[0].data);
                        var minimumValue = self.device.scaleIOInformation.maxCapacityInKb * .05;
                        angular.forEach(self.scaleIODonut.series[0].data, function (slice) {
                            if (slice.y > 0 && slice.y < minimumValue) {
                                slice.y = minimumValue;
                            }
                        });
                    };
                }
                callToMake = self.$http.post(url, { id: self.id });
                self.GlobalServices.ClearErrors();
                self.Loading(callToMake);
                callToMake
                    .then(function (response) {
                    self.device = response.data.responseObj;
                    callback && callback();
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                });
            }
        };
        DeviceController.prototype.updateSystemUsage = function () {
            var self = this;
            self.systemUsageHistoricalData = self.getLineChartObject(self.usages.system);
        };
        DeviceController.prototype.updateCPUUsage = function () {
            var self = this;
            self.cpuUsageHistoricalData = self.getLineChartObject(self.usages.cpu);
        };
        DeviceController.prototype.updateMemoryUsage = function () {
            var self = this;
            self.memoryUsageHistoricalData = self.getLineChartObject(self.usages.ram);
        };
        DeviceController.prototype.updateIOUsage = function () {
            var self = this;
            self.ioUsageHistoricalData = self.getLineChartObject(self.usages.io);
        };
        //pie with usage data
        DeviceController.prototype.getPieChartObject = function (UsageData, customOptions) {
            var self = this;
            return angular.merge(self.getGenericPie(customOptions), {
                series: [
                    {
                        data: [
                            { name: self.$translate.instant('DEVICEDETAILS_CurrentUtilization'), y: Math.round(parseFloat(UsageData.currentvalue)), color: UsageData.currentvalue <= UsageData.threshold ? '#7AB800' : '#f0ad4e' },
                            { name: self.$translate.instant('DEVICEDETAILS_CurrentAvailable'), y: Math.round(100 - UsageData.currentvalue), color: '#ccc' }
                        ]
                    }
                ]
            });
        };
        DeviceController.prototype.getGenericPie = function (customOptions) {
            return {
                options: {
                    chart: {
                        type: 'pie',
                        animation: false
                    },
                    title: {
                        text: ''
                    },
                    plotOptions: {
                        pie: {
                            allowPointSelect: false,
                            innerSize: 100,
                            dataLabels: { enabled: false },
                            borderWidth: 3
                        },
                        series: {
                            states: {
                                hover: {
                                    enabled: false
                                }
                            }
                        }
                    }
                },
                size: {
                    height: 170,
                    width: 170
                },
                spacingBottom: 0,
                spacingTop: 0,
                spacingLeft: 0,
                spacingRight: 0,
                series: [{
                        data: [],
                        innerSize: customOptions && customOptions.innerSize || '70%'
                    }]
            };
        };
        DeviceController.prototype.getLineChartObject = function (UsageDataSeries) {
            var self = this;
            return {
                options: {
                    chart: {
                        type: 'area',
                        animation: true,
                        height: 130,
                        borderWidth: 0,
                        backgroundColor: 'rgba(255, 255, 255, 0)'
                    },
                    title: {
                        text: ''
                    },
                    tooltip: {
                        useHTML: true,
                        shared: true,
                        enabled: true,
                        formatter: function () {
                            return "Value: " + parseFloat(this.y.toFixed(2)) + "%<br />Date: " + moment(this.x).format('LLL');
                        }
                    },
                },
                xAxis: {
                    labels: { enabled: false },
                    type: 'datetime',
                    //                    tickPositioner: () => {
                    //                        return _.map(UsageDataSeries.data, (point: any) => [
                    //                            moment(point.timestamp).toDate().getTime(),
                    //                            point.value
                    //                        ]
                    //                        );
                    //                    },
                    lineColor: '#ececec',
                    tickColor: '#ececec',
                    title: { margin: 10, style: { color: '#888888' }, text: UsageDataSeries.chartlabel }
                },
                yAxis: {
                    gridLineColor: "#cecece",
                    title: null,
                    tickInterval: 20,
                    plotLines: [{
                            value: 0,
                            width: 1,
                            color: '#808080'
                        }],
                    min: 0,
                    max: 100
                },
                legend: {
                    enabled: false
                },
                series: [{
                        showInLegend: false,
                        name: 'Usage',
                        color: "#007db8",
                        data: self.convertLineChartData(UsageDataSeries.data),
                        fillColor: {
                            linearGradient: [0, 0, 0, 75],
                            stops: [
                                [0, self.setOpacity(Highcharts.Color(Highcharts.getOptions().colors[0]), 0.3)],
                                [1, self.setOpacity(Highcharts.Color(Highcharts.getOptions().colors[0]), 0)]
                            ]
                        },
                        marker: {
                            radius: 2
                        }
                    }],
                spacingBottom: 0,
                spacingTop: 0,
                spacingLeft: 0,
                spacingRight: 0
            };
        };
        //laundering type to prevent build-breaking
        DeviceController.prototype.setOpacity = function (highChartsColor, opacity) {
            return highChartsColor.setOpacity(opacity).get("rgba");
        };
        DeviceController.prototype.convertLineChartData = function (data) {
            return _.map(data, function (point) { return [
                //moment(point.timestamp).toDate().getTime(),
                moment(point.timestamp, 'YYYY-MM-DDTHH:mm:ss').toDate().getTime(),
                point.value
            ]; });
        };
        DeviceController.prototype.openFirmwareReport = function (deviceId) {
            var self = this;
            //self.pauseRefreshDevices = true;
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
                    //self.pauseRefreshDevices = false;
                    //self.refresh();
                }
            });
            firmwareReportModal.modal.show();
        };
        DeviceController.prototype.launchGUI = function () {
            var self = this;
            window.open(self.device.ipaddressurl);
        };
        DeviceController.prototype.isCurrentTab = function (tab) {
            var self = this;
            return self.activeTab === tab;
        };
        DeviceController.prototype.setTab = function (tab) {
            var self = this;
            self.activeTab = tab;
        };
        DeviceController.prototype.hideTabView = function () {
            var self = this;
            return self.isIOM() || self.isDellSwitch() || self.isCiscoSwitch();
        };
        DeviceController.prototype.isChassis = function () {
            return (this.deviceType === 'ChassisM1000e' || this.deviceType === 'ChassisVRTX' || this.deviceType === 'ChassisFX');
        };
        DeviceController.prototype.isFX2 = function () {
            return (this.deviceType === 'ChassisFX');
        };
        DeviceController.prototype.isIOM = function () {
            return (this.deviceType === 'AggregatorIOM' || this.deviceType === 'MXLIOM' || this.deviceType === 'FXIOM');
        };
        DeviceController.prototype.isServer = function () {
            return (this.deviceType === 'RackServer' || this.deviceType === 'TowerServer' || this.deviceType === 'BladeServer' || this.deviceType === 'FXServer' || this.deviceType === 'Server');
        };
        DeviceController.prototype.isEqualLogic = function () {
            return (this.deviceType === 'equallogic');
        };
        DeviceController.prototype.isCompellent = function () {
            return (this.deviceType === 'compellent');
        };
        DeviceController.prototype.isEmcUnity = function () {
            return this.deviceType === "emcunity";
        };
        DeviceController.prototype.isEmcvnx = function () {
            return this.deviceType === "emcvnx";
        };
        DeviceController.prototype.isNetApp = function () {
            return (this.deviceType === 'netapp');
        };
        DeviceController.prototype.isDellSwitch = function () {
            return (this.deviceType === 'dellswitch' || this.deviceType === 'genericswitch');
        };
        DeviceController.prototype.isCiscoSwitch = function () {
            return (this.deviceType === 'ciscoswitch');
        };
        DeviceController.prototype.isScaleIO = function () {
            return this.deviceType === 'scaleio';
        };
        DeviceController.prototype.isEm = function () {
            return this.deviceType === 'em';
        };
        DeviceController.$inject = ['$http', '$translate', '$routeParams', 'GlobalServices', '$timeout', 'constants', "Commands", "Loading", "$q", "$filter", "Modal"];
        return DeviceController;
    }());
    asm.DeviceController = DeviceController;
    angular
        .module("app")
        .controller("DeviceController", DeviceController);
})(asm || (asm = {}));
//# sourceMappingURL=device.js.map
