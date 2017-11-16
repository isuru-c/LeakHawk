
(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['webservice', '$state', $timeout, 'Notification'];

    function HomeController(webservice, $state,$timeout, Notification) {
        var vm = this;
        vm.routeToIncident = routeToIncident;
        vm.loadTable = loadTable;


        // $timeout(getOutput, 3000);
        vm.responseData = "";

        vm.loadTable();
        setInterval(function(){
            vm.loadTable();
        }, 30000);


        function loadTable() {
            $("#dataTable").LoadingOverlay("show");
            webservice.call('incident/get_all_incidents', 'GET').then(function (response) {
                    vm.postList = response.data;
                    $("#dataTable").LoadingOverlay("hide");
            });
        }


        function routeToIncident(id) {
            $state.go("incident",{ 'id' : id });
        }
    }

})();
