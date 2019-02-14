angular.module('app')
    .controller('DashboardController',
    [
        '$scope', '$rootScope', '$route', '$http', '$compile', '$window', '$log', '$q', '$location', '$translate', '$timeout', 'Commands', 'Modal', 'Messagebox', 'GlobalServices',
        function ($scope, $rootScope, $route, $http, $compile, $window, $log, $q, $location, $translate, $timeout, Commands, Modal, MessageBox, GlobalServices) {

            //HACK: Used to force discovery to show
            $scope.showDiscovery = $route.current.params.showDiscovery === 'true' || false;
            if ($rootScope.discoveryShown) $scope.showDiscovery = false;

            $scope.data = {
                //chart: true,
                //devicealerts: false,

                services: [],
                servers: [],
                alerts: [
                    { state: 'critical', message: 'Server 6 cabling error. View Details', timestamp: new Date() },
                    { state: 'warning', message: '2 device warnings found.', timestamp: new Date() }
                ],
                activities: [
                    { state: '', message: 'Service VMware Cluster 2 has deployed. 4 Servers, 1TB Storage.', timestamp: new Date() },
                    { state: '', message: 'Service VMware ESXi 13 is deploying.', timestamp: new Date() }
                ],
                tickets: [
                    { message: 'Update on Ticket #12345 for Asset ABCDEF', timestamp: new Date() },
                    { message: 'Update on Ticket #12345 for Asset ABCDEF', timestamp: new Date() }
                ],
                entityClicked: '',
                selectedService: null,
                selectedServer: null,
                selectedStorage: null,
                selectedNetwork: null,
                serverExpanded: false,
                storageExpanded: false,
                networkExpanded: false,
                LeftStorageActive: false,
                LeftComputeActive: false,
                LeftFabricActive: false,
                messages: '',
                poll: null
            };

            $scope.viewmodel = {
                getPopoverTemplate: function () {
                    var html = '<div class="popover popover-blue" role="tooltip"><div class="popover-content"></div></div>';
                    return html;
                },

                getSolutionAlerts: function () {

                    var alerts = [];
                    angular.forEach($scope.data.services, function (service) {

                        angular.forEach(service.servers, function (server) {
                            alerts = alerts.concat(server.alerts || []);
                        });

                        angular.forEach(service.storage, function (storage) {
                            alerts = alerts.concat(storage.alerts || []);
                        });
                    });

                    alerts = _.sortBy(alerts, 'createdTime').reverse();
                    return alerts;
                },

                getServiceAlerts: function (service) {

                    var alerts = [];

                    angular.forEach(service.servers, function (server) {
                        alerts = alerts.concat(server.alerts || []);
                    });

                    angular.forEach(service.storage, function (storage) {
                        alerts = alerts.concat(storage.alerts || []);
                    });

                    alerts = _.sortBy(alerts, 'createdTime').reverse();
                    return alerts;
                },

                getServiceAlertsAllServers: function (service) {

                    var alerts = [];

                    angular.forEach(service.servers, function (server) {
                        alerts = alerts.concat(server.alerts || []);
                    });

                    alerts = _.sortBy(alerts, 'createdTime').reverse();

                    return alerts;
                }
            };

            var ws;
            $scope.pinnav = Boolean;
            $rootScope.$on('pin', function () {
                $scope.pinnav = true;
            });
            $rootScope.$on('unpin', function () {
                $scope.pinnav = false;
            });

            $scope.actions = {

                Connect: function () {
                    $scope.messages = 'Connecting...';
                    ws = new WebSocket('ws://localhost:14444/api/WebSocket');
                    ws.onopen = function () {
                        $scope.messages = 'Connected.';
                    };
                    ws.onmessage = function (evt) {
                        $scope.messages = evt.data;
                    };
                    ws.onerror = function (evt) {
                        $scope.messages = evt.message;
                    };
                    ws.onclose = function () {
                        $scope.messages = 'Disconnected.';
                    };
                },
                SendMessage: function () {
                    if (ws.readyState === WebSocket.OPEN) {
                        ws.send('Test');
                    }
                    else {
                        $scope.messages = 'Connection is closed.';
                    }
                },
                createService: function () {
                    $location.path('/deploy/service');
                    return;
                },

                viewService: function (id) {
                    $location.path('/services/' + id);
                    return;
                },

                viewStorage: function (id) {
                    $location.path('/storage/' + id);
                    return;
                },

                viewServer: function (id) {
                    $location.path('/server/' + id);
                    return;
                },

                expandServers: function () {

                    $scope.data.serverExpanded = !$scope.data.serverExpanded;
                    $scope.data.storageExpanded = false;
                    $scope.data.networkExpanded = false;
                    $scope.data.selectedServer = null;
                    $scope.data.selectedStorage = null;
                    $scope.data.selectedNetwork = null;

                    $scope.data.entityClicked = '';
                    $scope.data.entityClicked = 'servers';

                    if ($scope.data.serverExpanded) {
                        $timeout(function () {

                            $('.expanded-server, .expanded-network, .expanded-storage').velocity({
                                opacity: [0]
                            }, { duration: 0 });

                            //when selecting a service expand height of svg to accomodate
                            $('#concourseMain').velocity({
                                height: [600]
                            }, { duration: 1000, delay: 250 });

                            $('.expanded-server').velocity({
                                /* Coordinate animation works. */
                                height: ['100%', '0%'],
                                opacity: [1, 0]
                            }, { duration: 1000, delay: 0 });

                        }, 0);


                    } else {

                        $('.expanded-server, .expanded-network, .expanded-storage').velocity({
                            opacity: [0]
                        }, { duration: 0 });

                        $('#concourseMain').velocity({
                            height: [400],
                            marginTop: [0]
                        }, { duration: 1000, delay: 250 });

                    }
                },

                expandNetworking: function () {

                    $scope.data.serverExpanded = false;
                    $scope.data.storageExpanded = false;
                    $scope.data.networkExpanded = !$scope.data.networkExpanded;
                    $scope.data.selectedServer = null;
                    $scope.data.selectedStorage = null;
                    $scope.data.selectedNetwork = null;

                    $scope.data.entityClicked = '';
                    $scope.data.entityClicked = 'networking';

                    if ($scope.data.networkExpanded) {
                        $timeout(function () {

                            $('.expanded-server, .expanded-network, .expanded-storage').velocity({
                                opacity: [0]
                            }, { duration: 0 });

                            //when selecting a service expand height of svg to accomodate
                            $('#concourseMain').velocity({
                                height: [600]
                            }, { duration: 1000, delay: 250 });

                            $('.expanded-network').velocity({
                                /* Coordinate animation works. */
                                height: ['100%', '0%'],
                                opacity: [1, 0]
                            }, { duration: 1000, delay: 0 });

                        }, 0);


                    } else {

                        $('.expanded-server, .expanded-network, .expanded-storage').velocity({
                            opacity: [0]
                        }, { duration: 0 });

                        $('#concourseMain').velocity({
                            height: [400],
                            marginTop: [0]
                        }, { duration: 1000, delay: 250 });

                    }
                },

                expandStorage: function () {

                    $scope.data.serverExpanded = false;
                    $scope.data.storageExpanded = !$scope.data.storageExpanded;
                    $scope.data.networkExpanded = false;
                    $scope.data.selectedServer = null;
                    $scope.data.selectedStorage = null;
                    $scope.data.selectedNetwork = null;

                    $scope.data.entityClicked = 'storages';

                    if ($scope.data.storageExpanded) {
                        $timeout(function () {


                            $('.expanded-server, .expanded-network, .expanded-storage').velocity({
                                opacity: [0]
                            }, { duration: 0 });

                            //when selecting a service expand height of svg to accomodate
                            $('#concourseMain').velocity({
                                height: [600]
                            }, { duration: 1000, delay: 250 });

                            $('.expanded-storage').velocity({
                                /* Coordinate animation works. */
                                height: ['100%', '0%'],
                                opacity: [1, 0]
                            }, { duration: 1000, delay: 0 });

                        }, 0);
                    } else {

                        $('.expanded-network, .expanded-storage').velocity({
                            opacity: [0]
                        }, { duration: 0 });

                        $('#concourseMain').velocity({
                            height: [400],
                            marginTop: [0]
                        }, { duration: 1000, delay: 250 });

                    }
                },

                selectServer: function (server) {
                    $scope.data.entityClicked = '';
                    $scope.data.entityClicked = 'server';
                    $scope.data.selectedServer = angular.copy(server);
                },

                selectIndStorage: function (storage) {
                    $scope.data.entityClicked = '';
                    $scope.data.entityClicked = 'storage';
                    $scope.data.selectedStorage = angular.copy(storage);
                },

                selectIndNetwork: function (network) {
                    $scope.data.entityClicked = '';
                    $scope.data.entityClicked = 'network';
                    $scope.data.selectedNetwork = angular.copy(network);
                },

                selectService: function (service) {

                    $scope.data.LeftStorageActive = false;
                    $scope.data.LeftComputeActive = false;
                    $scope.data.LeftFabricActive = false;

                    if ($scope.data.selectedService && $scope.data.selectedService.id === service.id) {
                        //already selected so close it

                        $scope.data.entityClicked = '';

                        $scope.data.selectedService = null;

                        angular.forEach($scope.data.services, function (s) {
                            s.selected = false;
                        });

                        //when selecting a service expand height of svg to accomodate
                        $('#concourseMain').velocity({
                            height: [350],
                            marginTop: [0]
                        }, { duration: 1000, delay: 250 });

                        $('.fabricChart, .storageChart, .computeChart, #TopLineFabricTabs, #TopLineComputeTabs, #TopLineStorageTabs, #TopLine, #LeftTabCompute, #LeftTabStorage, #LeftTabFabric').velocity({
                            opacity: [0]
                        }, { duration: 250, display: 'none' });

                        return;
                    }

                    $scope.data.entityClicked = '';
                    $scope.data.entityClicked = 'service';

                    angular.forEach($scope.data.services, function (s) {
                        s.selected = false;
                    });

                    service.selected = true;

                    $scope.data.selectedService = angular.copy(service);

                    $scope.data.serverExpanded = false;
                    $scope.data.storageExpanded = false;
                    $scope.data.networkExpanded = false;


                    //set timeout to give time for angular to bind...
                    $timeout(function () {

                        //when selecting a service expand height of svg to accomodate
                        $('#concourseMain').velocity({
                            height: [400],
                            marginTop: [0]
                        }, { duration: 1000, delay: 250 });

                        //$('#servicelines').velocity({
                        //    translateX: [-1 * 200 * index]
                        //}, { duration: 2000 });


                        $('.expanded-service').velocity({
                            /* Coordinate animation works. */
                            height: ['100%', '0%'],
                            opacity: [1, 0]
                        }, { duration: 1000, delay: 250 });

                        //$('.expanded-server').velocity({
                        //    /* Coordinate animation works. */
                        //    height: ['0%'],
                        //    opacity: [0]
                        //}, { duration: 250, delay: 250 });

                        $('.fabricChart, .storageChart, .computeChart, #TopLineFabricTabs, #TopLineComputeTabs, #TopLineStorageTabs, #TopLine, #LeftTabCompute, #LeftTabStorage, #LeftTabFabric').velocity({
                            opacity: [0]
                        }, { duration: 250, display: 'none' });

                    }, 0);
                },

                closeLeftTabs: function () {

                    $scope.data.entityClicked = '';

                    $scope.data.LeftStorageActive = false;
                    $scope.data.LeftComputeActive = false;
                    $scope.data.LeftFabricActive = false;

                    //when selecting a service expand height of svg to accomodate
                    $('#concourseMain').velocity({
                        height: [350],
                        marginTop: [0]
                    }, { duration: 1000, delay: 250 });

                    $('.fabricChart, .storageChart, .computeChart, #TopLineFabricTabs, #TopLineComputeTabs, #TopLineStorageTabs, #TopLine, #LeftTabCompute, #LeftTabStorage, #LeftTabFabric').velocity({
                        opacity: [0]
                    }, { duration: 250, display: 'none' });

                },
                selectFabric: function () {

                    $scope.data.entityClicked = '';

                    if ($scope.data.LeftFabricActive) {
                        $scope.actions.closeLeftTabs();
                        return;
                    }

                    $scope.data.LeftStorageActive = false;
                    $scope.data.LeftComputeActive = false;
                    $scope.data.LeftFabricActive = true;


                    $timeout(function () {

                        angular.forEach($scope.data.services, function (s) {
                            s.selected = false;
                        });

                        $('.expanded-service').velocity({
                            /* Coordinate animation works. */
                            height: ['0%'],
                            opacity: [0]
                        }, { duration: 250 });

                        $('.storageChart, .computeChart, .fabricChart, #TopLineFabricTabs, #TopLine, #LeftTabCompute, #LeftTabStorage, #LeftTabFabric').velocity({
                            opacity: [0]
                        }, { duration: 0, delay: 0, display: 'none' });

                        //when selecting a service expand height of svg to accomodate
                        $('#concourseMain').velocity({
                            height: [350],
                            marginTop: [40]
                        }, { duration: 500 });

                        $('.fabricChart, #TopLineFabricTabs, #TopLine, #LeftTabFabric').velocity({
                            opacity: [1, 0]
                        }, { duration: 1000, delay: 500, display: 'block' });

                    }, 0);


                },

                selectStorage: function () {
                    $scope.data.entityClicked = '';

                    if ($scope.data.LeftStorageActive) {
                        $scope.actions.closeLeftTabs();
                        return;
                    }

                    $scope.data.LeftStorageActive = true;
                    $scope.data.LeftComputeActive = false;
                    $scope.data.LeftFabricActive = false;

                    $timeout(function () {


                        angular.forEach($scope.data.services, function (s) {
                            s.selected = false;
                        });

                        $('.expanded-service').velocity({
                            /* Coordinate animation works. */
                            height: ['0%'],
                            opacity: [0]
                        }, { duration: 250 });

                        $('.fabricChart, .computeChart, .storageChart, #TopLineFabricTabs, #TopLine, #LeftTabCompute, #LeftTabStorage, #LeftTabFabric').velocity({
                            opacity: [0]
                        }, { duration: 0, delay: 0, display: 'none' });

                        //when selecting a service expand height of svg to accomodate
                        $('#concourseMain').velocity({
                            height: [350],
                            marginTop: [40]
                        }, { duration: 500 });

                        $('.storageChart, #TopLineFabricTabs, #TopLine, #LeftTabStorage').velocity({
                            opacity: [1, 0]
                        }, { duration: 1000, delay: 500, display: 'block' });

                    }, 0);


                },

                selectCompute: function () {
                    $scope.data.entityClicked = '';

                    if ($scope.data.LeftComputeActive) {
                        $scope.actions.closeLeftTabs();
                        return;
                    }

                    $scope.data.LeftStorageActive = false;
                    $scope.data.LeftComputeActive = true;
                    $scope.data.LeftFabricActive = false;

                    $timeout(function () {
                        angular.forEach($scope.data.services, function (s) {
                            s.selected = false;
                        });

                        $('.expanded-service').velocity({
                            /* Coordinate animation works. */
                            height: ['0%'],
                            opacity: [0]
                        }, { duration: 250 });

                        $('.fabricChart, .storageChart, .computeChart, #TopLineFabricTabs, #TopLine, #LeftTabCompute, #LeftTabStorage, #LeftTabFabric').velocity({
                            opacity: [0]
                        }, { duration: 0, delay: 0, display: 'none' });

                        //when selecting a service expand height of svg to accomodate
                        $('#concourseMain').velocity({
                            height: [350],
                            marginTop: [40]
                        }, { duration: 500 });

                        $('.computeChart, #TopLineFabricTabs, #TopLine, #LeftTabCompute').velocity({
                            opacity: [1, 0]
                        }, { duration: 1000, delay: 500, display: 'block' });

                    }, 0);

                }


            };


            $scope.loadData = function () {

                GlobalServices.ClearErrors($rootScope.errors);
        
                //$http.get(Commands.services)
                //    .success(function (data, status, headers, config) {

                //        //services already exist so update state
                //        if ($scope.data.services.length > 0) {

                //            angular.forEach(data.data, function (updatedService) {
                //                angular.forEach($scope.data.services, function (existingService) {
                //                    if (updatedService.id === existingService.id) {
                //                        existingService.state = updatedService.state;
                //                    }
                //                });
                //            });

                //        } else {

                //            $scope.data.storageAllocation = 0;

                //            $scope.data.services = data.data;
                //            angular.forEach($scope.data.services, function (service) {

                //                $scope.data.storageAllocation += service.storageAllocation;

                //                if (!service.managementNetworks)
                //                    service.managementNetworks = [
                //                        { id: 1, name: 'Mgmt1', vlan: 25, bandwidth: '5 Mbps', health: 1, type: 'Management', model: 'IF1148', serviceTag: 'ABC12345' },
                //                        { id: 2, name: 'VMotion2', vlan: 26, bandwidth: '4 Mbps', health: 1, type: 'vMotion', model: 'IF1148', serviceTag: 'ABC12345' },
                //                        { id: 3, name: 'VMgroup3', vlan: 27, bandwidth: '4 Mbps', health: 1, type: 'Virtual Machine', model: 'IF1148', serviceTag: 'ABC12345' },
                //                        { id: 4, name: 'StorGrp4', vlan: 28, bandwidth: '4 Mbps', health: 1, type: 'Storage', model: 'IF1148', serviceTag: 'ABC12345' }
                //                    ];

                //                service.storageId = service.storage[0].id;

                //                service.storage = [
                //                        { id: 1, name: 'DataStore1', vlan: 25, bandwidth: '5 Mbps', overallHealth: 'OK', type: 'Management', model: 'IF1148', serviceTag: 'ABC12345' },
                //                        { id: 2, name: 'DataStore2', vlan: 25, bandwidth: '5 Mbps', overallHealth: 'OK', type: 'Management', model: 'IF1148', serviceTag: 'ABC12345' },
                //                        { id: 3, name: 'DataStore3', vlan: 25, bandwidth: '5 Mbps', overallHealth: 'OK', type: 'Management', model: 'IF1148', serviceTag: 'ABC12345' },
                //                        { id: 4, name: 'DataStore4', vlan: 25, bandwidth: '5 Mbps', overallHealth: 'OK', type: 'Management', model: 'IF1148', serviceTag: 'ABC12345' }
                //                ];

                //            });

                //            $scope.data.storageAllocation = $scope.data.storageAllocation / 1000;

                //        }
                //    })
                //    .error(function (data, status, headers, config) {
                //        GlobalServices.DisplayError(data, $rootScope.errors);
                //    });

                //var serverPromise = $http.get(Commands.servers);
                //var chassisPromise = $http.get(Commands.chassis);

                //$q.all([serverPromise, chassisPromise]).then(
                //    function (data) {

                //        var servers = _.filter(data[0].data.data, function (s) { return s.status !== 'ONBOARDING'; });
                //        var chassis = data[1].data.data;

                //        angular.forEach(servers, function (server) {
                //            if (server.chassisId) {
                //                server.chassis = _.findWhere(chassis, { id: server.chassisId });
                //            }
                //        });

                //        var updateCharts = false;

                //        if ($scope.data.servers.length !== servers.length) updateCharts = true;

                //        $scope.data.servers = servers;

                //        $scope.data.totalServers = servers.length;
                //        $scope.data.availableServers = _.where(servers, { state: 1 }).length;
                //        $scope.data.notAvailableServers = $scope.data.totalServers - $scope.data.availableServers;

                //        if (updateCharts)
                //            $scope.renderCharts();

                //    },
                //    function (data) {
                //        GlobalServices.DisplayError(data.data, $rootScope.errors);
                //        $log.debug(data.data);

                //    }
                //);

                
     

                $scope.data.poll = $timeout(function () {
                    $scope.loadData();
                }, 10000);

                $scope.$on(
                      '$destroy',
                      function (event) {
                          $timeout.cancel($scope.data.poll);
                      }
                     
                  );

            };

            $scope.renderCharts = function () {

                //#chartExample

                // Create the fabric chart
                var fabricChart = new Highcharts.Chart({
                    tooltip: {
                        enabled: true,
                        formatter: function () { return '<b>' + this.key + ': </b>' + this.y; },
                        useHTML: true,
                        style: { zIndex: 99999 },
                        positioner: function (labelWidth, labelHeight, point) { return { x: point.plotX + 10, y: point.plotY + 10 }; }
                    },
                    credits: {
                        enabled: false
                    },
                    exporting: {
                        enabled: false
                    },
                    chart: {
                        height: 75,
                        width: 75,
                        renderTo: 'fabricChart',
                        type: 'pie',
                        spacing: [0, 0, 0, 0],
                        backgroundColor: 'rgba(255,255,255,0.002)'
                    },
                    title: {
                        text: null
                    },
                    colors: ['#4CA5E9', '#264A68'],
                    plotOptions: {
                        pie: {
                            allowPointSelect: true,
                            cursor: 'pointer',
                            dataLabels: {
                                enabled: false
                            },
                            showInLegend: false,
                            borderColor: '#2A5273'
                        },

                    },
                    series: [
                        {
                            type: 'pie',
                            innerSize: 40,
                            name: 'Ports',
                            data: [
                                { name: 'Used', y: 91 },
                                { name: 'Available', y: 5 }
                            ],

                        }
                    ],
                    legend: {
                        enabled: false
                    }
                });

                // Create the compute chart
                var computeChart = new Highcharts.Chart({
                    tooltip: {
                        enabled: true,
                        formatter: function () { return '<b>' + this.key + ': </b>' + this.y; },
                        useHTML: true,
                        style: { zIndex: 99999 },
                        positioner: function (labelWidth, labelHeight, point) { return { x: point.plotX + 10, y: point.plotY + 10 }; }
                    },
                    credits: {
                        enabled: false
                    },
                    exporting: {
                        enabled: false
                    },
                    chart: {
                        height: 75,
                        width: 75,
                        renderTo: 'computeChart',
                        type: 'pie',
                        backgroundColor: 'rgba(255,255,255,0.002)',
                        spacing: [0, 0, 0, 0]
                    },
                    title: {
                        text: null
                    },
                    colors: ['#4CA5E9', '#264A68'],
                    plotOptions: {
                        pie: {
                            allowPointSelect: true,
                            cursor: 'pointer',
                            dataLabels: {
                                enabled: false
                            },
                            showInLegend: false,
                            borderColor: '#2A5273'
                        }
                    },
                    series: [
                        {
                            type: 'pie',
                            innerSize: 40,
                            name: 'Servers',
                            data: [
                                { name: 'In Service', y: $scope.data.notAvailableServers },
                                { name: 'Available', y: $scope.data.availableServers }
                            ]
                        }
                    ],
                    legend: {
                        enabled: false
                    }
                });



            };


            $scope.discoverDevices = function () {
                var discoverDevices = Modal({
                    title: $translate.instant('GETTINGSTARTED_DiscoveryAndOnboarding'),
                    modalSize: 'modal-lg',
                    templateUrl: 'views/first/firstrun_autodiscovery.html',
                    controller: 'FirstRunAutoDiscoveryController'
                });

                discoverDevices.modal.show();

                $rootScope.discoveryShown = true;
            }

            $scope.initialize = function () {
                $scope.pinnav = $rootScope.pinnav;

                //var cachebuster = Math.round(new Date().getTime() / 1000);
                //$http.get('/views/svg/concourse.html?ts=' + cachebuster)
                //    .success(function (data) {

                //        var tpl = $compile(data)($scope);
                //        $('#svg-container').append(tpl);
                //        $scope.loadData();

                //        var beforePan = function (oldPan, newPan) {

                //            //allow any X, but prevent any Y
                //            var customPan = {}
                //            customPan.x = newPan.x;
                //            customPan.y = oldPan.y;

                //            return customPan;
                //        }

                //        $scope.data.svgControls = svgPanZoom('#concourseMain', {
                //            viewportSelector: '#servicelines',
                //            panEnabled: true,
                //            zoomEnabled: false,
                //            controlIconsEnabled: false,
                //            fit: false,
                //            center: false,
                //            contain: false,
                //            beforePan: beforePan,
                //            dblClickZoomEnabled: false
                //        });

                //        $scope.data.svgControls.setBeforePan(beforePan);

                //        initSMIL();

                //        //  $scope.loadData();
                //        $timeout(function () {
                //            if ($scope.showDiscovery) {
                //                $scope.discoverDevices();
                //            };
                //        }, 1000);



                //    });

 

            };

            $scope.animation = {
                animate1: function () {

                    //$('.link')
                    //    .velocity({ 'stroke-dashoffset': 0 }, { duration: 1000, delay: 10 });

                },
                animate2: function () {
                    //$('.link2')
                    //    .velocity({ 'stroke-dashoffset': 0 }, { duration: 1000, delay: 500 });

                },
                animate3: function () {
                    //$('.link3')
                    //    .velocity({ 'stroke-dashoffset': 0 }, { duration: 1000, delay: 700 });

                },
                animate4: function () {
                    //$('.link4')
                    //    .velocity({ 'stroke-dashoffset': 0 }, { duration: 1000, delay: 800 });

                }
            };


            $scope.initialize();

        }
    ])



    .controller('FirstRunAutoDiscoveryController',
        ['$window', '$scope', '$log', '$resource', '$translate', 'localStorageService', '$filter', '$timeout', 'Commands', '$http',
            function ($window, $scope, $log, $resource, $translate, localStorage, $filter, $timeout, Commands, $http) {

                $scope.discoveringDevices = 0;
                $timeout(function () {
                    $scope.discoveringDevices = 1;
                }, 4000);

                $scope.viewmodel = {
                    devices: []
                };

                $scope.data = {
                    serverdiscovery: [
                        { overallHealth: 'OK', servicetag: 'ABCEDF1', model: 'PowerEdge FX2', memory: '512GB', processor: 'Intel® Xeon® E5-2670', type: 'Chassis', state: 'Available' },
                        { overallHealth: 'OK', servicetag: 'ABCEDF2', model: 'PowerEdge FX2', memory: '512GB', processor: 'Intel® Xeon® E5-2670', type: 'Chassis', state: 'Available' },
                        { overallHealth: 'OK', servicetag: 'ABCEDF3', model: 'PowerEdge FC630', memory: '512GB', processor: 'Intel® Xeon® E5-2670', type: 'Server', state: 'Available' },
                        { overallHealth: 'OK', servicetag: 'ABCEDF4', model: 'PowerEdge FC630', memory: '512GB', processor: 'Intel® Xeon® E5-2670', type: 'Server', state: 'Available' },
                        { overallHealth: 'OK', servicetag: 'ABCEDF5', model: 'PowerEdge FC630', memory: '512GB', processor: 'Intel® Xeon® E5-2670', type: 'Server', state: 'Available' },
                        { overallHealth: 'OK', servicetag: 'ABCEDF6', model: 'PowerEdge FC630', memory: '512GB', processor: 'Intel® Xeon® E5-2670', type: 'Server', state: 'Available' },
                        { overallHealth: 'OK', servicetag: 'ABCEDF7', model: 'PowerEdge FC630', memory: '512GB', processor: 'Intel® Xeon® E5-2670', type: 'Server', state: 'Available' },
                        { overallHealth: 'OK', servicetag: 'ABCEDF8', model: 'PowerEdge FC630', memory: '512GB', processor: 'Intel® Xeon® E5-2670', type: 'Server', state: 'Available' },
                        { overallHealth: 'OK', servicetag: 'ABCEDF9', model: 'PowerEdge FC630', memory: '512GB', processor: 'Intel® Xeon® E5-2670', type: 'Server', state: 'Available' },
                        { overallHealth: 'OK', servicetag: 'ABCEDF0', model: 'PowerEdge FC630', memory: '512GB', processor: 'Intel® Xeon® E5-2670', type: 'Server', state: 'Available' },
                        { overallHealth: 'OK', servicetag: 'ADZYRE7', model: 'SC4020', memory: '128GB', processor: 'Intel® Xeon® E5-2620', type: 'Storage', state: 'Available' }
                    ]
                };

                $scope.actions = {

                };


                $scope.initialize = function () {


                };

                $scope.initialize();

                //replace with $http call if necessary

                //Device.query().$promise
                //    .then(function (data) {
                //        $scope.data.serverdata = data;
                //        //copy the references (you could clone ie angular.copy but then have to go through a dirty checking for the matches)
                //        $scope.data.displayedserverdata = [].concat($scope.data.serverdata);
                //    })

                //$scope.parseDiscoveredDevices = function () {
                //    $scope.totalDiscovered = $scope.viewmodel.devices.length;
                //    $scope.totalPending = 0;
                //    $scope.totalErrors = 0;
                //    $.each($scope.viewmodel.devices, function (index, device) {
                //        if ($filter('deviceHealth')(device.health) == "red") {
                //            $scope.totalErrors++;
                //        }
                //    });
                //    $scope.totalPending = ($scope.totalDiscovered - $scope.totalErrors);
                //};

                //DiscoverDevice.query().$promise.then(function (data) {
                //    $scope.viewmodel.devices = data;

                //    //copy the references (you could clone ie angular.copy but then have to go through a dirty checking for the matches)
                //    $scope.viewmodel.displayeddevices = [].concat($scope.viewmodel.devices);

                //    $scope.parseDiscoveredDevices();
                //}).catch(function (error) {
                //    var z = 0;
                //});
            }])





;
