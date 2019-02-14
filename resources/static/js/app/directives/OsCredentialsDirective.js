var asm;
(function (asm) {
    "use strict";
    var OSCredentialsController = (function () {
        function OSCredentialsController($http, $translate, Commands, GlobalServices, $q, $timeout, $interval, $location, Loading, $filter, Modal, $scope) {
            this.$http = $http;
            this.$translate = $translate;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$q = $q;
            this.$timeout = $timeout;
            this.$interval = $interval;
            this.$location = $location;
            this.Loading = Loading;
            this.$filter = $filter;
            this.Modal = Modal;
            this.$scope = $scope;
            this.credentials = [];
            //OS credentials, credential types, and the selectable credentials are fetched by this directive and passed back
            var self = this;
            self.$scope.$watch(function () { return self.refreshService; }, function (newValue, oldValue) {
                if (oldValue !== newValue) {
                    self.refresh(true);
                }
            });
            self.credentialTypes = [
                { type: "server", name: self.$translate.instant("GENERIC_Servers"), credentialId: "" },
                { type: "vm", name: self.$translate.instant("SERVICE_ADD_EXISTING_Service_SVMs"), credentialId: "" }
            ];
            self.refresh();
        }
        OSCredentialsController.prototype.refresh = function (force) {
            var self = this;
            if (self.credentials.length > 0 && !force)
                return false;
            var d = self.$q.defer();
            if (!self.hideSpinner)
                self.Loading(d.promise);
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$q.all([
                self.getCredentialSummaryList()
                    .then(function (response) {
                    self.credentials = response.data.responseObj;
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                }),
                self.getExistingServiceOSCredentials()
                    .then(function (response) {
                    self.osCredentials = response.data.responseObj;
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                })
            ])
                .finally(function () { return d.resolve(); });
        };
        OSCredentialsController.prototype.masterCredentialChange = function (selectedCredentialId, list) {
            angular.forEach(list, function (listItem) {
                listItem.credentialId = selectedCredentialId;
                listItem._editMode = false;
            });
        };
        OSCredentialsController.prototype.getFilteredCredentialArray = function (array, type) {
            var self = this;
            return self.$filter("filter", { type: type })(array);
        };
        OSCredentialsController.prototype.addCredential = function (device, masterList) {
            var self = this;
            var theModal = self.Modal({
                title: self.$translate.instant('CREDENTIALS_CreateTitle'),
                onHelp: function () {
                    self.GlobalServices.showHelp('creatingcredentials');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/credentials/editcredentials.html',
                controller: 'EditCredentialsController as editCredentialsController',
                params: {
                    canChangeCredentialType: false,
                    typeId: "os",
                    editMode: false
                },
                onComplete: function (credentialId) {
                    var d = self.$q.defer();
                    self.Loading(d.promise);
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.getCredentialSummaryList()
                        .then(function (response) {
                        self.credentials = response.data.responseObj;
                        device.credentialId = credentialId;
                        if (masterList) {
                            self.masterCredentialChange(credentialId, masterList);
                        }
                    })
                        .catch(function (response) {
                        self.GlobalServices.DisplayError(response.data, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
            });
            theModal.modal.show();
        };
        //pass service service.template for storageonly
        OSCredentialsController.prototype.getExistingServiceOSCredentials = function () {
            var self = this;
            if (self.storageOnly()) {
                return self.$http.post(self.Commands.data.services.getExistingSOServiceOSCredentials, self.credentialsRequestObj);
            }
            else {
                return self.$http.post(self.Commands.data.services.getExistingServiceOSCredentials, self.credentialsRequestObj);
            }
        };
        OSCredentialsController.prototype.getCredentialSummaryList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.credential.getCredentialByType, { id: 'os' });
        };
        OSCredentialsController.$inject = ["$http", "$translate", "Commands", "GlobalServices", "$q", "$timeout", "$interval", "$location", "Loading", "$filter", "Modal", "$scope"];
        return OSCredentialsController;
    }());
    angular.module('app')
        .component("osCredentials", {
        templateUrl: "views/oscredentials.html",
        controller: OSCredentialsController,
        controllerAs: 'osCredentialsController',
        bindings: {
            osCredentials: "=",
            credentialsRequestObj: "=",
            form: "=",
            errors: "=",
            credentials: "=",
            credentialTypes: "=",
            hideSpinner: "=",
            storageOnly: "&",
            refreshService: "<"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=OsCredentialsDirective.js.map
