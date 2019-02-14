var asm;
(function (asm) {
    var NtpEditModalController = (function () {
        function NtpEditModalController($scope, Modal, Dialog, $http, $q, $timeout, Loading, GlobalServices, Commands) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$q = $q;
            this.$timeout = $timeout;
            this.Loading = Loading;
            this.GlobalServices = GlobalServices;
            this.Commands = Commands;
            this.viewModel = { ntpTimeZoneSettings: {} };
            this.errors = new Array();
            var self = this;
            self.initialize();
        }
        NtpEditModalController.prototype.initialize = function () {
            var self = this;
            self.viewModel = self.$scope.modal.params.viewModel;
        };
        NtpEditModalController.prototype.save = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.saveNtpTimeZoneSettings(self.viewModel.ntpTimeZoneSettings)
                .then(function () {
                d.resolve();
                self.close();
            })
                .catch(function (response) {
                d.resolve();
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        NtpEditModalController.prototype.saveNtpTimeZoneSettings = function (settings) {
            var self = this;
            return self.$http.post(self.Commands.data.environment.setNtpTimeZoneSettings, settings);
        };
        NtpEditModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        NtpEditModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        NtpEditModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$q', '$timeout', 'Loading', 'GlobalServices', 'Commands'];
        return NtpEditModalController;
    }());
    asm.NtpEditModalController = NtpEditModalController;
    angular
        .module('app')
        .controller('NtpEditModalController', NtpEditModalController);
})(asm || (asm = {}));
//# sourceMappingURL=ntpEditModal.js.map
