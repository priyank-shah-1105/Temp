var asm;
(function (asm) {
    var StopManagingApplicationsModalController = (function () {
        function StopManagingApplicationsModalController($scope, Modal, Dialog, $http, $q, Loading, Commands, GlobalServices) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$q = $q;
            this.Loading = Loading;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.errors = new Array();
            var self = this;
            self.initialize();
        }
        StopManagingApplicationsModalController.prototype.initialize = function () {
            var self = this;
            self.serviceId = self.$scope.modal.params.serviceId;
            self.selectedComponent = self.$scope.modal.params.selectedComponent;
        };
        StopManagingApplicationsModalController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        StopManagingApplicationsModalController.prototype.getNumberOfComponents = function () {
            var self = this;
            return _.filter(self.selectedComponent.relatedcomponents, function (rc) {
                return rc.installOrder > 0;
            }).length;
        };
        StopManagingApplicationsModalController.prototype.getCheckedApplications = function () {
            var self = this;
            var checkedItems = _.filter(self.selectedComponent.relatedcomponents, function (rc) {
                return rc.rowChecked == true;
            });
            var idList = [];
            angular.forEach(checkedItems, function (item) {
                idList.push(item.id);
            });
            return idList;
        };
        StopManagingApplicationsModalController.prototype.isApplicationChecked = function () {
            var self = this;
            return self.getCheckedApplications().length > 0;
            //var self: StopManagingApplicationsModalController = this;
            //return _.filter(self.selectedComponent.relatedcomponents,
            //    function (rc: any) {
            //        return rc.rowChecked == true
            //    }
            //).length>0;
        };
        StopManagingApplicationsModalController.prototype.stopManagingApps = function () {
            var self = this;
            var d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            self.$http.post(self.Commands.data.services.stopManagingApplications, {
                serviceId: self.serviceId,
                componentId: self.selectedComponent.id,
                applicationIds: self.getCheckedApplications()
            }).then(function (data) {
                d.resolve();
                self.$scope.modal.close();
            }).catch(function (data) {
                d.resolve();
                self.GlobalServices.DisplayError(data.data, self.errors);
            });
        };
        StopManagingApplicationsModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        StopManagingApplicationsModalController.$inject = ['$scope', 'Modal', 'Dialog', '$http', '$q', 'Loading', 'Commands', 'GlobalServices'];
        return StopManagingApplicationsModalController;
    }());
    asm.StopManagingApplicationsModalController = StopManagingApplicationsModalController;
    angular
        .module('app')
        .controller('StopManagingApplicationsModalController', StopManagingApplicationsModalController);
})(asm || (asm = {}));
//# sourceMappingURL=stopManagingApplicationsModal.js.map
