var asm;
(function (asm) {
    "use strict";
    var DeviceConfigurationController = (function () {
        function DeviceConfigurationController($http, commands, $q, $translate, loading, GlobalServices, Modal, constants, $filter) {
            this.$http = $http;
            this.commands = commands;
            this.$q = $q;
            this.$translate = $translate;
            this.loading = loading;
            this.GlobalServices = GlobalServices;
            this.Modal = Modal;
            this.constants = constants;
            this.$filter = $filter;
            this.configModel = {
                "id": null,
                "name": null,
                "dataCenter": null,
                "aisle": null,
                "rack": null,
                "rackSlot": null,
                "registerChassisManagementControllerOnDns": false,
                "dnsDomainName": null,
                "enableTelnet": false,
                "enableSsh": false,
                "trapSettings": [],
                "smtpServer": null,
                "smtpAuthentication": false,
                "smtpUsername": null,
                "smtpPassword": null,
                "sourceEmailName": null,
                "destinationEmails": [],
                "users": [],
                "systemInputPowercap": 0.0,
                "powercapMeasurementType": null,
                "redundancyPolicy": null,
                "serverPerformanceOverPowerRedundancy": false,
                "enableDynamicPowerSupplyEngagement": false,
                "chassisaddressingmode": 'existing',
                "chassisnetworkid": null,
                "serveraddressingmode": 'existing',
                "servernetworkid": null,
                "iomaddressingmode": 'existing',
                "iomnetworkid": null,
                "chassisCredentialId": null,
                "iomCredentialId": null,
                "bladeCredentialId": null,
                "idracuserlist": [],
                "syslogDestination": null,
                "timeZone": null,
                "enableNTPServer": false,
                "preferredNTPServer": null,
                "secondaryNTPServer": null,
                "registeriDracOnDns": false,
                "enableipmi": false
            };
            this.view = {};
            var self = this;
            self.config = self.config.configurationmode === "onboarding" ? angular.extend(self.config, self.configModel) : self.config;
            //          var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            //            self.loading(d.promise);
            self.getTimeZones()
                .then(function (response) {
                self.timeZones = response.data.responseObj;
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () {
                //  d.resolve();
            });
            self.view = {
                onboarding: {
                    users: {
                        visible: function () {
                            return self.editMode ||
                                self.config.users.length ||
                                self.config.idracuserlist.length;
                        }
                    },
                    monitoring: {
                        visible: function () {
                            return self.editMode ||
                                self.config.trapSettings.length ||
                                self.config.destinationEmails.length ||
                                self.config.smtpServer ||
                                self.config.syslogDestination;
                        }
                    },
                    ntp: {
                        visible: function () {
                            return self.editMode ||
                                self.config.timeZone;
                        }
                    },
                    power: {
                        visible: function () {
                            return self.editMode ||
                                self.config.redundancyPolicy ||
                                (!self.hasFxChassis() &&
                                    (self.config.serverPerformanceOverPowerRedundancy ||
                                        self.config.enableDynamicPowerSupplyEngagement));
                        }
                    },
                    networking: {
                        visible: function () {
                            return self.editMode ||
                                self.config.registerChassisManagementControllerOnDns ||
                                self.config.registeriDracOnDns ||
                                self.config.enableipmi;
                        }
                    }
                },
                onboardingVisible: function () {
                    return self.config.configurationmode !== "onboarding" &&
                        self.numConfigurableDevices() !== 0 &&
                        (self.editMode || !!_.find(self.view.onboarding, function (obj) { return obj.visible(); }));
                },
                //below makes collapsable row ids between edit mode and non-edit mode unique
                //note:  this does limit the usage of the directive to these 2 times on any given document/page, so if we ever use more than 2, we'll need to update this
                //if truly unique ids are needed, we need to use GlobalServices.NewGuid()
                editMode: function () {
                    return self.editMode ? "edit_" : "view_";
                }
            };
        }
        DeviceConfigurationController.prototype.createUser = function (userType, editType, user) {
            var self = this, title, helptoken, array = userType === "idrac" ? self.config.idracuserlist : self.config.users;
            if (editType === "create") {
                if (userType === "cmc") {
                    title = "CREATE_EDIT_DEVICE_USERS_CreateCMCUser";
                    helptoken = 'ConfigureChassisAddCMCUser';
                }
                else if (userType === "idrac") {
                    title = "CREATE_EDIT_DEVICE_USERS_CreateiDRACUser";
                    helptoken = 'ConfigureChassisiDRACUser';
                }
            }
            else if (editType === "edit") {
                if (userType === "cmc") {
                    title = "CREATE_EDIT_DEVICE_USERS_EditCMCUser";
                    helptoken = 'ConfigureChassisAddCMCUser';
                }
                else if (userType === "idrac") {
                    title = "CREATE_EDIT_DEVICE_USERS_EditiDRACUser";
                    helptoken = 'ConfigureChassisiDRACUser';
                }
            }
            var theModal = self.Modal({
                title: self.$translate.instant(title),
                onHelp: function () {
                    self.GlobalServices.showHelp(helptoken);
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/deviceusermodal.html',
                controller: 'EditDeviceModalController as editDeviceModalController',
                params: {
                    type: userType,
                    mode: editType,
                    user: angular.copy(user)
                },
                onComplete: function (createdUser) {
                    if (editType === "create") {
                        array.push(createdUser);
                    }
                    else if (editType === "edit") {
                        angular.extend(user, createdUser);
                    }
                }
            });
            theModal.modal.show();
        };
        //        selectAllUsers(array: Array<any>) {
        //            var self: DeviceConfigurationController = this,
        //                allSelected = self.numUsersChecked(array) === array.length;
        //            angular.forEach(array, (user: any) => {
        //                user.rowChecked = !allSelected;
        //            });
        //        }
        //        numUsersChecked(array: Array<any>) {
        //            return _.filter(array, { rowChecked: true }).length;
        //        }
        DeviceConfigurationController.prototype.deleteUser = function (array, user) {
            _.pull(array, user);
        };
        DeviceConfigurationController.prototype.numConfigurableDevices = function () {
            var self = this;
            return _.filter(self.devices, function (device) {
                return self.$filter("isTypeChassis")(device) && device.chassisConfiguration.configChassis === true;
            }).length;
        };
        DeviceConfigurationController.prototype.hasFxChassis = function () {
            var self = this;
            return !!_.find(_.filter(self.devices, function (device) {
                return self.$filter("isTypeChassis")(device);
            }), { resourceType: 'ChassisFX' });
        };
        DeviceConfigurationController.prototype.doManageCredentials = function (updateType, credentialName) {
            var self = this, theTitle, theId, theName, canChangeCredentialType, editMode = false;
            ;
            if (updateType.toUpperCase() === "CREATE") {
                theTitle = self.$translate.instant('CREDENTIALS_CreateTitle');
                theId = "";
                canChangeCredentialType = true;
            }
            else {
                theTitle = self.$translate.instant('CREDENTIALS_EditTitle');
                switch (_.upperFirst(_.toLower(credentialName))) {
                    case 'Chassis':
                        theId = self.config.chassisCredentialId;
                        break;
                    case 'Server':
                        theId = self.config.bladeCredentialId;
                        break;
                    case 'Iom':
                        theId = self.config.iomCredentialId;
                        break;
                }
                canChangeCredentialType = false;
                editMode = true;
            }
            var theModal = self.Modal({
                title: theTitle,
                modalSize: 'modal-lg',
                templateUrl: 'views/credentials/editcredentials.html',
                controller: 'EditCredentialsController as editCredentialsController',
                params: {
                    source: 'device-config',
                    credential: editMode ? { id: theId } : undefined,
                    editMode: editMode,
                    canChangeCredentialType: canChangeCredentialType
                },
                onComplete: function (credentialId) {
                    self.GlobalServices.ClearErrors(self.errors);
                    self.loading(self.$q(function (resolve) {
                        self.getCredentials(credentialName)
                            .then(function (response) {
                            self.credentials[credentialName] = response.data.responseObj;
                        })
                            .catch(function (error) { self.GlobalServices.DisplayError(error, self.errors); })
                            .finally(function () { return resolve(); });
                    }));
                }
            });
            theModal.modal.show();
        };
        DeviceConfigurationController.prototype.doIPAddressing = function (updateType) {
            var self = this;
            var editNetworkModal = self.Modal({
                title: self.$translate.instant(updateType.toUpperCase() === "CREATE" ? "NETWORKS_Edit_CreateTitle" : "NETWORKS_Edit_EditTitle"),
                modalSize: 'modal-lg',
                templateUrl: 'views/networking/networks/editnetwork.html',
                controller: 'EditNetworkModalController as editNetwork',
                params: {
                    editMode: updateType
                },
                onComplete: function () {
                    //self.refresh();
                }
            });
            editNetworkModal.modal.show();
        };
        DeviceConfigurationController.prototype.getCredentials = function (type) {
            var self = this;
            return self.$http.post(self.commands.data.credential.getCredentialByType, { id: type });
        };
        DeviceConfigurationController.prototype.getTimeZones = function () {
            var self = this;
            return self.$http.post(self.commands.data.environment.getTimeZones, {});
        };
        DeviceConfigurationController.prototype.getNetworks = function () {
            var self = this;
            return self.$http.post(self.commands.data.networking.networks.getHardwareManagementNetworks, null);
        };
        DeviceConfigurationController.$inject = ["$http", "Commands", "$q", "$translate", "Loading", "GlobalServices", "Modal", "constants", "$filter"];
        return DeviceConfigurationController;
    }());
    angular.module("app")
        .component("deviceConfig", {
        templateUrl: "views/resources/deviceconfiguration.html",
        controller: DeviceConfigurationController,
        controllerAs: "deviceConfig",
        bindings: {
            config: "=",
            devices: "=?",
            editMode: "<?",
            networks: "<?",
            credentials: "<?",
            forms: "=?",
            errors: "=",
            redundancyPolicies: "=?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=deviceConfig.js.map
