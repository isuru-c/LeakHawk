(function () {
    'use strict';

    angular.module('leakhawk', [
        'ui.router', 'ngCookies','flow','chart.js'
    ]).config(['$stateProvider', '$urlRouterProvider', '$httpProvider',
        function ($stateProvider, $urlRouterProvider, $httpProvider) {

            $stateProvider.state('default', {
                url: '',
                views: {
                    "body@": {
                        templateUrl: 'leakhawk/components/core/body.html',
                        controller: 'BodyController',
                        controllerAs: 'vm'
                    }
                }
            }).state('home', {
                parent: 'default',
                url: '/home',
                views: {
                    "view@default": {
                        templateUrl: 'leakhawk/components/pages/home/new_home.html',
                        controller: 'HomeController',
                        controllerAs: 'vm'
                    }
                }
            }).state('analysis', {
                parent: 'default',
                url: '/analysis',
                views: {
                    "view@default": {
                        templateUrl: 'leakhawk/components/pages/analysis/analysis.html',
                        controller: 'AnalysisController',
                        controllerAs: 'vm'
                    }
                }
            }).state('incident', {
                parent: 'default',
                url: '/incident/:id',
                views: {
                    "view@default": {
                        templateUrl: 'leakhawk/components/pages/incident/incident.html',
                        controller: 'IncidentController',
                        controllerAs: 'vm'
                    }
                }
            }).state('control_panel', {
                parent: 'default',
                url: '/control_panel',
                views: {
                    "view@default": {
                        templateUrl: 'leakhawk/components/pages/control_panel/control_panel.html',
                        controller: 'IncidentController',
                        controllerAs: 'vm'
                    }
                }
            }).state('configuration', {
                parent: 'default',
                url: '/configuration',
                views: {
                    "view@default": {
                        templateUrl: 'leakhawk/components/pages/configuration/configuration.html',
                        controller: 'ControlPanelController',
                        controllerAs: 'vm'
                    }
                }
            }).state('404', {
                parent: 'default',
                url: '/404',
                views: {
                    "view@default": {
                        templateUrl: 'leakhawk/components/core/404/404.html',
                        controller: 'NotFoundController',
                        controllerAs: 'vm'
                    }
                }
            });

            $urlRouterProvider.otherwise('/404');

        }]);
})();