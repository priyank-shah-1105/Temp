angular.module('Enums', [])
    .factory('Enum', function () {
        return {
            deviceHealth: function (value) {

                var deviceHealths = [
                        "unknown",
                        "green",
                        "yellow",
                        "red"
                ];
                return deviceHealths[value];
            },
            deviceType: function (value) {

                var deviceType = [
                    "Unknown",
                    "Rack Server",
                    "Blade Server",
                    "Chassis",
                    "Fabric",
                    "Switch",
                    "Internal Storage",
                    "External Storage",
                    "Controller"
                ];
                return deviceType[value];
            },
            serviceHealth: function (value) {
                
                var serviceHealths = [
                        "unknown",
                        "green",
                        "yellow",
                        "red"
                ];
                return serviceHealths[value];
            },
            serviceState: function (value) {

                var serviceState = [
                        "In Progress",
                        "Complete",
                        "Pending",
                        "Completed With Errors"
                ];
                return serviceState[value];
            },
            healthToColor: function (value) {
                var color = [
                    "bg-primary",
                    "bg-success",
                    "bg-warning",
                    "bg-danger"
                ];
                return color[value];
            },
            healthToColorText: function (value) {
                var color = [
                    "",
                    "text-success",
                    "text-warning",
                    "text-danger"
                ];
                return color[value];
            },
            slaToText: function (value) {
                var sla = [
                    "Bronze",
                    "Silver",
                    "Gold",
                    "Platinum"
                ];
                return sla[value];
            },

            StatusToText: function (value) {
                var status = [
                    "Unknown",
                    "Available",
                    "Pending"
                ];
                return status[value];
            },


            deviceStatusToText: function (value) {
                var devicestatus = [
                    "Unknown",
                    "Available",
                    "Pending"
                ];
                return devicestatus[value];
            },
            devicePowerStateToText: function (value) {
                var powerstate = [
                    "Off",
                    "On"
                ];
                return powerstate[value];
            },
            serviceDetailsHealthIcon: function (value) {
                var health = [
                    "ci-threshold-alert-clock",
                    "ci-ok-square-check",
                    "ci-status-warning",
                    "ci-critical-circle-x"
                ];
                return health[value];
            }
            
        }
    });
