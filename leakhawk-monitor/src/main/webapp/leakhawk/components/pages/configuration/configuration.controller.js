
(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('ConfigurationController', ConfigurationController);

    ConfigurationController.$inject = ['webservice','$stateParams' ,'$state'];

    function ConfigurationController(webservice, $stateParams,$state) {
        var vm = this;

        webservice.call('/getIncident/'+$stateParams.id, 'GET').then(function (response) {

            vm.postDetails = response.data.details;
        });
    }

})();
