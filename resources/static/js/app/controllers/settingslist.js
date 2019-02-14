var asm;
(function (asm) {
    var SettingslistController = (function () {
        function SettingslistController(Modal, Dialog, $http, $timeout, $q, $router, $location, $routeParams, GlobalServices, commands) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$router = $router;
            this.$location = $location;
            this.$routeParams = $routeParams;
            this.GlobalServices = GlobalServices;
            this.commands = commands;
            this.sections = {
                //addOnModule: "AddOnModule",
                backupAndRestore: "BackupAndRestore",
                credentialsManagement: "CredentialsManagement",
                gettingStarted: "GettingStarted",
                jobs: "Jobs",
                logs: "Logs",
                networks: "Networks",
                repositories: "Repositories",
                initialApplianceSetup: "InitialApplianceSetup",
                users: "Users",
                virtualApplianceManagement: "VirtualApplianceManagement"
            };
            var self = this;
            self.changeSection(self.$routeParams.settingType
                ? self.$routeParams.settingType
                : self.sections.backupAndRestore);
            self.fullscreen = self.$routeParams.fullscreen === 'true';
            self.modalOrTab = self.$routeParams.modalOrTab;
            self.setInitialSetup();
        }
        SettingslistController.prototype.changeSection = function (section) {
            var self = this;
            self.showSection = section;
            self.$location.path("settings/" + section);
        };
        SettingslistController.prototype.isCurrentTab = function (section) {
            var self = this;
            return angular.equals(self.showSection, section);
        };
        SettingslistController.prototype.setInitialSetup = function () {
            var self = this;
            self.getStartupData()
                .then(function (response) {
                self.GlobalServices.showInitialSetup = !response.data.responseObj.initialSetupCompleted;
            });
        };
        SettingslistController.prototype.getStarted = function () {
            var self = this;
            self.$location.path("/gettingstarted");
        };
        SettingslistController.prototype.getStartupData = function () {
            var self = this;
            return self.$http.post(self.commands.data.initialSetup.gettingStarted, null);
        };
        SettingslistController.prototype.setupWizard = function () {
            var self = this;
            var setupWizard = self.Modal({
                title: 'Setup Wizard',
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/setupwizard.html',
                controller: 'SetupWizardController as SetupWizard',
                params: {},
                onComplete: function () {
                    self.setInitialSetup();
                }
            });
            setupWizard.modal.show();
        };
        SettingslistController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$router', "$location", "$routeParams", 'GlobalServices', "Commands"];
        return SettingslistController;
    }());
    asm.SettingslistController = SettingslistController;
    angular
        .module('app')
        .controller('SettingslistController', SettingslistController);
})(asm || (asm = {}));
//# sourceMappingURL=settingslist.js.map
