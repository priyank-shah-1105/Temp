var asm;
(function (asm) {
    var DefaultPasswordModalController = (function () {
        function DefaultPasswordModalController($scope, $translate) {
            this.$scope = $scope;
            this.$translate = $translate;
            this.checked = false;
            var self = this;
            self.initialize();
        }
        DefaultPasswordModalController.prototype.initialize = function () {
            var self = this;
        };
        DefaultPasswordModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        DefaultPasswordModalController.$inject = ['$scope', '$translate'];
        return DefaultPasswordModalController;
    }());
    asm.DefaultPasswordModalController = DefaultPasswordModalController;
    angular
        .module('app')
        .controller('DefaultPasswordModalController', DefaultPasswordModalController);
})(asm || (asm = {}));
//# sourceMappingURL=defaultpassword.js.map
