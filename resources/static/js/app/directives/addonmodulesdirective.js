var asm;
(function (asm) {
    "use strict";
    var AddonmodulesController = (function () {
        function AddonmodulesController(Dialog, $http, $timeout, $q, $translate, Modal, GlobalServices, Commands, Loading, $filter) {
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.GlobalServices = GlobalServices;
            this.Commands = Commands;
            this.Loading = Loading;
            this.$filter = $filter;
            this.refresh();
        }
        AddonmodulesController.prototype.activate = function () {
            var self = this;
            self.$timeout(function () {
                self.refresh();
                self.activate();
            }, 60000);
        };
        AddonmodulesController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.addonmodules.getAddOnModules, null).then(function (data) {
                self.results = self.$filter('orderBy')(data.data.responseObj, "name");
                self.displayedresults = [].concat(self.results);
                //when finished getting jobs, reselect the selected and include elapsed time
            }).then(function (response) {
                self.selected = self.selected
                    ? _.find(self.displayedresults, { id: self.selected.id })
                    : self.displayedresults[0];
            }).catch(function (response) {
                self.GlobalServices.DisplayError(response.data);
            }).finally(function () { return d.resolve(); });
        };
        ;
        //delete module
        AddonmodulesController.prototype.deleteModule = function (module) {
            var self = this;
            //Confirmation Dialog box that fire delete on confirmation
            var confirm = self.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('SETTINGS_DeleteConfirmation')));
            confirm.then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.addonmodules.removeAddOnModule, module)
                    .then(function (data) {
                    self.selected = false;
                    self.refresh();
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        AddonmodulesController.prototype.addModule = function () {
            var self = this;
            var testModal = self.Modal({
                title: self.$translate.instant('SETTINGS_AddModule'),
                onHelp: function () {
                    self.GlobalServices.showHelp('AddingAddonModules');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/addonmodules/addmodule.html',
                controller: 'AddModuleModalController as AddModuleModal',
                params: {},
                onComplete: function () {
                    self.refresh();
                }
            });
            testModal.modal.show();
        };
        AddonmodulesController.$inject = ['Dialog', '$http', '$timeout', '$q', '$translate', 'Modal', 'GlobalServices', 'Commands', 'Loading', '$filter'];
        return AddonmodulesController;
    }());
    angular.module('app')
        .component('addonModules', {
        templateUrl: 'views/addonmodules.html',
        controller: AddonmodulesController,
        controllerAs: 'addonmodules',
        bindings: {}
    });
})(asm || (asm = {}));
//# sourceMappingURL=addonmodulesdirective.js.map
