var asm;
(function (asm) {
    "use strict";
    var DevicePerformanceController = (function () {
        function DevicePerformanceController($http, $interval, $translate, Commands, GlobalServices, Loading) {
            this.$http = $http;
            this.$interval = $interval;
            this.$translate = $translate;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.Loading = Loading;
            this.noData = false;
            this.emptyArray = [];
            var self = this;
            self.timeFrames = [
                { id: "LastHour", name: self.$translate.instant("DEVICES_EMC_SCALEIO_LastHour"), index: 0 },
                { id: "LastDay", name: self.$translate.instant("DEVICES_EMC_SCALEIO_LastDay"), index: 1 },
                { id: "LastWeek", name: self.$translate.instant("DEVICES_EMC_SCALEIO_LastWeek"), index: 2 },
                { id: "LastMonth", name: self.$translate.instant("DEVICES_EMC_SCALEIO_LastMonth"), index: 3 },
                { id: "LastYear", name: self.$translate.instant("DEVICES_EMC_SCALEIO_LastYear"), index: 4 },
            ];
            self.iopSelectedTimeFrame = 0;
            self.bandWidthSelectedTimeFrame = 0;
            //holds line charts
            self.usages = {
                iop: {
                    total: undefined,
                    read: undefined,
                    write: undefined
                },
                bandwidth: {
                    total: undefined,
                    read: undefined,
                    write: undefined
                }
            };
            //empty data sets to init the highcharts with when there is no data
            self.emptyArray = [
                { chartlabel: self.$translate.instant("DEVICES_EMC_SCALEIO_Minutes") },
                { chartlabel: self.$translate.instant("DEVICES_EMC_SCALEIO_Hours") },
                { chartlabel: self.$translate.instant("DEVICES_EMC_SCALEIO_Days") },
                { chartlabel: self.$translate.instant("DEVICES_EMC_SCALEIO_Days") },
                { chartlabel: self.$translate.instant("DEVICES_EMC_SCALEIO_Months") }];
            self.emptyDataSets = {
                total: { historicaldata: self.emptyArray },
                read: { historicaldata: self.emptyArray },
                write: { historicaldata: self.emptyArray },
            };
            self.activate();
        }
        DevicePerformanceController.prototype.activate = function () {
            var self = this;
            self.refreshWorkLoads();
            if (!self.device) {
                self.device = {};
            }
            self.refreshIOP();
            self.refreshBandwidth();
        };
        DevicePerformanceController.prototype.refresh = function () {
            var self = this;
            self.GlobalServices.ClearErrors();
            self.Loading(self.getScaleIOById(self.device.id)
                .then(function (response) {
                self.device = response.data.responseObj;
                self.noData = false;
                self.refreshIOP();
                self.refreshBandwidth();
            })
                .catch(function (data) {
                self.device.ioData = self.emptyDataSets;
                self.device.bandwidthData = self.emptyDataSets;
                self.GlobalServices.DisplayError(data.data);
            }));
        };
        DevicePerformanceController.prototype.$onDestroy = function () {
            var self = this;
            if (self.workloadsInterval)
                self.$interval.cancel(self.workloadsInterval);
        };
        DevicePerformanceController.prototype.refreshWorkLoads = function () {
            var self = this;
            if (self.workloadsInterval) {
                self.$interval.cancel(self.workloadsInterval);
            }
            self.workloadsInterval = self.$interval(function () {
                self.pollWorkloads && self.getScaleIOWorkload(self.device.id)
                    .then(function (response) {
                    self.workload = response.data.responseObj;
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                });
            }, 5000);
        };
        DevicePerformanceController.prototype.refreshIOP = function () {
            var self = this;
            if (!self.device.ioData) {
                self.noData = false;
                self.device.ioData = self.emptyDataSets;
            }
            self.checkForNullHistoricalData(self.device.ioData);
            angular.extend(self.usages.iop, {
                total: self.getLineChartObject(self.device.ioData.total.historicaldata[self.iopSelectedTimeFrame], self.device.ioData.total.category),
                read: self.getLineChartObject(self.device.ioData.read.historicaldata[self.iopSelectedTimeFrame], self.device.ioData.read.category),
                write: self.getLineChartObject(self.device.ioData.write.historicaldata[self.iopSelectedTimeFrame], self.device.ioData.write.category)
            });
        };
        DevicePerformanceController.prototype.refreshBandwidth = function () {
            var self = this;
            if (!self.device.bandwidthData) {
                self.noData = true;
                self.device.bandwidthData = self.emptyDataSets;
            }
            self.checkForNullHistoricalData(self.device.bandwidthData);
            angular.extend(self.usages.bandwidth, {
                total: self.getLineChartObject(self.device.bandwidthData.total.historicaldata[self.bandWidthSelectedTimeFrame], self.device.bandwidthData.total.category),
                read: self.getLineChartObject(self.device.bandwidthData.read.historicaldata[self.bandWidthSelectedTimeFrame], self.device.bandwidthData.read.category),
                write: self.getLineChartObject(self.device.bandwidthData.write.historicaldata[self.bandWidthSelectedTimeFrame], self.device.bandwidthData.write.category)
            });
        };
        DevicePerformanceController.prototype.checkForNullHistoricalData = function (context) {
            //handles the case where ioData/bandwidthData is sent, but comes with null historical data
            var self = this;
            if (!context.total.historicaldata) {
                context.total.historicaldata = self.emptyArray;
            }
            if (!context.read.historicaldata) {
                context.read.historicaldata = self.emptyArray;
            }
            if (!context.write.historicaldata) {
                context.write.historicaldata = self.emptyArray;
            }
        };
        DevicePerformanceController.prototype.getLineChartObject = function (UsageDataSeries, label) {
            var self = this;
            return {
                options: {
                    chart: {
                        type: 'line',
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
                            return self.formatTooltip(this, UsageDataSeries, label);
                        }
                    },
                },
                xAxis: {
                    labels: { enabled: false },
                    type: 'datetime',
                    categories: angular.forEach(UsageDataSeries.data, function (point) { return point.timestamp; }),
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
                    max: UsageDataSeries.maximumvalue || 100
                },
                legend: {
                    enabled: false
                },
                series: [{
                        showInLegend: false,
                        name: 'Usage',
                        color: "#81b8e2",
                        data: UsageDataSeries.data ? UsageDataSeries.data.map(function (dataPoint) { return dataPoint.value; }) : [],
                        marker: {
                            radius: 3,
                            fillColor: '#666666'
                        }
                    }],
                spacingBottom: 0,
                spacingTop: 0,
                spacingLeft: 0,
                spacingRight: 0
            };
        };
        DevicePerformanceController.prototype.formatTooltip = function (context, UsageDataSeries, label) {
            var toReturn = moment(context.x.timestamp).format('LLL') + " <br /> <label>" + label + ": " + parseFloat(context.y.toFixed(2)) + "</label>";
            return toReturn;
        };
        DevicePerformanceController.prototype.getScaleIOWorkload = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.scaleIO.getScaleIOWorkload, { id: id });
        };
        DevicePerformanceController.prototype.getScaleIOById = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.scaleIO.getScaleIObyId, { id: id });
        };
        DevicePerformanceController.$inject = ["$http", "$interval", "$translate", "Commands", "GlobalServices", "Loading"];
        return DevicePerformanceController;
    }());
    angular.module('app')
        .component('devicePerformance', {
        templateUrl: 'views/devicedetails/performance.html',
        controller: DevicePerformanceController,
        controllerAs: 'devicePerformanceController',
        bindings: {
            device: '=',
            deviceType: '=',
            pollWorkloads: '<'
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=performance.js.map
