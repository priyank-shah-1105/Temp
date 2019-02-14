var asm;
(function (asm) {
    var ResourceLogsController = (function () {
        function ResourceLogsController($http, $timeout, $q, $translate, modal, loading, commands, globalServices, $scope, constants) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.modal = modal;
            this.loading = loading;
            this.commands = commands;
            this.globalServices = globalServices;
            this.$scope = $scope;
            this.constants = constants;
            this.logs = [];
            this.logsSafe = [];
            this.errors = new Array();
            var self = this;
            self.refresh();
        }
        ResourceLogsController.prototype.refresh = function () {
            var self = this;
            var d = self.$q.defer();
            self.globalServices.ClearErrors(self.errors);
            self.loading(d.promise);
            var componentId = self.$scope.modal.params.componentid;
            var deploymentId = self.$scope.modal.params.deploymentid;
            var request = { componentId: componentId, deploymentId: deploymentId };
            self.$http.post(self.commands.data.services.getPuppetLogs, request).then(function (data) {
                self.logs = data.data.responseObj;
                self.logsSafe = angular.copy(self.logs);
            }).catch(function (response) {
                self.globalServices.DisplayError(response.data, self.errors);
            }).finally(function () { return d.resolve(); });
        };
        ResourceLogsController.prototype.exportLogs = function () {
            var self = this;
            var componentId = self.$scope.modal.params.componentid;
            var deploymentId = self.$scope.modal.params.deploymentid;
            window.location.href = self.commands.data.services.exportPuppetLogs + '?componentId=' + componentId + '&deploymentId=' + deploymentId;
        };
        ResourceLogsController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Commands', 'GlobalServices', '$scope', 'constants'];
        return ResourceLogsController;
    }());
    asm.ResourceLogsController = ResourceLogsController;
    angular
        .module('app')
        .controller('ResourceLogsController', ResourceLogsController);
})(asm || (asm = {}));
//# sourceMappingURL=resourceLogs.js.map
