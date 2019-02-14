var asm;
(function (asm) {
    'use strict';
    var DirectoryservicesController = (function () {
        function DirectoryservicesController(Modal, Dialog, $http, $timeout, $q, $compile, $scope, $translate, GlobalServices, Commands) {
            this.Modal = Modal;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$compile = $compile;
            this.$scope = $scope;
            this.$translate = $translate;
            this.GlobalServices = GlobalServices;
            this.Commands = Commands;
            //this.jobs = [
            //    { isSelected: false, "id": 0, "name": 'Scheduled Job 30', "state": 'Scheduled', "starttime": 'Today at 12:00', "timeelapsed": 'none' },
            //    { isSelected: false, "id": 1, "name": 'Scheduled Job 26', "state": 'Scheduled', "starttime": 'Today at 12:00', "timeelapsed": 'none' },
            //    { isSelected: false, "id": 2, "name": 'Scheduled Job 27', "state": 'Scheduled', "starttime": 'Today at 12:00', "timeelapsed": 'none' }
            //];
            this.checkAllBox = false;
            this.selectedStates = [];
            this.selectedUserObjects = [];
            this.activeTab = 'userinfo';
            this.refresh();
        }
        DirectoryservicesController.prototype.refresh = function () {
            var self = this;
            //Get All directories
            this.$http.post(self.Commands.data.users.getDirectoryList, null).then(function (data) {
                self.results = data.data.responseObj;
                self.displayedresults = [].concat(self.results);
                //console.log(self.displayedresults);
                self.selectedDetail = self.results[0];
            }).catch(function (response) {
                self.GlobalServices.DisplayError(response.data, self.errors);
            });
        };
        DirectoryservicesController.prototype.selectedDirectories = function () {
            return _.filter(this.results, { 'isSelected': true });
        };
        //check all checkbox
        DirectoryservicesController.prototype.checkAll = function () {
            var _this = this;
            var self = this;
            this.displayedresults.forEach(function (job) {
                if (_this.checkAllBox) {
                    job.isSelected = true;
                }
                else {
                    job.isSelected = false;
                }
            });
        };
        DirectoryservicesController.prototype.deleteDirectory = function () {
            var self = this;
            var directoryIds = _.map(this.selectedDirectories(), 'id');
            var directoryNames = _.map(this.selectedDirectories(), function (u) {
                return '<li>' + u.hostName + '</li>';
            });
            var Directories = (directoryNames.toString()).replace(/[, ]+/g, " ").trim();
            //Confirmation Dialog box that fires delete on confirmation
            var confirm = this.Dialog((self.$translate.instant('GENERIC_Confirm')), (self.$translate.instant('SETTINGS_DeleteDirectory') + '<br> <ul class="">' + Directories + '</ul>'));
            confirm.then(function () {
                self.$http.post(self.Commands.data.users.deleteADUser, directoryIds)
                    .then(function (data) {
                    self.refresh();
                })
                    .catch(function (response) {
                    self.GlobalServices.DisplayError(response.data, self.errors);
                });
            });
        };
        DirectoryservicesController.prototype.createdirectory = function () {
            var self = this;
            var setupWizard = self.Modal({
                title: self.$translate.instant('SETTINGS_DirectoryServicesCreateDir'),
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/directorywizard.html',
                controller: 'DirectoryWizardController as DirectoryWizard',
                params: {},
                onCancel: function () {
                    var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('ADDPOOL_Cancel_Confirmation'));
                    confirm.then(function (modalScope) {
                        setupWizard.modal.dismiss();
                        self.refresh();
                    });
                },
                onComplete: function (modalScope) {
                    self.refresh(); //When the modal is closed, update the data.
                }
            });
            setupWizard.modal.show();
        };
        DirectoryservicesController.prototype.editdirectory = function () {
            var self = this;
            var modaldirectory = this.selectedDirectories();
            var setupWizard = self.Modal({
                title: this.$translate.instant('SETTINGS_DirectoryServicesEditDir'),
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/directorywizard.html',
                controller: 'DirectoryWizardController as DirectoryWizard',
                params: {
                    editmode: true,
                    directory: modaldirectory
                },
                onCancel: function () {
                    var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('ADDPOOL_Cancel_Confirmation'));
                    confirm.then(function (modalScope) {
                        setupWizard.modal.dismiss();
                        self.refresh();
                    });
                },
                onComplete: function () {
                    self.refresh(); //When the modal is closed, update the data.
                }
            });
            setupWizard.modal.show();
        };
        DirectoryservicesController.$inject = ['Modal', 'Dialog', '$http', '$timeout', '$q', '$compile', '$scope', '$translate', 'GlobalServices', 'Commands'];
        return DirectoryservicesController;
    }());
    angular.module("app")
        .component("directoryList", {
        templateUrl: "views/directoryserviceslist.html",
        controller: DirectoryservicesController,
        controllerAs: "directoryList",
        bindings: {
            errors: "="
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=directoryservicesdirective.js.map
