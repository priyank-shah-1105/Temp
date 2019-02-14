var asm;
(function (asm) {
    var ViewPhysicalDisksController = (function () {
        function ViewPhysicalDisksController($scope) {
            this.$scope = $scope;
        }
        ViewPhysicalDisksController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ViewPhysicalDisksController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.dismiss();
        };
        ViewPhysicalDisksController.$inject = ["$scope"];
        return ViewPhysicalDisksController;
    }());
    asm.ViewPhysicalDisksController = ViewPhysicalDisksController;
    angular
        .module("app")
        .controller("ViewPhysicalDisksController", ViewPhysicalDisksController);
})(asm || (asm = {}));
//# sourceMappingURL=viewPhysicalDisksModal.js.map
