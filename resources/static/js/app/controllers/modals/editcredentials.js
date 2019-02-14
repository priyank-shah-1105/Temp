var asm;
(function (asm) {
    var EditCredentialsController = (function () {
        function EditCredentialsController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, constants) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            this.errors = new Array();
            var self = this;
            var params = self.$scope.modal.params;
            self.credential = params.credential || {
                id: null,
                typeId: params.typeId,
                credentialsName: '',
                username: 'root',
                password: '',
                confirmpassword: '',
                enableCertificateCheck: false,
                isTelnet: false,
                communityString: 'public',
                snmpVersionId: '',
                snmpUsername: '',
                authorizationProtocolId: '',
                authorizationProtocolPassword: '',
                privacyProtocolId: '',
                privacyProtocolPassword: '',
                numberOfDevices: 0,
                createdBy: '',
                creationTime: '',
                updateTime: '',
                updatedBy: '',
                credentialProtocol: 'ssh',
                domain: '',
                candelete: true,
                canedit: true,
                gatewayosusername: 'root',
                gatewayospassword: '',
                confirmgatewayospassword: ''
            };
            self.canChangeCredentialType = params.canChangeCredentialType;
            self.editMode = params.editMode;
            self.activate();
        }
        EditCredentialsController.prototype.activate = function () {
            var self = this;
            if (self.editMode) {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.getCredential(self.credential.id)
                    .then(function (data) {
                    angular.extend(self.credential, data.data.responseObj, {
                        confirmPassword: data.data.responseObj.password,
                        confirmGatewayOSPassword: data.data.responseObj.gatewayospassword
                    });
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            }
            //check if any fields need updated upon activate
            self.updateType(self.credential.typeId);
        };
        EditCredentialsController.prototype.updateType = function (newVal) {
            var self = this;
            if (newVal && (self.credential.username == '' || self.credential.username == 'root' || self.credential.username == 'administrator' || self.credential.username == 'admin')) {
                if (newVal == 'scvmm') {
                    self.credential.username = 'administrator';
                }
                else if (newVal == 'scaleio') {
                    self.credential.username = 'admin';
                    self.credential.gatewayosusername = 'root';
                }
                else {
                    self.credential.username = 'root';
                }
            }
        };
        EditCredentialsController.prototype.save = function (formHasErrors) {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.credential.saveCredential, self.credential)
                .then(function (data) {
                self.close(data.data.responseObj.id);
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        EditCredentialsController.prototype.getCredential = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.credential.getCredentialById, { id: id });
        };
        EditCredentialsController.prototype.close = function (credential) {
            var self = this;
            self.$scope.modal.close(credential);
        };
        EditCredentialsController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        EditCredentialsController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices', 'constants'];
        return EditCredentialsController;
    }());
    asm.EditCredentialsController = EditCredentialsController;
    angular
        .module('app')
        .controller('EditCredentialsController', EditCredentialsController);
})(asm || (asm = {}));
//# sourceMappingURL=editcredentials.js.map
