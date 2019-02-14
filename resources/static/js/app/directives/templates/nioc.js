var asm;
(function (asm) {
    'use strict';
    var NIOCController = (function () {
        function NIOCController(GlobalServices, $translate) {
            this.GlobalServices = GlobalServices;
            this.$translate = $translate;
            this.editMode = true;
            this.validityObj = {
                id: this.GlobalServices.NewGuid(),
                invalid: false
            };
            this.propertyNames = ["management", "vmotion", "vsan", "vdp", "faultTolerance", "virtualMachine", "iSCSI", "nfs", "hbr"];
            var self = this;
            self.id = "setting_" + self.setting.id + "_" + (self.setting.category ? self.setting.category.id : '') + "_" + (self.component ? self.component.id : '');
            if (self.setting.readOnly || self.readOnlyMode) {
                self.editMode = false;
            }
            if (!self.invalidArray) {
                self.invalidArray = [];
            }
            if (!self.setting.value) {
                self.setting.value = {};
            }
            else {
                self.setting.value = angular.fromJson(self.setting.value);
            }
            self.refresh();
        }
        NIOCController.prototype.refresh = function () {
            var self = this;
            //push an object into invalidArray for each pool
            angular.forEach(self.propertyNames, function (propName) {
                var value = self.getPoolValue(propName);
                if (!angular.isNumber(value)) {
                    //set default value to its default if there is none
                    self.setPoolValue(propName, propName === "virtualMachine" ? 100 : 50);
                }
                if (self.setting.value.configure) {
                    //initialize invalid array object for this input so that it can be seen by parent
                    self.invalidArray
                        .push({ id: self.getInputName(propName), invalid: self.inputInvalid(propName) });
                }
                else {
                    //remove invalid array object for this input if there is one
                    _.remove(self.invalidArray, { id: self.getInputName(propName) });
                }
            });
        };
        NIOCController.prototype.$onDestroy = function () {
            var self = this;
            //clear all invalid array objects out before destroying (switching from distributed switch type to standard)
            self.setting.value.configure = false;
            self.refresh();
        };
        NIOCController.prototype.getInputName = function (propName) {
            return "value_" + propName + "_" + this.id;
        };
        //everything below this is only used if validation is used
        NIOCController.prototype.invalidKinds = function (poolValue) {
            //returns object with properties relating to whether an pool value is valid or not
            return {
                empty: poolValue == null || angular.isUndefined(poolValue),
                outOfBounds: (poolValue > 100 || poolValue < 1),
                min: poolValue < 1,
                max: poolValue > 100
            };
        };
        NIOCController.prototype.setPoolValue = function (propName, value) {
            var self = this;
            self.setting.value[propName] = value;
        };
        NIOCController.prototype.getPoolValue = function (propName) {
            var self = this;
            return self.setting.value[propName];
        };
        NIOCController.prototype.inputInvalid = function (propName) {
            var self = this;
            var value = self.getPoolValue(propName);
            var invalid = self.invalidKinds(value);
            return invalid.empty || invalid.outOfBounds;
        };
        NIOCController.prototype.getValidationMessage = function (propName) {
            //returns validation message to the view
            var self = this;
            var value = self.getPoolValue(propName);
            var invalid = self.invalidKinds(value);
            if (self.inputInvalid(propName)) {
                if (invalid.empty) {
                    return self.$translate.instant("GENERIC_RequiredField");
                }
                else if (invalid.outOfBounds) {
                    if (invalid.min) {
                        return self.$translate.instant("VALIDATIONMESSAGES_minNumber", { min: 1 });
                    }
                    else if (invalid.max) {
                        return self.$translate.instant("VALIDATIONMESSAGES_maxNumber", { max: 100 });
                    }
                }
            }
        };
        NIOCController.prototype.getInputStyle = function (propName) {
            //if it's invalid, mark it with red border, otherwise gray border
            //this prevents bootstrap from marking all under a .form-group.ng-invalid > .form-control with red border
            var self = this;
            if (self.form && self.form._submitted && self.inputInvalid(propName)) {
                return { "border": "1px solid #ce1126" };
            }
        };
        NIOCController.prototype.inputChanged = function (propName) {
            //if input is reqired, set its coresponding object in invalid array to its validity
            var self = this;
            _.find(self.invalidArray, { id: self.getInputName(propName) }).invalid = self.inputInvalid(propName);
        };
        NIOCController.prototype.checkboxToggled = function () {
            var self = this;
        };
        NIOCController.$inject = ['GlobalServices', "$translate"];
        return NIOCController;
    }());
    angular.module('app')
        .component('niocSetting', {
        templateUrl: 'views/nioc.html',
        controller: NIOCController,
        controllerAs: 'niocController',
        bindings: {
            setting: '=',
            readOnlyMode: '<?',
            component: '=?',
            category: '=?',
            form: '=?',
            invalidArray: "=?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=nioc.js.map
