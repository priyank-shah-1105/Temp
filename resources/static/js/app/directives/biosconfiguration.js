var asm;
(function (asm) {
    "use strict";
    var BiosConfigurationController = (function () {
        function BiosConfigurationController(GlobalServices) {
            this.GlobalServices = GlobalServices;
            this.radioGuid = this.GlobalServices.NewGuid();
            this.editMode = true;
            var self = this;
            if (self.setting.readOnly || self.readOnlyMode) {
                self.editMode = false;
            }
        }
        BiosConfigurationController.$inject = ['GlobalServices'];
        return BiosConfigurationController;
    }());
    angular.module('app')
        .component('biosConfiguration', {
        templateUrl: 'views/biosconfiguration.html',
        controller: BiosConfigurationController,
        controllerAs: 'biosConfigurationController',
        bindings: {
            setting: '=',
            readOnlyMode: '<?'
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=biosconfiguration.js.map
