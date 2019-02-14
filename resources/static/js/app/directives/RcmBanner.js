var asm;
(function (asm) {
    "use strict";
    var RcmBannerController = (function () {
        function RcmBannerController($http, $translate, Commands, GlobalServices, $q, $timeout, $interval, $location) {
            this.$http = $http;
            this.$translate = $translate;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$q = $q;
            this.$timeout = $timeout;
            this.$interval = $interval;
            this.$location = $location;
            var self = this;
            //if not set, set it
            if (self.getAlertHidden() === "") {
                self.setAlertHidden("false");
            }
            //if userPreferences exist, parse them from string to JSON
            self.userPreference = self.parseUserPreferences(self.userPreference);
            //if userPreferences haven't been created yet, create them
            if (!self.userPreference) {
                self.userPreference = {};
            }
            //if rcm hasn't been created yet, create it
            if (!self.userPreference.rcm) {
                self.resetRcmPreferences();
            }
            self.isDismissExpired = self.dismissExpired(self.userPreference.rcm.dismissedDate);
            //if rcmVersionAlert is false, user may have recently updated, so reset the dismiss setting
            if (self.isDismissExpired && self.rcmVersionAlert) {
                self.resetRcmPreferences();
            }
        }
        RcmBannerController.prototype.getShowBanner = function () {
            var self = this;
            return (self.isDismissExpired && self.getAlertHidden() !== "true") && self.rcmVersionAlert;
        };
        RcmBannerController.prototype.viewDetails = function () {
            var self = this;
            self.$location.path("settings/Repositories/false/ManageRCMVersions");
        };
        RcmBannerController.prototype.hide = function () {
            var self = this;
            self.setAlertHidden("true");
        };
        RcmBannerController.prototype.dismiss = function () {
            var self = this;
            self.hide();
            self.userPreference.rcm.dismissedDate = moment().toISOString();
            self.save();
        };
        RcmBannerController.prototype.dismissExpired = function (dismissedDate) {
            if (!dismissedDate)
                return true;
            var expirationDate = moment(dismissedDate).add(30, 'days');
            return moment().isAfter(expirationDate);
        };
        RcmBannerController.prototype.resetRcmPreferences = function () {
            var self = this;
            self.userPreference.rcm = { dismissedDate: null };
            self.save();
        };
        RcmBannerController.prototype.getAlertHidden = function () {
            return sessionStorage.getItem("rcmAlertHidden");
        };
        RcmBannerController.prototype.setAlertHidden = function (value) {
            sessionStorage.setItem("rcmAlertHidden", value);
        };
        RcmBannerController.prototype.parseUserPreferences = function (userPreference) {
            if (typeof userPreference === "string") {
                userPreference = angular.fromJson(userPreference);
            }
            return userPreference;
        };
        RcmBannerController.prototype.save = function () {
            var self = this;
            self.$http.post(self.Commands.data.users.updateUserPreferences, angular.toJson(self.userPreference))
                .then(function (data) {
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            });
        };
        RcmBannerController.$inject = ["$http", "$translate", "Commands", "GlobalServices", "$q", "$timeout", "$interval", "$location"];
        return RcmBannerController;
    }());
    angular.module('app')
        .component("rcmBanner", {
        templateUrl: "views/rcmbanner.html",
        controller: RcmBannerController,
        controllerAs: 'rcmBannerController',
        bindings: {
            userPreference: "=",
            rcmVersionAlert: "<"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=RcmBanner.js.map
