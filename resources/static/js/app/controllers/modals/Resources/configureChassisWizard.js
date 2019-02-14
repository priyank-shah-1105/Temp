var asm;
(function (asm) {
    var ConfigureChassisWizardController = (function () {
        function ConfigureChassisWizardController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, commands, GlobalServices, constants, $filter, $anchorScroll, filteredDevicesFilter, $rootScope, fileUploader) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.commands = commands;
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            this.$filter = $filter;
            this.$anchorScroll = $anchorScroll;
            this.filteredDevicesFilter = filteredDevicesFilter;
            this.$rootScope = $rootScope;
            this.fileUploader = fileUploader;
            this.mode = "resource";
            this.chassisDevices = this.$scope.modal.params.devices;
            this.deviceConfigEditMode1 = true;
            this.deviceConfigEditMode2 = false;
            this.summary_display_row_firmwareCompliance = true;
            this.allFirmwareSelected = false;
            this.vltObj = {
                uplinkName: "VLT",
                uplinkId: "VLT",
                portChannel: '',
                networks: [],
                networkNames: []
            };
            this.errors = new Array();
            this.displayedDevices = [];
            this.chassisFound = false;
            this.selectedFirmwarePackage = null;
            this.isIOMconfigurable = true;
            this.steps = {
                ioModuleSettings: { invalid: false }
            };
            this.forms = {};
            this.view = {};
            var self = this;
            self.mode = self.$scope.modal.params.mode;
            self.view = {
                //see deviceConfig.ts version of this for what this is used for
                editMode: function () {
                    return self.deviceConfigEditMode1 ? "edit_" : "view_";
                }
            };
            self.validRedundancyPolicies = angular.copy(constants.availableRedundancyPolicies);
            self.refresh();
            var uploader = $scope.uploader = new fileUploader({
                url: self.commands.data.configureChassis.uploadPortConfiguration,
            });
        }
        ConfigureChassisWizardController.prototype.updatePowerSettings = function () {
            var self = this;
            var fxcount = 0;
            var m1000count = 0;
            var policies = [];
            policies = angular.copy(self.constants.availableRedundancyPolicies);
            angular.forEach(self.filteredconfigureResources, function (device) {
                if (device.chassisConfiguration && device.chassisConfiguration.configChassis === true) {
                    if (device.resourceType === 'ChassisFX') {
                        fxcount++;
                    }
                    else {
                        m1000count++;
                    }
                }
            });
            if (fxcount > 0 && m1000count > 0) {
                policies = angular.copy(self.constants.availableRedundancyPolicies);
            }
            else if (fxcount > 0) {
                policies.splice(2, 1);
            }
            else {
                policies.splice(3, 1);
            }
            self.validRedundancyPolicies = angular.copy(policies);
        };
        ConfigureChassisWizardController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.loadingResources = true;
            self.Loading(d.promise);
            self.$q.all([
                self.getConfigurableResources(true, self.chassisDevices &&
                    {
                        filterObj: [
                            {
                                field: "id",
                                op: "=",
                                opTarget: self.chassisDevices
                            }
                        ]
                    })
                    .then(function (data) {
                    self.configureResources = data.data.responseObj;
                    self.markChassis();
                    //sort devices
                    self.configureResources.devices = self.$filter("sortConfigureDevices")(self.configureResources.devices);
                    //set compliance of devices
                    $.each(self.configureResources.devices, function (index, device) {
                        $.each(device.firmwarecomponents, function (index2, fc) {
                            //ASM-6731 UI - Block users from configuring FN-IOA uplinks with firmware less than 9.9
                            if (!fc.compliant || (device.fnioa && device.fnioaUpdateRequired)) {
                                device.compliant = false;
                                device.rowChecked = false;
                            }
                        });
                    });
                    self.displayedDevices = angular.copy(self.configureResources.devices);
                    //set table for Firmware Compliance
                    self.firmwareComplianceDevices = _.filter(self.configureResources.devices, function (resource) { return resource.complianceDetails !== "compliant"; });
                    self.firmwareComplianceDevicesSafe = angular.copy(self.firmwareComplianceDevices);
                    //filter for chassis
                    self.filteredconfigureResources = self.$filter('filteredDevices')(self.configureResources.devices);
                    self.configureResources.configIOMMode = (self.filteredconfigureResources.length &&
                        self.configureResources.canConfigAllIOM == true)
                        ? "all"
                        : "independent";
                }),
                self.getFirmwarePackages()
                    .then(function (response) {
                    self.firmwarePackages = response.data.responseObj;
                    if (self.selectedFirmwarePackage == null)
                        self.selectedFirmwarePackage = self.firmwarePackages[0];
                })
            ])
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () {
                self.loadingResources = false;
                d.resolve();
            });
            return d;
        };
        ConfigureChassisWizardController.prototype.enterWelcome = function () {
            var self = this;
            self.$rootScope.helpToken = 'configuringchassis';
        };
        ConfigureChassisWizardController.prototype.enterDiscoveredResources = function () {
            var self = this;
            self.$rootScope.helpToken = 'configurechassisdiscoveredresources';
        };
        ConfigureChassisWizardController.prototype.enterDefaultFirmwareRepository = function () {
            var self = this;
            self.$rootScope.helpToken = 'configurechassisconfiguredefaultrepository';
        };
        ConfigureChassisWizardController.prototype.enterFirmwareCompliance = function () {
            var self = this;
            self.$rootScope.helpToken = 'configurechassisfirmwarecompliance';
        };
        ConfigureChassisWizardController.prototype.enterChassisConfiguration = function () {
            var self = this;
            self.$rootScope.helpToken = 'configurechassischassisglobal';
            self.updatePowerSettings();
        };
        ConfigureChassisWizardController.prototype.enterUniqueChassisSettings = function () {
            var self = this;
            self.$rootScope.helpToken = 'configurechassisconfigurechassis';
            //in new asm code, these are no longer individual wizard steps, they are child accordions on the unique chassis settings wizard step
            //self.$rootScope.helpToken = 'configurechassisconfigureserver';
            //self.$rootScope.helpToken = 'configurechassisconfigureIOmodules';
        };
        ConfigureChassisWizardController.prototype.enterIOModuleConfiguration = function () {
            var self = this;
            self.$rootScope.helpToken = 'configurechassiconfigureuplinks';
        };
        ConfigureChassisWizardController.prototype.enterSummary = function () {
            var self = this;
            self.$rootScope.helpToken = 'ConfigureChassisSummary';
        };
        ConfigureChassisWizardController.prototype.finishWizard = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.configureChassis(self.configureResources)
                .then(function () {
                self.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ConfigureChassisWizardController.prototype.goToIndex = function (x) {
            var self = this;
            if (self.mode == 'resource') {
                self.$scope.$root.$broadcast("wizard:goto", 3);
            }
            else {
                self.$scope.$root.$broadcast("wizard:goto", 1);
            }
        };
        ConfigureChassisWizardController.prototype.uplinkPortConfigurationShown = function () {
            var self = this;
            //Check for FNIOA and if they are configurable (ASM-6731 UI - Block users from configuring FN-IOA uplinks with firmware less than 9.9)
            self.isIOMconfigurable = _.filter(self.configureResources.devices, function (device) {
                return device.isFNioa && device.fnioaUpdateRequired && !device.updateFW;
            }).length === 0;
        };
        ConfigureChassisWizardController.prototype.getUplinkList = function (arr, vltenabled) {
            var self = this;
            var x = angular.copy(arr);
            if (vltenabled)
                x.unshift(self.vltObj);
            return x;
        };
        ConfigureChassisWizardController.prototype.getUplinksLength = function (options) {
            var self = this;
            return !!_.find(options, { id: self.vltObj.id }) ? options.length - 1 : options.length;
        };
        ConfigureChassisWizardController.prototype.getUplinks = function (options, vltEnabled) {
            var self = this;
            var hasVltObj = !!_.find(options, { id: self.vltObj.id });
            if (vltEnabled) {
                return hasVltObj ? options : [self.vltObj].concat(options);
            }
            else {
                return hasVltObj ? _.pull(options, self.vltObj) : options;
            }
        };
        ConfigureChassisWizardController.prototype.sortConfigureDevices = function (devices) {
            var self = this;
            //sorts into 1. Chassis, 2. Servers, 3. Other 
            var devicesCopy = angular.copy(devices);
            var chassis = _.remove(devicesCopy, function (device) { return (device.chassisId || device.id) && self.$filter("isTypeChassis")(device); });
            var servers = _.remove(devicesCopy, function (device) { return device.resourceType.indexOf('Server') >= 0; });
            var other = devicesCopy;
            return _.concat(chassis, servers, other);
        };
        ConfigureChassisWizardController.prototype.getAllFirmwareSelected = function () {
            var self = this;
            return self.allFirmwareSelected = !_.find(self.configureResources.devices, { updateFW: false });
        };
        ConfigureChassisWizardController.prototype.getCheckedChassis = function () {
            var self = this;
            return _.filter(self.configureResources.devices, function (device) {
                return device.isChecked === true && self.isTypeChassis(device.deviceType);
            }).map(function (chassis) { return chassis.id; });
        };
        ConfigureChassisWizardController.prototype.selectAllFirmware = function () {
            var self = this, selectAll = !self.getAllFirmwareSelected();
            self.configureResources.devices = _.map(self.configureResources.devices, function (device) {
                device.updateFW = selectAll;
                return device;
            });
            self.getAllFirmwareSelected();
        };
        ConfigureChassisWizardController.prototype.getSelectedFirmware = function (devices) {
            var self = this;
            return _.filter(angular.isDefined(devices) ? devices : self.configureResources.devices, { updateFW: true });
        };
        ConfigureChassisWizardController.prototype.getNonCompliantFirmware = function () {
            var self = this;
            return _.filter(self.configureResources.devices, { complianceDetails: "noncompliant" });
        };
        ConfigureChassisWizardController.prototype.getChassisNonCompliantValues = function () {
            var self = this;
            return {
                numIdentified: self.getNonCompliantFirmware().length,
                numSelected: self.getSelectedFirmware(self.getNonCompliantFirmware()).length
            };
        };
        ConfigureChassisWizardController.prototype.getUpdateRequiredFirmware = function () {
            var self = this;
            return _.filter(self.configureResources.devices, { complianceDetails: "updaterequired" });
        };
        ConfigureChassisWizardController.prototype.getChassisRequiredUpdateValues = function () {
            var self = this;
            return {
                numIdentified: self.getUpdateRequiredFirmware().length,
                numSelected: self.getSelectedFirmware(self.getUpdateRequiredFirmware()).length
            };
        };
        ConfigureChassisWizardController.prototype.isTypeChassis = function (type) {
            return !!_.find(["ChassisM1000e", "ChassisVRTX", "ChassisFX"], function (val) { return val === type; });
        };
        ConfigureChassisWizardController.prototype.markChassis = function () {
            var self = this;
            self.chassisFound = false;
            angular.forEach(self.configureResources.devices, function (resource) {
                if (self.isTypeChassis(resource.resourceType)) {
                    resource.isChassis =
                        resource.chassisConfiguration.configChassis =
                            self.chassisFound = true;
                    resource.chassisId = resource.id;
                }
                ;
            });
        };
        ConfigureChassisWizardController.prototype.topLevelDevice = function (device) {
            var self = this;
            if (self.isTypeChassis(device.resourceType))
                return true;
            if (device.chassisId)
                return false;
            return true;
        };
        ConfigureChassisWizardController.prototype.modularDevice = function (device) {
            var self = this;
            if (self.isTypeChassis(device.resourceType))
                return false;
            if (device.chassisId)
                return true;
            return false;
        };
        ConfigureChassisWizardController.prototype.removeDevice = function (id) {
            var self = this;
            var confirm = self.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('CONFIGURECHASSIS_DeleteConfirm')));
            confirm.then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.commands.data.devices.remove, [id])
                    .then(function (data) {
                    self.refresh();
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        ConfigureChassisWizardController.prototype.validateUniqueChassisSettings = function () {
            var self = this;
            self.forms.uniqueChassisSettingsForm._submitted = true;
            return self.$q(function (resolve, reject) {
                if (_.find(self.forms.uniqueChassisSettingsForm, { $invalid: true })) {
                    angular.forEach(self.forms.uniqueChassisSettingsForm, function (form) { return form._submitted = true; });
                    self.GlobalServices.scrollToInvalidElement("form_chassisconfiguration_uniquechassisconfig");
                    reject();
                }
                else
                    resolve();
            });
        };
        ConfigureChassisWizardController.prototype.validateChassisConfig = function () {
            var self = this, forms = [
                self.forms.cmc,
                self.forms.alerts,
                self.forms.ntpForm,
                self.forms.redundancy,
                self.forms.dns
            ];
            return self.$q(function (resolve, reject) {
                if (_.find(forms, { $invalid: true })) {
                    angular.forEach(forms, function (form) { return form._submitted = true; });
                    self.GlobalServices.scrollToInvalidElement(self.view.editMode() + "deviceConfigOnboarding");
                    reject();
                }
                else
                    resolve();
            });
        };
        //Modals
        ConfigureChassisWizardController.prototype.viewBundles = function (selectedFirmware) {
            var self = this;
            var theModal = self.Modal({
                title: selectedFirmware.name,
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/viewbundles.html',
                controller: 'ViewBundlesController as vb',
                params: {
                    id: selectedFirmware.id
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            theModal.modal.show();
        };
        ConfigureChassisWizardController.prototype.defineUplinks = function (chassisConfig) {
            //passing in chassisConfig instead of uplinks so that on save the reference is changed and not just value
            var self = this;
            var theModal = self.Modal({
                title: self.$translate.instant("DEFINE_UPLINKS_DefineUplinks"),
                onHelp: function () {
                    self.GlobalServices.showHelp('configuechassisDefineuplinkdialog');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/resources/modals/defineuplinksmodal.html',
                controller: 'DefineUplinksController as defineUplinksController',
                params: {
                    uplinks: angular.copy(chassisConfig.uplinks),
                    vltModel: self.vltObj
                },
                onComplete: function (uplinks) {
                    chassisConfig.uplinks = uplinks;
                }
            });
            theModal.modal.show();
        };
        ConfigureChassisWizardController.prototype.ioModuleConfiguration = function () {
            var self = this;
            switch (self.configureResources.configIOMMode) {
                case "all":
                case "independent":
                    return self.validateUplinks();
                case "upload":
                    self.validateIoModuleUpload();
                    return;
            }
        };
        ConfigureChassisWizardController.prototype.initFileWatcher = function () {
            var self = this;
            self.$timeout(function () {
                document.getElementById('configFile').onchange = function (evt) {
                    var element = angular.element(evt.target)[0];
                    self.$timeout(function () { self.ioModuleFileModel = element.files[0]; });
                };
            }, 500);
        };
        ConfigureChassisWizardController.prototype.validateIoModuleUpload = function () {
            var self = this;
            var d = self.$q.defer();
            self.forms.configIoModuleUpload._submitted = true; //triggers form-validation directive to light up the form
            if (self.ioModuleFileModel) {
                return self.uploadPortConfiguration();
            }
            else {
                d.reject();
                return d.promise;
            }
        };
        ConfigureChassisWizardController.prototype.uploadPortConfiguration = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            var fileName = "";
            //using angular uploader
            //set error and success callbacks 
            angular.extend(self.$scope.uploader, {
                onBeforeUploadItem: function (item) {
                    fileName = item.file.name;
                },
                onErrorItem: function (fileItem, response, status, headers) {
                    d.resolve();
                    self.GlobalServices.DisplayError(response.data);
                },
                onCompleteAll: function (fileItem, response, status, headers) {
                    self.configureResources.configIOMXMLSettingsFileName = fileName;
                    d.resolve();
                }
            });
            self.$scope.uploader.uploadAll();
            return d.promise;
        };
        ConfigureChassisWizardController.prototype.portVisible = function (index, slotNum, context, configureAll, multiFabric) {
            var self = this, slot = {
                hasSlot: "hasSlot" + slotNum,
                slotPorts: "slot" + slotNum + "Ports",
                slotQuadPortSupported: "slot" + slotNum + "QuadPortSupported",
                slotFCModule: "slot" + slotNum + "FCModule",
                slotConfig: "slot" + slotNum + "Config"
            };
            if (configureAll) {
                if (multiFabric) {
                    return (context[slot.hasSlot] &&
                        context.iomconfigurable &&
                        context.iompresent &&
                        context[slot.slotPorts] === 2 &&
                        context[slot.slotQuadPortSupported] &&
                        (index === 3 || index === 7) ||
                        (!context[slot.slotQuadPortSupported] && index < context[slot.slotPorts]) ||
                        (context.quadPortMode && context[slot.slotQuadPortSupported])) &&
                        context[slot.slotConfig].portType !== 'Fc';
                }
                else {
                    return context[slot.hasSlot] &&
                        context.iomconfigurable &&
                        context.iompresent &&
                        context[slot.slotPorts] === 2 &&
                        context[slot.slotQuadPortSupported] &&
                        (index === 3 || index === 7) ||
                        (!context[slot.slotQuadPortSupported] && index < context[slot.slotPorts]) ||
                        (context.quadPortMode && context[slot.slotQuadPortSupported]);
                }
            }
            else {
                if (multiFabric) {
                    return (context[slot.hasSlot] &&
                        context.iomconfigurable &&
                        context.iompresent &&
                        (context[slot.slotPorts] === 2 &&
                            context[slot.slotQuadPortSupported] &&
                            (index === 3 || index === 7)) ||
                        (!context[slot.slotQuadPortSupported] && index < context[slot.slotPorts]) ||
                        (context.quadPortMode && context[slot.slotQuadPortSupported])) &&
                        context[slot.slotConfig].portType !== 'Fc';
                }
                else {
                    return (context[slot.hasSlot] &&
                        context.iomconfigurable &&
                        context.iompresent &&
                        (context[slot.slotPorts] === 2 &&
                            context[slot.slotQuadPortSupported] &&
                            (index === 3 || index === 7)) ||
                        (!context[slot.slotQuadPortSupported] && index < context[slot.slotPorts]) ||
                        (context.quadPortMode && context[slot.slotQuadPortSupported]));
                }
            }
        };
        ConfigureChassisWizardController.prototype.validateUplinks = function () {
            var self = this, chassisToCheck = [], chassis = [], slotNum, 
            //standardizes formatting for validating below
            format = function (arrayOfSwitches, configureAll, multiFabric, chassisId) {
                chassis = [];
                //for each fabric
                angular.forEach(_.filter(arrayOfSwitches, function (slot) { return slot.iompresent && slot.iomconfigurable; }), function (_switch) {
                    chassis.push({});
                    var slots = angular.copy([
                        _switch.slot1Config,
                        _switch.slot2Config,
                        _switch.slot3Config
                    ]);
                    angular.forEach(slots, function (slotConfig, slotConfigIndex) {
                        //remove ports that aren't visible
                        _.pullAll(slotConfig, _.filter(slotConfig, function (port, portIndex) {
                            return !self.portVisible(portIndex, slotConfigIndex + 1, _switch, configureAll, multiFabric);
                        }));
                        //set speeds of remaining ports
                        angular.forEach(slotConfig, function (port) {
                            port._speed = self
                                .getPortType(port.portType, chassis.indexOf(port), _switch.quadPortSupported, _switch["slot" + (slotConfigIndex + 1) + "Ports"], _switch.quadPortMode)
                                .id;
                        });
                    });
                    angular.extend(chassis[multiFabric ? arrayOfSwitches.indexOf(_switch) : 0], {
                        ports: _.flatten(_.filter(slots, function (slotConfig) {
                            return slotConfig.length;
                        })),
                        multiFabric: multiFabric,
                        id: angular.isDefined(chassisId) ? chassisId : undefined
                    });
                });
                chassisToCheck.push(chassis);
            };
            if (self.configureResources.configUplinks) {
                if (self.configureResources.configIOMMode === "all") {
                    if (!self.configureResources.configAllIOM) {
                        // Configure All Chassis the Same / Configure All IOMs (Switches) the Same
                        format([self.configureResources.commonIOMConfiguration], true, false);
                    }
                    else {
                        // Configure All Chassis the Same / Configure All IOMs (Switches) Independently
                        format(self.configureResources.iomConfiguration, false, true);
                    }
                }
                if (self.configureResources.configIOMMode === "independent") {
                    var filteredDevices = self.$filter("filteredDevices")(self.configureResources.devices);
                    angular.forEach(filteredDevices, function (chassis) {
                        // Configure All Chassis Independently / Configure All IOMs (Switches) the Same
                        if (!chassis.chassisConfiguration.configAllIOM) {
                            format([chassis.chassisConfiguration.commonIOMConfiguration], false, false, chassis.id);
                        }
                        else {
                            // Configure All Chassis Independently / Configure All IOMs (Switches) Independently
                            format(chassis.chassisConfiguration.iomConfiguration, true, true, chassis.id);
                        }
                    });
                }
                var mismatchPortSpeed = undefined, vltFabricSpeed;
                //look for invalid chassis
                /*chassisToCheck looks like this :

                [
                    *chassis*,
                    [
                        *switches*,
                        [
                            *switch*,
                            {
                                multiFabric: boolean
                                ports: []
                            }
                        ]
                    ]
                ]
                */
                self.steps.ioModuleSettings.invalidChassis = _.find(chassisToCheck, function (chassis) {
                    //filter for valid fabrics
                    return mismatchPortSpeed ||
                        _.find(_.filter(chassis, function (_switch) {
                            return chassis.indexOf(_switch) % 2 === 0;
                        }), function (_switch) {
                            vltFabricSpeed = 0;
                            var fabricSwitches = [_switch];
                            if (_switch.multiFabric) {
                                fabricSwitches.push(chassis[chassis.indexOf(_switch) + 1]);
                            }
                            var portsWithUplinks = function (switchPorts) { return _.filter(switchPorts, function (port) {
                                return port.uplinkId;
                            }); }, hasUplinkNoVLT = function (switchPorts) { return _.filter(switchPorts, function (port) {
                                return port.uplinkId && port.uplinkId !== self.vltObj.uplinkId;
                            }); }, noUplinksFound = _.find(fabricSwitches, function (_switch) {
                                return _switch.ports.length > 0 && !_.find(_switch.ports, function (port) {
                                    return port.uplinkId && port.uplinkId !== self.vltObj.uplinkId;
                                });
                            });
                            //filter for invalid slots
                            var slotConflicts = _.find(fabricSwitches, function (_switch) {
                                return _.find(portsWithUplinks(_switch.ports), function (port) {
                                    //An uplink cannot have two different speeds on a single switch within a fabric
                                    var intraSwitchSpeedConflict = _.find(hasUplinkNoVLT(_switch.ports), function (portToSearch) {
                                        if (port.uplinkId === portToSearch.uplinkId && port._speed !== portToSearch._speed) {
                                            console.log("uplink issue");
                                            return mismatchPortSpeed = true;
                                        }
                                    });
                                    //VLT cannot be different speeds across switches within a fabric
                                    var crossSlotVLTPortSpeedConflict = function () {
                                        if (port.uplinkId === self.vltObj.uplinkId) {
                                            if (vltFabricSpeed === 0) {
                                                vltFabricSpeed = port._speed;
                                            }
                                            else if (vltFabricSpeed !== port._speed) {
                                                console.log("vlt issue");
                                                return mismatchPortSpeed = true;
                                            }
                                        }
                                    };
                                    return intraSwitchSpeedConflict || crossSlotVLTPortSpeedConflict();
                                });
                            });
                            return noUplinksFound || slotConflicts;
                        });
                });
                self.GlobalServices.ClearErrors(self.errors);
                if (self.steps.ioModuleSettings.invalidChassis) {
                    self.GlobalServices.DisplayError({
                        severity: "critical",
                        message: mismatchPortSpeed
                            ? self.$translate.instant("DEFINE_UPLINKS_mismatchPortSpeed")
                            : self.$translate.instant("DEFINE_UPLINKS_ErrorMessage")
                    }, self.errors);
                    $.each(self.filteredconfigureResources, function (index, resource) {
                        var element = $("#collapseIo" + index), elementTitle = $("#headIo" + index);
                        if (resource.id === self.steps.ioModuleSettings.invalidChassis[0].id) {
                            element.collapse("show");
                        }
                        else {
                            element.collapse("hide");
                            elementTitle.removeClass("text-danger");
                        }
                    });
                    self.$anchorScroll("page_configureChassisWizard");
                }
                else {
                    $.each(self.filteredconfigureResources, function (index, resource) {
                        $("#headIo" + index).removeClass("text-danger");
                    });
                }
            }
            //create promise to return to wizard step
            var d = self.$q.defer();
            if (self.steps.ioModuleSettings.invalidChassis && self.configureResources.configUplinks) {
                d.reject();
            }
            else {
                d.resolve();
            }
            return d.promise;
        };
        ConfigureChassisWizardController.prototype.getPortType = function (portType, index, quadPortSupported, slot, quadPortMode) {
            var self = this, fourty = { id: 40, name: self.$translate.instant("CONFIGURECHASSIS_DEVICE_CONFIG_40Gb") }, ten = { id: 10, name: self.$translate.instant("CONFIGURECHASSIS_DEVICE_CONFIG_10Gb") };
            if (slot === 2 &&
                quadPortSupported &&
                (index === 3 || index === 7)) {
                return quadPortMode === false ? fourty : ten;
            }
            else {
                return portType === "Fo" ? fourty : ten;
            }
        };
        ConfigureChassisWizardController.prototype.viewPackageBundleDetails = function (selectedItem, packageId) {
            var self = this;
            var theModal = self.Modal({
                title: selectedItem.bundleName,
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/viewbundledetails.html',
                controller: 'ViewBundleDetailsController as vbd',
                params: {
                    firmwarePackageId: packageId,
                    firmwareBundleId: selectedItem.id
                },
                onComplete: function (modalScope) {
                    self.refresh();
                }
            });
            theModal.modal.show();
        };
        ConfigureChassisWizardController.prototype.addRepo = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_Repositories_ResyncOSRepo'),
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/repositories/editrepomodal.html',
                controller: 'EditRepoModalController as editRepoModalController',
                params: {
                    repo: {},
                    type: 'add'
                },
                onComplete: function () {
                    self.refresh();
                },
                onCancel: function () {
                    self.refresh();
                    modal.modal.dismiss();
                }
            });
            modal.modal.show();
        };
        ConfigureChassisWizardController.prototype.clearErrors = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
        };
        ConfigureChassisWizardController.prototype.deleteRepo = function (repo) {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_Repositories_Confirm'),
                modalSize: 'modal-md',
                templateUrl: 'views/settings/repositories/confirmdeleterepo.html',
                controller: 'GenericModalController as c',
                params: {},
                onComplete: function () {
                    var d = self.$q.defer();
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.$http.post(self.commands.data.repository.deleteRepository, { id: repo.id })
                        .then(function () { self.refresh(); })
                        .catch(function (response) { return self.GlobalServices.DisplayError(response.data, self.errors); })
                        .finally(function () { return d.resolve(); });
                },
                onCancel: function () {
                    self.refresh();
                    modal.modal.dismiss();
                }
            });
            modal.modal.show();
        };
        //APIs
        ConfigureChassisWizardController.prototype.getHardwareManagementNetworks = function () {
            var self = this;
            return self.$http.post(self.commands.data.networking.networks.getHardwareManagementNetworks, []);
        };
        ConfigureChassisWizardController.prototype.getFirmwarePackages = function (array) {
            var self = this;
            //dunno where this array is coming from
            return self.$http.post(self.commands.data.firmwarepackages.getFirmwarePackages, null);
        };
        ConfigureChassisWizardController.prototype.getCredentialList = function (array) {
            var self = this;
            //dunno where this array is coming from
            return self.$http.post(self.commands.data.credential.getCredentialList, array);
        };
        ConfigureChassisWizardController.prototype.getConfigurableResources = function (requireComplianceCheck, criteriaObj) {
            var self = this;
            return self.$http.post(self.commands.data.configureChassis.getConfigurableResources, {
                requireComplianceCheck: requireComplianceCheck,
                criteriaObj: criteriaObj
            });
        };
        ConfigureChassisWizardController.prototype.setDefaultFirmwarePackage = function (id) {
            var self = this;
            return self.$http.post(self.commands.data.firmwarepackages.setDefaultFirmwarePackage, id);
        };
        ConfigureChassisWizardController.prototype.getTimeZones = function () {
            var self = this;
            return self.$http.post(self.commands.data.environment.getTimeZones, []);
        };
        ConfigureChassisWizardController.prototype.configureChassis = function (chassis) {
            var self = this;
            return self.$http.post(self.commands.data.configureChassis.configureResources, chassis);
        };
        ConfigureChassisWizardController.prototype.saveBundle = function (form) {
            var self = this;
            self.config = {
                directPost: true,
                headers: { 'Content-Type': undefined },
                transformRequest: angular.identity
            };
            return self.$http.post(self.commands.data.firmwarepackages.saveFirmwareBundle, form, self.config);
        };
        ConfigureChassisWizardController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ConfigureChassisWizardController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ConfigureChassisWizardController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal',
            'Loading', 'Dialog', 'Commands', 'GlobalServices', 'constants', '$filter', '$anchorScroll', 'filteredDevicesFilter', '$rootScope', "FileUploader"];
        return ConfigureChassisWizardController;
    }());
    asm.ConfigureChassisWizardController = ConfigureChassisWizardController;
    angular
        .module("app")
        .controller("ConfigureChassisWizardController", ConfigureChassisWizardController);
})(asm || (asm = {}));
//# sourceMappingURL=configureChassisWizard.js.map
