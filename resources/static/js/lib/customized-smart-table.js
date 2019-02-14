/** 
* @version 1.4.11
* @license MIT
*/
(function (ng, undefined) {
    'use strict';

    ng.module('smart-table', []).run([
        '$templateCache', function ($templateCache) {
            $templateCache.put('template/smart-table/pagination.html',
                '<nav>' +
                '<ul class="list-unstyled list-inline">' +
                '<li class="pull-left"><p><span ng-if="itemCount != totalItems">{{itemCount}} of {{totalItems}}</span><span ng-if="itemCount == totalItems">{{itemCount}}</span> items found. <span ng-if="itemCount > 0">Displaying items {{(currentPage - 1) * stItemsByPage + 1 }} - {{ currentPage == numPages ? itemCount : currentPage * stItemsByPage }}.</span></p></li>' +
                '<li ng-if="numPages >= 2" ng-disabled="currentPage == numPages" class="pull-right"><a style="position: relative; top: 3px;" ng-disabled="currentPage == numPages" ng-click="selectPage(numPages)"><i ng-class="{\'text-muted\': currentPage == numPages}" class="glyphicon glyphicon-step-forward"></i></a></li>' +
                '<li ng-if="numPages >= 2" ng-disabled="currentPage == numPages" class="pull-right"><a style="position: relative; top: 3px;" ng-disabled="currentPage == numPages" ng-click="selectPage(currentPage+1)"><i ng-class="{\'text-muted\': currentPage == numPages}" class="glyphicon glyphicon-forward"></i></a></li>' +
                '<li ng-if="numPages >= 2" class="pull-right"><span style="margin-left: 5px; margin-right: 5px;">Page <input type="text" class="input-xs text-center" style="width: 40px;" ng-model="pageState.txtPage" ng-change="updatePage()" ng-blur="updatePage(true)"  ng-model-options="{ updateOn: \'default\', debounce: {\'default\': 500, \'blur\': 0} }" /> of {{numPages}}</span></li>' +
                '<li ng-if="numPages >= 2" ng-disabled="currentPage == 1" class="pull-right"><a style="position: relative; top: 3px;" ng-disabled="currentPage == 1" ng-click="selectPage(currentPage-1)"><i ng-class="{\'text-muted\': currentPage == 1}" class="glyphicon glyphicon-backward"></i></a></li>' +
                '<li ng-if="numPages >= 2" ng-disabled="currentPage == 1" class="pull-right"><a style="position: relative; top: 3px;" ng-disabled="currentPage == 1" ng-click="selectPage(1)"><i ng-class="{\'text-muted\': currentPage == 1}" class="glyphicon glyphicon-step-backward"></i></a></li>' +
                '</ul></nav>');

            $templateCache.put('template/smart-table/stSelectMultiple.html',
                '<div class="dropdown dropdown-filter">' +
                '  <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">' +
                '  {{dropdownLabel}}&nbsp;<span class="caret"></span>' +
                '  </button>' +
                '  <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">' +
                '    <li ng-repeat="item in distinctItems">' +
                '      <label>' +
                '        <input type="checkbox" ng-model="item.selected" style="margin-left: 3px; margin-right: 3px;">{{item.label}}</label>' +
                '    </li>' +
                '  </ul>' +
                '</div>');
        }
    ]);

    ng.module('smart-table')
      .controller('stTableController', ['$scope', '$parse', '$filter', '$attrs', function StTableController($scope, $parse, $filter, $attrs) {

          function copyRefs(src) {
              return src ? [].concat(src) : [];
          }

          var propertyName = $attrs.stTable;
          var displayGetter = $parse(propertyName);
          var displaySetter = displayGetter.assign;
          var safeGetter;
          var orderBy = $filter('orderBy');
          var filter = $filter('filter');
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

          /**
           * sort the rows
           * @param {Function | String} predicate - function or string which will be used as predicate for the sorting
           * @param [reverse] - if you want to reverse the order
           */
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

          /**
           * search matching rows
           * @param {String} input - the input string
           * @param {String} [predicate] - the property name against you want to check the match, otherwise it will search on all properties
           */
          this.search = function search(input, predicate, strict) {
              var predicateObject = tableState.search.predicateObject || {};
              var prop = predicate ? predicate : '$';

              input = ng.isString(input) ? input.trim() : input;
              predicateObject[prop] = input;
              // to avoid to filter out null value
              if (!input) {
                  delete predicateObject[prop];
              }
              tableState.search.predicateObject = predicateObject;
              tableState.pagination.start = 0;
              tableState.search.strict = strict;
              return this.pipe();
          };

          /**
           * this will chain the operations of sorting and filtering based on the current table state (sort options, filtering, ect)
           */
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

          /**
           * select a dataRow (it will add the attribute isSelected to the row object)
           * @param {Object} row - the row to select
           * @param {String} [mode] - "single" or "multiple" (multiple by default)
           */
          this.select = function select(row, mode) {
              var rows = safeCopy;
              var index = rows.indexOf(row);
              if (index !== -1) {
                  if (mode === 'single') {
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

          /**
           * take a slice of the current sorted/filtered collection (pagination)
           *
           * @param {Number} start - start index of the slice
           * @param {Number} number - the number of item in the slice
           */
          this.slice = function splice(start, number) {
              tableState.pagination.start = start;
              tableState.pagination.number = number;
              return this.pipe();
          };

          /**
           * return the current state of the table
           * @returns {{sort: {}, search: {}, pagination: {start: number}}}
           */
          this.tableState = function getTableState() {
              return tableState;
          };

          /**
           * Use a different filter function than the angular FilterFilter
           * @param filterName the name under which the custom filter is registered
           */
          this.setFilterFunction = function setFilterFunction(filterName) {
              filter = $filter(filterName);
          };

          /**
           *User a different function than the angular orderBy
           * @param sortFunctionName the name under which the custom order function is registered
           */
          this.setSortFunction = function setSortFunction(sortFunctionName) {
              orderBy = $filter(sortFunctionName);
          };

          /**
           * Usually when the safe copy is updated the pipe function is called.
           * Calling this method will prevent it, which is something required when using a custom pipe function
           */
          this.preventPipeOnWatch = function preventPipe() {
              pipeAfterSafeCopy = false;
          };
      }])
      .directive('stTable', function () {
          return {
              restrict: 'A',
              controller: 'stTableController',
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

    ng.module('smart-table')
        .directive('stSearch', ['$timeout', function ($timeout) {
            return {
                require: '^stTable',
                scope: {
                    predicate: '=?stSearch'
                },
                link: function (scope, element, attr, ctrl) {
                    var tableCtrl = ctrl;
                    var promise = null;
                    var throttle = attr.stDelay || 400;

                    scope.$watch('predicate', function (newValue, oldValue) {
                        if (newValue !== oldValue) {
                            ctrl.tableState().search = {};
                            tableCtrl.search(element[0].value || '', newValue);
                        }
                    });

                    //table state -> view
                    scope.$watch(function () {
                        return ctrl.tableState().search;
                    }, function (newValue) {
                        var predicateExpression = scope.predicate || '$';
                        if (newValue.predicateObject && newValue.predicateObject[predicateExpression] !== element[0].value) {
                            element[0].value = newValue.predicateObject[predicateExpression] || '';
                        }
                    }, true);

                    // view -> table state
                    //element.bind('input', function (evt) {
                    //    evt = evt.originalEvent || evt;
                    //    if (promise !== null) {
                    //        $timeout.cancel(promise);
                    //    }
                    //    promise = $timeout(function () {
                    //        tableCtrl.search(evt.target.value, scope.predicate || '');
                    //        promise = null;
                    //    }, throttle);
                    //});

                    var e = element[0].tagName.toLowerCase() === 'input' ? 'keydown' : 'change';
                    // view -> table state
                    element.bind(e, function (evt) {
                        evt = evt.originalEvent || evt || window.event; //ie8 fixes
                        var target = evt.target || evt.srcElement;
                        if (promise !== null) {
                            $timeout.cancel(promise);
                        }
                        promise = $timeout(function () {
                            tableCtrl.search(target.value, scope.predicate || '');
                            promise = null;
                        }, throttle);
                    });

                }
            };
        }]);

    ng.module('smart-table')
      .directive('stSelectRow', function () {
          return {
              restrict: 'A',
              require: '^stTable',
              scope: {
                  row: '=stSelectRow'
              },
              link: function (scope, element, attr, ctrl) {
                  var mode = attr.stSelectMode || 'single';
                  element.bind('click', function () {
                      scope.$apply(function () {
                          ctrl.select(scope.row, mode);
                      });
                  });

                  scope.$watch('row.isSelected', function (newValue) {
                      if (newValue === true) {
                          element.addClass('st-selected');
                      } else {
                          element.removeClass('st-selected');
                      }
                  });
              }
          };
      });

    ng.module('smart-table')
      .directive('stSort', ['$parse', function ($parse) {
          return {
              restrict: 'A',
              require: '^stTable',
              link: function (scope, element, attr, ctrl) {

                  var predicate = attr.stSort;
                  var getter = $parse(predicate);
                  var index = 0;
                  var classAscent = attr.stClassAscent || 'st-sort-ascent';
                  var classDescent = attr.stClassDescent || 'st-sort-descent';
                  var stateClasses = [classAscent, classDescent];
                  var sortDefault;

                  if (attr.stSortDefault) {
                      sortDefault = scope.$eval(attr.stSortDefault) !== undefined ? scope.$eval(attr.stSortDefault) : attr.stSortDefault;
                  }

                  //view --> table state
                  function sort() {
                      index++;
                      predicate = ng.isFunction(getter(scope)) ? getter(scope) : attr.stSort;
                      if (index % 3 === 0 && attr.stSkipNatural === undefined) {
                          //manual reset
                          index = 0;
                          ctrl.tableState().sort = {};
                          ctrl.tableState().pagination.start = 0;
                          ctrl.pipe();
                      } else {
                          ctrl.sortBy(predicate, index % 2 === 0);
                      }
                  }

                  element.bind('click', function sortClick() {
                      if (predicate) {
                          scope.$apply(sort);
                      }
                  });

                  if (sortDefault) {
                      index = attr.stSortDefault === 'reverse' ? 1 : 0;
                      sort();
                  }

                  //table state --> view
                  scope.$watch(function () {
                      return ctrl.tableState().sort;
                  }, function (newValue) {
                      if (newValue.predicate !== predicate) {
                          index = 0;
                          element
                            .removeClass(classAscent)
                            .removeClass(classDescent);
                      } else {
                          index = newValue.reverse === true ? 2 : 1;
                          element
                            .removeClass(stateClasses[index % 2])
                            .addClass(stateClasses[index - 1]);
                      }
                  }, true);
              }
          };
      }]);

    ng.module('smart-table')
      .directive('stPagination', function () {
          return {
              restrict: 'EA',
              require: '^stTable',
              scope: {
                  stItemsByPage: '=?',
                  stDisplayedPages: '=?',
                  stPageChange: '&',
                  txtPage: '=?'
              },
              templateUrl: function (element, attrs) {
                  if (attrs.stTemplate) {
                      return attrs.stTemplate;
                  }
                  return 'template/smart-table/pagination.html';
              },
              link: function (scope, element, attrs, ctrl) {

                  scope.stItemsByPage = scope.stItemsByPage ? +(scope.stItemsByPage) : 10;
                  scope.stDisplayedPages = scope.stDisplayedPages ? +(scope.stDisplayedPages) : 5;
                  scope.pageState = { txtPage: 1 };

                  scope.currentPage = 1;
                  scope.pages = [];
                  scope.itemCount = 0;
                  scope.totalItems = 0;

                  scope.updatePage = function (reset) {

                      if (reset && scope.pageState.txtPage === '') {
                          scope.pageState.txtPage = scope.currentPage;
                      }

                      if (scope.pageState.txtPage === '') return;

                      var newPage = +(scope.pageState.txtPage);

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
                          scope.stPageChange({ newPage: scope.currentPage });
                          scope.pageState.txtPage = scope.currentPage;
                      }
                  }

                  //table state --> view
                  scope.$watch(function () {
                      return ctrl.tableState().pagination;
                  }, redraw, true);

                  //scope --> table state  (--> view)
                  scope.$watch('stItemsByPage', function (newValue, oldValue) {
                      if (newValue !== oldValue) {
                          scope.selectPage(1);
                          scope.pageState.txtPage = 1;
                      }
                  });

                  scope.$watch('stDisplayedPages', redraw);

                  //view -> table state
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

    ng.module('smart-table')
      .directive('stPipe', function () {
          return {
              require: 'stTable',
              scope: {
                  stPipe: '='
              },
              link: {

                  pre: function (scope, element, attrs, ctrl) {
                      if (ng.isFunction(scope.stPipe)) {
                          ctrl.preventPipeOnWatch();
                          ctrl.pipe = function () {
                              return scope.stPipe(ctrl.tableState(), ctrl);
                          }
                      }
                  },

                  post: function (scope, element, attrs, ctrl) {
                      ctrl.pipe();
                  }
              }
          };
      });
    ng.module('smart-table')
      .directive('csSelect', function () {
          return {
              require: '^stTable',
              template: '',
              scope: {
                  row: '=csSelect'
              },
              link: function (scope, element, attr, ctrl) {

                  element.bind('change', function () {
                      scope.$apply(function () {
                          ctrl.select(scope.row, 'multiple');
                      });
                  });

                  scope.$watch('row.isSelected', function (newValue) {
                      if (newValue === true) {
                          element.parent().addClass('st-selected');
                      } else {
                          element.parent().removeClass('st-selected');
                      }
                  });
              }
          };
      });
    ng.module('smart-table')
      .directive('csSelectAll', function () {
          return {
              require: '^stTable',
              template: '',
              scope: {
                  rows: '=csSelectAll'
              },
              link: function (scope, element, attr, ctrl) {

                  element.bind('change', function (evt) {
                      scope.globalInputState = evt.target.checked;
                      scope.$apply(function () {
                          $.each(scope.rows, function (index, row) {
                              if (row.isSelected !== evt.target.checked) {
                                  ctrl.select(row, 'multiple');
                              }
                          });
                      });
                  });

                  scope.$watch('row.isSelected', function (newValue) {
                      if (newValue === true) {
                          element.parent().addClass('st-selected');
                      } else {
                          element.parent().removeClass('st-selected');
                      }
                  });
              }
          };
      });

    ng.module('smart-table')
      .directive('csCheckAll', function () {
          return {
              require: '^stTable',
              template: '',
              scope: {
                  rows: '=csCheckAll'
              },
              link: function (scope, element) {

                  element.bind('change', function (evt) {
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


    /**
         * search options
         * @param {Object} st-search-options
         * stSearchOptions.model = property to monitor for value to filter on
         * stSearchOptions.strict = match filter value exact
         * stSearchOptions.field = specific field to filter against
         */

    ng.module('smart-table')
    .directive('stSearchOptions', function () {
        return {
            require: '^stTable',
            scope: {
                stSearchOptions: '='
            },
            link: function (scope, ele, attr, ctrl) {
                var table = ctrl;

                scope.$watch('stSearchOptions.model', function (val) {
                    ctrl.search(val, scope.stSearchOptions.field, scope.stSearchOptions.strict);
                });

                scope.$watch('stSearchOptions.range', function (val) {
                    if (val) ctrl.search(val, val.field);
                });

            }
        };
    });


    // Smart Table Select Directives

    ng.module('smart-table')
    .directive('stSelectDistinct', [function () {
        return {
            restrict: 'E',
            require: '^stTable',
            scope: {
                collection: '=',
                predicate: '@',
                predicateExpression: '='
            },
            template: '<select ng-model="selectedOption" ng-change="optionChanged(selectedOption)" ng-options="opt for opt in distinctItems"></select>',
            link: function (scope, element, attr, table) {
                var getPredicate = function () {
                    var predicate = scope.predicate;
                    if (!predicate && scope.predicateExpression) {
                        predicate = scope.predicateExpression;
                    }
                    return predicate;
                }

                scope.$watch('collection', function (newValue) {
                    var predicate = getPredicate();

                    if (newValue) {
                        var temp = [];
                        scope.distinctItems = ['All'];

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

                    if (query.distinct === 'All') {
                        query.distinct = '';
                    }

                    table.search(query, predicate);
                };
            }
        }
    }])
    .directive('stSelectMultiple', [function () {
        return {
            restrict: 'E',
            require: '^stTable',
            scope: {
                collection: '=',
                items: '=',
                predicate: '@',
                predicateExpression: '=',
                title: '=',
                selectedOptions: '='
            },
            templateUrl: function (element, attrs) {
                if (attrs.stTemplate) {
                    return attrs.stTemplate;
                }
                return 'template/smart-table/stSelectMultiple.html';
            },
            link: function (scope, element, attr, table) {
                scope.dropdownLabel = '';

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
                        return scope.title || 'All';
                    }

                    if (selected.length === 1) {
                        return selected[0].label || selected[0].value;
                    }

                    return selected.length + ' items';
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
                    scope.$watch('distinctItems', function (newVal, oldVal) {
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
        }
    }])
    .filter('customFilter', ['$filter', function ($filter) {
        var filterFilter = $filter('filter');
        var standardComparator = function (obj, text) {
            text = ('' + text).toLowerCase();
            return ('' + obj).toLowerCase().indexOf(text) > -1;
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
                    //exact match
                    if (expected.distinct) {
                        if (!actual || actual.toLowerCase() !== expected.distinct.toLowerCase()) {
                            return false;
                        }

                        return true;
                    }

                    //matchAny
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

                    //date range
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
                        //number range
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
                    //etc

                    return true;

                }
                return standardComparator(actual, expected);
            }

            var output = filterFilter(array, expression, customComparator);
            return output;
        };
    }]);


})(angular);
