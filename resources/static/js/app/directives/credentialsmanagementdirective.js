var asm;
(function (asm) {
    "use strict";
    var CredentialsController = (function () {
        function CredentialsController($http, $timeout, $scope, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.errors = new Array();
            var self = this;
            //note:  this is necessary due to the manual refresh found in the html
            self.refresh(false);
        }
        CredentialsController.prototype.activate = function () {
            var self = this;
            this.$timeout(function () {
                self.refresh(false);
            }, 10000);
        };
        CredentialsController.prototype.refresh = function (calledFromUI) {
            var self = this;
            if (calledFromUI) {
                var deferred = self.$q.defer();
                self.Loading(deferred.promise);
            }
            self.GlobalServices.ClearErrors(self.errors);
            self.$http.post(self.Commands.data.credential.getCredentialSummaryList, null)
                .then(function (data) {
                self.credentials = _.sortBy(data.data.responseObj, function (credential) {
                    return credential.credentialsName.toLowerCase();
                });
                self.credentialsSafe = angular.copy(self.credentials);
                self.selectedCredential = self.selectedCredential ? _.find(self.credentials, { id: self.selectedCredential.id }) ? self.selectedCredential : self.credentials[0] : self.credentials[0];
                self.onSelectedCredential(self.selectedCredential);
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () {
                if (calledFromUI) {
                    deferred.resolve();
                }
            });
        };
        ;
        CredentialsController.prototype.onSelectedCredential = function (selectedItem) {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.$http.post(self.Commands.data.credential.getCredentialById, { id: selectedItem.id })
                .then(function (response) {
                self.selectedCredential = response.data.responseObj;
            })
                .catch(function (error) {
                self.GlobalServices.DisplayError(error.data, self.errors);
            });
        };
        CredentialsController.prototype.doUpdates = function (updateType) {
            var self = this, title, helptoken, editMode = false, canChangeCredentialType, credential;
            if (updateType.toUpperCase() === 'CREATE') {
                title = self.$translate.instant('CREDENTIALS_CreateTitle');
                helptoken = 'creatingcredentials';
                canChangeCredentialType = true;
            }
            else {
                title = self.$translate.instant('CREDENTIALS_EditTitle');
                helptoken = 'editingcredentials';
                editMode = true;
                canChangeCredentialType = false;
                credential = angular.copy(self.selectedCredential);
            }
            var theModal = self.Modal({
                title: title,
                onHelp: function () {
                    self.GlobalServices.showHelp(helptoken);
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/credentials/editcredentials.html',
                controller: 'EditCredentialsController as editCredentialsController',
                params: {
                    source: 'credentials-management',
                    editMode: editMode,
                    credential: credential,
                    canChangeCredentialType: canChangeCredentialType
                },
                onComplete: function (credentialId) {
                    self.refresh(true);
                    if (self.credentials.length > 0) {
                        self.onSelectedCredential(_.filter(self.credentials, { id: self.selectedCredential.id })[0]);
                    }
                }
            });
            theModal.modal.show();
        };
        CredentialsController.prototype.doDelete = function () {
            var self = this;
            if (!self.selectedCredential.candelete) {
                return;
            }
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('CREDENTIALS_DeleteConfirmation'))
                .then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.credential.deleteCredential, { id: self.selectedCredential.id })
                    .then(function (data) {
                    self.refresh(false);
                })
                    .catch(function (error) {
                    self.GlobalServices.DisplayError(error.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        CredentialsController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return CredentialsController;
    }());
    angular.module('app')
        .component('credentialsManagement', {
        templateUrl: "views/listcredentials.html",
        controller: CredentialsController,
        controllerAs: 'creds',
        bindings: {}
    });
})(asm || (asm = {}));
//# sourceMappingURL=credentialsmanagementdirective.js.map
