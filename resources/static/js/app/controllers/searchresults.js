var asm;
(function (asm) {
    var SearchResultsController = (function () {
        function SearchResultsController($rootScope, $log, $q, Loading, $translate, $timeout, $location, $filter, Modal, GlobalServices, SearchService, $window) {
            this.$rootScope = $rootScope;
            this.$log = $log;
            this.$q = $q;
            this.Loading = Loading;
            this.$translate = $translate;
            this.$timeout = $timeout;
            this.$location = $location;
            this.$filter = $filter;
            this.Modal = Modal;
            this.GlobalServices = GlobalServices;
            this.SearchService = SearchService;
            this.$window = $window;
            this.searchResults = {};
            this.defaultSearchResults = {};
            this.subcategories = {};
            var self = this;
            self.showResults = false;
            self.noResults = false;
            self.searching = false;
            self.allCategories = true;
            //*** temporary flag to allow for faster search
            self.skipRealSearch = false;
            self.defaultSearchResults = {
                "searchedTerm": null,
                "totalResults": 0,
                "totalUnlimitedResults": 0,
                "categories": [
                    {
                        "name": "Resources",
                        "totalItems": 0,
                        "showcategory": false,
                        "subcategories": []
                    },
                    {
                        "name": "Services",
                        "totalItems": 0,
                        "showcategory": false,
                        "subcategories": []
                    },
                    {
                        "name": "Templates",
                        "totalItems": 0,
                        "showcategory": false,
                        "subcategories": []
                    }]
            };
            //console.log( 'constructor' );
            self.initialize();
        }
        SearchResultsController.prototype.initialize = function () {
            var self = this;
            //$( document ).ready( function ()
            //{
            //    //to do                
            //});
            //console.log( 'initialize' );
            var search = self.$location.search();
            if (search != undefined && search['term'] != undefined) {
                self.searchTerm = search['term'];
            }
            $("#searchimg").css("display", "inline");
            $("#searchimgfocus").css("display", "none");
            if (self.searchTerm) {
                //self.$timeout.cancel( timeoutPromise_con );
                //timeoutPromise_con = self.$timeout( function ()
                //{
                //    self.executeSearch();
                //}, delayInMs );
                //execute the search on initial page load
                //self.executeSearch();
                //execute the search on initial page load by calling searchInput_searchChange on the element
                self.searchInput_searchChange();
            }
            //this never runs and never throws an error
            //$( "#searchInput" ).focus( function ()
            //{
            //    console.log( 'focus' );
            //    $( "#searchInput" ).addClass( "on" );
            //    $( "#searchimg" ).css( "display", "none" );
            //    $( "#searchimgfocus" ).css( "display", "inline" );
            //});
            //$( "#searchInput" ).on( "focus", function ()
            //{
            //    console.log( 'focus' );
            //    $( "#searchInput" ).addClass( "on" );
            //    $( "#searchimg" ).css( "display", "none" );
            //    $( "#searchimgfocus" ).css( "display", "inline" );
            //});
            //this never runs and never throws an error
            //$( "#searchInput" ).focusout( function ()
            //{
            //    console.log( 'focusout' );
            //    if ( !self.searchTerm || self.searchTerm.length == 0 )
            //    {
            //        $( "#searchInput" ).removeClass( "on" );
            //        $( "#searchimg" ).css( "display", "inline" );
            //        $( "#searchimgfocus" ).css( "display", "none" );
            //    }
            //});
            //$( "#searchInput" ).on( "focusout", function ()
            //{
            //    console.log( 'focusout' );
            //    if ( !self.searchTerm || self.searchTerm.length == 0 )
            //    {
            //        $( "#searchInput" ).removeClass( "on" );
            //        $( "#searchimg" ).css( "display", "inline" );
            //        $( "#searchimgfocus" ).css( "display", "none" );
            //    }
            //});
            //sample $watch from loaderModal.ts
            //$scope.$watch( 'modal.params.loaderMessage', function ( newVal, oldVal )
            //{
            //    self.loaderMessage = newVal;
            //});
            //sample from devices.ts
            //self.$watch('selectedDevice', function (newValue, oldValue) {
            //    alert("here");
            //});
            //sample from web:  http://stackoverflow.com/questions/24078535/angularjs-controller-as-syntax-and-watch
            //$scope.$watch( angular.bind( this, function ()
            //{
            //    return this.name;
            //}), function ( newVal )
            //{
            //    console.log( 'Name changed to ' + newVal );
            //});
            //these do not work:
            //self.$rootScope.$watch( self.searchTerm, self.searchInput_searchChange() );
            //self.$watch( self.searchTerm, self.searchInput_searchChange() );
            //var boundvar = angular.bind( self, function ()
            //{
            //    return self.searchTerm;
            //});
            //self.$rootScope.$watch( boundvar, self.searchInput_searchChange() );
            self.$rootScope.$on('$locationChangeStart', function (a, destination, c) {
                //console.log( '$locationChangeStart' );
                if (destination.indexOf("Search") === -1) {
                    self.clearSearch(true);
                }
                else {
                    self.initialize();
                }
            });
            //$( "body" ).on( "click", function ( e )
            //{
            //    if ( !$( e.target ).parents( ".globalSearch" ).length )
            //    {
            //        self.clearSearch( true );
            //    }
            //});
        };
        SearchResultsController.prototype.activate = function () {
            var self = this;
            //console.log( 'activate' );
            //searchpageLink
            //searchpageDesc
            //return $( '<span>' ).html( "<strong>" + str + "</strong>" ).text();
            //return $( '<strong>' ).text( str );
            //var activateTimeoutPromise: any;
            //var delayInMs = 1000;
            //self.$timeout.cancel( activateTimeoutPromise );
            //activateTimeoutPromise = self.$timeout( function ()
            //{
            //    self.addBold();
            //}, delayInMs );
        };
        SearchResultsController.prototype.addBold = function () {
            var self = this;
            //return $( '<span>' ).html( "<strong>" + str + "</strong>" ).text();
            //return $( '<strong>' ).text( str );
            //console.log( 'addBold' );
            //$( '#searchpageDesc:contains("Hostname")' ).css( { 'font-weight': 'bold;'} );                                              
            //try this on a parent element?
            var html = $('.searchpageDesc').html();
            $('.searchpageDesc').html(html.replace(/Hostname/gi, '<strong>$&</strong>'));
            //try using a class--it may be having an issue finding a collection of objects with the same id
            //$( '.searchpageDesc' ).each( function ( index, element )
            //{
            //    var $desc = $( this );
            //    console.log( $desc.html() );
            //    console.log( $desc.text() );
            //});
        };
        SearchResultsController.prototype.goPreviousPage = function () {
            var self = this;
            self.$window.history.back();
        };
        SearchResultsController.prototype.limitString = function (str, limit) {
            var self = this;
            return self.$filter('ellipsis')(str, limit);
        };
        SearchResultsController.prototype.searchInput_onFocus = function () {
            var self = this;
            $("#searchInput").addClass("on");
            $("#searchimg").css("display", "none");
            $("#searchimgfocus").css("display", "inline");
        };
        SearchResultsController.prototype.searchInput_onBlur = function () {
            var self = this;
            if (!self.searchTerm || self.searchTerm.length == 0) {
                $("#searchInput").removeClass("on");
                $("#searchimg").css("display", "inline");
                $("#searchimgfocus").css("display", "none");
            }
        };
        SearchResultsController.prototype.allCheck = function () {
            var self = this;
            self.searchResults.categories.forEach(function (category) {
                if (self.allCategories) {
                    category.showcategory = true;
                }
                else {
                    category.showcategory = false;
                }
                self.categoryCheck(category);
            });
        };
        SearchResultsController.prototype.categoryCheck = function (cat) {
            var self = this;
            //var connection: any = _.filter( self.portView.portConnections, { nicId: nicId, nicPortId: portId });
            //var sw = cat.name;
            //var subcats: any = _.filter( self.subcategories, function ( s: any )
            //{
            //    return s.indexOf( cat.name ) == 0;
            //});
            //_.map( subcats, function ( x )
            //{
            //    x = true;
            //    return x
            //});            
            //console.log( JSON.stringify( subcats ) );
            //test this
            //for the category checked, set the value of all associated subcategories to the same value as the category
            angular.forEach(self.subcategories, function (value, key) {
                if (key.indexOf(cat.name) == 0) {
                    //found
                    self.subcategories[key] = cat.showcategory;
                }
                else {
                }
                if (!cat.showcategory) {
                }
            });
            //self.subcategories.forEach(( localsub ) =>
            //_.forEach( self.subcategories, function ( value, key )
            //left off on this, but this was method 1
            //angular.forEach( self.subcategories,
            //(subcat: any) =>
            //{
            //    if ( subcat.indexOf( cat.name ) == 0 )
            //    {
            //        if ( cat.showcategory )
            //        {
            //            subcat.value = true;
            //        } else
            //        {
            //            subcat.value = false;
            //        }
            //    }
            //});
            //self.searchResults.categories[cat].subcategories.forEach(( subcat ) =>
            //{
            //    //subcat.showsubcategory = true;
            //    //self.subcategories[cat.name + subcat]
            //    self.subcategories.forEach(( localsub ) =>
            //    {
            //        if ( localsub.startsWith( cat.name ) )
            //        {
            //            if ( cat.showcategory )
            //            {
            //                localsub = true;
            //            } else
            //            {
            //                localsub = false;
            //            }
            //        }
            //    });
            //});
        };
        SearchResultsController.prototype.subcategoryCheck = function (cat, sub) {
            var self = this;
            //cateogry ng-model="searchResults.searchResults.categories[category].showcategory"
            //subcategory ng-model="searchResults.subcategories[category.name + sub]"
            var subvalue = self.subcategories[cat.name + sub];
            //if all subcats are unchecked, then uncheck the category
            //if all subcats are checked, then check the category; no, if even one subcat is checked, then check the category, so that we show the header
            if (subvalue === true) {
                cat.showcategory = true;
            }
            else {
                var allsubcatssame = true;
                angular.forEach(self.subcategories, function (value, key) {
                    if (key.indexOf(cat.name) == 0) {
                        //found
                        if (self.subcategories[key] != subvalue) {
                            allsubcatssame = false;
                        }
                    }
                    else {
                    }
                });
                if (allsubcatssame) {
                    //check/unchceck the category to match the subcat
                    cat.showcategory = subvalue;
                }
            }
        };
        SearchResultsController.prototype.subcategoryCheck_Old = function (cat, sub) {
            var self = this;
            //cateogry ng-model="searchResults.searchResults.categories[category].showcategory"
            //subcategory ng-model="searchResults.subcategories[category.name + sub]"
            var subvalue = self.subcategories[cat.name + sub];
            //if even one subcat is unchecked, then uncheck the category--is this really what we want to do we want a tri-state, disabled checked value or something else?  no
            //if all subcats are unchecked, then uncheck the category
            if (subvalue === false) {
            }
            else {
                //if all subcats are checked, then check the category
                var allsubcatstrue = true;
                angular.forEach(self.subcategories, function (value, key) {
                    if (key.indexOf(cat.name) == 0) {
                        //found
                        if (self.subcategories[key] === false) {
                            //if even one subcat is false, then the cat should ramain false
                            allsubcatstrue = false;
                        }
                    }
                    else {
                    }
                });
                if (allsubcatstrue) {
                    //check the category
                    cat.showcategory = true;
                }
            }
        };
        SearchResultsController.prototype.searchInput_searchChange = function () {
            var self = this;
            //console.log('searchInput_searchChange function');
            var timeoutPromise;
            var delayInMs = 650;
            //cancel and reset timer to create delay for:  user entry, variable updates before waiting on promise
            self.$timeout.cancel(timeoutPromise);
            timeoutPromise = self.$timeout(function () {
                self.executeSearch();
            }, delayInMs);
        };
        SearchResultsController.prototype.clearSearch = function (clearTerm) {
            var self = this;
            //console.log( 'clearSearch' );
            self.$rootScope.$evalAsync(function () {
                if (clearTerm) {
                    self.showResults = false;
                    self.searchTerm = "";
                    self.noResults = false;
                    self.searchResults = angular.copy(self.defaultSearchResults);
                    $("#searchInput").removeClass("on");
                    $("#searchimg").css("display", "inline");
                    $("#searchimgfocus").css("display", "none");
                    $("#searchInput").blur();
                }
            });
        };
        SearchResultsController.prototype.executeSearch = function () {
            var self = this;
            //console.log( 'executeSearch' );
            var currentTerm = self.searchTerm.trim();
            if (currentTerm === undefined || currentTerm === "") {
                self.clearSearch(true);
                self.showResults = false;
                self.noResults = false;
            }
            else {
                self.searching = true;
                //console.log( 'searching:  ' + self.searching );
                if (self.skipRealSearch) {
                    self.skipSearch();
                    self.searching = false;
                    self.allCategories = true;
                    self.showResults = true;
                    self.noResults = false;
                }
                else {
                    self.doSearch(currentTerm);
                }
            }
            //self.searching = false;
            //console.log( 'searching:  ' + self.searching );
        };
        SearchResultsController.prototype.doSearch = function (searchTerm) {
            var self = this;
            if (!searchTerm || searchTerm.length < 3) {
                //console.log( '*** short search term' );
                self.searching = false;
                //console.log( 'searching:  ' + self.searching );
                return;
            }
            self.searchTerm = searchTerm;
            self.searchResults = angular.copy(self.defaultSearchResults);
            //console.log( 'running the regular search with doSearch' );
            //SearchService.search promise
            //by default, the search page does not have a limit, unlike the measthead search ( refer to app.js onSearch)
            self.SearchService.search(searchTerm).then(function (results) {
                self.processResults(results);
                self.searching = false;
                //console.log( 'searching:  ' + self.searching );
                self.allCategories = true;
                self.showResults = true;
                self.noResults = false;
            }).catch(function (data) {
                self.GlobalServices.DisplayError(data, self.$rootScope.errors);
                self.$log.debug(data);
            });
        };
        SearchResultsController.prototype.processResults = function (results) {
            var self = this;
            if (results != undefined && (results.categories != undefined && results.categories.length > 0)) {
                self.searchResults = results;
                //processing subcategories
                angular.forEach(self.searchResults.categories, function (category) {
                    category.showcategory = true;
                    category.subcategories = [];
                    var uniquesubcategories = self.$filter('unique')(category.items, 'subcategory');
                    angular.forEach(uniquesubcategories, function (usub) {
                        self.subcategories[category.name + usub.subcategory] = true;
                        category.subcategories.push(usub.subcategory);
                        //var show
                        //if ( usub.subcategory === null )
                        //{
                        //    show = false;
                        //} else
                        //{
                        //    show = true;                                
                        //}
                        //category.subcategories.push( {
                        //    id: category.name + usub.subcategory,
                        //    name: usub.subcategory,
                        //    showsubcategory: show
                        //    });
                    });
                });
            }
            //console.log( 'post processing self.searchResults:  ' + JSON.stringify( self.searchResults ) );
            //console.log( 'post processing self.subcategories:  ' + JSON.stringify( self.subcategories ) );
        };
        SearchResultsController.prototype.skipSearch = function () {
            var self = this;
            var results = {};
            self.processResults(results);
        };
        SearchResultsController.$inject = ['$rootScope', '$log', '$q', 'Loading', '$translate',
            '$timeout', '$location', '$filter', 'Modal', 'GlobalServices', 'SearchService', '$window'];
        return SearchResultsController;
    }());
    asm.SearchResultsController = SearchResultsController;
    angular
        .module('app')
        .controller('SearchResultsController', SearchResultsController);
})(asm || (asm = {}));
//# sourceMappingURL=searchresults.js.map
