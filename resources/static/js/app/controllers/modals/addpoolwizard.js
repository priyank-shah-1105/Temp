var asm;
(function (asm) {
    var AddPoolWizardController = (function () {
        function AddPoolWizardController(Modal, $scope, Dialog, $http, $translate, Loading, $q, $timeout, Commands, $rootScope, GlobalServices) {
            this.Modal = Modal;
            this.$scope = $scope;
            this.Dialog = Dialog;
            this.$http = $http;
            this.$translate = $translate;
            this.Loading = Loading;
            this.$q = $q;
            this.$timeout = $timeout;
            this.Commands = Commands;
            this.$rootScope = $rootScope;
            this.GlobalServices = GlobalServices;
            this.pool = {
                name: '',
                description: '',
                virtualMACIdentityCount: 0,
                virtualMACUserPrefixSelection: '00',
                virtualMACAutoGenerateOnDeploy: true,
                virtualIQNIdentityCount: 0,
                virtualIQNUserPrefix: '',
                virtualIQNAutoGenerateOnDeploy: true,
                virtualWWPNIdentityCount: 0,
                virtualWWPNUserPrefixSelection: '00',
                virtualWWPNAutoGenerateOnDeploy: true,
                virtualWWNNIdentityCount: 0,
                virtualWWNNUserPrefixSelection: '00',
                virtualWWNNAutoGenerateOnDeploy: true
            };
            this.errors = new Array();
            this.poolPrefixList = [];
            var self = this;
            self.refresh();
        }
        AddPoolWizardController.prototype.refresh = function () {
            var self = this, d = self.$q.defer();
            self.GlobalServices.ClearErrors(self.errors);
            self.Loading(d.promise);
            // jek 3.30.2016 : ASM.old always provided id = null
            // within the Wizard
            self.$http.post(self.Commands.data.pools.getPoolPrefixList, { 'id': null })
                .then(function (data) {
                self.poolPrefixList = data.data.responseObj;
                //set these to the first item in poolPrefixList
                var x = self.poolPrefixList[0];
                if (x) {
                    self.pool.virtualMACUserPrefixSelection = x.id;
                    self.pool.virtualWWPNUserPrefixSelection = x.id;
                    self.pool.virtualWWNNUserPrefixSelection = x.id;
                }
                //// add 'Select' to the list
                //var newItem = { id: 'select', name: 'Select' };
                //self.poolPrefixList.splice(0, 0, newItem);
            })
                .catch(function (data) {
                self.GlobalServices.DisplayError(data.data, self.errors);
            })
                .finally(function () { return d.resolve(); });
        };
        AddPoolWizardController.prototype.validatePool = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                if (self.forms.form_editpool_information.$invalid) {
                    self.forms.form_editpool_information._submitted = true;
                    return reject();
                }
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.pools.validatePool, self.pool)
                    .then(function (data) {
                    resolve();
                })
                    .catch(function (data) {
                    reject();
                    self.GlobalServices.DisplayError(data.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            });
        };
        AddPoolWizardController.prototype.validatePoolInfo = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                if (self.forms.form_edit_virtualmac.$invalid) {
                    self.forms.form_edit_virtualmac._submitted = true;
                    reject();
                }
                else {
                    resolve();
                }
            });
        };
        AddPoolWizardController.prototype.validateVirtualIQN = function () {
            var self = this, d = self.$q.defer();
            if (self.forms.form_edit_virtualiqn.$invalid) {
                self.forms.form_edit_virtualiqn._submitted = true;
                d.reject();
            }
            else {
                d.resolve();
            }
            return d.promise;
        };
        AddPoolWizardController.prototype.validateVirtualWWPN = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                if (self.forms.form_edit_virtualwwpn.$invalid) {
                    self.forms.form_edit_virtualwwpn._submitted = true;
                    reject();
                }
                else {
                    resolve();
                }
            });
        };
        AddPoolWizardController.prototype.validateVirtualWWNN = function () {
            var self = this;
            return self.$q(function (resolve, reject) {
                if (self.forms.form_edit_virtualwwnn.$invalid) {
                    self.forms.form_edit_virtualwwnn._submitted = true;
                    reject();
                }
                else {
                    resolve();
                }
            });
        };
        AddPoolWizardController.prototype.enterPoolInformation = function () {
            var self = this;
            self.$rootScope.helpToken = 'virtualidentitypoolcreate';
        };
        AddPoolWizardController.prototype.enterVirtualMAC = function () {
            var self = this;
            self.$rootScope.helpToken = 'virtualidentitiespooladdingMAC';
        };
        AddPoolWizardController.prototype.enterVirtualIQN = function () {
            var self = this;
            self.$rootScope.helpToken = 'virtualidentitiespooladdingIQN';
        };
        AddPoolWizardController.prototype.enterVirtualWWPN = function () {
            var self = this;
            self.$rootScope.helpToken = 'virualidentitypooladdingWWPN';
        };
        AddPoolWizardController.prototype.enterVirtualWWNN = function () {
            var self = this;
            self.$rootScope.helpToken = 'virtualidentitypooladdingWWNN';
        };
        AddPoolWizardController.prototype.enterPoolSummary = function () {
            var self = this;
            self.$rootScope.helpToken = 'virtualidentitypoolcreate';
        };
        //cancelWizard() {
        //    //THIS FUNCTION IS CALLED ON WIZARD.CANCEL, not MODAL.CANCEL, if you need this logic when clicking the X in the modal move it to the modal definition.
        //    //THE WAY THIS IS CODED WILL LEAD TO 2 CONFIRM DIALOGS, 1 for the Wizard and then another one for the Modal.
        //    //var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('ADDPOOL_Cancel_Confirmation'));
        //    //confirm.then(function () {
        //    //    self.$scope.modal.cancel();
        //    //});
        //    var self: AddPoolWizardController = this;
        //    self.$scope.modal.cancel();
        //}
        AddPoolWizardController.prototype.finishWizard = function () {
            var self = this;
            self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('ADDPOOL_Submit_Confirmation'))
                .then(function () {
                var d = self.$q.defer();
                self.GlobalServices.ClearErrors(self.errors);
                self.Loading(d.promise);
                self.$http.post(self.Commands.data.pools.createPool, self.pool)
                    .then(function () {
                    d.resolve();
                    self.$scope.modal.close();
                })
                    .catch(function (data) {
                    self.GlobalServices.DisplayError(data.data, self.errors);
                })
                    .finally(function () { return d.resolve(); });
            });
            //})
        };
        AddPoolWizardController.prototype.getVirtualMACPrefix = function () {
            var self = this;
            return "00:0E:AA:" + self.pool.virtualMACUserPrefixSelection;
        };
        AddPoolWizardController.prototype.getVirtualWWPNPrefix = function () {
            var self = this;
            return "20:01:00:0E:" + self.pool.virtualWWPNUserPrefixSelection;
        };
        AddPoolWizardController.prototype.getVirtualWWNNPrefix = function () {
            var self = this;
            return "20:00:00:0E:" + self.pool.virtualWWNNUserPrefixSelection;
        };
        AddPoolWizardController.prototype.cancel = function () {
            var self = this;
            self.$scope.modal.cancel();
        };
        AddPoolWizardController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        AddPoolWizardController.$inject = ['Modal', '$scope', 'Dialog', '$http', '$translate', 'Loading', '$q', '$timeout', 'Commands', '$rootScope', 'GlobalServices'];
        return AddPoolWizardController;
    }());
    asm.AddPoolWizardController = AddPoolWizardController;
    angular
        .module('app')
        .controller('AddPoolWizardController', AddPoolWizardController);
})(asm || (asm = {}));
//# sourceMappingURL=addpoolwizard.js.map
