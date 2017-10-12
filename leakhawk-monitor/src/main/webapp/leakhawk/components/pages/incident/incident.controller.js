
(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('IncidentController', IncidentController);

    IncidentController.$inject = ['webservice','$stateParams' ,'$state'];

    function IncidentController(webservice, $stateParams,$state) {
        var vm = this;

        webservice.call('incident/get_incident/'+$stateParams.id, 'GET').then(function (response) {
            vm.postDetails = response.data;
        });
    }

})();
