var asm;
(function (asm) {
    var ServiceController = (function () {
        function ServiceController($http, $timeout, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, $routeParams, constants, $location, $window) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$routeParams = $routeParams;
            this.constants = constants;
            this.$location = $location;
            this.$window = $window;
            this.activeTab = 'serviceDetails';
            this.warnings = [];
            var self = this;
            self.serviceId = self.$routeParams.id;
            self.firmwarereport = self.$routeParams.firmwarereport;
            //having child populate parent so that updates here take place automatically
            //self.refresh(); 
        }
        ServiceController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors();
            self.Loading(d.promise);
            self.getService(self.serviceId).then(function (data) {
                self.service = data.data.responseObj;
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            }).finally(function () { return d.resolve(); });
        };
        ServiceController.prototype.editService = function () {
            var self = this;
            var editServiceModal = self.Modal({
                title: self.$translate.instant('SERVICE_DETAIL_EditServiceInformation'),
                onHelp: function () {
                    self.GlobalServices.showHelp('EditServiceInformation');
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/services/editservice.html',
                controller: 'EditServiceModalController as editService',
                params: {
                    id: self.service.id
                },
                onComplete: function () {
                    self.refreshService = !self.refreshService;
                }
            });
            editServiceModal.modal.show();
        };
        ServiceController.prototype.showPortView = function (tab, server) {
            var self = this;
            self.activeTab = tab;
            //set server id
            self.portviewServer = server;
        };
        ServiceController.prototype.getService = function (id) {
            var self = this;
            return self.$http.post(self.Commands.data.services.getServiceById, { id: id });
        };
        ServiceController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', '$routeParams', 'constants', '$location', '$window'];
        return ServiceController;
    }());
    asm.ServiceController = ServiceController;
    angular
        .module("app")
        .controller("ServiceController", ServiceController);
})(asm || (asm = {}));
//# sourceMappingURL=service.js.map
