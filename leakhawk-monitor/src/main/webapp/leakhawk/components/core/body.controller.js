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
            $("#totalPosts").LoadingOverlay("show");
            $("#sensitivePosts").LoadingOverlay("show");
            $("#criticalPosts").LoadingOverlay("show");
            webservice.call('/incident/get_header_data', 'GET').then(function (response) {
                vm.numOfTotalPosts = response.data.totalPosts;
                vm.totalSensitivePosts = response.data.sensitivePosts;
                vm.totalCriticalPosts = response.data.criticalPosts;
                $("#totalPosts").LoadingOverlay("hide");
                $("#sensitivePosts").LoadingOverlay("hide");
                $("#criticalPosts").LoadingOverlay("hide");
            });
        }




        $state.go('home');
    }


})();

