var asm;
(function (asm) {
    "use strict";
    var AddUsersController = (function () {
        function AddUsersController(modal, $translate) {
            this.modal = modal;
            this.$translate = $translate;
        }
        AddUsersController.prototype.removeUsers = function () {
            var self = this;
            _.remove(self.usersList, { enabled: true });
        };
        AddUsersController.prototype.addUsers = function () {
            var self = this;
            var newUsersModal = self.modal({
                title: self.$translate.instant("ADD_USERS_AddUsers"),
                modalSize: "modal-lg",
                templateUrl: "views/addusersmodal.html",
                controller: "AddUsersController as addUsersController",
                params: {
                    users: angular.copy(self.usersList)
                },
                onComplete: function (users) {
                    self.usersList = users;
                },
                onFinish: function () {
                }
            });
            newUsersModal.modal.show();
        };
        AddUsersController.$inject = ["Modal", "$translate"];
        return AddUsersController;
    }());
    angular.module('app')
        .component('addUsers', {
        templateUrl: 'views/adduserscomponent.html',
        controller: AddUsersController,
        controllerAs: 'addUsersController',
        bindings: {
            usersList: "=?"
        }
    });
})(asm || (asm = {}));
//# sourceMappingURL=addUsers.js.map
