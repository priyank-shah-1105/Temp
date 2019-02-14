angular.module('ASM.dataservices')
    .filter('uppercaseFirstLetter', function () {
        return function (val) {
            return val ? val.substring(0, 1).toUpperCase() + val.substring(1).toLowerCase() : "";
        };
    })
     .filter('scaleioStorageFilter', function () {
         return function (protectionDomains) {
             var a = [];
             _.forEach(protectionDomains, function (protectionDomain) {
                 if (protectionDomain.scaleIOStoragePools.length) {
                     a.push(protectionDomain);
                 }
             });
             return a;
         };
     })
    .filter('scaleioServerFilter', function () {
        return function (protectionDomains) {
            return _.filter(protectionDomains, function (protectionDomain) {
                return protectionDomain.scaleIOServerTypes.length;
            })
        };
    })
        .filter('clusterFilter', function () {
            return function (components) {
                return _.filter(components, function (component) {
                    return component.type == 'cluster' || component.type == 'scaleio';
                })
            };
        })
     .filter('byteConvert', function () {
         return function (size) {
             //using this function found on Stack™ to get a rounding to 2 decimals only when necessary
             function round(value, decimals) {
                 return Number(Math.round(value + 'e' + decimals) + 'e-' + decimals).toString();
             }
             var hrSize = "0";
             var k = size;
             var m = size / 1024;
             var g = size / 1048576;
             var t = size / 1073741824;
             var decimalPlaces = 2;

             if (t > 1) {
                 hrSize = round(t, decimalPlaces).concat(" TB");
             } else if (g > 1) {
                 hrSize = round(g, decimalPlaces).concat(" GB");
             } else if (m > 1) {
                 hrSize = round(m, decimalPlaces).concat(" MB");
             } else if (k > 1) {
                 hrSize = round(k, decimalPlaces).concat(" KB");
             }
             return hrSize;
         };
     })
    .filter('unique', [
        '$parse', function ($parse) {
            return function (input, filter) {
                if (angular.isArray(input)) {

                    //unique key
                    var getter = $parse(filter);

                    return _.uniq(input, function (elm) {
                        return getter(elm);
                    });
                }
                return input;
            };
        }
    ])
    .filter('lookup', function () {
        return function (list, key) {
            return list[key];
        };
    })
    .filter('ip2long', function () {
        return function (ipaddress) {
            var parts = ipaddress.split('.');
            var res = 0;

            res += (parseInt(parts[0], 10) << 24) >>> 0;
            res += (parseInt(parts[1], 10) << 16) >>> 0;
            res += (parseInt(parts[2], 10) << 8) >>> 0;
            res += parseInt(parts[3], 10) >>> 0;

            return res;
        };
    })
    .filter('ip2link', function () {
        return function (ipaddress) {
            var link = ipaddress;
            if (link != undefined && link !== '' && link.indexOf('http') !== 0) link = 'http://' + link;
            return link;
        };
    })
    .filter('toclassname', function () {
        return function (val) {

            if (!val) return '';

            return val.replace(/[^a-z0-9]/g, function (s) {
                var c = s.charCodeAt(0);
                if (c === 32) return '-';
                if (c >= 65 && c <= 90) return '_' + s.toLowerCase();
                return '__' + ('000' + c.toString(16)).slice(-4);
            });

        };
    })
    .filter('dependenciesMet', function () {
        return function (setting, component) {
            var returnVal = [];
            if (setting && component) {
                angular.forEach(setting, function (option) {
                    if (option.dependencyTarget && option.dependencyValue) {
                        var targetSetting = null;

                        angular.forEach(component.categories, function (c) {
                            var matchingSetting = _.find(c.settings, function (s) { return (s.id == setting.dependencyTarget); });

                            if (matchingSetting) {
                                targetSetting = matchingSetting;
                                return;
                            }
                        });

                        var matchingValue = false;

                        if (targetSetting && targetSetting.value != null) {
                            var settingvalues = option.dependencyValue.split(',');
                            angular.forEach(settingvalues, function (val) { if (val.toString() === targetSetting.value.toString()) matchingValue = true; });
                        }

                        if (matchingValue) returnVal.push(option);
                    } else {
                        returnVal.push(option);
                    }
                });
            }
            return returnVal;
        };
    })
    .filter('settingVisible', ["$filter", function ($filter) {
        var _isVisible = function (setting, component) {
            var isVisible = false;

            if (setting && component) {
                if (setting.dependencyTarget && setting.dependencyValue) {
                    var targetSetting = null;

                    angular.forEach(component.categories, function (c) {
                        var matchingSetting = _.find(c.settings, function (s) { return (s.id == setting.dependencyTarget); });

                        if (matchingSetting) {
                            targetSetting = matchingSetting;
                            return;
                        }
                    });

                    var matchingValue = false;

                    if (targetSetting && targetSetting.value != null) {
                        var settingvalues = setting.dependencyValue.split(',');
                        angular.forEach(settingvalues, function (val) { if (val.toString() === targetSetting.value.toString()) matchingValue = true; });
                    }

                    isVisible = (matchingValue && _isVisible(targetSetting, component));
                } else {
                    isVisible = true;
                }
            }

            return isVisible;
        };

        return function (settings, component) {
            var returnVal = [];
            angular.forEach(settings, function (setting) { if (_isVisible(setting, component)) returnVal.push(setting); });
            return returnVal;
        };
    }])
    .filter('settingsVisibleComponentEditorFilter', [function () {
        return function (settings, component) {
            var visible = function (setting, component) {
                if (!setting || !component) return true;

                if (setting.dependencyTarget && setting.dependencyValue) {
                    var targetSetting = null;

                    $.each(component.categories, function (ix, c) {
                        var matchingSetting = _.find(c.settings, function (s) { return (s.id == setting.dependencyTarget); });

                        if (matchingSetting) {
                            targetSetting = matchingSetting;
                            return;
                        }
                    });

                    var matchingValue = false;
                    if (targetSetting && targetSetting.value != null) {
                        var settingvalues = setting.dependencyValue.split(',');
                        $.each(settingvalues, function (idx, val) {
                            if (val.toString() === targetSetting.value.toString()) matchingValue = true;
                        });
                    }

                    return matchingValue && visible(targetSetting, component);
                }

                return true;
            }
            return _.filter(settings, function (setting) { return visible(setting, component) });
        }

    }])
    .filter('addExistingSettings', ["$filter", function ($filter) {
        return function (settings, component) {
            var visible = function (setting, component) {
                switch (setting.datatype) {
                    case 'raidconfiguration':
                    case 'biosconfiguration':
                    case 'networkconfiguration':
                        return false;
                }
                if (setting.hidefromtemplate) { return false };
                if (!setting || !component) return true;

                if (setting.dependencyTarget && setting.dependencyValue) {
                    var targetSetting = null;

                    $.each(component.categories, function (ix, c) {
                        var matchingSetting = _.find(c.settings, function (s) {
                            return (s.id == setting.dependencyTarget);
                        });

                        if (matchingSetting) {
                            targetSetting = matchingSetting;
                            return;
                        }
                    });

                    var matchingValue = false;

                    if (targetSetting && targetSetting.value != null) {
                        var settingvalues = setting.dependencyValue.split(',');
                        $.each(settingvalues, function (idx, val) {
                            if (val.toString() == targetSetting.value.toString())
                                matchingValue = true;
                        });
                    }
                    return matchingValue && visible(targetSetting, component);
                }

                return true;
            }

            return _.filter(settings, function (setting) { return visible(setting, component) });
        }

    }])
    .filter('serviceComponentFilter', ["$filter", function ($filter) {
        return function (settings, component) {
            var visible = function (setting, component) {
                var targetSetting, matchingValue;

                if (!setting.requireatdeployment) { return false };

                if (!setting || !component) return true;

                if (setting.dependencyTarget && setting.dependencyValue) {

                    _.find(component.categories, function (c) {
                        return targetSetting = _.find(c.settings, { id: setting.dependencyTarget });
                    });


                    if (targetSetting && targetSetting.value != null) {
                        matchingValue = !!_.find(_.split(setting.dependencyValue, ","), function (val) {
                            return val.toString() == targetSetting.value.toString();
                        });
                    }

                    return matchingValue && visible(targetSetting, component);
                }
                return true;
            }

            return _.filter(settings, function (setting) { return visible(setting, component) });
        }

    }])

 .filter('updateComponents', ["$filter", function ($filter) {
     return function (settings, component) {
         var visible = function (setting, component) {
             var targetSetting = null, matchingValue = false;

             if (setting.hidefromtemplate) { return false };
             if (!setting || !component) return true;

             if (setting.dependencyTarget && setting.dependencyValue) {
                 _.find(component.categories,
                     function (c) {
                         targetSetting = _.find(c.settings, { id: setting.dependencyTarget }) || null;
                         return targetSetting
                     });

                 if (targetSetting && targetSetting.value != null) {
                     var settingvalues = setting.dependencyValue.split(',');
                     matchingValue = !!_.find(settingvalues, function (val) {
                         return (val.toString() == targetSetting.value.toString())
                     });
                 }
                 return matchingValue && visible(targetSetting, component);
             }
             return true;
         }

         return _.filter(settings, function (setting) { return visible(setting, component) });
     }

 }])

 .filter('viewTemplateDetailsFilter', ["$filter", function ($filter) {
     return function (settings, component) {
         var visible = function (setting, component) {
             var targetSetting,
                 matchingValue;

             if (!setting || !component) return true;

             //if (setting.hidefromtemplate || component.type === 'application') return false;

             if (setting.dependencyTarget && setting.dependencyValue) {

                 _.find(component.categories,
                     function (c) {
                         return targetSetting = _.find(c.settings, { id: setting.dependencyTarget });
                     });

                 if (targetSetting && targetSetting.value != null) {
                     matchingValue = !!_.find(_.split(setting.dependencyValue, ","),
                         function (val) {
                             return val.toString() == targetSetting.value.toString();
                         });
                 }
                 return matchingValue && visible(targetSetting, component);
             }

             return true;
         }
         return _.filter(settings, function (setting) { return visible(setting, component) });
     }

 }])
 .filter('addApplicationSettingsFilter', ["$filter", function ($filter) {
     return function (settings, component) {
         var visible = function (setting, component) {
             if (!setting || !component) return true;

             if (setting.dependencyTarget && setting.dependencyValue) {
                 var targetSetting = null;

                 $.each(component.categories, function (ix, c) {
                     var matchingSetting = _.find(c.settings, function (s) {
                         return (s.id == setting.dependencyTarget);
                     });

                     if (matchingSetting) {
                         targetSetting = matchingSetting;
                         return;
                     }
                 });

                 var matchingValue = false;

                 if (targetSetting && targetSetting.value != null) {
                     var settingvalues = setting.dependencyValue.split(',');
                     $.each(settingvalues, function (idx, val) {
                         if (val.toString() == targetSetting.value.toString())
                             matchingValue = true;
                     });
                 }

                 return matchingValue && visible(targetSetting, component);
             }

             return true;
         }
         var settingsNotHiddenFromTemplate = $filter("templatesettings")(settings);
         return _.filter(settingsNotHiddenFromTemplate, function (setting) { return visible(setting, component) });
     }

 }])
    .filter('vdsTemplateSettingsFilter', ["$filter", function ($filter) {
        return function (settings) {
            var visible = function (setting, settingsList) {
                var targetSetting = null;
                if (setting && settingsList && settingsList.length && setting.dependencyTarget && setting.dependencyValue) {

                    targetSetting = _.find(settingsList, { id: setting.dependencyTarget }) || null;

                    var matchingValue = false;

                    if (targetSetting && targetSetting.value != null) {
                        var settingvalues = setting.dependencyValue.split(',');
                        matchingValue = !!_.find(settingvalues, function (val) {
                            return (val.toString() == targetSetting.value.toString())
                        });
                    }
                    return matchingValue && visible(targetSetting, settingsList);
                }
                return true;
            }
            var settingsNotHiddenFromTemplate = $filter("templatesettings")(settings);
            return _.filter(settingsNotHiddenFromTemplate, function (setting) { return visible(setting, settings) });
        }

    }])

 .filter('componentEditorLinkableType', function () {
     return function (array, componentType) {
         var k = _.filter(array,
             function (component) {
                 switch (componentType) {
                     case 'application':
                         return false;
                     case 'vm':
                         return component.type == 'cluster';
                     case 'cluster':
                     case 'scaleio':
                         return component.type == 'server' || component.type == 'vm';
                     case 'server':
                         return component.type == 'storage' || component.type == 'cluster' || component.type == 'scaleio';
                     case 'storage':
                         return component.type == 'server';
                 }
             });
         return k;
     };
 })

    .filter('networkVisible', function () {
        return function (listOfNetworks, network) {
            return _.filter(listOfNetworks,
                function (networkListItem) {
                    if (network === "PUBLIC_LAN" || network === "PRIVATE_LAN") {
                        if (networkListItem.typeid === "PUBLIC_LAN" || networkListItem.typeid === "PRIVATE_LAN") {
                            return true;
                        }
                    }
                    return networkListItem.typeid === network || networkListItem.id === "new" || angular.isUndefined(networkListItem.id);
                });
        };
    })
    .filter('deviceHealth', [
        'constants', function (constants) {
            return function (id) {
                var match = _.find(constants.deviceHealth, { id: id }) || { name: '' };
                return match.name;
            };
        }
    ])
    //.filter('filteredDevices', [
    //    function () {
    //        return function (arr) {
    //            var ret = [];
    //            $.each(arr, function (index, device) {
    //                if (device.isChassis && device.chassisConfiguration.configChassis) {
    //                    ret.push(device);
    //                }
    //            });
    //            return ret;
    //        };
    //    }
    //])
    .filter('uplinkList', [
        function () {
            return function (arr, vltenabled) {
                var x = angular.copy(arr);
                if (vltenabled)
                    x.unshift({
                        id: 'VLT',
                        uplinkId: 'VLT',
                        uplinkName: 'VLT',
                        portChannel: '',
                        networks: [],
                        networkNames: []
                    });

                return x;
            };
        }
    ])
      .filter('noUpdate', ["$translate", function ($translate){
            return function (str) {
                if (!str) {
                    return $translate.instant("GENERIC_NoUpdate");
                }
                
                return str;
            };
        }
      ])
    .filter('deviceType', [
        'constants', function (constants) {
            return function (id) {
                var match = _.find(constants.deviceTypes, { id: id }) || { name: '' };
                return match.name;
            };
        }
    ])
    .filter('firmwareStatus', [
            'constants', function (constants) {
                return function (id) {
                    var match = _.find(constants.firmwareStatus, { id: id }) || { name: '' };
                    return match.name;
                };
            }
    ])
    .filter('firmwareStatusIcon', [
                'constants', function (constants) {
                    return function (id) {
                        var match = _.find(constants.firmwareStatus, { id: id }) || { icon: '' };
                        return match.icon;
                    };
                }
    ])
    .filter('deviceState', [
            'constants', function (constants) {
                return function (id) {
                    var match = _.find(constants.deviceState, { id: id }) || { name: '' };
                    return match.name;
                };
            }
    ])
    .filter('deviceManagementState', [
                'constants', function (constants) {
                    return function (id) {
                        var match = _.find(constants.resourceStateFilter, { id: id }) || { name: '' };
                        return match.name;
                    };
                }
    ])
    .filter('staticIPState', [
            'constants', function (constants) {
                return function (id) {
                    var match = _.find(constants.staticIPState, { id: id }) || { name: '' };
                    return match.name;
                };
            }
    ])
    .filter('availableResourceTypes', [
            'constants', function (constants) {
                return function (id) {
                    var match = _.find(constants.availableResourceTypes, { id: id }) || { name: '' };
                    return match.name;
                };
            }
    ])
    .filter('availableManagedStates', [
            'constants', function (constants) {
                return function (id) {
                    var match = _.find(constants.availableManagedStates, { id: id }) || { name: '' };
                    return match.name;
                };
            }
    ])
    .filter('healthToColor', [
        'Enum', function (Enum) {
            return function (health) {
                return Enum.healthToColor(health);
            };
        }
    ])
    .filter('healthToColorText', [
        'Enum', function (Enum) {
            return function (health) {
                return Enum.healthToColorText(health);
            };
        }
    ])
    .filter('serviceState', [
        'Enum', function (Enum) {
            return function (state) {
                return Enum.serviceState(state);
            };
        }
    ])
    .filter('StatusToText', [
        'Enum', function (Enum) {
            return function (status) {
                return Enum.StatusToText(status);
            };
        }
    ])
    .filter('deviceStatusToText', [
        'Enum', function (Enum) {
            return function (devicestatus) {
                return Enum.deviceStatusToText(devicestatus);
            };
        }
    ])
    .filter('devicePowerStateToText', [
        'Enum', function (Enum) {
            return function (powerstate) {
                return Enum.devicePowerStateToText(powerstate);
            };
        }
    ])
    .filter('slaToText', [
        'Enum', function (Enum) {
            return function (sla) {
                return Enum.slaToText(sla);
            };
        }
    ])
    .filter('serviceDetailsHealthIcon', [
        'Enum', function (Enum) {
            return function (health) {
                return Enum.serviceDetailsHealthIcon(health);
            };
        }
    ])
    .filter('sum', [
        function () {
            return function (items, prop) {
                return items.reduce(function (a, b) {
                    return a + b[prop];
                }, 0);
            };
        }
    ])
    .filter('ellipsis', [function () {
        return function (value, limit) {
            var returnVal = value;
            if (value.length > limit) {
                returnVal = value.substring(0, limit) + '...';
            }
            return returnVal;
        };
    }
    ])
    .filter('able', ["$translate", function ($translate) {
        return function (bool) {
            if (bool === true || bool === "true") {
                return $translate.instant('SETTINGS_Enabled');
            }
            if (bool === false || bool === "false") {
                return $translate.instant('SETTINGS_Disabled');
            }
            return $translate.instant('DEVICETYPE_Unknown');
        }
    }
    ])
    .filter('range', [function () {
        return function (input, min, max) {
            min = parseInt(min); //Make string input int
            max = parseInt(max);
            for (var i = min; i < max; i++) {
                if (i < 10) { i = "0" + i; }
                input.push(i.toString());
            }

            return input;
        };
    }
    ])
    .filter('percentage', [function () {
        return function (input) {
            var x = input.toFixed(2);
            return x + "%";
        };
    }])
    .filter('bool', ['$translate', function ($translate) {
        return function (bool) {
            return bool ? $translate.instant('SETTINGS_Repositories_True') :
                $translate.instant('SETTINGS_Repositories_False');
        };
    }
    ])
    .filter('yesNo', ['$translate', function ($translate) {
        return function (bool) {
            return bool ? $translate.instant('GENERIC_Yes') :
                $translate.instant('GENERIC_No');
        };
    }
    ])
    .filter('repoState', ['$translate', function ($translate) {
        return function (status) {
            switch (status) {
                case 'errors':
                    return $translate.instant('SETTINGS_Repositories_Error');
                case 'pending':
                    return $translate.instant('SETTINGS_Repositories_Pending');
                case 'copying':
                    return $translate.instant('SETTINGS_Repositories_Copying');
                case 'available':
                    return $translate.instant('SETTINGS_Repositories_Available');

            }
        }
    }])
