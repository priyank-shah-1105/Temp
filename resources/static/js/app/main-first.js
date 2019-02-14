angular.module('app-first', ['ASM.constants', 'ASM.directives', 'ASM.dataservices', 'LocalStorageModule', 'Enums', 'Clarity'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/welcome', { templateUrl: 'views/first/firstrun_welcome.html', controller: 'FirstRunWelcomeController', pageTemplate: 'welcome' }).
            when('/configure', { templateUrl: 'views/first/firstrun_configurationsettings.html', controller: '' }).
            when('/fabric', { templateUrl: 'views/first/firstrun_fabricsetup.html', controller: 'FirstRunFabricSetupController' }).
            otherwise({ templateUrl: 'views/first/firstrun_welcome.html', controller: 'FirstRunWelcomeController', pageTemplate: 'welcome' });
    }]).config(['localStorageServiceProvider', function (localStorageServiceProvider) {
        localStorageServiceProvider.prefix = 'ASM';
    }])
    .config(['$translateProvider', 'CoreTranslations', 'AppTranslations', function ($translateProvider, CoreTranslations, AppTranslations) {
        var translations = {};
        angular.extend(translations, CoreTranslations.en);
        angular.extend(translations, AppTranslations.en);
        $translateProvider.translations('en', translations);
        $translateProvider.preferredLanguage('en');
        $translateProvider.useSanitizeValueStrategy('sanitizeParameters');
    }])
    .config([
        '$httpProvider', function ($httpProvider) {

            if (!$httpProvider.defaults.headers.get) {
                $httpProvider.defaults.headers.common = {};
            }
            $httpProvider.defaults.headers.common["Cache-Control"] = "no-cache";
            $httpProvider.defaults.headers.common.Pragma = "no-cache";
        }
    ])
    .run(['$rootScope', '$route', function ($rootScope, $route) {

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

    }]);
