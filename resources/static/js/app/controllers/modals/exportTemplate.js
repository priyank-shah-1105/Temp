var asm;
(function (asm) {
    var ExportTemplateController = (function () {
        function ExportTemplateController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.form = {
                templateId: this.$scope.modal.params.templateId,
                useEncPwdFromBackup: true,
                id: '',
                fileName: '',
                encryptionPassword: '',
                encryptionVPassword: ''
            };
            this.errors = new Array();
            this.submitForm = false;
        }
        ExportTemplateController.prototype.doExport = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.templates.validateExport, self.form)
                .then(function (data) {
                d.resolve();
                $('#form_exporttemplate').submit();
                self.close();
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ExportTemplateController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ExportTemplateController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return ExportTemplateController;
    }());
    asm.ExportTemplateController = ExportTemplateController;
    angular
        .module('app')
        .controller('ExportTemplateController', ExportTemplateController);
})(asm || (asm = {}));
//# sourceMappingURL=exportTemplate.js.map
