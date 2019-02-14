var asm;
(function (asm) {
    "use strict";
    var DeviceLocalStorageController = (function () {
        function DeviceLocalStorageController($translate, Modal) {
            this.$translate = $translate;
            this.Modal = Modal;
            var self = this;
            self.safeSource = angular.copy(self.device.localstoragedata);
            self.filterOptions = [
                { id: "logical", name: self.$translate.instant("DEVICES_DETAILS_LOCAL_STORAGE_LogicalDisks") },
                { id: "physical", name: self.$translate.instant("DEVICES_DETAILS_LOCAL_STORAGE_PhysicalDisks") }
            ];
            self.filter = "logical";
        }
        DeviceLocalStorageController.prototype.viewDisks = function (disk) {
            var self = this;
            var viewLogsModal = self.Modal({
                title: self.$translate.instant("DEVICES_DETAILS_LOCAL_STORAGE_PhysicalDisksForLogicalDiskModalTitle", { diskName: disk.logicalDiskName }),
                modalSize: 'modal-lg',
                templateUrl: 'views/devicedetails/viewphysicaldisksmodal.html',
                controller: 'ViewPhysicalDisksController as viewPhysicalDisksController',
                params: {
                    layout: disk.layout,
                    size: disk.size,
                    disks: disk.physicaldiskdata
                }
            });
            viewLogsModal.modal.show();
        };
        DeviceLocalStorageController.$inject = ["$translate", "Modal"];
        return DeviceLocalStorageController;
    }());
    angular.module('app')
        .component('deviceLocalStorage', {
        templateUrl: 'views/devicedetails/localstorage.html',
        controller: DeviceLocalStorageController,
        controllerAs: 'deviceLocalStorageController',
        bindings: {
            device: '<',
            deviceType: '<'
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=LocalStorage.js.map
