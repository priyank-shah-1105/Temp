var asm;
(function (asm) {
    'use strict';
    var TemplatebuildersvgController = (function () {
        //efforts to remove $watch
        //private _intervalX: any;
        //get intervalX(): any {
        //    var self = this;
        //    return self._intervalX;
        //}
        //set intervalX(theIntervalX: any) {
        //    var self = this;
        //    var oldIntervalX = self._intervalX;
        //    self._intervalX = theIntervalX;
        //    if (self.intervalX != oldIntervalX) {
        //        if (self.template != '') {
        //            self.refresh();
        //        }
        //    }
        //}
        function TemplatebuildersvgController(Modal, Dialog, $http, $timeout, $q, GlobalServices, $route, localStorageService, $routeParams, $compile, $scope, $translate, $location, $window, Loading, $rootScope, Commands) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.GlobalServices = GlobalServices;
            this.$route = $route;
            this.localStorageService = localStorageService;
            this.$routeParams = $routeParams;
            this.$compile = $compile;
            this.$scope = $scope;
            this.$translate = $translate;
            this.$location = $location;
            this.$window = $window;
            this.Loading = Loading;
            this.$rootScope = $rootScope;
            this.Commands = Commands;
            this.svgheight = 300;
            this.templateServers = [];
            this.templateStorages = [];
            this.templateClusters = [];
            this.templateVMs = [];
            this.networks = [];
            this.timerIntervals = 300;
            var self = this;
            self.selectedTemplateId = '';
            self.pollwindowtimer = '';
            self.windowtimer = '';
            self.mode = self.$scope.mode;
            self.errors = self.$scope.errors;
            self.template = '';
            self.jobRequest = { requestObj: { id: '', name: '' } };
            self.intervalX = 0;
            self.mostItems = 0;
            self.render = false;
            self.selectedTemplateId = self.$scope.selectedTemplateId;
            self.mode = self.$scope.mode;
            self.$scope.$watch(function () { return self.intervalX; }, function (newValue, oldValue) {
                if (oldValue !== newValue) {
                    if (self.template !== '') {
                        self.refresh();
                    }
                }
            });
            self.root = this.$rootScope;
            self.refresh();
            self.windowResize();
        }
        TemplatebuildersvgController.prototype.windowResize = function () {
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
        TemplatebuildersvgController.prototype.$onDestroy = function () {
            var self = this;
            $(window).off("resize");
            if (self.windowtimer)
                self.$timeout.cancel(self.windowtimer);
            if (self.pollwindowtimer)
                self.$timeout.cancel(self.pollwindowtimer);
            if (self.template.draft == true && self.mode === 'edit' && (self.template.draft == self.templateCopy.draft)) {
                self.$timeout(function () {
                    self.Dialog(self.$translate.instant('TEMPLATES_TemplateBuilderDraftTitle'), self.$translate.instant('TEMPLATES_TemplateBuilderDraftMessage'), true).then(function () {
                    });
                }, 1000);
            }
        };
        TemplatebuildersvgController.prototype.centerValue = function (components) {
            var self = this;
            if (components <= (self.mostItems - 1)) {
                var x = ((self.mostItems - components) / 2) * self.intervalX;
                return x;
            }
        };
        //Called on window resize
        TemplatebuildersvgController.prototype.calculatewindowheight = function () {
            var self = this;
            //console.log('redrawing window');
            self.windowtimer = self.$timeout(function () {
                self.svgcanvaswidth = $('#TemplateBuilderSVG').width();
                self.windowheight = $(window).height() - 300;
                //if (self.windowheight >= 400) {
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
                //    //self.componentscale = self.svgheight * .0015;
                //    //self.Xlines = self.svgheight * .052;
                //}
                self.changewidths();
            }, self.timerIntervals);
        };
        //This gets called after calculating window height and after drawing the lines
        TemplatebuildersvgController.prototype.changewidths = function () {
            var self = this;
            $('#drawingContainer').css('height', (self.svgheight - 50 + 'px'));
            if (self.render && self.mostItems >= 1 && self.furthestComponentId !== '') {
                //Chrome needs this: It adjusts heights and widths of various DOM containers to allow horizontal scrolling on the SVG
                if (self.render) {
                    //if (self.windowheight >= 400) {
                    //    $('#drawingContainer').css('height', (self.windowheight * .8 + 'px'));
                    //}
                    //$('#drawingContainer').css('width', '400px');
                    var BScontainerWidth = $('#templatearticle').width();
                    $('#drawingContainer').css('width', BScontainerWidth);
                    $('#drawingContainer').css('width', (BScontainerWidth).toString() + 'px');
                    $('#TemplateBuilderSVG').css('width', (BScontainerWidth - 45).toString() + 'px');
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
                //if (self.windowheight >= 400) {
                //    $('#drawingContainer').css('height', (self.windowheight  * .8 + 'px'));
                //}
                //$('#drawingContainer').css('width', '400px');
                var BScontainerWidth = $('#templatearticle').width();
                $('#drawingContainer').css('width', BScontainerWidth);
                $('#drawingContainer').css('width', (BScontainerWidth).toString() + 'px !important');
                $('#TemplateBuilderSVG').css('width', (BScontainerWidth - 45).toString() + 'px');
                self.furthestSVGPoint = '100%';
            }
        };
        TemplatebuildersvgController.prototype.cloneComponent = function (id) {
            var self = this, d = self.$q.defer();
            var component = _.find(self.template.components, { id: id });
            var copy = angular.copy(component), newId = self.GlobalServices.NewGuid();
            angular.extend(copy, {
                clonedFromId: component.id,
                componentid: newId,
                name: self.GlobalServices.namePicker(_.filter(self.template.components, { type: copy.type }), [copy], self.GlobalServices.getBaseName(copy.name))[0].name
            }, { id: newId });
            //for components without applications
            if (component.type !== 'cluster') {
                angular.forEach(_.reject(self.template.components, { type: 'application' }), function (cmp) {
                    //relate clone to seed's original associated components
                    if (_.find(cmp.relatedcomponents, { id: component.id })) {
                        angular.forEach(copy.relatedcomponents, function (relatedComponent) {
                            cmp.relatedcomponents.push({
                                installOrder: relatedComponent.installOrder,
                                name: copy.name,
                                id: copy.id,
                                instances: copy.instances,
                                subtype: copy.subtype
                            });
                        });
                    }
                });
                //Note: Do not copy related application components for clusters
                if (component.type === 'server' || component.type === 'vm') {
                    //_.remove returns array of applications
                    angular.forEach(_.remove(copy.relatedcomponents, { type: 'application' }), function (rc) {
                        var cmp = _.find(self.template.components, { id: rc.id });
                        var copyOfCurrentComponent = angular.copy(cmp);
                        self.template.components.push(angular.extend(copyOfCurrentComponent, {
                            id: self.GlobalServices.NewGuid(),
                            relatedcomponents: [],
                        }));
                        copy.relatedcomponents.push({
                            installOrder: rc.installOrder,
                            name: copyOfCurrentComponent.name,
                            id: copyOfCurrentComponent.id
                        });
                    });
                }
            }
            else {
                copy.relatedcomponents = [];
            }
            self.template.components.push(copy);
            self.$http.post(self.Commands.data.templates.saveTemplate, self.template)
                .then(function (data) {
                self.getTemplateData();
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            });
        };
        //Get the template data
        TemplatebuildersvgController.prototype.getTemplateData = function () {
            var self = this;
            $('.popover').remove();
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            //Shut everything down and start over
            self.selectedComponent = {};
            self.render = false;
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
            self.jobRequest.requestObj.id = self.selectedTemplateId;
            self.$http.post(self.Commands.data.templates.getTemplateBuilderById, self.jobRequest.requestObj)
                .then(function (data) {
                //test for popover alerts
                //data.data = { "criteriaObj": null, "responseCode": 0, "errorObj": null, "requestObj": null, "responseObj": { "id": "ff8080816698f53b016698f7e5110000", "name": "Test", "description": "", "createdBy": "admin", "createdDate": "2018-10-21T23:31:14.833Z", "updatedBy": "admin", "updatedDate": "2018-10-21T23:33:52.627Z", "draft": true, "components": [{ "id": "bae611b8-8a6d-45a3-ab4c-8d7f9ae020b5", "name": "VMWare Cluster", "type": "cluster", "subtype": null, "componentid": "component-cluster-vcenter-1", "identifier": null, "helptext": null, "relatedcomponents": [{ "id": "4393e740-5524-4260-ac51-cfd939aae9d0", "name": "Server", "installOrder": null, "subtype": null }], "categories": [{ "id": "asm::cluster", "name": "Cluster Settings", "settings": [{ "id": "asm_guid", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "Target Virtual Machine Manager", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "vcenter-100.68.106.72", "name": "aer10-cvcsa65.asm.delllabs.net", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "datacenter", "value": "$new$", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "Data Center Name", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "$new$", "name": "Create New Datacenter...", "dependencyTarget": null, "dependencyValue": null }, { "id": "aFlexDCHCI", "name": "aFlexDCHCI", "dependencyTarget": "asm_guid", "dependencyValue": "vcenter-100.68.106.72" }], "requireatdeployment": true, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$datacenter", "value": "TestDC", "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New datacenter name", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": true, "hidefromtemplate": false, "dependencyTarget": "datacenter", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "cluster", "value": "$new$", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "Cluster Name", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "$new$", "name": "Create New Cluster...", "dependencyTarget": null, "dependencyValue": null }, { "id": "aFlexCLHCI", "name": "aFlexCLHCI", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }], "requireatdeployment": true, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$cluster", "value": "TestCluster", "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New cluster name", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": true, "hidefromtemplate": false, "dependencyTarget": "cluster", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_enabled", "value": "distributed", "datatype": "radio", "componentid": "component-cluster-vcenter-1", "name": "Switch Type", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "distributed", "name": "Distributed", "dependencyTarget": null, "dependencyValue": null }, { "id": "standard", "name": "Standard", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ha_config", "value": "true", "datatype": "boolean", "componentid": "component-cluster-vcenter-1", "name": "Cluster HA Enabled", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": true, "hidefromtemplate": false, "dependencyTarget": "cluster", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "drs_config", "value": "true", "datatype": "boolean", "componentid": "component-cluster-vcenter-1", "name": "Cluster DRS Enabled", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": true, "hidefromtemplate": false, "dependencyTarget": "cluster", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ensure", "value": "present", "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": null, "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": "none", "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }] }, { "id": "asm::cluster::vds", "name": "vSphere VDS Settings", "settings": [{ "id": "vds_name", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "VDS Name", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "$new$", "name": "Create VDS Name ...", "dependencyTarget": null, "dependencyValue": null }, { "id": "aflexD1", "name": "aflexD1", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }, { "id": "aflexD2", "name": "aflexD2", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }, { "id": "aflexmgmt", "name": "aflexmgmt", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_name", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New VDS Name", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_name", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_pg", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "Port Group", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "$new$", "name": "Create Port Group ...", "dependencyTarget": null, "dependencyValue": null }, { "id": "aflexCust", "name": "aflexCust", "dependencyTarget": "vds_name", "dependencyValue": "aflexmgmt" }, { "id": "aflexD1-uplink-pg", "name": "aflexD1-uplink-pg", "dependencyTarget": "vds_name", "dependencyValue": "aflexD1" }, { "id": "aflexD1pg", "name": "aflexD1pg", "dependencyTarget": "vds_name", "dependencyValue": "aflexD1" }, { "id": "aflexD2-uplink-pg", "name": "aflexD2-uplink-pg", "dependencyTarget": "vds_name", "dependencyValue": "aflexD2" }, { "id": "aflexD2pg", "name": "aflexD2pg", "dependencyTarget": "vds_name", "dependencyValue": "aflexD2" }, { "id": "aflexFM", "name": "aflexFM", "dependencyTarget": "vds_name", "dependencyValue": "aflexmgmt" }, { "id": "aflexHM", "name": "aflexHM", "dependencyTarget": "vds_name", "dependencyValue": "aflexmgmt" }, { "id": "aflexmgmt-uplink-pg", "name": "aflexmgmt-uplink-pg", "dependencyTarget": "vds_name", "dependencyValue": "aflexmgmt" }, { "id": "aflexPXE", "name": "aflexPXE", "dependencyTarget": "vds_name", "dependencyValue": "aflexmgmt" }, { "id": "cust2", "name": "cust2", "dependencyTarget": "vds_name", "dependencyValue": "aflexmgmt" }], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_pg", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New Port Group", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_pg", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ensure", "value": "present", "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": null, "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": "none", "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "VDS Name", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "$new$", "name": "Create VDS Name ...", "dependencyTarget": null, "dependencyValue": null }, { "id": "aflexD1", "name": "aflexD1", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }, { "id": "aflexD2", "name": "aflexD2", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }, { "id": "aflexmgmt", "name": "aflexmgmt", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS 1", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New VDS Name", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS 1", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a76e1010025::1", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "vcesys-sio-mgmt Port Group", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": null }, { "id": "$new$", "name": "Create Port Group ...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": null }, { "id": "aflexCust", "name": "aflexCust", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexD1-uplink-pg", "name": "aflexD1-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD1" }, { "id": "aflexD1pg", "name": "aflexD1pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD1" }, { "id": "aflexD2-uplink-pg", "name": "aflexD2-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD2" }, { "id": "aflexD2pg", "name": "aflexD2pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD2" }, { "id": "aflexFM", "name": "aflexFM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexHM", "name": "aflexHM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexmgmt-uplink-pg", "name": "aflexmgmt-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexPXE", "name": "aflexPXE", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "cust2", "name": "cust2", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS 1", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a76e1010025::1", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New Port Group", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a76e1010025::1", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS 1", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a78d2530063::1", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "vcesys-esx-mgmt Port Group", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": null }, { "id": "$new$", "name": "Create Port Group ...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": null }, { "id": "aflexCust", "name": "aflexCust", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexD1-uplink-pg", "name": "aflexD1-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD1" }, { "id": "aflexD1pg", "name": "aflexD1pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD1" }, { "id": "aflexD2-uplink-pg", "name": "aflexD2-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD2" }, { "id": "aflexD2pg", "name": "aflexD2pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD2" }, { "id": "aflexFM", "name": "aflexFM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexHM", "name": "aflexHM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexmgmt-uplink-pg", "name": "aflexmgmt-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexPXE", "name": "aflexPXE", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "cust2", "name": "cust2", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS 1", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a78d2530063::1", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New Port Group", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a78d2530063::1", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS 1", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a7913aa0086::1", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "vcesys-os-install Port Group", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": null }, { "id": "$new$", "name": "Create Port Group ...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": null }, { "id": "aflexCust", "name": "aflexCust", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexD1-uplink-pg", "name": "aflexD1-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD1" }, { "id": "aflexD1pg", "name": "aflexD1pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD1" }, { "id": "aflexD2-uplink-pg", "name": "aflexD2-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD2" }, { "id": "aflexD2pg", "name": "aflexD2pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD2" }, { "id": "aflexFM", "name": "aflexFM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexHM", "name": "aflexHM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexmgmt-uplink-pg", "name": "aflexmgmt-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexPXE", "name": "aflexPXE", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "cust2", "name": "cust2", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS 1", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a7913aa0086::1", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New Port Group", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a7913aa0086::1", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS 1", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a7969be0087::1", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "vcesys-customer Port Group", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": null }, { "id": "$new$", "name": "Create Port Group ...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": null }, { "id": "aflexCust", "name": "aflexCust", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexD1-uplink-pg", "name": "aflexD1-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD1" }, { "id": "aflexD1pg", "name": "aflexD1pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD1" }, { "id": "aflexD2-uplink-pg", "name": "aflexD2-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD2" }, { "id": "aflexD2pg", "name": "aflexD2pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexD2" }, { "id": "aflexFM", "name": "aflexFM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexHM", "name": "aflexHM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexmgmt-uplink-pg", "name": "aflexmgmt-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "aflexPXE", "name": "aflexPXE", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }, { "id": "cust2", "name": "cust2", "dependencyTarget": "vds_name::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087", "dependencyValue": "aflexmgmt" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS 1", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a7969be0087::1", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New Port Group", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_pg::ff808081665a6dcd01665a76e1010025:ff808081665a6dcd01665a78d2530063:ff808081665a6dcd01665a7913aa0086:ff808081665a6dcd01665a7969be0087::ff808081665a6dcd01665a7969be0087::1", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS 1", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_name::ff808081665a6dcd01665a750f7f0001", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "VDS Name", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "$new$", "name": "Create VDS Name ...", "dependencyTarget": null, "dependencyValue": null }, { "id": "aflexD1", "name": "aflexD1", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }, { "id": "aflexD2", "name": "aflexD2", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }, { "id": "aflexmgmt", "name": "aflexmgmt", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS 2", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_name::ff808081665a6dcd01665a750f7f0001", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New VDS Name", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS 2", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_pg::ff808081665a6dcd01665a750f7f0001::ff808081665a6dcd01665a750f7f0001::1", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "vcesys-sio-data1 Port Group", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": null }, { "id": "$new$", "name": "Create Port Group ...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": null }, { "id": "aflexCust", "name": "aflexCust", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "aflexmgmt" }, { "id": "aflexD1-uplink-pg", "name": "aflexD1-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "aflexD1" }, { "id": "aflexD1pg", "name": "aflexD1pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "aflexD1" }, { "id": "aflexD2-uplink-pg", "name": "aflexD2-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "aflexD2" }, { "id": "aflexD2pg", "name": "aflexD2pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "aflexD2" }, { "id": "aflexFM", "name": "aflexFM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "aflexmgmt" }, { "id": "aflexHM", "name": "aflexHM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "aflexmgmt" }, { "id": "aflexmgmt-uplink-pg", "name": "aflexmgmt-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "aflexmgmt" }, { "id": "aflexPXE", "name": "aflexPXE", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "aflexmgmt" }, { "id": "cust2", "name": "cust2", "dependencyTarget": "vds_name::ff808081665a6dcd01665a750f7f0001", "dependencyValue": "aflexmgmt" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS 2", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_pg::ff808081665a6dcd01665a750f7f0001::ff808081665a6dcd01665a750f7f0001::1", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New Port Group", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_pg::ff808081665a6dcd01665a750f7f0001::ff808081665a6dcd01665a750f7f0001::1", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS 2", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_name::ff808081665a6dcd01665a75a9a30013", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "VDS Name", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "$new$", "name": "Create VDS Name ...", "dependencyTarget": null, "dependencyValue": null }, { "id": "aflexD1", "name": "aflexD1", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }, { "id": "aflexD2", "name": "aflexD2", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }, { "id": "aflexmgmt", "name": "aflexmgmt", "dependencyTarget": "datacenter", "dependencyValue": "aFlexDCHCI" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS 3", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_name::ff808081665a6dcd01665a75a9a30013", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New VDS Name", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS 3", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "vds_pg::ff808081665a6dcd01665a75a9a30013::ff808081665a6dcd01665a75a9a30013::1", "value": "", "datatype": "enum", "componentid": "component-cluster-vcenter-1", "name": "vcesys-sio-data2 Port Group", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": null }, { "id": "$new$", "name": "Create Port Group ...", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": null }, { "id": "aflexCust", "name": "aflexCust", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "aflexmgmt" }, { "id": "aflexD1-uplink-pg", "name": "aflexD1-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "aflexD1" }, { "id": "aflexD1pg", "name": "aflexD1pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "aflexD1" }, { "id": "aflexD2-uplink-pg", "name": "aflexD2-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "aflexD2" }, { "id": "aflexD2pg", "name": "aflexD2pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "aflexD2" }, { "id": "aflexFM", "name": "aflexFM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "aflexmgmt" }, { "id": "aflexHM", "name": "aflexHM", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "aflexmgmt" }, { "id": "aflexmgmt-uplink-pg", "name": "aflexmgmt-uplink-pg", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "aflexmgmt" }, { "id": "aflexPXE", "name": "aflexPXE", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "aflexmgmt" }, { "id": "cust2", "name": "cust2", "dependencyTarget": "vds_name::ff808081665a6dcd01665a75a9a30013", "dependencyValue": "aflexmgmt" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_enabled", "dependencyValue": "distributed", "addAction": null, "readOnly": false, "group": "VDS 3", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "$new$vds_pg::ff808081665a6dcd01665a75a9a30013::ff808081665a6dcd01665a75a9a30013::1", "value": null, "datatype": "string", "componentid": "component-cluster-vcenter-1", "name": "New Port Group", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "vds_pg::ff808081665a6dcd01665a75a9a30013::ff808081665a6dcd01665a75a9a30013::1", "dependencyValue": "$new$", "addAction": null, "readOnly": false, "group": "VDS 3", "generated": true, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }] }], "referenceid": null, "referenceip": null, "referenceipurl": "https://null", "showNetworkInfo": null, "network": null, "newComponent": null, "cloned": false, "continueClicked": null, "AsmGUID": null, "clonedFromId": null, "allowClone": false, "isComponentValid": false, "raid": null, "configfilename": null, "instances": 1, "errorObj": { "errorMessage": "---consolidated message/header...", "fldErrors": [{ "field": null, "errorMessage": "Component VMWare Cluster is missing data for required field: Target Virtual Machine Manager.", "errorDetails": "The template cannot be published with missing required fields.", "errorAction": "Please enter a valid value for the required field.", "errorCode": "VXFM00215" }, { "field": null, "errorMessage": "Component VMWare Cluster is missing data for required field: VDS Name.", "errorDetails": "The template cannot be published with missing required fields.", "errorAction": "Please enter a valid value for the required field.", "errorCode": "VXFM00215" }, { "field": null, "errorMessage": "Component VMWare Cluster is missing data for required field: vcesys-esx-mgmt Port Group.", "errorDetails": "The template cannot be published with missing required fields.", "errorAction": "Please enter a valid value for the required field.", "errorCode": "VXFM00215" }, { "field": null, "errorMessage": "Component VMWare Cluster is missing data for required field: vcesys-os-install Port Group.", "errorDetails": "The template cannot be published with missing required fields.", "errorAction": "Please enter a valid value for the required field.", "errorCode": "VXFM00215" }, { "field": null, "errorMessage": "Duplicate Port Group Names or VDS Names are not allowed for a cluster.", "errorDetails": "Each Port Group name and VDS name for a cluster in a template must be unique.", "errorAction": "Please check your template and try again.", "errorCode": "VXFM00384" }] } }, { "id": "4393e740-5524-4260-ac51-cfd939aae9d0", "name": "Server", "type": "server", "subtype": "HYPERVISOR", "componentid": "component-server-1", "identifier": null, "helptext": null, "relatedcomponents": [{ "id": "bae611b8-8a6d-45a3-ab4c-8d7f9ae020b5", "name": "VMWare Cluster", "installOrder": null, "subtype": null }], "categories": [{ "id": "asm::server", "name": "OS Settings", "settings": [{ "id": "assign_host_name", "value": "define_host_name", "datatype": "enum", "componentid": "component-server-1", "name": "Host Name Selection", "tooltip": "Define how the Host Name will be selected at deployment time.<br/>Auto Generate - Use the Host Name Template field to auto-generate host names at deployment time.<br/>Specify At Deployment Time - A unique host name will be requested at deployment time.<br/>Reverse DNS Lookup - Host name will be assigned based on reverse DNS lookup of the host IP Address at deployment time.", "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "define_host_name", "name": "Specify At Deployment Time", "dependencyTarget": null, "dependencyValue": null }, { "id": "generate_host_name", "name": "Auto Generate", "dependencyTarget": null, "dependencyValue": null }, { "id": "lookup_host_name", "name": "Reverse DNS Lookup", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "target_boot_device", "dependencyValue": "LOCAL_FLASH_STORAGE,HD,SD,SD_WITH_RAID_VSAN,SD_WITH_RAID,AHCI_VSAN,LOCAL_FLASH_STORAGE_FOR_SCALE_IO", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "os_host_name_template", "value": "server${num}", "datatype": "string", "componentid": "component-server-1", "name": "Host Name Template", "tooltip": "Template used to generate host names at deployment time. Must contain variables that will produce a unique host name.<br/>Allowed variables are ${num} (an auto-generated unique number), ${num_2d or ${num_3d) (an auto-generated number forced to be 2 or 3 digits), ${service_tag}, ${model}, or ${vendor}.", "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "assign_host_name", "dependencyValue": "generate_host_name", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "os_host_name", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "Host Name", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": true, "hidefromtemplate": true, "dependencyTarget": "assign_host_name", "dependencyValue": "define_host_name", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "os_image_type", "value": "vmware_esxi", "datatype": "string", "componentid": "component-server-1", "name": "OS Image Type", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "target_boot_device", "dependencyValue": "LOCAL_FLASH_STORAGE,SD,SD_WITH_RAID,SD_WITH_RAID_VSAN,HD,AHCI_VSAN,LOCAL_FLASH_STORAGE_FOR_SCALE_IO", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "razor_image", "value": "ESXi6.57967591-6.5.X_Dell_14G", "datatype": "enum", "componentid": "component-server-1", "name": "OS Image", "tooltip": "Location of OS image installation files", "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "esxi-6.0", "name": "esxi-6.0", "dependencyTarget": null, "dependencyValue": null }, { "id": "esxi-6.5", "name": "esxi-6.5", "dependencyTarget": null, "dependencyValue": null }, { "id": "ESXi6.57967591-6.5.X_Dell_14G", "name": "ESXi 6.5 7967591-6.5.X_Dell_14G", "dependencyTarget": null, "dependencyValue": null }, { "id": "rcm_esx", "name": "Use RCM ESXi image", "dependencyTarget": null, "dependencyValue": null }, { "id": "rcm_rhel", "name": "Use RCM RHEL image", "dependencyTarget": null, "dependencyValue": null }, { "id": "RHEL7.520180322.0-Server", "name": "RHEL 7.5 20180322.0-Server", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "target_boot_device", "dependencyValue": "LOCAL_FLASH_STORAGE,SD,SD_WITH_RAID,SD_WITH_RAID_VSAN,HD,AHCI_VSAN,LOCAL_FLASH_STORAGE_FOR_SCALE_IO", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "hyperv_install", "value": "false", "datatype": "boolean", "componentid": "component-server-1", "name": "Install HyperV", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "razor_image", "dependencyValue": " ", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "os_image_version", "value": "", "datatype": "enum", "componentid": "component-server-1", "name": "OS Image Version", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "razor_image", "dependencyValue": " ", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "os_credential", "value": "ff808081665a6dcd01665a8072a2052a", "datatype": "oscredential", "componentid": "component-server-1", "name": "OS Credential", "tooltip": "Credential used to set username and password on the installed OS", "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "target_boot_device", "dependencyValue": "LOCAL_FLASH_STORAGE,SD,SD_WITH_RAID,SD_WITH_RAID_VSAN,HD,AHCI_VSAN,LOCAL_FLASH_STORAGE_FOR_SCALE_IO", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "product_key", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "Product Key", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "razor_image", "dependencyValue": " ", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "timezone", "value": "Central Standard Time", "datatype": "enum", "componentid": "component-server-1", "name": "Timezone", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "A.U.S. Central Standard Time", "name": "(GMT+09:30) Darwin", "dependencyTarget": null, "dependencyValue": null }, { "id": "A.U.S. Eastern Standard Time", "name": "(GMT+10:00) Canberra, Melbourne, Sydney", "dependencyTarget": null, "dependencyValue": null }, { "id": "Alaskan Standard Time", "name": "(GMT-09:00) Alaska", "dependencyTarget": null, "dependencyValue": null }, { "id": "Arab Standard Time", "name": "(GMT+03:00) Kuwait, Riyadh", "dependencyTarget": null, "dependencyValue": null }, { "id": "Arabian Standard Time", "name": "(GMT+04:00) Abu Dhabi, Muscat", "dependencyTarget": null, "dependencyValue": null }, { "id": "Arabic Standard Time", "name": "(GMT+03:00) Baghdad", "dependencyTarget": null, "dependencyValue": null }, { "id": "Atlantic Standard Time", "name": "(GMT-04:00) Atlantic Time (Canada)", "dependencyTarget": null, "dependencyValue": null }, { "id": "Azores Standard Time", "name": "(GMT-01:00) Azores", "dependencyTarget": null, "dependencyValue": null }, { "id": "Canada Central Standard Time", "name": "(GMT-06:00) Saskatchewan", "dependencyTarget": null, "dependencyValue": null }, { "id": "Cape Verde Standard Time", "name": "(GMT-01:00) Cape Verde Islands", "dependencyTarget": null, "dependencyValue": null }, { "id": "Caucasus Standard Time", "name": "(GMT+04:00) Baku, Tbilisi, Yerevan", "dependencyTarget": null, "dependencyValue": null }, { "id": "Cen. Australia Standard Time", "name": "(GMT+09:30) Adelaide", "dependencyTarget": null, "dependencyValue": null }, { "id": "Central America Standard Time", "name": "(GMT-06:00) Central America", "dependencyTarget": null, "dependencyValue": null }, { "id": "Central Asia Standard Time", "name": "(GMT+06:00) Astana, Dhaka", "dependencyTarget": null, "dependencyValue": null }, { "id": "Central Europe Standard Time", "name": "(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague", "dependencyTarget": null, "dependencyValue": null }, { "id": "Central European Standard Time", "name": "(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb", "dependencyTarget": null, "dependencyValue": null }, { "id": "Central Pacific Standard Time", "name": "(GMT+11:00) Magadan, Solomon Islands, New Caledonia", "dependencyTarget": null, "dependencyValue": null }, { "id": "Central Standard Time", "name": "(GMT-06:00) Central Time (US and Canada)", "dependencyTarget": null, "dependencyValue": null }, { "id": "China Standard Time", "name": "(GMT+08:00) Beijing, Chongqing, Hong Kong SAR, Urumqi", "dependencyTarget": null, "dependencyValue": null }, { "id": "Dateline Standard Time", "name": "(GMT-12:00) International Date Line West", "dependencyTarget": null, "dependencyValue": null }, { "id": "E. Africa Standard Time", "name": "(GMT+03:00) Nairobi", "dependencyTarget": null, "dependencyValue": null }, { "id": "E. Australia Standard Time", "name": "(GMT+10:00) Brisbane", "dependencyTarget": null, "dependencyValue": null }, { "id": "E. Europe Standard Time", "name": "(GMT+02:00) Bucharest", "dependencyTarget": null, "dependencyValue": null }, { "id": "E. South America Standard Time", "name": "(GMT-03:00) Brasilia", "dependencyTarget": null, "dependencyValue": null }, { "id": "Eastern Standard Time", "name": "(GMT-05:00) Eastern Time (US and Canada)", "dependencyTarget": null, "dependencyValue": null }, { "id": "Egypt Standard Time", "name": "(GMT+02:00) Cairo", "dependencyTarget": null, "dependencyValue": null }, { "id": "Ekaterinburg Standard Time", "name": "(GMT+05:00) Ekaterinburg", "dependencyTarget": null, "dependencyValue": null }, { "id": "Fiji Islands Standard Time", "name": "(GMT+12:00) Fiji Islands, Kamchatka, Marshall Islands", "dependencyTarget": null, "dependencyValue": null }, { "id": "FLE Standard Time", "name": "(GMT+02:00) Helsinki, Kiev, Riga, Sofia, Tallinn, Vilnius", "dependencyTarget": null, "dependencyValue": null }, { "id": "GMT Standard Time", "name": "(GMT) Greenwich Mean Time: Dublin, Edinburgh, Lisbon, London", "dependencyTarget": null, "dependencyValue": null }, { "id": "Greenland Standard Time", "name": "(GMT-03:00) Greenland", "dependencyTarget": null, "dependencyValue": null }, { "id": "Greenwich Standard Time", "name": "(GMT) Casablanca, Monrovia", "dependencyTarget": null, "dependencyValue": null }, { "id": "GTB Standard Time", "name": "(GMT+02:00) Athens, Istanbul, Minsk", "dependencyTarget": null, "dependencyValue": null }, { "id": "Hawaiian Standard Time", "name": "(GMT-10:00) Hawaii", "dependencyTarget": null, "dependencyValue": null }, { "id": "India Standard Time", "name": "(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi", "dependencyTarget": null, "dependencyValue": null }, { "id": "Iran Standard Time", "name": "(GMT+03:30) Tehran", "dependencyTarget": null, "dependencyValue": null }, { "id": "Israel Standard Time", "name": "(GMT+02:00) Jerusalem", "dependencyTarget": null, "dependencyValue": null }, { "id": "Korea Standard Time", "name": "(GMT+09:00) Seoul", "dependencyTarget": null, "dependencyValue": null }, { "id": "Mexico Standard Time", "name": "(GMT-06:00) Guadalajara, Mexico City, Monterrey", "dependencyTarget": null, "dependencyValue": null }, { "id": "Mexico Standard Time 2", "name": "(GMT-07:00) Chihuahua, La Paz, Mazatlan", "dependencyTarget": null, "dependencyValue": null }, { "id": "Mid-Atlantic Standard Time", "name": "(GMT-02:00) Mid-Atlantic", "dependencyTarget": null, "dependencyValue": null }, { "id": "Mountain Standard Time", "name": "(GMT-07:00) Mountain Time (US and Canada)", "dependencyTarget": null, "dependencyValue": null }, { "id": "Myanmar Standard Time", "name": "(GMT+06:30) Yangon Rangoon", "dependencyTarget": null, "dependencyValue": null }, { "id": "N. Central Asia Standard Time", "name": "(GMT+06:00) Almaty, Novosibirsk", "dependencyTarget": null, "dependencyValue": null }, { "id": "Nepal Standard Time", "name": "(GMT+05:45) Kathmandu", "dependencyTarget": null, "dependencyValue": null }, { "id": "New Zealand Standard Time", "name": "(GMT+12:00) Auckland, Wellington", "dependencyTarget": null, "dependencyValue": null }, { "id": "Newfoundland and Labrador Standard Time", "name": "(GMT-03:30) Newfoundland and Labrador", "dependencyTarget": null, "dependencyValue": null }, { "id": "North Asia East Standard Time", "name": "(GMT+08:00) Irkutsk, Ulaanbaatar", "dependencyTarget": null, "dependencyValue": null }, { "id": "North Asia Standard Time", "name": "(GMT+07:00) Krasnoyarsk", "dependencyTarget": null, "dependencyValue": null }, { "id": "Pacific S.A. Standard Time", "name": "(GMT-04:00) Santiago", "dependencyTarget": null, "dependencyValue": null }, { "id": "Pacific Standard Time", "name": "(GMT-08:00) Pacific Time (US and Canada); Tijuana", "dependencyTarget": null, "dependencyValue": null }, { "id": "Romance Standard Time", "name": "(GMT+01:00) Brussels, Copenhagen, Madrid, Paris", "dependencyTarget": null, "dependencyValue": null }, { "id": "Russian Standard Time", "name": "(GMT+03:00) Moscow, St. Petersburg, Volgograd", "dependencyTarget": null, "dependencyValue": null }, { "id": "S.A. Eastern Standard Time", "name": "(GMT-03:00) Buenos Aires, Georgetown", "dependencyTarget": null, "dependencyValue": null }, { "id": "S.A. Pacific Standard Time", "name": "(GMT-05:00) Bogota, Lima, Quito", "dependencyTarget": null, "dependencyValue": null }, { "id": "S.A. Western Standard Time", "name": "(GMT-04:00) Caracas, La Paz", "dependencyTarget": null, "dependencyValue": null }, { "id": "S.E. Asia Standard Time", "name": "(GMT+07:00) Bangkok, Hanoi, Jakarta", "dependencyTarget": null, "dependencyValue": null }, { "id": "Samoa Standard Time", "name": "(GMT-11:00) Midway Island, Samoa", "dependencyTarget": null, "dependencyValue": null }, { "id": "Singapore Standard Time", "name": "(GMT+08:00) Kuala Lumpur, Singapore", "dependencyTarget": null, "dependencyValue": null }, { "id": "South Africa Standard Time", "name": "(GMT+02:00) Harare, Pretoria", "dependencyTarget": null, "dependencyValue": null }, { "id": "Sri Lanka Standard Time", "name": "(GMT+06:00) Sri Jayawardenepura", "dependencyTarget": null, "dependencyValue": null }, { "id": "Taipei Standard Time", "name": "(GMT+08:00) Taipei", "dependencyTarget": null, "dependencyValue": null }, { "id": "Tasmania Standard Time", "name": "(GMT+10:00) Hobart", "dependencyTarget": null, "dependencyValue": null }, { "id": "Tokyo Standard Time", "name": "(GMT+09:00) Osaka, Sapporo, Tokyo", "dependencyTarget": null, "dependencyValue": null }, { "id": "Tonga Standard Time", "name": "(GMT+13:00) Nuku'alofa", "dependencyTarget": null, "dependencyValue": null }, { "id": "Transitional Islamic State of Afghanistan Standard Time", "name": "(GMT+04:30) Kabul", "dependencyTarget": null, "dependencyValue": null }, { "id": "U.S. Eastern Standard Time", "name": "(GMT-05:00) Indiana (East)", "dependencyTarget": null, "dependencyValue": null }, { "id": "U.S. Mountain Standard Time", "name": "(GMT-07:00) Arizona", "dependencyTarget": null, "dependencyValue": null }, { "id": "Vladivostok Standard Time", "name": "(GMT+10:00) Vladivostok", "dependencyTarget": null, "dependencyValue": null }, { "id": "W. Australia Standard Time", "name": "(GMT+08:00) Perth", "dependencyTarget": null, "dependencyValue": null }, { "id": "W. Central Africa Standard Time", "name": "(GMT+01:00) West Central Africa", "dependencyTarget": null, "dependencyValue": null }, { "id": "W. Europe Standard Time", "name": "(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna", "dependencyTarget": null, "dependencyValue": null }, { "id": "West Asia Standard Time", "name": "(GMT+05:00) Islamabad, Karachi, Tashkent", "dependencyTarget": null, "dependencyValue": null }, { "id": "West Pacific Standard Time", "name": "(GMT+10:00) Guam, Port Moresby", "dependencyTarget": null, "dependencyValue": null }, { "id": "Yakutsk Standard Time", "name": "(GMT+09:00) Yakutsk", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "hyperv_install", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "time_zone", "value": "11", "datatype": "enum", "componentid": "component-server-1", "name": "Timezone", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "1", "name": "(UTC-12:00) International Date Line West", "dependencyTarget": null, "dependencyValue": null }, { "id": "10", "name": "(UTC-06:00) Central America", "dependencyTarget": null, "dependencyValue": null }, { "id": "100", "name": "(UTC+13:00) Samoa", "dependencyTarget": null, "dependencyValue": null }, { "id": "11", "name": "(UTC-06:00) Central Time (US & Canada)", "dependencyTarget": null, "dependencyValue": null }, { "id": "12", "name": "(UTC-06:00) Guadalajara, Mexico City, Monterrey", "dependencyTarget": null, "dependencyValue": null }, { "id": "13", "name": "(UTC-06:00) Saskatchewan", "dependencyTarget": null, "dependencyValue": null }, { "id": "14", "name": "(UTC-05:00) Bogota, Lima, Quito", "dependencyTarget": null, "dependencyValue": null }, { "id": "15", "name": "(UTC-05:00) Eastern Time (US & Canada)", "dependencyTarget": null, "dependencyValue": null }, { "id": "16", "name": "(UTC-05:00) Indiana (East)", "dependencyTarget": null, "dependencyValue": null }, { "id": "17", "name": "(UTC-04:30) Caracas", "dependencyTarget": null, "dependencyValue": null }, { "id": "18", "name": "(UTC-04:00) Asuncion", "dependencyTarget": null, "dependencyValue": null }, { "id": "19", "name": "(UTC-04:00) Atlantic Time (Canada)", "dependencyTarget": null, "dependencyValue": null }, { "id": "2", "name": "(UTC-11:00) Coordinated Universal Time-11", "dependencyTarget": null, "dependencyValue": null }, { "id": "20", "name": "(UTC-04:00) Cuiaba", "dependencyTarget": null, "dependencyValue": null }, { "id": "21", "name": "(UTC-04:00) Georgetown, La Paz, Manaus, San Juan", "dependencyTarget": null, "dependencyValue": null }, { "id": "22", "name": "(UTC-04:00) Santiago", "dependencyTarget": null, "dependencyValue": null }, { "id": "23", "name": "(UTC-03:30) Newfoundland", "dependencyTarget": null, "dependencyValue": null }, { "id": "24", "name": "(UTC-03:00) Brasilia", "dependencyTarget": null, "dependencyValue": null }, { "id": "25", "name": "(UTC-03:00) Buenos Aires", "dependencyTarget": null, "dependencyValue": null }, { "id": "26", "name": "(UTC-03:00) Cayenne, Fortaleza", "dependencyTarget": null, "dependencyValue": null }, { "id": "27", "name": "(UTC-03:00) Greenland", "dependencyTarget": null, "dependencyValue": null }, { "id": "28", "name": "(UTC-03:00) Montevideo", "dependencyTarget": null, "dependencyValue": null }, { "id": "29", "name": "(UTC-03:00) Salvador", "dependencyTarget": null, "dependencyValue": null }, { "id": "3", "name": "(UTC-10:00) Hawaii", "dependencyTarget": null, "dependencyValue": null }, { "id": "30", "name": "(UTC-02:00) Coordinated Universal Time-02", "dependencyTarget": null, "dependencyValue": null }, { "id": "32", "name": "(UTC-01:00) Azores", "dependencyTarget": null, "dependencyValue": null }, { "id": "33", "name": "(UTC-01:00) Cape Verde Is.", "dependencyTarget": null, "dependencyValue": null }, { "id": "34", "name": "(UTC) Casablanca", "dependencyTarget": null, "dependencyValue": null }, { "id": "35", "name": "(UTC) Coordinated Universal Time", "dependencyTarget": null, "dependencyValue": null }, { "id": "36", "name": "(UTC) Dublin, Edinburgh, Lisbon, London", "dependencyTarget": null, "dependencyValue": null }, { "id": "37", "name": "(UTC) Monrovia, Reykjavik", "dependencyTarget": null, "dependencyValue": null }, { "id": "38", "name": "(UTC+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna", "dependencyTarget": null, "dependencyValue": null }, { "id": "39", "name": "(UTC+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague", "dependencyTarget": null, "dependencyValue": null }, { "id": "4", "name": "(UTC-09:00) Alaska", "dependencyTarget": null, "dependencyValue": null }, { "id": "40", "name": "(UTC+01:00) Brussels, Copenhagen, Madrid, Paris", "dependencyTarget": null, "dependencyValue": null }, { "id": "41", "name": "(UTC+01:00) Sarajevo, Skopje, Warsaw, Zagreb", "dependencyTarget": null, "dependencyValue": null }, { "id": "42", "name": "(UTC+01:00) West Central Africa", "dependencyTarget": null, "dependencyValue": null }, { "id": "43", "name": "(UTC+01:00) Windhoek", "dependencyTarget": null, "dependencyValue": null }, { "id": "44", "name": "(UTC+02:00) Amman", "dependencyTarget": null, "dependencyValue": null }, { "id": "45", "name": "(UTC+02:00) Athens, Bucharest", "dependencyTarget": null, "dependencyValue": null }, { "id": "46", "name": "(UTC+02:00) Beirut", "dependencyTarget": null, "dependencyValue": null }, { "id": "47", "name": "(UTC+02:00) Cairo", "dependencyTarget": null, "dependencyValue": null }, { "id": "48", "name": "(UTC+02:00) Damascus", "dependencyTarget": null, "dependencyValue": null }, { "id": "49", "name": "(UTC+02:00) Harare, Pretoria", "dependencyTarget": null, "dependencyValue": null }, { "id": "5", "name": "(UTC-08:00) Baja California", "dependencyTarget": null, "dependencyValue": null }, { "id": "50", "name": "(UTC+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius", "dependencyTarget": null, "dependencyValue": null }, { "id": "51", "name": "(UTC+02:00) Istanbul", "dependencyTarget": null, "dependencyValue": null }, { "id": "52", "name": "(UTC+02:00) Jerusalem", "dependencyTarget": null, "dependencyValue": null }, { "id": "53", "name": "(UTC+02:00) Nicosia", "dependencyTarget": null, "dependencyValue": null }, { "id": "54", "name": "(UTC+03:00) Baghdad", "dependencyTarget": null, "dependencyValue": null }, { "id": "55", "name": "(UTC+03:00) Kaliningrad, Minsk", "dependencyTarget": null, "dependencyValue": null }, { "id": "56", "name": "(UTC+03:00) Kuwait, Riyadh", "dependencyTarget": null, "dependencyValue": null }, { "id": "57", "name": "(UTC+03:00) Nairobi", "dependencyTarget": null, "dependencyValue": null }, { "id": "58", "name": "(UTC+03:30) Tehran", "dependencyTarget": null, "dependencyValue": null }, { "id": "59", "name": "(UTC+04:00) Abu Dhabi, Muscat", "dependencyTarget": null, "dependencyValue": null }, { "id": "6", "name": "(UTC-08:00) Pacific Time (US & Canada)", "dependencyTarget": null, "dependencyValue": null }, { "id": "60", "name": "(UTC+04:00) Baku", "dependencyTarget": null, "dependencyValue": null }, { "id": "61", "name": "(UTC+04:00) Moscow, St. Petersburg, Volgograd", "dependencyTarget": null, "dependencyValue": null }, { "id": "62", "name": "(UTC+04:00) Port Louis", "dependencyTarget": null, "dependencyValue": null }, { "id": "63", "name": "(UTC+04:00) Tbilisi", "dependencyTarget": null, "dependencyValue": null }, { "id": "64", "name": "(UTC+04:00) Yerevan", "dependencyTarget": null, "dependencyValue": null }, { "id": "65", "name": "(UTC+04:30) Kabul", "dependencyTarget": null, "dependencyValue": null }, { "id": "66", "name": "(UTC+05:00) Islamabad, Karachi", "dependencyTarget": null, "dependencyValue": null }, { "id": "67", "name": "(UTC+05:00) Tashkent", "dependencyTarget": null, "dependencyValue": null }, { "id": "68", "name": "(UTC+05:30) Chennai, Kolkata, Mumbai, New Delhi", "dependencyTarget": null, "dependencyValue": null }, { "id": "69", "name": "(UTC+05:30) Sri Jayawardenepura", "dependencyTarget": null, "dependencyValue": null }, { "id": "7", "name": "(UTC-07:00) Arizona", "dependencyTarget": null, "dependencyValue": null }, { "id": "70", "name": "(UTC+05:45) Kathmandu", "dependencyTarget": null, "dependencyValue": null }, { "id": "71", "name": "(UTC+06:00) Astana", "dependencyTarget": null, "dependencyValue": null }, { "id": "72", "name": "(UTC+06:00) Dhaka", "dependencyTarget": null, "dependencyValue": null }, { "id": "73", "name": "(UTC+06:00) Ekaterinburg", "dependencyTarget": null, "dependencyValue": null }, { "id": "74", "name": "(UTC+06:30) Yangon (Rangoon)", "dependencyTarget": null, "dependencyValue": null }, { "id": "75", "name": "(UTC+07:00) Bangkok, Hanoi, Jakarta", "dependencyTarget": null, "dependencyValue": null }, { "id": "76", "name": "(UTC+07:00) Novosibirsk", "dependencyTarget": null, "dependencyValue": null }, { "id": "77", "name": "(UTC+08:00) Beijing, Chongqing, Hong Kong, Urumqi", "dependencyTarget": null, "dependencyValue": null }, { "id": "78", "name": "(UTC+08:00) Krasnoyarsk", "dependencyTarget": null, "dependencyValue": null }, { "id": "79", "name": "(UTC+08:00) Kuala Lumpur, Singapore", "dependencyTarget": null, "dependencyValue": null }, { "id": "8", "name": "(UTC-07:00) Chihuahua, La Paz, Mazatlan", "dependencyTarget": null, "dependencyValue": null }, { "id": "80", "name": "(UTC+08:00) Perth", "dependencyTarget": null, "dependencyValue": null }, { "id": "81", "name": "(UTC+08:00) Taipei", "dependencyTarget": null, "dependencyValue": null }, { "id": "82", "name": "(UTC+08:00) Ulaanbaatar", "dependencyTarget": null, "dependencyValue": null }, { "id": "83", "name": "(UTC+09:00) Irkutsk", "dependencyTarget": null, "dependencyValue": null }, { "id": "84", "name": "(UTC+09:00) Osaka, Sapporo, Tokyo", "dependencyTarget": null, "dependencyValue": null }, { "id": "85", "name": "(UTC+09:00) Seoul", "dependencyTarget": null, "dependencyValue": null }, { "id": "86", "name": "(UTC+09:30) Adelaide", "dependencyTarget": null, "dependencyValue": null }, { "id": "87", "name": "(UTC+09:30) Darwin", "dependencyTarget": null, "dependencyValue": null }, { "id": "88", "name": "(UTC+10:00) Brisbane", "dependencyTarget": null, "dependencyValue": null }, { "id": "89", "name": "(UTC+10:00) Canberra, Melbourne, Sydney", "dependencyTarget": null, "dependencyValue": null }, { "id": "9", "name": "(UTC-07:00) Mountain Time (US & Canada)", "dependencyTarget": null, "dependencyValue": null }, { "id": "90", "name": "(UTC+10:00) Guam, Port Moresby", "dependencyTarget": null, "dependencyValue": null }, { "id": "91", "name": "(UTC+10:00) Hobart", "dependencyTarget": null, "dependencyValue": null }, { "id": "92", "name": "(UTC+10:00) Yakutsk ", "dependencyTarget": null, "dependencyValue": null }, { "id": "93", "name": "(UTC+11:00) Solomon Is., New Caledonia", "dependencyTarget": null, "dependencyValue": null }, { "id": "94", "name": "(UTC+11:00) Vladivostok", "dependencyTarget": null, "dependencyValue": null }, { "id": "95", "name": "(UTC+12:00) Auckland, Wellington", "dependencyTarget": null, "dependencyValue": null }, { "id": "96", "name": "(UTC+12:00) Coordinated Universal Time+12", "dependencyTarget": null, "dependencyValue": null }, { "id": "97", "name": "(UTC+12:00) Fiji", "dependencyTarget": null, "dependencyValue": null }, { "id": "98", "name": "(UTC+12:00) Magadan", "dependencyTarget": null, "dependencyValue": null }, { "id": "99", "name": "(UTC+13:00) Nuku'alofa", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "razor_image", "dependencyValue": "RHEL7.520180322.0-Server,rcm_rhel", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ntp_server", "value": "3.centos.pool.ntp.org", "datatype": "string", "componentid": "component-server-1", "name": "NTP Server", "tooltip": "Multiple NTP servers may be entered as a comma-separated list.", "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "razor_image", "dependencyValue": "RHEL7.520180322.0-Server,rcm_rhel,ESXi6.57967591-6.5.X_Dell_14G,esxi-6.0,esxi-6.5,rcm_esx", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "language", "value": "en-US", "datatype": "enum", "componentid": "component-server-1", "name": "Language", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "de-DE", "name": "German (Germany) ", "dependencyTarget": null, "dependencyValue": null }, { "id": "en-US", "name": "English (United States)", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "hyperv_install", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "keyboard", "value": "00000409", "datatype": "enum", "componentid": "component-server-1", "name": "Keyboard", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "00000409", "name": "U.S. English", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "hyperv_install", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "domain_name", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "Domain Name", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "razor_image", "dependencyValue": " ", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "fqdn", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "FQ Domain Name", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "hyperv_install", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "domain_admin_user", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "Domain Admin Username", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "razor_image", "dependencyValue": " ", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "domain_admin_password", "value": "", "datatype": "password", "componentid": "component-server-1", "name": "Domain Admin Password", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "razor_image", "dependencyValue": " ", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "domain_admin_password_confirm", "value": "", "datatype": "password", "componentid": "component-server-1", "name": "Domain Admin Password Confirm", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "razor_image", "dependencyValue": " ", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "scaleio_enabled", "value": "true", "datatype": "boolean", "componentid": "component-server-1", "name": "Use Server for Dell EMC VxFlex OS", "tooltip": "Dell EMC VxFlex OS is based on VxFlex OS software. VxFlex OS values may be entered/shown in this field.", "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "scaleio_role", "value": "hyperconverged", "datatype": "radio", "componentid": "component-server-1", "name": "VxFlex OS Role", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "compute_only", "name": "Compute Only", "dependencyTarget": "razor_image", "dependencyValue": "ESXi6.57967591-6.5.X_Dell_14G,esxi-6.0,esxi-6.5,rcm_esx" }, { "id": "hyperconverged", "name": "Hyperconverged", "dependencyTarget": "razor_image", "dependencyValue": "ESXi6.57967591-6.5.X_Dell_14G,esxi-6.0,esxi-6.5,rcm_esx" }, { "id": "storage_only", "name": "Storage Only", "dependencyTarget": "razor_image", "dependencyValue": "RHEL7.520180322.0-Server,rcm_rhel,rcm_rhel" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "scaleio_enabled", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "port_channel_protocol", "value": "standard", "datatype": "enum", "componentid": "component-server-1", "name": "Port Channel Protocol", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "lacp", "name": "Lacp Protocol", "dependencyTarget": null, "dependencyValue": null }, { "id": "none", "name": "No Protocol", "dependencyTarget": null, "dependencyValue": null }, { "id": "standard", "name": "Standard Protocol", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_role", "dependencyValue": "hyperconverged", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "scaleio_sdc_guid", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "VxFlex OS SDC Guid", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_enabled", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": true }, { "id": "scaleio_sds_guid", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "VxFlex OS SDS Guid", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_enabled", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": true }, { "id": "scaleio_mdm_role", "value": "none", "datatype": "enum", "componentid": "component-server-1", "name": "VxFlex OS MDM Role", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "none", "name": "None", "dependencyTarget": null, "dependencyValue": null }, { "id": "primary_mdm", "name": "Primary MDM", "dependencyTarget": null, "dependencyValue": null }, { "id": "secondary_mdm", "name": "Secondary MDM", "dependencyTarget": null, "dependencyValue": null }, { "id": "standby_mdm", "name": "Standby MDM", "dependencyTarget": null, "dependencyValue": null }, { "id": "standby_tie_breaker", "name": "Standby Tie Breaker", "dependencyTarget": null, "dependencyValue": null }, { "id": "tie_breaker", "name": "Tie Breaker", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_enabled", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": true }, { "id": "mdm_data_ips", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "VxFlex OS Configure MDM Data IP Addresses", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_enabled", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": true }, { "id": "scaleio_disk_configuration", "value": null, "datatype": "storagepooldisksconfiguration", "componentid": "component-server-1", "name": "VxFlex OS Storage Pool Disks Configuration", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_enabled", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": true }, { "id": "scaleio_inventory_mdm_role", "value": "none", "datatype": "enum", "componentid": "component-server-1", "name": "VxFlex OS Inventory MDM Role", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "none", "name": "None", "dependencyTarget": null, "dependencyValue": null }, { "id": "primary_mdm", "name": "Primary MDM", "dependencyTarget": null, "dependencyValue": null }, { "id": "secondary_mdm", "name": "Secondary MDM", "dependencyTarget": null, "dependencyValue": null }, { "id": "standby_mdm", "name": "Standby MDM", "dependencyTarget": null, "dependencyValue": null }, { "id": "standby_tie_breaker", "name": "Standby Tie Breaker", "dependencyTarget": null, "dependencyValue": null }, { "id": "tie_breaker", "name": "Tie Breaker", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_enabled", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": true }, { "id": "scaleio_inventory_mdm_management_ips", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "VxFlex OS Inventory MDM Management IP Addresses", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_enabled", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": true }, { "id": "scaleio_inventory_mdm_data_ips", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "VxFlex OS Inventory MDM Data IP Addresses", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_enabled", "dependencyValue": "true", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": true }, { "id": "vib_repo", "value": "VxFlexOS2.6.1SDC", "datatype": "string", "componentid": "component-server-1", "name": "VIB location", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_role", "dependencyValue": "hyperconverged,compute_only", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "yum_repo", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "YUM Base URI", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": "scaleio_role", "dependencyValue": "storage_only", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "razor_image_name", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "Razor image", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ensure", "value": "present", "datatype": "string", "componentid": "component-server-1", "name": null, "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": "none", "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }] }, { "id": "asm::svm", "name": "SVM OS Settings", "settings": [{ "id": "svm_assign_host_name", "value": "generate_host_name", "datatype": "enum", "componentid": "component-server-1", "name": "Host Name Selection", "tooltip": "Define how the Host Name will be selected at deployment time.<br/>Auto Generate - Use the Host Name Template field to auto-generate host names at deployment time.<br/>Specify At Deployment Time - A unique host name will be requested at deployment time.", "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "define_host_name", "name": "Specify At Deployment Time", "dependencyTarget": null, "dependencyValue": null }, { "id": "generate_host_name", "name": "Auto Generate", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "scaleio_role", "dependencyValue": "hyperconverged", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "svm_os_host_name_template", "value": "svm-${server_hostname}", "datatype": "string", "componentid": "component-server-1", "name": "Host Name Template", "tooltip": "Template used to generate host names at deployment time. Must contain variables that will produce a unique host name.<br/>Allowed variables  are ${num} (an auto-generated unique number) and ${server_hostname} (uses server hostname).", "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "svm_assign_host_name", "dependencyValue": "generate_host_name", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "svm_os_host_name", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "Host Name", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": true, "hidefromtemplate": true, "dependencyTarget": "svm_assign_host_name", "dependencyValue": "define_host_name", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "svm_os_credential", "value": "ff808081665a6dcd01665a8072a2052a", "datatype": "oscredential", "componentid": "component-server-1", "name": "OS Credential", "tooltip": "Credential used to set username and password on the installed OS", "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "scaleio_role", "dependencyValue": "hyperconverged", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "svm_ntp_server", "value": "3.centos.pool.ntp.org", "datatype": "string", "componentid": "component-server-1", "name": "NTP Server", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "scaleio_role", "dependencyValue": "hyperconverged", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ensure", "value": "present", "datatype": "string", "componentid": "component-server-1", "name": null, "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": "none", "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }] }, { "id": "asm::idrac", "name": "Hardware Settings", "settings": [{ "id": "target_boot_device", "value": "LOCAL_FLASH_STORAGE_FOR_SCALE_IO", "datatype": "enum", "componentid": "component-server-1", "name": "Target Boot Device", "tooltip": "Local Flash Storage - Installs OS to either the SATADOM or BOSS flash storage device present in the server<br/><br/>Local Flash Storage for Dell EMC ScaleIO - Installs OS to either the SATADOM or BOSS flash storage device present in the server and configures the server to support ScaleIO", "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "LOCAL_FLASH_STORAGE", "name": "Local Flash Storage", "dependencyTarget": "scaleio_enabled", "dependencyValue": "false" }, { "id": "LOCAL_FLASH_STORAGE_FOR_SCALE_IO", "name": "Local Flash Storage for DellEMC VxFlex OS", "dependencyTarget": "scaleio_enabled", "dependencyValue": "true" }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": false, "isPreservedForDeployment": false }, { "id": "raid_configuration", "value": "", "datatype": "raidconfiguration", "componentid": "component-server-1", "name": "RAID", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": "target_boot_device", "dependencyValue": "HD", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": true }, { "id": "server_source", "value": "pool", "datatype": "enum", "componentid": "component-server-1", "name": "Server Source", "tooltip": "Method for server selection", "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "manual", "name": "Manual Entry", "dependencyTarget": null, "dependencyValue": null }, { "id": "pool", "name": "Server Pool", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": true, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "server_pool", "value": "-1", "datatype": "enum", "componentid": "component-server-1", "name": "Server Pool", "tooltip": "Pool from which servers are selected during deployment", "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "-1", "name": "Global", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": true, "hidefromtemplate": false, "dependencyTarget": "server_source", "dependencyValue": "pool", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "server_select", "value": "", "datatype": "enum", "componentid": "component-server-1", "name": "Choose Server", "tooltip": "Select specific server from a drop-down list", "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "ff808081665a6dcd01665a7a083c0093", "name": "4PMVHL2", "dependencyTarget": null, "dependencyValue": null }, { "id": "ff808081665a6dcd01665a7a08480094", "name": "7LD5KH2", "dependencyTarget": null, "dependencyValue": null }, { "id": "ff808081665a6dcd01665a7a08650095", "name": "4PNQHL2", "dependencyTarget": null, "dependencyValue": null }, { "id": "ff808081665a6dcd01665a7a08e60096", "name": "4PNSHL2", "dependencyTarget": null, "dependencyValue": null }, { "id": "ff808081665a6dcd01665a7a8424009a", "name": "7LD4KH2", "dependencyTarget": null, "dependencyValue": null }, { "id": "ff808081665a6dcd01665a7a850a009b", "name": "4PNPHL2", "dependencyTarget": null, "dependencyValue": null }, { "id": "ff808081665a6dcd01665a7a852e009c", "name": "7LD3KH2", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": true, "hidefromtemplate": true, "dependencyTarget": "server_source", "dependencyValue": "manual", "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "attempted_servers", "value": null, "datatype": "string", "componentid": "component-server-1", "name": "Attempted Servers", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ensure", "value": "present", "datatype": "string", "componentid": "component-server-1", "name": null, "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": "none", "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }] }, { "id": "asm::bios", "name": "BIOS Settings", "settings": [{ "id": "bios_configuration", "value": "basic", "datatype": "biosconfiguration", "componentid": "component-server-1", "name": "BIOS Settings", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "SysProfile", "value": "PerfOptimized", "datatype": "enum", "componentid": "component-server-1", "name": "System Profile", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "Custom", "name": "Custom", "dependencyTarget": null, "dependencyValue": null }, { "id": "DenseCfgOptimized", "name": "Dense Configuration", "dependencyTarget": null, "dependencyValue": null }, { "id": "n/a", "name": "Not Applicable", "dependencyTarget": null, "dependencyValue": null }, { "id": "PerfOptimized", "name": "Performance", "dependencyTarget": null, "dependencyValue": null }, { "id": "PerfPerWattOptimizedDapc", "name": "Performance Per Watt (DAPC)", "dependencyTarget": null, "dependencyValue": null }, { "id": "PerfPerWattOptimizedOs", "name": "Performance Per Watt (OS)", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "UsbPorts", "value": "AllOn", "datatype": "enum", "componentid": "component-server-1", "name": "User Accessible USB Ports", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "AllOff", "name": "All Ports Off", "dependencyTarget": null, "dependencyValue": null }, { "id": "AllOn", "name": "All Ports On", "dependencyTarget": null, "dependencyValue": null }, { "id": "n/a", "name": "Not Applicable", "dependencyTarget": null, "dependencyValue": null }, { "id": "OnlyBackPortsOn", "name": "Only Back Ports On", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ProcCores", "value": "All", "datatype": "enum", "componentid": "component-server-1", "name": "Number of Cores per Processor", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "1", "name": "1", "dependencyTarget": null, "dependencyValue": null }, { "id": "2", "name": "2", "dependencyTarget": null, "dependencyValue": null }, { "id": "4", "name": "4", "dependencyTarget": null, "dependencyValue": null }, { "id": "6", "name": "6", "dependencyTarget": null, "dependencyValue": null }, { "id": "All", "name": "All", "dependencyTarget": null, "dependencyValue": null }, { "id": "n/a", "name": "Not Applicable", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ProcVirtualization", "value": "Enabled", "datatype": "enum", "componentid": "component-server-1", "name": "Virtualization Technology", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "Disabled", "name": "Disabled", "dependencyTarget": null, "dependencyValue": null }, { "id": "Enabled", "name": "Enabled", "dependencyTarget": null, "dependencyValue": null }, { "id": "n/a", "name": "Not Applicable", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "LogicalProc", "value": "Enabled", "datatype": "enum", "componentid": "component-server-1", "name": "Logical Processor", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "Disabled", "name": "Disabled", "dependencyTarget": null, "dependencyValue": null }, { "id": "Enabled", "name": "Enabled", "dependencyTarget": null, "dependencyValue": null }, { "id": "n/a", "name": "Not Applicable", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ProcExecuteDisable", "value": "Enabled", "datatype": "enum", "componentid": "component-server-1", "name": "Execute Disable", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "Disabled", "name": "Disabled", "dependencyTarget": null, "dependencyValue": null }, { "id": "Enabled", "name": "Enabled", "dependencyTarget": null, "dependencyValue": null }, { "id": "n/a", "name": "Not Applicable", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "NodeInterleave", "value": "Enabled", "datatype": "enum", "componentid": "component-server-1", "name": "Node Interleaving", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "Disabled", "name": "Disabled", "dependencyTarget": null, "dependencyValue": null }, { "id": "Enabled", "name": "Enabled", "dependencyTarget": null, "dependencyValue": null }, { "id": "n/a", "name": "Not Applicable", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ensure", "value": "present", "datatype": "string", "componentid": "component-server-1", "name": null, "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": "none", "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }] }, { "id": "asm::esxiscsiconfig", "name": "Network Settings", "settings": [{ "id": "network_configuration", "value": "{\"id\":\"85e89ecc-281f-4158-a09e-2797bfa3a72f\",\"interfaces\":[{\"id\":\"be58b8a6-1823-4be3-b994-ed7f8974cb84\",\"name\":\"Interface\",\"interfaces\":[{\"id\":\"f1795dd6-5024-4d18-9c00-f39dbe04135d\",\"name\":\"Port 1\",\"partitions\":[{\"id\":\"cae34ca1-83a8-4a39-b98e-fe8127ef6332\",\"name\":\"1\",\"networks\":[\"ff808081665a6dcd01665a75a9a30013\"],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"eef7bf82-5db9-4f2c-80a8-5fb9b7b44966\",\"name\":\"2\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"c0963a5c-c128-4759-b4bd-257bf4745130\",\"name\":\"3\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"676b43c3-ad94-48b4-ae5d-05bdb145f3e6\",\"name\":\"4\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null}]},{\"id\":\"e384f118-1dbd-44d7-b07d-f912bc1ae974\",\"name\":\"Port 2\",\"partitions\":[{\"id\":\"c697d1ed-aab0-4d54-bbe9-3dd5f9e26788\",\"name\":\"1\",\"networks\":[\"ff808081665a6dcd01665a76e1010025\",\"ff808081665a6dcd01665a7913aa0086\",\"ff808081665a6dcd01665a7969be0087\",\"ff808081665a6dcd01665a78d2530063\"],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"e52ddb95-c5c7-4fae-887b-e6fd433bfae7\",\"name\":\"2\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"a637df8c-3c53-4416-b794-1cbcb79652ff\",\"name\":\"3\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"97502234-cc0a-46f5-af41-dd820bb0be43\",\"name\":\"4\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null}]},{\"id\":\"44627f51-db99-4e01-af72-10420e0b6978\",\"name\":\"Port 3\",\"partitions\":[{\"id\":\"15c6bce3-367c-44c6-ad1e-23d1dd27660e\",\"name\":\"1\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"9093a008-c04b-49c3-bc1f-73f493648015\",\"name\":\"2\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"d2bd99f8-709b-4248-bc2d-a889fbbc9429\",\"name\":\"3\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"d04d6f99-3184-4995-8c0c-26d3d8f81273\",\"name\":\"4\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null}]},{\"id\":\"75ab47ad-14cf-4179-8d92-c69dba0631dd\",\"name\":\"Port 4\",\"partitions\":[{\"id\":\"ea05ed76-14b3-43d8-8148-06e9c4f76395\",\"name\":\"1\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"9348742f-5e23-433b-a346-f8e39f617370\",\"name\":\"2\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"ccee639f-8b7a-4d71-aee7-bb3472d5d486\",\"name\":\"3\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"2caa44c8-39e1-468b-912c-cf4e1c58a1b7\",\"name\":\"4\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null}]}],\"enabled\":true,\"redundancy\":false,\"nictype\":\"2x10Gb\",\"partitioned\":false,\"fabrictype\":\"ethernet\"},{\"id\":\"b9899e08-8c72-4534-817b-08e862b821b6\",\"name\":\"Interface\",\"interfaces\":[{\"id\":\"ba22e0ca-cafe-46e2-8e35-772ff5caf71f\",\"name\":\"Port 1\",\"partitions\":[{\"id\":\"ca730272-b75e-46d0-9daf-b408213a21b4\",\"name\":\"1\",\"networks\":[\"ff808081665a6dcd01665a750f7f0001\"],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"94238946-f086-4196-9960-ac41b5cb36e9\",\"name\":\"2\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"a97b03e8-9c25-4cd5-b102-7b94317e9a09\",\"name\":\"3\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"12ead1d5-fc0e-4b5c-ba7f-b2b68d015f8e\",\"name\":\"4\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null}]},{\"id\":\"0cf2c17b-5f33-4889-b681-1f2874f6be2d\",\"name\":\"Port 2\",\"partitions\":[{\"id\":\"d8d2b037-8bc5-449c-88a5-d4f9cfa38b76\",\"name\":\"1\",\"networks\":[\"ff808081665a6dcd01665a76e1010025\",\"ff808081665a6dcd01665a7913aa0086\",\"ff808081665a6dcd01665a7969be0087\",\"ff808081665a6dcd01665a78d2530063\"],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"d463d97d-6569-4844-829e-a8fb38f80be8\",\"name\":\"2\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"973309ac-2b3d-4df8-b611-9cc35039e3ff\",\"name\":\"3\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"f2e5082d-897d-4194-8731-6393a8301118\",\"name\":\"4\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null}]},{\"id\":\"54e80a81-33f0-470f-942e-8d4c4ae1f022\",\"name\":\"Port 3\",\"partitions\":[{\"id\":\"df7d4b28-13cd-4c48-810f-206584c60ca0\",\"name\":\"1\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"6eef4cef-5f05-422f-82b5-26add036045b\",\"name\":\"2\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"d5d71ba2-1722-406a-963f-91e95c231c97\",\"name\":\"3\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"b42f00cf-dfbf-43c4-9301-78e745e6d3fa\",\"name\":\"4\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null}]},{\"id\":\"e0328696-40e6-4881-a0dc-59522cb5a69c\",\"name\":\"Port 4\",\"partitions\":[{\"id\":\"691abe64-22cd-4550-98ab-a71be6b5597a\",\"name\":\"1\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"efc75862-a10f-461c-9ac2-537f3cb2063f\",\"name\":\"2\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"8bd36924-d3b6-44d1-99a1-f2ebe88bedc5\",\"name\":\"3\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null},{\"id\":\"78a4a188-94d8-489e-b9f1-6e41dbf5d8e4\",\"name\":\"4\",\"networks\":[],\"virtualidentitypool\":null,\"minimum\":0,\"maximum\":100,\"boot\":false,\"personality\":null}]}],\"enabled\":true,\"redundancy\":false,\"nictype\":\"2x10Gb\",\"partitioned\":false,\"fabrictype\":\"ethernet\"}]}", "datatype": "networkconfiguration", "componentid": "component-server-1", "name": "Network Config", "tooltip": null, "required": false, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": false, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "default_gateway", "value": "", "datatype": "string", "componentid": "component-server-1", "name": "Static Network Default Gateway", "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "", "name": "Select...", "dependencyTarget": null, "dependencyValue": null }, { "id": "dhcp_workload", "name": "DHCP / No Gateway", "dependencyTarget": null, "dependencyValue": null }, { "id": "ff808081665a6dcd01665a750f7f0001", "name": "vcesys-sio-data1", "dependencyTarget": null, "dependencyValue": null }, { "id": "ff808081665a6dcd01665a75a9a30013", "name": "vcesys-sio-data2", "dependencyTarget": null, "dependencyValue": null }, { "id": "ff808081665a6dcd01665a76e1010025", "name": "vcesys-sio-mgmt", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "mtu", "value": "1500", "datatype": "enum", "componentid": "component-server-1", "name": "MTU size for bonded interfaces:", "tooltip": "Allows the Maximum Transfer Unit (MTU) to be set in the server Operating System. This will only take effect on bonded interfaces.", "required": true, "min": 0, "max": 0, "multiple": false, "options": [{ "id": "1500", "name": "1500", "dependencyTarget": null, "dependencyValue": null }, { "id": "9000", "name": "9000", "dependencyTarget": null, "dependencyValue": null }], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": null, "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }, { "id": "ensure", "value": "present", "datatype": "string", "componentid": "component-server-1", "name": null, "tooltip": null, "required": true, "min": 0, "max": 0, "multiple": false, "options": [], "requireatdeployment": false, "hidefromtemplate": true, "dependencyTarget": null, "dependencyValue": null, "addAction": null, "readOnly": false, "group": "none", "generated": false, "useinfotooltip": false, "maxlength": 256, "step": 1, "isOptionsSortable": true, "isPreservedForDeployment": false }] }], "referenceid": null, "referenceip": null, "referenceipurl": "https://null", "showNetworkInfo": true, "network": { "id": "85e89ecc-281f-4158-a09e-2797bfa3a72f", "interfaces": [{ "id": "be58b8a6-1823-4be3-b994-ed7f8974cb84", "name": "Interface", "interfaces": [{ "id": "f1795dd6-5024-4d18-9c00-f39dbe04135d", "name": "Port 1", "partitions": [{ "id": "cae34ca1-83a8-4a39-b98e-fe8127ef6332", "name": "1", "networks": ["ff808081665a6dcd01665a75a9a30013"], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "eef7bf82-5db9-4f2c-80a8-5fb9b7b44966", "name": "2", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "c0963a5c-c128-4759-b4bd-257bf4745130", "name": "3", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "676b43c3-ad94-48b4-ae5d-05bdb145f3e6", "name": "4", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }] }, { "id": "e384f118-1dbd-44d7-b07d-f912bc1ae974", "name": "Port 2", "partitions": [{ "id": "c697d1ed-aab0-4d54-bbe9-3dd5f9e26788", "name": "1", "networks": ["ff808081665a6dcd01665a76e1010025", "ff808081665a6dcd01665a7913aa0086", "ff808081665a6dcd01665a7969be0087", "ff808081665a6dcd01665a78d2530063"], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "e52ddb95-c5c7-4fae-887b-e6fd433bfae7", "name": "2", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "a637df8c-3c53-4416-b794-1cbcb79652ff", "name": "3", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "97502234-cc0a-46f5-af41-dd820bb0be43", "name": "4", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }] }, { "id": "44627f51-db99-4e01-af72-10420e0b6978", "name": "Port 3", "partitions": [{ "id": "15c6bce3-367c-44c6-ad1e-23d1dd27660e", "name": "1", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "9093a008-c04b-49c3-bc1f-73f493648015", "name": "2", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "d2bd99f8-709b-4248-bc2d-a889fbbc9429", "name": "3", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "d04d6f99-3184-4995-8c0c-26d3d8f81273", "name": "4", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }] }, { "id": "75ab47ad-14cf-4179-8d92-c69dba0631dd", "name": "Port 4", "partitions": [{ "id": "ea05ed76-14b3-43d8-8148-06e9c4f76395", "name": "1", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "9348742f-5e23-433b-a346-f8e39f617370", "name": "2", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "ccee639f-8b7a-4d71-aee7-bb3472d5d486", "name": "3", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "2caa44c8-39e1-468b-912c-cf4e1c58a1b7", "name": "4", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }] }], "enabled": true, "redundancy": false, "nictype": "2x10Gb", "partitioned": false, "fabrictype": "ethernet" }, { "id": "b9899e08-8c72-4534-817b-08e862b821b6", "name": "Interface", "interfaces": [{ "id": "ba22e0ca-cafe-46e2-8e35-772ff5caf71f", "name": "Port 1", "partitions": [{ "id": "ca730272-b75e-46d0-9daf-b408213a21b4", "name": "1", "networks": ["ff808081665a6dcd01665a750f7f0001"], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "94238946-f086-4196-9960-ac41b5cb36e9", "name": "2", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "a97b03e8-9c25-4cd5-b102-7b94317e9a09", "name": "3", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "12ead1d5-fc0e-4b5c-ba7f-b2b68d015f8e", "name": "4", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }] }, { "id": "0cf2c17b-5f33-4889-b681-1f2874f6be2d", "name": "Port 2", "partitions": [{ "id": "d8d2b037-8bc5-449c-88a5-d4f9cfa38b76", "name": "1", "networks": ["ff808081665a6dcd01665a76e1010025", "ff808081665a6dcd01665a7913aa0086", "ff808081665a6dcd01665a7969be0087", "ff808081665a6dcd01665a78d2530063"], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "d463d97d-6569-4844-829e-a8fb38f80be8", "name": "2", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "973309ac-2b3d-4df8-b611-9cc35039e3ff", "name": "3", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "f2e5082d-897d-4194-8731-6393a8301118", "name": "4", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }] }, { "id": "54e80a81-33f0-470f-942e-8d4c4ae1f022", "name": "Port 3", "partitions": [{ "id": "df7d4b28-13cd-4c48-810f-206584c60ca0", "name": "1", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "6eef4cef-5f05-422f-82b5-26add036045b", "name": "2", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "d5d71ba2-1722-406a-963f-91e95c231c97", "name": "3", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "b42f00cf-dfbf-43c4-9301-78e745e6d3fa", "name": "4", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }] }, { "id": "e0328696-40e6-4881-a0dc-59522cb5a69c", "name": "Port 4", "partitions": [{ "id": "691abe64-22cd-4550-98ab-a71be6b5597a", "name": "1", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "efc75862-a10f-461c-9ac2-537f3cb2063f", "name": "2", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "8bd36924-d3b6-44d1-99a1-f2ebe88bedc5", "name": "3", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }, { "id": "78a4a188-94d8-489e-b9f1-6e41dbf5d8e4", "name": "4", "networks": [], "virtualidentitypool": null, "minimum": 0, "maximum": 100, "boot": false, "personality": null }] }], "enabled": true, "redundancy": false, "nictype": "2x10Gb", "partitioned": false, "fabrictype": "ethernet" }] }, "newComponent": null, "cloned": false, "continueClicked": null, "AsmGUID": null, "puppetCertName": null, "clonedFromId": null, "allowClone": true, "isComponentValid": false, "raid": null, "configfilename": null, "instances": 3, "errorObj": { "errorMessage": "---consolidated message/header...", "fldErrors": [{ "field": null, "errorMessage": "Component VMWare Cluster is missing data for required field: Target Virtual Machine Manager.", "errorDetails": "The template cannot be published with missing required fields.", "errorAction": "Please enter a valid value for the required field.", "errorCode": "VXFM00215" }] } }], "attachments": [], "category": "Test", "enableApps": false, "enableVMs": false, "enableCluster": false, "enableServer": false, "enableStorage": false, "allStandardUsers": false, "assignedUsers": [], "isValid": false, "manageFirmware": true, "firmwarePackageId": "usedefaultcatalog", "firmwarePackageName": "Use VxFM appliance default catalog", "updateServerFirmware": false, "updateNetworkFirmware": false, "updateStorageFirmware": false, "isLocked": false, "configureTemplateConfiguration": null, "source": "VxRack FLEX", "inConfiguration": false, "originalId": null } };
                self.template = data.data.responseObj;
                self.templateCopy = angular.copy(self.template);
                if (self.mode === 'edit') {
                    self.template.draft = true;
                }
                self.refresh();
                self.windowResize();
                //self.calculatewindowheight();
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            })
                .finally(function () { return d.resolve(); });
        };
        TemplatebuildersvgController.prototype.editApplications = function (id) {
            var self = this;
            $('.popover').remove();
            var addApplicationWizard = self.Modal({
                title: self.$translate.instant('TEMPLATES_EditApplication'),
                onHelp: function () {
                    self.GlobalServices.showHelp('addapplication');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/addapplicationwizard.html',
                controller: 'AddApplicationWizardController as Application',
                onComplete: function () {
                    self.mode === 'edit';
                    self.refresh();
                },
                params: {
                    template: self.template,
                    action: 'edit',
                    id: id
                }
            });
            addApplicationWizard.modal.show();
        };
        TemplatebuildersvgController.prototype.deleteApplications = function (id) {
            var self = this, parentComponent = _.find(self.template.components, { id: id });
            var applicationIds = _.map(_.filter(parentComponent.relatedcomponents, function (component) { return component.installOrder > 0; }), function (application) { return { id: application.id }; });
            angular.forEach([parentComponent.relatedcomponents, self.template.components], function (array) { return _.pullAllBy(array, applicationIds, "id"); });
            self.$http.post(self.Commands.data.templates.saveTemplate, self.template)
                .then(function () {
                self.mode === 'edit';
                self.getTemplateData();
            })
                .catch(function (data) { return self.GlobalServices.DisplayError(data.data); });
        };
        TemplatebuildersvgController.prototype.refresh = function () {
            var self = this;
            $('#TemplateBuilderSVGLines').empty();
            $('#TemplateBuilderSVGHoverLines').empty();
            $('#TemplateBuilderSVGSelectedLines').empty();
            if (self.template === '') {
                self.getTemplateData();
                self.getNetworkData();
            }
            else {
                self.getTemplateServers();
                self.getTemplateCluster();
                self.getTemplateStorage();
                self.getTemplateVMs();
            }
        };
        //Filters data and figures out which device type has the most components
        TemplatebuildersvgController.prototype.getTemplateServers = function () {
            var self = this;
            self.templateServers = _.filter(self.template.components, { 'type': 'server' });
            if (self.templateServers.length && self.templateServers.length >= self.mostItems) {
                self.mostItems = self.templateServers.length;
                self.mostComponents = 'serverWidth';
                var arrayitem = self.templateServers.length - 1;
                self.furthestComponentId = self.templateServers[arrayitem].id;
            }
            self.drawlines(self.templateServers);
        };
        TemplatebuildersvgController.prototype.getTemplateStorage = function () {
            var self = this;
            self.templateStorages = _.filter(self.template.components, { 'type': 'storage' });
            if (self.templateStorages.length && self.templateStorages.length >= self.mostItems) {
                self.mostItems = self.templateStorages.length;
                self.mostComponents = 'storageWidth';
                var arrayitem = self.templateStorages.length - 1;
                self.furthestComponentId = self.templateStorages[arrayitem].id;
            }
            self.drawlines(self.templateStorages);
        };
        TemplatebuildersvgController.prototype.getTemplateCluster = function () {
            var self = this;
            //self.templateClusters = _.filter(self.template.components, { 'type': 'cluster' });
            self.templateClusters = _.filter(self.template.components, function (component) { return component.type == 'cluster' || component.type == 'scaleio'; });
            if (self.templateClusters.length && self.templateClusters.length >= self.mostItems) {
                self.mostItems = self.templateClusters.length;
                self.mostComponents = 'clusterWidth';
                var arrayitem = self.templateClusters.length - 1;
                self.furthestComponentId = self.templateClusters[arrayitem].id;
            }
            self.drawlines(self.templateClusters);
        };
        TemplatebuildersvgController.prototype.getTemplateVMs = function () {
            var self = this;
            self.templateVMs = _.filter(self.template.components, { 'type': 'vm' });
            if (self.templateVMs.length && self.templateVMs.length >= self.mostItems) {
                self.mostItems = self.templateVMs.length;
                self.mostComponents = 'vmWidth';
                var arrayitem = self.templateVMs.length - 1;
                self.furthestComponentId = self.templateVMs[arrayitem].id;
            }
            self.drawlines(self.templateVMs);
        };
        TemplatebuildersvgController.prototype.drawlines = function (x) {
            var self = this;
            self.$timeout(function () {
                x.forEach(function (component) {
                    component.relatedcomponents.forEach(function (related) {
                        var newFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var newHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var newSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                        var x1 = $('#TemplateBuilderSVG #' + component.id).attr('x');
                        var y1 = $('#TemplateBuilderSVG #' + component.id).attr('y');
                        var x2 = $('#TemplateBuilderSVG #' + related.id).attr('x');
                        var y2 = $('#TemplateBuilderSVG #' + related.id).attr('y');
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
        TemplatebuildersvgController.prototype.hoverLine = function (x) {
            var self = this;
            self.componentHover = x;
            x.relatedcomponents.forEach(function (related) {
                var HoverFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var HoverHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var HoverSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var x1 = $('#TemplateBuilderSVG #' + x.id).attr('x');
                var y1 = $('#TemplateBuilderSVG #' + x.id).attr('y');
                var x2 = $('#TemplateBuilderSVG #' + related.id).attr('x');
                var y2 = $('#TemplateBuilderSVG #' + related.id).attr('y');
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
        TemplatebuildersvgController.prototype.removeHovers = function () {
            var self = this;
            $('#TemplateBuilderSVGHoverLines').empty();
            self.componentHover = '';
        };
        TemplatebuildersvgController.prototype.removeSelection = function () {
            var self = this;
            $('#TemplateBuilderSVGSelectedLines').empty();
            self.selectedComponent = '';
        };
        TemplatebuildersvgController.prototype.selectLine = function (x) {
            var self = this;
            $('#TemplateBuilderSVGSelectedLines').empty();
            self.selectedComponent = x;
            $.each(x.relatedcomponents, function (index, related) {
                //x.relatedcomponents.forEach((related: any) => {
                var SelectedFirstVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var SelectedHorizontal = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var SelectedSecondVertical = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                var x1 = $('#TemplateBuilderSVG #' + x.id).attr('x');
                var y1 = $('#TemplateBuilderSVG #' + x.id).attr('y');
                var x2 = $('#TemplateBuilderSVG #' + related.id).attr('x');
                var y2 = $('#TemplateBuilderSVG #' + related.id).attr('y');
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
        TemplatebuildersvgController.prototype.hasApplication = function (component) {
            if (component.type == 'storage' || component.type == 'cluster' || component.type === 'scaleio')
                return false;
            var hasApplication = false;
            $.each(component.relatedcomponents, function (index, model) {
                if (model.installOrder > 0) {
                    hasApplication = true;
                    return;
                }
            });
            return hasApplication;
        };
        TemplatebuildersvgController.prototype.addComponent = function (resourceType) {
            var self = this;
            if (resourceType == 'application') {
                var addApplicationWizard = self.Modal({
                    title: self.$translate.instant('ADDAPPLICATION_Title'),
                    onHelp: function () {
                        self.GlobalServices.showHelp('addapplication');
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/addapplicationwizard.html',
                    controller: 'AddApplicationWizardController as Application',
                    onComplete: function () {
                        self.refresh();
                    },
                    params: {
                        template: self.template,
                        action: 'add'
                    },
                    onCancel: function () {
                        addApplicationWizard.modal.dismiss();
                        self.getTemplateData();
                    },
                });
                addApplicationWizard.modal.show();
            }
            else {
                var title = '';
                var helptoken;
                switch (resourceType) {
                    case 'storage':
                        title = self.$translate.instant('COMPONENTEDITOR_StorageComponent');
                        helptoken = 'addstorage';
                        break;
                    case 'server':
                        title = self.$translate.instant('COMPONENTEDITOR_ServerComponent');
                        helptoken = 'addserver';
                        break;
                    case 'cluster':
                        title = self.$translate.instant('COMPONENTEDITOR_ClusterComponent');
                        helptoken = 'addcluster';
                        break;
                    case 'vm':
                        title = self.$translate.instant('COMPONENTEDITOR_VMComponent');
                        helptoken = 'addappliance';
                        break;
                }
                var adduserModal = self.Modal({
                    title: title,
                    onHelp: function () {
                        self.GlobalServices.showHelp(helptoken);
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/addcomponentmodal.html',
                    controller: 'AddComponentController as AddComponent',
                    params: {
                        mode: 'edit',
                        action: 'add',
                        type: resourceType,
                        templateId: self.selectedTemplateId,
                        serviceId: null,
                        template: self.template
                    },
                    onComplete: function () {
                        self.mode === 'edit';
                        self.getTemplateData();
                    }
                });
                adduserModal.modal.show();
            }
        };
        TemplatebuildersvgController.prototype.editComponent = function (component, mode) {
            var self = this, title;
            var helptoken;
            $(".popover").remove();
            switch (component.type) {
                case 'storage':
                    title = self.$translate.instant('COMPONENTEDITOR_StorageComponent');
                    helptoken = 'addstorage';
                    break;
                case 'server':
                    title = self.$translate.instant('COMPONENTEDITOR_ServerComponent');
                    helptoken = 'addserver';
                    break;
                case 'cluster':
                    title = self.$translate.instant('COMPONENTEDITOR_VmWareCluster');
                    helptoken = 'addcluster';
                    break;
                case 'scaleio':
                    title = self.$translate.instant('COMPONENTEDITOR_VxFlexOsCluster');
                    helptoken = 'addcluster';
                    break;
                case 'vm':
                    title = self.$translate.instant('COMPONENTEDITOR_VMComponent');
                    helptoken = 'addappliance';
                    break;
            }
            var addComponentModal = self.Modal({
                title: title,
                onHelp: function () {
                    self.GlobalServices.showHelp(helptoken);
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/addcomponentmodal.html',
                controller: 'AddComponentController as AddComponent',
                params: {
                    mode: mode,
                    action: mode,
                    type: component.type,
                    templateId: self.selectedTemplateId,
                    template: angular.copy(self.template),
                    componentId: component.id,
                    component: component
                },
                onComplete: function () {
                    self.mode === 'edit';
                    self.getTemplateData();
                }
            });
            addComponentModal.modal.show();
        };
        TemplatebuildersvgController.prototype.getNetworkData = function () {
            var self = this;
            self.$http.post(self.Commands.data.networking.networks.getNetworksList, [])
                .then(function (response) {
                self.networks = response.data.responseObj;
            });
        };
        TemplatebuildersvgController.prototype.deleteComponent = function (id) {
            var self = this;
            $('.popover').remove();
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('TEMPLATEBUILDER_Areyousureyouwanttoremovethiscomponent')).then(function () {
                angular.forEach(self.template.components, function (component) {
                    _.remove(component.relatedcomponents, { id: id });
                });
                _.remove(self.template.components, { id: id });
                self.$http.post(self.Commands.data.templates.saveTemplate, self.template).then(function (data) {
                    self.mode === 'edit';
                    self.getTemplateData();
                }).catch(function (data) {
                    self.GlobalServices.DisplayError(data.data);
                });
            });
        };
        TemplatebuildersvgController.prototype.addAttachment = function () {
            var self = this;
            var addAttachmentModal = self.Modal({
                title: 'Add Attachment',
                onHelp: function () {
                    self.GlobalServices.showHelp('templateAddAttachment');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/templates/templateaddattachmentmodal.html',
                controller: 'AddAttachmentModalController as AddAttachmentModal',
                params: {
                    templateId: self.template.id
                },
                onComplete: function () {
                    self.getTemplateData();
                    //self.selectedItem = null;
                }
            });
            addAttachmentModal.modal.show();
        };
        TemplatebuildersvgController.prototype.deleteAttachment = function (attachment) {
            var self = this;
            self.Dialog((self.$translate.instant('GENERIC_Confirm')), self.$translate.instant('TEMPLATEBUILDER_Areyousureyouwanttodeletethisattachment'))
                .then(function () {
                self.$http.post(self.Commands.data.templates.deleteAttachment, { id: self.template.id, name: attachment.name })
                    .then(function () {
                    self.getTemplateData();
                    //self.selectedItem = null;
                })
                    .catch(function (data) {
                    //error is in data
                    self.GlobalServices.DisplayError(data.data);
                });
            });
        };
        TemplatebuildersvgController.prototype.downloadAttachment = function (attachment) {
            var self = this;
            var url = 'templates/downloadattachment?name=' + attachment.name + '&templateId=' + self.template.id;
            self.$window.open(url, '_blank');
            //self.config = {
            //    directPost: true,
            //    headers: { 'Content-Type': undefined },
            //    transformRequest: angular.identity
            //};
            //return self.$http.post('/templates/downloadattachment', attachment, self.config);
        };
        TemplatebuildersvgController.prototype.getViewDetailsAction = function () {
            var self = this;
            return self.editComponent(self.selectedComponent, (self.mode !== 'edit' || self.GlobalServices.IsInRole('readonly')) ? "view" : "edit");
        };
        TemplatebuildersvgController.prototype.deployNewService = function () {
            var self = this;
            var addServiceWizard = self.Modal({
                title: self.$translate.instant('SERVICES_NEW_SERVICE_DeployService'),
                onHelp: function () {
                    self.GlobalServices.showHelp('deployingserviceoverview');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/deployservice/deployservicewizard.html',
                controller: 'DeployServiceWizard as deployServiceWizard',
                params: {
                    //selectedService: self.selectedItem
                    templateId: self.template.id
                },
                onCancel: function () {
                    self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_DEPLOY_ConfirmWizardClosing'))
                        .then(function () {
                        addServiceWizard.modal.close();
                    });
                }
            });
            addServiceWizard.modal.show();
        };
        TemplatebuildersvgController.prototype.cloneTemplate = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.$http.post(self.Commands.data.templates.getTemplateList, {})
                .then(function (response) {
                var createTemplateModal = self.Modal({
                    title: self.$translate.instant("TEMPLATES_CREATE_TEMPLATE_WIZARD_cloneTemplate"),
                    onHelp: function () {
                        self.GlobalServices.showHelp('cloningtemplate');
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/createtemplate.html',
                    controller: 'CreateTemplateModalController as createTemplateModalController',
                    params: {
                        type: 'clone',
                        template: self.template,
                        templates: response.data.responseObj
                    },
                    onComplete: function () {
                    },
                    close: function () {
                    }
                });
                createTemplateModal.modal.show();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data);
                self.template.draft = true;
                self.mode === 'edit';
            });
        };
        TemplatebuildersvgController.prototype.editMode = function () {
            var self = this;
            self.$location.path("/templatebuilder/" + self.template.id + "/edit");
            self.mode === 'edit';
            self.template.draft = true;
        };
        TemplatebuildersvgController.prototype.viewDetails = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('TEMPLATES_TEMPLATESETTINGS_MODAL_TemplateSettings'),
                onHelp: function () {
                    self.GlobalServices.showHelp('templatesettings');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/templatebuilder/viewtemplatedetailsmodal.html',
                controller: 'ViewTemplateDetailsModalController as viewTemplateDetailsModalController',
                params: {
                    template: self.template
                },
                close: function () {
                    self.$scope.modal.close();
                },
            });
            modal.modal.show();
        };
        TemplatebuildersvgController.prototype.editTemplateInformation = function () {
            var self = this;
            self.GlobalServices.ClearErrors();
            self.$http.post(self.Commands.data.templates.getTemplateList, {})
                .then(function (response) {
                var createTemplateModal = self.Modal({
                    title: self.$translate.instant('TEMPLATES_EditTemplateInformation'),
                    onHelp: function () {
                        self.GlobalServices.showHelp('TemplateEditingTemplateInformation');
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/createtemplate.html',
                    controller: 'CreateTemplateModalController as createTemplateModalController',
                    params: {
                        //selecteduser: modalUser
                        templates: response.data.responseObj,
                        template: self.template,
                        type: 'edit'
                    },
                    onComplete: function (id) {
                        self.getTemplateData();
                    },
                    close: function () {
                        self.getTemplateData();
                    }
                });
                createTemplateModal.modal.show();
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            });
        };
        TemplatebuildersvgController.prototype.publishTemplate = function () {
            var self = this;
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('SERVICES_DEPLOY_Confirm'))
                .then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.template.draft = false;
                self.publish(self.template)
                    .then(function (response) {
                    d.resolve();
                    self.$timeout(function () {
                        self.$location.path("templatebuilder/" + self.template.id + "/view");
                    }, 500);
                })
                    .catch(function (response) {
                    d.resolve();
                    self.GlobalServices.DisplayError(response.data);
                    self.template.draft = true;
                    self.mode === 'edit';
                })
                    .finally(function () { return d.resolve(); });
            })
                .catch(function () { });
        };
        TemplatebuildersvgController.prototype.publish = function (template) {
            var self = this;
            return self.$http.post(self.Commands.data.templates.saveTemplate, template);
        };
        TemplatebuildersvgController.prototype.deleteTemplate = function () {
            var self = this;
            self.Dialog(("Confirm"), self.$translate.instant("TEMPLATEBUILDER_DiscardConfirm"))
                .then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.templates.discardTemplate, [self.template.id.toString()])
                    .then(function (data) {
                    self.$location.path('templates');
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        TemplatebuildersvgController.prototype.importTemplate = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('IMPORTTEMPLATE_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp('ImportingTemplates');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/importtemplatemodal.html',
                controller: 'ImportTemplateModalController as importtemplate',
                params: {
                    template: self.template.id
                },
                onComplete: function () {
                    self.getTemplateData();
                },
            });
            modal.modal.show();
        };
        TemplatebuildersvgController.prototype.getInterfaceNumTranslation = function (interfaceNum) {
            var self = this;
            return self.$translate.instant("TEMPLATEBUILDER_InterfaceNum", { interfaceNum: interfaceNum });
        };
        TemplatebuildersvgController.prototype.getPartitionTranslation = function (partitionName) {
            var self = this;
            return self.$translate.instant("TEMPLATEBUILDER_Partition", { partitionName: partitionName });
        };
        TemplatebuildersvgController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', 'GlobalServices', '$route',
            'localStorageService', '$routeParams', '$compile', '$scope', '$translate',
            '$location', '$window', 'Loading', '$rootScope', 'Commands'];
        return TemplatebuildersvgController;
    }());
    function templateBuilder() {
        return {
            restrict: 'E',
            templateUrl: 'views/templatebuildersvg.html',
            replace: true,
            transclude: false,
            controller: TemplatebuildersvgController,
            controllerAs: 'templatebuildersvg',
            scope: {
                selectedTemplateId: '=routeparam',
                mode: '=mode',
                errors: '=?'
            },
            link: function (scope, element, attributes, controller) {
            }
        };
    }
    angular.module('app').
        directive('templateBuilder', templateBuilder);
})(asm || (asm = {}));
//# sourceMappingURL=templatebuilderSVGdirective.js.map
