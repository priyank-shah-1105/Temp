var asm;
(function (asm) {
    "use strict";
    var RackTableController = (function () {
        function RackTableController($http, commands, $q, $translate, loading, globalServices, Modal, constants, $filter, GlobalServices) {
            this.$http = $http;
            this.commands = commands;
            this.$q = $q;
            this.$translate = $translate;
            this.loading = loading;
            this.globalServices = globalServices;
            this.Modal = Modal;
            this.constants = constants;
            this.$filter = $filter;
            this.GlobalServices = GlobalServices;
            var self = this;
        }
        Object.defineProperty(RackTableController.prototype, "pending", {
            get: function () {
                var self = this;
                return self._pending;
            },
            set: function (value) {
                var self = this;
                self._pending = value;
                if (value === false) {
                    self.selectAll();
                }
            },
            enumerable: true,
            configurable: true
        });
        RackTableController.prototype.selectAll = function () {
            var self = this, val = !!_.find(self.rackList, { configureDevice: false });
            //determine whether should toggle all on or all off ^, then toggle all v
            angular.forEach(self.rackList, function (rack) { rack.configureDevice = val; });
        };
        RackTableController.prototype.getAllChecked = function () {
            var self = this;
            return !_.find(self.rackList, { configureDevice: false });
        };
        RackTableController.$inject = ["$http", "Commands", "$q", "$translate", "Loading", "GlobalServices", "Modal", "constants", "$filter", 'GlobalServices'];
        return RackTableController;
    }());
    angular.module("app")
        .component("rackTable", {
        templateUrl: "views/resources/racktable.html",
        controller: RackTableController,
        controllerAs: "rackTableController",
        bindings: {
            mode: "@",
            rackList: "=",
            pending: "<?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=rackTable.js.map