.filter('deviceState', ['$translate', function ($translate) {
    return function (status) {
        switch (status) {
            case "poweringoff":
                return $translate.instant("TEMPLATEBUILDER_VALIDATE_PoweringOff");
            case "available":
                return $translate.instant("GENERIC_Available");
            case "reserved":
                return $translate.instant("TEMPLATEBUILDER_VALIDATE_Reserved");
            case "unknown":
                return $translate.instant("GENERIC_Unknown");
            case "unmanaged":
                return $translate.instant("TEMPLATEBUILDER_VALIDATE_Unmanaged");
            case "pending":
                return $translate.instant("GENERIC_Pending");

        }
    }
}])

.filter('jobState', ['$translate', function ($translate) {
    return function (status) {
        switch (status) {
            case 'running':
                return $translate.instant('GENERIC_Running');
            case 'scheduled':
                return $translate.instant('GENERIC_Scheduled');
            case 'completed':
                return $translate.instant('GENERIC_Completed');
            case 'error':
                return $translate.instant('GENERIC_Error');
        }
    }
}])
.filter('serviceHealth', ['$translate', function ($translate) {
    return function (health) {
        switch (health) {
            case 'green':
                return $translate.instant("GENERIC_Healthy");
            case 'yellow':
                return $translate.instant("GENERIC_Warning");
            case 'red':
                return $translate.instant("GENERIC_Critical");
            case 'pending':
                return $translate.instant("GENERIC_Pending");
            case 'unknown':
                return $translate.instant("GENERIC_InProgress");
            case 'cancelled':
                return $translate.instant("GENERIC_Cancelled");
            case 'incomplete':
                return $translate.instant("GENERIC_Incomplete");
            case 'servicemode':
                return $translate.instant("GENERIC_ServiceMode");
        }
    }
}])
    .filter('resourceHealth', ['$translate', function ($translate) {
        return function (health) {
            switch (health) {
                case 'green':
                    return $translate.instant("GENERIC_Healthy");
                case 'yellow':
                    return $translate.instant("GENERIC_Warning");
                case 'red':
                    return $translate.instant("GENERIC_Critical");
                case 'cancelled':
                    return $translate.instant("GENERIC_Cancelled");
                case 'pending':
                    return $translate.instant("GENERIC_Pending");
                case 'servicemode':
                    return $translate.instant("GENERIC_ServiceMode");
                case 'blue':
                case 'unknown':
                case '':
                    return $translate.instant("GENERIC_InProgress");
            }
        }
    }])
    .filter("onOff", ['$translate', function ($translate) {
        return function (bool) {
            return bool ? $translate.instant('GENERIC_On_') :
                $translate.instant('CONFIGURECHASSIS_DEVICE_CONFIG_Off');
        };
    }
    ])
    .filter("compliant", ['$translate', function ($translate) {
        return function (compliance) {
            switch (compliance) {
                case "compliant":
                    return $translate.instant("DEVICES_CompliantStatus_Compliant");
                case "noncompliant":
                    return $translate.instant("DEVICES_CompliantStatus_NonCompliant");
                case "updaterequired":
                    return $translate.instant("DEVICES_CompliantStatus_UpdateRequired");
                case "updatefailed":
                    return $translate.instant("DEVICES_CompliantStatus_UpdateFailed");
                case "updating":
                    return $translate.instant("DEVICES_CompliantStatus_PendingUpdates");
                default:
                    return $translate.instant("DEVICES_CompliantStatus_Unknown");
            }
        }
    }
    ])
    .filter('templatesettings', [function () {
        return function (arr, isService) {
            var x = [];
            $.each(arr, function (index, model) {
                if (!isService) {
                    if ((!model.hidefromtemplate) && !model.isDisposed)
                        x.push(model);
                } else {
                    if ((!model.hidefromtemplate || model.requireatdeployment) && !model.isDisposed)
                        x.push(model);
                }
            });

            return x;
        }
    }])

    .filter('deploysettings', [function () {
        return function (arr) {
            var x = [];
            $.each(arr, function (index, model) {
                if (model.requireatdeployment)
                    x.push(model);
            });

            return x;
        }
    }])
