var asm;
(function (asm) {
    "use strict";
    var StorageAlertBannerController = (function () {
        function StorageAlertBannerController($http, $translate, Commands, GlobalServices, $q, $timeout, $interval) {
            this.$http = $http;
            this.$translate = $translate;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$q = $q;
            this.$timeout = $timeout;
            this.$interval = $interval;
            this.sessionStorageKey = "storageAlertHidden";
            var self = this;
            //if not set, set it 
            //or
            //if storageUtilization is normal, reset the session alert setting
            var alertHidden = self.getAlertHidden();
            var criticality = self.getCriticality(self.storageUtilization.percentageUsed);
            if (!self.getAlertHidden() || !self.getCriticality(self.storageUtilization.percentageUsed)) {
                self.setAlertHidden("false");
            }
            //if userPreferences exist, parse them from string to JSON
            self.userPreference = self.parseUserPreferences(self.userPreference);
            //if userPreferences haven't been created yet, create them
            if (!self.userPreference) {
                self.userPreference = {};
            }
            //if isStorageAlertDismissed hasn't been created yet, create it
            if (typeof (self.userPreference.isStorageAlertDismissed) !== "boolean") {
                self.userPreference.isStorageAlertDismissed = false;
                self.save();
            }
        }
        StorageAlertBannerController.prototype.getCriticality = function (percentUsed) {
            if (percentUsed >= .95) {
                return "critical";
            }
            else if (percentUsed >= .75) {
                return "warning";
            }
        };
        StorageAlertBannerController.prototype.getShowBanner = function () {
            var self = this;
            //return that it's neither been hidden nor dismissed, and that it's at least at warning levels
            return (!self.userPreference.isStorageAlertDismissed && self.getAlertHidden() !== "true") && !!self.getCriticality(self.storageUtilization.percentageUsed);
        };
        StorageAlertBannerController.prototype.getBannerTitleText = function () {
            var self = this;
            return self.$translate.instant("INDEX_STORAGE_BANNER_Title", { percentage: self.storageUtilization.percentageUsed * 100, partitionName: self.storageUtilization.partitionName });
        };
        StorageAlertBannerController.prototype.learnMore = function () {
            var self = this;
            self.GlobalServices.showHelp("storageAlertLearnMore");
        };
        StorageAlertBannerController.prototype.hide = function () {
            var self = this;
            self.setAlertHidden("true");
        };
        StorageAlertBannerController.prototype.dismiss = function () {
            var self = this;
            self.userPreference.isStorageAlertDismissed = true;
            self.save();
        };
        StorageAlertBannerController.prototype.getAlertHidden = function () {
            var self = this;
            return sessionStorage.getItem(self.sessionStorageKey);
        };
        StorageAlertBannerController.prototype.setAlertHidden = function (value) {
            var self = this;
            sessionStorage.setItem(self.sessionStorageKey, value);
        };
        StorageAlertBannerController.prototype.parseUserPreferences = function (userPreference) {
            if (typeof userPreference === "string") {
                userPreference = angular.fromJson(userPreference);
            }
            return userPreference;
        };
        StorageAlertBannerController.prototype.save = function () {
            var self = this;
            self.$http.post(self.Commands.data.users.updateUserPreferences, angular.toJson(self.userPreference))
                .then(function (data) {
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            });
        };
        StorageAlertBannerController.$inject = ["$http", "$translate", "Commands", "GlobalServices", "$q", "$timeout", "$interval"];
        return StorageAlertBannerController;
    }());
    angular.module('app')
        .component("storageAlertBanner", {
        templateUrl: "views/storagealertbanner.html",
        controller: StorageAlertBannerController,
        controllerAs: 'storageAlertBannerController',
        bindings: {
            userPreference: "=",
            storageUtilization: "<",
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=StorageAlertBanner.js.map
