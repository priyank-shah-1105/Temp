var asm;
(function (asm) {
    "use strict";
    var ChassisTableController = (function () {
        function ChassisTableController($http, commands, $q, $translate, loading, globalServices, Modal, constants, $filter, GlobalServices) {
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
        Object.defineProperty(ChassisTableController.prototype, "pending", {
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
        ChassisTableController.prototype.selectAll = function () {
            var self = this, val = !!_.find(self.chassisList, { configureDevice: false });
            //determine whether should toggle all on or all off ^, then toggle all v
            angular.forEach(self.chassisList, function (rack) { rack.configureDevice = val; });
        };
        ChassisTableController.prototype.getAllChecked = function () {
            var self = this;
            return !_.find(self.chassisList, { configureDevice: false });
        };
        ChassisTableController.$inject = ["$http", "Commands", "$q", "$translate", "Loading", "GlobalServices", "Modal", "constants", "$filter", 'GlobalServices'];
        return ChassisTableController;
    }());
    angular.module("app")
        .component("chassisTable", {
        templateUrl: "views/resources/chassistable.html",
        controller: ChassisTableController,
        controllerAs: "chassisTableController",
        bindings: {
            mode: "@",
            chassisList: "=",
            pending: "=?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=chassisTable.js.map
