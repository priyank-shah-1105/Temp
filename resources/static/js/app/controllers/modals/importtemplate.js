var asm;
(function (asm) {
    var ImportTemplateModalController = (function () {
        function ImportTemplateModalController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.templateId = "";
            this.templates = null;
            this.selectedId = null;
            this.errors = new Array();
            var self = this;
            self.templateId = self.$scope.modal.params.template;
            self.getTemplateList();
        }
        ImportTemplateModalController.prototype.getTemplateList = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.templates.getTemplateList, {})
                .then(function (data) {
                self.templates = data.data.responseObj;
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ImportTemplateModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ImportTemplateModalController.prototype.import = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            var params = {
                id: self.templateId,
                importId: self.selectedId
            };
            self.$http.post(self.Commands.data.templates.importTemplate, params)
                .then(function (data) {
                self.$scope.modal.close();
            })
                .catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ImportTemplateModalController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return ImportTemplateModalController;
    }());
    asm.ImportTemplateModalController = ImportTemplateModalController;
    angular
        .module('app')
        .controller('ImportTemplateModalController', ImportTemplateModalController);
})(asm || (asm = {}));
//# sourceMappingURL=importtemplate.js.map
