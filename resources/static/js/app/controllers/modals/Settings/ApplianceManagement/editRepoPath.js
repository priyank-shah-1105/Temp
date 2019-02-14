var asm;
(function (asm) {
    var EditRepoPathModalController = (function () {
        function EditRepoPathModalController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            this.applianceUpdateInfo = {};
            this.errors = [];
            var self = this;
            self.initialize();
        }
        //help url: ASM.urlConfig.help.UpdateRepositoryPath
        EditRepoPathModalController.prototype.initialize = function () {
            var self = this;
            self.applianceUpdateInfo = self.$scope.modal.params.applianceUpdateInfo;
        };
        EditRepoPathModalController.prototype.save = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.saveApplianceUpdate(self.applianceUpdateInfo).then(function (response) {
                d.resolve();
                self.close();
            }).catch(function (response) { self.GlobalServices.DisplayError(response.data, self.errors); });
        };
        EditRepoPathModalController.prototype.saveApplianceUpdate = function (update) {
            var self = this;
            return self.$http.post(self.Commands.data.applianceManagement.setApplianceUpdate, { requestObj: update });
        };
        EditRepoPathModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        EditRepoPathModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        EditRepoPathModalController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return EditRepoPathModalController;
    }());
    asm.EditRepoPathModalController = EditRepoPathModalController;
    angular
        .module('app')
        .controller('EditRepoPathModalController', EditRepoPathModalController);
})(asm || (asm = {}));
//# sourceMappingURL=editRepoPath.js.map