.filter('groupFilter', [function () {
    return function (array) {
        return _.uniqBy(array, 'group');
    }
}])
    .filter('criticality', ['constants', function (constants) {
        return function (constant) {
            return constants.firmwareCriticality.filter(function (option) { return option.id === constant })[0].name;
        }
    }])
    .filter('isTypeChassis', [function () {
        return function (device) {
            return !!_.find(["ChassisM1000e", "ChassisVRTX", "ChassisFX"],
            function (val) { return val === device.resourceType });

        }
    }])
    .filter('filteredDevices', ["$filter", function ($filter) {
        return function (devices) {
            return $filter('filter')(devices,
                function (d) {
                    return $filter("isTypeChassis")(d) && d.chassisConfiguration.configChassis === true;
                });
        }
    }])
    .filter('policyFilter', ['constants', '$filter', function (constants, $filter) {
        return function (policies, devices) {
            var fxcount = 0,
                m1000count = 0,
                devices = $filter("filteredDevices")(devices),
                returnArray = [];

            angular.forEach(devices, function (device) {
                if (device.chassisConfiguration && device.chassisConfiguration.configChassis === true) {
                    if (device.resourceType === 'ChassisFX') {
                        fxcount++;
                    } else {
                        m1000count++;
                    }
                }
            });

            if (fxcount > 0 && m1000count > 0) {
                returnArray = policies;
            } else if (fxcount > 0) {
                returnArray.push(policies[2]);
            } else {
                returnArray.push(policies[3]);
            }
            return returnArray;
        }
    }])

    .filter('orderComponents', [function () {
        return function (components) {
            return _.flatten(
                 _.map(["vm", "cluster", "scaleio", "server", "storage"],
                     function (type) {
                         return _.filter(components, { type: type });
                     })
             );
        }
    }])

      .filter('sortConfigureDevices', ["$filter", function ($filter) {
          return function (devices) {
              //sorts into 1. Chassis, 2. Servers, 3. Other 
              var devicesCopy = angular.copy(devices);

              var chassis = _.remove(devicesCopy,
                  function (device) { return (device.chassisId || device.id) && $filter("isTypeChassis")(device) });

              var servers = _.remove(devicesCopy, function (device) { return device.resourceType.indexOf('Server') >= 0; });

              var other = devicesCopy;
              return _.concat([],
                    chassis,
                    servers,
                    other
                  );
          }
      }])

    .filter('translateLabel', ['$translate', function ($translate) {
        return function (id, category, name) {
            var nameToTry = "FORM_BUILDER_LABELS_" + category + "_" + id,
                    translation = $translate.instant(nameToTry);
            return translation === nameToTry ? name : translation;
        }
    }])

    .filter('titleCase', [function () {
        return function (input) {
            return input ? input.replace(/\w\S*/g, function (txt) { return txt.charAt(0).toUpperCase() + txt.substr(1) }) : '';
        }
    }])


    .directive('stringToNumber', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, ngModel) {
                ngModel.$parsers.push(function (value) {
                    return '' + value;
                });
                ngModel.$formatters.push(function (value) {
                    return parseFloat(value, 10);
                });
            }
        };
    })
    .constant('donutChartConfig', {



        navigator: { enabled: false },
        credits: { enabled: false },
        exporting: { enabled: false },

        options: {
            chart: {
                events: {
                    load: function () {
                        $('.highcharts-container').css('z-index', '12');
                        $('.highcharts-background').attr('fill', 'none');
                    }
                }
            },
            tooltip: {
                backgroundColor: '#ffffff'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false
                    }
                }
            }
        },

        title: {
            text: '',
            verticalAlign: 'bottom'
        },
        size: {
            width: 250,
            height: 200
        },
        series: [
            {
                type: 'pie',
                innerSize: '95%',
                name: '',
                data: []
            }
        ]
    })
