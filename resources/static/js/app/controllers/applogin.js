angular.module('app-login')
    .controller('AppLoginController',
    [
        '$window', '$http', '$scope', '$log', '$resource', '$translate', 'localStorageService', 'Commands', '$cookies',
        function ($window, $http, $scope, $log, $resource, $translate, localStorage, Commands, $cookies) {

            $scope.verifyingCredentials = false;
            $scope.failedLogin = false;
            $scope.message = '';
            $scope.shouldNextButtonBeEnabled = false;

            var rememberUser = function (userdata) {
                if (userdata && $scope.data.settings.rememberMe) {
                    var currentUser = {
                        username: userdata.username,
                        domain: userdata.domain
                    };

                    localStorage.set('ASM.currentUser', currentUser);
                } else {
                    localStorage.remove('ASM.currentUser');
                }
            };

            $scope.data = {
                loginalert: true,
                settings: {
                    username: '',
                    rememberMe: false,
                    applicationName: $translate.instant('ApplicationTitle'),
                    //applicationLogo: 'images/icee_icon.svg',
                    //applicationLogo: 'css/app/images/logos/dellemc-logo-196x32.png',
                    //appIcon: 'ci-logo-asm',
                    applicationLogo: 'images/AppIcon-VxFlexManager-white-32.svg',

                    copyright:  $translate.instant('DELL_COPYRIGHT'),
                    //UPDATE THIS FOR China build...
                    //copyrightIcon: 'ci-logo-dell-halo-o',
                    copyrightIcon: 'logo-dellemc',

                    messageIcon: '',
                    messageIconColor: '',
                    messageText: 'Powered by Dell EMC',
                    signInLabel: $translate.instant('GENERIC_Login'),
                    showRememberMe: true,
                    rememberMeText: $translate.instant('GENERIC_RememberMe')
                }
            };

            $scope.displayError = function (status, message) {

                delete $window.sessionStorage.token;

                var error = $translate.instant('GENERIC_LoginFailed');

                if (status === 404) {
                    error = $translate.instant('GENERIC_NoConnection');
                }

                if (status === 200 && message) {
                    error = message;
                }

                $scope.data.settings.isSpinning = false;
                $scope.failedLogin = true;
                $scope.data.settings.showMessage = true;
                $scope.data.settings.messageText = error;

            };

            $scope.actions = {
                clickLogin: function (userdata) {

                    $scope.data.settings.isSpinning = true;
                    $scope.data.settings.showMessage = false;

                    $scope.verifyingCredentials = true;

                    localStorage.clearAll();

                    $http.post(Commands.data.session.doLogin, { Username: userdata.username, Password: userdata.password })
                        .success(function (data, status, headers, config) {

                            var response = data.responseObj;

                            if (!response.success) {
                                var errorMessage = data.errorObj && data.errorObj.errorMessage ? data.errorObj.errorMessage : null;
                                $scope.displayError(status, errorMessage);
                                return;
                            }
                            $scope.actions.setAlertHidden("false");
                            $scope.actions.setStorageAlertHidden("false");
                            $scope.actions.setVirtualApplianceVersionAlertHidden("false");
                            rememberUser(userdata); //Only remember if successful
                            $scope.verifyingCredentials = false;

                            $window.sessionStorage.token = headers('JSESSIONID');
                            $window.sessionStorage.currentUser = userdata.username;

                            $scope.verifyingCredentials = false;

                            $cookies.put('username', userdata.username);

                            var url = response.url;

                            if (response.route.length > 0) {
                                url += '#' + response.route;
                                url = url.replace('##', '#');
                            }
                            window.location = url;


                        })
                        .error(function (data, status) {

                            $scope.displayError(status, null);

                        });

                },
                setStorageAlertHidden: function (value) {
                    sessionStorage.setItem("storageAlertHidden", value);
                },
                setVirtualApplianceVersionAlertHidden: function (value) {
                    sessionStorage.setItem("virtualApplianceVersionBannerHidden", value);
                },
                setAlertHidden: function (value) {
                    sessionStorage.setItem("rcmAlertHidden", value);
                }
            };

            $scope.initialize = function () {
                var currentUser = localStorage.get('ASM.currentUser');
                if (currentUser != undefined) {
                    $scope.data.settings.username = currentUser.username;
                    $scope.data.settings.rememberMe = true;
                }

                localStorage.remove('ASM.currentUserObject');
                //sessionStorage.removeItem('ASM.currentUserObject');

                ////dummy call to force certificate acceptance (can be replaced with get version eventually)
                //$http.get(Commands.users)
                //    .success(function(data, status, headers, config) {
                //    })
                //    .error(function(data, status, headers, config) {
                //    });


            };
            $scope.initialize();
        }
    ])


;
