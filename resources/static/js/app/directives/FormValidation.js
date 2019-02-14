var asm;
(function (asm) {
    "use strict";
    function formValidation() {
        return {
            restrict: "A",
            require: "form",
            replace: true,
            transclude: false,
            scope: {
                submitted: "<formValidation"
            },
            link: function ($scope, element, attributes, form) {
                $scope.$watch(function () {
                    return $scope.submitted && form.$error;
                }, function () {
                    return setTimeout(function () {
                        element.find(".form-group").each(function (index, e) {
                            var $formGroup = $(this);
                            if ($formGroup.find(".skip-form-group").length) {
                                return;
                            }
                            ;
                            var $inputs = $formGroup.find("input[ng-model],textarea[ng-model],select[ng-model]"), markFormGroup = false;
                            if ($inputs.length) {
                                var isInvalid = false;
                                angular.forEach($inputs, function (input) {
                                    var $input = $(input);
                                    var isSelect = $input.is("select");
                                    //put class of "skip-input" on an input to not have it marked
                                    var skipInput = input.classList.contains("skip-input");
                                    isInvalid = $scope.submitted && (isInvalid || input.classList.contains("ng-invalid"));
                                    markFormGroup = markFormGroup || !isSelect;
                                    var $next = $input.next();
                                    var hasRedXAlready = $next.hasClass("glyphicon glyphicon-remove form-control-feedback");
                                    if (isInvalid && !isSelect && !skipInput) {
                                        hasRedXAlready || $input.after("<i class=\"glyphicon glyphicon-remove form-control-feedback\" aria-hidden=\"true\"></i>");
                                    }
                                    else {
                                        hasRedXAlready && $next.remove();
                                    }
                                });
                                $formGroup.toggleClass("has-error " + (markFormGroup ? "has-feedback" : ""), !!isInvalid);
                            }
                        });
                    }, 200);
                }, true);
            }
        };
    }
    angular
        .module('app')
        .directive('formValidation', formValidation);
})(asm || (asm = {}));
/*
Notes:
bind the form-validation attr to a property that when toggled to true will highlight
Example:
<form name="controllerName.myForm"
      form-validation="controllerName.formSubmitted"
      class="form-horizontal">
    <div class="form-group">
        <div class="col-sm-6">
            <label class="control-label" for="name">
                Label
            </label>
        </div>
        <input type="text"
               id="name"
               name="name"
               ng-model="controllerName.form.name"
               class="form-control" />
    </div>
</form>
*/
//# sourceMappingURL=FormValidation.js.map
