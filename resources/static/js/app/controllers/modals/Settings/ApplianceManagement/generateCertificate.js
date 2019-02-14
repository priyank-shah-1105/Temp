var asm;
(function (asm) {
    var GenerateCertModalController = (function () {
        function GenerateCertModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
            this.Modal = Modal;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.settings = {};
            this.countries = new Array();
            this.showLink = false;
            this.errors = [];
            var self = this;
            self.initialize();
        }
        GenerateCertModalController.prototype.initialize = function () {
            var self = this;
            self.settings = self.$scope.modal.params.settings;
            self.getCountries().then(function (response) {
                self.countries = response.data.responseObj;
                self.countries.unshift({ name: self.$translate.instant('SETTINGS_SelectaCountry'), id: null });
            });
        };
        GenerateCertModalController.prototype.save = function () {
            var self = this;
            self.Dialog(self.$translate.instant("GENERIC_Success"), self.$translate.instant("SETTINGS_Certificate_ConfirmationDialog"))
                .then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.saveCert(self.settings)
                    .then(function (response) {
                    self.close();
                    self.$timeout(function () { return self.downloadCert(); }, 1000);
                })
                    .catch(function (error) {
                    self.GlobalServices.DisplayError(error.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            })
                .catch(function () {
                self.close();
            });
        };
        GenerateCertModalController.prototype.saveCert = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.submitCertificateSigReqForm, { requestObj: update });
        };
        GenerateCertModalController.prototype.getCountries = function () {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.getAvailableCountries, {});
        };
        GenerateCertModalController.prototype.downloadCert = function () {
            var self = this;
            var modal = self.Modal({
                title: self.$translate.instant('SETTINGS_CertSignRequest'),
                modalSize: 'modal-lg',
                templateUrl: 'views/settings/virtualappliancemanagement/downloadcertmodal.html',
                controller: 'DownloadCertModalController as downloadCertModalController',
                params: {
                    certInfo: angular.copy(self.settings)
                },
                onComplete: function () {
                }
            });
            modal.modal.show();
        };
        GenerateCertModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        GenerateCertModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        GenerateCertModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return GenerateCertModalController;
    }());
    asm.GenerateCertModalController = GenerateCertModalController;
    angular
        .module('app')
        .controller('GenerateCertModalController', GenerateCertModalController);
})(asm || (asm = {}));
//# sourceMappingURL=generateCertificate.js.map
