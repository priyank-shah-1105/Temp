var asm;
(function (asm) {
    'use strict';
    var BasicSettingController = (function () {
        function BasicSettingController($rootScope) {
            this.$rootScope = $rootScope;
            this.editMode = true;
            var self = this;
            if (self.filterOverwrite()) {
                self.filteredoptions = self.filterOverwrite;
            }
            if (self.setting.readOnly || self.readOnlyMode) {
                self.editMode = false;
            }
            //self.setting.value is already set to a default value (sometimes -1, sometimes other values) by the backend so no need to set it again
            //if (self.setting.options.length > 0 && !self.setting.value) {
            //    var options = self.filteredoptions(self.setting, self.component);
            //    var possibleDefault = _.find(options, { id: '-1' });
            //    self.setting.value = possibleDefault ? possibleDefault.id : null;
            //}
            self.id = "setting_" + self.setting.id + "_" + (self.setting.category ? self.setting.category.id : '') + "_" + (self.component ? self.component.id : '') + "_";
            self.id += self.$rootScope.ASM.NewGuid();
            self.updateValueArray();
        }
        BasicSettingController.prototype.updateValueArray = function () {
            var self = this;
            self.setting.valuearray = self.getValueArray(self.setting);
            self.setting.valuearraydisplay = self.getValueArrayDisplay(self.setting);
        };
        BasicSettingController.prototype.filteredoptions = function (setting, component) {
            if (!setting || !component)
                return [];
            var returnVal = [];
            $.each(setting.options, function (optionIndex, option) {
                if (option.dependencyTarget && option.dependencyValue) {
                    var targetSetting = null;
                    $.each(component.categories, function (categoryIndex, c) {
                        var matchingSetting = _.find(c.settings, function (s) {
                            return (s.id === option.dependencyTarget);
                        });
                        if (matchingSetting) {
                            targetSetting = matchingSetting;
                            return;
                        }
                    });
                    var matchingValue = false;
                    if (targetSetting && targetSetting.value != null) {
                        var settingvalues = option.dependencyValue.split(',');
                        $.each(settingvalues, function (idx, val) {
                            if (val.toString() === targetSetting.value.toString())
                                matchingValue = true;
                        });
                    }
                    if (matchingValue) {
                        returnVal.push(option);
                    }
                }
                else {
                    returnVal.push(option);
                }
            });
            if (returnVal.length > 0 && !setting.multiple) {
                var valueExists = _.find(returnVal, function (s) {
                    return (s.id === setting.value);
                });
                if (!valueExists) {
                    setting.value = returnVal[0].id;
                }
            }
            return returnVal;
        };
        BasicSettingController.prototype.checkboxSettingClicked = function (id, evt) {
            var self = this;
            var value = self.setting.value;
            var tmpArray = (value == null || value === '') ? [] : value.split(',');
            if (evt.currentTarget.checked) {
                tmpArray.push(id);
            }
            else {
                tmpArray = _.without(tmpArray, id);
            }
            //    self.$timeout(function() {
            self.setting.value = tmpArray.join(',');
            //    }, 0);
            self.updateValueArray();
        };
        BasicSettingController.prototype.getValueArray = function (setting) {
            //if null return blank, if string split, if object return blank
            return setting.value == null ? '' :
                angular.isString(setting.value) ? setting.value.split(',') : '';
        };
        BasicSettingController.prototype.getValueArrayDisplay = function (setting) {
            var selectedoptions = setting.value == null ? [] :
                angular.isString(setting.value) ? setting.value.split(',') : [], returnval = [];
            if (setting.options.length) {
                angular.forEach(setting.options, function (c) {
                    if (selectedoptions.indexOf(c.id) !== -1)
                        returnval.push(c.name);
                });
            }
            return returnval.join(', ');
        };
        BasicSettingController.$inject = ['$rootScope'];
        return BasicSettingController;
    }());
    angular.module('app')
        .component('basicSetting', {
        templateUrl: 'views/basicsetting.html',
        controller: BasicSettingController,
        controllerAs: 'basicSetting',
        bindings: {
            setting: '=',
            readOnlyMode: '<?',
            component: '=?',
            category: '=?',
            form: '=?',
            filterOverwrite: "&"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=basicsetting.js.map