.factory('GlobalServices', [
    '$rootScope', 'localStorageService', '$timeout', '$window', '$q', '$http', 'URLs', '$translate', '$filter', '$location', '$injector', '$resource', 'Enums', '$route', 'Commands', '$anchorScroll',
    function ($rootScope, localStorage, $timeout, $window, $q, $http, URLs, $translate, $filter, $location, $injector, $resource, Enums, $route, Commands, $anchorScroll) {
        $rootScope.ASM = this;

        $rootScope.ASM.showInitialSetup = false;

        $rootScope.ASM.CurrentUser = null;

        $rootScope.ASM.Enums = Enums;

        $rootScope.ASM.IsInRole = function (role) {
            if ($rootScope.ASM.CurrentUser == null) {
                var currentUser = localStorage.get('ASM.currentUserObject');

                //var currentUser = sessionStorage.getItem('ASM.currentUserObject');
                //currentUser = JSON.parse(currentUser);
                $rootScope.ASM.CurrentUser = angular.copy(currentUser);
            }
            return ($rootScope.ASM.CurrentUser && $rootScope.ASM.CurrentUser.roleId && $rootScope.ASM.CurrentUser.roleId.toLowerCase() === role.toLowerCase());
        };

        $rootScope.ASM.NewGuid = function () {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        };

        $rootScope.ASM.showHelp = function (helptoken) {

            //if null check to see if one was set prior to the call
            if (!helptoken) helptoken = $rootScope.helpToken;

            //if still no help token set to none
            var helpToken = helptoken || 'none';

            var helpUrl = Commands.help[helpToken];

            var width = 820;
            var height = 600;

            if (height > screen.height) {
                height = screen.height - 20;
            }

            var left = Math.round((screen.width - width) / 2);
            var top = Math.round((screen.height - height) / 2);

            var winHelp = window.open(helpUrl, 'winHelp', 'width=' + width + ',height=' + height + ',top=' + top + ',left=' + left + ',scrollbars=1,resizable=1,location=1,toolbar=1');
            if (winHelp.focus) {
                winHelp.focus();
            }
        };

        $rootScope.ASM.resetCache = function () {
            $rootScope.ASM.cache = {
                devices: [],
                templates: [],
                services: []
            };
        }
        $rootScope.ASM.resetCache();

        $rootScope.ASM.servicesData = {
            healthy: 0,
            warning: 0,
            error: 0,
            pending: 0,
            inprogress: 0,
            cancelled: 0,
            incomplete: 0,
            servicemode: 0
        };

        $rootScope.ASM.loadNavigation = function (serviceData) {

            return [
                {
                    guid: 'nav_dashboard_home',
                    label: $translate.instant('DASHBOARD_Title'),
                    icon: 'ci-grid-dashboard',
                    href: '#/home',
                    children: [
                    ]
                },
                {
                    guid: 'nav_services',
                    label: $translate.instant('GENERIC_Services'),
                    icon: 'ci-deploy',
                    href: '#/services',
                    children: [
                        {
                            guid: 'nav_services_healthy', label: $translate.instant('GENERIC_Healthy') + ' (' + serviceData.healthy + ')',
                            href: '/services/green', disabled: serviceData.healthy >= 1 ? false : true
                        },
                        {
                            guid: 'nav_services_warning', label: $translate.instant('GENERIC_Warning') + ' (' + serviceData.warning + ')',
                            href: '/services/yellow', disabled: serviceData.warning >= 1 ? false : true
                        },
                        {
                            guid: 'nav_services_error', label: $translate.instant('GENERIC_Critical') + ' (' + serviceData.error + ')',
                            href: '/services/red', disabled: serviceData.error >= 1 ? false : true
                        },
                        {
                            guid: 'nav_services_pending', label: $translate.instant('GENERIC_Pending') + ' (' + serviceData.pending + ')',
                            href: '/services/pending', disabled: serviceData.pending >= 1 ? false : true
                        },
                        {
                            guid: 'nav_services_inprogress', label: $translate.instant('GENERIC_InProgress') + ' (' + serviceData.inprogress + ')',
                            href: '/services/unknown', disabled: serviceData.inprogress >= 1 ? false : true
                        },
                        {
                            guid: 'nav_services_cancelled', label: $translate.instant('GENERIC_Cancelled') + ' (' + serviceData.cancelled + ')',
                            href: 'services/cancelled', disabled: serviceData.cancelled >= 1 ? false : true
                        },
                        {
                            guid: 'nav_services_incomplete', label: $translate.instant('GENERIC_Incomplete') + ' (' + serviceData.incomplete + ')',
                            href: 'services/incomplete', disabled: serviceData.incomplete >= 1 ? false : true
                        },
                        {
                            guid: 'nav_services_servicemode', label: $translate.instant('GENERIC_ServiceMode') + ' (' + serviceData.servicemode + ')',
                            href: '/services/servicemode', disabled: serviceData.servicemode >= 1 ? false : true
                        }

                        //{ guid: 'nav_services_cancelled', label: $translate.instant('GENERIC_Cancelled') + ' (' + serviceData.cancelled + ')', href: '/services/Cancelled', disabled: serviceData.cancelled >= 1 ? false : true },
                        //{ guid: 'nav_services_error', label: $translate.instant('GENERIC_Critical') + ' (' + serviceData.error + ')', href: '/services/Critical', disabled: serviceData.error >= 1 ? false : true },
                        //{ guid: 'nav_services_healthy', label: $translate.instant('GENERIC_Healthy') + ' (' + serviceData.healthy + ')', href: '/services/Healthy', disabled: serviceData.healthy >= 1 ? false : true },
                        //{ guid: 'nav_services_inprogress', label: $translate.instant('GENERIC_InProgress') + ' (' + serviceData.inprogress + ')', href: '/services/In Progress', disabled: serviceData.inprogress >= 1 ? false : true },
                        //{ guid: 'nav_services_warning', label: $translate.instant('GENERIC_Warning') + ' (' + serviceData.warning + ')', href: '/services/Warning', disabled: serviceData.warning >= 1 ? false : true }
                    ]
                },
                {
                    guid: 'nav_templates',
                    label: $translate.instant('NAVIGATION_Templates'),
                    href: '#/templates',
                    icon: 'ci-device-templates-blank-stacked',
                    children: [
                        { guid: 'nav_templates_mytemplates', label: $translate.instant('NAVIGATION_MyTemplates'), href: '/templates/mytemplates' },
                        { guid: 'nav_templates_sampletemplates', label: $translate.instant('NAVIGATION_SampleTemplates'), href: '/templates/sampletemplates' }
                    ]
                },
                {
                    guid: 'nav_resources',
                    label: $translate.instant('NAVIGATION_Resources'),
                    href: '#/devices',
                    icon: 'ci-device-tower',
                    children: [
                        { guid: 'nav_resources_allresources', label: $translate.instant('NAVIGATION_AllResources'), href: '/devices' },
                        { guid: 'nav_resources_serverpools', label: $translate.instant('NAVIGATION_ServerPools'), href: '/devices/serverpools' }
                    ]
                }
                //,                {
                //    guid: 'nav_settings',
                //    label: $translate.instant('Settings'),
                //    href: '/settings',
                //    icon: 'ci-settings-sliders-vert-2',
                //    hideintopmenu: true,
                //    pinright: true,
                //    children: [
                //        { guid: 'nav_settings_addonmodules', label: $translate.instant('SETTINGS_AddOnModule'), href: '/settings/AddOnModule' },
                //        { guid: 'nav_settings_backuprestore', label: $translate.instant('SETTINGS_BackupRestore'), href: '/settings/BackupAndRestore' }
                //    ]
                //}
            ];
        }

        $rootScope.ASM.navigation = $rootScope.ASM.loadNavigation($rootScope.ASM.servicesData);

        $rootScope.$watch('ASM.servicesData', function (serviceData) {
            $rootScope.ASM.navigation = $rootScope.ASM.loadNavigation(serviceData);
            $rootScope.$broadcast('navigationUpdated', $rootScope.ASM.navigation);
        });



        //$rootScope.ASM.showMenuItems = function () {
        //    $rootScope.$broadcast('navigationUpdated', $rootScope.ASM.navigation);
        //};

        //$rootScope.ASM.hideMenuItems = function () {
        //    $rootScope.$broadcast('navigationUpdated', []);
        //};



        $rootScope.ASM.ClearErrors = function (errorArray) {

            if (errorArray)
                errorArray.splice(0, errorArray.length);
            else
                errorArray = [];

            return errorArray;
        };

        $rootScope.ASM.RemoveError = function (error, array) {
            var errorArray = array || angular.extend([], $rootScope.errors);
            angular.forEach(errorArray, function (e, index) {
                if (e.refId && e.refId === error.refId) {
                    errorArray.splice(index, 1);
                }
                else if (e.id && e.id === error.id) errorArray.splice(index, 1);
            });

            $rootScope.errors = errorArray;
        };

        $rootScope.ASM.DisplayError = function (error, errorArray) {

            if (!error) return;
            if (!errorArray) errorArray = $rootScope.errors;

            //convert old style errors to new style
            if (error.errorMessage) {

                if (error.errorMessage) {
                    error.message = error.errorMessage;
                }

                if (error.errorAction) {
                    error.details = (error.details || '') + error.errorAction;
                }

                if (error.errorDetails) {
                    if (error.details) { error.details += '<br />'; }
                    error.details = error.errorDetails;
                }

                if (error.errorCode) {
                    error.code = error.errorCode;
                } else {
                    error.code = -1;
                }

                //copy fldErrors to error.errors collection for errorDisplay directive
                error.fldErrors = error.fldErrors || undefined;
                if (error.fldErrors) {
                    angular.forEach(error.fldErrors, function (suberror) {
                        if (suberror.errorAction) {
                            if (suberror.errorDetails) {
                                suberror.errorAction += ('<br />' + suberror.errorDetails);
                            }
                        }
                        var fldError =
                        {
                            message: suberror.errorMessage || null,
                            details: suberror.errorAction || suberror.errorDetails || null,
                            code: suberror.errorCode || null,
                            severity: suberror.errorSeverity || 'CRITICAL'
                        };

                        
                        if (!error.errors)
                            error.errors = [];
                        error.errors.push(fldError);
                    });
                }

                error.Message = '' + error.code + ': ' + error.message;
                //  error.refId = '' + response.config.url;
            }

            angular.forEach(errorArray, function (e, index) {
                if (e.refId === error.refId)
                    errorArray.splice(index, 1);
            });

            error.id = $rootScope.ASM.NewGuid();
            error.errors = error.errors || [];
            errorArray.push(error);
        };

        $rootScope.ASM.findInvalidElements = function (id) {
            return $("#" + id)
                .find(".ng-invalid")
                .not("ng-form, form, [ng-form]");
        }

        $rootScope.ASM.hasInvalidElement = function (id) {
            return $rootScope.ASM.findInvalidElements(id).length;
        }

        $rootScope.ASM.scrollToInvalidElement = function (id) {
            //form elements must have an id to be scrolled to 
            var invalidElement = $rootScope.ASM.findInvalidElements(id).first();
            if (invalidElement.length) {
                // open parent collapsed rows of the input that are closed
                var panels = $(invalidElement.parents()).filter(".collapse").not(".in"),
                    completedCollapses = 0;
                if (panels.length) {
                    angular.forEach(_.reverse(panels),
                   function (panel) {
                       var panelElement = $("#" + $(panel).attr("id"));
                       panelElement.collapse("show");
                       panelElement.on("shown.bs.collapse", function (event) {
                           event.preventDefault();
                           completedCollapses++;
                           if (completedCollapses === panels.length) {
                               //all panels done collapsing
                               $rootScope.ASM.scrollTo(invalidElement.attr("id"));
                           }
                           panelElement.off("shown.bs.collapse");
                       })
                   });
                    //scroll to invalid element

                } else $rootScope.ASM.scrollTo(invalidElement.attr("id"));
            }
            return !!invalidElement.length;
        }

        $rootScope.ASM.scrollTo = function (id) {
            $timeout(function () {
                $anchorScroll(id);
            })

        }

        $rootScope.ASM.scrollToCollapsableRowTitle = function (idOfTitle) {
            //wait for collapsing row to close, then only scroll to it if it's opening and not closing
            $timeout(function () {
                $("#" + idOfTitle).find(".collapsed")[0] || $rootScope.ASM.scrollTo(idOfTitle);
            }, 500);
        }

        $rootScope.ASM.getBaseName = function (name) {
            var pat = /\s\([0-9]*\)$/, // Match the pattern like: (7)
                match = name.match(pat);
            return match ? name.replace(match, '') : name;
        }

        $rootScope.ASM.namePicker = function (namedArray, unNamedArray, baseName) {
            //namedArray is array of all components of same type as in unNamedArray
            var defaultName = $rootScope.ASM.getBaseName(unNamedArray[0].name).toLowerCase();
            //if default name
            var nameToTry = "", i = (defaultName === unNamedArray[0].type) ? _.filter(namedArray, function (name) { _.startsWith(name, unNamedArray[0].type) }).length + 1 : 1;
            _.map(unNamedArray, function (unNamedItem) {
                var originalName = unNamedItem.name,
                    done;
                while (!done) {
                    nameToTry = i > 1 ? baseName + " (" + i + ")" : baseName;
                    if (!(_.find(namedArray, { name: nameToTry }) ||
                        _.find(unNamedArray, function (unNamed) {
                            return unNamed.name === nameToTry && unNamed.id !== unNamedItem.id
                    }))) {
                        //untaken name found
                        unNamedItem.name = nameToTry;
                        return done = true;
                    }
                    i++;
                }
            });
            return unNamedArray;
        }

        $rootScope.ASM.stringifyCategories = function (categories) {
            return angular.forEach(categories, function (category) {
                angular.forEach(category.settings, function (setting) {
                    if (angular.isString(setting.value)) { return }
                    else if (setting.value === null || angular.isUndefined(setting.value)) { return }
                    else if (!angular.isString(setting.value)) { return setting.value = JSON.stringify(setting.value) }
                    else if (setting.value === true) { return setting.value = "true" }
                    else if (setting.value === false) { return setting.value = "false" }
                })
            });
        }

        $rootScope.ASM.setTab = function (tabgroup, tabname) {
            var params = $route.current.params;
            if (tabgroup && tabname) {
                params[tabgroup] = tabname;
            } else {
                if (tabgroup) {
                    delete params[tabgroup];
                }
            }
            $route.updateParams(params);
        }

        $rootScope.errors = [];

        //$rootScope.$watch('errors', function () { $rootScope.ASM.ErrorDetailsHeight(); }, true);
        //clear out errors because we navigated away
        $rootScope.$on('$locationChangeStart', function () { $rootScope.errors = []; });
        //Mark added this so we can clear errors after specific events like closing a modal
        $rootScope.$on('clearErrors', function () { $rootScope.errors = []; });

        $rootScope.ASM.anyChecked = function (arr) {

            if (!arr) return false;

            var val = false;

            $.each(arr, function (index, value) {
                if (value.isChecked) {
                    val = true;
                    return;
                }
            });
            return val;
        };

        $rootScope.ASM.onlyChecked = function (arr, prop, values) {

            //If only passed single valid property, turn it into an array
            if (typeof value == 'string') {
                values = [values];
            }

            //default return value to true. We will be checking for invalid
            var ret = true;
            $.each(arr, function (index, value) {
                //if row is checked and its property is not in the values array
                if (value.isChecked && values.indexOf(value[prop]) == -1) {
                    ret = false;
                    return;
                }
            });

            return ret;
        };


        $rootScope.ASM.IsDeviceType = function (deviceType, targetDeviceType) {

            var dt = deviceType;
            var tdt = targetDeviceType;

            switch (tdt) {
                case 'Switch':
                    return dt.indexOf('switch') != -1 || dt.indexOf('IOM') != -1;
                case 'IOM':
                    return dt.indexOf('IOM') != -1;
                case 'Server':
                    return dt.indexOf('Server') != -1;
                case 'RackServer':
                    return dt.indexOf('RackServer') != -1;
                case 'BladeServer':
                    return dt.indexOf('BladeServer') != -1;
                case 'FXServer':
                    return dt.indexOf('FXServer') != -1;
                case 'Chassis':
                    return dt.indexOf('Chassis') != -1;
                case 'Storage':
                    return dt == 'storage' || dt == 'compellent' || dt == 'equallogic' || dt == 'netapp';
                case 'VM':
                    return dt == 'vm';
                case 'vCenter':
                    return dt == 'vcenter';
                case 'SCVMM':
                    return dt == 'scvmm';
            }

            return false;
        };

        $rootScope.ASM.getters = {
            ipaddress: function (field) {
                return function (object) {
                    return $filter('ip2long')(object[field]);
                }
            },
            resourcename: function () {
                return function (device) {

                    if ($rootScope.ASM.isServer(device)) return device['dnsdracname'];
                    if ($rootScope.ASM.isIOM(device) || $rootScope.ASM.isDellSwitch(device)) return device['hostname'];
                    if ($rootScope.ASM.isChassis(device)) return device['chassisname'];
                    if ($rootScope.ASM.isEqualLogic(device)) return device['groupname'];
                    if ($rootScope.ASM.isCompellent(device) || $rootScope.ASM.isNetApp(device)) return device['storagecentername'];
                    if (device.deviceType === 'scvmm' || device.deviceType === 'vcenter' || device.deviceType === 'em') return device['model'];
                    if (device.deviceType === 'storage' || device.deviceType === 'Server' || device.deviceType === 'vm') return '-';

                    return '';
                }
            },
            model: function () {
                return function (device) {
                    if (device.deviceType === 'scvmm' || device.deviceType === 'vcenter' || device.deviceType === 'em') return '-';
                    var manufacturer = device.manufacturer || '';
                    var model = device.model || '';

                    return manufacturer + ' ' + model;
                }
            },
            memory: function () {
                return function (rowItem) {
                    return parseFloat(rowItem.memory);
                }
            }
        };

        $rootScope.ASM.isChassis = function (device) {
            return (device.deviceType === 'ChassisM1000e' || device.deviceType === 'ChassisVRTX' || device.deviceType === 'ChassisFX');
        }
        $rootScope.ASM.isFX2 = function (device) {
            return (device.deviceType === 'ChassisFX');
        }
        $rootScope.ASM.isIOM = function (device) {
            return (device.deviceType === 'AggregatorIOM' || device.deviceType === 'MXLIOM' || device.deviceType === 'FXIOM');
        }
        $rootScope.ASM.isServer = function (device) {
            return (device.deviceType === 'RackServer' || device.deviceType === 'TowerServer' || device.deviceType === 'BladeServer' || device.deviceType === 'FXServer' || device.deviceType === 'Server');
        }
        $rootScope.ASM.isStorage = function (device) {
            return device.deviceType === 'storage' || device.deviceType === 'compellent' || device.deviceType === 'equallogic' || device.deviceType === 'netapp' || device.deviceType === 'emcvnx' || device.deviceType === 'emcunity';
        }
        $rootScope.ASM.isEqualLogic = function (device) {
            return (device.deviceType === 'equallogic');
        }
        $rootScope.ASM.isCompellent = function (device) {
            return (device.deviceType === 'compellent');
        }
        $rootScope.ASM.isEmcvnx = function (device) {
            return device.deviceType === 'emcvnx';
        }
        $rootScope.ASM.isEmcUnity = function (device) {
            return device.deviceType === 'emcunity';
        }
        $rootScope.ASM.isNetApp = function (device) {
            return (device.deviceType === 'netapp');
        }
        $rootScope.ASM.isDellSwitch = function (device) {
            return (device.deviceType === 'dellswitch' || device.deviceType === 'genericswitch');
        }

        $rootScope.ASM.gettingStarted = null;

        $rootScope.ASM.limitString = function (str, limit) {
            if (str && limit) {
                return $filter('ellipsis')(str, limit);
            }
        }

        return $rootScope.ASM;
    }
])


;
