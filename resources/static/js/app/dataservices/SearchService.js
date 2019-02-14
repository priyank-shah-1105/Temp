angular.module('ASM.dataservices')
    .service('SearchService', ['$http', '$q', '$filter', 'Commands', 'GlobalServices', function ($http, $q, $filter, Commands, GlobalServices) {
        this.search = function (term, limit, skip) {

            if (!limit) limit = 0;

            //skip = true;
            if (skip && skip === true) {
                //console.log('short-circuited the actual search in SearchService');
                var promise = $q.all().then(function () {
                    return {
                        "bogus-data": true,
                        "searchedTerm": "template",
                        "totalResults": 70,
                        "totalUnlimitedResults": 70
                    };
                });
                return promise;
            }

            //console.log('all searches started');

            var unlimitedCount_devices;
            var unlimitedCount_services;
            var unlimitedCount_templates;

            var request = {
                requestObj: null,
                criteriaObj: null
            }

            var devicePromise, servicePromise, templatePromise;

            if (GlobalServices.cache.devices.length > 0) {
                devicePromise = $q.resolve({ data: { responseObj: GlobalServices.cache.devices } });
                //console.log('retrieving DEVICES data from cache');
            }
            else {
                devicePromise = $http.post(Commands.data.devices.getDeviceList, request);
            }

            if (GlobalServices.cache.services.length > 0) {
                servicePromise = $q.resolve({ data: { responseObj: GlobalServices.cache.services } });
                //console.log('retrieving SERVICES data from cache');
            }
            else {
                servicePromise = $http.post(Commands.data.services.getServiceList, request);
            }

            if (GlobalServices.cache.templates.length > 0) {
                templatePromise = $q.resolve({ data: { responseObj: GlobalServices.cache.templates } });
                //console.log('retrieving TEMPLATES data from cache');
            }
            else {
                templatePromise = $http.post(Commands.data.templates.getTemplateList, request);
            }

            //clear cache every 5 minutes
            setTimeout(GlobalServices.resetCache, 60000 * 5);

            var searches = [devicePromise, servicePromise, templatePromise];

            //console.log('searches data before q:  ' + JSON.stringify(searches));

            var promise = $q.all(searches)
                //load data
                .then(
                    function (data) {

                        //console.log('all searches returned');
                        //console.log('searches data after q:  ' + JSON.stringify(searches));

                        var devices = data[0].data.responseObj;
                        var services = data[1].data.responseObj;
                        var templates = data[2].data.responseObj;

                        if (GlobalServices.cache.devices.length === 0) { GlobalServices.cache.devices = devices; }
                        if (GlobalServices.cache.services.length === 0) { GlobalServices.cache.services = services; }
                        if (GlobalServices.cache.templates.length === 0) { GlobalServices.cache.templates = templates; }

                        var matchDevices = $filter('filter')(devices, term);
                        var matchServices = $filter('filter')(services, term);
                        var matchTemplates = $filter('filter')(templates, term);

                        unlimitedCount_devices = matchDevices.length;
                        unlimitedCount_services = matchServices.length;
                        unlimitedCount_templates = matchTemplates.length;

                        if (limit > 0) {
                            //note:  this overrides the matched filter above
                            matchDevices = $filter('limitTo')(matchDevices, limit);
                            matchServices = $filter('limitTo')(matchServices, limit);
                            matchTemplates = $filter('limitTo')(matchTemplates, limit);
                        }

                        //note:  if filtering is slow, should we store the filtered results in the cache and return them both?
                        //console.log('all filtering done');

                        return { devices: matchDevices, services: matchServices, templates: matchTemplates };

                    },
                    function (data) {

                        return { devices: [], services: [], templates: [] };

                    }
                )
                .then(function (matches) {

                    //console.log('matches:  ' + JSON.stringify(matches));
                    //console.log('building results');

                    var deviceResults = [];
                    var serviceResults = [];
                    var templatesResults = [];

                    angular.forEach(matches.devices, function (d) {
                        deviceResults.push({
                            id: d.id,
                            status: d.health,
                            name: d.name || d.serviceTag || d.ipAddress || d.deviceid,
                            description: 'Resource Sub-category:  ' + d.deviceType + ', OS Hostname:  ' + d.hostname + ', Management IP:  ' + d.ipAddress + ', Model:  ' + d.manufacturer + ' ' + d.model,
                            deviceType: d.deviceType,
                            osHostname: d.hostname,
                            managementIp: d.ipAddress,
                            model: d.manufacturer + ' ' + d.model,
                            target: '#/device/' + d.id + '/' + d.deviceType,
                            relevance: null,
                            subcategory: d.deviceType
                        });
                    });

                    angular.forEach(matches.services, function (d) {
                        serviceResults.push({
                            id: d.id,
                            status: d.health,
                            name: d.name,
                            //description: d.description,
                            description: 'Deployed By:  ' + d.deployedBy + ', Deployed On:  ' + moment(d.deployedOn).format('LLL'),
                            deployedBy: d.deployedBy,
                            deployedOn: d.deployedOn,
                            target: '#/service/' + d.id + '/details',
                            relevance: null,
                            subcategory: null
                        });
                    });

                    angular.forEach(matches.templates, function (d) {
                        templatesResults.push({
                            id: d.id,
                            status: d.isTemplateValid,
                            name: d.name,
                            //description: d.description,
                            description: 'State:  ' + (d.draft == true ? 'Draft' : 'Published') + ', Category:  ' + d.category + ', Last Deployed On:  ' + moment(d.lastDeployed).format('LLL'),
                            state: d.draft,
                            category: d.category,
                            lastDeployed: d.lastDeployed,
                            target: '#/templatebuilder/' + d.id + '/view',
                            relevance: null,
                            subcategory: null
                        });
                    });

                    var results = {
                        searchedTerm: term,
                        totalResults: matches.devices.length + matches.services.length + matches.templates.length,
                        totalUnlimitedResults:  unlimitedCount_devices + unlimitedCount_services + unlimitedCount_templates,
                        categories: [
                            { name: 'Resources', icon: 'ci-device-tower', items: deviceResults, totalItems: deviceResults.length, unlimitedItems:  unlimitedCount_devices },
                            { name: 'Services', icon: 'ci-deploy', items: serviceResults, totalItems: serviceResults.length, unlimitedItems: unlimitedCount_services },
                            { name: 'Templates', icon: 'ci-device-templates-blank-stacked', items: templatesResults, totalItems: templatesResults.length, unlimitedItems: unlimitedCount_templates }
                        ]
                    };

                    //console.log('done building results');

                    return results;

                });

            return promise;

        };
    }]);
