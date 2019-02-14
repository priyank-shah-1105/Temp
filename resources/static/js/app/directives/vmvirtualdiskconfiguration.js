var asm;
(function (asm) {
    "use strict";
    var VmVirtualDiskConfigurationController = (function () {
        function VmVirtualDiskConfigurationController(GlobalServices, constants) {
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            this.editMode = true;
            this.validityObj = {
                id: this.GlobalServices.NewGuid(),
                invalid: false
            };
            var self = this;
            self.id = "setting_" + self.setting.id + "_" + (self.setting.category ? self.setting.category.id : '') + "_" + (self.component ? self.component.id : '');
            if (self.invalidArray) {
                self.invalidArray.push(self.validityObj);
            }
            else {
                self.invalidArray = [];
                self.invalidArray.push(self.validityObj);
            }
            if (self.setting.readOnly || self.readOnlyMode) {
                self.editMode = false;
            }
            self.refresh();
        }
        VmVirtualDiskConfigurationController.prototype.refresh = function () {
            var self = this;
            if (!self.setting.value) {
                self.setting.value = {};
            }
            else {
                self.setting.value = typeof self.setting.value === 'string' ?
                    angular.fromJson(self.setting.value) :
                    self.setting.value;
            }
            self.setting.value.virtualdisks = self.setting.value.virtualdisks || [{ id: self.GlobalServices.NewGuid(), disksize: 32 }];
            self.checkAllDisks();
        };
        VmVirtualDiskConfigurationController.prototype.createDisk = function () {
            var self = this;
            self.setting.value.virtualdisks.push({
                id: self.GlobalServices.NewGuid(),
                disksize: 32
            });
        };
        VmVirtualDiskConfigurationController.prototype.checkAllDisks = function () {
            var self = this;
            return self.validityObj.invalid =
                self.editMode
                    && !self.setting.value.virtualdisks.length
                    || !!_.find(self.setting.value.virtualdisks, function (disk) { return self.invalidDisk(disk, true); });
        };
        VmVirtualDiskConfigurationController.prototype.invalidDisk = function (disk, skip) {
            var self = this;
            skip || !self.editMode || self.checkAllDisks();
            return self.editMode && !disk.disksize;
        };
        VmVirtualDiskConfigurationController.$inject = ['GlobalServices', 'constants'];
        return VmVirtualDiskConfigurationController;
    }());
    angular.module('app')
        .component('vmVirtualDiskConfiguration', {
        templateUrl: 'views/vmvirtualdiskconfiguration.html',
        controller: VmVirtualDiskConfigurationController,
        controllerAs: 'diskConfiguration',
        bindings: {
            setting: '=',
            readOnlyMode: '<?',
            invalidArray: '=?',
            form: '=?',
            component: '<?'
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=vmvirtualdiskconfiguration.js.map
