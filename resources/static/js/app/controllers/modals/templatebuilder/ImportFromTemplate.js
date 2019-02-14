var asm;
(function (asm) {
    var ImportFromTemplateController = (function () {
        function ImportFromTemplateController(Modal, $http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices) {
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
            this.loading = true;
            this.errors = new Array();
            var self = this;
            self.initialize();
        }
        ImportFromTemplateController.prototype.initialize = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.templates.getTemplateBuilderList, null)
                .then(function (data) {
                //remove all templates without server components
                self.templates = _.filter(data.data.responseObj, function (template) {
                    //remove all non-server components
                    _.remove(template.components, function (component) { return component.type !== "server"; });
                    return template.components.length;
                });
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { d.resolve(); self.loading = false; });
        };
        ImportFromTemplateController.prototype.save = function () {
            var self = this, d = self.$q.defer();
            self.Loading(d.promise);
            self.getClonedComponentFromTemplate(self.GlobalServices.NewGuid(), self.selectedComponent.id, self.selectedTemplate.id)
                .then(function (data) {
                self.close(data.data.responseObj);
            })
                .catch(function (error) {
                self.GlobalServices.DisplayError(error.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        ImportFromTemplateController.prototype.getClonedComponentFromTemplate = function (id, componentId, referenceId) {
            var self = this;
            return self.$http.post(self.Commands.data.templates.getClonedComponentFromTemplate, { id: id, componentId: componentId, referenceId: referenceId });
        };
        ImportFromTemplateController.prototype.close = function (data) {
            var self = this;
            self.$scope.modal.close(data);
        };
        ImportFromTemplateController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ImportFromTemplateController.$inject = ['Modal', '$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices'];
        return ImportFromTemplateController;
    }());
    asm.ImportFromTemplateController = ImportFromTemplateController;
    angular
        .module('app')
        .controller('ImportFromTemplateController', ImportFromTemplateController);
})(asm || (asm = {}));
//# sourceMappingURL=ImportFromTemplate.js.map
