var asm;
(function (asm) {
    var ConfirmDeployServiceModalController = (function () {
        function ConfirmDeployServiceModalController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, $filter, MessageBox, Modal, $location) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$filter = $filter;
            this.MessageBox = MessageBox;
            this.Modal = Modal;
            this.$location = $location;
            var self = this;
            self.activate();
        }
        ConfirmDeployServiceModalController.prototype.activate = function () {
            var self = this;
        };
        ConfirmDeployServiceModalController.prototype.deployServiceWizard = function () {
            var self = this;
            self.close();
            var deployServiceWizard = self.Modal({
                title: self.$translate.instant('SERVICES_NEW_SERVICE_DeployService'),
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/deployservice/deployservicewizard.html',
                controller: 'DeployServiceWizard as deployServiceWizard',
                params: {
                    templateId: self.$scope.modal.params.templateId
                }
            });
            self.$timeout(function () { return deployServiceWizard.modal.show(); }, 500);
        };
        ConfirmDeployServiceModalController.prototype.no = function () {
            var self = this;
            self.goToTemplateBuilder(self.$scope.modal.params.templateId);
            self.close();
        };
        ConfirmDeployServiceModalController.prototype.goToTemplateBuilder = function (id) {
            var self = this;
            self.$timeout(function () {
                self.$location.path("templatebuilder/" + id + "/view");
            }, 500);
        };
        ConfirmDeployServiceModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ConfirmDeployServiceModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ConfirmDeployServiceModalController.$inject = [
            '$http', '$timeout',
            '$scope', '$q', '$translate', 'Loading',
            'Dialog', 'Commands', 'GlobalServices',
            '$filter', 'Messagebox', 'Modal', "$location"];
        return ConfirmDeployServiceModalController;
    }());
    asm.ConfirmDeployServiceModalController = ConfirmDeployServiceModalController;
    angular
        .module('app')
        .controller('ConfirmDeployServiceModalController', ConfirmDeployServiceModalController);
})(asm || (asm = {}));
//# sourceMappingURL=confirmDeployServiceModal.js.map
