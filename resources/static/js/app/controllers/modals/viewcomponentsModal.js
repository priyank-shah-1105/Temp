var asm;
(function (asm) {
    var ViewComponentsController = (function () {
        function ViewComponentsController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, constants) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            var self = this;
            self.components = self.$scope.modal.params.selected.components;
            self.displayedComponents = [].concat(self.components);
            self.componentType();
            //get all unique types except for where they are undefined
            self.filterItems = [{ name: self.$translate.instant("GENERIC_All") }]
                .concat(_.map(_.filter(_.uniqBy(self.components, "basicType"), function (type) { return type.basicType; }), function (type) {
                return { name: type.basicType, id: type.basicType };
            }));
        }
        ViewComponentsController.prototype.componentType = function () {
            var self = this;
            angular.forEach(self.components, function (component) {
                if (self.GlobalServices.IsDeviceType(component.type, "Server")) {
                    component.basicType = 'Server';
                }
                if (self.GlobalServices.IsDeviceType(component.type, "Chassis")) {
                    component.basicType = 'Chassis';
                }
                if (self.GlobalServices.IsDeviceType(component.type, "Switch")) {
                    component.basicType = 'Switch';
                }
                if (self.GlobalServices.IsDeviceType(component.type, "IOM")) {
                    component.basicType = 'IOM';
                }
                if (self.GlobalServices.IsDeviceType(component.type, "RackServer")) {
                    component.basicType = 'Rack Server';
                }
                if (self.GlobalServices.IsDeviceType(component.type, "BladeServer")) {
                    component.basicType = 'Blade Server';
                }
                if (self.GlobalServices.IsDeviceType(component.type, "FXServer")) {
                    component.basicType = 'FX Server';
                }
                if (self.GlobalServices.IsDeviceType(component.type, "VM")) {
                    component.basicType = 'VM';
                }
                if (self.GlobalServices.IsDeviceType(component.type, "vCenter")) {
                    component.basicType = 'vCenter';
                }
                if (self.GlobalServices.IsDeviceType(component.type, "SCVMM")) {
                    component.basicType = 'SCVMM';
                }
                if (self.GlobalServices.IsDeviceType(component.type, "Storage")) {
                    component.basicType = 'Storage';
                }
            });
        };
        ViewComponentsController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ViewComponentsController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        ViewComponentsController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'Loading', 'Dialog', 'Commands', 'GlobalServices', "constants"];
        return ViewComponentsController;
    }());
    asm.ViewComponentsController = ViewComponentsController;
    angular
        .module('app')
        .controller('ViewComponentsController', ViewComponentsController);
})(asm || (asm = {}));
//# sourceMappingURL=viewcomponentsModal.js.map
