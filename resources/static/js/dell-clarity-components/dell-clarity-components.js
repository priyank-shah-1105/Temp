/*
 *  © 2017 Dell Inc.
 *  ALL RIGHTS RESERVED.
 *  dell-clarity-components 1.0.0
 */
(function () {
    window.__clarity = {
        version: "1.0.0",
        major: "1",
        minor: "0",
        patch: "0",
        full: "1.0.0"
    };
    angular.module("Clarity.services", []);
    angular.module("Clarity.localizations", []);
    angular.module("Clarity.directives", ["Clarity.services"]);
    angular.module("Clarity", ["ngRoute", "ngResource", "pascalprecht.translate", "ngSanitize", "angularFileUpload", "highcharts-ng", "smart-table", "ngCookies", "perfect_scrollbar", "ngAnimate", "Clarity.directives", "Clarity.services", "Clarity.localizations"]).config(["$translateProvider", "ClarityTranslations", function ($translateProvider, ClarityTranslations) {
        angular.forEach(ClarityTranslations, function (translations, lang) {
            var words = {};
            angular.extend(words, translations);
            $translateProvider.translations(lang, translations);
            $translateProvider.useSanitizeValueStrategy("sanitizeParameters");
        });
        if (ClarityTranslations.en != undefined) {
            $translateProvider.preferredLanguage("en");
        } else {
            $translateProvider.preferredLanguage(Object.keys(ClarityTranslations)[0]);
        }
    }]);
    (function (ng, undefined) {
        "use strict";
        ng.module("smart-table", []).run(["$templateCache", function ($templateCache) {
            $templateCache.put("template/smart-table/pagination.html", "<nav>" + '<ul class="list-unstyled list-inline">' + '<li class="pull-left"><p><span ng-if="itemCount != totalItems">{{itemCount}} of {{totalItems}}</span><span ng-if="itemCount == totalItems">{{itemCount}}</span> items found. <span ng-if="itemCount > 0">Displaying items {{(currentPage - 1) * stItemsByPage + 1 }} - {{ currentPage == numPages ? itemCount : currentPage * stItemsByPage }}.</span></p></li>' + '<li ng-if="numPages >= 2" ng-disabled="currentPage == numPages" class="pull-right"><a style="position: relative; top: 3px;" ng-disabled="currentPage == numPages" ng-click="selectPage(numPages)"><i ng-class="{\'text-muted\': currentPage == numPages}" class="glyphicon glyphicon-step-forward"></i></a></li>' + '<li ng-if="numPages >= 2" ng-disabled="currentPage == numPages" class="pull-right"><a style="position: relative; top: 3px;" ng-disabled="currentPage == numPages" ng-click="selectPage(currentPage+1)"><i ng-class="{\'text-muted\': currentPage == numPages}" class="glyphicon glyphicon-forward"></i></a></li>' + '<li ng-if="numPages >= 2" class="pull-right"><span style="margin-left: 5px; margin-right: 5px;">Page <input type="text" class="input-xs text-center" style="width: 40px;" ng-model="pageState.txtPage" ng-change="updatePage()" ng-blur="updatePage(true)"  ng-model-options="{ updateOn: \'default\', debounce: {\'default\': 500, \'blur\': 0} }" /> of {{numPages}}</span></li>' + '<li ng-if="numPages >= 2" ng-disabled="currentPage == 1" class="pull-right"><a style="position: relative; top: 3px;" ng-disabled="currentPage == 1" ng-click="selectPage(currentPage-1)"><i ng-class="{\'text-muted\': currentPage == 1}" class="glyphicon glyphicon-backward"></i></a></li>' + '<li ng-if="numPages >= 2" ng-disabled="currentPage == 1" class="pull-right"><a style="position: relative; top: 3px;" ng-disabled="currentPage == 1" ng-click="selectPage(1)"><i ng-class="{\'text-muted\': currentPage == 1}" class="glyphicon glyphicon-step-backward"></i></a></li>' + "</ul></nav>");
            $templateCache.put("template/smart-table/stSelectMultiple.html", '<div class="dropdown dropdown-filter">' + '  <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">' + '  {{dropdownLabel}}&nbsp;<span class="caret"></span>' + "  </button>" + '  <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">' + '    <li ng-repeat="item in distinctItems">' + "      <label>" + '        <input type="checkbox" ng-model="item.selected" style="margin-left: 3px; margin-right: 3px;">{{item.label}}</label>' + "    </li>" + "  </ul>" + "</div>");
        }]);
        ng.module("smart-table").controller("stTableController", ["$scope", "$parse", "$filter", "$attrs", function StTableController($scope, $parse, $filter, $attrs) {
            function copyRefs(src) {
                return src ? [].concat(src) : [];
            }
            var propertyName = $attrs.stTable;
            var displayGetter = $parse(propertyName);
            var displaySetter = displayGetter.assign;
            var safeGetter;
            var orderBy = $filter("orderBy");
            var filter = $filter("filter");
            var safeCopy = copyRefs(displayGetter($scope));
            var tableState = {
                sort: {},
                search: {},
                pagination: {
                    start: 0
                }
            };
            var pipeAfterSafeCopy = true;
            var ctrl = this;
            var lastSelected;
            function updateSafeCopy() {
                safeCopy = copyRefs(safeGetter($scope));
                if (pipeAfterSafeCopy === true) {
                    ctrl.pipe();
                }
            }
            if ($attrs.stSafeSrc) {
                safeGetter = $parse($attrs.stSafeSrc);
                $scope.$watch(function () {
                    var safeSrc = safeGetter($scope);
                    return safeSrc ? safeSrc.length : 0;
                }, function (newValue) {
                    if (newValue !== safeCopy.length) {
                        updateSafeCopy();
                    }
                });
                $scope.$watch(function () {
                    return safeGetter($scope);
                }, function (newValue, oldValue) {
                    if (newValue !== oldValue) {
                        updateSafeCopy();
                    }
                });
            }
            this.sortBy = function sortBy(predicate, reverse) {
                tableState.sort.predicate = predicate;
                tableState.sort.reverse = reverse === true;
                if (ng.isFunction(predicate)) {
                    tableState.sort.functionName = predicate.name;
                } else {
                    delete tableState.sort.functionName;
                }
                tableState.pagination.start = 0;
                return this.pipe();
            };
            this.search = function search(input, predicate, strict) {
                var predicateObject = tableState.search.predicateObject || {};
                var prop = predicate ? predicate : "$";
                input = ng.isString(input) ? input.trim() : input;
                predicateObject[prop] = input;
                if (!input) {
                    delete predicateObject[prop];
                }
                tableState.search.predicateObject = predicateObject;
                tableState.pagination.start = 0;
                tableState.search.strict = strict;
                return this.pipe();
            };
            this.pipe = function pipe() {
                var pagination = tableState.pagination;
                var filtered = tableState.search.predicateObject ? filter(safeCopy, tableState.search.predicateObject, tableState.search.strict) : safeCopy;
                if (tableState.sort.predicate) {
                    filtered = orderBy(filtered, tableState.sort.predicate, tableState.sort.reverse);
                }
                if (pagination.number !== undefined) {
                    pagination.numberOfPages = filtered.length > 0 ? Math.ceil(filtered.length / pagination.number) : 1;
                    pagination.start = pagination.start >= filtered.length ? (pagination.numberOfPages - 1) * pagination.number : pagination.start;
                    pagination.itemCount = filtered.length;
                    pagination.totalItems = safeCopy.length;
                    filtered = filtered.slice(pagination.start, pagination.start + parseInt(pagination.number));
                }
                displaySetter($scope, filtered);
            };
            this.select = function select(row, mode) {
                var rows = safeCopy;
                var index = rows.indexOf(row);
                if (index !== -1) {
                    if (mode === "single") {
                        row.isSelected = row.isSelected !== true;
                        if (lastSelected) {
                            lastSelected.isSelected = false;
                        }
                        lastSelected = row.isSelected === true ? row : undefined;
                    } else {
                        rows[index].isSelected = !rows[index].isSelected;
                    }
                }
            };
            this.slice = function splice(start, number) {
                tableState.pagination.start = start;
                tableState.pagination.number = number;
                return this.pipe();
            };
            this.tableState = function getTableState() {
                return tableState;
            };
            this.setFilterFunction = function setFilterFunction(filterName) {
                filter = $filter(filterName);
            };
            this.setSortFunction = function setSortFunction(sortFunctionName) {
                orderBy = $filter(sortFunctionName);
            };
            this.preventPipeOnWatch = function preventPipe() {
                pipeAfterSafeCopy = false;
            };
        }]).directive("stTable", function () {
            return {
                restrict: "A",
                controller: "stTableController",
                link: function (scope, element, attr, ctrl) {
                    if (attr.stSetFilter) {
                        ctrl.setFilterFunction(attr.stSetFilter);
                    }
                    if (attr.stSetSort) {
                        ctrl.setSortFunction(attr.stSetSort);
                    }
                }
            };
        });
        ng.module("smart-table").directive("stSearch", ["$timeout", function ($timeout) {
            return {
                require: "^stTable",
                scope: {
                    predicate: "=?stSearch"
                },
                link: function (scope, element, attr, ctrl) {
                    var tableCtrl = ctrl;
                    var promise = null;
                    var throttle = attr.stDelay || 400;
                    scope.$watch("predicate", function (newValue, oldValue) {
                        if (newValue !== oldValue) {
                            ctrl.tableState().search = {};
                            tableCtrl.search(element[0].value || "", newValue);
                        }
                    });
                    scope.$watch(function () {
                        return ctrl.tableState().search;
                    }, function (newValue) {
                        var predicateExpression = scope.predicate || "$";
                        if (newValue.predicateObject && newValue.predicateObject[predicateExpression] !== element[0].value) {
                            element[0].value = newValue.predicateObject[predicateExpression] || "";
                        }
                    }, true);
                    var e = element[0].tagName.toLowerCase() === "input" ? "keydown" : "change";
                    element.bind(e, function (evt) {
                        evt = evt.originalEvent || evt || window.event;
                        var target = evt.target || evt.srcElement;
                        if (promise !== null) {
                            $timeout.cancel(promise);
                        }
                        promise = $timeout(function () {
                            tableCtrl.search(target.value, scope.predicate || "");
                            promise = null;
                        }, throttle);
                    });
                }
            };
        }]);
        ng.module("smart-table").directive("stSelectRow", function () {
            return {
                restrict: "A",
                require: "^stTable",
                scope: {
                    row: "=stSelectRow"
                },
                link: function (scope, element, attr, ctrl) {
                    var mode = attr.stSelectMode || "single";
                    element.bind("click", function () {
                        scope.$apply(function () {
                            ctrl.select(scope.row, mode);
                        });
                    });
                    scope.$watch("row.isSelected", function (newValue) {
                        if (newValue === true) {
                            element.addClass("st-selected");
                        } else {
                            element.removeClass("st-selected");
                        }
                    });
                }
            };
        });
        ng.module("smart-table").directive("stSort", ["$parse", function ($parse) {
            return {
                restrict: "A",
                require: "^stTable",
                link: function (scope, element, attr, ctrl) {
                    var predicate = attr.stSort;
                    var getter = $parse(predicate);
                    var index = 0;
                    var classAscent = attr.stClassAscent || "st-sort-ascent";
                    var classDescent = attr.stClassDescent || "st-sort-descent";
                    var stateClasses = [classAscent, classDescent];
                    var sortDefault;
                    if (attr.stSortDefault) {
                        sortDefault = scope.$eval(attr.stSortDefault) !== undefined ? scope.$eval(attr.stSortDefault) : attr.stSortDefault;
                    }
                    function sort() {
                        index++;
                        predicate = ng.isFunction(getter(scope)) ? getter(scope) : attr.stSort;
                        if (index % 3 === 0 && attr.stSkipNatural === undefined) {
                            index = 0;
                            ctrl.tableState().sort = {};
                            ctrl.tableState().pagination.start = 0;
                            ctrl.pipe();
                        } else {
                            ctrl.sortBy(predicate, index % 2 === 0);
                        }
                    }
                    element.bind("click", function sortClick() {
                        if (predicate) {
                            scope.$apply(sort);
                        }
                    });
                    if (sortDefault) {
                        index = attr.stSortDefault === "reverse" ? 1 : 0;
                        sort();
                    }
                    scope.$watch(function () {
                        return ctrl.tableState().sort;
                    }, function (newValue) {
                        if (newValue.predicate !== predicate) {
                            index = 0;
                            element.removeClass(classAscent).removeClass(classDescent);
                        } else {
                            index = newValue.reverse === true ? 2 : 1;
                            element.removeClass(stateClasses[index % 2]).addClass(stateClasses[index - 1]);
                        }
                    }, true);
                }
            };
        }]);
        ng.module("smart-table").directive("stPagination", function () {
            return {
                restrict: "EA",
                require: "^stTable",
                scope: {
                    stItemsByPage: "=?",
                    stDisplayedPages: "=?",
                    stPageChange: "&",
                    txtPage: "=?"
                },
                templateUrl: function (element, attrs) {
                    if (attrs.stTemplate) {
                        return attrs.stTemplate;
                    }
                    return "template/smart-table/pagination.html";
                },
                link: function (scope, element, attrs, ctrl) {
                    scope.stItemsByPage = scope.stItemsByPage ? +scope.stItemsByPage : 10;
                    scope.stDisplayedPages = scope.stDisplayedPages ? +scope.stDisplayedPages : 5;
                    scope.pageState = {
                        txtPage: 1
                    };
                    scope.currentPage = 1;
                    scope.pages = [];
                    scope.itemCount = 0;
                    scope.totalItems = 0;
                    scope.updatePage = function (reset) {
                        if (reset && scope.pageState.txtPage === "") {
                            scope.pageState.txtPage = scope.currentPage;
                        }
                        if (scope.pageState.txtPage === "") return;
                        var newPage = +scope.pageState.txtPage;
                        if (isNaN(newPage) || newPage < 1) {
                            scope.pageState.txtPage = scope.currentPage;
                            return;
                        }
                        if (newPage > scope.numPages) {
                            newPage = scope.pageState.txtPage = scope.numPages;
                        }
                        if (newPage === scope.currentPage) return;
                        scope.selectPage(newPage);
                    };
                    function redraw() {
                        var paginationState = ctrl.tableState().pagination;
                        var start = 1;
                        var end;
                        var i;
                        var prevPage = scope.currentPage;
                        scope.currentPage = Math.floor(paginationState.start / paginationState.number) + 1;
                        scope.itemCount = paginationState.itemCount;
                        scope.totalItems = paginationState.totalItems;
                        start = Math.max(start, scope.currentPage - Math.abs(Math.floor(scope.stDisplayedPages / 2)));
                        end = start + scope.stDisplayedPages;
                        if (end > paginationState.numberOfPages) {
                            end = paginationState.numberOfPages + 1;
                            start = Math.max(1, end - scope.stDisplayedPages);
                        }
                        scope.pages = [];
                        scope.numPages = paginationState.numberOfPages;
                        for (i = start; i < end; i++) {
                            scope.pages.push(i);
                        }
                        if (prevPage !== scope.currentPage) {
                            scope.stPageChange({
                                newPage: scope.currentPage
                            });
                            scope.pageState.txtPage = scope.currentPage;
                        }
                    }
                    scope.$watch(function () {
                        return ctrl.tableState().pagination;
                    }, redraw, true);
                    scope.$watch("stItemsByPage", function (newValue, oldValue) {
                        if (newValue !== oldValue) {
                            scope.selectPage(1);
                            scope.pageState.txtPage = 1;
                        }
                    });
                    scope.$watch("stDisplayedPages", redraw);
                    scope.selectPage = function (page) {
                        if (page > 0 && page <= scope.numPages) {
                            ctrl.slice((page - 1) * scope.stItemsByPage, scope.stItemsByPage);
                            scope.pageState.txtPage = page;
                        }
                    };
                    if (!ctrl.tableState().pagination.number) {
                        ctrl.slice(0, scope.stItemsByPage);
                    }
                }
            };
        });
        ng.module("smart-table").directive("stPipe", function () {
            return {
                require: "stTable",
                scope: {
                    stPipe: "="
                },
                link: {
                    pre: function (scope, element, attrs, ctrl) {
                        if (ng.isFunction(scope.stPipe)) {
                            ctrl.preventPipeOnWatch();
                            ctrl.pipe = function () {
                                return scope.stPipe(ctrl.tableState(), ctrl);
                            };
                        }
                    },
                    post: function (scope, element, attrs, ctrl) {
                        ctrl.pipe();
                    }
                }
            };
        });
        ng.module("smart-table").directive("csSelect", function () {
            return {
                require: "^stTable",
                template: "",
                scope: {
                    row: "=csSelect"
                },
                link: function (scope, element, attr, ctrl) {
                    element.bind("change", function () {
                        scope.$apply(function () {
                            ctrl.select(scope.row, "multiple");
                        });
                    });
                    scope.$watch("row.isSelected", function (newValue) {
                        if (newValue === true) {
                            element.parent().addClass("st-selected");
                        } else {
                            element.parent().removeClass("st-selected");
                        }
                    });
                }
            };
        });
        ng.module("smart-table").directive("csSelectAll", function () {
            return {
                require: "^stTable",
                template: "",
                scope: {
                    rows: "=csSelectAll"
                },
                link: function (scope, element, attr, ctrl) {
                    element.bind("change", function (evt) {
                        scope.globalInputState = evt.target.checked;
                        scope.$apply(function () {
                            $.each(scope.rows, function (index, row) {
                                if (row.isSelected !== evt.target.checked) {
                                    ctrl.select(row, "multiple");
                                }
                            });
                        });
                    });
                    scope.$watch("row.isSelected", function (newValue) {
                        if (newValue === true) {
                            element.parent().addClass("st-selected");
                        } else {
                            element.parent().removeClass("st-selected");
                        }
                    });
                }
            };
        });
        ng.module("smart-table").directive("csCheckAll", function () {
            return {
                require: "^stTable",
                template: "",
                scope: {
                    rows: "=csCheckAll"
                },
                link: function (scope, element) {
                    element.bind("change", function (evt) {
                        scope.globalInputState = evt.target.checked;
                        scope.$apply(function () {
                            $.each(scope.rows, function (index, row) {
                                row.isChecked = evt.target.checked;
                            });
                        });
                    });
                }
            };
        });
        ng.module("smart-table").directive("stSearchOptions", function () {
            return {
                require: "^stTable",
                scope: {
                    stSearchOptions: "="
                },
                link: function (scope, ele, attr, ctrl) {
                    var table = ctrl;
                    scope.$watch("stSearchOptions.model", function (val) {
                        ctrl.search(val, scope.stSearchOptions.field, scope.stSearchOptions.strict);
                    });
                    scope.$watch("stSearchOptions.range", function (val) {
                        if (val) ctrl.search(val, val.field);
                    });
                }
            };
        });
        ng.module("smart-table").directive("stSelectDistinct", [function () {
            return {
                restrict: "E",
                require: "^stTable",
                scope: {
                    collection: "=",
                    predicate: "@",
                    predicateExpression: "="
                },
                template: '<select ng-model="selectedOption" ng-change="optionChanged(selectedOption)" ng-options="opt for opt in distinctItems"></select>',
                link: function (scope, element, attr, table) {
                    var getPredicate = function () {
                        var predicate = scope.predicate;
                        if (!predicate && scope.predicateExpression) {
                            predicate = scope.predicateExpression;
                        }
                        return predicate;
                    };
                    scope.$watch("collection", function (newValue) {
                        var predicate = getPredicate();
                        if (newValue) {
                            var temp = [];
                            scope.distinctItems = ["All"];
                            angular.forEach(scope.collection, function (item) {
                                var value = item[predicate];
                                if (value && value.trim().length > 0 && temp.indexOf(value) === -1) {
                                    temp.push(value);
                                }
                            });
                            temp.sort();
                            scope.distinctItems = scope.distinctItems.concat(temp);
                            scope.selectedOption = scope.distinctItems[0];
                            scope.optionChanged(scope.selectedOption);
                        }
                    }, true);
                    scope.optionChanged = function (selectedOption) {
                        var predicate = getPredicate();
                        var query = {};
                        query.distinct = selectedOption;
                        if (query.distinct === "All") {
                            query.distinct = "";
                        }
                        table.search(query, predicate);
                    };
                }
            };
        }]).directive("stSelectMultiple", [function () {
            return {
                restrict: "E",
                require: "^stTable",
                scope: {
                    collection: "=",
                    items: "=",
                    predicate: "@",
                    predicateExpression: "=",
                    title: "=",
                    selectedOptions: "="
                },
                templateUrl: function (element, attrs) {
                    if (attrs.stTemplate) {
                        return attrs.stTemplate;
                    }
                    return "template/smart-table/stSelectMultiple.html";
                },
                link: function (scope, element, attr, table) {
                    scope.dropdownLabel = "";
                    function getPredicate() {
                        var predicate = scope.predicate;
                        if (!predicate && scope.predicateExpression) {
                            predicate = scope.predicateExpression;
                        }
                        return predicate;
                    }
                    function getSelectedOptionValues() {
                        var selectedOptions = [];
                        angular.forEach(scope.distinctItems, function (item) {
                            if (item.selected) {
                                selectedOptions.push(item.value);
                            }
                        });
                        return selectedOptions;
                    }
                    function getSelectedOptions() {
                        var selectedOptions = [];
                        angular.forEach(scope.distinctItems, function (item) {
                            if (item.selected) {
                                selectedOptions.push(item);
                            }
                        });
                        scope.selectedOptions = selectedOptions;
                        return selectedOptions;
                    }
                    function getDropdownLabel() {
                        var allCount = scope.distinctItems.length;
                        var selected = getSelectedOptions();
                        if (allCount === selected.length || selected.length === 0) {
                            return scope.title || "All";
                        }
                        if (selected.length === 1) {
                            return selected[0].label || selected[0].value;
                        }
                        return selected.length + " items";
                    }
                    function filterChanged() {
                        scope.dropdownLabel = getDropdownLabel();
                        var predicate = getPredicate();
                        var query = {
                            matchAny: {}
                        };
                        query.matchAny.items = getSelectedOptionValues();
                        var numberOfItems = query.matchAny.items.length;
                        if (numberOfItems === 0 || numberOfItems === scope.distinctItems.length) {
                            query.matchAny.all = true;
                        } else {
                            query.matchAny.all = false;
                        }
                        table.search(query, predicate);
                    }
                    function findItemWithValue(collection, value) {
                        var found = _.find(collection, function (item) {
                            return item.value === value;
                        });
                        return found;
                    }
                    function fillDistinctItems(value, distinctItems) {
                        if (value && value.trim().length > 0 && !findItemWithValue(distinctItems, value)) {
                            var option = {
                                label: value,
                                value: value,
                                selected: true
                            };
                            distinctItems.push(option);
                        }
                    }
                    function bindCollection(collection, items) {
                        var predicate = getPredicate();
                        var distinctItems = [];
                        if (items && items.length > 0) {
                            distinctItems = items;
                        } else {
                            angular.forEach(collection, function (item) {
                                var value = item[predicate];
                                fillDistinctItems(value, distinctItems);
                            });
                        }
                        distinctItems.sort(function (obj, other) {
                            if (obj.value > other.value) {
                                return 1;
                            } else if (obj.value < other.value) {
                                return -1;
                            }
                            return 0;
                        });
                        scope.distinctItems = distinctItems;
                        scope.$watch("distinctItems", function (newVal, oldVal) {
                            filterChanged();
                        }, true);
                        filterChanged();
                    }
                    function initialize() {
                        bindCollection(scope.collection, scope.items);
                    }
                    scope.filterChanged = filterChanged;
                    initialize();
                }
            };
        }]).filter("customFilter", ["$filter", function ($filter) {
            var filterFilter = $filter("filter");
            var standardComparator = function (obj, text) {
                text = ("" + text).toLowerCase();
                return ("" + obj).toLowerCase().indexOf(text) > -1;
            };
            return function customFilter(array, expression) {
                function customComparator(actual, expected) {
                    var isBeforeActivated = expected.before;
                    var isAfterActivated = expected.after;
                    var isLower = expected.lower;
                    var isHigher = expected.higher;
                    var higherLimit;
                    var lowerLimit;
                    var itemDate;
                    var queryDate;
                    if (ng.isObject(expected)) {
                        if (expected.distinct) {
                            if (!actual || actual.toLowerCase() !== expected.distinct.toLowerCase()) {
                                return false;
                            }
                            return true;
                        }
                        if (expected.matchAny) {
                            if (expected.matchAny.all) {
                                return true;
                            }
                            if (!actual) {
                                return false;
                            }
                            for (var i = 0; i < expected.matchAny.items.length; i++) {
                                if (actual.toLowerCase() === expected.matchAny.items[i].toLowerCase()) {
                                    return true;
                                }
                            }
                            return false;
                        }
                        if (expected.before || expected.after) {
                            try {
                                if (isBeforeActivated) {
                                    higherLimit = expected.before;
                                    itemDate = new Date(actual);
                                    queryDate = new Date(higherLimit);
                                    if (itemDate > queryDate) {
                                        return false;
                                    }
                                }
                                if (isAfterActivated) {
                                    lowerLimit = expected.after;
                                    itemDate = new Date(actual);
                                    queryDate = new Date(lowerLimit);
                                    if (itemDate < queryDate) {
                                        return false;
                                    }
                                }
                                return true;
                            } catch (e) {
                                return false;
                            }
                        } else if (isLower || isHigher) {
                            if (isLower) {
                                higherLimit = expected.lower;
                                if (actual > higherLimit) {
                                    return false;
                                }
                            }
                            if (isHigher) {
                                lowerLimit = expected.higher;
                                if (actual < lowerLimit) {
                                    return false;
                                }
                            }
                            return true;
                        }
                        return true;
                    }
                    return standardComparator(actual, expected);
                }
                var output = filterFilter(array, expression, customComparator);
                return output;
            };
        }]);
    })(angular);
    angular.module("Clarity.directives").provider("About", [function () {
        this.$get = function (_this) {
            return ["Modal", "$timeout", "$q", "ClarityServices", function (Modal, $timeout, $q, ClarityServices) {
                return function (icon, title, version, patents, trademarks, copyright) {
                    var localScope;
                    var deferred = $q.defer();
                    var promise = deferred.promise;
                    localScope = Modal({
                        icon: icon,
                        applicationTitle: title,
                        version: version,
                        patents: patents,
                        trademarks: trademarks,
                        copyright: copyright,
                        title: null,
                        templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "modal-about.html",
                        className: "modal-about",
                        modalSize: "",
                        backdrop: "static",
                        "static": true,
                        keyboard: false
                    });
                    localScope.ok = function () {
                        localScope.modal.dismiss(false);
                        return deferred.resolve.apply(null, arguments);
                    };
                    localScope.$on("modal:cancelled", function () {
                        return deferred.resolve.apply(null, arguments);
                    });
                    localScope.modal.show();
                    return promise;
                };
            }];
        }(this);
        return this;
    }]);
    angular.module("Clarity.directives").directive("alert", ["$timeout", "$q", "ClarityServices", function ($timeout, $q, ClarityServices) {
        var getDefaults;
        getDefaults = function (type) {
            switch (type) {
                case "danger":
                    return ["ci-action-circle-remove", "Danger Icon"];

                case "warning":
                    return ["ci-health-warning-tri-bang", "Warning Icon"];

                case "unknown":
                    return ["", "Unknown Icon"];

                case "success":
                    return ["ci-health-floating-check", "Success Icon"];

                case "info":
                    return ["ci-info-circle-info", "Info Icon"];

                default:
                    return ["", ""];
            }
        };
        return {
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "alerts.html",
            restrict: "EA",
            transclude: true,
            scope: {
                type: "@",
                destroyAfter: "@",
                dismissable: "=?",
                icon: "@",
                label: "@"
            },
            link: function (scope, element, attrs) {
                var icon, label, _ref;
                scope.isClosed = false;
                if (scope.dismissable === null || scope.dismissable === undefined) {
                    scope.dismissable = true;
                }
                _ref = getDefaults(scope.type);
                scope._icon = scope.icon || _ref[0];
                scope._label = scope.label || _ref[1];
                if (angular.isNumber(parseInt(scope.destroyAfter)) && scope.destroyAfter > 0) {
                    $timeout(function () {
                        return scope.close();
                    }, scope.destroyAfter);
                }
                scope.$on("alert:dismiss", function () {
                    return scope.close();
                });
                element.find(".alert").on("closed.bs.alert", function () {
                    scope.$emit("alert:dismissed");
                    element.remove();
                });
                return scope.close = function () {
                    element.find(".alert").alert("close");
                    scope.isClosed = true;
                };
            }
        };
    }]).factory("Alert", ["$document", "$timeout", "$q", "$rootScope", "$sce", "$compile", "$templateCache", function ($document, $timeout, $q, $rootScope, $sce, $compile, $templateCache) {
        var alertService, getAlertsLocation, getDocument, setAlerts, baseTemplatePath;
        baseTemplatePath = "";
        getDocument = function (args, type) {
            var deferred;
            deferred = $q.defer();
            $timeout(function () {
                return setAlerts(args, type, deferred);
            }, 0);
            return deferred.promise;
        };
        alertService = function () {
            return getDocument(arguments);
        };
        alertService.danger = function () {
            return getDocument(arguments, "danger");
        };
        alertService.warning = function () {
            return getDocument(arguments, "warning");
        };
        alertService.unknown = function () {
            return getDocument(arguments, "unknown");
        };
        alertService.success = function () {
            return getDocument(arguments, "success");
        };
        alertService.flush = function (id) {
            var elLocation;
            elLocation = getAlertsLocation(id);
            return angular.forEach(elLocation.children(), function (child) {
                return angular.element(child).isolateScope().$broadcast("alert:dismiss");
            });
        };
        getAlertsLocation = function (id) {
            var alertPlaceholder;
            if (!id) {
                id = "global-alerts";
            }
            alertPlaceholder = angular.element(document.getElementById(id));
            return alertPlaceholder;
        };
        setAlerts = function (args, type, deferred) {
            var alert, el, elContent, elLocation, scope;
            alert = {
                type: type
            };
            if (angular.isString(args[0])) {
                alert.content = args[0];
                if (angular.isObject(args[1])) {
                    alert = angular.extend(alert, args[1]);
                } else if (angular.isString(args[1])) {
                    alert.location = args[1];
                    if (args[2] !== null) {
                        alert = angular.extend(alert, args[2]);
                    }
                }
            } else if (angular.isObject(args[0])) {
                alert = args[0];
            }
            elContent = $templateCache.get("" + baseTemplatePath + "alertService.html");
            el = angular.element(elContent);
            scope = $rootScope.$new();
            scope.content = $sce.trustAsHtml(alert.content);
            scope.dismissable = alert.dismissable;
            scope.label = alert.label;
            scope.id = alert.id;
            scope.type = alert.type;
            scope.icon = alert.icon;
            scope.dismissable = alert.dismissable;
            scope.destroyAfter = alert.destroyAfter;
            el = $compile(el)(scope);
            scope.$digest();
            elLocation = getAlertsLocation(alert.location);
            elLocation.append(el);
            return scope.$on("alert:dismissed", function () {
                return deferred.resolve(alert.id);
            });
        };
        return alertService;
    }]);
    angular.module("Clarity.directives").directive("datetimeDisplay", [function () {
        return {
            restrict: "E",
            template: "<span>{{displayTime}}</span>",
            scope: {
                time: "=?",
                timeFormat: "=?",
                fromDate: "=?"
            },
            transclude: false,
            replace: true,
            link: function ($scope) {
                function updateTime() {
                    var _time;
                    var _format = "";
                    var _fromDate = null;
                    if ($scope.time && moment($scope.time).isValid()) _time = $scope.time; else {
                        $scope.displayTime = "";
                        return;
                    }
                    if ($scope.timeFormat) _format = $scope.timeFormat;
                    if ($scope.fromDate) _fromDate = $scope.fromDate;
                    if (moment(_time).isValid()) {
                        if (_format !== "") {
                            $scope.displayTime = moment(_time).format(_format);
                        } else {
                            if (_fromDate === undefined || _fromDate === "") _fromDate = new Date();
                            $scope.displayTime = moment(_time).from(_fromDate, false);
                        }
                    }
                }
                $scope.$watch("time", function (value) {
                    updateTime();
                });
                $scope.$watch("timeFormat", function (value) {
                    updateTime();
                });
                updateTime();
            }
        };
    }]);
    angular.module("Clarity.directives").directive("datetimepicker", ["$timeout", "$translate", function ($timeout, $translate) {
        return {
            require: "?ngModel",
            restrict: "EA",
            scope: {
                datetimepickerOptions: "@",
                onDateChangeFunction: "&",
                onDateClickFunction: "&"
            },
            link: function ($scope, $element, $attrs, controller) {
                $element.on("dp.change", function () {
                    $timeout(function () {
                        var dtp = $element.data("DateTimePicker");
                        controller.$setViewValue(dtp.date());
                        $scope.onDateChangeFunction();
                    });
                });
                $element.on("click", function () {
                    $scope.onDateClickFunction();
                });
                $element.on("dp.show", function () {
                    $("li.picker-switch a span").remove();
                    $("li.picker-switch a").append("<p>" + $translate.instant("CLARITY_ToggleDateTime") + "</p>");
                });
                controller.$render = function () {
                    if (!!controller && !!controller.$viewValue) {
                        var result = controller.$viewValue;
                        $element.data("DateTimePicker").date(result);
                    }
                };
                $element.datetimepicker($scope.$eval($attrs.datetimepickerOptions));
            }
        };
    }]);
    angular.module("Clarity.directives").provider("Dialog", [function () {
        this.$get = function (_this) {
            return ["Modal", "$timeout", "$q", "$translate", "ClarityServices", function (Modal, $timeout, $q, $translate, ClarityServices) {
                return function (title, body, hideOk) {
                    var deferred = $q.defer();
                    var promise = deferred.promise;
                    var localScope = Modal({
                        title: title,
                        bodyText: body,
                        templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "modal-confirm.html",
                        className: "modal-confirm",
                        modalSize: "modal-sm",
                        backdrop: "static",
                        "static": true,
                        keyboard: false,
                        okText: $translate.instant("CLARITY_Yes"),
                        cancelText: hideOk === true ? $translate.instant("CLARITY_Close") : $translate.instant("CLARITY_No"),
                        hideOkButton: hideOk,
                        allowResize: false
                    });
                    localScope.ok = function () {
                        localScope.modal.dismiss(false);
                        return deferred.resolve.apply(null, arguments);
                    };
                    localScope.cancel = function () {
                        localScope.modal.dismiss(true);
                        return deferred.reject.apply(null, arguments);
                    };
                    localScope.$on("modal:cancelled", function () {
                        return deferred.reject.apply(null, arguments);
                    });
                    localScope.modal.show();
                    return promise;
                };
            }];
        }(this);
        return this;
    }]);
    angular.module("Clarity.directives").filter("htmlSafe", ["$sce", function ($sce) {
        return function (htmlCode) {
            return $sce.trustAsHtml(htmlCode);
        };
    }]).directive("errorDisplay", ["$rootScope", "$location", "$timeout", "ClarityServices", "$anchorScroll", function ($rootScope, $location, $timeout, ClarityServices, $anchorScroll) {
        return {
            restrict: "E",
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "errorDisplay.html",
            scope: {
                errors: "<",
                onClick: "&",
                onExpandCollapse: "&",
                disableScrollTo: '<?',
                scrollToTop: '<'
            },
            transclude: false,
            replace: true,
            link: function ($scope, $element, $attrs) {
                $scope._generatedId = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                    var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
                    return v.toString(16);
                });
                $scope.actions = {
                    errorClicked: function (error) {
                        if ($scope.onClick != undefined && typeof $scope.onClick === "function") {
                            $scope.onClick({
                                error: error
                            });
                        }
                    },
                    detailsClicked: function () {
                        if ($scope.onExpandCollapse != undefined && typeof $scope.onClick === "function") {
                            $scope.onExpandCollapse();
                        }
                    }
                };
                $scope.$watch("errors", function (errorList, oldList) {
                    if (!errorList) return;
                    if (errorList.length > oldList.length) {
                        $timeout(function () {
                            if ($scope.scrollToTop) {
                                window.scrollTo(0,0);
                            } else {
                                !$scope.disableScrollTo && $anchorScroll($scope._generatedId)
                            }
                        }, 10);
                    };
                    angular.forEach(errorList, function (e) {
                        if (!e.errors) e.errors = [];
                    });
                }, true);
            }
        };
    }]);
    angular.module("Clarity.directives").directive("showValidation", [function () {
        return {
            restrict: "A",
            require: "form",
            link: function (scope, element, attrs, formCtrl) {
                scope.$watch(function () {
                    return formCtrl.$invalid;
                }, function () {
                    element.find(".form-group").each(function () {
                        var $formGroup = $(this);
                        var $inputs = $formGroup.find("input[ng-model],textarea[ng-model],select[ng-model]");
                        if ($inputs.length > 0) {
                            var isInvalid = false;
                            angular.forEach($inputs, function (i, idx) {
                                if (formCtrl[i.id]) {
                                    var invalid = formCtrl[i.id].$invalid;
                                    isInvalid = isInvalid || invalid;
                                }
                            });
                            $formGroup.toggleClass("has-error", isInvalid);
                        }
                    });
                });
            }
        };
    }]);
    angular.module("Clarity.directives").directive("ipurlValidator", [function () {
        return {
            restrict: "A",
            require: "ngModel",
            link: function (scope, element, attrs, ngModel) {
                var ipPattern = /\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b/;
                var urlPattern = new RegExp("^(https?:\\/\\/)?" + "((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|" + "((\\d{1,3}\\.){3}\\d{1,3}))" + "(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*" + "(\\?[;&a-z\\d%_.~+=-]*)?" + "(\\#[-a-z\\d_]*)?$", "i");
                ngModel.$validators.ipurlValidator = function (stringInput) {
                    if (!stringInput) return true;
                    if (!stringInput.match(ipPattern)) {
                        if (!urlPattern.test(stringInput)) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                };
            }
        };
    }]);
    angular.module("Clarity.directives").directive("ipValidator", [function () {
        return {
            restrict: "A",
            require: "ngModel",
            link: function (scope, element, attrs, ngModel) {
                var ipPattern = /\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b/;
                ngModel.$validators.ipValidator = function (stringInput) {
                    if (!stringInput) return true;
                    if (!stringInput.match(ipPattern)) {
                        return false;
                    } else {
                        return true;
                    }
                };
            }
        };
    }]);
    angular.module("Clarity.directives").directive("list", ["$timeout", "$q", "ClarityServices", function ($timeout, $q, ClarityServices) {
        return {
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "list.html",
            restrict: "E",
            transclude: true,
            scope: {},
            link: function (scope, element, attrs) { }
        };
    }]).directive("listLabel", ["$timeout", "$q", "ClarityServices", function ($timeout, $q, ClarityServices) {
        return {
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "list-label.html",
            restrict: "E",
            transclude: true,
            scope: {},
            link: function (scope, element, attrs) { }
        };
    }]).directive("listValue", ["$timeout", "$q", "ClarityServices", function ($timeout, $q, ClarityServices) {
        return {
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "list-value.html",
            restrict: "E",
            transclude: true,
            scope: {},
            link: function (scope, element, attrs) { }
        };
    }]);
    angular.module("Clarity.directives").provider("Loading", [function () {
        var _this = this;
        this.spinnerColor = "#007db8";
        return {
            $get: ["Modal", "LoadingPromise", "$timeout", "$q", "ClarityServices", "$rootScope", function (Modal, LoadingPromise, $timeout, $q, ClarityServices, $rootScope) {
                var loadingScope = Modal({
                    type: "bare",
                    templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "loading.html",
                    className: "loading",
                    backdrop: "static",
                    keyboard: false
                });
                loadingScope.spinnerColor = _this.spinnerColor;
                var pushPromise = LoadingPromise(function () {
                    return loadingScope.modal.close();
                });
                return function (obj) {
                    pushPromise(obj);
                    var timeoutPromise = $timeout(function () {
                        loadingScope.modal.show();
                        $(".modal-backdrop").last().addClass("loading");
                    });
                    return $q.all(angular.isArray(obj) ? obj : [obj])["finally"](function () {
                        return $timeout.cancel(timeoutPromise);
                    });
                };
            }]
        };
    }]).factory("LoadingPromise", ["$q", function ($q) {
        var promises;
        promises = [];
        return function (done) {
            var promiseUpdated, pushPromise;
            promiseUpdated = function (promise) {
                promises.splice(promises.indexOf(promise), 1);
                if (promises.length === 0) {
                    return done();
                }
            };
            pushPromise = function (promise) {
                promise = $q.when(promise).then(function () {
                    return promiseUpdated(promise);
                }, function () {
                    return promiseUpdated(promise);
                });
                return promises.push(promise);
            };
            return function (obj) {
                if (angular.isArray(obj)) {
                    return obj.forEach(function (promise) {
                        return pushPromise(promise);
                    });
                } else {
                    return pushPromise(obj);
                }
            };
        };
    }]);
    angular.module("Clarity.directives").directive("login", ["$filter", "$timeout", "$translate", "ClarityServices", function ($filter, $timeout, $translate, ClarityServices) {
        return {
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "startScreen.html",
            restrict: "EA",
            transclude: true,
            scope: {
                config: "=?",
                onSignIn: "&"
            },
            controller: ["$scope", function ($scope) {
                if ($scope.config == null) {
                    $scope.config = {};
                }
                this.defaults = {
                    applicationName: $translate.instant("ApplicationTitle"),
                    applicationLogo: "",
                    appIcon: "icon-ui-dell",
                    copyrightIcon: "ci-logo-dell-halo-o",
                    showDomain: false,
                    showRememberMe: false,
                    rememberMe: false,
                    usernameParser: function (username) {
                        var temp;
                        username = username || "";
                        if (!$scope.config.showDomain) return {
                            username: null,
                            domain: null
                        };
                        if (username.indexOf("\\") > -1) {
                            temp = username.split("\\");
                            return {
                                domain: temp[0],
                                username: temp[1]
                            };
                        } else if (username.indexOf("@") > -1) {
                            temp = username.split("@");
                            return {
                                username: temp[0],
                                domain: temp[1]
                            };
                        } else {
                            return {
                                username: null,
                                domain: null
                            };
                        }
                    }
                };
                $scope.config = angular.extend({}, this.defaults, $scope.config);
                return this;
            }],
            link: function (scope, element, attrs) {
                var messageText;
                $timeout(function () {
                    return element[0].querySelector("#loginUsername").focus();
                });
                messageText = angular.element(element[0].querySelector(".start-screen-message"));
                scope.$watch("config.showMessage", function (show) {
                    return messageText.css("display", show ? "" : "none");
                });
                scope.$watch("config.username", function (username) {
                    var _base, _ref;
                    _ref = typeof (_base = scope.config).usernameParser === "function" ? _base.usernameParser(username) : void 0,
                    scope.parsedUsername = _ref.username, scope.parsedDomain = _ref.domain;
                    if (scope.parsedDomain) {
                        return scope.config.domain = scope.parsedDomain;
                    }
                });
                return scope.signIn = function () {
                    var data, onSignInPromise;
                    data = {
                        username: scope.parsedUsername || scope.config.username,
                        password: scope.config.password,
                        domain: scope.config.domain,
                        rememberMe: scope.config.rememberMe
                    };
                    scope.$emit("startScreen:signIn", data);
                    onSignInPromise = scope.onSignIn({
                        userData: data
                    });
                    if ((onSignInPromise != null ? onSignInPromise["finally"] : void 0) != null) {
                        scope.config.isSpinning = true;
                        return onSignInPromise["finally"](function () {
                            return scope.config.isSpinning = false;
                        });
                    }
                };
            }
        };
    }]);
    angular.module("Clarity.directives").directive("lookup", ["$filter", function ($filter) {
        return {
            restrict: "E",
            template: "<span>{{displayText}}</span>",
            scope: {
                property: "=property",
                label: "=label",
                lookupvalue: "=lookupvalue",
                list: "=list"
            },
            transclude: true,
            replace: true,
            link: function ($scope, $element, $attrs) {
                if ($scope.lookupvalue == undefined) $scope.lookupvalue = "";
                if ($scope.list == undefined) $scope.list = [];
                if ($scope.property == undefined) $scope.property = "";
                if ($scope.label == undefined) $scope.label = "";
                $scope.displayText = "";
                $scope.updateText = function () {
                    var filterArgs = $scope.lookupvalue;
                    if ($scope.property != "") {
                        filterArgs = {};
                        filterArgs[$scope.property] = $scope.lookupvalue;
                    }
                    var filteredItems = $filter("filter")($scope.list, filterArgs, true);
                    if (filteredItems.length > 0 && $scope.label != "" && filteredItems[0][$scope.label] != undefined) {
                        $scope.displayText = filteredItems[0][$scope.label];
                    } else {
                        $scope.displayText = "";
                    }
                };
                $scope.$watch("lookupvalue", function (newValue, oldValue) {
                    $scope.updateText();
                });
                $scope.$watch("list", function (newValue, oldValue) {
                    $scope.updateText();
                });
                $scope.$watch("property", function (newValue, oldValue) {
                    $scope.updateText();
                });
                $scope.$watch("label", function (newValue, oldValue) {
                    $scope.updateText();
                });
            }
        };
    }]);
    angular.module("Clarity.directives").directive("masthead", ["$rootScope", "$location", "$window", "$http", "ClarityServices", function ($rootScope, $location, $window, $http, ClarityServices) {
        return {
            restrict: "E",
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "masthead.html",
            scope: {
                applicationTitle: "=",
                menuItems: "=",
                hideTopNav: "=",
                hideSearch: "=",
                hideAlerts: "=",
                currentUserName: "=",
                onHome: "&",
                onLogout: "&",
                onHelp: "&",
                onMenuItem: "&",
                iconClass: "=?",
                pinMenu: "=?"
            },
            transclude: {
                menu: "?mastheadMenu",
                menuleft: "?mastheadMenuLeft",
                header: "?mastheadHeader",
                pinnedmenu: "?mastheadPinnedMenu"
            },
            replace: true,
            link: function ($scope, $element, $attrs) {
                if (!$scope.iconClass) {
                    $scope.iconClass = "ci-logo-dell-halo-o";
                } else {
                    $scope.iconClass = $scope.iconClass;
                }
                $scope.data = {
                    showNavigation: false,
                    pinnav: false,
                    showPinNavigation: new Boolean(),
                    showTopMenuButton: new Boolean(),
                    showpinnednav: "dontshow"
                };
                if ($scope.pinMenu === true) {
                    $scope.data.showpinnednav = "show";
                    $scope.data.showPinNavigation = true;
                    $scope.data.showTopMenuButton = false;
                } else {
                    $scope.data.showpinnednav = "dontshow";
                    $scope.data.showPinNavigation = false;
                    $scope.data.showTopMenuButton = true;
                }
                $scope.actions = {
                    refresh: function () { },
                    home: function () {
                        if ($scope.onHome != undefined && typeof $scope.onHome === "function") $scope.onHome();
                    },
                    menuClick: function (menuItem) {
                        if ($scope.onMenuItem != undefined && typeof $scope.onMenuItem === "function") $scope.onMenuItem();
                    },
                    logout: function () {
                        if ($scope.onLogout != undefined && typeof $scope.onLogout === "function") $scope.onLogout();
                    },
                    help: function () {
                        if ($scope.onHelp != undefined && typeof $scope.onHelp === "function") $scope.onHelp();
                    },
                    pinnav: function () {
                        $scope.data.pinnav = false;
                    }
                };
                $scope.clearPopovers = function () {
                    $(".popover.in").remove();
                };
                $(".userButton").on("click", function (e) {
                    $rootScope.$emit("MenuTop::Hide");
                    $rootScope.$emit("AlertMenu::Hide");
                });
                $scope.$watch("menuItems", $scope.actions.refresh, true);
                $rootScope.$on("$locationChangeStart", function () {
                    $scope.data.showNavigation = false;
                });
                $rootScope.$on("$routeChangeSuccess", function () {
                    $scope.clearPopovers();
                });
                $scope.initialize = function () { };
                $scope.initialize();
            }
        };
    }]);
    angular.module("Clarity.directives").directive("mastheadAlerts", ["$rootScope", "$location", "$timeout", "ClarityServices", "$interval", "$q", function ($rootScope, $location, $timeout, ClarityServices, $interval, $q) {
        return {
            restrict: "E",
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "mastheadAlerts.html",
            scope: {
                alerts: "=",
                showAlerts: "=?",
                firstLoad: "=?"
            },
            transclude: {
                alertsmenu: "?alertsMenu"
            },
            replace: true,
            link: function ($scope, $element, $attrs) {
                $scope.data = {
                    errors: 0,
                    showAlerts: $scope.showAlerts || false,
                    firstLoad: $scope.firstLoad || true,
                    alerts: $scope.alerts
                };
                $scope.actions = {
                    hideAlertDetails: function () {
                        $scope.$evalAsync(function () {
                            $scope.data.showAlerts = false;
                        });
                    },
                    hideNav: function () {
                        $rootScope.$emit("MenuTop::Hide");
                    },
                    redAlertAnimate: function () {
                        $(".alertButton").addClass("criticalAlert");
                        $(".alertButton").removeClass("criticalAlertFade");
                        $timeout(function () {
                            $(".alertButton").addClass("criticalAlertFade");
                            $(".alertButton").removeClass("criticalAlert");
                            $scope.data.showbadge = true;
                        }, 4e3);
                    },
                    yellowAlertAnimate: function () {
                        $(".alertButton").addClass("warningAlert");
                        $(".alertButton").removeClass("warningAlertFade");
                        $timeout(function () {
                            $(".alertButton").addClass("warningAlertFade");
                            $(".alertButton").removeClass("warningAlert");
                            $scope.data.showbadge = true;
                        }, 4e3);
                    }
                };
                $scope.refreshAlerts = function (newAlerts, oldAlerts) {
                    $scope.data.errors = 0;
                    newAlerts.forEach(function (alert, index) {
                        if (alert.severity === "CRITICAL" || alert.severity === "WARNING") {
                            $scope.data.errors++;
                        }
                    });
                    $(".alertButton").removeClass("criticalAlertFade");
                    $(".alertButton").removeClass("warningAlertFade");
                    var duplicateAlerts = [];
                    if ($scope.data.firstLoad) {
                        $scope.data.firstLoad = false;
                        var criticalAlerts = 0;
                        var warningAlerts = 0;
                        var alertCounterArray = [];
                        alertCounterArray.push(newAlerts.forEach(function (freshalert, index) {
                            if (freshalert.severity === "CRITICAL") {
                                criticalAlerts++;
                            }
                            if (freshalert.severity === "WARNING") {
                                warningAlerts++;
                            }
                        }));
                        if (criticalAlerts !== 0) {
                            $scope.actions.redAlertAnimate();
                        }
                        if (criticalAlerts === 0 && warningAlerts !== 0) {
                            $scope.actions.yellowAlertAnimate();
                        }
                    } else {
                        newAlerts.forEach(function (alert, index) {
                            oldAlerts.forEach(function (oldalert, oldindex) {
                                if (alert.id == oldalert.id) {
                                    duplicateAlerts.push(alert);
                                }
                            });
                        });
                        duplicateAlerts.forEach(function (duplicate, index) {
                            newAlerts.forEach(function (alert, dupindex) {
                                if (duplicate.id == alert.id) {
                                    newAlerts.splice(dupindex, 1);
                                }
                            });
                        });
                        var criticalAlerts = 0;
                        var warningAlerts = 0;
                        newAlerts.forEach(function (freshalert, index) {
                            if (freshalert.severity === "CRITICAL") {
                                criticalAlerts++;
                            }
                            if (freshalert.severity === "WARNING") {
                                warningAlerts++;
                            }
                        });
                        if (criticalAlerts >= 1) {
                            $scope.actions.redAlertAnimate();
                        }
                        if (criticalAlerts === 0 && warningAlerts >= 1) {
                            $scope.actions.yellowAlertAnimate();
                        }
                    }
                };
                $scope.$watchCollection("alerts", function (newAlerts, oldAlerts) {
                    var newAlertsCopy = [];
                    var oldAlertsCopy = [];
                    newAlertsCopy = angular.copy(newAlerts);
                    oldAlertsCopy = angular.copy(oldAlerts);
                    $scope.refreshAlerts(newAlertsCopy, oldAlertsCopy);
                });
                $rootScope.$on("AlertMenu::Hide", function () {
                    $scope.actions.hideAlertDetails();
                });
                $scope.initialize = function () {
                    $scope.refreshAlerts($scope.data.alerts);
                };
                $scope.initialize();
                $rootScope.$on("$locationChangeStart", function () {
                    $scope.data.showAlerts = false;
                });
            }
        };
    }]);
    angular.module("Clarity.directives").directive("mastheadSearch", ["$rootScope", "$location", "$timeout", "$resource", "ClarityServices", function ($rootScope, $location, $timeout, $resource, ClarityServices) {
        return {
            restrict: "E",
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "mastheadSearch.html",
            scope: {
                onSearch: "&"
            },
            transclude: {
                searchresults: "?searchResults"
            },
            replace: true,
            link: function ($scope, $element, $attrs) {
                var timeoutPromise;
                var delayInMs = 650;
                $scope.data = {
                    showResults: false,
                    noResults: false,
                    searchTerm: ""
                };
                $scope.actions = {
                    clearSearch: function (clearTerm) {
                        $scope.$evalAsync(function () {
                            if (clearTerm) {
                                $scope.data.showResults = false;
                                $scope.data.searchTerm = "";
                                $scope.data.noResults = false;
                                $("#searchBar").removeClass("on");
                                $("#searchicon").css("display", "inline");
                                $("#searchiconfocus").css("display", "none");
                                $("#searchBar").blur();
                            }
                        });
                    },
                    searchChange: function () {
                        $timeout.cancel(timeoutPromise);
                        timeoutPromise = $timeout(function () {
                            $scope.actions.executeSearch();
                        }, delayInMs);
                    },
                    executeSearch: function () {
                        var currentTerm = $scope.data.searchTerm.trim();
                        if (currentTerm === undefined || currentTerm === "") {
                            $scope.actions.clearSearch(true);
                            $scope.data.showResults = false;
                            $scope.data.noResults = false;
                        } else {
                            $scope.onSearch({
                                searchTerm: currentTerm
                            });
                            $scope.data.showResults = true;
                            $scope.data.noResults = false;
                        }
                    }
                };
                $(".searchField").on("click", function (e) {
                    $rootScope.$emit("MenuTop::Hide");
                });
                $scope.initialize = function () {
                    $("#searchicon").css("display", "inline");
                    $("#searchiconfocus").css("display", "none");
                    $("#searchBar").focus(function () {
                        $("#searchBar").addClass("on");
                        $("#searchicon").css("display", "none");
                        $("#searchiconfocus").css("display", "inline");
                    });
                    $("#searchBar").focusout(function () {
                        if (!$scope.data.searchTerm || $scope.data.searchTerm.length == 0) {
                            $("#searchBar").removeClass("on");
                            $("#searchicon").css("display", "inline");
                            $("#searchiconfocus").css("display", "none");
                        }
                    });
                };
                $scope.initialize();
                function unwrap(fn) {
                    return fn;
                }
                $scope.$watch("data.searchTerm", $scope.actions.searchChange);
                $rootScope.$on("$locationChangeStart", function () {
                    $scope.actions.clearSearch(true);
                });
                $("body").on("click", function (e) {
                    if (!$(e.target).parents(".globalSearch").length) {
                        $scope.actions.clearSearch(true);
                    }
                });
            }
        };
    }]);
    angular.module("Clarity.directives").directive("mastheadTopMenu", ["$rootScope", "$log", "$compile", "$location", "$window", "$http", "$route", "Alert", "ClarityServices", function ($rootScope, $log, $compile, $location, $window, $http, $route, Alert, ClarityServices) {
        return {
            restrict: "E",
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "mastheadTopMenu.html",
            transclude: {
                extraleft: "?menuButtonsLeft",
                extra: "?menuButtons",
                extrapinned: "?menuPinnedButtons"
            },
            replace: true,
            link: function ($scope, $element, $attrs) {
                $scope.actions = {
                    hideMenus: function () {
                        $rootScope.$emit("AlertMenu::Hide");
                    },
                    hideNav: function () {
                        $scope.$evalAsync(function () {
                            $scope.data.showNavigation = false;
                            $scope.data.showPinNavigation = $scope.data.showpinnednav == "show";
                            $("#pinned-nav").find('[aria-expanded="true"]').collapse("hide");
                        });
                    },
                    toggleNav: function () {
                        $scope.data.showNavigation = !($scope.data.showNavigation || false);
                        $scope.data.showPinNavigation = false;
                    },
                    pinNav: function () {
                        $scope.data.showpinnednav = "show";
                        Alert.flush();
                        $scope.actions.setMenuPosition();
                        $scope.data.pinnav = true;
                    },
                    unpinNav: function () {
                        $scope.data.showpinnednav = "dontshow";
                        Alert.flush();
                        $scope.actions.setMenuPosition();
                        $scope.data.pinnav = false;
                    },
                    setMenuPosition: function () {
                        if ($scope.data.showpinnednav === "show") {
                            $scope.data.showPinNavigation = true;
                            $rootScope.pinnav = true;
                            $scope.data.showNavigation = false;
                            $scope.data.showTopMenuButton = false;
                            $rootScope.$broadcast("pin");
                        } else {
                            $scope.data.showPinNavigation = false;
                            $rootScope.pinnav = false;
                            $scope.data.showTopMenuButton = true;
                            $rootScope.$broadcast("unpin");
                        }
                    },
                    go: function (path) {
                        $location.path(path);
                    },
                    refresh: function () { }
                };
                $rootScope.$on("MenuTop::Hide", function () {
                    $scope.actions.hideNav();
                });
                $rootScope.$on("$locationChangeSuccess", function () {
                    $scope.actions.hideNav();
                });
                $rootScope.$on("modalOpen", function () {
                    $scope.actions.hideNav();
                });
                $scope.$watch("menuItems", function () {
                    $scope.actions.refresh();
                }, true);
                $scope.initialize = function () {
                    Alert.flush();
                    $scope.actions.setMenuPosition();
                };
                $scope.initialize();
            }
        };
    }]);
    angular.module("Clarity.directives").directive("mastheadUser", ["$rootScope", "ClarityServices", function ($rootScope, ClarityServices) {
        return {
            restrict: "E",
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "mastheadUser.html",
            scope: {
                userAvatar: "=",
                userName: "="
            },
            transclude: true,
            replace: true,
            link: function ($scope, $element, $attrs) { }
        };
    }]);
    angular.module("Clarity.directives").directive("ngMatch", ["$rootScope", function ($rootScope) {
        return {
            require: "ngModel",
            link: function (scope, elem, attrs, ctrl) {
                var me = attrs.ngModel;
                var matchTo = attrs.ngMatch;
                scope.$watchGroup([me, matchTo], function (value) {
                    var isValid = value[0] === value[1];
                    ctrl.$setValidity("ngmatch", isValid);
                });
            }
        };
    }]);
    angular.module("Clarity.directives").provider("Messagebox", [function () {
        this.$get = function (_this) {
            return ["Modal", "$timeout", "$q", "ClarityServices", function (Modal, $timeout, $q, ClarityServices) {
                return function (title, body) {
                    var localScope;
                    var deferred = $q.defer();
                    var promise = deferred.promise;
                    localScope = Modal({
                        title: title,
                        bodyText: body,
                        templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "modal-messagebox.html",
                        className: "modal-messagebox",
                        modalSize: "modal-sm",
                        backdrop: "static",
                        "static": true,
                        keyboard: false
                    });
                    localScope.ok = function () {
                        localScope.modal.dismiss(false);
                        return deferred.resolve.apply(null, arguments);
                    };
                    localScope.$on("modal:cancelled", function () {
                        return deferred.resolve.apply(null, arguments);
                    });
                    localScope.modal.show();
                    return promise;
                };
            }];
        }(this);
        return this;
    }]);
    angular.module("Clarity.directives").provider("Modal", [function () {
        this.$get = function (_this) {
            return ["$rootScope", "$compile", "$document", "$templateCache", "$log", "$timeout", "$controller", "$sce", "ClarityServices", function ($rootScope, $compile, $document, $templateCache, $log, $timeout, $controller, $sce, ClarityServices) {
                return function (options) {
                    var config, defaultConfig, dom, el, scope;
                    if (options == null) {
                        options = {};
                    }
                    defaultConfig = {
                        backdrop: "static",
                        visible: false,
                        keyboard: true,
                        show: true,
                        fullscreen: false,
                        allowResize: true
                    };
                    if (options.templateUrl) {
                        options.body = "";
                    }
                    if (!options.id) {
                        options.id = ClarityServices.NewGuid();
                    }
                    config = angular.extend({}, defaultConfig, options);
                    if (!config.className) {
                        config.className = "";
                    }
                    if (!config.modalSize) {
                        config.modalSize = "";
                    }
                    config.classes = [];
                    var templateUrl = "" + ClarityServices.Settings.BaseTemplatePath + "modal.html";
                    dom = $templateCache.get(templateUrl);
                    scope = angular.extend((config.scope || $rootScope).$new(), {
                        modal: config
                    });
                    if (config.controller) {
                        $controller(config.controller, {
                            $scope: scope
                        });
                    }
                    el = $compile(dom)(scope);
                    scope.modal.show = function () {
                        $rootScope.$broadcast("modalOpen", options.title);
                        el[0].id = "myModal-" + config.id;
                        $("#modalArea").append(el);
                        $("#myModal-" + config.id).modal(scope.modal);
                        window.setTimeout(function () {
                            $(window).trigger("resize");
                        }, 500);
                    };
                    if (scope.modal.visible === true) {
                        scope.modal.visible = false;
                        scope.modal.show();
                    }
                    scope.renderHTML = function (html) {
                        return $sce.trustAsHtml(html);
                    };
                    scope._onComplete = function (childScope) {
                        if (childScope && scope.modal.onComplete) {
                            scope.modal.onComplete(childScope, scope.modal);
                        } else {
                            if (scope.modal.onComplete) {
                                scope.modal.onComplete(scope);
                            }
                        }
                    };
                    scope._onCancel = function () {
                        if (scope.modal.onCancel) {
                            scope.modal.onCancel(scope.modal);
                        } else {
                            scope.modal.dismiss();
                        }
                    };
                    scope._onHelp = function () {
                        if (scope.modal.onHelp) {
                            scope.modal.onHelp(scope.modal);
                        }
                    };
                    scope.modal.dismiss = function () {
                        $rootScope.$broadcast("modalClose", options.title);
                        $("#myModal-" + config.id).off("hide.bs.modal");
                        $("#myModal-" + config.id).on("hidden.bs.modal", function () {
                            $("#myModal-" + config.id).removeData();
                            $("#myModal-" + config.id).remove();
                            var existingModals = $(".modal.fade").length;
                            if (existingModals > 0) {
                                $("body").addClass("modal-open");
                            } else {
                                $(".modal-backdrop").remove();
                            }
                        });
                        $("#myModal-" + config.id).modal("hide");
                        scope.$destroy();
                    };
                    scope.modal.cancel = function () {
                        scope._onCancel();
                    };
                    scope.modal.close = function (childScope) {
                        scope._onComplete(childScope);
                        scope.modal.dismiss();
                    };
                    return scope;
                };
            }];
        }(this);
        return this;
    }]).directive("draggable", ["$document", function ($document) {
        return function (scope, element, attr) {
            var startX = 0, startY = 0, x = 0, y = 0;
            element.css({
                position: "relative",
                cursor: "pointer",
                display: "block"
            });
            element.on("mousedown", function (event) {
                event.preventDefault();
                startX = event.screenX - x;
                startY = event.screenY - y;
                $document.on("mousemove", mousemove);
                $document.on("mouseup", mouseup);
            });
            function mousemove(event) {
                y = event.screenY - startY;
                x = event.screenX - startX;
                element.css({
                    top: y + "px",
                    left: x + "px"
                });
            }
            function mouseup() {
                $document.off("mousemove", mousemove);
                $document.off("mouseup", mouseup);
            }
        };
    }]);
    angular.module("Clarity.directives").directive("popover", ["$compile", "$timeout", "$templateCache", function ($compile, $timeout, $templateCache) {
        return {
            restrict: "A",
            transclude: false,
            replace: false,
            link: function ($scope, $element, $attrs, $ctrl) {
                $timeout(function () {
                    var popoverClass = $attrs["popoverClass"] || "";
                    var delayShow = $attrs["delayShow"] || "0";
                    var delayHide = $attrs["delayHide"] || "0";
                    $scope.$on("$destroy", function () {
                        $element.popover("destroy");
                    });
                    $element.popover({
                        trigger: $attrs["trigger"] || "click",
                        container: $attrs["container"] || "body",
                        placement: $attrs["placement"] || "auto right",
                        title: $attrs["title"] || "&nbsp;",
                        html: true,
                        content: function () {
                            var content = "";
                            var compiledElements;
                            if ($attrs["content"]) {
                                content = $attrs["content"];
                                compiledElements = $compile(content)($scope);
                            }
                            if ($attrs["contentTemplate"]) {
                                content = $attrs["contentTemplate"];
                                var template = $templateCache.get(content);
                                compiledElements = $compile(template)($scope);
                            }
                            if (content === "" || !compiledElements) {
                                throw new Error("Unable to locate content or content template for Popover.");
                            }
                            $scope.$apply();
                            return compiledElements;
                        },
                        template: '<div class="popover ' + ($attrs["trigger"] || "click") + " " + popoverClass + '" role="tooltip">' + '<div class="arrow"></div>' + '<div class="popover-header" style="position: absolute; top: 10px; right: 5px; z-index: 1;">' + '<button type="button" id="close" class="close">&times;</button>' + "</div>" + '<div class="popover-title"></div>' + '<div class="popover-content"></div>' + "</div>",
                        delay: {
                            hide: delayHide,
                            show: delayShow
                        }
                    }).on("show.bs.popover", function () {
                        var disable = $attrs["disablePopover"];
                        if (disable === "true") return false;
                        return true;
                    });
                }, 250);
            }
        };
    }]);
    "use strict";
    (function (angular) {
        function whichTransitionEnd(element) {
            var transitions = {
                WebkitTransition: "webkitTransitionEnd",
                MozTransition: "transitionend",
                OTransition: "oTransitionEnd otransitionend",
                transition: "transitionend"
            };
            for (var t in transitions) {
                if (element.style[t] !== undefined) {
                    return transitions[t];
                }
            }
        }
        var mdl = angular.module("Clarity.directives");
        mdl.provider("progressButtonConfig", ProgressButtonConfig);
        function ProgressButtonConfig() {
            var profiles = {};
            var defaultProfile = null;
            var defaultProgressButtonConfig = {
                style: "fill",
                direction: "horizontal",
                randomProgress: true
            };
            this.profile = function (profileName, options) {
                if (arguments.length == 1) {
                    if (defaultProfile) throw Error("Default profile already set.");
                    defaultProfile = profileName;
                } else {
                    if (profiles[profileName]) throw Error("Profile [" + profileName + "] aready set.");
                    profiles[profileName] = options;
                }
            };
            this.$get = function () {
                return {
                    getProfile: function (profileName) {
                        if (profileName && profiles[profileName]) {
                            return profiles[profileName];
                        } else {
                            return defaultProfile || defaultProgressButtonConfig;
                        }
                    }
                };
            };
        }
        mdl.directive("progressButton", ProgressButton);
        ProgressButton.$inject = ["$q", "progressButtonConfig", "$interval"];
        function ProgressButton($q, progressButtonConfig, $interval) {
            return {
                restrict: "A",
                transclude: true,
                scope: {
                    progressButton: "&",
                    pbStyle: "@",
                    pbDirection: "@",
                    pbProfile: "@",
                    indeterminate: "@",
                    successTimeout: "@"
                },
                template: '<span class="content" ng-transclude></span>' + '<span class="progress">' + '<span class="progress-inner" ng-style="progressStyles" ng-class="{ notransition: !allowProgressTransition }"></span>' + "</span>",
                controller: function () { },
                link: function ($scope, $element, $attrs) {
                    _configure();
                    var transitionEndEventName = whichTransitionEnd($element[0]);
                    var progressProperty = $scope.pbDirection == "vertical" ? "height" : "width";
                    if ($scope.pbPerspective) {
                        var wrap = angular.element('<span class="progress-wrap"></span>');
                        wrap.append($element.children());
                        $element.append(wrap);
                        $element.addClass("progress-button-perspective");
                    }
                    $scope.progressStyles = {};
                    $scope.disabled = false;
                    $scope.allowProgressTransition = false;
                    $element.addClass("progress-button");
                    $element.addClass("progress-button-dir-" + $scope.pbDirection);
                    $element.addClass("progress-button-style-" + $scope.pbStyle);
                    $scope.$watch("disabled", function (newValue) {
                        $element.toggleClass("disabled", newValue);
                    });
                    $element.on("click", function () {
                        $scope.$apply(function () {
                            if ($scope.disabled) return;
                            $scope.disabled = true;
                            $element.addClass("state-loading");
                            $scope.allowProgressTransition = true;
                            var interval = null;
                            $q.when($scope.progressButton()).then(function success() {
                                setProgress(1);
                                interval && $interval.cancel(interval);
                                doStop(1);
                            }, function error() {
                                interval && $interval.cancel(interval);
                                doStop(-1);
                            }, function notify(arg) {
                                !$scope.pbRandomProgress && !$scope.indeterminate && setProgress(arg);
                            });
                            if ($scope.pbRandomProgress && !$scope.indeterminate) {
                                interval = runProgressInterval();
                            } else if ($scope.indeterminate) {
                                interval = runIndeterminateProgressInterval();
                            }
                        });
                    });
                    function _configure() {
                        var profile = progressButtonConfig.getProfile($scope.pbProfile);
                        $scope.pbStyle = $scope.pbStyle || profile.style || "fill";
                        if ($scope.pbStyle != "lateral-lines") {
                            $scope.pbDirection = $scope.pbDirection || profile.direction || "horizontal";
                        } else {
                            $scope.pbDirection = "vertical";
                        }
                        $scope.pbPerspective = $scope.pbStyle.indexOf("rotate") == 0 || $scope.pbStyle.indexOf("flip-open") == 0;
                        $scope.pbRandomProgress = $attrs.pbRandomProgress ? $attrs.pbRandomProgress !== "false" : profile.randomProgress || true;
                    }
                    function setProgress(val) {
                        $scope.progressStyles[progressProperty] = 100 * val + "%";
                    }
                    function runProgressInterval() {
                        var progress = 0;
                        return $interval(function () {
                            progress += (1 - progress) * Math.random() * .5;
                            setProgress(progress);
                        }, 200);
                    }
                    function runIndeterminateProgressInterval() {
                        var progress = 0;
                        return $interval(function () {
                            if (progress >= 1) {
                                progress = -.25;
                            } else {
                                progress += .01;
                            }
                            setProgress(progress);
                        }, 20);
                    }
                    function enable() {
                        $scope.$apply(function () {
                            $scope.disabled = false;
                        });
                    }
                    function doStop(status) {
                        function onOpacityTransitionEnd(ev) {
                            if (ev.propertyName !== "opacity" && (!ev.originalEvent || ev.originalEvent.propertyName !== "opacity")) return;
                            $element.off(transitionEndEventName, onOpacityTransitionEnd);
                            $scope.$apply(function () {
                                $scope.allowProgressTransition = false;
                                setProgress(0);
                                $scope.progressStyles.opacity = 1;
                            });
                        }
                        if (transitionEndEventName) {
                            $scope.progressStyles.opacity = 0;
                            $element.on(transitionEndEventName, onOpacityTransitionEnd);
                        }
                        if (typeof status === "number") {
                            var statusClass = status >= 0 ? "state-success" : "state-error";
                            $element.addClass(statusClass);
                            setTimeout(function () {
                                $element.removeClass(statusClass);
                                enable();
                            }, $scope.successTimeout);
                        } else {
                            enable();
                        }
                        $element.removeClass("state-loading");
                    }
                }
            };
        }
    })(angular);
    angular.module("Clarity.directives").directive("spinner", ["ClarityServices", function (ClarityServices) {
        return {
            restrict: "EA",
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "spinner.html",
            scope: {
                size: "@",
                color: "@"
            },
            link: function (scope, element) {
                if (scope.color == null) {
                    scope.color = "#007db8";
                }
                if (scope.size == null) {
                    scope.size = "10px";
                }
                element.css({
                    height: scope.size,
                    width: scope.size
                });
            }
        };
    }]);
    angular.module("Clarity.directives").directive("statusicon", [function () {
        return {
            restrict: "E",
            template: "<span><span ng-class=\"{'ci-stack': iconClassOverlay !== null }\">" + '<i ng-if="iconClassOverlay !== null" class="ci-stack-1x {{iconClass}}"></i>' + '<i ng-if="iconClassOverlay === null" class="{{iconClass}}"></i>' + '<i ng-if="iconClassOverlay" class="ci-stack-1x {{iconClassOverlay}}"></i>' + '<span class="sr-only">{{state}}</span>' + "</span>" + "<span ng-transclude></span></span>",
            scope: {
                state: "=value"
            },
            transclude: true,
            replace: true,
            link: function (scope, $element, $attrs) {
                function updateIcon() {
                    scope.iconClass = null;
                    scope.iconClassOverlay = null;
                    scope.state = scope.state || "";
                    switch (scope.state.toUpperCase()) {
                        case "SUCCESS":
                        case "OK":
                        case "GREEN":
                        case "HEALTHY":
                            scope.iconClass = "text-white ci-state-shape-blank-square";
                            scope.iconClassOverlay = "text-success ci-health-square-check";
                            break;

                        case "CRITICAL":
                        case "RED":
                            scope.iconClass = "text-white ci-state-shape-blank-circle";
                            scope.iconClassOverlay = "text-danger ci-action-circle-remove";
                            break;

                        case "WARNING":
                        case "YELLOW":
                            scope.iconClass = "text-black ci-state-shape-blank-tri";
                            scope.iconClassOverlay = "text-warning ci-health-warning-tri-bang";
                            break;

                        case "INFO":
                        case "BLUE":
                            scope.iconClass = "text-white ci-state-shape-blank-circle";
                            scope.iconClassOverlay = "text-primary ci-info-circle-info";
                            break;

                        case "INPROGRESS":
                        case "IN PROGRESS":
                            scope.iconClass = "text-white ci-state-shape-blank-circle";
                            scope.iconClassOverlay = "text-primary ci-state-standby-clock";
                            break;

                        case "STANDBY":
                        case "PENDING":
                            scope.iconClassOverlay = "text-light-gray ci-state-standby-clock";
                            break;

                        case "CANCELLED":
                        case "CANCELED":
                            scope.iconClass = "text-light-gray ci-action-circle-remove-slash-o";
                            break;

                        case "MAINTENANCE":
                            scope.iconClass = "text-dark-gray ci-state-shape-blank-circle";
                            scope.iconClassOverlay = "text-gray ci-action-circle-wrench";
                            break;

                        case "UNKNOWN":
                            scope.iconClass = "text-dark-gray ci-state-shape-blank-square";
                            scope.iconClassOverlay = "text-gray ci-state-unknown-slash";
                            break;

                        case "":
                            scope.iconClass = null;
                            scope.iconClassOverlay = null;
                            break;
                    }
                }
                scope.$watch("state", function (value) {
                    if (!value) return; else updateIcon();
                });
            }
        };
    }]);
    angular.module("Clarity.directives").directive("topology", ["$timeout", "$filter", "ClarityServices", function ($timeout, $filter, ClarityServices) {
        return {
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "topologyTemplate.html",
            restrict: "E",
            transclude: true,
            scope: {
                config: "="
            },
            link: function ($scope, $element, $attrs) {
                var rootShapeIndex = 0;
                $scope.data = {
                    svgwidth: 0,
                    svgheight: 0,
                    totalSVGs: 0,
                    svgTotalWidth: 0,
                    containerWidth: 0,
                    svgRowWidth: 0,
                    maxRow: 1,
                    maxColumns: 1,
                    wrapIncrement: 1,
                    directiveZoom: 1,
                    portList: [],
                    displayErrors: false,
                    originalConnections: [],
                    hifi: false,
                    layout: "wrap",
                    zoom: 1,
                    columnWidth: 0,
                    rowHeight: 0,
                    shapeClick: null,
                    connectionClick: null,
                    shapeHover: null,
                    connectionHover: null,
                    flowPadding: 100,
                    flowMarginRightSVG: 100,
                    wrapPadding: 100,
                    wrapMarginRightSVG: 100,
                    wrapSpaceBetweenRows: 100,
                    gridContainerHeight: 800,
                    gridPadding: 100,
                    lineShape1Distance: 25,
                    lineShape2Distance: 15,
                    lineIncrement: 3,
                    lineSegments: "direct"
                };
                $scope.timeout = null;
                $scope.draw = function (oldConfig, newConfig) {
                    if ($scope.timeout) $timeout.cancel($scope.timeout);
                    $scope.timeout = $timeout(function () {
                        $scope.init();
                        if (oldConfig && newConfig && oldConfig.layout != newConfig.layout) {
                            var text = $("svg text").hide().show();
                        }
                    }, 500);
                };
                $scope.init = function () {
                    if (!$scope.config.topologyTemplates || $scope.config.topologyTemplates.length == 0) return;
                    if (!$scope.config.topologyShapes || $scope.config.topologyShapes.length == 0) return;
                    $scope.data = angular.extend($scope.data, $scope.config);
                    if ($scope.data.zoom != 1) {
                        $scope.data.directiveZoom = $scope.data.zoom;
                    }
                    $scope.config.zoomIn = function () {
                        var svgcanvas = $("#svg-canvas");
                        var svgcontainer = $("#svg-container");
                        if ($scope.data.layout == "grid") {
                            var str = document.getElementById("svg-canvas").getAttribute("viewBox");
                            var res = str.split(" ");
                            var currentHeight = parseInt(svgcanvas.css("height").split("p")[0]);
                            var currentWidth = parseInt(svgcanvas.css("width").split("p")[0]);
                            var currentContainerWidth = parseInt(svgcontainer.css("width").split("p")[0]);
                            var currentContainerHeight = parseInt(svgcontainer.css("height").split("p")[0]);
                            svgcanvas.css("height", currentHeight + currentHeight * .25 + "px");
                            svgcanvas.css("width", currentWidth + currentWidth * .25 + "px");
                            svgcontainer.css("width", currentContainerWidth + currentContainerWidth * .25 + "px");
                            svgcontainer.css("height", currentContainerHeight + currentContainerHeight * .25 + "px");
                        }
                        if ($scope.data.layout == "flow") {
                            $scope.data.directiveZoom = $scope.data.directiveZoom + .15;
                            $scope.draw();
                        }
                        if ($scope.data.layout == "wrap") {
                            $scope.data.directiveZoom = $scope.data.directiveZoom + .15;
                            $scope.draw();
                        }
                    };
                    $scope.config.zoomOut = function () {
                        var svgcanvas = $("#svg-canvas");
                        var svgcontainer = $("#svg-container");
                        if ($scope.data.layout == "grid") {
                            var str = document.getElementById("svg-canvas").getAttribute("viewBox");
                            var res = str.split(" ");
                            var currentHeight = parseInt(svgcanvas.css("height").split("p")[0]);
                            var currentWidth = parseInt(svgcanvas.css("width").split("p")[0]);
                            var currentContainerWidth = parseInt(svgcontainer.css("width").split("p")[0]);
                            var currentContainerHeight = parseInt(svgcontainer.css("height").split("p")[0]);
                            svgcanvas.css("height", currentHeight - currentHeight * .25 + "px");
                            svgcanvas.css("width", currentWidth - currentWidth * .25 + "px");
                            svgcontainer.css("width", currentContainerWidth - currentContainerWidth * .25 + "px");
                            svgcontainer.css("height", currentContainerHeight - currentContainerHeight * .25 + "px");
                        }
                        if ($scope.data.layout == "flow") {
                            $scope.data.directiveZoom = $scope.data.directiveZoom - .15;
                            $scope.draw();
                        }
                        if ($scope.data.layout == "wrap") {
                            $scope.data.directiveZoom = $scope.data.directiveZoom - .15;
                            $scope.draw();
                        }
                    };
                    $scope.config.zoomReset = function () {
                        var svgcanvas = $("#svg-canvas");
                        var svgcontainer = $("#svg-container");
                        if ($scope.data.layout == "grid") {
                            $scope.data.scaledtoFit = false;
                            document.getElementById("svg-canvas").removeAttribute("viewBox");
                            svgcanvas.css("height", "");
                            svgcanvas.css("width", "");
                            svgcontainer.css("height", "");
                            svgcontainer.css("overflow", "");
                            svgcontainer.css("width", "");
                            $scope.draw();
                        }
                        if ($scope.data.layout == "flow" || $scope.data.layout == "wrap") {
                            $scope.data.directiveZoom = $scope.data.zoom;
                            $scope.draw();
                        }
                    };
                    var shapes = angular.copy($scope.data.topologyShapes);
                    $scope.data.totalSVGs = 0;
                    $scope.data.maxColumns = 0;
                    $scope.data.maxRow = 0;
                    rootShapeIndex = 0;
                    var svgwrapper = $("#svg-wrapper");
                    var svgview = $("#svg-view");
                    var svgcanvas = $("#svg-canvas");
                    var svgcontainer = $("#svg-container");
                    svgview.empty();
                    angular.forEach(shapes, function (shape) {
                        if (shape.row >= $scope.data.maxRow) {
                            $scope.data.maxRow = shape.row;
                        }
                        if (shape.column >= $scope.data.maxColumns) {
                            $scope.data.maxColumns = shape.column;
                        }
                        $scope.drawShape(shape, null);
                        rootShapeIndex++;
                    });
                    svgwrapper.css("overflow-x", "");
                    svgwrapper.css("height", "");
                    if ($scope.data.layout == "grid") {
                        $scope.data.directiveZoom = 1;
                        $scope.data.svgTotalWidth = $scope.data.maxColumns * $scope.data.columnWidth + $scope.data.gridPadding;
                        $scope.data.svgTotalHeight = $scope.data.maxRow * $scope.data.rowHeight + $scope.data.gridPadding;
                        if (!$scope.data.scaledtoFit) {
                            svgcanvas.css("height", ($scope.data.gridContainerHeight - 20) / $scope.data.svgTotalHeight * ($scope.data.gridContainerHeight - 20) + "px");
                            svgcanvas.css("width", ($scope.data.containerWidth - 20) / $scope.data.svgTotalWidth * ($scope.data.containerWidth - 20) + "px");
                            document.getElementById("svg-canvas").setAttribute("viewBox", "0 0 " + ($scope.data.containerWidth - 20 + " " + ($scope.data.gridContainerHeight - 20)));
                            svgcontainer.css("height", $scope.data.gridContainerHeight - 20 + "px");
                            svgcontainer.css("overflow", "hidden");
                            svgcontainer.css("width", $scope.data.containerWidth - 20 + "px");
                            $scope.data.scaledtoFit = true;
                        }
                        svgwrapper.css("overflow-x", "scroll");
                        svgwrapper.css("height", $scope.data.gridContainerHeight + "px");
                    }
                    if ($scope.data.layout == "flow") {
                        $scope.data.scaledtoFit = false;
                        if (document.getElementById("svg-canvas")) {
                            document.getElementById("svg-canvas").removeAttribute("viewBox");
                        }
                        svgcanvas.css("height", "");
                        svgcanvas.css("width", "");
                        svgcontainer.css("height", "");
                        svgcontainer.css("overflow", "");
                        svgcontainer.css("width", "");
                        $scope.data.svgTotalWidth = $scope.data.totalSVGs * $scope.data.svgwidth + $scope.data.totalSVGs * $scope.data.flowMarginRightSVG;
                        svgcanvas.css("width", $scope.data.svgTotalWidth * $scope.data.directiveZoom + $scope.data.flowPadding + "px");
                        svgwrapper.css("overflow-x", "scroll");
                        svgcanvas.css("height", $scope.data.flowPadding * $scope.data.directiveZoom * 2 + $scope.data.svgheight * $scope.data.directiveZoom + "px");
                    }
                    if ($scope.data.layout == "wrap") {
                        $scope.data.scaledtoFit = false;
                        if (document.getElementById("svg-canvas")) {
                            document.getElementById("svg-canvas").removeAttribute("viewBox");
                        }
                        svgcanvas.css("height", "");
                        svgcanvas.css("width", "");
                        svgcontainer.css("height", "");
                        svgcontainer.css("overflow", "");
                        svgcontainer.css("width", "");
                        var lastShape = $scope.data.topologyShapes[$scope.data.topologyShapes.length - 1].nodeId;
                        farY = parseInt($("svg[data-node-id='" + lastShape + "']").attr("y"));
                        if ($scope.data.zoom >= 1) {
                            svgcanvas.css("height", (farY + $scope.data.svgheight) * $scope.data.directiveZoom + $scope.data.wrapSpaceBetweenRows + "px");
                        }
                        if ($scope.data.zoom < 1) {
                            svgcanvas.css("height", (farY + $scope.data.svgheight) / $scope.data.directiveZoom + "px");
                        }
                        $(window).resize(function () {
                            if (self.pollwindowtimer) $timeout.cancel(self.pollwindowtimer);
                            self.pollwindowtimer = $timeout(function () {
                                $scope.draw();
                            }, 1);
                        });
                    }
                    $scope.deleteLines();
                    var svgLines = $("<svg style='overflow: visible;' class='staticLines'></svg>");
                    svgview.append(svgLines);
                    $scope.drawLines();
                };
                $scope.calculateWidth = function () {
                    $scope.windowtimer = $timeout(function () {
                        $scope.deleteLines();
                        var svgview = $("#svg-view");
                        svgview.empty();
                        $scope.data.containerWidth = $("#svg-container").width();
                        $scope.data.totalSVGs = 0;
                        rootShapeIndex = 0;
                        $scope.data.svgRowWidth = 0;
                        angular.forEach($scope.topologyShapes, function (shape) {
                            $scope.drawShape(shape, null);
                            rootShapeIndex++;
                        });
                        var svgLines = $("<svg style='overflow: visible;' class='staticLines'></svg>");
                        svgview.append(svgLines);
                        $scope.drawLines();
                    }, 1e3);
                };
                $scope.defineSvgWidth = function () {
                    var svgId = $("#svg-view svg").attr("id");
                    if (!svgId) {
                        $(window).off("resize");
                        return;
                    }
                    $scope.data.svgwidth = parseInt(document.getElementById(svgId).getBBox().width);
                    $scope.data.svgheight = parseInt(document.getElementById(svgId).getBBox().height);
                };
                $scope.getShapeData = function (nodeId) {
                    var data = $filter("filter")($scope.data.topologyData, {
                        id: nodeId
                    })[0];
                    return data;
                };
                $scope.drawShape = function (shape, parent) {
                    var svgShape = $filter("filter")($scope.data.topologyTemplates, {
                        templateId: shape.templateId
                    })[0];
                    var boundData = $scope.getShapeData(shape.nodeId);
                    svgShape.template = svgShape.template.replace(/%(\w*)%/g, function (m, key) {
                        var string = boundData && boundData[key] ? boundData[key] : "";
                        return string;
                    });
                    var svg = $(svgShape.template);
                    svg.attr("data-node-id", shape.nodeId);
                    if (!parent) {
                        $scope.data.totalSVGs++;
                        $scope.data.containerWidth = $("#svg-container").width();
                        if ($scope.data.layout == "grid") {
                            svg.attr("x", parseInt(shape.column) * parseInt($scope.data.columnWidth) - parseInt($scope.data.columnWidth) + $scope.data.gridPadding);
                            svg.attr("y", parseInt(shape.row) * parseInt($scope.data.rowHeight) - parseInt($scope.data.columnWidth) + $scope.data.gridPadding);
                        }
                        if ($scope.data.layout == "flow") {
                            svg.attr("x", rootShapeIndex * ($scope.data.svgwidth + $scope.data.flowMarginRightSVG) + $scope.data.flowPadding);
                            svg.attr("y", $scope.data.flowPadding);
                        }
                        if ($scope.data.layout == "wrap") {
                            if ($scope.data.svgRowWidth > $scope.data.containerWidth - $scope.data.svgwidth * $scope.data.directiveZoom - $scope.data.wrapMarginRightSVG - $scope.data.wrapPadding * 2 * $scope.data.directiveZoom) {
                                $scope.data.svgRowWidth = 0;
                                $scope.data.wrapIncrement = 0;
                                $scope.data.previousRowHeight = $scope.data.previousRowHeight + $scope.data.svgheight + $scope.data.wrapSpaceBetweenRows;
                                $scope.data.svgRowWidth = $scope.data.wrapIncrement * (($scope.data.svgwidth + $scope.data.wrapMarginRightSVG) * $scope.data.directiveZoom) + $scope.data.svgwidth * $scope.data.directiveZoom * 2;
                                svg.attr("x", $scope.data.wrapIncrement * ($scope.data.svgwidth + $scope.data.wrapMarginRightSVG) + $scope.data.wrapPadding);
                                svg.attr("y", $scope.data.previousRowHeight);
                                $scope.data.wrapIncrement++;
                            } else {
                                $scope.data.svgRowWidth = $scope.data.wrapIncrement * (($scope.data.svgwidth + $scope.data.wrapMarginRightSVG) * $scope.data.directiveZoom) + $scope.data.svgwidth * $scope.data.directiveZoom * 2;
                                svg.attr("x", $scope.data.wrapIncrement * ($scope.data.svgwidth + $scope.data.wrapMarginRightSVG) + $scope.data.wrapPadding);
                                svg.attr("y", $scope.data.previousRowHeight);
                                $scope.data.wrapIncrement++;
                            }
                            if (rootShapeIndex == 0) {
                                $scope.data.previousRowHeight = $scope.data.wrapPadding;
                                $scope.data.wrapIncrement = 0;
                                svg.attr("x", $scope.data.wrapPadding);
                                svg.attr("y", $scope.data.previousRowHeight);
                                $scope.data.wrapIncrement++;
                                $scope.data.svgRowWidth = 0;
                                $scope.data.svgRowWidth = $scope.data.wrapIncrement * (($scope.data.svgwidth + $scope.data.wrapMarginRightSVG) * $scope.data.directiveZoom) + $scope.data.svgwidth * $scope.data.directiveZoom;
                            }
                        }
                        $("#svg-view").append(svg);
                        $scope.defineSvgWidth();
                    } else {
                        $('#svg-view [data-node-id="' + parent.nodeId + '"] #' + shape.targetElementId).append(svg);
                        $scope.defineSvgWidth();
                    }
                    if (shape.isClickable && $scope.data.shapeClick) {
                        svg.on("click", function () {
                            if ($scope.data.shapeClick) {
                                $scope.data.shapeClick(shape.nodeId);
                            }
                        });
                    }
                    shape.class = shape.class || "";
                    shape.connectionGroups = shape.connectionGroups || [];
                    $scope.setupHover(svg, shape, $scope.data.shapeHover);
                    angular.forEach(shape.childShapes, function (childShape) {
                        $scope.drawShape(childShape, shape);
                    });
                };
                $scope.deleteLines = function () {
                    $("#svg-view svg.staticLines").remove();
                };
                $scope.drawLines = function () {
                    var x = "test";
                    var connections = {};
                    angular.forEach($scope.data.topologyConnections, function (connection, index) {
                        if (connection.fromId && connection.toId) {
                            connections[connection.fromId] = connections[connection.fromId] || 0;
                            connections[connection.toId] = connections[connection.toId] || 0;
                            connections[connection.fromId]++;
                            connections[connection.toId]++;
                            var x1Offset = 0;
                            var y1Offset = 0;
                            var x2Offset = 0;
                            var y2Offset = 0;
                            var shape1Width = 0;
                            var shape2Width = 0;
                            var shape1svg = $("svg[data-node-id='" + connection.fromId + "']");
                            var shape2svg = $("svg[data-node-id='" + connection.toId + "']");
                            if (!shape1svg[0] || !shape2svg[0]) {
                                $(window).off("resize");
                                return;
                            }
                            x1Offset = x1Offset + parseInt(shape1svg.attr("x"));
                            y1Offset = y1Offset + parseInt(shape1svg.attr("y"));
                            x2Offset = x2Offset + parseInt(shape2svg.attr("x"));
                            y2Offset = y2Offset + parseInt(shape2svg.attr("y"));
                            shape1Width = parseInt(shape1svg[0].getBBox().width);
                            shape1Height = parseInt(shape1svg[0].getBBox().height);
                            shape2Width = parseInt(shape2svg[0].getBBox().width);
                            shape2Height = parseInt(shape2svg[0].getBBox().height);
                            if (!x1Offset || !y1Offset) {
                                var x1Offset = 0;
                                var y1Offset = 0;
                            }
                            if (!x2Offset || !y2Offset) {
                                var x2Offset = 0;
                                var y2Offset = 0;
                            }
                            angular.forEach(shape1svg.parents(), function (x) {
                                angular.forEach(x.attributes, function (attribute) {
                                    if (attribute.nodeName == "id" && attribute.nodeValue == "svg-view") {
                                        return;
                                    }
                                    if (attribute.nodeName == "x") {
                                        x1Offset = x1Offset + parseInt(attribute.nodeValue);
                                    }
                                    if (attribute.nodeName == "y") {
                                        y1Offset = y1Offset + parseInt(attribute.nodeValue);
                                    }
                                });
                            });
                            angular.forEach(shape2svg.parents(), function (x) {
                                angular.forEach(x.attributes, function (attribute) {
                                    if (attribute.nodeName == "id" && attribute.nodeValue == "svg-view") {
                                        return;
                                    }
                                    if (attribute.nodeName == "x") {
                                        x2Offset = x2Offset + parseInt(attribute.nodeValue);
                                    }
                                    if (attribute.nodeName == "y") {
                                        y2Offset = y2Offset + parseInt(attribute.nodeValue);
                                    }
                                });
                            });
                            var x1 = 0;
                            var y1 = 0;
                            var x2 = 0;
                            var y2 = 0;
                            var x1 = x1Offset;
                            var y1 = y1Offset;
                            var x2 = x2Offset;
                            var y2 = y2Offset;
                            x1 = parseInt(x1);
                            y1 = parseInt(y1);
                            x2 = parseInt(x2);
                            y2 = parseInt(y2);
                            var xLeftOfShape1 = 0;
                            var xRightOfShape1 = 0;
                            var xLeftOfShape2 = 0;
                            var xRightOfShape2 = 0;
                            var xLineStart = 0;
                            var yHeightFromShape1 = 0;
                            var X1Shape1 = 0;
                            var Y1shape1 = 0;
                            var X2shape1 = 0;
                            var Y2shape1 = 0;
                            var x2LineStart = 0;
                            var yHeightFromShape2 = 0;
                            var X1Shape2 = 0;
                            var Y1shape2 = 0;
                            var X2shape2 = 0;
                            var Y2shape2 = 0;
                            if (connection.fromPosition == "top") {
                                xLineStart = shape1Width / 2;
                                yHeightFromShape1 = $scope.data.lineShape1Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                xLeftOfShape1 = x1 - $scope.data.lineShape1Distance - $scope.data.lineIncrement * connections[connection.fromId];
                                xRightOfShape1 = x1 + $scope.data.lineShape1Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                X1Shape1 = x1 + xLineStart;
                                Y1shape1 = y1;
                                X2shape1 = X1Shape1;
                                Y2shape1 = y1 - yHeightFromShape1;
                            }
                            if (connection.toPosition == "top") {
                                x2LineStart = shape2Width / 2;
                                yHeightFromShape2 = $scope.data.lineShape2Distance + $scope.data.lineIncrement * connections[connection.toId];
                                xLeftOfShape2 = x2 - $scope.data.lineShape2Distance - $scope.data.lineIncrement * connections[connection.fromId];
                                xRightOfShape2 = x2 + $scope.data.lineShape2Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                X1Shape2 = x2 + x2LineStart;
                                Y1shape2 = y2;
                                X2shape2 = X1Shape2;
                                Y2shape2 = y2 - yHeightFromShape2;
                            }
                            if (connection.fromPosition == "bottom") {
                                xLineStart = shape1Width / 2;
                                yHeightFromShape1 = $scope.data.lineShape1Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                xLeftOfShape1 = x1 - $scope.data.lineShape1Distance - $scope.data.lineIncrement * connections[connection.fromId];
                                xRightOfShape1 = x1 + $scope.data.lineShape1Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                X1Shape1 = x1 + xLineStart;
                                Y1shape1 = y1;
                                X2shape1 = X1Shape1;
                                Y2shape1 = y1 + shape1Height + yHeightFromShape1;
                            }
                            if (connection.toPosition == "bottom") {
                                x2LineStart = shape2Width / 2;
                                yHeightFromShape2 = $scope.data.lineShape2Distance + $scope.data.lineIncrement * connections[connection.toId];
                                xLeftOfShape2 = x2 - $scope.data.lineShape2Distance - $scope.data.lineIncrement * connections[connection.fromId];
                                xRightOfShape2 = x2 + $scope.data.lineShape2Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                X1Shape2 = x2 + x2LineStart;
                                Y1shape2 = y2;
                                X2shape2 = X1Shape2;
                                Y2shape2 = y2 + shape2Height + yHeightFromShape2;
                            }
                            if (connection.fromPosition == "left") {
                                xLineStart = parseInt($("svg[data-node-id='" + connection.fromId + "']")[0].getBBox().height) / 2;
                                yHeightFromShape1 = $scope.data.lineShape1Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                xLeftOfShape1 = x1 - $scope.data.lineShape1Distance - $scope.data.lineIncrement * connections[connection.fromId];
                                xRightOfShape1 = x1 + $scope.data.lineShape1Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                X1Shape1 = x1;
                                Y1shape1 = y1 + xLineStart;
                                X2shape1 = X1Shape1;
                                Y2shape1 = Y1shape1;
                            }
                            if (connection.toPosition == "left") {
                                x2LineStart = parseInt($("svg[data-node-id='" + connection.toId + "']")[0].getBBox().height) / 2;
                                yHeightFromShape2 = $scope.data.lineShape2Distance + $scope.data.lineIncrement * connections[connection.toId];
                                xLeftOfShape2 = x2 - $scope.data.lineShape2Distance - $scope.data.lineIncrement * connections[connection.fromId];
                                xRightOfShape2 = x2 + $scope.data.lineShape2Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                X1Shape2 = x2;
                                Y1shape2 = y2 + x2LineStart;
                                X2shape2 = X1Shape2;
                                Y2shape2 = Y1shape2;
                            }
                            if (connection.fromPosition == "right") {
                                xLineStart = parseInt($("svg[data-node-id='" + connection.fromId + "']")[0].getBBox().height) / 2;
                                yHeightFromShape1 = $scope.data.lineShape1Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                xLeftOfShape1 = x1 - $scope.data.lineShape1Distance - $scope.data.lineIncrement * connections[connection.fromId];
                                xRightOfShape1 = x1 + $scope.data.lineShape1Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                X1Shape1 = x1 + shape1Width;
                                Y1shape1 = y1 + xLineStart;
                                X2shape1 = X1Shape1 + yHeightFromShape1;
                                Y2shape1 = Y1shape1;
                            }
                            if (connection.toPosition == "right") {
                                x2LineStart = parseInt($("svg[data-node-id='" + connection.toId + "']")[0].getBBox().height) / 2;
                                yHeightFromShape2 = $scope.data.lineShape2Distance + $scope.data.lineIncrement * connections[connection.toId];
                                xLeftOfShape2 = x2 + $scope.data.lineShape2Distance + $scope.data.lineIncrement * connections[connection.fromId];
                                xRightOfShape2 = x2 - $scope.data.lineShape1Distance - $scope.data.lineIncrement * connections[connection.fromId];
                                X1Shape2 = x2 + shape2Width;
                                Y1shape2 = y2 + x2LineStart;
                                X2shape2 = X1Shape2 + yHeightFromShape2;
                                Y2shape2 = Y1shape2;
                            }
                            var path = "";
                            if (x2 >= x1) {
                                path = "M " + X1Shape1 + " " + Y1shape1;
                                if ($scope.data.lineSegments == "simple") {
                                    path += " L " + X1Shape1 + " " + Y1shape2;
                                }
                                if ($scope.data.lineSegments == "complex") {
                                    path += " L " + X2shape1 + " " + Y2shape1;
                                    path += " L " + xLeftOfShape1 + " " + Y2shape1;
                                    path += " L " + xLeftOfShape1 + " " + Y2shape2;
                                    path += " L " + X1Shape2 + " " + Y2shape2;
                                }
                                path += " L " + X1Shape2 + " " + Y1shape2;
                            }
                            if (x2 < x1) {
                                path = "M " + X1Shape1 + " " + Y1shape1;
                                if ($scope.data.lineSegments == "simple") {
                                    path += " L " + X1Shape2 + " " + Y1shape1;
                                }
                                if ($scope.data.lineSegments == "complex") {
                                    path += " L " + X2shape1 + " " + Y2shape1;
                                    path += " L " + xRightOfShape1 + " " + Y2shape1;
                                    path += " L " + xRightOfShape1 + " " + Y2shape2;
                                    path += " L " + X1Shape2 + " " + Y2shape2;
                                }
                                path += " L " + X1Shape2 + " " + Y1shape2;
                            }
                            connection.class = connection.class || "";
                            connection.connectionGroups = connection.connectionGroups || [];
                            var svgpath = document.createElementNS("http://www.w3.org/2000/svg", "path");
                            svgpath.setAttribute("d", path);
                            svgpath.setAttribute("fill", "none");
                            $scope.setupHover(svgpath, connection, $scope.data.connectionHover);
                            var staticLines = $("#svg-view svg.staticLines");
                            staticLines.append(svgpath);
                        }
                    });
                };
                $scope.setupHover = function (element, connection, customHover) {
                    element = element[0] || element;
                    element.className.baseVal = "" + connection.class + " " + connection.connectionGroups.join(" ");
                    if (customHover) {
                        customHover(element, connection);
                    }
                    $(element).hover(function () {
                        $scope.hoverLogic(connection);
                    }, function () {
                        $scope.resetHover(connection);
                    });
                };
                $scope.resetHover = function (connection) {
                    if (connection.connectionGroups) {
                        angular.forEach(connection.connectionGroups, function (connectionGroup) {
                            var elements = $("." + connectionGroup);
                            angular.forEach(elements, function (e) {
                                e.className.baseVal = e.className.baseVal.replace(" hover ", "");
                            });
                        });
                    }
                };
                $scope.hoverLogic = function (connection) {
                    if (connection.connectionGroups) {
                        angular.forEach(connection.connectionGroups, function (connectionGroup) {
                            var elements = $("." + connectionGroup);
                            angular.forEach(elements, function (e) {
                                e.className.baseVal = e.className.baseVal + " hover ";
                            });
                        });
                    }
                };
                $scope.$watch("config", $scope.draw, true);
            }
        };
    }]);
    angular.module("Clarity.directives").directive("wizard", ["$q", "$translate", "ClarityServices", function ($q, $translate, ClarityServices) {
        return {
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "wizard.html",
            restrict: "E",
            require: "^?modal",
            transclude: true,
            scope: {
                editMode: "=?",
                onFinish: "&",
                onCancel: "&",
                currentStep: "=?",
                currentStepIndex: "=?",
                finishButtonText: "=?"
            },
            controller: ["$scope", "$element", "$rootScope", function ($scope, $element, $rootScope) {
                $rootScope.$broadcast("modalOpen");
                var canJumpToStep, runOnNext, steps;
                if ($scope.editMode == null) {
                    $scope.editMode = false;
                }
                $scope.currentStepIndex = 0;
                steps = this.steps = $scope.steps = [];
                if (!$scope.finishButtonText) {
                    $scope.finishButtonText = $scope.editMode ? $translate.instant("CLARITY_Save") : $translate.instant("CLARITY_Finish");
                }
                $scope.stepCount = function () {
                    return {
                        current: $scope.currentStepIndex + 1,
                        total: $scope.steps.length
                    };
                };
                this.updateSteps = function (_this) {
                    return function () {
                        var newStep, oldStep, scope, step, stepElements, _i, _len;
                        oldStep = steps[$scope.currentStepIndex];
                        stepElements = $element[0].querySelectorAll("wizard-step");
                        steps = _this.steps = $scope.steps = [];
                        for (_i = 0, _len = stepElements.length; _i < _len; _i++) {
                            step = stepElements[_i];
                            scope = angular.element(step).isolateScope();
                            if (scope != null) {
                                steps.push(scope);
                            }
                        }
                        if ($scope.currentStepIndex >= steps.length) {
                            $scope.currentStepIndex = steps.length - 1;
                            if ($scope.currentStepIndex === -1) {
                                $scope.currentStepIndex = 0;
                            }
                        }
                        newStep = steps[$scope.currentStepIndex];
                        if (oldStep !== newStep) {
                            _this.updateStepShown(newStep, oldStep, $scope.currentStepIndex);
                        }
                    };
                }(this);
                this.updateStepShown = function (newStep, oldStep, oldStepIndex) {
                    if (newStep != null) {
                        newStep.touched = true;
                    }
                    $scope.currentStep = (newStep != null ? newStep.stepTitle : void 0) || (newStep != null ? newStep.stepTitle : void 0);
                    if (newStep != null) {
                        newStep.onEnter();
                    }
                    return $scope.$emit("wizard:stepChanged", {
                        index: $scope.currentStepIndex,
                        title: (newStep != null ? newStep.stepTitle : void 0) || (newStep != null ? newStep.stepTitle : void 0)
                    }, {
                        index: oldStepIndex,
                        title: (oldStep != null ? oldStep.stepTitle : void 0) || (oldStep != null ? oldStep.stepTitle : void 0)
                    });
                };
                this.onStep = function (step) {
                    return steps[$scope.currentStepIndex] === step;
                };
                canJumpToStep = function (index) {
                    return $scope.editMode || index <= $scope.currentStepIndex;
                };
                this.updateForwards = function () {
                    var i, invalidStep, step, _i, _len;
                    invalidStep = -1;
                    for (i = _i = 0, _len = steps.length; _i < _len; i = ++_i) {
                        step = steps[i];
                        if (step.invalid && invalidStep === -1) {
                            invalidStep = i;
                        }
                        if (i < $scope.currentStepIndex) {
                            step.forward = true;
                        } else if (invalidStep !== -1 && invalidStep !== i) {
                            step.forward = false;
                        } else {
                            step.forward = true;
                        }
                    }
                    $element.scrollTop(0);
                    window.setTimeout(function () {
                        $(window).trigger("resize");
                    }, 500);
                };
                $scope.$watch("currentStepIndex", this.updateForwards);
                runOnNext = function (cb) {
                    var onNext;
                    onNext = steps[$scope.currentStepIndex].onNext;
                    steps[$scope.currentStepIndex].onNextFailed = false;
                    if (steps[$scope.currentStepIndex].onNextExists()) {
                        onNext = onNext();
                        return $q.when(onNext).then(function () {
                            return cb();
                        }, function () {
                            return steps[$scope.currentStepIndex].onNextFailed = true;
                        });
                    } else {
                        return cb();
                    }
                };
                $scope.isStepDisabled = function (index) {
                    if ($scope.editMode) return false;
                    return !canJumpToStep(index) || !steps[index].forward;
                };
                $scope.canFinish = function () {
                    var _ref, _ref1, _ref2;
                    if ($scope.editMode) {
                        return ((_ref = steps[steps.length - 1]) != null ? _ref.forward : void 0) && !((_ref1 = steps[steps.length - 1]) != null ? _ref1.invalid : void 0);
                    } else {
                        return !((_ref2 = steps[$scope.currentStepIndex]) != null ? _ref2.invalid : void 0) && $scope.currentStepIndex === steps.length - 1;
                    }
                };
                $scope.selectStepIndex = function (_this) {
                    return function (index) {
                        if ($scope.editMode) {
                            return _this.goto(index);
                        } else if (canJumpToStep(index) && steps[index].forward) {
                            if (index > $scope.currentStepIndex) {
                                return runOnNext(function () {
                                    return _this.goto(index);
                                });
                            } else {
                                return _this.goto(index);
                            }
                        }
                    };
                }(this);
                this.goto = function (_this) {
                    return function (identifier) {
                        var i, old, step, _i, _len;
                        old = $scope.currentStepIndex;
                        if (angular.isString(identifier)) {
                            for (i = _i = 0, _len = steps.length; _i < _len; i = ++_i) {
                                step = steps[i];
                                if (step.stepTitle === identifier) {
                                    $scope.currentStepIndex = i;
                                    return;
                                }
                            }
                            throw new Error("wizard: Couldn't find step '" + identifier + "'");
                        } else if (angular.isNumber(identifier)) {
                            if (identifier < 0 || identifier >= steps.length) {
                                throw new Error("wizard: Step " + identifier + " not in index range 0-" + (steps.length - 1));
                            }
                            $scope.currentStepIndex = identifier;
                        } else {
                            throw new Error("wizard: Identifier not valid");
                        }
                        return _this.updateStepShown(steps[$scope.currentStepIndex], steps[old], old);
                    };
                }(this);
                $scope.$on("wizard:goto", function (_this) {
                    return function (e, identifier) {
                        return _this.goto(identifier);
                    };
                }(this));
                $scope.goto = this.goto;
                this.previous = function (_this) {
                    return function () {
                        if ($scope.currentStepIndex <= 0) {
                            throw new Error("wizard: At beginning of wizard steps");
                        }
                        $scope.currentStepIndex--;
                        return _this.updateStepShown(steps[$scope.currentStepIndex], steps[$scope.currentStepIndex + 1], $scope.currentStepIndex + 1);
                    };
                }(this);
                $scope.$on("wizard:previous", this.previous);
                $scope.previous = this.previous;
                this.next = function (_this) {
                    return function () {
                        if ($scope.currentStepIndex >= steps.length - 1) {
                            throw new Error("wizard: At end of wizard steps");
                        }
                        $scope.currentStepIndex++;
                        return _this.updateStepShown(steps[$scope.currentStepIndex], steps[$scope.currentStepIndex - 1], $scope.currentStepIndex - 1);
                    };
                }(this);
                $scope.$on("wizard:next", this.next);
                $scope.next = function (_this) {
                    return function () {
                        return runOnNext(_this.next);
                    };
                }(this);
                this.first = function (_this) {
                    return function () {
                        var old;
                        old = $scope.currentStepIndex;
                        $scope.currentStepIndex = 0;
                        return _this.updateStepShown(steps[$scope.currentStepIndex], steps[old], old);
                    };
                }(this);
                $scope.$on("wizard:first", this.first);
                this.last = function (_this) {
                    return function () {
                        var old;
                        old = $scope.currentStepIndex;
                        $scope.currentStepIndex = steps.length - 1;
                        return _this.updateStepShown(steps[$scope.currentStepIndex], steps[old], old);
                    };
                }(this);
                $scope.$on("wizard:last", this.last);
                this.cancel = function (e) {
                    var onCancel;
                    onCancel = $scope.onCancel;
                    if (onCancel && typeof onCancel === "function") {
                        onCancel = onCancel({
                            $event: e
                        });
                        return $q.when(onCancel);
                    }
                };
                $scope.$on("wizard:cancel", this.cancel);
                $scope.cancel = this.cancel;
                this.finish = function (e) {
                    var onFinish;
                    onFinish = $scope.onFinish;
                    if (onFinish && typeof onFinish === "function") {
                        onFinish = onFinish({
                            $event: e
                        });
                        return $q.when(onFinish);
                    }
                };
                $scope.$on("wizard:finish", this.finish);
                $scope.finish = this.finish;
                return this;
            }],
            link: function (scope, element, attrs, modalCtrl) {
                var x = 0;
            }
        };
    }]);
    angular.module("Clarity.directives").directive("wizardStep", ["$timeout", "$translate", "ClarityServices", function ($timeout, $translate, ClarityServices) {
        return {
            templateUrl: "" + ClarityServices.Settings.BaseTemplatePath + "wizardStep.html",
            restrict: "E",
            transclude: true,
            require: "^wizard",
            scope: {
                stepTitle: "@",
                invalid: "=?",
                onNext: "&",
                onEnter: "&",
                nextButtonTitle: "@",
                extraButton: "<?"
            },
            link: function (scope, element, attrs, wizard) {
                if (scope.invalid == null) {
                    scope.invalid = false;
                }
                if (!scope.nextButtonTitle) {
                    scope.nextButtonTitle = $translate.instant("CLARITY_Next");
                }
                scope.touched = false;
                scope.onNextExists = function () {
                    return angular.isDefined(attrs.onNext);
                };
                scope.isCurrentStep = function () {
                    return wizard.onStep(scope);
                };
                wizard.updateSteps();
                scope.$on("$destroy", function () {
                    return $timeout(function () {
                        return wizard.updateSteps();
                    });
                });
                return scope.$watch("invalid", function () {
                    return wizard.updateForwards();
                });
            }
        };
    }]);
    angular.module("Clarity.services").filter("unique", function ($parse) {
        return function (input, filter) {
            if (angular.isArray(input)) {
                var getter = $parse(filter);
                return _.uniqBy(input, function (elm) {
                    return getter(elm);
                });
            }
            return input;
        };
    }).filter("lookup", [function () {
        return function (list, key) {
            return list[key];
        };
    }]).filter("ip2long", [function () {
        return function (ipaddress) {
            var parts = ipaddress.split(".");
            var res = 0;
            res += parseInt(parts[0], 10) << 24 >>> 0;
            res += parseInt(parts[1], 10) << 16 >>> 0;
            res += parseInt(parts[2], 10) << 8 >>> 0;
            res += parseInt(parts[3], 10) >>> 0;
            return res;
        };
    }]).filter("ip2link", [function () {
        return function (ipaddress) {
            var link = ipaddress;
            if (link != undefined && link !== "" && link.indexOf("http") !== 0) link = "http://" + link;
            return link;
        };
    }]).filter("toclassname", [function () {
        return function (val) {
            if (!val) return "";
            return val.replace(/[^a-z0-9]/g, function (s) {
                var c = s.charCodeAt(0);
                if (c === 32) return "-";
                if (c >= 65 && c <= 90) return "_" + s.toLowerCase();
                return "__" + ("000" + c.toString(16)).slice(-4);
            });
        };
    }]).filter("sum", [function () {
        return function (items, prop) {
            return items.reduce(function (a, b) {
                return a + b[prop];
            }, 0);
        };
    }]).filter("ellipsis", ["ClarityServices", function (ClarityServices) {
        return function (value, limit) {
            var returnVal = value;
            if (limit === undefined && ClarityServices.Settings.TextOverflowLength != undefined) {
                limit = ClarityServices.Settings.TextOverflowLength;
            }
            if (value.length > limit) {
                returnVal = value.substring(0, limit) + "...";
            }
            return returnVal;
        };
    }]).factory("ClarityServices", ["$rootScope", function ($rootScope) {
        $rootScope.Clarity = this;
        $rootScope.Clarity.Settings = {
            BaseTemplatePath: "__clarity/",
            TextOverflowLength: null
        };
        $rootScope.Clarity.SetTextOverflowLength = function (length) {
            $rootScope.Clarity.Settings.TextOverflowLength = length;
        };
        $rootScope.Clarity.NewGuid = function () {
            return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0, v = c === "x" ? r : r & 3 | 8;
                return v.toString(16);
            });
        };
        $rootScope.Clarity.MergeObjects = function (baseObj, sourceObj) {
            var combinedObj = angular.copy({}, baseObj);
            combinedObj = angular.copy(combinedObj, sourceObj);
            return combinedObj;
        };
        return $rootScope.Clarity;
    }]);
    angular.module("Clarity.localizations").constant("ClarityTranslations", {
        en: {
            CLARITY_Acknowledge: "Acknowledge",
            CLARITY_Action: "Action",
            CLARITY_Actions: "Actions",
            CLARITY_Active: "Active",
            CLARITY_Activity: "Activity",
            CLARITY_Add: "Add",
            CLARITY_Address: "Address",
            CLARITY_Address1: "Address Line 1",
            CLARITY_Address2: "Address Line 2",
            CLARITY_Address3: "Address Line 3",
            CLARITY_AnotherAction: "Another Action",
            CLARITY_Aisle: "Aisle",
            CLARITY_alert: "alert",
            CLARITY_Alert: "Alert",
            CLARITY_Alerts: "Alerts",
            CLARITY_All: "All",
            CLARITY_AlternateEmail: "Alternate Email",
            CLARITY_Amps: "Amps",
            CLARITY_Apply: "Apply",
            CLARITY_Array: "Array",
            CLARITY_Arrays: "Arrays",
            CLARITY_AssetTag: "Asset Tag",
            CLARITY_Assign: "Assign",
            CLARITY_Assigned: "Assigned",
            CLARITY_Attribute: "Attribute",
            CLARITY_AvailableMemory: "Available Memory",
            CLARITY_Available: "Available",
            CLARITY_Average: "Average",
            CLARITY_Back: "Back",
            CLARITY_Backplane: "Backplane",
            CLARITY_Bandwidth: "Bandwidth",
            CLARITY_Baseline: "Baseline",
            CLARITY_Baselines: "Baselines",
            CLARITY_BiWeekly: "Bi-Weekly",
            CLARITY_Bottom: "Bottom",
            CLARITY_Browse: "Browse",
            CLARITY_Cancel: "Cancel",
            CLARITY_Capacity: "Capacity",
            CLARITY_Category: "Category",
            CLARITY_Chassis: "Chassis",
            CLARITY_CheckUpdates: "Check for Updates",
            CLARITY_CityTown: "City/Town",
            CLARITY_Clear: "Clear",
            CLARITY_ClearLogs: "Clear Logs",
            CLARITY_Close: "Close",
            CLARITY_Command: "Command",
            CLARITY_Community: "Community",
            CLARITY_Compliance: "Compliance",
            CLARITY_Component: "Component",
            CLARITY_Compute: "Compute",
            CLARITY_Configuration: "Configuration",
            CLARITY_ConfigureDeploy: "Configure and Deploy",
            CLARITY_Configure: "Configure",
            CLARITY_Confirm: "Confirm",
            CLARITY_confirm: "confirm",
            CLARITY_Console: "Console",
            CLARITY_Connect: "Connect",
            CLARITY_Connections: "Connections",
            CLARITY_Consistent: "Consistent",
            CLARITY_ContactInfo: "Contact Info",
            CLARITY_Controller: "Controller",
            CLARITY_CPU: "CPU",
            CLARITY_cpus: "CPUs",
            CLARITY_create: "create",
            CLARITY_Create: "Create",
            CLARITY_Created: "Created",
            CLARITY_CreatedBy: "Created By",
            CLARITY_CreatedDate: "Created Date",
            CLARITY_CreatedOn: "Created On",
            CLARITY_Critical: "Critical",
            CLARITY_Criticality: "Criticality",
            CLARITY_CriticalErrors: "Critical Errors",
            CLARITY_Country: "Country",
            CLARITY_CountryTerritory: "Country or Territory",
            CLARITY_Current: "Current",
            CLARITY_CurrentVersion: "Current Version",
            CLARITY_CurrentUpdateStatus: "Current Update Status",
            CLARITY_Daily: "Daily",
            CLARITY_Database: "Database",
            CLARITY_DataCenter: "Data Center",
            CLARITY_Datastore: "Datastore",
            CLARITY_Date: "Date",
            CLARITY_DateInstalled: "Date Installed",
            CLARITY_DateUpdated: "Date Updated",
            CLARITY_DateTime: "Date and Time",
            CLARITY_Day_: "day",
            CLARITY_Days_: "days",
            CLARITY_DayOfWeek_Sunday: "Sunday",
            CLARITY_DayOfWeek_Monday: "Monday",
            CLARITY_DayOfWeek_Tuesday: "Tuesday",
            CLARITY_DayOfWeek_Wednesday: "Wednesday",
            CLARITY_DayOfWeek_Thursday: "Thursday",
            CLARITY_DayOfWeek_Friday: "Friday",
            CLARITY_DayOfWeek_Saturday: "Saturday",
            CLARITY_Debug: "Debug",
            CLARITY_Default: "Default",
            CLARITY_Delete: "Delete",
            CLARITY_delete: "delete",
            CLARITY_DellCopyright: "© 2016 Dell Technologies. ALL RIGHTS RESERVED",
            CLARITY_DellNews: "Dell News",
            CLARITY_Deploy: "Deploy",
            CLARITY_Deployed: "Deployed",
            CLARITY_DeployedBy: "Deployed By",
            CLARITY_DeployedOn: "Deployed On",
            CLARITY_Device: "Device",
            CLARITY_Devices: "Devices",
            CLARITY_Description: "Description",
            CLARITY_Destination: "Destination",
            CLARITY_Detail: "Detail",
            CLARITY_Details: "Details",
            CLARITY_Disable: "Disable",
            CLARITY_Disabled: "Disabled",
            CLARITY_Discover: "Discover",
            CLARITY_Discovered: "Discovered",
            CLARITY_Disk: "Disk",
            CLARITY_Disks: "Disks",
            CLARITY_Discovering: "Discovering",
            CLARITY_Display: "Display",
            CLARITY_Domain: "Domain",
            CLARITY_Drives: "Drives",
            CLARITY_Edit: "Edit",
            CLARITY_Email: "Email",
            CLARITY_EmailAddress: "Email Address",
            CLARITY_EmptyTable: "There are no items to display.",
            CLARITY_Enable: "Enable",
            CLARITY_Enabled: "Enabled",
            CLARITY_Enclosures: "Enclosures",
            CLARITY_Enclosure: "Enclosure",
            CLARITY_EndTime: "End Time",
            CLARITY_Environmental: "Environmental",
            CLARITY_Errors_Multiple: "Multiple Errors",
            CLARITY_Errors_Details: "Details",
            CLARITY_estimatedtime: "estimated time:",
            CLARITY_Events: "Events",
            CLARITY_Expand: "Expand",
            CLARITY_Expire: "Expire",
            CLARITY_Expires: "Expires",
            CLARITY_Export: "Export",
            CLARITY_Fabric: "Fabric",
            CLARITY_Facility: "Facility",
            CLARITY_Failed: "Failed",
            CLARITY_FileName: "File Name",
            CLARITY_ClearFilter: "Clear All",
            CLARITY_Filter: "Filter",
            CLARITY_FilterResults: "Filter Results",
            CLARITY_Finish: "Finish",
            CLARITY_FirstName: "First Name",
            CLARITY_FirmwareVersion: "Firmware Version",
            CLARITY_FirmwareStatus: "Firmware Status",
            CLARITY_Free: "Free",
            CLARITY_GB: "GB",
            CLARITY_Gateway: "Gateway",
            CLARITY_General: "General",
            CLARITY_GeneralInfo: "General Info",
            CLARITY_Growth: "Growth",
            CLARITY_Good: "Good",
            CLARITY_Health: "Health",
            CLARITY_HideDetails: "Hide Details",
            CLARITY_Host: "Host",
            CLARITY_Hosts: "Hosts",
            CLARITY_HostName: "Host Name",
            CLARITY_Hours: "Hours",
            CLARITY_ID: "ID",
            CLARITY_Identify: "Identify",
            CLARITY_Immediate: "Immediate",
            CLARITY_Index: "Index",
            CLARITY_Information: "Information",
            CLARITY_Informational: "Informational",
            CLARITY_Install: "Install",
            CLARITY_InstallOptions: "Install Options",
            CLARITY_InService: "In Service",
            CLARITY_Interconnects: "Interconnects",
            CLARITY_IO: "I/O",
            CLARITY_IpAddress: "IP Address",
            CLARITY_IPMI: "IPMI",
            CLARITY_IssuedTo: "Issued To",
            CLARITY_IssuedBy: "Issued By",
            CLARITY_Job: "Job",
            CLARITY_Jobs: "Jobs",
            CLARITY_Last: "Last",
            CLARITY_LastName: "Last Name",
            CLARITY_Leader: "Leader",
            CLARITY_Learn: "Learn",
            CLARITY_LearnMore: "Learn More",
            CLARITY_LicensedFor: "Licensed for",
            CLARITY_List: "List",
            CLARITY_Loading: "Loading... Please Wait.",
            CLARITY_location: "Location",
            CLARITY_Login: "Log In",
            CLARITY_LoginDateTime: "Login Date and Time",
            CLARITY_LoginFailed: "The specified username and/or password is incorrect. Reenter the login ID and password.",
            CLARITY_Logout: "Logout",
            CLARITY_Logs: "Logs",
            CLARITY_Major: "Major",
            CLARITY_Mappings: "Mappings",
            CLARITY_Mbps: "Mbps",
            CLARITY_memory: "Memory",
            CLARITY_Message: "Message",
            CLARITY_Min: "Min",
            CLARITY_Model: "Model",
            CLARITY_Monthly: "Monthly",
            CLARITY_More: "More",
            CLARITY_MoreActions: "More Actions",
            CLARITY_Name: "Name",
            CLARITY_Network: "Network",
            CLARITY_Networking: "Networking",
            CLARITY_Networks: "Networks",
            CLARITY_New: "New",
            CLARITY_New_: "New...",
            CLARITY_Next: "Next",
            CLARITY_No: "No",
            CLARITY_None: "None",
            CLARITY_Note: "Note",
            CLARITY_Notes: "Notes",
            CLARITY_FeatureNotImplemented: "This feature is not yet implemented.",
            CLARITY_Offline: "Offline",
            CLARITY_OK: "OK",
            CLARITY_on: "on",
            CLARITY_On_: "On",
            CLARITY_Online: "Online",
            CLARITY_Onboarding: "Onboarding",
            CLARITY_optional_: "(optional)",
            CLARITY_OperatingSystem: "Operating System",
            CLARITY_DellSupportAssistTickets: "Dell Support Assist Tickets",
            CLARITY_RecentDellSupportAssistTickets: "Recent Dell Support Assist Tickets",
            CLARITY_Overview: "Overview",
            CLARITY_Password: "Password",
            CLARITY_Path: "Path",
            CLARITY_Peak: "Peak",
            CLARITY_Phone: "Phone",
            CLARITY_PhoneNumber: "Phone Number",
            CLARITY_Port: "Port",
            CLARITY_Ports: "Ports",
            CLARITY_Power: "Power",
            CLARITY_Present: "Present",
            CLARITY_Print: "Print",
            CLARITY_Product: "Product",
            CLARITY_Progress: "Progress",
            CLARITY_Protocol: "Protocol",
            CLARITY_Proxy: "Proxy",
            CLARITY_Purpose: "Purpose",
            CLARITY_Rack: "Rack",
            CLARITY_Reading: "Reading",
            CLARITY_Recheck: "Recheck",
            CLARITY_Rediscover: "Rediscover",
            CLARITY_ReleaseNotes: "Release Notes",
            CLARITY_Relevancy: "Relevancy",
            CLARITY_Refresh: "Refresh",
            CLARITY_RememberMe: "Remember Me",
            CLARITY_Remove: "Remove",
            CLARITY_RepositoryName: "Repository Name",
            CLARITY_Repositories: "Repositories",
            CLARITY_Repository: "Repository",
            CLARITY_Required_: "REQUIRED",
            CLARITY_Restart: "Restart",
            CLARITY_Retry: "Retry",
            CLARITY_Role: "Role",
            CLARITY_Roles: "Roles",
            CLARITY_Room: "Room",
            CLARITY_RunReport: "Run Report",
            CLARITY_Save: "Save",
            CLARITY_SaveSettings: "Save Settings",
            CLARITY_Search: "Search",
            CLARITY_SearchResults: "Results for ",
            CLARITY_Secured: "Secured",
            CLARITY_select: "Select",
            CLARITY_Select_: "--Select--",
            CLARITY_Server: "Server",
            CLARITY_Servers: "Servers",
            CLARITY_ServerInformation: "Server Information",
            CLARITY_ServerName: "Server Name",
            CLARITY_Service: "Service",
            CLARITY_Services: "Services",
            CLARITY_ServiceHealth: "Service Health",
            CLARITY_ServiceInformation: "Service Information",
            CLARITY_ServiceName: "Service Name",
            CLARITY_ServiceSettings: "Service Settings",
            CLARITY_ServiceTag: "Service Tag",
            CLARITY_Settings: "Settings",
            CLARITY_Severity: "Severity",
            CLARITY_SignIn: "Log In",
            CLARITY_Shutdown: "Shutdown",
            CLARITY_Size: "Size",
            CLARITY_Slot: "Slot",
            CLARITY_Source: "Source",
            CLARITY_Speed: "Speed",
            CLARITY_stars: "********",
            CLARITY_State: "State",
            CLARITY_StartTime: "Start Time",
            CLARITY_StateProvinceRegion: "State / Province / Region",
            CLARITY_Statistic: "Statistic",
            CLARITY_Status: "Status",
            CLARITY_Storage: "Storage",
            CLARITY_StorageInformation: "Storage Information",
            CLARITY_Submit: "Submit",
            CLARITY_Subnet: "Subnet",
            CLARITY_Success: "Success",
            CLARITY_Summary: "Summary",
            CLARITY_Support: "Support",
            CLARITY_SupportAssist: "Support Assist",
            CLARITY_Switches: "Switches",
            CLARITY_SystemVersion: "System Version",
            CLARITY_TB: "TB",
            CLARITY_Tags: "Tags",
            CLARITY_Temperature: "Temperature",
            CLARITY_Test: "Test",
            CLARITY_Time: "Time",
            CLARITY_TimeZone: "Time Zone",
            CLARITY_ToggleDropdown: "Toggle Dropdown",
            CLARITY_ToggleDateTime: "Toggle Date/Time",
            CLARITY_Top: "Top",
            CLARITY_TopOffenders: "Top Offenders",
            CLARITY_Total: "Total",
            CLARITY_TotalUsage: "Total Usage",
            CLARITY_Troubleshoot: "Troubleshoot",
            CLARITY_Type: "Type",
            CLARITY_Unknown: "Unknown",
            CLARITY_unmanage: "Unmanage",
            CLARITY_Unallocated: "Unallocated",
            CLARITY_Unsuccessful: "Unsuccessful",
            CLARITY_UpdatedBy: "Updated By",
            CLARITY_UpdatedDate: "Updated Date",
            CLARITY_UpdatedOn: "Updated On",
            CLARITY_Update: "Update",
            CLARITY_Updated: "Updated",
            CLARITY_UpdateStatus: "Update Status",
            CLARITY_UpdateFirmware: "Update Firmware",
            CLARITY_Upload: "Upload",
            CLARITY_UploadConfigure: "Upload and Configure",
            CLARITY_Urgent: "Urgent",
            CLARITY_Used: "Used",
            CLARITY_User: "User",
            CLARITY_UserDetails: "User Details",
            CLARITY_Users: "Users",
            CLARITY_UserRoles: "User and Roles",
            CLARITY_UsersRolesDirectoryService: "Users, Roles, and Directory Service",
            CLARITY_UserName: "User Name",
            CLARITY_Utilization: "Utilization",
            CLARITY_Value: "Value",
            CLARITY_ValidFrom: "Valid From",
            CLARITY_ValidTo: "Valid To",
            CLARITY_Validate: "Validate",
            CLARITY_Vendor: "Vendor",
            CLARITY_Version: "Version",
            CLARITY_Version_: "version",
            CLARITY_ViewAll: "View All",
            CLARITY_ViewLess: "View Less",
            CLARITY_ViewBy: "View By",
            CLARITY_View_By: "View By:",
            CLARITY_ViewDetails: "View Details",
            CLARITY_VLANS: "VLANS",
            CLARITY_Volts: "Volts",
            CLARITY_Volume: "Volume",
            CLARITY_Volumes: "Volumes",
            CLARITY_Warning: "Warning",
            CLARITY_WarningAt: "Warning at",
            CLARITY_Warnings: "Warnings",
            CLARITY_Watts: "Watts",
            CLARITY_Welcome: "Welcome",
            CLARITY_Weekly: "Weekly",
            CLARITY_Yes: "Yes",
            CLARITY_ZipPostalCode: "Zip/Postal Code",
            CLARITYNAVIGATION_Home: "Home",
            CLARITYMESSAGES_creditcard: "Please enter a valid credit card number.",
            CLARITYMESSAGES_date: "Please enter a valid date.",
            CLARITYMESSAGES_dateISO: "Please enter a valid date (ISO).",
            CLARITYMESSAGES_digits: "Please enter only digits.",
            CLARITYMESSAGES_email: "Please enter a valid email address.",
            CLARITYMESSAGES_equalTo: "Please enter the same value again.",
            CLARITYMESSAGES_ipaddress: "Please enter a valid IP Address.",
            CLARITYMESSAGES_max: "Please enter a value less than or equal to {0}.",
            CLARITYMESSAGES_maxlength: "Please enter no more than {0} characters.",
            CLARITYMESSAGES_min: "Please enter a value greater than or equal to {0}.",
            CLARITYMESSAGES_minlength: "Please enter at least {0} characters.",
            CLARITYMESSAGES_number: "Please enter a valid number.",
            CLARITYMESSAGES_range: "Please enter a value between {0} and {1}.",
            CLARITYMESSAGES_rangelength: "Please enter a value between {0} and {1} characters long.",
            CLARITYMESSAGES_remote: "Please fix this field.",
            CLARITYMESSAGES_required: "This field is required.",
            CLARITYMESSAGES_url: "Please enter a valid URL.",
            WIZARD_Step_Count: "Step {{current}} of {{total}}",
            WIZARD_SAVE_AND_NEXT: "Save and Next"
        }
    });
})();
