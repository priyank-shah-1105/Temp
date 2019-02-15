var asm;
(function (asm) {
    "use strict";
    var VirtualApplianceManagementController = (function () {
        function VirtualApplianceManagementController(Dialog, $http, $window, $timeout, $scope, $q, $translate, Modal, Loading, Commands, $filter, GlobalServices, $rootScope, $location) {
            this.Dialog = Dialog;
            this.$http = $http;
            this.$window = $window;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Commands = Commands;
            this.$filter = $filter;
            this.GlobalServices = GlobalServices;
            this.$rootScope = $rootScope;
            this.$location = $location;
            this.allSettings = { monitoringSettings: {} };
            this.configServerEabled = true;
            this.refreshTimer = null;
            this.statusTimer = null;
            this.vxRackStateOptions = [];
            this.vxRackConfiguring = false;
            this.possibleVxRackStates = [];
            this.connectionTypeOptions = [];
            var self = this;
            self.initialize();
        }
        VirtualApplianceManagementController.prototype.$onDestroy = function () {
            var self = this;
            if (self.refreshTimer)
                self.$timeout.cancel(self.refreshTimer);
            if (self.statusTimer)
                self.$timeout.cancel(self.statusTimer);
        };
        VirtualApplianceManagementController.prototype.canDeactivate = function () {
            var allow = true;
            //Any logic to prevent navigating away (change allow to false to prevent)
            return allow;
        };
        VirtualApplianceManagementController.prototype.activate = function () {
            var self = this;
            self.refreshTimer = self.$timeout(function () {
                self.refresh();
            }, 10000);
        };
        VirtualApplianceManagementController.prototype.refresh = function (action) {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors();
            //if there's an action, don't use the spinner because too many loaders will cause a meltdown 
            action || self.Loading(d.promise);
            self.$q.all([
                self.getNtpTimeZoneSettings()
                    .then(function (response) {
                    self.allSettings.ntpTimeZoneSettings = response.data.responseObj;
                }),
                self.getTimeZones()
                    .then(function (response) {
                    self.allSettings.timeZones = response.data.responseObj;
                }),
                self.getApplianceUpgrade()
                    .then(function (response) {
                    self.allSettings.applianceUpdateInfo = response.data.responseObj;
                }),
                self.getHttpProxySettings()
                    .then(function (response) {
                    self.allSettings.httpProxySettings = response.data.responseObj;
                }),
                self.getVxRackSettings()
                    .then(function (response) {
                    //response.data.responseObj = { "serialNumber": "None", "model": "VxRack FLEX", "srsAddress": "None", "state": "None", "status": "None", "suspendedUntil": "", "omePassword": "", "alertFilter": "Critical", "alertPollingIntervalHours": 1, "alertPollingIntervalMinutes": 0, "elmsSoftwareId": "", "solutionSerialNumber": "", "srsGatewayHostIp": "", "srsPassword": null, "deviceType": "VxRack FLEX", "srsGatewayHostPort": 9443, "hostIp": "", "username": "", "userId": "" };
                    self.allSettings.vxRackSettings = response.data.responseObj;
                }),
                self.getDhcpSettings()
                    .then(function (response) {
                    self.allSettings.dhcpSettings = response.data.responseObj;
                }),
                self.getApplianceCertInfo()
                    .then(function (response) {
                    self.allSettings.applianceCertInfo = response.data.responseObj;
                }),
                self.getLicenseData()
                    .then(function (response) {
                    self.allSettings.licenseData = response.data.responseObj;
                }),
                self.getServiceTag()
                    .then(function (response) {
                    self.allSettings.aboutData = response.data.responseObj;
                }),
                self.getIpVerification()
                    .then(function (response) {
                    self.allSettings.ipVerification = response.data.responseObj;
                })
            ])
                .then(function () { return action && action(); })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); })
                .finally(function () { return d.resolve(); });
        };
        VirtualApplianceManagementController.prototype.initialize = function () {
            var self = this;
            self.$scope.$on("settingsRefresh", function () { self.refresh(); });
            //id as string
            self.vxRackStateOptions = [
                { id: "Enabled", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Enabled"), readName: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Enabled") },
                { id: "3", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Suspend3hours"), readName: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Suspendedxhours", { number: 3 }) },
                { id: "6", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Suspend6hours"), readName: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Suspendedxhours", { number: 6 }) },
                { id: "12", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Suspend12hours"), readName: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Suspendedxhours", { number: 12 }) },
                { id: "24", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Suspend24hours"), readName: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Suspendedxhours", { number: 24 }) }
            ];
            //these are states that the user cannot set it to be, but can come from the backend 
            self.possibleVxRackStates = [
                { id: "None", readName: self.$translate.instant("SETTINGS_VirtualApplianceManagement_NotAvailable") },
                { id: "Error", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Error"), readName: self.$translate.instant("SETTINGS_VirtualApplianceManagement_Error") },
            ].concat(self.vxRackStateOptions);
            self.connectionTypeOptions = [
                { id: "srs", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_ConnectionType_SRS") },
                { id: "phonehome", name: self.$translate.instant("SETTINGS_VirtualApplianceManagement_ConnectionType_PhoneHome") }
            ];
            //get the status of the configure server job
            self.getConfigServerStatus();
            self.refresh(self.dealWithRouteParams());
        };
        VirtualApplianceManagementController.prototype.dealWithRouteParams = function () {
            var self = this;
            //returns modal associated with the route parameter
            if (self.modalOrTab) {
                if (self.modalOrTab === "editRepoPath") {
                    return function () { self.editRepoPath(); };
                }
            }
        };
        VirtualApplianceManagementController.prototype.ntpModal = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETUPWIZARD_NTPSettingsTitle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('EditingDefaultNTPSettings');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/ntpeditmodal.html',
                controller: 'NtpEditModalController as ntpEditModalController',
                params: {
                    viewModel: angular.copy(self.allSettings)
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        /*
          editRepoPath() {
             var self: VirtualApplianceManagementController = this;
             var modal = self.Modal({
                 title: self.$translate.instant('SETTINGS_UpdateRepositoryPath'),
                 onHelp() {
                     self.GlobalServices.showHelp('UpdateRepositoryPath');
                 },
                 modalSize: 'modal-lg',
                 templateUrl: 'views/settings/virtualappliancemanagement/editrepopathmodal.html',
                 controller: 'EditRepoPathModalController as editRepoPathModalController',
                 params: {
                     applianceUpdateInfo: angular.copy(self.allSettings.applianceUpdateInfo)
                 },
                 onComplete() {
                     self.refresh();
                 }
             });
             modal.modal.show();
         }
         */
        VirtualApplianceManagementController.prototype.editRepoPath = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_EditApplianceUpgradeSettings_ModalTitle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('UpdateRepositoryPath');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/applianceupgradesettingsmodal.html',
                controller: 'ApplianceUpgradeSettingsModalController as applianceUpgradeSettingsModalController',
                params: {
                    applianceUpdateInfo: angular.copy(self.allSettings.applianceUpdateInfo)
                },
                onComplete: function () {
                    self.refresh();
                },
                onCancel: function () {
                    modal.modal.dismiss();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.editProxySettings = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETUPWIZARD_ProxySettingsTitle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('EditingProxySettings');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/editproxysettingsmodal.html',
                controller: 'EditProxySettingsModalController as editProxySettingsModalController',
                params: {
                    httpProxySettings: angular.copy(self.allSettings.httpProxySettings)
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.editDhcp = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_DHCPSettings'),
                onHelp: function () {
                    self.GlobalServices.showHelp('EditingDHCPSettings');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/editdhcpsettingsmodal.html',
                controller: 'EditDhcpSettingsModalController as editDhcpSettingsModalController',
                params: {
                    settings: angular.copy(self.allSettings.dhcpSettings)
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.editVxRack = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_VirtualApplianceManagement_VxRackFLEXAlertConnectorModalTitle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('EditingVxRackFLEXAlertConnectorSettings');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/vxrackflexalertconnectormodal.html',
                controller: 'VxRackFlexAlertConnectorModalController as vxRackFlexAlertConnectorModalController',
                params: {
                    settings: angular.copy(self.allSettings.vxRackSettings)
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.generateCert = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_GenerateSigningRequest'),
                onHelp: function () {
                    self.GlobalServices.showHelp('appliancemanagementgeneratecert');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/generatecertificatemodal.html',
                controller: 'GenerateCertModalController as generateCertModalController',
                params: {
                    settings: angular.copy(self.allSettings.applianceCertInfo)
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.uploadCert = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_UploadSSLCertificate'),
                onHelp: function () {
                    self.GlobalServices.showHelp('UploadinganSSLCertificate');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/uploadcertificatemodal.html',
                controller: 'UploadCertificateModalController  as uploadCertificateModalController',
                params: {
                    settings: angular.copy(self.allSettings.applianceCertInfo)
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.addLicense = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_AddLicense'),
                onHelp: function () {
                    self.GlobalServices.showHelp('LicenseManagement');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/addlicensemodal.html',
                controller: 'AddLicenseModalController as addLicenseModalController',
                params: {
                    license: angular.copy(self.allSettings.licenseData)
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.downloadCert = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_CertSignRequest'),
                onHelp: function () {
                    self.GlobalServices.showHelp('DownloadinganSSLCertificate');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/downloadcertmodal.html',
                controller: 'DownloadCertModalController as downloadCertModalController',
                params: {
                    certInfo: angular.copy(self.allSettings.applianceCertInfo)
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.updateVirtualAppliance = function () {
            var self = this;
            if (self.allSettings.applianceUpdateInfo.currentVersion ===
                self.allSettings.applianceUpdateInfo.availableVersion) {
                //versions are the same, ask if user still wants to perform update
                self.Dialog((self.$translate.instant('SETTINGS_VirtualApplianceManagement_Update_Not_Required')), (self.$translate.instant('SETTINGS_VirtualApplianceManagement_Update_Warning') +
                    '<br/><br/>' +
                    self.$translate.instant('SETTINGS_VirtualApplianceManagement_Update_Current_Version') +
                    self.allSettings.applianceUpdateInfo.currentVersion +
                    '<br/><br/>' +
                    self.$translate.instant('SETTINGS_VirtualApplianceManagement_Update_Confirm')))
                    .then(function () {
                    self.doUpdate();
                });
            }
            else {
                var d = self.$q.defer(), bullets = {};
                self.GlobalServices.ClearErrors();
                self.Loading(d.promise);
                self.$q.all([
                    self.getJobList()
                        .then(function (data) {
                        angular.merge(bullets, {
                            numProgressJobs: _.filter(data.data.responseObj, { status: "running" }).length,
                            numScheduledJobs: _.filter(data.data.responseObj, { status: "scheduled" }).length
                        });
                    }),
                    self.getCurrentUsersAndJobs()
                        .then(function (data) {
                        angular.merge(bullets, {
                            numUsers: data.data.responseObj.currentusers
                        });
                    })
                ])
                    .then(function () {
                    //passing bullets as a parameter to $translate to interpolate values into translation
                    self.Dialog(self.$translate.instant("GENERIC_Warning"), self.$translate.instant("UPDATE_VIRTUAL_APPLIANCE_Description", bullets), false)
                        .then(function () {
                        self.doUpdate();
                    });
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data);
                })
                    .finally(function () { return d.resolve(); });
            }
        };
        VirtualApplianceManagementController.prototype.doUpdate = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.Loading(d.promise);
            //Suspend the polling since the backend is abandoning the session and restarting the appliance
            //in the following call which is causing 403 error when the polling request continues
            window.clearInterval(self.$rootScope.ASM.templateInterval);
            window.clearInterval(self.$rootScope.ASM.jobsInterval);
            window.clearInterval(self.$rootScope.ASM.servicesDashboardInterval);
            self.$http.post(self.Commands.data.applianceManagement.submitUpdateVirtualAppliance, self.allSettings.applianceUpdateInfo)
                .then(function (data) {
                self.$timeout(function () { self.$window.location.href = 'status.html#/status'; }, 500);
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data);
            })
                .finally(function () { return d.resolve(); });
        };
        VirtualApplianceManagementController.prototype.editServiceTag = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_EditServiceTag'),
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/editservicetag.html',
                controller: 'EditServiceTagController as editServiceTagController',
                params: {
                    serviceTag: self.allSettings.aboutData.serviceTag
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.editIpVerification = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_EditIPVerificationPortNumbers'),
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/editipverification.html',
                controller: 'EditIpVerificationController as editIpVerificationController',
                params: {
                    ipVerificationPorts: self.allSettings.ipVerification.ipVerificationPorts
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.generateTroubleBundle = function () {
            var self = this;
            var d = self.$q.defer();
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.applianceManagement.exportTroubleshootingBundle, null)
                .then(function (response) {
                //self.$timeout(() => { window.location = response.data.responseObj; }, 500);
                self.$window.location.assign("" + response.data.responseObj);
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data);
            })
                .finally(function () { return d.resolve(); });
        };
        VirtualApplianceManagementController.prototype.generateTroubleBundle_modal = function () {
            console.log('In virtual Applicanec Managemnet .js file');
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
                    callingPage: 'VirtualApplianceManagement'
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            theModal.modal.show();
        };

        VirtualApplianceManagementController.prototype.generateSNMPModal = function () {
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
                    callingPage: 'VirtualApplianceManagement'
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            theModal.modal.show();
        };

        VirtualApplianceManagementController.prototype.saveVxRackSettings = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.applianceManagement.setVxRackSettingsSuspend, self.allSettings.vxRackSettings)
                .then(function (data) {
                self.refresh();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data);
            })
                .finally(function () { return d.resolve(); });
        };
        VirtualApplianceManagementController.prototype.configureServersForAlertConnector = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_VirtualApplianceManagement_AlertConfigJobModalWarning'),
                modalSize: 'modal-md',
                titleIcon: 'text-warning ci-health-warning-tri-bang',
                templateUrl: 'views/settings/virtualappliancemanagement/configureserversforalertconnector.html',
                controller: 'ConfigureServersForAlertConnectorController as configureServersForAlertConnectorController',
                params: {},
                onComplete: function () {
                    self.vxRackConfiguring = true;
                    self.configServerEabled = false;
                    self.getConfigureServersForAlertConnector()
                        .then(function (response) {
                        //get the status of the configure server job
                        self.getConfigServerStatus();
                        self.refresh();
                    })
                        .catch(function (response) {
                        self.GlobalServices.DisplayError(response.data);
                        //get the status of the configure server job
                        self.getConfigServerStatus();
                    })
                        .finally(function () {
                        self.vxRackConfiguring = false;
                    });
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.getConfigServerStatus = function () {
            var self = this;
            self.configServerEabled = false;
            self.getConfigureServerStatus()
                .then(function (response) {
                if (response.data.responseObj == 'completed') {
                    self.configServerEabled = true;
                }
                else {
                    self.statusTimer = self.$timeout(function () {
                        self.getConfigServerStatus();
                    }, 60000);
                }
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); });
        };
        VirtualApplianceManagementController.prototype.vxRackSuspended = function () {
            var self = this;
            return self.allSettings.vxRackSettings &&
                self.allSettings.vxRackSettings.state !== "None" &&
                self.allSettings.vxRackSettings.state !== "Enabled" &&
                self.allSettings.vxRackSettings.state !== "Error";
        };
        VirtualApplianceManagementController.prototype.deregisterVxRack = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_VirtualApplianceManagement_DeregisterWarningTitle'),
                modalSize: 'modal-md',
                titleIcon: 'text-danger ci-action-circle-remove',
                templateUrl: 'views/settings/virtualappliancemanagement/deregisterwarning.html',
                controller: 'DeregisterWarningController as deregisterWarningController',
                params: {
                    connectionType: self.allSettings.vxRackSettings.connectionType
                },
                onComplete: function () {
                    self.refresh();
                }
            });
            modal.modal.show();
        };
        VirtualApplianceManagementController.prototype.vxRackNotAvailable = function () {
            var self = this;
            return self.allSettings.vxRackSettings && self.allSettings.vxRackSettings.state === "None";
        };
        VirtualApplianceManagementController.prototype.getNtpTimeZoneSettings = function () {
            var self = this;
            return self.$http.post(self.Commands.data.environment.getNtpTimeZoneSettings, {});
        };
        VirtualApplianceManagementController.prototype.getTimeZones = function () {
            var self = this;
            return self.$http.post(self.Commands.data.environment.getTimeZones, {});
        };
        VirtualApplianceManagementController.prototype.getApplianceUpdateInfo = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getApplianceUpdateInfo, {});
        };
        VirtualApplianceManagementController.prototype.getApplianceUpgrade = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getApplianceUpgrade, {});
        };
        VirtualApplianceManagementController.prototype.getHttpProxySettings = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getHttpProxySettings, {});
        };
        VirtualApplianceManagementController.prototype.getDhcpSettings = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getDhcpSettings, {});
        };
        VirtualApplianceManagementController.prototype.getVxRackSettings = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getVxRackSettings, {});
        };
        VirtualApplianceManagementController.prototype.getApplianceCertInfo = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getCertificateInfo, {});
        };
        VirtualApplianceManagementController.prototype.getLicenseData = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getLicenseData, {});
        };
        VirtualApplianceManagementController.prototype.getServiceTag = function () {
            var self = this;
            return self.$http.post(self.Commands.data.about.getAboutData, '');
        };
        VirtualApplianceManagementController.prototype.getJobList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.jobs.getJobList, null);
        };
        VirtualApplianceManagementController.prototype.getCurrentUsersAndJobs = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getCurrentUsersAndJobs, null);
        };
        VirtualApplianceManagementController.prototype.getIpVerification = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getIpVerifyPorts, null);
        };
        VirtualApplianceManagementController.prototype.getConfigureServersForAlertConnector = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.configureServersForAlertConnector, null);
        };
        VirtualApplianceManagementController.prototype.getConfigureServerStatus = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getConfigureServerStatus, null);
        };
        VirtualApplianceManagementController.$inject = ['Dialog', '$http', '$window', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Commands', '$filter', 'GlobalServices', '$rootScope', '$location'];
        return VirtualApplianceManagementController;
    }());
    angular.module('app')
        .component('virtualApplianceManagement', {
        templateUrl: "views/appliancemanagement.html",
        controller: VirtualApplianceManagementController,
        controllerAs: 'virtualApplianceManagementController',
        bindings: {
            modalOrTab: "<"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=virtualappliancemanagement.js.map
