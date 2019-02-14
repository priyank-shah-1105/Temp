var asm;
(function (asm) {
    "use strict";
    var DeviceSummaryController = (function () {
        function DeviceSummaryController($http) {
            this.$http = $http;
            this.refresh();
        }
        DeviceSummaryController.prototype.activate = function () {
            var self = this;
        };
        DeviceSummaryController.prototype.refresh = function () {
            var self = this;
            //Get All Jobs
        };
        DeviceSummaryController.$inject = ['$http'];
        return DeviceSummaryController;
    }());
    asm.DeviceSummaryController = DeviceSummaryController;
    angular.module('app').
        controller('DeviceSummaryController', DeviceSummaryController);
})(asm || (asm = {}));
//# sourceMappingURL=devicesummary.js.map
