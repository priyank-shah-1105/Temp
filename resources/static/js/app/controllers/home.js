var asm;
(function (asm) {
    var HomeController = (function () {
        function HomeController($http, $filter, $translate, $timeout, GlobalServices, Modal, $location, dialog, Commands, $q, $rootScope) {
            this.$http = $http;
            this.$filter = $filter;
            this.$translate = $translate;
            this.$timeout = $timeout;
            this.GlobalServices = GlobalServices;
            this.Modal = Modal;
            this.$location = $location;
            this.dialog = dialog;
            this.Commands = Commands;
            this.$q = $q;
            this.$rootScope = $rootScope;
            this.popoverbuttontext = 'Test';
            this.wsMessages = [];
            var self = this;
            self.loadingServerUtilization = true;
            self.loadingServerHealth = true;
            self.viewByOptions = [
                { id: 'lastTenDays', name: self.$translate.instant('DASHBOARD_LastTen') },
                { id: 'lastWeek', name: self.$translate.instant('DASHBOARD_LastWeek') },
                { id: 'lastMonth', name: self.$translate.instant('DASHBOARD_LastMonth') },
                { id: 'lastSixMonths', name: self.$translate.instant('DASHBOARD_LastSixMonths') },
                { id: 'lastYear', name: self.$translate.instant('DASHBOARD_LastYear') }
            ];
            self.config = {};
            self.dashboardRequest = {
                deploymentTemplates: [],
                totalServersInDeployments: 0,
                totalServersAvailable: 0,
                totalChassisAvailable: 0,
                totalIOModAvailable: 0,
                totalIpAddressesAvailable: 0,
                chassisdiscovered: 0,
                serversdiscovered: 0,
                switchesdiscovered: 0,
                storagediscovered: 0,
                licenseData: {
                    id: '',
                    licensefile: '',
                    type: 'Perpetual',
                    totalnodes: 100,
                    usednodes: 0,
                    availablenodes: 0,
                    activationdate: '',
                    expirationdate: new Date(),
                    softwareservicetag: '',
                    currentWizardStep: 0,
                    isValid: true,
                    signature: '',
                    warningmessage: '',
                    expired: false,
                    expiressoon: false,
                    expiressoonmessage: '',
                    force: false
                }
            };
            self.serverHealth = {
                green: 0,
                unknown: 0,
                yellow: 0,
                red: 0
            };
            self.parseServerPoolsProxy = function (data) {
                self.parseServerPools(data);
            };
            self.licenseDataLoaded = false;
            self.isDeployServiceEnabled = false;
            //set default values
            self.gettingStarted = {
                initialConfigurationCompleted: true,
                firmwareUpdateCompleted: true,
                inventoryUpdateCompleted: true
            };
        }
        HomeController.prototype.activate = function () {
            var self = this;
            self.$rootScope.$on('$locationChangeStart', function (a, destination, c) {
                //if actually changing routes. 
                //note: when / is hit, this will fire and change route to /home, this condition checks if route is changing away from home
                if (destination.indexOf("home") === -1) {
                    self.$timeout.cancel(self.gaugeTimeout);
                }
            });
            self.setGauge();
            //service overview config
            self.config = {
                options: {
                    chart: {
                        type: 'pie',
                        renderTo: 'serviceOverviewPie',
                        animation: false,
                        backgroundColor: "none"
                    },
                    title: {
                        text: ''
                    },
                    tooltip: {
                        enabled: true,
                        formatter: function () { return self.getServicesTooltip(this.point.status, this.y); },
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
                            point: {
                                events: {
                                    click: function (evt) {
                                        var _this = this;
                                        self.$timeout(function () {
                                            self.wedge = _this.status;
                                        });
                                    }
                                }
                            }
                        }
                    }
                },
                size: {
                    height: 260,
                    width: 260
                },
                spacingBottom: 0,
                spacingTop: 0,
                spacingLeft: 0,
                spacingRight: 0,
                series: [{
                        data: [],
                        innerSize: '85%'
                    }]
            };
            //server health config
            self.serverHealthConfig = {
                options: {
                    chart: {
                        type: 'pie',
                        renderTo: 'serverHealthPie',
                        animation: false
                    },
                    title: {
                        text: ''
                    },
                    tooltip: {
                        enabled: true,
                        formatter: function () { return self.getServersTooltip(this.point._state, this.y); },
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
                            point: {
                                events: {
                                    click: function (evt) {
                                        var _this = this;
                                        self.$timeout(function () {
                                            self.$location.path("devices/server/" + _this._state);
                                        });
                                    }
                                }
                            }
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
                        data: [],
                        innerSize: '90%'
                    }]
            };
            //server utilization
            self.serverUtilizationConfig = {
                options: {
                    chart: {
                        type: 'pie',
                        renderTo: 'serverUtilizationPie',
                        animation: false
                    },
                    title: {
                        text: ''
                    },
                    tooltip: {
                        enabled: true,
                        formatter: function () { return '<b>' + this.key + ': </b>' + this.y; },
                        useHTML: true,
                        style: { zIndex: 99999 },
                        positioner: function (labelWidth, labelHeight, point) { return { x: point.plotX + 10, y: point.plotY + 10 }; }
                    },
                    plotOptions: {
                        pie: {
                            allowPointSelect: false,
                            size: '100%',
                            //innerSize: '75%',
                            dataLabels: { enabled: false },
                            states: { select: { borderColor: '#0085c3' } },
                            borderWidth: 3,
                            showInLegend: false
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
                        data: [],
                        innerSize: '90%'
                    }]
            };
            //Storage Capacity
            self.scaleIOConfig = {
                options: {
                    chart: {
                        type: 'pie',
                        renderTo: 'dashboardStorageCapacityPie',
                        animation: false
                    },
                    title: {
                        text: ''
                    },
                    plotOptions: {
                        pie: {
                            allowPointSelect: false,
                            size: '100%',
                            //innerSize: '75%',
                            dataLabels: { enabled: false },
                            states: { select: { borderColor: '#0085c3' } },
                            borderWidth: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: function () {
                            return "<b>" + this.key + ": </b>" + Math.round(this.percentage) + "%";
                        },
                        useHTML: true,
                        style: { zIndex: 99999 },
                        positioner: function (labelWidth, labelHeight, point) { return { x: point.plotX + 10, y: point.plotY + 10 }; }
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
                        data: [],
                        innerSize: '90%'
                    }]
            };
            //base server pool usage bar - possible service
            self.serverPoolUsageBaseConfig = {
                options: {
                    chart: {
                        type: 'bar',
                        animation: false,
                        style: { 'font-family': 'inherit', },
                        height: 30,
                        spacingTop: 0,
                        spacingBottom: 0,
                        margin: [0, 0, 0, 0]
                    },
                    title: {
                        text: ''
                    },
                    plotOptions: {
                        bar: {
                            allowPointSelect: true,
                            size: '100%',
                            dataLabels: { enabled: false },
                            states: { select: { borderColor: '#0085c3' } },
                            borderWidth: 3,
                            stacking: 'percent'
                        }
                    },
                    legend: { enabled: false },
                    xAxis: { labels: { enabled: false }, gridLineWidth: 0, lineColor: 'transparent', tickLength: 0 },
                    yAxis: { labels: { enabled: false }, gridLineWidth: 0, lineColor: 'transparent', tickLength: 0, title: '' },
                    tooltip: { enabled: false }
                },
                margin: 0,
                padding: 0,
                series: []
            };
            self.serverPoolUsageConfigs = [];
            self.refresh('all');
        };
        ;
        //Reusable refresh method that we can put on a timeout
        HomeController.prototype.refresh = function (mode) {
            var self = this, servicesDataCriteriaObj = { paginationObj: { rowCountPerPage: undefined }, filterObj: {} }, genericPieSlice = { sliced: false, selected: false }, request = { requestObj: [], criteriaObj: {} };
            self.loadingServiceOverview = true;
            var filterObj = [];
            if (self.viewBy) {
                switch (self.viewBy) {
                    case "lastTenDays":
                        servicesDataCriteriaObj.paginationObj.rowCountPerPage = 10;
                        filterObj = [];
                        break;
                    case "lastWeek":
                        var week = moment().subtract('week', 6).toISOString();
                        servicesDataCriteriaObj.paginationObj.rowCountPerPage = 9999;
                        filterObj.push({ field: 'deployedOn', op: '>=', opTarget: [week] });
                        break;
                    case "lastMonth":
                        var month = moment().subtract('month', 1).toISOString();
                        servicesDataCriteriaObj.paginationObj.rowCountPerPage = 9999;
                        filterObj.push({ field: 'deployedOn', op: '>=', opTarget: [month] });
                        break;
                    case "lastSixMonths":
                        var sixmonths = moment().subtract('month', 6).toISOString();
                        servicesDataCriteriaObj.paginationObj.rowCountPerPage = 9999;
                        filterObj.push({ field: 'deployedOn', op: '>=', opTarget: [sixmonths] });
                        break;
                    case "lastYear":
                        var oneyear = moment().subtract('year', 1).toISOString();
                        servicesDataCriteriaObj.paginationObj.rowCountPerPage = 9999;
                        filterObj.push({ field: 'deployedOn', op: '>=', opTarget: [oneyear] });
                        break;
                    default:
                        servicesDataCriteriaObj.paginationObj.rowCountPerPage = 9999;
                        break;
                }
            }
            servicesDataCriteriaObj.filterObj = filterObj;
            request.criteriaObj = servicesDataCriteriaObj;
            self.$q.all([
                self.$http.post(self.Commands.data.dashboard.getServicesDashboardData, {
                    servicecancelledcount: 0,
                    servicecount: 0,
                    servicecriticalcount: 0,
                    servicesuccesscount: 0,
                    serviceunknowncount: 0,
                    servicewarningcount: 0,
                    servicependingcount: 0,
                    serviceservicemodecount: 0,
                    serviceincompletecount: 0
                })
                    .then(function (data) {
                    self.servicesData = data.data.responseObj;
                    //temporary fix until api is updated
                    if (!self.servicesData.serviceservicemodecount) {
                        self.servicesData.serviceservicemodecount = 0;
                    }
                    self.wedge = self.getDefaultWedgeColor(self.servicesData, self.wedge);
                    self.config.series[0].data = [
                        angular.extend({}, genericPieSlice, {
                            y: 0,
                            color: "#eeeeee",
                            name: 'none',
                            status: "none",
                        }),
                        angular.extend({}, genericPieSlice, {
                            y: self.servicesData.servicesuccesscount,
                            color: "#7AB800",
                            name: self.$translate.instant("DASHBOARD_TOOLTIP_Health_Healthy"),
                            status: "green"
                        }),
                        angular.extend({}, genericPieSlice, {
                            y: self.servicesData.serviceunknowncount,
                            color: "#0085c3",
                            name: self.$translate.instant("DASHBOARD_TOOLTIP_Health_InProgress"),
                            status: "unknown"
                        }),
                        angular.extend({}, genericPieSlice, {
                            y: self.servicesData.servicependingcount,
                            color: '#cccccc',
                            name: self.$translate.instant("DASHBOARD_TOOLTIP_Health_Pending"),
                            status: "pending"
                        }),
                        angular.extend({}, genericPieSlice, {
                            y: self.servicesData.servicewarningcount,
                            color: "#efb106",
                            name: self.$translate.instant("DASHBOARD_TOOLTIP_Health_Warning"),
                            status: "yellow"
                        }),
                        angular.extend({}, genericPieSlice, {
                            y: self.servicesData.servicecriticalcount,
                            color: "#d00e28",
                            name: self.$translate.instant("DASHBOARD_TOOLTIP_Health_Critical"),
                            status: "red"
                        }),
                        angular.extend({}, genericPieSlice, {
                            y: self.servicesData.serviceservicemodecount,
                            color: "#efad18",
                            name: self.$translate.instant("DASHBOARD_TOOLTIP_Health_ServiceMode"),
                            status: "servicemode"
                        }),
                        angular.extend({}, genericPieSlice, {
                            y: self.servicesData.servicecancelledcount,
                            color: "#333333",
                            name: self.$translate.instant("DASHBOARD_TOOLTIP_Health_Cancelled"),
                            status: "cancelled"
                        }),
                        angular.extend({}, genericPieSlice, {
                            y: self.servicesData.serviceincompletecount,
                            color: "#EE6411",
                            name: self.$translate.instant("DASHBOARD_TOOLTIP_Health_Incomplete"),
                            status: "incomplete"
                        })
                    ];
                    if (!self.servicesData.servicecount) {
                        self.config.series[0].data[0].y = 1;
                    }
                }),
                self.$http.post(self.Commands.data.services.getServiceList, request)
                    .then(function (data) {
                    self.services = data.data.responseObj;
                }),
                self.$http.post(self.Commands.data.dashboard.getDashboardScaleIOData, {})
                    .then(function (data) {
                    self.scaleIOData = data.data.responseObj;
                    angular.forEach(self.scaleIOData.storageComponents, function (component) {
                        if (component.usedInKb === null) {
                            component.usedInKb = 0;
                        }
                        if (component.totalInKb === null || component.totalInKb === 0) {
                            component.totalInKb = 1;
                        }
                    });
                    self.setScaleIOConfig();
                })
            ])
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            })
                .finally(function () {
                self.loadingServiceOverview = false;
            });
            if (mode == "all") {
                self.loadingServerUtilization = true;
                self.loadingServerHealth = true;
                self.loadingStorageCapacity = true;
                self.loadingNotifications = true;
                self.$q.all([
                    self.$http.post(self.Commands.data.dashboard.getDashboardLandingPageData, self.dashboardRequest)
                        .then(function (data) {
                        self.dashboard = data.data.responseObj;
                        self.licenseDataLoaded = true;
                        //JUST FOR LICENSE TESTING
                        //                        self.dashboard.licenseData.availablenodes = 0;
                        //                        self.dashboard.licenseData.expiressoon = true;
                    }),
                    self.$http.post(self.Commands.data.serverpools.getServerPools, null)
                        .then(function (data) {
                        self.parseServerPoolsProxy(data);
                    }),
                    self.$http.post(self.Commands.data.dashboard.getDashboardStorageData, null)
                        .then(function (data) {
                        self.storageUtilization = data.data.responseObj;
                        self.loadingStorageCapacity = false;
                    }),
                    self.$http.post(self.Commands.data.templates.getQuickTemplateList, null)
                        .then(function (data) {
                        var publishedTemplate = false;
                        self.templates = data.data.responseObj.forEach(function (template) {
                            if (!template.draft)
                                publishedTemplate = true;
                        });
                        self.isDeployServiceEnabled = publishedTemplate;
                    })
                        .catch(function () { return self.isDeployServiceEnabled = true; }),
                    self.$http.post(self.Commands.data.dashboard.getDashboardNotifications, null)
                        .then(function (data) {
                        self.dashboardNotifications = data.data.responseObj;
                        self.loadingNotifications = false;
                    })
                        .catch(function () { return self.loadingNotifications = false; }),
                    self.$http.post(self.Commands.data.initialSetup.gettingStarted, null)
                        .then(function (data) {
                        //Show navmenu when initial setup is completed instead of checking all steps - Requested by Donna                            
                        //if (response.initialSetupCompleted && response.discoveryCompleted && response.templateCompleted && response.networksCompleted && response.configurationCompleted) {
                        self.gettingStarted = data.data.responseObj;
                    })
                ]).then(function () { })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                });
            }
        };
        HomeController.prototype.setScaleIOConfig = function (scaleIO) {
            var self = this;
            var used, available;
            self.scaleIOConfig.series[0].data = [];
            if (scaleIO) {
                self.scaleIODetailsLoading = true;
                self.selectedStorageDisplay = {};
                self.$http.post(self.Commands.data.scaleIO.getScaleIObyId, { id: scaleIO.id })
                    .then(function (response) {
                    angular.extend(self.selectedStorageDisplay, response.data.responseObj);
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                })
                    .finally(function () { return self.scaleIODetailsLoading = false; });
                used = scaleIO.usedInKb;
                available = scaleIO.totalInKb - used;
                self.scaleIODonutPercent = Math.round((scaleIO.usedInKb / scaleIO.totalInKb) * 100) || 0;
            }
            else {
                if (self.scaleIOData.usedInKb === null) {
                    self.scaleIOData.usedInKb = 0;
                }
                if (self.scaleIOData.totalInKb === null || self.scaleIOData.totalInKb === 0) {
                    self.scaleIOData.totalInKb = 1;
                }
                self.selectedStorageDisplay = null;
                used = self.scaleIOData.usedInKb;
                available = self.scaleIOData.totalInKb - self.scaleIOData.usedInKb;
                self.scaleIODonutPercent = Math.round((self.scaleIOData.usedInKb / self.scaleIOData.totalInKb) * 100) || 0;
            }
            self.scaleIOConfig.series[0].data.push({ name: self.$translate.instant("DASHBOARD_StorageUsed"), y: used, color: '#0685C2', percentage: self.scaleIODonutPercent }, { name: self.$translate.instant("DASHBOARD_StorageAvailable"), y: available, color: '#CCCCCC', percentage: 100 - self.scaleIODonutPercent });
        };
        HomeController.prototype.getDefaultWedgeColor = function (servicesData, currentSelection) {
            var options = [
                { name: "red", value: servicesData.servicecriticalcount },
                { name: "yellow", value: servicesData.servicewarningcount },
                { name: "pending", value: servicesData.servicependingcount },
                { name: "unknown", value: servicesData.serviceunknowncount },
                { name: "green", value: servicesData.servicesuccesscount },
                { name: "servicemode", value: servicesData.serviceservicemodecount },
                { name: "cancelled", value: servicesData.servicecancelledcount },
                { name: "incomplete", value: servicesData.serviceincompletecount },
                { name: "none", value: 1 },
            ];
            var match = _.find(options, { name: currentSelection });
            if (currentSelection && match && match.value) {
                //if current selection matches one with > 0 
                return currentSelection;
            }
            else {
                //return first in options where value > 1
                var firstNotEmpty = _.find(options, function (option) { return option.value; });
                return firstNotEmpty ? firstNotEmpty.name : "";
            }
        };
        HomeController.prototype.getServicesTooltip = function (status, count) {
            var self = this;
            switch (status) {
                case "green":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Healthy", { count: count });
                case "unknown":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_InProgress", { count: count });
                case "pending":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Pending", { count: count });
                case "yellow":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Warning", { count: count });
                case "red":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Critical", { count: count });
                case "cancelled":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Cancelled", { count: count });
                case "none":
                    return self.$translate.instant("DASHBOARD_NoServices");
                case "servicemode":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Server_Health_ServiceMode", { count: count });
                case "incomplete":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Server_Health_Incomplete", { count: count });
                default:
                    return self.$translate.instant("DASHBOARD_TOOLTIP_InProgress", { count: count });
            }
        };
        HomeController.prototype.getServersTooltip = function (status, count) {
            var self = this;
            switch (status) {
                case "green":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Server_Health_Healthy", { count: count });
                case "yellow":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Server_Health_Warning", { count: count });
                case "red":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Server_Health_Critical", { count: count });
                case "servicemode":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Server_Health_ServiceMode", { count: count });
                case "cancelled":
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Server_Health_Unknown", { count: count });
                default:
                    return self.$translate.instant("DASHBOARD_TOOLTIP_Server_Health_Unknown", { count: count });
            }
        };
        HomeController.prototype.parseServerPools = function (response) {
            var self = this;
            var data = response.data.responseObj;
            var serverHealth = {
                green: 0,
                yellow: 0,
                red: 0,
                unknown: 0,
                servicemode: 0,
                total: 0
            };
            var serverUtilization = {
                available: 0,
                deployed: 0,
                pending: 0,
                unknown: 0,
                deploying: 0,
                errors: 0,
                poweringoff: 0,
                poweringon: 0,
                reserved: 0,
                totalavailable: 0,
                totalinuse: 0,
                totalservers: 0,
                pctavailable: 0,
                pctinuse: 0,
                serverpools: [],
                currentview: 'inuse'
            };
            var servers = [];
            //foreach serverpool
            data.forEach(function (pool) {
                var serverpool = {
                    id: pool.id,
                    name: pool.name,
                    available: 0,
                    deployed: 0,
                    pending: 0,
                    unknown: 0,
                    deploying: 0,
                    errors: 0,
                    poweringoff: 0,
                    poweringon: 0,
                    reserved: 0,
                    totalavailable: 0,
                    totalinuse: 0,
                    totalservers: 0
                };
                pool.servers.forEach(function (server) {
                    var newServer = false;
                    if (servers.indexOf(server.id) == -1) {
                        servers.push(server.id);
                        newServer = true;
                    }
                    if (newServer) {
                        switch (server.health.toLowerCase()) {
                            case "green":
                                serverHealth.green++;
                                break;
                            case "yellow":
                                serverHealth.yellow++;
                                break;
                            case "servicemode":
                                serverHealth.servicemode++;
                                break;
                            case "red":
                                serverHealth.red++;
                                break;
                            default:
                                serverHealth.unknown++;
                                break;
                        }
                        serverHealth.total++;
                    }
                    //switch (server.state) {
                    //    case 'available':
                    //        serverpool.available++;
                    //        serverpool.totalavailable++;
                    //        serverpool.totalservers++;
                    //        if (newServer) {
                    //            serverUtilization.available++;
                    //            serverUtilization.totalavailable++;
                    //            serverUtilization.totalservers++;
                    //        }
                    //        break;
                    //    case 'deployed':
                    //        serverpool.deployed++;
                    //        serverpool.totalinuse++;
                    //        serverpool.totalservers++;
                    //        if (newServer) {
                    //            serverUtilization.deployed++;
                    //            serverUtilization.totalinuse++;
                    //            serverUtilization.totalservers++;
                    //        }
                    //        break;
                    //    case 'pending':
                    //        serverpool.pending++;
                    //        serverpool.totalavailable++;
                    //        serverpool.totalservers++;
                    //        if (newServer) {
                    //            serverUtilization.pending++;
                    //            serverUtilization.totalavailable++;
                    //            serverUtilization.totalservers++;
                    //        }
                    //        break;
                    //    case 'deploying':
                    //        serverpool.deploying++;
                    //        serverpool.totalinuse++;
                    //        serverpool.totalservers++;
                    //        if (newServer) {
                    //            serverUtilization.deploying++;
                    //            serverUtilization.totalinuse++;
                    //            serverUtilization.totalservers++;
                    //        }
                    //        break;
                    //    case 'errors':
                    //        serverpool.errors++;
                    //        serverpool.totalinuse++;
                    //        serverpool.totalservers++;
                    //        if (newServer) {
                    //            serverUtilization.errors++;
                    //            serverUtilization.totalinuse++;
                    //            serverUtilization.totalservers++;
                    //        }
                    //        break;
                    //    case 'poweringoff':
                    //        serverpool.poweringoff++;
                    //        serverpool.totalavailable++;
                    //        serverpool.totalservers++;
                    //        if (newServer) {
                    //            serverUtilization.poweringoff++;
                    //            serverUtilization.totalavailable++;
                    //            serverUtilization.totalservers++;
                    //        }
                    //        break;
                    //    case 'poweringon':
                    //        serverpool.poweringon++;
                    //        serverpool.totalavailable++;
                    //        serverpool.totalservers++;
                    //        if (newServer) {
                    //            serverUtilization.poweringon++;
                    //            serverUtilization.totalavailable++;
                    //            serverUtilization.totalservers++;
                    //        }
                    //        break;
                    //    case 'reserved':
                    //        serverpool.reserved++;
                    //        serverpool.totalinuse++;
                    //        serverpool.totalservers++;
                    //        if (newServer) {
                    //            serverUtilization.reserved++;
                    //            serverUtilization.totalinuse++;
                    //            serverUtilization.totalservers++;
                    //        }
                    //        break;
                    //    case 'unknown':
                    //    default: //By default, assume this server is unavailable and in an unknown state.
                    //        serverpool.unknown++;
                    //        serverpool.totalavailable++;
                    //        serverpool.totalservers++;
                    //        if (newServer) {
                    //            serverUtilization.unknown++;
                    //            serverUtilization.totalavailable++;
                    //            serverUtilization.totalservers++;
                    //        }
                    //        break;
                    //}
                    switch (server.availability) {
                        case 'notinuse':
                            serverpool.available++;
                            serverpool.totalavailable++;
                            serverpool.totalservers++;
                            if (newServer) {
                                serverUtilization.available++;
                                serverUtilization.totalavailable++;
                                serverUtilization.totalservers++;
                            }
                            break;
                        case 'inuse':
                            serverpool.deployed++;
                            serverpool.totalinuse++;
                            serverpool.totalservers++;
                            if (newServer) {
                                serverUtilization.deployed++;
                                serverUtilization.totalinuse++;
                                serverUtilization.totalservers++;
                            }
                            break;
                    }
                });
                serverUtilization.serverpools.push(serverpool);
            });
            if (serverUtilization.totalservers > 0) {
                serverUtilization.pctavailable = Math.floor(100 * serverUtilization.totalavailable / serverUtilization.totalservers);
                serverUtilization.pctinuse = Math.floor(100 * serverUtilization.totalinuse / serverUtilization.totalservers);
            }
            if (serverUtilization.totalavailable > 0)
                serverUtilization.currentview = 'available';
            self.serverPoolUtilization = serverUtilization;
            self.serverHealth = serverHealth;
            self.serverHealthConfig.series.data = self.serverHealthConfig.series.data;
            self.loadingServerUtilization = false;
            self.loadingServerHealth = false;
            self.serverHealthConfig.series[0].data = [];
            self.serverHealthConfig.series[0].data.push({
                name: self.$translate.instant('SERVICES_DEPLOY_STATE_Healthy'),
                y: self.serverHealth.green,
                color: '#7AB800',
                sliced: false,
                selected: false,
                _state: "green"
            }, {
                name: self.$translate.instant('GENERIC_Unknown'),
                y: self.serverHealth.unknown,
                color: '#cccccc',
                sliced: false,
                selected: false,
                _state: "unknown"
            }, {
                name: self.$translate.instant('SERVICES_DEPLOY_STATE_Warning'),
                y: self.serverHealth.yellow,
                color: '#efb106',
                sliced: false,
                selected: false,
                _state: "yellow"
            }, {
                name: self.$translate.instant('GENERIC_ServiceMode'),
                y: self.serverHealth.servicemode,
                color: '#efad18',
                sliced: false,
                selected: false,
                _state: "servicemode"
            }, {
                name: self.$translate.instant('GENERIC_Critical'),
                y: self.serverHealth.red,
                color: '#d00e28',
                sliced: false,
                selected: false,
                _state: "red"
            });
            self.serverUtilizationConfig.series[0].data = [];
            self.serverUtilizationConfig.series[0].data.push({
                name: self.$translate.instant('DASHBOARD_ServersInUse'),
                y: self.serverPoolUtilization.totalinuse,
                color: '#0685C2'
            }, {
                name: self.$translate.instant('DASHBOARD_ServersAvailable'),
                y: self.serverPoolUtilization.totalavailable,
                color: '#CCCCCC'
            });
            self.buildServerPoolBarGraphs();
            //reset loading watchers
            self.loadingServerUtilization = false;
            self.loadingServerHealth = false;
        };
        HomeController.prototype.buildServerPoolBarGraphs = function () {
            var self = this;
            self.serverPoolUtilization.serverpools.forEach(function (pool) {
                var graphConfig = {};
                angular.copy(self.serverPoolUsageBaseConfig, graphConfig);
                graphConfig.series.push({ name: self.$translate.instant("DASHBOARD_Unused"), data: [pool.totalavailable], color: '#CCCCCC', id: 'unused', states: { hover: { enabled: false } } });
                graphConfig.series.push({ name: self.$translate.instant("DASHBOARD_InUse"), data: [pool.totalinuse], color: '#0685C2', id: 'inuse', states: { hover: { enabled: false } } });
                self.serverPoolUsageConfigs.push(graphConfig);
            });
        };
        HomeController.prototype.changeType = function () {
            var self = this;
            if (self.config.options.chart.type == 'pie') {
                self.config.options.chart.type = 'line';
            }
            else {
                self.config.options.chart.type = 'pie';
            }
        };
        HomeController.prototype.viewByChanged = function () {
            var self = this;
            self.refresh('services');
        };
        HomeController.prototype.toObj = function (obj) {
            return obj;
        };
        HomeController.prototype.setGauge = function () {
            var self = this;
            //if value for gauge is defined
            if (!self.gauge) {
                self.gauge = {
                    options: {
                        chart: {
                            type: 'gauge',
                            animation: false,
                            backgroundColor: "none",
                            marginTop: 16,
                            marginLeft: 0,
                            marginRight: 0,
                            spacingLeft: 0,
                            spacingRight: 0
                        },
                        title: {
                            text: self.$translate.instant("DASHBOARD_STORAGE_CAPACITY_ChartTitle"),
                            style: { "color": "#333333", "fontSize": "13px" },
                            y: 0
                        },
                        tooltip: {
                            enabled: false
                        },
                        pane: {
                            startAngle: -90,
                            endAngle: 90,
                            background: null
                        },
                        plotOptions: {
                            gauge: {
                                dataLabels: {
                                    enabled: false
                                },
                                pivot: {
                                    backgroundColor: "#007db8",
                                },
                                dial: {
                                    baseLength: '0%',
                                    baseWidth: 10,
                                    radius: '75%',
                                    rearLength: '0%',
                                    topWidth: 1,
                                    backgroundColor: "#007db8",
                                }
                            }
                        }
                    },
                    //    // the value axis
                    yAxis: {
                        min: 0,
                        max: 100,
                        lineWidth: 0,
                        minorTickWidth: 0,
                        tickWidth: 0,
                        labels: {
                            enabled: false
                        },
                        title: {
                            text: "",
                            y: 6
                        },
                        plotBands: [
                            {
                                from: 0,
                                to: 73,
                                color: '#6ea204',
                                thickness: '22%' // green
                            }, {
                                from: 75,
                                to: 93,
                                color: '#efb106',
                                thickness: '22%' // yellow
                            }, {
                                from: 95,
                                to: 100,
                                color: '#ce1126',
                                thickness: '22%' // red
                            }
                        ]
                    },
                    size: {
                        height: 225,
                        width: 240
                    },
                    spacingBottom: 0,
                    spacingTop: 0,
                    spacingLeft: 0,
                    spacingRight: 0,
                    series: [
                        {
                            name: '',
                            data: [0]
                        }
                    ]
                };
            }
            if (self.GlobalServices.gettingStarted) {
                self.gauge.series[0].name = self.GlobalServices.gettingStarted.partitionName;
                self.gauge.series[0].data[0] = self.GlobalServices.gettingStarted.storageUtilization.percentageUsed * 100;
                self.gauge.yAxis.title.text = "<span style=\"font-size: 19px\">" + self
                    .$filter("number")(self.gauge.series[0].data[0], 0) + " %</span> <br/> <span style=\"font-size: 15px\">" + self
                    .$translate.instant("GENERIC_Used") + "</span>";
            }
            self.gaugeTimeout = self.$timeout(function () { return self.setGauge(); }, 5000);
        };
        HomeController.prototype.getOpacity = function (id) {
            var self = this;
            var active = (self.selectedStorage && self.selectedStorage.id !== id);
            return active ? '.5' : '1';
        };
        HomeController.prototype.createTemplate = function (template, templateInputType) {
            var self = this;
            var createTemplateWizard = self.Modal({
                title: self.$translate.instant('TEMPLATES_CREATE_TEMPLATE_WIZARD_CreateTemplate'),
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/templatewizard.html',
                controller: 'TemplateWizardController as templateWizardController',
                params: {
                    template: template ? angular.copy(template) : undefined,
                    templateInputType: templateInputType
                },
                onComplete: function (id) {
                    //self.$timeout(function () {
                    //    self.$location.path(`templatebuilder/${id}/edit`);
                    //}, 500)
                }
            });
            createTemplateWizard.modal.show();
        };
        HomeController.prototype.addExistingService = function () {
            var self = this;
            var addServiceWizard = self.Modal({
                title: self.$translate.instant('SERVICE_ADD_EXISTING_SERVICE_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('AddingExistingService');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/addexistingservice.html',
                controller: 'AddExistingServiceController as addExistingServiceController',
                params: {},
                onCancel: function () {
                    //THIS FUNCTION IS CALLED ON MODAL.CANCEL, not WIZARD.CANCEL
                    //var confirm : self.Dialog(self.$translate.instant('GENERIC_Confirm'), 'Are you sure you want to cancel?');
                    var confirm = self.dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICE_ADD_EXISTING_SERVICE_Cancel_Confirmation'));
                    confirm.then(function (modalScope) {
                        addServiceWizard.modal.dismiss();
                        self.$timeout(function () {
                            self.activate();
                        }, 500);
                    });
                },
                onComplete: function () {
                    self.$timeout(function () {
                        self.activate(); //When the modal is closed, update the data.
                    }, 500);
                }
            });
            addServiceWizard.modal.show();
        };
        HomeController.prototype.deployNewService = function () {
            var self = this;
            var addServiceWizard = self.Modal({
                title: self.$translate.instant('SERVICES_NEW_SERVICE_DeployService'),
                onHelp: function () {
                    self.GlobalServices.showHelp('deployingserviceoverview');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/deployservice/deployservicewizard.html',
                controller: 'DeployServiceWizard as deployServiceWizard',
                params: {},
                onCancel: function () {
                    self.dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_DEPLOY_ConfirmWizardClosing'))
                        .then(function () {
                        addServiceWizard.modal.close();
                    });
                }
            });
            addServiceWizard.modal.show();
        };
        HomeController.prototype.testing123 = function () {
            var self = this;
            if (self.wedge != 'red')
                self.wedge = 'red';
            else
                self.wedge = 'unknown';
        };
        HomeController.prototype.Connect = function () {
            var self = this;
            self.wsMessages.messages = 'Connecting...';
            self.ws = new WebSocket('ws://localhost:1750/api/WebSocket');
            self.ws.onopen = function () {
                self.wsMessages = 'Connected.';
            };
            self.ws.onmessage = function (evt) {
                self.$timeout(function () {
                    self.wsMessages = evt.data;
                }, 100);
            };
            self.ws.onerror = function (evt) {
                self.wsMessages = evt.message;
            };
            self.ws.onclose = function () {
                self.wsMessages = 'Disconnected.';
            };
        };
        HomeController.prototype.SendMessage = function () {
            var self = this;
            if (self.ws.readyState === WebSocket.OPEN) {
                self.ws.send('Test');
            }
            else {
                self.wsMessages = 'Connection is closed.';
            }
        };
        HomeController.prototype.goTo = function (route) {
            var self = this;
            self.$location.path(route);
        };
        HomeController.prototype.showHelp = function (helptoken) {
            var self = this;
            self.GlobalServices.showHelp(helptoken);
        };
        HomeController.prototype.getServerPoolTitleTranslation = function (serverPoolName) {
            var self = this;
            return self.$translate.instant("DASHBOARD_TOOLTIP_ViewServerPool", { serverPoolName: serverPoolName });
        };
        HomeController.$inject = ['$http', '$filter', '$translate',
            '$timeout', 'GlobalServices', 'Modal', '$location', 'Dialog', 'Commands', '$q', "$rootScope"];
        return HomeController;
    }());
    asm.HomeController = HomeController;
    angular
        .module("app")
        .controller("HomeController", HomeController);
})(asm || (asm = {}));
//# sourceMappingURL=home.js.map
