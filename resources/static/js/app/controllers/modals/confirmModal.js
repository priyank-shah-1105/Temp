var asm;
(function (asm) {
    var ConfirmModalController = (function () {
        function ConfirmModalController($http, $timeout, $scope, $q, $translate, Loading, Dialog, Commands, GlobalServices, $filter, MessageBox) {
            this.$http = $http;
            this.$timeout = $timeout;
            this.$scope = $scope;
            this.$q = $q;
            this.$translate = $translate;
            this.Loading = Loading;
            this.Dialog = Dialog;
            this.Commands = Commands;
            this.GlobalServices = GlobalServices;
            this.$filter = $filter;
            this.MessageBox = MessageBox;
            var self = this;
            self.confirm =
                {
                    id: '',
                    text: '',
                    html: '',
                    items: [],
                    alert: false,
                    icontype: 'warning',
                    titletext: '',
                    footnote: '',
                    alternateActionText: ''
                };
            self.confirmed = false;
            self.alternateAction = false;
            if ($scope.modal.params.confirm) {
                self.confirm = angular.copy($scope.modal.params.confirm);
            }
            self.activate();
        }
        ConfirmModalController.prototype.activate = function () {
            var self = this;
            //this.$el.addClass( 'confirmation' );
            //if ( self.selectedNetworkId )
            //{
            //    self.$http.post( 'networks/getnetworkbyid', { 'id': self.selectedNetworkId })
            //        .then( function ( data: any )
            //        {
            //            self.network = data.data.responseObj;
            //            //this must be called here so that it runs upon return from this call
            //            self.setSelectedNetworkType();                        
            //        }).catch( function ( data )
            //        {
            //            self.GlobalServices.DisplayError( data.data );
            //        });
            //}
            //else
            //{
            //    self.editMode = 'create';
            //    // new network, set some defaults
            //    self.network.ipaddressranges = [];
            //    self.network.staticipaddressdetails = [];
            //    self.network.addressPools = [];
            //    self.network.networkTemplateUsages = [];
            //}
        };
        ConfirmModalController.prototype.save = function () {
            var self = this;
            self.confirmed = true;
            self.$scope.modal.params.confirmed = self.confirmed;
            self.$scope.modal.close();
        };
        ConfirmModalController.prototype.doAlternateAction = function () {
            var self = this;
            self.alternateAction = true;
            self.$scope.modal.params.alternateAction = self.alternateAction;
            self.$scope.modal.close();
        };
        ConfirmModalController.prototype.close = function () {
            var self = this;
            self.$scope.modal.close();
        };
        ConfirmModalController.$inject = ['$http', '$timeout', '$scope', '$q', '$translate', 'loading', 'dialog',
            'Commands', 'GlobalServices', '$filter', 'messagebox'];
        return ConfirmModalController;
    }());
    asm.ConfirmModalController = ConfirmModalController;
    angular
        .module('app')
        .controller('ConfirmModalController', ConfirmModalController);
})(asm || (asm = {}));
//# sourceMappingURL=confirmModal.js.map
