
(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('SearchController', SearchController);

    SearchController.$inject = ['webservice', '$stateParams' , '$state', $timeout];

    function SearchController(webservice, $stateParams, $state, $timeout) {
        var vm = this;
        vm.loadTable = loadTable;

        vm.responseData = "";

        vm.loadTable();
        setInterval(function(){
            vm.loadTable();
        }, 30000);


        function loadTable() {
            $("#dataTable").LoadingOverlay("show");
            webservice.call('incident/get_slevel_incidents/'+$stateParams.level, 'GET').then(function (response) {
                vm.postList = response.data;
                $("#dataTable").LoadingOverlay("hide");
            });
        }

    }

})();
