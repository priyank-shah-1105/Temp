var asm;
(function (asm) {
    var SetupWizardController = (function () {
        function SetupWizardController(Modal, $scope, Dialog, $http, Loading, $q, $timeout, $rootScope, Commands, $translate, GlobalServices, FileUploader) {
            this.Modal = Modal;
            this.$scope = $scope;
            this.Dialog = Dialog;
            this.$http = $http;
            this.Loading = Loading;
            this.$q = $q;
            this.$timeout = $timeout;
            this.$rootScope = $rootScope;
            this.Commands = Commands;
            this.$translate = $translate;
            this.GlobalServices = GlobalServices;
            this.FileUploader = FileUploader;
            this.setupData = {};
            this.aboutData = {};
            this.timeZones = new Array();
            this.ranges = {
                days: 31,
                hours: 24,
                minutes: 60,
                seconds: 60
            };
            this.testProxyRunning = false;
            this.errors = new Array();
            var self = this;
            self.refresh();
            self.uploader = self.$scope.uploader = new FileUploader({
                url: self.Commands.data.applianceManagement.verifylicense
            });
        }
        SetupWizardController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.$http.post(self.Commands.data.initialSetup.getInitialSetup, null)
                    .then(function (response) {
                    self.setupData = response.data.responseObj;
                    //console.log('refresh, self.setupData:');
                    //console.log(self.setupData);
                    self.aboutData.serviceTag = self.setupData.licenseData.softwareservicetag;
                }),
                self.$http.post(self.Commands.data.environment.getTimeZones, {})
                    .then(function (response) {
                    self.timeZones = response.data.responseObj;
                })
            ])
                .then(function () {
                //self.setWatchForFileUpload();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        SetupWizardController.prototype.enterWelcome = function () {
            var self = this;
            self.$rootScope.helpToken = 'initialsetupwelcome';
        };
        SetupWizardController.prototype.enterLicense = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.$rootScope.helpToken = 'initialsetuplicensing';
            self.setWatchForFileUpload();
            //testing code; force isValid to true in order to test scenarios where a valid license file has already been successfully uploaded
            //and we are running the wizard anyway to update something else, or the user cancelled out of the wizard and came back in
            //self.setupData.licenseData.isValid = true;
        };
        SetupWizardController.prototype.setWatchForFileUpload = function () {
            var self = this;
            document.getElementById('idLocationFileUpload').onchange = function (evt) {
                var fileInput = document.getElementById("idLocationFileUpload");
                //reset form
                self.forms.uploadLicenseForm._submitted = false;
                //if file cleared
                if (!fileInput.value) {
                    //clear form validation
                    self.setupData.licenseData.fileModel = "";
                    self.fileModel = "";
                }
                else {
                    //set file
                    var element = angular.element(evt.target)[0];
                    self.fileModel = element.files[0];
                    self.setupData.licenseData.fileModel = element.value;
                    self.$timeout(function () { self.verifyLicense(self.fileModel); }, 10); //Wrap in a timeout to trigger a digest cycle.
                }
            };
        };
        SetupWizardController.prototype.verifyLicense = function (file) {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            //using angular uploader
            self.$scope.uploader.formData = [];
            self.$scope.uploader.formData.push(file);
            //set error and success callbacks 
            angular.extend(self.uploader, {
                onErrorItem: function (fileItem, response, status, headers) {
                    self.GlobalServices.DisplayError(response ||
                        { message: self.$translate.instant("SETUPWIZARD_ErrorInUpload") }, self.errors);
                    d.reject();
                },
                onCompleteAll: function (criteriaObj, errorObj, responseObj, responseCode) {
                    d.resolve();
                },
                onBeforeUploadItem: function (item) {
                    item.formData = angular.copy(item.uploader.formData);
                },
                onSuccessItem: function (item, response, status, headers) {
                    self.setupData.licenseData = response.responseObj;
                    //console.log('verifyLicense, self.setupData.licenseData:');
                    //console.log(self.setupData.licenseData);
                    d.resolve();
                }
            });
            self.uploader.uploadAll();
            return d.promise;
        };
        SetupWizardController.prototype.updateLicense = function () {
            var self = this;
            self.forms.uploadLicenseForm._submitted = true;
            var step = self.$q.defer();
            if (self.forms.uploadLicenseForm.$invalid) {
                step.reject();
                return step.promise;
            }
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.$http.post(self.Commands.data.initialSetup.updateLicenseData, self.setupData.licenseData)
                    .then(function (response) {
                    //JIRA 1506, 1493 bug fixes:
                    //if the responseObj is null, then we must have sent the back-end a null licensefile value, and
                    //that could happen under normal/valid circumstances where we have already sent a valid license file and have
                    //restarted the setup wizard to update something else, for example (or the user never finished and cancelled out of the wizard)
                    //in this situation, we do not want to overwrite the values in setupData.licenseData with nulls so we will ignore the response
                    //if (response.data.responseObj == null) {
                    //    //this was the error scenario
                    //    console.log('response.data.responseObj is null, do not overwrite self.setupData.licenseData');
                    //}
                    if (response.data.responseObj != null) {
                        //console.log('response.data.responseObj is not null, overwriting self.setupData.licenseData');
                        self.setupData.licenseData = response.data.responseObj;
                    }
                }),
                self.aboutData.serviceTag &&
                    self.$http.post(self.Commands.data.applianceManagement.updateServiceTag, self.aboutData.serviceTag)
            ])
                .then(function () {
                step.resolve();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
                step.reject();
            })
                .finally(function () { return d.resolve(); });
            return step.promise;
        };
        SetupWizardController.prototype.enterNTP = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.$rootScope.helpToken = 'initialsetuptimezone';
        };
        SetupWizardController.prototype.updateNTP = function () {
            var self = this;
            self.forms.step3._submitted = true;
            return self.$q(function (resolve, reject) {
                if (self.forms.step3.$valid) {
                    var d = self.$q.defer();
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.$http.post(self.Commands.data.initialSetup.updateTimeData, self.setupData.timeData)
                        .then(function (response) {
                        self.setupData.timeData = response.data.responseObj;
                        resolve();
                    })
                        .catch(function (response) {
                        self.GlobalServices.DisplayError(response.data, self.errors);
                        reject();
                    })
                        .finally(function () { return d.resolve(); });
                }
                else {
                    reject();
                }
            });
        };
        SetupWizardController.prototype.enterVxRack = function () {
            var self = this;
            self.$rootScope.helpToken = 'InitialSetupAlertConnectorSettings';
            return self.$q(function (resolve, reject) {
                self.$http.post(self.Commands.data.applianceManagement.getVxRackSettings, {})
                    .then(function (response) {
                    self.setupData.vxRackSettings = response.data.responseObj;
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                })
                    .finally(function () { return resolve(); });
            });
        };
        SetupWizardController.prototype.saveVxRack = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                if (!self.enableAlertConnector) {
                    return resolve();
                }
                if (self.forms.step6.$valid) {
                    var d = self.$q.defer();
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.$http.post(self.Commands.data.applianceManagement.setVxRackSettingsRegister, self.setupData.vxRackSettings)
                        .then(function (data) {
                        resolve();
                    })
                        .catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); reject(); })
                        .finally(function () { return d.resolve(); });
                }
                else {
                    reject();
                }
            });
        };
        SetupWizardController.prototype.enterProxy = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.$rootScope.helpToken = 'initialsetupproxysettings';
        };
        SetupWizardController.prototype.updateProxy = function () {
            var self = this;
            self.forms.step4._submitted = true;
            return self.$q(function (resolve, reject) {
                if (self.forms.step4.$valid) {
                    var d = self.$q.defer();
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.$http.post(self.Commands.data.initialSetup.updateProxyData, self.setupData.proxyData)
                        .then(function (response) {
                        self.setupData.proxyData = response.data.responseObj;
                        resolve();
                    })
                        .catch(function (response) {
                        self.GlobalServices.DisplayError(response.data, self.errors);
                        reject();
                    })
                        .finally(function () { return d.resolve(); });
                }
                else {
                    reject();
                }
            });
        };
        SetupWizardController.prototype.enterDHCP = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.$rootScope.helpToken = 'configureDHCPsettings';
        };
        SetupWizardController.prototype.updateDHCP = function () {
            var self = this;
            self.forms.step5._submitted = true;
            return self.$q(function (resolve, reject) {
                if (self.forms.step5.$valid) {
                    var d = self.$q.defer();
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.$http.post(self.Commands.data.initialSetup.updateDhcpData, self.setupData.dhcpData)
                        .then(function (response) {
                        self.setupData.dhcpData = response.data.responseObj;
                        resolve();
                    })
                        .catch(function (response) {
                        self.GlobalServices.DisplayError(response.data, self.errors);
                        reject();
                    })
                        .finally(function () { return d.resolve(); });
                }
                else {
                    reject();
                }
            });
        };
        SetupWizardController.prototype.getSelectedTimeZoneName = function () {
            var self = this;
            if (self.timeZones.length) {
                var match = _.find(self.timeZones, function (tz) {
                    return tz.id == self.setupData.timeData.timeZone;
                });
                return match ? match.name : "";
            }
        };
        SetupWizardController.prototype.enterSummary = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.$rootScope.helpToken = 'initialsetupsummary';
        };
        SetupWizardController.prototype.finishWizard = function () {
            var self = this;
            var d = self.$q.defer();
            self.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('SETUPWIZARD_SubmitInfo')))
                .then(function () {
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.initialSetup.completeInitialSetup, self.setupData)
                    .then(function () {
                    d.resolve();
                    self.$scope.modal.close();
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                    d.resolve();
                });
            });
        };
        SetupWizardController.prototype.testProxy = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.testProxyRunning = true;
            self.$http.post(self.Commands.data.initialSetup.testProxy, self.setupData.proxyData)
                .then(function (response) {
                self.Dialog(self.$translate.instant("GENERIC_Alert"), self.$translate.instant("SETUPWIZARD_ProxySettingsProxyConnectionSuccess"), true);
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () {
                d.resolve();
                self.testProxyRunning = false;
            });
        };
        SetupWizardController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        SetupWizardController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        SetupWizardController.$inject = ['Modal', '$scope', 'Dialog', '$http', 'Loading', '$q', '$timeout', '$rootScope', 'Commands', '$translate', 'GlobalServices', "FileUploader"];
        return SetupWizardController;
    }());
    asm.SetupWizardController = SetupWizardController;
    angular
        .module('app')
        .controller('SetupWizardController', SetupWizardController);
})(asm || (asm = {}));
//# sourceMappingURL=setupwizard.js.map
