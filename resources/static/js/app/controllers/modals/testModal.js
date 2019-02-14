var asm;
(function (asm) {
    var TestModalController = (function () {
        function TestModalController($scope, Modal, Dialog) {
            this.$scope = $scope;
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.jobs = $scope.modal.params.jobs;
        }
        TestModalController.$inject = ['$scope', 'Modal', 'Dialog'];
        return TestModalController;
    }());
    asm.TestModalController = TestModalController;
    angular
        .module('app')
        .controller('TestModalController', TestModalController);
})(asm || (asm = {}));
//# sourceMappingURL=testModal.js.map
