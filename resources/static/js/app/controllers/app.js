angular.module('app')
    .controller('AppController',
    [
       '$rootScope', '$q', '$filter', '$route', '$window', '$http', '$templateCache', '$scope', '$log', '$resource', '$translate', 'GlobalServices', '$location', 'Commands', '$cookies', 'Modal', 'localStorageService', 'SearchService', 'Dialog', '$timeout', 'About',
        function ($rootScope, $q, $filter, $route, $window, $http, $templateCache, $scope, $log, $resource, $translate, GlobalServices, $location, Commands, $cookies, Modal, localStorageService, SearchService, Dialog, $timeout, About) {

            //in order to update the masthead from using icon files to using images with background-images, we overwrite the templateCache entries used by the components with custom templates
            $templateCache.put("__clarity/masthead.html", "<div class=\"navbar navbar-inverse clarity\"><div class=container-fluid><div id=navHover class=\"navbar-header menu-top\" ng-class=\"{\'hoverClass\' : data.showpinnednav === \'dontshow\' && hideTopNav !== true, \'nohoverClass\' : data.showpinnednav !== \'dontshow\' || hideTopNav == true}\"><span class=navbar-brand ng-if=hideTopNav style=\"margin-top: 0; margin-bottom: 0\"><i class='masthead-logo-vxflexmanager-white'></i>{{applicationTitle}}</span><div class=\"nav navbar-nav\" ng-if=!hideTopNav style=\"margin-top: 0; margin-bottom: 0\"><masthead-top-menu menu-items=menuItems icon-class=iconClass pin-menu=pinMenu><menu-buttons-left><ng-transclude ng-transclude-slot=menuleft></ng-transclude></menu-buttons-left><menu-buttons><ng-transclude ng-transclude-slot=menu></ng-transclude></menu-buttons><menu-pinned-buttons><ng-transclude ng-transclude-slot=pinnedmenu></ng-transclude></menu-pinned-buttons></masthead-top-menu></div></div><div id=navbar class=\"nav navbar-nav navbar-right navbar-collapse collapse\" style=\"margin-right: 5px\"><ng-transclude ng-transclude-slot=header></ng-transclude></div></div></div>");
            $templateCache.put("__clarity/mastheadTopMenu.html", "<div class=menu-top><div style=\"position: relative; margin-right: 0px\" class=\"pull-left top-nav-arrow\"><span ng-show=data.showPinNavigation class=navbar-brand><i class='masthead-logo-vxflexmanager-white'></i>{{applicationTitle}}</span><button class=\"btn btn-link\" ng-click=actions.toggleNav() style=\"padding: 0; border: 0; text-decoration: none; margin-left: -14px\"><span ng-show=\"data.showPinNavigation == false\" class=navbar-brand style=\"margin-right: 20px; padding-left: 29px\"><i class='masthead-logo-vxflexmanager-white'></i>{{applicationTitle}}</span><i ng-show=data.showTopMenuButton style=\"position: relative; top: 19px; right: 18px\" class=ci-arrow-chev-down-2-med></i></button></div><span class=backdrop ng-show=data.showNavigation||false ng-click=\"data.showNavigation=false\"></span><div class=top-menu ng-show=data.showNavigation||false><nav role=navigation><div class=form><div style=\"position: relative; left: -6px\"><button class=\"btn btn-link\" ng-click=actions.toggleNav() style=\"margin-left: 19px; padding: 0; border: 0; text-decoration: none; color: #007db8 !important; background-color: #fff; padding-top: 2px\"><span class=\"navbar-brand navbarOpen\" style=\"margin-right: 2px\"><i class='masthead-logo-vxflexmanager-blue'></i>{{applicationTitle}}</span><i style=\"position: relative; top: 19px\" class=ci-arrow-chev-up-2-med></i></button> <button class=\"btn btn-link\" ng-click=actions.toggleNav() style=\"text-decoration: none; padding: 10px; line-height: 0px; border: 0; float: right; margin-top: 7px; margin-right: 14px\"><i style=\"margin: 0px\" class=ci-state-critical-health></i></button> <button class=\"btn btn-link\" ng-click=actions.pinNav() style=\"padding: 10px; line-height: 0px; border: 0; float: right; margin-top: 7px; margin-right: 3px\"><i style=\"margin: 0px\" class=\"text-primary ci-pin\"></i></button></div><hr style=\"width: 95%; margin-left: 11px; margin-right: auto; margin-top: 0px; border-color: #BBB\"><div id=top-buttons-left class=pull-left style=\"margin-right: 20px\"><ng-transclude ng-transclude-slot=extraleft></ng-transclude></div><ul class=\"list-unstyled list-inline pull-left\"><li class=menu-item ng-class=\"{\'hidden\': menuItem.hideintopmenu==true}\" ng-repeat=\"menuItem in menuItems\"><span class=menu-item-block ng-class=\"{\'navactive\': menuItem.activeitem == true}\"><i class={{menuItem.icon}}></i> <a href={{menuItem.href}}>{{menuItem.label}}</a></span><ul class=list-unstyled><li class=sub-menu-item ng-repeat=\"subMenuItem in menuItem.children\"><button ng-disabled=\"subMenuItem.disabled==true\" class=\"btn btn-link\" ng-class=\"{\'navactive\': subMenuItem.activeitem == true,  \'disabled\':subMenuItem.disabled==true}\" ng-click=actions.go(subMenuItem.href) ng-if=\"subMenuItem.label != \'Controllers\' && subMenuItem.label != \'Fabric\'\">{{subMenuItem.label}}</button></li></ul><ul class=list-unstyled><li class=sub-menu-item ng-class=\"{\'subsection\': subMenuItem.label === \'Fabric\',\'disabled\':subMenuItem.disabled==true}\" ng-repeat=\"subMenuItem in menuItem.children\"><button class=\"btn btn-link\" ng-disabled=\"subMenuItem.disabled==true\" ng-class=\"{\'navactive\': subMenuItem.activeitem == true}\" ng-click=actions.go(subMenuItem.href) ng-if=\"subMenuItem.label === \'Controllers\' || subMenuItem.label === \'Fabric\'\">{{subMenuItem.label}}</button></li></ul></li></ul><div id=top-buttons class=pull-right style=\"margin-right: 20px\"><ng-transclude ng-transclude-slot=extra></ng-transclude></div></div></nav></div><script type=text/ng-template id=topMenuPinTemplate.html><button ng-disabled=\"subMenuItem.disabled==true\" ng-click=\"actions.go(subMenuItem.href)\" class=\"sub-menu-button btn btn-link\" ng-class=\"{\'disabled\':subMenuItem.disabled==true}\">\r\n            {{subMenuItem.label}}\r\n        </button>\r\n        <span tabindex=\"0\" class=\"collapsed\" data-toggle=\"collapse\" data-target=\"#pin-{{subMenuItem.guid | toclassname}}\" ng-show=\"subMenuItem.children.length > 0\">\r\n            <i class=\"ci-arrow-chev-down-2-med\"></i>\r\n            <i class=\"ci-arrow-chev-down-2-med\"></i>\r\n        </span>\r\n        <ul id=\"pin-{{subMenuItem.guid | toclassname}}\" class=\"list-unstyled collapse\" ng-show=\"subMenuItem.children.length > 0\">\r\n            <li ng-repeat=\"subMenuItem in subMenuItem.children\" class=\"sub-menu-item\" ng-include=\"\'topMenuTemplate.html\'\"></li>\r\n        </ul></script><div id=pinned-nav ng-if=data.showPinNavigation><div class=panel-group id=pinaccordion role=tablist aria-multiselectable=true><div class=\"panel panel-default\" ng-repeat=\"menuItem in menuItems\"><div class=panel-heading role=tab ng-class=\"{\'pinright\': menuItem.pinright==true}\"><h4 class=\"panel-title hoverItem\"><i class={{menuItem.icon}}></i> <a href={{menuItem.href}}>{{menuItem.label}}</a> <a role=button class=collapsed data-toggle=collapse data-parent=#pinaccordion data-target=\"#pin-{{menuItem.guid | toclassname}}\" aria-expanded=true><i class=ci-arrow-chev-down-2-med ng-show=\"menuItem.children.length > 0\"></i> <i class=ci-arrow-chev-up-2-med ng-show=\"menuItem.children.length > 0\"></i></a></h4></div><div id=\"pin-{{menuItem.guid | toclassname}}\" class=\"panel-collapse collapse\" role=tabpanel><div class=panel-body><ul class=\"list-unstyled subnav\" ng-show=\"menuItem.children.length > 0\"><li ng-repeat=\"subMenuItem in menuItem.children\" class=sub-menu-item ng-include=\"\'topMenuPinTemplate.html\'\" ng-class=\"{\'navactive\': subMenuItem.activeitem == true}\"></li></ul></div></div></div><ng-transclude class=menu-extras ng-transclude-slot=extrapinned></ng-transclude><button id=unpin class=\"btn btn-link\" ng-click=actions.unpinNav()><i style=\"top: 3px\" class=\"text-primary ci-pin\"></i></button></div></div></div>");

            $scope.navigation = GlobalServices.navigation;
            $scope.appTitle = $translate.instant('ApplicationTitle')
            $scope.$on('navigationUpdated', function (evt, navigation) {
                $scope.navigation = navigation;
            });

            $scope.frontPageImages = {
                first: true,
                second: false,
                third: false
            };

            $scope.goTo = function (route) {
                $('#settings_menu').collapse('hide');
                $location.path(route);
            };

            $scope.setupWizard = function () {

                $('#settings_menu').collapse('hide');

                var setupWizard = Modal({
                    title: $translate.instant('SETUPWIZARD_Title'),
                    onHelp: function () {
                        GlobalServices.showHelp();
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/setupwizard.html',
                    controller: 'SetupWizardController as SetupWizard',
                    params: {
                    },
                    onComplete: function () {
                        $scope.initialize();
                    }
                });
                setupWizard.modal.show();
            }

            $scope.data = {
                showPinNavigation: true,
                searchResults: [],
                searching: false,
                alerts: [
                    {
                        id: 1,
                        description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit',
                        severity: 'warning',
                        source: 'admin',
                        date: "12/31/2015 12:59 PM"
                    },
                    {
                        id: 1,
                        description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit',
                        severity: 'critical',
                        source: 'admin',
                        date: "12/31/2015 12:59 PM"
                    }
                ]
            };

            $rootScope.$on('pin', function () {
                $scope.pinnav = true;
            });

            $rootScope.$on('unpin', function () {
                $scope.pinnav = false;
            });

            $scope.updateNavigation = function (menuItems) {
                var newMenu = [];
                //Add the first menu item (Home) to the top.  If there is only one Global menu item, save it for the end.
                if (GlobalServices.navigation.length >= 2) newMenu.push(GlobalServices.navigation[0]);
                menuItems.sort(function (a, b) {
                    var compare = 0;
                    if (a['order'] == undefined || b['order'] == undefined) compare = 0;
                    else compare = a['order'] - b['order'];
                    if (compare === 0) {
                        if (a['label'] < b['label']) compare = -1;
                        if (a['label'] > b['label']) compare = 1;
                    }
                    return compare;
                });
                newMenu = newMenu.concat(menuItems);
                //Add any other global menu items to the end (start with 0 if there is only 1, start with 1 if there are at least two)
                for (var i = (GlobalServices.navigation.length >= 2) ? 1 : 0; i < GlobalServices.navigation.length; i++) {
                    newMenu.push(GlobalServices.navigation[i]);
                }
                $scope.navigation = newMenu;
                if ($location.path() == undefined || $location.path() === '') {
                    var newHash = $scope.navigation[0]['href'];
                    if (newHash.indexOf('#') === 0) newHash = newHash.slice(1, newHash.length); //Strip off the leading hash
                    if (newHash.indexOf('/') !== 0) newHash = '/' + newHash; //Ensure path starts with a slash
                    $location.path(newHash);
                }
            };

            $scope.about = function () {

                $http.post(Commands.data.about.getAboutData, {}).then(function (data) {
                    var aboutData = data.data.responseObj;
                    //about-logo-vxflexmanager-blue
                    //ci-logo-asm
                    var about = About(
                           "about-logo-vxflexmanager-blue",
                           $translate.instant('ApplicationTitle'),
                           aboutData.version + " / " + aboutData.build,
                           aboutData.patent,
                           aboutData.trademark,
                           aboutData.copyright
                    );

                }).catch(function (data) {
                    d.resolve();
                    GlobalServices.DisplayError(data.data);
                });


                //var aboutModal = Modal({
                //    modalSize: 'modal-md',
                //    templateUrl: 'views/about.html',
                //    controller: 'AboutController as aboutController'
                //});

                //aboutModal.modal.show();
                //aboutModal.$on('modal:closed', function () {
                //    //$scope.initialize();
                //});
            };

            $scope.help = function () {
                GlobalServices.showHelp();
            };

            $scope.logout = function () {

                localStorageService.remove('ASM.currentUserObject');
                //sessionStorage.removeItem('ASM.currentUserObject');
                GlobalServices.CurrentUser = null;
                $window.sessionStorage.passwordDialogShown = 'false';

                $http.post(Commands.data.session.doLogout, {})
                    .success(function (data, status, headers, config) {
                        delete $window.sessionStorage.token;
                        window.location = data.responseObj.url;
                    })
                    .error(function (data, status, headers, config) {
                        delete $window.sessionStorage.token;
                        window.location = '/';
                    });
            };

            $scope.editUser = function () {

                var editUser = Modal({
                    title: $translate.instant('SETTINGS_EditUser'),
                    modalSize: 'modal-md',
                    templateUrl: 'views/settings/modals/edituser.html',
                    controller: 'EditUserController',
                    params: {
                        user: angular.copy($scope.data.currentUser)
                    }
                });

                editUser.modal.show();
                editUser.$on('modal:closed', function () {
                    $scope.initialize();
                }); //When the modal is closed, update the data.

            };


            $scope.onSearch = function (searchTerm, limit) {

                //$("#searchBar").off('focus').focus(function () {
                //    $("#searchicon").css("display", "none");
                //    $("#searchiconfocus").css("display", "inline");
                //});

                ////not sure how to keep search bar open long enough to click on an item or scroll
                //$("#searchBar").off('focusout').focusout(function () {
                //    if ($("#searchBar").val().length == 0) {
                //        setTimeout(function() {
                //            $("#searchicon").css("display", "inline");
                //            $("#searchiconfocus").css("display", "none");
                //        }, 1000);
                //    }
                //});

                if (!searchTerm || searchTerm.length < 3) return;
                $scope.data.searchTerm = searchTerm;
                $scope.data.searchResults = [];
                $scope.data.searching = true;
                SearchService.search(searchTerm, 5).then(function (results) {
                    //$scope.data.searchTerm = searchTerm;
                    $scope.data.searchResults = results;
                    //console.log('$scope.data.searchResults:  ' + JSON.stringify($scope.data.searchResults));
                    $scope.data.searching = false;
                });
            };

            $scope.mockServices = function () {
                //$http.get(Commands.mockServices)
                //    .success(function (data, status, headers, config) {
                //        $route.reload();
                //        $rootScope.$emit('MenuTop::Hide', 'AppController.mockServices()');
                //    })
                //    .error(function (data, status, headers, config) {
                //        $route.reload();
                //        $rootScope.$emit('MenuTop::Hide', 'AppController.mockServices()');
                //    });
            };

            $scope.createTemplate = function () {
                var createTemplateModal = Modal({
                    title: $translate.instant('TEMPLATES_CreateTemplate'),
                    onHelp: function () {
                        GlobalServices.showHelp('creatingtemplate');
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/templatewizard.html',
                    controller: 'TemplateWizardController as templateWizardController',
                    params: {
                    },
                    onComplete: function (id) {
                        $timeout(function () {
                            self.$location.path("templatebuilder/" + id + "/edit");
                        }, 500)
                    }
                });
                createTemplateModal.modal.show();
            }

            $scope.addExistingService = function () {
                var addServiceWizard = Modal({
                    title: $translate.instant('SERVICE_ADD_EXISTING_SERVICE_Title'),
                    onHelp: function () {
                        GlobalServices.showHelp('AddingExistingService');
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/services/addexistingservice.html',
                    controller: 'AddExistingServiceController as addExistingServiceController',
                    params: {
                    },
                    onCancel: function () {
                        var confirm = Dialog($translate.instant('GENERIC_Confirm'), $translate.instant('SERVICE_ADD_EXISTING_SERVICE_Cancel_Confirmation'));
                        confirm.then(function (modalScope) {
                            addServiceWizard.modal.dismiss();
                        });
                    }
                });
                addServiceWizard.modal.show();
            }

            $scope.deployNewService = function () {
                var addServiceWizard = Modal({
                    title: $translate.instant('SERVICES_NEW_SERVICE_DeployService'),
                    onHelp: function () {
                        GlobalServices.showHelp('deployingserviceoverview');
                    },
                    modalSize: 'modal-lg',
                    templateUrl: 'views/services/deployservice/deployservicewizard.html',
                    controller: 'DeployServiceWizard as deployServiceWizard',
                    params: {
                    },

                    onCancel: function () {
                        Dialog($translate.instant('GENERIC_Confirm'), $translate.instant('SERVICES_DEPLOY_ConfirmWizardClosing')).then(function () {
                            addServiceWizard.modal.close();
                        });
                    }
                });
                addServiceWizard.modal.show();
            }

            $scope.setActiveMenu = function () {
                //check for current page and set nav item to active class
                var currentpage = '#' + $location.path();

                angular.forEach($scope.navigation, function (menuItem) {
                    if (currentpage === menuItem.href) {
                        menuItem.activeitem = true;
                    } else {
                        menuItem.activeitem = false;
                    }

                    if (menuItem.children) {
                        angular.forEach(menuItem.children, function (subMenuItem) {
                            if (currentpage === subMenuItem.href) {
                                subMenuItem.activeitem = true;
                            } else {
                                subMenuItem.activeitem = false;
                            }
                        });
                    }
                });
            };

            $scope.limitString = function (str, limit) {
                return GlobalServices.limitString(str, limit);
            };

            //set active navigation by tabs
            $rootScope.$on('$locationChangeSuccess', function () { $scope.setActiveMenu(); });
            //$rootScope.$on('$routeChangeSuccess', function () { $scope.setActiveMenu(); });

            $scope.loadServiceData = function () {

                $http.post(Commands.data.dashboard.getServicesDashboardData, {})
                    .success(function (data, status, headers, config) {

                        var response = data.responseObj;

                        //'Error', red, self.viewmodel.servicesData.servicecriticalcount()
                        //'Healthy', green, self.viewmodel.servicesData.servicesuccesscount()
                        //'In Progress', unknown/blue, self.viewmodel.servicesData.serviceunknowncount()
                        //'Warning', yellow, self.viewmodel.servicesData.servicewarningcount()
                        //'Cancelled', cancelled, self.viewmodel.servicesData.servicecancelledcount()

                        $rootScope.ASM.servicesData = {
                            healthy: response.servicesuccesscount ? response.servicesuccesscount : 0,
                            warning: response.servicewarningcount ? response.servicewarningcount : 0,
                            error: response.servicecriticalcount ? response.servicecriticalcount : 0,
                            pending: response.servicependingcount ? response.servicependingcount : 0,
                            inprogress: response.serviceunknowncount ? response.serviceunknowncount : 0,
                            cancelled: response.servicecancelledcount ? response.servicecancelledcount : 0,
                            incomplete: response.serviceincompletecount ? response.serviceincompletecount : 0,
                            servicemode: response.serviceservicemodecount ? response.serviceservicemodecount : 0
                        };

                    })
                    .error(function (data, status) {

                    });
            };
            $scope.initialize = function () {

                $scope.servicesDashboardInterval = window.setInterval(function () { $scope.loadServiceData(); }, 30000);

                $http.post(Commands.data.initialSetup.gettingStarted, null)
                    .success(function (data, status, headers, config) {
                        $rootScope.ASM.showInitialSetup = !data.responseObj.initialSetupCompleted;
                    })
                    .error(function (data, status) {
                        $rootScope.ASM.showInitialSetup = false;
                    });


                window.clearInterval($scope.templateInterval);
                $scope.templateInterval = window.setInterval(function () {
                    $http.post(Commands.data.templates.getQuickTemplateList, null)
                        .success(function (data, status, headers, config) {
                            $scope.templatesAvailable = data.responseObj.length;
                        })
                        .error(function (data, status) {
                            $scope.templatesAvailable = 0;
                        });
                }, 30000);

                //load current user, get role, send role in to get list of permissions
                $http.post(Commands.GenerateUrl(Commands.data.users.getCurrentUser, {}))
                    .success(function (data) {

                        var user = data.responseObj;
                        user.userPreference = $scope.parseUserPreferences(user.userPreference);
                        $rootScope.ASM.CurrentUser = angular.copy(user);
                        localStorageService.set('ASM.currentUserObject', angular.copy(user));

                        //var stringUser = JSON.stringify(user);
                        //sessionStorage.setItem('ASM.currentUserObject', angular.copy(stringUser));

                        //show password modal
                        if ($rootScope.ASM.IsInRole('administrator') && $rootScope.ASM.CurrentUser.userPreference.showdefaultpasswordprompt && $rootScope.ASM.CurrentUser.showdefaultpasswordprompt && (!$window.sessionStorage.passwordDialogShown || $window.sessionStorage.passwordDialogShown !== 'true')) {
                            $window.sessionStorage.passwordDialogShown = 'true';

                            var self = this;

                            var defaultPasswordModal = Modal({
                                title: $translate.instant('DEFAULTPASSWORD_Title'),
                                //modalSize: 'modal-sm',
                                className: 'showOnTop',
                                templateUrl: 'views/defaultpasswordmodal.html',
                                controller: 'DefaultPasswordModalController as defaultPassword',
                                onComplete: function (modalScope) {
                                    if (defaultPasswordModal.defaultPassword.checked) {

                                        $rootScope.ASM.CurrentUser.userPreference.showdefaultpasswordprompt = false;

                                        $http.post(Commands.data.users.updateUserPreferences, angular.toJson($rootScope.ASM.CurrentUser.userPreference))
                                            .success(function (data) {

                                            });

                                    }
                                }
                            });

                            defaultPasswordModal.modal.show();
                            //window.setTimeout(function () { defaultPasswordModal.modal.show() }, 30000);

                        }
                    })
                    .error(function () {
                    });


                $(document).on('shown.bs.tab', function (e) {

                    //fire resize event on tab change to give time to repaint modal static backdrop
                    window.setTimeout(function () {
                        $(window).trigger('resize');
                    }, 500);

                    var tab = e.target;
                    var key = $(tab).closest('.nav-tabs')[0].id;
                    var val = $(tab).attr('aria-controls');
                    if (!val)
                        val = $(tab).attr('data-target').replace('#', '');

                    var tabstate = localStorageService.get('tabstate') || {};

                    tabstate[key] = val;
                    localStorageService.set('tabstate', tabstate);

                });

                //collapse any collapsibles if clicking off of menu
                $('body').on('click', function (e) {

                    angular.forEach($('.menu-top .panel-collapse'), function (element) {
                        if (element.id !== e.target.id)
                            $(element).collapse('hide');
                    });

                });

            };

            $scope.parseUserPreferences = function (userPreference) {
                if (userPreference) {
                    if (typeof (userPreference) === "string") {
                        //userPreference is a stringified object ready to be parsed
                        userPreference = angular.fromJson(userPreference);
                    } 
                   if (angular.isUndefined(userPreference.showdefaultpasswordprompt)) {
                        //userPreference has been set elsewhere but has no setting for showdefaultpasswordprompt, so we set it
                       userPreference.showdefaultpasswordprompt = true;
                    }
                } else {
                    //userPreference is empty, so we set it up
                    userPreference = { showdefaultpasswordprompt: true };
                }
                return userPreference;
            }

            $scope.initialize();

        }
    ])


;
