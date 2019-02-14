angular.module('app-login', ['ASM.constants', 'ASM.directives', 'ASM.dataservices', 'LocalStorageModule', 'Enums', 'Clarity'])
    .config([
        '$routeProvider', function ($routeProvider) {
            $routeProvider.
                otherwise({ templateUrl: '', controller: '' });
        }
    ]).config([
        'localStorageServiceProvider', function (localStorageServiceProvider) {
            localStorageServiceProvider.prefix = 'ASM';
        }
    ])
    .config([
        '$translateProvider', 'CoreTranslations', 'AppTranslations', function ($translateProvider, CoreTranslations, AppTranslations) {
            var translations = {};
            angular.extend(translations, CoreTranslations.en);
            angular.extend(translations, AppTranslations.en);
            $translateProvider.translations('en', translations);
            $translateProvider.preferredLanguage('en');
            $translateProvider.useSanitizeValueStrategy('sanitizeParameters');
        }
    ])
   .config([
        '$httpProvider', function ($httpProvider) {

            if (!$httpProvider.defaults.headers.get) {
                $httpProvider.defaults.headers.common = {};
            }
            $httpProvider.defaults.headers.common["Cache-Control"] = "no-cache";
            $httpProvider.defaults.headers.common.Pragma = "no-cache";

            $httpProvider.interceptors.push([
                '$q', '$window', '$translate', function ($q, $window, $translate) {
                    return {
                        'request': function (config) {

                            if ($window.sessionStorage.token) {

                                //Configure defaults for global variables that don't change... use interceptors to change headers on a call by call basis
                                //$httpProvider.defaults.headers.common['JSESSIONID'] = $window.sessionStorage.token;
                                config.headers['JSESSIONID'] = $window.sessionStorage.token;
                            }

                            if (!config.data) return config;

                            var __appendRequestOptions = function (jobRequest) {
                                /// <summary>This is a private DellClarity API function.  DO NOT USE IT IN YOUR APPLICATION!</summary>
                                //$(DellClarity.Ajax.requestOptions).each(function(index, item) {
                                //    jobRequest[item.name] = item.value;
                                //});
                            };

                            var __buildJobRequest = function (requestObj, criteriaObj) {
                                var jobRequest;

                                if (requestObj) {
                                    jobRequest = { requestObj: requestObj };
                                } else {
                                    jobRequest = { requestObj: null };
                                }

                                if (criteriaObj)
                                    jobRequest.criteriaObj = criteriaObj;

                                __appendRequestOptions(jobRequest);

                                return jobRequest;
                            };


                            var data;
                            var requestObjData;

                            //if we send in explicit object to post use that, otherwise use the associated model                
                            if (config.data.requestObj) {
                                requestObjData = config.data.requestObj;
                            } else {
                                requestObjData = config.data;
                            }


                            var jobRequestObj = __buildJobRequest(requestObjData, config.data.criteriaObj);

                            config.data = JSON.stringify(jobRequestObj, function (key, value) {

                                if (config.options && config.options.customstringify) {
                                    var newValue = config.options.customstringify(this, key, value);
                                    if (newValue !== false)
                                        return newValue;
                                }

                                if (typeof value === 'function') {
                                    return value.toString();
                                } else {
                                    return value;
                                }

                            });

                            return config;
                        },
                        'response': function (response) {

                            if (response && (response.status === 403 || response.status === 401)) {
                                $window.location.href = 'login.html';
                                return null;
                            }

                            //if (response && response.data['return-code'] != null && response.data['return-code'] != 0) {
                            //    response.config.response = response.data;
                            //    return $q.reject(response.data['return-msg']);
                            //}

                            window.setTimeout(function () {
                                $(window).trigger('resize');
                            }, 500);


                            return response;
                        },
                        'responseError': function (rejection) {


                            if (rejection && (rejection.status === 403 || rejection.status === 401)) {
                                $window.location.href = 'login.html';
                            }

                            //convert all ajax errors to common error format
                            var e = {
                                error: {
                                    refId: '',
                                    code: '',
                                    severity: 'CRITICAL',
                                    message: $translate.instant('GENERIC_UnknownError'),
                                    details: ''
                                }
                            };

                            if (rejection) {

                                //if no EEMI error returned
                                if (rejection.status)
                                    e.error.code = rejection.status;
                                if (rejection.statusText)
                                    e.error.message = rejection.statusText;

                                if (rejection.data && rejection.data.ExceptionMessage) {
                                    e.error.message = rejection.data.ExceptionMessage;
                                }

                                if (rejection.data && rejection.data.Message) {
                                    e.error.message = rejection.data.Message;
                                }

                                if (rejection.data && rejection.data.MessageDetail) {
                                    e.error.details = rejection.data.MessageDetail;
                                }

                                if (rejection.data && rejection.data.error) {
                                    e.error = rejection.data.error;
                                }

                                e.error.Message = '' + e.error.code + ': ' + e.error.message;
                                e.error.refId = '' + rejection.config.url;
                                rejection.data = e.error;
                            }


                            return $q.reject(rejection);
                        }
                    };
                }
            ]);
        }
   ])
    .run([
        '$rootScope', '$route', function ($rootScope, $route) {

            var updatePageTemplate = function () {
                var pageTemplate = $route.current ? $route.current.pageTemplate || '' : '';
                $rootScope.pageTemplate = pageTemplate;
            }

            $rootScope.$on('$routeChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                updatePageTemplate();
            });

            $rootScope.$on('$locationChangeSuccess', function () {
                updatePageTemplate();
            });

        }
    ])




;
