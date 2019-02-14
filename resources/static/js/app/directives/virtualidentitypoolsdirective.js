var asm;
(function (asm) {
    "use strict";
    var VirtualIdentityPoolsController = (function () {
        function VirtualIdentityPoolsController($http, $timeout, $q, $translate, Modal, Loading, Dialog, Commands, GlobalServices, $window) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$q = $q;
            this.$translate = $translate;
            this.Modal = Modal;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$window = $window;
            this.viewModel = {};
            this.selectedVIP = '';
            this.selectedSummary = {};
            this.displayedData = [];
            this.checkAllBox = false;
            var self = this;
            self.refresh(false);
        }
        VirtualIdentityPoolsController.prototype.activate = function () {
            var self = this;
            this.$timeout(function () {
                self.refresh(false);
            }, 10000);
        };
        VirtualIdentityPoolsController.prototype.refresh = function (calledFromUI) {
            var self = this;
            var data;
            if (calledFromUI) {
                var deferred = self.$q.defer();
                self.GlobalServices.ClearErrors();
                self.Loading(deferred.promise);
            }
            //get the list
            self.$http.post(self.Commands.data.pools.getPools, null).then(function (data) {
                //self.$http.post('bogus', null).then(function (data: any) {
                self.viewModel = data.data.responseObj;
                self.displayedData = [].concat(self.viewModel);
                if (self.viewModel.length) {
                    if (self.selectedVIP)
                        self.onSelectedVIP(self.selectedVIP);
                    else
                        self.onSelectedVIP(self.viewModel[0]);
                }
                else {
                    // no data
                    self.selectedSummary = '';
                    self.selectedVIP = '';
                }
                if (calledFromUI)
                    deferred.resolve();
            }).catch(function (data) {
                //error
                self.viewModel = [];
                if (calledFromUI)
                    deferred.resolve();
                self.GlobalServices.DisplayError(data.data);
            });
        };
        VirtualIdentityPoolsController.prototype.vipItemsSelected = function () {
            // used to enable/disable export & delete 
            // if any items are selected, return false (buttons will not be disabled
            // if no items are selected, return true (buttons will be disabled
            var self = this;
            return _.filter(self.displayedData, { 'isSelected': true });
        };
        VirtualIdentityPoolsController.prototype.canDelete = function () {
            var self = this;
            var retValue = true;
            for (var i = 0; i < self.displayedData.length; i++) {
                var currentItem = self.displayedData[i];
                if (currentItem.isSelected == true && currentItem.canDelete == false) {
                    retValue = false;
                    break;
                }
            }
            return retValue;
        };
        VirtualIdentityPoolsController.prototype.onCheckAllPools = function () {
            var self = this;
            self.displayedData.forEach(function (vip) {
                //automagically adds isSelected proeprty 
                //to data 
                vip.isSelected = self.checkAllBox;
            });
        };
        VirtualIdentityPoolsController.prototype.onSelectedVIP = function (selectedVIP) {
            var self = this;
            var data;
            //var deferred = self.$q.defer();
            //self.Loading(deferred.promise);
            self.GlobalServices.ClearErrors();
            data = { 'id': selectedVIP.id };
            self.$http.post(self.Commands.data.pools.getPoolById, { requestObj: data }).then(function (data) {
                //self.$http.post('bogus', { requestObj: data }).then(function(data:any){
                self.selectedSummary = data.data.responseObj;
                self.selectedVIP = selectedVIP;
                //deferred.resolve();
            }).catch(function (data) {
                //need to handle error
                //deferred.resolve();
                //error is in data
                self.GlobalServices.DisplayError(data.data);
            });
        };
        VirtualIdentityPoolsController.prototype.doExport = function () {
            var self = this;
            var vipIds = _.map(self.vipItemsSelected(), 'id');
            var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('LISTPOOLS_Export_Confirmation'));
            confirm.then(function () {
                var deferred = self.$q.defer();
                self.GlobalServices.ClearErrors();
                self.Loading(deferred.promise);
                self.$http.post(self.Commands.data.pools.exportPools, { requestObj: vipIds }).then(function (data) {
                    //self.$http.post('bogus', { requestObj: data }).then(function(data:any){
                    self.$window.location.assign("" + data.data.responseObj.id);
                    //var iframe;
                    //iframe = document.createElement('iframe');
                    //iframe.src = data.data.responseObj;
                    //iframe.style.display = 'none';
                    //document.body.appendChild(iframe);
                    ////document.body.appendChild("<iframe src='" + data.data.responseObj + "' style='display: none;' ></iframe>");
                    deferred.resolve();
                    self.refresh(false);
                }).catch(function (data) {
                    //need to handle error
                    deferred.resolve();
                    //data object is always empty
                    self.GlobalServices.DisplayError(data.data);
                });
            });
        };
        VirtualIdentityPoolsController.prototype.doDelete = function () {
            var self = this;
            var vipIds = _.map(self.vipItemsSelected(), 'id');
            var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('LISTPOOLS_Delete_Confirmation'));
            confirm.then(function () {
                var deferred = self.$q.defer();
                self.GlobalServices.ClearErrors();
                self.Loading(deferred.promise);
                self.$http.post(self.Commands.data.pools.deletePools, { requestObj: vipIds }).then(function (data) {
                    //self.$http.post('bogus', { requestObj: data }).then(function(data:any){
                    self.selectItemAfterDelete();
                    deferred.resolve();
                    self.refresh(false);
                }).catch(function (data) {
                    //need to handle error
                    deferred.resolve();
                    //data object is always empty
                    self.GlobalServices.DisplayError(data.data);
                });
            });
        };
        VirtualIdentityPoolsController.prototype.selectItemAfterDelete = function () {
            var self = this;
            if (self.vipItemsSelected().length > 1) {
                self.selectedVIP = '';
            }
            else {
                // where was the item that we just deleted?
                var deleteItemNdx = self.displayedData.map(function (ds) { return ds.id; }).indexOf(self.selectedVIP.id);
                // top of list, pick next item down
                if (deleteItemNdx == 0) {
                    //self.displayedData[deleteItemNdx + 1]; may not exist (undefined), this is OK
                    self.selectedVIP = self.displayedData[deleteItemNdx + 1];
                }
                else {
                    if (deleteItemNdx >= 0) {
                        //self.displayedData[deleteItemNdx - 1]; may not exist (undefined), this is OK
                        self.selectedVIP = self.displayedData[deleteItemNdx - 1];
                    }
                }
            }
        };
        VirtualIdentityPoolsController.prototype.doUpdatePool = function (selectedType) {
            var self = this;
            var editModal = this.Modal({
                title: self.$translate.instant('LISTPOOLS_Update_Pool_Title'),
                modalSize: 'modal-lg',
                templateUrl: 'views/networking/pools/editpool.html',
                controller: 'EditPoolController as EditPoolController',
                params: {
                    'id': self.selectedVIP.id,
                    'type': selectedType
                },
                onComplete: function (modalScope) {
                    //modalScope.modal.dismiss();
                    self.refresh(false);
                }
            });
            editModal.modal.show();
        };
        VirtualIdentityPoolsController.prototype.doAddWizard = function () {
            var self = this;
            var addPoolWizard = self.Modal({
                title: self.$translate.instant('ADDPOOL_Title'),
                onHelp: function () {
                    self.GlobalServices.showHelp();
                },
                modalSize: 'modal-lg',
                templateUrl: 'views/networking/pools/addpoolwizard.html',
                controller: 'AddPoolWizardController as AddPoolWizardController',
                params: {},
                onCancel: function () {
                    //THIS FUNCTION IS CALLED ON MODAL.CANCEL, not WIZARD.CANCEL
                    //var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), 'Are you sure you want to cancel?');
                    var confirm = self.Dialog(self.$translate.instant('GENERIC_Confirm'), self.$translate.instant('ADDPOOL_Cancel_Confirmation'));
                    confirm.then(function (modalScope) {
                        addPoolWizard.modal.dismiss();
                        self.refresh(false);
                    });
                },
                onComplete: function (modalScope) {
                    self.refresh(false); //When the modal is closed, update the data.
                }
            });
            addPoolWizard.modal.show();
        };
        VirtualIdentityPoolsController.$inject = ['$http', '$timeout', '$q', '$translate', 'Modal', 'Loading', 'Dialog', 'Commands', 'GlobalServices', "$window"];
        return VirtualIdentityPoolsController;
    }());
    angular.module('app')
        .component('virtualidentitypools', {
        templateUrl: 'views/listpools.html',
        controller: VirtualIdentityPoolsController,
        controllerAs: 'VirtualIdentityPoolsController',
        bindings: {}
    });
})(asm || (asm = {}));
//# sourceMappingURL=virtualidentitypoolsdirective.js.map
