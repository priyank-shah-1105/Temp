var asm;
(function (asm) {
    var DownloadCertModalController = (function () {
        function DownloadCertModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            this.cert = {};
            this.countries = new Array();
            this.showLink = false;
            var self = this;
            self.initialize();
        }
        DownloadCertModalController.prototype.initialize = function () {
            var self = this;
            self.$http.post('appliance/getappliancecertificateinfo', null)
                .then(function (response) {
                self.cert = response.data.responseObj;
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); });
        };
        DownloadCertModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        DownloadCertModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        DownloadCertModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return DownloadCertModalController;
    }());
    asm.DownloadCertModalController = DownloadCertModalController;
    angular
        .module('app')
        .controller('DownloadCertModalController', DownloadCertModalController);
})(asm || (asm = {}));
//# sourceMappingURL=downloadCert.js.map
