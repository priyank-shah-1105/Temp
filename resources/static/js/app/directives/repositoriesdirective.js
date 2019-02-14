var asm;
(function (asm) {
    "use strict";
    var ListRepositoriesController = (function () {
        function ListRepositoriesController($http, $timeout, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, $rootScope) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$rootScope = $rootScope;
            this.errors = new Array();
            var self = this;
            self.viewData = {
                activeTab: 'firmware'
            };
            if (self.modalOrTab === "ManageRCMVersions") {
                self.viewData.activeTab = "firmware";
            }
        }
        ListRepositoriesController.prototype.clickTab = function (tab) {
            var self = this;
            if (tab == 'iso') {
                self.viewData.activeTab = tab;
                self.$rootScope.helpToken = 'repositorieshomepage';
            }
            else if (tab == 'firmware') {
                self.viewData.activeTab = tab;
                self.$rootScope.helpToken = 'UnderstandingFirmwareRepositories';
            }
        };
        ListRepositoriesController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$rootScope'];
        return ListRepositoriesController;
    }());
    angular.module("app")
        .component("listrepositories", {
        templateUrl: "views/listrepositories.html",
        controller: ListRepositoriesController,
        controllerAs: "rep",
        bindings: {
            modalOrTab: "<"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=repositoriesdirective.js.map
