/**
 * Created by Buddhi on 2/27/2017.
 */

(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('BodyController', BodyController);

    BodyController.$inject = ['$state', '$rootScope', 'webservice','$cookies'];


    function BodyController($state, $rootScope, webservice,$cookies) {
        var vm = this;

        loadData();

        function loadData() {
            webservice.call('/incident/get_header_data', 'GET').then(function (response) {
                vm.numOfTotalPosts = response.data.totalPosts;
                vm.totalSensitivePosts = response.data.sensitivePosts;
                vm.totalCriticalPosts = response.data.criticalPosts;
            });
        }




        $state.go('home');
    }


})();

