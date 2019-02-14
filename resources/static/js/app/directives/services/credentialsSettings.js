var asm;
(function (asm) {
    "use strict";
    var CredentialsSettings = (function () {
        function CredentialsSettings(Modal, dialog, $http, $timeout, $q, $translate, $window, GlobalServices, Loading, Commands, $scope) {
            this.Modal = Modal;
            this.dialog = dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.$window = $window;
            this.GlobalServices = GlobalServices;
            this.Loading = Loading;
            this.Commands = Commands;
            this.$scope = $scope;
            this.editMode = true;
            this.loadingCredentials = true;
            var self = this;
            self.refresh();
            self.id = "setting_" + self.setting.id + "_" + (self.setting.category ? self.setting.category.id : '') + "_" + (self.component ? self.component.id : '') + "_" + self.GlobalServices.NewGuid();
        }
        CredentialsSettings.prototype.refresh = function () {
            var self = this;
            self.GlobalServices.ClearErrors(self.errors);
            self.getCredentialSummaryList()
                .then(function (response) {
                self.credentials = response.data.responseObj;
                self.loadingCredentials = false;
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        CredentialsSettings.prototype.addCredential = function (device) {
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
                    editMode: false,
                    typeId: "os",
                    canChangeCredentialType: false
                },
                onComplete: function (credentialId) {
                    self.setting.value = credentialId;
                    var d = self.$q.defer();
                    self.Loading(d.promise);
                    self.GlobalServices.ClearErrors(self.errors);
                    self.Loading(d.promise);
                    self.getCredentialSummaryList()
                        .then(function (response) {
                        self.credentials = response.data.responseObj;
                        //self.$scope.$emit("CredentialListUpdate", self.credentials);
                    })
                        .catch(function (response) {
                        self.GlobalServices.DisplayError(response.data, self.errors);
                    })
                        .finally(function () { return d.resolve(); });
                }
            });
            theModal.modal.show();
        };
        CredentialsSettings.prototype.getCredentialSummaryList = function () {
            var self = this;
            return self.$http.post(self.Commands.data.credential.getCredentialByType, { id: 'os' });
        };
        CredentialsSettings.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$translate', "$window", "GlobalServices", "Loading", "Commands", "$scope"];
        return CredentialsSettings;
    }());
    angular.module('app')
        .component('credentialsSettings', {
        templateUrl: 'views/services/credentialssettings.html',
        controller: CredentialsSettings,
        controllerAs: 'credentialsSettingsController',
        bindings: {
            setting: '=',
            readOnlyMode: '<?',
            component: '=?',
            category: '=?',
            form: '=?',
            errors: '=',
            credentials: '=?'
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=credentialsSettings.js.map
