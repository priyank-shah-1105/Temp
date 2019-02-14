angular.module('ASM.dataservices')
    .service('CollectionService', [ function () {
        this.MergeObjects = function (baseObj, sourceObj) {
            var combinedObj = angular.copy({}, baseObj);
            combinedObj = angular.copy(combinedObj, sourceObj);
            return combinedObj;
        };
    }]);
