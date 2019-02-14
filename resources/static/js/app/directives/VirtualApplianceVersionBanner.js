var asm;
(function (asm) {
    "use strict";
    var VirtualApplianceVersionBannerController = (function () {
        function VirtualApplianceVersionBannerController($http, $translate, Commands, GlobalServices, $q, $timeout, $interval, $location, modal, loading, $scope) {
            this.$http = $http;
            this.$translate = $translate;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$q = $q;
            this.$timeout = $timeout;
            this.$interval = $interval;
            this.$location = $location;
            this.modal = modal;
            this.loading = loading;
            this.$scope = $scope;
            this.sessionStorageKey = "virtualApplianceVersionBannerHidden";
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
            //if virtualApplianceAlert hasn't been created yet, create it
            if (!self.userPreference.virtualApplianceAlert) {
                self.resetPreferences();
            }
            self.isDismissExpired = self.dismissExpired(self.userPreference.virtualApplianceAlert.dismissedDate);
            //if virtualApplianceAlert is false, user may have recently updated, so reset the dismiss setting
            if (self.isDismissExpired && self.virtualApplianceAlert) {
                self.resetPreferences();
            }
        }
        VirtualApplianceVersionBannerController.prototype.getShowBanner = function () {
            var self = this;
            var value = (self.isDismissExpired && self.getAlertHidden() !== "true") && self.virtualApplianceAlert;
            return value;
        };
        VirtualApplianceVersionBannerController.prototype.viewDetails = function () {
            var self = this;
            //if already at editRepoPath location, just open modal
            //if (self.$location.$$path.endsWith("editRepoPath")) {
            //    self.applianceUpgradeSettingsModal();
            //} else {
            self.$location.path("settings/VirtualApplianceManagement/false/editRepoPath");
            //}
        };
        VirtualApplianceVersionBannerController.prototype.hide = function () {
            var self = this;
            self.setAlertHidden("true");
        };
        VirtualApplianceVersionBannerController.prototype.dismiss = function () {
            var self = this;
            self.hide();
            self.userPreference.virtualApplianceAlert.dismissedDate = moment().toISOString();
            self.save();
        };
        VirtualApplianceVersionBannerController.prototype.dismissExpired = function (dismissedDate) {
            if (!dismissedDate)
                return true;
            var expirationDate = moment(dismissedDate).add(30, 'days');
            return moment().isAfter(expirationDate);
        };
        VirtualApplianceVersionBannerController.prototype.resetPreferences = function () {
            var self = this;
            self.userPreference.virtualApplianceAlert = { dismissedDate: null };
            self.save();
        };
        VirtualApplianceVersionBannerController.prototype.getAlertHidden = function () {
            var value = sessionStorage.getItem(this.sessionStorageKey);
            return value;
        };
        VirtualApplianceVersionBannerController.prototype.setAlertHidden = function (value) {
            sessionStorage.setItem(this.sessionStorageKey, value);
        };
        VirtualApplianceVersionBannerController.prototype.parseUserPreferences = function (userPreference) {
            if (typeof userPreference === "string") {
                userPreference = angular.fromJson(userPreference);
            }
            return userPreference;
        };
        VirtualApplianceVersionBannerController.prototype.save = function () {
            var self = this;
            self.$http.post(self.Commands.data.users.updateUserPreferences, angular.toJson(self.userPreference))
                .then(function (data) {
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data);
            });
        };
        VirtualApplianceVersionBannerController.prototype.applianceUpgradeSettingsModal = function () {
            var self = this;
            var d = self.$q.defer();
            self.loading(d.promise);
            self.$http.post(self.Commands.data.applianceManagement.getApplianceUpgrade, {})
                .then(function (response) {
                var modal = self.modal({
                    title: self.$translate.instant('SETTINGS_EditApplianceUpgradeSettings_ModalTitle'),
                    onHelp: function () {
                        self.GlobalServices.showHelp('UpdateRepositoryPath');
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/settings/virtualappliancemanagement/applianceupgradesettingsmodal.html',
                    controller: 'ApplianceUpgradeSettingsModalController as applianceUpgradeSettingsModalController',
                    params: {
                        applianceUpdateInfo: response.data.responseObj
                    },
                    onComplete: function () {
                        ////hack to trigger refresh
                        //self.$scope.$emit("settingsRefresh", true);
                        self.$location.path("settings/VirtualApplianceManagement/false");
                    },
                    onCancel: function () {
                        self.$location.path("settings/VirtualApplianceManagement/false");
                    }
                });
                modal.modal.show();
            })
                .catch(function (response) { self.GlobalServices.DisplayError(response.data); })
                .finally(function () { return d.resolve(); });
        };
        VirtualApplianceVersionBannerController.$inject = ["$http", "$translate", "Commands", "GlobalServices", "$q", "$timeout", "$interval", "$location", "Modal", "Loading", "$scope"];
        return VirtualApplianceVersionBannerController;
    }());
    angular.module('app')
        .component("virtualApplianceVersionBanner", {
        templateUrl: "views/virtualapplianceversionbanner.html",
        controller: VirtualApplianceVersionBannerController,
        controllerAs: 'virtualApplianceVersionBannerController',
        bindings: {
            userPreference: "=",
            virtualApplianceAlert: "<"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=VirtualApplianceVersionBanner.js.map
