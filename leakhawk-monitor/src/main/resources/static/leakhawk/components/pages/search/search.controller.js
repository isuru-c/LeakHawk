
(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('SearchController', SearchController);

    SearchController.$inject = ['webservice', '$stateParams' , '$state', $timeout];

    function SearchController(webservice, $stateParams, $state, $timeout) {
        var vm = this;
        vm.loadSensitivityTable = loadSensitivityTable;
        vm.routeToIncident = routeToIncident;

        vm.responseData = "";

       /* if($stateParams.type == 'sensitivity') {*/
            vm.loadSensitivityTable();
            setInterval(function () {
                vm.loadSensitivityTable();
            }, 30000);
        /*}else if($stateParams.type == 'date') {
            vm.loadDateTable();
            setInterval(function () {
                vm.loadDateTable();
            }, 30000);
        }*/


        function loadSensitivityTable() {
            $("#dataTable").LoadingOverlay("show");
            webservice.call('incident/get_slevel_incidents/'+$stateParams.level, 'GET').then(function (response) {
                vm.postList = response.data;
                $("#dataTable").LoadingOverlay("hide");
            });
        }

        function loadDateTable() {
            $("#dataTable").LoadingOverlay("show");
            webservice.call('incident/get_incidents_orderby_date', 'GET').then(function (response) {
                vm.postList = response.data;
                $("#dataTable").LoadingOverlay("hide");
            });
        }

        function routeToIncident(id) {
            $state.go("incident",{ 'id' : id });
        }

    }

})();
