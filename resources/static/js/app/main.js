angular.module('app', ['ngNewRouter', 'ASM.constants', 'ASM.directives', 'ASM.dataservices', 'LocalStorageModule', 'Enums', 'Clarity'])
    //Route Configuration now in AppController definition.
    .config(['$componentLoaderProvider', function ($componentLoaderProvider) {
        $componentLoaderProvider.setTemplateMapping(function (name) {
            // name is component name.  We can replace them all with Template Cache here.
            return 'views/' + name + '.html';
        });
    }])
    .run(['$router', function ($router) {
        $router.config([
            { name: 'ASM', path: '/', redirectTo: '/home', data: { pageTemplate: 'gray' } }, //, pageTemplate: 'gray'
            { name: 'Home', path: '/home', component: 'home', useAsDefault: true, data: { pageTemplate: 'gray' } },
            //{ name: 'Logs', path: '/logs', component: 'logs' },
            { name: 'Devices', path: '/devices', component: 'devices', data: { pageTemplate: 'gray' } },
            { name: 'DevicesAndServerPools', path: '/devices/:resourceType', component: 'devices' },
            { name: 'DevicesByTypeAndHealth', path: '/devices/:resourceType/:health', component: 'devices' },
            { name: 'DeviceDetails', path: '/device/:id/:resourceType', component: 'device' },
            { name: 'GettingStarted', path: '/gettingstarted', component: 'gettingstarted' },
            { name: 'GettingStarted', path: '/gettingstarted/:foo', component: 'gettingstarted' },
            { name: 'Settings', path: '/settings/:settingType/:fullscreen', component: ('settingslist') },
            { name: 'Settings', path: '/settings/:settingType/:fullscreen/:modalOrTab', component: ('settingslist') },
            { name: 'Settings', path: '/settings/:settingType/', component: ('settingslist') },
            { name: 'Settings', path: '/settings', component: ('settingslist') },
            { name: 'Templates', path: '/templates', component: ('templates') },
            { name: 'TemplatesByCategory', path: '/templates/:category', component: ('templates') },
            { name: 'TemplateBuilder', path: '/templatebuilder/:id/:mode', component: 'templatebuilder' },
            { name: 'ServiceDetails', path: '/service/:id/:firmwarereport', component: ('service') },
            { name: 'ServicesByHealth', path: '/services/:health', component: ('services') },
            { name: 'Services', path: '/services', component: ('services') },
            { name: 'Search', path: '/Search', component: ('searchResults') }
        ]);

    }])
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
        'localStorageServiceProvider', function (localStorageServiceProvider) {
            localStorageServiceProvider.prefix = 'ASM';
            localStorageServiceProvider.setStorageType('sessionStorage');
        }
    ])
    .config([
        '$httpProvider', function ($httpProvider) {

            if (!$httpProvider.defaults.headers.get) {
                $httpProvider.defaults.headers.common = {};
            }
            $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache';
            $httpProvider.defaults.headers.common.Pragma = 'no-cache';

            $httpProvider.interceptors.push([
                '$q', '$window', '$translate', function ($q, $window, $translate) {
                    return {
                        'request': function (config) {


                            //prevent caching of html files that are not in template cache
                            if (config.method === 'GET' && config.url.indexOf('.html') > -1 && config.cache.get(config.url) === undefined) {
                                config.url += ((config.url.indexOf('?') > -1) ? '&' : '?')
                                    + config.paramSerializer({ v: $window['cacheKey'] || '' });
                            }

                            if ($window.sessionStorage.token) {

                                //Configure defaults for global variables that don't change... use interceptors to change headers on a call by call basis
                                //$httpProvider.defaults.headers.common['JSESSIONID'] = $window.sessionStorage.token;
                                config.headers['JSESSIONID'] = $window.sessionStorage.token;
                            }

                            //if (!config.data) return config;
                            if (config.method === 'GET' || config.directPost) return config;

                            config.data = config.data || { requestObj: null, criteriaObj: null };

                            var __buildJobRequest = function (requestObj, criteriaObj) {
                                var jobRequest;

                                if (requestObj) {
                                    jobRequest = { requestObj: requestObj };
                                } else {
                                    jobRequest = { requestObj: null };
                                }

                                if (criteriaObj)
                                    jobRequest.criteriaObj = criteriaObj;

                                return jobRequest;
                            };

                            var requestObjData;

                            //if we send in explicit object to post use that, otherwise use the associated model                
                            if (config.data && config.data.requestObj) {
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

                            //replace xhr response data for error testing
                            //if (response.config.url === "devices/getscaleiobyid") {
                            //    response.data =
                            //        { "criteriaObj": null, "responseCode": 0, "errorObj": null, "requestObj": null, "responseObj": { "id": "scaleio-10.239.139.190", "systemId": "2e83a313221c6f03", "name": "flex-os-gateway-2", "ipaddressurl": "https://10.239.139.190", "scaleIOInformation": { "protectedInKb": 13505658880, "inMaintenanceInKb": 0, "degradedInKb": 0, "failedInKb": 0, "unusedInKb": 40567895040, "spareInKb": 27856071680, "decreasedInKb": 0, "unavailableUnusedInKb": 0, "maxCapacityInKb": 81929625600 }, "scaleIOProtectionDomains": [{ "id": "4921f70f00000000", "protectionDomainName": "fpr2-cluster-service-2.0-PD-1", "storageVolumes": 3, "storageSize": "76.0 TB", "mappedSDCs": 3, "scaleIOServerTypes": [{ "id": "sds", "name": "SDS", "scaleIOServerDetails": [{ "id": "dab4fe8d00000002", "name": "Sds-10.239.139.110", "connected": "Connected", "ipAddresses": ["192.168.161.19", "192.168.162.19"] }, { "id": "dab4fe8c00000001", "name": "Sds-10.239.139.108", "connected": "Connected", "ipAddresses": ["192.168.161.17", "192.168.162.17"] }, { "id": "dab4fe8b00000000", "name": "Sds-10.239.139.109", "connected": "Connected", "ipAddresses": ["192.168.161.18", "192.168.162.18"] }] }, { "id": "sdc", "name": "SDC", "scaleIOServerDetails": [{ "id": "fb32450500000000", "name": "192.168.161.15", "connected": "Connected", "ipAddresses": ["192.168.161.15"] }, { "id": "fb32450700000002", "name": "192.168.161.13", "connected": "Connected", "ipAddresses": ["192.168.161.13"] }, { "id": "fb32450600000001", "name": "192.168.162.14", "connected": "Connected", "ipAddresses": ["192.168.162.14"] }] }], "scaleIOStoragePools": [{ "id": "0366b83900000000", "name": "2-SP-SSD-1", "scaleIOStorageVolumes": [{ "id": "68be565300000000", "name": "2-DS-SSD-1", "size": "184.0 GB", "type": "ThickProvisioned", "mappedSDCs": 3 }, { "id": "68be565400000001", "name": "2-DS-SSD-3", "size": "184.0 GB", "type": "ThickProvisioned", "mappedSDCs": 3 }] }, { "id": "0366b83a00000001", "name": "2-SP-HDD-1", "scaleIOStorageVolumes": [{ "id": "68be565500000002", "name": "2-DS-HDD-2", "size": "5.0 TB", "type": "ThickProvisioned", "mappedSDCs": 3 }] }] }], "protectionDomainCount": 1, "volumeCount": 3, "sDCCount": 3, "sDSCount": 3, "management": "Clustered", "ioData": { "total": { "id": "iops", "category": "iops", "currentvalue": 2.11, "currentvaluelabel": "Value", "peakvalue": 13.16, "peaktime": "2018-10-22T00:00:00.000Z", "starttime": "2018-10-01T00:00:00.000Z", "threshold": 13.818000000000001, "historicaldata": null }, "read": { "id": "iops", "category": "iops", "currentvalue": 0.91, "currentvaluelabel": "Value", "peakvalue": 6.61, "peaktime": "2018-10-22T00:00:00.000Z", "starttime": "2018-10-01T00:00:00.000Z", "threshold": 6.940500000000001, "historicaldata": null }, "write": { "id": "iops", "category": "iops", "currentvalue": 1.2, "currentvaluelabel": "Value", "peakvalue": 6.55, "peaktime": "2018-10-22T00:00:00.000Z", "starttime": "2018-10-01T00:00:00.000Z", "threshold": 6.8775, "historicaldata": null } }, "bandwidthData": { "total": { "id": "bw", "category": "bw", "currentvalue": 6.05, "currentvaluelabel": "Value", "peakvalue": 39.13, "peaktime": "2018-10-22T00:00:00.000Z", "starttime": "2018-10-01T00:00:00.000Z", "threshold": 41.0865, "historicaldata": null }, "read": { "id": "bw", "category": "bw", "currentvalue": 5.4, "currentvaluelabel": "Value", "peakvalue": 35.51, "peaktime": "2018-10-22T00:00:00.000Z", "starttime": "2018-10-01T00:00:00.000Z", "threshold": 37.2855, "historicaldata": null }, "write": { "id": "bw", "category": "bw", "currentvalue": 1.2, "currentvaluelabel": "Value", "peakvalue": 38.77, "peaktime": "2018-10-22T00:00:00.000Z", "starttime": "2018-10-01T00:00:00.000Z", "threshold": 40.8123, "historicaldata": null } } } }
                            //        ;
                            //}

                            if (response && response.data['responseCode'] != null && response.data['responseCode'] !== 0) {

                                var e = {
                                    error: {
                                        refId: '',
                                        code: response.data['responseCode'],
                                        severity: 'CRITICAL',
                                        message: $translate.instant('GENERIC_UnknownError'),
                                        details: '',
                                        errors: []
                                    }
                                };

                                if (response.data.errorObj) {

                                    if (response.data.errorObj.errorMessage) {
                                        e.error.message = response.data.errorObj.errorMessage;
                                    }

                                    if (response.data.errorObj.errorAction) {
                                        e.error.details = response.data.errorObj.errorAction;
                                        if (e.error.details) { e.error.details += '<br />'; }
                                    }

                                    if (response.data.errorObj.errorDetails) {
                                        e.error.details = response.data.errorObj.errorDetails;
                                    }

                                    if (response.data.errorObj.errorCode) {
                                        e.error.code = response.data.errorObj.errorCode;
                                    }

                                    //copy fldErrors to error.errors collection for errorDisplay directive
                                    e.error.fldErrors = response.data.errorObj.fldErrors || undefined;
                                    if (e.error.fldErrors) {
                                        angular.forEach(e.error.fldErrors, function (suberror) {
                                            if (suberror.errorAction) {
                                                if (suberror.errorDetails) {
                                                    suberror.errorAction += ('<br />' + suberror.errorDetails);
                                                }
                                            }
                                            var fldError = {
                                                message: suberror.errorMessage || null,
                                                details: suberror.errorAction || suberror.errorDetails || null,
                                                code: suberror.errorCode || null,
                                                severity: suberror.errorSeverity || 'CRITICAL'
                                            };

                                            if (!e.error.errors)
                                                e.error.errors = [];

                                            e.error.errors.push(fldError);
                                        });
                                    }

                                    e.error.Message = '' + e.error.code + ': ' + e.error.message;
                                    e.error.refId = '' + response.config.url;
                                    response.data = e.error;
                                }

                                return $q.reject(response);
                            }

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
        '$rootScope', '$location', 'localStorageService', '$translate', function ($rootScope, $location, localStorageService, $translate) {

            var updatePageTemplate = function () {
                var pageTemplate = $location.url().indexOf('gettingstarted') > 0 ? 'gray' : '';

                if ($location.url().indexOf('device') > 0 && $location.url().indexOf('devices') < 0) {
                    pageTemplate = 'gray';
                }

                if ($location.url().indexOf('home') > 0) {
                    pageTemplate = 'gray';
                }
                $rootScope.pageTemplate = pageTemplate;

                if ($location.absUrl().indexOf('#/gettingstarted') !== -1) { $rootScope.helpToken = 'gettingstarted'; $rootScope.pageTitle = $translate.instant('GETTINGSTARTED_instructiontitle'); }
                if ($location.absUrl().indexOf('#/home') !== -1) { $rootScope.helpToken = 'dashboardoverview'; $rootScope.pageTitle = $translate.instant('DASHBOARD_Title'); }
                if ($location.absUrl().indexOf('#/service') !== -1) { $rootScope.helpToken = 'servicedetails'; $rootScope.pageTitle = $translate.instant('SERVICE_DETAIL_Details'); }
                if ($location.absUrl().indexOf('#/services') !== -1) { $rootScope.helpToken = 'services'; $rootScope.pageTitle = $translate.instant('SERVICE_DETAIL_Services'); }
                if ($location.absUrl().indexOf('#/templates') !== -1) { $rootScope.helpToken = 'templateshomepage'; $rootScope.pageTitle = $translate.instant('TEMPLATES_Templates'); }
                if ($location.absUrl().indexOf('#/templates/mytemplates') !== -1) { $rootScope.helpToken = 'templateshomepage'; $rootScope.pageTitle = $translate.instant('TEMPLATES_Templates'); }
                if ($location.absUrl().indexOf('#/templates/sampletemplates') !== -1) { $rootScope.helpToken = 'sampletemplates'; $rootScope.pageTitle = $translate.instant('TEMPLATES_Templates'); }
                if ($location.absUrl().indexOf('#/templatebuilder') !== -1) { $rootScope.helpToken = 'templateshomepage'; $rootScope.pageTitle = $translate.instant('TEMPLATES_TemplateBuilder'); }
                if ($location.absUrl().indexOf('#/device') !== -1) { $rootScope.helpToken = 'resourcedetails'; $rootScope.pageTitle = $translate.instant('DEVICEDETAILS_Title'); }
                if ($location.absUrl().indexOf('#/devices') !== -1) { $rootScope.helpToken = 'resources'; $rootScope.pageTitle = $translate.instant('DEVICES_Resources'); }
                if ($location.absUrl().indexOf('#/devices/serverpools') !== -1) { $rootScope.helpToken = 'serverpools'; $rootScope.pageTitle = $translate.instant('DEVICES_Resources'); }
                if ($location.absUrl().indexOf('#/settings') !== -1) { $rootScope.helpToken = 'Settings'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }

                //if ($location.absUrl().indexOf('#/settings/AddOnModule') !== -1) { $rootScope.helpToken = 'addonmodules'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                if ($location.absUrl().indexOf('#/settings/BackupAndRestore') !== -1) { $rootScope.helpToken = 'backupandrestore'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                if ($location.absUrl().indexOf('#/settings/CredentialsManagement') !== -1) { $rootScope.helpToken = 'credentialslandingpage'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                if ($location.absUrl().indexOf('#/settings/GettingStarted') !== -1) { $rootScope.helpToken = 'gettingstarted'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                if ($location.absUrl().indexOf('#/settings/Jobs') !== -1) { $rootScope.helpToken = 'scheduledjobs'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                if ($location.absUrl().indexOf('#/settings/Logs') !== -1) { $rootScope.helpToken = 'Logs'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                if ($location.absUrl().indexOf('#/settings/Networks') !== -1) { $rootScope.helpToken = 'networkslandingpage'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                if ($location.absUrl().indexOf('#/settings/Repositories') !== -1) { $rootScope.helpToken = 'repositorieshomepage'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                if ($location.absUrl().indexOf('#/settings/InitialApplianceSetup') !== -1) { $rootScope.helpToken = 'initialsetupwelcome'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                if ($location.absUrl().indexOf('#/settings/Users') !== -1) { $rootScope.helpToken = 'SettingsUsersHomePage'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                if ($location.absUrl().indexOf('#/settings/VirtualApplianceManagement') !== -1) { $rootScope.helpToken = 'appliancemanagementhomepage'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }
                //if ($location.absUrl().indexOf('#/settings/VirtualIdentityPools') !== -1) { $rootScope.helpToken = 'virtualidentitypoolslandingpage'; $rootScope.pageTitle = $translate.instant('SETTINGS_Settings'); }

                //fullscreen settings pages
                //if ($location.absUrl().indexOf('#/settings/AddOnModule/true') !== -1) { $rootScope.helpToken = 'addonmodules'; $rootScope.pageTitle = $translate.instant('SETTINGS_AddOnModule'); }
                if ($location.absUrl().indexOf('#/settings/BackupAndRestore/true') !== -1) { $rootScope.helpToken = 'backupandrestore'; $rootScope.pageTitle = $translate.instant('SETTINGS_BackupRestore'); }
                if ($location.absUrl().indexOf('#/settings/CredentialsManagement/true') !== -1) { $rootScope.helpToken = 'credentialslandingpage'; $rootScope.pageTitle = $translate.instant('SETTINGS_CredentialsManagement'); }
                if ($location.absUrl().indexOf('#/settings/GettingStarted/true') !== -1) { $rootScope.helpToken = 'gettingstarted'; $rootScope.pageTitle = $translate.instant('SETTINGS_GettingStarted'); }
                if ($location.absUrl().indexOf('#/settings/Jobs/true') !== -1) { $rootScope.helpToken = 'scheduledjobs'; $rootScope.pageTitle = $translate.instant('SETTINGS_Jobs'); }
                if ($location.absUrl().indexOf('#/settings/Logs/true') !== -1) { $rootScope.helpToken = 'Logs'; $rootScope.pageTitle = $translate.instant('SETTINGS_Logs'); }
                if ($location.absUrl().indexOf('#/settings/Networks/true') !== -1) { $rootScope.helpToken = 'networkslandingpage'; $rootScope.pageTitle = $translate.instant('SETTINGS_Networks'); }
                if ($location.absUrl().indexOf('#/settings/Repositories/true') !== -1) { $rootScope.helpToken = 'repositorieshomepage'; $rootScope.pageTitle = $translate.instant('SETTINGS_Repositories'); }
                if ($location.absUrl().indexOf('#/settings/InitialApplianceSetup/true') !== -1) { $rootScope.helpToken = 'initialsetupwelcome'; $rootScope.pageTitle = $translate.instant('SETTINGS_InitialApplianceSetup'); }
                if ($location.absUrl().indexOf('#/settings/Users/true') !== -1) { $rootScope.helpToken = 'SettingsUsersHomePage'; $rootScope.pageTitle = $translate.instant('SETTINGS_Users'); }
                if ($location.absUrl().indexOf('#/settings/VirtualApplianceManagement/true') !== -1) { $rootScope.helpToken = 'appliancemanagementhomepage'; $rootScope.pageTitle = $translate.instant('SETTINGS_VirtualApplianceManagement'); }
                //if ($location.absUrl().indexOf('#/settings/VirtualIdentityPools/true') !== -1) { $rootScope.helpToken = 'virtualidentitypoolslandingpage'; $rootScope.pageTitle = $translate.instant('SETTINGS_VirtualIdPools'); }

                //console.log('$rootScope.helpToken:  ' + $rootScope.helpToken);
                if ($rootScope.pageTitle) {
                    $rootScope.pageTitle = $rootScope.pageTitle + ' - ' + $translate.instant('ApplicationTitle');
                    $rootScope.backgroundPageTitle = angular.copy($rootScope.pageTitle);
                };

            }

            $rootScope.$on('modalClose', function (event, args) {
                if (args) {
                    $rootScope.pageTitle = $rootScope.backgroundPageTitle;
                }
            });

            $rootScope.$on('modalOpen', function (event, args) {
                if (args) {
                    $rootScope.pageTitle = args + ' - ' + $translate.instant('ApplicationTitle');
                }
            });

            $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
                if (previous && current.originalPath !== previous.originalPath) {
                    localStorageService.set('tabstate', {});
                }
                updatePageTemplate();

                $('.tooltip').remove();

            });

            $rootScope.$on('$locationChangeSuccess', function () {

                updatePageTemplate();

                $('.tooltip').remove();

            });

        }
    ])
    //Allow you to include HTML in your translations (Add ng-bind-html="'TOKEN'|translate|htmlSafe" to your element).
    .filter('htmlSafe', ['$sce', function ($sce) { return function (htmlCode) { return $sce.trustAsHtml(htmlCode); }; }])
;

