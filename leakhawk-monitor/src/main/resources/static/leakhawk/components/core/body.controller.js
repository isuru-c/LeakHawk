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
        vm.routeToLevel = routeToLevel;
        vm.pastebinRunning = false;
        vm.twitterRunning = false;


        loadData();
        setInterval(function(){
            loadData();
        }, 20000);


        function loadData() {
            $("#totalPosts").LoadingOverlay("show");
            $("#sensitivePosts").LoadingOverlay("show");
            $("#criticalPosts").LoadingOverlay("show");
            webservice.call('incident/get_header_data', 'GET').then(function (response) {
                vm.numOfTotalPosts = response.data.totalPosts;
                vm.totalSensitivePosts = response.data.sensitivePosts;
                vm.totalCriticalPosts = response.data.criticalPosts;
                $("#totalPosts").LoadingOverlay("hide");
                $("#sensitivePosts").LoadingOverlay("hide");
                $("#criticalPosts").LoadingOverlay("hide");
            });

            dataFeedsCheck();
        }


        function dataFeedsCheck() {
            $("#dataFeeds").LoadingOverlay("show");
            webservice.call('configuration/get_config', 'GET').then(function (response) {
                vm.twitterRunning = response.data.twitterSensor;
                vm.pastebinRunning = response.data.pastebinSensor;
                $("#dataFeeds").LoadingOverlay("hide");
            });
        }

        function routeToLevel(level) {
            $state.go("search",{ 'level' : level});
        }

        function routeToDate() {
            $state.go("search",{  'type' : 'date' });
        }

        function routeToSource() {
            $state.go("search",{ 'type' : 'source' });
        }

        function routeToType() {
            $state.go("search",{ 'type' : 'type' });
        }


        $state.go('home');
    }




})();

