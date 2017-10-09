
(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['webservice', '$state', $timeout];

    function HomeController(webservice, $state,$timeout) {
        var vm = this;
        vm.startLeakHawk = startLeakHawk;
        vm.stopLeakHawk = stopLeakHawk;
        vm.addTwitter = addTwitter;
        vm.removeTwitter = removeTwitter;
        vm.routeToOrder = routeToOrder;
        vm.leakHawkButton = false;
        vm.twitterButton = false;
        vm.loadTable = loadTable;


        // $timeout(getOutput, 3000);
        vm.responseData = "";

        vm.loadTable();
        setInterval(function(){
            vm.loadTable();
        }, 30000);


        function loadTable() {
            webservice.call('/incident/get_all_incidents', 'GET').then(function (response) {
                vm.postList = response.data;
            });
        }





        function startLeakHawk(){
            $.LoadingOverlay("show");
            webservice.call('/application/start', 'GET').then(function (response) {
                $.LoadingOverlay("hide");
                vm.responseData += "LeakHawk Started.....\n";
            });
            vm.leakHawkButton = true;
        }

        function stopLeakHawk(){
            $.LoadingOverlay("show");
            webservice.call('/application/stop', 'GET').then(function (response) {
                $.LoadingOverlay("hide");
                vm.responseData += "LeakHawk Stoped.....\n";
            });
            vm.leakHawkButton = false;
        }


        function addTwitter() {
            var sendJson = {id:2,name:"Dishan"};
            $.LoadingOverlay("show");
            webservice.call('/application/addTwitter', 'POST',sendJson).then(function (response) {
                console.log(response.data);
                $.LoadingOverlay("hide");
            });
            vm.responseData += "Twitter feed added.....\n";
            vm.twitterButton = true;

        }

        function removeTwitter() {
            $.LoadingOverlay("show");
            webservice.call('/application/removeTwitter', 'GET').then(function (response) {
                console.log(response.data);
                $.LoadingOverlay("hide");
            });
            vm.responseData += "Twitter feed removed.....\n";
            vm.twitterButton = false;

        }


        function routeToOrder(id) {
            $state.go("incident",{ 'id' : id });
        }
    }

})();
