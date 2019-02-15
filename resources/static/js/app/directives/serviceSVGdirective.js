var asm;
(function (asm) {
    'use strict';
    var ServicesvgController = (function () {
        //get intervalX(): number {
        //    var self: ServicesvgController = this;
        //    return self._intervalX;
        //}
        //set intervalX(theIntervalX: number) {
        //    var self: ServicesvgController = this;
        //    var oldIntervalX = self._intervalX;
        //    self._intervalX = theIntervalX;
        //    if (self.intervalX !== oldIntervalX) {
        //        if (self.service !== "") {
        //            self.refresh();
        //        }
        //    }
        //}
        function ServicesvgController(Modal, Dialog, $http, $timeout, $q, GlobalServices, $route, localStorageService, $routeParams, $translate, $location, $window, Loading, Commands, constants, $scope, $rootScope) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.GlobalServices = GlobalServices;
            this.$route = $route;
            this.localStorageService = localStorageService;
            this.$routeParams = $routeParams;
            this.$translate = $translate;
            this.$location = $location;
            this.$window = $window;
            this.Loading = Loading;
            this.Commands = Commands;
            this.constants = constants;
            this.$scope = $scope;
            this.$rootScope = $rootScope;
            this.svgheight = 300;
            this.mode = "edit"; //
            this.serviceServers = [];
            this.popoverServers = [];
            this.serviceStorages = [];
            this.popoverStorages = [];
            this.serviceClusters = [];
            this.popoverClusters = [];
            this.serviceVMs = [];
            this.popoverVMs = [];
            this.mostItems = 0;
            this.render = false;
            //public serviceId: any;
            this.timerIntervals = 300;
            this.addResource = null;
            this.enableSvgLogging = false;
            this.overallServiceHealthText = '';
            this.overallServiceResourceHealthText = '';
            this.intervalX = 0;
            this.serviceTimeoutTime = 60000;
            var self = this;
            self.const_firmwareStatus = constants.firmwareStatus;
            self.serviceId = self.$scope.serviceId;
            self.firmwarereport = self.$scope.firmwarereport;
            self.activeTab = self.$scope.activeTab;
            //self.serviceName = self.$scope.serviceName;
            self.$scope.$watch(function () { return self.intervalX; }, function (newValue, oldValue) {
                if (oldValue !== newValue) {
                    if (self.service !== '') {
                        self.refresh();
                    }
                }
            });
            self.$scope.$watch(function () { return self.$scope.refreshService; }, function (newValue, oldValue) {
                if (oldValue !== newValue) {
                    self.getserviceData();
                }
            });
            self.refresh();
            self.windowResize();
            //$(window).resize(function () {
            //    if (self.pollwindowtimer) self.$timeout.cancel(self.pollwindowtimer);
            //    //self.pollwindowtimer = self.$timeout(function() {
            //    //    self.calculatewindowheight();
            //    //}, 1);
            //    self.pollwindowtimer = self.calculatewindowheight();
            //});
            //self.upgradeServiceComponents();
        }
        ServicesvgController.prototype.removePopovers = function () {
            $('.popover').remove();
        };
        ServicesvgController.prototype.portviewServer = function (server) {
            var self = this;
            self.removePopovers();
            self.$scope.onServerPortViewClick({ tab: 'portView', server: server });
        };
        ServicesvgController.prototype.viewLogs = function (component) {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var viewLogsModal = self.Modal({
                title: self.$translate.instant("LOGS_RESOURCE_SEVERITIES_ComponentLogsModalTitle", { componentName: component.name }),
                modalSize: 'modal-lg',
                templateUrl: 'views/resources/modals/resourcelogs.html',
                controller: 'ResourceLogsController as resourceLogsController',
                params: {
                    componentid: component.id,
                    deploymentid: self.$scope.serviceId,
                    componentname: component.name
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    viewLogsModal.modal.dismiss();
                }
            });
            viewLogsModal.modal.show();
        };
        ServicesvgController.prototype.hasApplication = function (component) {
            if (component.type == 'storage' || component.type == 'cluster' || component.type === 'scaleio')
                return false;
            return _.find(component.relatedcomponents, function (relatedComponent) { return relatedComponent.installOrder > 0; });
        };
        ServicesvgController.prototype.clearTooltips = function () {
            $('[data-toggle="tooltip"]').tooltip('hide');
        };
        ServicesvgController.prototype.windowResize = function () {
            var self = this;
            $(window).resize(function () {
                //console.log('inside resize event');
                if (self.pollwindowtimer)
                    self.$timeout.cancel(self.pollwindowtimer);
                //if (self.windowtimer) self.$timeout.cancel(self.windowtimer);
                self.pollwindowtimer = self.$timeout(function () {
                    self.calculatewindowheight();
                }, 1);
                //self.pollwindowtimer = self.calculatewindowheight();
            });
        };
        ServicesvgController.prototype.$onDestroy = function () {
            var self = this;
            $(window).off("resize");
            if (self.windowtimer)
                self.$timeout.cancel(self.windowtimer);
            if (self.pollwindowtimer)
                self.$timeout.cancel(self.pollwindowtimer);
            if (self.serviceTimeout)
                self.$timeout.cancel(self.serviceTimeout);
        };
        ServicesvgController.prototype.centerValue = function (components) {
            var self = this;
            if (components <= (self.mostItems - 1)) {
                var x = ((self.mostItems - components) / 2) * self.intervalX;
                return x;
            }
        };
        /* Original service stuff start */
        ServicesvgController.prototype.doAddExistingService = function () {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var addServiceWizard = self.Modal({
                title: 'Add Existing Service',
                onHelp: function () {
                    self.GlobalServices.showHelp('AddingExistingService');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/networking/pools/addpoolwizard.html',
                controller: 'AddPoolWizardController as AddPoolWizard',
                params: {},
                onComplete: function () {
                    self.pauseRefresh = false;
                },
                onCancel: function () {
                    addServiceWizard.modal.dismiss();
                    self.pauseRefresh = false;
                }
            });
            addServiceWizard.modal.show();
        };
        ServicesvgController.prototype.doMigrate = function () {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var migrateModal = self.Modal({
                title: self.$translate.instant('MIGRATE_SERVER_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('migrateserver');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/migrate.html',
                controller: 'MigrateController as migrateController',
                params: {
                    serviceId: self.serviceId
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                    self.getserviceData();
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    migrateModal.modal.dismiss();
                }
            });
            migrateModal.modal.show();
        };
        ServicesvgController.prototype.viewSettings = function () {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var modal = self.Modal({
                title: self.$translate.instant('TEMPLATES_TEMPLATESETTINGS_MODAL_ServiceSettings'),
                onHelp: function () {
                    self.GlobalServices.showHelp('ViewDeploymentSettings');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/templatebuilder/viewtemplatedetailsmodal.html',
                controller: 'ViewTemplateDetailsModalController as viewTemplateDetailsModalController',
                params: {
                    service: angular.copy(self.service)
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    modal.modal.dismiss();
                }
            });
            modal.modal.show();
        };
        ServicesvgController.prototype.doDeleteResources = function () {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var deleteModal = self.Modal({
                title: self.$translate.instant('SERVICE_DELETE_RESOURCES_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('DeleteResources');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/deleteresources.html',
                controller: 'DeleteResourcesController as deleteResources',
                params: {
                    serviceId: self.serviceId
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                    self.getserviceData();
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    deleteModal.modal.dismiss();
                }
            });
            deleteModal.modal.show();
        };
        ServicesvgController.prototype.deleteService = function () {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var deleteserviceModal = self.Modal({
                title: self.$translate.instant('SERVICE_DETAIL_RemoveService'),
                onHelp: function () {
                    self.GlobalServices.showHelp('deleteservice');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/deleteservice.html',
                controller: 'DeleteServiceModalController as DeleteServiceModal',
                params: {
                    service: angular.copy(self.service)
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                    //self.getserviceData();
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    deleteserviceModal.modal.dismiss();
                }
            });
            deleteserviceModal.modal.show();
        };
        ServicesvgController.prototype.doUpdateFirmware = function () {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var updateFirmware = self.Modal({
                title: self.$translate.instant('SERVICE_APPLY_FIRMWARE_UPDATES_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('resourcesupdatingfirmware');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/updateservicefirmware.html',
                controller: 'UpdateServiceFirmwareController as UpdateServiceFirmware',
                params: {
                    id: self.serviceId,
                    mode: 'service'
                },
                onCancel: function () {
                    var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICE_APPLY_FIRMWARE_UPDATES_Cancel_Confirmation'));
                    confirm
                        .then(function () {
                        updateFirmware.modal.dismiss();
                    })
                        .finally(function () { self.pauseRefresh = false; });
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                    self.getserviceData();
                }
            });
            updateFirmware.modal.show();
        };
        ServicesvgController.prototype.editService = function (revertRouteOnFinish) {
            var self = this;
            self.pauseRefresh = true;
            var editServiceModal = self.Modal({
                title: self.$translate.instant('SERVICE_DETAIL_EditServiceInformation'),
                onHelp: function () {
                    self.GlobalServices.showHelp('EditServiceInformation');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/editservice.html',
                controller: 'EditServiceModalController as editService',
                params: {
                    id: self.serviceId
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                    if (revertRouteOnFinish) {
                        self.$location.path("/service/" + self.$scope.serviceId + "/details");
                    }
                    self.getserviceData();
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    editServiceModal.modal.dismiss();
                }
            });
            editServiceModal.modal.show();
        };
        ServicesvgController.prototype.openFirmwareReport = function () {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var firmwareReportModal = self.Modal({
                title: self.$translate.instant('SERVICES_SERVICE_FirmwareReportTitle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('viewfirmwarecomplianceservice');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/servicecompliancereport.html',
                controller: 'ServiceComplianceReportController as serviceComplianceReportController',
                params: {
                    type: "service",
                    id: self.serviceId
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    firmwareReportModal.modal.dismiss();
                }
            });
            firmwareReportModal.modal.show();
        };
        ServicesvgController.prototype.exportService = function () {
            var self = this;
            self.$window.location.href = self.Commands.data.services.exportService + '/' + (self.serviceId ? self.serviceId : '');
        };
        ServicesvgController.prototype.generateTroubleshootingBundle = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.applianceManagement.exportTroubleshootingBundle, null)
                .then(function (data) {
                $("body").append("<iframe src='" + data.data.responseObj + "' style='display: none;' ></iframe>");
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ServicesvgController.prototype.generateTroubleBundle_modal = function () {
            var self = this;
            var theModal = self.Modal({
                title: self.$translate.instant('SETTINGS_GenerateTroubleshootingBundle'),
                /*onHelp() {
                    self.GlobalServices.showHelp('Addingfirmwarerepositories');
                },*/
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/generatetroubleshootingbundlemodal.html',
                controller: 'GenerateTroubleshootingBundleModalController as generateTroubleshootingBundleModalController',
                params: {
                    callingPage: 'Service',
                    serviceId: self.serviceId
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            theModal.modal.show();
        };

        ServicesvgController.prototype.generateSNMPModal = function () {
            var self = this;
            var theModal = self.Modal({
                title: self.$translate.instant('Generate SNMP data'),
                /*onHelp() {
                    self.GlobalServices.showHelp('Addingfirmwarerepositories');
                },*/
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/editSNMPdetails.html',
                controller: 'GenerateTroubleshootingBundleModalController as generateTroubleshootingBundleModalController',
                params: {
                    callingPage: 'Service',
                    serviceId: self.serviceId
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            theModal.modal.show();
        };

        ServicesvgController.prototype.retryService_Modal = function () {
            var self = this;
            var showAAParm = false;
            self.removePopovers();
            self.pauseRefresh = true;
            //console.log('retryService_Modal, self.service:');
            //console.log(self.service);
            //console.log('self.service.firmwarePackageId:  ' + self.service.firmwarePackageId);
            if (self.service.firmwarePackageId != null && self.service.firmwarePackageId != '' && self.service.firmwarePackageId != 'undefined') {
                showAAParm = true;
            }
            else {
                showAAParm = false;
            }
            var modal = self.Modal({
                title: self.$translate.instant('GENERIC_Warning'),
                //onHelp() {
                //    //TODO: get new help topic guid
                //    self.GlobalServices.showHelp('');
                //},
                modalSize: 'modal-md',
                titleIcon: 'warning ci-health-warning-tri-bang',
                templateUrl: 'views/services/retryServiceModal.html',
                controller: 'RetryServiceController as RetryServiceController',
                params: {
                    //serviceId: angular.copy(self.serviceId),
                    service: angular.copy(self.service),
                    showAlternateAction: showAAParm
                },
                onComplete: function (alternateAction) {
                    if (alternateAction == true) {
                        self.doUpdateFirmware();
                    }
                    self.pauseRefresh = false;
                    self.getserviceData();
                }
            });
            modal.modal.show();
        };
        ServicesvgController.prototype.updateInventory = function () {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var modalTitle = self.$translate.instant('SERVICES_UPDATE_RESOURCES_UpdateResources');
            var modal = self.Modal({
                title: modalTitle,
                onHelp: function () {
                    //TODO: get new help topic guid
                    self.GlobalServices.showHelp('UpdateInventory');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/updateinventorymodal.html',
                controller: 'UpdateInventoryModalController as updateInventoryModalController',
                params: {
                    service: angular.copy(self.service)
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                    self.getserviceData();
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    modal.modal.dismiss();
                }
            });
            modal.modal.show();
        };
        /* Original service stuff end */
        //The window size is constantly being calculated
        ServicesvgController.prototype.calculatewindowheight = function () {
            var self = this;
            //self.enableSvgLogging && console.log('redrawing window');
            self.windowtimer = self.$timeout(function () {
                self.svgcanvaswidth = $('#serviceBuilderSVG').width();
                self.windowheight = $(window).height() - 300;
                //if (self.windowheight >= 300) {
                //    self.svgheight = self.windowheight - 50;
                //}
                //min height
                if (self.windowheight <= 600) {
                    self.svgheight = self.windowheight - 50;
                    if (self.svgheight <= 500) {
                        self.svgheight = 500;
                    }
                }
                //max height
                if (self.windowheight >= 600) {
                    self.svgheight = self.windowheight - 50;
                    if (self.svgheight >= 600) {
                        self.svgheight = 600;
                    }
                }
                self.componentscale = self.svgheight * .002;
                self.Xlines = self.svgheight * .065;
                self.intervalX = 15;
                if (self.svgcanvaswidth >= 800 && self.svgheight <= 600) {
                    self.intervalX = 10;
                }
                if (self.svgcanvaswidth <= 800 && self.svgheight >= 600) {
                    self.intervalX = 18;
                }
                //The x interval needs to change depending on the size of the window and canvas
                //short windows
                //if (self.windowheight <= 601 && self.svgcanvaswidth <= 400) {
                //    self.intervalX = 25;
                //}
                //if (self.windowheight <= 601 && self.svgcanvaswidth >= 400) {
                //    self.intervalX = 14;
                //}
                ////tall windows
                //if (self.windowheight >= 601 && self.svgcanvaswidth >= 800) {
                //    self.intervalX = 15;
                //}
                //if (self.windowheight >= 601 && self.svgcanvaswidth <= 800) {
                //    self.intervalX = 20;
                //    self.componentscale = self.svgheight * .0015;
                //    self.Xlines = self.svgheight * .052;
                //}
                self.changewidths();
            }, self.timerIntervals);
        };
        //This gets called after calculating window height and after drawing the lines
        ServicesvgController.prototype.changewidths = function () {
            var self = this;
            $('#drawingContainer').css('height', (self.svgheight - 50 + 'px'));
            if (self.render && self.mostItems >= 1 && self.furthestComponentId !== '') {
                //Chrome needs this: It adjusts heights and widths of various DOM containers to allow horizontal scrolling on the SVG
                if (self.render) {
                    //if (self.windowheight >= 300) {
                    //    $('#drawingContainer').css('height', (self.windowheight * .8 + 'px'));
                    //}
                    //$('#drawingContainer').css('width', '400px');
                    var BScontainerWidth = $('#servicearticle').width();
                    $('#drawingContainer').css('width', BScontainerWidth);
                    $('#drawingContainer').css('width', (BScontainerWidth).toString() + 'px');
                    $('#serviceBuilderSVG').css('width', (BScontainerWidth - 45).toString() + 'px');
                    var widestRowStart = $('#' + self.mostComponents).offset().left;
                    var widestRowEnd = $('#' + self.furthestComponentId + ' #topright').offset().left;
                    var wideDivwidth = widestRowEnd - widestRowStart;
                    $('#wideDiv').css('width', (wideDivwidth + 250).toString() + 'px');
                    $('#deselectionBG').attr('width', (wideDivwidth + 250).toString() + 'px');
                    //Adjust background lines
                    self.furthestSVGPoint = $('#' + self.furthestComponentId).attr('x');
                    self.furthestSVGPoint = parseInt(self.furthestSVGPoint.replace(/[^\w\s]/gi, '')) + 50;
                    self.furthestSVGPoint = self.furthestSVGPoint.toString() + '%';
                    if (self.furthestSVGPoint.replace(/[^\w\s]/gi, '') <= 100) {
                        self.furthestSVGPoint = '100%';
                    }
                }
            }
            else {
                //if (self.windowheight >= 300) {
                //    $('#drawingContainer').css('height', (self.windowheight * .8 + 'px'));
                //}
                //$('#drawingContainer').css('width', '400px');
                var BScontainerWidth = $('#servicearticle').width();
                $('#drawingContainer').css('width', BScontainerWidth);
                $('#drawingContainer').css('width', (BScontainerWidth).toString() + 'px !important');
                $('#serviceBuilderSVG').css('width', (BScontainerWidth - 45).toString() + 'px');
                self.furthestSVGPoint = '100%';
            }
        };
        ServicesvgController.prototype.addComponent = function (resourceType) {
            var self = this, component = {
                controller: "ServiceComponentModalController as serviceComponentModalController",
                templateUrl: "views/services/servicecomponentwrapper.html"
            }, network = {
                controller: "AddNetworkModalController as addNetworkModalController",
                templateUrl: "views/networking/networks/addnetworktoservice.html"
            }, application = {
                controller: "AddApplicationWizardController as Application",
                templateUrl: "views/addapplicationwizard.html"
            }, defaultParams = {
                mode: "edit",
                action: "add",
                type: resourceType,
                templateId: self.service.templateId,
                serviceId: self.service.id,
                template: angular.copy(self.service),
                service: angular.copy(self.service)
            }, modalProps = {};
            self.pauseRefresh = true;
            self.removePopovers();
            switch (resourceType) {
                case "application":
                    modalProps = angular.extend(application, {
                        title: self.$translate.instant("ADDAPPLICATION_Title"),
                        onHelp: function () {
                            self.GlobalServices.showHelp('addapplicationservice');
                        },
                        params: {
                            template: null,
                            service: self.service,
                            action: "add"
                        }
                    });
                    break;
                case "network":
                    modalProps = angular.extend(network, {
                        title: self.$translate.instant("SERVICES_AddNetwork"),
                        onHelp: function () {
                            self.GlobalServices.showHelp('DeployServiceAddNetwork');
                        },
                        params: defaultParams
                    });
                    break;
                case "storage":
                    modalProps = angular.extend(component, {
                        title: self.$translate.instant("COMPONENTEDITOR_AddVolume"),
                        onHelp: function () {
                            self.GlobalServices.showHelp('servicesaddstorage');
                        },
                        params: defaultParams
                    });
                    break;
                case "server":
                    modalProps = angular.extend(component, {
                        title: self.$translate.instant("COMPONENTEDITOR_AddServerComponent"),
                        onHelp: function () {
                            self.GlobalServices.showHelp('servicesaddserver');
                        },
                        params: defaultParams
                    });
                    break;
                case "cluster":
                    modalProps = angular.extend(component, {
                        title: self.$translate.instant("COMPONENTEDITOR_ClusterComponent"),
                        onHelp: function () {
                            self.GlobalServices.showHelp('adjustresources');
                        },
                        params: defaultParams
                    });
                    break;
                case "vm":
                    modalProps = angular.extend(component, {
                        title: self.$translate.instant("COMPONENTEDITOR_AddVirtualMachinesVMs"),
                        onHelp: function () {
                            self.GlobalServices.showHelp('servicesaddvm');
                        },
                        params: defaultParams
                    });
                    break;
                default:
                    //if title option is selected, do nothing
                    return;
            }
            //build and launch the modal
            var componentEditModal = self.Modal(angular.extend({
                modalSize: "modal-lg",
                onComplete: function () {
                    self.pauseRefresh = false;
                    self.getserviceData();
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    componentEditModal.modal.dismiss();
                }
            }, modalProps));
            componentEditModal.modal.show();
            //reset the dropdown
            self.addResource = null;
        };
        ServicesvgController.prototype.updateServiceComponents = function () {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var modal = self.Modal({
                title: self.$translate.instant('SERVICE_DETAIL_UpgradeServiceComponents'),
                modalSize: 'modal-lg',
                templateUrl: 'views/services/updatecomponents.html',
                controller: 'UpdateComponentsController as updateComponentsController',
                params: {
                    serviceId: self.serviceId
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                    self.getserviceData();
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    modal.modal.dismiss();
                }
            });
            modal.modal.show();
        };
        ServicesvgController.prototype.deleteApplications = function (selectedComponent) {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var stopManagingApplicationsModal = self.Modal({
                title: self.$translate.instant('STOP_MANAGING_APPLICATIONS_Title', { componentName: selectedComponent.name }),
                modalSize: 'modal-lg',
                templateUrl: 'views/stopmanagingapplications.html',
                controller: 'StopManagingApplicationsModalController as Stop',
                params: {
                    serviceId: self.serviceId,
                    selectedComponent: selectedComponent
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                    self.getserviceData();
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    stopManagingApplicationsModal.modal.dismiss();
                }
            });
            stopManagingApplicationsModal.modal.show();
        };
        ServicesvgController.prototype.editApplications = function (selectedComponent) {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var addApplicationWizard = self.Modal({
                title: self.$translate.instant('EDITAPPLICATION_Title', { componentName: selectedComponent.name }),
                modalSize: 'modal-lg',
                templateUrl: 'views/addapplicationwizard.html',
                controller: 'AddApplicationWizardController as Application',
                params: {
                    service: self.service,
                    action: 'edit',
                    id: selectedComponent.id
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                    self.getserviceData();
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    addApplicationWizard.modal.dismiss();
                }
            });
            addApplicationWizard.modal.show();
        };
        //Get the service data
        ServicesvgController.prototype.getserviceData = function () {
            var _this = this;
            var self = this;
            self.removePopovers();
            if (self.pauseRefresh) {
                //skip call and wait for next refresh cycle
                self.$timeout.cancel(self.serviceTimeout);
                self.serviceTimeout = self.$timeout(function () {
                    self.getserviceData();
                }, self.serviceTimeoutTime); //1 minute
                return;
            }
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            //Shut everything down and start over
            self.selectedComponent = {};
            self.render = false;
            self.service = '';
            $('#TemplateBuilderSVGLines').empty();
            $('#TemplateBuilderSVGHoverLines').empty();
            $('#TemplateBuilderSVGSelectedLines').empty();
            $(window).off("resize");
            if (self.windowtimer)
                self.$timeout.cancel(self.windowtimer);
            if (self.pollwindowtimer)
                self.$timeout.cancel(self.pollwindowtimer);
            self.mostItems = 0;
            self.intervalX = 0;
            //New call for data
            self.$http.post(self.Commands.data.services.getServiceById, { id: self.serviceId })
                .then(function (data) {
                self.service = data.data.responseObj;
                //self.service = { "id": "ff80808166e9f49e0166ea52b8140f5a", "name": "4nodeHCI", "template": "4nodeHCI", "health": "yellow", "deployedBy": "admin", "deployedOn": "2018-11-06T19:34:20.188Z", "description": null, "errors": null, "firmwareCompliant": "noncompliant", "firmwarePackageName": "RCM 3.3.4 (VxFlex OS 2.6.1) (2)", "brownField": false, "count_application": 0, "count_cluster": 2, "count_server": 4, "count_storage": 2, "count_switch": 0, "count_vm": 4, "count_scaleio": 1, "components": [{ "id": "cedc09e6-2db8-4d85-a2a6-c438171e5835", "name": "avgw140-261-123", "type": "scaleio", "subtype": "HYPERCONVERGED", "componentid": "component-scaleio-gateway-1", "identifier": null, "helptext": null, "relatedcomponents": [{ "id": "f7cb158f-8db4-4bd0-a94f-d23203539937", "name": "Hyperconverged-3", "installOrder": null, "subtype": null }, { "id": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "name": "Hyperconverged", "installOrder": null, "subtype": null }, { "id": "c3e8e319-9599-4ec0-9508-3807b113c52c", "name": "Hyperconverged-4", "installOrder": null, "subtype": null }, { "id": "b3234398-576f-413c-bb6c-b59ff11bd85a", "name": "Hyperconverged-2", "installOrder": null, "subtype": null }], "categories": [], "referenceid": null, "referenceip": null, "referenceipurl": "https://null", "showNetworkInfo": null, "network": null, "newComponent": null, "cloned": false, "continueClicked": null, "AsmGUID": "scaleio-100.68.85.140", "puppetCertName": "scaleio-avgw140-261-123", "clonedFromId": null, "allowClone": false, "isComponentValid": true, "raid": null, "configfilename": null, "instances": 1, "errorObj": null }, { "id": "a8a25147-5e58-43a3-85c1-465ce9f6a8bf", "name": "14G-ESXv65CustomerVCSA.asm.delllabs.net", "type": "cluster", "subtype": "HYPERCONVERGED", "componentid": "component-cluster-vcenter-1", "identifier": null, "helptext": null, "relatedcomponents": [{ "id": "f7cb158f-8db4-4bd0-a94f-d23203539937", "name": "Hyperconverged-3", "installOrder": null, "subtype": null }, { "id": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "name": "Hyperconverged", "installOrder": null, "subtype": null }, { "id": "c3e8e319-9599-4ec0-9508-3807b113c52c", "name": "Hyperconverged-4", "installOrder": null, "subtype": null }, { "id": "b3234398-576f-413c-bb6c-b59ff11bd85a", "name": "Hyperconverged-2", "installOrder": null, "subtype": null }], "categories": [], "referenceid": null, "referenceip": null, "referenceipurl": "https://null", "showNetworkInfo": null, "network": null, "newComponent": null, "cloned": false, "continueClicked": null, "AsmGUID": "vcenter-100.68.77.55", "puppetCertName": "vcenter-14g-esxv65customervcsa.asm.delllabs.net", "clonedFromId": null, "allowClone": false, "isComponentValid": true, "raid": null, "configfilename": null, "instances": 1, "errorObj": null }, { "id": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "name": "server1", "type": "server", "subtype": "HYPERVISOR", "componentid": "component-server-1", "identifier": "host-5934", "helptext": null, "relatedcomponents": [{ "id": "cedc09e6-2db8-4d85-a2a6-c438171e5835", "name": "avgw140-261-123", "installOrder": null, "subtype": null }, { "id": "1a07d408-8f43-4e84-be75-58f84f562492", "name": "pk-scl-vol-bf", "installOrder": null, "subtype": null }, { "id": "a8a25147-5e58-43a3-85c1-465ce9f6a8bf", "name": "VMWare Cluster", "installOrder": null, "subtype": null }, { "id": "061394da-2514-4575-bb2e-e69f3adb1367", "name": "pk-scl-vol-gf", "installOrder": null, "subtype": null }], "categories": [], "referenceid": null, "referenceip": null, "referenceipurl": "https://null", "showNetworkInfo": true, "network": null, "newComponent": null, "cloned": false, "continueClicked": null, "AsmGUID": "ff808081655d48c801655d86300302e9", "puppetCertName": "rackserver-jgkp7m2", "clonedFromId": null, "allowClone": true, "isComponentValid": true, "raid": null, "configfilename": null, "instances": 1, "errorObj": null }, { "id": "b3234398-576f-413c-bb6c-b59ff11bd85a", "name": "server2", "type": "server", "subtype": "HYPERVISOR", "componentid": "component-server-1", "identifier": "host-5683", "helptext": null, "relatedcomponents": [{ "id": "cedc09e6-2db8-4d85-a2a6-c438171e5835", "name": "avgw140-261-123", "installOrder": null, "subtype": null }, { "id": "1a07d408-8f43-4e84-be75-58f84f562492", "name": "pk-scl-vol-bf", "installOrder": null, "subtype": null }, { "id": "a8a25147-5e58-43a3-85c1-465ce9f6a8bf", "name": "VMWare Cluster", "installOrder": null, "subtype": null }, { "id": "061394da-2514-4575-bb2e-e69f3adb1367", "name": "pk-scl-vol-gf", "installOrder": null, "subtype": null }], "categories": [], "referenceid": null, "referenceip": null, "referenceipurl": "https://null", "showNetworkInfo": true, "network": null, "newComponent": null, "cloned": true, "continueClicked": null, "AsmGUID": "ff808081655d48c801655d862fc702e8", "puppetCertName": "rackserver-jgks7m2", "clonedFromId": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "allowClone": true, "isComponentValid": true, "raid": null, "configfilename": null, "instances": 1, "errorObj": null }, { "id": "f7cb158f-8db4-4bd0-a94f-d23203539937", "name": "server3", "type": "server", "subtype": "HYPERVISOR", "componentid": "component-server-1", "identifier": "host-5685", "helptext": null, "relatedcomponents": [{ "id": "cedc09e6-2db8-4d85-a2a6-c438171e5835", "name": "avgw140-261-123", "installOrder": null, "subtype": null }, { "id": "1a07d408-8f43-4e84-be75-58f84f562492", "name": "pk-scl-vol-bf", "installOrder": null, "subtype": null }, { "id": "a8a25147-5e58-43a3-85c1-465ce9f6a8bf", "name": "VMWare Cluster", "installOrder": null, "subtype": null }, { "id": "061394da-2514-4575-bb2e-e69f3adb1367", "name": "pk-scl-vol-gf", "installOrder": null, "subtype": null }], "categories": [], "referenceid": null, "referenceip": null, "referenceipurl": "https://null", "showNetworkInfo": true, "network": null, "newComponent": null, "cloned": true, "continueClicked": null, "AsmGUID": "ff808081655d48c801655d862f8f02e7", "puppetCertName": "rackserver-jgkr7m2", "clonedFromId": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "allowClone": true, "isComponentValid": true, "raid": null, "configfilename": null, "instances": 1, "errorObj": null }, { "id": "c3e8e319-9599-4ec0-9508-3807b113c52c", "name": "server4", "type": "server", "subtype": "HYPERVISOR", "componentid": "component-server-1", "identifier": "host-5687", "helptext": null, "relatedcomponents": [{ "id": "cedc09e6-2db8-4d85-a2a6-c438171e5835", "name": "avgw140-261-123", "installOrder": null, "subtype": null }, { "id": "1a07d408-8f43-4e84-be75-58f84f562492", "name": "pk-scl-vol-bf", "installOrder": null, "subtype": null }, { "id": "a8a25147-5e58-43a3-85c1-465ce9f6a8bf", "name": "VMWare Cluster", "installOrder": null, "subtype": null }, { "id": "061394da-2514-4575-bb2e-e69f3adb1367", "name": "pk-scl-vol-gf", "installOrder": null, "subtype": null }], "categories": [], "referenceid": null, "referenceip": null, "referenceipurl": "https://null", "showNetworkInfo": true, "network": null, "newComponent": null, "cloned": true, "continueClicked": null, "AsmGUID": "ff808081655da6b601655deb48fb01a5", "puppetCertName": "rackserver-9jlp9n2", "clonedFromId": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "allowClone": true, "isComponentValid": true, "raid": null, "configfilename": null, "instances": 1, "errorObj": null }, { "id": "061394da-2514-4575-bb2e-e69f3adb1367", "name": "pk-scl-vol-gf", "type": "storage", "subtype": null, "componentid": "component-flexos-storage-1", "identifier": "b8e8d51700000000", "helptext": null, "relatedcomponents": [{ "id": "f7cb158f-8db4-4bd0-a94f-d23203539937", "name": "server3", "installOrder": null, "subtype": null }, { "id": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "name": "server1", "installOrder": null, "subtype": null }, { "id": "c3e8e319-9599-4ec0-9508-3807b113c52c", "name": "server4", "installOrder": null, "subtype": null }, { "id": "b3234398-576f-413c-bb6c-b59ff11bd85a", "name": "server2", "installOrder": null, "subtype": null }], "categories": [], "referenceid": null, "referenceip": null, "referenceipurl": "https://null", "showNetworkInfo": null, "network": null, "newComponent": null, "cloned": false, "continueClicked": null, "AsmGUID": "scaleio-100.68.85.140", "puppetCertName": "scaleio-avgw140-261-123", "clonedFromId": "component-flexos-storage-1", "allowClone": false, "isComponentValid": true, "raid": null, "configfilename": null, "instances": 1, "errorObj": null }, { "id": "1a07d408-8f43-4e84-be75-58f84f562492", "name": "pk-scl-vol-bf", "type": "storage", "subtype": null, "componentid": "component-flexos-storage-1", "identifier": "b8e8d51800000001", "helptext": null, "relatedcomponents": [{ "id": "f7cb158f-8db4-4bd0-a94f-d23203539937", "name": "server3", "installOrder": null, "subtype": null }, { "id": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "name": "server1", "installOrder": null, "subtype": null }, { "id": "c3e8e319-9599-4ec0-9508-3807b113c52c", "name": "server4", "installOrder": null, "subtype": null }, { "id": "b3234398-576f-413c-bb6c-b59ff11bd85a", "name": "server2", "installOrder": null, "subtype": null }], "categories": [], "referenceid": null, "referenceip": null, "referenceipurl": "https://null", "showNetworkInfo": null, "network": null, "newComponent": null, "cloned": false, "continueClicked": null, "AsmGUID": "scaleio-100.68.85.140", "puppetCertName": "scaleio-avgw140-261-123", "clonedFromId": null, "allowClone": false, "isComponentValid": true, "raid": null, "configfilename": null, "instances": 1, "errorObj": null }], "expires": null, "servers": 0, "ram": 0, "processors": 0, "arrays": 0, "volumes": 0, "vlans": 0, "networktype": null, "vms": 0, "clusters": 0, "type": null, "state": "Deployed", "createddate": "2018-11-06T18:39:41.588Z", "createdBy": null, "priority": null, "compute": 0, "storage": 0, "network": 0, "serverlist": [{ "id": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "health": "green", "healthmessage": "OK", "ipAddress": "100.68.64.154", "serviceTag": "JGKP7M2", "deviceType": "RackServer", "compliant": "noncompliant", "state": null, "model": null, "displayserverpools": null, "manufacturer": null, "processorcount": -1, "processor": null, "memory": "N/A", "ipaddressurl": "https://100.68.64.154", "deviceid": "JGKP7M2", "hostname": "server1", "chassisname": null, "groupname": null, "dnsdracname": null, "storagecentername": null, "replayprofile": null, "newserverpool": "Abels14GPool", "availability": null, "status": "complete", "nics": 0, "servicelist": [], "name": null, "resourcename": null, "partitions": null, "profile": null, "statusText": null, "firmwareName": null, "template": null, "lastInventory": null, "lastComplianceCheck": null, "discoveredOn": null, "blades": 0, "canpoweron": false, "canpoweroff": false, "candelete": true, "os": null, "storage": null, "ipaddresses": null, "ipaddresstype": "management", "powerstate": null, "location": null, "systemstatus": null, "groupmembers": null, "volumes": null, "volumesup": null, "deviceidtype": "servicetag", "snapshots": null, "freegroupspace": null, "serialnumber": null, "datacenters": null, "networks": [], "clusters": null, "hosts": null, "virtualmachines": null, "dnsname": null, "cpu": null, "freediskspace": null, "online": null, "luns": null, "hostgroups": null, "volumelist": [], "serverpool": "Abels14GPool", "hypervisorIPAddress": null, "aggregates": null, "disks": null, "vm_hostname": "", "vm_ostype": "", "vm_cpus": "", "vm_disksize": "", "vm_memory": "", "ipaddresslist": [{ "ipaddress": "100.68.77.134", "ipaddressurl": "https://100.68.77.134" }], "firmwarecomponents": [], "showcompliancereport": false, "spaipaddress": null, "spaipaddressurl": null, "spbipaddress": null, "spbipaddressurl": null, "datacentername": null, "clustername": null, "asmGUID": "ff808081655d48c801655d86300302e9", "deviceDetails": null, "protectionDomain": null, "storagePools": [], "osMode": "Hyper-converged", "mdmRole": "Secondary", "vxflexosmanagementipaddress": "100.68.85.133", "primarymdmipaddress": null }, { "id": "b3234398-576f-413c-bb6c-b59ff11bd85a", "health": "green", "healthmessage": "OK", "ipAddress": "100.68.64.155", "serviceTag": "JGKS7M2", "deviceType": "RackServer", "compliant": "noncompliant", "state": null, "model": null, "displayserverpools": null, "manufacturer": null, "processorcount": -1, "processor": null, "memory": "N/A", "ipaddressurl": "https://100.68.64.155", "deviceid": "JGKS7M2", "hostname": "server2", "chassisname": null, "groupname": null, "dnsdracname": null, "storagecentername": null, "replayprofile": null, "newserverpool": "Abels14GPool", "availability": null, "status": "complete", "nics": 0, "servicelist": [], "name": null, "resourcename": null, "partitions": null, "profile": null, "statusText": null, "firmwareName": null, "template": null, "lastInventory": null, "lastComplianceCheck": null, "discoveredOn": null, "blades": 0, "canpoweron": false, "canpoweroff": false, "candelete": true, "os": null, "storage": null, "ipaddresses": null, "ipaddresstype": "management", "powerstate": null, "location": null, "systemstatus": null, "groupmembers": null, "volumes": null, "volumesup": null, "deviceidtype": "servicetag", "snapshots": null, "freegroupspace": null, "serialnumber": null, "datacenters": null, "networks": [], "clusters": null, "hosts": null, "virtualmachines": null, "dnsname": null, "cpu": null, "freediskspace": null, "online": null, "luns": null, "hostgroups": null, "volumelist": [], "serverpool": "Abels14GPool", "hypervisorIPAddress": null, "aggregates": null, "disks": null, "vm_hostname": "", "vm_ostype": "", "vm_cpus": "", "vm_disksize": "", "vm_memory": "", "ipaddresslist": [{ "ipaddress": "100.68.77.135", "ipaddressurl": "https://100.68.77.135" }], "firmwarecomponents": [], "showcompliancereport": false, "spaipaddress": null, "spaipaddressurl": null, "spbipaddress": null, "spbipaddressurl": null, "datacentername": null, "clustername": null, "asmGUID": "ff808081655d48c801655d862fc702e8", "deviceDetails": null, "protectionDomain": null, "storagePools": [], "osMode": "Hyper-converged", "mdmRole": "Standby MDM", "vxflexosmanagementipaddress": "100.68.85.134", "primarymdmipaddress": null }, { "id": "f7cb158f-8db4-4bd0-a94f-d23203539937", "health": "green", "healthmessage": "OK", "ipAddress": "100.68.64.156", "serviceTag": "JGKR7M2", "deviceType": "RackServer", "compliant": "noncompliant", "state": null, "model": null, "displayserverpools": null, "manufacturer": null, "processorcount": -1, "processor": null, "memory": "N/A", "ipaddressurl": "https://100.68.64.156", "deviceid": "JGKR7M2", "hostname": "server3", "chassisname": null, "groupname": null, "dnsdracname": null, "storagecentername": null, "replayprofile": null, "newserverpool": "Abels14GPool", "availability": null, "status": "complete", "nics": 0, "servicelist": [], "name": null, "resourcename": null, "partitions": null, "profile": null, "statusText": null, "firmwareName": null, "template": null, "lastInventory": null, "lastComplianceCheck": null, "discoveredOn": null, "blades": 0, "canpoweron": false, "canpoweroff": false, "candelete": true, "os": null, "storage": null, "ipaddresses": null, "ipaddresstype": "management", "powerstate": null, "location": null, "systemstatus": null, "groupmembers": null, "volumes": null, "volumesup": null, "deviceidtype": "servicetag", "snapshots": null, "freegroupspace": null, "serialnumber": null, "datacenters": null, "networks": [], "clusters": null, "hosts": null, "virtualmachines": null, "dnsname": null, "cpu": null, "freediskspace": null, "online": null, "luns": null, "hostgroups": null, "volumelist": [], "serverpool": "Abels14GPool", "hypervisorIPAddress": null, "aggregates": null, "disks": null, "vm_hostname": "", "vm_ostype": "", "vm_cpus": "", "vm_disksize": "", "vm_memory": "", "ipaddresslist": [{ "ipaddress": "100.68.77.136", "ipaddressurl": "https://100.68.77.136" }], "firmwarecomponents": [], "showcompliancereport": false, "spaipaddress": null, "spaipaddressurl": null, "spbipaddress": null, "spbipaddressurl": null, "datacentername": null, "clustername": null, "asmGUID": "ff808081655d48c801655d862f8f02e7", "deviceDetails": null, "protectionDomain": null, "storagePools": [], "osMode": "Hyper-converged", "mdmRole": "Tie Breaker", "vxflexosmanagementipaddress": "100.68.85.135", "primarymdmipaddress": null }, { "id": "c3e8e319-9599-4ec0-9508-3807b113c52c", "health": "green", "healthmessage": "OK", "ipAddress": "100.68.65.170", "serviceTag": "9JLP9N2", "deviceType": "RackServer", "compliant": "noncompliant", "state": null, "model": null, "displayserverpools": null, "manufacturer": null, "processorcount": -1, "processor": null, "memory": "N/A", "ipaddressurl": "https://100.68.65.170", "deviceid": "9JLP9N2", "hostname": "server4", "chassisname": null, "groupname": null, "dnsdracname": null, "storagecentername": null, "replayprofile": null, "newserverpool": "Abels14GPool", "availability": null, "status": "complete", "nics": 0, "servicelist": [], "name": null, "resourcename": null, "partitions": null, "profile": null, "statusText": null, "firmwareName": null, "template": null, "lastInventory": null, "lastComplianceCheck": null, "discoveredOn": null, "blades": 0, "canpoweron": false, "canpoweroff": false, "candelete": true, "os": null, "storage": null, "ipaddresses": null, "ipaddresstype": "management", "powerstate": null, "location": null, "systemstatus": null, "groupmembers": null, "volumes": null, "volumesup": null, "deviceidtype": "servicetag", "snapshots": null, "freegroupspace": null, "serialnumber": null, "datacenters": null, "networks": [], "clusters": null, "hosts": null, "virtualmachines": null, "dnsname": null, "cpu": null, "freediskspace": null, "online": null, "luns": null, "hostgroups": null, "volumelist": [], "serverpool": "Abels14GPool", "hypervisorIPAddress": null, "aggregates": null, "disks": null, "vm_hostname": "", "vm_ostype": "", "vm_cpus": "", "vm_disksize": "", "vm_memory": "", "ipaddresslist": [{ "ipaddress": "100.68.77.137", "ipaddressurl": "https://100.68.77.137" }], "firmwarecomponents": [], "showcompliancereport": false, "spaipaddress": null, "spaipaddressurl": null, "spbipaddress": null, "spbipaddressurl": null, "datacentername": null, "clustername": null, "asmGUID": "ff808081655da6b601655deb48fb01a5", "deviceDetails": null, "protectionDomain": null, "storagePools": [], "osMode": "Hyper-converged", "mdmRole": "Primary", "vxflexosmanagementipaddress": "100.68.85.136", "primarymdmipaddress": null }], "storagelist": [{ "id": "061394da-2514-4575-bb2e-e69f3adb1367", "health": "unknown", "healthmessage": "", "ipAddress": "100.68.85.140", "serviceTag": "avgw140-261-123", "deviceType": "scaleio", "compliant": "compliant", "state": null, "model": null, "displayserverpools": null, "manufacturer": null, "processorcount": -1, "processor": null, "memory": "N/A", "ipaddressurl": "https://100.68.85.140", "deviceid": "avgw140-261-123", "hostname": null, "chassisname": null, "groupname": null, "dnsdracname": null, "storagecentername": null, "replayprofile": null, "newserverpool": null, "availability": null, "status": "complete", "nics": 0, "servicelist": [], "name": null, "resourcename": null, "partitions": null, "profile": null, "statusText": null, "firmwareName": null, "template": null, "lastInventory": null, "lastComplianceCheck": null, "discoveredOn": null, "blades": 0, "canpoweron": false, "canpoweroff": false, "candelete": true, "os": null, "storage": null, "ipaddresses": null, "ipaddresstype": "management", "powerstate": null, "location": null, "systemstatus": null, "groupmembers": null, "volumes": null, "volumesup": null, "deviceidtype": "servicetag", "snapshots": null, "freegroupspace": null, "serialnumber": null, "datacenters": null, "networks": [], "clusters": null, "hosts": null, "virtualmachines": null, "dnsname": null, "cpu": null, "freediskspace": null, "online": null, "luns": null, "hostgroups": null, "volumelist": [{ "id": "b8e8d51700000000", "name": "pk-scl-vol-gf", "size": "24.0 GB" }], "serverpool": null, "hypervisorIPAddress": null, "aggregates": null, "disks": null, "vm_hostname": "", "vm_ostype": "", "vm_cpus": "", "vm_disksize": "", "vm_memory": "", "ipaddresslist": [], "firmwarecomponents": [], "showcompliancereport": false, "spaipaddress": null, "spaipaddressurl": null, "spbipaddress": null, "spbipaddressurl": null, "datacentername": null, "clustername": null, "asmGUID": "scaleio-100.68.85.140", "deviceDetails": null, "protectionDomain": null, "storagePools": [], "osMode": null, "mdmRole": null, "vxflexosmanagementipaddress": null, "primarymdmipaddress": null }, { "id": "1a07d408-8f43-4e84-be75-58f84f562492", "health": "unknown", "healthmessage": "", "ipAddress": "100.68.85.140", "serviceTag": "avgw140-261-123", "deviceType": "scaleio", "compliant": "compliant", "state": null, "model": null, "displayserverpools": null, "manufacturer": null, "processorcount": -1, "processor": null, "memory": "N/A", "ipaddressurl": "https://100.68.85.140", "deviceid": "avgw140-261-123", "hostname": null, "chassisname": null, "groupname": null, "dnsdracname": null, "storagecentername": null, "replayprofile": null, "newserverpool": null, "availability": null, "status": "complete", "nics": 0, "servicelist": [], "name": null, "resourcename": null, "partitions": null, "profile": null, "statusText": null, "firmwareName": null, "template": null, "lastInventory": null, "lastComplianceCheck": null, "discoveredOn": null, "blades": 0, "canpoweron": false, "canpoweroff": false, "candelete": true, "os": null, "storage": null, "ipaddresses": null, "ipaddresstype": "management", "powerstate": null, "location": null, "systemstatus": null, "groupmembers": null, "volumes": null, "volumesup": null, "deviceidtype": "servicetag", "snapshots": null, "freegroupspace": null, "serialnumber": null, "datacenters": null, "networks": [], "clusters": null, "hosts": null, "virtualmachines": null, "dnsname": null, "cpu": null, "freediskspace": null, "online": null, "luns": null, "hostgroups": null, "volumelist": [{ "id": "b8e8d51800000001", "name": "pk-scl-vol-bf", "size": "56.0 GB" }], "serverpool": null, "hypervisorIPAddress": null, "aggregates": null, "disks": null, "vm_hostname": "", "vm_ostype": "", "vm_cpus": "", "vm_disksize": "", "vm_memory": "", "ipaddresslist": [], "firmwarecomponents": [], "showcompliancereport": false, "spaipaddress": null, "spaipaddressurl": null, "spbipaddress": null, "spbipaddressurl": null, "datacentername": null, "clustername": null, "asmGUID": "scaleio-100.68.85.140", "deviceDetails": null, "protectionDomain": null, "storagePools": [], "osMode": null, "mdmRole": null, "vxflexosmanagementipaddress": null, "primarymdmipaddress": null }], "networklist": [], "clusterlist": [{ "id": "a8a25147-5e58-43a3-85c1-465ce9f6a8bf", "health": "unknown", "healthmessage": "", "ipAddress": "100.68.77.55", "serviceTag": "14G-ESXv65CustomerVCSA.asm.delllabs.net", "deviceType": "vcenter", "compliant": "compliant", "state": null, "model": null, "displayserverpools": null, "manufacturer": null, "processorcount": -1, "processor": null, "memory": "N/A", "ipaddressurl": "https://100.68.77.55", "deviceid": "14G-ESXv65CustomerVCSA.asm.delllabs.net", "hostname": null, "chassisname": null, "groupname": null, "dnsdracname": null, "storagecentername": null, "replayprofile": null, "newserverpool": null, "availability": null, "status": "complete", "nics": 0, "servicelist": [], "name": null, "resourcename": null, "partitions": null, "profile": null, "statusText": null, "firmwareName": null, "template": null, "lastInventory": null, "lastComplianceCheck": null, "discoveredOn": null, "blades": 0, "canpoweron": false, "canpoweroff": false, "candelete": true, "os": null, "storage": null, "ipaddresses": null, "ipaddresstype": "management", "powerstate": null, "location": null, "systemstatus": null, "groupmembers": null, "volumes": null, "volumesup": null, "deviceidtype": "servicetag", "snapshots": null, "freegroupspace": null, "serialnumber": null, "datacenters": null, "networks": [], "clusters": null, "hosts": null, "virtualmachines": null, "dnsname": null, "cpu": null, "freediskspace": null, "online": null, "luns": null, "hostgroups": null, "volumelist": [], "serverpool": null, "hypervisorIPAddress": null, "aggregates": null, "disks": null, "vm_hostname": "", "vm_ostype": "", "vm_cpus": "", "vm_disksize": "", "vm_memory": "", "ipaddresslist": [], "firmwarecomponents": [], "showcompliancereport": false, "spaipaddress": null, "spaipaddressurl": null, "spbipaddress": null, "spbipaddressurl": null, "datacentername": "AVDC1", "clustername": "AVCL1", "asmGUID": "vcenter-100.68.77.55", "deviceDetails": null, "protectionDomain": null, "storagePools": [], "osMode": null, "mdmRole": null, "vxflexosmanagementipaddress": null, "primarymdmipaddress": null }], "applicationlist": [], "vmlist": [], "activityLogs": [{ "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The 4nodeHCI deployment is started.", "logTimeStamp": "2018-11-12 08:42:07 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKP7M2 100.68.64.154 networking on Nexus9000 100.68.69.164 is configuring.", "logTimeStamp": "2018-11-12 08:43:56 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKS7M2 100.68.64.155 networking on Nexus9000 100.68.69.164 is configuring.", "logTimeStamp": "2018-11-12 08:43:56 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKR7M2 100.68.64.156 networking on Nexus9000 100.68.69.164 is configuring.", "logTimeStamp": "2018-11-12 08:43:56 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node 9JLP9N2 100.68.65.170 networking on Nexus9000 100.68.69.164 is configuring.", "logTimeStamp": "2018-11-12 08:43:56 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKP7M2 100.68.64.154 networking on Nexus9000 100.68.69.165 is configuring.", "logTimeStamp": "2018-11-12 08:44:31 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKS7M2 100.68.64.155 networking on Nexus9000 100.68.69.165 is configuring.", "logTimeStamp": "2018-11-12 08:44:31 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKR7M2 100.68.64.156 networking on Nexus9000 100.68.69.165 is configuring.", "logTimeStamp": "2018-11-12 08:44:31 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node 9JLP9N2 100.68.65.170 networking on Nexus9000 100.68.69.165 is configuring.", "logTimeStamp": "2018-11-12 08:44:31 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The initial configuration for node components is processing.", "logTimeStamp": "2018-11-12 08:47:17 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The initial configuration for component server2 is complete.", "logTimeStamp": "2018-11-12 08:47:17 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The initial configuration for component server4 is complete.", "logTimeStamp": "2018-11-12 08:47:17 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The initial configuration for component server3 is complete.", "logTimeStamp": "2018-11-12 08:47:17 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The initial configuration for component server1 is complete.", "logTimeStamp": "2018-11-12 08:47:18 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The initial processing of node components is complete.", "logTimeStamp": "2018-11-12 08:47:18 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of storage components started.", "logTimeStamp": "2018-11-12 08:47:18 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS volume pk-scl-vol-bf is being mapped.", "logTimeStamp": "2018-11-12 08:47:18 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS datastores are being mapped.", "logTimeStamp": "2018-11-12 08:47:21 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The pk-scl-vol-bf deployment is complete.", "logTimeStamp": "2018-11-12 08:47:53 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS volume pk-scl-vol-gf is being mapped.", "logTimeStamp": "2018-11-12 08:47:53 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS datastores are being mapped.", "logTimeStamp": "2018-11-12 08:47:56 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The pk-scl-vol-gf deployment is complete.", "logTimeStamp": "2018-11-12 08:48:16 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of storage components is complete.", "logTimeStamp": "2018-11-12 08:48:16 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of node components started.", "logTimeStamp": "2018-11-12 08:48:16 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The server1 deployment is complete.", "logTimeStamp": "2018-11-12 08:48:16 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The server3 deployment is complete.", "logTimeStamp": "2018-11-12 08:48:16 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The server4 deployment is complete.", "logTimeStamp": "2018-11-12 08:48:16 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The server2 deployment is complete.", "logTimeStamp": "2018-11-12 08:48:16 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of node components is complete.", "logTimeStamp": "2018-11-12 08:48:16 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of cluster components started.", "logTimeStamp": "2018-11-12 08:48:16 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The 14G-ESXv65CustomerVCSA.asm.delllabs.net deployment is complete.", "logTimeStamp": "2018-11-12 09:06:27 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of cluster components is complete.", "logTimeStamp": "2018-11-12 09:06:27 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKP7M2 100.68.64.154 networking on Nexus9000 100.68.69.164 is configuring.", "logTimeStamp": "2018-11-12 09:07:03 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKS7M2 100.68.64.155 networking on Nexus9000 100.68.69.164 is configuring.", "logTimeStamp": "2018-11-12 09:07:03 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKR7M2 100.68.64.156 networking on Nexus9000 100.68.69.164 is configuring.", "logTimeStamp": "2018-11-12 09:07:03 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node 9JLP9N2 100.68.65.170 networking on Nexus9000 100.68.69.164 is configuring.", "logTimeStamp": "2018-11-12 09:07:04 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKP7M2 100.68.64.154 networking on Nexus9000 100.68.69.165 is configuring.", "logTimeStamp": "2018-11-12 09:07:39 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKS7M2 100.68.64.155 networking on Nexus9000 100.68.69.165 is configuring.", "logTimeStamp": "2018-11-12 09:07:39 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node JGKR7M2 100.68.64.156 networking on Nexus9000 100.68.69.165 is configuring.", "logTimeStamp": "2018-11-12 09:07:40 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The node 9JLP9N2 100.68.65.170 networking on Nexus9000 100.68.69.165 is configuring.", "logTimeStamp": "2018-11-12 09:07:40 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of virtualmachine components started.", "logTimeStamp": "2018-11-12 09:10:09 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The svm-server1 deployment is complete.", "logTimeStamp": "2018-11-12 09:10:10 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The svm-server2 deployment is complete.", "logTimeStamp": "2018-11-12 09:10:10 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The svm-server4 deployment is complete.", "logTimeStamp": "2018-11-12 09:10:10 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The svm-server3 deployment is complete.", "logTimeStamp": "2018-11-12 09:10:10 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of virtualmachine components is complete.", "logTimeStamp": "2018-11-12 09:10:10 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of VxFlex OS components started.", "logTimeStamp": "2018-11-12 09:10:10 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS Gateway is being configured.", "logTimeStamp": "2018-11-12 09:10:13 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS Gateway script is being uploaded to 100.68.85.140.", "logTimeStamp": "2018-11-12 09:10:44 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "Completed execution of VxFlex OS gateway script on 100.68.85.140", "logTimeStamp": "2018-11-12 09:10:49 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The disks are being added to the VxFlex OS storage pools.", "logTimeStamp": "2018-11-12 09:11:30 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS deployment is complete.", "logTimeStamp": "2018-11-12 09:11:52 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "VxFlex OS deployment complete", "logTimeStamp": "2018-11-12 09:11:52 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of VxFlex OS components is complete.", "logTimeStamp": "2018-11-12 09:11:52 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of storage components started.", "logTimeStamp": "2018-11-12 09:11:52 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS volume pk-scl-vol-gf is being mapped.", "logTimeStamp": "2018-11-12 09:11:53 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS volume pk-scl-vol-bf is being mapped.", "logTimeStamp": "2018-11-12 09:11:55 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS datastores are being mapped.", "logTimeStamp": "2018-11-12 09:11:58 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The pk-scl-vol-bf deployment is complete.", "logTimeStamp": "2018-11-12 09:12:19 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The VxFlex OS datastores are being mapped.", "logTimeStamp": "2018-11-12 09:12:19 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The pk-scl-vol-gf deployment is complete.", "logTimeStamp": "2018-11-12 09:12:42 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The processing of storage components is complete.", "logTimeStamp": "2018-11-12 09:12:42 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "The 4nodeHCI deployment is complete.", "logTimeStamp": "2018-11-12 09:12:42 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "Updating Resource Inventories", "logTimeStamp": "2018-11-12 09:12:48 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "Updated Resource Inventory for Device 14G-ESXv65CustomerVCSA.asm.delllabs.net", "logTimeStamp": "2018-11-12 09:17:37 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "Updated Resource Inventory for Device avgw140-261-123", "logTimeStamp": "2018-11-12 09:17:45 -0Z", "logUser": null, "progress": 0 }, { "id": null, "severity": null, "category": null, "description": null, "date": null, "user": null, "logDetailMessage": null, "logMessage": "Finished Updating Resource Inventories", "logTimeStamp": "2018-11-12 09:17:48 -0Z", "logUser": null, "progress": 0 }], "profile": null, "componentstatus": [{ "componentid": "cedc09e6-2db8-4d85-a2a6-c438171e5835", "deviceid": "cedc09e6-2db8-4d85-a2a6-c438171e5835", "devicetype": "scaleio", "status": "complete", "statustime": "2018-11-12 09:11:52 -0600", "statusmessage": null, "resourcestate": "complete" }, { "componentid": "a8a25147-5e58-43a3-85c1-465ce9f6a8bf", "deviceid": "a8a25147-5e58-43a3-85c1-465ce9f6a8bf", "devicetype": "cluster", "status": "complete", "statustime": "2018-11-12 09:06:27 -0600", "statusmessage": null, "resourcestate": "complete" }, { "componentid": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "deviceid": "9c4f0752-b899-4308-a9f6-35ad60be6c43", "devicetype": "server", "status": "complete", "statustime": "2018-11-12 08:48:16 -0600", "statusmessage": "Firmware does not meet the default firmware repository requirements and is non-compliant.", "resourcestate": "warning" }, { "componentid": "b3234398-576f-413c-bb6c-b59ff11bd85a", "deviceid": "b3234398-576f-413c-bb6c-b59ff11bd85a", "devicetype": "server", "status": "complete", "statustime": "2018-11-12 08:48:16 -0600", "statusmessage": "Firmware does not meet the default firmware repository requirements and is non-compliant.", "resourcestate": "warning" }, { "componentid": "f7cb158f-8db4-4bd0-a94f-d23203539937", "deviceid": "f7cb158f-8db4-4bd0-a94f-d23203539937", "devicetype": "server", "status": "complete", "statustime": "2018-11-12 08:48:16 -0600", "statusmessage": "Firmware does not meet the default firmware repository requirements and is non-compliant.", "resourcestate": "warning" }, { "componentid": "c3e8e319-9599-4ec0-9508-3807b113c52c", "deviceid": "c3e8e319-9599-4ec0-9508-3807b113c52c", "devicetype": "server", "status": "complete", "statustime": "2018-11-12 08:48:16 -0600", "statusmessage": "Firmware does not meet the default firmware repository requirements and is non-compliant.", "resourcestate": "warning" }, { "componentid": "061394da-2514-4575-bb2e-e69f3adb1367", "deviceid": "061394da-2514-4575-bb2e-e69f3adb1367", "devicetype": "storage", "status": "complete", "statustime": "2018-11-12 09:12:42 -0600", "statusmessage": null, "resourcestate": "complete" }, { "componentid": "1a07d408-8f43-4e84-be75-58f84f562492", "deviceid": "1a07d408-8f43-4e84-be75-58f84f562492", "devicetype": "storage", "status": "complete", "statustime": "2018-11-12 09:12:20 -0600", "statusmessage": null, "resourcestate": "complete" }], "canMigrate": false, "canScaleupStorage": true, "canScaleupServer": true, "canScaleupVM": false, "canScaleupCluster": false, "canScaleupApplication": false, "forceRetry": false, "allStandardUsers": false, "assignedUsers": [], "owner": "admin", "canEdit": true, "canDelete": true, "canCancel": false, "canRetry": true, "canDeleteResources": true, "manageFirmware": true, "firmwarePackageId": "ff80808166e9f49e0166eb49a9c615fb", "updateServerFirmware": false, "updateNetworkFirmware": false, "updateStorageFirmware": false, "componentUpdateRequired": false, "canScaleupNetwork": true, "templateId": "ff80808166e9f49e0166ea0f277a0ae5", "resourceHealth": "green", "isHyperV": false, "hasVDS": true, "cancelInprogress": false, "scaleiolist": [{ "id": "cedc09e6-2db8-4d85-a2a6-c438171e5835", "health": "unknown", "healthmessage": "", "ipAddress": "100.68.85.140", "serviceTag": "avgw140-261-123", "deviceType": "scaleio", "compliant": "compliant", "state": null, "model": null, "displayserverpools": null, "manufacturer": null, "processorcount": -1, "processor": null, "memory": "N/A", "ipaddressurl": "https://100.68.85.140", "deviceid": "avgw140-261-123", "hostname": "avgw140-261-123", "chassisname": null, "groupname": null, "dnsdracname": null, "storagecentername": null, "replayprofile": null, "newserverpool": null, "availability": null, "status": "complete", "nics": 0, "servicelist": [], "name": null, "resourcename": null, "partitions": null, "profile": null, "statusText": null, "firmwareName": null, "template": null, "lastInventory": null, "lastComplianceCheck": null, "discoveredOn": null, "blades": 0, "canpoweron": false, "canpoweroff": false, "candelete": true, "os": null, "storage": null, "ipaddresses": null, "ipaddresstype": "management", "powerstate": null, "location": null, "systemstatus": null, "groupmembers": null, "volumes": null, "volumesup": null, "deviceidtype": "servicetag", "snapshots": null, "freegroupspace": null, "serialnumber": null, "datacenters": null, "networks": [], "clusters": null, "hosts": null, "virtualmachines": null, "dnsname": null, "cpu": null, "freediskspace": null, "online": null, "luns": null, "hostgroups": null, "volumelist": [], "serverpool": null, "hypervisorIPAddress": null, "aggregates": null, "disks": null, "vm_hostname": "", "vm_ostype": "", "vm_cpus": "", "vm_disksize": "", "vm_memory": "", "ipaddresslist": [], "firmwarecomponents": [], "showcompliancereport": false, "spaipaddress": null, "spaipaddressurl": null, "spbipaddress": null, "spbipaddressurl": null, "datacentername": null, "clustername": null, "asmGUID": "scaleio-100.68.85.140", "deviceDetails": null, "protectionDomain": "PD-1", "storagePools": [{ "id": "SP-SSD-1", "name": "SP-SSD-1", "scaleIOStorageVolumes": [{ "id": "pk-scl-vol-gf", "name": "pk-scl-vol-gf", "size": null, "type": null, "mappedSDCs": null }, { "id": "pk-scl-vol-bf", "name": "pk-scl-vol-bf", "size": null, "type": null, "mappedSDCs": null }] }], "osMode": null, "mdmRole": null, "vxflexosmanagementipaddress": null, "primarymdmipaddress": "100.68.85.136" }], "enableServiceMode": true, "canUpdateInventory": true };
                //console.log('getserviceData, self.service:');
                //console.log(JSON.stringify(self.service));
                self.service.canDelete = (self.service.health == 'unknown' || self.service.health == 'pending');
                //this testing code is forcing brownField to true
                //self.service.brownField = true;
                self.refresh();
                var deviceTypeList = _.concat(angular.copy(self.service.clusterlist), angular.copy(self.service.serverlist), angular.copy(self.service.storagelist), angular.copy(self.service.vmlist), angular.copy(self.service.scaleiolist));
                angular.forEach(self.service.components, function (device) {
                    var match = _.find(self.service.components, { id: device.id });
                    if (match) {
                        var status = _.find(self.service.componentstatus, { componentid: device.id }) || {};
                        switch (status.resourcestate) {
                            case "complete":
                                status.health = "green";
                                break;
                            case "warning":
                                status.health = "yellow";
                                break;
                            case "unknown":
                            case "inprogress":
                            case "inProgress":
                                status.health = "blue";
                                break;
                            case "pending":
                                status.health = "pending";
                                break;
                            case "error":
                                status.health = "red";
                                break;
                            case "cancelled":
                                status.health = "cancelled";
                                break;
                            case "servicemode":
                                status.health = "servicemode";
                        }
                        angular.extend(match, {
                            componentStatus: angular.copy(status),
                            device: angular.copy(device),
                            deviceTypeListData: _.find(deviceTypeList, { id: match.id })
                        });
                        $(document).off('click', '#edit_' + device.id);
                        $(document).on('click', '#edit_' + device.id, function (evt) {
                            $('body').find('.popover').popover('hide');
                            self.editApplications($(evt.currentTarget).attr('id').split('_')[1]);
                        });
                        $(document).off('click', '#delete_' + device.id);
                        $(document).on('click', '#delete_' + device.id, function (evt) {
                            $('body').find('.popover').popover('hide');
                            self.deleteApplications($(evt.currentTarget).attr('id').split('_')[1]);
                        });
                    }
                });
                //console.log('getserviceData, self.service:');
                //console.log(JSON.stringify(self.service));
                //console.log(self.service);
                self.$scope.service = self.service;
                self.addableComponents = [
                    //{
                    //    id: "application",
                    //    name: this.$translate.instant("SERVICE_DETAIL_Add_Application"),
                    //    disabled() {
                    //        return !self.service.canScaleupApplication
                    //    }
                    //},
                    //{
                    //    id: "vm",
                    //    name: this.$translate.instant("SERVICE_DETAIL_Add_VM"),
                    //    disabled() {
                    //        return !self.service.canScaleupVM
                    //    }
                    //},
                    {
                        id: "cluster",
                        name: _this.$translate.instant("SERVICE_DETAIL_Add_Cluster"),
                        disabled: function () {
                            return !self.service.canScaleupCluster;
                        }
                    },
                    {
                        id: "server",
                        name: _this.$translate.instant("SERVICE_DETAIL_Add_Server"),
                        disabled: function () {
                            return !self.service.canScaleupServer;
                        }
                    },
                    {
                        id: "storage",
                        name: _this.$translate.instant("SERVICE_DETAIL_Add_Volume"),
                        disabled: function () {
                            return !self.service.canScaleupStorage;
                        }
                    },
                    {
                        id: "network",
                        name: _this.$translate.instant("SERVICE_DETAIL_Add_Network"),
                        disabled: function () {
                            return !self.service.canScaleupNetwork;
                        }
                    }
                ];
                self.checkForCancelling();
                self.calculatewindowheight();
            })
                .catch(function (data) {
                if (data.status == 404) {
                    self.$location.path('/services/');
                }
                else {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                }
            })
                .finally(function () {
                d.resolve();
                self.$timeout.cancel(self.serviceTimeout);
                self.serviceTimeout = self.$timeout(function () {
                    self.getserviceData();
                }, self.serviceTimeoutTime); //1 minute
            });
        };
        ServicesvgController.prototype.refresh = function () {
            var self = this;
            $('.tooltip').remove();
            $('#TemplateBuilderSVGLines').empty();
            $('#TemplateBuilderSVGHoverLines').empty();
            $('#TemplateBuilderSVGSelectedLines').empty();
            if (self.service) {
                var longestResource = self.getLongestResourceType(self.service.components);
                self.mostComponents = longestResource.type + "Width";
                self.mostItems = longestResource.length;
                self.furthestComponentId = self.getfurthestComponentId(longestResource.type, self.service.components);
                self.drawlines(self.service.components);
                self.dealWithActionableRouteParams();
                setTimeout(self.updateScroll, 500);
            }
            else {
                self.getserviceData();
            }
        };
        ServicesvgController.prototype.dealWithActionableRouteParams = function () {
            var self = this;
            if (self.firmwarereport === 'firmwarereport') {
                self.openFirmwareReport();
            }
            else if (self.firmwarereport === 'edit') {
                self.editService(true);
            }
        };
        ServicesvgController.prototype.getLongestResourceType = function (array) {
            array = _.filter(array, function (component) {
                //exclude networks and applications from count
                return !_.find(["network", "application"], function (type) { return component.type === type; });
            });
            return _.maxBy(_.map(_.groupBy(array, "type"), function (value, key) {
                return { type: key, length: value.length };
            }), "length");
        };
        ServicesvgController.prototype.getfurthestComponentId = function (mostComponentsType, componentsArray) {
            return _.findLast(componentsArray, { type: mostComponentsType }).id;
        };
        ServicesvgController.prototype.drawlines = function (x) {
            var self = this;
            self.$timeout(function () {
                x.forEach(function (component) {
                    component.relatedcomponents.forEach(function (related) {
                        var newFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var newHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var newSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var x1 = $('#serviceBuilderSVG #' + component.id).attr('x');
                        var y1 = $('#serviceBuilderSVG #' + component.id).attr('y');
                        var x2 = $('#serviceBuilderSVG #' + related.id).attr('x');
                        var y2 = $('#serviceBuilderSVG #' + related.id).attr('y');
                        var lanechange = '';
                        var y1Number = 0;
                        var y2Number = 0;
                        if (y1 && y2) {
                            //Helps figure out if we're going up or down
                            y1Number = parseInt(y1.replace(/[^\w\s]/gi, ''));
                            y2Number = parseInt(y2.replace(/[^\w\s]/gi, ''));
                        }
                        if (component.type === 'cluster' || component.type === 'scaleio') {
                            lanechange = '38%';
                        }
                        if (component.type === 'server') {
                            lanechange = '58%';
                        }
                        if (component.type === 'storage') {
                            lanechange = '58%';
                        }
                        if (component.type === 'vm') {
                            lanechange = '18%';
                        }
                        //Are we drawing up?
                        if (y1Number > y2Number) {
                            if (component.type === 'cluster' || component.type === 'scaleio') {
                                lanechange = '10%';
                            }
                            if (component.type === 'server') {
                                lanechange = '38%';
                            }
                            if (component.type === 'vm' || component.type === 'cluster' || component.type === 'scaleio') {
                                lanechange = '18%';
                            }
                        }
                        if (x1 && y1 && x2 && y2) {
                            newFirstVertical.setAttribute('x1', x1);
                            newFirstVertical.setAttribute('y1', y1);
                            newFirstVertical.setAttribute('x2', x1);
                            newFirstVertical.setAttribute('y2', lanechange);
                            newHorizontal.setAttribute('x1', x1);
                            newHorizontal.setAttribute('y1', lanechange);
                            newHorizontal.setAttribute('x2', x2);
                            newHorizontal.setAttribute('y2', lanechange);
                            newSecondVertical.setAttribute('x1', x2);
                            newSecondVertical.setAttribute('y1', lanechange);
                            newSecondVertical.setAttribute('x2', x2);
                            newSecondVertical.setAttribute('y2', y2);
                        }
                        if (x2 && y2 && y1) {
                            newFirstVertical.className.baseVal = 'templateline ' + component.name;
                            newHorizontal.className.baseVal = 'templateline ' + component.name;
                            newSecondVertical.className.baseVal = 'templateline ' + component.name;
                            $('#TemplateBuilderSVGLines').append(newFirstVertical);
                            $('#TemplateBuilderSVGLines').append(newHorizontal);
                            $('#TemplateBuilderSVGLines').append(newSecondVertical);
                        }
                    });
                });
                self.changewidths();
                self.render = true;
            }, self.timerIntervals);
        };
        ServicesvgController.prototype.hoverLine = function (x) {
            var self = this;
            self.componentHover = x;
            x.relatedcomponents.forEach(function (related) {
                var HoverFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var HoverHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var HoverSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var x1 = $('#serviceBuilderSVG #' + x.id).attr('x');
                var y1 = $('#serviceBuilderSVG #' + x.id).attr('y');
                var x2 = $('#serviceBuilderSVG #' + related.id).attr('x');
                var y2 = $('#serviceBuilderSVG #' + related.id).attr('y');
                var lanechange = '';
                var y1Number = 0;
                var y2Number = 0;
                if (y1 && y2) {
                    //Helps figure out if we're going up or down
                    y1Number = parseInt(y1.replace(/[^\w\s]/gi, ''));
                    y2Number = parseInt(y2.replace(/[^\w\s]/gi, ''));
                }
                if (x.type === 'cluster' || x.type === 'scaleio') {
                    lanechange = '38%';
                }
                if (x.type === 'server') {
                    lanechange = '58%';
                }
                if (x.type === 'storage') {
                    lanechange = '58%';
                }
                if (x.type === 'vm') {
                    lanechange = '18%';
                }
                //Are we drawing up?
                if (y1Number > y2Number) {
                    if (x.type === 'cluster' || x.type === 'scaleio') {
                        lanechange = '10%';
                    }
                    if (x.type === 'server') {
                        lanechange = '38%';
                    }
                    if (x.type === 'vm' || x.type === 'cluster' || x.type === 'scaleio') {
                        lanechange = '18%';
                    }
                }
                if (x1 && y1 && x2 && y2) {
                    HoverFirstVertical.setAttribute('x1', x1);
                    HoverFirstVertical.setAttribute('y1', y1);
                    HoverFirstVertical.setAttribute('x2', x1);
                    HoverFirstVertical.setAttribute('y2', lanechange);
                    HoverHorizontal.setAttribute('x1', x1);
                    HoverHorizontal.setAttribute('y1', lanechange);
                    HoverHorizontal.setAttribute('x2', x2);
                    HoverHorizontal.setAttribute('y2', lanechange);
                    HoverSecondVertical.setAttribute('x1', x2);
                    HoverSecondVertical.setAttribute('y1', lanechange);
                    HoverSecondVertical.setAttribute('x2', x2);
                    HoverSecondVertical.setAttribute('y2', y2);
                }
                HoverFirstVertical.className.baseVal = 'hoverline ' + x.id;
                HoverHorizontal.className.baseVal = 'hoverline ' + x.id;
                HoverSecondVertical.className.baseVal = 'hoverline ' + x.id;
                if (x2 && y2 && y1) {
                    $('#TemplateBuilderSVGHoverLines').append(HoverFirstVertical);
                    $('#TemplateBuilderSVGHoverLines').append(HoverHorizontal);
                    $('#TemplateBuilderSVGHoverLines').append(HoverSecondVertical);
                }
            });
        };
        ServicesvgController.prototype.removeHovers = function () {
            var self = this;
            $('#TemplateBuilderSVGHoverLines').empty();
            self.componentHover = '';
        };
        ServicesvgController.prototype.removeSelection = function () {
            var self = this;
            $('#TemplateBuilderSVGSelectedLines').empty();
            self.selectedComponent = '';
        };
        ServicesvgController.prototype.selectLine = function (x) {
            var self = this;
            $('#TemplateBuilderSVGSelectedLines').empty();
            self.selectedComponent = x;
            x.relatedcomponents.forEach(function (related) {
                var SelectedFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var SelectedHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var SelectedSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var x1 = $('#serviceBuilderSVG #' + x.id).attr('x');
                var y1 = $('#serviceBuilderSVG #' + x.id).attr('y');
                var x2 = $('#serviceBuilderSVG #' + related.id).attr('x');
                var y2 = $('#serviceBuilderSVG #' + related.id).attr('y');
                var lanechange = '';
                var y1Number = 0;
                var y2Number = 0;
                if (y1 && y2) {
                    //Helps figure out if we're going up or down
                    y1Number = parseInt(y1.replace(/[^\w\s]/gi, ''));
                    y2Number = parseInt(y2.replace(/[^\w\s]/gi, ''));
                }
                if (x.type === 'cluster' || x.type === 'scaleio') {
                    lanechange = '38%';
                }
                if (x.type === 'server') {
                    lanechange = '58%';
                }
                if (x.type === 'storage') {
                    lanechange = '58%';
                }
                if (x.type === 'vm') {
                    lanechange = '18%';
                }
                //Are we drawing up?
                if (y1Number > y2Number) {
                    if (x.type === 'cluster' || x.type === 'scaleio') {
                        lanechange = '10%';
                    }
                    if (x.type === 'server') {
                        lanechange = '38%';
                    }
                    if (x.type === 'vm' || x.type === 'cluster' || x.type === 'scaleio') {
                        lanechange = '18%';
                    }
                }
                if (x1 && y1 && x2 && y2) {
                    SelectedFirstVertical.setAttribute('x1', x1);
                    SelectedFirstVertical.setAttribute('y1', y1);
                    SelectedFirstVertical.setAttribute('x2', x1);
                    SelectedFirstVertical.setAttribute('y2', lanechange);
                    SelectedHorizontal.setAttribute('x1', x1);
                    SelectedHorizontal.setAttribute('y1', lanechange);
                    SelectedHorizontal.setAttribute('x2', x2);
                    SelectedHorizontal.setAttribute('y2', lanechange);
                    SelectedSecondVertical.setAttribute('x1', x2);
                    SelectedSecondVertical.setAttribute('y1', lanechange);
                    SelectedSecondVertical.setAttribute('x2', x2);
                    SelectedSecondVertical.setAttribute('y2', y2);
                }
                SelectedFirstVertical.className.baseVal = 'selectedline ' + x.id;
                SelectedHorizontal.className.baseVal = 'selectedline ' + x.id;
                SelectedSecondVertical.className.baseVal = 'selectedline ' + x.id;
                if (x2 && y2 && y1) {
                    $('#TemplateBuilderSVGSelectedLines').append(SelectedFirstVertical);
                    $('#TemplateBuilderSVGSelectedLines').append(SelectedHorizontal);
                    $('#TemplateBuilderSVGSelectedLines').append(SelectedSecondVertical);
                }
            });
        };
        ServicesvgController.prototype.checkForCancelling = function () {
            var self = this;
            if (self.service.cancelInprogress) {
                var msg = self.$translate.instant("SERVICE_CANCEL_processofbeingcancelledbanner");
                var match = !!_.find(self.$scope.warnings, { dismissable: false, message: msg });
                //only add the warning if it is not already present
                if (!match) {
                    self.$scope.warnings.push({
                        dismissable: false,
                        message: msg
                    });
                }
            }
        };
        ServicesvgController.prototype.toggleServiceMode = function (server, entering) {
            var self = this;
            //set server health or status to service mode
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            var match = _.find(self.service.serverlist, { id: server.id });
            self.$http.post(self.Commands.data.devices.setServiceMode, { deviceId: match.asmGUID, mode: entering ? "enter" : "exit" })
                .then(function () { self.getserviceData(); })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ServicesvgController.prototype.cancelService = function () {
            var self = this;
            self.removePopovers();
            self.pauseRefresh = true;
            var addApplicationWizard = self.Modal({
                title: self.$translate.instant("SERVICE_CANCEL_ModalTitle"),
                modalSize: 'modal-lg',
                templateUrl: 'views/services/deployservice/canceldeploymentmodal.html',
                controller: 'CancelDeploymentModalController as cancelDeploymentModalController',
                params: {
                    serviceId: self.serviceId
                },
                onComplete: function () {
                    self.pauseRefresh = false;
                    self.getserviceData();
                },
                onCancel: function () {
                    self.pauseRefresh = false;
                    addApplicationWizard.modal.dismiss();
                }
            });
            addApplicationWizard.modal.show();
        };
        ServicesvgController.prototype.updateScroll = function () {
            $('#activityLogItems').scrollTop($('#activityLogItems')[0].scrollHeight);
        };
        ServicesvgController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', 'GlobalServices', '$route', 'localStorageService', '$routeParams', '$translate', '$location', '$window', 'Loading', 'Commands', 'constants', '$scope', '$rootScope'];
        return ServicesvgController;
    }());
    function servicesvg() {
        return {
            restrict: 'E',
            templateUrl: 'views/servicesvg.html',
            replace: true,
            transclude: false,
            controller: ServicesvgController,
            controllerAs: 'servicesvg',
            scope: {
                serviceId: '=serviceId',
                firmwarereport: '=firmwarereport',
                mode: '=mode',
                onServerPortViewClick: '&',
                refreshService: "<",
                errors: "=",
                warnings: "=",
                service: "="
            },
            link: function (scope, element, attributes, controller) {
            }
        };
    }
    angular.module('app').
        directive('servicesvg', servicesvg);
})(asm || (asm = {}));
//# sourceMappingURL=serviceSVGdirective.js.map
