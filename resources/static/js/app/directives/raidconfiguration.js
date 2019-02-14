var asm;
(function (asm) {
    "use strict";
    var RaidConfigurationController = (function () {
        function RaidConfigurationController(GlobalServices, constants) {
            var _this = this;
            this.GlobalServices = GlobalServices;
            this.constants = constants;
            this.editMode = true;
            this.radioGuid = "";
            this.validityObj = {
                id: this.GlobalServices.NewGuid(),
                invalid: false
            };
            this.defaultDisk = function () {
                return {
                    id: _this.GlobalServices.NewGuid(),
                    raidlevel: 'raid0',
                    comparator: 'minimum',
                    numberofdisks: 1,
                    disktype: 'any'
                };
            };
            this.hiddenDisks = {
                internal: [], external: []
            };
            var self = this;
            if (self.setting.readOnly || self.readOnlyMode) {
                self.editMode = false;
            }
            else {
                if (self.invalidArray) {
                    self.invalidArray.push(self.validityObj);
                }
                else {
                    self.invalidArray = [];
                    self.invalidArray.push(self.validityObj);
                }
            }
            self.radioGuid = self.GlobalServices.NewGuid();
            if (self.setting.value) {
                self.setting.value = typeof self.setting.value === 'string' ? angular.fromJson(self.setting.value) : self.setting.value;
            }
            else {
                self.updateSetting();
            }
            self.id = "setting_" + self.setting.id + "_" + (self.setting.category ? self.setting.category.id : '') + "_";
            self.id += self.GlobalServices.NewGuid();
            self.validateForm();
        }
        RaidConfigurationController.prototype.raidTypeChange = function () {
            var self = this;
            //cache disks when raidtype turns to basic
            //retrieve them when it turns advanced if they aren't there already
            if (self.setting.value.raidtype === "basic") {
                angular.extend(self.hiddenDisks, {
                    internal: self.setting.value.virtualdisks,
                    external: self.setting.value.externalvirtualdisks
                });
                angular.extend(self.setting.value, {
                    virtualdisks: [],
                    externalvirtualdisks: []
                });
            }
            else {
                if (self.hasNoDisks()) {
                    angular.extend(self.setting.value, {
                        virtualdisks: self.hiddenDisks.internal,
                        externalvirtualdisks: self.hiddenDisks.external
                    });
                }
            }
            self.validateForm();
        };
        RaidConfigurationController.prototype.addVirtualDisk = function () {
            var self = this;
            self.setting.value.virtualdisks.push(self.defaultDisk());
            self.validateExternalDisks();
        };
        RaidConfigurationController.prototype.deleteVirtualDisk = function (raidconfig, virtualdisk) {
            var self = this;
            _.remove(raidconfig.virtualdisks, { id: virtualdisk.id });
            self.validateDisks();
        };
        RaidConfigurationController.prototype.deleteExternalVirtualDisk = function (raidconfig, virtualdisk) {
            var self = this;
            _.remove(raidconfig.externalvirtualdisks, { id: virtualdisk.id });
            self.validateExternalDisks();
        };
        RaidConfigurationController.prototype.addExternalVirtualDisk = function () {
            var self = this;
            self.setting.value.externalvirtualdisks.push(self.defaultDisk());
            self.validateForm();
        };
        RaidConfigurationController.prototype.validVirtualDisk = function (virtualdisk, type, index, skip) {
            var self = this;
            var raidconfig = self.setting.value;
            //if (virtualdisk == null) return true;
            //return true if we're not in basic mode
            if (raidconfig && raidconfig.raidtype != 'advanced')
                return true;
            var valid = true;
            if (!virtualdisk.disktype || !virtualdisk.disktype || !virtualdisk.numberofdisks || !virtualdisk.raidlevel || !virtualdisk.comparator)
                valid = false;
            else if (virtualdisk.numberofdisks <= 0)
                valid = false;
            else if (virtualdisk.raidlevel == 'raid0' && virtualdisk.numberofdisks < 1)
                valid = false;
            else if (virtualdisk.raidlevel == 'raid1' && (virtualdisk.numberofdisks != 2 || virtualdisk.comparator != 'exact'))
                valid = false;
            else if (virtualdisk.raidlevel == 'raid5' && virtualdisk.numberofdisks < 3)
                valid = false;
            else if (virtualdisk.raidlevel == 'raid6' && virtualdisk.numberofdisks < 4)
                valid = false;
            else if (virtualdisk.raidlevel == 'raid10' && (virtualdisk.numberofdisks < 4 || virtualdisk.numberofdisks % 2 != 0))
                valid = false;
            else if (virtualdisk.raidlevel == 'raid50' && (virtualdisk.numberofdisks < 6 || virtualdisk.numberofdisks % 3 != 0))
                valid = false;
            else if (virtualdisk.raidlevel == 'raid60' && virtualdisk.numberofdisks < 8)
                valid = false;
            else if ((virtualdisk.disktype == 'first' || virtualdisk.disktype == 'last') && ((index == 0 && virtualdisk.comparator != 'exact') || index > 0))
                valid = false; //can only be set on 1
            if (!skip) {
                self.validateForm();
            }
            return valid;
        };
        RaidConfigurationController.prototype.validateDisks = function () {
            var self = this, raidconfig = self.setting.value, valid = true;
            //mark form false
            if (raidconfig && raidconfig.raidtype == 'advanced' && (raidconfig.virtualdisks.length == 0 && raidconfig.externalvirtualdisks.length == 0))
                valid = false;
            if (valid && raidconfig && raidconfig.raidtype == 'basic' && raidconfig.basicraidlevel == null)
                valid = false;
            //only check virtual disks if we're in advanced mode
            if (raidconfig && raidconfig.raidtype == 'advanced' && valid) {
                valid = !_.find(raidconfig.virtualdisks, function (disk) {
                    return !self.validVirtualDisk(disk, 'internal', _.indexOf(raidconfig.virtualdisks, disk), true);
                });
            }
            return !valid;
        };
        RaidConfigurationController.prototype.validateExternalDisks = function () {
            var self = this, raidconfig = self.setting.value, valid = true;
            //mark form false
            if (raidconfig && raidconfig.raidtype == 'advanced' && (raidconfig.externalvirtualdisks.length == 0 && raidconfig.virtualdisks.length == 0))
                valid = false;
            if (raidconfig && raidconfig.raidtype == 'basic' && raidconfig.basicraidlevel == null)
                valid = false;
            //only check virtual disks if we're in advanced mode
            if (raidconfig && raidconfig.raidtype == 'advanced' && valid) {
                valid = !_.find(raidconfig.externalvirtualdisks, function (disk) {
                    return !self.validVirtualDisk(disk, 'external', _.indexOf(raidconfig.virtualdisks, disk), true);
                });
            }
            return !valid;
        };
        RaidConfigurationController.prototype.hasNoDisks = function () {
            var self = this;
            return self.setting.value.raidtype === "advanced" &&
                (!(self.setting.value.virtualdisks && self.setting.value.virtualdisks.length) &&
                    !(self.setting.value.externalvirtualdisks && self.setting.value.externalvirtualdisks.length));
        };
        RaidConfigurationController.prototype.validateForm = function () {
            var self = this;
            return !self.editMode || (self.validityObj.invalid = self.validateExternalDisks() || self.validateDisks() || self.hasNoDisks());
        };
        RaidConfigurationController.prototype.updateSetting = function () {
            var self = this;
            self.setting.value = {
                raidtype: 'basic',
                basicraidlevel: 'raid1',
                enableglobalhotspares: false,
                globalhotspares: 0,
                minimumssd: 0,
                enableglobalhotsparesexternal: false,
                globalhotsparesexternal: 0,
                minimumssdexternal: 0,
                virtualdisks: [],
                externalvirtualdisks: []
            };
            //self.addVirtualDisk();
            //self.addExternalVirtualDisk();
        };
        RaidConfigurationController.$inject = ['GlobalServices', 'constants'];
        return RaidConfigurationController;
    }());
    angular.module('app')
        .component('raidConfiguration', {
        templateUrl: 'views/raidconfiguration.html',
        controller: RaidConfigurationController,
        controllerAs: 'raidConfigurationController',
        bindings: {
            setting: '=',
            readOnlyMode: '<?',
            invalidArray: '=',
            form: "="
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=raidconfiguration.js.map
